package com.mrll.javelin.tikaparser.delegate;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.mrll.javelin.tikaparser.utils.TextExtractionServiceConstants;

import org.springframework.core.io.ByteArrayResource;

/**
 * 
 * @author shipra.pandey
 * @description  BlobUploadServiceDelegate provides functionality to
 *         upload a document to blob store.
 */
@Component
public class BlobUploadServiceDelegate {
	private RestTemplate restTemplate;
	private String blobUploadServicedUrl;
	private static final Logger LOG = LoggerFactory.getLogger(BlobDownloadServiceDelegate.class);


	/**
	 * 
	 * @param blobUploadServicedUrl
	 * @param restTemplate
	 */
	@Autowired
	public BlobUploadServiceDelegate(@Value("${settings.blobUploadServicedUrl}") String blobUploadServicedUrl, RestTemplate restTemplate) {
		this.blobUploadServicedUrl = blobUploadServicedUrl;
		this.restTemplate = restTemplate;

	}

	/**
	 * Method provides functionality for Single blob download.
	 * 
	 * @param conatinerName
	 * @param metadataResponseBean
	 * @param response
	 * @throws IOException
	 */
	public String blobUpload(String conatinerName, String docId,ByteArrayResource contentsAsResource) {

		String processedFileId=docId+"_"+ TextExtractionServiceConstants.PROCESS_NAME_VALUE;
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
		LOG.info("File upload start");
		parts.add("containername",conatinerName);
		parts.add("azurekeyUrl", "");
		parts.add("docId",processedFileId );
		parts.add("filestream", contentsAsResource); 
		restTemplate=new RestTemplate();
		restTemplate
				.postForEntity(blobUploadServicedUrl, parts, String.class);
		LOG.info("File upload done");
         
		return processedFileId;
	}

}
