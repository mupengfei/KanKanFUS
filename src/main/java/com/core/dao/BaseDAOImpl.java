package com.core.dao;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.List;
import javax.persistence.Id;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author peeply
 * @version 创建时间：2012-8-2 下午02:33:15
 * 
 *          类说明
 */

public abstract class BaseDAOImpl<M extends java.io.Serializable, PK extends java.io.Serializable> implements IBaseDAO<M, PK> {

	protected static final Logger LOGGER = LoggerFactory.getLogger(BaseDAOImpl.class);

	private final Class<M> entityClass;
	private final String HQL_LIST_ALL;
	private final String HQL_COUNT_ALL;
	private final String HQL_OPTIMIZE_PRE_LIST_ALL;
	private final String HQL_OPTIMIZE_NEXT_LIST_ALL;
	private String pkName = null;

	@SuppressWarnings("unchecked")
	public BaseDAOImpl() {
		this.entityClass = (Class<M>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		Field[] fields = this.entityClass.getDeclaredFields();
		for (Field f : fields) {
			if (f.isAnnotationPresent(Id.class)) {
				this.pkName = f.getName();
			}
		}

		// TODO @Entity name not null
		HQL_LIST_ALL = "from " + this.entityClass.getSimpleName() + " order by " + pkName + " desc";
		HQL_OPTIMIZE_PRE_LIST_ALL = "from " + this.entityClass.getSimpleName() + " where " + pkName + " > ? order by " + pkName + " asc";
		HQL_OPTIMIZE_NEXT_LIST_ALL = "from " + this.entityClass.getSimpleName() + " where " + pkName + " < ? order by " + pkName + " desc";
		HQL_COUNT_ALL = " select count(*) from " + this.entityClass.getSimpleName();
		// System.out.println("HQL:   " + HQL_LIST_ALL);
	}

	private SessionFactory sessionFactory;

	public Session getSession() {
		// 事务必须是开启的(Required)，否则获取不到
		return sessionFactory.getCurrentSession();
	}

	@SuppressWarnings("unchecked")
	@Override
	public PK save(M model) {
		return (PK) getSession().save(model);
	}

	@Override
	public void saveOrUpdate(M model) {
		getSession().saveOrUpdate(model);
	}

	@Override
	public void update(M model) {
		getSession().update(model);

	}

	@Override
	public void merge(M model) {
		getSession().merge(model);
	}

	@Override
	public void delete(PK id) {
		getSession().delete(this.get(id));
	}

	@Override
	public void deleteObject(M model) {
		getSession().delete(model);
	}

	@Override
	public boolean exists(PK id) {
		return get(id) != null;
	}

	@Override
	public M get(PK id) {
		return (M) getSession().get(this.entityClass, id);
	}

	@Override
	public void flush() {
		getSession().flush();
	}

	@Override
	public void clear() {
		getSession().clear();
	}

	@Override
	public List<M> getByParams(final String hql, final Object... paramlist) {
		Query query = getSession().createQuery(hql);
		setParameters(query, paramlist);
		List<M> results = query.list();
		return results;
	}

	protected void setParameters(Query query, Object... paramlist) {
		if (paramlist != null) {
			for (int i = 0; i < paramlist.length; i++) {
				if (paramlist[i] instanceof Date) {
					query.setTimestamp(i, (Date) paramlist[i]);
				} else {
					query.setParameter(i, paramlist[i]);
				}
			}
		}
	}

	@Override
	public List<M> listAll() {
		Query query = getSession().createQuery(HQL_LIST_ALL);
		query.setCacheable(true);

		List<M> results = query.list();
		return results;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

}
