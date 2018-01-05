package com.mrll.javelin.tikaparser.utils;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.mrll.javelin.tikaparser.exception.RestAPIException;
import com.recognition.software.jdeskew.ImageDeskew;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.ImageHelper;

public class DocumentProcesser {

	private static final Logger logger = LoggerFactory.getLogger(DocumentProcesser.class);

	public DocumentProcesser() {

	}

	/*public DocumentProcesser(MultipartFile imageFile) throws IOException {

		File file;
		String fname = UUID.randomUUID().toString() + File.separator + imageFile.getOriginalFilename();
		file = new File(fname);
		file.getParentFile().mkdirs();
		file.createNewFile();
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(imageFile.getBytes());
		fos.close();
		run(file);
	}*/

	/*
	 * public void run(File file, Tesseract instance1) {
	 * 
	 * try { // Tesseract instance = new Tesseract(); //
	 * System.setProperty("java.library.path", "/lib"); //
	 * System.loadLibrary("native-libs"); String type =
	 * file.getName().split("\\.")[1]; String path = file.getParent();
	 * if(type.equals("pdf") || type.equals("PDF") ){
	 * 
	 * PDFDocument doc = new PDFDocument(); doc.load(file); file.delete(); file
	 * = new GhostHelper().getTiffFromPdf(path, doc); file = new
	 * PdfBoxUtils().convertToTiff(file); }
	 * 
	 * Tesseract instance = new Tesseract(); //ImageIO.scanForPlugins();
	 * instance.setLanguage("eng"); ClassLoader classLoader =
	 * getClass().getClassLoader(); URL url =
	 * classLoader.getResource("tessdata"); String path = url.getPath(); File f
	 * = new File(path); instance.setDatapath(f.getAbsolutePath()); //File
	 * tessDataFolder = LoadLibs.extractTessResources("tessdata");
	 * //instance.setDatapath("C:\\Program Files (x86)\\Tesseract-OCR");
	 * instance.setDatapath("/usr/local/share"); instance.setOcrEngineMode(3);
	 * instance.setTessVariable("tessedit_create_hocr", "1");
	 * instance.setTessVariable("tessedit_char_whitelist",
	 * "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.,/?_!@#$%&*()\\:'\"-=+[]{}<>~`"
	 * ); instance.setPageSegMode(6); // instance.setConfigs(configs);
	 * 
	 * //String result = instance.doOCR(file); List<ITesseract.RenderedFormat>
	 * formats = new ArrayList<>(); formats.add(ITesseract.RenderedFormat.PDF);
	 * formats.add(ITesseract.RenderedFormat.TEXT); String output_fname =
	 * path+File.separator+"Result_"+file.getName().substring(0,
	 * file.getName().lastIndexOf('.'));
	 * instance.createDocuments(file.getAbsolutePath(), output_fname, formats);
	 * FileUtils.cleanDirectory(file.getParentFile());
	 * file.getParentFile().delete(); //System.out.println(result);
	 * //file.delete(); System.out.println(file.getName()+" Completed"); } catch
	 * (TesseractException e) { logger.error(e.getMessage(), e); throw new
	 * RestAPIException("Unable to extract text from file"); } catch
	 * (IllegalStateException e) { logger.error(e.getMessage(), e); throw new
	 * RestAPIException("Unable to extract text from file"); } catch (Exception
	 * e) { logger.error(e.getMessage(), e); throw new
	 * RestAPIException("Unable to extract text from file"); }
	 * 
	 * }
	 */

	/*public void run(File file) {

		try {
			// Tesseract instance = new Tesseract();
			// System.setProperty("java.library.path", "/lib");
			// System.loadLibrary("native-libs");
			String type = file.getName().substring(file.getName().lastIndexOf('.'), file.getName().length());
			// String path = file.getParent();
			if (type.equals(".pdf") || type.equals(".PDF")) {

				new PdfBoxUtils().getPngFromPdf(file);
			} else {
				processImage(file);
			}

		} catch (IOException e) {

		}
	}*/

	public void processImage(File file) throws IOException, TesseractException {

		
			String type = file.getName().split("\\.")[1];
			String path = file.getParent();
			Tesseract instance = new Tesseract();
			// ImageIO.scanForPlugins();
			instance.setLanguage("eng");
			instance.setDatapath("C:\\Program Files (x86)\\Tesseract-OCR");
			// instance.setDatapath("/usr/local/share");
			instance.setOcrEngineMode(3);
			// instance.setTessVariable("tessedit_create_hocr", "1");
			instance.setTessVariable("tessedit_char_whitelist",
					"0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.,/?_!@#$%&*()\\:'\"-=+[]{}<>~`");
			instance.setPageSegMode(6);
			instance.setHocr(true);

			// String result = instance.doOCR(file);
			List<ITesseract.RenderedFormat> formats = new ArrayList<>();
			formats.add(ITesseract.RenderedFormat.PDF);
			formats.add(ITesseract.RenderedFormat.TEXT);
			formats.add(ITesseract.RenderedFormat.HOCR);

			System.out.println("File Processing Started");
			if (!type.equals("tiff") && !type.equals("TIFF") && !type.equals("tif") & !type.equals("TIF")) {
				file = getProcessedFile(file);
			}
			System.out.println("File Processing Done");
			String output_fname = path + File.separator + "result";
			synchronized (DocumentProcesser.class) {
				instance.createDocuments(file.getAbsolutePath(), output_fname, formats);

			}
			// FileUtils.cleanDirectory(file.getParentFile());
			// file.getParentFile().delete();
			// System.out.println(result);
			// file.delete();
			System.out.println(file.getName() + " Completed"); 

	}

	public String getHocr(File file) {
		String result = "";
		try {
			Tesseract instance = new Tesseract();
			// ImageIO.scanForPlugins();
			instance.setLanguage("eng");
			instance.setDatapath("C:\\Program Files (x86)\\Tesseract-OCR");
			// instance.setDatapath("/usr/local/share");
			instance.setOcrEngineMode(3);
			// instance.setTessVariable("tessedit_create_hocr", "1");
			instance.setTessVariable("tessedit_char_whitelist",
					"0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.,/?_!@#$%&*()\\:'\"-=+[]{}<>~`");
			instance.setPageSegMode(6);
			//instance.setHocr(true);
            synchronized (DocumentProcesser.class){
			result = instance.doOCR(file);
            }
			
		}catch (IllegalStateException e) {
			logger.error(e.getMessage(), e);
			throw new RestAPIException("Unable to extract text from file");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RestAPIException("Unable to extract text from file");
		}
		
		return result;

	}

	public File getProcessedFile(File file) throws IOException {

		BufferedImage bi = ImageIO.read(file);

		ImageDeskew id = new ImageDeskew(bi);
		double imageSkewAngle = id.getSkewAngle(); // determine skew angle
		System.out.println(imageSkewAngle);
		System.out.println("Width : " + bi.getWidth());
		System.out.println("Height : " + bi.getHeight());
		// bi = RemoveNoise(bi);
		bi = scaleImage(bi, bi.getWidth() * 2, bi.getHeight() * 2);
		bi = ImageHelper.rotateImage(bi, -imageSkewAngle);
		/*if(imageSkewAngle < 0){
			bi = ImageHelper.rotateImage(bi, -imageSkewAngle);
		}*/
		// bi = scaleImage(bi, bi.getWidth()*2, bi.getHeight()*2 , Color.WHITE);
		file.delete();
		String output_fname = file.getParent() + File.separator
				+ file.getName().substring(0, file.getName().lastIndexOf('.'));
		File Processedfile = new File(output_fname + ".png");
		Processedfile.createNewFile();
		OutputStream os = new FileOutputStream(Processedfile);
		ImageIOUtil.writeImage(bi, "png", os, 300, 1.0F);
		os.close();
		return Processedfile;

	}

	public void testDoOCR_SkewedImage() throws Exception {
		logger.info("doOCR on a skewed PNG image");

		Tesseract instance = new Tesseract();
		// ImageIO.scanForPlugins();
		instance.setLanguage("eng");
		instance.setDatapath("C:\\Program Files (x86)\\Tesseract-OCR");
		// instance.setDatapath("/usr/local/share");
		instance.setOcrEngineMode(3);
		// instance.setTessVariable("tessedit_create_hocr", "1");
		instance.setTessVariable("tessedit_char_whitelist",
				"0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.,/?_!@#$%&*()\\:'\"-=+[]{}<>~`");
		// instance.setPageSegMode(6);
		instance.setHocr(true);

		File imageFile = new File("C:\\Users\\rishabh.jain1\\Downloads\\Asprise\\giff.gif");
		BufferedImage bi = ImageIO.read(imageFile);

		ImageDeskew id = new ImageDeskew(bi);
		double imageSkewAngle = id.getSkewAngle(); // determine skew angle
		System.out.println(imageSkewAngle);
		System.out.println("Width : " + bi.getWidth());
		System.out.println("Height : " + bi.getHeight());
		// bi = RemoveNoise(bi);
		bi = scaleImage(bi, bi.getWidth() * 2, bi.getHeight() * 2);
		bi = ImageHelper.rotateImage(bi, -imageSkewAngle);
		// bi = scaleImage(bi, bi.getWidth()*2, bi.getHeight()*2 , Color.WHITE);
		File file = new File("E:/test.tiff");
		file.createNewFile();
		OutputStream os = new FileOutputStream(file);
		ImageIOUtil.writeImage(bi, "tiff", os, 300, 1.0F);

		if ((imageSkewAngle > 180 || imageSkewAngle < -(90))) {
			bi = ImageHelper.rotateImage(bi, -imageSkewAngle);
			// deskew image
		}
		String result = instance.doOCR(bi);
		logger.info(result);
	}

	public BufferedImage scaleImage(BufferedImage img, int width, int height) {
		int imgWidth = img.getWidth();
		int imgHeight = img.getHeight();
		if (imgWidth * height < imgHeight * width) {
			width = imgWidth * height / imgHeight;
		} else {
			height = imgHeight * width / imgWidth;
		}

		System.out.println("New Width : " + width);
		System.out.println("New Height : " + height);
		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		Graphics2D g = newImage.createGraphics();
		try {
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.clearRect(0, 0, width, height);
			g.drawImage(img, 0, 0, width, height, null);
		} finally {
			g.dispose();
		}
		return newImage;
	}

	public BufferedImage scale(final BufferedImage image, final int targetW, final int targetH) {
		final int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();
		final BufferedImage scaledImg = new BufferedImage(targetW, targetH, type);
		final Graphics2D g = scaledImg.createGraphics();
		g.drawImage(image, 0, 0, targetW, targetH, null);
		g.dispose();
		g.setComposite(AlphaComposite.Src);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		return scaledImg;
	}

	public BufferedImage RemoveNoise(BufferedImage bmap) {

		for (int x = 0; x < bmap.getWidth(); x++) {
			for (int y = 0; y < bmap.getHeight(); y++) {
				Color pixel = new Color(bmap.getRGB(x, y));
				if (pixel.getRed() < 162 && pixel.getGreen() < 162 && pixel.getBlue() < 162)
					bmap.setRGB(x, y, Color.BLACK.getRGB());
				else if (pixel.getRed() > 162 && pixel.getGreen() > 162 && pixel.getBlue() > 162)
					bmap.setRGB(x, y, Color.WHITE.getRGB());
			}
		}

		return bmap;
	}

	/*public static void main(String[] args) {
		File f = new File("E:\\Original Files\\TiltedTextPNG.png");
		try {
			 new DocumentProcesser().processImage(f);
			 //System.out.println(hocr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/

}
