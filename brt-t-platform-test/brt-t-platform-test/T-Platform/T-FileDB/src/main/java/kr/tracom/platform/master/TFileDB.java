package kr.tracom.platform.master;

import kr.tracom.platform.common.config.AppConfig;
import kr.tracom.platform.common.config.Constants;
import kr.tracom.platform.db.TDatabaseModule;
import kr.tracom.platform.db.factory.PlatformDbFactory;
import kr.tracom.platform.db.factory.ServiceDbFactory;
import kr.tracom.platform.db.util.DbHelper;
import kr.tracom.platform.master.bis.BisBuilder;
import kr.tracom.platform.master.bis.BisLoader;
import kr.tracom.platform.master.bis.bit.BitBuilder;
import kr.tracom.platform.service.model.json.JsonData;
import org.apache.ibatis.session.SqlSessionFactory;
import org.joda.time.DateTime;

public class TFileDB {
    public static final String SVC_BIS_ID = "TA-BIS-01";
    public static final String SVC_BIS_VER = "v01";

    private static final String APPLY_DT_PATTERN = "yyyyMMddHHmm";
    private static final String MASTER_ALL = "CG,CD,BS,BT,RT,ST,LK,ND,RL,RS,RN,LV";

    public static void main(String[] args) {
        AppConfig.read(Constants.PROPERTIES_PATH);

        SqlSessionFactory platformDb = PlatformDbFactory.getSqlSessionFactory();
        SqlSessionFactory serviceDb = ServiceDbFactory.getSqlSessionFactory();
        String applyDateTime = DateTime.now().toString(APPLY_DT_PATTERN);

        String exeType = "LOADER"; // BUILD : LOADER

        if("BUILD".equalsIgnoreCase(exeType)) {
            BisBuilder builder = new BisBuilder(SVC_BIS_ID, SVC_BIS_VER, applyDateTime, serviceDb);

            builder.execute(MASTER_ALL);
        } else {
            TDatabaseModule.getInstance().startup();

            DbHelper.addMapper(platformDb,
                    AppConfig.getClasspath(String.format("%s/platform-h2.xml", Constants.DB_PATH)));

            DbHelper.executeSql(platformDb.openSession().getConnection(),
                    AppConfig.getResources(String.format("%s/bis/service-schema-h2.sql", Constants.DB_PATH)));

            BisLoader loader = new BisLoader(SVC_BIS_ID, SVC_BIS_VER, platformDb);
            loader.execute(MASTER_ALL);
        }
    }

    /* singleton */
    private static class SingletonHolder {
        public static final TFileDB INSTANCE = new TFileDB();
    }

    public static TFileDB getInstance() {
        return SingletonHolder.INSTANCE;
    }
	/* singleton */


	public String buildBisMaster(String serviceId, String applyDateTime) {
	    if(applyDateTime == null || applyDateTime.length() != APPLY_DT_PATTERN.length()) {
            applyDateTime = DateTime.now().toString(APPLY_DT_PATTERN);
        }

        SqlSessionFactory serviceDb = ServiceDbFactory.getSqlSessionFactory();

        BisBuilder builder = new BisBuilder(serviceId, SVC_BIS_VER, applyDateTime, serviceDb);

        return builder.execute(MASTER_ALL);
    }

    public String loadBisMaster(String serviceId) {
        SqlSessionFactory platformDb = PlatformDbFactory.getSqlSessionFactory();
        BisLoader loader = new BisLoader(serviceId, SVC_BIS_VER, platformDb);

        return loader.execute(MASTER_ALL);
    }

    public String buildBisBit(String serviceId, JsonData jsonData) {
        String applyDateTime = jsonData.getApplyDateTime();
        if(applyDateTime == null || applyDateTime.length() != APPLY_DT_PATTERN.length()) {
            applyDateTime = DateTime.now().toString(APPLY_DT_PATTERN);
        }
        SqlSessionFactory serviceDb = ServiceDbFactory.getSqlSessionFactory();
        BitBuilder builder = new BitBuilder(serviceId, applyDateTime, serviceDb);

        return builder.execute(jsonData);
    }
}
