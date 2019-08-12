package com.zhy.dao;

import com.zhy.pojo.entity.MenuInfo;
import com.zhy.pojo.entity.RoleInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RoleDao extends JpaRepository<RoleInfo,Long> {

    @Query(value = "select br.* from base_user_role bur INNER JOIN base_role br ON bur.roleId=br.id where bur.userId=?1",nativeQuery = true)
    public RoleInfo forRoleInfoByUserId(Long userId);

    @Query(value = "SELECT * FROM base_role role WHERE role.id in (SELECT roleId FROM base_user_role WHERE userId = ?1)",nativeQuery = true)
    public RoleInfo selRoleByUid(Long id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM base_user_role WHERE userId = ?1",nativeQuery = true)
    public int delURByUid(Long uid);    //根据uid删除中间表

    @Transactional
    @Modifying
    @Query(value = "INSERT base_user_role (userId,roleId) VALUES (?1,?2)",nativeQuery = true)
    public int addURByUidRid(Long uid,Long rid);    //通过uid和rid往中间表填值

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM base_role_menu WHERE roleId = ?1",nativeQuery = true)
    public int delRMByRid(Long rid);        //删除中间表

    @Query(value = "SELECT  menuId FROM base_role_menu WHERE roleId = ?1",nativeQuery = true)
    public List<MenuInfo> getMenuIdByRid(Long rid);

    @Transactional
    @Modifying
    @Query(value = "INSERT base_role_menu (roleId,menuId) VALUES (?1,?2)",nativeQuery = true)
    public void addBRM(Long rid,Long mid);        //角色和权限的中间表

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM base_role_menu where roleId = ?1",nativeQuery = true)
    public void delBRMByRid(Long rid);        //修改角色和权限的中间表之前应该先对中间表进行删除操作

}
