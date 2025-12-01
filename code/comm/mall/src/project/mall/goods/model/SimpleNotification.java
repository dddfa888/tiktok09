package project.mall.goods.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SimpleNotification {
    private String id;  // 提示表ID
    private String fromId;  // 发送者ID
    private String aimId;  // 接收者ID
    private String tipCode;  //customize无翻译  none翻译 single一个参数
    private Integer state;  // 状态（0=未读,1=已读,2删除）
    private String title;  // 标题
    private String content;  // 消息内容
    private LocalDateTime createTime;  // 创建时间
}

