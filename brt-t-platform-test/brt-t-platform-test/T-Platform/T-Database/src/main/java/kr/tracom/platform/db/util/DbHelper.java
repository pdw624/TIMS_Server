package kr.tracom.platform.db.util;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DbHelper {
	private static final String REMARK_STRING = "#";
	private static final String ENDLINE_STRING = ";";

	/*
	public static void addMapper(SqlSessionFactory sqlSessionFactory, String mapperFile) {
		ClassLoader classLoader = DbHelper.class.getClassLoader();	
		InputStream in = classLoader.getResourceAsStream(mapperFile);

		addMapper(sqlSessionFactory, mapperFile, in);
	}
	*/

	private static List<String> listMapper = new ArrayList<>();

	private static boolean checkMapperFile(String mapperFile) {
		for(String mapper : listMapper) {
			if(mapper.equals(mapperFile)) {
				return true;
			}
		}
		return false;
	}

	public static void addMapper(SqlSessionFactory sqlSessionFactory, String mapperFile) {
		if(checkMapperFile(mapperFile)) {
			return;
		}

		InputStream in = null;
		try {
			in = new FileInputStream(mapperFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		addMapper(sqlSessionFactory, mapperFile, in);

		listMapper.add(mapperFile);
	}

	public static void addMapper(SqlSessionFactory sqlSessionFactory, String mapperFile, InputStream in) {
		if (in != null) {
			Configuration configuration = sqlSessionFactory.getConfiguration();
			XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(in, configuration, mapperFile,
					configuration.getSqlFragments());

			xmlMapperBuilder.parse();
		} else {
			System.err.println("Error file not loaded " + mapperFile);
		}
	}

	public static void executeDirectSql(Connection conn, String query) {
		try {
			Statement stmt = conn.createStatement();
			stmt.execute(query);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void executeSql(Connection conn, String resourceFile) {
		executeSql(conn, DbHelper.class.getResourceAsStream(resourceFile));
	}
	
	public static void executeSql(Connection conn, InputStream is) {		
		String allText = readSqlFile(is);
		String[] lines = allText.split(ENDLINE_STRING);
		
		try {
			conn.setAutoCommit(false);
			Statement stmt = conn.createStatement();
			
			for(String sql : lines) {
				/*System.out.println("sql======================start");
				System.out.println(sql);
				System.out.println("sql======================end");
				sql = sql.trim();*/
				
				if(sql=="" || sql==null) {
					continue;
				}else {
					stmt.execute(sql);
				}
				
			}			
			conn.commit();
			
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private static String readSqlFile(InputStream is) {				
		StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                if (sb.length() > 0) {
                    sb.append("\n");
                }
                if(line.trim().isEmpty() || line.startsWith(REMARK_STRING)) {
                	continue;
                }
                
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return sb.toString();
	}
}
