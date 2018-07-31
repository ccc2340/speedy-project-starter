package org.speedy.common.domain;

import lombok.Getter;
import org.speedy.common.annotation.DetailObjectField;

import java.util.LinkedList;
import java.util.List;

/**
 * @Description 详情对象entry模型，用于li标签，分别包含名称和数值
 * @Author chenguangxue
 * @CreateDate 2018/7/12 18:03
 */
@Getter
public class DetailObjectEntry {

    private List<EntryData> datas;

    @Getter
    private class EntryData {
        private String title;
        private Object value;
        private String clazz;

        EntryData(Object value, DetailObjectField annotation) {
            this.title = annotation.value();
            this.value = value;
            this.clazz = String.format("width%d height%d", annotation.width(), annotation.height());
        }
    }

    public DetailObjectEntry() {
        this.datas = new LinkedList<>();
    }

    public void addData(Object value, DetailObjectField annotation) {
        EntryData entry = new EntryData(value, annotation);
        this.datas.add(entry);
    }
}
