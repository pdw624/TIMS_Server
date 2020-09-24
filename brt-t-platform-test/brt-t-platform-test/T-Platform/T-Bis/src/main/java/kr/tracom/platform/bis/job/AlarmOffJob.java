package kr.tracom.platform.bis.job;

import kr.tracom.platform.bis.TBisLauncher;
import kr.tracom.platform.bis.dao.BisMapper;
import kr.tracom.platform.bis.domain.ItBusLocation;
import kr.tracom.platform.service.dao.PlatformDao;
import org.apache.ibatis.session.SqlSessionFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AlarmOffJob implements Job {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public AlarmOffJob() {
		//10 seconds
	}

	@Override
	public void execute(JobExecutionContext ctx) throws JobExecutionException {
		JobDataMap jobDataMap = ctx.getJobDetail().getJobDataMap();
		TBisLauncher launcher = (TBisLauncher)jobDataMap.get("LAUNCHER");

		SqlSessionFactory platformDb = launcher.getPlatformDb();

		run(platformDb);
	}

	private void run(SqlSessionFactory platformDb) {
		PlatformDao platformDao = new PlatformDao(platformDb);

		List<Object> list = platformDao.selectList(BisMapper.BUS_SELECT_ALARM, null);
		if(list.size() == 0) {
			return;
		}

		ItBusLocation item;
		int affect = 0;
		StringBuilder sb = new StringBuilder();
		for(Object obj : list) {
			item = (ItBusLocation)obj;

			affect += platformDao.update(BisMapper.PR_UPDATE_BUS_ALARM_OFF,
					platformDao.buildMap("ALM_TYPE", "", "ALM_DT", "", "BUS_ID", item.getBusId()));

			sb.append(String.format("%s(%d), ", item.getBusId(), affect));
		}
		if(affect > 0) {
			logger.debug("bis alarm off job(10) : " + sb.toString());
		}
	}
}
