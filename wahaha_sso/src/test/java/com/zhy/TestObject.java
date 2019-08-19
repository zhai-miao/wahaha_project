package com.zhy;

import com.zhy.dao.UserDao;
import com.zhy.pojo.entity.UserInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestObject {

    @Autowired
    private LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private UserDao userDao;
    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String from;

    @Test
    public void test05(){
        //UserInfo zhangsan = userDao.findByLoginNameAndEmail("zhangsan", "565663762@qq.com");
        SimpleDateFormat sp=new SimpleDateFormat("yyyy-MM-dd");
        String format = sp.format(new Date(System.currentTimeMillis()-1000*60*60*24*1));
        redisTemplate.opsForValue().set(format,"3");
        //System.out.println("返回的数据是:"+zhangsan);
    }

    @Test
    public void test06() throws FileNotFoundException {

        MimeMessage message=mailSender.createMimeMessage();
        try {
            //true表示需要创建一个multipart message
            MimeMessageHelper helper=new MimeMessageHelper(message,true);
            helper.setFrom(from);
            helper.setTo("565663762@qq.com");
            helper.setSubject("密码重置");
            helper.setText("<html><head></head><body><a href='https://localhost:8080'/>https://localhost:8080</body></html>",true);
            mailSender.send(message);
            System.out.println("html格式邮件发送成功");
        }catch (Exception e){
            System.out.println("html格式邮件发送失败");
        }
    }

    @Test
    public void test01(){
        EntityManager entityManager = localContainerEntityManagerFactoryBean.getNativeEntityManagerFactory().createEntityManager();

        Query nativeQueryCount = entityManager.createNativeQuery("SELECT COUNT(*) as amount FROM base_user WHERE 1=1");

        System.out.println(nativeQueryCount.getSingleResult());
    }

    @Test
    public void test02(){
        Date date = new Date();                                         //获取当前时间
        Date d=new Date(System.currentTimeMillis()-1000*60*60*24);
        SimpleDateFormat sp=new SimpleDateFormat("yyyy-MM-dd");
        String ZUOTIAN=sp.format(d);
        /*for (int i = 2;i<7;i++){
            date-(1000*60*60*24);
        }*/
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        System.out.println("当前时间是:"+date);
        System.out.println("sqlDate是:"+sqlDate);
    }

    @Test
    public void test03(){
        Date date = new Date();
        SimpleDateFormat sp=new SimpleDateFormat("yyyy-MM-dd");
        //System.currentTimeMillis()-1000*60*60*24;
        for (int i = 5;i<9;i++){
            String format = sp.format(new Date(System.currentTimeMillis()-1000*60*60*24*i));
            redisTemplate.opsForValue().set(format,"3");
        }
    }

    @Test
    public void test04(){
        String[] arrNum = new String[7];
        String[] arrDate = new String[7];
        SimpleDateFormat sp=new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0;i<7;i++){
            String format = sp.format(new Date(System.currentTimeMillis()-1000*60*60*24*i));
            String format1 = redisTemplate.opsForValue().get(format);
            arrNum[i] = format1;
            arrDate[i] = format;
        }
        for(int y = 0;y<arrNum.length;y++){
            System.out.println("数组是:"+arrNum[y]);
            System.out.println("时间是:"+arrDate[y]);
        }

    }
}
