package com.briup.organization.service.impl;

import com.briup.organization.bean.*;
import com.briup.organization.bean.custom.DingID;
import com.briup.organization.bean.custom.DingMemberDetail;
import com.briup.organization.bean.custom.DingMemberIds;
import com.briup.organization.exception.CustomerException;
import com.briup.organization.mapper.DeparmentMapper;
import com.briup.organization.mapper.DeparmentMemberMapper;
import com.briup.organization.mapper.MemberMapper;
import com.briup.organization.mapper.ex.MemberExMapper;
import com.briup.organization.service.MemberService;
import com.briup.organization.util.*;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.*;
import com.dingtalk.api.response.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taobao.api.ApiException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @Description: 成员接口实现
 * @Author: GX Cui
 * @Date 3:52 下午 2020/4/20
 */
@Service
public class MemberImpl implements MemberService {


    @Resource
    private DeparmentMapper deptMapper;

    @Resource
    private MemberMapper memberMapper;

    @Resource
    private MemberExMapper memberExMapper;

    @Resource
    private DeparmentMemberMapper dmmapper;

    //准备集合用来批量插入(暂未用)
    List<Member> memberList = new LinkedList<>();
    //准备计数（暂未用）
    int count = 0;

    @Override
    public void memberDataSynchronization() throws CustomerException {

        //同步前清空成员表（可以选择是否清空表）
        memberExMapper.truncateTable("tb_member");

        //获取部门列表
        List<Deparment>  deptlist =  deptMapper.selectByExample(new DeparmentExample());
        //准备二维数组接收所有用户id
        List<List<String>> allIdValue = new ArrayList<>();
        //准备一个map来接收部门id和对应的用户id的集合
        Map<Long,List<String>> dmmap = new HashMap<>();


        //遍历部门列表拿到部门id
        for (Deparment deparment : deptlist){

            String result = getUserIdListByDeptId(String.valueOf(deparment.getId()));
            //解析
            ObjectMapper mapper = new ObjectMapper();
            DingMemberIds ids = null;
            try {
                ids = mapper.readValue(result, DingMemberIds.class);
                //拿到所有的id
                List<String> idArr = Arrays.asList(ids.getUserIds());
                allIdValue.add(idArr);
                dmmap.put(deparment.getId(),idArr);
            } catch (JsonProcessingException e) {
                throw new CustomerException(CodeStatus.JSON_PARSING_ERROR);
            }
        }

        //遍历id，拿到用用户详情
        for (List<String> a: allIdValue) {
            //for(int i = 0;i<1000;i++){
                //String id = a.get(i);
            for (String id : a){
                //先判断再调用，不然会有调用次数限制，先和数据库进行比对，再进行接口调用
                Member m =  memberMapper.selectByPrimaryKey(id);
                if(m!=null){
                    continue;
                }else {
                    String temp = getMemberDetailByUserId(id);
                    ObjectMapper mapper = new ObjectMapper();
                    DingMemberDetail detail = null;
                    try {
                        detail = mapper.readValue(temp, DingMemberDetail.class);

                        //设置加入数据库中的数据，5月1日cgx修改表，去掉id自动增长
                        Member member = new Member();
                        member.setId(detail.getUserid()); //成员id
                        member.setName(detail.getName()); //成员名字
                        //判断是否有邮箱
                        if (detail.getEmail() == null) {
                            member.setEmail("未开通邮箱"); //企业邮箱
                        } else {
                            member.setEmail(detail.getEmail()); //企业邮箱
                        }
                        member.setJobNumber(detail.getJobnumber()); //工号
                        //判断是否显示手机号
                        if (detail.isHide()) {
                            member.setPhone(detail.getTel()); //手机号码
                        } else {
                            member.setPhone("号码已隐藏"); //手机号码
                        }
                        //判断是否有角色，多个角色只展示第一个
                        if (detail.getRoles() == null || detail.getRoles().length == 0) {
                            member.setRoleName("无角色");
                        } else {
                            member.setRoleName(detail.getRoles()[0].getName()); //角色名，角色名不止一个，如果有多个显示第一个
                        }
                        member.setPosition(detail.getPosition());//职位
                        member.setStatus(detail.isActive());//成员状态 1是已激活，0是未激活，默认是1
                        member.setWorkAddress("");//工作地点
                        member.setSchoolClazz("");//学校班级
                        member.setGraduateSchool(""); //毕业学校

                        //memberMapper.insert(member);
                        //memberMapper.insertSelective(member);

                        memberExMapper.insertWithDingId(member);

                        memberList.add(member);
//                        count++;
//                        if(count % 1000 ==0){
//                            return;
//                        }
//                        System.out.println(member);

                    } catch (JsonProcessingException e) {
                        throw new CustomerException(CodeStatus.JSON_PARSING_ERROR);
                    }
                }
            }
        }

        // 批量插入集合
        //memberExMapper.insertMemberBatch(memberList);

        //插入前清空桥表
        memberExMapper.truncateTable("tb_deparment_member");
        //遍历map插入到桥表中
        for (Map.Entry<Long,List<String>> entry:dmmap.entrySet()){
            for (String id:entry.getValue()) {
                DeparmentMemberKey dm = new DeparmentMemberKey();
                dm.setMemberId(id);
                dm.setDeparmentId(entry.getKey());
                dmmapper.insert(dm);
            }
        }

    }

    @Override
    public List<Member> getMembersByDeptId(String deptId) throws CustomerException {
        return memberExMapper.getAllMembersByDeptId(deptId);
    }

    @Override
    public Member getMemberDetailById(String id) throws CustomerException {
        return memberMapper.selectByPrimaryKey(id);
    }

    @Override
    public void addMember(Member member , int[] deptIds) throws CustomerException {

        //钉钉官方添加，获得钉钉官方ID
        String result = createUser(member,deptIds);
        //解析json结果
        ObjectMapper om = new ObjectMapper();
        DingID dd = null;
        try {
            dd = om.readValue(result,DingID.class);
            //得到用户id之后，查询激活状态,有些账号添加时可能已经激活
            member = getMemberWithDingMember(getMemberDetailByUserId(dd.getUserid()));
        } catch (JsonProcessingException e) {
            throw new CustomerException(CodeStatus.JSON_PARSING_ERROR);
        }
        //判断返回值
        if(dd.getErrcode()==0&&"ok".equals(dd.getErrmsg())){
            //添加成功
            memberExMapper.insertWithDingId(member);
            //成功之后还需要插入桥表
            for(int id : deptIds){
                DeparmentMemberKey dm = new DeparmentMemberKey();
                dm.setMemberId(dd.getUserid());
                dm.setDeparmentId((long) id);
                dmmapper.insert(dm);
            }
        }else if(dd.getErrcode()== 60102) {
            //Userid在公司中已经存在
            throw new CustomerException(CodeStatus.USER_CREATE_FAILED);
        }else if(dd.getErrcode()== 60104) {
            //Userid在公司中已经存在
            throw new CustomerException(CodeStatus.USER_MOBILE_ALREADY_EXISTS);
        }else{
            //添加失败，可以抛出异常
            throw new CustomerException(CodeStatus.USER_CREATE_FAILED);
        }
    }

    @Override
    public List<Object> createExcel(int isAlive, String ids, String title) throws CustomerException {
        String[] headList = null;
        List<Object> dataObj = new ArrayList<>();

        if(StringUtils.isBlank(title)){
            //查询全部字段
            headList = DingDingUtil.MEMBER_ALL_FILED.split(",");
        }else{
            //查询部分字段
            headList = title.split(",");
        }
        String excelName = "成员列表.xlsx";
        //遍历枚举
        MemberFiled[] mf = MemberFiled.values();
        String[] mstr = new String[mf.length];
        for(int i=0;i<mf.length;i++){
            mstr[i] = mf[i].getDescription();
        }
        //准备一个属性的集合
        List<String> fieldList = new ArrayList<>();

        //遍历列头的汉字
        for (String value : headList) {
            //遍历枚举得到对应的英文属性
            for(MemberFiled m : mf){
                if (value.equals(m.getDescription())) {
                    fieldList.add(m.getMessage());
                }
            }
        }

        //根据id查询查询对应用户，然后生成数据列表
        //遍历id集合
        String[] idList = ids.split(",");
        List<Member> list = new ArrayList<>();
        List<Map<String, Object>> dataList = new ArrayList<>();

        for (String s : idList) {
            Member member = getMemberWithDingMember(getMemberDetailByUserId(s));
            //判断该用户是否激活
            if(isAlive == 1){
                if(member.getStatus()){
                    list.add(member);
                }
            }else {
                list.add(member);
            }
        }

        //通过反射动态调用filed的get方法
        for(Member m : list){
            Map<String, Object> map = new HashMap<>();
            Class clazz = m.getClass();

            for(String filed : fieldList){
                Method gm = null;
                try {
                    PropertyDescriptor descriptor = new PropertyDescriptor(filed, clazz);
                    gm = descriptor.getReadMethod();
                    Object o = gm.invoke(m);
                    map.put(filed,o);
                } catch (IllegalAccessException | InvocationTargetException | IntrospectionException e) {
                    throw new CustomerException(CodeStatus.INVOKE_EXCEPTION);
                };
            }
            dataList.add(map);
        }

        dataObj.add(excelName);
        dataObj.add(headList);
        dataObj.add(fieldList);
        dataObj.add(dataList);

        return dataObj;
    }

    @Override
    public void deleteMemberByIds(List<String> ids) throws CustomerException {

        //准备两个集合
        //保存成功的id
        List<String> successIds = new LinkedList<>();
        //保存失败的id
        List<String> failedIds = new LinkedList<>();
        //钉钉先删
        for (String id : ids){
            //需要在删除前确定id是否都在数据库里
            Member member = memberMapper.selectByPrimaryKey(id);
            if(member == null){
                throw new CustomerException(CodeStatus.USER_DELETE_ID_NOT_EXISTS);
            }
            String result = deleteUser(id);
            //解析json结果
            ObjectMapper objectMapper = new ObjectMapper();
            DingTalkMessage msg = null;
            try {
                msg = objectMapper.readValue(result,DingTalkMessage.class);
            } catch (JsonProcessingException e) {
                throw new CustomerException(CodeStatus.JSON_PARSING_ERROR);
            }
            if(msg.getErrcode()==0&&msg.getErrmsg().equals("ok")){
                successIds.add(id);
            }else {
                failedIds.add(id);
            }
        }
        //返回成功之后再删本地数据库（要及联删除桥表）
        memberExMapper.deleteByIds(successIds);


    }

    @Override
    public void updateMember(Member member, int[] deptIds) throws CustomerException {
        //钉钉端更新
        String result = updateUser(member,deptIds);
        ObjectMapper om = new ObjectMapper();
        DingTalkMessage dtm = null;
        try {
            dtm =  om.readValue(result,DingTalkMessage.class);
        } catch (JsonProcessingException e) {
            throw new CustomerException(CodeStatus.JSON_PARSING_ERROR);
        }
        //本地数据库成员表更新
        if(dtm.getErrcode() == 0 && "ok".equals(dtm.getErrmsg())){
            memberMapper.updateByPrimaryKeySelective(member);
            //如果部门列表发生变化则桥表更新
            //桥表更新逻辑为先删除成员ID下所有部门ID记录，再进行添加
            DeparmentMemberExample example = new DeparmentMemberExample();
            example.createCriteria().andMemberIdEqualTo(member.getId());
            dmmapper.deleteByExample(example);
            //判断如果没有输入部门id列表，会保持原来的数据
            if(deptIds != null && deptIds.length>0){
                for(int i : deptIds){
                    DeparmentMemberKey dm = new DeparmentMemberKey();
                    dm.setMemberId(member.getId());
                    dm.setDeparmentId((long) i);
                    dmmapper.insert(dm);
                }
            }
        }else {
            throw new CustomerException(CodeStatus.USER_UPDATE_FAILED);
        }
    }

    /* 私有方法封装  */

    /**
     * @Description: 对于一个英文字符串首字母大写的方法，注意避免输入汉字
     * @Author: GX Cui
     * @Date 9:59 上午 2020/6/29
     * @Param strs
     * @Return
     */
    private String capitalized(String strs){
        String b = strs.substring(0,1).toUpperCase();
        String c= strs.substring(1);
        return b+c;
    }


    /**
     * @Description: 根据钉钉返回的数据封装为本地数据库的用户实体
     * @Author: GX Cui
     * @Date 3:58 下午 2020/7/1
     * @Param 钉钉端返回的结果
     * @Return 封装好的用户对象
     */
    private Member getMemberWithDingMember(String responseResult){
        ObjectMapper mapper = new ObjectMapper();
        DingMemberDetail detail = null;
        try {
            detail = mapper.readValue(responseResult, DingMemberDetail.class);
        } catch (JsonProcessingException e) {
            throw new CustomerException(CodeStatus.JSON_PARSING_ERROR);
        }
        //设置加入数据库中的数据，5月1日cgx修改表，去掉id自动增长
        Member member = new Member();
        member.setId(detail.getUserid()); //成员id
        member.setName(detail.getName()); //成员名字
        //判断是否有邮箱
        if (detail.getEmail() == null) {
            member.setEmail("未开通邮箱"); //企业邮箱
        } else {
            member.setEmail(detail.getEmail()); //企业邮箱
        }
        member.setJobNumber(detail.getJobnumber()); //工号
        //判断是否显示手机号
        if (detail.isHide()) {
            member.setPhone(detail.getTel()); //手机号码
        } else {
            member.setPhone("号码已隐藏"); //手机号码
        }
        //判断是否有角色，多个角色只展示第一个
        if (detail.getRoles() == null || detail.getRoles().length == 0) {
            member.setRoleName("无角色");
        } else {
            member.setRoleName(detail.getRoles()[0].getName()); //角色名，角色名不止一个，如果有多个显示第一个
        }
        member.setPosition(detail.getPosition());//职位
        member.setStatus(detail.isActive());//成员状态 1是已激活，0是未激活，默认是1
        member.setWorkAddress("");//工作地点
        member.setSchoolClazz("");//学校班级
        member.setGraduateSchool(""); //毕业学校

        return member;
    }

    /**
     * @Description: 根据用户id获取用户详情
     * @Author: GX Cui
     * @Date 5:21 下午 2020/4/20
     * @Param String userId  用户id
     * @Return 返回值为json字符串
     */
    private String getMemberDetailByUserId(String userId) throws  CustomerException{
        DingTalkClient client = new DefaultDingTalkClient(DingDingUtil.USER_GET);
        OapiUserGetRequest req = new OapiUserGetRequest();
        req.setUserid(userId);
        req.setHttpMethod("GET");
        OapiUserGetResponse rsp = null;
        String result = "";
        try {
            rsp = client.execute(req, DingDingUtil.getToken());
            result = rsp.getBody();
        } catch (ApiException e) {
            throw new CustomerException(CodeStatus.API_EXCEPTION);
        }
        return result;
    }
    /**
     * @Description: 根据部门id获取用户id列表
     * @Author: GX Cui
     * @Date 5:24 下午 2020/4/20
     * @Param String deptId  部门id
     * @Return 返回值为json字符串
     */
    private String getUserIdListByDeptId(String deptId) throws CustomerException{
        DingTalkClient client = new DefaultDingTalkClient(DingDingUtil.DEPARTMENT_MEMBER_GET);
        OapiUserGetDeptMemberRequest req = new OapiUserGetDeptMemberRequest();
        req.setHttpMethod("GET");
        req.setDeptId(deptId);
        OapiUserGetDeptMemberResponse rsp = null;
        try {
            rsp = client.execute(req, DingDingUtil.getToken());
        } catch (ApiException e) {
            throw new CustomerException(CodeStatus.API_EXCEPTION);
        }
        return rsp.getBody();
    }

    /**
     * @Description: 钉钉端创建用户
     * @Author: GX Cui
     * @Date 9:59 上午 2020/6/29
     * @Param member 添加的成员对象
     * @Return
     */
    private String createUser(Member member,int[] departmentIds) throws CustomerException{

        DingTalkClient client = new DefaultDingTalkClient(DingDingUtil.USER_CREATE);
        OapiUserCreateRequest req = new OapiUserCreateRequest();
        req.setMobile(member.getPhone()); //手机号
        req.setName(member.getName()); // 成员名称
        ObjectMapper om = new ObjectMapper();
        try {
           String deptJson = om.writeValueAsString(departmentIds);
            req.setDepartment(deptJson); //部门id列表，是数组类型，但是需要传递字符串

        } catch (JsonProcessingException e) {
            throw new CustomerException(CodeStatus.JSON_PARSING_ERROR);
        }

        //选填
        req.setUserid(member.getId()==null?"":member.getId()); //成员编号，可自定义，不传默认由钉钉自动生成
        req.setEmail(member.getEmail()==null?"":member.getEmail());//企业邮箱
        req.setJobnumber(member.getJobNumber()==null?"":member.getJobNumber()); //工号
        req.setPosition(member.getPosition()==null?"":member.getPosition()); //职位
        req.setWorkPlace(member.getWorkAddress()==null?"":member.getWorkAddress()); //工作地点

        OapiUserCreateResponse rsp = null;
        try {
            rsp = client.execute(req, DingDingUtil.getToken());
            System.out.println(rsp.getBody());
        } catch (ApiException e) {
            throw new CustomerException(CodeStatus.API_EXCEPTION);
        }
        return rsp.getBody();
    }

    /**
     * @Description: 钉钉端根据用户id删除用户信息
     * @Author: GX Cui
     * @Date 3:54 下午 2020/7/1
     * @Param userID 用户id
     * @Return
     */
    private String deleteUser(String userId) throws CustomerException{
        DingTalkClient client = new DefaultDingTalkClient(DingDingUtil.USER_DELETE);
        OapiUserDeleteRequest req = new OapiUserDeleteRequest();
        req.setUserid(userId);
        req.setHttpMethod("GET");
        OapiUserDeleteResponse rsp = null;
        try {
            rsp = client.execute(req, DingDingUtil.getToken());
        } catch (ApiException e) {
            throw new CustomerException(CodeStatus.API_EXCEPTION);
        }
        return rsp.getBody();
    }

    /**
     * @Description: 钉钉端根据用户id修改用户信息
     * @Author: GX Cui
     * @Date 3:16 下午 2020/7/2
     * @Param member 用户对象 departmentIds 部门id列表
     * @Return json数据
     */
    private String updateUser(Member member,int[] departmentIds) throws CustomerException{
        DingTalkClient client = new DefaultDingTalkClient(DingDingUtil.USER_UPDATE);
        OapiUserUpdateRequest req = new OapiUserUpdateRequest();
        //必填项
        req.setUserid(member.getId()); //id不可修改
        //选填修改项，如果传递参数为空，则不修改之前保存的数值
        //因为使用的属性不是member的所有属性，所以这边暂时不进行重构封装，后期有需要可以使用反射动态调用
        if(member.getName() != null && !StringUtils.isBlank(member.getId())){
            req.setName(member.getName());
        }
        if(member.getPhone() != null && !StringUtils.isBlank(member.getPhone())){
            req.setMobile(member.getPhone());
        }
        if(member.getEmail() != null && !StringUtils.isBlank(member.getEmail())){
            req.setEmail(member.getEmail());
        }
        if(member.getJobNumber() != null && !StringUtils.isBlank(member.getJobNumber())){
            req.setJobnumber(member.getJobNumber());
        }
        if(member.getWorkAddress() != null && !StringUtils.isBlank(member.getWorkAddress())){
            req.setWorkPlace(member.getWorkAddress());
        }
        if(member.getPosition() != null && !StringUtils.isBlank(member.getPosition())){
            req.setPosition(member.getPosition());
        }
        //部门列表需要单独处理，这里和添加不同，需要使用集合而不是json字符串
        if(departmentIds != null && !ArrayUtils.isEmpty(departmentIds)){
            List<Long> deptList = new ArrayList<>();
            for (int departmentId : departmentIds) {
                deptList.add((long) departmentId);
            }
            req.setDepartment(deptList);
        }

        OapiUserUpdateResponse rsp = null;
        try {
            rsp = client.execute(req, DingDingUtil.getToken());
        } catch (ApiException e) {
            throw new CustomerException(CodeStatus.API_EXCEPTION);
        }
        System.out.println(rsp.getBody());

        return rsp.getBody();
    }

}
