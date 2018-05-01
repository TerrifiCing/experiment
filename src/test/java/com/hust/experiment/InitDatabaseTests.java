package com.hust.experiment;

import com.hust.experiment.dao.UserDao;
import com.hust.experiment.model.User;
import com.hust.experiment.service.ExpService;
import com.hust.experiment.service.MailService;
import com.hust.experiment.service.ReportService;
import com.hust.experiment.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;


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
            mailService.addMail(6,"公告内容","公告标题");
        }
        //添加实验课
        expService.addExperiment("物理实验",7,32,2.5);
        expService.addExperiment("电路实验",8,28,2);
        expService.addExperiment("数字电路实验",9,48,4);
        //添加课程

        //添加实验报告
    }
}
