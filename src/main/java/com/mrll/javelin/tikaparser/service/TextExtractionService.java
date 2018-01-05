package com.mrll.javelin.tikaparser.service;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.tika.exception.TikaException;
import org.codehaus.jettison.json.JSONException;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import com.mrll.javelin.tikaparser.model.Document;

/**
 * Interface provides contract to implement search on index
 * 
 * @author shipra.pandey
 *
 */
@FunctionalInterface
public interface TextExtractionService {
	/**
	 * 
	 * @param containerName
	 * @param metadataId
	 * @param
	 * @return
	 * @throws JSONException 
	 * @throws TikaException 
	 * @throws SAXException 
	 * @throws IOException 
	 * @throws Exception 
	 */
	FileInputStream parseDocument(MultipartFile mfile , Document document) throws JSONException, IOException, SAXException, TikaException, Exception;
}
