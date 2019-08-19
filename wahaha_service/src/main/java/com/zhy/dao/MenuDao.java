package com.zhy.dao;

import com.zhy.pojo.entity.MenuInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MenuDao extends JpaRepository<MenuInfo,Long> {

    @Query(value = "select bm.* from base_role_menu brm INNER JOIN base_menu bm ON brm.menuId=bm.id where brm.roleId=?1 and bm.leval=?2 ",nativeQuery = true)
    public List<MenuInfo> getFirstMenuInfo(Long roleId, Integer leval);

    @Query(value = "SELECT * FROM base_menu WHERE leval = ?1 and id in (SELECT menuId FROM base_role_menu WHERE roleId = ?2)",nativeQuery = true)
    public List<MenuInfo> findByLevalAndRoleId(Integer leval, Long roleId);

    //通过角色rid查询当前角色下的所有权限
    @Query(value = "SELECT * FROM base_menu menu WHERE menu.id in (SELECT menuId FROM base_role_menu WHERE roleId = ?1)",nativeQuery = true)
    public List<MenuInfo> selMenuByRid(Long rid);

    public List<MenuInfo> findByLevalAndParentId(int level,int parentId);

    @Transactional
    @Modifying
    //@Query(value = "SELECT  menuId FROM base_role_menu WHERE roleId = ?1",nativeQuery = true)
    @Query(value = "SELECT  id FROM base_menu me WHERE me.leval = 4 and id in (SELECT  menuId FROM base_role_menu rm WHERE rm.roleId = ?1)",nativeQuery = true)
    public List<Long> getMenuIdByRid(Long rid);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM base_role_menu WHERE menuId = ?1",nativeQuery = true)
    public int deleteMenu(Long mid);

    @Query(value = "SELECT * FROM base_menu WHERE id in (?1) and `leval` = ?2",nativeQuery = true)
    public List<MenuInfo> menuByIdAndLeval(List<Long> menuId,int leval);

    @Query(value = "SELECT * FROM base_menu WHERE id in (?1) and `leval` = ?2 and parentId = ?3",nativeQuery = true)
    public List<MenuInfo> byIdAndLevalAndParentId(List<Long> menuId,int leval,int parentId);

}
