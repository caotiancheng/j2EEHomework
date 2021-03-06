package spring;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
//import java.lang.reflect.Method;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import test.Autowired;

/**
 * Created by wzj on 2016/10/1.
 */
public final class AnnoManageUtil
{
    /**
     * 获取当前包路径下指定的Controller注解类型的文件
     * @param packageName 包名
     * @param annotation 注解类型
     * @return 文件
     */
    public static  List<Class<?>> getPackageController(String packageName, Class<? extends Annotation> annotation)
    {
        List<Class<?>> classList = new ArrayList<Class<?>>();

        String packageDirName = packageName.replace('.', '/');

        Enumeration<URL> dirs = null;

        //获取当前目录下面的所有的子目录的url
        try
        {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        while (dirs.hasMoreElements())
        {
            URL url = dirs.nextElement();

            //得到但钱url的类型
            String protocol = url.getProtocol();

            //如果当前类型是文件类型
            if ("file".equals(protocol))
            {
                //获取包的物理路径
                String filePath = null;
                try
                {
                    filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                }
                catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }

                filePath = filePath.substring(1);
                getFilePathClasses(packageName,filePath,classList,annotation);
            }
        }


            return classList;
    }

    /**
     * 从指定的包下面找到文件名
     * @param packageName
     * @param filePath
     * @param classList
     * @param annotation 注解类型
     * @throws SecurityException 
     * @throws NoSuchMethodException 
     */
    private static void getFilePathClasses(String packageName,String filePath,List<Class<?>> classList,
                                           Class<? extends Annotation> annotation)
    {
        Path dir = Paths.get(filePath);

        DirectoryStream<Path> stream = null;
        try
        {
            //获得当前目录下的文件的stream流
            stream = Files.newDirectoryStream(dir);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        for(Path path : stream)
        {
            String fileName = String.valueOf(path.getFileName());

            String className = fileName.substring(0, fileName.length() - 6);

            Class<?> classes = null;
            try
            {
                classes = Thread.currentThread().getContextClassLoader().loadClass(packageName + "." + className);
            }
            catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }

            //判断该注解类型是不是所需要的类型
            //System.out.println(classes.isAnnotationPresent(annotation));
            Constructor[] Constructors = classes.getConstructors();
            for (Constructor constructor : Constructors){
            	if (constructor.getAnnotation(annotation)!=null) {
            		classList.add(classes);
            		//return;
				}
            }
            if (null != classes && null != classes.getAnnotation(annotation))
            {
                //把这个文件加入classlist中
            	if (!classList.contains(classes)) {
            		classList.add(classes);
				}
                
            }
        }
    }

    
    


  

}
