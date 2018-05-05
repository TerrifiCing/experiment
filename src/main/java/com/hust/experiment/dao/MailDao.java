package com.hust.experiment.dao;

import com.hust.experiment.model.Mail;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MailDao {
    String TABLE_NAME = "mail";
    String SELECT_FIELDS = " id, from_id, to_id, type, content, time, title, about";
    String INSERT_FIELDS = " from_id, to_id, type, content, time, title, about";

    @Insert({"insert into ",TABLE_NAME,"(",INSERT_FIELDS ,") values (#{fromId},#{toId},#{type},#{content},#{time},#{title},#{about})"})
    int addMail(Mail mail);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
    Mail selectMailById(int id);

    @Delete({"delete from ", TABLE_NAME, " where id=#{id}"})
    void deleteMailById(int id);

    List<Mail> getAllMail(@Param("limit") int limit,@Param("type") String type);
}
