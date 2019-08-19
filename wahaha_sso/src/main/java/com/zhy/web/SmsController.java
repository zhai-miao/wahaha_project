package com.zhy.web;


import com.alibaba.fastjson.JSON;
import com.zhy.jwt.JWTUtils;
import com.zhy.pojo.ResponseResult;
import com.zhy.pojo.entity.UserInfo;
import com.zhy.service.UserService;
import com.zhy.utils.HttpUtils;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@RestController
public class SmsController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping("sendPhoneMessage")
    public void sendPhoneMessage(@RequestBody Map<String,Object> map){
        String verifyCode = String.valueOf(new Random().nextInt(899999) + 100000);      //获取6位随机验证码
        System.out.println("6位随机验证码:"+verifyCode);
        String phoneTel = map.get("tel").toString();
        System.out.println("手机号是:"+phoneTel);
        //手机号
        String host = "http://dingxin.market.alicloudapi.com";
        String path = "/dx/sendSms";
        String method = "POST";
        String appcode = "02327eaece0b43bd8c358b19ef1bda34";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("mobile", phoneTel);
        querys.put("param", "code:"+verifyCode);
        querys.put("tpl_id", "TP1711063");
        Map<String, String> bodys = new HashMap<String, String>();
        try {
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            redisTemplate.opsForValue().set("Phone:"+phoneTel,verifyCode);                //往Redis里存值
            redisTemplate.expire("Phone:"+phoneTel,120, TimeUnit.SECONDS);        //设置超时时间
            System.out.println(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("checkedPhoneMessage")
    public ResponseResult checkedPhoneMessage(@RequestBody Map<String,Object> map){
        String phoneTel = map.get("tel").toString();
        String yanZhengMa = map.get("yanZhengMa").toString();
        ResponseResult responseResult = new ResponseResult();
        String verifyCode = (String) redisTemplate.opsForValue().get("Phone:"+phoneTel);
        System.out.println("前台验证码:"+yanZhengMa+",从Redis中取得验证码是:"+verifyCode);
        if(verifyCode!=null && verifyCode != ""){
            if(!verifyCode.equals(yanZhengMa)){
                responseResult.setCode(445);
                responseResult.setError("对不起,验证码输入不正确,请重新输入...");
                return responseResult;
            }else {                             //走到这一步，证明验证码正确
                //因为是自己做，手机号也没辙唯一性，所以后台根据手机号的话只要第一个用户
                UserInfo user = userService.getUserByTel(phoneTel);
                String userInfo = JSON.toJSONString(user);
                String token = JWTUtils.generateToken(userInfo);
                responseResult.setToken(token);
                redisTemplate.opsForValue().set("USERINFO"+user.getId().toString(),token);
                redisTemplate.opsForHash().putAll("USERDATAAUTH"+user.getId(),user.getAuthmap());
                redisTemplate.expire("USERINFO"+user.getId(),30,TimeUnit.MINUTES);
                responseResult.setResult(user);
                responseResult.setCode(200);
                responseResult.setSuccess("登陆成功！^_^");
                return responseResult;
            }
        }else {
            responseResult.setCode(444);
            responseResult.setError("对不起,验证码已过期,请重新发送...");
            return responseResult;
        }

    }
}