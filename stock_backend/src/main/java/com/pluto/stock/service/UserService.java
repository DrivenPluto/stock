package com.pluto.stock.service;

import com.pluto.stock.vo.req.LoginReqVo;
import com.pluto.stock.vo.resp.LoginRespVo;
import com.pluto.stock.vo.resp.R;

import java.util.Map;

/**
 * @author pluto
 * @date 7/27/23 21:03
 * @description: 定义用户接口
 */
public interface UserService {
    /**
     * 用户登录
     * @param vo
     * @return
     */
    R<LoginRespVo> login(LoginReqVo vo);

    R<Map> getCaptCha();
}
