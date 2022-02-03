/*
 * DialogView  2021-10-04
 *
 * Copyright (c) Pro Data GmbH & ASA KG. All rights reserved.
 */

package com.example.application.views;

import com.example.application.MainLayout;
import com.example.application.components.UploadS3;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import org.springframework.beans.factory.annotation.Value;

@PageTitle("UploadS3")
@Route(value = "uploads3", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@CssImport(value = "./components/dialog-layout.css", themeFor = "vaadin-dialog-overlay")
public class UploadS3View extends Composite<VerticalLayout> {

	private final String accessKey;
	private final String secretKey;
	private final String bucketName;

	public UploadS3View(@Value("${aws.accessKey}") String accessKey,
		@Value("${aws.secretKey}") String secretKey,
		@Value("${aws.s3bucket.name}") String bucketName)
	{
		this.accessKey = accessKey;
		this.secretKey = secretKey;
		this.bucketName = bucketName;
	}

	@Override
	protected VerticalLayout initContent() {
		UploadS3 upload = new UploadS3(accessKey, secretKey, bucketName);

		var layout = new VerticalLayout(upload);
		layout.addClassName("centered-content");
		layout.setSizeFull();
		return layout;
	}
}
