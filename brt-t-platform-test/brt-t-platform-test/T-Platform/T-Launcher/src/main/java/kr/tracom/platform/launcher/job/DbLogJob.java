package kr.tracom.platform.launcher.job;

import kr.tracom.platform.db.factory.PlatformDbFactory;
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
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class DbLogJob implements Job {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final String period = String.valueOf(CodeManager.ScheduleCycle.ONE_DAYS.getValue());

	public DbLogJob() {
			
	}

	public void execute(JobExecutionContext ctx) throws JobExecutionException {
		SqlSessionFactory platformDb = PlatformDbFactory.getSqlSessionFactory();
		run(PlatformConfig.PLF_ID, platformDb);
	}

	public void run(String appId, SqlSessionFactory platformDb) {
		PlatformDao platformDao = new PlatformDao(platformDb);

		List<Object> list = platformDao.selectList(
				PlatformMapper.BACKUP_SELECT,
				platformDao.buildMap("APP_ID", appId, "PERIOD", period));

		BackupArgs backupArgs = new BackupArgs();
		backupArgs.setServiceId(appId);
		backupArgs.setBatchSize(100);
		backupArgs.setTimeType("SECOND");
		backupArgs.setTime("-" + period);
		backupArgs.setNowDateTime(DateTime.now().toString(PlatformConfig.PLF_DT_FORMAT));

		MtBackupTable item;
		for(Object obj : list) {
			item = (MtBackupTable)obj;

			backupArgs.setTableKey(item.getTableKey());
			backupArgs.setServiceTable(item.getServiceTableName().toLowerCase());
			backupArgs.setPlatformTable(item.getPlatformTableName());

			if(item.getType().equalsIgnoreCase(CodeManager.BackupType.DELETE.getValue())) {
				BackupManager.deleteBackup(backupArgs, platformDb, item.getDeleteMapper());

				logger.info("db log job => " + item.toString());
			}
		}
	}
}
