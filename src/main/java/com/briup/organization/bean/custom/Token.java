package com.briup.organization.bean.custom;

import com.briup.organization.util.DingTalkMessage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Description: 从钉钉接口中获取的令牌实体类
 * 该实体类字段必须按照钉钉官方文档返回格式为准，不必遵守命名规范
 * 官方文档地址：https://ding-doc.dingtalk.com/doc#/serverapi2/eev437
 * @Author: GX Cui
 * @Date 3:34 下午 2020/4/20
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@ApiModel
public class Token extends DingTalkMessage {
    @ApiModelProperty(value = "通行证")
    private String access_token;
    @ApiModelProperty(value = "in")
    private int expires_in;
}
