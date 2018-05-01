package com.hust.experiment.service;

import com.hust.experiment.dao.ExpDao;
import com.hust.experiment.model.Exp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpService {
    @Autowired
    ExpDao expDao;

    private static final Logger logger = LoggerFactory.getLogger(ExpService.class);

    public boolean addExperiment(String expName,int teacherId,int period,double credit){
        try{
            Exp exp = new Exp();
            exp.setExpName(expName);
            exp.setTeacherId(teacherId);
            exp.setPeriod(period);
            exp.setCredit(credit);
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
}
