package com.mrll.javelin.tikaparser.extractor;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.RecursiveParserWrapper;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.parser.ocr.TesseractOCRParser;
import org.apache.tika.parser.pdf.PDFParserConfig;
import org.apache.tika.parser.pdf.PDFParserConfig.OCR_STRATEGY;
import org.apache.tika.sax.BasicContentHandlerFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

@Component
public class PDFExtractor implements Extractor {

	private boolean ocrIng;
	private String contents;

	public PDFExtractor() {
		this.contents = "";
		this.ocrIng = false;
	}

	@Override
	public String extract(InputStream stream) {
		try {
			contents = runExtraction(stream, BasicContentHandlerFactory.HANDLER_TYPE.BODY,
					TesseractOCRConfig.OUTPUT_TYPE.TXT);
		} catch (IOException | SAXException | TikaException e) {

			e.printStackTrace();
		}
		return contents;
	}

	private String runExtraction(InputStream stream, BasicContentHandlerFactory.HANDLER_TYPE handlerType,
			TesseractOCRConfig.OUTPUT_TYPE outputType) throws IOException, SAXException, TikaException {
		TesseractOCRConfig config = new TesseractOCRConfig();
		config.setOutputType(outputType);
		// config.setTesseractPath("C:\\Program Files (x86)\\Tesseract-OCR");
		// config.setTesseractPath("/usr/local/share/tessdata");
		config.setEnableImageProcessing(1);
		config.setPreserveInterwordSpacing(true);

	    config.setLanguage("eng");

		config.setPageSegMode("6");

		Parser parser = new RecursiveParserWrapper(new AutoDetectParser(),
				new BasicContentHandlerFactory(handlerType, -1));

		PDFParserConfig pdfConfig = new PDFParserConfig();
		if (ocrIng) {
			pdfConfig.setOcrStrategy(OCR_STRATEGY.OCR_ONLY);
		} else {
			pdfConfig.setOcrStrategy(OCR_STRATEGY.NO_OCR);
		}

		ParseContext parseContext = new ParseContext();
		parseContext.set(TesseractOCRConfig.class, config);
		parseContext.set(Parser.class, new TesseractOCRParser());
		parseContext.set(PDFParserConfig.class, pdfConfig);

		parser.parse(stream, new DefaultHandler(), new Metadata(), parseContext);

		List<Metadata> metadataList = ((RecursiveParserWrapper) parser).getMetadata();
		StringBuilder contents = new StringBuilder();
		for (Metadata m : metadataList) {

			contents.append(m.get(RecursiveParserWrapper.TIKA_CONTENT));
		}
		stream.close();
		return contents.toString();
	}

	public boolean isOcrIng() {
		return ocrIng;
	}

	public void setOcrIng(boolean ocrIng) {
		this.ocrIng = ocrIng;
	}

}
