package com.example.application.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import software.amazon.awssdk.arns.Arn;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.AccessKey;
import software.amazon.awssdk.services.iam.model.AccessKeyMetadata;
import software.amazon.awssdk.services.iam.model.AttachUserPolicyRequest;
import software.amazon.awssdk.services.iam.model.CreateAccessKeyRequest;
import software.amazon.awssdk.services.iam.model.CreateAccessKeyResponse;
import software.amazon.awssdk.services.iam.model.CreatePolicyRequest;
import software.amazon.awssdk.services.iam.model.CreatePolicyResponse;
import software.amazon.awssdk.services.iam.model.CreateUserRequest;
import software.amazon.awssdk.services.iam.model.CreateUserResponse;
import software.amazon.awssdk.services.iam.model.DeleteAccessKeyRequest;
import software.amazon.awssdk.services.iam.model.GetPolicyRequest;
import software.amazon.awssdk.services.iam.model.GetPolicyResponse;
import software.amazon.awssdk.services.iam.model.GetUserRequest;
import software.amazon.awssdk.services.iam.model.GetUserResponse;
import software.amazon.awssdk.services.iam.model.IamException;
import software.amazon.awssdk.services.iam.model.ListAccessKeysRequest;
import software.amazon.awssdk.services.iam.model.ListAccessKeysResponse;
import software.amazon.awssdk.services.iam.model.Policy;
import software.amazon.awssdk.services.iam.model.User;
import software.amazon.awssdk.services.iam.waiters.IamWaiter;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PublicAccessBlockConfiguration;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutPublicAccessBlockRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.utils.IoUtils;

/**
 * AwsS3Service
 * @author Hannes Tribus
 * @since 2022-02-04
 */
public class AwsS3Service {

	private final S3Client s3client;
	private final BucketManager manager;

	public AwsS3Service(String accessKey, String secretKey, String bucketName) {
		this.manager = new BucketManager(accessKey, secretKey, bucketName);
		AwsBasicCredentials credentials = this.manager.getCredentials(true);

		s3client = S3Client.builder()
			.region(Region.EU_CENTRAL_1)
			.credentialsProvider(credentials != null ? StaticCredentialsProvider.create(credentials) : null)
			.build();
	}

	public URL getUrl(String objectKey) {
		return s3client.utilities().getUrl(
			GetUrlRequest.builder().bucket(manager.getBucketName()).key(objectKey).build()
		);
	}

	private boolean doesObjectExist(String key) {
		try {
			s3client.headObject(HeadObjectRequest.builder().bucket(manager.getBucketName()).key(key).build());
			return true;
		}
		catch (NoSuchKeyException e) {
			return false;
		}
	}

	public String uploadFile(InputStream is, String fileName) {
		try {
			File tempFile = File.createTempFile("aws", null);
			tempFile.deleteOnExit();
			FileUtils.copyInputStreamToFile(is, tempFile);
			if (doesObjectExist(fileName)) {
				System.out.println("File" + fileName + "already there");
			}
			else {
				s3client.putObject(
					PutObjectRequest.builder().bucket(manager.getBucketName()).key(fileName).build(),
					RequestBody.fromFile(tempFile)
				);
			}
			if (tempFile.exists()) {
				tempFile.delete();
			}
			return fileName;
		}
		catch (AwsServiceException e) {
			System.err.println(e.awsErrorDetails());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public byte[] downloadObject(String objectKey) {
		byte[] imageBytes = new byte[0];
		var inputStream = s3client.getObject(
			GetObjectRequest.builder().bucket(manager.getBucketName()).key(objectKey).build()
		);
		try {
			imageBytes = IoUtils.toByteArray(inputStream);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return imageBytes;
	}

	private static class BucketManager {

		private static final String PolicyDocument =
			"{"
				+ "    \"Version\": \"2012-10-17\",\n"
				+ "    \"Statement\": [\n"
				+ "        {\n"
				+ "            \"Sid\": \"ListObjectsInBucket\",\n"
				+ "            \"Effect\": \"Allow\",\n"
				+ "            \"Action\": [\n"
				+ "                \"s3:ListBucket\"\n"
				+ "            ],\n"
				+ "            \"Resource\": [\n"
				+ "                \"arn:aws:s3:::%s\"\n"
				+ "            ]\n"
				+ "        },\n"
				+ "        {\n"
				+ "            \"Sid\": \"AllObjectActions\",\n"
				+ "            \"Effect\": \"Allow\",\n"
				+ "            \"Action\": \"s3:*Object\",\n"
				+ "            \"Resource\": [\n"
				+ "                \"arn:aws:s3:::%s/*\"\n"
				+ "            ]\n"
				+ "        }\n"
				+ "    ]\n"
				+ "}";

		private final S3Client s3;
		private final IamClient iam;
		private final String bucketName;

		public BucketManager(String accessKey, String secretKey, String bucketName) {
			this.bucketName = bucketName;
			s3 = S3Client.builder()
				.region(Region.EU_CENTRAL_1)
				.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
				.build();
			iam = IamClient.builder()
				.region(Region.AWS_GLOBAL)
				.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
				.build();
			initbucket();
		}

		public void initbucket() {
			createBucketIfNeeded();
			try {
				var user = createIamUserIfNeeded("user-" + bucketName);
				if (user != null) {
					String policyName = "policy-" + bucketName;
					var policyArn = Arn.fromString(user.arn()).toBuilder().resource("policy/" + policyName).build();
					var policy = createIamPolicyIfNeeded(policyArn);
					if (policy != null) {
						attachIamUserPolicy(user, policy);
					}
				}
			}
			catch (IamException e) {
				System.err.println(e.awsErrorDetails().errorMessage());
			}
		}

		public AwsBasicCredentials getCredentials(boolean deleteExisting) {
			try {
				if (deleteExisting) {
					deleteAllIamAccessKeys("user-" + bucketName);
				}

				var accessKey = createIamAccessKey("user-" + bucketName);
				System.out.println("accessKey: " + accessKey);
				return AwsBasicCredentials.create(accessKey.accessKeyId(), accessKey.secretAccessKey());
			}
			catch (IamException e) {
				System.err.println(e.awsErrorDetails());
				return null;
			}
		}

		private User getIamUser(String username) {
			try {
				GetUserRequest request = GetUserRequest.builder()
					.userName(username)
					.build();
				return iam.getUser(request).user();
			}
			catch (IamException e) {
				System.err.println(e.awsErrorDetails());
				return null;
			}
		}

		private User createIamUserIfNeeded(String username) throws IamException {
			var user = getIamUser(username);
			if (user != null)
				return user;
			// Create an IamWaiter object
			IamWaiter iamWaiter = iam.waiter();

			CreateUserRequest request = CreateUserRequest.builder()
				.userName(username)
				.build();

			CreateUserResponse response = iam.createUser(request);

			GetUserRequest userRequest = GetUserRequest.builder()
				.userName(response.user().userName())
				.build();

			// Wait until the user is created
			WaiterResponse<GetUserResponse> waitUntilUserExists = iamWaiter.waitUntilUserExists(userRequest);
			waitUntilUserExists.matched().response().ifPresent(System.out::println);
			return response.user();
		}

		private Policy getIamPolicy(Arn arn) {
			try {
				GetPolicyRequest request = GetPolicyRequest.builder()
					.policyArn(arn.toString())
					.build();
				return iam.getPolicy(request).policy();
			}
			catch (IamException e) {
				System.err.println(e.awsErrorDetails());
				return null;
			}
		}

		private Policy createIamPolicyIfNeeded(Arn policyArn) throws IamException {
			Policy policy = getIamPolicy(policyArn);
			if (policy != null)
				return policy;
			// Create an IamWaiter object
			IamWaiter iamWaiter = iam.waiter();

			CreatePolicyRequest request = CreatePolicyRequest.builder()
				.policyName(policyArn.resource().resource())
				.policyDocument(String.format(PolicyDocument, bucketName, bucketName))
				.build();

			CreatePolicyResponse response = iam.createPolicy(request);

			// Wait until the policy is created
			GetPolicyRequest polRequest = GetPolicyRequest.builder()
				.policyArn(response.policy().arn())
				.build();

			WaiterResponse<GetPolicyResponse> waitUntilPolicyExists = iamWaiter.waitUntilPolicyExists(polRequest);
			waitUntilPolicyExists.matched().response().ifPresent(System.out::println);
			return response.policy();
		}

		private void attachIamUserPolicy(User user, Policy policy) throws IamException {
			iam.attachUserPolicy(AttachUserPolicyRequest.builder()
				.policyArn(policy.arn())
				.userName(user.userName())
				.build()
			);
		}

		private void deleteAllIamAccessKeys(String userName) throws IamException {
			boolean done = false;
			String newMarker = null;
			while (!done) {
				ListAccessKeysRequest request = newMarker == null
					? ListAccessKeysRequest.builder().userName(userName).build()
					: ListAccessKeysRequest.builder().userName(userName).marker(newMarker).build();

				ListAccessKeysResponse response = iam.listAccessKeys(request);

				for (AccessKeyMetadata metadata : response.accessKeyMetadata()) {
					deleteIamKey(userName, metadata.accessKeyId());
				}

				if (!response.isTruncated()) {
					done = true;
				}
				else {
					newMarker = response.marker();
				}
			}
		}

		private void deleteIamKey(String username, String accessKey) throws IamException {
			DeleteAccessKeyRequest request = DeleteAccessKeyRequest.builder()
				.accessKeyId(accessKey)
				.userName(username)
				.build();
			iam.deleteAccessKey(request);
		}

		private AccessKey createIamAccessKey(String user) throws IamException {
			CreateAccessKeyRequest request = CreateAccessKeyRequest.builder()
				.userName(user)
				.build();

			CreateAccessKeyResponse response = iam.createAccessKey(request);
			return response.accessKey();
		}

		public String getBucketName() {
			return bucketName;
		}

		private void createBucketIfNeeded() {
			if (!doesBucketExist()) {
				try {
					s3.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
					HeadBucketRequest bucketRequestWait = HeadBucketRequest.builder().bucket(bucketName).build();
					// Wait until the bucket is created and print out the response
					WaiterResponse<HeadBucketResponse> waiterResponse = s3.waiter()
						.waitUntilBucketExists(bucketRequestWait);
					waiterResponse.matched().response().ifPresent(System.out::println);
					s3.putPublicAccessBlock(
						PutPublicAccessBlockRequest.builder().publicAccessBlockConfiguration(
							PublicAccessBlockConfiguration.builder()
								.blockPublicPolicy(true)
								.blockPublicAcls(true)
								.ignorePublicAcls(true)
								.restrictPublicBuckets(true)
								.build()
						).bucket(bucketName).build()
					);
				}
				catch (S3Exception e) {
					System.err.println(e.awsErrorDetails());
				}
			}
		}

		private boolean doesBucketExist() {
			try {
				HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
					.bucket(bucketName)
					.build();
				s3.headBucket(headBucketRequest);
				return true;
			}
			catch (NoSuchBucketException e) {
				System.err.println(e.awsErrorDetails());
				return false;
			}
		}
	}
}
