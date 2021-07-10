package com.boot.oss.mapper;

import com.boot.oss.entity.ObjInfoEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * @author bmj
 */
@Mapper
public interface ObjInfoMapper {

    @Insert("insert into oss_obj_info(obj_id, obj_name, obj_suffix, obj_path, pre_obj_id, created_by, last_updated_by, bucket) values (#{objId}, #{objName}, #{objSuffix}, #{objPath}, #{preObjId}, #{createdBy}, #{lastUpdatedBy}, #{bucket})")
    void insertObjInfo(ObjInfoEntity objInfoEntity) throws Exception;

    @Select("SELECT * FROM oss_obj_info where obj_id = #{objId}")
    @Results({
            @Result(property = "objId", column = "obj_id"),
            @Result(property = "objName", column = "obj_name"),
            @Result(property = "objSuffix", column = "obj_suffix"),
            @Result(property = "objPath", column = "obj_path"),
            @Result(property = "preObjId", column = "pre_obj_id"),
            @Result(property = "bucket", column = "bucket")})
    Map<String, Object> selectObjInfo(@Param("objId") String objId) throws Exception;

    @Delete("delete from oss_obj_info where obj_id = #{objId}")
    void deleteObjInfo(@Param("objId") String objId) throws Exception;
}
