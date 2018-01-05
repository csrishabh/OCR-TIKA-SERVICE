package com.mrll.javelin.tikaparser.controller;

import java.io.FileInputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mrll.javelin.tikaparser.model.Document;
import com.mrll.javelin.tikaparser.service.TextExtractionService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


/**
 * Rest Controller for Document Parser Service
 * 
 *
 */
@RestController
@RequestMapping(value = { "/api" })
public class TikaParserController {
	private static final Logger LOGGER = LoggerFactory.getLogger(TikaParserController.class);

	private TextExtractionService textExtractionService;

	@Autowired
	public TikaParserController(TextExtractionService textExtractionService) {
		this.textExtractionService = textExtractionService;
	}

	/**
	 * Method for extracting text from document
	 * 
	 * @param containerName
	 * @param indexRequestBean
	 * @return 
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value = "ocr", nickname = "ocr")
	@RequestMapping(value = "/ocr", method = RequestMethod.POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	@ApiResponses(value = { 
	            @ApiResponse(code = 200, message = "Success", response = ResponseEntity.class),
	            @ApiResponse(code = 401, message = "Unauthorized"),
	            @ApiResponse(code = 403, message = "Forbidden"),
	            @ApiResponse(code = 404, message = "Not Found"),
	            @ApiResponse(code = 500, message = "Failure")}) 
	public  void parseDocument(@RequestPart(value = "filestream", required = true) MultipartFile filestream , HttpServletResponse response)
			throws Exception {
		//System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
		long startTime = System.currentTimeMillis();
		System.out.println("In parseDocument in controller");
		Document document = new Document();
		/*if(!filestream.getOriginalFilename().substring(filestream.getOriginalFilename().lastIndexOf('.')+1).equalsIgnoreCase("PDF")){
			
			response.setStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
		}
		else{*/
		FileInputStream inputStream = textExtractionService.parseDocument(filestream ,document);
		response.setContentType("Application/zip");
		response.setHeader("Content-Disposition", "attachment; filename=\"" +
                filestream.getOriginalFilename() + "." + "zip");
		IOUtils.copy(inputStream, response.getOutputStream());
		
		response.flushBuffer();
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		LOGGER.info("text extraction completed..."+elapsedTime);
		//}
	}
	
	
	 


}