package org.wso2.iot.firealarm.access;

import org.wso2.iot.firealarm.access.api.AccessTokenClient;
import org.wso2.iot.firealarm.access.api.AccessTokenException;
import org.wso2.iot.firealarm.access.api.AccessTokenInfo;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

public class LoginServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request,
						  HttpServletResponse response)
			throws ServletException, IOException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");

		//UserAgent + ClientIP + '
		String userAgent = request.getHeader("User-Agent");
		if (userAgent == null) {
			userAgent = username;

		}
		String ipAddress = request.getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) {
			ipAddress = request.getRemoteAddr();
		}
		String salt = UUID.randomUUID().toString();
		String instanceId = userAgent + ipAddress + salt;
//		AccessTokenClient client = new AccessTokenClient();
		AccessTokenClient client = AccessTokenClient.getInstance();
		AccessTokenInfo accessTokenInfo;

		try {
			accessTokenInfo = client.getAccessToken(username, password, instanceId);
			if (accessTokenInfo != null) {
				request.getSession(true).setAttribute("token", accessTokenInfo.getAccess_token());
				request.getSession(true).setAttribute("username", username);
				request.getSession(true).removeAttribute("password");

				request.getRequestDispatcher("control.jsp").forward(request, response);
			} else {
				request.setAttribute("errMsg", "Invalid Credential");
				request.getRequestDispatcher("index.jsp").forward(request, response);
			}
		} catch (AccessTokenException e) {
			request.setAttribute("errMsg", "Invalid Credential");
			request.getRequestDispatcher("index.jsp").forward(request, response);
		}


	}

	protected void doGet(HttpServletRequest request,
						 HttpServletResponse response)
			throws ServletException, IOException {
		response.sendRedirect("index.jsp");

	}
}
