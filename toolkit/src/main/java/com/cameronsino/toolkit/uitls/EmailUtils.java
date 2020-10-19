package com.cameronsino.toolkit.uitls;

import com.cameronsino.toolkit.RegexConstants;
import com.cameronsino.toolkit.configuration.EmailConfiguration;
import com.cameronsino.toolkit.function.ExceptionRunnable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.annotation.PostConstruct;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Pattern;

@Configuration
@ConditionalOnExpression(value = "'${spring.mail.enable-email-utils}'.equals('true')")
@EnableConfigurationProperties({EmailConfiguration.class})
public class EmailUtils {
    @Autowired
    private EmailConfiguration _configuration;

    @PostConstruct
    private void initializer() {
        configuration = this._configuration;
    }

    private static EmailConfiguration configuration;

    /**
     * 发送邮件
     *
     * @param subject 邮件主题
     * @param content 内容
     * @param to      收件人
     */

    public static void send(String subject, String content, String... to) throws MessagingException {
        retry(() -> sendEmail(subject, content, to, null, null));
    }

    /**
     * 发送邮件
     *
     * @param subject 邮件主题
     * @param content 内容
     * @param to      收件人
     * @param cc      抄送收件人
     */
    public static void send(String subject, String content, String[] to, String[] cc) throws MessagingException {
        retry(() -> sendEmail(subject, content, to, cc, null));
    }

    /**
     * 发送邮件
     *
     * @param subject 邮件主题
     * @param content 内容
     * @param to      收件人
     * @param files   附件
     */
    public static void send(String subject, String content, String[] to, File[] files) throws MessagingException {
        retry(() -> sendEmail(subject, content, to, null, files));
    }

    /**
     * 发送邮件
     *
     * @param subject 邮件主题
     * @param content 内容
     * @param to      收件人
     * @param cc      抄送收件人
     * @param file    附件
     */
    public static void send(String subject, String content, String[] to, String[] cc, File[] file) throws MessagingException {
        retry(() -> sendEmail(subject, content, to, cc, file));
    }

    /**
     * 重试
     *
     * @param runnable ExceptionRunnable
     */
    private static void retry(ExceptionRunnable<MessagingException> runnable) throws MessagingException {
        try {
            runnable.run();
        } catch (Exception ex) {
            if (!configuration.getRetryEnable()) {
                throw ex;
            }
            List<String> exceptions = new ArrayList<>(configuration.getRetryFrequency());
            try {
                for (int i = 0; i < configuration.getRetryFrequency(); i++) {
                    runnable.run();
                }
            } catch (Exception ex2) {
                exceptions.add(ex2.getMessage());
            }
            if (exceptions.size() == (configuration.getRetryFrequency())) {
                throw new MessagingException(String.join("|", exceptions));
            }
        }
    }

    /**
     * 发送邮件
     *
     * @param subject 邮件主题
     * @param content 内容
     * @param to      收件人
     * @param cc      抄送收件人
     * @param file    附件
     */
    private static void sendEmail(String subject, String content, String[] to, String[] cc, File[] file) throws MessagingException {
        if (!StringUtils.hasText(subject)) {
            throw new NullPointerException("邮件主题不能为空");
        }
        if (!StringUtils.hasText(content)) {
            throw new NullPointerException("邮件内容不能为空");
        }

        //获取系统属性
        Properties properties = System.getProperties();
        //设置发送邮件的基本参数
        //发送邮件服务器
        properties.put("mail.smtp.host", configuration.getHost());
        properties.put("mail.smtp.auth", "true");

        if (configuration.getSslEnable()) {//发送端口
            properties.put("mail.smtp.socketFactory.port", configuration.getPort()); //配置 ssl
            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        } else {
            properties.put("mail.smtp.port", configuration.getPort());
        }
        //设置发送邮件的账号和密码
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                //两个参数分别是发送邮件的账户和密码
                return new PasswordAuthentication(configuration.getFrom(), configuration.getPassword());
            }
        });


        //创建邮件对象
        Message message = new MimeMessage(session);
        //设置发件人
        message.setFrom(new InternetAddress(configuration.getFrom()));
        // 添加邮件的各个部分内容，包括文本内容和附件
        Multipart multipart = new MimeMultipart();

        if (null == to || to.length == 0) {
            throw new NullPointerException("收件人人邮箱不能为空");
        }
        //设置收件人
        Address[] address = new InternetAddress[to.length];
        for (int i = 0; i < to.length; i++) {
            if (!Pattern.matches(RegexConstants.EMAIL, to[i])) {
                throw new SecurityException("邮箱[" + to[i] + "]格式错误");
            }
            address[i] = new InternetAddress(to[i]);
        }
        message.setRecipients(Message.RecipientType.TO, address);

        //判断是否添加抄送人
        if (Objects.nonNull(cc)) {
            Address[] addressCC = new InternetAddress[cc.length];
            for (int i = 0; i < cc.length; i++) {
                if (!Pattern.matches(RegexConstants.EMAIL, cc[i])) {
                    throw new SecurityException("抄送邮箱[" + cc[i] + "]格式错误");
                }
                addressCC[i] = new InternetAddress(cc[i]);
            }
            message.setRecipients(Message.RecipientType.CC, addressCC);
        }
        //判断是否添加附件
        if (Objects.nonNull(file)) {
            for (File item : file) {
                BodyPart attachmentBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(item);
                attachmentBodyPart.setDataHandler(new DataHandler(source));
                attachmentBodyPart.setFileName(item.getName());
                multipart.addBodyPart(attachmentBodyPart);
            }
        }
        // 添加邮件正文
        BodyPart contentPart = new MimeBodyPart();
        contentPart.setContent(content, "text/html;charset=UTF-8");
        multipart.addBodyPart(contentPart);

        //设置主题
        message.setSubject(subject);
        // 将多媒体对象放到message中
        message.setContent(multipart);
        //发送一封邮件
        Transport.send(message);
    }
}
