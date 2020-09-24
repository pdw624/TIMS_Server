package kr.tracom.platform.api.dao;

import kr.tracom.platform.common.config.AppConfig;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;
import java.util.Properties;

public class ApiDbFactory {
	private static SqlSessionFactory platformFactory;

	static {
		InputStream reader = AppConfig.getResources("/resources/db/mybatis-config.xml");
		if (reader == null) {
			System.err.println("Error file not loaded mybatis-config.xml");
		}

		Properties properties = new Properties();
		properties.setProperty("db.driver", "org.h2.Driver");
		properties.setProperty("db.url", "jdbc:h2:tcp://127.0.0.1:8084/mem:tdb");
		properties.setProperty("db.id", "tracom");
		properties.setProperty("db.pw", "tracom");

		if (platformFactory == null) {
			platformFactory = new SqlSessionFactoryBuilder().build(reader, properties);
		}
	}

	public static SqlSessionFactory getSqlSessionFactory() {
		return platformFactory;
	}
}
