package net.tenie.lib.tools;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
 
public class DynamicJarClassLoader extends URLClassLoader {
    private static boolean canCloseJar = false;
    private List<JarURLConnection> cachedJarFiles;
 
    static {
        // JDK1.7以上版本支持直接调用close方法关闭打开的jar
        // 如果不支持close方法，需要手工释放缓存，避免卸载模块后无法删除jar
        try {
            URLClassLoader.class.getMethod("close");
            canCloseJar = true;
        } catch (NoSuchMethodException e) {
//            System.out.println(e);
            e.printStackTrace();
        } catch (SecurityException e) {
//            System.out.println(e);
            e.printStackTrace();
        }
    }
 
    public DynamicJarClassLoader(String libDir, ClassLoader parent) {
        super(new URL[]{}, null == parent ? Thread.currentThread().getContextClassLoader() : parent);
        File base = new File(libDir);
        URL[] urls = null;
        if (null != base && base.canRead() && base.isDirectory()) {
            File[] files = base.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    if (pathname.getName().contains(".jar")) {
                        return true;
                    } else return false;
                }
            });
            urls = new URL[files.length];
            for (int j = 0; j < files.length; j++) {
                try {
                    URL element = files[j].toURI().normalize().toURL();
//                    System.out.println("Adding '" + element.toString() + "' to classloader");
                    urls[j] = element;
                } catch (MalformedURLException e) {
//                    System.out.println(e);
                	e.printStackTrace();
                }
            }
        }
        init(urls);
    }
 
    private void init(URL[] urls) {
        cachedJarFiles = canCloseJar ? null : new ArrayList<JarURLConnection>();
        if (urls != null) {
            for (URL url : urls) {
                this.addURL(url);
            }
        }
    }
 
    @Override
    protected void addURL(URL url) {
        if (!canCloseJar) {
            try {
                // 打开并缓存文件url连接
                URLConnection uc = url.openConnection();
                if (uc instanceof JarURLConnection) {
                    uc.setUseCaches(true);
                    ((JarURLConnection) uc).getManifest();
                    cachedJarFiles.add((JarURLConnection) uc);
                }
            } catch (Exception e) {
            }
        }
        super.addURL(url);
    }
 
    public void close() throws IOException {
        if (canCloseJar) {
            try {
                super.close();
            } catch (IOException e) {
//                System.out.println(e);
            	e.printStackTrace();
            }
        } else {
            for (JarURLConnection conn : cachedJarFiles) {
                conn.getJarFile().close();
            }
            cachedJarFiles.clear();
        }
    }
 

}

// 测试类
class DynamicJarApp {
    final static String libDir = "f://lib";
    final static String testClass = "cn.test.TestClass";
    static URLClassLoader currentClassload;
    static long changeLastModified=0;
 
    /**
     * 根据修改时间判断文件是否修改
     * @param libDir
     * @return
     */
    public static long changeJarVersion(String libDir) {
        long lastModified = 0;
        File base = new File(libDir);
        if (null != base && base.canRead() && base.isDirectory()) {
            File[] files = base.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    if (pathname.getName().contains(".jar")) {
                        return true;
                    } else return false;
                }
            });
            for (int j = 0; j < files.length; j++) {
                lastModified += files[j].lastModified();
            }
        }
        return lastModified;
    }
 
    public static void main(String[] args) {
        Thread thead=null;
        try {
            changeLastModified=changeJarVersion(libDir);
            currentClassload= new DynamicJarClassLoader(libDir, null);
            Class<?> clazz = currentClassload.loadClass(testClass);
//            System.out.println(clazz.getName());
            Object object= clazz.newInstance();
            Method method= clazz.getDeclaredMethod("info");
            method.setAccessible(true);
            method.invoke(object);
 
            thead=new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
//                            System.out.println("wait......");
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
 
                        long lastModified = changeJarVersion(libDir);
 
                        //jar包版本是否修改
                        if (changeLastModified != lastModified) {
                            try {
                                //关闭旧的类加载器
                                currentClassload.close();
//                                System.out.println("close......");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            finally {
                                changeLastModified = lastModified;
                            }
                            //创建新的类加载器
                            DynamicJarClassLoader newDynamicJarClassLoader = new DynamicJarClassLoader(libDir, currentClassload.getParent());
                            currentClassload = newDynamicJarClassLoader;
                        }
 
                        try {
                            //执行jar包类方法
                            Class<?> clazz = currentClassload.loadClass(testClass);
//                            System.out.println(clazz.getName());
                            Object object = clazz.newInstance();
                            Method method = clazz.getDeclaredMethod("info");
                            method.setAccessible(true);
                            method.invoke(object);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thead.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        try {
            thead.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }       
    }
}
