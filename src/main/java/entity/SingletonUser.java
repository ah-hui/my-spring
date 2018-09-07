package entity;

import annotation.MyComponent;
import annotation.MyValue;

/**
 * @author ah-hui
 */
@MyComponent(scope = "singleton")
public class SingletonUser {

    @MyValue("1")
    private Integer id;

    @MyValue("张三")
    private String name;

    @MyValue("123456")
    private String password;

    public SingletonUser() {
        System.out.println("创建了一个【单例User】");
    }

    public void login() {
        System.out.println("用户登录：" + this.toJson());
    }

    public String toJson() {
        return "[id=]" + id + ",name=" + name + ",password=" + password + "]";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
