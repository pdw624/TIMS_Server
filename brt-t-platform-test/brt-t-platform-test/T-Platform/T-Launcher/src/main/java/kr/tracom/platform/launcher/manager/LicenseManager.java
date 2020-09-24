package kr.tracom.platform.launcher.manager;

import kr.tracom.platform.service.model.ServiceArgs;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

public class LicenseManager {
    public static boolean licenseCheck(ServiceArgs serviceArgs) {
        final DateTimeFormatter fmt = org.joda.time.format.DateTimeFormat.forPattern("yyyyMMdd");
        DateTime nowDateTime = DateTime.now();

        //String lockKey = CryptoManager.encode(RuntimeManager.lockString(serviceArgs));
        //String checkKey = serviceArgs.getCheckKey();

        //if(lockKey.equals(checkKey)) {
            DateTime stDate = DateTime.parse(serviceArgs.getLicenseStartDate(), fmt);
            DateTime edDate = DateTime.parse(serviceArgs.getLicenseEndDate(), fmt).plusDays(1);

            if(nowDateTime.getMillis() < stDate.getMillis() || nowDateTime.getMillis() > edDate.getMillis()) {
                return false;
            }
        //} else {
            return false;
        //}

        //return true;
    }
}
