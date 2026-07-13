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
     * 获取用户未读消息数量
     */
    @Select("SELECT COUNT(*) FROM tb_message WHERE user_id = #{userId} AND read_flag = 0")
    Integer countUnreadByUserId(@Param("userId") Long userId);
}