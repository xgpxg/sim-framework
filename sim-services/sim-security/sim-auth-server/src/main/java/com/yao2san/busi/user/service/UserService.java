package com.yao2san.busi.user.service;

import com.yao2san.busi.user.bean.Role;
import com.yao2san.busi.user.bean.User;
import com.yao2san.sim.framework.web.response.ResponseData;

import java.util.List;
import java.util.Map;

public interface UserService {
    ResponseData getUserInfo(String token, String scope);


    ResponseData updateCurrUserInfo(User user);

    String upsertUserAttr(Long userId, Map<String, List<Object>> urlAttr);

    void updateUserAttrValue(Long userId, String attrCode, Object attrValue);

    List<Map> qryUserAttr(String attrCode);

    String qryCurrUserAttrSingleValue(String attrCode);

    String qryUserAttrSingleValue(Long userId, String attrCode);

    List<String> qryUserAttrListValue(String attrCode);


}
