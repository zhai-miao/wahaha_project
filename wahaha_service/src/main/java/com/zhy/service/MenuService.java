package com.zhy.service;

import com.zhy.dao.MenuDao;
import com.zhy.dao.RoleDao;
import com.zhy.pojo.entity.MenuInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MenuService {
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private MenuDao menuDao;

    public int addMenu(MenuInfo menuInfo) {
        MenuInfo save = menuDao.save(menuInfo);
        if(save!=null){
            return 1;
        }
        return 0;
    }

    public int deleteMenu(Long id) {
        menuDao.deleteById(id);     //删除权限表
        menuDao.deleteMenu(id);//删除中间表
        return 1;
    }
}
