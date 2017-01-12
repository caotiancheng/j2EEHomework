package Servlet;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import test.Request;
import test.Response;
public class LoginServlet implements ServletInterface{
	public void service(Request request, Response response) throws ServletException, IOException {
        // TODO Auto-generated method stub
		
		request.setCharacterEncoding("utf-8");
	   String username = request.getParameter("username");
	   String password = request.getParameter("paw");
	   PrintWriter out = response.getWriter();
	   
       out.println("username:"+username);
       out.println("password:"+password);
   }
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	@Override
	public ServletConfig getServletConfig() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getServletInfo() {
		// TODO Auto-generated method stub
		return null;
	}
	public void init(ServletConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}
}
