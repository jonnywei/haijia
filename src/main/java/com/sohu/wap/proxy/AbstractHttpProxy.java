package com.sohu.wap.proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.wap.util.CheckProxyThreadPool;
import com.sohu.wap.util.RandomUtil;
import com.sohu.wap.util.ScheduledThreadPool;

public abstract class AbstractHttpProxy implements HttpProxy {

	private static Logger log = LoggerFactory
			.getLogger(AbstractHttpProxy.class);

	protected static ExecutorService checkService = CheckProxyThreadPool
			.getInstance().getExecutorService();

	protected static ScheduledExecutorService scheduledService = ScheduledThreadPool
			.getInstance().getScheduledExecutorService();

	protected ConcurrentHashMap<String, Host> HOST_MAP = new ConcurrentHashMap<String, Host>();

	protected static long initialDelay = 0;

	protected static long delay = 30 * 60;
	
	//seconds
	 static long long_request_time = 5;
	    
	 static long max_request_time = 10;
	 
	 static  int  proxy_min_size = 50;
	 
	 protected abstract  void  init();

	protected class ProxyChecker implements Runnable {
		@Override
		public void run() {
			List<Future<Boolean>> resultList = new ArrayList<Future<Boolean>>();
			System.out.println("check proxy task start");
			Object[] proxyArray = HOST_MAP.keySet().toArray();
			int index = 0;
			for (index = 0; index < proxyArray.length; index++) {
				final String key = (String) proxyArray[index];
				final Host host = HOST_MAP.get(key);
				resultList.add(checkService.submit(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return Boolean.valueOf(ProxyHelper.testProxy(host));
					}
				}));
			}
			for (index = 0; index < resultList.size(); index++) {
				final String key = (String) proxyArray[index];
				Future<Boolean> fs = resultList.get(index);
				try {
					Boolean checkOk = fs.get(long_request_time, TimeUnit.SECONDS);
					
					if (!checkOk) {
						HOST_MAP.remove(key);
					}
				} catch (InterruptedException e) {
					log.error(key + " InterruptedException! remove",e);
					HOST_MAP.remove(key);
				} catch (ExecutionException e) {
					// 超时删除
					log.error(key + " ExecutionException ! remove");
					HOST_MAP.remove(key);
				} catch (TimeoutException e) {
					// 超时删除
					log.error(key + " timeout! remove");
					HOST_MAP.remove(key);
				} catch (Exception e){
					// 超时删除
					log.error(key + " exception! remove",e);
					HOST_MAP.remove(key);
				}
			}
			log.info("schedule check over! size=" + HOST_MAP.size());
			//如果代理数目小于30，重新加载
			if (HOST_MAP.size() < proxy_min_size){
				log.info("size less than "+proxy_min_size+".reload! " + HOST_MAP.size());
			    init();
			}

		}
	}

	public ConcurrentHashMap<String, Host> getProxy() {

		return HOST_MAP;
	}

	public Host getRandomHost() {
		Object[] proxyArray = HOST_MAP.keySet().toArray();
		return HOST_MAP.get(proxyArray[RandomUtil
				.getRandomInt(proxyArray.length)]);
	}
}
