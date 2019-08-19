package com.zhy.web;

import com.alibaba.fastjson.JSON;
import com.zhy.jwt.JWTUtils;
import com.zhy.pojo.ResponseResult;
import com.zhy.pojo.entity.UserInfo;
import com.zhy.service.UserService;
import com.zhy.utils.MD5;
import com.zhy.utils.UID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@RestController
public class EmailController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String from;

    @RequestMapping("sendEmailMessage")
    public ResponseResult sendEmailMessage(@RequestBody Map<String,Object> map){
        ResponseResult responseResult = new ResponseResult();
        String email = map.get("email").toString();
        String loginName = map.get("loginName").toString();
        //String verifyCode = String.valueOf(new Random().nextInt(899999) + 100000);      //获取6位随机验证码
        String uuid16 = UID.getUUID16();
        System.out.println("随机验证码是:"+uuid16);
        UserInfo userInfo = userService.checkEmail(email, loginName);
        if(userInfo != null){           //证明登录名和邮箱验证正确
            MimeMessage message=mailSender.createMimeMessage();
            try {
                //true表示需要创建一个multipart message
                MimeMessageHelper helper=new MimeMessageHelper(message,true);
                helper.setFrom(from);
                helper.setTo(email);
                helper.setSubject("密码重置");
                helper.setText("<html><head></head><body><a href='http://localhost:8080'/>http://localhost:8080/email?code="+uuid16+"</body></html>",true);
                //helper.setText("验证码是:"+verifyCode+",过期时间为5分钟,发送时间为:"+new Date());
                mailSender.send(message);
                System.out.println("html格式邮件发送成功");
                redisTemplate.opsForValue().set(uuid16,loginName);    //往redis存值，后续判断修改账户密码的账户是否对应
                redisTemplate.expire(uuid16,5, TimeUnit.MINUTES);   //设置过期时间
                responseResult.setCode(200);
                responseResult.setSuccess("html格式邮件发送成功");
                return responseResult;
            }catch (Exception e){
                System.out.println("html格式邮件发送失败");
                responseResult.setCode(205);
                responseResult.setSuccess("html格式邮件发送失败");
                return responseResult;
            }
        }
        responseResult.setCode(300);
        responseResult.setError("对不起,登陆名和邮箱不匹配,请核实后再操作...");
        return responseResult;
    }

    @RequestMapping("editPass")
    public ResponseResult editPass(@RequestBody Map<String,String> map){
        ResponseResult responseResult = new ResponseResult();
        String code = map.get("code");
        String password = map.get("password");
        String lcgPwd = MD5.encryptPassword(password, "lcg");
        String username = map.get("username");
        String loginName = redisTemplate.opsForValue().get(code);
        if(loginName.equals(username)){
            userService.udatePwd(username,lcgPwd);
            responseResult.setCode(200);
            responseResult.setSuccess("密码修改成功,请重新登陆...");
        }else {
            responseResult.setCode(500);
            responseResult.setError("对不起,未知错误,请核实账号之后再操作...");
        }
        return responseResult;
    }

    @RequestMapping("checkedEmailVerifyCode")
    public ResponseResult checkedPhoneMessage(@RequestBody Map<String,Object> map){
        String emailYanZhengMa = map.get("emailYanZhengMa").toString();
        String loginName = map.get("loginName").toString();
        ResponseResult responseResult = new ResponseResult();
        String verifyCode = (String) redisTemplate.opsForValue().get(loginName+"emailVerifyCode");
        System.out.println("前台验证码:"+emailYanZhengMa+",从Redis中取得验证码是:"+verifyCode);
        if(verifyCode!=null && verifyCode != ""){
            if(!verifyCode.equals(emailYanZhengMa)){
                responseResult.setCode(445);
                responseResult.setError("对不起,验证码输入不正确,请重新输入...");
                return responseResult;
            }else {                             //走到这一步，证明验证码正确
                responseResult.setCode(200);
                responseResult.setSuccess("验证码匹配成功！^_^");
                return responseResult;
            }
        }else {
            responseResult.setCode(444);
            responseResult.setError("对不起,验证码已过期,请重新发送...");
            return responseResult;
        }
    }
}
