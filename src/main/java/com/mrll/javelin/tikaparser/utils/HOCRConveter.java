package com.mrll.javelin.tikaparser.utils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Component;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;

@Component
public class HOCRConveter {
	
	PDFont font;
	
	
	public HOCRConveter() {

    this.font = PDType1Font.TIMES_ROMAN;
	}

	
	public String convert(String hocrFile, PDDocument document, PDPage page, float imageX, float imageY,
			float imageH, float imageW) throws IOException {

		Source source = new Source(hocrFile);
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

		//StartTag pageTag = source.getNextStartTag(0, "class", "ocr", false);

		//if (pageTag != null) {
			Source pageSource = new Source(hocrFile);

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

				imageXPosition = imageX;
				imageYPosition = imageY;

				float diffX = imageW / imageWidth;
				float diffY = imageH / imageHeight;

				PDPageContentStream contentStream = new PDPageContentStream(document, page,
						PDPageContentStream.AppendMode.APPEND, true);

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

						if (line != null && !line.equals("")) {
							text = text + " "+ line;
							float bboxWidthPt = ((coordinates[2] - coordinates[0]) * diffX);
							float bboxHeightPt = ((coordinates[3] - coordinates[1]) * diffY);
							try {

								boolean textScaled = false;
								do {
									float lineWidth = font.getStringWidth(line) / 1000
											* bboxHeightPt;
									if (lineWidth < bboxWidthPt) {
										textScaled = true;
									} else {
										bboxHeightPt -= 0.1f;
									}
								} while (textScaled == false);

								// put text in the document

								contentStream.setFont(font, bboxHeightPt);
								contentStream.beginText();
								contentStream.newLineAtOffset((float) (imageXPosition + (coordinates[0] * diffX)),
										(float) ((imageHeight - coordinates[3]) * diffY) + imageYPosition);

								contentStream.showText(line);
								contentStream.endText();
							} catch (Exception e) {

								System.out.println(line + " is not available in this font");
							}

						}
					}
					ocrLineTag = pageSource.getNextStartTag(ocrLineTag.getEnd(), "class", "ocrx_word", false);
				}
				contentStream.close();

			}
		//}

		return text;

	}

}
