package com.digi.uniprr.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.itextpdf.io.source.ByteArrayOutputStream;

@Component
public class ExcelGenerator {

    public ByteArrayInputStream generateExcel(List<Object[]> data) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Manuscript Report");

            // Header Row (match your Excel format)
            String[] headers = { "Author Name", "Author Email", "Author Phone", "Editor Name", "Editor Email",
                    "Editor Phone", "Reviewers", "Journal Title", "Manuscript Code", "Manuscript Title", "DOI",
                    "Manuscript Type", "Submission Date", "Editorial Review", "Invite Reviewer",
                    "Accept Review Invitation", "Reviewed Manuscript", "Revision", "Final Decision", "Accepted/Rejected",
                    "Published" };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Data Rows
            int rowIdx = 1;
            for (Object[] row : data) {
                Row excelRow = sheet.createRow(rowIdx++);
                for (int i = 0; i < row.length; i++) {
                    excelRow.createCell(i).setCellValue(row[i] != null ? row[i].toString() : "");
                }
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}

