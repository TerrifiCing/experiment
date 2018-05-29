package com.hust.experiment.controller;

import com.hust.experiment.model.*;
import com.hust.experiment.service.*;
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

    @Autowired
    ReportService reportService;

    @Autowired
    CourseService courseService;

    @Autowired
    ExpService expService;
    //查
    @GetMapping(path = "/{account}/userCenter")
    public String userCenter(@PathVariable("account") String account,
                             @RequestParam(value = "change",defaultValue = "0")Integer change,
                             Model model){
        Map<String ,Object> map = new HashMap<>();
        model.addAttribute("change",change);
        try{
            if(userService.hasUser(account)){
                if(account.charAt(0) == 'M'){
                    return "html-admin/personal-center";
                }else if(account.charAt(0) == 'U'){
                    return "html-student/personal-center";
                }else {
                    return "html-teacher/personal-center";
                }

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
                             @RequestParam(value = "name") String name,
                             @RequestParam(value = "classname",defaultValue = "") String classname,
                             @RequestParam(value = "academy",defaultValue = "") String academy,
                             @RequestParam(value = "nickname") String nickname){
        Map<String,Object> map = new HashMap<>();
        try{
            if(userService.hasUser(account)){
                userService.updateUserMessage(account,userService.getUserbyAccount(account).getUrl(),name,classname,academy,nickname);
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
            return "html-student/broad-manage";
        }else if(c == 'T'){
            return "html-teacher/broad-manage";
        }else {
            return "html-admin/broad-manage";
        }
    }

    @RequestMapping(path = "{account}/changePassword",method = {RequestMethod.GET,RequestMethod.POST})
    public String changePassword(@PathVariable("account") String account){
        User user = userService.getUserbyAccount(account);
        if(user.getAccount().charAt(0) == 'M'){
            return "html-admin/change-pwd";
        }else if(user.getAccount().charAt(0) == 'U'){
            return "html-student/change-pwd";
        }else {
            return "html-teacher/change-pwd";
        }

    }

    @PostMapping(path = "/checkOldPassword")
    @ResponseBody
    public String checkOldPassword(@RequestParam("oldPassword")String oldPass,@RequestParam("account")String account){
        User user = userService.getUserbyAccount(account);
        if(ExperimentUtil.MD5(oldPass + user.getSalt()).equals(user.getPassword())){
            return ExperimentUtil.getJSONString(0,"正确");
        }else {
            return ExperimentUtil.getJSONString(1,"错误");
        }
    }

    @PostMapping(path = "/changePassword")
    @ResponseBody
    public String changePassword(@RequestParam("newPassword")String newPassword,@RequestParam("account")String account){
        User user = userService.getUserbyAccount(account);
        userService.updatePassword(newPassword,user.getId());
        return ExperimentUtil.getJSONString(0,"修改成功");
    }


    @RequestMapping(path = "/uploadFile",method = RequestMethod.POST)
    @ResponseBody
    public String upload(@RequestParam("file")MultipartFile file,@RequestParam("account")String account,
                         @RequestParam("courseId")Integer courseId){
        try{
            String fileUrl = userService.saveFile(file);
            if(fileUrl == null){
                return ExperimentUtil.getJSONString(1,"上传失败");
            }
            Report report = new Report();
            report.setStatus(0);
            report.setStudentId(userService.getUserbyAccount(account).getId());
            report.setDataUrl("");
            report.setReportUrl(fileUrl);
            report.setCourseId(courseId);
            reportService.addReport(report);
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
            httpServletResponse.setContentType("application/pdf");
            httpServletResponse.setHeader("Content-Disposition", "inline; filename="+fileName);
            StreamUtils.copy(new FileInputStream(ExperimentUtil.FILE_DIR + fileName),httpServletResponse.getOutputStream());
        }catch (Exception e){
            logger.error("读取文件错误!" + e.getMessage());
        }
    }

    @RequestMapping(path = "/downloadFile" ,method = RequestMethod.GET)
    @ResponseBody
    public void downloadFile(@RequestParam("name")String fileName, HttpServletResponse httpServletResponse){
        try{
            httpServletResponse.setContentType("application/octet-stream");
            httpServletResponse.setHeader("Content-Disposition", "attachment; filename="+fileName);
            StreamUtils.copy(new FileInputStream(ExperimentUtil.FILE_DIR + fileName),httpServletResponse.getOutputStream());
        }catch (Exception e){
            logger.error("读取文件错误!" + e.getMessage());
        }
    }

    @GetMapping(path = "/{account}/viewReport")
    public String viewReport(Model model,@RequestParam("reportId")Integer reportId,
                             @PathVariable("account")String account){
        ViewObject vo = new ViewObject();
        Report report = reportService.findById(reportId);
        vo.set("report",report);
        vo.set("student",userService.getUserById(report.getStudentId()));
        vo.set("course",courseService.getCourseById(report.getCourseId()));
        vo.set("exp",expService.getExpById(courseService.getCourseById(report.getCourseId()).getExpId()));
        vo.set("teacher",userService.getUserById(expService.getExpById(courseService.getCourseById(report.getCourseId()).getExpId()).getTeacherId()));
        model.addAttribute("reportVo",vo);

        if(userService.getUserbyAccount(account).getPosition().equals("教师")){
            return "html-teacher/view-report";
        }else if(userService.getUserbyAccount(account).getPosition().equals("管理员")){
            return "html-admin/view-report";
        }else {
            return "html-student/view-report";
        }
    }

    @GetMapping(path = "/{account}/attendance")
    public String attendance(@PathVariable("account")String account){
        return "html-teacher/student-attendance";
    }
}
