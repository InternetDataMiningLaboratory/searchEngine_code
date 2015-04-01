package com.proj.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class StringUtils {
	public static boolean isBlank(String s) {
		if (s == null || s.isEmpty() || s.trim().isEmpty())
			return true;
		return false;
	}

	public static boolean isNotBlank(String s) {

		return !isBlank(s);
	}

	/**
	 * 将当前date格式转换成需要的日期格式
	 * 
	 * @param date
	 * @param dateFormat
	 *            所需要转换成的日期格式
	 * @return
	 */
	public static Date dateFormatTransform(Date date, String dateFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		String tmp = sdf.format(date);
		Date result = null;
		try {
			result = sdf.parse(tmp);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String getTimeByCalendar() {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);// 获取年份
		int month = cal.get(Calendar.MONTH) + 1;// 获取月份
		int day = cal.get(Calendar.DATE);// 获取日
		int hour = cal.get(Calendar.HOUR);// 小时
		int minute = cal.get(Calendar.MINUTE);// 分
		int second = cal.get(Calendar.SECOND);// 秒
		String gettime = year + "年" + month + "月" + day + "日      " + hour
				+ "时" + minute + "分" + second + "秒 ";
		return gettime;
	}

	/*
	 * 根据编号找到对应工作年限
	 */
	public static String getWorkExperience(String key) {
		HashMap<String, String> WorkExperiences = new HashMap<String, String>();
		String WorkExperience = null;
		WorkExperiences.put("50", "半年");
		WorkExperiences.put("51", "1年");
		WorkExperiences.put("52", "2年");
		WorkExperiences.put("53", "3年");
		WorkExperiences.put("54", "4年");
		WorkExperiences.put("55", "5年");
		WorkExperiences.put("56", "5年以上");
		WorkExperience = WorkExperiences.get(key);
		if (WorkExperience == null) {
			WorkExperience = "半年";
		}
		return WorkExperience;
	}

}