package com.proj.utils;

import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.proj.entity.MailSenderInfo;
import com.proj.entity.MyAuthenticator;

/**
 * ���ʼ��������������ʼ���������
 */
public class SimpleMailSender {
	/**
	 * ���ı���ʽ�����ʼ�
	 * 
	 * @param mailInfo
	 *            �����͵��ʼ�����Ϣ
	 */
	public boolean sendTextMail(MailSenderInfo mailInfo) {
		// �ж��Ƿ���Ҫ�����֤
		MyAuthenticator authenticator = null;
		Properties pro = mailInfo.getProperties();
		if (mailInfo.isValidate()) {
			// �����Ҫ�����֤���򴴽�һ��������֤��
			authenticator = new MyAuthenticator(mailInfo.getUserName(),
					mailInfo.getPassword());
		}
		// �����ʼ��Ự���Ժ�������֤������һ�������ʼ���session
		Session sendMailSession = Session
				.getDefaultInstance(pro, authenticator);
		try {
			// ����session����һ���ʼ���Ϣ
			Message mailMessage = new MimeMessage(sendMailSession);
			// �����ʼ������ߵ�ַ
			Address from = new InternetAddress(mailInfo.getFromAddress());
			// �����ʼ���Ϣ�ķ�����
			mailMessage.setFrom(from);
			// �����ʼ��Ľ����ߵ�ַ�������õ��ʼ���Ϣ��
			Address to = new InternetAddress(mailInfo.getToAddress());
			mailMessage.setRecipient(Message.RecipientType.TO, to);
			// �����ʼ���Ϣ������
			mailMessage.setSubject(mailInfo.getSubject());
			// �����ʼ���Ϣ���͵�ʱ��
			mailMessage.setSentDate(new Date());
			// �����ʼ���Ϣ����Ҫ����
			String mailContent = mailInfo.getContent();
			mailMessage.setText(mailContent);
			// �����ʼ�
			Transport.send(mailMessage);
			return true;
		} catch (MessagingException ex) {
			System.out.println("com.proj.utils.sendTextMail:�ʼ�����ʧ��");
			ex.printStackTrace();
		}
		return false;
	}

	/**
	 * ��HTML��ʽ�����ʼ�
	 * 
	 * @param mailInfo
	 *            �����͵��ʼ���Ϣ
	 */
	public boolean sendHtmlMail(MailSenderInfo mailInfo) {
		// �ж��Ƿ���Ҫ�����֤
		MyAuthenticator authenticator = null;
		Properties pro = mailInfo.getProperties();
		// �����Ҫ�����֤���򴴽�һ��������֤��
		if (mailInfo.isValidate()) {
			authenticator = new MyAuthenticator(mailInfo.getUserName(),
					mailInfo.getPassword());
		}
		// �����ʼ��Ự���Ժ�������֤������һ�������ʼ���session
		Session sendMailSession = Session
				.getDefaultInstance(pro, authenticator);
		try {
			// ����session����һ���ʼ���Ϣ
			Message mailMessage = new MimeMessage(sendMailSession);
			// �����ʼ������ߵ�ַ
			Address from = new InternetAddress(mailInfo.getFromAddress());
			// �����ʼ���Ϣ�ķ�����
			mailMessage.setFrom(from);
			// �����ʼ��Ľ����ߵ�ַ�������õ��ʼ���Ϣ��
			Address to = new InternetAddress(mailInfo.getToAddress());
			// Message.RecipientType.TO���Ա�ʾ�����ߵ�����ΪTO
			mailMessage.setRecipient(Message.RecipientType.TO, to);
			// �����ʼ���Ϣ������
			mailMessage.setSubject(mailInfo.getSubject());
			// �����ʼ���Ϣ���͵�ʱ��
			mailMessage.setSentDate(new Date());
			// MiniMultipart����һ�������࣬����MimeBodyPart���͵Ķ���
			Multipart mainPart = new MimeMultipart();
			// ����һ������HTML���ݵ�MimeBodyPart
			BodyPart html = new MimeBodyPart();
			// ����HTML����
			html.setContent(mailInfo.getContent(), "text/html; charset=utf-8");
			mainPart.addBodyPart(html);
			// ��MiniMultipart��������Ϊ�ʼ�����
			mailMessage.setContent(mainPart);
			// �����ʼ�
			Transport.send(mailMessage);
			return true;
		} catch (MessagingException ex) {
			System.out.println("com.proj.utils.endHtmlMail:�ʼ�����ʧ��");
			ex.printStackTrace();
		}
		return false;
	}

	/*
	 * �һ�������ʼ�����
	 * 
	 * @param email �ʼ��Ľ��ܵ�ַ
	 * 
	 * @param body �����ʼ�������
	 */
	public boolean forgetPwdSendMail(String email, String pwd) {

		String address = ConstantUtil.MAIL_ADDRESS;
		String username = ConstantUtil.MAIL_USERNAME;
		String password = ConstantUtil.MAIL_PASSWORD;

		MailSenderInfo mailInfo = new MailSenderInfo();
		mailInfo.setMailServerHost(address);
		mailInfo.setMailServerPort("25");
		mailInfo.setValidate(true);
		mailInfo.setUserName(username);
		mailInfo.setPassword(password);// ������������
		mailInfo.setFromAddress(username);
		mailInfo.setToAddress(email);
		mailInfo.setSubject("WebHr�һ�����");
		mailInfo.setContent(forgetPwdBody(pwd));

		// �����ʼ�
		if (!sendHtmlMail(mailInfo)) {
			return false;
		}
		return true;
	}
	
	/*
	 * �һ����뷢������
	 * */
	public String forgetPwdBody(String pwd){
		String currenttime = StringUtils.getTimeByCalendar();
		String body = "�װ����û���</br>" + "���ã����� " + currenttime
				+ "�ύ���һ���������</br>" + "���������ǣ�" + pwd
				+ "</br>" + "���Ǳ��˲����뾡���޸����룡�����ڼ�ס���������ɾ�����ʼ�����������й¶��</br>"
				+ "WebHr�û�����</br>" + currenttime;
		return body;
	}

}