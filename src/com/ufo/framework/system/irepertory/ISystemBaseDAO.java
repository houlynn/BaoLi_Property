package com.ufo.framework.system.irepertory;

import java.util.List;

@SuppressWarnings("rawtypes")
public interface ISystemBaseDAO {

	/**
	 * 新增一条记录
	 * 
	 * @param record
	 */
	public void save(Object record);

	/**
	 * 更新一条记录
	 * 
	 * @param record
	 * @param old
	 */
	public void attachDirty(Object record, Object old);

	/**
	 * 删除一条
	 * 
	 * @param record
	 */
	public void delete(Object record);

	/**
	 * 根据bean类和id取得记录
	 * 
	 * @param className
	 * @param id
	 * @return record ,如果未找到返回null
	 */
	public Object findById(Class<?> className, Object id);

	/**
	 * 根据bean类名称和id取得记录
	 * 
	 * @param beanClassName
	 * @param id
	 * @return record ,如果未找到返回null
	 */
	public Object findById(String beanClassName, Object id);

	/**
	 * 根据bean类名称和属性及值取得记录数
	 * 
	 * @param beanClassName
	 * @param propertyName
	 * @param value
	 * @return 返回记录列表
	 */
	public List findByProperty(Class<?> className, String propertyName,
			Object value);
	
	/**
	 * 根据bean类名称和属性及值取得记录的第一条
	 * 
	 * @param beanClassName
	 * @param propertyName
	 * @param value
	 * @return 返回记录列表
	 */
	public Object findByPropertyFirst(Class<?> className, String propertyName,
			Object value);
	
	
	
	/**
	 * 根据bean类名称自字义字符串取得数据
	 * 
	 * @param beanClassName
	 * @param value
	 * @return 返回记录列表
	 */
	public List findByString(Class<?> className, String value);
	/**
	 * 根据bean类名称和属性及值取得记录数
	 * 
	 * @param beanClassName
	 * @param propertyName
	 * @param value
	 * @return 返回记录列表
	 */
	public List findByProperty(String beanClassName, String propertyName,
			Object value);

	
	
	/**
	 * 根据bean类名称和属性及值,以及附件条件 取得记录数
	 * 
	 * @param beanClassName
	 * @param propertyName
	 * @param value
	 * @return 返回记录列表
	 */
	public List findByPropertyWithOtherCondition(Class<?> className, String propertyName,
			Object value , String otherCondString);
	
	
	
	
	/**
	 * 根据bean类名称和属性及值,以及附件条件 取得记录数
	 * 
	 * @param beanClassName
	 * @param propertyName
	 * @param value
	 * @return 返回记录列表
	 */
	public List findByLikeProperty(String beanClassName, String propertyName,
			Object value);
	
	
	/**
	 * 根据bean类名称和属性及值,以及附件条件 取得记录数 ,可以加附加的sql
	 * 
	 * @param beanClassName
	 * @param propertyName
	 * @param value
	 * @return 返回记录列表
	 */
	public List findByLikePropertyWithOtherCondition(String beanClassName, String propertyName,
			Object value, String otherCondString);
	
	
	/**
	 * 根据bean类名称和属性及值,以及附件条件 取得记录数
	 * 
	 * @param beanClassName
	 * @param propertyName
	 * @param value
	 * @return 返回记录列表
	 */
	public List findByPropertyWithOtherCondition(String beanClassName, String propertyName,
			Object value , String otherCondString);
	
	
	/**
	 * 根据bean类名称和属性及值取得记录数,并按指定字段排序
	 * 
	 * @param beanClassName
	 * @param sort
	 *            排序字段
	 * @param dir
	 *            排序方向
	 * @param propertyName
	 *            筛选字段
	 * @param value
	 *            筛选值
	 * @param defaultSort
	 *            默认排序字段
	 * @param defaultDir
	 *            默认排序方向
	 * @return
	 */
	public List findByPropertyAllSort(String beanClassName, String sort,
			String dir, String propertyName, Object value, String defaultSort,
			String defaultDir);

	/**
	 * 根据bean类取得所有记录
	 * 
	 * @param className
	 * @return 所有记录列表
	 */
	public List findAll(Class<?> className);

	/**
	 * 根据bean类名称取得所有记录
	 * 
	 * @param className
	 * @return 所有记录列表
	 */
	public List findAll(String className);

	/**
	 * 根据bean类名称取得所有记录，并按指定字段排序
	 * 
	 * @param beanClassName
	 * @param sort
	 *            排序字段
	 * @param dir
	 *            排序方向
	 * @return
	 */
	public List findAllSort(String beanClassName, String sort, String dir);

	List findByPropertyAllSort(Class<?> className, String sort, String dir,
			String propertyName, Object value, String defaultSort, String defaultDir);

}
