package com.hust.experiment.service;

import com.hust.experiment.dao.CourseDao;
import com.hust.experiment.model.Course;
import com.hust.experiment.util.JedisAdapter;
import com.hust.experiment.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseService {
    @Autowired
    CourseDao courseDao;

    @Autowired
    JedisAdapter jedisAdapter;

    @Autowired
    UserService userService;

    public void addCourse(Course course){
        courseDao.addCourse(course);
    }

    public boolean existCourse(int id){
        if(courseDao.selectCourse(id) != null){
            return true;
        }else {
            return false;
        }
    }

    public void deleteCourse(int id){
        if(existCourse(id)){
            courseDao.deleteCourse(id);
        }
    }

    //把选了课的学生的学号加入Redis，这里的studentId是序号，account才是学号
    public boolean subscribeCourse(int studentId,int courseId){
        String subscribeKey = RedisKeyUtil.getSubscribeKey(courseId);
        if(jedisAdapter.scard(subscribeKey) < courseDao.selectCourse(courseId).getCount()){
            jedisAdapter.sadd(subscribeKey,userService.getUserById(studentId).getAccount());
            return true;
        }else {
            return false;
        }
    }

    //判断学生是否选了课
    public boolean whetherSubCourse(int studentId,int courseId){
        String subscribeKey = RedisKeyUtil.getSubscribeKey(courseId);
        return jedisAdapter.sismember(subscribeKey,userService.getUserById(studentId).getAccount());
    }

    //取消选课
    public void cancelCourse(int studentId,int courseId){
        String subscribeKey = RedisKeyUtil.getSubscribeKey(courseId);
        jedisAdapter.srem(subscribeKey,userService.getUserById(studentId).getAccount());
    }
}
