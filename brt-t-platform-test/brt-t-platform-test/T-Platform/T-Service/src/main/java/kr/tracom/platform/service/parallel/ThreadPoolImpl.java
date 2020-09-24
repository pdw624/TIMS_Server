package kr.tracom.platform.service.parallel;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolImpl implements ThreadPool {
	private BlockingQueue<ParallelJob> jobQueue = new ArrayBlockingQueue<ParallelJob>(256);
	private ExecutorService executorService = Executors.newCachedThreadPool();
	private List<ThreadProcessor> jobList = new LinkedList<ThreadProcessor>();
	private volatile boolean shutdownCalled = false;

	public ThreadPoolImpl(int poolSize) {
		for (int i = 0; i < poolSize; i++) {
			ThreadProcessor jobThread = new ThreadProcessor(jobQueue);
			jobList.add(jobThread);
			executorService.execute(jobThread);
		}
	}

	public boolean work(ParallelJob j) {
		if (!shutdownCalled) {
			try {
				jobQueue.put(j);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
				return false;
			}
			return true;
		} else {
			return false;
		}
	}

	public int queueCount() {
		return jobQueue.size();
	}

	public void finish() {
		shutdownCalled = true;

		for (ThreadProcessor j : jobList) {
			j.cancelExecution();
		}
		executorService.shutdown();
	}
}
