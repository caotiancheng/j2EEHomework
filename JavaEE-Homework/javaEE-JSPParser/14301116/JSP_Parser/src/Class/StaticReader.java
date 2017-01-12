package Class;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.regex.Matcher;

import javax.servlet.ServletException;
import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

import Servlet.PrimitiveServlet;
import Servlet.ServletInterface;
import test.Request;
import test.Response;

public class StaticReader {
	public void process(Request request, Response response){
		String uri=request.getUri();
		String jsp_name=uri.substring(uri.lastIndexOf("/")+1,uri.indexOf(".jsp"));
		String java_path=System.getProperty("user.dir")+"\\src\\Servlet\\temp_"+jsp_name+".java";
		File java_file=new File(java_path);
		if(java_file.exists()){
			try {
				run(request,response,jsp_name);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			String jsp_path=System.getProperty("user.dir")+"\\WebContent\\WEB-INF\\"+jsp_name+".jsp";
			
			File jsp_file=new File(jsp_path);
			if(jsp_file.exists()){
				 if(javaForJsp(jsp_file,jsp_name)){
					 try {
							run(request,response,jsp_name);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				 }else{
					 Warning(request,response);
				 }
				 
				
			}else{
				Warning(request,response);
			}
		}
		
	}
	
public boolean javaForJsp(File jsp_file,String jsp_name){
		String jsp_string="";
		try 
        {
            BufferedReader in = new BufferedReader(new FileReader(jsp_file));
            
            String str;
            while ((str = in.readLine()) != null) 
            {
                 jsp_string+=str;
            }
            in.close();
        } 
        catch (IOException e) 
        {
        	
            e.getStackTrace();
            return false;
        }
		/*jsp_string=jsp_string.replaceAll("<%=", ");out.print(");
		jsp_string=jsp_string.replaceAll("%>", ");out.print(\"");
		jsp_string=jsp_string.replaceAll("<%@", "&&&");
		jsp_string=jsp_string.replaceAll("<%", "\");");
		String java_string="out.print(\"";
		java_string+=jsp_string;
		java_string+="\");";
		System.out.println(java_string);*/
		//System.out.println(jsp_string);
		
		
		String java_strings[]=jsp_string.split("<%");
		int length=java_strings.length;
		String java_string="import java.io.PrintWriter;import java.io.IOException;import javax.servlet.ServletConfig;import javax.servlet.ServletException;import test.Request;import test.Response;import Servlet.ServletInterface;\n public class temp_"+jsp_name+" implements ServletInterface{ \n public void destroy() {}\n @Override \n public ServletConfig getServletConfig() {return null;}\n@Override\n	public String getServletInfo() {return null;}\n	public void init(ServletConfig arg0) throws ServletException {}\n public void service(Request request, Response response) throws ServletException, IOException { PrintWriter out = response.getWriter();";
		for(int i=1;i<length;i++){
			//System.out.println("______________________");
			//System.out.println(java_strings[i]);
			if(java_strings[i].startsWith("@")){
				java_strings[i]=java_strings[i].replaceAll("\"",Matcher.quoteReplacement( "\\\""));
				java_string+="out.println(\"";
				String s[]=java_strings[i].split("%>");
				java_string+=s[1];
				java_string+="\");";
			}else if(java_strings[i].startsWith("=")){
				java_strings[i]=java_strings[i].replaceAll("\"",Matcher.quoteReplacement( "\\\""));
				String s[]=java_strings[i].split("%>");
				java_string+="out.println(";
				java_string+=s[0].replaceAll("=","");
				java_string+=");";
				java_string+="out.println(\"";
				java_string+=s[1];
				java_string+="\");";
			}else{
				String s[]=java_strings[i].split("%>");
				java_string+=s[0];
				s[1]=s[1].replaceAll("\"",Matcher.quoteReplacement( "\\\""));
				java_string+="out.println(\"";
				java_string+=s[1];
				java_string+="\");";
			}
		}
		
		java_string+="}\n}";
		String pathname = System.getProperty("user.dir")+"\\src\\Servlet\\temp_"+jsp_name+".java"; 
		

        /* 写入java文件 */  
        File writename = new File(pathname); // 相对路径，如果没有则要建立一个新的output。txt文件  
        try {
			writename.createNewFile();
			 BufferedWriter out = new BufferedWriter(new FileWriter(writename));  
		        out.write(java_string); // \r\n即为换行  
		        out.flush(); // 把缓存区内容压入文件  
		        out.close(); // 最后记得关闭文件  
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} // 创建新文件  
       return true;
	}
	public void Warning(Request request,Response response){
		PrimitiveServlet p=new PrimitiveServlet();
		try {
			p.service(request, response);
		} catch (ServletException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run(Request request,Response response,String jsp_name) throws Exception{
        JavaCompiler complier = ToolProvider.getSystemJavaCompiler();     
        StandardJavaFileManager sjf =   
                complier.getStandardFileManager(null, null, null);  
        Iterable it = sjf.getJavaFileObjects(System.getProperty("user.dir")+"/src/Servlet/temp_"+jsp_name+".java");
        CompilationTask task = complier.getTask(null, sjf, null, null, null, it);  
        task.call();  //调用创建  ,创建class文件
        sjf.close();  
           
        URL urls[] = new URL[]{ new URL("file:"+System.getProperty("user.dir")+"/src/Servlet/")}; //储存文件目录的地址
        URLClassLoader uLoad = new URLClassLoader(urls);  //classloader从哪个目录找？ 
        //uLoad.loadClass("Hello");
        Class c = uLoad.loadClass("temp_"+jsp_name);  //找哪个class文件 注意不带后缀名  
        //c.newInstance();  //创建一个实例  
         ServletInterface servlet=(ServletInterface) c.newInstance();
       servlet.service(request, response);
       }
}
