package com.hust.experiment.controller;

import com.hust.experiment.model.*;
import com.hust.experiment.service.*;
import com.hust.experiment.util.ExperimentUtil;
import com.hust.experiment.util.ReadExcel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.websocket.server.PathParam;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class ManagerController {
    @Autowired
    MailService mailService;

    @Autowired
    UserService userService;

    @Autowired
    StudentService studentService;

    @Autowired
    ExpService expService;

    @Autowired
    ReportService reportService;

    @RequestMapping(path = "/createAnnouncement" ,method = {RequestMethod.GET,RequestMethod.POST})
    public String createAnnouncement(Model model){
        model.addAttribute("exps",expService.getAllExp());
        return "/html-admin/create-announcement";
    }

    @PostMapping(path = "/addMail")
    public String addMail(@RequestParam("fromId") Integer fromId,
                          @RequestParam(value = "toId",defaultValue = "0") Integer toId,
                          @RequestParam("type") String type,
                          @RequestParam("content") String content,
                          @RequestParam("title") String title,
                          @RequestParam(value = "about",defaultValue = "其他") String about){
        if(type.equals("公告")){
            if(mailService.addMail(fromId,content,title,about)){
                String account = userService.getUserById(fromId).getAccount();
                return "redirect:/" + account + "/announcement";
            }else{
                return ExperimentUtil.getJSONString(1,"发送失败");
            }
        }else if(type.equals("私信")){
            if(mailService.addMail(fromId,toId,content,title,about)){
                return ExperimentUtil.getJSONString(0,"私信发送成功");
            }else{
                return ExperimentUtil.getJSONString(1,"发送失败");
            }
        }else {
            return "发送错误";
        }
    }

    @PostMapping(path = "/deleteMail")
    public String deleteMail(@RequestParam("mailId") int id,@RequestParam("account")String account){
        mailService.delete(id);
        return "redirect:/" + account + "/announcement";
    }

    @RequestMapping(path = "/userManage" ,method = {RequestMethod.GET,RequestMethod.POST})
    public String userManage(Model model,@RequestParam(value = "param1",defaultValue = "")String accountParam,
                             @RequestParam(value = "param2",defaultValue = "")String accademyParam,
                             @RequestParam(value = "param3",defaultValue = "")String classParam,
                             @RequestParam(value = "seeAll",defaultValue = "1") Integer seeAll){
        List<User> list = userService.getAllUser();
        List<User> selectUserList = null;
        model.addAttribute("allUser",list);
        model.addAttribute("classes",userService.getAllClass());
        model.addAttribute("academies",userService.getAllAcademy());
        model.addAttribute("seeAll",seeAll);
        if(seeAll != 1){
            if(!userService.getUserByParams(accountParam,accademyParam,classParam).isEmpty()){
                selectUserList = userService.getUserByParams(accountParam,accademyParam,classParam);
            }
        }
        model.addAttribute("selectUsers",selectUserList);
        return "/html-admin/member-manage";
    }

    @RequestMapping(path = "/courseManage" ,method = {RequestMethod.GET,RequestMethod.POST})
    public String courseManage(Model model,@RequestParam(value = "param1",defaultValue = "")String semesterParam,
                               @RequestParam(value = "param2",defaultValue = "")String expParam,
                               @RequestParam(value = "param3",defaultValue = "")String teacherParam,
                               @RequestParam(value = "seeAll",defaultValue = "1")Integer seeAll){
        List<Exp> list = expService.getAllExp();
        model.addAttribute("vos",expService.getExpViewObjects(list));
        model.addAttribute("teachers",expService.getAllTeacher());
        model.addAttribute("expNames",expService.getAllExp());
        model.addAttribute("semesters",expService.getAllSemester());
        List<Exp> selectExp = null;
        if(seeAll != 1){
            if(!expService.getExpByParams(semesterParam,expParam,teacherParam).isEmpty()){
                selectExp = expService.getExpByParams(semesterParam,expParam,teacherParam);
                model.addAttribute("selectedVos",expService.getExpViewObjects(selectExp));
            }
        }
        model.addAttribute("seeAll",seeAll);
        model.addAttribute("selectedExps",selectExp);
        return "/html-admin/lab-course";
    }

    @RequestMapping(path = "/reportManage" ,method = {RequestMethod.GET,RequestMethod.POST})
    public String reportManage(Model model,@RequestParam(value = "param1",defaultValue = "")String account,
                               @RequestParam(value = "param2",defaultValue = "")String accademy,
                               @RequestParam(value = "param3",defaultValue = "")String expName,
                               @RequestParam(value = "seeAll",defaultValue = "1")Integer seeAll){
        model.addAttribute("allVos",reportService.getReportViewObjects(reportService.getAllReports()));
        model.addAttribute("exps",reportService.getAllExpOfReport());
        model.addAttribute("academies",reportService.getAllAcademiesOfReport());
        List<Report> selectedReports = null;
        if(seeAll != 1){
            List<Report> tempList = reportService.getReportsByParams(account,accademy,expName);
            if(!tempList.isEmpty()){
                selectedReports = tempList;
                model.addAttribute("selectedVos",reportService.getReportViewObjects(selectedReports));
            }
        }
        model.addAttribute("seeAll",seeAll);
        model.addAttribute("selectedReports",selectedReports);
        return "html-admin/lab-report-data";
    }


    //批量导入
    @PostMapping(value = "/importExcel")
    public String importStudentInfo(@RequestParam("student") MultipartFile file){
        File new_file = new File("student_info.xlsx");
        if(!file.isEmpty()){
            try{
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new_file));
                out.write(file.getBytes());
                out.flush();
                out.close();
            }catch (Exception e){

            }
        }

        ReadExcel readExcel = new ReadExcel(new_file);
        readExcel.loadExcel();
        List<Student> list = readExcel.getStudentList();
        int offset = studentService.selectLastStudent() == null? 0:studentService.selectLastStudent().getId();
        for(Student student:list){
            studentService.addStudent(student);
        }
        List<Student> studentList = studentService.selectNewStudent(offset);
        Map<String ,Object> map = null;
        try{
           map = studentService.batchRegistration(studentList);
           return "redirect:/userManage";
        }catch (Exception e){
            return ExperimentUtil.getJSONString(1,e.getMessage());
        }
    }

    @GetMapping(path = "/import")
    public String importStudent(){
        return "import_student_info";
    }

    @GetMapping(value = "/addMember")
    public String addMember(){
        return "html-admin/add-member";
    }

    @PostMapping(path = "/addUser")
    public String addUser(@RequestParam("account")String account,
                          @RequestParam("name")String name,
                          @RequestParam(value = "class",defaultValue = "")String classname,
                          @RequestParam(value = "academy",defaultValue = "")String academy){
        if(account.charAt(0) == 'U'&&account.length() == 10){
            Student student = new Student();
            student.setStuId(account);
            student.setClassname(classname);
            student.setAcademy(academy);
            student.setName(name);
            studentService.addStudent(student);
            studentService.registerStudent(student);
        }else {
            userService.register(account,"123456","123456");
            User user = userService.getUserbyAccount(account);
            userService.updateUserMessage(account,user.getUrl(),name,classname,academy,account);
        }
        return "redirect:/userManage";
    }
}
