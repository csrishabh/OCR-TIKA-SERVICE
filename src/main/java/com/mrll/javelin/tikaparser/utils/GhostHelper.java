package com.mrll.javelin.tikaparser.utils;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.ghost4j.document.PDFDocument;
import org.ghost4j.renderer.SimpleRenderer;

import net.sourceforge.tess4j.util.ImageIOHelper;

public class GhostHelper {
	
	
	 public File getTiffFromPdf(String path,PDFDocument document) {
		 
		 File[] pngFiles = null;
		 File tiffFile = null ;
	      
		 try {
	         // create renderer
	         SimpleRenderer renderer = new SimpleRenderer();
	 
	         // set resolution (in DPI)
	         renderer.setResolution(300);
	         //renderer.setMaxProcessCount(4);
	        
	         // render
	         List<Image> images = renderer.render(document);
	         pngFiles = new File[images.size()];
	         // write images to files to disk as PNG
	            try {
	                for (int i = 0; i < images.size(); i++) {
	                    ImageIO.write((RenderedImage) images.get(i), "png", new File(path + File.separator + (i + 1) + ".png"));
	                    pngFiles[i] = new File(path + File.separator + (i + 1) + ".png");
	                }
	                //tiffFile = File.createTempFile("multipage", ".tif");
	                tiffFile = new File(path+File.separator+"multipage.tif");
	                ImageIOHelper.mergeTiff(pngFiles, tiffFile);
	                
	            } catch (IOException e) {
	                System.out.println("ERROR: " + e.getMessage());
	            }
	 
	       } catch (Exception e) {
	         System.out.println("ERROR: " + e.getMessage());
	       }
		 
		/* finally {
             if (pngFiles != null) {
                 // delete temporary PNG images
                 for (File tempFile : pngFiles) {
                     tempFile.delete();
                 }
             }
         }*/
	 
	    return tiffFile;
	    }
	 
	 
	/* public static void main(String[] args) throws FileNotFoundException, IOException {
		
		 PDFDocument doc = new PDFDocument();
         File file = new File("C:\\Users\\rishabh.jain1\\Downloads\\LargeExcelFiles\\Pdf _files\\MobileTesting_done.pdf");
		 doc.load(file);
		 
		 GhostHelper helper = new GhostHelper();
		 
		 helper.getTiffFromPdf(file.getParent(),doc);
	}*/
		

}
