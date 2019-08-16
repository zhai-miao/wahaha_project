package com.zhy.service;

import com.zhy.dao.MenuDao;
import com.zhy.dao.RoleDao;
import com.zhy.dao.UserDao;
import com.zhy.pojo.entity.MenuInfo;
import com.zhy.pojo.entity.RoleInfo;
import com.zhy.pojo.entity.UserInfo;
import com.zhy.utils.UID;
import org.apache.commons.beanutils.ConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

@Component
public class UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private MenuDao menuDao;

    public Page<UserInfo> getUserList(Integer currentPage,Integer pageSize,Long userId){
        Page<UserInfo> userList = userDao.findAll(PageRequest.of(currentPage - 1, pageSize, Sort.by(Sort.Order.desc("delStatus"))));
        //Page<UserInfo> userList = userDao.findAll(PageRequest.of(currentPage-1, pageSize));
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
        return userList;
    }

    public int addUser(UserInfo user){
        String loginName = user.getLoginName();
        UserInfo byLoginName = userDao.findByLoginName(loginName);
        if(byLoginName==null){
            long next = UID.next();
            user.setId(next);
            UserInfo save = userDao.save(user);
            if(save!=null){
                return 1;
            }else {
                return 0;
            }
        }
        return 0;
    }

    public int updateUser(UserInfo user) {
        UserInfo save = userDao.save(user);
        if(save!=null){
            return 1;
        }else {
            return 0;
        }
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

    public int delByIdStatus(Long id) {       //逻辑删除
        Long uid = Long.valueOf(id.toString());
        UserInfo userInfo = userDao.findById(uid).get();
        userInfo.setDelStatus(0);
        userDao.save(userInfo);
        return 1;
    }

    public int delById(Long id) {       //物理删除
        Long uid = Long.valueOf(id.toString());
        UserInfo userInfo = userDao.findById(uid).get();
        userDao.delete(userInfo);
        return 1;
    }

    public void delByIds(String[] ids) {
        for(int i = 1;i<ids.length-1;i++){
            //Long uid = Long.valueOf(ids[i]);
            long uid = Long.parseLong(ids[i].substring(1));
            System.out.println("uid");
            UserInfo userInfo = userDao.findById(uid).get();
            userDao.delete(userInfo);
        }
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

    /*public List<MenuInfo> getMenuList(){
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
        return byLevalAndParentId;
    }*/

    public List<MenuInfo> getMenuList(){
        List<MenuInfo> menuList02 = this.getMenuList02(1, 0L);
        System.out.println(menuList02);
        return menuList02;
    }
    public List<MenuInfo> getMenuList02(Integer leval,Long parentCode){
        List<MenuInfo> list = menuDao.findByLevalAndParentId(leval, Integer.valueOf(parentCode.toString()));
        list.forEach(menuInfo -> {
            List<MenuInfo> menuList02 = this.getMenuList02(menuInfo.getLeval() + 1, menuInfo.getId());
            menuInfo.setMenuInfoList(menuList02);
        });
        return list;
    }

    public void getMenuList02(){
        int level = 1;
        List<MenuInfo> byLevalAndParentId = menuDao.findByLevalAndParentId(level, 0);
        for (MenuInfo menuInfo: byLevalAndParentId) {
            List<MenuInfo> byLevalAndParentId1 = menuDao.findByLevalAndParentId(level + 1, menuInfo.getParentId());
            menuInfo.setMenuInfoList(byLevalAndParentId1);

        }
    }

    public UserInfo getUserById(Long userId) {
        UserInfo userInfo = userDao.findById(userId).get();
        return userInfo;
    }



    public void addExcelUser(UserInfo user) {

    }

    public List<UserInfo> selectUserByName(String uname) {
        List<UserInfo> listUser = userDao.selectUserByName(uname);
        return listUser;
    }

    public Page<UserInfo> getUserListByExecel(Integer pageSize, Integer currentPage) {
        Page<UserInfo> userList = userDao.findAll(PageRequest.of(currentPage - 1, pageSize));
        return userList;
    }
}
