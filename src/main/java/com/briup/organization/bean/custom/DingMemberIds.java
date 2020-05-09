package com.briup.organization.bean.custom;

import com.briup.organization.util.DingTalkMessage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Description: 接收钉钉部门下的用户id列表
 * @Author: GX Cui
 * @Date 10:28 上午 2020/4/27
 * @Param
 * @Return
 */
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DingMemberIds extends DingTalkMessage {
    @ApiModelProperty(value = "用户ID列表")
    private String[] userIds;
}
