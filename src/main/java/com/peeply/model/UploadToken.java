package com.peeply.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by peeply on 14-5-19.
 */

@Entity
@javax.persistence.Table(name = "UploadToken")
public class UploadToken implements Serializable {

	private String token;
	private String fileName;
	private String releaseName;
	private long fileSize;
	private boolean completed = false;
	private Timestamp createTime = new Timestamp(System.currentTimeMillis());
	private Timestamp completeTime = null;

	
	public UploadToken() {

	}
	
	
	public UploadToken(String token, String name, long size) {
		this.token = token;
		this.fileName = name;
		this.fileSize = size;
	}

	public UploadToken(String token) {
		this.token = token;
	}

	@Id
	@GenericGenerator(name = "idGenerator", strategy = "assigned")
	@GeneratedValue(generator = "idGenerator")
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

	@Column
	public String getReleaseName() {
		return releaseName;
	}


	public void setReleaseName(String releaseName) {
		this.releaseName = releaseName;
	}


 


	public void put2Json(JSONObject json) {
		try {
			json.put("token", token);
			json.put("releaseName", releaseName);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
