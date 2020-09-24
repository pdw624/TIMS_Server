package kr.tracom.platform.service.module;

import kr.tracom.platform.common.module.ModuleInterface;
import kr.tracom.platform.common.util.ClassUtil;
import kr.tracom.platform.service.domain.MtScheduleJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

public class TScheduleModule implements ModuleInterface {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private Scheduler scheduler;

	/* singleton */
	private static class SingletonHolder {
		public static final TScheduleModule INSTANCE = new TScheduleModule();
	}

	public static TScheduleModule getInstance() {
		return SingletonHolder.INSTANCE;
	}
	/* singleton */

	@Override
	public void init(Object args) {
		try {
			scheduler = new StdSchedulerFactory().getScheduler();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void startup() {
		try {
			scheduler.start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}

		String logMsg = "\r\n\r\n";
		logMsg += "===================================================================\r\n";
		logMsg += "Schedule module startup !!!!!\r\n";
		logMsg += "===================================================================\r\n";
		logger.info(logMsg);
	}

	@Override
	public void shutdown() {
		if(scheduler != null) {
			try {
				scheduler.shutdown(true);
				scheduler = null;
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
		}

		String logMsg = "\r\n\r\n";
		logMsg += "===================================================================\r\n";
		logMsg += "Schedule module shutdown !!!\r\n";
		logMsg += "===================================================================\r\n";
		logger.info(logMsg);
	}

	public void loadPlatformJob(List<Object> scheduleList) {
		Class scheduleClass;
		MtScheduleJob mtScheduleJob;
		for(Object obj : scheduleList) {
			mtScheduleJob = (MtScheduleJob) obj;
			scheduleClass = ClassUtil.getClass(mtScheduleJob.getJobClass());

			addPlatformJob(
					scheduleClass,
					mtScheduleJob.getAppId(),
					mtScheduleJob.getJobName(),
					mtScheduleJob.getPeriod(),
					mtScheduleJob.getArgs());
		}
	}

	public void loadServiceJob(List<Object> scheduleList, Object launcher) {
		Class scheduleClass;
		MtScheduleJob mtScheduleJob;
		for(Object obj : scheduleList) {
			mtScheduleJob = (MtScheduleJob) obj;
			scheduleClass = ClassUtil.getClass(mtScheduleJob.getJobClass());

			addServiceJob(
					scheduleClass,
					mtScheduleJob.getAppId(),
					mtScheduleJob.getJobName(),
					mtScheduleJob.getPeriod(),
					launcher);
		}
	}
	
	public void addJob(String jobClass, String groupName, String jobName, String expression) {
		Class<?> cls;
		try {
			cls = Class.forName(jobClass);
			addJob(cls, groupName, jobName, expression);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void addJob(Class<?> jobClass, String groupName, String jobName, String expression) {
		JobDetail jobDetail = JobBuilder.newJob((Class<? extends Job>) jobClass).withIdentity(jobName, groupName).build();
		Trigger trigger = TriggerBuilder
				.newTrigger()
				.withIdentity(jobName, groupName)
				.withSchedule(
					CronScheduleBuilder.cronSchedule(expression))
				.build();
		try {
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	
	public void addPlatformJob(Class<? extends Job> jobClass, String groupName, String jobName, String expression, String args) {
		JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, groupName).build();
		
		JobDataMap jobDataMap = jobDetail.getJobDataMap();
		jobDataMap.put("PLATFORM_ID", groupName);
		jobDataMap.put("PERIOD", args);
		
		Trigger trigger = TriggerBuilder
				.newTrigger()
				.withIdentity(jobName, groupName)
				.withSchedule(
					CronScheduleBuilder.cronSchedule(expression))
				.build();			
		try {
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	
	public void addServiceJob(Class<? extends Job> jobClass, String groupName, String jobName, String expression, Object launcher) {
		JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, groupName).build();
		
		JobDataMap jobDataMap = jobDetail.getJobDataMap();
		jobDataMap.put("LAUNCHER", launcher);
		
		Trigger trigger = TriggerBuilder
				.newTrigger()
				.withIdentity(jobName, groupName)
				.withSchedule(
					CronScheduleBuilder.cronSchedule(expression))
				.build();			
		try {
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	public void printScheduleList()  {
		try {
			for (String groupName : scheduler.getJobGroupNames()) {
				for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {

					String jobName = jobKey.getName();
					String jobGroup = jobKey.getGroup();

					List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
					Date nextFireTime = triggers.get(0).getNextFireTime();

					logger.info("[jobName] : " + jobName + " [groupName] : "	+ jobGroup + " - " + nextFireTime);
				}
			}
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
}
