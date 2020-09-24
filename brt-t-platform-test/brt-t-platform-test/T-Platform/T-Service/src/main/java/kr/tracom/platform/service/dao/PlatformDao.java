package kr.tracom.platform.service.dao;

import kr.tracom.platform.db.base.BaseDAO;
import kr.tracom.platform.db.factory.PlatformDbFactory;
import org.apache.ibatis.session.SqlSessionFactory;

public class PlatformDao extends BaseDAO 	{
	public PlatformDao() {
        super(PlatformDbFactory.getSqlSessionFactory());
    }
    public PlatformDao(SqlSessionFactory sqlSessionFactory) {
        super(sqlSessionFactory);
    }
}
