/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sam;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.DatabaseMetaData;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 *
 * @author Sanatan Home
 */
public class Sam {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Connection con;
        Statement stmt;
        boolean isRegistered=false;
        try {
            con = DriverManager.getConnection("jdbc:derby://localhost:1527/sam_manager","sam_user","pass_sam");
            stmt = con.createStatement();
            DatabaseMetaData dbm=con.getMetaData();
            ResultSet rs=dbm.getTables(null, null, "SERVICE", null);
            if(rs.next()){
                isRegistered=true;
            }         
        } catch (SQLException e) {
            System.out.println("error_1207:: "+e.getMessage());
        }
        if(isRegistered){
            load_main();
        }else{
            load_setup();
        }
    }
    
    static void load_main(){
        main frame = new main();
        frame.getContentPane().setBackground(Color.white);
        
        
        UIManager.getDefaults().put("Table.alternateRowColor", new Color(240,240,240));
        
        frame.setVisible(true);
        Point point = new Point();
        frame.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                point.x = e.getX();
                point.y = e.getY();
            }
        });
        frame.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point p = frame.getLocation();
                frame.setLocation(p.x + e.getX() - point.x, p.y + e.getY() - point.y);
            }
        });
    }
    
    static void load_setup(){
        setup frame = new setup();
        frame.getContentPane().setBackground(Color.white);
        frame.setVisible(true);
        Point point = new Point();
        
        frame.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                point.x = e.getX();
                point.y = e.getY();
            }
        });
        frame.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point p = frame.getLocation();
                frame.setLocation(p.x + e.getX() - point.x, p.y + e.getY() - point.y);
            }
        });
    }
}