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

    //���б����������bean���������map�У���������xml�Ժ����е�bean�����������������
    private Map<String, Object> beans = new HashMap<String, Object>();

    @SuppressWarnings("unchecked")
    public ClassPathXmlApplicationContext(String[] xmlPath) {
        xmlPath[0] = "src\\" + xmlPath[0];
        SAXBuilder builder = new SAXBuilder(false);// ������ȡxml�ļ�
        Document document;// ����һ���ĵ�����������xmlת����Document����
        try {
            document = builder.build(new FileInputStream(new File(xmlPath[0])));// �Ӹ������ļ��ж�ȡxml���ҹ�����Document
            Element elementRoot = document.getRootElement();// ����ĵ�����ĸ��ڵ�

            List<Element> elementList = elementRoot.getChildren("bean");// ��ø��ڵ��������е�bean�ڵ�
            for (Element e : elementList) {// ����bean�ڵ�
                Element element = e;// ��ǰ��bean�ڵ�
                String id = element.getAttribute("id").getValue();// bean��id
                String clazzStr = element.getAttribute("class").getValue();// bena��class
                
                Class clazz = Class.forName(clazzStr);// ͨ�����䣬�õ�bean��class��Class
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
                //Object clazzObj = clazz.newInstance();// ���һ��class��Ӧ��ʵ��
                beans.put(id, clazzObj);// ��bean��id��class����map������
                List<Element> propertyElementList = element.getChildren("property");// �õ�bean�����е�propertyԪ��
                for (Element property : propertyElementList) {// �������е�propertyԪ�ؽڵ�
                    String name = property.getAttributeValue("name");// �õ�property��name
                    Object valueObj = null;// perperty��name��Ӧ��object

                    Method[] methods = clazz.getMethods();// �õ�class�����еķ���
                    
                    // �����Ҫע����ǻ������Ͱ���String
                    String valueStr = property.getAttributeValue("value");
                    if (valueStr != null) {
                        valueObj = valueStr;
                        for (Method method : methods) {// �������еķ���
                            String methodName = method.getName();// ��������
                            Class[] types = method.getParameterTypes();// ������������
                            // �ҵ���Ӧ��setXxx����
                            if (methodName.contains("set") && methodName.toUpperCase().contains(name.toUpperCase())) {
                                Class parameterType = types[0];// �õ�setXxx������������
                                // �����Integer����
                                if (parameterType == Integer.class) {
                                    method.invoke(clazzObj, Integer.parseInt(valueStr));
                                } else {// �����String����
                                    method.invoke(clazzObj, valueStr);
                                }
                            }
                        }
                    }

                    // ���ע�������������
                    String refStr = property.getAttributeValue("ref");
                    if (refStr != null) {
                        valueObj = beans.get(refStr);// ��map��ȡ����Ӧ�����ö���
                        for (Method m : methods) {
                            String methodName = m.getName();
                            if (methodName.contains("set") && methodName.toUpperCase().contains(name.toUpperCase())) {
                                // �õ���Ӧ�ķ������ڶ���������method�����Ĳ������ͣ���һ���ӿ�����
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
