/**
 *@version:2012-12-5-上午10:53:13
 *@author:jianjunwei
 *@date:上午10:53:13
 *
 */
package com.sohu.wap.proxy;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.wap.YueCheHelper;
import com.sohu.wap.http.HttpUtil4Exposer;
import com.sohu.wap.util.DateUtil;
import com.sohu.wap.util.NetSystemConfigurations;
import com.sohu.wap.util.ThreadUtil;

/**
 * @author jianjunwei
 *
 */
public class XroxyHttpProxy extends AbstractHttpProxy implements HttpProxy {
    
    
    private static Logger log = LoggerFactory.getLogger(XroxyHttpProxy.class);
   
    static String XROXY_PROXY_URL = "http://www.xroxy.com/proxylist.php?port=&type=All_http&ssl=&country=CN&latency=&reliability=&sort=reliability&desc=true&pnum=%d#table";
//    proxy:name=XROXY proxy&host=221.130.29.184&port=8888
//    static String REGEXP = "&host=((\\d{1,3}[.]?){4})&port=(\\d{1,4})";
    static String REGEXP = "((\\d{1,3}[.]?){4})";

    static String TEST_URL = "http://w.sohu.com/t2/reqinfo.do";
    
    static long min_proxy_size  = 30;
    
  //单例对象
    private static  XroxyHttpProxy  _instance;
    
    private static volatile boolean isInit = false;
   
    
    //初始化
   public static XroxyHttpProxy getInstance(){
        
        if (_instance == null){
            synchronized (ConfigHttpProxy.class){
                if (_instance == null){
                    _instance  = new XroxyHttpProxy();
                }
            }
        }
      return   _instance;
    }

    
    
    private  XroxyHttpProxy (){
 
    	GetHostFromXroxyTask loadTask = new GetHostFromXroxyTask();
        
        scheduledService.scheduleWithFixedDelay(loadTask, 0, 60*60, TimeUnit.SECONDS);
        
    }
    
    protected class GetHostFromXroxyTask implements Runnable {
        @Override
        public void run() {
            try {
                log.info("GetHostFromXroxyTask execute");
                loadHostProxyMap();
            } catch (Exception ex) {
                log.error("throw exception", ex);
            }
            System.out.println("check after proxyhost size =" + HOST_MAP.size());
        }
    }; 
  
    private  void loadHostProxyMap(String url) throws ScriptException {

        ScriptEngineManager sem = new ScriptEngineManager();
        ScriptEngine se = sem.getEngineByName("javascript");
//        String pip = NetSystemConfigurations.getSystemStringProperty("system.spy.proxy.ip", "127.0.0.1");
//        int pport = NetSystemConfigurations.getSystemIntProperty("system.spy.proxy.port", 8087);
        String pip ="127.0.0.1";
        int pport =8087;
        
        HttpUtil4Exposer httpUtil4 = HttpUtil4Exposer.createHttpClient(pip,pport);
      
        String result = httpUtil4.getContent(url);
        if (result == null) {
            log.error("fetch proxy info error!");
            return;
        }
//      proxy:name=XROXY proxy&host=221.130.29.184&port=8888

        int index = result.indexOf("proxy:name=XROXY proxy&host=");
        while (index != -1){
        	String  rest = result.substring(index+28);
        	int portIndex = rest.indexOf("&port=");
        	String ip = rest.substring(0,portIndex);
        	
        	String portIndexString = rest.substring(portIndex+6);
        	
        	int portEndIndex = portIndexString.indexOf("&");
        	String port =portIndexString.substring(0,portEndIndex);
        	result = result.substring(index +ip.length() + port.length());
        	index = result.indexOf("proxy:name=XROXY proxy&host=");
        	System.out.println(ip + ":"+ port); 
        	Host host = new Host(ip, port);
            HOST_MAP.put(ip, host);

        	   //向服务器推送信息
           YueCheHelper.addPrxoyHost(ip, port);
        }
        
//        
//        System.out.println(result);
//        Pattern pattern = Pattern.compile(REGEXP);
//        Matcher matcher = pattern.matcher(result);
//        System.out.println(matcher.group());
//	        matcher.find();
////        
//        
//        Document html = Jsoup.parse(result);
//        String scriptStr = "body > script";
//        String script = html.select(scriptStr).get(0).html();
//        se.eval(script);
//
//        String selectStr = "body > table";
//        Elements elems = html.select(selectStr).get(1).select("tr").get(2).select("td > table > tr");
//        int size = 204;
//        for (int i = 3; i < (size - 1); i++) {
//            Element elem = elems.get(i);
//            Elements tds = elem.select("td");
//            // System.out.println(tds.text());
//
//            // 得到ip和port
//            Element td0 = tds.get(0);
//            Element fontNu = td0.select("font.spy1").get(0);
//            // String id = fontNu.text();
//            Element font = td0.select("font.spy14").get(0);
//            String ip = font.text();
//            String strPort = font.child(0).html().substring(prefix.length());
//            strPort = "\"\"+" + strPort.substring(0, strPort.length() - 1);
//            String port = (String) se.eval(strPort);
//
//            Host host = new Host(ip, port);
//
//            Element td1 = tds.get(1);
//            host.setType(td1.text());
//            Element td2 = tds.get(2);
//            host.setAnonymity(td2.text());
//            Element td3 = tds.get(3);
//            host.setCity(td3.text());
//            Element td4 = tds.get(4);
//            host.setName(td4.text());
//            Element td5 = tds.get(5);
//            host.setCheckDate(DateUtil.getDate(td5.text(), "dd-MMM-yyyy HH:mm"));
//            //向服务器推送信息
//            YueCheHelper.addPrxoyHost(ip, port);
//            
//            System.out.println(host);
//
//            HOST_MAP.put(ip, host);

//        }
    }

    
    
    private  void loadHostProxyMap() throws ScriptException {
            
        for(int i= 0; i< 100; i++){
          String url =String.format(XROXY_PROXY_URL, i)  ;
          System.out.println(url);
          loadHostProxyMap(url); 
        }
       
        System.out.println("load ok! size="+HOST_MAP.size());
 
    }

    /**
     * @param args
     * @throws ScriptException 
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws ScriptException, InterruptedException {

        Map<String, Host>  hostMap =XroxyHttpProxy.getInstance().getProxy();
//		ThreadUtil.sleep(120);
//		Iterator<String> iter = hostMap.keySet().iterator();
//		int index =1000075;
//		while(iter.hasNext()){
//			index ++;
//			String key = iter.next();
//			Host host =hostMap.get(key);
//			System.out.println(index+"="+host.getIp()+":"+host.getPort());
//		}
//		System.exit(0);
    }


    /* (non-Javadoc)
     * @see com.sohu.wap.proxy.AbstractHttpProxy#init()
     */
    @Override
    protected void init() {
        // TODO Auto-generated method stub
        
    }

}
