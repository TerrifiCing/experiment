package com.hust.experiment;

import com.hust.experiment.dao.ExpDao;
import com.hust.experiment.dao.MailDao;
import com.hust.experiment.dao.UserDao;
import com.hust.experiment.model.Exp;
import com.hust.experiment.model.Mail;
import com.hust.experiment.model.User;
import com.hust.experiment.service.MailService;
import com.hust.experiment.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExperimentApplication.class)
public class UserTests {
    @Autowired
    UserDao userDao;

    @Autowired
    MailDao mailDao;

    @Autowired
    ExpDao expDao;

    @Autowired
    MailService mailService;

    @Autowired
    UserService userService;

    @Test
    public void test1(){
        List<User> list = userService.getAllUser();
        for(User user : list){
            userService.updateUserMessage(user.getAccount(),user.getUrl(),user.getName(),"电信1406","电信学院");
        }
    }

    @Test
    public void test2(){
        for(int i = 0;i < 10;i ++){
            Mail mail = new Mail();
            mail.setContent("公告"+ (i+ 1) + "的内容");
            mail.setType("公告");
            mail.setTitle("公告" + (i + 1));
            mail.setFromId(1);
            mail.setTime(new Date());
            mailDao.addMail(mail);
        }
    }

    @Test
    public void test3(){
        Exp exp = new Exp();
        exp.setExpName("物理实验");
        exp.setCredit(2.5);
        exp.setPeriod(32);
        exp.setTeacherId(10);
        expDao.addExperiment(exp);
    }

    @Test
    public void test4(){
        List<Mail> list = mailService.getMails(0,"公告");
        System.out.println(list.size());
        assert list.size() == 11;
    }

    @Test
    public void test5(){
        for(int i = 0;i < 5;i ++){
            userService.register("T20001300" + (i + 1),"123456","123456");
        }
        userService.register("M201413407","123456","123456");
    }

    @Test
    public void test6(){
        System.out.println(expDao.getAllExperiment().size());
    }

    @Test
    public void test7(){
        User user = userDao.selectUserById(1);
        user.setPassword("123456");
        userDao.updatePassword(user);
    }
}
