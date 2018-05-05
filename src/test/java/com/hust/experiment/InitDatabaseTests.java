package com.hust.experiment;

import com.hust.experiment.dao.UserDao;
import com.hust.experiment.model.Course;
import com.hust.experiment.model.Report;
import com.hust.experiment.model.User;
import com.hust.experiment.service.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExperimentApplication.class)
@Sql("/init-schema.sql")
public class InitDatabaseTests {
    @Autowired
    UserDao userDao;

    @Autowired
    UserService userService;

    @Autowired
    MailService mailService;

    @Autowired
    ExpService expService;

    @Autowired
    ReportService reportService;

    @Autowired
    CourseService courseService;

    @Test
    public void initData(){
        //添加5个学生用户
        for(int i = 0;i < 5;i ++){
            userService.register(String.format("U20141300%d",i + 1),"123456","123456");
            userService.updateUserMessage(String.format("U20141300%d",i + 1),userService.getUserById(i + 1).getUrl(),"学生"+i,
                    "1406","电信学院",String.format("U20141300%d",i + 1));
        }
        
        //添加一个管理员
        userService.register("M201413407","123456","123456");
        //添加三个老师用户
        for(int i= 0;i < 3;i ++){
            userService.register(String.format("T20141301%d",i + 1),"123456","123456");
        }
        //添加公告
        for(int i = 0;i < 10;i ++){
            mailService.addMail(6,"公告内容","公告标题","数电");
        }
        //添加实验课
        expService.addExperiment("物理实验",7,32,2.5,"2018学年春季");
        expService.addExperiment("电路实验",8,28,2,"2018学年春季");
        expService.addExperiment("数字电路实验",9,48,4,"2018学年秋季");
        //添加课程
        Course course = new Course();
        course.setExpId(1);
        course.setClassroom("南一楼中203");
        course.setClassTime(new Date());
        course.setCount(50);
        course.setStatus(0);
        courseService.addCourse(course);
        //添加实验报告
        for(int i = 0;i < 5;i ++){
            Report report = new Report();
            report.setCourseId(1);
            report.setDataUrl("www.dataUrl" + i + ".com");
            report.setReportUrl("www.reportUrl" + i + ".com");
            report.setStudentId(i + 1);
            report.setScore("8" + i);
            report.setStatus(1);
            reportService.addReport(report);
        }
    }
}
