package com.mrll.javelin.tikaparser.service;
import java.io.FileInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mrll.javelin.tikaparser.model.Document;
import com.mrll.javelin.tikaparser.utils.TextExtractionServiceHelper;


@Service
public class TextExtractionServiceImpl implements TextExtractionService {

	private TextExtractionServiceHelper helper;

	private String valifileformats;
	
	
	@Autowired
	public TextExtractionServiceImpl(TextExtractionServiceHelper helper,
			@Value("${settings.valifileformats}") String valifileformats) {
		this.helper = helper;
		this.valifileformats = valifileformats;
	}

	@Override
	public FileInputStream parseDocument(MultipartFile mfile , Document document) throws Exception {
		System.out.println("In parseDocument in TextExtractionService");
		//Document document = new Document();
		document.setDocId(mfile.getOriginalFilename());
		//document.setContent(mfile.getInputStream());
		document.setSize(mfile.getSize());
		return helper.extractDocument(document,mfile);
		//return helper.extractDocumentFromAbbyy(document,mfile);

	}

    
}
