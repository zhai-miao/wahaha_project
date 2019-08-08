package com.zhy.filter;

import com.alibaba.fastjson.JSONObject;
import com.zhy.jwt.JWTUtils;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
public class MyGlobalFilter implements GlobalFilter {       //全局的过滤器
    //全局设置直接通过的路径
    @Value("${my.auth.loginPath}")   //登陆的路径
    private String loginPath;
    @Value("${my.auth.urls}")        //其他的一些路径
    private String[] urls;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();  //获取请求
        ServerHttpResponse response = exchange.getResponse();   //获取响应
        String currentPath = request.getURI().toString();   //获取当前的路径

        //验证当前路径是否是公共资源路径也就是不需要进行登录校验的路径
        List<String> strings = Arrays.asList(urls);     //对不需要过滤的路径进行管理
        if(strings.contains(currentPath)){
            return chain.filter(exchange);      //是，则直接放行
        }else{
            List<String> listToken = request.getHeaders().get("token"); //获取请求头的token
            //解密Token校验是否超时，如果超时的话需要重新登录============该步骤是校验Token的
            JSONObject jsonObject = null;   //JSONObject是一个beans,collections,maps,java arrays和xml和JSON互相转换的包
            try{
                //解密判断token是否失效
                jsonObject = JWTUtils.decodeJwtTocken(listToken.get(0));    //获取请求头的第一个token
                //如果没有报错则证明未失效，重新加密登录信息
                String token = JWTUtils.generateToken(jsonObject.toJSONString());
                //重新存在header头里面
                response.getHeaders().set("token",token);
            }catch (JwtException e){
                //报异常则证明已经失效，需要重新登陆，或者token信息是错误的信息，则跳转到登陆页面
                System.out.println("token已经失效或没携带token，需要重新登陆");
                response.getHeaders().set("Location",loginPath);
                response.setStatusCode(HttpStatus.SEE_OTHER);
                return exchange.getResponse().setComplete();
            }
            String userId = jsonObject.get("id").toString();    //获取用户的ID
            System.out.println("当前用户ID是:"+userId+",当前路径是:"+currentPath);
            //重Redis中取值看是否有当前路径的访问权限

            Boolean aBoolean = redisTemplate.opsForHash().hasKey("USERDATAAUTH" + userId, currentPath);
            if(aBoolean){
                return chain.filter(exchange);
            }else {
                throw  new RuntimeException("不能访问该资源 !");
            }
        }
    }
}
