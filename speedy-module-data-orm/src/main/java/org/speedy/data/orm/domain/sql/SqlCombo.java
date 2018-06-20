package org.speedy.data.orm.domain.sql;

import java.util.ArrayList;
import java.util.List;

import org.speedy.data.orm.domain.statement.SqlStatement;

import lombok.Data;

/**
 * @Description
 * @Author chenguangxue
 * @CreateDate 2018/06/12 13:44
 */
@Data
public class SqlCombo {

	private String sql;
	private List<Object> args;

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public List<Object> getArgs() {
		return args;
	}

	public void setArgs(List<Object> args) {
		this.args = args;
	}

	public SqlCombo(String sql, List<Object> args) {
		this.sql = sql;
		this.args = args;
	}

	public static SqlCombo from(SqlStatement sqlStatement) {
		List<Object> argsList = new ArrayList<>();
		/* 将参数值中的enum值转换为String */
		for (Object e : sqlStatement.getArgs()) {
			if (e == null) {
				argsList.add(null);
			} else if (e.getClass().isEnum()) {
				argsList.add(e.toString());
			} else {
				argsList.add(e);
			}
		}
		return new SqlCombo(sqlStatement.getSql(), argsList);
	}
}
