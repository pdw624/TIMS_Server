package kr.tracom.platform.service.task;

import kr.tracom.platform.service.ServiceLauncher;
import kr.tracom.platform.service.parallel.ThreadPool;
import kr.tracom.platform.tcp.model.TcpChannelMessage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public abstract class ServiceTask extends Thread {
	private volatile boolean shutdownCalled = false;
	
	private ServiceLauncher launcher;

	public ServiceTask(ServiceLauncher launcher) {
		this.launcher = launcher;
	}

	public abstract void parallelWork(TcpChannelMessage tcpMessage);
	
	@Override
	public void run() {		
		ThreadPool threadPool = launcher.getThreadPool();
		BlockingQueue<TcpChannelMessage> readQueue = launcher.getReadQueue();
		
		while (!shutdownCalled) {
			try {
				final TcpChannelMessage tcpMesssage = readQueue.poll(5, TimeUnit.SECONDS);
				if (tcpMesssage == null) {
					continue;
				}
				
				threadPool.work(() -> parallelWork(tcpMesssage));

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void shutdown() {
		this.shutdownCalled = true;
	}
}