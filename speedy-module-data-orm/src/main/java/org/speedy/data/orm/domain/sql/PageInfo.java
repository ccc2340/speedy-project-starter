package org.speedy.data.orm.domain.sql;

/**
 * @Description
 * @Author chenguangxue
 * @CreateDate 2018/06/11 21:37
 */
public class PageInfo {

    private int index;
    private int cpp;
    private boolean page;

    private PageInfo(boolean page, int index, int cpp) {
        this.page = page;
        this.index = index;
        this.cpp = cpp;
    }

    public static PageInfo noPage() {
        return new PageInfo(false, 0, 0);
    }

    public static PageInfo needPage(int index, int cpp) {
        return new PageInfo(true, index, cpp);
    }
}
