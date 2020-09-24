package kr.tracom.platform.db;

import kr.tracom.platform.common.config.AppConfig;
import kr.tracom.platform.common.config.Constants;
import kr.tracom.platform.common.module.ModuleInterface;
import kr.tracom.platform.db.factory.PlatformDbFactory;
import kr.tracom.platform.db.util.DbHelper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

public class TDatabaseModule implements ModuleInterface {
	private static final Logger logger = LoggerFactory.getLogger(TDatabaseModule.class);

	/* singleton */
	private static class SingletonHolder {
		public static final TDatabaseModule INSTANCE = new TDatabaseModule();
	}

	public static TDatabaseModule getInstance() {
		return SingletonHolder.INSTANCE;
	}
	/* singleton */

	private Server tcpServer = null;
	private Server webServer = null;

	private TDatabaseModule() {}

	@Override
	public void init(Object args) {

	}

	@Override
	public void startup() {
		initialize();
		try {
			String dbPort = AppConfig.get("platform.db.port");

			tcpServer = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", dbPort).start();
			webServer = Server.createWebServer("-webAllowOthers").start();

			String logMsg = "\r\n\r\n";
			logMsg += "===================================================================\r\n";
			logMsg += "DB module startup !!!\r\n";
			logMsg += "===================================================================\r\n";
			logger.info(logMsg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void shutdown() {
		try {
			String query = "SHUTDOWN COMPACT";
			SqlSessionFactory sessionFactory = PlatformDbFactory.getSqlSessionFactory();
			Connection conn = sessionFactory.openSession().getConnection();
			DbHelper.executeDirectSql(conn, query);

			String logMsg = "\r\n\r\n";
			logMsg += "===================================================================\r\n";
			logMsg += "DB module shutdown !!!\r\n";
			logMsg += "===================================================================\r\n";
			logger.info(logMsg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initialize() {
		String dbCreation = AppConfig.get("platform.db.creation");

		if("true".equalsIgnoreCase(dbCreation)) {
			String dbType = AppConfig.get("platform.db.profile");
			// DB 구성
			SqlSessionFactory sessionFactory = PlatformDbFactory.getSqlSessionFactory();
			Connection conn = sessionFactory.openSession().getConnection();

			DbHelper.executeSql(conn,
					AppConfig.getResources(String.format("%s/platform/schema-%s.sql", Constants.DB_PATH, dbType)));

			DbHelper.executeSql(conn,
					AppConfig.getResources(String.format("%s/platform/data-%s.sql", Constants.DB_PATH, dbType)));

			String query = "CREATE ALIAS FN_PARAM_VALUE FOR \"kr.tracom.platform.db.func.StoredProcedures.paramValue\"";
			//0325 주석 , 함수 MARIADB에 추가
			//DbHelper.executeDirectSql(conn, query);
		}
	}

	public static void main(String[] args) {
		AppConfig.read(Constants.PROPERTIES_PATH);
		TDatabaseModule dbServer = new TDatabaseModule();
		dbServer.startup();
	}
}
