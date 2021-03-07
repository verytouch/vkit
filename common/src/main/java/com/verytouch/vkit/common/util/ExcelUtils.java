package com.verytouch.vkit.common.util;

import com.verytouch.vkit.common.base.Assert;
import com.verytouch.vkit.common.exception.BusinessException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * excel工具类
 *
 * @author verytouch
 * @date 2021/3/7 20:41
 */
public class ExcelUtils {

    public static Writer writer() {
        return new Writer();
    }

    public Reader reader(InputStream inputStream) {
        return new Reader(inputStream);
    }

    /**
     * 读取excel
     *
     * @author verytouch
     * @date 2021/3/7 20:41
     */
    public static class Reader {

        private final XSSFWorkbook workbook;

        public Reader(InputStream inputStream) {
            try {
                workbook = new XSSFWorkbook(inputStream);
            } catch (IOException e) {
                throw new BusinessException(e);
            }
        }

        public List<List<String>> read(int sheetIndex) {
            XSSFSheet sheet = workbook.getSheetAt(sheetIndex);
            int lastRowNum = sheet.getLastRowNum();
            List<List<String>> data = new ArrayList<>();
            for (int i = 0; i < lastRowNum; i++) {
                XSSFRow row = sheet.getRow(i);
                int lastCellNum = row.getLastCellNum();
                List<String> list = new ArrayList<>();
                for (int j = 0; j < lastCellNum; j++) {
                    list.add(row.getCell(j).getStringCellValue());
                }
                data.add(list);
            }
            return data;
        }
    }

    /**
     * 写入excel
     *
     * @author verytouch
     * @date 2021/3/7 20:41
     */
    public static class Writer {

        private final Map<String, String> title;
        private List<?> data;

        private Writer() {
            this.title = new LinkedHashMap();
        }

        public Writer addTitle(String name, String field) {
            title.put(name, field);
            return this;
        }

        public Writer setData(List<?> data) {
            this.data = data;
            return this;
        }

        public XSSFWorkbook write() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
            Assert.nonEmpty(title, "标题不能为空");
            Assert.nonNull(data, "数据不能为空");
            XSSFWorkbook workbook = new XSSFWorkbook();

            XSSFSheet sheet = workbook.createSheet();
            // 标题
            Map<Integer, String> maxLengthCol = writeTitle(workbook, sheet);
            Set<Map.Entry<String, String>> titleEntries = title.entrySet();
            // 正文
            for (int i = 0; i < data.size(); i++) {
                XSSFRow row = sheet.createRow(i + 1);
                int j = 0;
                for (Map.Entry<String, String> titleEntry : titleEntries) {
                    Object value = PropertyUtils.getProperty(data.get(i), titleEntry.getValue());
                    String valueStr = Objects.toString(value, "");
                    maxLengthCol.merge(j, valueStr, (a, b) -> a.length() > b.length() ? a : b);
                    XSSFCell cell = row.createCell(j, Cell.CELL_TYPE_STRING);
                    cell.setCellValue(valueStr);
                    j++;
                }
            }
            // 列宽
            for (int i = 0; i < title.size(); i++) {
                sheet.setColumnWidth(i, (maxLengthCol.get(i) + "占位").getBytes().length * 256);
            }
            return workbook;
        }

        public void write(OutputStream outputStream) {
            XSSFWorkbook workbook = null;
            try {
                workbook = write();
                workbook.write(outputStream);
            } catch (Exception e) {
                throw new BusinessException("生成excel失败");
            } finally {
                try {
                    if (workbook != null) {
                        workbook.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private Map<Integer, String> writeTitle(XSSFWorkbook workbook, XSSFSheet sheet) {
            // 字体
            XSSFFont whiteBold = workbook.createFont();
            whiteBold.setBold(true);
            whiteBold.setColor(HSSFColor.WHITE.index);
            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFillForegroundColor(HSSFColor.BLUE_GREY.index);
            titleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            titleStyle.setFont(whiteBold);
            // 用来调整宽度
            Map<Integer, String> maxLengthCol = new HashMap<>();
            XSSFRow row = sheet.createRow(0);
            int colNum = 0;
            for (Map.Entry<String, String> entry : title.entrySet()) {
                XSSFCell cell = row.createCell(colNum);
                maxLengthCol.put(colNum, entry.getKey());
                cell.setCellValue(entry.getKey());
                cell.setCellStyle(titleStyle);
                colNum++;
            }
            return maxLengthCol;
        }
    }
}
