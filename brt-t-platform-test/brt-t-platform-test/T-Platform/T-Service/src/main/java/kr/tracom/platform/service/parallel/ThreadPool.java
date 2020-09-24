package kr.tracom.platform.service.parallel;

public interface ThreadPool {	
    public boolean work(ParallelJob j);
    public void finish();
    public int queueCount();   
}