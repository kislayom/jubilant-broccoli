/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goasat.verifier;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author Rishi
 */
public class MacVerifier {

    public boolean macverifier(String mac) {
        boolean flag = true;

        try {
            ConnectionManager cnr = new ConnectionManager();
            Connection con = null;
            con = cnr.getCon();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select id from mac_addresses where mac = '" + mac + "'");
            if (rs.next()) {
                flag = true;
            } else {
                flag = false;
            }


        } catch (Exception exc) {
            System.out.print(exc);
        }
        return flag;
    }
    
    
    public static void main(String[] args){
        MacVerifier mv = new MacVerifier();
        System.out.print(mv.macverifier("E0-CB-4E-D4-3C-3C"));
    }
}
