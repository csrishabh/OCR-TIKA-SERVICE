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
import org.apache.tika.sax.BasicContentHandlerFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

@Component
public class ImageExtractor implements Extractor {

	private String contents;

	public ImageExtractor() {

		this.contents = "";

	}

	@Override
	public String extract(InputStream stream) {
		try {
			contents = runExtraction(stream, BasicContentHandlerFactory.HANDLER_TYPE.XML,
					TesseractOCRConfig.OUTPUT_TYPE.HOCR);
		} catch (IOException | SAXException | TikaException e) {

			e.printStackTrace();
		}
		return contents;
	}

	private String runExtraction(InputStream stream, BasicContentHandlerFactory.HANDLER_TYPE handlerType,
			TesseractOCRConfig.OUTPUT_TYPE outputType) throws IOException, SAXException, TikaException {
		TesseractOCRConfig config = new TesseractOCRConfig();
		config.setOutputType(outputType);
		config.setPreserveInterwordSpacing(true);
		config.setLanguage("eng");
		config.setPageSegMode("6");
		Parser parser = new RecursiveParserWrapper(new AutoDetectParser(),
				new BasicContentHandlerFactory(handlerType, -1));
		ParseContext parseContext = new ParseContext();
		parseContext.set(TesseractOCRConfig.class, config);
		parseContext.set(Parser.class, new TesseractOCRParser());
		parser.parse(stream, new DefaultHandler(), new Metadata(), parseContext);
		List<Metadata> metadataList = ((RecursiveParserWrapper) parser).getMetadata();
		StringBuilder contents = new StringBuilder();
		for (Metadata m : metadataList) {

			contents.append(m.get(RecursiveParserWrapper.TIKA_CONTENT));
		}
		stream.close();
		//System.out.println(contents.toString());
		return contents.toString();
	}

	

	/*public static void main(String[] args) throws IOException {

		InputStream is = new FileInputStream(new File("E:\\Original Files\\TiltedTextPNG2.png"));
		System.out.println(new ImageExtractor().extract(is));

		// new ImageExtractor().test();

	}*/


}
