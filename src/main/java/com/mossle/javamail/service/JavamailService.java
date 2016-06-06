 package com.mossle.javamail.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.util.Date;
import java.util.Properties;

import javax.annotation.Resource;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import com.mossle.javamail.persistence.domain.JavamailConfig;
import com.mossle.javamail.persistence.domain.JavamailMessage;
import com.mossle.javamail.persistence.manager.JavamailConfigManager;
import com.mossle.javamail.persistence.manager.JavamailMessageManager;
import com.mossle.javamail.support.SmtpAuthenticator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

@Service
public class JavamailService {
    private static Logger logger = LoggerFactory
            .getLogger(JavamailService.class);
    private JavamailConfigManager javamailConfigManager;
    private JavamailMessageManager javamailMessageManager;

    public Properties createSmtpProperties(JavamailConfig javamailConfig) {
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol",
                javamailConfig.getSendType());
        props.setProperty("mail.smtp.host", javamailConfig.getSendHost());
        props.setProperty("mail.smtp.port", javamailConfig.getSendPort());
        props.setProperty("mail.smtp.auth", "true");

        if ("ssl".equals(javamailConfig.getSendSecure())) {
            props.setProperty("mail.smtp.ssl.enable", "true");
            props.setProperty("mail.smtp.ssl.trust",
                    javamailConfig.getSendHost());
        } else if ("ssl-all".equals(javamailConfig.getSendSecure())) {
            props.setProperty("mail.smtp.ssl.enable", "true");
            props.setProperty("mail.smtp.ssl.trust", "*");
        } else {
            logger.info("unsuppport : {}", javamailConfig.getSendSecure());
        }

        return props;
    }

    public Properties createPop3Properties(JavamailConfig javamailConfig) {
        Properties props = new Properties();
        props.setProperty("mail.store.protocol",
                javamailConfig.getReceiveType());
        props.setProperty("mail.pop3.host", javamailConfig.getReceiveHost());
        props.setProperty("mail.pop3.port", javamailConfig.getReceivePort());
        props.setProperty("mail.pop3.ssl.enable", "true");
        props.setProperty("mail.pop3.ssl.trust", "*");

        if ("ssl".equals(javamailConfig.getReceiveSecure())) {
            props.setProperty("mail.pop3.ssl.enable", "true");
            props.setProperty("mail.pop3.ssl.trust",
                    javamailConfig.getReceiveHost());
        } else if ("ssl-all".equals(javamailConfig.getReceiveSecure())) {
            props.setProperty("mail.pop3.ssl.enable", "true");
            props.setProperty("mail.pop3.ssl.trust", "*");
        } else {
            logger.info("unsuppport : {}", javamailConfig.getReceiveSecure());
        }

        return props;
    }

    public void send(String from, String to, String cc, String bcc,
            String subject, String content) throws MessagingException {
        JavamailConfig javamailConfig = javamailConfigManager.findUniqueBy(
                "userId", from);
        this.send(to, cc, bcc, subject, content, javamailConfig);
    }

    public void send(String to, String subject, String content,
            JavamailConfig javamailConfig) throws MessagingException {
        this.send(to, null, null, subject, content, javamailConfig);
    }

    public void send(String to, String cc, String bcc, String subject,
            String content, JavamailConfig javamailConfig)
            throws MessagingException {
        logger.debug("send : {}, {}", to, subject);

        try {
            Properties props = createSmtpProperties(javamailConfig);
            String username = javamailConfig.getUsername();
            String password = javamailConfig.getPassword();

            // 创建Session实例对象
            Session session = Session.getInstance(props, new SmtpAuthenticator(
                    username, password));
            session.setDebug(false);

            // 创建MimeMessage实例对象
            MimeMessage message = new MimeMessage(session);
            // 设置邮件主题
            message.setSubject(subject);
            // 设置发送人
            message.setFrom(new InternetAddress(username));
            // 设置发送时间
            message.setSentDate(new Date());
            // 设置收件人
            message.setRecipients(RecipientType.TO, InternetAddress.parse(to));
            // 设置html内容为邮件正文，指定MIME类型为text/html类型，并指定字符编码为gbk
            message.setContent(content, "text/html;charset=gbk");

            // 保存并生成最终的邮件内容
            message.saveChanges();

            // 发送邮件
            Transport.send(message);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void receive(String userId) throws MessagingException, IOException {
        JavamailConfig javamailConfig = javamailConfigManager.findUniqueBy(
                "userId", userId);
        this.receive(javamailConfig);
    }

    public void receive(JavamailConfig javamailConfig)
            throws MessagingException, IOException {
        this.receivePop3(javamailConfig);
    }

    public void receivePop3(JavamailConfig javamailConfig)
            throws MessagingException, IOException {
        // 准备连接服务器的会话信息
        Properties props = createPop3Properties(javamailConfig);

        // 创建Session实例对象
        Session session = Session.getInstance(props);
        session.setDebug(false);

        Store store = session.getStore(javamailConfig.getReceiveType());
        store.connect(javamailConfig.getUsername(),
                javamailConfig.getPassword());

        Folder defaultFolder = store.getDefaultFolder();
        logger.info("default folder : {}", defaultFolder);

        this.receiveByFolder(defaultFolder, javamailConfig);

        logger.info("personal folder");

        for (Folder folder : store.getPersonalNamespaces()) {
            logger.info("personal folder : {}", folder);

            this.receiveByFolder(folder, javamailConfig);
        }

        logger.info("shared folder");

        for (Folder folder : store.getSharedNamespaces()) {
            logger.info("shared folder : {}", folder);

            this.receiveByFolder(folder, javamailConfig);
        }

        logger.info("user folder : {}", javamailConfig.getUsername());

        for (Folder folder : store.getUserNamespaces(javamailConfig
                .getUsername())) {
            logger.info("user folder : {}", folder);

            this.receiveByFolder(folder, javamailConfig);
        }

        store.close();
    }

    public void receiveByFolder(Folder folder, JavamailConfig javamailConfig)
            throws MessagingException, IOException {
        logger.info("receive : {}", folder);

        if ((Folder.HOLDS_MESSAGES & folder.getType()) != 0) {
            this.receiveMessageByFolder(folder, javamailConfig);
        }

        if ((Folder.HOLDS_FOLDERS & folder.getType()) != 0) {
            for (Folder childFolder : folder.list()) {
                this.receiveByFolder(childFolder, javamailConfig);
            }
        }

        // 关闭资源
        folder.close(false);
    }

    public void receiveMessageByFolder(Folder folder,
            JavamailConfig javamailConfig) {
        try {
            /*
             * Folder.READ_ONLY：只读权限 Folder.READ_WRITE：可读可写（可以修改邮件的状态）
             */
            folder.open(Folder.READ_WRITE); // 打开收件箱

            // 获得收件箱的邮件列表
            Message[] messages = folder.getMessages();

            // 打印不同状态的邮件数量
            logger.debug("收件箱中共" + messages.length + "封邮件!");
            logger.debug("收件箱中共" + folder.getUnreadMessageCount() + "封未读邮件!");
            logger.debug("收件箱中共" + folder.getNewMessageCount() + "封新邮件!");
            logger.debug("收件箱中共" + folder.getDeletedMessageCount() + "封已删除邮件!");

            logger.debug("------------------------开始解析邮件----------------------------------");

            // 解析邮件
            for (Message message : messages) {
                // IMAPMessage msg = (IMAPMessage) message;
                MimeMessage mimeMessage = (MimeMessage) message;

                try {
                    if (javamailMessageManager.findUniqueBy("messageId",
                            mimeMessage.getMessageID()) != null) {
                        continue;
                    }
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);

                    continue;
                }

                String subject = this.getSubject(mimeMessage);
                logger.debug("[" + subject + "]未读，是否需要阅读此邮件（yes/no）？");

                // BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                // String answer = reader.readLine();
                // String answer = "no";
                // if ("yes".equalsIgnoreCase(answer)) {
                // POP3ReceiveMailTest.parseMessage(msg); // 解析邮件
                // 第二个参数如果设置为true，则将修改反馈给服务器。false则不反馈给服务器
                // msg.setFlag(Flag.SEEN, true); //设置已读标志
                String from = this.getFrom(mimeMessage);
                logger.debug("from : " + from);

                JavamailMessage javamailMessage = new JavamailMessage();

                if (subject.length() > 255) {
                    logger.info("{} length {} larger than 255", subject,
                            subject.length());
                    subject = subject.substring(0, 255);
                }

                javamailMessage.setSubject(subject);
                javamailMessage.setSender(from);
                javamailMessage.setSendTime(mimeMessage.getSentDate());
                javamailMessage.setReceiveTime(mimeMessage.getReceivedDate());
                javamailMessage
                        .setMessageNumber(mimeMessage.getMessageNumber());
                javamailMessage.setMessageId(mimeMessage.getMessageID());
                javamailMessage.setFolder("INBOX");
                logger.debug("before content");

                StringBuffer content = new StringBuffer(30);
                getMailTextContent(message, content);
                logger.debug("content : " + content);
                javamailMessage.setContent(content.toString());
                javamailMessage.setJavamailConfig(javamailConfig);
                javamailMessageManager.save(javamailMessage);
                logger.debug("end");

                // }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public static String getSubject(MimeMessage msg)
            throws UnsupportedEncodingException, MessagingException {
        return MimeUtility.decodeText(msg.getSubject());
    }

    public static String getFrom(MimeMessage msg) throws MessagingException,
            UnsupportedEncodingException {
        String from = "";
        Address[] froms = msg.getFrom();

        if (froms.length < 1) {
            throw new MessagingException("没有发件人!");
        }

        InternetAddress address = (InternetAddress) froms[0];
        String person = address.getPersonal();

        if (person != null) {
            person = MimeUtility.decodeText(person) + " ";
        } else {
            person = "";
        }

        from = person + "<" + address.getAddress() + ">";

        return from;
    }

    public void getMailTextContent(Part part, StringBuffer content)
            throws MessagingException, IOException {
        // 如果是文本类型的附件，通过getContent方法可以取到文本内容，但这不是我们需要的结果，所以在这里要做判断
        boolean isContainTextAttach = part.getContentType().indexOf("name") > 0;

        if (part.isMimeType("text/*") && !isContainTextAttach) {
            content.append(part.getContent().toString());
        } else if (part.isMimeType("message/rfc822")) {
            getMailTextContent((Part) part.getContent(), content);
        } else if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            int partCount = multipart.getCount();

            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                getMailTextContent(bodyPart, content);
            }
        }
    }

    @Resource
    public void setJavamailMessageManager(
            JavamailMessageManager javamailMessageManager) {
        this.javamailMessageManager = javamailMessageManager;
    }

    @Resource
    public void setJavamailConfigManager(
            JavamailConfigManager javamailConfigManager) {
        this.javamailConfigManager = javamailConfigManager;
    }
}
