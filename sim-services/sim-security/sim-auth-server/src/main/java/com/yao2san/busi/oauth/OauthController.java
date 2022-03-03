package com.yao2san.busi.oauth;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Oauth2授权接口实现(待完成)
 */
@RequestMapping("oauth2")
public class OauthController {
    /**
     * 获取授权码
     * <p>
     * @param responseType 必须	对于授权码模式response_type=code
     * @param clientId     必须	客户端ID，用于标识一个客户端，等同于appId，在注册应用时生成
     * @param redirectUri  可选	授权回调地址，具体参见2.2.3小节
     * @param scope        可选	权限范围，用于对客户端的权限进行控制，如果客户端没有传递该参数，那么服务器则以该应用的所有权限代替
     * @param state        推荐	用于维持请求和回调过程中的状态，防止CSRF攻击，服务器不对该参数做任何处理，如果客户端携带了该参数，则服务器在响应时原封不动的返回
     * @return {"code":"xxx","state":"xx"}
     */
    @GetMapping("authorize")
    public void authorize(@RequestParam(name = "response_type") String responseType,
                          @RequestParam(name = "response_type", required = false) String clientId,
                          @RequestParam(name = "redirect_uri", required = false) String redirectUri,
                          @RequestParam(name = "scope", required = false) String scope,
                          @RequestParam(name = "state", required = false) String state,
                          HttpServletResponse response
    ) throws IOException, URISyntaxException {

        String code = "";
        String url = redirectUri;
        URI uri = new URI(redirectUri);
        if (StringUtils.isNotEmpty(uri.getQuery())) {
            url += "&code=" + code;
        } else {
            url = url.endsWith("?") ? (url += "code=" + code) : (url += "?code=" + code);
        }
        response.sendRedirect(url);
    }

    /**
     * code换取token
     *
     * @param grantType   必须	对于授权码模式grant_type=authorization_code
     * @param code        必须	上一步骤获取的授权码
     * @param redirectUri 必须	授权回调地址
     * @param clientId    必须	客户端ID，用于标识一个客户端，等同于appId，在注册应用时生成
     * @return {
     * "access_token":"xxx",
     * "token_type":"example",
     * "expires_in":3600,
     * "refresh_token":"yyy",
     * "example_parameter":"example_value"
     * }
     */
    @PostMapping("token")
    @ResponseBody
    public Map<String, Object> token(@RequestParam(name = "grant_type") String grantType,
                                     @RequestParam(name = "code") String code,
                                     @RequestParam(name = "redirect_uri") String redirectUri,
                                     @RequestParam(name = "client_id") String clientId
    ) {
        Map<String, Object> result = new HashMap<>();
        result.put("access_token", "");
        result.put("token_type", "");
        result.put("expires_in", 3600);
        result.put("refresh_token", "");
        result.put("scope", "");
        return result;
    }
}
