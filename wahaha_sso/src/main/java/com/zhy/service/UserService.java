package com.zhy.service;

import com.zhy.dao.MenuDao;
import com.zhy.dao.RoleDao;
import com.zhy.dao.UserDao;
import com.zhy.pojo.entity.MenuInfo;
import com.zhy.pojo.entity.RoleInfo;
import com.zhy.pojo.entity.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;


/**
 * 作者: LCG
 * 日期: 2019/7/24 09:08
 * 描述:
 */
@Component
public class UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private MenuDao menuDao;

    public Page<UserInfo> getUserList(Integer currentPage,Integer pageSize){
        Page<UserInfo> userList = userDao.findAll(PageRequest.of(currentPage-1, pageSize));
        return userList;
    }

    public UserInfo getUserByLogin(String loginName){
        //获取用户信息
        UserInfo byLoginName = userDao.findByLoginName(loginName);
        if(byLoginName!=null){
            //获取用户的角色信息
            RoleInfo roleInfoByUserId = roleDao.forRoleInfoByUserId(byLoginName.getId());
            //List<RoleInfo> list = null;
            //设置用户的角色信息
            //list.add(roleInfoByUserId);
            byLoginName.setRoleInfo(roleInfoByUserId);

            if(roleInfoByUserId!=null){
               //获取用户的权限信息
               List<MenuInfo> firstMenuInfo = menuDao.getFirstMenuInfo(roleInfoByUserId.getId(), 1);
               //递归的查询子菜单权限
               Map<String,String> authMap=new Hashtable<>();
               this.getForMenuInfo(firstMenuInfo,roleInfoByUserId.getId(),authMap);
               //设置菜单的子权限
               byLoginName.setAuthmap(authMap);
               byLoginName.setListMenuInfo(firstMenuInfo);
            }
        }
        return byLoginName;
    }
    /**
     * 获取子权限的递归方法
     * @param firstMenuInfo
     * @param roleId
     */
    public void getForMenuInfo(List<MenuInfo> firstMenuInfo,Long roleId,Map<String,String> authMap){

        for(MenuInfo menuInfo:firstMenuInfo){
            int leval=menuInfo.getLeval() + 1;
            //获取下级的菜单信息
            List<MenuInfo> firstMenuInfo1 = menuDao.getFirstMenuInfo(roleId, leval);
            if(firstMenuInfo1!=null){
                //整理后台的数据访问链接
                if(leval==4){
                    for(MenuInfo menu:firstMenuInfo1){
                        authMap.put(menu.getUrl(),"");
                    }
                }
                //设置查出来的菜单到父级对象中
                menuInfo.setMenuInfoList(firstMenuInfo1);
                //根据查出来的下级菜单继续查询该菜单包含的子菜单
                getForMenuInfo(firstMenuInfo1,roleId,authMap);
            }else{
                break;
            }
        }
    }

    public int delById(Integer id) {
        Long uid = Long.valueOf(id.toString());
        UserInfo userInfo = userDao.findById(uid).get();
        userDao.delete(userInfo);
        return 1;
    }

    /*public UserInfo getUserByUid(Integer uid) {       //自写的用户角色一对多的方法
        UserInfo userById = userDao.findById(uid);
        if(userById!=null){
            //获取用户的角色信息
            List<RoleInfo> RoleList = userDao.findByUid(uid);
            //设置用户的角色信息
            userById.setRoleInfo(RoleList);
            if(RoleList!=null){
                for (RoleInfo roleInfo: RoleList) {
                    List<MenuInfo> byLevalAndRoleId = menuDao.findByLevalAndRoleId(1, roleInfo.getId());
                    //递归的查询子菜单权限
                    Map<String,String> authMap = new Hashtable<>();
                    this.findByLevalAndRoleId02(authMap,byLevalAndRoleId,roleInfo.getId());
                    userById.setAuthmap(authMap);
                    userById.setListMenuInfo(byLevalAndRoleId);
                }
            }
        }
        return userById;
    }
    public void findByLevalAndRoleId02(Map<String,String> authMap,List<MenuInfo> byLevalAndRoleId,Long roleId){
        for (MenuInfo menuInfo: byLevalAndRoleId) {
            int level = menuInfo.getLeval()+1;  //下级权限
            List<MenuInfo> byLevalAndRoleId02 = menuDao.findByLevalAndRoleId(level, roleId);
            if(byLevalAndRoleId02!=null){
                menuInfo.setMenuInfoList(byLevalAndRoleId02);
                findByLevalAndRoleId02(authMap,byLevalAndRoleId02,roleId);
            }else{
                break;
            }
        }
    }*/
}
