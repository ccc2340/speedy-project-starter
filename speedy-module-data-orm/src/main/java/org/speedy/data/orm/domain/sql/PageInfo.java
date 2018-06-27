package org.speedy.data.orm.domain.sql;

import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

/**
 * @Description
 * @Author chenguangxue
 * @CreateDate 2018/06/11 21:37
 */
@Data
public class PageInfo {

    /* 这3个属性有默认值 */
    private int index;
    private int cpp;
    private boolean page;

    /* 这个参数是由index和cpp计算而来 */
    private int offset;

    /* 以下属性需要设置，而且只需要设置totalCount就可以计算出其他的 */
    private long totalCount;
    private int first;
    private int front;
    private int next;
    private int last;

    public int getOffset() {
        return (index - 1) * cpp;
    }

    public int getFirst() {
        return 1;
    }

    public int getFront() {
        return index == 1 ? index : index - 1;
    }

    public int getNext() {
        return index == last ? last : index + 1;
    }

    public int getLast() {
        return last;
    }

    public PageInfo() {
        this.page = true;
        this.index = 1;
        this.cpp = 10;
    }

    private PageInfo(boolean page, int index, int cpp) {
        this.page = page;
        this.index = index;
        this.cpp = cpp;
    }

    /* 设置总条数，这个时候需要根据总条数计算出相关参数 */
    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
        if (page) {
            last = (int) ((totalCount - 1) / cpp + 1);
            first = 1;
            front = index == 1 ? 1 : index - 1;
            next = index == last ? last : index + 1;
        }
    }

    /* 不分页 */
    public static PageInfo noPage() {
        return new PageInfo(false, 0, 0);
    }

    /* 特定分页参数 */
    public static PageInfo customPage(Integer index, Integer cpp) {
        return new PageInfo(true, index, cpp);
    }

    /* 默认分页参数 */
    public static PageInfo defaultPage() {
        return new PageInfo();
    }
}
