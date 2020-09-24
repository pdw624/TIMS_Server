package kr.tracom.platform.service.dao;

public class PlatformMapper {
	public static final String PLATFORM_NAME_SPACE = "Platform.";

	public static String PLF_LINK_SCHEMA = PLATFORM_NAME_SPACE + "plfLinkSchema";
	public static String PLF_COPY = PLATFORM_NAME_SPACE + "plfCopy";
	public static String PLF_MERGE = PLATFORM_NAME_SPACE + "plfMerge";
	public static String PLF_DELETE = PLATFORM_NAME_SPACE + "plfDelete";

	public static String SERVER_INFO = PLATFORM_NAME_SPACE + "selectServer";

	public static String LAUNCHER_CONFIG = PLATFORM_NAME_SPACE + "selectLauncherConfig";
	public static String CHANNEL_CONFIG = PLATFORM_NAME_SPACE + "selectChannelConfig";
	public static String SERVICE_CONFIG = PLATFORM_NAME_SPACE + "selectServiceApp";
	
	public static String DB_STATUS = PLATFORM_NAME_SPACE + "dbStatus";

	public static String DDL_SELECT_TABLES = PLATFORM_NAME_SPACE + "selectTables";
	public static String DDL_TRUNCATE_TABLES = PLATFORM_NAME_SPACE + "truncateTable";
	public static String DDL_DROP_VIEW = PLATFORM_NAME_SPACE + "dropView";

	public static String PARAMETER_GETVALUE = PLATFORM_NAME_SPACE + "getParameter";
	public static String PARAMETER_SELECT = PLATFORM_NAME_SPACE + "selectParameter";
	
	public static String SESSION_SELECT = PLATFORM_NAME_SPACE + "selectSession";
	public static String SESSION_LIST = PLATFORM_NAME_SPACE + "selectListSession";
	public static String SESSION_LOGIN = PLATFORM_NAME_SPACE + "updateSessionIn";
	public static String SESSION_LOGOUT = PLATFORM_NAME_SPACE + "updateSessionOut";
	public static String SESSION_UPDATE = PLATFORM_NAME_SPACE + "updateSession";

	public static String STATUS_SELECT = PLATFORM_NAME_SPACE + "selectStatus";
	public static String STATUS_UPDATE = PLATFORM_NAME_SPACE + "updateStatus";
	public static String ADMIN_SELECT = PLATFORM_NAME_SPACE + "selectAdmin";

	public static String FILE_LOG_SELECT = PLATFORM_NAME_SPACE + "selectFileLog";

	public static String BACKUP_SELECT = PLATFORM_NAME_SPACE + "selectBackup";

	public static String SCHEDULE_SELECT = PLATFORM_NAME_SPACE + "selectSchedule";
	public static String SCHEDULE_LIST = PLATFORM_NAME_SPACE + "selectAllSchedule";

	public static String TRANSACTION_SELECT = PLATFORM_NAME_SPACE + "selectTransaction";
	public static String TRANSACTION_INSERT = PLATFORM_NAME_SPACE + "insertTransaction";
	public static String TRANSACTION_UPDATE = PLATFORM_NAME_SPACE + "updateTransaction";
	public static String TRANSACTION_DELETE = PLATFORM_NAME_SPACE + "deleteTransaction";

	public static String FTP_CONFIG = PLATFORM_NAME_SPACE + "selectFtpConfig";

	public static String ROUTING_GROUP_ID = PLATFORM_NAME_SPACE + "selectRoutingGroup";
	public static String ROUTING_SELECT_ID = PLATFORM_NAME_SPACE + "selectRoutingById";
	public static String ROUTING_SYSTEM_LIST = PLATFORM_NAME_SPACE + "selectRoutingSystem";

	public static String FILE_TRANSFER_SELECT = PLATFORM_NAME_SPACE + "selectFileTransfer";
	public static String FILE_TRANSFER_LIST = PLATFORM_NAME_SPACE + "selectListFileTransfer";
	public static String FILE_TRANSFER_INSERT = PLATFORM_NAME_SPACE + "insertFileRegister";
	public static String FILE_TRANSFER_SENDING = PLATFORM_NAME_SPACE + "updateFileSending";
	public static String FILE_TRANSFER_SENT = PLATFORM_NAME_SPACE + "updateFileSent";
	public static String FILE_TRANSFER_CANCEL = PLATFORM_NAME_SPACE + "cancelFileTransfer";


	public static String DEVICE_VERSION_UPDATE = PLATFORM_NAME_SPACE + "updateDeviceVersion";
	public static String DEVICE_STATUS_INSERT = PLATFORM_NAME_SPACE + "insertDeviceStatus";
	public static String DEVICE_CONTROL_INSERT = PLATFORM_NAME_SPACE + "insertDeviceControl";
	
	/*191015 JH*/
	public static String SELECT_IMP_ID = PLATFORM_NAME_SPACE + "selectImpId";
}
