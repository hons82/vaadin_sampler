package com.example.application.components;

import com.example.application.services.AwsS3Service;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.server.StreamResource;

import java.io.ByteArrayInputStream;

/**
 * UploadS3
 * @author Hannes Tribus
 * @since 2022-02-03
 */
public class UploadS3 extends Composite<VerticalLayout> {

	private final MemoryBuffer buffer;
	private final Upload upload;
	private final AwsS3Service service;

	private String objectKey;

	private TextField link;

	public UploadS3(AwsS3Service service) {
		this.service = service;
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
			byte[] imageBytes = service.downloadObject(objectKey);
			StreamResource resource = new StreamResource("image",
				() -> new ByteArrayInputStream(imageBytes));
			image.setSrc(resource);
		});

		var layout = new VerticalLayout(upload, link, image);
		layout.setSizeFull();
		return layout;
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		super.onAttach(attachEvent);
		uploadFile();
	}

	private void uploadFile() {
		upload.addSucceededListener(event-> {
			objectKey = service.uploadFile(buffer.getInputStream(), event.getFileName());
			if (objectKey != null)
				link.setValue(service.getUrl(event.getFileName()).toString());
		});
	}
}
