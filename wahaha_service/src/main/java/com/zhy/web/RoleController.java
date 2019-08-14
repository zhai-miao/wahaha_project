package com.zhy.web;

import com.zhy.pojo.entity.RoleInfo;
import com.zhy.pojo.entity.UserInfo;
import com.zhy.service.RoleService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
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
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class RoleController {
    @Autowired
    private RoleService roleService;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean;

    @RequestMapping("getRoleList")      //用户列表的更改角色的时候用来获取角色全查
    public List<RoleInfo> getRoleList(){
        List<RoleInfo> roleList = roleService.getRoleList();
        return roleList;
    }

    @RequestMapping("RoleList")     //角色列表全查，包括当前页，页面最大值
    public Page<RoleInfo> RoleList(@RequestBody Map<String,Object> map){
        Integer pageSize = Integer.valueOf((Integer) map.get("pageSize"));
        Integer currentPage = Integer.valueOf((Integer) map.get("currentPage"));
        System.out.println("RoleList方法..."+pageSize+","+currentPage);
        Page<RoleInfo> userList = roleService.RoleList(currentPage,pageSize);
        return userList;
    }

    /*@RequestMapping("menuList")
    public Object[] getMenuId(@RequestBody Map<String,Object> map){
        Integer rid = Integer.valueOf(map.get("id").toString());
        List<Long> longList = roleService.getMids(rid);
        Object[] objects = longList.toArray();
        System.out.println(longList);
        return objects;
    }*/

    @RequestMapping("addRole")
    public int  addRole(@RequestBody RoleInfo roleInfo) throws Exception {      //角色添加
        Object[] menuIds = roleInfo.getMenuIds();
        for (int i = 0;i<menuIds.length;i++){
            System.out.println(menuIds[i]);
        }
        //因为在RoleInfo实体类中权限ID是Object类型，而数据库是long类型，所以需要在后续进行权限的更改
        Date date = new Date();
        roleInfo.setCreateTime(date);
        roleInfo.setUpdateTime(date);
        System.out.println(roleInfo);
        return roleService.addRole(roleInfo, menuIds);
    }
/*
    @RequestMapping("addRole")
    public int  addRole(@RequestBody Map<String,Object> map) throws Exception {      //角色添加
        String[] arr = map.get("menuIds").toString().split(",");
        String miaoShu = map.get("miaoShu").toString();
        String roleName = map.get("roleName").toString();
        //因为在RoleInfo实体类中权限ID是Object类型，而数据库是long类型，所以需要在后续进行权限的更改
        long[] arrMids = (long[]) ConvertUtils.convert(arr,long.class);
        RoleInfo roleInfo = new RoleInfo();
        BeanUtils.populate(roleInfo, (Map) map.get("dataRole"));    //利用BeanUtils的工具类对map中的值进行取值
        Date date = new Date();
        roleInfo.setCreateTime(date);
        roleInfo.setUpdateTime(date);
        roleInfo.setMiaoShu(miaoShu);
        roleInfo.setRoleName(roleName);
        System.out.println(roleInfo);
        return roleService.addRole(roleInfo, arrMids);
    }
*/

    @RequestMapping("updateRole")
    public int updateRole(@RequestBody RoleInfo roleInfo){       //角色修改
        return roleService.updateRole02(roleInfo);
    }
/*
    @RequestMapping("updateRole")
    public int updateRole(@RequestBody Map<String,Object> map) throws Exception{       //角色修改
        String[] arr = map.get("arrMids").toString().split(",");
        //因为在RoleInfo实体类中权限ID是Object类型，而数据库是long类型，所以需要在后续进行权限的更改
        long[] arrMids = (long[]) ConvertUtils.convert(arr,long.class);
        RoleInfo roleInfo = new RoleInfo();
        Date date = new Date();
        roleInfo.setCreateTime(date);
        roleInfo.setUpdateTime(date);
        Object[] amm = new Object[0];
        roleInfo.setMenuIds(amm);
        BeanUtils.populate(roleInfo, (Map) map.get("dataRole"));    //利用BeanUtils的工具类对map中的值进行取值
        return roleService.updateRole(roleInfo, arrMids);
    }
*/

    @RequestMapping("mohuRole")     //角色信息模糊查询
    public Object mohuSelect(@RequestBody Map<String,Object> map){
        EntityManager entityManager = localContainerEntityManagerFactoryBean.getNativeEntityManagerFactory().createEntityManager();
        StringBuffer stringBuffer = new StringBuffer("SELECT * FROM base_role WHERE 1=1 ");
        StringBuffer stringBufferCount = new StringBuffer("SELECT COUNT(*) as amount FROM base_role WHERE 1=1 ");
        if(map.get("roleName") != null){
            //条件查询roleName
            stringBuffer.append("AND roleName like CONCAT('%','"+map.get("roleName").toString()+"','%')");
            //总条数
            stringBufferCount.append("AND roleName like CONCAT('%','"+map.get("roleName").toString()+"','%')");
        }
        Integer pageSize = Integer.valueOf(map.get("pageSize").toString());
        Integer currentPage = Integer.valueOf(map.get("currentPage").toString())-1;
        stringBuffer.append(" LIMIT "+pageSize*currentPage+','+pageSize+"");
        System.out.println("SQL语句是:"+stringBuffer+",数量语句是:"+stringBufferCount);
        //列表全查
        Query nativeQuery = entityManager.createNativeQuery(stringBuffer.toString(), RoleInfo.class);
        List<RoleInfo> roleInfo = nativeQuery.getResultList();
        System.out.println("角色对象是:"+roleInfo);
        //总条数
        Query nativeQueryCount = entityManager.createNativeQuery(stringBufferCount.toString());
        System.out.println("总条数:"+nativeQueryCount.getResultList());
        //封装给前台
        Map<String,Object> map02 = new HashMap<>();
        map02.put("content",roleInfo);
        map02.put("totalElements",nativeQueryCount.getSingleResult());
        return map02;
    }

    @RequestMapping("CutRole")      //用户列表的更改角色的时候
    public int CutRole(@RequestBody Map<String,Object> map){
        System.out.println("角色切换...");
        Long rid = Long.valueOf(map.get("rid").toString());
        Long uid = Long.valueOf(map.get("uid").toString());
        System.out.println("RID是:"+rid+",UID是:"+uid);
        int i = roleService.CutRole(rid, uid);
        return i;
    }

    @RequestMapping("delRoleById")
    public int delRoleById(@RequestBody Map<String,Object>map){
        Long id = Long.valueOf(map.get("id").toString());
        System.out.println("要删除的ID是:"+id);
        int x = roleService.delRoleById(id);        //删除权限的同时要删除中间表
        return 1;
    }
}
