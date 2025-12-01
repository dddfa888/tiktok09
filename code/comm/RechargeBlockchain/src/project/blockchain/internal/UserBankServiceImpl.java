package project.blockchain.internal;

import kernel.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.blockchain.UserBank;
import project.blockchain.UserBankService;

import java.util.List;

@RequiredArgsConstructor
public class UserBankServiceImpl extends HibernateDaoSupport implements UserBankService {

    /**
     * 通过id查找
     */
    @Override
    public UserBank findByPartyId(String partyId) {
        List<UserBank> obj = this.getHibernateTemplate().execute(session ->
                session.createQuery("FROM UserBank WHERE partyId = :partyId and status=1", UserBank.class)
                        .setParameter("partyId", partyId)
                        .setMaxResults(1)
                        .list());
        if(obj!=null&& !obj.isEmpty()){
            obj.get(0).setCreateTime(null);
            return obj.get(0);
        }
        return null;
    }

    /**
     * 添加银行卡
     */
    @Override
    public void saveOrUpdateBank(UserBank arg) {
        List<UserBank> obj = this.getHibernateTemplate().execute(session ->
                session.createQuery("FROM UserBank WHERE partyId = :partyId and status=1", UserBank.class)
                        .setParameter("partyId", arg.getPartyId())
                        .setMaxResults(1)
                        .list());
        if(obj!=null&& !obj.isEmpty()){
            throw new BusinessException("银行卡已绑定");
        }
        List<UserBank> obj2 = this.getHibernateTemplate().execute(session ->
                session.createQuery("FROM UserBank WHERE cardNo = :cardNo ", UserBank.class)
                        .setParameter("cardNo", arg.getCardNo())
                        .setMaxResults(1)
                        .list());
        if(obj2!=null&& !obj2.isEmpty()){
            throw new BusinessException("卡号已存在");
        }
        getHibernateTemplate().saveOrUpdate(arg);
    }
}
