package kr.tracom.platform.imp.dao;

public class ImpMapper {
    public static final String IMP_NAME_SPACE = "Imp.";

    
    public static String SELECT_IMP_SESSION = IMP_NAME_SPACE + "selectImpSession";

    public static String INSERT_IT_SESSION = IMP_NAME_SPACE + "insertItSession";
    public static String INSERT_HT_SESSION = IMP_NAME_SPACE + "insertHtSession";

    public static String SELECT_HT_SESSION_1 = IMP_NAME_SPACE + "selectHtSession1";

    public static String INSERT_IT_VERSION = IMP_NAME_SPACE + "insertItVersion";
    public static String DELETE_IT_VERSION = IMP_NAME_SPACE + "deleteItVersion";
    public static String SELECT_IT_VERSION = IMP_NAME_SPACE + "selectItVersion";
    
    public static String INSERT_CMD_RES = IMP_NAME_SPACE + "insertCMDRes";
    public static String INSERT_CMD_REQ = IMP_NAME_SPACE + "insertCMDReq";
    
    public static String SELECT_CMD_LOG = IMP_NAME_SPACE + "selectCMDLog";
}
