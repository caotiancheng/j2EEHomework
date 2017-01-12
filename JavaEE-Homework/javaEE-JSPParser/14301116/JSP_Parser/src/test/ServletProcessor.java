package test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import Servlet.ServletInterface;

public class ServletProcessor {
	
	public void process(Request request, Response response) {
	    String uri            = request.getUri();
	    String servletName=null;
	   if(uri.contains("?")){
		   servletName    = uri.substring(uri.lastIndexOf("/")+1,uri.indexOf("?"));
	   }else{
		   servletName    = uri.substring(uri.lastIndexOf("/")+1);
	   }
	   //System.out.println(servletName);
	    
	    Class clazz = null;
		try {
			clazz = Class.forName(XML.reader(servletName));
			
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	    ServletInterface servlet = null;
	    try {
	        servlet = (ServletInterface) clazz.newInstance();
	        
	        servlet.service(request,response);
	        
	    }
	    
	    catch (Exception e) {
	        System.out.println(e.toString());
	    }
	    catch (Throwable e) {
	        System.out.println(e.toString());
	    }
	}

}
