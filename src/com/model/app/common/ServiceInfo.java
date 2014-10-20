package com.model.app.common;

import java.io.Serializable;


@SuppressWarnings("serial")
// 这是服务单位情况的设置，也是放在数据库里的，可以进行修改
public class ServiceInfo implements Serializable {

	private String tf_serviceDepartment;// 服务单位
	private String tf_serviceMen;// 服务人员
	private String tf_serviceTelnumber;// 联系电话
	private String tf_serviceFaxnumber;// 传真
	private String tf_serviceEmail;// 电子邮件
	private String tf_serviceHomepage;// 主页
	private String tf_serviceQQ;// ＱＱ号
	private String tf_copyrightOwner;// 版权所有单位
	private String tf_copyrightInfo;// 版权信息

	public ServiceInfo() {

	}

	public String getTf_serviceDepartment() {
		return tf_serviceDepartment;
	}

	public void setTf_serviceDepartment(String tf_serviceDepartment) {
		this.tf_serviceDepartment = tf_serviceDepartment;
	}

	public String getTf_serviceMen() {
		return tf_serviceMen;
	}

	public void setTf_serviceMen(String tf_serviceMen) {
		this.tf_serviceMen = tf_serviceMen;
	}

	public String getTf_serviceTelnumber() {
		return tf_serviceTelnumber;
	}

	public void setTf_serviceTelnumber(String tf_serviceTelnumber) {
		this.tf_serviceTelnumber = tf_serviceTelnumber;
	}

	public String getTf_serviceFaxnumber() {
		return tf_serviceFaxnumber;
	}

	public void setTf_serviceFaxnumber(String tf_serviceFaxnumber) {
		this.tf_serviceFaxnumber = tf_serviceFaxnumber;
	}

	public String getTf_serviceEmail() {
		return tf_serviceEmail;
	}

	public void setTf_serviceEmail(String tf_serviceEmail) {
		this.tf_serviceEmail = tf_serviceEmail;
	}

	public String getTf_serviceHomepage() {
		return tf_serviceHomepage;
	}

	public void setTf_serviceHomepage(String tf_serviceHomepage) {
		this.tf_serviceHomepage = tf_serviceHomepage;
	}

	public String getTf_serviceQQ() {
		return tf_serviceQQ;
	}

	public void setTf_serviceQQ(String tf_serviceQQ) {
		this.tf_serviceQQ = tf_serviceQQ;
	}

	public String getTf_copyrightOwner() {
		return tf_copyrightOwner;
	}

	public void setTf_copyrightOwner(String tf_copyrightOwner) {
		this.tf_copyrightOwner = tf_copyrightOwner;
	}

	public String getTf_copyrightInfo() {
		return tf_copyrightInfo;
	}

	public void setTf_copyrightInfo(String tf_copyrightInfo) {
		this.tf_copyrightInfo = tf_copyrightInfo;
	}

}