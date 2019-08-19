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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
public class SsoController {
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private UserService userService;

    @RequestMapping("login")
    public ResponseResult userLogin(@RequestBody Map<String, Object> map) throws LoginException {
        ResponseResult responseResult = new ResponseResult();
        String code = redisTemplate.opsForValue().get(map.get("codekey").toString());//获取生成的验证码
        System.out.println(code);
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
                    redisTemplate.opsForHash().putAll("USERDATAAUTH"+user.getId(),user.getAuthmap());
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

    @RequestMapping("loginOut")
    public ResponseResult loginOut(@RequestBody Map<String,Object> map){
        String userid = map.get("userid").toString();
        System.out.println("用户ID是:"+userid);
        redisTemplate.delete("USERINFO"+userid.toString());
        redisTemplate.delete("USERDATAAUTH"+userid.toString());
        ResponseResult responseResult = new ResponseResult();
        responseResult.setCode(200);
        return responseResult;
    }
    @RequestMapping("getCode")
    public ResponseResult getCode(HttpServletResponse response, HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        String code = VerifyCodeUtils.generateVerifyCode(5);    //使用工具类生成一个5位数的code
        ResponseResult responseResult  = new ResponseResult();            //创建自定义类的实例
        responseResult.setResult(code);     //将生成的code存进去自定义类里面

        String uuid16 = "CODE"+UID.getUUID16(); //利用UUID工具生成唯一共识的唯一ID
        redisTemplate.opsForValue().set(uuid16,code);//将UUID和5位随机数存进redis以便后续的验证
        redisTemplate.expire(uuid16,5, TimeUnit.MINUTES);   //存进Redis的值之后，设置保存时间
        Cookie cookie = new Cookie("authcode",uuid16);  //uuid16是存进去Redis的5位随机数的Key值，后续通过获取这个值就可以获取Redis里面的5位随机数，进行验证
        cookie.setPath("/");
        cookie.setDomain("localhost");
        response.addCookie(cookie);

        return responseResult;
    }

    @RequestMapping("getxLineData")
    public Map<String,Object> getxLineData(@RequestBody Map<String,Object> map){
        String[] arrNum = new String[7];
        String[] arrDate = new String[7];
        SimpleDateFormat sp=new SimpleDateFormat("yyyy-MM-dd");     //根据时间戳转为正常时间
        String format = sp.format(new Date(System.currentTimeMillis()));    //当天的字符串
        //如果当天没登录，可以设置一个一天更新一次的定时器，往List集合穿个空值，然后登陆次数减一，我没写 0_0
        Integer userId = Integer.valueOf(map.get("userId").toString()); //前台登陆的用户的ID
        //Date date = new Date();                                         //获取当前时间
        //java.sql.Date sqlDate = new java.sql.Date(date.getTime());      //util格式的Date转为sql格式的Date
        if(redisTemplate.opsForValue().get(format) == null){    //如果为空，则证明当天没添加过任何用户登陆的痕迹
            redisTemplate.opsForValue().set(format,"1");        //当天登陆次数初始值为1
            redisTemplate.expire(format,30,TimeUnit.DAYS); //存活时间为1天
            for(int y = 0;y<arrNum.length;y++){                 //遍历最近5天的相关值
                String format1 = redisTemplate.opsForValue().get(format);
                arrNum[y] = format1;
                arrDate[y] = format;
                format = sp.format(new Date(System.currentTimeMillis()-1000*60*60*24*(y+1)));
                System.out.println("数组是:"+arrNum[y]);
                System.out.println("时间是:"+arrDate[y]);
            }

        }else {
            if(redisTemplate.opsForValue().get(userId.toString()+format) == null){    //如果为空，证明该用户改天没登录过
                redisTemplate.opsForValue().set(userId.toString()+format,"1");          //做个相当于当天已登陆的标识
                redisTemplate.expire(userId.toString()+format,1,TimeUnit.DAYS); //存活时间为1天
                Integer num = Integer.valueOf(redisTemplate.opsForValue().get(format)); //获取当天不同用户登陆的次数
                num = num + 1;  //次数+1
                redisTemplate.opsForValue().set(format,num.toString());  //当天的登陆次数加1

                for(int y = 0;y<arrNum.length;y++){
                    String format1 = redisTemplate.opsForValue().get(format);
                    arrNum[y] = format1;
                    arrDate[y] = format;
                    format = sp.format(new Date(System.currentTimeMillis()-1000*60*60*24*(y+1)));
                    System.out.println("数组是:"+arrNum[y]);
                    System.out.println("时间是:"+arrDate[y]);
                }

            }else {
                //该用户今天登录过
                for(int y = 0;y<arrNum.length;y++){
                    String format1 = redisTemplate.opsForValue().get(format);   //今天的时间，返回的是今天的值
                    arrNum[y] = format1;
                    arrDate[y] = format;
                    format = sp.format(new Date(System.currentTimeMillis()-1000*60*60*24*(y+1)));
                    System.out.println("数组是:"+arrNum[y]);
                    System.out.println("时间是:"+arrDate[y]);
                }

            }

        }

        Map<String,Object> map02 = new HashMap<>();
        map02.put("arrNum",arrNum);
        map02.put("arrDate",arrDate);
        return map02;
    }
}
