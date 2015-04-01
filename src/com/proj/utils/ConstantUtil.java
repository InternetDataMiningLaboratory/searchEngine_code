package com.proj.utils;

public class ConstantUtil {
	
	public static final String SYSTEM_ID = "Admin";
	
	public static final String DATE_FORMAT="yyyy-MM-dd HH:mm:ss";//日期格式化
	
	public static final String ACTIVITY_DATE_FORMAT="yyyy-MM-dd";//活动日期格式化
//关注问题有答案，发送信息
	public static final String NEW_ANSWER_MESSAGE = "尊敬的%s,您关注的问题\"%s\"有了新的答案，请点击查看.";
//	被邀请回答问题
	public static final String INVITE_ANSWER_QUESTION="尊敬的%s,您被邀请回答问题\"%s\"";
	
//	发布新的活动
	public static final String NEW_ACTIVITY_MESSAGE = "尊敬的%s,%s发布了新的活动\"%s\"，请点击查看.";
//	改动的活动
	public static final String EDIT_ACTIVITY_MESSAGE = "尊敬的%s,您参加的活动\"%s\"有了改动，请点击查看.";
//	活动地址
	public static final String ACTIVITY_URL = "/getActivityById.do?activity_id=%s";
//	问题地址
	public static final String QUESTION_URL = "/getQuestionById.do?questionId=%s";
	
//	匿名回答
	
	public static final String ANONYMOUS_NAME="匿名用户";
	public static final String ANONYMOUS_PHOTO="/picture/userPhoto/default.jpg";
	
	//发送邮件
	public static final String MAIL_ADDRESS = "pop.163.com";//邮件发送方地址
	
	public static final String MAIL_USERNAME = "whuwebhr@163.com";//发送方用户名
	
	public static final String MAIL_PASSWORD = "whuwebhrb506";//发送方密码
	
	//图片保存相对位置
	public static final String RELATIVE_PATH = "/picture/userPhoto/";
	
	//管理员注册密钥
	public static final String ADMIN_PWD = "whuwebhr";
}