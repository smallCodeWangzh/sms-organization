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
 * @Description: 钉钉端成员详情实体
 * @Author: GX Cui
 * @Date 4:25 下午 2020/4/20
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class DingMemberDetail extends DingTalkMessage {
    @ApiModelProperty(value = "员工在当前开发者企业账号范围内的唯一标识，系统生成，固定值，不会改变")
    private String unionid;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "员工在当前企业内的唯一标识，也称staffId。可由企业在创建时指定，并代表一定含义比如工号，创建后不可修改")
    private String userid;
    @ApiModelProperty(value = "在对应的部门中是否为主管：Map结构的json字符串，key是部门的Id，value是人员在这个部门中是否为主管，true表示是，false表示不是")
    private String isLeaderInDepts;
    @ApiModelProperty(value = "入职时间。Unix时间戳 （在OA后台通讯录中的员工基础信息中维护过入职时间才会返回)")
    private long hiredDate;
    @ApiModelProperty(value = "分机号（仅限企业内部开发调用）")
    private String tel;
    @ApiModelProperty(value = "成员所属部门id列表")
    private long[] department;
    @ApiModelProperty(value = "办公地点")
    private String workPlace;
    @ApiModelProperty(value = "员工的电子邮箱")
    private String email;
    @ApiModelProperty(value = "在对应的部门中的排序，Map结构的json字符串，key是部门的Id，value是人员在这个部门的排序值")
    private String orderInDepts;
    @ApiModelProperty(value = "手机号码")
    private String mobile;
    @ApiModelProperty(value = "是否已经激活，true表示已激活，false表示未激活")
    private boolean active;
    @ApiModelProperty(value = "头像url")
    private String avatar;
    @ApiModelProperty(value = "员工工号")
    private String jobnumber;
    @ApiModelProperty(value = "员工名字")
    private String name;
    @ApiModelProperty(value = "扩展属性，可以设置多种属性（手机上最多显示10个扩展属性，具体显示哪些属性，请到OA管理后台->设置->通讯录信息设置和OA管理后台->设置->手机端显示信息设置）。\n" +
            "该字段的值支持链接类型填写，同时链接支持变量通配符自动替换，目前支持通配符有：userid，corpid。示例： [工位地址](http://www.dingtalk.com?userid=#userid#&corpid=#corpid#) ")
    private String extattr;
    @ApiModelProperty(value = "国家地区码")
    private String stateCode;
    @ApiModelProperty(value = "职位信息")
    private String position;
    @ApiModelProperty(value = "用户所在角色列表")
    private DingRole[] roles;

    //以下是争议字段，boolean类型
    @ApiModelProperty(value = "是否为企业的管理员，true表示是，false表示不是")
    private boolean isAdmin;
    @ApiModelProperty(value = "是否号码隐藏，true表示隐藏，false表示不隐藏")
    private boolean isHide;
    @ApiModelProperty(value = "")
    private boolean isSenior;
    @ApiModelProperty(value = "是否为企业的老板，true表示是，false表示不是")
    private boolean isBoss;


}
