package com.mrll.javelin.tikaparser.delegate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 
 * @author shipra.pandey
 * @description BlobDownloadServiceDelegate provides functionality to download a
 *             document from blob.
 */
@Component
public class BlobDownloadServiceDelegate {

	private RestTemplate restTemplate;
	private final String blobDownloadUrl;
	private static final Logger LOG = LoggerFactory.getLogger(BlobDownloadServiceDelegate.class);

	/**
	 * 
	 * @param blobDownloadUrl
	 * @param restTemplate
	 */

	@Autowired
	public BlobDownloadServiceDelegate(@Value("${settings.blobDownloadUrl}") String blobDownloadUrl,
			RestTemplate restTemplate) {
		this.blobDownloadUrl = blobDownloadUrl;
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
	public InputStream blobDownload(final String containerName, final String docId) {
		LOG.info("azureblobdownload-service called with ContainerName.." + containerName);
		LOG.info("docId:" + docId);
		
		LOG.info("service:" + blobDownloadUrl);
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));

		HttpEntity<String> entity = new HttpEntity<>(headers);

		LOG.info("container:" + containerName);
		LOG.info("fileId:" + docId);
		restTemplate=new RestTemplate();
		ResponseEntity<byte[]> response = restTemplate.exchange(blobDownloadUrl + "/"+ containerName + "/{fileId}",
				HttpMethod.GET, entity, byte[].class, docId);
		return new ByteArrayInputStream(response.getBody());
	}

}
