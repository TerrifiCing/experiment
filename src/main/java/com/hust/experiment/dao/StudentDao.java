package com.hust.experiment.dao;

import com.hust.experiment.model.Student;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StudentDao {
    String TABLE_NAME = "student";
    String SELECT_FIELDS = " id,  name, stu_id, classname, academy";
    String INSERT_FIELDS = "  name, stu_id, classname, academy";

    List<Student> getAllStudents();

    @Insert({"insert into",TABLE_NAME,"(",INSERT_FIELDS,") values (#{name},#{stuId},#{classname},#{academy})"})
    int addStudent(Student student);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where stu_id = #{account}"})
    Student selectStudentByStudentId(String account);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id = #{id}"})
    Student selectStudentById(int id);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id > #{offset}"})
    List<Student> selectNewImportStudent(int offset);
}
