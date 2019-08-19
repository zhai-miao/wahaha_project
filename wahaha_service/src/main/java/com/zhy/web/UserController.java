package com.zhy.web;

import com.zhy.pojo.ResponseResult;
import com.zhy.pojo.entity.MenuInfo;
import com.zhy.pojo.entity.UserInfo;
import com.zhy.service.UserService;
import com.zhy.utils.MD5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean;

    private String imageUrl;

    @RequestMapping("UserById")
    public UserInfo getUserById(@RequestBody Map<String,Object> map){
        Long userId = Long.valueOf(map.get("userId").toString());
        UserInfo userInfo = userService.getUserById(userId);
        return userInfo;
    }

    @RequestMapping("userList")
    public Page<UserInfo> getMenuList(@RequestBody Map<String,Object> map){
        Integer pageSize = Integer.valueOf((Integer) map.get("pageSize"));
        Integer currentPage = Integer.valueOf((Integer) map.get("currentPage"));
        Long userId = Long.valueOf(map.get("userId").toString());
        System.out.println("getMenuList方法..."+pageSize+","+currentPage);
        Page<UserInfo> userList = userService.getUserList(currentPage,pageSize,userId);
        return userList;
    }

    @RequestMapping("addUser")
    public ResponseResult addUser(@RequestBody UserInfo user){
        user.setPhotoUrl(imageUrl);
        String password = user.getPassword();
        String lcgPwd = MD5.encryptPassword(password, "lcg");
        user.setPassword(lcgPwd);
        Date date = new Date();
        user.setCreateTime(date);
        user.setUpdateTime(date);
        int x = userService.addUser(user);
        imageUrl = null;
        ResponseResult responseResult = new ResponseResult();
        if(x >= 1){
            responseResult.setCode(200);
            responseResult.setSuccess("恭喜你,用户添加操作成功...");
            return responseResult;
        }
        responseResult.setCode(500);
        responseResult.setSuccess("对不起,用户添加失败,请换个登录名...");
        return responseResult;
    }

    @RequestMapping("updateUser")
    public ResponseResult updateUser(@RequestBody UserInfo user){
        user.setPhotoUrl(imageUrl);
        Date date = new Date();
        user.setUpdateTime(date);
        int x = userService.updateUser(user);
        imageUrl = null;
        ResponseResult responseResult = new ResponseResult();
        if(x >= 1){
            responseResult.setCode(200);
            responseResult.setSuccess("恭喜你,用户修改操作成功...");
            return responseResult;
        }
        responseResult.setCode(500);
        responseResult.setSuccess("对不起,用户修改操作失败...");
        return responseResult;
    }

    @RequestMapping("delById")
    public int delById(@RequestBody Map<String,Object>map){     //通过ID进行逻辑删除
        if(map.get("id")!=null ){
            Long id = Long.valueOf(map.get("id").toString());
            System.out.println("要删除的ID是:"+id);
            int x = userService.delByIdStatus(id);
            return x;
        }
        if(map.get("ids")!=null){
            String[] ids = map.get("ids").toString().split(",");
            System.out.println("要删除的IDS是:"+ids);
            userService.delByIds(ids);
            return 1;
        }
        return 0;
    }

    @RequestMapping("mohuSelect")
    public Object mohuSelect(@RequestBody Map<String,Object> map){
        EntityManager entityManager = localContainerEntityManagerFactoryBean.getNativeEntityManagerFactory().createEntityManager();
        StringBuffer stringBuffer = new StringBuffer("SELECT * FROM base_user WHERE 1=1 ");
        StringBuffer stringBufferCount = new StringBuffer("SELECT COUNT(*) as amount FROM base_user WHERE 1=1 ");
        if(map.get("userName") != null){
            //条件查询userName
            stringBuffer.append("AND userName like CONCAT('%','"+map.get("userName").toString()+"','%')");
            //总条数
            stringBufferCount.append("AND userName like CONCAT('%','"+map.get("userName").toString()+"','%')");
        }
        if(map.get("sex") != null && map.get("sex").toString()!=""){
            //条件查询userName
            stringBuffer.append("AND sex = "+Integer.valueOf(map.get("sex").toString()));
            //总条数
            stringBufferCount.append("AND sex = "+Integer.valueOf(map.get("sex").toString()));
        }
        Integer pageSize = Integer.valueOf(map.get("pageSize").toString());
        Integer currentPage = Integer.valueOf(map.get("currentPage").toString())-1;
        stringBuffer.append(" LIMIT "+pageSize*currentPage+','+pageSize+"");
        System.out.println("SQL语句是:"+stringBuffer+",数量语句是:"+stringBufferCount);
        //列表全查
        Query nativeQuery = entityManager.createNativeQuery(stringBuffer.toString(), UserInfo.class);
        List<UserInfo> userInfo = nativeQuery.getResultList();
        System.out.println("用户对象是:"+userInfo);
        //总条数
        Query nativeQueryCount = entityManager.createNativeQuery(stringBufferCount.toString());
        System.out.println("总条数:"+nativeQueryCount.getResultList());
        //封装给前台
        Map<String,Object> map02 = new HashMap<>();
        map02.put("content",userInfo);
        map02.put("totalElements",nativeQueryCount.getSingleResult());
        return map02;
    }

    @RequestMapping("toUpLoad")
    public void toUpLoad(@RequestParam("file") MultipartFile file) throws IOException {
        file.transferTo(new File("H:\\photo02\\"+file.getOriginalFilename()));
        imageUrl = "http://localhost:8090/"+file.getOriginalFilename();
        System.out.println(imageUrl);
    }

    @RequestMapping("getMenuList")
    public List<MenuInfo> getMenuListByUid(@RequestBody Map<String,Object> map){
        Long userid = Long.valueOf(map.get("userid").toString());

        List<MenuInfo> menuListByUid = userService.getMenuListByUid(userid);
        return menuListByUid;

        //List<MenuInfo> menuList = userService.getMenuList();      //所有的权限列表
        //return menuList;
    }
}
