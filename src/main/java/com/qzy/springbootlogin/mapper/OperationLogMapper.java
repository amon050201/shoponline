package com.qzy.springbootlogin.mapper;

import com.qzy.springbootlogin.pojo.OperationLog;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface OperationLogMapper {

    @Insert("INSERT INTO operation_log (user_id, username, module, operation, method, params, ip, result, duration, created_time) " +
            "VALUES (#{userId}, #{username}, #{module}, #{operation}, #{method}, #{params}, #{ip}, #{result}, #{duration}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OperationLog log);

    @Select("SELECT * FROM operation_log ORDER BY created_time DESC LIMIT #{limit}")
    List<OperationLog> findRecent(@Param("limit") int limit);

    @Select("SELECT * FROM operation_log ORDER BY created_time DESC")
    List<OperationLog> findAll();

    @Select("SELECT * FROM operation_log WHERE user_id = #{userId} ORDER BY created_time DESC")
    List<OperationLog> findByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM operation_log WHERE module = #{module} ORDER BY created_time DESC")
    List<OperationLog> findByModule(@Param("module") String module);

    @Select("SELECT COUNT(*) FROM operation_log")
    int count();

    @Delete("DELETE FROM operation_log WHERE created_time < DATE_SUB(NOW(), INTERVAL #{days} DAY)")
    int deleteOlderThan(@Param("days") int days);

    @Select("SELECT * FROM operation_log WHERE module = #{module} ORDER BY created_time DESC LIMIT #{offset}, #{limit}")
    List<OperationLog> findByModulePage(@Param("module") String module, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM operation_log WHERE module = #{module}")
    int countByModule(@Param("module") String module);

    @Select("SELECT * FROM operation_log WHERE username LIKE CONCAT('%',#{keyword},'%') OR operation LIKE CONCAT('%',#{keyword},'%') OR module LIKE CONCAT('%',#{keyword},'%') ORDER BY created_time DESC LIMIT #{offset}, #{limit}")
    List<OperationLog> search(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM operation_log WHERE username LIKE CONCAT('%',#{keyword},'%') OR operation LIKE CONCAT('%',#{keyword},'%') OR module LIKE CONCAT('%',#{keyword},'%')")
    int countSearch(@Param("keyword") String keyword);

    @Select("SELECT * FROM operation_log WHERE created_time >= #{start} AND created_time <= #{end} ORDER BY created_time DESC LIMIT #{offset}, #{limit}")
    List<OperationLog> findByDateRange(@Param("start") String start, @Param("end") String end, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM operation_log WHERE created_time >= #{start} AND created_time <= #{end}")
    int countByDateRange(@Param("start") String start, @Param("end") String end);

    @Select("SELECT * FROM operation_log ORDER BY created_time DESC LIMIT #{offset}, #{limit}")
    List<OperationLog> findByPage(@Param("offset") int offset, @Param("limit") int limit);
}
