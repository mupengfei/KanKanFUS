package com.peeply;

import com.peeply.DAO.UploadTokenDAO;
import com.peeply.model.UploadToken;
import com.peeply.service.TokenService;

public class DefaultUploadEventImpl implements UploadEvent{

	private TokenService tokenService;
	
	
	@Override
	public void event_End(UploadToken model) {
		tokenService.remove2Log(model);
		// TODO Auto-generated method stub
		// 转移数据库信息
	}
	
	
	
	
	

	@Override
	public void event_Begin(UploadToken model) {
		// TODO Auto-generated method stub
		
	}






	public TokenService getTokenService() {
		return tokenService;
	}






	public void setTokenService(TokenService tokenService) {
		this.tokenService = tokenService;
	}
	
	
	
	
	
	
	

 
	
	

}
