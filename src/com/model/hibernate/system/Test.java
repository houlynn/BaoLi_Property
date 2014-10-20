package com.model.hibernate.system;
import java.io.UnsupportedEncodingException;
import com.ufo.framework.common.core.utils.ClassUtil;

public class Test {
	public static void main(String[] args) throws UnsupportedEncodingException {
		
	ClassUtil.getClassName("com.model.hibernate.system", true).forEach(item->System.out.println(item));
	}

	
	
}
