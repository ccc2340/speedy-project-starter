package org.speedy.file.domain;

import java.util.List;

/**
 * @Description 用于导出excel数据时的接口
 * @Author chenguangxue
 * @CreateDate 2018/7/4 16:28
 */
public interface ExcelExportObject {

    /* 创建一个模板对象 */
    default ExcelExportObject template() {
        return this;
    }

    /* 提取标题 */
    List<String> extractTitle();

    /* 提取数据 */
    List<Object> extractData(Object po, Object template);
}
