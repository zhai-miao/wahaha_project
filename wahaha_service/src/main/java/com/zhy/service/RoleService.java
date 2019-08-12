package com.zhy.service;

import com.zhy.dao.MenuDao;
import com.zhy.dao.RoleDao;
import com.zhy.pojo.entity.MenuInfo;
import com.zhy.pojo.entity.RoleInfo;
import com.zhy.pojo.entity.UserInfo;
import com.zhy.utils.UID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Array;
import java.util.List;

@Component
public class RoleService {
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private MenuDao menuDao;

    public List<RoleInfo> getRoleList() {   //用户列表的更改角色的时候用来获取角色全查
        List<RoleInfo> all = roleDao.findAll();
        return all;
    }

    public int CutRole(Long rid, Long uid) {
        int i = roleDao.delURByUid(uid);    //根据uid删除中间表
        if(i>=0){
            int i1 = roleDao.addURByUidRid(uid, rid);
            return i1;
        }else {
            return 0;
        }
    }

    public Page<RoleInfo> RoleList(Integer currentPage, Integer pageSize) {     //角色列表全查，包括当前页，页面最大值
        Page<RoleInfo> roleList = roleDao.findAll(PageRequest.of(currentPage-1, pageSize));
        for (RoleInfo roleInfo: roleList) {
            Long id = roleInfo.getId();
            List<MenuInfo> menuInfoList = menuDao.selMenuByRid(id);
            roleInfo.setMenuInfoList(menuInfoList);
           List<Long> menuIds = menuDao.getMenuIdByRid(roleInfo.getId());
            Object[] objects = menuIds.toArray();
            roleInfo.setMenuIds(objects);
        }
        return roleList;
    }

    public int delRoleById(Long id) {
        Long rid = Long.valueOf(id.toString());
        RoleInfo roleInfo = roleDao.findById(rid).get();
        roleDao.delete(roleInfo);   //单删角色表
        int i = roleDao.delRMByRid(rid);
        return i;
    }

    public int addRole(RoleInfo roleInfo,Object[] menuIds){
        long next = UID.next();
        roleInfo.setId(next);
        System.out.println("addRole测试:"+roleInfo.getMiaoShu()+roleInfo.getRoleName());
        RoleInfo save = roleDao.saveAndFlush(roleInfo);     //保存
        RoleInfo save1 = roleDao.save(save);
        System.out.println(save1);
        for(Object e : menuIds){                      // 使用foreach语句遍历数组成员
            roleDao.addBRM(save.getId(),Long.valueOf(e.toString()));
        }
        if(save!=null){
            return 1;
        }else {
            return 0;
        }
    }

    public int updateRole(RoleInfo roleInfo, long[] arrMids) {
        RoleInfo save = roleDao.saveAndFlush(roleInfo);     //保存
        roleDao.delBRMByRid(save.getId());      //修改角色和权限的中间表之前应该先对中间表进行删除操作
        for(Long e : arrMids){                      // 使用foreach语句遍历数组成员
            roleDao.addBRM(save.getId(),e);
        }
        if(save!=null){
            return 1;
        }else {
            return 0;
        }

    }

    public int updateRole02(RoleInfo roleInfo) {
        RoleInfo save = roleDao.saveAndFlush(roleInfo);     //保存
        roleDao.delBRMByRid(save.getId());      //修改角色和权限的中间表之前应该先对中间表进行删除操作
        for (Object objects: roleInfo.getMenuIds()) {
            roleDao.addBRM(save.getId(),Long.valueOf(String.valueOf(objects)).longValue());     //把Object类型转为long类型
        }
        if(save!=null){
            return 1;
        }else {
            return 0;
        }
    }
}
