package com.peeply;

import com.peeply.model.UploadToken;

/**
 * Created by peeply on 14-5-19.
 */
public interface UploadEvent {

    public int EVENT_BEGIN = 0;
    public int   EVENT_END =9 ;
	void event_End(UploadToken model);
	void event_Begin(UploadToken model);
}
