package com.ufo.framework.system.ebo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.model.app.common.ApplicationInfo;
import com.model.app.common.UserInfo;
import com.model.hibernate.system.MenuGroup;
import com.model.hibernate.system.Module;
import com.model.hibernate.system.ModuleFormScheme;
import com.model.hibernate.system.ModuleFormSchemeGroup;
import com.model.hibernate.system.ModuleGridScheme;
import com.model.hibernate.system.ModuleGridSchemeGroup;
import com.model.hibernate.system.ServiceInfo;
import com.model.hibernate.system.SystemInfo;
import com.ufo.framework.common.core.utils.AppUtils;
import com.ufo.framework.system.repertory.SystemBaseDAO;

@Service
public class ApplicationService {

	@Resource
	private SystemBaseDAO systemBaseDAO;

	private static List<Module> modules = null;

	// 事务注释
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public ApplicationInfo getApplicationInfo(HttpServletRequest request) {

		ApplicationInfo result = new ApplicationInfo();

		// 以上内容暂时为自定义的，以后会改为从数据库和登录信息中读取。
		SystemInfo systemInfo = new SystemInfo();
		systemInfo.setTf_systemName("保利物业管理系统");
		systemInfo.setTf_systemVersion(AppUtils.getCurDate());
		result.setSystemInfo(systemInfo);

		UserInfo userInfo = new UserInfo();
		userInfo.setTf_userdwmc("红花会总坛");
		userInfo.setTf_userStartdate(new Date());
		userInfo.setTf_userName("管理员");
		userInfo.setTf_loginName("admin");
		userInfo.setTf_userId(0);
		userInfo.setTf_departmentId("00");
		userInfo.setTf_departmentName("工程部");
		result.setUserInfo(userInfo);

		ServiceInfo serviceInfo = new ServiceInfo();
		serviceInfo.setTf_serviceDepartment("广州英趣科技有限公司");
		serviceInfo.setTf_serviceMen("独孤厚玲");
		serviceInfo.setTf_serviceTelnumber("1320528xxxx");
		serviceInfo.setTf_serviceFaxnumber("0510-88888888");
		serviceInfo.setTf_serviceQQ("7858xxxx");
		serviceInfo.setTf_serviceEmail("houlynn@gzinterest.com");
		serviceInfo.setTf_serviceHomepage("www.www.net");
		serviceInfo.setTf_copyrightInfo("广州英趣信息科技有限公司");
		serviceInfo.setTf_copyrightOwner("精英软件");

		result.setServiceInfo(serviceInfo);

		// 把所有的模块定义信息加进去
		result
				.setTf_Modules(new HashSet<Module>((List<Module>) systemBaseDAO.findAll(Module.class)));

		// 加入这一条是为了让菜单组下面的菜单也执行sql 语句加进来，不然的话，返回以后mvc要加入菜单，
		// 就会在执行sql的时候因为session已经关闭而报错
		for (Module module : result.getTf_Modules()) {
			module.getTf_fields().size();
			for (ModuleGridScheme scheme : module.getTf_gridSchemes()) {
				for (ModuleGridSchemeGroup group : scheme.getTf_schemeGroups()) {
					group.getTf_groupFields().size();
				}
			}
			for (ModuleFormScheme scheme : module.getTf_formSchemes()) {
				for (ModuleFormSchemeGroup group : scheme.getTf_schemeGroups()) {
					group.getTf_groupFields().size();
				}
			}

		}

		// 加入菜单分组
		result.setTf_MenuGroups((List<MenuGroup>) systemBaseDAO.findAll(MenuGroup.class));

		for (MenuGroup mg : result.getTf_MenuGroups()) {
			// 加入这一条是为了让菜单组下面的菜单也执行sql 语句加进来，不然的话，返回以后mvc要加入菜单，
			// 就会在执行sql的时候因为session已经关闭而报错
			mg.getTf_menuModules().size();
		}

		modules = new ArrayList<Module>(result.getTf_Modules());

		return result;
	}

	// 根据模块 name 号取得模块定义
	public static Module getModuleWithName(String name) {
		for (Module module : getModules())
			if (module.getTf_moduleName().equals(name))
				return module;
		return null;
	}

	public static List<Module> getModules() {
		return modules;
	}

	public static void setModules(List<Module> modules) {
		ApplicationService.modules = modules;
	}

}
