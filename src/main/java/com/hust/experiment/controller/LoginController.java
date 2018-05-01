package com.hust.experiment.controller;

import com.hust.experiment.model.Mail;
import com.hust.experiment.model.User;
import com.hust.experiment.model.ViewObject;
import com.hust.experiment.service.MailService;
import com.hust.experiment.service.UserService;
import com.hust.experiment.util.ExperimentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.View;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;

    @Autowired
    MailService mailService;

    @GetMapping(path = {"/loginIndex"})
    public String loginIndex(Model model){
        return "login";
    }

    @GetMapping(path = "/registerIndex")
    public String registerIndex(Model model){
        return "register";
    }

    @PostMapping(path = "/register")
    public String register(Model model, @RequestParam("account") String account,
                             @RequestParam("password") String pass,@RequestParam("reinputPass") String repass){
        try{
            Map<String,Object> map = userService.register(account,pass,repass);
            if(map.isEmpty()){
                return ExperimentUtil.getJSONString(0,"注册成功！");
            }else{
                return ExperimentUtil.getJSONString(1,map);
            }
        }catch (Exception e){
            logger.error("注册异常"+ e.getMessage());
            return ExperimentUtil.getJSONString(1,"注册异常");
        }
    }

    @PostMapping(path = "/index")
    public String login(Model model, @RequestParam("account") String account, @RequestParam("password") String password,
                        HttpServletResponse httpServletResponse){
        try{
            Map<String,Object> map = userService.login(account,password);
            if(map.containsKey("ticket")){
                Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
                cookie.setPath("/");
                httpServletResponse.addCookie(cookie);
                return "redirect:/" + account + "/announcement";
            }else{
                return ExperimentUtil.getJSONString(1,map);
            }
        }catch (Exception e){
            logger.error("登录异常"+ e.getMessage());
            return ExperimentUtil.getJSONString(1,"登录异常");
        }
    }

    @RequestMapping(path = {"/logout"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/loginIndex";
    }
}
