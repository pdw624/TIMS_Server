package kr.tracom.platform.bis.job;

import kr.tracom.platform.bis.TBisLauncher;
import kr.tracom.platform.service.config.PlatformConfig;
import kr.tracom.platform.service.dao.PlatformDao;
import kr.tracom.platform.service.dao.PlatformMapper;
import kr.tracom.platform.service.domain.MtBackupTable;
import kr.tracom.platform.service.manager.BackupManager;
import kr.tracom.platform.service.manager.CodeManager;
import kr.tracom.platform.service.model.BackupArgs;
import org.apache.ibatis.session.SqlSessionFactory;
import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class BackupJob60 implements Job {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final String period = String.valueOf(CodeManager.ScheduleCycle.ONE_MINUTES.getValue());

	public BackupJob60() {

	}

	public void execute(JobExecutionContext ctx) throws JobExecutionException {
		JobDataMap jobDataMap = ctx.getJobDetail().getJobDataMap();
		TBisLauncher launcher = (TBisLauncher)jobDataMap.get("LAUNCHER");

		SqlSessionFactory platformDb = launcher.getPlatformDb();
		run(launcher.ServiceId, platformDb);
	}

	private void run(String serviceId, SqlSessionFactory platformDb) {
		PlatformDao platformDao = new PlatformDao(platformDb);

		List<Object> list = platformDao.selectList(
				PlatformMapper.BACKUP_SELECT,
				platformDao.buildMap("APP_ID", serviceId, "PERIOD", period));

		BackupArgs backupArgs = new BackupArgs();
		backupArgs.setServiceId(serviceId);
		backupArgs.setBatchSize(100);
		backupArgs.setTimeType("SECOND");
		backupArgs.setTime("-" + period);
		backupArgs.setNowDateTime(DateTime.now().toString(PlatformConfig.PLF_DT_FORMAT));

		MtBackupTable item;
		int affect, total = 0;
		StringBuilder sb = new StringBuilder();
		for(Object obj : list) {
			item = (MtBackupTable)obj;

			if(item.getType().equalsIgnoreCase(CodeManager.BackupType.MOVE.getValue())) {
				backupArgs.setTableKey(item.getTableKey());
				backupArgs.setServiceTable(item.getServiceTableName().toLowerCase());
				backupArgs.setFieldList(item.getFieldList());
				backupArgs.setPlatformTable(item.getPlatformTableName());

				affect = BackupManager.dbLinkBackup(backupArgs, platformDb, item.getDbLinkMapper(), item.getDeleteMapper());
			} else if(item.getType().equalsIgnoreCase(CodeManager.BackupType.DELETE.getValue())) {
				affect = BackupManager.deleteBackup(backupArgs, platformDb, item.getDeleteMapper());
			} else {
				affect = 0;
			}
			total += affect;
			sb.append(String.format("%s(%d), ", item.getPlatformTableName(), affect));
		}
		if(total > 0) {
			logger.debug("bis backup job(60) : " + sb.toString());
		}
	}
}
