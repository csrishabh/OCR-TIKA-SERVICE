package com.mrll.javelin.tikaparser.utils;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.apache.pdfbox.util.Matrix;
import org.springframework.beans.factory.annotation.Autowired;

import com.mrll.javelin.tikaparser.extractor.Extractor;

public class PDFPageProcesser extends PDFGraphicsStreamEngine implements Runnable

{

	@Autowired
	Extractor extractor;

	@Autowired
	HOCRConveter conveter;

	PDDocument txtDoc;
	PDPage txtPage;
	PDPage page;
	int pageNo;
	int index;
	File fDir;
	Map<Integer, String> ovmap;
	String text = "";
	Point2D currentPoint;
	
	//DocumentProcesser processer;
	
	public PDFPageProcesser(PDPage page, int pageNo, File fDir, Map<Integer, String> ovmap, Extractor extractor,
			HOCRConveter conveter) {

		super(page);
		this.page = page;
		this.pageNo = pageNo;
		this.fDir = fDir;
		this.ovmap = ovmap;
		this.conveter = conveter;
		this.extractor = extractor;
		PDRectangle rec = new PDRectangle(0, 0, page.getMediaBox().getWidth(), page.getMediaBox().getHeight());
		txtDoc = new PDDocument();
		txtPage = new PDPage(rec);
		txtDoc.addPage(txtPage);
		this.index = 0;
		this.currentPoint = new Point();
		//this.processer = new DocumentProcesser();
	}

	public String getContains() {
		return text;
	}

	@Override
	public void appendRectangle(Point2D p0, Point2D p1, Point2D p2, Point2D p3) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawImage(PDImage pdImage) {
		File file = null;
		if (pdImage instanceof PDImageXObject) {

			PDImageXObject image = (PDImageXObject) pdImage;
			Matrix ctmNew = getGraphicsState().getCurrentTransformationMatrix();
			if (image.getHeight() >= 10 && image.getWidth() >= 10) {

				try {
					System.out.println(
							"image Type_" + image.getSuffix() + "_" + image.getHeight() + "_" + image.getWidth());
					file = File.createTempFile(String.format("10948-new-engine-%s-%s-", pageNo, index), ".png");
					//file = new File(String.format("10948-new-engine-%s-%s-", pageNo, index)+".png");
					ImageIOUtil.writeImage(image.getImage(), "PNG", new FileOutputStream(file));
					String hocr = extractor.extract(new FileInputStream(file));
					//String hocr = processer.getHocr(file);
					text = text + " " + conveter.convert(hocr, txtDoc, txtPage, ctmNew.getTranslateX(),
							ctmNew.getTranslateY(), ctmNew.getScalingFactorY(), ctmNew.getScalingFactorX());
					if (text != null && !text.trim().equals("")) {
						ovmap.put(pageNo, fDir.getPath() + File.separator + "result_page_" + pageNo);
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					file.delete();
				}
				index++;
			}
		}

	}

	@Override
	public void clip(int windingRule) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void moveTo(float x, float y) throws IOException {
		// TODO Auto-generated method stub

		currentPoint.setLocation(x, x);
	}

	@Override
	public void lineTo(float x, float y) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public Point2D getCurrentPoint() throws IOException {
		// TODO Auto-generated method stub
		return currentPoint;
	}

	@Override
	public void closePath() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void endPath() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void strokePath() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void fillPath(int windingRule) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void fillAndStrokePath(int windingRule) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void shadingFill(COSName shadingName) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {

		try {
			System.out.println("page " + pageNo + " is in process");
			this.processPage(page);
			if (text != null && !text.trim().equals("")) {
				txtDoc.save(fDir.getPath() + File.separator + "result_page_" + pageNo);
			}
			System.out.println("page " + pageNo + " is Processed");
			txtDoc.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
