
package com.mrll.javelin.tikaparser.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.multipdf.Overlay;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
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
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.helpers.DefaultHandler;

import com.mrll.javelin.tikaparser.exception.RestAPIException;
import com.mrll.javelin.tikaparser.extractor.Extractor;
import com.mrll.javelin.tikaparser.extractor.ImageExtractor;
import com.mrll.javelin.tikaparser.extractor.PDFExtractor;
import com.mrll.javelin.tikaparser.model.Document;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.PdfUtilities;

/**
 * Helper class to create indexes on file content.
 * 
 * @author nitish.k.rai
 *
 */
@Component
public class TextExtractionServiceHelper {

	private static final Logger logger = LoggerFactory.getLogger(TextExtractionServiceHelper.class);
	
	@Autowired
	ImageExtractor imageExtractor;
	
	@Autowired
	PDFExtractor pdfExtractor;
	
	@Autowired
	HOCRConveter conveter;
	
	DocumentProcesser DocProcesser = new DocumentProcesser();

	/*public Document extractDocumentFromTesseract(Document document, MultipartFile mfile) {

		try {
			String rawText = getRawTextFromTesseract(mfile.getInputStream());

			// this code for extracting text with image-text extraction plugin

			 * if (rawText.equals("") || rawText.equals(null)) {
			 * logger.info("Got no text, converting pdf to image"); List<String>
			 * files = getTextAsOCR(mfile.getInputStream(),
			 * document.getDocId()); if (!files.isEmpty()) { for (String path :
			 * files) { String text = getRawText(new FileInputStream(path));
			 * rawText = rawText.concat(text); File f = new File(path);
			 * f.delete(); logger.info("Deleted Temp file:" + path);
			 * 
			 * } } }
			 

			String languages = getLanguage(rawText);
			document.setRawText(rawText);
			document.setLanguageName(languages);

		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new RestAPIException("Unable to extract text from file");
		}

		return document;
	}*/

	/*private String getRawTextFromTesseract(InputStream stream) {
		String rawText = null;
		Parser parser = new AutoDetectParser();
		BodyContentHandler handler = new BodyContentHandler(Integer.MAX_VALUE);
		TesseractOCRConfig config = new TesseractOCRConfig();
		ClassLoader classLoader = getClass().getClassLoader();
		
        File tessDataFolder = LoadLibs.extractTessResources("tessdata"); 
		// if (!checkIfExecutableInPath("tesseract")) {
		config.setTesseractPath(tessDataFolder.getParent());
		// }
		PDFParserConfig pdfConfig = new PDFParserConfig();
		pdfConfig.setExtractInlineImages(true);
		ParseContext parseContext = new ParseContext();

		parseContext.set(TesseractOCRConfig.class, config);
		parseContext.set(PDFParserConfig.class, pdfConfig);
		parseContext.set(Parser.class, parser);
		Metadata metadata = new Metadata();
		try {
			logger.info("calling TIKA parser");
			parser.parse(stream, handler, metadata, parseContext);
			rawText = handler.toString().trim();

		} catch (IOException | SAXException | TikaException e) {
			logger.error(e.getMessage(), e);
			throw new RestAPIException("Unable to extract text from file");
		}
		return rawText;
	}*/
	
	
	/*public Document extractDocumentFromAbbyy(Document document, MultipartFile mfile) {
		
		
		AbbyyDocumentProcesser processer = new AbbyyDocumentProcesser();
		try {
			processer.Run(mfile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return document;
	}*/
	
	public void addTextLayer(PDDocument originalDoc , File fDir , Map<Integer, String> ovmap) throws IOException {
		originalDoc.setAllSecurityToBeRemoved(true);
		Overlay overlayObj = new Overlay();
		overlayObj.setOverlayPosition(Overlay.Position.BACKGROUND);
		overlayObj.setInputPDF(originalDoc);
		// overlayObj.setAllPagesOverlayPDF(overlayDoc);
		/*Map<Integer, String> ovmap = new HashMap<Integer, String>();
		for (int pageNo = 1; pageNo <= originalDoc.getNumberOfPages(); pageNo++) {
			ovmap.put(pageNo, fDir.getPath()+File.separator+"result_page_" + pageNo);
		}*/
		PDDocument finalDoc = overlayObj.overlay(ovmap);
		finalDoc.save(fDir.getPath()+File.separator+"result.pdf");
		finalDoc.close();
	}
	

	public FileInputStream extractDocument(Document document, MultipartFile mfile) {

		FileInputStream inputStream = null;
		
	    
		try {
			/*ITesseract instance = new Tesseract1();
			List<RenderedFormat> list = new ArrayList<RenderedFormat>();
			list.add(RenderedFormat.PDF);
			instance.setLanguage("eng");
			instance.setDatapath("C:\\Program Files (x86)\\Tesseract-OCR");
			instance.createDocuments(
			    "C:/Users/rishabh.jain1/Downloads/Asprise/Double_sentence-spaced_typewriter_text_vs._single-spaced_printed_text_-_vertical_comparison.jpg","E:/results/test", list);
			System.out.println("Inside ExtractDocument");*/
			//String rawText = null;
			String type = mfile.getOriginalFilename().split("\\.")[1];
			//AbbyyDocumentProcesser processer = new AbbyyDocumentProcesser();
			//processer.Run(mfile);
		    //String rawText = testDOCXOCR(mfile.getInputStream());
			File fDir;
			String fname = UUID.randomUUID().toString();
			fDir = new File(fname);
			fDir.mkdirs();
			String hocr = "";
			
			File orgFile = new File(fDir.getPath()+File.separator+mfile.getOriginalFilename());
			orgFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(orgFile);
			fos.write(mfile.getBytes());
			fos.close();
			//mfile.transferTo(orgFile);
			if(type.equalsIgnoreCase("pdf")){
			inputStream = processPDFDocument(orgFile, fDir , document);
			}
			else{
				
				inputStream = processImage(orgFile, fDir , document);
			}
			/*ExecutorService executor = Executors.newFixedThreadPool(3);
			
		    for (int page = 0; page < doc.getNumberOfPages() ; page++) {
				
		    	PDDocument pd = new PDDocument();
		    	pd.addPage(doc.getPage(page));
		    	pd.save(fDir.getPath()+File.separator+"page_"+(page+1));
		    	
		    	 File f = new File(fDir.getPath()+File.separator+"page_"+(page+1));
	        	 HOCRConveter hcov = new HOCRConveter(f , pd);
	        	 executor.execute(hcov);
			}
		    
           for (int page = 0; page < doc.getNumberOfPages() ; page++) {
				
        	   File f = new File(fDir.getPath()+File.separator+"_page_"+(page+1));
        	   HOCRConveter hcov = new HOCRConveter(f);
        	   executor.execute(hcov);
           
			}
           
           executor.shutdown();
           // Wait until all threads are finish
           executor.awaitTermination(90, TimeUnit.MINUTES);
           
           System.out.println("Finished all threads");*/
           
           
	    	//InputStream stream = mfile.getInputStream();
	    	//hocr = testDOCXOCR(mfile.getInputStream());
	    	
	    	
	    	//helper.makePdfSearchable(stream, "test");
	    	/*PDDocument originalDoc = PDDocument.load(stream);
	    	//File hocrFile = new File("E:\\Tika_WorkSpace\\test\\test1.hocr");
	    	new OcrService().hocr2pdf(hocr,originalDoc,fDir);
	    	stream.close();*/
		    
	    	//new DocumentProcesser(mfile);
			//t.start();
		    /*File tempFile = File.createTempFile("temp", ".pdf");
		    
		    FileUtils.copyInputStreamToFile(mfile.getInputStream(), tempFile);*/
		   
		    //rawText = getPdfText(mfile.getInputStream());
		    
		    /*tempFile.delete();*/
			/*if (type.equalsIgnoreCase("jpg") || type.equalsIgnoreCase("bmp")|| type.equalsIgnoreCase("png")) {
			    rawText = testDOCXOCR(mfile.getInputStream());
			} else if (type.equalsIgnoreCase("pdf")) {
				rawText = testDOCXOCR(mfile.getInputStream());
		    }  else if (type.equalsIgnoreCase("gif")) {
                rawText = testDOCXOCR(mfile.getInputStream());
            }  else {
				rawText = testDOCXOCR(mfile.getInputStream());
			}
			rawText = getRawText(mfile.getInputStream());*/
		     //String fname = mfile.getOriginalFilename().split("\\.")[0];
		    // rawText = makePdfSearchable(mfile.getInputStream() , fname);
		    /// File file = new File("E://results//"+fname+".txt");
		     
		    // OutputStream out = new FileOutputStream(file);
			//PrintWriter out = new PrintWriter("D://text.txt");
			
			 //out.write(rawText.getBytes());
			
			 //out.close();
			
			//String languages = getLanguage(rawText);
			//document.setRawText(rawText);
			//document.setLanguageName(languages);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return inputStream;
	}

	private boolean checkIfExecutableInPath(String exec) {
		String path = System.getenv("PATH");
		if (StringUtils.isNotBlank(path)) {
			for (String dir : path.split(":")) {
				if (new File(dir, exec).exists()) {
					return true;
				}
			}
		}
		return false;
	}
	
	
	
   public String readTextFile(){
	   String sCurrentLine;
	   String  text = "";
	   
	   try (BufferedReader br = new BufferedReader(new FileReader("E:\\Tika_WorkSpace\\test.hocr"))) {

			while ((sCurrentLine = br.readLine()) != null) {
				text = text +sCurrentLine + "\n";
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	   
	   return text;
   }

	
	
	
	 
	  public String testPDFOCR(InputStream stream ) throws Exception {
	        TesseractOCRConfig config = new TesseractOCRConfig();
	        config.setTesseractPath("C:\\Program Files (x86)\\Tesseract-OCR");
	        config.setOutputType(TesseractOCRConfig.OUTPUT_TYPE.HOCR);
	        String s ="";
	        Parser parser = new AutoDetectParser();
	        BodyContentHandler handler = new BodyContentHandler();
	        Metadata metadata = new Metadata();

	        PDFParserConfig pdfConfig = new PDFParserConfig();
	        //pdfConfig.setExtractInlineImages(true);
	        pdfConfig.setOcrStrategy(OCR_STRATEGY.NO_OCR);
	        
	        ParseContext parseContext = new ParseContext();
	        parseContext.set(TesseractOCRConfig.class, config);
	        parseContext.set(Parser.class, new TesseractOCRParser());
	        parseContext.set(PDFParserConfig.class, pdfConfig);

	        
	        try {
	            parser.parse(stream, handler, metadata, parseContext);
	            s = handler.toString();
	        } finally {
	            stream.close();
	        }
	        
	        return s;
	    }
	
	

	/*private String getLanguage(String content) {
		String languages = null;
		LanguageDetector lDetector;

		try {
			lDetector = new OptimaizeLangDetector().loadModels();

			lDetector.addText(content);
			List<LanguageResult> detect = lDetector.detectAll();

			for (LanguageResult l : detect) {
				if (!l.isUnknown()) {
					if (languages != null) {
						languages = languages + "," + l.getLanguage() + "(score=" + l.getRawScore() + ")";
					} else {
						languages = l.getLanguage() + "(score=" + l.getRawScore() + ")";
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return languages;
	}*/

	

	private String extract(MultipartFile imageFile) {
		String result = null;
		
		try {
			// Tesseract instance = new Tesseract();
//			System.setProperty("java.library.path", "/lib");
//			System.loadLibrary("native-libs");
			Tesseract instance = new Tesseract();
			//ImageIO.scanForPlugins();
			instance.setLanguage("eng");
			/*ClassLoader classLoader = getClass().getClassLoader();
			URL url = classLoader.getResource("tessdata");
			String path = url.getPath();
			File f = new File(path);
			instance.setDatapath(f.getAbsolutePath());*/
			//File tessDataFolder = LoadLibs.extractTessResources("tessdata"); 
	        instance.setDatapath("C:\\Program Files (x86)\\Tesseract-OCR");
			instance.setOcrEngineMode(3);
			instance.setTessVariable("tessedit_create_hocr", "1");
			instance.setTessVariable("tessedit_char_whitelist",
					"0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.,/?_!@#$%&*()\\:'\"-=+[]{}<>~`");
			instance.setPageSegMode(6);
			// instance.setConfigs(configs);
			File file = new File(imageFile.getOriginalFilename());
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(imageFile.getBytes());
			fos.close();
			//result = instance.doOCR(file);
			result = "";
			List<ITesseract.RenderedFormat> formats = new ArrayList<>();
			formats.add(ITesseract.RenderedFormat.PDF);
			formats.add(ITesseract.RenderedFormat.TEXT);
			String fname = imageFile.getOriginalFilename().substring(0, imageFile.getOriginalFilename().lastIndexOf('.'));
			instance.createDocuments(file.getPath(), "Result"+fname, formats);
			file.delete();
		} catch (TesseractException e) {
			logger.error(e.getMessage(), e);
			throw new RestAPIException("Unable to extract text from file");
		} catch (IllegalStateException e) {
			logger.error(e.getMessage(), e);
			throw new RestAPIException("Unable to extract text from file");
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new RestAPIException("Unable to extract text from file");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RestAPIException("Unable to extract text from file");
		}
		return result;
	}

	/*private MultipartFile convertXpsToPdf(InputStream stream) {
		System.loadLibrary("PDFNetC");
		MultipartFile multipartFile = null;
		File tempFile = null;
		try {
			PDFDoc doc = new PDFDoc();
			System.out.println("Converting XPS document to PDF");
			Convert.fromXps(doc, IOUtils.toByteArray(stream));
			System.out.println(doc.getDocInfo().toString());
			String tempFileLocation = this.getClass().getResource("/output").getFile() + File.separator;
			String tempFileName = tempFileLocation + System.currentTimeMillis() + ".pdf";
			tempFile = new File(tempFileName);
			System.out.println(tempFile.getAbsolutePath());
			FileOutputStream outputStream = new FileOutputStream(tempFile);
			doc.save(outputStream, SDFDoc.e_linearized, null);
			outputStream.close();
			DiskFileItem fileItem = new DiskFileItem("file", "text/plain", false, tempFile.getName(),
					(int) tempFile.length(), tempFile.getParentFile());
			try (OutputStream out = fileItem.getOutputStream(); InputStream in = new FileInputStream(tempFile)) {
				IOUtils.copy(in, out);
				in.close();
				out.close();
			}
			multipartFile = new CommonsMultipartFile(fileItem);
		} catch (PDFNetException e) {
			logger.error(e.getMessage(), e);
			throw new RestAPIException("Unable to convert Xps file to PDF");
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new RestAPIException("Unable to write PDF file");
		} finally {
			// System.out.println("Delete Temp File "+tempFile.delete());
		}
		return multipartFile;
	}*/
	
   /* public String getTessBaseAPIGetUTF8Text(InputStream ins) throws Exception {
    	TessAPI1.TessBaseAPI handle = TessAPI1.TessBaseAPICreate();
        System.out.println("TessBaseAPIGetUTF8Text");
        String expResult = "Downloading";
        String lang = "eng";
          File gif = new File("D:\\failed\\giphy.gif");
         // require jai-imageio lib to read gif
        BufferedImage image = ImageIO.read(ins);
        ByteBuffer buf = ImageIOHelper.convertImageData(image);
        int bpp = image.getColorModel().getPixelSize();
        int bytespp = bpp / 8;
        int bytespl = (int) Math.ceil(image.getWidth() * bpp / 8.0);
        TessAPI1.TessBaseAPIInit3(handle, "C:\\Program Files (x86)\\Tesseract-OCR\\", lang);
        TessAPI1.TessBaseAPISetPageSegMode(handle, TessAPI1.TessPageSegMode.PSM_AUTO);
        TessAPI1.TessBaseAPISetImage(handle, buf, image.getWidth(), image.getHeight(), bytespp, bytespl);
        TessAPI1.TessBaseAPISetRectangle(handle, 0, 0, 1024, 800);
        Pointer utf8Text = TessAPI1.TessBaseAPIGetUTF8Text(handle);
        String result = utf8Text.getString(0);
        TessAPI1.TessDeleteText(utf8Text);
        System.out.println(result);
		return result;
    }*/
    
    public String testDOCXOCR(InputStream stream) throws Exception {
        return testBasicOCR(stream);
    }
    
    private String testBasicOCR(InputStream stream) throws Exception{
        String contents = runOCR(stream,
        		BasicContentHandlerFactory.HANDLER_TYPE.XML, TesseractOCRConfig.OUTPUT_TYPE.HOCR);
        return contents;
    }
    
   
    
    private String runOCR(InputStream stream,
            BasicContentHandlerFactory.HANDLER_TYPE handlerType, TesseractOCRConfig.OUTPUT_TYPE outputType)
            throws Exception {
        TesseractOCRConfig config = new TesseractOCRConfig();
        config.setOutputType(outputType);
        //config.setTesseractPath("C:\\Program Files (x86)\\Tesseract-OCR");
         //config.setTesseractPath("/usr/local/share/tessdata");
        config.setEnableImageProcessing(1);
        config.setPreserveInterwordSpacing(true);
       
        //config.setLanguage("eng");
     
        config.setPageSegMode("6");
          
       
        

        Parser parser = new RecursiveParserWrapper(new AutoDetectParser(),
                new BasicContentHandlerFactory(handlerType, -1));
       
       

        PDFParserConfig pdfConfig = new PDFParserConfig();
        //pdfConfig.setExtractInlineImages(true);
        pdfConfig.setOcrStrategy(OCR_STRATEGY.NO_OCR);
       
        ParseContext parseContext = new ParseContext();
        parseContext.set(TesseractOCRConfig.class, config);
        parseContext.set(Parser.class, new TesseractOCRParser());
        //parseContext.set(Parser.class, parser);
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
    
    
    public FileInputStream processPDFDocument(File mfile , File fDir , Document doc) throws InvalidPasswordException, IOException, InterruptedException{
    	
    	String contains = "";
    	Map<Integer, String> ovmap = new ConcurrentHashMap<Integer, String>();
    	List<PDFPageProcesser> pdfPageProcessers = new ArrayList<>();
    	contains = pdfExtractor.extract(new FileInputStream(mfile));
    	
    	InputStream stream = new FileInputStream(mfile);
    	PDDocument document = PDDocument.load(stream);
    	int page = 1;
        ExecutorService executor = Executors.newFixedThreadPool(3);
    	for (final PDPage pdPage : document.getPages())
    	{
    		
    		PDFPageProcesser processer = new PDFPageProcesser(pdPage, page, fDir, ovmap , imageExtractor , conveter);
    		pdfPageProcessers.add(processer);
    		executor.execute(processer);
    	    page++;
    	}
    	executor.shutdown();
        // Wait until all threads are finish
        executor.awaitTermination(90, TimeUnit.MINUTES);
        System.out.println("Finished all threads");
        
        for (final PDFPageProcesser processer : pdfPageProcessers)
    	{
    		
    		contains =  contains + processer.getContains() + "\n";
    	}
        
        addTextLayer(document, fDir , ovmap);
        document.close();
        stream.close();
        doc.setRawText(contains);
        writeTextFile(fDir, contains);
        
        /*File zipFile = new File(fDir.getPath() + File.separator + "result.zip");
		FileOutputStream fos = new FileOutputStream(zipFile);
		ZipOutputStream out = new ZipOutputStream(fos);
		
		addToZipFile(fDir, "result.txt", out);
		addToZipFile(fDir, "final.pdf", out);
		out.close();
		fos.close();*/
        return new FileInputStream(createZipFile(fDir));
        //return new FileInputStream(new File(fDir.getPath()+File.separator+"final.pdf"));
    }
    
    
    
    public File createZipFile(File fDir) throws IOException{
    	
    	File zipFile = new File(fDir.getPath() + File.separator + "result.zip");
		FileOutputStream fos = new FileOutputStream(zipFile);
		ZipOutputStream out = new ZipOutputStream(fos);
		
		addToZipFile(fDir, "result.txt", out);
		addToZipFile(fDir, "result.pdf", out);
		out.close();
		fos.close();
		return zipFile;
    }
    
    
    
   public FileInputStream processImage(File image , File fDir , Document doc) throws InterruptedException, IOException{
	
	   String type = image.getName().substring(image.getName().lastIndexOf('.')+1);
	   File pdfFile = null;
	   if(type.equalsIgnoreCase("TIFF") || type.equalsIgnoreCase("TIF")){
		 pdfFile = PdfBoxUtils.createPdfFromTiff(image, image.getName().
				   substring(0, image.getName().lastIndexOf(".")), fDir);
	   }
	   else{
	   pdfFile = PdfBoxUtils.createPdfFromImage(image, image.getName().
			   substring(0, image.getName().lastIndexOf(".")), fDir);
	   }
	   return processPDFDocument(pdfFile, fDir, doc);
    }
    
    /*public FileInputStream processImage(File image , File fDir , Document doc) throws FileNotFoundException, IOException{
    	
    	try {
			DocProcesser.processImage(image);
		} catch (TesseractException e) {
			System.out.println("Unable to process image");
		}
    	return new FileInputStream(createZipFile(fDir));
    }*/
    
    
    
	public void writeTextFile(File fDir, String contains) {

		File file = new File(fDir.getPath() + File.separator + "result.txt");

		PrintWriter out = null;
		try {
			file.createNewFile();
			out = new PrintWriter(file);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		out.write(contains);
		out.close();

	}
	
	
	public void getResultZipfile(File fDir, ZipOutputStream out) throws IOException{
		
		
		
		addToZipFile(fDir, "result.txt", out);
		addToZipFile(fDir, "final.pdf", out);
		
			
	}
	
	
	public void addToZipFile(File fDir, String fileName, ZipOutputStream zos) throws FileNotFoundException, IOException {

		System.out.println("Writing '" + fileName + "' to zip file");

		File file = new File(fDir.getPath() + File.separator + fileName);
		FileInputStream fis = new FileInputStream(file);
		ZipEntry zipEntry = new ZipEntry(fileName);
		zos.putNextEntry(zipEntry);

		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}

		zos.closeEntry();
		fis.close();
	}

    
    
    
    /*public static void main(String[] args) throws Exception {
		
    	
    	InputStream stream = new FileInputStream(new File("C:\\Users\\rishabh.jain1\\Downloads\\LargeExcelFiles\\Pdf _files\\kik.pdf"));
    	
    	
    	File fDir;
		String fname = UUID.randomUUID().toString();
		fDir = new File(fname);
		fDir.mkdirs();
		
		
    	
    	TextExtractionServiceHelper helper = new TextExtractionServiceHelper();
    	helper.getPDfImages(stream , fDir);
    	
    	//String hocr = helper.testDOCXOCR(stream);
    	//System.out.println(hocr);
    	
    	//helper.makePdfSearchable(stream, "test");
    	PDDocument originalDoc = PDDocument.load(new File("C:\\Users\\rishabh.jain1\\Downloads\\LargeExcelFiles\\Pdf _files\\Electromechanical.pdf"));
    	originalDoc.save("test.pdf");
    	//File fDir = new File("E:\\Tika_WorkSpace\\ocr-tika-service-dev\\build\\libs\\2e6ae95d-6bf6-4e6e-a164-c38a22c77010");
    	//new TextExtractionServiceHelper().addTextLayer(originalDoc, fDir);
    	//File hocrFile = new File("E:\\Tika_WorkSpace\\test\\test1.hocr");
    	//new OcrService().hocr2pdfnew(hocr, originalDoc, 0, new File("C:\\Users\\rishabh.jain1\\Downloads\\LargeExcelFiles\\Pdf _files"), 73.5f, 368.91f);
    
    	stream.close();
    	
    	File file;
		String fname = UUID.randomUUID().toString();
		file = new File(fname);
		file.mkdirs();
    	
    	
    	PDDocument overlayDoc = PDDocument.load(new File("pdfBoxHelloWorld.pdf"));
    	overlayDoc.getPage(0).setMediaBox(originalDoc.getPage(0).getMediaBox());
    	Overlay overlayObj = new Overlay();
        overlayObj.setOverlayPosition(Overlay.Position.FOREGROUND);
        overlayObj.setInputPDF(originalDoc);
        overlayObj.setAllPagesOverlayPDF(overlayDoc);
        Map<Integer, String> ovmap = new HashMap<Integer, String>();
        PDDocument finalPdfDoc = overlayObj.overlay(ovmap);
        finalPdfDoc.save("E:\\Tika_WorkSpace\\test\\test1_final.PDF");
        overlayDoc.close();
        originalDoc.close();
	}*/
    
	
}
