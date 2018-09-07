package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注明注解用在什么位置：TYPE-类、接口、枚举
 */
@Target(ElementType.TYPE)
/**
 * 注明注解何时有效：运行时
 */
@Retention(RetentionPolicy.RUNTIME)
/**
 * 类型@interface表示注解类型
 */
public @interface MyComponent {
    /**
     * 为注解定义属性
     */
    public String scope() default "";

}
