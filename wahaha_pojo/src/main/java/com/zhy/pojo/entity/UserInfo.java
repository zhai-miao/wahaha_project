package com.zhy.pojo.entity;

import com.zhy.pojo.base.BaseAuditable;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 作者: LCG
 * 日期: 2019/8/4 15:18
 * 描述: 用户信息描述
 */
@Data
@Entity
@Table(name = "base_user")
public class UserInfo extends BaseAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "version")
    private Long version;

    @Column(name = "userName")
   private String userName;

    @Column(name = "loginName")
   private String loginName;

    @Column(name = "password")
   private String password;

    @Column(name = "tel")
   private String tel;

    @Column(name = "createTime")
    private Date createTime;

    @Column(name = "updateTime")
    private Date updateTime;

    @Column(name = "sex")
   private int sex;

    @Column(name = "parentId")
    private Long parentId;

    @Column(name = "delStatus")
    private Integer delStatus;

    @Column(name = "photoUrl")
    private String photoUrl;

    /*@Transient
    private Integer*/

    @Transient
    private List<MenuInfo> listMenuInfo;

    @Transient
    private RoleInfo roleInfo;
    //private List<RoleInfo> roleInfo;

    @Transient
    private Map<String,String> authmap;

}
