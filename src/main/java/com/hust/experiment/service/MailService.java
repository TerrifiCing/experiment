package com.hust.experiment.service;

import com.hust.experiment.dao.MailDao;
import com.hust.experiment.model.Mail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MailService {
    @Autowired
    MailDao mailDao;

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    public boolean addMail(int fromId,String content,String title,String about){
        try{
            Mail mail = new Mail();
            mail.setTime(new Date());
            mail.setType("公告");
            mail.setFromId(fromId);
            mail.setContent(content);
            mail.setTitle(title);
            mail.setAbout(about);
            mailDao.addMail(mail);
            return true;
        }catch (Exception e){
            logger.error(e.getMessage());
            return false;
        }
    }

    public boolean addMail(int fromId,int toId,String content,String title,String about){
        try{
            Mail mail = new Mail();
            mail.setFromId(fromId);
            mail.setToId(toId);
            mail.setContent(content);
            mail.setType("私信");
            mail.setTime(new Date());
            mail.setTitle(title);
            mail.setAbout(about);
            mailDao.addMail(mail);
            return true;
        }catch (Exception e){
            logger.error(e.getMessage());
            return false;
        }
    }

    public List<Mail> getMails(int limit,String type){
        List<Mail> mails = mailDao.getAllMail(limit,type);
        return mails;
    }

    public void delete(int id){
        mailDao.deleteMailById(id);
    }
}
