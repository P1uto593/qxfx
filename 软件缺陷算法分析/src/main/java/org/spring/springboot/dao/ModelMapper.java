package org.spring.springboot.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.spring.springboot.domain.Admin;
import org.spring.springboot.domain.model;

import java.util.List;
import java.util.Map;

public interface ModelMapper {
    @Insert("insert into t_model (name,type,owner)values (#{record.name},#{record.type},#{record.owner})")
    int insert(@Param("record") model record);

    @Select("SELECT * FROM `t_model` where owner=#{record.owner}")
    List<model> select(@Param("record") Map record);
}
