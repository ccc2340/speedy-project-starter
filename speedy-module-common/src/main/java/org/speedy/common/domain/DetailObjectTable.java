package org.speedy.common.domain;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

/**
 * @Description
 * @Author chenguangxue
 * @CreateDate 2018/7/12 18:09
 */
@Getter
public class DetailObjectTable {

    private List<String> title;
    private List<List<Object>> data;

    private DetailObjectTable() {
        this.title = new LinkedList<>();
        this.data = new LinkedList<>();
    }

    public static DetailObjectTable build(List<String> title, List<List<Object>> data) {
        DetailObjectTable table = new DetailObjectTable();

        table.title.addAll(title);
        table.data.addAll(data);

        return table;
    }
}
