package com.zhy.web;

import com.zhy.pojo.entity.MenuInfo;
import com.zhy.pojo.entity.UserInfo;
import com.zhy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private UserService userService;

    @RequestMapping("userList")
    public Page<UserInfo> getMenuList(@RequestBody Map<String,Object> map){
        Integer pageSize = Integer.valueOf((Integer) map.get("pageSize"));
        Integer currentPage = Integer.valueOf((Integer) map.get("currentPage"));
        System.out.println("getMenuList方法..."+pageSize+","+currentPage);
        Page<UserInfo> userList = userService.getUserList(currentPage,pageSize);
        return userList;
    }

    @RequestMapping("delById")
    public int delById(@RequestBody Map<String,Object>map){
        Integer id = Integer.valueOf(map.get("id").toString());
        System.out.println("要删除的ID是:"+id);
        int x = userService.delById(id);
        return x;
    }

    @Autowired
    private LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean;
    @RequestMapping("mohuSelect")
    public Object mohuSelect(@RequestBody Map<String,Object> map){
        EntityManager entityManager = localContainerEntityManagerFactoryBean.getNativeEntityManagerFactory().createEntityManager();
        StringBuffer stringBuffer = new StringBuffer("SELECT * FROM base_user WHERE 1=1 ");
        StringBuffer stringBufferCount = new StringBuffer("SELECT COUNT(*) FROM base_user WHERE 1=1 ");
        if(map.get("userName") != null){
            //条件查询userName
            stringBuffer.append("AND userName like CONCAT('%','"+map.get("userName").toString()+"','%')");
            //总条数
            stringBufferCount.append("AND userName like CONCAT('%','"+map.get("userName").toString()+"','%')");
        }
        Integer pageSize = Integer.valueOf(map.get("pageSize").toString());
        Integer currentPage = Integer.valueOf(map.get("currentPage").toString())-1;
        stringBuffer.append("LIMIT "+pageSize*currentPage+','+pageSize+"");
        System.out.println("SQL语句是:"+stringBuffer+",数量语句是:"+stringBufferCount);
        //列表全查
        Query nativeQuery = entityManager.createNativeQuery(stringBuffer.toString(), UserInfo.class);
        //总条数
        Query nativeQueryCount = entityManager.createQuery(stringBufferCount.toString(), Long.class);
        //封装给前台
        Map<String,Object> map02 = new HashMap<>();
        UserInfo userInfo = (UserInfo) nativeQuery.getResultList().get(0);
        System.out.println("返回测试"+userInfo);
        map02.put("content",nativeQuery.getResultList().get(0));
        map02.put("totalElements",nativeQueryCount.getResultList());
        //Integer.valueOf(map.get("pageSize").toString());

        return map02;
    }
}
