package BaseSql.Mysql;

import java.sql.*;

public class Manager {
    private  Connection conn = null;
//    private Statement statement;
//    PreparedStatement ps = null;
//    ResultSet rs = null;

    public Manager(String url,String username,String password) throws Exception{
        // 获取配置
        
        // 获取数据库链接
        conn = DriverManager.getConnection(url, username, password);
//        statement = conn.createStatement();
//        String sql = "select * from user";
        // 预执行查询
    }
    public void close(){
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public Statement createStatement() throws SQLException {
        return conn.createStatement();
    }
//    public PreparedStatement execute(String sql) throws SQLException{
//        PreparedStatement ps = conn.prepareStatement(sql);
//        return ps;
//        //执行查询
////        ResultSet rs = ps.executeQuery();
////        // 解析查询结果
////        while (rs.next()) {
////            String name = rs.getString("nickname");// 获取查询字段nickname的值
////            String loginname = rs.getString("username");
////            String pass  = rs.getString("password");
////            System.out.println("昵称:"+name+" ,登录名:"+loginname+" ,密码:"+pass);
////        }
//    }
}
