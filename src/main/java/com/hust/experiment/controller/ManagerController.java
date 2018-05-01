package com.hust.experiment.controller;

import com.hust.experiment.model.Exp;
import com.hust.experiment.model.Student;
import com.hust.experiment.model.User;
import com.hust.experiment.model.ViewObject;
import com.hust.experiment.service.ExpService;
import com.hust.experiment.service.MailService;
import com.hust.experiment.service.StudentService;
import com.hust.experiment.service.UserService;
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

    @RequestMapping(path = "/createAnnouncement" ,method = {RequestMethod.GET,RequestMethod.POST})
    public String createAnnouncement(){
        return "/html-admin/create-announcement";
    }

    @PostMapping(path = "/addMail")
    public String addMail(@RequestParam("fromId") Integer fromId,
                          @RequestParam(value = "toId",defaultValue = "0") Integer toId,
                          @RequestParam("type") String type,
                          @RequestParam("content") String content,
                          @RequestParam("title") String title){
        if(type.equals("公告")){
            if(mailService.addMail(fromId,content,title)){
                String account = userService.getUserById(fromId).getAccount();
                return "redirect:/" + account + "/announcement";
            }else{
                return ExperimentUtil.getJSONString(1,"发送失败");
            }
        }else if(type.equals("私信")){
            if(mailService.addMail(fromId,toId,content,title)){
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
    public String userManage(Model model){
        List<User> list = userService.getAllUser();
        model.addAttribute("allUser",list);
        return "/html-admin/member-manage";
    }
    @RequestMapping(path = "/courseManage" ,method = {RequestMethod.GET,RequestMethod.POST})
    public String courseManage(Model model){
        List<Exp> list = expService.getAllExp();
        List<ViewObject> vos = new ArrayList<>();
        for(Exp exp : list){
            ViewObject vo = new ViewObject();
            vo.set("course",exp);
            vo.set("teacher",userService.getUserById(exp.getTeacherId()));
            vos.add(vo);
        }
        model.addAttribute("vos",vos);
        return "/html-admin/lab-course";
    }

    @RequestMapping(path = "/reportManage" ,method = {RequestMethod.GET,RequestMethod.POST})
    public String reportManage(Model model){
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
}
