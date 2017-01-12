package Servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import test.Request;
import test.Response;

public interface ServletInterface {
	public void service(Request request, Response response) throws ServletException, IOException;
	public void destroy();
	public String getServletInfo();
	public ServletConfig getServletConfig();
}
