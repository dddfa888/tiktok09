package project.web.api.impl;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import project.syspara.Syspara;
import project.syspara.SysparaService;
import project.wallet.rate.ExchangeRate;
import project.wallet.rate.ExchangeRateService;
import project.web.api.service.LocalSysparaService;

public class LocalSysparaServiceImpl implements LocalSysparaService {
	
	private ExchangeRateService exchangeRateService;

	private SysparaService sysparaService;

	@Override
	public Map<String, Object> find(String code) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (code.indexOf(",") == -1) {
			/**
			 * 单个code
			 */
			map.put(code, single(code));
		} else {
			/**
			 * 多个code，用逗号分隔
			 */
			String[] codes = code.split(",");
			for (int i = 0; i < codes.length; i++) {
				String split = codes[i];
				map.put(split, single(split));
			}
		}
		return map;
	}

	/**
	 * 获取客服在线时间
	 */
	@Override
	public Map<String, Object> getCustomerServiceTimeRange() {
		List<String> timeRange=new ArrayList<>();
		Syspara syspara = sysparaService.find("customer_service_time_range");
		if (syspara != null && syspara.getValue() != null) {
			String[] times = syspara.getValue().split("-");
			if (times.length == 2) {
				timeRange.add(times[0]);
				timeRange.add(times[1]);
			}
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		LocalTime startTime = LocalTime.parse(timeRange.get(0), formatter);
		LocalTime endTime = LocalTime.parse(timeRange.get(1), formatter);
		LocalTime currentTime = ZonedDateTime.now(ZoneId.of("Asia/Shanghai")).toLocalTime();//GMT+8

		boolean isWithinRange;
		if (endTime.isAfter(startTime) || endTime.equals(startTime)) {
			isWithinRange = !currentTime.isBefore(startTime) && !currentTime.isAfter(endTime);
		} else {
			isWithinRange = !currentTime.isBefore(startTime) || !currentTime.isAfter(endTime);
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("start", timeRange.get(0));
		map.put("end", timeRange.get(1));
		map.put("oline", isWithinRange);
		return map;
	}

	public Object single(String code) {
		Object object = null;
		if ("exchange_rate_out".equals(code)) {

			/**
			 * 兑出货币和汇率
			 */
//			List<ExchangeRate> result = exchangeRateService.findBy("out");
//			// 手续费(USDT)
//
//			object = result;
			object = new ArrayList<>();

		} else if ("exchange_rate_in".equals(code)) {
			/**
			 * 兑入货币和汇率
			 */

//			List<ExchangeRate> list = exchangeRateService.findBy("in");
//
//			object = list;
			object = new ArrayList<>();

		} else if ("withdraw_fee".equals(code)) {
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("type", sysparaService.find("withdraw_fee_type").getValue());
			result.put("fee", sysparaService.find("withdraw_fee").getValue());
			object = result;
		} else if ("index_top_symbols".equals(code)) {
			String result = sysparaService.find("index_top_symbols").getValue();
			object = result;
		} else if ("customer_service_url".equals(code)) {
			String result = sysparaService.find("customer_service_url").getValue();
			object = result;
		} else if ("can_recharge".equals(code)) {
			String result = sysparaService.find("can_recharge").getValue();
			object = result;
		} else if ("miner_buy_symbol".equals(code)) {
			String result = sysparaService.find("miner_buy_symbol").getValue();
			object = result;
		} else if ("miner_bonus_parameters".equals(code)) {
			String result = sysparaService.find("miner_bonus_parameters").getValue();
			object = result;
		} else if ("test_user_money".equals(code)) {
			String result = sysparaService.find("test_user_money").getValue();
			object = result;
		} else if ("index_new_symbols".equals(code)) {
			String result = sysparaService.find("index_new_symbols").getValue();
			object = result;
		} else if ("mall_max_goods_number_in_order".equals(code)) {
			String result = sysparaService.find("mall_max_goods_number_in_order").getValue();
			object = result;
		}else if ("mall_first_recharge_rewards".equals(code)) {
			String result = sysparaService.find("mall_first_recharge_rewards").getValue();
			object = result;
		}else if ("mall_first_invite_recharge_rewards".equals(code)) {
			String result = sysparaService.find("mall_first_invite_recharge_rewards").getValue();
			object = result;
		}else if ("valid_recharge_amount_for_seller_upgrade".equals(code)) {
			String result = sysparaService.find("valid_recharge_amount_for_seller_upgrade").getValue();
			object = result;
		}else if ("valid_recharge_amount_for_first_recharge_bonus".equals(code)) {
			String result = sysparaService.find("valid_recharge_amount_for_first_recharge_bonus").getValue();
			object = result;
		}else if ("valid_recharge_amount_for_team_num".equals(code)) {
			String result = sysparaService.find("valid_recharge_amount_for_team_num").getValue();
			object = result;
		}

		return object;

	}

	public void setExchangeRateService(ExchangeRateService exchangeRateService) {
		this.exchangeRateService = exchangeRateService;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

}
