package org.wso2.iot.firealarm.access;

import org.wso2.iot.firealarm.access.api.FirealarmClient;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class OperationServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request,
	                      HttpServletResponse response)
			throws ServletException, IOException {

		String token = (String) request.getSession(true).getAttribute("token");
		request.setAttribute("errMsg", null);
		if (token == null) {
			request.setAttribute("errMsg", "Invalid Credential / Unauthorized Access");

			request.getRequestDispatcher("index.jsp").forward(request, response);
		}

		String operation = request.getParameter("operation");
		String protocol = request.getParameter("protocol");
		String username = (String) request.getSession(true).getAttribute("username");

		if (operation.equals("bulb")) {
			String deviceId = request.getParameter("deviceId");
			String state = request.getParameter("state");
			FirealarmClient fc = new FirealarmClient();
			String resp = fc.switchBulb(token, username, deviceId, state, protocol);
			response.getWriter().print(resp);
		} else if (operation.equals("temp")) {
			String deviceId = request.getParameter("deviceId");
			FirealarmClient fc = new FirealarmClient();
			String temperature = fc.getTemperature(token, username, deviceId, protocol);
			if (temperature != null) {
//				request.setAttribute("tempReply",temperature);
//				request.getRequestDispatcher("control.jsp").forward(request,response);
				try {
					response.getWriter().print("Temperature is " + Double.parseDouble
							(temperature) +

							                           " C");
				} catch (NumberFormatException e) {

					response.getWriter().print(temperature);
				}
			} else {
//				request.setAttribute("errMsg","Connection Failed try again later");
//				request.setAttribute("tempReply",null);
//				request.getRequestDispatcher("control.jsp").forward(request,response);
				response.getWriter().print("Connection Failure,Try again later");
			}
		} else if (operation.equals("humid")) {
			String deviceId = request.getParameter("deviceId");
			FirealarmClient fc = new FirealarmClient();
			String humidity = fc.getHumidity(token, username, deviceId, protocol);
			if (humidity != null) {
//				request.setAttribute("tempReply",temperature);
//				request.getRequestDispatcher("control.jsp").forward(request,response);
				try {
					response.getWriter().print("Humidity is " + Double.parseDouble(humidity) +

							                           "%");
				} catch (NumberFormatException e) {

					response.getWriter().print(humidity);
				}
			} else {
//				request.setAttribute("errMsg","Connection Failed try again later");
//				request.setAttribute("tempReply",null);
//				request.getRequestDispatcher("control.jsp").forward(request,response);
				response.getWriter().print("Connection Failure,Try again later");
			}
		}

	}


	protected void doGet(HttpServletRequest request,
	                     HttpServletResponse response)
			throws ServletException, IOException {

		String token = (String) request.getSession(true).getAttribute("token");
		request.setAttribute("errMsg", null);
		if (token == null) {
			request.setAttribute("errMsg", "Invalid Credential / Unauthorized Access");

			request.getRequestDispatcher("index.jsp").forward(request, response);
		}
		request.getRequestDispatcher("control.jsp").forward(request, response);

	}
}
