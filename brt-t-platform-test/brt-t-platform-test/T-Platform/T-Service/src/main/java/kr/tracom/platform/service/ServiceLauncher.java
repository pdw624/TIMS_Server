package kr.tracom.platform.service;

import kr.tracom.platform.net.config.TimsConfig;
import kr.tracom.platform.service.model.ServiceGetArgs;
import kr.tracom.platform.service.model.ServiceSetArgs;
import kr.tracom.platform.service.parallel.ThreadPool;
import kr.tracom.platform.service.parallel.ThreadPoolImpl;
import kr.tracom.platform.service.task.ServiceTask;
import kr.tracom.platform.tcp.model.TcpChannelMessage;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public abstract class ServiceLauncher implements ServiceInterface {
	public String PlatformId = "-";
	public String ServiceId = "-";
	public String ServiceName = "-";
	public String ServiceVersion = "-";

	private ServiceTask serviceTask;
	
	protected ThreadPool threadpool;
	protected ServiceSetArgs serviceSetArgs;
	protected ServiceGetArgs serviceGetArgs;
	protected BlockingQueue<TcpChannelMessage> readQueue;
	
	
	public ThreadPool getThreadPool() {
		return threadpool;
	}
	public String getPlatformId() {
		return serviceSetArgs.getPlatformId();
	}
	public TimsConfig getTimsConfig() {
		return serviceSetArgs.getTimsConfig();
	}
	
	public ServiceGetArgs getServiceGetArgs() { return serviceGetArgs; }
	public BlockingQueue<TcpChannelMessage> getReadQueue() { return readQueue; }

	public ServiceLauncher(int readQueueSize) {
		serviceGetArgs = new ServiceGetArgs();
		if(readQueueSize > 0) {
			threadpool = new ThreadPoolImpl(Runtime.getRuntime().availableProcessors());
			readQueue = new ArrayBlockingQueue<>(readQueueSize);
		} else {
			threadpool = new ThreadPoolImpl(1);
			readQueue = null;
		}
		serviceGetArgs.setReadQueue(readQueue);
	}

	public abstract SqlSessionFactory getPlatformDb();
	public abstract SqlSessionFactory getServiceDb();
	public abstract void serviceWork(TcpChannelMessage tcpChannelMessage);

	@Override
	public void startup() {
		serviceTask = new ServiceTask(this) {			
			@Override
			public void parallelWork(TcpChannelMessage tcpChannelMessage) {
				serviceWork(tcpChannelMessage);
			}
		};		
		serviceTask.start();
	}

	@Override
	public void shutdown() {
		if(serviceTask != null) {
			serviceTask.shutdown();
		}
	}
}
