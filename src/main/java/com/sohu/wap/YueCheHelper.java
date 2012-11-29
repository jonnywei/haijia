package com.sohu.wap;



import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.wap.util.DateUtil;
import com.sohu.wap.util.RandomUtil;
import com.sohu.wap.util.SystemConfigurations;
import com.sohu.wap.util.ThreadUtil;



/**
 * 程序启动
 *
 */
public class YueCheHelper 
{
    private static Logger log = LoggerFactory.getLogger(YueCheHelper.class);
    
    private static String  IMAGE_CODE_INPUT_METHOD =SystemConfigurations.getSystemStringProperty("system.imagecode.inputmethod","auto") ;
    
    public static boolean IMAGE_CODE_INPUT_METHOD_IS_AUTO = true;
    
    
    public  static String CAR_TYPE[]= new String[] {"als","byd","zdd","fk","stn"};
    
    private static String[]  AM_PM_NUM ={"812","15","58"};
    private static String[]  AM_PM_STR={"上午","下午","晚上"};
    private static String[]  AM_PM_STR1={"sw","xw","ws"};
    private static String[]  AM_PM_STR2={"am","pm","ni"};
    public static Map<String, String> AMPM = new HashMap<String, String>();
    static  {
    	//初始化 ampm信息
    	for(int i =0; i< AM_PM_NUM.length; i++){
    		AMPM.put(AM_PM_NUM[i], AM_PM_STR[i]);
    		AMPM.put(AM_PM_STR[i], AM_PM_NUM[i]);
    		AMPM.put(AM_PM_STR1[i], AM_PM_NUM[i]);
    		AMPM.put(AM_PM_STR2[i], AM_PM_NUM[i]);
    	}
    	
    	if(! IMAGE_CODE_INPUT_METHOD.equals("auto")){
    		IMAGE_CODE_INPUT_METHOD_IS_AUTO = false;
    	}
    	
    	
    }
    
    public static  int   MIN_SCAN_INTEVAL = SystemConfigurations.getSystemIntProperty("system.scan.min.interval", 60);
    
    public static  int   MAX_SCAN_INTEVAL = SystemConfigurations.getSystemIntProperty("system.scan.max.interval", 180);
    
   
    public static  int   MAX_SLEEP_TIME = SystemConfigurations.getSystemIntProperty("system.maxsleeptime", 3);
    
    public static String YUCHE_TIME = SystemConfigurations.getSystemStringProperty("system.yueche.time","812,15") ;
    
    
    
    private static String    BYD_YUECHE_BEGIN_TIME = "07:35";
    
    private static String    SERVICE_BEGIN_TIME ="07:35";
    
    private static String   SERVICE_END_TIME ="20:00";
    
    public  static int     WAITTING_SCAN_INTERVAL= 5;
    
    public static  int SESSION_TIMEOUT_MILLISECOND  =  30 * 60 *1000;
    
    
   
    
    public static   boolean isInServiceTime(){
    	
        return   DateUtil.isCurrTimeInTimeInterval(SERVICE_BEGIN_TIME,SERVICE_END_TIME);
    }
    
    public static  void waitForService(){
    	 // 选择周六周末
        do {
            //在服务时间内
            if (!YueCheHelper.isInServiceTime()){
                ThreadUtil.sleep(YueCheHelper.WAITTING_SCAN_INTERVAL);
            }else{
            	break;
            }
        }while (true);
    }
}
