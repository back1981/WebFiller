package com.yj.webtool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DBReader {
	public Connection createConnection() throws Exception {
		String url = Config.getInstance().getProp("mysql.url");
		return DriverManager.getConnection(url);
	}
	
	public List<Map<String, String>> read() throws Exception {
		Connection con = null;
		try {
		List<Map<String, String>> retval = new LinkedList<Map<String, String>>();
		String query = Config.getInstance().getProp("sql");
		con = createConnection();
		PreparedStatement  stmt = con.prepareStatement(query);
		ResultSet rs=stmt.executeQuery();
		while(rs.next()) {
			Map<String, String> values = new HashMap<String, String>();
			String name = rs.getString("sellername");
			String id = rs.getString("ID");
			String city = rs.getString("city");
			String mobile = rs.getString("mobile");
			String carType = rs.getString("carType");
			values.put("name", name);
			values.put("id", id);
			values.put("city", city);
			values.put("cel_num", mobile);
			values.put("brand", carType);
			retval.add(values);
		}
		return retval;
		} finally {
			try {
				con.close();
			} catch(Exception e) {
				
			}
		}
	}
}
