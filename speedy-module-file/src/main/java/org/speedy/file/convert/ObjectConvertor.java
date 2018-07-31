package org.speedy.file.convert;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.speedy.file.annotation.ExcelObjectField;
import org.speedy.file.constant.ConvertConstant;
import org.speedy.file.domain.ExcelImportObject;
import org.speedy.file.exception.SpeedyFileException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.speedy.file.constant.ConvertConstant.*;

/**
 * @Description
 * @Author chenguangxue
 * @CreateDate 2018/7/3 23:41
 */
@Component
@Slf4j
public class ObjectConvertor {

    public <T extends ExcelImportObject> List<T> toObjects(File file, Class<T> clazz) {
        Assert.notNull(clazz, "EXCEL转换的类型不能为空");
        Assert.notNull(file, "执行转换的EXCEL文件为空");

        List<Field> fields = findFields(clazz);
        Map<String, String> fieldNameMap = new HashMap<>(fields.size() * 2);

        fields.forEach(field -> {
            if (field.isAnnotationPresent(ExcelObjectField.class)) {
                ExcelObjectField annotation = field.getAnnotation(ExcelObjectField.class);
                String fieldZhName = annotation.value();
                fieldNameMap.put(fieldZhName, field.getName());
            }
        });

        Map<String, Object> fileData = convert(file);

        List<String> titles = (List<String>) fileData.get(TITLE_KEY);
        List<List<Object>> datas = (List<List<Object>>) fileData.get(ConvertConstant.DATA_KEY);
        List<JSONObject> objects = new ArrayList<>();

        datas.forEach(data -> {
            JSONObject jsonObject = new JSONObject();

            for (int i = 0, size = data.size(); i < size; i++) {
                String zhTitle = titles.get(i); // 这是中文的标题
                Object d = data.get(i);
                if (fieldNameMap.containsKey(zhTitle)) {
                    String fieldName = fieldNameMap.get(zhTitle);// 获取字段名称
                    jsonObject.put(fieldName, d);
                }
            }
            objects.add(jsonObject);
        });

        return objects.stream().map(jsonObject -> jsonObject.toJavaObject(clazz)).collect(Collectors.toList());
    }

    /* 将excel文件中的数据，转换为标题和数据两部分 */
    private Map<String, Object> convert(File file) {
        Assert.notNull(file, "需要处理的excel文件为空");

        Workbook workbook = null;
        try {
            workbook = WorkbookFactory.create(file);
        }
        catch (IOException | InvalidFormatException e) {
            throw new SpeedyFileException(e, "创建Workbook失败");
        }

        String defaultSheetName = DEFAULT_SHEET_NAME;
        Sheet sheet = workbook.getSheet(defaultSheetName);
        if (sheet == null) {
            String message = String.format("没有发现名称为[%s]的sheet", defaultSheetName);
            throw new SpeedyFileException(message);
        }

        int lastRowNum = sheet.getLastRowNum();
        if (lastRowNum < 1) {
            throw new SpeedyFileException("需要导入的数据为空");
        }
        log.info("需要导入的数据有：{}条", lastRowNum);

        /* 提取标题行数据 */
        Row titleRow = sheet.getRow(FIRST_ROW_NUM);
        List<Object> title = extractRow(titleRow);

        List<Object> datas = new ArrayList<>(lastRowNum - 1);
        for (int r = FIRST_ROW_NUM + 1; r <= lastRowNum; r++) {
            List<Object> rowData = extractRow(sheet.getRow(r));
            datas.add(rowData);
        }

        try {
            workbook.close();
        }
        catch (IOException e) {
            throw new SpeedyFileException(e, "关闭Workbook失败");
        }

        Map<String, Object> fileData = new HashMap<>();
        fileData.put(TITLE_KEY, title);
        fileData.put(DATA_KEY, datas);

        return fileData;
    }

    /* 按照顺序提取指定行之中每个单元格的数据 */
    private List<Object> extractRow(Row row) {
        short lastCellNum = row.getLastCellNum();
        List<Object> cellValues = new ArrayList<>(lastCellNum);
        for (int c = FIRST_CELL_NUM; c <= lastCellNum; c++) {
            Cell cell = row.getCell(c);
            Object cellValue = getCellValue(cell);
            cellValues.add(cellValue);
        }
        return cellValues;
    }

    /* 获取单元格的数值 */
    private Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        Object cellValue;
        CellType cellTypeEnum = cell.getCellTypeEnum();
        switch (cellTypeEnum) {
            case STRING:
                return cell.getStringCellValue();
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case NUMERIC:
                return cell.getNumericCellValue();
            case FORMULA:
                return cell.getCellFormula();
            case ERROR:
                return cell.getErrorCellValue();
            default:
                return null;
        }
    }
}
