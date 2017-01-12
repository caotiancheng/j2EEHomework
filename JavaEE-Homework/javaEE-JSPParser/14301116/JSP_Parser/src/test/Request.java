package test;

import java.io.InputStream; 
import java.io.IOException; 
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

 

public class Request { 

  private InputStream input; 
  private String uri; 
  private String character;
  private Map<String, String> map=null;
  public Request(InputStream input) { 
    this.input = input; 
  } 

 

  public void parse() throws UnsupportedEncodingException { 
   // Read a set of characters from the socket 
    StringBuffer request = new StringBuffer(2048); 
    int i; 
    byte[] buffer = new byte[2048]; 
    try { 
      i = input.read(buffer); 
    } 

    catch (IOException e) { 
      e.printStackTrace(); 
      i = -1; 
    } 

    for (int j=0; j<i; j++) { 
      request.append((char) buffer[j]); 
    } 
   System.out.print(request.toString()); 
    uri = parseUri(request.toString()); 
    
    setParameter();
    
  } 

 

  private String parseUri(String requestString) { 
    int index1, index2; 
    index1 = requestString.indexOf(' '); 

    if (index1 != -1) { 
      index2 = requestString.indexOf(' ', index1 + 1); 
      
      if (index2 > index1) 
        return requestString.substring(index1 + 1, index2); 
    } 

    return null; 

  } 

 

  public String getUri() { 
    return uri; 
  }
  
  public Object getAttribute(String attribute) {
	  return null;
	    }

	  public Enumeration getAttributeNames() {
	  return null;
	    }

	  public String getRealPath(String path) {
	  return null;
	    }



	public void setCharacterEncoding(String character) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		this.character=character;
		
		uri=URLDecoder.decode(uri, character);
       /*byte[] b = uri.getBytes("ISO8859—1");//编码  
        String sa = new String(b, character);//解码:用什么字符集编码就用什么字符集解码  
        uri=sa;
        System.out.println(uri);  */
         // System.out.println(uri);
		map.clear();
		try {
			setParameter();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public String getParameter(String aim) {
		
		//System.out.println(aim);
		return map.get(aim).toString();
	}
 
	private void setParameter() throws UnsupportedEncodingException{
		if(uri.contains("?")){
			String a[] = uri.split("\\?");  
			
			String url[]=a[1].split("&");
			map = new HashMap<String,String>();
			for(int i=0;i<url.length;i++){
				
				String b[]=url[i].split("=");

				map.put(b[0],b[1]);
			}
		}
		
		
	}
 

} 