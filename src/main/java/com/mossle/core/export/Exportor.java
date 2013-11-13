package com.mossle.core.export;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.mossle.core.util.ServletUtils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class Exportor {
    public void exportExcel(HttpServletResponse response, TableModel tableModel)
            throws IOException {
        Workbook wb = new HSSFWorkbook();
        Sheet s = wb.createSheet(tableModel.getName());

        for (int i = 0; i < tableModel.getHeaderCount(); i++) {
            s.autoSizeColumn(0);
        }

        Row r = s.createRow(0);

        for (int i = 0; i < tableModel.getHeaderCount(); i++) {
            Cell c = r.createCell(i);
            c.setCellValue(tableModel.getHeader(i));
        }

        for (int i = 0; i < tableModel.getDataCount(); i++) {
            r = s.createRow(i + 1);

            for (int j = 0; j < tableModel.getHeaderCount(); j++) {
                Cell c = r.createCell(j);
                c.setCellValue(tableModel.getValue(i, j));
            }
        }

        response.setContentType(ServletUtils.EXCEL_TYPE);
        ServletUtils.setFileDownloadHeader(response, tableModel.getName()
                + ".xls");
        wb.write(response.getOutputStream());
        response.getOutputStream().flush();
    }
}
