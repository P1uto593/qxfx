package org.spring.springboot.dao;

import org.apache.ibatis.annotations.*;
import org.spring.springboot.domain.Admin;
import org.spring.springboot.domain.Record;
import org.spring.springboot.domain.model;

import java.util.List;
import java.util.Map;

public interface ModelMapper {
    @Insert("insert into t_model (name,type,owner,acc,cost)values (#{record.name},#{record.type},#{record.owner},#{record.acc},#{record.cost})")
    int insert(@Param("record") model record);

    @Select("SELECT * FROM `t_model` where owner=#{record.owner} and name=#{record.model}")
    List<model> selectOne(@Param("record") Map record);

    @Select("SELECT * FROM `t_model` where owner=#{record.owner}")
    List<model> select(@Param("record") Map record);

    @Update("update t_model set acc=#{record.acc},cost=#{cost} where name=#{record.model} and owner=#{record.owner}")
    void update(@Param("record") Record record,@Param("cost")Double cost);

    @Delete("delete from t_model where name=#{record.name} and owner=#{record.owner}")
    void delete(@Param("record") Map record);

    @Select("SELECT acc FROM `t_record` where owner=#{record.owner} and model=#{record.name}")
    List<Double> selectEcharts(@Param("record") Map record);
}
