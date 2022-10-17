package org.spring.springboot.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.spring.springboot.domain.Dataset;
import org.spring.springboot.domain.Record;
import org.spring.springboot.domain.model;

import java.util.List;
import java.util.Map;

public interface RecordMapper {
    @Insert("insert into t_record (model,owner,acc,type,dataset)values " +
            "(#{record.model},#{record.owner},#{record.acc},#{record.type},#{record.dataset})")
    int insert(@Param("record") Record record);

    @Select("SELECT * FROM `t_record` where owner=#{record.owner} and model=#{record.model} and type=#{record.type}")
    List<Record> select(@Param("record") Map record);
}
