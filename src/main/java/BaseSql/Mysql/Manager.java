package BaseSql.Mysql;

import java.sql.*;

public class Manager {
    private  Connection conn = null;
//    private Statement statement;
//    PreparedStatement ps = null;
//    ResultSet rs = null;

    public Manager(String url,String username,String password) throws Exception{
        // ��ȡ����
        
        // ��ȡ���ݿ�����
        conn = DriverManager.getConnection(url, username, password);
//        statement = conn.createStatement();
//        String sql = "select * from user";
        // Ԥִ�в�ѯ
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
//        //ִ�в�ѯ
////        ResultSet rs = ps.executeQuery();
////        // ������ѯ���
////        while (rs.next()) {
////            String name = rs.getString("nickname");// ��ȡ��ѯ�ֶ�nickname��ֵ
////            String loginname = rs.getString("username");
////            String pass  = rs.getString("password");
////            System.out.println("�ǳ�:"+name+" ,��¼��:"+loginname+" ,����:"+pass);
////        }
//    }
}
