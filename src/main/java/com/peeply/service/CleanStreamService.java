package com.peeply.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.peeply.DAO.UploadTokenDAO;
import com.peeply.model.UploadToken;

/**
 * Created by peeply on 14-5-18.
 */
public class CleanStreamService {

	private static Logger logger = Logger.getLogger(CleanStreamService.class);

	private int cleanDay = 1;
	
	
	
	private TokenService tokenService;
	private IoService ioService;

	public void event()   {

		List<UploadToken> list = tokenService
				.getByParams(
						"from UploadToken as ut where  ut.completed=0 and ut.createTime<?  ",
						getNextDay(new Date(), getCleanDay()));
		for (UploadToken ut : list) {
			File file;
			try {
				file = ioService.getTokenedFile( ut.getToken() );
				file.delete();
			} catch (FileNotFoundException e) {
			}finally{
				tokenService.remove2Log( ut );
			}
			
			
			logger.info("clean "+ut.getToken());
		}

	}

	public static Date getNextDay(Date nowdate, int delay) {

		long myTime = (nowdate.getTime() / 1000) + delay * 24 * 60 * 60;
		return new Date(myTime * 1000);

	}

 
	public int getCleanDay() {
		return cleanDay;
	}

	public void setCleanDay(int cleanDay) {
		this.cleanDay = cleanDay * -1;
	}

	public IoService getIoService() {
		return ioService;
	}

	public void setIoService(IoService ioService) {
		this.ioService = ioService;
	}

	public TokenService getTokenService() {
		return tokenService;
	}

	public void setTokenService(TokenService tokenService) {
		this.tokenService = tokenService;
	}
}
