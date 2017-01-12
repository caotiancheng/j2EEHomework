package test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import Class.StaticReader;


class HttpServlet{
	 private static final String SHUTDOWN_COMMAND ="/SHUTDOWN"; 
	  // the shutdown command received 
	  private boolean shutdown = false; 

	  public static void main(String[] args) { 
		  HttpServlet server = new HttpServlet(); 
	    server.await(); 
	  } 

	  
	public void await() {
		ServerSocket serverSocket = null;
		int       port  = 8080;

		try {
		serverSocket =  new ServerSocket(port, 1,
		InetAddress.getByName("127.0.0.1"));
		    }
		catch (IOException e) {
		e.printStackTrace();
		System.exit(1);
		    }
		
		// 循环，等待一个请求
		while (!shutdown) {
		Socket socket       = null;
		InputStream input   = null;
		OutputStream output = null;
		
				try {
					
				socket = serverSocket.accept();
				//System.out.println(socket);
				input  = socket.getInputStream();
				output = socket.getOutputStream();
				
				// 创建请求对象并解析
				Request request = new Request(input);
				request.parse();
				
				// 创建回应对象
				Response response = new Response(output);
				response.setRequest(request);
				//检测是否是 servlet 或静态资源的请求
				//servlet 请求以 &quot;/servlet/&quot; 开始 
				
					if (request.getUri().startsWith("/servlet/")) 
					{
						
					ServletProcessor processor = new ServletProcessor();
					
					processor.process(request, response);
					
					}
					else 
					{
						
						if(request.getUri().endsWith(".jsp"))
						{
							StaticReader staticReader=new StaticReader();
							staticReader.process(request,response);
						}
					}
			
				// 关闭socket 
				socket.close();
				System.out.println("关闭");
				//检测是否前面的 URI 是一个 shutdown 命令
				shutdown = request.getUri().equals(SHUTDOWN_COMMAND);
			    }
				catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
		        }
		    }
		}

}