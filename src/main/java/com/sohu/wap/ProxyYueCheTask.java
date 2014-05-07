//package com.sohu.wap;
//
//import java.io.IOException;
//import java.util.concurrent.Callable;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.sohu.wap.bo.Result;
//import com.sohu.wap.http.HttpUtil4;
//import com.sohu.wap.http.HttpUtil4Exposer;
//import com.sohu.wap.util.RandomUtil;
//import com.sohu.wap.util.ThreadUtil;
//
//public class ProxyYueCheTask  extends YueChe2 implements Callable<Integer> {
//
//	protected static Logger log = LoggerFactory.getLogger(ProxyYueCheTask.class);
//	
//	XueYuanAccount yueCheItem;
//	
//	String date;
//	
//	public ProxyYueCheTask(XueYuanAccount yueCheItem, String date){
//	  
//
//	    if (YueCheHelper.isUseProxy()){
//	    		 httpUtil4 = HttpUtil4Exposer.createHttpClient(YueCheHelper.PROXY_IP,YueCheHelper.PROXY_PORT);
//	    }else{
//	    		 httpUtil4 = HttpUtil4Exposer.createHttpClient();
//	    }
//	
//		this.yueCheItem = yueCheItem;
//		this.date = date; 
//	}
//	
//	@Override
//	public Integer call() throws Exception {
//	    
//		 YueCheHelper.waiting(yueCheItem.getCarType());
//		 doLogin();
//         doYuche();
//         if (yueCheItem.isBookSuccess()){
//        	 return Integer.valueOf(0);
//         }
//         return Integer.valueOf(1);
//	}
//	
//	
//	private  void  doLogin ( ) throws InterruptedException{
//        boolean  isLogin = false;
//        boolean first = true;
//      do {
//             if (!first){
//                 log.error("login error. retry!");
//                 ThreadUtil.sleep( RandomUtil.getRandomInt(YueCheHelper.MAX_SLEEP_TIME));
//             }else{
//                 first = false;
//             }
//             
//             isLogin =  login(yueCheItem.getUserName() , yueCheItem.getPassword());
//            
//       }while (!isLogin);
//        log.info("login success!");
//       return ;
//    }
//    
//    /**
//     * @throws IOException 
//     * 
//     */
//    private  void  doYuche () throws InterruptedException {
//    
//    	String[] timeArray = yueCheItem.getYueCheAmPm().split("[,;]");
//        if (timeArray.length  <  0) {
//        	timeArray = YueCheHelper.YUCHE_TIME.split("[,;]");
//        }
//        
//        for (String amPm1 : timeArray){  //按情况约车
//            String amPm  =  YueCheHelper.AMPM.get(amPm1);
//            boolean  isSuccess = false;
//            boolean first = true;
//            do {
//                 if (!first){
//                     log.error("yuche  error. retry!");
//                     ThreadUtil.sleep( RandomUtil.getRandomInt(YueCheHelper.MAX_SLEEP_TIME));
//                 }else{
//                     first = false;
//                 }
//               Result ret =  yuche(date, amPm,false);
//              int  result  = ret.getRet();
//              if (result == YueChe.BOOK_CAR_SUCCESS){
//                  isSuccess = true;
//                  String info = yueCheItem.getUserName() +":"+ret.getData()+":"+date+ YueCheHelper.AMPM.get(amPm)+"约车成功";
//                  System.out.println(info);
//                  log.info(info);
//                  yueCheItem.setBookSuccess(isSuccess);
//              }else if (result == YueChe.NO_CAR){  //无车
//                  System.out.println(date + YueCheHelper.AMPM.get(amPm)+"无车!");
//                  break;
//              }else if (result == YueChe.GET_CAR_ERROR){  //无车
//                  System.out.println("得到车辆信息错误！重试！");
//              }else if (result == YueChe.ALREADY_BOOKED_CAR){  //无车
//            	  String info = yueCheItem.getUserName() +":"+ret.getData()+":"+date+ YueCheHelper.AMPM.get(amPm)+
//            	  "该日已经预约车辆。不能在约车了！";
//            	  log.info(info);
//                  System.out.println(info);
//                  break;
//              }else {  //无车
//                  System.out.println("未知错误！重试!RUSULT="+result);
//              }
//              
//             }while (!isSuccess);
//            
//            if (isSuccess){ //如果约车成功的话，退出
//            	break;
//            }
//            
//        }    
//         
//        
//       log.info("yuche finish !");
//        return ;
//    }
//    
//    
//    
//
//}
