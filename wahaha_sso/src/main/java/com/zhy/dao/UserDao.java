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


}
