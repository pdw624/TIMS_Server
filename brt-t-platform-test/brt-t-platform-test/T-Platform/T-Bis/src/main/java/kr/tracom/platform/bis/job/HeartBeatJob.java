package kr.tracom.platform.bis.job;

import java.util.Iterator;

import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormat;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.tracom.platform.attribute.AtCode;
import kr.tracom.platform.bis.TBisLauncher;
import kr.tracom.platform.net.config.TimsConfig;
import kr.tracom.platform.net.protocol.TimsMessage;
import kr.tracom.platform.net.protocol.TimsMessageBuilder;
import kr.tracom.platform.service.config.PlatformConfig;
import kr.tracom.platform.service.manager.CodeManager;
import kr.tracom.platform.service.manager.ParameterManager;
import kr.tracom.platform.service.manager.SessionManager;
import kr.tracom.platform.service.manager.TransactionManager;
import kr.tracom.platform.tcp.manager.TcpSessionManager;
import kr.tracom.platform.tcp.model.TcpChannelMessage;
import kr.tracom.platform.tcp.model.TcpChannelSession;
import kr.tracom.platform.tcp.model.TcpSession;

public class HeartBeatJob implements Job {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final String period = "10";

	public HeartBeatJob() {

	}

	@Override
	public void execute(JobExecutionContext ctx) throws JobExecutionException {
		JobDataMap jobDataMap = ctx.getJobDetail().getJobDataMap();
		TBisLauncher launcher = (TBisLauncher)jobDataMap.get("LAUNCHER");

		run(launcher.ServiceId, launcher.getTimsConfig());
	}

	private void run(String appId, TimsConfig timsConfig) {
		Iterator<TcpChannelSession> iterator =
				TcpSessionManager.getTcpChannelSessionByRemoteType(CodeManager.RoutingGroupName.STN.getValue());

		DateTime nowDate = new DateTime();

		final int heartBeatMinSeconds = ParameterManager.getInt(appId, "HEART_BEAT_MIN_SECONDS");
		final int heartBeatMaxSeconds = ParameterManager.getInt(appId, "HEART_BEAT_MAX_SECONDS");
		short attrId;

		attrId = AtCode.STATUS;
		while(iterator.hasNext()) {
			TcpChannelSession tcpChannelSession = iterator.next();
			TcpSession tcpSession = tcpChannelSession.getSession();

			if(!tcpSession.getLastDateTime().equals("-")) {
				DateTime lastDt = DateTimeFormat.forPattern(PlatformConfig.PLF_DT_FORMAT).parseDateTime(tcpSession.getLastDateTime());
				int gapSeconds = Seconds.secondsBetween(lastDt, nowDate).getSeconds();

				//Debug.log(this.getClass().getName(), "gapSeconds : " + gapSeconds);
				if (gapSeconds > heartBeatMinSeconds && gapSeconds <= heartBeatMaxSeconds) {
					getRequest(timsConfig, tcpChannelSession, attrId);
				} else if (gapSeconds > heartBeatMaxSeconds) {
					// 접속후 오랫동안 통신이 없으면 세션을 끊는다.
					SessionManager.forceClose(tcpChannelSession.getChannel(), tcpSession.getSessionId(), "TIME OUT");
				}
			}
		}
	}

	private void getRequest(TimsConfig timsConfig, TcpChannelSession tcpChannelSession, short attrId) {
		TcpSession tcpSession = tcpChannelSession.getSession();

		TimsMessageBuilder builder = new TimsMessageBuilder(timsConfig);
		TimsMessage timsSendMessage = builder.getRequest(attrId);
		TcpChannelMessage tcpChannelMessage = new TcpChannelMessage(tcpChannelSession.getChannel(), tcpSession, timsSendMessage);

		TransactionManager.write(tcpChannelMessage);
	}
}
