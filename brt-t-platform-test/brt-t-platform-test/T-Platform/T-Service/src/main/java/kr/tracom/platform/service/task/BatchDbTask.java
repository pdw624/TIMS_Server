package kr.tracom.platform.service.task;

/*
public class BatchDbTask {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public static Map<String, List<Map<String, String>>> batchQueue = new HashMap<String, List<Map<String,String>>>();
	
	private final Timer timer = new Timer();
	private ServiceDao serviceDao;
	
	public BatchDbTask(SqlSessionFactory dbSession) {
		serviceDao = new ServiceDao(dbSession);
	}
	
	public static void addQueue(String mapperId, Map<String, String> paramMap) {
		if(batchQueue.containsKey(mapperId)) {
			List<Map<String, String>> items = batchQueue.get(mapperId);
			items.add(paramMap);
		} else {
			List<Map<String, String>> items = new ArrayList<Map<String, String>>();
			items.add(paramMap);
			
			batchQueue.put(mapperId, items);
		}
	}

	public void startup() {
		TimerTask task = new TimerTask() {
            @Override
            public void startup() {
            	
            	int affect = 0;
        		long stTime, edTime;
        		double elapseSeconds;

        		Map<String, List<Map<String, String>>> copyMap = new HashMap<String, List<Map<String,String>>>();
        		synchronized (batchQueue) {
        			copyMap.putAll(batchQueue);
        			batchQueue.clear();					
        		}
        		
        		stTime = System.nanoTime();
        		for(Map.Entry<String, List<Map<String, String>>> item : copyMap.entrySet()) {
        			affect += serviceDao.insertBatch(item.getCode(), item.getValue(), 0);
                }	
        		edTime = System.nanoTime();
        		
        		elapseSeconds = (edTime - stTime) / 1000000000.0;		
        		logger.info(String.format("[DB Batch(%s) : %5d]  [%7.3f]", Thread.currentThread().getName(), affect, elapseSeconds));
            }
        };

        timer.scheduleAtFixedRate(task, 5, 5);
	}
	
	public void shutdown() {
		timer.cancel();
        timer.purge();
	}
}
*/