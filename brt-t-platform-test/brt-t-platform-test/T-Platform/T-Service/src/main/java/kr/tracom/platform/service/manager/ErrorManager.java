package kr.tracom.platform.service.manager;

import kr.tracom.platform.service.config.PlatformConfig;
import kr.tracom.platform.service.dao.PlatformDao;
import kr.tracom.platform.service.domain.HtErrorLog;
import org.joda.time.DateTime;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorManager {
	private static final String MapperId = "Platform.errorLog";

	public static void trace(String appId,
			String className, StackTraceElement[] methodName,
			String paramData, String errorMessage) {
		
		try {
			HtErrorLog errorLog = new HtErrorLog();
			errorLog.setAppId(appId);
			errorLog.setLogDate(DateTime.now().toString(PlatformConfig.PLF_DATE_FORMAT));
			errorLog.setClassName(className);
			errorLog.setMethodName(getMethodName(methodName));
			errorLog.setParameterData(paramData);
			errorLog.setErrorMessage(errorMessage.trim());
			errorLog.setSystemDateTime(DateTime.now().toString(PlatformConfig.PLF_DT_FORMAT));
	
			PlatformDao platformDao = new PlatformDao();
			platformDao.insert(ErrorManager.MapperId, errorLog.toMap());
		} catch(Exception e) {
			System.out.println("Error Log Insert Error!!");
			e.printStackTrace();
		}
	}

	public static String getMethodName(StackTraceElement e[]) {
		boolean doNext = false;
		for (StackTraceElement s : e) {
			if (doNext) {
				return s.getMethodName();
			}
			doNext = s.getMethodName().equals("getStackTrace");
		}
		return "-";
	}

	public static String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
}
