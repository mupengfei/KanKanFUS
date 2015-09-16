package com.core.dao;

import java.util.List;

/**
 * @author peeply
 * @version 创建时间：2012-8-2 下午02:33:15 类说明
 */

public interface IBaseDAO<M extends java.io.Serializable, PK extends java.io.Serializable> {

	/**
	 * save
	 * 
	 * @param model
	 * @return
	 */
	public PK save(M model);

	/**
	 * saveOrUpdate
	 * 
	 * @param model
	 */
	public void saveOrUpdate(M model);

	/**
	 * update
	 * 
	 * @param model
	 */
	public void update(M model);

	/**
	 * merge
	 * 
	 * @param model
	 */
	public void merge(M model);

	/**
	 * delete
	 * 
	 * @param id
	 */
	public void delete(PK id);

	/**
	 * deleteObject
	 * 
	 * @param model
	 */
	public void deleteObject(M model);

	/**
	 * get
	 * 
	 * @param id
	 * @return
	 */
	public M get(PK id);

	/**
	 * getByParams
	 * 
	 * @param hql
	 * @param paramlist
	 * @return
	 */
	public List<M> getByParams(String hql, Object... paramlist);

	/**
	 * exists
	 * 
	 * @param id
	 * @return
	 */
	boolean exists(PK id);

	/**
	 * flush
	 */
	public void flush();

	/**
	 * clear
	 */
	public void clear();

	/**
	 * listAll
	 * 
	 * @return
	 */
	public List<M> listAll();

	/*
	 * public int countAll();
	 * 
	 * public List<M> listAll(int pn, int pageSize);
	 * 
	 * public List<M> pre(PK pk, int pn, int pageSize);
	 * 
	 * public List<M> next(PK pk, int pn, int pageSize);
	 */
}
