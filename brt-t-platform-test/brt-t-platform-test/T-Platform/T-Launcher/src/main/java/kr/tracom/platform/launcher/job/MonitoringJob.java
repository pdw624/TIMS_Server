package kr.tracom.platform.launcher.job;

import kr.tracom.platform.common.config.AppConfig;
import kr.tracom.platform.common.util.SysMonUtil;
import kr.tracom.platform.service.config.PlatformConfig;
import kr.tracom.platform.service.dao.PlatformDao;
import kr.tracom.platform.service.dao.PlatformMapper;
import kr.tracom.platform.service.domain.ItH2DbStatus;
import kr.tracom.platform.service.domain.ItPlatformStatus;
import kr.tracom.platform.service.manager.ErrorManager;
import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitoringJob implements Job {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final String platformId = AppConfig.get("platform.id");

	@Override
	public void execute(JobExecutionContext ctx) throws JobExecutionException {
		JobDataMap jobDataMap = ctx.getJobDetail().getJobDataMap();
		int period = jobDataMap.getInt("PERIOD");

		try {
			ItPlatformStatus appStatus = (ItPlatformStatus) PlatformConfig.paramMap.get("PLF_STATUS");
			double maxCpu = (double) PlatformConfig.paramMap.get("MAX_CPU");
			double maxRam = (double) PlatformConfig.paramMap.get("MAX_RAM");
			double maxHdd = (double) PlatformConfig.paramMap.get("MAX_HDD");
			double maxJvm = (double) PlatformConfig.paramMap.get("MAX_JVM");
			double maxMdb = (double) PlatformConfig.paramMap.get("MAX_MDB");

			appStatus.setElapsedTime(appStatus.getElapsedTime() + period);
			appStatus.setCpuUsage(SysMonUtil.getCpuLog());
			appStatus.setRamUsage(SysMonUtil.getRamLog());
			appStatus.setHddUsage(SysMonUtil.getHddLog());
			appStatus.setJvmUsage(SysMonUtil.getJvmLog());
			appStatus.setUpdateDateTime(DateTime.now().toString(PlatformConfig.PLF_DT_FORMAT));

			PlatformDao platformDao = new PlatformDao();
			platformDao.update(PlatformMapper.STATUS_UPDATE, appStatus.toMap());
			ItH2DbStatus dbStatus = (ItH2DbStatus) platformDao.select(PlatformMapper.DB_STATUS, null);

			double cpu = SysMonUtil.getCpuUsage();
			double ram = SysMonUtil.getRamUsage();
			double hdd = SysMonUtil.getHddUsage();
			double jvm = SysMonUtil.getJvmUsage();
			double mdb = (double) dbStatus.getMemoryUsed() / (double) (dbStatus.getMemoryFree() + dbStatus.getMemoryUsed()) * 100.0;

			if (cpu > maxCpu) {
				logger.info(String.format("cpu usage : %.2f%%", cpu));
			}
			if (ram > maxRam) {
				logger.info(String.format("ram usage : %.2f%%", ram));
			}
			if (hdd > maxHdd) {
				logger.info(String.format("hdd usage : %.2f%%", hdd));
			}
			if (jvm > maxJvm) {
				logger.info(String.format("jvm usage : %.2f%%", jvm));
			}
			if (mdb > maxMdb) {
				logger.info(String.format("mdb usage : %.2f%%", mdb));
			}
		} catch (Exception e) {
			ErrorManager.trace(platformId,
					this.getClass().getName(), Thread.currentThread().getStackTrace(),
					"PLATFORM PARAMETER", e.getMessage());
		}
	}
}
