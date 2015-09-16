package com.peeply.service;

import java.sql.Timestamp;
import java.util.List;

import com.peeply.UploadEvent;
import com.peeply.DAO.UploadTokenDAO;
import com.peeply.model.UploadToken;

/**
 * Created by peeply on 14-5-19.
 */
public class EventService {

	private TokenService tokenService;
	
	
	private List<UploadEvent>  eventListeners;


	private Object get;

	public void event(int eventBegin, UploadToken model) {

		switch (eventBegin) {
		case UploadEvent.EVENT_BEGIN:
			event_Begin(model);
			break;
		case UploadEvent.EVENT_END:
			event_End(model);
			break;
		}
	}
	
	
	
	
	public void event_Begin(UploadToken model) {
		UploadToken tem = tokenService.get( model.getToken());
		if( tem==null ){
			tokenService.save(model);
		}
		
		for(UploadEvent ue:  getEventListeners() ){
			ue.event_Begin(model);
		}
	}

	private void event_End(UploadToken model) { 
		UploadToken 	tem = tokenService.get( model.getToken() );	
		tem.setCompleteTime( new Timestamp(System.currentTimeMillis())  );	
		tem.setCompleted(true);
		tem.setReleaseName( model.getReleaseName() );
		for(UploadEvent ue:  getEventListeners() ){
			ue.event_End(tem);
		}
		
	}
	
	
	
	

	

 

	public List<UploadEvent> getEventListeners() {
		return eventListeners;
	}




	public void setEventListeners(List<UploadEvent> eventListeners) {
		this.eventListeners = eventListeners;
	}




	public TokenService getTokenService() {
		return tokenService;
	}




	public void setTokenService(TokenService tokenService) {
		this.tokenService = tokenService;
	}

}
