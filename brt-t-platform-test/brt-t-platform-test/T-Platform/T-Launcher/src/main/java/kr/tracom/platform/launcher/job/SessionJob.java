package kr.tracom.platform.launcher.job;

import kr.tracom.platform.service.dao.PlatformDao;
import kr.tracom.platform.service.dao.PlatformMapper;
import kr.tracom.platform.service.manager.ErrorManager;
import kr.tracom.platform.tcp.manager.TcpSessionManager;
import kr.tracom.platform.tcp.model.TcpChannelSession;
import kr.tracom.platform.tcp.model.TcpSession;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SessionJob implements Job {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	public SessionJob() {

	}

	@Override
	public void execute(JobExecutionContext ctx) throws JobExecutionException {
		if(TcpSessionManager.getSize() == 0) {
			return;
		}

		JobDataMap jobDataMap = ctx.getJobDetail().getJobDataMap();
		String platformId = jobDataMap.getString("PLATFORM_ID");
		int period = jobDataMap.getInt("PERIOD");

		PlatformDao platformDao = new PlatformDao();
		Iterator<TcpChannelSession> iterator = TcpSessionManager.getSession();

		List<Map<String, Object>> listParam = new ArrayList<>();
		while(iterator.hasNext()) {
			TcpChannelSession tcpChannelSession = iterator.next();
			TcpSession tcpSession = tcpChannelSession.getSession();
			tcpSession.setElapsedTime(tcpSession.getElapsedTime() + period);

			listParam.add(tcpSession.toMap());
		}

		try {
			platformDao.updateBatch(PlatformMapper.SESSION_UPDATE, listParam, 0);
		} catch(Exception e) {
			ErrorManager.trace(platformId,
					this.getClass().getName(), Thread.currentThread().getStackTrace(),
					"TCP SESSION LIST", e.getMessage());

			logger.error(ErrorManager.getStackTrace(e));
		}

		logger.debug("tcp session update : " + listParam.size());
	}
}
