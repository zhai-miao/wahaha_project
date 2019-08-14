package com.zhy.pojo.entity;

import com.zhy.pojo.base.BaseAuditable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 作者: LCG
 * 日期: 2019/8/4 16:30
 * 描述:
 */
@Entity
@Data
@Table(name = "base_role")
@ApiModel("这是一个角色的实体类")
public class RoleInfo extends BaseAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty("角色的ID")
    private Long id;
    public Long getId() {
        return id;
    }

    @Column(name = "roleName")
    @ApiModelProperty("角色的名称")
    private String roleName;

    @Column(name = "miaoShu")
    @ApiModelProperty("角色的描述")
    private String miaoShu;

    @Column(name = "createTime")
    @ApiModelProperty("角色的创建时间")
    private Date createTime;

    @Column(name = "updateTime")
    @ApiModelProperty("角色的修改时间")
    private Date updateTime;

    @Transient
    @ApiModelProperty("角色所拥有的权限的集合")
    private List<MenuInfo> menuInfoList;

    @Transient
    @ApiModelProperty("角色所拥有的权限的ID")
    private Object[] menuIds;

}
