package kr.tracom.platform.service.manager;

import kr.tracom.platform.db.base.BaseDTO;
import kr.tracom.platform.db.model.Clause;
import kr.tracom.platform.service.dao.PlatformDao;
import kr.tracom.platform.service.dao.ServiceDao;
import kr.tracom.platform.service.model.BackupArgs;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BackupManager {
	
	public static int batchBackup(BackupArgs backupArgs,
			SqlSessionFactory platformSession,
			SqlSessionFactory serviceSession,
			String selectMapper, String insertMapper, String deleteMapper) {

		ServiceDao platformDao = new ServiceDao(platformSession);
		ServiceDao serviceDao = new ServiceDao(serviceSession);

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("BK_GBN", backupArgs.getTimeType());
		paramMap.put("BK_TIME", backupArgs.getTime());
		paramMap.put("BK_NOW", backupArgs.getNowDateTime());

		List<Object> list = platformDao.selectList(selectMapper, paramMap);
		List<Map<String, Object>> paramList = new ArrayList<Map<String,Object>>();
		for(Object obj : list) {
			BaseDTO base = (BaseDTO)obj;
			paramList.add(base.toMap());
		}

		int affect = 0;
		try {
			serviceDao.insertBatch(insertMapper, paramList, backupArgs.getBatchSize());
			affect = platformDao.delete(deleteMapper, paramMap);
		} catch(Exception e) {
			ErrorManager.trace(backupArgs.getServiceId(),
				BackupManager.class.getName(), 	Thread.currentThread().getStackTrace(),
				backupArgs.toString(), e.getMessage());
		}
		
		return affect;
	}

	public static int dbLinkBackup(BackupArgs backupArgs, SqlSessionFactory platformDb,
								   String plf2SvcMapper, String deleteMapper) {
		PlatformDao platformDao = new PlatformDao(platformDb);

		int affect = 0;
		try {
			Map<String, Object> paramMap = backupArgs.toMap();
			if(backupArgs.getSelectWhere() != null && backupArgs.getSelectWhere().length() > 0) {
				String[] tokens = backupArgs.getSelectWhere().split("\\|");

				if (tokens != null && tokens.length == 3) {
					ArrayList<Clause> params1 = new ArrayList<>();
					params1.add(new Clause(tokens[0], tokens[1], tokens[2]));

					paramMap.put("clauseItems", params1);
				}
			}

			affect = platformDao.insert(plf2SvcMapper, paramMap);

			if(deleteMapper.length() > 0) {
				affect = platformDao.delete(deleteMapper, paramMap);
			}
		} catch(Exception e) {
			ErrorManager.trace(backupArgs.getServiceId(),
					BackupManager.class.getName(), 	Thread.currentThread().getStackTrace(),
					backupArgs.toString(), e.getMessage());
		}

		return affect;
	}

	public static int deleteBackup(BackupArgs backupArgs, SqlSessionFactory platformDb, String deleteMapper) {
		PlatformDao platformDao = new PlatformDao(platformDb);

		int affect = 0;
		try {
			affect = platformDao.delete(deleteMapper, backupArgs.toMap());
		} catch(Exception e) {
			ErrorManager.trace(backupArgs.getServiceId(),
					BackupManager.class.getName(), 	Thread.currentThread().getStackTrace(),
					backupArgs.toString(), e.getMessage());
		}
		return affect;
	}
}
