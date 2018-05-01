package com.hust.experiment.dao;

import com.hust.experiment.model.Exp;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ExpDao {
    String TABLE_NAME = "exp";
    String SELECT_FIELDS = " id, exp_name, period, teacher_id, credit";
    String INSERT_FIELDS = " exp_name, period, teacher_id, credit";

    @Insert({"insert into ",TABLE_NAME,"(",INSERT_FIELDS ,") values (#{expName},#{period},#{teacherId},#{credit})"})
    int addExperiment(Exp exp);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
    Exp selectExperimentById(int id);

    @Delete({"delete from ", TABLE_NAME, " where id=#{id}"})
    void deleteExperimentById(int id);

    List<Exp> getAllExperiment();
}
