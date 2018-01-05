package com.mrll.javelin.tikaparser.delegate;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.mrll.javelin.tikaparser.model.DocFormat;
import com.mrll.javelin.tikaparser.model.DocProperties;

/**
 * 
 * @author shipra.pandey
 * @description DocMetadataServiceDelegate provides functionality to update
 *              doc-forms in metadata collection.
 */

@Component
public class DocMetadataServiceDelegate {
	private RestTemplate restTemplate;
	private String docMetadataServicedUrl;
	private static final Logger LOG = LoggerFactory.getLogger(DocMetadataServiceDelegate.class);

	/**
	 * 
	 * @param blobUploadServicedUrl
	 * @param restTemplate
	 */
	@Autowired
	public DocMetadataServiceDelegate(@Value("${settings.docMetadataServicedUrl}") String docMetadataServicedUrl,
			RestTemplate restTemplate) {
		this.docMetadataServicedUrl = docMetadataServicedUrl;
		this.restTemplate = restTemplate;

	}

	/**
	 * 
	 * @param conatinerName
	 * @param docId
	 * @param fileSize
	 * @param fileType
	 * @param pageCount
	 * @throws JSONException
	 */
	public void updateDocForms(String conatinerName, String docId, String fileSize, String fileType, String pageCount) {

		LOG.info("update doc-form call started");
		java.net.URI uri = UriComponentsBuilder.fromUriString(docMetadataServicedUrl)
				.queryParam("projectId", conatinerName).queryParam("docId", docId).build().toUri();
		DocProperties docProperties = new DocProperties();
		docProperties.setFileSize(fileSize);
		docProperties.setPageCount(pageCount);
		docProperties.setFileType(fileType);

		String processedFileId = docId + "_OCR_TIKA";
		DocFormat docFormat = new DocFormat();
		docFormat.setpName("OCR_TIKA");
		docFormat.setDocId(processedFileId);
		docFormat.setDocProperties(docProperties);
		restTemplate=new RestTemplate();
		restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<DocFormat>(docFormat), String.class);
		LOG.info("update doc-form call completed");
	}

}
