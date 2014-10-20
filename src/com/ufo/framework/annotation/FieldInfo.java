package com.ufo.framework.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
* @author HouLynn
* @date 2014年10月17日
  @version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldInfo {
		
		String title();

		int number() default 0;

		String remark() default "";

		boolean uniqueField() default false;

		boolean hidden() default false;

		String fieldGroup() default "默认组";

		boolean money() default false;
		
		boolean percent() default false;
}
