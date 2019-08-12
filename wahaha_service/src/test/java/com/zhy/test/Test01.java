package com.zhy.test;

import com.zhy.dao.MenuDao;
import com.zhy.pojo.entity.MenuInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class Test01 {
    @Autowired
    private MenuDao menuDao;

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
}