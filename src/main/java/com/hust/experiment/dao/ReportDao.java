package com.hust.experiment.dao;


import com.hust.experiment.model.Report;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ReportDao {

    String TABLE_NAME = "report";
    String SELECT_FIELDS = " id, student_id, course_id, score, report_url, data_url, status";
    String INSERT_FIELDS = " student_id, course_id, score, report_url, data_url, status";


    @Insert({"insert into ",TABLE_NAME,"(",INSERT_FIELDS ,") values (#{studentId},#{courseId},#{score},#{reportUrl},#{dataUrl},#{status})"})
    int addReport(Report report);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
    Report selectReportById(int id);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where student_id=#{id}"})
    List<Report> selectReportByStudentId(int id);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where course_id=#{id}"})
    List<Report> selectReportByCourseId(int id);

    @Delete({"delete from ", TABLE_NAME, " where id=#{id}"})
    void deleteReport(int id);

    List<Report> getAllReports();

    List<Integer> selectAllCourseId();

    List<Integer> selectAllStudentId();

}
