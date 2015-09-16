package com.peeply.service;

import com.peeply.DAO.UploadTokenDAO;
import com.peeply.DAO.UploadTokenLogDAO;
import com.peeply.model.UploadToken;
import com.peeply.model.UploadTokenLog;
import com.peeply.service.IoService;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Key Util: 1> according file name|size ..., generate a key; 2> the key should
 * be unique.
 */
public class TokenService {

	private IoService ioService;

	private UploadTokenDAO uploadTokenDAO;
	private UploadTokenLogDAO uploadTokenLogDAO;

	public IoService getIoService() {
		return ioService;
	}

	public void setIoService(IoService ioService) {
		this.ioService = ioService;
	}

	/**
	 * 生成Token， A(hashcode>0)|B + |name的Hash值| +_+size的值
	 * 
	 * @param name
	 * @param size
	 * @param vCode 校对码 可以用cookie
	 * @return
	 * @throws Exception
	 */
	public String generateToken(String name, String size,String vCode) throws IOException {
		if (name == null || size == null)
			return "";
		int code = name.hashCode();
		try {
			String token = (code > 0 ? "A" : "B") + Math.abs(code) + "_"
					+ size.trim()+"_"+vCode;
			/** TODO: store your token, here just create a file */
			ioService.storeToken(token);

			return token;
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	public void remove2Log(UploadToken model) {
		uploadTokenLogDAO.save(new UploadTokenLog(model));
		uploadTokenDAO.deleteObject(model);
	}

	public UploadTokenDAO getUploadTokenDAO() {
		return uploadTokenDAO;
	}

	public void setUploadTokenDAO(UploadTokenDAO uploadTokenDAO) {
		this.uploadTokenDAO = uploadTokenDAO;
	}

	public UploadTokenLogDAO getUploadTokenLogDAO() {
		return uploadTokenLogDAO;
	}

	public void setUploadTokenLogDAO(UploadTokenLogDAO uploadTokenLogDAO) {
		this.uploadTokenLogDAO = uploadTokenLogDAO;
	}

	public UploadToken get(String token) {

		return uploadTokenDAO.get(token);
	}

	public void save(UploadToken model) {
		uploadTokenDAO.save(model);

	}

	public List<UploadToken> getByParams(String hql, final Object... paramlist) {
		return uploadTokenDAO.getByParams(hql, paramlist);
	}

	public void deleteObject(UploadToken ut) {
		uploadTokenDAO.deleteObject( ut );
	}

}
