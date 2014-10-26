package com.ufo.framework.system.ebo;


import java.util.ArrayList;import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;
import ognl.Ognl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.model.hibernate.system._Module;
import com.ufo.framework.common.core.web.ModuleServiceFunction;
import com.ufo.framework.common.core.web.SortParameter;
import com.ufo.framework.system.ebi.ModelEbi;
import com.ufo.framework.system.irepertory.IModelRepertory;
import com.ufo.framework.system.repertory.SqlModuleFilter;
import com.ufo.framework.system.repertory.SystemBaseDAO;
import com.ufo.framework.system.shared.module.DataDeleteResponseInfo;
import com.ufo.framework.system.shared.module.DataFetchRequestInfo;
import com.ufo.framework.system.shared.module.DataFetchResponseInfo;
import com.ufo.framework.system.shared.module.DataInsertResponseInfo;
import com.ufo.framework.system.shared.module.DataUpdateResponseInfo;
import com.ufo.framework.system.shared.module.ModuleFormOperateType;
import com.ufo.framework.system.shared.module.grid.GridFilterData;

@Service
public class ModuleService extends Ebo implements ModelEbi {
	public static final int STATUS_FAILURE = -1;
	public static final int STATUS_LOGIN_INCORRECT = -5;
	public static final int STATUS_LOGIN_REQUIRED = -7;
	public static final int STATUS_LOGIN_SUCCESS = -8;
	public static final int STATUS_MAX_LOGIN_ATTEMPTS_EXCEEDED = -6;
	public static final int STATUS_SERVER_TIMEOUT = -100;
	public static final int STATUS_SUCCESS = 0;
	public static final int STATUS_TRANSPORT_ERROR = -90;
	public static final int STATUS_VALIDATION_ERROR = -4;
	public static final String UPDATEJSONOBJECT = "updateJsonObject";
	public static final String INSERTJSONOBJECT = "insertJsonObject";
	@Resource
	private IModelRepertory moduleDAO;
	
	public IModelRepertory getModuleDAO() {
		return moduleDAO;
	}

	public void setModuleDAO(IModelRepertory moduleDAO) {
		this.moduleDAO = moduleDAO;
	}

	// 返回json数据，要在这里加 application/json
	// produces = "application/json;text/plain;charset=UTF-8"
	// @Override
	/* (non-Javadoc)
	 * @see com.ufo.framework.system.ebo.ModelEbi#fetchData(java.lang.String, java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public Map<String, Object> fetchData(String moduleName, Integer start, Integer limit,
			String sort, String query, String columns, String navigates, String parentFilter,
			HttpServletRequest request) {
		DataFetchResponseInfo response = fetchDataInner(moduleName, start, limit, sort, query, columns,
				navigates, parentFilter, (SqlModuleFilter) null, request);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("records", response.getMatchingObjects());
		result.put("totalCount", response.getTotalRows());
		return result;
	}

	/* (non-Javadoc)
	 * @see com.ufo.framework.system.ebo.ModelEbi#fetchDataInner(java.lang.String, java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, com.ufo.framework.system.repertory.SqlModuleFilter, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public DataFetchResponseInfo fetchDataInner(String moduleName, Integer start, Integer limit,
			String sort, String query, String columns, String navigates, String parentFilter,
			SqlModuleFilter additionFilter, HttpServletRequest request) {
		System.out.println("sort: "+sort+"query :"+query+" columns: "+columns +" navigates ："+navigates +" parentFilter ");
	    System.out.println(parentFilter);
		SortParameter sorts[] = SortParameter.changeToSortParameters(sort);
		List<SqlModuleFilter> navigateFilters = changeToNavigateFilters(navigates);
		SqlModuleFilter pFilter = null;
		if (parentFilter != null && parentFilter.length() > 1) {
			JSONObject jo = JSONObject.fromObject(parentFilter);
			pFilter = (SqlModuleFilter) JSONObject.toBean(jo, SqlModuleFilter.class);
		}
		return fetchDataInner(moduleName, start, limit, sorts, query, columns, navigateFilters,
				pFilter, additionFilter, request);
	}

	/* (non-Javadoc)
	 * @see com.ufo.framework.system.ebo.ModelEbi#fetchDataInner(java.lang.String, java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.List, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public DataFetchResponseInfo fetchDataInner(String moduleName, Integer start, Integer limit,
			String sort, String query, String columns, String navigates, String parentFilter,
			List<SqlModuleFilter> additionFilters, HttpServletRequest request) {
		SortParameter sorts[] = SortParameter.changeToSortParameters(sort);
		List<SqlModuleFilter> navigateFilters = changeToNavigateFilters(navigates);
		navigateFilters.addAll(additionFilters);
		SqlModuleFilter pFilter = null;
		if (parentFilter != null && parentFilter.length() > 1) {
			JSONObject jo = JSONObject.fromObject(parentFilter);
			pFilter = (SqlModuleFilter) JSONObject.toBean(jo, SqlModuleFilter.class);
		}
		return fetchDataInner(moduleName, start, limit, sorts, query, columns, navigateFilters,
				pFilter, null, request);
	}

	/* (non-Javadoc)
	 * @see com.ufo.framework.system.ebo.ModelEbi#fetchDataInner(java.lang.String, java.lang.Integer, java.lang.Integer, com.ufo.framework.common.core.web.SortParameter[], java.lang.String, java.lang.String, java.util.List, com.ufo.framework.system.repertory.SqlModuleFilter, com.ufo.framework.system.repertory.SqlModuleFilter, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public DataFetchResponseInfo fetchDataInner(String moduleName, Integer start, Integer limit,
			SortParameter sorts[], String query, String columns, List<SqlModuleFilter> navigateFilters,
			SqlModuleFilter pFilter, SqlModuleFilter additionFilter, HttpServletRequest request) {

		DataFetchRequestInfo dsRequest = new DataFetchRequestInfo();
		dsRequest.setModuleName(moduleName);
		dsRequest.setStartRow(start);
		dsRequest.setEndRow(start + limit - 1);
		dsRequest.setSorts(sorts);

		GridFilterData gridFilterData = new GridFilterData();
		if (pFilter != null) {
			gridFilterData.setParentModuleFilter(pFilter);
		}
		gridFilterData.setSearchText(query);
		if (columns != null)
			gridFilterData.setGridColumnNames(columns.split(","));
		DataFetchResponseInfo response = moduleDAO.getModuleData(moduleName, dsRequest, gridFilterData,
				request);
		return response;
	}

	// @Override
	/* (non-Javadoc)
	 * @see com.ufo.framework.system.ebo.ModelEbi#getRecordNewDefault(java.lang.String, java.lang.String, java.lang.String, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public Object getRecordNewDefault(String moduleName, String parentFilter, String navigates,
			HttpServletRequest request) {

		Map<String, Object> result = new HashMap<String, Object>();
		List<SqlModuleFilter> navigateFilters = changeToNavigateFilters(navigates);

		// 父模块约束的加入
		GridFilterData gridFilterData = new GridFilterData();

		// 导航值的加入，某些模块的初始值，需要使用到导航值
		request.setAttribute("navigate", navigateFilters);

		// gridFilterData.setNavigateTreeSelected(navigateTreeSelected)
		if (parentFilter != null && parentFilter.length() > 1) {
			JSONObject jo = JSONObject.fromObject(parentFilter);
			SqlModuleFilter pFilter = (SqlModuleFilter) JSONObject.toBean(jo, SqlModuleFilter.class);
			gridFilterData.setParentModuleFilter(pFilter);
		}

		return result;
	}

	// @Override
	/* (non-Javadoc)
	 * @see com.ufo.framework.system.ebo.ModelEbi#getRecordById(java.lang.String, java.lang.String, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public Object getRecordById(String moduleName, String id, HttpServletRequest request) {
		debug("根据主键取得模块的一条记录:" + moduleName + "," + id);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("totalCount", 1);
		List<Object> records = new ArrayList<Object>();
		try {
			records.add(moduleDAO.getModuleRecord(moduleName, id, request).toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("records", records);
		debug("getRecordById返回值：" + result.toString());
		return result;
	}

	// @Override
	/* (non-Javadoc)
	 * @see com.ufo.framework.system.ebo.ModelEbi#add(java.lang.String, java.lang.String, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public DataInsertResponseInfo add(String moduleName, String inserted, HttpServletRequest request) {

		debug("数据insert:" + moduleName + ":" + inserted);

		JSONObject updateJsonObject = JSONObject.fromObject(inserted);
		request.setAttribute(INSERTJSONOBJECT, updateJsonObject);

		DataInsertResponseInfo result = new DataInsertResponseInfo();
		_Module module = ApplicationService.getModuleWithName(moduleName);

		Class<?> beanClass = ModuleServiceFunction.getModuleBeanClass(moduleName);
		try {
			Object record = Class.forName(beanClass.getName()).newInstance();
			moduleDAO.updateValueToBean(moduleName, record, updateJsonObject);

			save(record);
			// systemBaseDAO.getHibernateTemplate().evict(record)
			// 写入数据了以后，可能会有计算字段等 信息，重新读取
			record = findById(beanClass, Ognl.getValue(module.getTf_primaryKey(), record));
			// System.out.println("idkey : " +
			// Ognl.getValue(module.getTf_primaryKey(), record));
			// 写入日志

			result.setResultCode(STATUS_SUCCESS);

			result.setModuleName(moduleName);
			result.setKey(Ognl.getValue(module.getTf_primaryKey(), record).toString());
		} catch (DataAccessException e) {
			e.printStackTrace();
			// 这个出错是不会执行到的，这是没有加事务的出错 检查，
			ModuleServiceFunction.addExceptionCauseToErrorMessage(e, result.getErrorMessage(),
					module.getTf_primaryKey());
			result.setResultCode(STATUS_VALIDATION_ERROR);
		} catch (Exception e) {
			e.printStackTrace();
			result.getErrorMessage().put("error", e.getMessage());
			result.setResultCode(STATUS_FAILURE);
		}
		debug("insert返回值：" + result.toString());

		return result;
	}

	/* (non-Javadoc)
	 * @see com.ufo.framework.system.ebo.ModelEbi#changeRecordId(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public DataUpdateResponseInfo changeRecordId(String moduleName, String id, String oldid) {
		return moduleDAO.changeRecordId(moduleName, id, oldid);
	}

	// @Override
	/* (non-Javadoc)
	 * @see com.ufo.framework.system.ebo.ModelEbi#update(java.lang.String, java.lang.String, java.lang.String, java.lang.String, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public DataUpdateResponseInfo update(String moduleName, String id, String operType,
			String updated, HttpServletRequest request) {
		return moduleDAO.update(moduleName, id, operType, updated, request);

	}

	// @Override
	/* (non-Javadoc)
	 * @see com.ufo.framework.system.ebo.ModelEbi#remove(java.lang.String, java.lang.String, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public DataDeleteResponseInfo remove(String moduleName, String id, HttpServletRequest request) {
		debug("数据delete:模块" + moduleName + ",主键" + id);
		DataDeleteResponseInfo result = new DataDeleteResponseInfo();
		Class<?> beanClass = ModuleServiceFunction.getModuleBeanClass(moduleName);
		try {
			Object record = findById(beanClass, id);
			delete(record);
			result.setResultCode(STATUS_SUCCESS);
		} catch (DataIntegrityViolationException e) {
			result.setResultMessage(-1, "请检查与本记录相关联的其他数据是否全部清空！");
			e.printStackTrace();
		} catch (Exception e) {
			result.setResultMessage(-1, e.getMessage());
			e.printStackTrace();
		}
		debug("delete返回值：" + result.toString());
		return result;
	}

	/**
	 * 根据字符串返回grid导航的数据，生成一个数组
	 * 
	 * @param str
	 * @return
	 */
	private List<SqlModuleFilter> changeToNavigateFilters(String str) {
		List<SqlModuleFilter> result = new ArrayList<SqlModuleFilter>();
		if (str != null && str.length() > 5) {
			JsonConfig config = new JsonConfig();
			config.setArrayMode(JsonConfig.MODE_OBJECT_ARRAY);
			config.setRootClass(SqlModuleFilter.class);
			SqlModuleFilter[] navigateFilters = (SqlModuleFilter[]) JSONSerializer.toJava(
					JSONArray.fromObject(str), config);
			// System.out.println(navigateFilters[0]);
			for (SqlModuleFilter f : navigateFilters)
				result.add(f);
		}
		return result;

	}

}
