package com.digi.uniprr.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.uniprr.model.ManuScriptFile;

import javaxt.io.Image;

public class ImageToPdf {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public static void imageToDocx(File imageLocation, List<ManuScriptFile> manuscriptFile) throws Exception {

		for (File fileEntry : imageLocation.listFiles()) {
			if (fileEntry.getName().endsWith(".gif") || fileEntry.getName().endsWith(".png")
					|| fileEntry.getName().endsWith(".bmg") || fileEntry.getName().endsWith(".jpg")
					|| fileEntry.getName().endsWith(".jpeg") || fileEntry.getName().endsWith(".GIF")
					|| fileEntry.getName().endsWith(".PNG") || fileEntry.getName().endsWith(".BMG")
					|| fileEntry.getName().endsWith(".JPG") || fileEntry.getName().endsWith(".JPEG")
					|| fileEntry.getName().endsWith(".tiff") || fileEntry.getName().endsWith(".tif")) {
				for (ManuScriptFile manuScriptFile2 : manuscriptFile) {
					if (manuScriptFile2.getFileName().equals(fileEntry.getName())) {
						File DocxForImage = new File(imageLocation + File.separator + "DocxForImage");
						// log.info("outputFolder Location :" + DocxForImage);
						if (!DocxForImage.exists()) {
							DocxForImage.mkdir();
							// log.info("Output folder created for PDF Image");
						}
						String imageFile = imageLocation.getAbsolutePath() + File.separator + fileEntry.getName();
						XWPFDocument doc = new XWPFDocument();
						XWPFParagraph title = doc.createParagraph();
						XWPFRun run = title.createRun();
						Image image = new Image(imageFile);
						int height = image.getHeight();
						int weight = image.getWidth();
						while (height > 500 || weight > 500) {
							if (height > 500 || weight > 500) {
								height = (int) (height / 1.2);
								weight = (int) (weight / 1.2);
							}
						}

						FileInputStream is = new FileInputStream(imageFile);
						run.addBreak();
						run.addPicture(is, XWPFDocument.PICTURE_TYPE_JPEG, imageFile, Units.toEMU(weight),
								Units.toEMU(height)); // 200x200 pixels
						is.close();

						run.addBreak();
						String legend = manuScriptFile2.getLegend();
						if(legend!=null) {
							run.setText(legend);
							//run.setBold(true);
							title.setAlignment(ParagraphAlignment.CENTER);
						}
						

						FileOutputStream fos = new FileOutputStream(imageLocation +File.separator +"DocxForImage" + File.separator
								+ fileEntry.getName().replace(".gif", ".docx").replace(".png", ".docx")
										.replace(".bmg", ".docx").replace(".jpg", ".docx").replace(".jpeg", ".docx")
										.replace(".GIF", ".docx").replace(".PNG", ".docx").replace(".BMG", ".docx")
										.replace(".JPG", ".docx").replace(".JPEG", ".docx").replace(".tiff", ".docx")
										.replace(".tif", ".docx"));
						doc.write(fos);
						fos.close();

					}
				}
			}
		}

	}

}
