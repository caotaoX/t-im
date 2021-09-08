package com.ct.myim.im.controller;

import com.ct.myim.framework.web.entity.AjaxResult;
import com.ct.myim.im.entity.User;
import com.ct.myim.im.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public AjaxResult login(@RequestBody User user) {
        return loginService.login(user);
    }

    @PostMapping("/outLogin")
    public AjaxResult login() {
        return loginService.outLogin();
    }
}
