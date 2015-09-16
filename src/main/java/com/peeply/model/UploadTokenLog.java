package com.peeply.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

/**
 * Created by peeply on 14-5-19.
 */

@Entity
@javax.persistence.Table(name = "UploadTokenLog")
public class UploadTokenLog   implements Serializable{

	private int id;
	private String token;
	private String fileName;
	private String releaseName;
	private long fileSize;
	private boolean completed = false;
	private Timestamp createTime = new Timestamp(System.currentTimeMillis());
	private Timestamp completeTime = null;

	
	public UploadTokenLog() {

	}
	
	public UploadTokenLog(UploadToken ut) {
		this.token = ut.getToken();
		this.fileName = ut.getFileName();
		this.fileSize = ut.getFileSize();
		this.completed = ut.isCompleted();
		this.createTime = ut.getCreateTime();
		this.completeTime = ut.getCompleteTime();
		this.releaseName = ut.getReleaseName();
	}
 
	
	@Id
	@GenericGenerator(name = "idGenerator",strategy = "native")
	@GeneratedValue(generator = "idGenerator")
	public int getId() {
		return id;
	}

	
	

	@Column
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Column
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Column
	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	@Column
	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	@Column
	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	@Column
	public Timestamp getCompleteTime() {
		return completeTime;
	}

	public void setCompleteTime(Timestamp completeTime) {
		this.completeTime = completeTime;
	}

	public void setId(int id) {
		this.id = id;
	}
	@Column
	public String getReleaseName() {
		return releaseName;
	}

	public void setReleaseName(String releaseName) {
		this.releaseName = releaseName;
	}

 

}
