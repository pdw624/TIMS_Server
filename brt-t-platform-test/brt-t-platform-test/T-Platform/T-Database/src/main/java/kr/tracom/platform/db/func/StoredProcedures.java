package kr.tracom.platform.db.func;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StoredProcedures {
    public static String paramValue(Connection conn, String paramKey) throws SQLException {
        String sql = "SELECT PARAM_VALUE FROM PLF_MT_PARAM WHERE PARAM_KEY = '" + paramKey + "'";

        ResultSet rs = conn.createStatement().executeQuery(sql);
        if(rs.next()) {
            return rs.getString("PARAM_VALUE");
        } else {
            return "";
        }
    }
}
