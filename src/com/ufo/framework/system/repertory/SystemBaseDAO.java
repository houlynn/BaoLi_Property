package com.ufo.framework.system.repertory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.util.JSONUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;


@Repository
@SuppressWarnings("rawtypes")
public class SystemBaseDAO  {

	@Resource
	private SessionFactory sf;

	

}
