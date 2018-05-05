package com.hust.experiment.dao;

import com.hust.experiment.model.Course;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CourseDao {
    String TABLE_NAME = "course";
    String SELECT_FIELDS = " id, exp_id, class_time, classroom, count, status";
    String INSERT_FIELDS = " exp_id, class_time, classroom, count, status";

    @Insert({"insert into ",TABLE_NAME,"(",INSERT_FIELDS ,") values (#{expId},#{classTime},#{classroom},#{count},#{status})"})
    int addCourse(Course course);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
    Course selectCourse(int id);

    @Delete({"delete from ", TABLE_NAME, " where id=#{id}"})
    void deleteCourse(int id);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where exp_id=#{id}"})
    List<Course> selectCourseByExp(int id);
}
