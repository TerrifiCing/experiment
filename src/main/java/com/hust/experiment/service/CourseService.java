package com.hust.experiment.service;

import com.hust.experiment.dao.CourseDao;
import com.hust.experiment.model.Course;
import com.hust.experiment.model.Report;
import com.hust.experiment.model.User;
import com.hust.experiment.model.ViewObject;
import com.hust.experiment.util.ExperimentUtil;
import com.hust.experiment.util.JedisAdapter;
import com.hust.experiment.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class CourseService {
    @Autowired
    CourseDao courseDao;

    @Autowired
    JedisAdapter jedisAdapter;

    @Autowired
    UserService userService;

    @Autowired
    ExpService expService;

    @Autowired
    ReportService reportService;

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

    public Course getCourseById(int id){
        return courseDao.selectCourse(id);
    }

    public List<Course> getAllCourse(){
        return courseDao.selectAllCourse();
    }

    public List<Course> getValidCourse(){
        List<Course> list = courseDao.selectAllZeroCourse();
        if(!list.isEmpty()){
             for(int i = 0;i < list.size();i ++){
                 Date date = new Date();
                 Course course = list.get(i);
                 if(date.after(course.getClassTime())){
                   course.setStatus(1);
                   list.remove(course);
                   i --;
                 }
             }
        }
        return list;
    }

    public List<ViewObject> getViewObjectsForCourse(List<Course> list,String account){
        List<ViewObject> vos = new ArrayList<>();
        List<Report> reportList = reportService.findByAccount(account);
        for(Course course : list){
            ViewObject vo = new ViewObject();
            User user = userService.getUserbyAccount(account);
            vo.set("exp",expService.getExpById(course.getExpId()));
            vo.set("teacher",userService.getUserById(expService.getExpById(course.getExpId()).getTeacherId()));
            vo.set("course",course);
            vo.set("count",jedisAdapter.scard(RedisKeyUtil.getSubscribeKey(course.getId())));
            for(Report report : reportList){
                if(report.getCourseId() == course.getId()){
                    vo.set("report",report);
                }
            }
            if(user.getPosition().equals("学生")){
                vo.set("student",userService.getUserbyAccount(account));
                if(whetherSubCourse(user.getId(),course.getId())){
                    vo.set("sub","已选");
                }else {
                    vo.set("sub","选课");
                }
            }
            vos.add(vo);
        }
        return vos;
    }

    public List<Course> getCourseByExpId(int expId){
        return courseDao.selectCourseByExp(expId);
    }

    public void deleteCourse(int id){
        if(existCourse(id)){
            courseDao.deleteCourse(id);
        }
    }

    //把选了课的学生的学号加入Redis，这里的studentId是序号，account才是学号
    public boolean subscribeCourse(int studentId,int courseId){
        //创建RedisKey
        String subscribeKey = RedisKeyUtil.getSubscribeKey(courseId);
        //判断课堂容量是否已满
        if(jedisAdapter.scard(subscribeKey) < courseDao.selectCourse(courseId).getCount()){
            //将学号加入SUBSCRIBE_OF_COURSE中
            jedisAdapter.sadd(subscribeKey,userService.getUserById(studentId).getAccount());
            //将课程id加入COURSE_OF_STUDENT中
            addCoursesForStudent(studentId,courseId);
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
        removeCoursesForStudent(studentId,courseId);
    }

    //
    public void addCoursesForStudent(int studentId,int courseId){
        String addKey = RedisKeyUtil.getAddCourseKey(studentId);
        jedisAdapter.sadd(addKey,String.valueOf(courseId));
    }

    public void removeCoursesForStudent(int studentId,int courseId){
        String addKey = RedisKeyUtil.getAddCourseKey(studentId);
        jedisAdapter.srem(addKey,String.valueOf(courseId));
    }

    public List<Course> getCoursesOfStudent(int studentId){
        String addKey = RedisKeyUtil.getAddCourseKey(studentId);
        Set<String> set = jedisAdapter.getMembersOfKey(addKey);
        List<Course> list = new ArrayList<>();
        for(String id : set){
            int courseId = Integer.valueOf(id);
            list.add(getCourseById(courseId));
        }
        return list;
    }
}
