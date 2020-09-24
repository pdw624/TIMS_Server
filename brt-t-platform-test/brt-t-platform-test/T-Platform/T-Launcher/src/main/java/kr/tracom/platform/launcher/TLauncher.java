package kr.tracom.platform.launcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.tracom.platform.common.config.AppConfig;
import kr.tracom.platform.common.config.Constants;
import kr.tracom.platform.db.TDatabaseModule;
import kr.tracom.platform.launcher.http.THttpServer;
import kr.tracom.platform.launcher.manager.RuntimeManager;
import kr.tracom.platform.launcher.manager.StartUpManager;
import kr.tracom.platform.launcher.module.TChannelModule;
import kr.tracom.platform.launcher.module.TServiceModule;
import kr.tracom.platform.net.config.TimsConfig;
import kr.tracom.platform.service.model.SyncObject;
import kr.tracom.platform.service.module.TScheduleModule;

public class TLauncher {
	/* singleton */
	private static class SingletonHolder {
		public static final TLauncher INSTANCE = new TLauncher();
	}

	public static TLauncher getInstance() {
		return SingletonHolder.INSTANCE;
	}
	/* singleton */


	private static final Logger logger = LoggerFactory.getLogger(TLauncher.class);
	public static SyncObject restartEvent = new SyncObject();

	public static void main(String[] args) {

		
		AppConfig.read(Constants.PROPERTIES_PATH);
		RuntimeManager.configLogback(Constants.LOGBACK_PATH);
		String lockFile = AppConfig.getApplicationPath() + Constants.LOCK_FILE;

		if(RuntimeManager.isAleadyRunning(lockFile)) {
			logger.error("TLauncher process is already running!");
		} else {
			TLauncher.getInstance().init();
			TLauncher.getInstance().startup();
		}

		try {
			synchronized (restartEvent) {
				restartEvent.wait();
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		RuntimeManager.restartApplication();
	}

	private StartUpManager startUpManager;
	private volatile boolean isRunning = false;

	public void init() {
		startUpManager = new StartUpManager(AppConfig.get("platform.id"));

		TDatabaseModule.getInstance().startup();
		TScheduleModule.getInstance().init(null);
		if("true".equalsIgnoreCase(AppConfig.get("platform.web.enable"))) {
			THttpServer.getInstance().startup();
		}

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            TLauncher.getInstance().shutdown();
            startUpManager.display("shutdown");
        }));
	}

	public void startup() {
		startUpManager.step1();
		startUpManager.step2();
		startUpManager.step3();
		startUpManager.step4();
		startUpManager.step5();
		startUpManager.step6();
		startUpManager.step7();
		startUpManager.step8();

		startUpManager.display("startup");

		TServiceModule.getInstance().startup();
		TChannelModule.getInstance().startup();
		TScheduleModule.getInstance().startup();
		isRunning = true;

		test();
	}

	public void test() {
		/*
		SqlSessionFactory platformDb = PlatformDbFactory.getSqlSessionFactory();

		DbLogJob dbLogJob = new DbLogJob();
		dbLogJob.run(PlatformConfig.PLF_ID, platformDb);
		*/

		/*
		DateTime nowDate = DateTime.now();
		String logPath = AppConfig.getApplicationPath() + "/resources/log";
		FileLogManager.execute("ZAD", nowDate, logPath, 7);
		*/
	}
	
	public void shutdown() {
		try {
			TChannelModule.getInstance().shutdown();
			TServiceModule.getInstance().shutdown();
			TScheduleModule.getInstance().shutdown();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		isRunning = false;
	}

	public TimsConfig getTimsConfig() {
		if(startUpManager == null) return new TimsConfig();
		else return startUpManager.getTimsConfig();
	}

	public boolean getRunning() {
		return isRunning;
	}
}

