package spring;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import test.Autowired;
import test.car;
import test.office;

public class ClassPathXmlApplicationContext implements ApplicationContext {

    //所有被容器管理的bean都放在这个map中，当解析完xml以后，所有的bean都放在了这个集合中
    private Map<String, Object> beans = new HashMap<String, Object>();

    @SuppressWarnings("unchecked")
    public ClassPathXmlApplicationContext(String[] xmlPath) {
        xmlPath[0] = "src\\" + xmlPath[0];
        SAXBuilder builder = new SAXBuilder(false);// 用来读取xml文件
        Document document;// 构建一个文档对象，用来将xml转换成Document对象
        try {
            document = builder.build(new FileInputStream(new File(xmlPath[0])));// 从给定的文件中读取xml并且构建成Document
            Element elementRoot = document.getRootElement();// 获得文档对象的根节点

            List<Element> elementList = elementRoot.getChildren("bean");// 获得根节点下面所有的bean节点
            for (Element e : elementList) {// 遍历bean节点
                Element element = e;// 当前的bean节点
                String id = element.getAttribute("id").getValue();// bean的id
                String clazzStr = element.getAttribute("class").getValue();// bena的class
                
                Class clazz = Class.forName(clazzStr);// 通过反射，得到bean的class的Class
                Object clazzObj=null;
                List<Class<?>> list=AnnoManageUtil.getPackageController("test",Autowired.class);
                if (list.contains(clazz)) {
                	List<Element> propertyElementList = element.getChildren("property");
                	Object[] objectset=new Object[propertyElementList.size()];int i=0,j=0;
                	Class[] classset=new Class[propertyElementList.size()];
                	for (Element property : propertyElementList) {
                		String refStr = property.getAttributeValue("ref");
                        if (refStr != null) {
                        	Class class1=Class.forName("test."+refStr);
                        	List<String> beanStr=new ArrayList<>();
                        	for (Element e1 : elementList)  {
                        		String id1 = e1.getAttribute("id").getValue();
                        		beanStr.add(id1);
							}
                        	if (beanStr.contains(refStr)) {
								PropertyClassLoad pcLoad=new PropertyClassLoad("bean.xml");
								objectset[i++]=pcLoad.getBean(refStr);
								classset[j++]=pcLoad.getBean(refStr).getClass();
							}else{
								objectset[i++]=class1.newInstance();
								classset[j++]=class1;
							}
                        	
                        	//System.out.println(classset);
                        }
                	}
                	Constructor c1=clazz.getDeclaredConstructor(classset);   
                    c1.setAccessible(true);   
            		clazzObj=c1.newInstance(objectset);
            		//System.out.println(clazzObj);
				}else{
					clazzObj = clazz.newInstance();
				}
                //Object clazzObj = clazz.newInstance();// 获得一个class对应的实例
                beans.put(id, clazzObj);// 将bean的id和class放入map集合中
                List<Element> propertyElementList = element.getChildren("property");// 得到bean下所有的property元素
                for (Element property : propertyElementList) {// 遍历所有的property元素节点
                    String name = property.getAttributeValue("name");// 得到property的name
                    Object valueObj = null;// perperty的name对应的object

                    Method[] methods = clazz.getMethods();// 得到class下所有的方法
                    
                    // 如果需要注入的是基本类型包括String
                    String valueStr = property.getAttributeValue("value");
                    if (valueStr != null) {
                        valueObj = valueStr;
                        for (Method method : methods) {// 遍历所有的方法
                            String methodName = method.getName();// 方法名称
                            Class[] types = method.getParameterTypes();// 方法参数类型
                            // 找到对应的setXxx方法
                            if (methodName.contains("set") && methodName.toUpperCase().contains(name.toUpperCase())) {
                                Class parameterType = types[0];// 得到setXxx方法参数类型
                                // 如果是Integer类型
                                if (parameterType == Integer.class) {
                                    method.invoke(clazzObj, Integer.parseInt(valueStr));
                                } else {// 如果是String类型
                                    method.invoke(clazzObj, valueStr);
                                }
                            }
                        }
                    }

                    // 如果注入的是引用类型
                    String refStr = property.getAttributeValue("ref");
                    if (refStr != null) {
                        valueObj = beans.get(refStr);// 在map中取出对应的引用对象
                        for (Method m : methods) {
                            String methodName = m.getName();
                            if (methodName.contains("set") && methodName.toUpperCase().contains(name.toUpperCase())) {
                                // 得到对应的方法，第二个参数是method方法的参数类型，是一个接口类型
                                Method method = clazz.getMethod(methodName, valueObj.getClass().getInterfaces()[0]);
                                method.invoke(clazzObj, valueObj);
                            }
                        }
                    }

                }
            }
        } catch (JDOMException | IOException | ClassNotFoundException | InstantiationException | IllegalAccessException
                | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Object getBean(String beanName) {
        return beans.get(beanName);
    }

}
