INSERT INTO PLF_MT_SERVER(PLF_ID, GRP_ID, SVR_IP, SVR_PORT, PRTL_TYPE, USER_ID, USER_PW) VALUES
    ('TP-2017-01', 1, 'brt.sctc.kr', 21, 'FTP', 'tracom_ctr', 'center12!@'),
    ('TP-2017-01', 2, 'brt.sctc.kr', 21, 'FTP', 'tracom_obe', 'obe34#$'),
    ('TP-2017-01', 3, 'brt.sctc.kr', 21, 'FTP', 'tracom_bit', 'bit56%^'),
    ('TP-2017-01', 4, 'brt.sctc.kr', 2226, 'SFTP', 'brt', '!traCom#452');

INSERT INTO PLF_MT_ADMIN(ADM_ID, ADM_NAME, ADM_LEVEL, PHONE, EMAIL, UPD_DT)
    VALUES('T-ADM-0001', 'SongGJ', 'SUPER', '010-3002-2321', 'gjsong@tracom.kr', '20170801');

INSERT INTO PLF_MT_LAUNCHER(PLF_ID, IP, CITY, LANG_CD, TIME_ZONE, DT_FORMAT, DATE_FORMAT, TIME_FORMAT, LOG_CD, SINCE_DT)
    VALUES('TP-2017-01', 'brttims', 'SEOUL', 'KR', 'ASIA/SEOUL', 'yyyyMMddHHmmss', 'yyyy-MM-dd', 'HH:mm:ss', 'INFO', '20170701');

INSERT INTO PLF_MT_CHANNEL(PLF_ID, CH_NAME, CH_PORT, R_TIMEOUT, W_TIMEOUT, PRTL_VER, HEADER_TYPE, STR_TYPE, ENDIAN_TYPE,
                INDICATOR, MAX_SIZE, MAX_COUNT, APDU_OFFSET, OPT_RC, OPT_TO, OPT_TR, OPT_CE, OPT_RF, OPT_LF)
    VALUES('TP-2017-01', 'BIS_CHANNEL', 8083, 0, 0, 1, 'A', 'EUC-KR', 'little', 65, 65535, 256, 6, 3, 3, 0, 1, 0, 0);

INSERT INTO PLF_MT_SERVICE(SVC_ID, SVC_NAME, VERSION, ATTRIBUTE, LAUNCHER, USE_YN, ST_DT, ED_DT) VALUES
    ('TA-BIS-01', 'T-BIS', '1.0.0', 'kr.tracom.platform.attribute.bis', 'kr.tracom.platform.bis.TBisLauncher', 'N', '20170701', '20191231'),
    ('TA-IMP-01', 'T-IMP', '1.0.0', 'kr.tracom.platform.attribute.imp', 'kr.tracom.platform.imp.TImpLauncher', 'Y', '20170701', '99991231'),
    ('TA-BRT-01', 'T-BRT', '1.0.0', 'kr.tracom.platform.attribute.brt', 'kr.tracom.platform.brt.TBrtLauncher', 'Y', '20200204', '99991231');

INSERT INTO PLF_IT_STATUS(PLF_ID, RUN_DT, ELAPSED_TIME, CPU_USAGE, RAM_USAGE, HDD_USAGE, JVM_USAGE, UPD_DT)
    VALUES('TP-2017-01', DATE_FORMAT(NOW(), 'yyyyMMddHHmmss'), 0, '-', '-', '-', '-', '-');

INSERT INTO PLF_MT_SCHEDULE(APP_ID, JOB_CLASS, JOB_NAME, JOB_ARGS, PERIOD, USE_YN) VALUES
    ('TP-2017-01', 'kr.tracom.platform.launcher.job.TransactionJob', 'TransactionJob', '',		'0/1 * * * * ?', 'Y'),
    ('TP-2017-01', 'kr.tracom.platform.launcher.job.MonitoringJob', 'MonitoringJob', '10',		'0/10 * * * * ?', 'N'),
    ('TP-2017-01', 'kr.tracom.platform.launcher.job.SessionJob', 'SessionJob', '10',			'0/10 * * * * ?', 'Y'),
    ('TP-2017-01', 'kr.tracom.platform.launcher.job.FileLogJob', 'FileLogJob', '',				'0 0 2 * * ?', 'Y'),
    ('TP-2017-01', 'kr.tracom.platform.launcher.job.DbLogJob', 'DbLogJob', '',					'0 0 2 * * ?', 'Y'),
    ('TP-2017-01', 'kr.tracom.platform.launcher.job.LicenseJob', 'LicenseJob', '',				'0 0 0/1 * * ?', 'N'),
    ('TP-2017-01', 'kr.tracom.platform.launcher.job.RestartJob', 'RestartJob', '',				'0 0 3 * * ?', 'N'),
    ('TA-BRT-01', 'kr.tracom.platform.brt.job.NewsJob',	'NewsJob', '',							'0 10 4,16 * * ?', 'Y'),
    ('TA-BRT-01', 'kr.tracom.platform.brt.job.WeatherJob', 'WeatherJob', '',					'0 10 * * * ?', 'Y'),
    ('TA-BRT-01', 'kr.tracom.platform.brt.job.AirQualityJob', 'AirQualityJob', '',				'0 10 * * * ?', 'Y'),
    ('TA-BRT-01', 'kr.tracom.platform.brt.job.DeviceLocationJob', 'DeviceLocationJob', '',		'0/10 * * * * ?', 'Y'),
    ('TA-BRT-01', 'kr.tracom.platform.brt.job.DeviceStatusJob', 'DeviceStatusJob',  '',			'0 0/30 * * * ?', 'Y');

INSERT INTO PLF_MT_BACKUP(APP_ID, PLF_TB_NAME, SVC_TB_NAME, FIELD_LIST, BK_TYPE, BK_PERIOD, TABLE_KEY, LINK_MAPPER, DEL_MAPPER, SELECT_WHERE, USE_YN) VALUES
    ('TP-2017-01', 'PLF_HT_LOG', 'plf_ht_log', '', 'DELETE', '86400', 'APP_ID, PLF_TB_NAME', '', 'Platform.plfDelete', '', 'Y');

# PRCS_TYPE : DEL/ZIP/ZAD(Zip And Del)
INSERT INTO PLF_MT_FILE_LOG(APP_ID, LOG_NAME, LOG_PATH, PRCS_TYPE, DEL_DAYS, USE_YN) VALUES
    ('TP-2017-01', 'Platform Daily Log', '/resources/log', 'ZAD', 7, 'Y'),
    ('TP-2017-01', 'Platform Temp', '/resources/temp', 'DEL', 7, 'Y');

INSERT INTO PLF_MT_PARAM(APP_ID, PARAM_KEY, PARAM_VALUE, REMARK, USE_YN) VALUES
    ('TP-2017-01', 'MAX_USAGE_CPU', '40', 'Threshold cpu usage', 'Y'),
    ('TP-2017-01', 'MAX_USAGE_RAM', '80', 'Threshold ram usage', 'Y'),
    ('TP-2017-01', 'MAX_USAGE_HDD', '80', 'Threshold hdd usage', 'Y'),
    ('TP-2017-01', 'MAX_USAGE_JVM', '80', 'Threshold jvm usage', 'Y'),
    ('TP-2017-01', 'MAX_USAGE_MDB', '80', 'Threshold mdb usage', 'Y');

INSERT INTO PLF_MT_ROUTING(GRP_ID, SYS_ID, SVC_ID, GRP_NAME, SYS_NAME, SVC_NAME) VALUES
    (1,  1,  1, 'CTR', 'CTR0000001', 'TP-2017-01'),
    (2,  1,  1, 'BUS', 'T000000001', 'IMP'),
    (2,  1,  2, 'BUS', 'T000000001', 'DRIVER'),
    (2,  2,  1, 'BUS', 'T000000002', 'IMP'),
    (2,  2,  2, 'BUS', 'T000000002', 'DRIVER'),
    (2,  3,  1, 'BUS', 'T000000003', 'IMP'),
    (2,  3,  2, 'BUS', 'T000000003', 'DRIVER'),
    (2,  4,  1, 'BUS', 'T000000004', 'IMP'),
    (2,  4,  2, 'BUS', 'T000000004', 'DRIVER'),
    (2,  5,  1, 'BUS', 'T000000005', 'IMP'),
    (2,  5,  2, 'BUS', 'T000000005', 'DRIVER'),
    (2,  6,  1, 'BUS', 'T000000006', 'IMP'),
    (2,  6,  2, 'BUS', 'T000000006', 'DRIVER'),
    (2,  7,  1, 'BUS', 'T000000007', 'IMP'),
    (2,  7,  2, 'BUS', 'T000000007', 'DRIVER'),
    (2,  8,  1, 'BUS', 'T000000008', 'IMP'),
    (2,  8,  2, 'BUS', 'T000000008', 'DRIVER'),
    (2,  9,  1, 'BUS', 'T000000009', 'IMP'),
    (2,  9,  2, 'BUS', 'T000000009', 'DRIVER'),
    (2, 10,  1, 'BUS', 'T000000010', 'IMP'),
    (2, 10,  2, 'BUS', 'T000000010', 'DRIVER'),
    (3,  1,  1, 'STN', 'B000000001', 'BIT'),
    (3,  2,  1, 'STN', 'B000000002', 'BIT');
