DROP TABLE IF EXISTS IMP_MT_TERMINAL;
DROP TABLE IF EXISTS IMP_HT_SESSION;
DROP TABLE IF EXISTS IMP_IT_VERSION;

CREATE TABLE IMP_MT_TERMINAL (
	IMP_ID VARCHAR(10) NOT NULL PRIMARY KEY,
	IMP_NAME VARCHAR(30),
    IMP_TYPE VARCHAR(10),
    COMP_NAME VARCHAR(10)
);

CREATE TABLE IMP_HT_SESSION (
    REG_DT VARCHAR(16),
	SESSION_ID VARCHAR(10),
	SESSION_IP VARCHAR(20),
	ACTION_TYPE VARCHAR(10),
	PRIMARY KEY(REG_DT, SESSION_ID)
);


CREATE TABLE IMP_IT_VERSION (
    IMP_ID VARCHAR(10),
    PRCS_NAME VARCHAR(50),
    PRCS_INDEX TINYINT,
	VER_HIGH TINYINT,
	VER_MID TINYINT,
	VER_LOW TINYINT,
	START_DATE VARCHAR(16),
	LAST_DATE VARCHAR(16),
    BUILD_DATE VARCHAR(16),
    UPDATE_DATE VARCHAR(16),
	PRIMARY KEY(IMP_ID, PRCS_NAME)
);