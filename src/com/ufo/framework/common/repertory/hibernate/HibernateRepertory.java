package com.ufo.framework.common.repertory.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ufo.framework.common.core.utils.EntityUtil;
import com.ufo.framework.common.core.utils.ModelUtil;
import com.ufo.framework.common.core.utils.StringUtil;
import com.ufo.framework.common.irepertory.CommonIrpertory;
import com.ufo.framework.common.log.AppLoggerFactory;
import com.ufo.framework.common.model.Model;

@SuppressWarnings("unchecked")
@Repository
public class HibernateRepertory implements CommonIrpertory {

	public HibernateRepertory() {
		debug(this.getClass().getName());
	}
	private SessionFactory sf;
	
	@Autowired
	public SessionFactory getSf() {
		return sf;
	}
	public void setSf(SessionFactory sf) {
		this.sf = sf;
	}

	@Override
	public <T extends Model> T findById(Class<T> clazz, Serializable id)
			throws Exception {
		return  (T) sf.getCurrentSession().get(clazz.getName(), id);
	}
	@Override
	public <T extends Model> List<T> findAll(Class<T> clazz) throws Exception {
		List<T> list=null;
		list=sf.getCurrentSession().createQuery("from "+clazz.getName()).list();
		list=list==null?new ArrayList<>():list;
		return list;
	}
	@Override
	public <T extends Model> T getEntityByHql(Class<T> clazz, String hql)
			throws NonUniqueResultException {
		T datas=null;
		try
		{
			datas=(T) sf.getCurrentSession().createQuery(hql).uniqueResult();
		}catch(NonUniqueResultException e)
		{
			AppLoggerFactory.getyingquLogger(clazz).error("获取数据大于一条！");
			AppLoggerFactory.getyingquLogger(clazz).error(e);
			throw e ;
		}
		return datas;
	}
	@Override
	public <T extends Model> T update(T entity) throws Exception {
		// TODO Auto-generated method stub
		String pkName=ModelUtil.getClassPkName(entity.getClass());
		String pkValue=(String) EntityUtil.getPropertyValue(entity,pkName);
		//查询当前更新的实体
		 T model=(T) sf.getCurrentSession().get(entity.getClass(), pkValue);
		// EntityUtil.copyNewField(model, entity);
		 BeanUtils.copyProperties(model,entity );
		sf.getCurrentSession().update(model);
		return model;
	}
	@Override
	public List<?> queryByHql(String hql, Integer start, Integer limit)
			throws Exception {
		// TODO Auto-generated method stub
		Query query=sf.getCurrentSession().createQuery(hql);
		if(limit>0){
			query.setFirstResult(start);
			query.setMaxResults(limit);
		}
		return query.list();
	}
	@Override
	public <T extends Model> void removeById(Serializable id, Class<T> clazz)
			throws Exception {
		Object obj=sf.getCurrentSession().load(clazz, id);
		sf.getCurrentSession().delete(obj);
		
	}
	@Override
	public List<?> queryByHql(String hql) throws Exception {
		 return sf.getCurrentSession().createQuery(hql).list();
	}
	@Override
	public <T extends Model> T save(T entity) throws Exception {
		sf.getCurrentSession().save(entity);
		return entity;
	}
	@Override
	public Integer getCount(String hql) throws Exception {
		Integer c = 0;
		Query query = sf.getCurrentSession().createQuery(hql);
		Object count = query.uniqueResult();
		if(null != count && StringUtil.isInteger(count.toString())) {
			c = Integer.parseInt(count.toString());
		}else{
			c=0;
		}
		return c;
	}
	@Override
	public <T> List<T> doWork(String sql, Work work, List<T> list)
			throws Exception {
		sf.getCurrentSession().doWork(work);
		return list;
	}
	@Override
	public <T extends Model> List<T> findByPropert(String beanClassName, String propertyName,
			Object value, String CondString) throws Exception {
		// TODO Auto-generated method stub
		String hsql=" from "+beanClassName+" where  "+propertyName+"='"+value+"'";
		if(StringUtil.isNotEmpty(CondString)){
			hsql+=" and "+CondString;
		}
		List<T> list=sf.getCurrentSession().createQuery(hsql).list();
		return list;
	}
	@Override
	public <T extends Model> List<T> findByPropert(String beanClassName,
			String propertyName, Object value) throws Exception {
		// TODO Auto-generated method stub
		String hsql=" from "+beanClassName+" where  "+propertyName+"='"+value+"'";
		List<T> list=sf.getCurrentSession().createQuery(hsql).list();
		return list;
	}

	
}
