package com;

import ognl.Ognl;
import ognl.OgnlException;

import com.model.hibernate.system._MenuModule;

public class Test {

	public static void main(String[] args) throws OgnlException {
		_MenuModule menu=new _MenuModule();
		menu.setTf_ModuleId("我为I爱死我iawojaiwi");
		System.out.println(Ognl.getValue("tf_ModuleId", menu));
	}
	
	
	
}
