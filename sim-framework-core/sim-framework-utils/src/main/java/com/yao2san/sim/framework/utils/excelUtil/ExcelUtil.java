package com.yao2san.sim.framework.utils.excelUtil;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * excel util
 *
 * @author wxg
 */
public class ExcelUtil {

    /**
     * read excel
     *
     * @param filePath file path
     * @param sheetNum the number of sheet
     * @return return a list,contains map like [{"_COL_1":"happy"}]
     * @throws IOException IOException
     */
    public static List<Map<String, Object>> readAsList(String filePath, int sheetNum) throws IOException {
        return readAsList(new FileInputStream(filePath), sheetNum, null, 0, Integer.MAX_VALUE);
    }

    /**
     * read excel
     *
     * @param filePath file path
     * @param sheetNum the number of sheet
     * @param callback row callback
     * @return return a list,contains map like [{"_COL_1":"happy"}]
     * @throws IOException IOException
     */
    public static List<Map<String, Object>> readAsList(String filePath, int sheetNum, Callback callback) throws IOException {
        return readAsList(new FileInputStream(filePath), sheetNum, callback, 0, Integer.MAX_VALUE);
    }

    /**
     * read excel
     *
     * @param filePath      filePath file path
     * @param sheetNum      the number of sheet
     * @param callback      row callback
     * @param startRowIndex read from the <code>startRowIndex</code> line
     * @return return a list,contains map like [{"_COL_1":"happy"}]
     * @throws IOException IOException
     */
    public static List<Map<String, Object>> readAsList(String filePath, int sheetNum, Callback callback, int startRowIndex) throws IOException {
        return readAsList(new FileInputStream(filePath), sheetNum, callback, startRowIndex, Integer.MAX_VALUE);
    }

    /**
     * read excel
     *
     * @param file     a file
     * @param sheetNum the number of sheet
     * @return return a list,contains map like [{"_COL_1":"happy"}]
     * @throws IOException IOException
     */
    public static List<Map<String, Object>> readAsList(File file, int sheetNum) throws IOException {
        return readAsList(new FileInputStream(file), sheetNum, null, 0, Integer.MAX_VALUE);
    }

    /**
     * read excel
     *
     * @param file     a file
     * @param sheetNum the number of sheet
     * @param callback row callback
     * @return return a list,contains map like [{"_COL_1":"happy"}]
     * @throws IOException IOException
     */
    public static List<Map<String, Object>> readAsList(File file, int sheetNum, Callback callback) throws IOException {
        return readAsList(new FileInputStream(file), sheetNum, callback, 0, Integer.MAX_VALUE);
    }

    /**
     * read excel
     *
     * @param file          a file
     * @param sheetNum      the number of sheet
     * @param callback      row callback
     * @param startRowIndex read from the <code>startRowIndex</code> line
     * @return return a list,contains map like [{"_COL_1":"happy"}]
     * @throws IOException IOException
     */
    public static List<Map<String, Object>> readAsList(File file, int sheetNum, Callback callback, int startRowIndex) throws IOException {
        return readAsList(new FileInputStream(file), sheetNum, callback, startRowIndex, Integer.MAX_VALUE);
    }

    /**
     * read excel
     *
     * @param inputStream   file input stream
     * @param sheetNum      the number of sheet
     * @param callback      row callback
     * @param startRowIndex read from the <code>startRowIndex</code> line
     * @param endRowIndex   stop reading from the <code>endRowIndex</code> line
     * @return return a list,contains map like [{"_COL_1":"happy"}]
     * @throws IOException IOException
     */
    @SuppressWarnings("all")
    public static List<Map<String, Object>> readAsList(InputStream inputStream, int sheetNum, Callback callback, int startRowIndex, int endRowIndex) throws IOException {
        List<Map<String, Object>> result = new LinkedList<>();
        try (Workbook wb = WorkbookFactory.create(inputStream);) {
            Sheet sheet = wb.getSheetAt(sheetNum);

            //Get header
            List<String> header = null;
            if (callback != null && callback.headerRowIndex() != -1) {
                Row row = sheet.getRow(callback.headerRowIndex());
                header = new LinkedList<>();
                Iterator<Cell> cellIterator = row.cellIterator();
                Cell cell;
                while (cellIterator.hasNext()) {
                    cell = cellIterator.next();
                    header.add(cell.getStringCellValue());
                }
            }

            Iterator<Row> rowIterator = sheet.rowIterator();
            Row row;
            while (rowIterator.hasNext()) {
                row = rowIterator.next();

                if (row.getRowNum() >= startRowIndex && row.getRowNum() < endRowIndex) {
                    Iterator<Cell> cellIterator = row.cellIterator();
                    Cell cell;
                    Map<String, Object> data = new LinkedHashMap<>();
                    while (cellIterator.hasNext()) {
                        cell = cellIterator.next();
                        Object value = "";
                        CellType cellType = cell.getCellType();
                        if (cellType == CellType.STRING) {
                            value = cell.getStringCellValue();
                        }
                        if (cellType == CellType.BOOLEAN) {
                            value = cell.getBooleanCellValue();
                        }
                        if (cellType == CellType.NUMERIC) {
                            value = BigDecimal.valueOf(cell.getNumericCellValue());
                        }
                        if (cellType == CellType.BLANK) {
                            value = "";
                        }
                        //When header is empty, return _COL_+ column index as header by default
                        if (header == null) {
                            data.put("_COL_" + cell.getColumnIndex(), value);
                        } else {
                            data.put(header.get(cell.getColumnIndex()), value);
                        }
                    }
                    if (callback != null && callback.rowCallback(data)) {
                        result.add(data);
                        callback.rowCallback(data);
                    }
                    if (callback == null) {
                        result.add(data);
                    }

                }
            }
        }
        return result;
    }


    /**
     * read excel with multithreading
     *
     * @param file     excel file
     * @param sheetNum the number of sheet
     * @param threads  thread number of threads
     * @param callback row calllback
     * @return
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @SuppressWarnings("all")
    public static List<Map<String, Object>> readAsList(File file, int sheetNum, int threads, Callback callback) throws IOException, ExecutionException, InterruptedException {
        List<Map<String, Object>> result = new LinkedList<>();
        try (Workbook wb = WorkbookFactory.create(file)) {

            Sheet sheet = wb.getSheetAt(sheetNum);

            //get header
            List<String> header = null;
            if (callback != null && callback.headerRowIndex() != -1) {
                Row row = sheet.getRow(callback.headerRowIndex());
                header = new LinkedList<>();
                Iterator<Cell> cellIterator = row.cellIterator();
                Cell cell;
                while (cellIterator.hasNext()) {
                    cell = cellIterator.next();
                    header.add(cell.getStringCellValue());
                }
            }

            List<FutureTask<List<Map<String, Object>>>> futureTasks = new ArrayList<>();

            int rowNum = sheet.getLastRowNum();
            int step = rowNum < threads ? 1 : rowNum / threads;
            for (int i = 0; i < threads; i++) {
                if (i == threads - 1) {
                    futureTasks.add(new FutureTask<>(new ReadExcelThread(sheet, header, step * i, rowNum, callback)));
                } else {
                    futureTasks.add(new FutureTask<>(new ReadExcelThread(sheet, header, step * i, step * (i + 1), callback)));
                }
            }
            ExecutorService executorService = Executors.newFixedThreadPool(threads);

            for (FutureTask<List<Map<String, Object>>> futureTask : futureTasks) {
                executorService.submit(futureTask);
            }
            for (FutureTask<List<Map<String, Object>>> futureTask : futureTasks) {
                List<Map<String, Object>> list = futureTask.get();
                result.addAll(list);
            }
            if (!executorService.isShutdown()) {
                executorService.shutdown();
            }
            wb.cloneSheet(sheetNum);
            return result;
        }
    }


    /**
     * row callback
     */
    public interface Callback {
        /**
         * Callback after row data is obtained
         *
         * @return If return false, the row of data will not be added to the collection
         */
        default boolean rowCallback(Map<String, Object> row) {
            return true;
        }

        /**
         * Get the row number of the header
         *
         * @return row index
         */
        default int headerRowIndex() {
            return -1;
        }

    }
}
