package org.speedy.data.orm.domain.sql;

import lombok.Data;
import lombok.Getter;

import java.util.List;

/**
 * @Description 分页查询结果
 * @Author chenguangxue
 * @CreateDate 2018/06/10 22:19
 */
@Data
public class PageResult {

    private PageInfo pageInfo;
    private List<?> data;
    private List<String> titles;
    private long totalCount;

    private PageResult() {
    }

    public static class Builder {
        private PageResult pageResult;

        private Builder() {
            this.pageResult = new PageResult();
        }

        public static Builder start() {
            return new Builder();
        }

        public Builder fillData(List<?> data) {
            this.pageResult.data = data;
            return this;
        }

        public Builder fillCount(long totalCount) {
            this.pageResult.totalCount = totalCount;
            return this;
        }

        public Builder fillPageInfo(PageInfo pageInfo) {
            this.pageResult.pageInfo = pageInfo;
            return this;
        }

        public PageResult complete() {
            this.pageResult.pageInfo.setTotalCount(this.pageResult.totalCount);
            return pageResult;
        }
    }
}
