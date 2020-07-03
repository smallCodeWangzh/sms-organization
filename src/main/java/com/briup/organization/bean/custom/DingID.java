package com.briup.organization.bean.custom;

import com.briup.organization.util.DingTalkMessage;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Description: 钉钉添加用户获取用户ID，其中字段为钉钉指定字段，不可修改，不必遵守命名规则
 * @Author: GX Cui
 * @Date 10:46 上午 2020/6/29
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class DingID extends DingTalkMessage {
    @ApiModelProperty(value = "钉钉用户编号")
    private String userid;
}
