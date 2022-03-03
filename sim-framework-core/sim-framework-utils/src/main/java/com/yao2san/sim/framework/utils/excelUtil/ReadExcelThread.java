package com.yao2san.sim.framework.utils.excelUtil;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * Excel thread
 *
 * @author wxg
 */
@SuppressWarnings("all")
public class ReadExcelThread implements Callable<List<Map<String, Object>>> {

    private Sheet sheet;
    private List<String> header;
    private int startRowIndex;
    private int endRowIndex;
    private ExcelUtil.Callback callback;
    List<Map<String, Object>> result;

    public ReadExcelThread(Sheet sheet, List<String> header, int startRowIndex, int endRowIndex, ExcelUtil.Callback callback) {
        this.sheet = sheet;
        this.header = header;
        this.startRowIndex = startRowIndex;
        this.endRowIndex = endRowIndex;
        this.callback = callback;
        this.result = new LinkedList<>();
    }

    @Override
    public List<Map<String, Object>> call() throws Exception {
        Row row;
        for (int index = startRowIndex; index < endRowIndex; index++) {
            row = sheet.getRow(index);
            if (row.getRowNum() >= startRowIndex) {
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
        return result;
    }
}
