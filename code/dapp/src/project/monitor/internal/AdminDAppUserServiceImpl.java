package project.monitor.internal;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.PagedQueryDao;
import org.springframework.security.providers.encoding.PasswordEncoder;
import project.Constants;
import project.log.LogService;
import project.monitor.AdminDAppUserService;
import project.party.PartyService;
import project.party.model.Party;
import project.party.model.UserRecom;
import project.party.recom.UserRecomService;
import project.syspara.Syspara;
import project.syspara.SysparaService;
import project.user.User;
import project.user.token.Token;
import project.user.token.TokenService;
import project.wallet.Wallet;
import project.wallet.WalletService;
import security.Role;
import security.RoleService;
import security.SaltSigureUtils;
import security.SecUser;
import security.internal.SecUserService;

public class AdminDAppUserServiceImpl extends HibernateDaoSupport implements AdminDAppUserService {

	private PagedQueryDao pagedQueryDao;
	private PartyService partyService;

	private SysparaService sysparaService;

	private UserRecomService userRecomService;

	private WalletService walletService;

	private LogService logService;

	private SecUserService secUserService;

	private RoleService roleService;
	private PasswordEncoder passwordEncoder;
	protected TokenService tokenService;
	@Override
	public void save(String address, boolean login_authority, boolean withdraw_authority, boolean enabled,
			String remarks, String code, String operator, String ip,String rolez) {
		address = address.trim();
		//统一转换成小写
		address = address.toLowerCase();
		
		//操作人为代理商时，默认添加代理商的usercode
		SecUser sec =  this.secUserService.findUserByLoginName(operator);
		if(!StringUtils.isEmptyString(sec.getPartyId())) {
			Party party = this.partyService.cachePartyBy(sec.getPartyId(), false);
			for (Role role : sec.getRoles()) {
				if (Constants.SECURITY_ROLE_AGENT.equals(role.getRoleName())||Constants.SECURITY_ROLE_AGENTLOW.equals(role.getRoleName())) {
					code=party.getUsercode();
				}
			}
		}
		
		
		
		
		if (partyService.findPartyByUsername(address) != null) {
			throw new BusinessException("地址已存在");
		}

		/**
		 * 用户code
		 */
		String usercode = getUsercode();

		Party party_reco = null;
		if (!StringUtils.isNullOrEmpty(code)) {
			party_reco = this.partyService.findPartyByUsercode(code);
			if (party_reco == null) {
				throw new BusinessException("上级UID推荐码不正确");
			}

		}

		/**
		 * party
		 */
		Party party = new Party();
		party.setUsername(address);
		party.setLogin_authority(login_authority);
		party.setWithdraw_authority(withdraw_authority);
		party.setEnabled(enabled);
		party.setRemarks(remarks);
		party.setUsercode(usercode);
		party.setUser_level(1);
		party.setAutoComment(true);
		Role role;
		SecUser secUser = new SecUser();
		if(Constants.SECURITY_ROLE_GUEST.equals(rolez)){
			rolez=Constants.SECURITY_ROLE_GUEST;
			role=this.roleService.findRoleByName(Constants.SECURITY_ROLE_GUEST);
		}else{
			rolez=Constants.SECURITY_ROLE_MEMBER;
			role=this.roleService.findRoleByName(Constants.SECURITY_ROLE_MEMBER);
		}
		party.setRolename(rolez);

		party = partyService.save(party);

		User user = new User();
		user.setPartyId(party.getId());
		this.getHibernateTemplate().save(user);

		if (party_reco != null) {
			UserRecom userRecom = new UserRecom();
			userRecom.setPartyId(party.getId());
			userRecom.setReco_id(party_reco.getId());// 父类partyId
			this.userRecomService.save(userRecom);
		}

		/**
		 * SecUser
		 */
		secUser.setPartyId(String.valueOf(party.getId()));
		secUser.getRoles().add(role);
		secUser.setUsername(address);
		secUser.setEnabled(login_authority);
		this.secUserService.saveUser(secUser);

		/**
		 * usdt账户
		 */
		Wallet wallet = new Wallet();
		wallet.setPartyId(party.getId().toString());
		this.walletService.save(wallet);

		/**
		 * 加入节点操作，未完成
		 */

		project.log.Log log = new project.log.Log();
		log.setCategory(Constants.LOG_CATEGORY_OPERATION);
		log.setUsername(party.getUsername());
		log.setOperator(operator);
		log.setLog("ip:" + ip + ",管理员手动新增了演示用户:" + address);
		logService.saveSync(log);

	}

	@Override
	public void update(String partyId, boolean login_authority, boolean enabled, boolean withdraw_authority,
			String remarks, String operatorUsername, String ip, boolean autoComment, String withdrawAddress,
					   String withdrawChainName, String withdrawCoinType,String roleName) {
		SecUser sec =  this.secUserService.findUserByLoginName(operatorUsername);
		
		Party party = this.partyService.cachePartyBy(partyId, false);

		for (Role role : sec.getRoles()) {
			//代理商只能修改演示账户
			if (Constants.SECURITY_ROLE_AGENT.equals(role.getRoleName())||Constants.SECURITY_ROLE_AGENTLOW.equals(role.getRoleName())) {
//					&&party!=null&&!Constants.SECURITY_ROLE_GUEST.equals(party.getRolename())) {
				if(party!=null&&!Constants.SECURITY_ROLE_GUEST.equals(party.getRolename())) {
					throw new BusinessException("只能修改演示账户");
				}
				List<String> children = userRecomService.findChildren(sec.getPartyId());
				if(!children.contains(partyId)) {
					throw new BusinessException("只能修改自己线下的用户演示账户");
					
				}
			}
		}
		String logtxt = MessageFormat.format(
				"ip:" + ip + ",管理员手动修改了用户信息,用户名:{0},原登录权限:{1},原是否业务锁定:{2},原提现权限:{3},原备注:{4},原提现地址:{5}",
				party.getUsername(),
				party.getLogin_authority(), 
				party.getEnabled(), 
				party.getWithdraw_authority(), 
				party.getRemarks(),
				party.getWithdrawAddress());
		if (party.getRolename().equals("GUEST")){
			party.setAutoComment(autoComment);
		}
		//修改账号类型逻辑
		roleName=roleName.toUpperCase();
		if (Arrays.asList("GUEST", "MEMBER").contains(roleName)&&!party.getRolename().equals(roleName)){
			party.setRolename(roleName);
			if("GUEST".equals(roleName)){
				party.setAutoComment(true);
			}else{
				party.setAutoComment(false);
			}
		}
		if (StringUtils.isNotEmpty(withdrawAddress)){
			party.setWithdrawAddress(withdrawAddress);
			party.setWithdrawChainName(withdrawChainName);
			party.setWithdrawCoinType(withdrawCoinType);
		}
		party.setRemarks(remarks);
		party.setLogin_authority(login_authority);
		party.setEnabled(enabled);
		party.setWithdraw_authority(withdraw_authority);
		this.partyService.update(party);
		logtxt += MessageFormat.format(",新登录权限:{0},新是否业务锁定:{1},新提现权限:{2},新备注:{3},新提现地址", party.getLogin_authority(),
				party.getEnabled(), party.getWithdraw_authority(), party.getRemarks(),party.getWithdrawAddress());
		SecUser secUser = secUserService.findUserByPartyId(partyId);
		secUser.setEnabled(login_authority);

		this.secUserService.update(secUser);

		project.log.Log log = new project.log.Log();
		log.setCategory(Constants.LOG_CATEGORY_OPERATION);
		log.setUsername(party.getUsername());
		log.setOperator(operatorUsername);
		log.setLog(logtxt);
		logService.saveSync(log);
	}
	@Override
	public void update2(String partyId,boolean login_authority, boolean enabled, boolean withdraw_authority, String remarks,
						String operatorUsername, String ip, boolean autoComment, String withdrawAddress,
						String withdrawChainName, String withdrawCoinType,String roleName,String username,String email,
						String phone,String password,String funPassword,String avatar) {
		SecUser sec =  this.secUserService.findUserByLoginName(operatorUsername);

		Party party = this.partyService.cachePartyBy(partyId, false);

		for (Role role : sec.getRoles()) {
			//代理商只能修改演示账户
			if (Constants.SECURITY_ROLE_AGENT.equals(role.getRoleName())||Constants.SECURITY_ROLE_AGENTLOW.equals(role.getRoleName())) {
				if(party!=null&&!Constants.SECURITY_ROLE_GUEST.equals(party.getRolename())) {
					throw new BusinessException("只能修改演示账户");
				}
				List<String> children = userRecomService.findChildren(sec.getPartyId());
				if(!children.contains(partyId)) {
					throw new BusinessException("只能修改自己线下的用户演示账户");

				}
			}
		}

		String logtxt = MessageFormat.format(
				"ip:" + ip + ",管理员手动修改了用户信息,用户名:{0},原登录权限:{1},原是否业务锁定:{2},原提现权限:{3},原备注:{4},原提现地址:{5}",
				party.getUsername(),
				party.getLogin_authority(),
				party.getEnabled(),
				party.getWithdraw_authority(),
				party.getRemarks(),
				party.getWithdrawAddress());
		if (party.getRolename().equals("GUEST")){
			party.setAutoComment(autoComment);
		}
		SecUser secUser = secUserService.findUserByPartyId(partyId);
		//修改账号类型逻辑
		roleName=roleName.toUpperCase();
		if (Arrays.asList("GUEST", "MEMBER").contains(roleName)&&!party.getRolename().equals(roleName)){
			party.setRolename(roleName);
			Role role;
			if("GUEST".equals(roleName)){
				role=this.roleService.findRoleByName(Constants.SECURITY_ROLE_GUEST);
				party.setAutoComment(true);
			}else{
				role=this.roleService.findRoleByName(Constants.SECURITY_ROLE_MEMBER);
				party.setAutoComment(false);
			}
			Set<Role> roles = secUser.getRoles();
			roles.clear();
			roles.add(role);
			secUser.setRoles(roles);
		}

		//未添加日志
		if(phone!=null){
			party.setPhone(phone);
			party.setPhone_authority(true);
		}
		if (email != null && !email.trim().isEmpty()) {
			if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
				throw new IllegalArgumentException("邮箱格式不正确");
			}
			party.setEmail(email);
			party.setEmail_authority(true);
		}

		if (password != null && !password.trim().isEmpty()) {
			if (password.length() < 6 || password.length() > 15) {
				throw new IllegalArgumentException("密码需为6-15位");
			}
			if (password.contains(" ")) {
				throw new IllegalArgumentException("密码不能包含空格");
			}
			if (!password.matches("^[A-Za-z0-9!@#$%^&*]+$")) {
				throw new IllegalArgumentException("密码只能包含字母、数字和!@#$%^&*");
			}
			secUser.setPassword(passwordEncoder.encodePassword(password, partyId));
		}
		if(!party.getUsername().equals(username)){
			secUser.setUsername(username);
			party.setUsername(username);
		}
		if (funPassword != null && !funPassword.trim().isEmpty()) {
			if (!funPassword.matches("^\\d{6}$")) {
				throw new IllegalArgumentException("资金密码必须是6位数字");
			}
			party.setSafeword(passwordEncoder.encodePassword(funPassword, SaltSigureUtils.saltfigure));
		}
		//
		if (StringUtils.isNotEmpty(withdrawAddress)){
			party.setWithdrawAddress(withdrawAddress);
			party.setWithdrawChainName(withdrawChainName);
			party.setWithdrawCoinType(withdrawCoinType);
		}
		party.setRemarks(remarks);
		if(!login_authority){
			Token token = this.tokenService.find(party.getId().toString());
			if (token != null) {
				tokenService.delete(token.getToken());
			}
		}
		party.setAvatar(avatar);
		party.setLogin_authority(login_authority);
		party.setEnabled(enabled);
		party.setWithdraw_authority(withdraw_authority);
		this.partyService.update(party);
		logtxt += MessageFormat.format(",新登录权限:{0},新是否业务锁定:{1},新提现权限:{2},新备注:{3},新提现地址", party.getLogin_authority(),
				party.getEnabled(), party.getWithdraw_authority(), party.getRemarks(),party.getWithdrawAddress());

		secUser.setEnabled(login_authority);

		this.secUserService.update(secUser);

		project.log.Log log = new project.log.Log();
		log.setCategory(Constants.LOG_CATEGORY_OPERATION);
		log.setUsername(party.getUsername());
		log.setOperator(operatorUsername);
		log.setLog(logtxt);
		logService.saveSync(log);
	}
	private String getUsercode() {
		Syspara syspara = sysparaService.find("user_uid_sequence");
		int random = (int) (Math.random() * 3 + 1);
		int user_uid_sequence = syspara.getInteger() + random;
		syspara.setValue(user_uid_sequence);
		sysparaService.update(syspara);

		String usercode = String.valueOf(user_uid_sequence);
		return usercode;
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
	}

	public void setWalletService(WalletService walletService) {
		this.walletService = walletService;
	}

	public void setLogService(LogService logService) {
		this.logService = logService;
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

	public void setSecUserService(SecUserService secUserService) {
		this.secUserService = secUserService;
	}

	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public void setTokenService(TokenService tokenService) {
		this.tokenService = tokenService;
	}
}
