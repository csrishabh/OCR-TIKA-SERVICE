package com.mrll.javelin.tikaparser.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.springframework.core.io.ClassPathResource;

public class ImageProcesser {
	
	
	
	public void processImage(File streamingObject, TesseractOCRConfig config) throws IOException, TikaException {

		// fetch rotation script from resources
		InputStream in = new ClassPathResource("rotation.py").getInputStream();
		TemporaryResources tmp = new TemporaryResources();
		File rotationScript = tmp.createTemporaryFile();
		Files.copy(in, rotationScript.toPath(), StandardCopyOption.REPLACE_EXISTING);

		String cmd = "python " + rotationScript.getAbsolutePath() + " -f " + streamingObject.getAbsolutePath();
		String angle = "0";

		DefaultExecutor executor = new DefaultExecutor();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
		executor.setStreamHandler(streamHandler);

		// determine the angle of rotation required to make the text horizontal
		CommandLine cmdLine = CommandLine.parse(cmd);
		if (config.getApplyRotation() && hasPython()) {
			try {
				executor.execute(cmdLine);
				angle = outputStream.toString("UTF-8").trim();
			} catch (Exception e) {

			}
		}

		// process the image - parameter values can be set in
		// TesseractOCRConfig.properties
		String line = "convert -density " + config.getDensity() + " -depth " + config.getDepth() + " -colorspace "
				+ config.getColorspace() + " -filter " + config.getFilter() + " -resize " + config.getResize()
				+ "% -rotate " + angle + " " + streamingObject.getAbsolutePath() + " "
				+ streamingObject.getAbsolutePath();
		cmdLine = CommandLine.parse(line);
		try {
			executor.execute(cmdLine);
		} catch (Exception e) {

		}

		tmp.close();
	}

	static boolean hasPython() {
		// check if python is installed and it has the required dependencies for
		// the rotation program to run
		boolean hasPython = false;

		try {
			TemporaryResources tmp = new TemporaryResources();
			File importCheck = tmp.createTemporaryFile();
			String prg = "import numpy, matplotlib, skimage";
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(importCheck),
					Charset.forName("UTF-8"));
			out.write(prg);
			out.close();

			Process p = Runtime.getRuntime().exec("python " + importCheck.getAbsolutePath());
			if (p.waitFor() == 0) {
				hasPython = true;
			}

			tmp.close();

		} catch (Exception e) {

		}

		return hasPython;
	}

}
