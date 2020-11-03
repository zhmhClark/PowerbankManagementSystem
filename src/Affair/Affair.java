/**
 * 作者:周明航
 * 2020-08-30
 * 功能:事务基类，为不同事务类型提供基础服务，主要包括数据库连接
 */
package Affair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DriverManager;

public abstract class Affair {
    protected Connection conn;
    protected PreparedStatement pstmt;
    protected ResultSet rt;
    protected boolean run;
    
    //抽象函数实现多态
    public abstract void run(); //不同事务，不同运行
    protected abstract boolean login();//不同角色，不同登录

    public Affair() {//TODO: properties
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rt = null;
        run = true;
    }

    protected Connection connect() {//提供数据库连接服务
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/powerbanksystem?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8";
			String username = "root";
			String password = "ch3ch2oh";
            conn = DriverManager.getConnection(url, username, password);
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void splitLine() {//分割线函数
        System.out.println("\n***********************************************\n");
    }

}