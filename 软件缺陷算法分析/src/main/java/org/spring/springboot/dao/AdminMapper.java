package org.spring.springboot.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.spring.springboot.domain.Admin;

import java.util.List;

public interface AdminMapper {

    @Insert("insert into t_admin (username,password)values (#{record.username},#{record.password})")
    int insert(@Param("record") Admin record);

    @Select("select * from t_admin where username=#{admin.username}")
    List<Admin> selectByUsername(@Param("admin") Admin admin);
    @Select("select * from t_admin where username=#{admin.username} and password=#{admin.password}")
    List<Admin> selectByUser(@Param("admin")Admin admin);
}