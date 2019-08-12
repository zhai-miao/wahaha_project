package com.zhy.web;

import com.zhy.pojo.ResponseResult;
import com.zhy.pojo.entity.MenuInfo;
import com.zhy.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.util.Date;
import java.util.Map;

@RestController
public class MenuController {
    @Autowired
    private MenuService menuService;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean;

    @RequestMapping("addMenu")
    public ResponseResult addMenu(@RequestBody Map<String,Object> map){
        Long id = 0L;
        Integer leval = Integer.valueOf(map.get("leval").toString());
        Integer parentId = Integer.valueOf(map.get("parentId").toString());
        String menuName = map.get("menuNameadd").toString();
        String url = map.get("url").toString();
        Date date = new Date();
        MenuInfo menuInfo = new MenuInfo();
        menuInfo.setId(id);
        menuInfo.setLeval(leval);
        menuInfo.setMenuName(menuName);
        menuInfo.setParentId(parentId);
        menuInfo.setUrl("http://localhost:10000/"+url);
        menuInfo.setCreateTime(date);
        menuInfo.setUpdateTime(date);
        System.out.println("权限添加测试:"+menuInfo);
        int x = menuService.addMenu(menuInfo);
        ResponseResult responseResult = new ResponseResult();
        if(x >= 1){
            responseResult.setCode(200);
            responseResult.setSuccess("恭喜你,操作成功...");
            return responseResult;
        }
        responseResult.setCode(500);
        responseResult.setSuccess("对不起,操作失败...");
        return responseResult;
    }

    @RequestMapping("deleteMenu")
    public ResponseResult deleteMenu(@RequestBody Map<String,Object> map){
        Long id = Long.valueOf(map.get("id").toString());
        int x = menuService.deleteMenu(id);
        ResponseResult responseResult = new ResponseResult();
        if(x >= 1){
            responseResult.setCode(200);
            responseResult.setSuccess("恭喜你,权限删除操作成功...");
            return responseResult;
        }
        responseResult.setCode(500);
        responseResult.setSuccess("对不起,权限删除操作失败...");
        return responseResult;
    }

    @RequestMapping("updateMenu")
    public ResponseResult updateMenu(@RequestBody MenuInfo menuInfo){
        int x = menuService.addMenu(menuInfo);
        ResponseResult responseResult = new ResponseResult();
        if(x >= 1){
            responseResult.setCode(200);
            responseResult.setSuccess("恭喜你,权限修改操作成功...");
            return responseResult;
        }
        responseResult.setCode(500);
        responseResult.setSuccess("对不起,权限修改操作失败...");
        return responseResult;
    }

}
