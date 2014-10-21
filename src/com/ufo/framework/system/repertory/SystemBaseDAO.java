package com.ufo.framework.system.repertory;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.util.JSONUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import com.ufo.framework.system.irepertory.ISystemBaseDAO;

@Repository
@SuppressWarnings("rawtypes")
public class SystemBaseDAO extends HibernateDaoSupport implements ISystemBaseDAO {

	@Resource
	private SessionFactory sf;

	public static SystemBaseDAO systemBaseDAO;

	@PostConstruct
	public void InjectedSessionFactory() {
		//System.out.println("system base dao impl injected sessionFactory");
		super.setSessionFactory(sf);
		systemBaseDAO = this;
	}

	public SystemBaseDAO() {
		super();
		//System.out.println("system base dao impl created");
		String[] dateFormats = new String[] { "yyyy-MM-dd" };
		JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpher(dateFormats));
		//System.out.println("json tobean dataformats created");
	}

	private static final Log log = LogFactory.getLog(SystemBaseDAO.class);

	@Override
	public void save(Object record) {
		getHibernateTemplate().save(record);
		log.debug("new record saved:" + record.getClass().getSimpleName() + ":"
				+ record.toString());
	}

	@Override
	public void attachDirty(Object record, Object old) {
		getHibernateTemplate().saveOrUpdate(record);
		log.debug("save record:" + record.getClass().getSimpleName() + ":"
				+ record.toString());
	}

	@Override
	public void delete(Object record) {
		getHibernateTemplate().delete(record);
		log.debug("delete record:" + record.getClass().getSimpleName() + ":"
				+ record.toString());
	}

	@Override
	public Object findById(Class<?> className, Object id) {
		return findById(className.getName(), id);
	}

	@Override
	public Object findById(String beanClassName, Object id) {
		Object record;
		try {
			record = getHibernateTemplate().get(beanClassName, Integer.parseInt(id.toString()));
		} catch (Exception e) {
			record = getHibernateTemplate().get(beanClassName, (Serializable) id);
		}
		// log.debug("get record " + beanClassName + " key:" + id + ":" + record);
		return record;
	}

	@Override
	public List<?> findAll(Class<?> className) {
		return findAll(className.getName());
	}

	@Override
	public List<?> findAll(String className) {
		log.debug("find all:" + className);
		String queryString = "from " + className;
		return getHibernateTemplate().find(queryString);
	}

	public void setMySessionFactory(SessionFactory mySessionFactory) {
		this.sf = mySessionFactory;
	}

	@Override
	public List<?> findAllSort(String beanClassName, String sort, String dir) {
		log.debug("find all:" + beanClassName + "---sort:" + sort + "--" + dir);
		String queryString;
		if (sort == null || sort.length() == 0)
			queryString = "from " + beanClassName + " as model ";
		else
			queryString = "from " + beanClassName + " as model " + " order by " + sort + " "
					+ dir;
		return getHibernateTemplate().find(queryString);
	}

	@Override
	public List<?> findByPropertyAllSort(Class<?> className, String sort, String dir,
			String propertyName, Object value, String defaultSort, String defaultDir) {
		return findByPropertyAllSort(className.getName(), sort, dir, propertyName, value,
				defaultSort, defaultDir);
	}

	@Override
	public List<?> findByPropertyAllSort(String beanClassName, String sort, String dir,
			String propertyName, Object value, String defaultSort, String defaultDir) {

		log.debug("find all:" + beanClassName + "---sort:" + sort + "--" + dir);

		if (propertyName.indexOf(".") > 0)
			return findByPropertyCriteria(beanClassName, sort, dir, propertyName, value,
					defaultSort, defaultDir);

		String queryString;
		String otherFilter = "";
		if (sort == null || sort.length() == 0) {
			if (defaultSort != null) {
				sort = defaultSort;
				dir = defaultDir;
			}
		}
		if (sort == null || sort.length() == 0)
			queryString = "from " + beanClassName + " as model where model." + propertyName
					+ "= ? " + otherFilter;
		else
			queryString = "from " + beanClassName + " as model where model." + propertyName
					+ "= ? " + otherFilter + " order by " + sort + " " + dir;
	
		//System.out.println(queryString);
		
		return getHibernateTemplate().find(queryString, value);
	}

	public List<?> findByPropertyCriteria(String beanClassName, String sort, String dir,
			String propertyName, Object value, String defaultSort, String defaultDir) {
		Session session = getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(beanClassName);
		String[] props = propertyName.split("\\.");
		Criteria subCriteria = criteria.createCriteria(props[0]);
		subCriteria.add(Restrictions.eq(props[1], value));
		if (sort != null) {
			if (dir == null || !dir.toLowerCase().equals("desc"))
				criteria.addOrder(Order.asc(sort));
			else
				criteria.addOrder(Order.desc(sort));
		} else if (defaultSort != null) {
			if (defaultDir == null || !defaultDir.toLowerCase().equals("desc"))
				criteria.addOrder(Order.asc(defaultSort));
			else
				criteria.addOrder(Order.desc(defaultSort));
		}
		List<?> result = criteria.list();
		session.close();
		return result;
	}

	@Override
	public Object findByPropertyFirst(Class<?> className, String propertyName, Object value) {
		List<?> result = findByProperty(className, propertyName, value);
		if (result.size() == 0)
			return null;
		else
			return result.get(0);
	}

	// @Override
	public Object findByPropertyFirstWithOtherCondition(Class<?> className,
			String propertyName, Object value, String otherCondString) {
		List<?> result = findByPropertyWithOtherCondition(className, propertyName, value,
				otherCondString);
		if (result.size() == 0)
			return null;
		else
			return result.get(0);
	}

	@Override
	public List<?> findByProperty(Class<?> className, String propertyName, Object value) {
		return findByPropertyWithOtherCondition(className.getSimpleName(), propertyName,
				value, null);
	}

	@Override
	public List<?> findByProperty(String beanClassName, String propertyName, Object value) {
		return findByPropertyWithOtherCondition(beanClassName, propertyName, value, null);

	}

	@Override
	public List<?> findByPropertyWithOtherCondition(Class<?> className,
			String propertyName, Object value, String otherCondString) {
		return findByPropertyWithOtherCondition(className.getSimpleName(), propertyName,
				value, otherCondString);

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<?> findByPropertyWithOtherCondition(String beanClassName,
			String propertyName, Object value, String otherCondString) {
		String queryString = "from " + beanClassName + " as model where model."
				+ propertyName + "= ?";
		if (otherCondString != null && otherCondString.length() > 1) {
			queryString = queryString + " and (" + otherCondString + ")";
		}
		List<Object> result = (List<Object>) getHibernateTemplate().find(queryString, value);

		log.debug(String.format("finding %s with property:%s value: %s : record number:%d",
				beanClassName, propertyName, value, result.size()));
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<?> findByString(Class<?> className, String value) {
		String queryString = "from " + className.getSimpleName() + " as model where " + value;
		List<Object> result = (List<Object>) getHibernateTemplate().find(queryString);
		log.debug(String.format("finding %s with string:%s : record number:%d",
				className.getSimpleName(), value, result.size()));
		return result;

	}

	@Override
	public List findByLikeProperty(String beanClassName, String propertyName, Object value) {

		return findByLikePropertyWithOtherCondition(beanClassName, propertyName, value, "");

	}

	@Override
	public List findByLikePropertyWithOtherCondition(String beanClassName,
			String propertyName, Object value, String otherCondString) {
		String queryString = "from " + beanClassName + " as model where model."
				+ propertyName + " like ? ";

		if (otherCondString != null && otherCondString.length() > 1) {
			queryString = queryString + " and (" + otherCondString + ")";
		}

		List<?> result = getHibernateTemplate().find(queryString, value);

		log.debug(String.format(
				"finding %s with like property:%s value: %s : record number:%d", beanClassName,
				propertyName, value, result.size()));
		return result;
	}

}
