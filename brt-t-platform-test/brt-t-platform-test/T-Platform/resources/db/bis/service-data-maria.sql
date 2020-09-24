# 백업 스케줄 - 복사/삭제(10)
INSERT INTO PLF_MT_BACKUP(APP_ID, PLF_TB_NAME, SVC_TB_NAME, FIELD_LIST, BK_TYPE, BK_PERIOD, TABLE_KEY, LINK_MAPPER, DEL_MAPPER, SELECT_WHERE, USE_YN) VALUES
    ('TA-BIS-01', 'PLF_HT_TRANSACTION', 'plf_ht_transaction',
    'REG_DT, PLF_ID, SESSION_ID, PACKET_ID, REMOTE_TYPE, PAYLOAD_TYPE, ATTR_COUNT, ATTR_LIST, STR_DATA, RETRY_COUNT, SEND_STATE, SEND_DT, SYS_DT',
    'MOVE', '10', 'PLF_ID, REG_DT, SESSION_ID, PACKET_ID', 'Platform.plfCopy', 'Platform.plfDelete', '', 'Y'),
    ('TA-BIS-01', 'PLF_HT_FILE_TRANSFER', 'plf_ht_file_transfer',
    'SYS_DT, PLF_ID, SESSION_ID, FILE_NAME, FILE_CD, SEND_TYPE, FILE_PATH, FILE_SIZE, FILE_POINTER, SEND_STATE, SEND_ST_DT, SEND_ED_DT',
    'MOVE', '10', 'PLF_ID, SESSION_ID, FILE_NAME', 'Platform.plfCopy', 'Platform.plfDelete', 'SEND_STATE|=|SENT', 'Y');

# 백업 스케줄 - 복사/삭제(60)
INSERT INTO PLF_MT_BACKUP(APP_ID, PLF_TB_NAME, SVC_TB_NAME, FIELD_LIST, BK_TYPE, BK_PERIOD, TABLE_KEY, LINK_MAPPER, DEL_MAPPER, SELECT_WHERE, USE_YN) VALUES
    ('TA-BIS-01', 'BIS_HT_BUS_EVENT', 'bis_ht_eventinfo',
    'BUS_ID, GPS_DT, EVENT_CD, EVENT_DATA, EVENT_SEQ, IMP_ID, ROUTE_ID, LINK_ID, RUN_TYPE, LON, LAT, HEADING, SPEED, STOP_TM, SYS_DT',
    'MOVE', '60', 'GPS_DT, IMP_ID', 'Platform.plfCopy', 'Platform.plfDelete', '', 'Y'),
    ('TA-BIS-01', 'PLF_HT_DEV_CONTROL', 'bis_ht_systemcontrol',
    'DEVICE_ID, SYS_DT, DEVICE_TYPE, CONTROL_CD, CONTROL_VALUE, CONTROL_RESULT',
    'MOVE', '60', 'SYS_DT, DEVICE_ID', 'Platform.plfCopy', 'Platform.plfDelete', '', 'Y'),
    ('TA-BIS-01', 'PLF_HT_DEV_STATUS', 'bis_ht_systemstate',
    'DEVICE_ID, SYS_DT, STATUS_CD, DEVICE_TYPE, STATUS_VALUE',
    'MOVE', '60', 'SYS_DT, DEVICE_ID', 'Platform.plfCopy', 'Platform.plfDelete', '', 'Y');

# OBE / BIT 제공
INSERT INTO PLF_MT_BACKUP(APP_ID, PLF_TB_NAME, SVC_TB_NAME, FIELD_LIST, BK_TYPE, BK_PERIOD, TABLE_KEY, LINK_MAPPER, DEL_MAPPER, SELECT_WHERE, USE_YN) VALUES
    ('TA-BIS-01', 'BIS_HT_BUS_SERVICE', 'bis_ht_servicevehicle',
    'BUS_ID, SYS_DT, PREV_BUS_NAME, NEXT_BUS_NAME, PREV_DIST, NEXT_DIST, PREV_REL_SEQ, NEXT_REL_SEQ, PREV_TIME, NEXT_TIME',
    'MOVE', '60', 'SYS_DT, BUS_ID', 'Platform.plfCopy', 'Platform.plfDelete', '', 'Y'),
    ('TA-BIS-01', 'BIS_HT_BIT_SERVICE', 'bis_ht_servicebit',
    'BIT_ID, BUS_ID, SYS_DT, ROUTE_ID, ROUTE_TYPE, BUS_TYPE, ARRIVAL_TYPE, RUN_TYPE, BUS_STN_SEQ',
    'MOVE', '60', 'SYS_DT, BIT_ID, BUS_ID', 'Platform.plfCopy', 'Platform.plfDelete', '', 'Y');

# 백업 스케줄 - 덮어쓰기
INSERT INTO PLF_MT_BACKUP(APP_ID, PLF_TB_NAME, SVC_TB_NAME, FIELD_LIST, BK_TYPE, BK_PERIOD, TABLE_KEY, LINK_MAPPER, DEL_MAPPER, SELECT_WHERE, USE_YN) VALUES
    ('TA-BIS-01', 'PLF_IT_DEV_VERSION', 'bis_it_systemversion',
    'DEVICE_ID, VERSION_CD, DEVICE_TYPE, VERSION_VALUE',
    'MOVE', '10', 'SYSTEM_ID, VERSION_CODE', 'Platform.plfMerge', '', '', 'Y');

# 스케줄 잡
INSERT INTO PLF_MT_SCHEDULE(APP_ID, JOB_CLASS, JOB_NAME, JOB_ARGS, PERIOD, USE_YN) VALUES
    ('TA-BIS-01', 'kr.tracom.platform.bis.job.BackupJob60', 'BackupJob60', '', '0 0/1 * * * ?', 'Y'),
    ('TA-BIS-01', 'kr.tracom.platform.bis.job.BackupJob10', 'BackupJob10', '', '0/10 * * * * ?', 'Y'),
    ('TA-BIS-01', 'kr.tracom.platform.bis.job.AlarmOffJob', 'AlarmOffJob', '', '0/10 * * * * ?', 'Y'),
    ('TA-BIS-01', 'kr.tracom.platform.bis.job.HeartBeatJob','HeartBeatJob','', '0/10 * * * * ?', 'Y');

INSERT INTO PLF_MT_PARAM(APP_ID, PARAM_KEY, PARAM_VALUE, REMARK, USE_YN) VALUES
    ('TA-BIS-01', 'BUS_LIVE_SECONDS', '-150', 'Bus live time', 'Y'),
    ('TA-BIS-01', 'BUS_DEFAULT_SPEED', '20', 'Bus default speed', 'Y'),
    ('TA-BIS-01', 'BUS_ALARM_SECONDS', '-600', 'Bus alarm keep time', 'Y'),
    ('TA-BIS-01', 'HEART_BEAT_MIN_SECONDS', '60',  'Heartbeat minimum seconds', 'Y'),
    ('TA-BIS-01', 'HEART_BEAT_MAX_SECONDS', '180', 'Heartbeat maximum seconds', 'Y');
