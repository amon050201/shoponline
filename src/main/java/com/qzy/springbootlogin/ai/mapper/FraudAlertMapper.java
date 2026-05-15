package com.qzy.springbootlogin.ai.mapper;

import com.qzy.springbootlogin.ai.pojo.FraudAlert;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FraudAlertMapper {

    @Insert("INSERT INTO fraud_alert (user_id, alert_type, description, risk_score, severity, resolved, reference_id) " +
            "VALUES (#{userId}, #{alertType}, #{description}, #{riskScore}, #{severity}, 0, #{referenceId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(FraudAlert alert);

    @Select("SELECT * FROM fraud_alert WHERE user_id = #{userId} ORDER BY created_time DESC")
    List<FraudAlert> findByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM fraud_alert WHERE resolved = 0 ORDER BY severity DESC, created_time DESC")
    List<FraudAlert> findUnresolved();

    @Select("SELECT * FROM fraud_alert WHERE id = #{id}")
    FraudAlert findById(@Param("id") Long id);

    @Update("UPDATE fraud_alert SET resolved = 1 WHERE id = #{id}")
    int resolveAlert(@Param("id") Long id);

    @Select("SELECT * FROM fraud_alert ORDER BY created_time DESC LIMIT #{limit}")
    List<FraudAlert> findRecent(@Param("limit") int limit);

    @Select("SELECT * FROM fraud_alert ORDER BY created_time DESC")
    List<FraudAlert> findAll();
}
