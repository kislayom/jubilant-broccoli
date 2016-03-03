/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.goasat.verifier;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 *
 * @author root
 */
public class ConnectionManager {
   
    
       public ConnectionManager() {
        try {
            Class.forName("com.mysql.jdbc.Driver");


        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


    public static Connection getCon() throws Exception {
         String url = "jdbc:mysql://192.168.2.197:3306/";
         String dbName = "goasat";
         String driver = "com.mysql.jdbc.Driver";
         String userName = "goasat";
         String password = "0jb1a11a";
        Connection conn= DriverManager.getConnection(url + dbName, userName, password);
       
        return conn;
    }


}
