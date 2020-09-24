package kr.tracom.platform.service.parallel;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;


public class ThreadProcessor implements Runnable {
	private BlockingQueue<ParallelJob> jobQueue;
	private volatile boolean keepProcessing;

	public ThreadProcessor(BlockingQueue<ParallelJob> queue) {
		this.jobQueue = queue;
		this.keepProcessing = true;
	}

	public void run() {
		while (keepProcessing || !jobQueue.isEmpty()) {
			try {
				ParallelJob j = jobQueue.poll(5, TimeUnit.SECONDS);

				if (j != null) {
					j.process();
				}
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
				return;
			}
		}
	}
	
	public void cancelExecution() {
		this.keepProcessing = false;
	}
}
