package test;

import java.io.File;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
public class XML {
 public static String reader(String servletName) throws Exception {
	boolean mark=false;
	int i=0;
	
  SAXReader reader = new SAXReader();
  File file = new File("WebContent/WEB-INF/web.xml");
  Document document = reader.read(file);
  Element root = document.getRootElement();
  List<Element> childElements = root.elements();
  
  for (Element child : childElements) {  
	   List<Element> elementList = child.elements();
	   for (Element ele : elementList) {
		   if(mark)
			   {
			   
			   return ele.getText();
			   }
	    if(ele.getName().toString().equals("servlet-name" ))
	    	{if(ele.getText().equals(servletName))
	    	{
	    		mark=true;
	    	}
	    	}
	   }
  }
  return null;
 }
}
//未知属性名情况下
/*List<Attribute> attributeList = child.attributes();
for (Attribute attr : attributeList) {
 System.out.println(attr.getName() + ": " + attr.getValue());
}*/

//已知属性名情况下
// System.out.println("id: " + child.attributeValue("id"));

//未知子元素名情况下

//已知子元素名的情况下
// System.out.println("title" + child.elementText("title"));
// System.out.println("author" + child.elementText("author"));
 //这行是为了格式化美观而存在