package spring;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import jdk.internal.dynalink.beans.StaticClass;
import test.Autowired;

public class PropertyClassLoad implements ApplicationContext {

    //���б����������bean���������map�У���������xml�Ժ����е�bean�����������������
    private Map<String, Object> beans = new HashMap<String, Object>();

    @SuppressWarnings("unchecked")
    public PropertyClassLoad(String xmlPath) {
        xmlPath = "src\\" + xmlPath;
        SAXBuilder builder = new SAXBuilder(false);// ������ȡxml�ļ�
        Document document;// ����һ���ĵ�����������xmlת����Document����
        try {
            document = builder.build(new FileInputStream(new File(xmlPath)));// �Ӹ������ļ��ж�ȡxml���ҹ�����Document
            Element elementRoot = document.getRootElement();// ����ĵ�����ĸ��ڵ�

            List<Element> elementList = elementRoot.getChildren("bean");// ��ø��ڵ��������е�bean�ڵ�
            for (Element e : elementList) {// ����bean�ڵ�
                Element element = e;// ��ǰ��bean�ڵ�
                String id = element.getAttribute("id").getValue();// bean��id
                String clazzStr = element.getAttribute("class").getValue();// bena��class
                Class clazz = Class.forName(clazzStr);// ͨ�����䣬�õ�bean��class��Class
                List<Class<?>> list=AnnoManageUtil.getPackageController("test",Autowired.class);
                Object clazzObj=null;
                if (!list.contains(clazz)) {
                	clazzObj = clazz.newInstance();// ���һ��class��Ӧ��ʵ��
                }
                
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