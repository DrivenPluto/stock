package com.pluto.stock.vo.req;

import lombok.Data;


@Data
public class LoginReqVo {
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 验证码
     */
    private String code;

    /**
     * 存入redis的随机码
     */
    private String rkey;
}
