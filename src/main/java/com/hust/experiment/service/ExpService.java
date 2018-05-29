package com.hust.experiment.service;

import com.hust.experiment.dao.ExpDao;
import com.hust.experiment.model.Exp;
import com.hust.experiment.model.User;
import com.hust.experiment.model.ViewObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExpService {
    @Autowired
    ExpDao expDao;

    @Autowired
    UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(ExpService.class);

    public boolean addExperiment(String expName,int teacherId,int period,double credit,String semester){
        try{
            Exp exp = new Exp();
            exp.setExpName(expName);
            exp.setTeacherId(teacherId);
            exp.setPeriod(period);
            exp.setCredit(credit);
            exp.setSemester(semester);
            expDao.addExperiment(exp);
            return true;
        }catch (Exception e){
            logger.error(e.getMessage());
            return false;
        }
    }

    public List<Exp> getAllExp(){
        return expDao.getAllExperiment();
    }

    public List<User> getAllTeacher(){
        List<User> list = new ArrayList<>();
        for(Integer teacherId : expDao.selectAllTeacher()){
            list.add(userService.getUserById(teacherId));
        }
        return list;
    }

    public Exp getExpById(int id){
        return expDao.selectExperimentById(id);
    }

    public List<String> getAllSemester(){
        List<String> list = expDao.selectAllSemester();
        return list;
    }

    public List<Exp> getExpByParams(String semesterParam,String expParam,String teacherParam){
        List<Exp> list = new ArrayList<>();
        if(!semesterParam.equals("")){
            list.addAll(getExpBySemester(semesterParam));
        }
        if(!expParam.equals("")){
            list.addAll(expDao.selectExperimentByExpName(expParam));
        }
        if(!teacherParam.equals("")){
            list.addAll(getExpByTeacherName(teacherParam));
        }
        return list;
    }

    public List<Exp> getExpBySemester(String semester){
        return expDao.selectExperimentBySemester(semester);
    }

    public List<Exp> getExpByTeacherName(String teacherName){
        List<User> list = userService.selectTeacherByName(teacherName);
        List<Exp> expList = new ArrayList<>();
        if(!list.isEmpty()){
            for(User teacher : list){
                expList.addAll(expDao.selectExperimentByTeacher(teacher.getId()));
            }
        }
        return expList;
    }

    public List<ViewObject> getExpViewObjects(List<Exp> expList){
        List<ViewObject> vos = new ArrayList<>();
        for(Exp exp : expList){
            ViewObject vo = new ViewObject();
            vo.set("course",exp);
            vo.set("teacher",userService.getUserById(exp.getTeacherId()));
            vos.add(vo);
        }
        return vos;
    }

    public boolean deleteExp(int id){
        try{
            expDao.deleteExperimentById(id);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
