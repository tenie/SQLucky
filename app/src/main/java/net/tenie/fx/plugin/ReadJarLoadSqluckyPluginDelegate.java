package net.tenie.fx.plugin;

import net.tenie.Sqlucky.sdk.SqluckyPluginDelegate;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.Map.Entry;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 通过jar文件路径, 实例化SqluckyPluginDelegate的对象
 */
public class ReadJarLoadSqluckyPluginDelegate {
    /**
     * 通过jar文件路径, 找到实现了接口的实例化对象
     * @param jarFilePath
     * @return
     * @throws MalformedURLException
     */
    public static SqluckyPluginDelegate loadClass(String jarFilePath) throws MalformedURLException {
        String interfaceName = SqluckyPluginDelegate.class.getName();
        File file = new File(jarFilePath);
        Class val = null;
        if (!file.exists()) {
            System.out.println("文件不存在！");
            return null;
        }
        if (!file.isFile()) {
            System.out.println("读取的为文件夹而非文件！");
            return null;
        }
        if (!file.canRead()) {
            System.out.println("当前文件不可读！");
            return null;
        }
        URL url1 = file.toURI().toURL();
        URLClassLoader jarUrlClassLoader = new URLClassLoader(new URL[]{url1},
                Thread.currentThread().getContextClassLoader());
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(file);
            // 获取jar中实际的MAINFEST.MF文件
//            Manifest manifest = jarFile.getManifest();
//            pringManifestFile(manifest);
            // 开始获取jar中的.class文件
            Enumeration<JarEntry> entries = jarFile.entries();
            List<String> classNames = getClassNames(entries);
            // 遍历jar中的class 找到实现接口的class
            for (var x : classNames) {
                var tmp = loadAndInstanceClass(x, interfaceName, jarUrlClassLoader);
                if (tmp != null) {
                    val = tmp;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (jarFile != null) {
                try {
                    jarFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // 返回实例对象
        if (val != null) {
            try {
                SqluckyPluginDelegate newInstance = (SqluckyPluginDelegate) val.getDeclaredConstructor().newInstance();
                System.out.println(newInstance.pluginName());
                return newInstance;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
        return null;
    }

    /**
     * 根据接口的名称字符串, 找到实现该接口的class
     */
    private static Class loadAndInstanceClass(String clazzName, String interfaceName, ClassLoader classLoader) {
        try {
            if ("module-info".equals(clazzName)) {
                return null;
            }
            // 需要使用其他的classLoader加载
            Class<?> clazz = classLoader.loadClass(clazzName);
            Class<?>[] arrs = clazz.getInterfaces();
            System.out.println(clazz);

            if (arrs != null && arrs.length > 0) {
                for (int i = 0; i < arrs.length; i++) {
                    String tmpInterfaceName = arrs[i].getName();
                    System.out.println(tmpInterfaceName);
                    if (tmpInterfaceName.equals(interfaceName)) {
                        return clazz;
                    }
                }

            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 打印并获取所有的class
     */
    private static List<String> getClassNames(Enumeration<JarEntry> entries) {
        List<String> classNames = new ArrayList<String>();
        while (entries.hasMoreElements()) {
            JarEntry nextElement = entries.nextElement();
            String name = nextElement.getName();
            // 这个获取的就是一个实体类class java.util.jar.JarFile$JarFileEntry
            // Class<? extends JarEntry> class1 = nextElement.getClass();
//            System.out.println("entry name=" + name);
            // 这样就获取所有的jar中的class文件

            // 加载某个class文件，并实现动态运行某个class
            if (name.endsWith(".class")) {
                String replace = name.replace(".class", "").replace("/", ".");
                classNames.add(replace);
            }
        }
        return classNames;
    }

    /**
     * 输出当前的manifest文件中的信息内容
     */
    private static void pringManifestFile(Manifest manifest) {
        Attributes mainAttributes = manifest.getMainAttributes();
        Set<Entry<Object, Object>> entrySet = mainAttributes.entrySet();
        Iterator<Entry<Object, Object>> iterator = entrySet.iterator();
        // 打印并显示当前的MAINFEST.MF文件中的信息
        while (iterator.hasNext()) {
            Entry<Object, Object> next = iterator.next();
            Object key = next.getKey();
            Object value = next.getValue();
            // 这里可以获取到Class-Path,或者某个执行的Main-Class
            System.out.println(key + ": " + value);
        }
    }
}

