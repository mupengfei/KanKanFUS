package com.peeply.service;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import cn.twinkling.stream.util.IoUtil;

import com.peeply.Range;
import com.peeply.UploadEvent;
import com.peeply.model.UploadToken;

/**
 * IO--closing, getting file name ... main function method
 */
public class IoService {


	private ConfigService configService;
	private EventService eventService;






    static final Pattern RANGE_PATTERN = Pattern.compile("bytes \\d+-\\d+/\\d+");





	
	/**
	 * According the key, generate a file (if not exist, then create
	 * a new file).
	 * @param filename
	 * @return
	 * @throws java.io.IOException
	 */
	public   File getFile(String filename) throws IOException {
		if (filename == null || filename.isEmpty())
			return null;
		String name = filename.replaceAll("/", Matcher.quoteReplacement(File.separator));
		File f = new File(configService.getFileRepository() + File.separator + name);
		if (!f.getParentFile().exists())
			f.getParentFile().mkdirs();
		if (!f.exists())
			f.createNewFile();
		
		return f;
	}

	/**
	 * Acquired the file.
	 * @param key
	 * @return
	 * @throws java.io.FileNotFoundException If key not found, will throws this.
	 */
	public   File getTokenedFile(String key) throws FileNotFoundException {
		if (key == null || key.isEmpty())
			return null;

		File f = new File(configService.getFileRepository() + File.separator + key);
		if (!f.getParentFile().exists())
			f.getParentFile().mkdirs();
		if (!f.exists())
			throw new FileNotFoundException("File `" +f + "` not exist.");
		
		return f;
	}
	
	public   void storeToken(String key) throws IOException {
		if (key == null || key.isEmpty())
			return;

		File f = new File(configService.getFileRepository() + File.separator + key);
		if (!f.getParentFile().exists())
			f.getParentFile().mkdirs();
		if (!f.exists())
			f.createNewFile();
	}
	
	/**
	 * close the IO stream.
	 * @param stream
	 */
	public   void close(Closeable stream) {
		try {
			if (stream != null)
				stream.close();
		} catch (IOException e) {
		}
	}
	
	/**
	 * 获取Range参数
	 * @param req
	 * @return
	 * @throws java.io.IOException
	 */
	public   Range parseRange(HttpServletRequest req) throws IOException {
		String range = req.getHeader( "content-range");
		Matcher m = RANGE_PATTERN.matcher(range);
		if (m.find()) {
			range = m.group().replace("bytes ", "");
			String[] rangeSize = range.split("/");
			String[] fromTo = rangeSize[0].split("-");

			long from = Long.parseLong(fromTo[0]);
			long to = Long.parseLong(fromTo[1]);
			long size = Long.parseLong(rangeSize[1]);

			return new Range(from, to, size);
		}
		throw new IOException("Illegal Access!");
	}

	/**
	 * From the InputStream, write its data to the given file.
	 */
	public   long streaming(InputStream in, String key, String fileName,UploadToken utoken ) throws IOException {
	
		
		
		eventService.event(UploadEvent.EVENT_BEGIN, utoken);
 
		
		
		OutputStream out = null;
	 
		File f = getTokenedFile(key);
		try {
			out = new FileOutputStream(f);

			int read = 0;
			final byte[] bytes = new byte[  10240  ];
			while ((read = in.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
		} finally {
			close(out);
		}
		/** rename the file * fix the `renameTo` bug */
  
		
		 
		
        
        
        
		
		
		long length = f.length();
		String prefix=fileName.substring(fileName.lastIndexOf("."));
		String releaseName = UUID.randomUUID().toString()+prefix;
		utoken.setReleaseName(releaseName  );
		utoken.setFileSize(length);
		File dst = IoUtil.getFile(releaseName,false);
		f.renameTo(dst);
        eventService.event(UploadEvent.EVENT_END   , utoken );
		
		
		/** if `STREAM_DELETE_FINISH`, then delete it. */
		if (configService.isDeleteFinished()) {
			dst.delete();
		}
		
		return length;
	}



    public ConfigService getConfigService() {
        return configService;
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

 

	public EventService getEventService() {
		return eventService;
	}

	public void setEventService(EventService eventService) {
		this.eventService = eventService;
	}
}
