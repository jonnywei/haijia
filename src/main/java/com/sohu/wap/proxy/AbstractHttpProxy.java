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

	protected static long delay = 10 * 60;
	
	//seconds
	 static long long_request_time = 3;
	    
	 static long max_request_time = 6;
	 

	protected class ProxyChecker implements Runnable {
		@Override
		public void run() {
			List<Future<Boolean>> resultList = new ArrayList<Future<Boolean>>();
			System.out.println("check proxy task");
			Object[] proxyArray = HOST_MAP.keySet().toArray();
			System.out.println("check");
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
					e.printStackTrace();
				} catch (ExecutionException e) {

				} catch (TimeoutException e) {
					// 超时删除
					log.error(key + " timeout! remove");
					HOST_MAP.remove(key);
				}
			}
			log.info("schedule check over! size=" + HOST_MAP.size());

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