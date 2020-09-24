package kr.tracom.platform.service.dao;

import org.apache.ibatis.session.SqlSessionFactory;

import kr.tracom.platform.db.base.BaseDAO;
import kr.tracom.platform.db.factory.ServiceDbFactory;


public class ServiceDao extends BaseDAO {
	public ServiceDao() {
		super(ServiceDbFactory.getSqlSessionFactory());
	}
	
	public ServiceDao(SqlSessionFactory sqlSessionFactory) {
        super(sqlSessionFactory);
    }
}
