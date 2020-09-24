package kr.tracom.platform.service.manager;

public class CodeManager {
    public static final String ALL_ID = "0000000000";

    public enum RoutingGroupId {
        ALL(0),
        CTR(1),
        BUS(2),
        STN(3);

        private int value;
        RoutingGroupId(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }

    public enum BackupType {
        MOVE("MOVE"),
        DELETE("DELETE");

        private String value;
        BackupType(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }

    public enum LogPrcsType {
        DEL("DEL"),
        ZIP("ZIP"),
        ZAD("ZAD");

        private String value;
        LogPrcsType(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }

    public enum ScheduleCycle {
        TEN_SECONDS(10),
        ONE_MINUTES(60),
        ONE_DAYS(86400);

        private int value;
        ScheduleCycle(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }

    public enum RoutingGroupName {
        CTR("CTR"),
        BUS("BUS"),
        STN("STN");

        private String value;
        RoutingGroupName(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }

    public enum FileTransferType {
        FTP("FTP"),
        TCP("TCP");

        private String value;
        FileTransferType(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }

    public enum FileTransferState {
        FILE_READY("READY"),
        FILE_SENDING("SENDING"),
        FILE_SENT("SENT");

        private String value;
        FileTransferState(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }

    public enum TransactionState {
        SENDING("SENDING"),
        SENT("SENT"),
        RETRY("RETRY"),
        RESPONSE("RESPONSE"),
        OK_FAIL("OK_FAIL"),
        FAIL("FAIL");

        private String value;
        TransactionState(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }

    public enum FtpFile {
        MASTER((byte)1),
        OBE_FW_1((byte)100),
        OBE_FW_2((byte)101),
        OBE_CONFIG((byte)102),
        BIT_FW_1((byte)150),
        BIT_CONFIG((byte)151),
        BIT_FORMFILE((byte)152),
        BIT_SCENARIO((byte)153),
        BIT_MONITOR((byte)154),
        BIT_ILLUMINATION((byte)155);

        private byte value;
        FtpFile(byte value) {
            this.value = value;
        }
        public byte getValue() {
            return value;
        }

        public static boolean isExist(byte item) {
            for (FtpFile ftpFile : FtpFile.values()) {
                if(ftpFile.getValue() == item) {
                    return true;
                }
            }
            return false;
        }
    }

    public enum IotControl {
        OBE_RESET("100"),
        BIT_RESET("150"),
        BIT_MONITOR("151");

        private String value;
        IotControl(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }

    public enum OnOff {
        On((short)1),
        Off((short)2);

        private short value;
        OnOff(short value) {
            this.value = value;
        }
        public short getValue() {
            return value;
        }
    }
}