package com.verytouch.vkit.common.util;

import com.google.gson.JsonObject;
import com.verytouch.vkit.common.base.Assert;
import com.verytouch.vkit.common.exception.BusinessException;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * excel工具类
 *
 * @author verytouch
 * @since 2021/3/7 20:41
 */
public class ExcelUtils {

    public static Writer writer(List<?> data) {
        return new Writer(data);
    }

    public Reader reader(InputStream inputStream) {
        return new Reader(inputStream);
    }

    /**
     * 读取excel
     *
     * @author verytouch
     * @since 2021/3/7 20:41
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

        public List<Map<String, String>> readAsMap(int sheetIndex, List<String> keys) {
            return read(sheetIndex).stream().map(values -> {
                Map<String, String> map = new HashMap<>();
                if (keys.size() != values.size()) {
                    return map;
                }
                for (int i = 0; i < keys.size(); i++) {
                    map.put(keys.get(i), values.get(i));
                }
                return map;
            }).collect(Collectors.toList());
        }
    }

    /**
     * 写入excel
     *
     * @author verytouch
     * @since 2021/3/7 20:41
     */
    public static class Writer {
        /**
         * key对应标题，value对应data中对象的字段名
         */
        private final LinkedHashMap<String, String> alias;
        /**
         * 数据
         */
        private final List<?> data;
        /**
         * 是否写标题
         */
        private final boolean writeTitle;

        private Writer(List<?> data, LinkedHashMap<String, String> alias, boolean writeTitle) {
            this.data = data;
            this.alias = alias;
            this.writeTitle = writeTitle;
        }

        private Writer(List<?> data) {
            this.data = data;
            this.alias = new LinkedHashMap<>();
            this.writeTitle = true;
        }

        public Writer addAlias(String name, String field) {
            this.alias.put(name, field);
            return this;
        }

        public XSSFWorkbook write() {
            Assert.nonEmpty(alias, "alias不能为空");
            Assert.nonNull(data, "data不能为空");
            XSSFWorkbook workbook = new XSSFWorkbook();

            XSSFSheet sheet = workbook.createSheet();
            // 标题
            Map<Integer, String> maxLengthCol;
            if (writeTitle) {
                maxLengthCol = writeTitle(workbook, sheet);
            } else {
                maxLengthCol = new HashMap<>();
            }

            Set<Map.Entry<String, String>> titleEntries = alias.entrySet();
            // 正文
            for (int i = 0; i < data.size(); i++) {
                XSSFRow row = sheet.createRow(i + 1);
                JsonObject json = JsonUtils.toJsonObject(data.get(i));
                int j = 0;
                for (Map.Entry<String, String> titleEntry : titleEntries) {
                    String valueStr = json.get(titleEntry.getValue()).getAsString();
                    maxLengthCol.merge(j, valueStr, (a, b) -> a.length() > b.length() ? a : b);
                    XSSFCell cell = row.createCell(j, Cell.CELL_TYPE_STRING);
                    cell.setCellValue(valueStr);
                    j++;
                }
            }
            // 列宽
            for (int i = 0; i < alias.size(); i++) {
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
            for (Map.Entry<String, String> entry : alias.entrySet()) {
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
