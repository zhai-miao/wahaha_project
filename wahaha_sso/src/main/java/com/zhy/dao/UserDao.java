package com.zhy.dao;

import com.zhy.pojo.entity.MenuInfo;
import com.zhy.pojo.entity.RoleInfo;
import com.zhy.pojo.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


/**
 * 作者: LCG
 * 日期: 2019/7/24 09:03
 * 描述:
 */
public interface UserDao extends JpaRepository<UserInfo,Long> {

    @Query(value = "select * from base_user where loginName=?1",nativeQuery = true)
    public UserInfo findByLoginName(String loginName);

    @Query(value = "SELECT * FROM base_role WHERE id in (SELECT roleId FROM base_user_role WHERE userId = ?1)",nativeQuery = true)
    List<RoleInfo> findByUid(Integer uid);

    UserInfo findById(Integer uid);

    @Query(value = "delete from base_user where id = ?1",nativeQuery = true)
    public void delByUidaa(Long uid);

    @Query(value = "SELECT * FROM base_user WHERE tel = ?1",nativeQuery = true)
    public List<UserInfo> findByPhone(String tel);

}
