package org.spring.springboot.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.spring.springboot.domain.Dataset;
import org.spring.springboot.domain.model;

import java.util.List;
import java.util.Map;

public interface DatasetMapper {
    @Insert("insert into t_dataset (name,type,owner)values (#{record.name},#{record.type},#{record.owner})")
    int insert(@Param("record") Dataset record);
    @Select("SELECT * FROM `t_dataset` where owner=#{record.owner}")
    List<Dataset> select(@Param("record") Map record);

}
