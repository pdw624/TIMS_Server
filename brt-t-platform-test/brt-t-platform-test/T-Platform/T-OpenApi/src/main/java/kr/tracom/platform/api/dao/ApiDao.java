package kr.tracom.platform.api.dao;

import kr.tracom.platform.db.base.BaseDAO;

public class ApiDao extends BaseDAO {
    public ApiDao() {
        super(ApiDbFactory.getSqlSessionFactory());
    }
}
