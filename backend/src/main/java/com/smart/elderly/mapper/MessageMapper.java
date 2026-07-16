package com.smart.elderly.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.elderly.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 消息Mapper接口
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    /**
     * 获取用户未读消息数量（包含系统消息）
     */
    @Select("SELECT COUNT(*) FROM tb_message WHERE target_type = 'user' AND read_flag = 0 AND (user_id = #{userId} OR user_id = 0)")
    Integer countUnreadByUserId(@Param("userId") Long userId);
}