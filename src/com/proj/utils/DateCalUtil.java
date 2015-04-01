package com.proj.utils;

import java.text.SimpleDateFormat;
import java.util.*;

public class DateCalUtil {
	
	public static String diffNow(Date tmp){
	    String ret = "";
	    try{
	    	if(tmp==null)return "";
	        Date now = new Date();
	         
	       
	         
	        if(tmp.after(now)){
	        	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ConstantUtil.ACTIVITY_DATE_FORMAT);
	            return simpleDateFormat.format(tmp);
	        }
	        long s = (now.getTime() - tmp.getTime()) / 1000;
	        long count = 0;
	        if((count = s / (3600 * 24 * 365)) > 0){
	            ret = count + "��ǰ";
	        }else if((count = s / (3600 * 24 * 30)) > 0){
	            ret = count + "��ǰ";
	        }else if((count = s / (3600 * 24)) > 0){
	            ret = count + "��ǰ";
	        }else if((count = s / 3600) > 0){
	            ret = count + "Сʱǰ";
	        }else if((count = s / 60) > 0){
	            ret = count + "����ǰ";
	        }else{
	            ret = "�ո�";
	        }
	    }catch (Exception e) {
	    }
	     
	    return ret;
	}
}
	