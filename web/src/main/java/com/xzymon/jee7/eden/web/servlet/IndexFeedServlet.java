package com.xzymon.jee7.eden.web.servlet;

import com.xzymon.jee7.eden.ejb.api.EventRemoteInterface;
import com.xzymon.jee7.eden.web.RequestAttributeName;
import com.xzymon.jee7.eden.web.extensions.ExtendedHttpServlet;
import com.xzymon.jee7.eden.web.managed.FeederBean;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class IndexFeedServlet extends ExtendedHttpServlet {
	@EJB
	private EventRemoteInterface event;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		invokeHttpMethod(request, response);
	}

	private void invokeHttpMethod(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		FeederBean feederBean = new FeederBean();
		feederBean.setMessage(event.getHelloMessage());
		setRequestAttribute(request, RequestAttributeName.FEEDER_BEAN, feederBean);
		//request.setAttribute(RequestAttributeName.FEEDER_BEAN.getName(), feederBean);
		request.getRequestDispatcher("/index.jsp").forward(request, response);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		invokeHttpMethod(request, response);
	}
}
