package com.pluto.stock.service.impl;

import com.google.common.base.Strings;
import com.pluto.stock.mapper.SysUserMapper;
import com.pluto.stock.pojo.SysUser;
import com.pluto.stock.service.UserService;
import com.pluto.stock.vo.req.LoginReqVo;
import com.pluto.stock.vo.resp.LoginRespVo;
import com.pluto.stock.vo.resp.R;
import com.pluto.stock.vo.resp.ResponseCode;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author pluto
 * @date 7/27/23 21:05
 * @description: 用户服务实现
 */
@Service("userService")
public class UserServiceImpl implements UserService {
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public R<LoginRespVo> login(LoginReqVo vo) {
        if (vo == null || Strings.isNullOrEmpty(vo.getUsername()) || Strings.isNullOrEmpty(vo.getPassword())) {
            return R.error(ResponseCode.DATA_ERROR.getMessage());
        }

        SysUser userInfo = sysUserMapper.findUserInfoByUserName(vo.getUsername());
        if (userInfo == null) {
            return R.error(ResponseCode.DATA_ERROR.getMessage());
        }
        if (!passwordEncoder.matches(vo.getPassword(),userInfo.getPassword())) {
            return R.error(ResponseCode.DATA_ERROR.getMessage());
        }
        LoginRespVo respVo = new LoginRespVo();
        BeanUtils.copyProperties(userInfo,respVo);
        return R.ok(respVo);
    }
}
