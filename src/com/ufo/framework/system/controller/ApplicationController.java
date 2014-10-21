package com.ufo.framework.system.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.model.app.common.ApplicationInfo;
import com.ufo.framework.system.ebo.ApplicationService;


@Controller
public class ApplicationController {

	// spring注释，自动注入ApplicationService 的实例
	@Resource
	private ApplicationService applicationService;

	@RequestMapping("/applicationinfo.do")
	public synchronized @ResponseBody
	ApplicationInfo getApplicationInfo(HttpServletRequest request) {
		return applicationService.getApplicationInfo(request);
	}
}
