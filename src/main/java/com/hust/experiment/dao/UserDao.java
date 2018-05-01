package com.hust.experiment.dao;

import com.hust.experiment.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserDao {
    String TABLE_NAME = "user";
    String SELECT_FIELDS = " id, account, password, position, url, salt, name, classname, academy, nickname";
    String INSERT_FIELDS = " account, password, position, url, salt, name, classname, academy, nickname";

    @Insert({"insert into ",TABLE_NAME,"(",INSERT_FIELDS ,") values (#{account},#{password},#{position},#{url},#{salt},#{name},#{classname},#{academy},#{nickname})"})
    int addUser(User user);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
    User selectUserById(int id);

    @Select({"select", SELECT_FIELDS,"from",TABLE_NAME, "where account = #{account}"})
    User selectByAccount(String account);

    @Update({"update ", TABLE_NAME, " set password=#{password} where id=#{id}"})
    void updatePassword(User user);

    @Delete({"delete from ", TABLE_NAME, " where id=#{id}"})
    void deleteUserById(int id);

    void updateUserMessage(@Param("account") String account,@Param("url") String url,@Param("name") String name,
                           @Param("classname")String classname,@Param("academy") String academy,@Param("nickname")String nickname);

    List<User> getAllUser();

    @Select({"select", SELECT_FIELDS,"from",TABLE_NAME, "where classname = #{classname}"})
    List<User> selectByClass(String classname);

    @Select({"select", SELECT_FIELDS,"from",TABLE_NAME, "where academy = #{academy}"})
    List<User> selectByAcademy(String academy);
}
