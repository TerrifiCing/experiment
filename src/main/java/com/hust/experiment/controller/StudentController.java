package com.hust.experiment.controller;

import com.hust.experiment.model.*;
import com.hust.experiment.service.*;
import com.hust.experiment.util.ExperimentUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class StudentController {
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

    //学生部分

    @RequestMapping(path = "{account}/gradeQuery",method = {RequestMethod.GET,RequestMethod.POST})
    public String getGrade(Model model,@PathVariable(value = "account")String account,
                           @RequestParam(value = "param1",defaultValue = "")String semester,
                           @RequestParam(value = "param2",defaultValue = "")String expName,
                           @RequestParam(value = "seeAll",defaultValue = "1")Integer seeAll){
        List<Report> selectedReports = null;
        model.addAttribute("allReports",reportService.getReportViewObjects(reportService.findByAccount(account)));
        model.addAttribute("semesters",expService.getAllSemester());
        model.addAttribute("expNames",expService.getAllExp());
        if(seeAll != 1){
            if(!reportService.getReportsForStudent(account,semester,expName).isEmpty()){
                selectedReports = reportService.getReportsForStudent(account,semester,expName);
                model.addAttribute("selectedVos",reportService.getReportViewObjects(selectedReports));
            }
        }
        model.addAttribute("seeAll",seeAll);
        model.addAttribute("selected",selectedReports);
        return "html-student/grade-query";
    }

    @RequestMapping(path = "/labCenter",method = {RequestMethod.GET,RequestMethod.POST})
    public String labCenter(Model model,@RequestParam("courseId")Integer courseId,
                            @RequestParam("account")String account){
        ViewObject courseVo = new ViewObject();
        courseVo.set("student",userService.getUserbyAccount(account));
        courseVo.set("course",courseService.getCourseById(courseId));
        courseVo.set("exp",expService.getExpById(courseService.getCourseById(courseId).getExpId()));
        model.addAttribute("courseVo",courseVo);
        return "html-student/lab-center";
    }

    @RequestMapping(path = "{account}/courseCenter",method = {RequestMethod.GET,RequestMethod.POST})
    public String courseCenter(Model model,@PathVariable("account")String account){
        List<Course> validCourses = null;
        if(!courseService.getValidCourse().isEmpty()){
            validCourses = courseService.getValidCourse();
            model.addAttribute("courseVos",courseService.getViewObjectsForCourse(validCourses,account));
        }
        model.addAttribute("validCourses",validCourses);
        return "html-student/course-center";
    }

    //operate = 1代表选课,operate = 0代表退选
    @RequestMapping(path = "/selectCourse",method = RequestMethod.POST)
    @ResponseBody
    public String selectCourse(@RequestParam(value = "operate")Integer operate,
                               @RequestParam(value = "courseId")Integer courseId,
                               @RequestParam(value = "studentId")Integer studentId){
        Map<String,Object> map =new HashMap<>();
        if(operate.equals(1)){
            if(!courseService.whetherSubCourse(studentId,courseId)){
                if(courseService.subscribeCourse(studentId,courseId)){
                    map.put("selectMsg","选课成功！");
                    return ExperimentUtil.getJSONString(0,map);
                }else {
                    map.put("selectMsg","课堂容量已满");
                    return ExperimentUtil.getJSONString(1,map);
                }
            }else {
                map.put("selectMsg","已选过此课");
                return ExperimentUtil.getJSONString(1,map);
            }

        }else {
            if(courseService.whetherSubCourse(studentId,courseId)){
                courseService.cancelCourse(studentId,courseId);
                map.put("selectMsg","退选成功");
                return ExperimentUtil.getJSONString(0,map);
            }else {
                map.put("selectMsg","你还没有选过此课，无法退选");
                return ExperimentUtil.getJSONString(1,map);
            }
        }
    }

    @RequestMapping(path = "/{account}/courseSelected",method = {RequestMethod.GET,RequestMethod.POST})
    public String courseSelected(Model model,@PathVariable("account")String account){
        List<Course> list = null;
        User user = userService.getUserbyAccount(account);
        if(!courseService.getCoursesOfStudent(user.getId()).isEmpty()){
            list = courseService.getCoursesOfStudent(user.getId());
            model.addAttribute("selectedVos",courseService.getViewObjectsForCourse(list,account));
        }
        model.addAttribute("selected",list);
        return "html-student/course-selected";
    }

    @RequestMapping(path = "/{account}/report",method = {RequestMethod.GET,RequestMethod.POST})
    public String report(Model model,@PathVariable("account")String account){
        List<Course> list = courseService.getCoursesOfStudent(userService.getUserbyAccount(account).getId());
        List<Report> reportList = reportService.findByAccount(account);
        List<Course> unfinish = new ArrayList<>();
        List<Course> finish = new ArrayList<>();
        for(Course course : list){
            if(!reportService.isFinish(reportList,course)){
                unfinish.add(course);
            }else {
                finish.add(course);
            }
        }
        model.addAttribute("unfinish",courseService.getViewObjectsForCourse(unfinish,account));
        model.addAttribute("finish",courseService.getViewObjectsForCourse(finish,account));
        return "html-student/report-to-finish";
    }

    @GetMapping(path = "/viewMyReport")
    public String viewReport(Model model,@RequestParam("reportId")Integer id){
        model.addAttribute("report",reportService.findById(id));
        return "html-student/view-report";
    }
}
