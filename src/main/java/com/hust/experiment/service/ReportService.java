package com.hust.experiment.service;


import com.hust.experiment.dao.ExpDao;
import com.hust.experiment.dao.ReportDao;
import com.hust.experiment.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    ReportDao reportDao;

    @Autowired
    StudentService studentService;

    @Autowired
    UserService userService;

    @Autowired
    CourseService courseService;

    @Autowired
    ExpService expService;

    @Autowired
    ExpDao expDao;

    public Report findById(int id){
        return reportDao.selectReportById(id);
    }

    public boolean existReport(int id){
        return findById(id) != null;
    }

    public void addReport(Report report){
        if(!existReport(report.getId())){
            reportDao.addReport(report);
        }
    }

    public List<Report> findByStudentId(int studentId){
        return reportDao.selectReportByStudentId(studentId);
    }

    public List<Report> getAllReports(){
        return reportDao.getAllReports();
    }

    public List<ViewObject> getReportViewObjects(List<Report> list){
        List<ViewObject> vos = new ArrayList<>();
        for(Report report : list){
            ViewObject vo = new ViewObject();
            vo.set("report",report);
            vo.set("student",userService.getUserById(report.getStudentId()));
            vo.set("exp",expService.getExpById(courseService.getCourseById(report.getCourseId()).getExpId()));
            vos.add(vo);
        }
        return vos;
    }

    public List<Course> getAllCourseOfReport(){
        List<Course> list = new ArrayList<>();
        for(Integer id : reportDao.selectAllCourseId()){
            list.add(courseService.getCourseById(id));
        }
        return list;
    }

    public List<Exp> getAllExpOfReport(){
        List<Exp> list = new ArrayList<>();
        for(Course course : getAllCourseOfReport()){
            if(!list.contains(expService.getExpById(course.getExpId()))){
                list.add(expService.getExpById(course.getExpId()));
            }
        }
        return list;
    }

    public List<String> getAllAcademiesOfReport(){
        List<String> list = new ArrayList<>();
        for(Integer id : reportDao.selectAllStudentId()){
            String academy = userService.getUserById(id).getAcademy();
            if(!list.contains(academy)){
                list.add(academy);
            }
        }
        return list;
    }

    public List<Report> getReportsByParams(String account,String academy,String expName){
        List<Report> list = new ArrayList<>();
        if(!account.equals("")){
            if(!findByAccount(account).isEmpty()){
                list.addAll(findByAccount(account));
            }
        }
        if(!academy.equals("")){
            List<String> academies = getAllAcademiesOfReport();
            if(academies.contains(academy)){
                for(User user : userService.selectUserByAcademy(academy)){
                    if(!findByAccount(user.getAccount()).isEmpty()){
                        list.addAll(findByAccount(user.getAccount()));
                    }
                }
            }
        }
        if(!expName.equals("")){
            for(Exp exp : expDao.selectExperimentByExpName(expName)){
                list.addAll(findByExp(exp));
            }
        }
        return list;
    }

    //按照学号查询
    public List<Report> findByAccount(String account){
        int studentId = userService.getUserbyAccount(account).getId();
        return findByStudentId(studentId);
    }

    //按照课程查询
    public List<Report> findByCourseId(int courseId){
        return reportDao.selectReportByCourseId(courseId);
    }

    public List<Report> findByExp(Exp exp){
        List<Report> list = new ArrayList<>();
        for(Course course : courseService.getCourseByExpId(exp.getId())){
            list.addAll(findByCourseId(course.getId()));
        }
        return list;
    }
}
