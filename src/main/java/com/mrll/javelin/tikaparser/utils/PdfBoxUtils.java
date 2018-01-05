package com.mrll.javelin.tikaparser.utils;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.springframework.web.multipart.MultipartFile;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfWriter;
import com.recognition.software.jdeskew.ImageDeskew;

import net.sourceforge.tess4j.util.ImageHelper;
import net.sourceforge.tess4j.util.ImageIOHelper;

public class PdfBoxUtils {
	
	
	/*public static void main(String[] args) {
		
		try {
			//new PdfBoxUtils().convertToTiff(new File("C:\\Users\\rishabh.jain1\\Downloads\\LargeExcelFiles\\Pdf _files\\01 - The C Language_done.pdf"));
			File tiffFile = null ;
			tiffFile = new File("C:\\Users\\rishabh.jain1\\Downloads\\LargeExcelFiles\\JAV-16707\\Images"+File.separator+"multipage.tif");
			
			File[] files = {new File("C:\\Users\\rishabh.jain1\\Downloads\\LargeExcelFiles\\JAV-16707\\1359846393.png"),
					new File("C:\\Users\\rishabh.jain1\\Downloads\\LargeExcelFiles\\JAV-16707\\image01.png"),
					new File("C:\\Users\\rishabh.jain1\\Downloads\\LargeExcelFiles\\JAV-16707\\Miller-series-webtype.png")};
			
			ImageIOHelper.mergeTiff(files, tiffFile);
			new PdfBoxUtils().gifToJpg();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	
	
	public static File createPdfFromImage(File image, String name ,File fDir) throws IOException{
		
		
		 try (PDDocument doc = new PDDocument())
	        {
			    BufferedImage b = ImageIO.read(image);
			    ImageDeskew id = new ImageDeskew(b);
				double imageSkewAngle = id.getSkewAngle(); // determine skew angle
				System.out.println(imageSkewAngle);
				b = ImageHelper.rotateImage(b, -imageSkewAngle);
			    PDImageXObject pdImage =  JPEGFactory.createFromImage(doc, b);
			    //PDImageXObject pdImage = PDImageXObject.createFromFile(image.getPath(),doc);
			    doc.addPage(new PDPage(new PDRectangle(pdImage.getWidth(), pdImage.getHeight())));
	            PDPage page = doc.getPage(0);
	            try (PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true, true))
	            {
	                contentStream.drawImage(pdImage, 0, 0, pdImage.getWidth(), pdImage.getHeight());
	                contentStream.close();
	            }
	            File pdfFile = new File(fDir.getPath()+File.separator+name+".pdf");
	            doc.save(pdfFile);
	            doc.close();
	            return pdfFile;
	        }	 
	}
	
	public void gifToJpg() throws IOException{
		 BufferedImage image=ImageIO.read(new File("E:/giff2.gif"));
	     File file=new File("E:/giff2_1.tiff");
	     file.createNewFile();
	     OutputStream os = new FileOutputStream(file);
	     //ImageIOUtil.writeImage(image, "E:/t7.tiff", 300);
	     ImageIOUtil.writeImage(image, "tiff", os, 300, 1.0F);
	     //ImageIO.write(image,"tiff", file);
		
	}
	
	/*public void getPngFromPdf(File file) throws IOException{
		
		//PDDocument document = PDDocument.load(new File("C:\\Users\\rishabh.jain1\\Downloads\\LargeExcelFiles\\Pdf _files\\Pdf_with_images_done.pdf"));
	    DocumentProcesser processer = new DocumentProcesser();
		PDDocument document = PDDocument.load(file);
		PDFRenderer pdfRenderer = new PDFRenderer(document);
		
		for (int page = 0; page < document.getNumberOfPages(); ++page)
		{ 
			BufferedImage image = pdfRenderer.renderImage(page, 4, ImageType.RGB);
			
			//images[page] = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
			System.out.println("PDFBOXUTILS Processing"+file.getName()+" Page "+page);
			
			ImageIOUtil.writeImage(image, file.getParent()+File.separator+"result" + "-" + (page+1) + ".tif", 300);
			processer.processImage(new File(file.getParent()+File.separator+"result" + "-" + (page+1) + ".tif"));
            System.gc();
            image.flush();
		}
		merge_pdfFiles(file.getParent(),document.getNumberOfPages());
		//deleteFiles(".pdf" ,file.getParent(),document.getNumberOfPages());
		mergeFiles(file.getParent(),document.getNumberOfPages());
		deleteFiles(".txt" ,file.getParent(),document.getNumberOfPages());
		file.delete();
		document.close();
	}*/
	
	public void merge_pdfFiles(String path , int page_count) throws IOException{
		
		PDFMergerUtility ut = new PDFMergerUtility();
		MemoryUsageSetting setting = MemoryUsageSetting.setupMainMemoryOnly();
		
		for (int page = 0; page < page_count; ++page)
		{ 
		  try {
			ut.addSource(new File(path+File.separator+"result" + "-" + (page+1) + ".pdf"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Unable to merge files");
		}   
		}
		ut.setDestinationFileName(path+File.separator+"result_PDF.pdf");
		ut.mergeDocuments(setting);
	}
	
	public void deleteFiles(String type ,String path , int page_count){
		
		for (int page = 0; page < page_count; ++page)
		{ 
			new File(path+File.separator+"result" + "-" + (page+1) + type).delete();
		}
	}
	
	
	/*public File convertToTiff(File file) throws IOException{
		String path = file.getParent();
		File tiffFile = null ;
		tiffFile = new File(path+File.separator+"multipage.tif");
		ImageIOHelper.mergeTiff(getPngFromPdf(file), tiffFile);
		System.out.println("PDFBOXUTILS Processing"+file.getName()+" convert to tiff successfully");
		file.delete();
		return tiffFile;
		
	}*/
	
	
	public static void mergeFiles(String path, int pageCount) throws IOException {
		 
		FileWriter fstream = null;
		BufferedWriter out = null;	
		File mergedFile = new File(path+File.separator+"result_TXT.txt");
		mergedFile.createNewFile();
		try {
			fstream = new FileWriter(mergedFile, true);
			 out = new BufferedWriter(fstream);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
 
		for (int page = 0; page < pageCount; ++page) {
			File f = new File(path+File.separator+"result" + "-" + (page+1) + ".txt");
			System.out.println("merging: " + f.getName());
			FileInputStream fis;
			try {
				fis = new FileInputStream(f);
				BufferedReader in = new BufferedReader(new InputStreamReader(fis));
 
				String aLine;
				while ((aLine = in.readLine()) != null) {
					out.write(aLine);
					out.newLine();
				}
 
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
 
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
 
	}
	
    public static File createPdfFromTiff(File image, String name ,File fDir) throws IOException{
		
    	int pages = tiffToPNG(image, fDir);
    	
    	try (PDDocument doc = new PDDocument())
        {
		    for (int i = 0; i < pages; i++) {
			
		    	File f = new File(fDir.getPath()+File.separator+"image" + "-" +i + ".png");
		    	BufferedImage b = ImageIO.read(f);
			    ImageDeskew id = new ImageDeskew(b);
				double imageSkewAngle = id.getSkewAngle(); // determine skew angle
				System.out.println(imageSkewAngle);
				b = ImageHelper.rotateImage(b, -imageSkewAngle);
				b = ImageHelper.getScaledInstance(b, b.getWidth()*2, b.getHeight()*2);
			    PDImageXObject pdImage =  JPEGFactory.createFromImage(doc, b);
			    doc.addPage(new PDPage(new PDRectangle(pdImage.getWidth(), pdImage.getHeight())));
	            PDPage page = doc.getPage(i);
	            PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true, true);
	            contentStream.drawImage(pdImage, 0, 0, pdImage.getWidth(), pdImage.getHeight());
	            contentStream.close();
	            f.delete();
			}
            File pdfFile = new File(fDir.getPath()+File.separator+name+".pdf");
            doc.save(pdfFile); 
            doc.close();
            return pdfFile;
        }		
    }
	
	public  static int  tiffToPNG(File file , File fDir) throws IOException {

		   // File sourceImageFile = getImageFile("tiff", "test-multi-gray-compression-type-4.tiff");
		    FileInputStream inputStream = new FileInputStream(file);
		    ImageInputStream is = ImageIO.createImageInputStream(inputStream);

		    // get the first matching reader
		    Iterator<ImageReader> iterator = ImageIO.getImageReadersByFormatName("TIFF");
		    
		    ImageReader imageReader = iterator.next();
		    
		    imageReader.setInput(is);

		    // split the multi-page TIFF
		    int pages = imageReader.getNumImages(true);
		    for(int i=0; i<pages; i++) {
		        BufferedImage bufferedImage = imageReader.read(i);
		        ImageIOUtil.writeImage(bufferedImage, fDir.getPath()+File.separator+"image" + "-" +i + ".png", 300);
		    }
		    is.close();
		    inputStream.close();
		    return pages;
  }
	
	
	/*public static void main(String[] args) throws IOException {
		
		File image = new File("C:\\Users\\rishabh.jain1\\Downloads\\Asprise\\Final OCR Image Files\\multipage_tif_example.tif");
		
		File fDir = new File("C:\\Users\\rishabh.jain1\\Downloads\\Asprise\\Final OCR Image Files");
		
		String type = image.getName().substring(image.getName().lastIndexOf('.')+1);
		
		System.out.println(type);
		
		//tiffToPNG(image , fDir);
		
		//addTiffToPDF(image, "test", fDir);
		
		
	}*/

}
