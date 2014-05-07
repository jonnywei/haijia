package com.sohu.wap;



import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import com.sohu.wap.proxy.Host;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.wap.util.RandomUtil;
import com.sohu.wap.util.ThreadUtil;



/**
 * 
 * 扫描约车和约考程序
 * 
 *
 */
public class HaijiaNetScanner 
{
    
    
    private static Logger log = LoggerFactory.getLogger(HaijiaNetScanner.class);



    public static void main( String[] args ) throws InterruptedException, IOException
    {
        YueCheHelper.IS_USE_PROXY = false;

        startScanService();

        System.in.read();

    }

    public  static void startScanService () throws InterruptedException{

        YueCheHelper.IS_USE_PROXY =false;

        do {
            //在服务时间内
            if (YueCheHelper.isInServiceTime()){

                oneTime();

                System.out.println("执行完毕");
                //max >休息时间 >min
                ThreadUtil.sleep(YueCheHelper.SCAN_MIN_INTEVAL +

                        RandomUtil.getRandomInt(YueCheHelper.SCAN_MAX_INTEVAL - YueCheHelper.SCAN_MIN_INTEVAL));

            }else{
                log.info("waiting");
                ThreadUtil.sleep(YueCheHelper.WAITTING_SCAN_INTERVAL);
            }
        }while (true);

    }

    public static void oneTime() throws InterruptedException {

        long startTime = System.currentTimeMillis();

        List<Future<Integer>> resultList = new ArrayList<Future<Integer>>();
        //初始化线程池的数目
        ScanXueYuanInfo scanYcInfo = new ScanXueYuanInfo();
        ExecutorService executeService = Executors.newFixedThreadPool( scanYcInfo.getXuanYuanInfo().size());

        for (String accoutId: scanYcInfo.getXuanYuanInfo().keySet()){
            XueYuanAccount  xy = scanYcInfo.getXuanYuanInfo().get(accoutId);
            if ( xy!=null){
                int threadPerUserNum = 1;
//                       YueCheHelper.isScanUseProxy()? YueCheHelper.getProxyNumPreUser():  1; // YueCheHelper.getThreadPerUser();

                for ( int num = 0 ; num < threadPerUserNum; num++){
                    Host proxyHost = null;
//                    if (YueCheHelper.isUseProxy() ){
//                        proxyHost = ConfigHttpProxy.getInstance().getRandomHost();
//                    }
                    ScanYueCheTask yueCheTask = new ScanYueCheTask(xy,proxyHost);
                    resultList.add(executeService.submit(yueCheTask) );

                }
            }
        }
        executeService.shutdown();
        //等待5分钟
        executeService.awaitTermination(5*60, TimeUnit.SECONDS);

        System.out.println("shutdown Now!");

        executeService.shutdownNow();

        for (Future<Integer> fs : resultList) {
            try {
                System.out.println(fs.get(2,TimeUnit.SECONDS));   // 打印各个线程（任务）执行的结果
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
        log.info("task execute time ="+(System.currentTimeMillis()- startTime));
    }












}
