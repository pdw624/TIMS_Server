package kr.tracom.platform.db.factory;

import kr.tracom.platform.common.config.AppConfig;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;
import java.util.Properties;

public class PlatformDbFactory {
	private static SqlSessionFactory platformFactory;

	static {
		InputStream reader = AppConfig.getResources("/resources/db/mybatis-config.xml");
		if (reader == null) {
			System.err.println("Error file not loaded mybatis-config.xml");
		}

		Properties properties = new Properties();
		properties.setProperty("db.driver", AppConfig.get("platform.db.driver"));
		properties.setProperty("db.url", AppConfig.get("platform.db.url"));
		properties.setProperty("db.id", AppConfig.get("platform.db.id"));
		properties.setProperty("db.pw", AppConfig.get("platform.db.pw"));

		if (platformFactory == null) {
			platformFactory = new SqlSessionFactoryBuilder().build(reader, properties);
		}
	}

	public static SqlSessionFactory getSqlSessionFactory() {
		return platformFactory;
	}
}
