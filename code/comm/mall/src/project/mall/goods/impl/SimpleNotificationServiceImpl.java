package project.mall.goods.impl;

import kernel.exception.BusinessException;
import lombok.Data;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import project.mall.goods.SimpleNotificationService;
import project.mall.goods.model.SimpleNotification;
import project.mall.goods.vo.SimpleNotificationVo;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional(propagation = Propagation.REQUIRED)
@Data
public class SimpleNotificationServiceImpl extends HibernateDaoSupport implements SimpleNotificationService {
    @Override
    public void addNot(String fid, String aid, String code,String title, String content) {
//        List<SimpleNotification> not = this.getHibernateTemplate().execute(session ->
//                session.createQuery("FROM SimpleNotification WHERE aimId = :aimId and state=0 and fromId=:fid and content=:content", SimpleNotification.class)
//                        .setParameter("aimId", aid)
//                        .setParameter("fid", fid)
//                        .setParameter("content", content)
//                        .setMaxResults(1)
//                        .list());
//        if (not!=null&& !not.isEmpty()) {
//            throw new BusinessException("通知已存在");
//        }
        SimpleNotification build = SimpleNotification.builder().fromId(fid)
                .aimId(aid)
                .title(title)
                .content(content)
                .tipCode(code)
                .state(0)
                .createTime(LocalDateTime.now())
                .build();
        getHibernateTemplate().save(build);
    }

    @Override
    public void addCustomize(String fid, String aid, String content) {
        this.addNot(fid,aid,"customize","系统通知",content);
    }

    @Override
    public void addNone(String fid, String aid, String title,String content) {
        this.addNot(fid,aid,"none",title,content);
    }

    @Override
    public void addSinge(String fid, String aid,String code,String title, String value) {
        Map<String,String> map = new HashMap<>();
        map.put("i18nCode",code);
        map.put("value",value);
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(map);
            this.addNot(fid,aid,"singe",title,json);
        } catch (IOException e) {
            throw new BusinessException("系统繁忙，请稍后重试");
        }
    }

    @Override
    public void updateStateById(String id, Integer state) {
        this.getHibernateTemplate().execute(session ->
                session.createQuery("UPDATE SimpleNotification SET state = :state WHERE id = :id")
                        .setParameter("id", id)
                        .setParameter("state", state)
                        .executeUpdate()
        );
    }

    /**
     * 读取并确认
     */
    @Override
    public List<SimpleNotificationVo> updateAndGet(String aid) {
        List<SimpleNotification> not = this.getHibernateTemplate().execute(session ->
                session.createQuery("FROM SimpleNotification WHERE aimId = :aimId and state in(0,1) ORDER BY createTime DESC",
                                SimpleNotification.class)
                        .setParameter("aimId", aid)
                        .setMaxResults(30)
                        .list());
        List<SimpleNotificationVo> notVo = new ArrayList<>();
        if (not == null || not.isEmpty()) return notVo;
        for (SimpleNotification no : not) {
            notVo.add(new SimpleNotificationVo(no.getId(),no.getTipCode(),no.getTitle(),no.getContent(),
                    no.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        }
        this.getHibernateTemplate().execute(session ->
                session.createQuery("UPDATE SimpleNotification SET state = 1 WHERE aimId = :aimId")
                        .setParameter("aimId", aid)
                        .executeUpdate()
        );
        return notVo;
    }

    /**
     * 获取未读数
     */
    @Override
    public Long getCount(String aid) {
        Long count = this.getHibernateTemplate().execute(session ->
                session.createQuery("SELECT count(*) FROM SimpleNotification WHERE aimId = :aimId AND state = 0", Long.class)
                        .setParameter("aimId", aid)
                        .uniqueResult()
        );
        return count;
    }
}
