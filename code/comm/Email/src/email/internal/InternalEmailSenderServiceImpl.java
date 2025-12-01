package email.internal;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import email.sender.EmailMessage;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import kernel.util.StringUtils;

public class InternalEmailSenderServiceImpl implements InternalEmailSenderService {
	private static final Log logger = LogFactory.getLog(InternalEmailSenderServiceImpl.class);
//	private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(InternalEmailSenderServiceImpl.class);
	@Resource
	private JavaMailSenderImpl mailSender;
	@Resource
	private SimpleMailMessage mailMessage;
	@Resource
	private FreeMarkerConfigurer freeMarkerConfigurer;
	private int currentIndex = 0; // 当前选择的邮箱账号索引

	private String hosts;
	private String ports;
	private String usernames;
	private String passwords;
	private String froms;

	private List<String> host;
	private List<Integer> port;
	private List<String> username;
	private List<String> password;
	private List<String> from;

	@PostConstruct
	public void init(){
		this.host = Arrays.asList(hosts.split("&&"));
		this.port = Arrays.stream(ports.split("&&"))
				.map(Integer::parseInt)
				.collect(Collectors.toList());
		this.username = Arrays.asList(usernames.split("&&"));
		this.password = Arrays.asList(passwords.split("&&"));
		this.from = Arrays.asList(froms.split("&&"));
	}



	public static void sendEmail(String recipient, String subject, String content) {
		// Gmail SMTP服务器地址
		String host = "smtp.gmail.com";
		// Gmail账户的电子邮件地址
		final String username = "linbing413@gmail.com";
		// Gmail账户的应用程序专用密码
		final String password = "dssl bhlv flnv ryev";

		// 设置邮件服务器属性
		Properties properties = System.getProperties();
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "587");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");

		// 获取默认会话对象
		Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
			protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
				return new javax.mail.PasswordAuthentication(username, password);
			}
		});

		try {
			// 创建默认的 MimeMessage 对象
			MimeMessage message = new MimeMessage(session);

			// 设置发件人
			message.setFrom(new InternetAddress(username));

			// 设置收件人
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));

			// 设置邮件主题
			message.setSubject(subject);

			// 设置邮件内容
			message.setText(content);

			// 发送邮件
			Transport.send(message);
			System.out.println("邮件发送成功！");
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}
	/*public static void mainback(String[] args) {
		System.out.println("开始发送 = " );
		String host = "smtp.163.com";
		final String user = "aznd6999@163.com";
		final String password = "HKAAFKSWAEECCNCL";
		final String user1 = "qin441311@gmail.com";
		final String password1 = "fpfv tbwe rkdv lvaq";
		String to = "15232523437897252232@163.com"; // 收件人地址

		// 配置SMTP服务器
		Properties properties = new Properties();
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "25");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.socketFactory.port", "465");
		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

		// 添加以下代码以忽略SSL验证（仅用于调试）
		properties.put("mail.smtp.ssl.checkserveridentity", "false");
		properties.put("mail.smtp.ssl.trust", "*");

		// 创建会话对象
		Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, password);
			}
		});

		// 创建邮件
		try {
			// 创建默认的 MimeMessage 对象
			MimeMessage message = new MimeMessage(session);

			// 设置发件人
			message.setFrom(new InternetAddress(user));

			// 设置收件人
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			// 设置邮件主题
			message.setSubject("验证码");

			String content = "你的验证码为";
//			if (StringUtils.isNullOrEmpty(emailMessage.getFtlname())) {
//				content = emailMessage.getContent();
//			} else {
//				content = this.getMailText(emailMessage.getFtlname(), emailMessage.getMap());
//			}

			System.out.println("向目标邮箱:" + to+ " 发送的标题为:" + message.getSubject() + " 的邮件内容解析为空，不发送该邮件！");
//				return;
//			}
			// 设置邮件内容
			message.setText(content);

			// 发送邮件
			Transport.send(message);
			System.out.println("邮件发送成功！");

		} catch (MessagingException e) {
			e.printStackTrace();
		}

	}
*/
	/*
	 * gmail邮箱SSL方式
	 */
	private static void gmailssl(Properties props) {
		final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
		props.put("mail.debug", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.ssl.enable", "true");
		props.put("mail.smtp.socketFactory.class", SSL_FACTORY);
		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.auth", "true");
	}


	//gmail邮箱的TLS方式
	private static void gmailtls(Properties props) {
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
	}



	/*public static void main(String[] args) throws MessagingException {
		System.out.println("开始发送 = " );
		//1.创建一封邮件的实例对象
		Properties props = new Properties();
		//选择ssl方式
		gmailssl(props);
		final String username = "tiktokcustomerservice9@gmail.com";// gmail 邮箱
		final String password = "yqriohaajafcsbpe";// Google应用专用密码
		String sentToEmail="1552333227897252@163.com";
		Session session = Session.getInstance(props,
				new Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});
		MimeMessage msg = new MimeMessage(session);
		//2.设置发件人地址
		msg.setFrom(new InternetAddress(sentToEmail));
		*//**
		 * 3.设置收件人地址（可以增加多个收件人、抄送、密送），即下面这一行代码书写多行
		 * MimeMessage.RecipientType.TO:发送
		 * MimeMessage.RecipientType.CC：抄送
		 * MimeMessage.RecipientType.BCC：密送
		 *//*
		msg.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(sentToEmail));
		//4.设置邮件主题
		msg.setSubject("To reset your password!", "UTF-8");


		String content = "你的验证码为";

		msg.setText(content);


		//设置邮件的发送时间,默认立即发送
		msg.setSentDate(new Date());
		Transport.send(msg);
		System.out.println("邮件发送成功！");

	}*/

	@Override
	public void send(EmailMessage emailMessage) throws Exception {
		try {
			if (currentIndex >= this.host.size()) {
				currentIndex = 0; // 重置计数器
			}
			System.out.println("开始发送 = " );
			//1.创建一封邮件的实例对象
			Properties props = new Properties();
			props.put("mail.smtp.starttls.enable", true); // added this line
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.port", "587");
			props.put("mail.smtp.auth", true);
			props.put("mail.smtp.ssl.protocols", "TLSv1.2");

			/*props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.ssl.enable", "true");
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.port", "465");
			props.put("mail.smtp.socketFactory.port", "465");
			props.put("mail.smtp.auth", "true");*/
			final String username = "tiktokcustomerservice9@gmail.com";// gmail 邮箱
			final String password = "yqriohaajafcsbpe";// Google应用专用密码
			// 创建会话对象
			Session session = Session.getInstance(props,
					new Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(username, password);
						}
					});
			try {
				// 创建默认的 MimeMessage 对象
				MimeMessage message = new MimeMessage(session);
				// 设置发件人
				message.setFrom(new InternetAddress(username));
				// 设置收件人
				message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(emailMessage.getTomail()));
				// 设置邮件主题
				message.setSubject(emailMessage.getSubject());
				String content;
				if (StringUtils.isNullOrEmpty(emailMessage.getFtlname())) {
					content = emailMessage.getContent();
				} else {
					content = this.getMailText(emailMessage.getFtlname(), emailMessage.getMap());
				}
				if (content == null || content.trim().isEmpty()) {
					System.err.println("向目标邮箱:" + emailMessage.getTomail() + " 发送的标题为:" + emailMessage.getSubject() + " 的邮件内容解析为空，不发送该邮件！");
					return;
				}
				// 设置邮件内容
				message.setText(content);

				// 发送邮件
				Transport.send(message);
				System.out.println("邮件发送成功！");
//				currentIndex++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*public void sendback(EmailMessage emailMessage) throws Exception {
		try {
			if (currentIndex >= this.host.size()) {
				currentIndex = 0; // 重置计数器
			}
			System.out.println("开始发送 = " );
			String host = "smtp.163.com";
			final String user = "aznd6999@163.com";
			final String password = "HKAAFKSWAEECCNCL";
			String to = "nanhuan1881@163.com"; // 收件人地址
			// 配置SMTP服务器
			Properties properties = new Properties();
			properties.put("mail.smtp.host", host);
			properties.put("mail.smtp.port", "25");
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.socketFactory.port", "465");
			properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

			// 添加以下代码以忽略SSL验证（仅用于调试）
			properties.put("mail.smtp.ssl.checkserveridentity", "false");
			properties.put("mail.smtp.ssl.trust", "*");
			// 创建会话对象
			Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(user, password);
				}
			});
			try {

				// 创建默认的 MimeMessage 对象
				MimeMessage message = new MimeMessage(session);

				// 设置发件人
				message.setFrom(new InternetAddress(user));

				// 设置收件人
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailMessage.getTomail()));

				// 设置邮件主题
				message.setSubject(emailMessage.getSubject());

				String content;
				if (StringUtils.isNullOrEmpty(emailMessage.getFtlname())) {
					content = emailMessage.getContent();
				} else {
					content = this.getMailText(emailMessage.getFtlname(), emailMessage.getMap());
				}
				if (content == null || content.trim().isEmpty()) {
					System.err.println("向目标邮箱:" + emailMessage.getTomail() + " 发送的标题为:" + emailMessage.getSubject() + " 的邮件内容解析为空，不发送该邮件！");
					return;
				}
				// 设置邮件内容
				message.setText(content);

				// 发送邮件
				Transport.send(message);
				System.out.println("邮件发送成功！");

//				currentIndex++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}catch (Exception e) {
		e.printStackTrace();
	}
	}*/

	/**
	 * 获取模板并将内容输出到模板
	 *
	 * @param ftlname
	 * @param map
	 * @return
	 */
	private String getMailText(String ftlname, Map<String, Object> map) {
		String html = "";

		try {

			// 装载模板
			Template tpl = this.freeMarkerConfigurer.getConfiguration().getTemplate(ftlname);
			// 加入map到模板中 输出对应变量
			html = FreeMarkerTemplateUtils.processTemplateIntoString(tpl, map);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}
		return html;
	}

	// 选择下一个邮箱账号索引
	private synchronized int getNextMailSender() {
		if (currentIndex >= this.host.size()) {
			currentIndex = 0; // 重置计数器
		}
		this.mailSender.setPort(port.get(currentIndex));
		this.mailSender.setUsername(username.get(currentIndex));
		this.mailSender.setPassword(password.get(currentIndex));
		this.mailSender.setHost(host.get(currentIndex));
		currentIndex++;
		return currentIndex++;
	}

	public void setMailSender(JavaMailSenderImpl mailSender) {
		this.mailSender = mailSender;
	}

	public void setMailMessage(SimpleMailMessage mailMessage) {
		this.mailMessage = mailMessage;
	}

	public void setFreeMarkerConfigurer(FreeMarkerConfigurer freeMarkerConfigurer) {
		this.freeMarkerConfigurer = freeMarkerConfigurer;
	}

	public void setHosts(String hosts) {
		this.hosts = hosts;
	}

	public void setPorts(String ports) {
		this.ports = ports;
	}

	public void setUsernames(String usernames) {
		this.usernames = usernames;
	}

	public void setPasswords(String passwords) {
		this.passwords = passwords;
	}

	public void setFroms(String froms) {
		this.froms = froms;
	}
}
