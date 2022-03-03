import com.yao2san.SimTaskClietnApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.InvocationTargetException;

@RunWith(SpringRunner.class)
//启动Spring
@SpringBootTest(classes = SimTaskClietnApplication.class)
public class SimTaskTest {

    @Test
    public void test() throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, InterruptedException {
/*new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println(SimTaskExecutors.getState(schedulerFactoryBean, "测试", "DEFAULT"));
                    Thread.sleep(3000);
                    System.out.println("暂停");
                    SimTaskExecutors.suspend(schedulerFactoryBean, "测试", "DEFAULT");
                    System.out.println(SimTaskExecutors.getState(schedulerFactoryBean, "测试", "DEFAULT"));

                    Thread.sleep(10000);
                    System.out.println("恢复");
                    SimTaskExecutors.resume(schedulerFactoryBean, "测试", "DEFAULT");
                    System.out.println(SimTaskExecutors.getState(schedulerFactoryBean, "测试", "DEFAULT"));

                    Thread.sleep(3000);
                    System.out.println("添加");
                    SimTaskExecutors.add(schedulerFactoryBean, "com.yao2san.simtaskclient.test.MainTest", "测试", "DEFAULT", "0/1 * * * * ?");
                    System.out.println(SimTaskExecutors.getState(schedulerFactoryBean, "测试", "DEFAULT"));

                    Thread.sleep(3000);
                    System.out.println("停止");
                    SimTaskExecutors.stop(schedulerFactoryBean, "测试", "DEFAULT");
                    System.out.println(SimTaskExecutors.getState(schedulerFactoryBean, "测试", "DEFAULT"));

                    Thread.sleep(10000);
                    System.out.println("添加");
                    SimTaskExecutors.add(schedulerFactoryBean, "com.yao2san.simtaskclient.test.MainTest", "测试", "DEFAULT", "0/1 * * * * ?");
                    System.out.println(SimTaskExecutors.getState(schedulerFactoryBean, "测试", "DEFAULT"));

                    Thread.sleep(3000);
                    System.out.println("恢复");
                    SimTaskExecutors.resume(schedulerFactoryBean, "测试", "DEFAULT");
                    System.out.println(SimTaskExecutors.getState(schedulerFactoryBean, "测试", "DEFAULT"));

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();*/

    }
}
