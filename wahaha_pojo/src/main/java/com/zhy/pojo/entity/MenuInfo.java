package com.zhy.pojo.entity;

import com.zhy.pojo.base.BaseAuditable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * 作者: LCG
 * 日期: 2019/8/4 16:30
 * 描述:
 */
@Entity
@Data
@Table(name = "base_menu")
@ApiModel("这是一个权限的实体类")
public class MenuInfo extends BaseAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty("权限的ID")
    private Long id;

    @Column(name = "menuName")
    @ApiModelProperty("权限的名称")
    private String menuName;

    @Column(name = "parentId")
    @ApiModelProperty("本权限的父级权限的ID")
    private int parentId;

    @Column(name = "leval")
    @ApiModelProperty("本权限的权限等级")
    private int leval;

    @Column(name = "url")
    @ApiModelProperty("本权限的访问路径")
    private String url;

    @Transient
    @ApiModelProperty("本权限的子权限集合")
    private List<MenuInfo> menuInfoList;

}
