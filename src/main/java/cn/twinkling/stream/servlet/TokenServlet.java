package cn.twinkling.stream.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.core.SpringContextUtil;
import com.peeply.UploadEvent;
import com.peeply.model.UploadToken;
import com.peeply.service.EventService;
import com.peeply.service.TokenService;

import cn.twinkling.stream.config.Configurations;
import cn.twinkling.stream.util.TokenUtil;

/**
 * According the file name and its size, generate a unique token. And this token
 * will be refer to user's file.
 */
public class TokenServlet extends HttpServlet {
	private static final long serialVersionUID = 2650340991003623753L;
	static final String FILE_NAME_FIELD = "name";
	static final String FILE_SIZE_FIELD = "size";
	static final String TOKEN_FIELD = "token";
	static final String SERVER_FIELD = "server";
	static final String SUCCESS = "success";
	static final String MESSAGE = "message";
 
 
	private EventService eventService;

	@Override
	public void init() throws ServletException {
		eventService =(EventService)SpringContextUtil.getBean("eventService");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		doOptions(req, resp);
		
		
		
		String name = req.getParameter(FILE_NAME_FIELD);
		String size = req.getParameter(FILE_SIZE_FIELD);
		String token = TokenUtil.generateToken(name, size);

		PrintWriter writer = resp.getWriter();

		JSONObject json = new JSONObject();
		try {
			json.put(TOKEN_FIELD, token);
			if (Configurations.isCrossed())
				json.put(SERVER_FIELD, Configurations.getCrossServer());
			json.put(SUCCESS, true);
			json.put(MESSAGE, "");
		} catch (JSONException e) {
		}
		/** TODO: save the token. */

		UploadToken utoken = new UploadToken(token, name, Long.parseLong(size));
		eventService.event(UploadEvent.EVENT_BEGIN, utoken);
		writer.write(json.toString());
	}

	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doHead(req, resp);
	}
	
	
	
	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("application/json");
		resp.setHeader("Access-Control-Allow-Headers", "Content-Range,Content-Type");
		resp.setHeader("Access-Control-Allow-Origin", Configurations.getCrossOrigins());
		resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
	}

	@Override
	public void destroy() {
		super.destroy();
	}

}
