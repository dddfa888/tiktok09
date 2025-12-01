package project.mall.event;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import project.RedisKeys;
import project.blockchain.internal.FundChangeService;
import project.mall.event.message.PurchaseOrderGoodsEvent;
import project.mall.event.model.PurchaseOrderInfo;
import project.mall.orders.GoodsOrdersService;
import project.mall.orders.model.MallOrdersPrize;
import project.mall.seller.MallLevelService;
import project.mall.seller.SellerService;
import project.mall.seller.model.MallLevel;
import project.mall.seller.model.Seller;
import project.party.PartyService;
import project.party.UserMetricsService;
import project.party.model.Party;
import project.party.model.UserMetrics;
import project.party.model.UserRecom;
import project.party.recom.UserRecomService;
import project.redis.RedisHandler;
import project.syspara.SysParaCode;
import project.syspara.Syspara;
import project.syspara.SysparaService;
import project.wallet.WalletLogService;

import java.util.*;

/**
 * 用户采购采购过后，有一些关联业务会同步受到影响
 * 目前可见受影响的业务数据：
 * 1. 检查当前充值用户是否满足升级指标：
 * 2. 检查当前充值用户直接上级代理是否满足升级指标
 *
 */
@Setter
public class UpgradeSellerLevelByPurchaseEventListener implements ApplicationListener<PurchaseOrderGoodsEvent> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private SellerService sellerService;
    private PartyService partyService;
    private UserRecomService userRecomService;
    private MallLevelService mallLevelService;
    private SysparaService sysparaService;
    private FundChangeService fundChangeService;
    private RedisHandler redisHandler;
    private GoodsOrdersService ordersService;
    private WalletLogService walletLogService;
    private UserMetricsService userMetricsService;

    /**
     * 采购资金触发升级
     */
    @Override
    public void onApplicationEvent(PurchaseOrderGoodsEvent event) {
        PurchaseOrderInfo obj = event.getPurchaseOrderInfo();
        MallOrdersPrize order = ordersService.getMallOrdersPrize(obj.getOrderId());
        String sellerId = order.getSellerId();
        logger.info("监听到用户成功采购事件，将判断当前用户或相关用户等级是否满足提升指标:" + JSON.toJSONString(order.getId()));

        try {
            Date now = new Date();
            synchronized (sellerId.intern()) {
                // 累计金额增加
                double rechargeAcc = walletLogService.getComputeRechargeAmount(sellerId);

                UserMetrics userMetrics = userMetricsService.getByPartyId(sellerId);
                if (userMetrics == null) {
                    userMetrics = new UserMetrics();

                    userMetrics.setAccountBalance(0.0D);
                    userMetrics.setMoneyRechargeAcc(0.0D);
                    userMetrics.setMoneyWithdrawAcc(0.0D);
                    userMetrics.setPartyId(sellerId);
                    userMetrics.setStatus(1);
                    userMetrics.setTotleIncome(0.0D);
                    userMetrics.setCreateTime(now);
                    userMetrics.setUpdateTime(now);
                    userMetrics = userMetricsService.save(userMetrics);
                }

                userMetrics.setStoreMoneyRechargeAcc(userMetrics.getStoreMoneyRechargeAcc() + order.getPushAmount());
                userMetrics.setMoneyRechargeAcc(rechargeAcc);
                userMetricsService.update(userMetrics);
                // 升级
            }
            redisHandler.zadd(RedisKeys.RECHARGE_PASS_TIME, now.getTime(), obj.getOrderId());
            Seller sellerEntity = sellerService.getSeller(sellerId);
            if (sellerEntity == null) {
                // 不是商家类型用户，不做后续处理
                return;
            }

            Party currentParty = this.partyService.getById(sellerId);
            // 上级推荐人
            String parentPartyId = "";
            UserRecom firstRecom = userRecomService.findByPartyId(currentParty.getId().toString());
            if (firstRecom != null) {
                Party parentParty = this.partyService.getById(firstRecom.getReco_id().toString());
                if (parentParty != null) {
                    parentPartyId = parentParty.getId().toString();
                }
            }

            // 提取用于店铺升级业务的有效充值用户的充值金额临界值
            double limitRechargeAmount = 100.0;
            // 提取用于店铺升级业务的计算团队人数充值金额临界值
            double limitRechargeAmountOnTeam = 100.0;
            Syspara syspara = sysparaService.find(SysParaCode.VALID_RECHARGE_AMOUNT_FOR_SELLER_UPGRADE.getCode());
            Syspara sysparaOnTeam = sysparaService.find(SysParaCode.VALID_RECHARGE_AMOUNT_FOR_TEAM_NUM.getCode());
            if (syspara != null) {
                String validRechargeAmountInfo = syspara.getValue().trim();
                if (StrUtil.isNotBlank(validRechargeAmountInfo)) {
                    limitRechargeAmount = Double.parseDouble(validRechargeAmountInfo);
                }
            }
            if (sysparaOnTeam != null) {
                String rechargeAmountOnTeamInfo = sysparaOnTeam.getValue().trim();
                if (StrUtil.isNotBlank(rechargeAmountOnTeamInfo)) {
                    limitRechargeAmountOnTeam = Double.parseDouble(rechargeAmountOnTeamInfo);
                }
            }

            // 对等级集合进行排序，方便升级判断
            List<MallLevel> levelEntityList = this.mallLevelService.listLevel();
            Map<String, Integer> levelSortMap = new HashMap<>();

            levelSortMap.put("C", 1);
            levelSortMap.put("B", 2);
            levelSortMap.put("A", 3);
            levelSortMap.put("S", 4);
            levelSortMap.put("SS", 5);
            levelSortMap.put("SSS", 6);

            CollUtil.sort(levelEntityList, new Comparator<MallLevel>() {
                @Override
                public int compare(MallLevel o1, MallLevel o2) {
                    Integer seq1 = levelSortMap.get(o1.getLevel());
                    Integer seq2 = levelSortMap.get(o2.getLevel());
                    seq1 = seq1 == null ? 0 : seq1;
                    seq2 = seq2 == null ? 0 : seq2;

                    return seq1 - seq2;
                }
            });

            // 处理当前用户的升级
            fundChangeService.upgradeMallLevelProcess(sellerId, limitRechargeAmount, limitRechargeAmountOnTeam,
                    levelEntityList, levelSortMap, order.getPushAmount());


            // =================== 顺别识别当前充值用户的直接上级商家是否满足升级条件 =========================
            if (StrUtil.isBlank(parentPartyId) || Objects.equals(parentPartyId, "0")) {
                logger.info("-------> UpgradeSellerLevelByRechargeEventListener.onApplicationEvent 当前用户:{} 的上级商家不存在，不再处理上级商家的升级判断处理",
                        sellerId);
                return;
            }
            synchronized (parentPartyId.intern()) {
                // 处理当前用户的直接上级用户的升级逻辑
                fundChangeService.upgradeMallLevelProcess(parentPartyId, limitRechargeAmount, limitRechargeAmountOnTeam,
                        levelEntityList, levelSortMap, 0.0);
            }

        } catch (Exception e) {
            logger.error("用户采购后，计算更新相关用户的店铺等级处理报错，采购订单号为:{} ", obj.getOrderId(), e);
        }

    }

}
