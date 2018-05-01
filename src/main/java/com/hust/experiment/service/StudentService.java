package com.hust.experiment.service;

import com.hust.experiment.controller.UserController;
import com.hust.experiment.dao.StudentDao;
import com.hust.experiment.model.Student;
import com.hust.experiment.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudentService {
    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    @Autowired
    StudentDao studentDao;

    @Autowired
    UserService userService;

    //批量注册
    public Map<String,Object> batchRegistration(List<Student> studentList){
        Map<String,Object> map  = new HashMap();
        int number = 0;
        try{
            for(Student student : studentList){
                Map<String,Object> regMap = userService.register(student.getStuId(),"123456","123456");
                if(regMap.isEmpty()||regMap.size() == 0){
                    User user = userService.getUserbyAccount(student.getStuId());
                    userService.updateUserMessage(user.getAccount(),user.getUrl(),student.getName(),student.getClassname(),student.getAcademy());
                }else{
                    number ++;
                    map.putAll(regMap);
                }
            }
            String result = (studentList.size() - number) + "/" + studentList.size() + "条记录添加成功！";
            map.put("batchregMsg",result);
            return map;
        }catch (Exception e){
            logger.error(e.getMessage());
            map.put("registerMsg","注册失败");
            return map;
        }
    }

    public void addStudent(Student student){
        if(!ifExistStudent(student)){
            studentDao.addStudent(student);
        }
    }

    public Student selectByStudentId(String account){
        return studentDao.selectStudentByStudentId(account);
    }

    public Student selectLastStudent(){
        int size = studentDao.getAllStudents().size();
        if(size == 0){
            return null;
        }else{
            return studentDao.selectStudentById(size - 1);
        }
    }

    public List<Student> selectNewStudent(int offset){
        return studentDao.selectNewImportStudent(offset);
    }

    public boolean ifExistStudent(Student student){
        if(studentDao.selectStudentByStudentId(student.getStuId()) != null){
            return true;
        }
        return false;
    }
}
