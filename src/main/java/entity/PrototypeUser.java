package entity;

import annotation.MyComponent;
import annotation.MyValue;

/**
 * @author ah-hui
 */
@MyComponent(scope = "prototype")
public class PrototypeUser {

    @MyValue("2")
    private Integer id;

    @MyValue("李四")
    private String name;

    @MyValue("12345678")
    private String password;

    public PrototypeUser() {
        System.out.println("创建一个【原型User】");
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
