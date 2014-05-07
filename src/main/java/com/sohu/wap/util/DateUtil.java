package com.sohu.wap.util;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 *@author jianjunwei  修改
 * 
 */
public class DateUtil 
{
	public static final String FEED_TIME_FORMAT = "MM月dd日 HH:mm";
	public static final String HHMM_TIME_FORMAT = "HH:mm";
	public static final String YEAR_FORMAT = "yyyy";
	public static final String YMDHM_TIME_FROMAT = "yyyy年M月d日HH时mm分";
	public static final String YMDHMS_TIME_FROMAT = "yyyy-MM-dd HH:mm:ss";
	
	
	
	public static String getFeedTimeStr(Date dt) {
	    SimpleDateFormat sdf = new SimpleDateFormat(FEED_TIME_FORMAT, Locale.CHINA);
		return sdf.format(dt).toLowerCase();
	}
	
	/**
	 *得到年 
	 * 
	 */
	public static String getYearStr(Date dt) {
        SimpleDateFormat sdf = new SimpleDateFormat(YEAR_FORMAT, Locale.CHINA);
        return sdf.format(dt).toLowerCase();
    }
	
	
	/**
	 *得到年 
	 * 
	 */
	public static String getCurrYearStr( ) {
		return	getYearStr(new Date());
    }
	
	/**
	 * 获取时间格式化输出
	 * @param dt
	 * @param type
	 * @return
	 */
	public static String getFormatTime(Date dt,String type){
	    SimpleDateFormat sdf = new SimpleDateFormat(type, Locale.CHINA);
	    return sdf.format(dt);
	}
	
	/**
     * 获取时间格式化输出yyyyMMddHHmmss
     * 
     * @return yyyyMMddHHmmss
     */
    public static String getNow(){
        Date date = new Date();
        return  getFormatTime(date,"yyyyMMddHHmmss");
    }
    
    /**
     * 获取当前时间 截取至分
     * @return yyyyMMddHHmm
     */
    public static String getNowMin(){
        return  getFormatTime(new Date(),"yyyyMMddHHmm");
    }
    
    /**
     * 获取当前时间 截取至天
     * @return yyyyMMdd
     */
    public static String getNowDay(){
        return  getFormatTime(new Date(),"yyyyMMdd");
    }
    
    
    /**
     * 计算当前时间是否在时间间隔
     * @author jianjunwei
     * @param startDate 开始时间
     * @param  endDate  结束时间
     * @return 
     */
    public static  boolean isCurrTimeInTimeInterval(Date startDate, Date endDate){
        Date date = new Date();
        if (date.after(startDate) && date.before(endDate)){
            return true;
        }
        return false;
    }
    
    /**
     * 计算当前时间是否在某一天时间间隔 24小时制
     * @author jianjunwei
     * @param startHourMin 开始的小时和分钟
     * @param  endHourMin  结束的小时和分钟
     * @return 
     */
    public static  boolean isCurrTimeInTimeInterval(String startHourMin, String endHourMin){
     
    	return isCurrTimeInTimeInterval(getTodayTime(startHourMin), getTodayTime(endHourMin));
    }
    
    /**
     * 根据分钟得到当天的时间 24小时制
     * @author jianjunwei
     * @return
     */
    public static  Date getTodayTime(String hourMin){
        
        Calendar ca =   Calendar.getInstance(Locale.CHINA);
        
        String  now =  getNowDay() +" ";
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm", Locale.CHINA);
       
        try {
            Date startDate =  sdf.parse(now+hourMin);
            
            return startDate;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
        
        
        return null;
    }
    
    
    /**
     * 获取未来时间 截取至天
     * @return yyyyMMdd
     */
    public static String getFetureDay(int after){
        Calendar ca =   Calendar.getInstance(Locale.CHINA);
        ca.add(Calendar.DAY_OF_MONTH, after);
        ca.getTime();
        return  getFormatTime(ca.getTime(),"yyyyMMdd");
    }
    
    
    
    /**
     * 获取未来时间 7天的周六和周日
     * @return yyyyMMdd
     */
    public static String getSATURDAYDay(){
        Calendar ca =   Calendar.getInstance(Locale.CHINA);
        ca.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        ca.getTime();
        return  getFormatTime(ca.getTime(),"yyyyMMdd");
    }
    
    
    /**
     * 获取未来时间 7天的周六和周日
     * @return yyyyMMdd
     */
    public static Date getDate(String dstr, String format){
       
        SimpleDateFormat df =new SimpleDateFormat(format, Locale.US);
        try {
            Date time =   df.parse(dstr);
            
            Calendar ca =   Calendar.getInstance(Locale.CHINA);
            ca.setTime(time);
            ca.add(Calendar.HOUR_OF_DAY, 4);
            return ca.getTime();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return   new Date();
    }


    /**
     * 得到含有折线的日期
     * @param dstr
     * @return
     */
    public static String getDashDate(String dstr){

        SimpleDateFormat df =new SimpleDateFormat("yyyyMMdd" );
        SimpleDateFormat df2 =new SimpleDateFormat("yyyy-MM-dd" );
        try {
            Date time =   df.parse(dstr);

            return    df2.format(time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return  null;
    }

    public static void main (String[] args){
        System.out.println(getSATURDAYDay());
        System.out.println( isCurrTimeInTimeInterval("07:35","18:00"));
        
        System.out.println(getDate("05-dec-2012 06:39","dd-MMM-yyyy HH:mm"));
       
    }
}
