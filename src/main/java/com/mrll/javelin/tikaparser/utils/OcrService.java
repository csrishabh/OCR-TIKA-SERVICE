package com.mrll.javelin.tikaparser.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.multipdf.Overlay;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.CMYKColor;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;

/**
 * @author Federico Tarantino
 */
public class OcrService {

	/**
	 * from hocr html and image create a pdf, with itext and jericho
	 * 
	 * @param hocrFile
	 *            (html hocr file, generated with tesseract or other ocr
	 *            software)
	 * @param inputFile
	 *            (source image file)
	 * @param outputFile
	 *            (outputstream where write pdf)
	 * @throws IOException
	 */
	
public String hocr2pdfnew(String hocrFile, PDDocument document , PDPage page , float imageX , float imageY , float imageH , float imageW) throws IOException {
        
		Source source = new Source(hocrFile);
		// The resolution of a PDF file (using iText) is 72pt per inch
		//float pointsPerInch = 72.0f;
		float imageWidth = 0f;
		float imageHeight = 0f;
		float imageXPosition = 0f;
		float imageYPosition = 0f;
		String text = "";

		// Using the jericho library to parse the HTML file

		// In order to place text behind the recognised text snippets we are
		// interested in the bbox property
		Pattern bboxPattern = Pattern.compile("bbox(\\s+\\d+){4}");
		// This pattern separates the coordinates of the bbox property
		Pattern bboxCoordinatePattern = Pattern.compile("(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)");

		StartTag pageTag = source.getNextStartTag(0, "class", "ocr", false);

		
            if(pageTag != null){
			Source pageSource = new Source(pageTag.getElement());
			
			if (pageSource != null && pageSource.getParseText().toString().contains("ocrx_word")) {
				StartTag ocrImageTag = pageSource.getNextStartTag(0, "class", "ocr_page", false);

				

				Element imageElement = ocrImageTag.getElement();
				Matcher imageMatcher = bboxPattern.matcher(imageElement.getAttributeValue("title"));

				if (imageMatcher.find()) {

					Matcher imageCoordinateMatcher = bboxCoordinatePattern.matcher(imageMatcher.group());

					if (imageCoordinateMatcher.find()) {

						imageWidth = Integer.parseInt((imageCoordinateMatcher.group(3)));
						imageHeight = Integer.parseInt((imageCoordinateMatcher.group(4)));

					}

				}

				imageXPosition = imageX ;
				imageYPosition = imageY;

				
				float diffX = imageW/imageWidth;
				float diffY = imageH/imageHeight;

				PDPageContentStream contentStream = new PDPageContentStream(
					    document, page, PDPageContentStream.AppendMode.APPEND, true
					);

		
				// Only tags of the ocrx_word class are interesting
				StartTag ocrLineTag = pageSource.getNextStartTag(0, "class", "ocrx_word", false);
				while (ocrLineTag != null) {
					Element lineElement = ocrLineTag.getElement();
					Matcher bboxMatcher = bboxPattern.matcher(lineElement.getAttributeValue("title"));
					if (bboxMatcher.find()) {
						// We found a tag of the ocr_line class containing a
						// bbox
						// property
						Matcher bboxCoordinateMatcher = bboxCoordinatePattern.matcher(bboxMatcher.group());
						bboxCoordinateMatcher.find();
						int[] coordinates = { Integer.parseInt((bboxCoordinateMatcher.group(1))),
								Integer.parseInt((bboxCoordinateMatcher.group(2))),
								Integer.parseInt((bboxCoordinateMatcher.group(3))),
								Integer.parseInt((bboxCoordinateMatcher.group(4))) };
						String line = lineElement.getContent().getTextExtractor().toString();
						
                        if(line != null && !line.equals("")){
                        	text = text + line;
                        	float bboxWidthPt = ((coordinates[2] - coordinates[0])  * diffX);
    						float bboxHeightPt = ((coordinates[3] - coordinates[1])  * diffY );
    				try{
    					
						boolean textScaled = false;
						do {
							float lineWidth = PDType1Font.TIMES_ROMAN.getStringWidth(line)/1000 * bboxHeightPt;
							if (lineWidth < bboxWidthPt) {
								textScaled = true;
							} else {
								bboxHeightPt -= 0.1f;
							}
						} while (textScaled == false);

						// put text in the document

						contentStream.setFont(PDType1Font.TIMES_ROMAN, bboxHeightPt);
						contentStream.beginText();
						contentStream.newLineAtOffset((float) (imageXPosition + (coordinates[0] * diffX)),
								(float) ((imageHeight - coordinates[3]) * diffY) + imageYPosition);
						
						contentStream.showText(line);
						contentStream.endText();
						}
						catch(Exception e){
							
							System.out.println(line+" is not available in this font");
						}
						

					}
				}
					ocrLineTag = pageSource.getNextStartTag(ocrLineTag.getEnd(), "class", "ocrx_word", false);
				}
				contentStream.close();
				
		}
            }
			
		return text;	
		
	}
	
	
	public void hocr2pdf(String hocrFile, PDDocument doc , File fDir) throws IOException {
        
		Source source = new Source(hocrFile);
		int pageEnd = 0;
		int pageNo = 0;

		// The resolution of a PDF file (using iText) is 72pt per inch
		float pointsPerInch = 72.0f;
		float imageWidth = 0f;
		float imageHeight = 0f;

		// Using the jericho library to parse the HTML file

		// In order to place text behind the recognised text snippets we are
		// interested in the bbox property
		Pattern bboxPattern = Pattern.compile("bbox(\\s+\\d+){4}");
		// This pattern separates the coordinates of the bbox property
		Pattern bboxCoordinatePattern = Pattern.compile("(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)");

		StartTag pageTag = source.getNextStartTag(pageEnd, "class", "ocr", false);

		while (pageTag != null) {

			Source pageSource = new Source(pageTag.getElement());
			PDDocument document = null;
			if (pageSource.getParseText().toString().contains("ocrx_word")) {
				StartTag ocrImageTag = pageSource.getNextStartTag(0, "class", "ocr_page", false);

				pageEnd = pageTag.getEnd();

				Element imageElement = ocrImageTag.getElement();
				Matcher imageMatcher = bboxPattern.matcher(imageElement.getAttributeValue("title"));

				if (imageMatcher.find()) {

					Matcher imageCoordinateMatcher = bboxCoordinatePattern.matcher(imageMatcher.group());

					if (imageCoordinateMatcher.find()) {

						imageWidth = Integer.parseInt((imageCoordinateMatcher.group(3)));
						imageHeight = Integer.parseInt((imageCoordinateMatcher.group(4)));

					}

				}

				// Load the image
				// Image image = Image.getInstance(inputFile.getAbsolutePath());
				float dotsPerPointX;
				float dotsPerPointY;
				/*
				 * if(image.getDpiX()>0){ dotsPerPointX = image.getDpiX() /
				 * pointsPerInch; dotsPerPointY = image.getDpiY() /
				 * pointsPerInch; } else { dotsPerPointX = 1.0f; dotsPerPointY =
				 * 1.0f; }
				 */

				dotsPerPointX = 300 / pointsPerInch;
				dotsPerPointY = 300 / pointsPerInch;

				float pageImagePixelHeight = imageHeight;

				float orgDocWidth = doc.getPage(pageNo).getBBox().getWidth() * dotsPerPointX;
				float orgDocHight = doc.getPage(pageNo).getBBox().getHeight() * dotsPerPointY;

				float diffX = ((orgDocWidth - imageWidth) / imageWidth) + 1;
				float diffY = ((orgDocHight - imageHeight) / imageHeight) + 1;

				PDRectangle rec = new PDRectangle(0, 0, (imageWidth / dotsPerPointX) * diffX,
						(imageHeight / dotsPerPointY) * diffY);
				document = new PDDocument();

				PDPage page = new PDPage(rec);
				/*
				 * page.getCropBox().setLowerLeftX(0);
				 * page.getCropBox().setLowerLeftY(0);
				 * page.getCropBox().setUpperRightX(imageWidth / dotsPerPointX);
				 * page.getCropBox().setUpperRightY(imageHeight /
				 * dotsPerPointY);
				 */
				// page.setArtBox(rec);
				document.addPage(page);

				PDPageContentStream contentStream = new PDPageContentStream(document, page);

				// first define a standard font for our text
				Font defaultFont = FontFactory.getFont(FontFactory.TIMES, 8, Font.NORMAL, CMYKColor.BLACK);

				// image.scaleToFit(image.getWidth() / dotsPerPointX,
				// image.getHeight() / dotsPerPointY);
				// image.setAbsolutePosition(0, 0);

				// Only tags of the ocrx_word class are interesting
				StartTag ocrLineTag = pageSource.getNextStartTag(0, "class", "ocrx_word", false);
				System.out.println(ocrLineTag.toString());
				while (ocrLineTag != null) {
					Element lineElement = ocrLineTag.getElement();
					Matcher bboxMatcher = bboxPattern.matcher(lineElement.getAttributeValue("title"));
					if (bboxMatcher.find()) {
						// We found a tag of the ocr_line class containing a
						// bbox
						// property
						Matcher bboxCoordinateMatcher = bboxCoordinatePattern.matcher(bboxMatcher.group());
						bboxCoordinateMatcher.find();
						int[] coordinates = { Integer.parseInt((bboxCoordinateMatcher.group(1))),
								Integer.parseInt((bboxCoordinateMatcher.group(2))),
								Integer.parseInt((bboxCoordinateMatcher.group(3))),
								Integer.parseInt((bboxCoordinateMatcher.group(4))) };
						String line = lineElement.getContent().getTextExtractor().toString();
						
                        if(line != null && !line.equals("")){
						float bboxWidthPt = ((coordinates[2] - coordinates[0]) * diffX) / dotsPerPointX;
						float bboxHeightPt = ((coordinates[3] - coordinates[1]) * diffY) / dotsPerPointY;

						// Put the text into the PDF
						// Comment the next line to debug the PDF output
						// (visible
						// Text)
						// Scale the text width to fit the OCR bbox
						boolean textScaled = false;
						do {
							float lineWidth = defaultFont.getBaseFont().getWidthPoint(line, bboxHeightPt);
							if (lineWidth < bboxWidthPt) {
								textScaled = true;
							} else {
								bboxHeightPt -= 0.1f;
							}
						} while (textScaled == false);

						// put text in the document

						contentStream.setFont(PDType1Font.TIMES_ROMAN, bboxHeightPt);
						contentStream.beginText();
						contentStream.newLineAtOffset((float) ((coordinates[0] * diffX / dotsPerPointX)),
								(float) (((pageImagePixelHeight - coordinates[3]) * diffY / dotsPerPointY)));
						try{
						contentStream.showText(line);
						}
						catch(Exception e){
							
							System.out.println(line+" is not available in this font");
						}
						contentStream.endText();

					}
				}
					ocrLineTag = pageSource.getNextStartTag(ocrLineTag.getEnd(), "class", "ocrx_word", false);
				}
				contentStream.close();
				document.save(fDir.getParentFile().getPath()+File.separator+"result_"+fDir.getName());
				document.close();
				
			}

			pageTag = source.getNextStartTag(pageEnd, "class", "ocr", false);
			pageNo++;
			//addTextLayer(doc, document, pageNo, fDir);
		}

		//addTextLayer(doc, document, pageNo+1);
		//deleteFiles(".pdf", "E:\\Tika_WorkSpace\\test", pageNo);
	}

	public void addTextLayer(PDDocument originalDoc , File fDir) throws IOException {
		originalDoc.setAllSecurityToBeRemoved(true);
		Overlay overlayObj = new Overlay();
		overlayObj.setOverlayPosition(Overlay.Position.FOREGROUND);
		overlayObj.setInputPDF(originalDoc);
		// overlayObj.setAllPagesOverlayPDF(overlayDoc);
		Map<Integer, String> ovmap = new HashMap<Integer, String>();
		for (int pageNo = 1; pageNo <= originalDoc.getNumberOfPages(); pageNo++) {
			ovmap.put(pageNo, fDir.getPath()+File.separator+"page_" + pageNo + ".pdf");
		}
		PDDocument finalDoc = overlayObj.overlay(ovmap);
		finalDoc.save(fDir.getParentFile().getPath()+File.separator+"final.pdf");
		finalDoc.close();
		originalDoc.close();
	}
	
	public void addTextLayer(PDDocument originalDoc , PDDocument layerlDoc , int pageNo , File fDir) throws IOException {
		originalDoc.setAllSecurityToBeRemoved(true);
		Overlay overlayObj = new Overlay();
		overlayObj.setOverlayPosition(Overlay.Position.BACKGROUND);
		overlayObj.setInputPDF(originalDoc);
		overlayObj.setAllPagesOverlayPDF(layerlDoc);
		Map<Integer, String> ovmap = new HashMap<Integer, String>();
		PDDocument finalDoc = overlayObj.overlay(ovmap);
		finalDoc.save(fDir.getParentFile().getPath()+File.separator+"final_"+fDir.getName());
		finalDoc.close();
		layerlDoc.close();
		originalDoc.close();
	}

	public void deleteFiles(String type, String path, int page_count) {

		for (int page = 1; page <=page_count; page++) {
			new File(path + File.separator + "page" + "_" + (page) + type).delete();
		}
	}

}
