package com.pluto.stock.service.impl;

import com.google.common.base.Strings;
import com.pluto.stock.mapper.SysUserMapper;
import com.pluto.stock.pojo.SysUser;
import com.pluto.stock.service.UserService;
import com.pluto.stock.utils.IdWorker;
import com.pluto.stock.vo.req.LoginReqVo;
import com.pluto.stock.vo.resp.LoginRespVo;
import com.pluto.stock.vo.resp.R;
import com.pluto.stock.vo.resp.ResponseCode;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public R<LoginRespVo> login(LoginReqVo vo) {
        //判断用户名、密码、验证码、sessionid是否存在
        if (vo == null || Strings.isNullOrEmpty(vo.getUsername()) || Strings.isNullOrEmpty(vo.getPassword())
            || Strings.isNullOrEmpty(vo.getCode()) || Strings.isNullOrEmpty(vo.getRkey())) {
            redisTemplate.delete(vo.getRkey());
            return R.error(ResponseCode.DATA_ERROR.getMessage());
        }
        //校验redis
        String redisCode = (String) redisTemplate.opsForValue().get(vo.getRkey());
        if (redisCode == null || !redisCode.equals(vo.getCode())) {
            return R.error(ResponseCode.DATA_ERROR.getMessage());
        }
         //快速淘汰校验码
        redisTemplate.delete(vo.getRkey());
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

    /**
     *
     * @return
     */
    @Override
    public R<Map> getCaptCha() {
        //1.生成随机校验码，长度为4
        String checkCode = RandomStringUtils.randomNumeric(4);
        //2.生成一个类似sessionid的id作为key，然后校验码作为value保存在redis下，同时设置有效期60s
        String sessionId = String.valueOf(idWorker.nextId());
        redisTemplate.opsForValue().set(sessionId,checkCode,60, TimeUnit.SECONDS);
        //3.组装响应的map对象
        HashMap<String, String> map = new HashMap<>();
        map.put("code",checkCode);
        map.put("rkey",sessionId);
        //4.返回组装数据
        return R.ok(map);
    }
}
