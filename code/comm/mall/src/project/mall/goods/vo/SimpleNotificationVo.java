package project.mall.goods.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SimpleNotificationVo {
    private String id;  // 提示表ID
    private String tipCode;  // 类型
    private String title;  // 标题
    private String content;  // 消息内容
    private String createTime;  // 创建时间
}

