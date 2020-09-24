package kr.tracom.platform.db.base;

import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseDAO {
    protected SqlSessionFactory sqlSessionFactory = null;    
    
    public BaseDAO() {
        
    }
    
    public BaseDAO(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

	public Map<String, Object> buildMap(String... args) {
    	int length = args.length;
		Map<String, Object> paramMap = new HashMap<>();
		for(int i=0; i<length; i=i+2) {
			paramMap.put(args[i], args[i + 1]);
		}

		return paramMap;
	}
    
	public Object select(String mapperId, Map<String, Object> paramMap) {
		SqlSession session = sqlSessionFactory.openSession();
		Object vo = null;
		try {
			vo = session.selectOne(mapperId, paramMap);
		} finally {
			session.close();
		}
		return vo;
	}
	
	public List<Object> selectList(String mapperId, Map<String, Object> paramMap) {
		SqlSession session = sqlSessionFactory.openSession();
		List<Object> list;
		try {
			list = session.selectList(mapperId, paramMap);
		} finally {
			session.close();
		}
		return list;
	}
	
	public int insert(String mapperId, Map<String, Object> paramMap) {
		int affect;
        SqlSession session = sqlSessionFactory.openSession();
        try {        	
        	affect = session.insert(mapperId, paramMap);
        	session.commit();
        } finally {
            session.close();
        }
        return affect;
	}
	
	public int update(String mapperId, Map<String, Object> paramMap) {
		int affect;
        SqlSession session = sqlSessionFactory.openSession();
        try {        	
        	affect = session.update(mapperId, paramMap);
        	session.commit();
        } finally {
            session.close();
        }
        return affect;
	}
	
	public int delete(String mapperId, Map<String, Object> paramMap) {
		int affect;
        SqlSession session = sqlSessionFactory.openSession();
        try {        	
        	affect = session.delete(mapperId, paramMap);
        	session.commit();
        } finally {
            session.close();
        }
        return affect;
	}
	
	public int insertBatch(String mapperId, List<Map<String, Object>> paramList, int batchSize) {
		int affect = 0;
		SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);

		if(batchSize == 0) {
			batchSize = 100;
		}
        try {
        	for (int i = 0 , size = paramList.size(); i < size ; i++) {
        		sqlSession.insert(mapperId, paramList.get(i));
        		
        		if ((i != 0 && i % batchSize == 0) || i == size -1) {
        			List<BatchResult> batchResults = sqlSession.flushStatements();
        			affect += batchResults.size() ;
                    sqlSession.commit();
                    sqlSession.clearCache();
        		}
        	}
        } finally {
        	sqlSession.close();
        }
        return affect;
	}
	
	public int updateBatch(String mapperId, List<Map<String, Object>> paramList, int batchSize) {
		int affect = 0;
		SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
		
		if(batchSize == 0) {
			batchSize = 20;
		}
		try {
			for (int i = 0, size = paramList.size(); i < size; i++) {
				sqlSession.update(mapperId, paramList.get(i));

				if ((i != 0 && i % batchSize == 0) || i == size - 1) {
					List<BatchResult> batchResults = sqlSession.flushStatements();
					affect += batchResults.size();
					sqlSession.commit();
					sqlSession.clearCache();
				}
			}
		} finally {
			sqlSession.close();
		}
		return affect;
	}
}
