package com.pluto.stock.controller;

import com.pluto.stock.service.UserService;
import com.pluto.stock.vo.req.LoginReqVo;

import com.pluto.stock.vo.resp.LoginRespVo;
import com.pluto.stock.vo.resp.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;
   /* @GetMapping("test")
    public  String getName(){
        return "pluto";
    }*/
    @PostMapping("/login")
    public R<LoginRespVo> login(@RequestBody LoginReqVo vo){
        return userService.login(vo);
    }

    /**
     * 生成登录校验码
     * @return
     */
    @GetMapping("/captcha")
    public R<Map> getCaptCha(){
        return userService.getCaptCha();
    }
}
