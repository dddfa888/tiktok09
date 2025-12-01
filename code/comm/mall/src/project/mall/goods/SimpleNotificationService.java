package project.mall.goods;


import project.mall.goods.vo.SimpleNotificationVo;

import java.util.List;
import java.util.Map;

public interface SimpleNotificationService {
    /**
     * 添加
     */
    void addNot(String fid,String aid,String code,String title,String content);
    /**
     * 自定义消息
     */
    void addCustomize(String fid,String aid,String content);
    /**
     * 空参数
     */
    void addNone(String fid,String aid,String title,String content);

    /**
     * 单参数
     */
    void addSinge(String fid,String aid,String code,String title,String value);
    /**
     * 修改状态
     */
    void updateStateById(String id,Integer state);
    /**
     * 查看
     */
    List<SimpleNotificationVo> updateAndGet(String aid);

    /**
     * 获取未读数
     */
    Long getCount(String aid);
}
