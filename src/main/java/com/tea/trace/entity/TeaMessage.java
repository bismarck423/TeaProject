package com.tea.trace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("tea_message")
public class TeaMessage {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 发送方ID */
    private Long senderId;

    /** 接收方ID */
    private Long receiverId;

    /** 消息正文 */
    private String content;

    /** 0:未读, 1:已读 */
    private Integer isRead;

    private LocalDateTime createTime;
}