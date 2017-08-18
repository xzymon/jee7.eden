package com.xzymon.jee7.eden.web.extensions;

import com.xzymon.jee7.eden.web.RequestAttributeName;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

public class ExtendedHttpServlet extends HttpServlet {
	public static void setRequestAttribute(HttpServletRequest request, RequestAttributeName requestAttributeName, Object o) {
		request.setAttribute(requestAttributeName.getName(), o);
	}
}
