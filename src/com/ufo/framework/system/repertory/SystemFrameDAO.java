package com.ufo.framework.system.repertory;

import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.model.hibernate.system.ModuleField;
import com.model.hibernate.system.ModuleFormScheme;
import com.model.hibernate.system.ModuleFormSchemeGroup;
import com.model.hibernate.system.ModuleFormSchemeGroupField;
import com.model.hibernate.system.ModuleGridScheme;
import com.model.hibernate.system.ModuleGridSchemeGroup;
import com.model.hibernate.system.ModuleGridSchemeGroupField;

@Repository
public class SystemFrameDAO {

	@Resource
	private SystemBaseDAO systemBaseDAO;

	/**
	 * 查找某个gridgroup是否包含某个字段
	 * 
	 * @param gridGroupId
	 * @param fieldId
	 * @param tag
	 * @return true or false
	 */
	@SuppressWarnings("unchecked")
	public Boolean isGridGroupHasField(Integer gridGroupId, Integer fieldId) {
		List<ModuleGridSchemeGroupField> fields = (List<ModuleGridSchemeGroupField>) systemBaseDAO
				.findByPropertyWithOtherCondition(ModuleGridSchemeGroupField.class,
						"tf_ModuleGridSchemeGroup.tf_gridGroupId", gridGroupId, " tf_ModuleField.tf_fieldId = "
								+ fieldId);
		return fields.size() > 0;
	}


	/**
	 * 查找某个formgroup是否包含某个字段
	 * 
	 * @param gridGroupId
	 * @param fieldId
	 * @param tag
	 * @return true or false
	 */
	@SuppressWarnings("unchecked")
	public Boolean isFormGroupHasField(Integer formGroupId, Integer fieldId) {
		List<ModuleFormSchemeGroupField> fields = (List<ModuleFormSchemeGroupField>) systemBaseDAO
				.findByPropertyWithOtherCondition(ModuleFormSchemeGroupField.class,
						"tf_ModuleFormSchemeGroup.tf_formGroupId", formGroupId, " tf_ModuleField.tf_fieldId = "
								+ fieldId);
		return fields.size() > 0;
	}



	@SuppressWarnings("unchecked")
	public List<ModuleField> get_ModuleFields(String moduleId) {
		Session session = systemBaseDAO.getSf().getCurrentSession();
		Criteria criteria = session.createCriteria(ModuleField.class);
		Criteria moduleCriteria = criteria.createCriteria("tf_Module");
		moduleCriteria.add(Restrictions.eq("tf_moduleId", moduleId));
		return (List<ModuleField>) criteria.list();

	}

	/**
	 * 在自动新增一个grid方案时，取得模块的下一个grid方案的序号
	 * 
	 * @param moduleId
	 * @return
	 */
	public Integer getNextGridSchemeOrder(String moduleId) {
		Session session = systemBaseDAO.getSf().getCurrentSession();
		Criteria criteria = session.createCriteria(ModuleGridScheme.class);
		Criteria moduleCriteria = criteria.createCriteria("tf_Module");
		moduleCriteria.add(Restrictions.eq("tf_moduleId", moduleId));
		criteria.setProjection(Projections.max("tf_schemeOrder"));
		List<?> results = criteria.list();
		if (results.get(0) == null)
			return 1;
		else
			return (Integer) results.get(0) + 1;
	}

	/**
	 * 取得一个模块的字段的最大序号
	 * 
	 * @param moduleId
	 * @return
	 */
	public Integer getMaxModuleFieldId(String moduleId) {
		Session session = systemBaseDAO.getSf().getCurrentSession();
		try {
			Criteria criteria = session.createCriteria(ModuleField.class);
			Criteria moduleCriteria = criteria.createCriteria("tf_Module");
			moduleCriteria.add(Restrictions.eq("tf_moduleId", moduleId));
			criteria.setProjection(Projections.max("tf_fieldId"));
			List<?> results = criteria.list();
			if (results.get(0) == null)
				return Integer.parseInt(moduleId) * 10000 + 10;
			else
				return (Integer) results.get(0) + 10;
		} finally {
			session.close();
		}
	}

	/**
	 * 在自动新增一个Form方案时，取得模块的下一个grid方案的序号
	 * 
	 * @param moduleId
	 * @return
	 */
	public Integer getNextFormSchemeOrder(String moduleId) {
		Session session = systemBaseDAO.getSf().getCurrentSession();
		Criteria criteria = session.createCriteria(ModuleFormScheme.class);
		Criteria moduleCriteria = criteria.createCriteria("tf_Module");
		moduleCriteria.add(Restrictions.eq("tf_moduleId", moduleId));
		criteria.setProjection(Projections.max("tf_schemeOrder"));
		List<?> results = criteria.list();
		if (results.get(0) == null)
			return 1;
		else
			return (Integer) results.get(0) + 1;
	}

	/**
	 * 用户选择了 grid scheme group 的字段之后，进行保存
	 * 
	 * @param gridGroupId
	 * @param parseInt
	 * @param isSelected
	 */
	public void addorDeleteGridGroupFields(int gridGroupId, int fieldId, Boolean isSelected) {
		Session session = systemBaseDAO.getSf().getCurrentSession();
		session.beginTransaction();
		try {
			Query query = session
					.createQuery(" select max(tf_gridFieldOrder) from _ModuleGridSchemeGroupField "
							+ "where tf_gridGroupId = ? ");
			query.setParameter(0, gridGroupId);
			Integer maxOrder = (Integer) query.uniqueResult();
			if (maxOrder == null)
				maxOrder = 10;
			else
				maxOrder += 10;

			query = session.createQuery(" select tf_gridFieldId from _ModuleGridSchemeGroupField "
					+ "where tf_gridGroupId = ? and tf_fieldId = ?");
			query.setParameter(0, gridGroupId);
			query.setParameter(1, fieldId);
			Integer gridFieldId = (Integer) query.uniqueResult();

			if (isSelected) {
				if (gridFieldId == null) {
					ModuleGridSchemeGroupField field = new ModuleGridSchemeGroupField();
					field.setTf_ModuleField(new ModuleField(fieldId));
					field.setTf_ModuleGridSchemeGroup(new ModuleGridSchemeGroup(gridGroupId));
					field.setTf_gridFieldOrder(maxOrder);
					session.save(field);
				}
			} else {
				if (gridFieldId != null) {
					query = session.createQuery(" delete _ModuleGridSchemeGroupField "
							+ "where tf_gridFieldId = ? ");
					query.setParameter(0, gridFieldId);
					query.executeUpdate();
				}
			}
		} finally {
			session.getTransaction().commit();
			session.close();
		}

	}

	/**
	 * 用户选择了 form scheme group 的字段之后，进行保存
	 * 
	 * @param formGroupId
	 * @param parseInt
	 * @param isSelected
	 */
	public void addorDeleteFormGroupFields(int formGroupId, int fieldId, Boolean isSelected) {
		Session session = systemBaseDAO.getSf().getCurrentSession();
		session.beginTransaction();
		try {
			Query query = session
					.createQuery(" select max(tf_formFieldOrder) from _ModuleFormSchemeGroupField "
							+ "where tf_formGroupId = ? ");
			query.setParameter(0, formGroupId);
			Integer maxOrder = (Integer) query.uniqueResult();
			if (maxOrder == null)
				maxOrder = 10;
			else
				maxOrder += 10;

			query = session.createQuery(" select tf_formFieldId from _ModuleFormSchemeGroupField "
					+ "where tf_formGroupId = ? and tf_fieldId = ?");
			query.setParameter(0, formGroupId);
			query.setParameter(1, fieldId);
			Integer gridFieldId = (Integer) query.uniqueResult();

			if (isSelected) {
				if (gridFieldId == null) {
					ModuleFormSchemeGroupField field = new ModuleFormSchemeGroupField();
					field.setTf_ModuleField(new ModuleField(fieldId));
					field.setTf_ModuleFormSchemeGroup(new ModuleFormSchemeGroup(formGroupId));
					field.setTf_formFieldOrder(maxOrder);
					session.save(field);
				}
			} else {
				if (gridFieldId != null) {
					query = session.createQuery(" delete _ModuleFormSchemeGroupField "
							+ "where tf_formFieldId = ? ");
					query.setParameter(0, gridFieldId);
					query.executeUpdate();
					// session.delete(new _ModuleFormSchemeGroupField(gridFieldId));
				}
			}
		} finally {
			session.getTransaction().commit();
			session.close();
		}

	}

}
