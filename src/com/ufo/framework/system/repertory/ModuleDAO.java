package com.ufo.framework.system.repertory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import ognl.Ognl;
import ognl.OgnlException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.model.hibernate.addition._Addition;
import com.model.hibernate.system.Module;
import com.ufo.framework.common.core.json.JsonDateProcessor;
import com.ufo.framework.common.core.web.ModuleServiceFunction;
import com.ufo.framework.common.core.web.TypeChange;
import com.ufo.framework.system.ebo.ApplicationService;
import com.ufo.framework.system.shared.module.DataFetchRequestInfo;
import com.ufo.framework.system.shared.module.DataFetchResponseInfo;
import com.ufo.framework.system.shared.module.grid.GridFilterData;

@Repository
public class ModuleDAO {

	private static final Log log = LogFactory.getLog(ModuleDAO.class);

	@Autowired
	private SystemBaseDAO systemBaseDAO;

	/**
	 * 根据模块的名称和 一个 name 的值来判断是否此模块已经有这个值了
	 * 
	 * @param moduleName
	 * @param name
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Object getModuleDataWithName(String moduleName, String name) {
		Module module = ApplicationService.getModuleWithName(moduleName);
		if (module == null)
			return null;
		List<?> records = systemBaseDAO.findByProperty(moduleName, module.getTf_nameFields(), name);
		if (records.size() >= 1)
			return records.get(0);
		else
			return null;
	}

	/**
	 * 根据module 和一个传进来的值，找到相应的记录,返回主键
	 * 
	 * @param moduleName
	 * @param idOrName
	 * @return
	 */
	public Object getBeanIdWithIdOrName(Module module, Object idOrName) {
		Object bean = getBeanWithIdOrName(module, idOrName);
		if (bean == null)
			return null;
		else
			try {
				return Ognl.getValue(module.getTf_primaryKey(), bean);
			} catch (OgnlException e) {
				e.printStackTrace();
				return null;
			}
	}

	/**
	 * 根据module 和一个传进来的值，找到相应的记录
	 * 如果这是一个父模块的字段，1.先检查是否是主键，2.检查是否是fieldnames,3.检查是否只有一个符合条件的like,
	 * 
	 * @param module
	 * @param idOrName
	 * @return
	 */
	public Object getBeanWithIdOrName(Module module, Object idOrName) {
		Class<?> BeanClass = ModuleServiceFunction.getModuleBeanClass(module.getTf_moduleName());
		Object bean = null;
		try {
			bean = systemBaseDAO.findById(BeanClass, idOrName);
		} catch (Exception e) {
		}
		if (bean == null) {
			try {
				List<?> beans = systemBaseDAO
						.findByProperty(BeanClass, module.getTf_nameFields(), idOrName);
				if (beans.size() == 1)
					bean = beans.get(0);
				else if ((beans.size() > 1))
					return null;
			} catch (Exception e) {
			}
		}
		if (bean == null) {
			try {
				@SuppressWarnings("unchecked")
				List<Object> beans = systemBaseDAO.findByLikeProperty(BeanClass.getSimpleName(),
						module.getTf_nameFields(), "%" + idOrName + "%");
				if (beans.size() == 1)
					bean = beans.get(0);
			} catch (Exception e) {
			}
		}
		return bean;
	}

	/**
	 * 用户修改了数据之后，将修改过的值updae到bean中去
	 * 
	 * @param moduleName
	 * @param object
	 * @param keyValue
	 * @throws OgnlException
	 */

	@SuppressWarnings("unchecked")
	public void updateValueToBean(String moduleName, Object record, JSONObject keyValue)
			throws OgnlException {
		Module module = ApplicationService.getModuleWithName(moduleName);
		Iterator<String> keyIterator = keyValue.keys();
		while (keyIterator.hasNext()) {
			String key = keyIterator.next();
			Object value = keyValue.get(key);
			// 是不是manytoone 的值进行了修改
			log.debug("更新字段:" + key + ",value:" + value);
			ModuleServiceFunction.setValueToRecord(key, record, value);
		}
	}


	/**
	 * 根据前台传进来的参数取得list 数据，然后返回
	 * 
	 * @param moduleName
	 * @param dsRequest
	 * @param gridFilterData
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public DataFetchResponseInfo getModuleData(String moduleName, DataFetchRequestInfo dsRequest,
			GridFilterData gridFilterData, HttpServletRequest request) {
		Module module = ApplicationService.getModuleWithName(moduleName);

		// 所有的导航tree产生的过滤条件
		List<SqlModuleFilter> treeAndParentFilters = new ArrayList<SqlModuleFilter>();

		addParentModuleFiltToSQLFilters(module, gridFilterData.getParentModuleFilter(),
				treeAndParentFilters);

		SqlGenerator generator = new SqlGenerator(module, request);

		generator.setModuleFilters(treeAndParentFilters);
		generator.setGridColumnNames(gridFilterData.getGridColumnNames());
		generator.setSearchText(gridFilterData.getSearchText());
		generator.setSorts(dsRequest.getSorts());
		generator.setGroupFieldname(gridFilterData.getGroupFieldName());
		Integer totalRow = getRecordCount(generator);
		log.debug("统计计录个数:" + totalRow);

		Integer startRow = dsRequest.getStartRow();
		Integer endRow = dsRequest.getEndRow();
		endRow = Math.min(endRow, totalRow - 1);

		JSONArray jsonArray = getData(generator, startRow, endRow);
		DataFetchResponseInfo response = new DataFetchResponseInfo();
		response.setStartRow(startRow);
		response.setEndRow(endRow);
		response.setTotalRows(totalRow);
		// if (dsRequest.getIsExport())
		response.setMatchingObjects(jsonArray);
		// else
		// response.setJsonMatchingItems(jsonArray.toString());

		return response;

	}

	/**
	 * // 如果有父模块约束，加入父模块约束
	 * 
	 * @param moduleName
	 * @param module
	 * @param parentModuleFilter
	 * @param treeAndParentFilters
	 */
	private void addParentModuleFiltToSQLFilters(Module module, SqlModuleFilter parentModuleFilter,
			List<SqlModuleFilter> treeAndParentFilters) {
		// 如果有父模块约束，加入父模块约束
		if (parentModuleFilter != null) {
			// 如果是附件的父模块约束，则要加入另外二个条件
			if (module.getTf_moduleName().equals(_Addition._ADDITION)) {
				SqlModuleFilter additionModuleIdFilter = new SqlModuleFilter();
				additionModuleIdFilter.setModuleName(module.getTf_moduleName());
				additionModuleIdFilter.setTableAsName(module.getTableAsName());
				additionModuleIdFilter.setPrimarykey(_Addition.MODULEID);
				additionModuleIdFilter.setEqualsValue(parentModuleFilter.getModuleId());
				treeAndParentFilters.add(additionModuleIdFilter);

				SqlModuleFilter additionModuleKeyIdFilter = new SqlModuleFilter();
				additionModuleKeyIdFilter.setModuleName(module.getTf_moduleName());
				additionModuleKeyIdFilter.setTableAsName(module.getTableAsName());
				additionModuleKeyIdFilter.setPrimarykey(_Addition.MODULEKEYID);
				additionModuleKeyIdFilter.setEqualsValue(parentModuleFilter.getEqualsValue());
				treeAndParentFilters.add(additionModuleKeyIdFilter);

			} else {
				treeAndParentFilters.add(parentModuleFilter);
			}
		}
	}


	/**
	 * 根据前台传进来的参数取一个模块的 record 数据，然后返回 用在用户增加，修改了数据之后，将修改新增的数据，通过这里取得数据后返回
	 * 
	 * @param moduleName
	 * @param keyValue
	 * @return JSONObject
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public JSONObject getModuleRecord(String moduleName, String keyValue, HttpServletRequest request) {
		Module module = ApplicationService.getModuleWithName(moduleName);
		SqlGenerator generator = new SqlGenerator(module, request);
		generator.setKeyValue(keyValue);
		JSONArray jsonArray = getData(generator, -1, 0);
		if (jsonArray.size() > 0)
			return jsonArray.getJSONObject(0);
		else
			return null;
	}

	/**
	 * 取得记录的个数
	 * 
	 * @param generator
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Integer getRecordCount(SqlGenerator generator) {

		String sql = generator.getCountSqlStatement();
		Session session = systemBaseDAO.getSessionFactory().getCurrentSession();
		SQLQuery query = session.createSQLQuery(sql);
		Integer countInteger = 0;
		try {
			countInteger = TypeChange.toInt(query.uniqueResult());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return countInteger;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public JSONArray getData(SqlGenerator generator, Integer startRow, Integer endRow) {

		String sql = generator.getSqlStatment();
		Session session = systemBaseDAO.getSessionFactory().getCurrentSession();
		SQLQuery query = session.createSQLQuery(sql);
		if (startRow != -1) {
			query.setFirstResult(startRow);
			query.setMaxResults(endRow - startRow + 1);
		}
		generator.addScalar(query);

		List<?> results = null;
		try {
			results = query.list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		JSONArray resultArray = new JSONArray();
		if (results != null)
			for (Object row : results) {
				Object[] rowObjects = (Object[]) row;
				Map<String, Object> objMap = new LinkedHashMap<String, Object>();
				JSONObject object = new JSONObject();
				int i = 0;
				for (SqlField field : generator.getFieldList())
					objMap.put(field.getFieldasScalar(), rowObjects[i++]);
				for (SqlField field : generator.getJoinField())
					objMap.put(field.getFieldasScalar(), rowObjects[i++]);
				object.putAll(objMap, JsonDateProcessor.us_jsonConfig);
				resultArray.add(object);
			}
		return resultArray;
	}

	public String getRecordNameValue(Module module, Object record) {
		String result = "";
		try {
			result = (module.getTf_nameFields() != null && module.getTf_nameFields().length() > 0) ? Ognl
					.getValue(module.getTf_nameFields(), record).toString() : "未定义";
		} catch (Exception e) {
		}
		return result;
	}

}
