package com.zhy.pojo;

import lombok.Data;

@Data
public class ResponseResult {
    private Integer code;   //返回状态码
    private String error;   //错误信息
    private Object result;  //程序返回结果
    private String success; //成功信息
    private String token;   //登陆成功的标识(这里存储了一些用户的信息)
    private String tokenkey;//用来表示token的一个唯一的字符串
    private Long[] menuIds; //选中的需要回显的菜单ID
}
