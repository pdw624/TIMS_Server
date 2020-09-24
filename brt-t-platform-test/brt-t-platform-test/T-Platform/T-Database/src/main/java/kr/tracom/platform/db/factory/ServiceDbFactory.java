package kr.tracom.platform.db.factory;

import kr.tracom.platform.common.config.AppConfig;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;
import java.util.Properties;

public class ServiceDbFactory {
	private static SqlSessionFactory serviceFactory;

	static {
		InputStream reader = AppConfig.getResources("/resources/db/mybatis-config.xml");
		if (reader == null) {
			System.err.println("Error file not loaded mybatis-config.xml");
		}

		Properties properties = new Properties();
		properties.setProperty("db.driver", AppConfig.get("service.db.driver"));
		properties.setProperty("db.url", AppConfig.get("service.db.url"));
		properties.setProperty("db.id", AppConfig.get("service.db.id"));
		properties.setProperty("db.pw", AppConfig.get("service.db.pw"));

		if (serviceFactory == null) {
			serviceFactory = new SqlSessionFactoryBuilder().build(reader, properties);
		}
	}

	public static SqlSessionFactory getSqlSessionFactory() {
		return serviceFactory;
	}
}