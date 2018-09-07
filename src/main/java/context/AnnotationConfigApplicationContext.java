package context;

import annotation.MyComponent;
import annotation.MyValue;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 参考：
 * 1.Spring首先解析类名为beanName参考：org.springframework.beans.factory.support.DefaultListableBeanFactory
 * 2.然后通过beanName去
 * 2.1.手动注册的单例对象找，org.springframework.beans.factory.support.DefaultSingletonBeanRegistry - singletonObjects
 * 2.2.如果1没有，则去对象缓存中取bean，是单例，参考：org.springframework.beans.factory.support.FactoryBeanRegistrySupport - factoryBeanObjectCache
 * 3.对象缓存找不到则通过FactoryBean创建，支持单例和原型，参考：org.springframework.beans.factory.config.AbstractFactoryBean - getObject()
 *
 * @author ah-hui
 */
public class AnnotationConfigApplicationContext {

    /**
     * 存储类定义对象
     */
    private Map<String, Class<?>> beanDefinationFactory = new ConcurrentHashMap<String, Class<?>>();
    /**
     * 存储单例对象
     */
    private Map<String, Object> singletonBeanFactory = new ConcurrentHashMap<String, Object>();

    /**
     * 构造方法
     *
     * @param packageName 指定要扫描加载的包名
     */
    public AnnotationConfigApplicationContext(String packageName) {
        scanPkg(packageName);
    }

    /**
     * 扫描指定包，找到包中的类文件
     * 对于标准类文件（有注解的）反射加载创建类定义对象并放入容器中
     *
     * @param pkg 指定包
     */
    private void scanPkg(final String pkg) {
        // 替换包名中的"."，将包结构转换为目录结构
        String pkgDir = pkg.replaceAll("\\.", "/");
        // 获取目录结构在类路径中的位置
        URL url = getClass().getClassLoader().getResource(pkgDir);
        // 基于这个路径资源(url)，构建一个文件对象
        File file = new File(url.getFile());
        // 获取次目录中的.class文件
        File[] files = file.listFiles(new FileFilter() {

            public boolean accept(File file) {
                // 获取文件名
                String fName = file.getName();
                // 如果是目录，递归扫描其内部文件
                if (file.isDirectory()) {
                    scanPkg(pkg + "." + fName);
                } else {
                    // 是class文件，ok
                    if (fName.endsWith(".class")) {
                        return true;
                    }
                }
                return false;
            }
        });
        // 遍历Class文件
        for (File f : files) {
            // 获取文件名
            String fName = f.getName();
            // 获取去除.class之后的文件名
            fName = fName.substring(0, fName.lastIndexOf("."));
            // 将名字的第一个字母转换为小写，用作beanName
            String beanName = String.valueOf(fName.charAt(0)).toLowerCase() + fName.substring(1);
            // 构建一个类全名（包名.类名）
            String pkgCls = pkg + "." + fName;
            try {
                // 通过反射构建类对象
                Class<?> c = Class.forName(pkgCls);
                // 判定这个类上是否有MyComponent注解
                if (c.isAnnotationPresent(MyComponent.class)) {
                    // 将类对象存储到map容器中
                    beanDefinationFactory.put(beanName, c);
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 根据传入的beanName获取容器中的对象
     *
     * @param beanName beanName
     * @return 对象
     */
    public Object getBean(String beanName) {
        // 根据传入的beanName获取类对象
        Class<?> cls = beanDefinationFactory.get(beanName);
        // 根据类对象获取定义的注解
        MyComponent annotation = cls.getAnnotation(MyComponent.class);
        // 获取注解的scope属性值
        String scope = annotation.scope();
        try {
            // 如果scope是单例，获取单例对象
            if ("singleton".equals(scope)) {
                // 如果对象缓存中有，直接返回，否则实例化一个
                if (singletonBeanFactory.get(beanName) == null) {
                    Object instance = cls.newInstance();
                    setFieldValues(cls, instance);
                    singletonBeanFactory.put(beanName, instance);
                }
                return singletonBeanFactory.get(beanName);
            }
            // 如果scope是原型，则直接创建对象
            if ("prototype".equals(scope)) {
                Object instance = cls.newInstance();
                setFieldValues(cls, instance);
                return instance;
            }
            // 先就处理这两种，暂没有设置默认单例
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        // 遇到异常，返回null
        return null;
    }

    /**
     * 重载getBean，多传入一个Class帮你强转了
     *
     * @param beanName beanName
     * @param c        强转的Class
     * @param <T>      泛型支持
     * @return 强转后的对象
     */
    public <T> T getBean(String beanName, Class<T> c) {
        return (T) getBean(beanName);
    }

    /**
     * 扫描字段上的@MyValue注解，通过反射为对象属性赋值
     *
     * @param cls 类定义
     * @param obj 实例对象
     */
    public void setFieldValues(Class<?> cls, Object obj) {
        // 获取类中的所有的成员属性
        Field[] declaredFields = cls.getDeclaredFields();
        // 遍历所有属性
        for (Field field : declaredFields) {
            // 如果此属性有@MyValue注解，做赋值
            if (field.isAnnotationPresent(MyValue.class)) {
                // 获取属性名
                String fieldName = field.getName();
                // 获取注解中的值
                String value = field.getAnnotation(MyValue.class).value();
                // 获取属性所定义的类型
                String type = field.getType().getName();
                // 将属性名首字母大写
                fieldName = String.valueOf(fieldName.charAt(0)).toUpperCase() + fieldName.substring(1);
                // setter方法
                String setterName = "set" + fieldName;
                try {
                    // 根据方法名和参数类型获取对应set方法
                    Method method = cls.getDeclaredMethod(setterName, field.getType());
                    // 反射调用
                    if ("java.lang.Integer".equals(type) || "int".equals(type)) {
                        int intValue = Integer.valueOf(value);
                        method.invoke(obj, intValue);
                    } else if ("java.lang.String".equals(type)) {
                        method.invoke(obj, value);
                    }
                    // 其他类型略
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 销毁方法，释放资源
     */
    public void close() {
        beanDefinationFactory.clear();
        beanDefinationFactory = null;
        singletonBeanFactory.clear();
        singletonBeanFactory = null;
    }
}
