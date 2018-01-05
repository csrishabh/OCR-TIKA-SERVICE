package com.mrll.javelin.tikaparser.extractor;

import java.io.InputStream;

import org.springframework.stereotype.Component;

@Component
public interface Extractor {
	
	
	public String extract(InputStream stream);

}
