package com.hust.experiment.service;


import com.hust.experiment.dao.ReportDao;
import com.hust.experiment.model.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {

    @Autowired
    ReportDao reportDao;

    @Autowired
    StudentService studentService;

    @Autowired
    UserService userService;

    public Report findById(int id){
        return reportDao.selectReportById(id);
    }

    public List<Report> findByStudentId(int studentId){
        return reportDao.selectReportByStudentId(studentId);
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

}
