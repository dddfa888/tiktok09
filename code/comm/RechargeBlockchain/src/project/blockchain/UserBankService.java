package project.blockchain;

public interface UserBankService {
    /**
     * 通过id查找
     */
    UserBank findByPartyId(String partyId);
    /**
     * 添加银行卡
     */
    void saveOrUpdateBank(UserBank obj);
}
