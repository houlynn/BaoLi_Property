package com.ufo.framework.common.irepertory;

import java.io.Serializable;
import java.util.List;

import org.hibernate.jdbc.Work;

import com.ufo.framework.common.model.Model;

public interface CommonIrpertory {

	/**
	 * 根据ID加载一个实体
	 * 
	 * @param clazz
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public abstract <T extends Model> T findById(Class<T> clazz, Serializable id)
			throws Exception;

	/**
	 * 查询所有实体
	 * 
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public abstract <T extends Model> List<T> findAll(Class<T> clazz)
			throws Exception;

	/**
	 * 根据HSQL 获取一个
	 * 
	 * @param hql
	 * @return
	 * @throws Exception
	 */

	public abstract <T extends Model> T getEntityByHql(Class<T> clazz,
			String hql) throws Exception;

	/**
	 * 更新一个实体
	 * 
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	public abstract <T extends Model> T update(T entity) throws Exception;

	/***
	 * 加载一笔数据
	 * 
	 * @param hql
	 * @param start
	 * @param limit
	 * @return
	 * @throws Exception
	 */
	public abstract List<?> queryByHql(String hql, Integer start, Integer limit)
			throws Exception;

	/**
	 * 根据ID删除
	 * 
	 * @param id
	 */
	public abstract <T extends Model> void removeById(Serializable id, Class<T> clazz)
			throws Exception;

	/**
	 * 获取一笔数
	 * 
	 * @param hql
	 * @return
	 * @throws Exception
	 */
	public abstract List<?> queryByHql(String hql) throws Exception;

	/**
	 * 添加一个实体
	 */
	public <T extends Model> T save(T entity) throws Exception;

	/**
	 * 获取count
	 * 
	 * @param hql
	 * @return
	 * @throws Exception
	 */
	public Integer getCount(String hql) throws Exception;

	/***
	 * 
	 * @param sql
	 * @param work
	 * @param list
	 * @return
	 * @throws Exception
	 */
	public <T> List<T> doWork(String sql, Work work, List<T> list)
			throws Exception;
	
	/**
	 * 
	 * @param beanClassName
	 * @param propertyName
	 * @param value
	 * @param CondString
	 * @return
	 * @throws Exception
	 */
	public <T extends Model> List<T>  findByPropert(String beanClassName,
			String propertyName, Object value, String CondString) 	throws Exception;
	
	
	/**
	 * @param beanClassName
	 * @param propertyName
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public <T extends Model> List<T>  findByPropert(String beanClassName,
			String propertyName, Object value) 	throws Exception;  

}
