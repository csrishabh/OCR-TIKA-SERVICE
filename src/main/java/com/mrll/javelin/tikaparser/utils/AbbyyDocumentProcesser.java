/*package com.mrll.javelin.tikaparser.utils;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.web.multipart.MultipartFile;

import com.abbyy.FREngine.Engine;
import com.abbyy.FREngine.FileExportFormatEnum;
import com.abbyy.FREngine.IEngine;
import com.abbyy.FREngine.IFRDocument;
import com.abbyy.FREngine.IPDFExportParams;
import com.abbyy.FREngine.IReadStream;
import com.abbyy.FREngine.PDFExportScenarioEnum;

public class AbbyyDocumentProcesser {

	public static void main( String[] args ) {
		try {
			AbbyyDocumentProcesser application = new AbbyyDocumentProcesser();
			application.Run(null);
		} catch( Exception ex ) {
			displayMessage( ex.getMessage() );
		}
	}
	
	public void Run(MultipartFile imageFile) throws Exception {
		// Load ABBYY FineReader Engine
		loadEngine();
		try {
			// Process with ABBYY FineReader Engine
			processWithEngine(imageFile);
		} finally {
			// Unload ABBYY FineReader Engine
			unloadEngine();
		}
	}

	private void loadEngine() throws Exception {
		displayMessage("Initializing Engine...");
		engine = Engine.GetEngineObject(SamplesConfig.GetDllFolder(), SamplesConfig.GetDeveloperSN());
	}

	private void processWithEngine(MultipartFile imageFile) {
		try {
			// Setup FREngine
			setupFREngine();

			// Process sample image
			processImage(imageFile);
		} catch (Exception ex) {
			displayMessage(ex.getMessage());
		}
	}

	private void setupFREngine() {
		displayMessage("Loading predefined profile...");
		engine.LoadPredefinedProfile("DocumentConversion_Accuracy");
		// Possible profile names are:
		// "DocumentConversion_Accuracy", "DocumentConversion_Speed",
		// "DocumentArchiving_Accuracy", "DocumentArchiving_Speed",
		// "BookArchiving_Accuracy", "BookArchiving_Speed",
		// "TextExtraction_Accuracy", "TextExtraction_Speed",
		// "FieldLevelRecognition",
		// "BarcodeRecognition_Accuracy", "BarcodeRecognition_Speed",
		// "HighCompressedImageOnlyPdf",
		// "BusinessCardsProcessing",
		// "EngineeringDrawingsProcessing",
		// "Version9Compatibility",
		// "Default"
	}

	private void processImage(MultipartFile imageFile) {
		//String imagePath = SamplesConfig.GetSamplesFolder() + "\\SampleImages\\Pdf_with_images_done.pdf";

		try {
			// Don't recognize PDF file with a textual content, just copy it
			if (engine.IsPdfWithTextualContent(imagePath, null)) {
				displayMessage("Copy results...");
				String resultPath = SamplesConfig.GetSamplesFolder() + "\\SampleImages\\Pdf_with_images_done.pdf";
				Files.copy(Paths.get(imagePath), Paths.get(resultPath), StandardCopyOption.REPLACE_EXISTING);
				return;
			}

			// Create document
			IFRDocument document = engine.CreateFRDocument();
			
			try {
				// Add image file to document
				//long startTime = System.currentTimeMillis();
				displayMessage("Loading image...");
				//FileInputStream stream = new FileInputStream(new File(imagePath));
				//document.AddImageFile(imagePath, null, null);
				//document.AddImageFileFromStream(new AbbyyInputStream(imageFile), null,null,null,"PDF.pdf");
				document.AddImageFileFromMemory(imageFile.getBytes(), null,null,null,imageFile.getOriginalFilename());
				

				// Process document
				displayMessage("Process...");
				document.Process(null);

				// Save results
				displayMessage("Saving results...");

				// Save results to rtf with default parameters
				String fileName = imageFile.getOriginalFilename().substring(0, imageFile.getOriginalFilename().lastIndexOf("."));
				String rtfExportPath = SamplesConfig.GetSamplesFolder() + "\\SampleImages\\"+fileName+".txt";
				document.Export(rtfExportPath, FileExportFormatEnum.FEF_TextVersion10Defaults, null);
				// Save results to pdf using 'balanced' scenario
				IPDFExportParams pdfParams = engine.CreatePDFExportParams();
				pdfParams.setScenario(PDFExportScenarioEnum.PES_Balanced);

				String pdfExportPath = SamplesConfig.GetSamplesFolder() + "\\SampleImages\\"+fileName+".pdf";
				document.Export(pdfExportPath, FileExportFormatEnum.FEF_PDF, pdfParams);
				//long stopTime = System.currentTimeMillis();
				//long elapsedTime = stopTime - startTime;
				//System.out.println(elapsedTime);
			} finally {
				// Close document
				document.Close();
			}
		} catch (Exception ex) {
			displayMessage(ex.getMessage());
		}
	}

	private void unloadEngine() throws Exception {
		displayMessage("Deinitializing Engine...");
		engine = null;
		Engine.DeinitializeEngine();
	}

	private static void displayMessage(String message) {
		System.out.println(message);
	}

	private IEngine engine = null;

}
*/