package kr.tracom.platform.bis;

import kr.tracom.platform.bis.dao.BisMapper;
import kr.tracom.platform.bis.handler.DataHandler;
import kr.tracom.platform.common.config.AppConfig;
import kr.tracom.platform.common.config.Constants;
import kr.tracom.platform.db.factory.PlatformDbFactory;
import kr.tracom.platform.db.factory.ServiceDbFactory;
import kr.tracom.platform.db.util.DbHelper;
import kr.tracom.platform.master.TFileDB;
import kr.tracom.platform.service.ServiceLauncher;
import kr.tracom.platform.service.dao.PlatformDao;
import kr.tracom.platform.service.dao.PlatformMapper;
import kr.tracom.platform.service.model.DbLinkArgs;
import kr.tracom.platform.service.model.ServiceSetArgs;
import kr.tracom.platform.service.module.TScheduleModule;
import kr.tracom.platform.tcp.model.TcpChannelMessage;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.List;

public class TBisLauncher extends ServiceLauncher {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());	

	private DataHandler dataHandler;
	
	public TBisLauncher() {
		super(32);

		ServiceId = "TA-BIS-01";
		ServiceName = "T-Bis";
		ServiceVersion = "1.0.0";

		this.dataHandler = new DataHandler(this);
	}

	public void initDatabase() {
		Connection conn = getPlatformDb().openSession().getConnection();

		String platformProfile = AppConfig.get("platform.db.profile");
		String serviceProfile = AppConfig.get("service.db.profile");

		DbHelper.addMapper(getPlatformDb(),
				AppConfig.getClasspath(String.format("%s/bis/service-%s.xml", Constants.DB_PATH, platformProfile)));

		DbHelper.addMapper(getServiceDb(),
				AppConfig.getClasspath(String.format("%s/bis/service-%s.xml", Constants.DB_PATH, serviceProfile)));

		if("true".equalsIgnoreCase(AppConfig.get("platform.db.creation"))) {
			DbHelper.executeSql(conn,
					AppConfig.getResources(String.format("%s/bis/service-schema-%s.sql", Constants.DB_PATH, platformProfile)));
			DbHelper.executeSql(conn,
					AppConfig.getResources(String.format("%s/bis/service-data-%s.sql", Constants.DB_PATH, platformProfile)));

			/* 내부 클래스 패스에 있을때
			DbHelper.executeSql(conn, String.format("/resources/db/bis-service-schema-%s.sql", platformProfile));
			DbHelper.executeSql(conn, String.format("/resources/db/bis-service-data-%s.sql", platformProfile));
			*/

			// BIS DB Load
			TFileDB.getInstance().loadBisMaster(ServiceId);

			initBisDB();
		}
	}

	public void connectDbLink() {

		PlatformDao platformDao = new PlatformDao();
		/* DB LINK */
		DbLinkArgs dbLink = new DbLinkArgs();
		dbLink.setSchema(AppConfig.get("service.db.schema"));
		dbLink.setDriver(AppConfig.get("service.db.driver"));
		dbLink.setUrl(AppConfig.get("service.db.url"));
		dbLink.setId(AppConfig.get("service.db.id"));
		dbLink.setPw(AppConfig.get("service.db.pw"));

		try {
			platformDao.update(PlatformMapper.PLF_LINK_SCHEMA, dbLink.toMap());
		} catch(Exception e) {
			logger.error("Connection refused service db!!!");
		}
	}

	public void initSchedule() {
		PlatformDao platformDao = new PlatformDao();
		List<Object> scheduleList = platformDao.selectList(PlatformMapper.SCHEDULE_SELECT,
				platformDao.buildMap("APP_ID", ServiceId));

		TScheduleModule.getInstance().loadServiceJob(scheduleList, this);
	}

	@Override
	public void startup() {
		PlatformId = getPlatformId();

		initDatabase();
		connectDbLink();
		initSchedule();

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

	}

	@Override
	public void logout(String sessionId, String sessionIp) {

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

	public void clearBisDB() {
        /*
        String[] tables = {"BIS_MT_ROUTE_STATION", "BIS_MT_ROUTE", "BIS_MT_LINK_VERTEX", "BIS_MT_NODE",
                "BIS_MT_BIT", "BIS_MT_LINK", "BIS_MT_ALL_INFO", "BIS_MT_ROUTE_LINK", "BIS_MT_ROUTE_NODE",
                "BIS_MT_BUS", "BIS_MT_STATION", "BIS_MT_ROUTE_PLAN" };
        String[] viewes = {"BIS_VW_ROUTE_BUS", "BIS_VW_PRDT_TIME", "BIS_VW_LINK_TIME"};
        */
		PlatformDao platformDao = new PlatformDao();
		List<Object> list = platformDao.selectList(PlatformMapper.DDL_SELECT_TABLES,
				platformDao.buildMap("TB_CATALOG", "TDB", "TB_SCHEMA", "PUBLIC", "TB_PREFIX", "BIS_MT"));

		for(Object obj : list) {
			platformDao.delete(PlatformMapper.DDL_TRUNCATE_TABLES, platformDao.buildMap("TB_NAME", (String)obj));
		}

        /*
        items = masterDao.selectList(PlatformMapper.DDL_SELECT_TABLES,
                masterDao.buildMap("TB_CATALOG", "TDB", "TB_SCHEMA", "PUBLIC", "TB_PREFIX", "BIS_VW"));
        String[] viewes = {"BIS_VW_ROUTE_BUS", "BIS_VW_PRDT_TIME", "BIS_VW_LINK_TIME"};
        for(Object obj : viewes) {
            masterDao.delete(PlatformMapper.DDL_DROP_VIEW, masterDao.buildMap("TB_NAME", (String)obj));
        }
        */
	}

	public void initBisDB() {
		PlatformDao platformDao = new PlatformDao();
		platformDao.update(BisMapper.UPDATE_SESSION, platformDao.buildMap("PLF_ID", PlatformId));

		platformDao.update(BisMapper.MT_CREATE_LINK_TIME, null);
		platformDao.update(BisMapper.MT_CREATE_ROUTE_BUS, null);
		platformDao.update(BisMapper.MT_CREATE_PRDT_TIME, null);
	}
}
