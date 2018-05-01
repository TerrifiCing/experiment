package com.hust.experiment.controller;

import com.hust.experiment.model.Exp;
import com.hust.experiment.model.Mail;
import com.hust.experiment.model.User;
import com.hust.experiment.model.ViewObject;
import com.hust.experiment.service.MailService;
import com.hust.experiment.service.StudentService;
import com.hust.experiment.service.UserService;
import com.hust.experiment.util.ExperimentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;

    @Autowired
    MailService mailService;

    //查
    @GetMapping(path = "/{account}/userCenter")
    public String userCenter(Model model,@PathVariable("account") String account){
        Map<String ,Object> map = new HashMap<>();
        try{
            if(userService.hasUser(account)){
                return "personal-center";
            }else{
                map.put("msgAccount","账户不存在");
                return ExperimentUtil.getJSONString(1,map);
            }
        }catch (Exception e){
            logger.error(e.getMessage());
            return ExperimentUtil.getJSONString(1,map);
        }
    }

    //改个人信息
    @PostMapping(path = "/user/update")
    @ResponseBody
    public String updateUser(@RequestParam(value = "account") String account,
                             @RequestParam(value = "url") String url,
                             @RequestParam(value = "name") String name,
                             @RequestParam(value = "classname") String classname,
                             @RequestParam(value = "academy") String academy,
                             @RequestParam(value = "nickname") String nickname){
        Map<String,Object> map = new HashMap<>();
        try{
            if(userService.hasUser(account)){
                userService.updateUserMessage(account,url,name,classname,academy,nickname);
                map.put("msgUpdate","更新成功");
                return ExperimentUtil.getJSONString(0,map);
            }else{
                map.put("msgUpdate","账户不存在");
                return ExperimentUtil.getJSONString(1,map);
            }
        }catch (Exception e){
            map.put("msgUpdate","更新失败");
            logger.error(e.getMessage());
            return ExperimentUtil.getJSONString(1,map);
        }
    }

    //删
    @DeleteMapping(path = "/user/delete")
    @ResponseBody
    public String deleteUser(@RequestParam("account") String account){
        Map<String ,Object> map = new HashMap<>();
        try{
            if(userService.hasUser(account)){
                userService.deleteUser(account);
                map.put("magDelete","删除成功");
                return ExperimentUtil.getJSONString(0,map);
            }else{
                map.put("msgDelete","账户不存在");
                return ExperimentUtil.getJSONString(1,map);
            }
        }catch (Exception e){
            map.put("msgDelete","删除失败" + e.getStackTrace());
            return ExperimentUtil.getJSONString(1,map);
        }
    }

    @RequestMapping(path = "/{account}/announcement",method = {RequestMethod.GET,RequestMethod.POST})
    public String announcement(Model model,@PathVariable("account") String account ){
        List<Mail> list = mailService.getMails(5,"公告");
        List<ViewObject> vos = new ArrayList<>();
        for(Mail m : list){
            ViewObject vo = new ViewObject();
            vo.set("mail",m);
            vo.set("fromName",userService.getUserById(m.getFromId()).getName());
            vos.add(vo);
        }
        model.addAttribute("mailVos",vos);
        char c = account.charAt(0);
        if(c == 'U'){
            return "stu_index";
        }else if(c == 'T'){
            return "tea_index";
        }else {
            return "/html-admin/broad-manage";
        }
    }

    @RequestMapping(path = "/changePassword",method = {RequestMethod.GET,RequestMethod.POST})
    public String changePassword(Model model){
        return "html-admin/change-pwd";
    }

    @PostMapping(path = "{account}/changePwd")
    public String changPwd(@PathVariable("account")String account, @RequestParam("oldPass") String oldPass,
                           @RequestParam("newPass")String newPass, @RequestParam("rePass")String rePass){
        User user = userService.getUserbyAccount(account);
        Map<String ,Object> map =new HashMap<>();
        if(oldPass.equals("")||oldPass == null||newPass.equals("")||newPass == null||rePass.equals("")||rePass == null){
            map.put("codeMsg","密码不能为空!");
            return ExperimentUtil.getJSONString(1,map);
        }else if(!ExperimentUtil.MD5(oldPass + user.getSalt()).equals(user.getPassword())){
            map.put("codeMsg","原密码不正确!");
            return ExperimentUtil.getJSONString(1,map);
        }else if(!newPass.equals(rePass)){
            map.put("codeMsg","两次输入的新密码不相等！");
            return ExperimentUtil.getJSONString(1,map);
        }else {
            userService.updatePassword(newPass,user.getId());
            return ExperimentUtil.getJSONString(0,"密码修改成功！");
        }
    }


    @RequestMapping(path = "/uploadFile",method = RequestMethod.POST)
    @ResponseBody
    public String upload(@RequestParam("file")MultipartFile file){
        try{
            String fileUrl = userService.saveFile(file);
            if(fileUrl == null){
                return ExperimentUtil.getJSONString(1,"上传失败");
            }
            return ExperimentUtil.getJSONString(0,fileUrl);
        }catch (Exception e){
            logger.error("上传失败" + e.getMessage());
            return ExperimentUtil.getJSONString(1,"上传失败");
        }
    }

    @RequestMapping(path = "/getFile" ,method = RequestMethod.GET)
    @ResponseBody
    public void getFile(@RequestParam("name")String fileName, HttpServletResponse httpServletResponse){
        try{
            httpServletResponse.setContentType("file");
            StreamUtils.copy(new FileInputStream(ExperimentUtil.FILE_DIR + fileName),httpServletResponse.getOutputStream());
        }catch (Exception e){
            logger.error("读取文件错误!" + e.getMessage());
        }
    }

    @GetMapping(path = "/viewReport")
    public String viewReport(){
        return "html-admin/view-report";
    }
}
