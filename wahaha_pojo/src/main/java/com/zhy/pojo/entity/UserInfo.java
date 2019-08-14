package com.zhy.pojo.entity;

import com.zhy.pojo.base.BaseAuditable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel("这是一个用户的实体类")
public class UserInfo extends BaseAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty("用户的ID")
    private Long id;

    @Column(name = "version")
    @ApiModelProperty("用户的版本")
    private Long version;

    @Column(name = "userName")
    @ApiModelProperty("用户的姓名")
   private String userName;

    @Column(name = "loginName")
    @ApiModelProperty("用户的登录名")
   private String loginName;

    @Column(name = "password")
    @ApiModelProperty("用户的密码")
   private String password;

    @Column(name = "tel")
    @ApiModelProperty("用户的电话")
   private String tel;

    @Column(name = "createTime")
    @ApiModelProperty("用户创建时间")
    private Date createTime;

    @Column(name = "updateTime")
    @ApiModelProperty("用户的修改时间")
    private Date updateTime;

    @Column(name = "sex")
    @ApiModelProperty("用户的性别")
   private int sex;

    @Column(name = "parentId")
    @ApiModelProperty("用户的父ID")
    private Long parentId;

    @Column(name = "delStatus")
    @ApiModelProperty("用户是否有效")
    private Integer delStatus;

    @Column(name = "photoUrl")
    @ApiModelProperty("用户的图表地址")
    private String photoUrl;

    /*@Transient
    private Integer*/

    @Transient
    @ApiModelProperty("用户的权限集合")
    private List<MenuInfo> listMenuInfo;

    @Transient
    @ApiModelProperty("用户对应的角色")
    private RoleInfo roleInfo;
    //private List<RoleInfo> roleInfo;

    @Transient
    @ApiModelProperty("用户的权限集合")
    private Map<String,String> authmap;

}
