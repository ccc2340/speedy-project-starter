package org.speedy.file.convert;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.speedy.common.util.ReflectUtils;
import org.speedy.file.annotation.ExcelObjectField;
import org.speedy.file.constant.ConvertConstant;
import org.speedy.file.domain.ExcelExportObject;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description 文件转换器
 * @Author chenguangxue
 * @CreateDate 2018/7/4 16:38
 */
@Component
public class FileConvertor {

    /* 根据ExcelExportObject集合，构造出excel文件 */
    public <T extends ExcelExportObject> File toFile(List<T> exportObjects, File emptyFile) throws IOException {
        List<String> title = extractTitle(exportObjects.get(0).getClass());
        List<List<Object>> data = extractData(exportObjects);
        return buildExcel(title, data, emptyFile);
    }

    /* 根据标题和数据，生成excel文件 */
    public File buildExcel(List<String> title, List<List<Object>> data, File excelFile) throws IOException {
        Workbook workbook = new SXSSFWorkbook();
        Sheet sheet = workbook.createSheet(ConvertConstant.DEFAULT_SHEET_NAME);

        // 生成标题行
        buildRow(sheet, ConvertConstant.FIRST_ROW_NUM, title);

        // 生成数据行
        for (int i = ConvertConstant.FIRST_ROW_NUM + 1; i <= data.size(); i++) {
            buildRow(sheet, i, data.get(i - 1));
        }

        OutputStream os = new FileOutputStream(excelFile);
        workbook.write(os);
        os.close();

        return excelFile;
    }

    private void buildRow(Sheet sheet, int index, List<?> objects) {
        Row row = sheet.createRow(index);

        for (int i = 0, size = objects.size(); i < size; i++) {
            Cell cell = row.createCell(i);
            cell.setCellType(CellType.STRING);
            Object objectValue = objects.get(i);
            if (objectValue == null) {
                cell.setCellValue("");
            }
            else {
                cell.setCellValue(objectValue.toString());
            }
        }
    }

    /* 提取标题 */
    private <T extends ExcelExportObject> List<String> extractTitle(Class<T> eeoClass) {
        List<Field> fields = ConvertConstant.findFields(eeoClass);
        return fields.stream().
                map(field -> field.getAnnotation(ExcelObjectField.class).value()).
                collect(Collectors.toList());
    }

    /* 提取数据 */
    private <T extends ExcelExportObject> List<List<Object>> extractData(List<T> exportObjects) {
        List<Field> fields = ConvertConstant.findFields(exportObjects.get(0).getClass());
        List<List<Object>> datas = new ArrayList<>(exportObjects.size());
        exportObjects.forEach(eo -> {
            List<Object> fieldValues = new ArrayList<>(fields.size());
            fields.forEach(field -> {
                Object fieldValue = ReflectUtils.directGetFieldValue(eo, field);
                fieldValues.add(fieldValue);
            });
            datas.add(fieldValues);
        });
        return datas;
    }
}