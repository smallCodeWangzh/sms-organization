package com.briup.organization.bean.custom;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 接口需要，钉钉端角色实体类
 * @Author: GX Cui
 * @Date 4:35 下午 2020/4/20
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DingRole {
    @ApiModelProperty(value = "角色ID")
    private long id;
    @ApiModelProperty(value = "角色名称")
    private String name;
    @ApiModelProperty(value = "角色组名称")
    private String groupName;
    @ApiModelProperty(value = "文档没写，可能是类型")
    private String type;
}
