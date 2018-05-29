package com.hust.experiment.service;

import com.hust.experiment.dao.LoginTicketDao;
import com.hust.experiment.dao.UserDao;
import com.hust.experiment.model.Exp;
import com.hust.experiment.model.LoginTicket;
import com.hust.experiment.model.User;
import com.hust.experiment.util.ExperimentUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class UserService {
    @Autowired
    UserDao userDao;

    @Autowired
    LoginTicketDao loginTicketDao;

    public int addNewUser(User user){
        userDao.addUser(user);
        return user.getId();
    }

    public List<User> getAllUser(){
        List<User> list = userDao.getAllUser();
        return list;
    }

    public boolean hasUser(String account){
        return userDao.selectByAccount(account) != null;
    }

    //注册
    public Map<String,Object> register(String account,String password,String rePass){
        Map<String,Object> map = new HashMap<>();
        if (account.equals("")||account == null) {
            map.put("msgAccount", "学号/学工号不能为空");
            return map;
        }else if(userDao.selectByAccount(account) != null){
            map.put("msgAccount","此学号/学工号已被注册");
            return map;
        }else if(!account.matches("(U|M|T)\\d{9}")){
            map.put("msgAccount","学号/学工号格式不正确");
            return map;
        }

        if (password.equals("")) {
            map.put("msgPassword", "密码不能为空");
            return map;
        }

        if(!rePass.equals(password)){
            map.put("msgRepass","两次输入的密码不一致");
            return map;
        }

        User user = new User();
        user.setAccount(account);
        String position = "";
        char F = account.charAt(0);
        if(F == 'U'){
            position = "学生";
        }else if(F == 'T'){
            position = "教师";
        }else if(F == 'M'){
            position = "管理员";
        }
        user.setPosition(position);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        user.setPassword(ExperimentUtil.MD5(password + user.getSalt()));
        user.setUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setName(account);
        user.setNickname(account);
        userDao.addUser(user);
        return map;
    }

    //登录
    public Map<String,Object> login(String account,String password){
        Map<String,Object> map = new HashMap<>();

        if(account.equals("")||account == null){
            map.put("msgAccount","学号/学工号不能为空");
            return map;
        }
        User user = userDao.selectByAccount(account);
        if(user == null){
            map.put("msgAccount","该学号/学工尚未注册");
            return map;
        }
        if(password.equals("")||password == null){
            map.put("msgPwd","密码不能为空");
            return map;
        }
        if(!ExperimentUtil.MD5(password + user.getSalt()).equals(user.getPassword())){
            map.put("msgPed","密码不正确");
            return map;
        }
        String ticket = addLoginTicket(getUserbyAccount(account).getId());
        map.put("ticket",ticket);

        return map;
    }

    public List<User> getUserByParams(String accountParam,String accademyParam,String classParam){
        List<User> list = new ArrayList<>();
        if(!accountParam.equals("")){
            //如果输入了准确的学号，则查出此用户
            if(accountParam.length() == 10&&hasUser(accountParam)){
                    list.add(userDao.selectByAccount(accountParam));
            }else {
                char head = accountParam.toUpperCase().charAt(0);
                if(head == 'M'||accountParam.equals("管理员")){
                    list.addAll(userDao.selectByPosition("管理员"));
                }else if(head == 'T'||accountParam.equals("教师")){
                    list.addAll(userDao.selectByPosition("教师"));
                }else if(head == 'U'||accountParam.equals("学生")){
                    list.addAll(userDao.selectByPosition("学生"));
                }
            }
        }
        //如果学院参数不为空，传参进行查找
        if(!accademyParam.equals("")){
            List<User> findList = userDao.selectByAcademy(accademyParam);
            if(findList != null&&!findList.isEmpty()){
                list.addAll(findList);
            }
        }
        //如果班级参数不为空，传参进行查找
        if(!classParam.equals("")){
            List<User> findList2 = userDao.selectByClass(classParam);
            if(findList2 != null&&!findList2.isEmpty()){
                list.addAll(findList2);
            }
        }
        return list;
    }

    public User getUserbyAccount(String account){
        return userDao.selectByAccount(account);
    }

    public User getUserById(int id){
        return userDao.selectUserById(id);
    }

    public boolean deleteUser(String account){
        User user = userDao.selectByAccount(account);
        userDao.deleteUserById(user.getId());
        return userDao.selectByAccount(account) == null;
    }

    //更新
    public void updateUserMessage(String account,String url,String name,String classname,String academy,String nickname){
        userDao.updateUserMessage(account,url,name,classname,academy,nickname);
    }

    public String addLoginTicket(int userId){
        LoginTicket loginTicket = new LoginTicket();
        Date date = new Date();
        date.setTime(date.getTime() + 1000*24*3600);
        loginTicket.setUserId(userId);
        loginTicket.setStatus(0);
        loginTicket.setExpired(date);
        loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-",""));
        loginTicketDao.addTicket(loginTicket);
        return loginTicket.getTicket();
    }

    public void logout(String ticket){
        loginTicketDao.updateStatus(ticket,1);
    }

    public boolean updatePassword(String password,int id){
        try{
            User user = userDao.selectUserById(id);
            user.setPassword(ExperimentUtil.MD5(password + user.getSalt()));
            userDao.updatePassword(user);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public String saveFile(MultipartFile file) throws IOException{
        int dotPos = file.getOriginalFilename().lastIndexOf(".");
        if(dotPos < 0){
            return null;
        }
        String fileExt = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();
        String fileType = null;
        if(ExperimentUtil.ifFileAllowed(fileExt) == null){
            return null;
        }else{
            fileType = ExperimentUtil.ifFileAllowed(fileExt);
        }
        String fileName = UUID.randomUUID().toString().replaceAll("-","") + "." + fileExt;
        Files.copy(file.getInputStream(),new File(ExperimentUtil.FILE_DIR + fileName).toPath(),
                StandardCopyOption.REPLACE_EXISTING);
        return ExperimentUtil.EXPERIMENT_DOMAIN + "/getFile?name=" + fileName;
    }

    public List<User> selectUserByClass(String classname){
        return userDao.selectByClass(classname);
    }

    public List<User> selectUserByAcademy(String academy){
        return userDao.selectByAcademy(academy);
    }

    public List<User> selectUserByPosition(String position){return userDao.selectByPosition(position);}

    public List<User> selectTeacherByName(String teacherName){
        List<User> list = new ArrayList<>();
        if(!userDao.selectByName(teacherName).isEmpty()){
            list.addAll(userDao.selectByName(teacherName));
        }
        return list;
    }

    public List<String> getAllAcademy(){
        return userDao.selectAllAcademy();
    }

    public List<String> getAllClass(){
        return userDao.selectAllClass();
    }

}
