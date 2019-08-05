package com.zhy.web;

import com.alibaba.fastjson.JSON;
import com.zhy.exception.LoginException;
import com.zhy.jwt.JWTUtils;
import com.zhy.pojo.ResponseResult;
import com.zhy.pojo.entity.UserInfo;
import com.zhy.random.VerifyCodeUtils;
import com.zhy.service.UserService;
import com.zhy.utils.MD5;
import com.zhy.utils.UID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
public class SsoController {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserService userService;

    @RequestMapping("login")
    public ResponseResult userLogin(@RequestBody Map<String, Object> map) throws LoginException {
        ResponseResult responseResult = new ResponseResult();
        String code = (String) redisTemplate.opsForValue().get(map.get("codekey").toString());//获取生成的验证码
        if(code==null||!code.equals(map.get("code").toString())){   //作比较
            responseResult.setCode(500);
            responseResult.setError("验证码错误,请重新刷新页面登陆");
            return responseResult;
        }
        if(map.get("loginname")!=null){
            UserInfo user = userService.getUserByLogin(map.get("loginname").toString());
            if(user!=null){
                String password = MD5.encryptPassword(map.get("password").toString(), "lcg");   //对比密码
                if(user.getPassword().equals(password)){
                    String userInfo = JSON.toJSONString(user);
                    String token = JWTUtils.generateToken(userInfo);
                    responseResult.setToken(token);
                    redisTemplate.opsForValue().set("USERINFO"+user.getId().toString(),token);
                    redisTemplate.opsForHash().putAll("USERDATAAUTH"+user.getId().toString(),user.getAuthmap());
                    redisTemplate.expire("USERINFO"+user.getId().toString(),30,TimeUnit.MINUTES);
                    responseResult.setResult(user);
                    responseResult.setCode(200);
                    responseResult.setSuccess("登陆成功！^_^");
                    return responseResult;
                }else {
                    throw new LoginException("用户名或密码错误");
                }
            }else {
                throw new LoginException("用户名或密码错误");
            }
        }
        throw new LoginException("用户名或密码错误");
    }
    @RequestMapping("getCode")
    public ResponseResult getCode(HttpServletResponse response, HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        String code = VerifyCodeUtils.generateVerifyCode(5);    //使用工具类生成一个5位数的code
        ResponseResult responseResult  = new ResponseResult();            //创建自定义类的实例
        responseResult.setResult(code);     //将生成的code存进去自定义类里面

        String uuid16 = "CODE"+UID.getUUID16(); //利用UUID工具生成唯一共识的唯一ID
        redisTemplate.opsForSet().add(uuid16,code); //将UUID和5位随机数存进redis以便后续的验证
        redisTemplate.expire(uuid16,1, TimeUnit.MINUTES);   //存进Redis的值之后，设置保存时间
        Cookie cookie = new Cookie("authcode",uuid16);  //uuid16是存进去Redis的5位随机数的Key值，后续通过获取这个值就可以获取Redis里面的5位随机数，进行验证
        cookie.setPath("/");
        cookie.setDomain("localhost");
        response.addCookie(cookie);

        return responseResult;
    }
}
