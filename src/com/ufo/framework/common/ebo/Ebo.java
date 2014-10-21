package com.ufo.framework.common.ebo;

import java.io.Serializable;
import java.util.List;

import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ufo.framework.common.core.utils.StringUtil;
import com.ufo.framework.common.ebi.Ebi;
import com.ufo.framework.common.irepertory.CommonIrpertory;
import com.ufo.framework.common.model.Model;

@Service
public class Ebo implements Ebi {

	public Ebo() {
		debug(this.getClass().getName());
	}
	@Autowired
	private CommonIrpertory  rpertory;
	public CommonIrpertory getRpertory() {
		return rpertory;
	}
	public void setRpertory(CommonIrpertory rpertory) {
		this.rpertory = rpertory;
	}
    @Transactional(readOnly=true)  
	@Override
	public <T extends Model> T findById(Class<T> clazz, Serializable id)
			throws Exception {
		// TODO Auto-generated method stub
		return rpertory.findById(clazz, id);
	}
    @Transactional(readOnly=true)  
	@Override
	public <T extends Model> List<T> findAll(Class<T> clazz) throws Exception {
		// TODO Auto-generated method stub
		return rpertory.findAll(clazz);
	}
    @Transactional(readOnly=true)  
	@Override
	public <T extends Model> T getEntityByHql(Class<T> clazz, String hql)
			throws Exception {
		// TODO Auto-generated method stub
		return rpertory.getEntityByHql(clazz, hql);
	}
    @Transactional(rollbackFor={Exception.class})  
	@Override
	public <T extends Model> T update(T entity) throws Exception {
		// TODO Auto-generated method stub
		return rpertory.update(entity);
	}
    @Transactional(readOnly=true)  
	@Override
	public List<?> queryByHql(String hql, Integer start, Integer limit)
			throws Exception {
		// TODO Auto-generated method stub
		return rpertory.queryByHql(hql, start, limit);
	}
    @Transactional(rollbackFor={Exception.class})  
	@Override
	public <T extends Model> void removeById(Serializable id, Class<T> clazz)
			throws Exception {
		// TODO Auto-generated method stub
		rpertory.removeById(id, clazz);

	}
    @Transactional(readOnly=true)  
	@Override
	public List<?> queryByHql(String hql) throws Exception {
		// TODO Auto-generated method stub
		return rpertory.queryByHql(hql);
	}
    @Transactional(rollbackFor={Exception.class})  
	@Override
	public <T extends Model> T save(T entity) throws Exception {
		// TODO Auto-generated method stub
		return rpertory.save(entity);
	}
    @Transactional(readOnly=true)  
	@Override
	public Integer getCount(String hql) throws Exception {
		// TODO Auto-generated method stub
		return rpertory.getCount(hql);
	}
    @Transactional(readOnly=true)  
	@Override
	public <T> List<T> doWork(String sql, Work work, List<T> list)
			throws Exception {
		// TODO Auto-generated method stub
		return rpertory.doWork(sql, work, list);
	}
    @Transactional(readOnly=true)  
	@Override
	public <T extends Model> List<T> findByPropert(String beanClassName,
			String propertyName, Object value, String CondString)
			throws Exception {
		// TODO Auto-generated method stub
		return rpertory.findByPropert(beanClassName, propertyName, value, CondString);
	}
    @Transactional(readOnly=true)  
	@Override
	public <T extends Model> T findByPropertFirst(String beanClassName,
			String propertyName, Object value, String CondString)
			throws Exception {
		// TODO Auto-generated method stub
		List<T> list=rpertory.findByPropert(beanClassName, propertyName, value, CondString);
		T mode=null;
		if(list!=null&&list.size()>0){
			mode=list.get(0);
		}
		return mode;
	}
    @Transactional(readOnly=true)  
	@Override
	public boolean checkUnique(String beanClassName, String propertyName,
			Object value, String CondString) {
		// TODO Auto-generated method stub
		boolean flag=false;
		String hsql="select count(*) from "+beanClassName+"  where "+beanClassName+"='"+value+"' ";
		if(StringUtil.isNotEmpty(CondString)){
			hsql+=CondString;
		}
		try {
			Integer count= rpertory.getCount(hsql);
			if(count==0){
				flag=true;	
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
    @Transactional(readOnly=true)  
	@Override
	public <T extends Model> List<T> findByPropert(String beanClassName,
			String propertyName, Object value) throws Exception {
		// TODO Auto-generated method stub
		return rpertory.findByPropert(beanClassName, propertyName, value);
	}

}
