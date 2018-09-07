package service;

import annotation.MyAutowired;
import annotation.MyComponent;
import entity.SingletonUser;

@MyComponent
public class UserService {

    @MyAutowired
    private SingletonUser user1;

    @MyAutowired
    private SingletonUser user2;

    public void userLogin() {
        System.out.println("用户1：" + user1);
        user1.login();
        System.out.println("用户2：" + user2);
        user2.login();

    }
}
