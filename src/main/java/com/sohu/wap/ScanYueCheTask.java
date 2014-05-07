package com.sohu.wap;

import java.io.IOException;
import java.util.Map;

import com.sohu.wap.bo.DayCarInfo;
import com.sohu.wap.bo.Result;
import com.sohu.wap.core.Constants;
import com.sohu.wap.http.HttpUtil4Exposer;
import com.sohu.wap.proxy.ConfigHttpProxy;
import com.sohu.wap.proxy.Host;
import com.sohu.wap.util.RandomUtil;
import com.sohu.wap.util.ThreadUtil;

public class ScanYueCheTask extends YueCheTask {

	public static int SCAN_YUECHE_SUCCESS = 0;
	public static int ALREADY_YUECHE  =1;
	public static int NO_CAR = 2;
	
	boolean isLogon = false;
	long  lastLoginTime = 0;

    private XueYuanAccount xueYuanAccount;
	
	public ScanYueCheTask(XueYuanAccount xueYuan, Host host) {
	    
	    super();

        if (host != null ){
                 httpUtil4 = HttpUtil4Exposer.createHttpClient(host.getIp(),host.getPort());
        }else{
                 httpUtil4 = HttpUtil4Exposer.createHttpClient();
        }

        this.xueYuanAccount = xueYuan;

        log.info(this.xueYuanAccount.getUserName()+" init thread");
 
	}
	
	@Override
	public Integer call()  throws Exception {
	    log.info(this.xueYuanAccount.getUserName()+"  yueche thread start!");
		 try {
//            YueCheHelper.waiting(yueCheItem.getCarType());

             int loginResult = doLogin();

             if ( loginResult == 0 ){
                 System.out.println(xueYuanAccount.getUserName());

                 for (YueCheItem yueCheItem : xueYuanAccount.getYueCheItemList()){  // 约考短路
                      if (isYueKao(yueCheItem))  {
                         return scanYueKao(yueCheItem);
                      }
                 }
                 scanYueChe();

             } else if ( loginResult == 3 ) {
                 String info ="accountError:"+ xueYuanAccount.getUserName()+","+ xueYuanAccount.getPassword();
                 log.error(info);
                 for (YueCheItem yueCheItem : xueYuanAccount.getYueCheItemList()){
                     YueCheHelper.updateYueCheBookInfo(yueCheItem.getId(), YueCheItem.BOOK_CAR_ACCOUNT_ERROR, info);
                 }
             }
            
        } catch (InterruptedException e) {
              log.error("cancel task! ");
              return Integer.valueOf(2);
        }  
        return Integer.valueOf(1);
        
	}


	private boolean isYueKao(YueCheItem yueCheItem){
		if ( yueCheItem.getKm() != null &&  yueCheItem.getKm().startsWith("ks"))
			return true;
		return false;
	}
	
	/**
	 * @throws InterruptedException 
	 * 
	 * 
	 */
	public int scanYueChe() throws InterruptedException{

        Result<Map<String, DayCarInfo>>  availableCarInfoResult  = getAvailableCarInfo();
        if (availableCarInfoResult.getRet() != 0 ){
            return 1;
        }


        for (YueCheItem yueCheItem : xueYuanAccount.getYueCheItemList()){
            Result<String> result = canYueChe(yueCheItem, availableCarInfoResult.getData());
            int yueCheInfo = result.getRet();

            if (yueCheInfo == 0){
                doYueche(yueCheItem, result.getData(), Constants.AM_STR);
            } else if (yueCheInfo == 1){
                doYueche(yueCheItem, result.getData(),Constants.PM_STR);

            }else if (yueCheInfo == 2){
                doYueche(yueCheItem, result.getData(),Constants.NI_STR);
            }else if (yueCheInfo == 3){
                return ALREADY_YUECHE;

            }else if (yueCheInfo == 4){

            }else if (yueCheInfo == 5){
                isLogon =false;
            }else if (yueCheInfo == 6){
                log.info("canYueChe InternalServerError");
            }

        }

        return 0;

//
//
//
//
//		if (yueCheItem.isBookSuccess()){
//			return SCAN_YUECHE_SUCCESS;
//		}
//		return NO_CAR;
	}


    /**
     * 0 上午可以
     * 1 下午可以
     * 2 晚上可以
     * 3 该日已经约车
     * 4 无车
     *
     */
    public Result<String>  canYueChe (YueCheItem yueCheItem , Map<String, DayCarInfo> yueCheCarInfoMap ){

        Result<String>  ret = new Result<String>(4);

        String yueCheDate = yueCheItem.getYueCheDate();
        DayCarInfo ycCarInfo =  yueCheCarInfoMap.get(yueCheDate);
        if (ycCarInfo != null){
            String[] timeArray = yueCheItem.getAmPm().split("[,;]");
            if (timeArray.length  <  0) {
                timeArray = YueCheHelper.YUCHE_TIME.split("[,;]");
            }
            //如果今天已经约车了
            if ( ycCarInfo.getCarInfo().get("am").equals("已约") ||  ycCarInfo.getCarInfo().get("pm").equals("已约") ||  ycCarInfo.getCarInfo().get("ni").equals("已约")){
                ret.setRet(3);
            }else {
                for (String amPmStr : timeArray){  //按情况约车
                    String info = ycCarInfo.getCarInfo().get(amPmStr);
                    if (info.equals("无")){

                    }else if (info.equals("已约")){
                        ret.setRet(3);
                    }else{
                        ret.setData(yueCheDate); //设置约车日期
                        if (Constants.AM_STR.equals(amPmStr)){
                            ret.setRet(0);
                            return ret;

                        }else if (Constants.PM_STR.equals(amPmStr)){
                            ret.setRet(1);
                            return ret;
                        }else{
                            ret.setRet(2);
                            return ret;
                        }
                    }
                }
            }

        }
        ret.setRet(4);
        return ret;
    }



    /**
	 * 科目考试
	 * @throws InterruptedException 
	 * 
	 * 
	 */
	public int scanYueKao(YueCheItem yueCheItem) throws InterruptedException{
		
		doLogin () ;
		System.out.println(yueCheItem.getUserName());
		
		Result<String> result = canYueKao(yueCheItem.getYueCheDate(), yueCheItem.getYueCheAmPm(), yueCheItem.getKm());
		
		int yueCheInfo = result.getRet();
		if (yueCheInfo == 0){
			doYueKao( yueCheItem.getKm());
			
		} else if (yueCheInfo == 1){
			
		}else if (yueCheInfo == 2){
			return ALREADY_YUECHE;
		}else if (yueCheInfo == 3){
			return ALREADY_YUECHE;
			
		}else if (yueCheInfo == 4){
		
		}
		
		if (yueCheItem.isBookSuccess()){
			return SCAN_YUECHE_SUCCESS;
		}
		return NO_CAR;
	}

    /**
     *
     *0 登录成功
     *1 登录失败
     *2 已经约车成功
     *3 账号密码错误
     *3 无法进行下一步了
     * */
	private  int  doLogin () throws InterruptedException {
		
		long currentTime = System.currentTimeMillis();
		
		//为了加快访问速度，只加载一次login页面
		//如果没有访问过登录页面，或者上次访问登录页面超过了超时时间
		if ( ! isLogon || (currentTime - lastLoginTime > YueCheHelper.LOGIN_SESSION_TIMEOUT_MILLISECOND) ){
			boolean  isLoginSuccess = false;
	        boolean first = true;
	        do {
	             if (!first){
	                 log.error("login error. retry!");
	                 ThreadUtil.sleep(  RandomUtil.getRandomInt(YueCheHelper.MAX_SLEEP_TIME));
	             }else{
	                 first = false;
	             }
	          int result =  login(xueYuanAccount.getUserName() , xueYuanAccount.getPassword());
	          if (result == YueChe.LONGIN_SUCCESS){
	              isLoginSuccess =  true;
	          } else if( result == YueChe.LONGIN_ACCOUNT_ERROR ){

                  return 3;
              }
	           
	       }while (!isLoginSuccess);
	        
	        isLogon = true;
	        lastLoginTime = System.currentTimeMillis();
	        log.info(xueYuanAccount.getUserName()+" login success!");
		}else{
	        log.info(xueYuanAccount.getUserName()+" retain login status!");

		}
        return 0;
        
    }
	
    
    /**
     * @throws IOException 
     * 
     */
    private  void  doYueche (YueCheItem yueCheItem, String date, String amPm ) throws InterruptedException {
    
    	//按情况约车
        amPm = YueCheHelper.AMPM.get(amPm);
        boolean  isSuccess = false;
        boolean first = true;
        do {
             if (!first){
                 log.error("yuche  error. retry!");
                 Thread.sleep(1000 * RandomUtil.getRandomInt(YueCheHelper.MAX_SLEEP_TIME));
             }else{
                 first = false;
             }
        
             Result<String> ret =  null;
             if(Constants.KM3.equals(yueCheItem.getKm())){
            	 ret =  yuche(date, amPm,Constants.KM3_HiddenKM, yueCheItem.getPhoneNum());
             }else if (Constants.KM1.equals(yueCheItem.getKm())){
            	 ret =  yuche(date, amPm,Constants.KM1_HiddenKM, yueCheItem.getPhoneNum());
             }else if (Constants.KM_AUTO.equals(yueCheItem.getKm())) {
            	 ret =  yuche(date, amPm,0, yueCheItem.getPhoneNum());
             }else{
            	 ret =  yuche(date, amPm,Constants.KM2_HiddenKM, yueCheItem.getPhoneNum());
             }

             int  result  = ret.getRet();
             
          if (result == YueChe.BOOK_CAR_SUCCESS){
              isSuccess = true;
              String info = yueCheItem.getUserName() +":"+ret.getData()+":"+date+ YueCheHelper.AMPM.get(amPm)+"约车成功";
              System.out.println(info);
              log.info(info);
              YueCheHelper.updateYueCheBookInfo(yueCheItem.getId(), XueYuanAccount.BOOK_CAR_SUCCESS, info);

              yueCheItem.setBookSuccess(isSuccess);
          }else if (result == YueChe.NO_CAR){  //无车
              System.out.println(date + YueCheHelper.AMPM.get(amPm)+"无车!");
              break;
          }else if (result == YueChe.GET_CAR_ERROR){  //无车
              System.out.println("得到车辆信息错误！重试！");
          }else if (result == YueChe.ALREADY_BOOKED_CAR){  //无车
              System.out.println(date+"该日已经预约车辆。不能在约车了！");
              break;
          }else {  //无车
              System.out.println("未知错误！重试!RUSULT="+result);
          }
          
         }while (!isSuccess);
        
        
    
       log.info("yuche finish !");
        return ;
    }

    
    /**
     * @throws IOException 
     * 
     */
    private  void  doYueKao (String ks) throws InterruptedException {
    
    	//按情况约车
    
    	boolean  isSuccess = false;
        boolean first = true;
        do {
             if (!first){
                 log.error("yuche  error. retry!");
                 Thread.sleep(1000 * RandomUtil.getRandomInt(YueCheHelper.MAX_SLEEP_TIME));
             }else{
                 first = false;
             }
        
          Result<String > ret =  yueKao(ks);
          
          int  result  = ret.getRet();
             
          if (result == YueChe.YUE_KAO_SUCCESS){
              isSuccess = true;
              String info = yueCheItem.getUserName() +":"+ret.getData() +"约考成功";
              System.out.println(info);
              log.info(info);
              yueCheItem.setBookSuccess(isSuccess);
          }else if (result == YueChe.YUE_KAO_NO_POSITION){  
              System.out.println(date + "可预约人数不足");
              break;
          }else if (result == YueChe.YUE_KAO_CANCEL){   
              System.out.println("错误！取消！错误");
          }else if (result == YueChe.YUE_KAO_ERROR){   
              System.out.println("约考失败");
              break;
          }else {  //无车
              System.out.println("未知错误！重试!RUSULT="+result);
          }
          
         }while (!isSuccess);
        log.info("yuekao finish !");
        return ;
    }
    
     
}
