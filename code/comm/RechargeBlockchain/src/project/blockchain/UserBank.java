package project.blockchain;

import kernel.bo.EntityObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.N;

import java.time.LocalDateTime;
public class UserBank  extends EntityObject {
    /**
     *
     */
    private static final long serialVersionUID = -4483090797419171871L;
    private Integer id;  // ID
    private String partyId;  // 用户ID
    private String bankName;  // 银行名ID
    private String accountName;  // 开户名ID
    private String cardNo;  // 卡号
    private Integer status;  // 0删除1默认2非默认
    private LocalDateTime createTime;  // 创建时间

    public UserBank(Integer id, String partyId, String bankName, String accountName, String cardNo, Integer status, LocalDateTime createTime) {
        this.id = id;
        this.partyId = partyId;
        this.bankName = bankName;
        this.accountName = accountName;
        this.cardNo = cardNo;
        this.status = status;
        this.createTime = createTime;
    }

    public UserBank() {
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
