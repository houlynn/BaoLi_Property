package com.ufo.framework.system.repertory;

/**
 * 加入到 sql语句里面的left outer join 的结构
 * @author jfok
 *
 */
public class SqlLeftJoin {
	private String moduleName;
	private String tableAsName;
	private String primaryKey;

	private String childModuleName;
	private String childTableAsName;

	private String joinString = " left outer join ";

	public SqlLeftJoin() {
		// String result = " left outer join " + tf_moduleName + " " +
		// getTableAsName() + " on "
		// + getTableAsName() + "." + tf_primaryKey + " = " + childTableAsName + "."
		// + tf_primaryKey
		// + " ";

	}

	public String getJoinSql() {
		String result = joinString + moduleName + " " + tableAsName + " on " + tableAsName + "."
				+ primaryKey + " = " + childTableAsName + "." + primaryKey + " ";
		return result;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getTableAsName() {
		return tableAsName;
	}

	public void setTableAsName(String tableAsName) {
		this.tableAsName = tableAsName;
	}

	public String getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

	public String getChildModuleName() {
		return childModuleName;
	}

	public void setChildModuleName(String childModuleName) {
		this.childModuleName = childModuleName;
	}

	public String getChildTableAsName() {
		return childTableAsName;
	}

	public void setChildTableAsName(String childTableAsName) {
		this.childTableAsName = childTableAsName;
	}

	public String getJoinString() {
		return joinString;
	}

	public void setJoinString(String joinString) {
		this.joinString = joinString;
	}

}
