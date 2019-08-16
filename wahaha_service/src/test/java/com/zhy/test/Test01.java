package com.zhy.test;

import com.zhy.dao.MenuDao;
import com.zhy.dao.RoleDao;
import com.zhy.dao.UserDao;
import com.zhy.pojo.entity.MenuInfo;
import com.zhy.pojo.entity.RoleInfo;
import com.zhy.pojo.entity.UserInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Random;

@SpringBootTest
@RunWith(SpringRunner.class)
public class Test01 {
    @Autowired
    private MenuDao menuDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private RoleDao roleDao;

    @Test
    public void test05(){
        Page<UserInfo> userList = userDao.findAll(PageRequest.of(1, 5, Sort.by(Sort.Order.desc("id"))));
        for (UserInfo userInfo: userList) {
            Long id = userInfo.getId();
            RoleInfo roleInfo = roleDao.selRoleByUid(id);
            if(roleInfo!=null){
                Long rid = roleInfo.getId();
                List<MenuInfo> menuInfoList = menuDao.selMenuByRid(rid);
                roleInfo.setMenuInfoList(menuInfoList);

                userInfo.setRoleInfo(roleInfo);
            }else {
                continue;
            }
        }
        for (UserInfo userInfo: userList) {
            System.out.println(userInfo);
        }
    }

    @Test
    public void test03(){
        List<MenuInfo> menuList02 = this.getMenuList02(1, 0L);
        System.out.println(menuList02);
    }
    public List<MenuInfo> getMenuList02(Integer leval,Long parentCode){
        List<MenuInfo> list = menuDao.findByLevalAndParentId(leval, Integer.valueOf(parentCode.toString()));
        list.forEach(menuInfo -> {
            List<MenuInfo> menuList02 = this.getMenuList02(menuInfo.getLeval() + 1, menuInfo.getId());
            menuInfo.setMenuInfoList(menuList02);
        });
        return list;
    }
    public void test02(List<MenuInfo> byLevalAndParentId){
        for (MenuInfo menuInfo: byLevalAndParentId) {
            List<MenuInfo> byLevalAndParentId1 = menuDao.findByLevalAndParentId(menuInfo.getLeval()+1, Integer.valueOf(menuInfo.getId().toString()));
            menuInfo.setMenuInfoList(byLevalAndParentId1);
        }
    }

    @Test
    public void test01(){
        long i = 1;
        List<Long> menuId = menuDao.getMenuIdByRid(i);
        System.out.println(menuId);
        /*Integer[] mid = menuId.toArray(new Integer[0]);
        System.out.println(mid);*/
    }

    @Test
    public void getMenuList(){
        List<MenuInfo> byLevalAndParentId = menuDao.findByLevalAndParentId(1, 0);
        for (MenuInfo menuInfo: byLevalAndParentId) {
            List<MenuInfo> byLevalAndParentId1 = menuDao.findByLevalAndParentId(2, 1);
            menuInfo.setMenuInfoList(byLevalAndParentId1);
            for (MenuInfo menuInfo1:
            byLevalAndParentId1) {
                List<MenuInfo> byLevalAndParentId2 = menuDao.findByLevalAndParentId(3, Integer.valueOf(menuInfo1.getId().toString()));
                menuInfo1.setMenuInfoList(byLevalAndParentId2);
                for (MenuInfo menuInfo2: byLevalAndParentId2) {
                    List<MenuInfo> byLevalAndParentId3 = menuDao.findByLevalAndParentId(4, Integer.valueOf(menuInfo2.getId().toString()));
                    menuInfo2.setMenuInfoList(byLevalAndParentId3);
                }
            }
        }

        System.out.println(byLevalAndParentId);
    }

    @Test
    public void verifyCode(){
        String verifyCode = String.valueOf(new Random().nextInt(899999) + 100000);
        System.out.println("验证码:"+verifyCode);
    }
}
