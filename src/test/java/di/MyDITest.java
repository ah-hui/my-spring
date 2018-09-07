package di;

import annotation.MyComponent;
import context.AnnotationConfigApplicationContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import service.UserService;

@MyComponent
public class MyDITest {

    /**
     * 上下文对象
     */
    AnnotationConfigApplicationContext ctx;
    /**
     * userService
     */
    UserService userService;

    @Before
    public void init() {
        ctx = new AnnotationConfigApplicationContext("entity", "service", "di");
        userService = ctx.getBean("userService", UserService.class);
    }

    @Test
    public void test() {
        userService.userLogin();
    }

    @After
    public void close() {
        ctx.close();
    }
}
