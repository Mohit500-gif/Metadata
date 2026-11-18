package com.digi.uniprr.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;

public class FileUploadValidation {

    private static final Logger log = LoggerFactory.getLogger(FileUploadValidation.class);

    public static boolean validateFile(File file, Map<String, String> expectedMimeTypes) {
        try {


            String fileName = file.getName();
            String fileExtension = getFileExtension(fileName);
            log.info("Validating file: {} with extension: {}", fileName, fileExtension);

            if (fileExtension != null) {
                switch (fileExtension.toLowerCase()) {
                    case "doc":
                    case "docx":
                        log.info("condition for docx");
                        return isValidDocFile(file);
                    case "pdf":
                        return isValidPdf(file);
                    case "jpeg":
                    case "jpg":
                    case "png":
                    case "gif":
                    case "bmp":
                    case "tiff":
                    case "tif":
                    case "svg":
                        return true;
                    case "mp4":
//                        return isValidMP4(file);
                        return true;
                    case "mov":
//                        return isValidMOV(file);
                        return true;
                    case "avi":
//                        return isValidAVI(file);
                        return true;
                    case "m4v":
//                      return isValidAVI(file);
                      return true;
                    default:
                        log.error("Unsupported file type: {}", fileExtension);
                        return false;
                }
            } else {
                log.error("File type could not be determined for file: {}", fileName);
                return false;
            }
        } catch (Exception e) {
            log.error("Error validating file: {}", e.getMessage(), e);
            return false;
        }
    }

    public static boolean isValidDocFile(File file) {
        log.info("Starting doc verification for: {}", file.getAbsolutePath());
        boolean isValid = false;

        try (FileInputStream fis = new FileInputStream(file)) {
            if (file.getName().endsWith(".doc")) {
                HWPFDocument document = new HWPFDocument(fis);
                WordExtractor extractor = new WordExtractor(document);
                String text = extractor.getText();
                extractor.close();
                isValid = (text != null && !text.isEmpty());
                log.info("DOC Validation: {} {}", isValid ? "Valid" : "Invalid", file.getAbsolutePath());
                return isValid;

            } else if (file.getName().endsWith(".docx")) {
                try {
                	                	 
                	ZipSecureFile.setMinInflateRatio(0.001);
                	XWPFDocument doc = new XWPFDocument(fis);
                	Path path = Paths.get(file.getAbsolutePath());
                	String mimeType = Files.probeContentType(path);
                	
                	if (mimeType != null && mimeType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                		isValid= true;
                	}	
                	log.info("DOCX Validation: {} {}", isValid ? "Valid" : "Invalid", file.getAbsolutePath());
                	return isValid;

                } catch (Exception e) {
                	log.error("Error while validating DOCX file using docx4j: {}", e.getMessage());
                	return false;

                }
            }
            return false;
        } catch (NotOfficeXmlFileException e) {
            log.error("The provided file is not a valid DOCX file.");
            return false;
        } catch (Exception e) {
            log.error("Error validating DOC file: {}", e.getMessage(), e);
            return false;
        }
    }

    
    public static void main(String[] args) {
		File f = new File("C:\\Users\\80132\\Downloads\\Manuscript (35).docx");
		isValidDocFile(f);
	}

    private static boolean isValidPdf(File file) {
        String fileName = file.getName();
        if (fileName != null && fileName.toLowerCase().endsWith(".pdf")) {
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] pdfBytes = new byte[4];
                int bytesRead = fis.read(pdfBytes, 0, 4);
                if (bytesRead >= 4) {
                    String pdfContent = new String(pdfBytes, 0, 4, StandardCharsets.UTF_8);
                    boolean isValid = pdfContent.startsWith("%PDF");
                    log.info("PDF Validation: {} {}", isValid ? "Valid" : "Invalid", file.getAbsolutePath());
                    return isValid;
                } else {
                    log.error("Could not read the PDF header for file: {}", file.getAbsolutePath());
                    return false;
                }
            } catch (IOException e) {
                log.error("Error reading PDF file: {}", e.getMessage(), e);
                return false;
            }
        }
        return false;
    }

//    private static boolean isValidImage(File file) {
//        try {
//            BufferedImage image = ImageIO.read(file);
//            boolean isValid = (image != null);
//            log.info("Image Validation: {} {}", isValid ? "Valid" : "Invalid", file.getAbsolutePath());
//            return isValid;
//        } catch (IOException e) {
//            log.error("Error validating image file: {}", e.getMessage(), e);
//            return false;
//        }
//    }

    private static boolean isValidMP4(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] header = new byte[4];
            int bytesRead = fis.read(header, 0, 4);
            boolean isValid = (bytesRead >= 4 && isMP4Header(header));
            log.info("MP4 Validation: {} {}", isValid ? "Valid" : "Invalid", file.getAbsolutePath());
            return isValid;
        } catch (IOException e) {
            log.error("Error validating MP4 file: {}", e.getMessage(), e);
            return false;
        }
    }

    private static boolean isValidMOV(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] header = new byte[4];
            int bytesRead = fis.read(header, 0, 4);
            boolean isValid = (bytesRead >= 4 && isMOVHeader(header));
            log.info("MOV Validation: {} {}", isValid ? "Valid" : "Invalid", file.getAbsolutePath());
            return isValid;
        } catch (IOException e) {
            log.error("Error validating MOV file: {}", e.getMessage(), e);
            return false;
        }
    }

    private static boolean isValidAVI(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] header = new byte[4];
            int bytesRead = fis.read(header, 0, 4);
            boolean isValid = (bytesRead >= 4 && isAVIHeader(header));
            log.info("AVI Validation: {} {}", isValid ? "Valid" : "Invalid", file.getAbsolutePath());
            return isValid;
        } catch (IOException e) {
            log.error("Error validating AVI file: {}", e.getMessage(), e);
            return false;
        }
    }

    private static boolean isMP4Header(byte[] header) {
        // MP4 header: 00 00 00 20
        return (header[0] == 0x00 && header[1] == 0x00 && header[2] == 0x00 && header[3] == 0x20);
    }

    private static boolean isMOVHeader(byte[] header) {
        // MOV header: 6D 6F 6F 76 (ASCII "moov")
        return (header[0] == 0x6D && header[1] == 0x6F && header[2] == 0x6F && header[3] == 0x76);
    }

    private static boolean isAVIHeader(byte[] header) {
        // AVI header: 52 49 46 46 (ASCII "RIFF")
        return (header[0] == 0x52 && header[1] == 0x49 && header[2] == 0x46 && header[3] == 0x46);
    }

    private static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        }
        return null;
    }

    public static Map<String, String> getExpectedMimeTypes() {
        return Arrays.stream(new String[][]{
                {"doc", "application/msword"},
                {"docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
                {"pdf", "application/pdf"},
                {"jpeg", "image/jpeg"},
                {"jpg", "image/jpeg"},
                {"png", "image/png"},
                {"gif", "image/gif"},
                {"bmp", "image/bmp"},
                {"tiff", "image/tiff"},
                {"tif", "image/tiff"},
                {"svg", "image/svg+xml"},
                {"mp4", "video/mp4"},
                {"avi", "video/x-msvideo"},
                {"mov", "video/quicktime"},
                {"wmv", "video/x-ms-wmv"},
                {"flv", "video/x-flv"},
                {"webm", "video/webm"}
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }
    
    public static File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

}
