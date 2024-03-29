package com.example.application.views;

import com.example.application.MainLayout;
import com.example.application.components.UploadS3;
import com.example.application.services.AwsS3Service;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import org.springframework.beans.factory.annotation.Value;

@PageTitle("UploadS3")
@Route(value = "uploads3", layout = MainLayout.class)
@CssImport(value = "./components/dialog-layout.css", themeFor = "vaadin-dialog-overlay")
public class UploadS3View extends Composite<VerticalLayout> {

	private final UploadS3 upload;

	public UploadS3View(@Value("${aws.accessKey}") String accessKey,
		@Value("${aws.secretKey}") String secretKey,
		@Value("${aws.s3bucket.name}") String bucketName)
	{
		var service = new AwsS3Service(accessKey, secretKey, bucketName);
		this.upload = new UploadS3(service);
	}

	@Override
	protected VerticalLayout initContent() {
		var layout = new VerticalLayout(upload);
		layout.addClassName("centered-content");
		layout.setSizeFull();
		return layout;
	}
}
