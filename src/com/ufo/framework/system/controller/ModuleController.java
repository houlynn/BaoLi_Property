package com.ufo.framework.system.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.model.hibernate.system._Module;
import com.ufo.framework.common.core.web.ModuleServiceFunction;
import com.ufo.framework.common.log.LogerManager;
import com.ufo.framework.system.ebi.ModelEbi;
import com.ufo.framework.system.ebo.ApplicationService;
import com.ufo.framework.system.ebo.ModuleService;
import com.ufo.framework.system.irepertory.IModelRepertory;
import com.ufo.framework.system.repertory.SqlModuleFilter;
import com.ufo.framework.system.repertory.SystemBaseDAO;
import com.ufo.framework.system.shared.module.DataDeleteResponseInfo;
import com.ufo.framework.system.shared.module.DataFetchResponseInfo;
import com.ufo.framework.system.shared.module.DataInsertResponseInfo;
import com.ufo.framework.system.shared.module.DataUpdateResponseInfo;
@Controller
@RequestMapping(value = "/module")
/**
 * 所有模块的的ＣＲＵＤ都是调用这个类的函数来完成的Controller里面完成的
 *
* @author HouLynn
* @date 2014年10月21日
  @version 1.0
 */
public class ModuleController implements LogerManager {

	public ModuleController() {
		debug(this.getClass().getName());
	}

	@Resource
	private SystemBaseDAO systemBaseDAO;

	@Resource
	private ModelEbi moduleService;

	@Resource
	private IModelRepertory moduleDAO;

	private static final Log log = LogFactory.getLog(ModuleController.class);

	/**
	 * 根据前台的请求取得数据
	 */
	@RequestMapping(value = "/fetchdata", method = RequestMethod.GET)
	public @ResponseBody
	Map<String, Object> fetchData(String moduleName, Integer start, Integer limit, String sort,
			String query, String columns, String navigates, String parentFilter,
			HttpServletRequest request) {
		DataFetchResponseInfo response = moduleService.fetchDataInner(moduleName, start, limit, sort,
				query, columns, navigates, parentFilter, (SqlModuleFilter) null, request);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("records", response.getMatchingObjects());
		result.put("totalCount", response.getTotalRows());
		return result;
	}

	/**
	 * 新增记录的时候，在后台取得缺省值
	 * 
	 * @param moduleName
	 * @param parentFilter
	 * @param navigates
	 * @param request
	 * @return
	 */

	@RequestMapping(value = "/getnewdefault.do", method = RequestMethod.POST)
	public @ResponseBody
	Object getRecordNewDefault(String moduleName, String parentFilter, String navigates,
			HttpServletRequest request) {

		return moduleService.getRecordNewDefault(moduleName, parentFilter, navigates, request);

	}

	@RequestMapping(value = "/fetchdata.do/{id}", method = RequestMethod.GET)
	public @ResponseBody
	Object getRecordById(String moduleName, @PathVariable("id") String id, HttpServletRequest request) {
		log.debug("根据主键取得模块的一条记录:" + moduleName + "," + id);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("totalCount", 1);
		List<Object> records = new ArrayList<Object>();
		try {
			records.add(moduleDAO.getModuleRecord(moduleName, id, request).toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("records", records);
		log.debug("getRecordById返回值：" + result.toString());
		return result;
	}

	/**
	 * 创建一条记录
	 * 
	 * @param moduleName
	 * @param inserted
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/create.do", method = RequestMethod.POST)
	public @ResponseBody
	DataInsertResponseInfo addWithNoPrimaryKey(String moduleName, @RequestBody String inserted,
			HttpServletRequest request) {

		return add(moduleName, inserted, request);
	}

	@RequestMapping(value = "/create.do/{id}", method = RequestMethod.POST)
	public @ResponseBody
	DataInsertResponseInfo add(String moduleName, @RequestBody String inserted,
			HttpServletRequest request) {
		DataInsertResponseInfo result = null;
		try {
			result = moduleService.add(moduleName, inserted, request);
			if (result.getKey() != null) // 如果是空的话，那么就没有保存，错误原因已经在errorMessage里了
				result.getRecords().add(
						moduleDAO.getModuleRecord(moduleName, result.getKey(), request).toString());
		} catch (DataAccessException e) {
			e.printStackTrace();
			if (result == null)
				result = new DataInsertResponseInfo();
			result.setResultCode(ModuleService.STATUS_VALIDATION_ERROR);
		} catch (Exception e) {
			e.printStackTrace();
			if (result == null)
				result = new DataInsertResponseInfo();
			result.getErrorMessage().put("error", e.getMessage());
			result.setResultCode(ModuleService.STATUS_FAILURE);
		}
		return result;
	}

	/**
	 * 修改记录
	 * 
	 * @param moduleName
	 * @param id
	 * @param oldid
	 * @param operType
	 * @param updated
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/update.do/{id}", method = RequestMethod.PUT)
	public @ResponseBody
	DataUpdateResponseInfo update(String moduleName, @PathVariable("id") String id, String oldid,
			String operType, @RequestBody String updated, HttpServletRequest request) {

		DataUpdateResponseInfo result = null;
		_Module module = ApplicationService.getModuleWithName(moduleName);
		// 如果主键值修改了，那么先进行主键的修改
		if (oldid != null && (!oldid.equals(id))) {
			try {
				result = moduleService.changeRecordId(moduleName, id, oldid);
			} catch (ConstraintViolationException e) {
				e.printStackTrace();
				result = new DataUpdateResponseInfo();
				if (e.getCause().toString().toLowerCase().indexOf("primary key") >= 0)
					result.getErrorMessage().put(module.getTf_primaryKey(), "修改过后的主键与原有的主键值重复！");
				else
					result.getErrorMessage().put(module.getTf_primaryKey(), "当前主键与子模块有约束关系，不可以修改！");
				result.setResultCode(ModuleService.STATUS_VALIDATION_ERROR);
			}
			if (!result.getResultCode().equals(0))
				return result;
		}
		// 修改记录
		try {
			result = moduleService.update(moduleName, id, operType, updated, request);
			result.getRecords().add(moduleDAO.getModuleRecord(moduleName, id, request).toString());
		} catch (DataAccessException e) {
			result = new DataUpdateResponseInfo();
			result.setResultCode(ModuleService.STATUS_VALIDATION_ERROR);
		} catch (Exception e) {
			e.printStackTrace();
			result = new DataUpdateResponseInfo();
			result.getErrorMessage().put("error", e.getMessage());
			result.setResultCode(ModuleService.STATUS_FAILURE);
		}
		return result;
	}

	/**
	 * 删除记录
	 * 
	 * @param moduleName
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/remove.do/{id}", method = RequestMethod.DELETE)
	public @ResponseBody
	DataDeleteResponseInfo remove(String moduleName, @PathVariable String id,
			HttpServletRequest request) {
		DataDeleteResponseInfo result = null;
		try {
			result = moduleService.remove(moduleName, id, request);
		} catch (DataAccessException e) {
			result = new DataDeleteResponseInfo();
			String errormessage = ModuleServiceFunction.addPK_ConstraintMessage(e, moduleName);
			result.setResultMessage(-1, errormessage != null ? errormessage
					: "请检查与本记录相关联的其他数据是否全部清空！<br/>");
		} catch (Exception e) {
			result = new DataDeleteResponseInfo();
			result.setResultMessage(-1, e.getMessage());
		}
		return result;
	}

	@RequestMapping(value = "/removerecords.do")
	public @ResponseBody
	DataDeleteResponseInfo removeRecords(String moduleName, String ids, String titles,
			HttpServletRequest request) {
		DataDeleteResponseInfo result = null;
		String[] idarray = ids.split(",");
		String[] titlearray = titles.split("~~");

		result = new DataDeleteResponseInfo();

		for (int i = 0; i < idarray.length; i++) {
			try {
				DataDeleteResponseInfo recordDeleteResult = moduleService.remove(moduleName, idarray[i],
						request);
				if (recordDeleteResult.getResultCode() == 0)

					result.getOkMessageList().add(titlearray[i]);
				else {
					if (recordDeleteResult.getErrorMessageList().size() > 0)
						result.getErrorMessageList().add(
								"【" + titlearray[i] + "】" + recordDeleteResult.getErrorMessageList().get(0));
					else
						result.getErrorMessageList().add("【" + titlearray[i] + "】" + "未知错误！");
				}
			} catch (DataAccessException e) {
				String errormessage = ModuleServiceFunction.addPK_ConstraintMessage(e, moduleName);

				result.getErrorMessageList().add(
						"【" + titlearray[i] + "】" + (errormessage != null ? errormessage : "关联的数据未清空！"));
			} catch (Exception e) {
				result.getErrorMessageList().add("【" + titlearray[i] + "】" + e.getMessage());
			}
		}
		result.setResultCode(result.getErrorMessageList().size());
		return result;
	}

}
