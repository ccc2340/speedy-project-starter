package org.speedy.data.orm.domain.sql;

import lombok.Data;

import java.util.List;

/**
 * @Description 分页查询结果
 * @Author chenguangxue
 * @CreateDate 2018/06/10 22:19
 */
@Data
public class PageResult {

	private PageInfo pageInfo;
	private List<?> pageData;
	private int first;
	private int front;
	private int next;
	private int last;
	private long totalCount;

	public PageInfo getPageInfo() {
		return pageInfo;
	}

	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}

	public List<?> getPageData() {
		return pageData;
	}

	public void setPageData(List<?> pageData) {
		this.pageData = pageData;
	}

	public int getFirst() {
		return first;
	}

	public void setFirst(int first) {
		this.first = first;
	}

	public int getFront() {
		return front;
	}

	public void setFront(int front) {
		this.front = front;
	}

	public int getNext() {
		return next;
	}

	public void setNext(int next) {
		this.next = next;
	}

	public int getLast() {
		return last;
	}

	public void setLast(int last) {
		this.last = last;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

}
