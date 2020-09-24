package kr.tracom.platform.imp;

import kr.tracom.platform.common.config.AppConfig;
import kr.tracom.platform.common.config.Constants;
import kr.tracom.platform.db.factory.PlatformDbFactory;
import kr.tracom.platform.db.factory.ServiceDbFactory;
import kr.tracom.platform.db.util.DbHelper;
import kr.tracom.platform.imp.dao.ImpMapper;
import kr.tracom.platform.imp.handler.DataHandler;
import kr.tracom.platform.service.ServiceLauncher;
import kr.tracom.platform.service.config.PlatformConfig;
import kr.tracom.platform.service.dao.PlatformDao;
import kr.tracom.platform.service.model.ServiceSetArgs;
import kr.tracom.platform.tcp.model.TcpChannelMessage;
import org.apache.ibatis.session.SqlSessionFactory;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

public class TImpLauncher extends ServiceLauncher {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private DataHandler dataHandler;

    public TImpLauncher() {
        super(64);

        ServiceId = "TA-IMP-01";
        ServiceName = "T-IMP";
        ServiceVersion = "1.0.0";

        this.dataHandler = new DataHandler(this);
    }

    public void initDatabase() {
        Connection conn = getPlatformDb().openSession().getConnection();

        String platformProfile = AppConfig.get("platform.db.profile");


        DbHelper.addMapper(getPlatformDb(),
                AppConfig.getClasspath(String.format("%s/imp/imp-sql-%s.xml", Constants.DB_PATH, platformProfile)));

        if("true".equalsIgnoreCase(AppConfig.get("platform.db.creation"))) {
            DbHelper.executeSql(conn,
                    AppConfig.getResources(String.format("%s/imp/imp-schema-%s.sql", Constants.DB_PATH, platformProfile)));
            DbHelper.executeSql(conn,
                    AppConfig.getResources(String.format("%s/imp/imp-data-%s.sql", Constants.DB_PATH, platformProfile)));

            initImpDB();
        }
    }

    @Override
    public void startup() {
        PlatformId = getPlatformId();

        initDatabase();

        super.startup();

        String logMsg = "\r\n\r\n";
        logMsg += "===================================================================\r\n";
        logMsg += PlatformId + " - " + ServiceId + " Service Run !!!\r\n";
        logMsg += "===================================================================\r\n";
        logger.info(logMsg);
    }

    @Override
    public void shutdown() {
        super.shutdown();

        String logMsg = "\r\n\r\n";
        logMsg += "===================================================================\r\n";
        logMsg += PlatformId + " - " + ServiceId + " Service Shutdown !!!\r\n";
        logMsg += "===================================================================\r\n";
        logger.info(logMsg);
    }

    @Override
    public void login(String sessionId, String sessionIp) {
        String regDt = DateTime.now().toString(PlatformConfig.PLF_DT_FORMAT);
        String action = "LOGIN";

        PlatformDao dao = new PlatformDao();
        dao.insert(ImpMapper.INSERT_HT_SESSION, dao.buildMap(
                "REG_DT", regDt, "SESSION_ID", sessionId, "SESSION_IP", sessionIp, "ACTION_TYPE", action));
    }

    @Override
    public void logout(String sessionId, String sessionIp) {
        String regDt = DateTime.now().toString(PlatformConfig.PLF_DT_FORMAT);
        String action = "LOGOUT";

        PlatformDao dao = new PlatformDao();
        dao.insert(ImpMapper.INSERT_HT_SESSION, dao.buildMap(
                "REG_DT", regDt, "SESSION_ID", sessionId, "SESSION_IP", sessionIp, "ACTION_TYPE", action));
    }

    @Override
    public void setServiceArgs(ServiceSetArgs args) {
        super.serviceSetArgs = args;
    }

    @Override
    public SqlSessionFactory getPlatformDb() {
        return PlatformDbFactory.getSqlSessionFactory();
    }

    @Override
    public SqlSessionFactory getServiceDb() {
        return ServiceDbFactory.getSqlSessionFactory();
    }

    @Override
    public void serviceWork(TcpChannelMessage tcpChannelMessage) {
        dataHandler.read(tcpChannelMessage);
    }

    public void initImpDB() {
        PlatformDao dao = new PlatformDao();
        dao.insert(ImpMapper.INSERT_IT_SESSION, dao.buildMap("PLF_ID", PlatformId));
    }
}
