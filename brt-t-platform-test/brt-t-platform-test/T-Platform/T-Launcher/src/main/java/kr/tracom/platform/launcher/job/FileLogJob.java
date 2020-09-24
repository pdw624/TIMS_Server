package kr.tracom.platform.launcher.job;

import kr.tracom.platform.common.config.AppConfig;
import kr.tracom.platform.service.dao.PlatformDao;
import kr.tracom.platform.service.dao.PlatformMapper;
import kr.tracom.platform.service.domain.MtLogFile;
import kr.tracom.platform.launcher.manager.FileLogManager;
import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FileLogJob implements Job {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public FileLogJob() {
		// 하루가 지난 로그폴더는 압축 후에 삭제한다.
		// 압축된 파일은 삭제일을 체크하에 삭제한다.
	}

	@Override
	public void execute(JobExecutionContext ctx) throws JobExecutionException {
		PlatformDao platformDao = new PlatformDao();
		List<Object> logList = platformDao.selectList(PlatformMapper.FILE_LOG_SELECT, null);
		MtLogFile logFile;
		String logPath;
		DateTime nowDateTime = DateTime.now().dayOfMonth().roundFloorCopy();

		for(Object obj : logList) {
			logFile = (MtLogFile)obj;
			logPath = AppConfig.getApplicationPath() + logFile.getLogPath();

			FileLogManager.execute(logFile.getPrcsType(), nowDateTime, logPath, logFile.getDeleteDays());

			logger.info("file log job : " + logFile.toString());
		}
	}
}
