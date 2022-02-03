/*
 * UploadS3  2022-02-03
 *
 * Copyright (c) Pro Data GmbH & ASA KG. All rights reserved.
 */

package com.example.application.components;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.server.StreamResource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;

/**
 * UploadS3
 * @author Hannes Tribus
 * @since 2022-02-03
 */
public class UploadS3 extends Composite<VerticalLayout> {

	private final String accessKey;
	private final String secretKey;
	private final String bucketName;
	private final MemoryBuffer buffer;
	private final Upload upload;

	private AmazonS3 s3client;
	private String objectKey;

	private TextField link;

	public UploadS3(String accessKey, String secretKey, String bucketName) {
		this.accessKey = accessKey;
		this.secretKey = secretKey;
		this.bucketName = bucketName;

		buffer = new MemoryBuffer();
		upload = new Upload(buffer);
	}

	@Override
	protected VerticalLayout initContent() {
		link = new TextField("Link");
		link.setReadOnly(true);
		link.setWidthFull();

		Image image = new Image("", "image");
		link.addValueChangeListener(e -> {
			byte[] imageBytes = downloadImage();
			StreamResource resource = new StreamResource("image",
				() -> new ByteArrayInputStream(imageBytes));
			image.setSrc(resource);
		});

		var layout = new VerticalLayout(upload, link, image);
		layout.setSizeFull();
		initAWSClient();
		return layout;
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		super.onAttach(attachEvent);
		initAWSClient();
		uploadFile();
	}

	private void initAWSClient() {
		AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
		this.s3client = AmazonS3ClientBuilder.standard()
			.withCredentials(new AWSStaticCredentialsProvider(credentials))
			.withRegion(Regions.EU_CENTRAL_1)
			.build();
	}

	private void uploadFile() {
		upload.addSucceededListener(event-> {
			try {
				InputStream is = buffer.getInputStream();
				File tempFile = new File(event.getFileName());
				FileUtils.copyInputStreamToFile(is, tempFile);

				objectKey = tempFile.getName();
				s3client.putObject(new PutObjectRequest(bucketName, objectKey, tempFile));
				if(tempFile.exists()) {
					tempFile.delete();
				}
				link.setValue(s3client.getUrl(bucketName, objectKey).toString());
			} catch (AmazonServiceException | IOException ex) {
				ex.printStackTrace();
			}
		});
	}

	public byte[] downloadImage() {
		byte[] imageBytes = new byte[0];
		S3Object s3object = s3client.getObject(bucketName, objectKey);
		S3ObjectInputStream inputStream = s3object.getObjectContent();
		try {
			imageBytes = IOUtils.toByteArray(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return imageBytes;
	}
}
