package com.model.app.common;

import java.io.Serializable;

//这是系统总体情况的设置，也是放在数据库里的，可以进行修改
@SuppressWarnings("serial")
public class SystemInfo implements Serializable {

	private String tf_systemName; // 系统名称
	private String tf_systemVersion; // 版本号
	private String tf_systemAddition;// 附加设置

	public SystemInfo() {

	}

	public String getTf_systemName() {
		return tf_systemName;
	}

	public void setTf_systemName(String tf_systemName) {
		this.tf_systemName = tf_systemName;
	}

	public String getTf_systemVersion() {
		return tf_systemVersion;
	}

	public void setTf_systemVersion(String tf_systemVersion) {
		this.tf_systemVersion = tf_systemVersion;
	}

	public String getTf_systemAddition() {
		return tf_systemAddition;
	}

	public void setTf_systemAddition(String tf_systemAddition) {
		this.tf_systemAddition = tf_systemAddition;
	}

}