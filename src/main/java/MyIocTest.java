import context.AnnotationConfigApplicationContext;
import entity.PrototypeUser;
import entity.SingletonUser;

/**
 * @author ah-hui
 */
public class MyIocTest {

    public static void main(String[] args) {
        // 创建上下文对象
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext("entity");
        SingletonUser singletonUser1 = (SingletonUser) ctx.getBean("singletonUser");
        System.out.println("单例User对象" + singletonUser1 + singletonUser1.toJson());
        SingletonUser singletonUser2 = ctx.getBean("singletonUser", SingletonUser.class);
        System.out.println("单例User对象" + singletonUser2 + singletonUser2.toJson());
        PrototypeUser prototypeUser1 = (PrototypeUser) ctx.getBean("prototypeUser");
        System.out.println("原型User对象" + prototypeUser1 + prototypeUser1.toJson());
        PrototypeUser prototypeUser2 = ctx.getBean("prototypeUser", PrototypeUser.class);
        System.out.println("原型User对象" + prototypeUser2 + prototypeUser2.toJson());
        // 销毁
        ctx.close();
    }
}
