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
	 * ����ǰdate��ʽת������Ҫ�����ڸ�ʽ
	 * 
	 * @param date
	 * @param dateFormat
	 *            ����Ҫת���ɵ����ڸ�ʽ
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
		int year = cal.get(Calendar.YEAR);// ��ȡ���
		int month = cal.get(Calendar.MONTH) + 1;// ��ȡ�·�
		int day = cal.get(Calendar.DATE);// ��ȡ��
		int hour = cal.get(Calendar.HOUR);// Сʱ
		int minute = cal.get(Calendar.MINUTE);// ��
		int second = cal.get(Calendar.SECOND);// ��
		String gettime = year + "��" + month + "��" + day + "��      " + hour
				+ "ʱ" + minute + "��" + second + "�� ";
		return gettime;
	}

	/*
	 * ���ݱ���ҵ���Ӧ��������
	 */
	public static String getWorkExperience(String key) {
		HashMap<String, String> WorkExperiences = new HashMap<String, String>();
		String WorkExperience = null;
		WorkExperiences.put("50", "����");
		WorkExperiences.put("51", "1��");
		WorkExperiences.put("52", "2��");
		WorkExperiences.put("53", "3��");
		WorkExperiences.put("54", "4��");
		WorkExperiences.put("55", "5��");
		WorkExperiences.put("56", "5������");
		WorkExperience = WorkExperiences.get(key);
		if (WorkExperience == null) {
			WorkExperience = "����";
		}
		return WorkExperience;
	}

}