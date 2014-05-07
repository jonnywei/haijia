package com.sohu.wap;



import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.wap.proxy.ConfigHttpProxy;
import com.sohu.wap.proxy.Host;
import com.sohu.wap.util.DateUtil;
import com.sohu.wap.util.NetSystemConfigurations;
import com.sohu.wap.util.SystemConfigurations;
import com.sohu.wap.util.ThreadPool;



/**
 * 约车主程序
 *停
 */
public class HaijiaMain 
{
    private static Logger log = LoggerFactory.getLogger(HaijiaMain.class);
   
    public static void main( String[] args ) throws InterruptedException, IOException
    {

    	ExecutorService executeService = ThreadPool.getInstance().getExecutorService();
    	
        List<Future<Integer>> resultList = new ArrayList<Future<Integer>>();  
        
        //约车日期
        String date =  DateUtil.getFetureDay(7);
        String dateModel = NetSystemConfigurations.getSystemStringProperty("system.yueche.date.model", "auto");
        
        if(dateModel.equals("config")){
       	 	date = NetSystemConfigurations.getSystemStringProperty("system.yueche.date", DateUtil.getFetureDay(7));
       }
      
      //初始化代理信息
//        Host host = ConfigHttpProxy.getInstance().getRandomHost();
        
        System.out.println("抢车日期为:"+ date);
       
        YueCheHelper.waitForService();
        
        if (YueCheHelper.isEnterCreakerModel()){
          //  进入破解模式
          //  速度肯定是最快的了
          //  利用海驾的验证码漏洞，事先输入验证码，之后约车
            System.out.println("Open Creak Model");
            log.info("Open Creak Model");
            CookieImgCodeHelper.getImageCodeCookie();
        }
      
        
        for (String accoutId: AccountMap.getInstance().getXueYuanAccountMap().keySet()){
            YueCheItem  xy =AccountMap.getInstance().getXueYuanAccountMap().get(accoutId);
            if ( xy!=null){
                if (YueCheHelper.isUseProxy()){
                    for ( int num = 0 ; num < YueCheHelper.getProxyNumPreUser(); num++){

                        YueCheTask yueCheTask = new YueCheTask(xy,date,ConfigHttpProxy.getInstance().getRandomHost());
                        resultList.add(executeService.submit(yueCheTask) );
                    }
                }else{
                    YueCheTask yueCheTask = new YueCheTask(xy,date,null);
                    resultList.add(executeService.submit(yueCheTask) );
                }
             }
        }
        
        executeService.shutdown(); 
        
        for (Future<Integer> fs : resultList) {  
            try {  
                System.out.println(fs.get());   // 打印各个线程（任务）执行的结果  
            } catch (InterruptedException e) {  
                e.printStackTrace();  
            } catch (ExecutionException e) {  
            	executeService.shutdownNow();  
                e.printStackTrace();  
                return;  
            }  
        }  
        
        System.out.println("请按任意键退出程序!");
        System.in.read();
      
    }
    
   
    
    
}
