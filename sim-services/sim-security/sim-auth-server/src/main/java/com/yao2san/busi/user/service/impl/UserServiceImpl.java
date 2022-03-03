package com.yao2san.busi.user.service.impl;

import com.yao2san.busi.user.bean.Role;
import com.yao2san.busi.user.bean.User;
import com.yao2san.busi.user.service.UserService;
import com.yao2san.sim.framework.web.response.ResponseData;
import com.yao2san.sim.framework.web.service.impl.BaseServiceImpl;
import com.yao2san.utils.UserUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends BaseServiceImpl implements UserService {
    @Override
    public ResponseData getUserInfo(String token, String scope) {
        //验证token
        if (StringUtils.equals(token, UserUtil.getCurrUserToken())) {
            switch (scope) {
                case "":
                case "all":
                    Map<String, Object> userBaseInfo = UserUtil.getCurrUserBaseInfo();
                    return ResponseData.success(userBaseInfo);
                case "roles":
                    return ResponseData.success(UserUtil.getCurrUserRoles());
                case "purview":
                    return ResponseData.success(UserUtil.getCurrUserPermissions());
            }
        }
        return ResponseData.success();
    }

    @Override
    @Transactional
    public ResponseData updateCurrUserInfo(User user) {
        user.setUserId(UserUtil.getCurrUserId());
        this.sqlSession.update("user.updateUser", user);
        //更新shiro中的用户信息
        UserUtil.updateCurrUserSessionInfo(user);

        return ResponseData.success();
    }


    //TODO 待优化
    @SuppressWarnings("all")
    public String upsertUserAttr(Long userId, Map<String, List<Object>> urlAttr) {
        //校验属性编码是否存在
        if (urlAttr != null) {
            AtomicBoolean isCheck = new AtomicBoolean(true);
            urlAttr.keySet().forEach(item -> {
                Integer count = this.sqlSession.selectOne("common.checkAttrCode", item);
                if (count <= 0) {
                    isCheck.set(false);
                }
            });
            if (!isCheck.get()) {
                return "扩展属性不存在,请检查属性编码";
            }
            urlAttr.forEach((key, value) -> {
                Map<String, Object> params = new HashMap();
                params.put("userId", userId);
                params.put("attrCode", key);
                params.put("attrValueList", value);
                int count = this.sqlSession.selectOne("user.checkUserAttr", params);
                if (count > 0) {
                    this.sqlSession.delete("user.deleteUserAttr", params);

                    this.sqlSession.insert("user.addUserAttr", params);
                } else {
                    this.sqlSession.insert("user.addUserAttr", params);
                }

            });

        }
        return "";
    }

    public void updateUserAttrValue(Long userId, String attrCode, Object attrValue) {
        Map<String, Object> params = new HashMap();
        params.put("userId", userId);
        params.put("attrCode", attrCode);
        params.put("attrValue", attrValue);
        this.sqlSession.update("user.updateUserAttr", params);
    }

    public List<Map> qryUserAttr(String attrCode) {
        Map<String, Object> params = new HashMap<>(2);
        params.put("userId", UserUtil.getCurrUserId());
        params.put("attrCode", attrCode);
        List<Map> list = this.sqlSession.selectList("user.qryUserAttr", params);
        return list;
    }

    @SuppressWarnings("unchecked")
    public String qryCurrUserAttrSingleValue(String attrCode) {
        Map<String, Object> params = new HashMap<>(2);
        params.put("userId", UserUtil.getCurrUserId());
        params.put("attrCode", attrCode);
        List<Map> list = this.sqlSession.selectList("user.qryUserAttr", params);
        if (list != null && list.size() > 0) {
            return (String) list.get(0).get("attrValue");
        }
        return null;
    }

    @Override
    public String qryUserAttrSingleValue(Long userId, String attrCode) {
        Map<String, Object> params = new HashMap<>(2);
        params.put("userId", userId);
        params.put("attrCode", attrCode);
        List<Map> list = this.sqlSession.selectList("user.qryUserAttr", params);
        if (list != null && list.size() > 0) {
            return (String) list.get(0).get("attrValue");
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<String> qryUserAttrListValue(String attrCode) {
        Map<String, Object> params = new HashMap<>(2);
        params.put("userId", UserUtil.getCurrUserId());
        params.put("attrCode", attrCode);
        List<Map> list = this.sqlSession.selectOne("user.qryUserAttr", params);
        if (list != null && list.size() > 0) {
            return list.stream().map(item -> (String) item.get("attrValue")).collect(Collectors.toList());
        }
        return null;
    }
}
