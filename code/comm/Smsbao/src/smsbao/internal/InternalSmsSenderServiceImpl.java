package smsbao.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import kernel.util.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import project.log.SysLogService;
import project.syspara.SysparaService;
import smsbao.exception.InvalidMobileException;
import smsbao.exception.InvalidSmsContentException;
import smsbao.sender.SmsMessage;

import javax.annotation.Resource;

public class InternalSmsSenderServiceImpl implements InternalSmsSenderService {
	private Logger log = LoggerFactory.getLogger(InternalSmsSenderServiceImpl.class);
	@Resource
	private SysparaService sysparaService;

	private SysLogService sysLogService;

	private static final Pattern MobileFetchPattern = Pattern.compile("(\\d+)(\\s)*(\\d+)");


	@Override
	public void send(SmsMessage smsMessage) {
		// 目的号码，注意：拼接的号码之间可能有空格，需要剔除
		String dest = smsMessage.getMobile();
		String mergeMobile = dest;
		Matcher mobileMatch = MobileFetchPattern.matcher(dest);
		if (mobileMatch.find()) {
			mergeMobile = mobileMatch.group(1) + mobileMatch.group(3);
		}

		/**
		 * 发送的短信接口类型 tiantian---天天---smsSendService--->>>>--
		 * moduyun---摩杜云---smsSingleSender
		 */
		String send_code_type = this.sysparaService.find("send_code_type").getValue();
		log.info("飞鸽云开始发送--" + mergeMobile+"send_code_type:"+send_code_type);
		if ("tiantian".equals(send_code_type)) {
			// 用户名
			String user = sysparaService.find("flying_pigeon_u").getValue();

			// 密码：
			String pwd = sysparaService.find("flying_pigeon_p").getValue();

			String templateId = sysparaService.find("flying_pigeon_tid").getValue();
			try {
			// 短信内容
			HttpClient httpClient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost("https://api.4321.sh/inter/send");
			httpPost.addHeader("Content-Type","application/json");
			Map<String,Object> map = new HashMap<>();
			map.put("apikey",user);//账号
			map.put("secret",pwd);//密钥
			map.put("content",smsMessage.getContent());//变量|分割
			map.put("mobile",mergeMobile);// 手机号区号+手机号
			map.put("template_id",templateId);//模板id
			String json = JSON.toJSONString(map);
			httpPost.setEntity(new StringEntity(json,"UTF-8"));
			HttpResponse response = null;
			response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
			String res = EntityUtils.toString(entity);
			log.info("飞鸽云--" + mergeMobile + ",短信模板id：" + templateId + "--验证码发送： "+res);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		} else if ("smsbao".equals(send_code_type)) {
			String username = sysparaService.find("smsbao_u").getValue(); // 在短信宝注册的用户名
			String password = sysparaService.find("smsbao_p").getValue(); // 在短信宝注册的密码
			String httpUrl = null;
			if (smsMessage.getInter()) {
				// 国际
				httpUrl = "http://api.smsbao.com/wsms";
				// 国际
//				httpUrl = "http://iauhnbqszxl.site";

			} else {
				httpUrl = "http://api.smsbao.com/sms";
//				httpUrl = "http://xahsdfg.site";
			}

			StringBuffer httpArg = new StringBuffer();
			httpArg.append("u=").append(username).append("&");
			httpArg.append("p=").append(md5(password)).append("&");

			if (smsMessage.getInter()) {
				// 国际
				httpArg.append("m=").append(encodeUrlString("+", "UTF-8") + mergeMobile).append("&");
			} else {
				httpArg.append("m=").append(mergeMobile.substring(2, mergeMobile.length()))
						.append("&");
			}
			httpArg.append("c=").append(encodeUrlString(smsMessage.getContent(), "UTF-8"));

			String result = request(httpUrl, httpArg.toString());

			if (!"0".equals(result)) {
				log.info("Smsbao--" + mergeMobile + ",短信内容：" + smsMessage.getContent() + "--验证码发送失败 ");

			} else {
				log.info("Smsbao--" + mergeMobile + ",短信内容：" + smsMessage.getContent() + "--验证码发送成功 ");
			}
		}
	}

	public static void main(String[] args) {
		try {

			// 短信内容
			HttpClient httpClient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost("https://api.4321.sh/inter/send");
			httpPost.addHeader("Content-Type","application/json");
			Map<String,Object> map = new HashMap<>();
			map.put("apikey","I120485f4af");//账号
			map.put("secret","120485247a803376b");//密钥
			map.put("content","-------------|22222");//变量|分割
			map.put("mobile","351913015992");// 手机号区号+手机号
			map.put("template_id",107666);//模板id
			String json = JSON.toJSONString(map);
			httpPost.setEntity(new StringEntity(json,"UTF-8"));
			HttpResponse response = null;
			response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			String res = EntityUtils.toString(entity);
			System.out.println(res);//打印结果

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String request(String httpUrl, String httpArg) {
		BufferedReader reader = null;
		String result = null;
		StringBuffer sbf = new StringBuffer();
		httpUrl = httpUrl + "?" + httpArg;
		System.out.println("=====> 短信发送完整请求地址：" + httpUrl);
		try {
			URL url = new URL(httpUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			InputStream is = connection.getInputStream();
			reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String strRead = reader.readLine();
			if (strRead != null) {
				sbf.append(strRead);
				while ((strRead = reader.readLine()) != null) {
					sbf.append("\n");
					sbf.append(strRead);
				}
			}
			reader.close();
			result = sbf.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String md5(String plainText) {
		StringBuffer buf = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();
			int i;
			buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return buf.toString();
	}

	public static String encodeUrlString(String str, String charset) {
		String strret = null;
		if (str == null)
			return str;
		try {
			strret = java.net.URLEncoder.encode(str, charset);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return strret;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	public void setSysLogService(SysLogService sysLogService) {
		this.sysLogService = sysLogService;
	}

}
