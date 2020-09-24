package kr.tracom.platform.brt.job;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import kr.tracom.platform.brt.TBrtLauncher;
import kr.tracom.platform.brt.handler.LivingHandler;
import kr.tracom.platform.common.config.AppConfig;
import kr.tracom.platform.service.manager.ErrorManager;

public class AirQualityJob implements Job {
	private final String platformId = AppConfig.get("platform.id");
	
	@Override
	public void execute(JobExecutionContext ctx) throws JobExecutionException {
		try {
			JobDataMap jobDataMap = ctx.getJobDetail().getJobDataMap();
			TBrtLauncher launcher = (TBrtLauncher)jobDataMap.get("LAUNCHER");
			
			LivingHandler.getInstance().sendAllMessageAirQuality(launcher.getTimsConfig());
		} catch(Exception e) {
			ErrorManager.trace(platformId,
					this.getClass().getName(), Thread.currentThread().getStackTrace(),
					"PLATFORM PARAMETER", e.getMessage());
		}
	}
}
