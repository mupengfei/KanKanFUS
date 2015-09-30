package com.peeply.controller;

import cn.twinkling.stream.config.Configurations;
import cn.twinkling.stream.util.IoUtil;

import com.peeply.Range;
import com.peeply.StreamException;
import com.peeply.UploadEvent;
import com.peeply.model.UploadToken;
import com.peeply.service.EventService;
import com.peeply.service.TokenService;
import com.peeply.service.ConfigService;
import com.peeply.service.IoService;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by peeply on 14-5-18.
 */
@RestController
public class UploadController {

	private static Logger logger = Logger.getLogger(UploadController.class);

	@Resource
	private ConfigService configService;
	@Resource
	private TokenService tokenService;
	@Resource
	private IoService ioService;
	@Resource
	private EventService eventService;

	static final String TOKEN_FIELD = "token";
	static final String SERVER_FIELD = "server";
	static final String SUCCESS = "success";
	static final String MESSAGE = "message";
	static final int BUFFER_LENGTH = 10240;
	static final String START_FIELD = "start";
	static final String CONTENT_RANGE_HEADER = "content-range";

	@RequestMapping("/tk")
	public Map<String, Object> token(String name, String size)
			throws ServletException, IOException {
		return getToken(name, size);
	}

	/*
	 * @RequestMapping("/tkp") public String tokenp(String name, String
	 * size,String jsonpCallback ) throws ServletException, IOException {
	 * 
	 * 
	 * Map<String, Object> json = getToken(name,size) ; JSONObject js = new
	 * JSONObject(json); jsonpCallback= jsonpCallback+"("+js.toString()+")";
	 * logger.info( jsonpCallback);
	 * 
	 * return jsonpCallback; }
	 */

	@RequestMapping("/tkp")
	public String tokenp(String name, String size, String jsonpCallback,
			String kankan_video_usertoken) throws ServletException, IOException {

		Map<String, Object> json = kankan_video_usertoken == null ? getToken(
				name, size) : getToken(name, size, kankan_video_usertoken);
		JSONObject js = new JSONObject(json);
		jsonpCallback = jsonpCallback + "(" + js.toString() + ")";
		logger.info(jsonpCallback);

		return jsonpCallback;
	}

	@RequestMapping("/tkInfo")
	public String fileInfo(String token) throws ServletException, IOException {

		return null;
	}

	private Map<String, Object> getToken(String name, String size)
			throws IOException {

		return getToken(name, size, "NoOne");
	}

	private Map<String, Object> getToken(String name, String size, String vCode)
			throws IOException {
		/**
		 * 获取 Token
		 *
		 */
		String token = tokenService.generateToken(name, size, vCode);
		Map<String, Object> json = new HashMap<>();
		json.put(TOKEN_FIELD, token);
		if (configService.isCrossed()) {
			json.put(SERVER_FIELD, configService.getCrossServer());
		}
		json.put(SUCCESS, true);
		json.put(MESSAGE, "");

		UploadToken utoken = new UploadToken(token, name, Long.parseLong(size));
		eventService.event(UploadEvent.EVENT_BEGIN, utoken);

		return json;
	}

	@RequestMapping(value = "/fd", method = RequestMethod.POST)
	public Map<String, Object> formData(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException {

		/** flash @ windows bug */
		req.setCharacterEncoding("utf8");

		final PrintWriter writer = resp.getWriter();
		// Check that we have a file upload request
		boolean isMultipart = ServletFileUpload.isMultipartContent(req);
		if (!isMultipart) {
			writer.println("ERROR: It's not Multipart form.");
			return null;
		}

		Map<String, Object> json = new HashMap<>();

		long start = 0;
		boolean success = true;
		String message = "";

		ServletFileUpload upload = new ServletFileUpload();
		InputStream in = null;
		String token = null;
		UploadToken utoken = null;
		try {
			FileItemIterator iter = upload.getItemIterator(req);
			while (iter.hasNext()) {
				FileItemStream item = iter.next();
				String name = item.getFieldName();
				in = item.openStream();
				if (item.isFormField()) {
					String value = Streams.asString(in);
					if (name.equals(TOKEN_FIELD)) {
						token = value;
						/** TODO: validate your token. */
					}
					logger.info(name + ":" + value);
				} else {
					String fileName = item.getName();
					start = ioService.streaming(in, token, fileName, utoken);
				}
			}
		} catch (FileUploadException fne) {
			success = false;
			message = "Error: " + fne.getLocalizedMessage();
		} finally {

			if (success)
				json.put(START_FIELD, start);
			json.put(SUCCESS, success);
			json.put(MESSAGE, message);

			return json;
		}
	}

	@RequestMapping(value = "/upload", method = RequestMethod.GET)
	public void upload_GET(String token, String size, String name,
			HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		logger.info("upload    GET 1");
		setOptions(req, resp);
		logger.info("upload    GET 2");

		final PrintWriter writer = resp.getWriter();

		final String fileName = name;

		/** TODO: validate your token. */

		JSONObject json = new JSONObject();

		long start = 0;
		boolean success = true;
		String message = "";
		try {
			File f = ioService.getTokenedFile(token);
			start = f.length();
			/** file size is 0 bytes. */
			if (token.endsWith("_0") && "0".equals(size) && 0 == start) {
				f.renameTo(ioService.getFile(fileName));
			}
		} catch (FileNotFoundException fne) {
			message = "Error: " + fne.getMessage();
			success = false;
		} finally {

			try {
				if (success)
					json.put(START_FIELD, start);
				json.put(SUCCESS, success);
				json.put(MESSAGE, message);
			} catch (JSONException ex) {
			}

			writer.write(json.toString());
			ioService.close(writer);
		}
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public void upload_POST(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		System.out.println("进来了");
		setOptions(req, resp); 
		final String token = req.getParameter(TOKEN_FIELD);
		final String fileName = req.getParameter("name");
		Range range = ioService.parseRange(req);

		OutputStream out = null;
		InputStream content = null;
		final PrintWriter writer = resp.getWriter();

		/** TODO: validate your token. */

		JSONObject json = new JSONObject();
		long start = 0;
		boolean success = true;
		String message = "";
		File f = ioService.getTokenedFile(token);
		try {
			if (f.length() != range.getFrom()) {
				/** drop this uploaded data */
				throw new StreamException(
						StreamException.ERROR_FILE_RANGE_START);
			}

			out = new FileOutputStream(f, true);
			content = req.getInputStream();
			int read;
			final byte[] bytes = new byte[BUFFER_LENGTH];
			while ((read = content.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}

			start = f.length();

		} catch (StreamException se) {
			success = StreamException.ERROR_FILE_RANGE_START == se.getCode();
			message = "Code: " + se.getCode();
		} catch (FileNotFoundException fne) {
			message = "Code: " + StreamException.ERROR_FILE_NOT_EXIST;
			success = false;
		} catch (IOException io) {
			message = "IO Error: " + io.getMessage();
			success = false;
		} finally {
			ioService.close(out);
			ioService.close(content);

			/** rename the file */
			if (range.getSize() == start) {
 
				/** fix the `renameTo` bug */
//				File dst = ioService.getFile(fileName);
//				dst.delete();
//				f.renameTo(dst);
//
//				UploadToken utoken = new UploadToken(token);
//				eventService.event(UploadEvent.EVENT_END, utoken);
//				logger.info("TK: `" + token + "`, NE: `" + fileName + "`");
//				/** if `STREAM_DELETE_FINISH`, then delete it. */
//				if (configService.isDeleteFinished()) {
//					dst.delete();
//				}
				File dst = IoUtil.getFile(fileName);
				dst.delete(); 
				UploadToken utoken = new UploadToken(token);
				String prefix=fileName.substring(fileName.lastIndexOf("."));
				String releaseName = UUID.randomUUID().toString()+prefix;
				utoken.setReleaseName(releaseName  );
				dst = IoUtil.getFile(releaseName,false);
				f.renameTo(dst);
                eventService.event(UploadEvent.EVENT_END   , utoken );
                utoken.put2Json(json);
             
				/** if `STREAM_DELETE_FINISH`, then delete it. */
				if (Configurations.isDeleteFinished()) {
					dst.delete();
				}
 
			}
			try {
				if (success)
					json.put(START_FIELD, start);
				json.put(SUCCESS, success);
				json.put(MESSAGE, message);
			} catch (JSONException ignored) {
			}

			writer.write(json.toString());
			ioService.close(writer);
		}
	}

	@RequestMapping(value = "/upload", method = RequestMethod.OPTIONS)
	public void doOptions(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		logger.info("upload    OPTIONS ");
		setOptions(req, resp);
	}

	private void setOptions(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		logger.info("upload    setOptions ");
		resp.setContentType("application/json");
		resp.setHeader("Access-Control-Allow-Headers",
				"Content-Range,Content-Type");
		resp.setHeader("Access-Control-Allow-Origin",
				configService.getCrossOrigins());
		resp.setHeader("Access-Control-Max-Age", "30");
		resp.setHeader("Access-Control-Allow-Methods",
				"	GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS, PATCH");

	}

	@RequestMapping("/getToken")
	public String tokenp(String name, String size) throws ServletException,
			IOException {

		Map<String, Object> json = getToken(name, size);
		JSONObject js = new JSONObject(json);
		return js.toString();
	}
}
