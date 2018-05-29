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
import java.text.SimpleDateFormat;
import java.util.*;

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

    @Autowired
    CourseService courseService;

    @RequestMapping(path = "/{account}/createAnnouncement" ,method = {RequestMethod.GET,RequestMethod.POST})
    public String createAnnouncement(Model model,@PathVariable("account")String account){
        model.addAttribute("exps",expService.getAllExp());
        if(userService.getUserbyAccount(account).getPosition().equals("教师")){
            return "/html-teacher/create-announcement";
        }else {
            return "/html-admin/create-announcement";
        }
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
    @ResponseBody
    public String deleteMail(@RequestParam("mailId") int id){
        mailService.delete(id);
        return "success";
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

    @RequestMapping(path = "/{account}/courseManage" ,method = {RequestMethod.GET,RequestMethod.POST})
    public String courseManage(Model model,@RequestParam(value = "param1",defaultValue = "")String semesterParam,
                               @RequestParam(value = "param2",defaultValue = "")String expParam,
                               @RequestParam(value = "param3",defaultValue = "")String teacherParam,
                               @RequestParam(value = "seeAll",defaultValue = "1")Integer seeAll,
                               @PathVariable("account")String account){
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
        if(userService.getUserbyAccount(account).getPosition().equals("教师")){
            return "/html-teacher/lab-course";
        }else {
            return "/html-admin/lab-course";
        }
    }

    @RequestMapping(path = "/{account}/reportManage" ,method = {RequestMethod.GET,RequestMethod.POST})
    public String reportManage(Model model,@RequestParam(value = "param1",defaultValue = "")String account1,
                               @RequestParam(value = "param2",defaultValue = "")String accademy,
                               @RequestParam(value = "param3",defaultValue = "")String expName,
                               @RequestParam(value = "seeAll",defaultValue = "1")Integer seeAll,
                               @PathVariable("account")String account){
        model.addAttribute("allVos",reportService.getReportViewObjects(reportService.getAllReports()));
        model.addAttribute("exps",reportService.getAllExpOfReport());
        model.addAttribute("academies",reportService.getAllAcademiesOfReport());
        List<Report> selectedReports = null;
        if(seeAll != 1){
            List<Report> tempList = reportService.getReportsByParams(account1,accademy,expName);
            if(!tempList.isEmpty()){
                selectedReports = tempList;
                model.addAttribute("selectedVos",reportService.getReportViewObjects(selectedReports));
            }
        }
        model.addAttribute("seeAll",seeAll);
        model.addAttribute("selectedReports",selectedReports);

        if(userService.getUserbyAccount(account).getPosition().equals("教师")){
            return "/html-teacher/lab-report-data";
        }else {
            return "html-admin/lab-report-data";
        }
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

    @GetMapping(value = "/{account}/addMember")
    public String addMember(@PathVariable("account")String account){
        if(userService.getUserbyAccount(account).getPosition().equals("教师")){
            return "/html-teacher/add-member";
        }else {
            return "html-admin/add-member";
        }

    }

    @PostMapping(path = "/addUser")
    @ResponseBody
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
        User user = userService.getUserbyAccount(account);
        Map<String,Object> map = new HashMap<>();
        map.put("account",user.getAccount());
        map.put("name",user.getName());
        return ExperimentUtil.getJSONString(0,map);
    }

    @RequestMapping(path = "/addExp",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String addExp(@RequestParam("courseName") String courseName,@RequestParam("period")Integer period,
                       @RequestParam("credit")double credit,@RequestParam("teacher")String teacher,
                       @RequestParam("semester")String semester){
        if(userService.hasUser(teacher)){
            try{
                expService.addExperiment(courseName,userService.getUserbyAccount(teacher).getId(),period,credit,semester);
                return ExperimentUtil.getJSONString(0,"添加成功");
            }catch (Exception e){
                return ExperimentUtil.getJSONString(1,"添加失败");
            }
        }else {
            return ExperimentUtil.getJSONString(1,"该教师学工号不存在");
        }
    }

    @PostMapping(path = "/deleteExp")
    @ResponseBody
    public String deleteExp(@RequestParam("expId")Integer id){
        if(expService.deleteExp(id)){
            return ExperimentUtil.getJSONString(0,"删除成功");
        }else {
            return ExperimentUtil.getJSONString(1,"删除失败");
        }
    }

    @GetMapping(path = "/{account}/viewSelectedCourse")
    public String viewCourse(Model model,@PathVariable("account")String account){
        List<Course> list = courseService.getAllCourse();
        model.addAttribute("courseVos",courseService.getViewObjectsForCourse(list,"M201413407"));
        if(userService.getUserbyAccount(account).getPosition().equals("教师")){
            return "/html-teacher/lab-course-select";
        }else {
            return "html-admin/lab-course-select";
        }
    }

    @GetMapping(path = "/{account}/releaseCourse")
    public String releaseCourse(Model model,@PathVariable("account")String account){
        model.addAttribute("exps",expService.getAllExp());

        if(userService.getUserbyAccount(account).getPosition().equals("教师")){
            return "html-teacher/lab-course-manage";
        }else {
            return "html-admin/lab-course-manage";
        }
    }

    @PostMapping(path = "/addCourse")
    @ResponseBody
    public String addCourse(@RequestParam("expId")String expmesg, @RequestParam("classroom")String classroom,
                            @RequestParam("time")String Strdate,@RequestParam("count")Integer count){
        String msg = expmesg.replaceAll("\\s*", "");
        int index = msg.indexOf("d");
        int expId = Integer.valueOf(msg.substring(index + 1));
        Course course = new Course();
        course.setStatus(0);
        course.setCount(count);
        course.setExpId(expId);
        course.setClassroom(classroom);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        String s = Strdate.replace('T',' ');
        try{
            Date date = sdf.parse(s);
            course.setClassTime(date);
            courseService.addCourse(course);
            return ExperimentUtil.getJSONString(0,"添加选课成功");
        }catch (Exception e){
            return ExperimentUtil.getJSONString(1,"添加选课失败");
        }
    }

    @PostMapping(path = "/deleteUser")
    @ResponseBody
    public String deleteUser(@RequestParam("account")String account){
        if(userService.deleteUser(account)){
            return ExperimentUtil.getJSONString(0,"删除成功");
        }else {
            return ExperimentUtil.getJSONString(1,"删除失败");
        }
    }

    @PostMapping(path = "/score")
    @ResponseBody
    public String score(@RequestParam("reportId")Integer reportId,@RequestParam("score")String score){
        reportService.updateScore(reportId,score);
        return ExperimentUtil.getJSONString(0,"评分成功");
    }
}
