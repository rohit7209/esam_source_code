/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sam;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DatabaseMetaData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.xswingx.PromptSupport;

/**
 *
 * @author Sanatan Home
 */
public class settings extends javax.swing.JPanel {

    /**
     * Creates new form settings
     */
    public settings() {
        initComponents();
        PromptSupport.setPrompt("Old Password", jPasswordField1);
        PromptSupport.setPrompt("New Password", jPasswordField2);
        PromptSupport.setPrompt("Re-Type Password", jPasswordField3);
        PromptSupport.setFontStyle(18, jPasswordField1);
        PromptSupport.setFontStyle(18, jPasswordField2);
        PromptSupport.setFontStyle(18, jPasswordField3);

        update();
    }

    
    boolean delete(){
        String class_name=String.valueOf(jComboBox12.getSelectedItem());
        List<String> package_names=new ArrayList<>();
        Connection con;
        Statement stmt;
        ResultSet rs;
        try{
            con=(Connection)DriverManager.getConnection("jdbc:derby://localhost:1527/sam_manager","sam_user","pass_sam");
            stmt=con.createStatement();
            
            rs=stmt.executeQuery("Select package_name from service where class_name='"+class_name+"'");
            while(rs.next()){
                package_names.add(rs.getString("PACKAGE_NAME"));
            }
            stmt.executeUpdate("DELETE FROM service WHERE class_name='"+class_name+"'");
            
            con=(Connection)DriverManager.getConnection("jdbc:derby://localhost:1527/sam_package","sam_user1","pass_sam");
            stmt=con.createStatement();
            
            Connection con1=(Connection)DriverManager.getConnection("jdbc:derby://localhost:1527/sam_record","sam_user","pass_sam");
            Statement stmt1=con1.createStatement();
            DatabaseMetaData dbm=con1.getMetaData();
            
            List<String> record_tables=new ArrayList<>();
            for(int i=0;i<package_names.size();i++){
                stmt.executeUpdate("DROP TABLE "+package_names.get(i));
                rs=dbm.getTables(null, null, package_names.get(i)+"%", null);
                while(rs.next()){
                    record_tables.add(rs.getString(3));
                }
            }
            
            for(int i=0;i<record_tables.size();i++){
                stmt1.executeUpdate("DROP TABLE "+record_tables.get(i));
            }
           return true; 
        }catch(SQLException e){
            e.getStackTrace();
            System.out.println("err_1504:: "+e.getMessage());
            JOptionPane.showMessageDialog(this, "There is a problem with your request!\n"+e.getMessage());
            return false;
        }
    }
    
    void update(){
        Connection con; 
        ResultSet rs;
        jComboBox12.removeAllItems();
        try {
            con=(Connection)DriverManager.getConnection("jdbc:derby://localhost:1527/sam_manager","sam_user","pass_sam");
            Statement stmt=con.createStatement();
            rs=stmt.executeQuery("Select class_name from SERVICE");
           
            while(rs.next()){
                boolean found=false;
                for(int i=0;i<jComboBox12.getItemCount();i++){
                    if(jComboBox12.getItemAt(i).equals(rs.getString("CLASS_NAME"))){
                       found=true; 
                    }
                }
                if(!found){
                    jComboBox12.addItem(rs.getString("CLASS_NAME"));
                }
            }
            rs.close();
            stmt.close();
            con.close();
        }
        catch(SQLException err){
            System.out.println("Error_packages1_fr::"+err.getMessage());
        }
    }
    
    void setup_class(){
        List<String> tables=fetch_tables(String.valueOf(jComboBox1.getSelectedItem()).substring(2, 4),String.valueOf(jComboBox6.getSelectedItem()),String.valueOf(jComboBox11.getSelectedItem()));
        try {
            Connection con=(Connection)DriverManager.getConnection("jdbc:derby://localhost:1527/sam_package","sam_user1","pass_sam");
            Statement stmt=con.createStatement();
            DatabaseMetaData dbm=con.getMetaData();
            
            ResultSet rs=dbm.getTables(null, null, "%", new String[]{"TABLE"});
            
            String duplicateTableName="";
            for(int i=0;i<tables.size();i++){
                while(rs.next()){
                    if(rs.getString(3).equals(tables.get(i))){
                        //load_table.remove(tables.get(i));
                        duplicateTableName=tables.get(i);
                    }
                }
            }
            
            boolean done=false;
            if(!duplicateTableName.equals("")){
                con=(Connection)DriverManager.getConnection("jdbc:derby://localhost:1527/sam_manager","sam_user","pass_sam");
                stmt=con.createStatement();
                rs=stmt.executeQuery("SELECT class_name FROM service WHERE package_name='"+duplicateTableName+"'");
                while(rs.next()){
                    duplicateTableName=rs.getString("class_name");
                }
            }
            else{
                //test case print
                System.out.println(tables);
                for(int i=0;i<tables.size();i++){
                    if(load(tables.get(i))){
                        done=true;
                        update();
                        continue;
                    }else{
                        done=false;
                        delete(tables);
                        break;
                    }
                }
            }
            
            if(done){
                con=(Connection)DriverManager.getConnection("jdbc:derby://localhost:1527/sam_manager","sam_user","pass_sam");
                stmt=con.createStatement();
                for(int i=0;i<tables.size();i++){
                    stmt.executeUpdate("INSERT INTO SERVICE VALUES ('"+tables.get(i)+"','"+jTextField5.getText()+"')");
                }
                jLabel7.setText("Add Now");
                jLabel7.setBackground(new Color(55,150,198));
                jLabel7.setForeground(Color.white);
                jLabel7.setIcon(new ImageIcon());
                stalled=true;
                JOptionPane.showMessageDialog(this, "Your request processed successfully!");
            }else{
                jLabel7.setText("Add Now");
                jLabel7.setBackground(new Color(55,150,198));
                jLabel7.setForeground(Color.white);
                jLabel7.setIcon(new ImageIcon());
                stalled=true;
                if(duplicateTableName.equals("")){
                    JOptionPane.showMessageDialog(this, "Sorry, we are unable to process the request!");
                }else{
                    JOptionPane.showMessageDialog(this, "It seems that you are making a duplicate request!\nA class '"+duplicateTableName+"' has similar details.");
                }
            }
            rs.close();
            stmt.close();
            con.close();
        }
        catch(SQLException err){
            jLabel7.setText("Add Now");
            jLabel7.setBackground(new Color(55,150,198));
            jLabel7.setForeground(Color.white);
            jLabel7.setIcon(new ImageIcon());
            System.out.println("Error_packages1206_fr::"+err.getMessage());
        }
    }
    
    //creates a temporary table from webserver 
    boolean load(String table_name){
        boolean k=true;
        try {
            URL url = new URL("http://aecwb.16mb.com/package_handler/get_package.php");
            
            URLConnection con = url.openConnection();
            con.setDoOutput(true);
            
            PrintStream ps = new PrintStream(con.getOutputStream());
            ps.print("table="+table_name);
            
            // we have to get the input stream in order to actually send the request
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = "";
            String res = "";
            while ((line = in.readLine()) != null) {
                res+=line;
            }
            //data is recieved in a stream of strings
            
            if(res.substring(res.length()-6, res.length()).equals("failed")){
                k=false;
            }else{
                int j=0;
                String[] data=new String[5];
                String r_data="";
                DefaultTableModel model=(DefaultTableModel) jTable1.getModel();
                model.setRowCount(0);
                //retrieve the data from string stream and store it in jtable
                for(int i=0;i<res.length();i++){
                    if(res.charAt(i)=='/'){
                        data[j%5]=r_data;
                        if(j%5==4){
                            model.addRow(new Object[]{data[0],data[1],data[2],data[3],data[4]});
                        }
                        r_data="";
                        j++;
                    }else{
                        r_data+=res.charAt(i);
                    }
                }
                create_local_table(model,table_name);
                k=true;
            }
            ps.close();
        } 
        catch (MalformedURLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            connectionMsg();
            return false;
        } 
        catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            connectionMsg();
            return false;
        }
        
        return k;
    }
    
      //creates a local table in application database as per the temporary table
    void create_local_table(DefaultTableModel model, String table_name){
    //create table in local database
        try {
            Connection con; ResultSet rs;
            con=(Connection)DriverManager.getConnection("jdbc:derby://localhost:1527/sam_package","sam_user1","pass_sam");
            Statement stmt=con.createStatement();
            DatabaseMetaData dbm=con.getMetaData();
            rs=dbm.getTables(null, null, table_name, null);
            boolean found=true;
            while(rs.next()){
                found=false;
            }
            if(found){
                stmt.executeUpdate("CREATE TABLE "+table_name+" (ROLL varchar(255) primary key, NAME varchar(255), EMAIL varchar(255), MOB varchar(255), PAR_MOB varchar(255))");
                for(int i=0;i<model.getRowCount();i++){
                    String roll=String.valueOf(model.getValueAt(i, 0));
                    String name=String.valueOf(model.getValueAt(i, 1));
                    String email=String.valueOf(model.getValueAt(i, 2));
                    String mob=String.valueOf(model.getValueAt(i, 3));
                    String par_mob=String.valueOf(model.getValueAt(i, 4));
                    stmt.executeUpdate("INSERT INTO "+table_name+" (roll,name,email,mob,par_mob) VALUES ('"+roll+"','"+name+"','"+email+"','"+mob+"','"+par_mob+"')");
                }
            }
            stmt.close();
            con.close();
        }
        catch(SQLException err){
            System.out.println("Error_packages1205::"+err.getMessage());
        }    
    }
    
    
    void connectionMsg(){
        JOptionPane.showMessageDialog(this,"No Internet Connection!");
    }
    
    void delete(List<String> tables){
        try {
            Connection con;
            con=(Connection)DriverManager.getConnection("jdbc:derby://localhost:1527/sam_package","sam_user1","pass_sam");
            Statement stmt=con.createStatement();
            DatabaseMetaData dbm=con.getMetaData();
            
            
            for(int i=0;i<tables.size();i++){
                ResultSet rs=dbm.getTables(null, null, tables.get(i), null);
                if(rs.next()){
                    stmt.executeUpdate("DROP TABLE "+tables.get(i));
                }
            }
            stmt.close();
            con.close();
        }
        catch(SQLException err){
            System.out.println("Error_packages1206::"+err.getMessage());
        }
    }
    
    List<String> fetch_tables(String year, String branch, String selected_batch){
        String branch_code="B";
        List<String> tables = new ArrayList<>();
        if(selected_batch.equals("Batch 1 & 2")){
            tables.add(branch+branch_code+"11"+year);
            tables.add(branch+branch_code+"12"+year);
        }else if(selected_batch.equals("Batch 3 & 4")){
            tables.add(branch+branch_code+"21"+year);
            tables.add(branch+branch_code+"22"+year);
        }else if(selected_batch.equals("Batch 1")){
            tables.add(branch+branch_code+"11"+year);
        }else if(selected_batch.equals("Batch 2")){
            tables.add(branch+branch_code+"12"+year);
        }else if(selected_batch.equals("Batch 3")){
            tables.add(branch+branch_code+"21"+year);
        }else if(selected_batch.equals("Batch 4")){
            tables.add(branch+branch_code+"22"+year);
        }
        return tables;
    }
    
    boolean send_message1(){
         // TODO add your handling code here:
        try{
            Connection con1=(Connection)DriverManager.getConnection("jdbc:derby://localhost:1527/sam_manager","sam_user","pass_sam");
            Statement stmt=con1.createStatement();

            String name="";
            String email="";
            String msg=jEditorPane1.getText();

            ResultSet rs=stmt.executeQuery("SELECT name,email FROM user_details");
            while(rs.next()){
                name=rs.getString("NAME");
                email=rs.getString("EMAIL");
            }
            
            //
            
            try {
                URL url = new URL("http://sybero.com/aec_esam/mailing/suggestion.php");

                URLConnection con = url.openConnection();
                con.setDoOutput(true);

                PrintStream ps = new PrintStream(con.getOutputStream());
                ps.print("msg="+msg);
                ps.print("&name="+name);
                ps.print("&email="+email);
                ps.print("&source="+"user");

                // we have to get the input stream in order to actually send the request
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String line = "";
                String res = "";
                while ((line = in.readLine()) != null) {
                    res+=line;
                }
                
                ps.close();
                return true;
            } 
            catch (MalformedURLException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
                connectionMsg();
                return false;
            } 
            catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
                connectionMsg();
                return false;
            }            
        }catch(SQLException e){
            e.getStackTrace();
            return false;
        }
    }
    
    void thankMsg(){
        JOptionPane.showMessageDialog(this, "Thanks for your response, \nYour suggestions are always welcome!");
    }
    
    //codes for starting threads 
    class add_new_class extends Thread{
        public void run(){
            setup_class();
            update();
        }
    }
        
    class send_message extends Thread{
        public void run(){
            jLabel12.setText("Sending...");
            jLabel12.setForeground(new Color(55,150,198));
            jLabel12.setBackground(Color.white);
            jLabel12.setIcon(new ImageIcon(getClass().getResource("images/ring-alt.gif")));
            if(send_message1()){
                jLabel12.setText("I have a complain/suggestion");
                jLabel12.setBackground(new Color(55,150,198));
                jLabel12.setForeground(Color.white);
                jLabel12.setIcon(new ImageIcon());
                thankMsg();
            }else{
                jLabel12.setText("Execution Failed! Retry?");
                jLabel12.setBackground(new Color(55,150,198));
                jLabel12.setForeground(Color.white);
                jLabel12.setIcon(new ImageIcon());
            }
        }
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jComboBox6 = new javax.swing.JComboBox();
        jComboBox11 = new javax.swing.JComboBox();
        jLabel34 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jComboBox12 = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        jTextField10 = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jPasswordField1 = new javax.swing.JPasswordField();
        jPasswordField2 = new javax.swing.JPasswordField();
        jPasswordField3 = new javax.swing.JPasswordField();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Roll", "Name", "email", "mob", "par_mob"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable1);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setFont(new java.awt.Font("Segoe UI Semilight", 0, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(55, 150, 198));
        jLabel3.setText("Change password:");

        jLabel6.setBackground(new java.awt.Color(55, 150, 198));
        jLabel6.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Change My Password");
        jLabel6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel6.setOpaque(true);
        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel6MouseClicked(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Segoe UI Semilight", 0, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(55, 150, 198));
        jLabel4.setText("Add New Class:");

        jLabel31.setBackground(new java.awt.Color(255, 255, 255));
        jLabel31.setFont(new java.awt.Font("Segoe UI Semilight", 0, 16)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(55, 150, 198));
        jLabel31.setText("Class NickName:");
        jLabel31.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        jTextField5.setFont(new java.awt.Font("Segoe UI Semilight", 0, 18)); // NOI18N
        jTextField5.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField5FocusLost(evt);
            }
        });

        jComboBox1.setFont(new java.awt.Font("Segoe UI Semilight", 0, 16)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020" }));

        jLabel32.setBackground(new java.awt.Color(255, 255, 255));
        jLabel32.setFont(new java.awt.Font("Segoe UI Semilight", 0, 16)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(55, 150, 198));
        jLabel32.setText("Adm Year:");
        jLabel32.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        jLabel33.setBackground(new java.awt.Color(255, 255, 255));
        jLabel33.setFont(new java.awt.Font("Segoe UI Semilight", 0, 16)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(55, 150, 198));
        jLabel33.setText("Branch:");
        jLabel33.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        jComboBox6.setFont(new java.awt.Font("Segoe UI Semilight", 0, 16)); // NOI18N
        jComboBox6.setMaximumRowCount(10);
        jComboBox6.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "CSE", "IT", "ECE", "EE", "ME", "CIVIL", "AEIE", "MCA", "BCA" }));
        jComboBox6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox6ActionPerformed(evt);
            }
        });

        jComboBox11.setFont(new java.awt.Font("Segoe UI Semilight", 0, 16)); // NOI18N
        jComboBox11.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Batch 1 & 2", "Batch 3 & 4", "Batch 1", "Batch 2", "Batch 3", "Batch 4" }));

        jLabel34.setBackground(new java.awt.Color(255, 255, 255));
        jLabel34.setFont(new java.awt.Font("Segoe UI Semilight", 0, 16)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(55, 150, 198));
        jLabel34.setText("Batch:");
        jLabel34.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        jLabel7.setBackground(new java.awt.Color(55, 150, 198));
        jLabel7.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Add Now");
        jLabel7.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel7.setOpaque(true);
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel7MouseClicked(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI Semilight", 0, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(55, 150, 198));
        jLabel5.setText("Delete Classes:");

        jComboBox12.setFont(new java.awt.Font("Segoe UI Semilight", 0, 16)); // NOI18N
        jComboBox12.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All batches", "Batch 1 & 2", "Batch 3 & 4", "Batch 1", "Batch 2", "Batch 3", "Batch 4" }));

        jLabel8.setBackground(new java.awt.Color(55, 150, 198));
        jLabel8.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Delete");
        jLabel8.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel8.setOpaque(true);
        jLabel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel8MouseClicked(evt);
            }
        });

        jScrollPane1.setViewportView(jEditorPane1);

        jLabel9.setBackground(new java.awt.Color(55, 150, 198));
        jLabel9.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Update My Profile");
        jLabel9.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel9.setOpaque(true);
        jLabel9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel9MouseClicked(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Segoe UI Semilight", 0, 24)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(55, 150, 198));
        jLabel10.setText("Write Us:");

        jLabel11.setFont(new java.awt.Font("Segoe UI Semilight", 0, 24)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(55, 150, 198));
        jLabel11.setText("Update Profile:");

        jTextField4.setFont(new java.awt.Font("Segoe UI Semilight", 0, 17)); // NOI18N
        jTextField4.setToolTipText("leave vacant, if no update required");
        jTextField4.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField4FocusLost(evt);
            }
        });

        jTextField6.setFont(new java.awt.Font("Segoe UI Semilight", 0, 17)); // NOI18N
        jTextField6.setToolTipText("leave vacant, if no update required");
        jTextField6.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField6FocusLost(evt);
            }
        });

        jTextField7.setFont(new java.awt.Font("Segoe UI Semilight", 0, 17)); // NOI18N
        jTextField7.setToolTipText("leave vacant, if no update required");
        jTextField7.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField7FocusLost(evt);
            }
        });

        jTextField10.setFont(new java.awt.Font("Segoe UI Semilight", 0, 17)); // NOI18N
        jTextField10.setToolTipText("leave vacant, if no update required");
        jTextField10.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField10FocusLost(evt);
            }
        });

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Segoe UI Semilight", 0, 14)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(55, 150, 198));
        jLabel20.setText("Name:");
        jLabel20.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Segoe UI Semilight", 0, 14)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(55, 150, 198));
        jLabel24.setText("Designation:");
        jLabel24.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Segoe UI Semilight", 0, 14)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(55, 150, 198));
        jLabel23.setText("Department:");
        jLabel23.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        jLabel35.setBackground(new java.awt.Color(255, 255, 255));
        jLabel35.setFont(new java.awt.Font("Segoe UI Semilight", 0, 14)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(55, 150, 198));
        jLabel35.setText("Email:");
        jLabel35.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        jLabel12.setBackground(new java.awt.Color(55, 150, 198));
        jLabel12.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("I have a complain/suggestion");
        jLabel12.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel12.setOpaque(true);
        jLabel12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel12MouseClicked(evt);
            }
        });

        jLabel36.setBackground(new java.awt.Color(255, 255, 255));
        jLabel36.setFont(new java.awt.Font("Segoe UI Semilight", 0, 16)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(55, 150, 198));
        jLabel36.setText("Select Class:");
        jLabel36.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        jPasswordField1.setFont(new java.awt.Font("Segoe UI Semilight", 0, 14)); // NOI18N
        jPasswordField1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPasswordField1MouseClicked(evt);
            }
        });
        jPasswordField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPasswordField1ActionPerformed(evt);
            }
        });

        jPasswordField2.setFont(new java.awt.Font("Segoe UI Semilight", 0, 14)); // NOI18N
        jPasswordField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPasswordField2ActionPerformed(evt);
            }
        });

        jPasswordField3.setFont(new java.awt.Font("Segoe UI Semilight", 0, 14)); // NOI18N
        jPasswordField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPasswordField3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jComboBox6, 0, 90, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jComboBox11, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(110, 110, 110)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBox12, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)
                            .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel3)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                            .addComponent(jPasswordField3)
                            .addComponent(jPasswordField2)
                            .addComponent(jPasswordField1))
                        .addGap(16, 16, 16)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jTextField4)
                                    .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(16, 16, 16)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jTextField10)
                                            .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel11))))
                .addContainerGap(26, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBox11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 80, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel10))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPasswordField2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPasswordField3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel35)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel23)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLabel24))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel6MouseClicked
        // TODO add your handling code here:
        String old_pass=String.valueOf(jPasswordField1.getPassword());
        String new_pass=String.valueOf(jPasswordField2.getPassword());
        String re_pass=String.valueOf(jPasswordField3.getPassword());
        try {    
            Connection con;
            ResultSet rs;
            con=(Connection)DriverManager.getConnection("jdbc:derby://localhost:1527/sam_manager","sam_user","pass_sam");
            Statement stmt=con.createStatement();
            rs=stmt.executeQuery("SELECT password FROM user_details");
            String pass="";
            while(rs.next()){
                pass=rs.getString("PASSWORD");
            }
            if(old_pass.equals("")||new_pass.equals("")||re_pass.equals("")){
                JOptionPane.showMessageDialog(this, "Plesa fill all the fields!");
                if(old_pass.equals("")){
                    jPasswordField1.requestFocus(true);
                }else if(new_pass.equals("")){
                    jPasswordField2.requestFocus(true);
                }else if(re_pass.equals("")){
                    jPasswordField3.requestFocus(true);
                }
            }else if(!old_pass.equals(pass)){
                JOptionPane.showMessageDialog(this, "The entered current password is incorrect!");
                jPasswordField1.requestFocus(true);
            }else if(!new_pass.equals(re_pass)){
                JOptionPane.showMessageDialog(this, "Password and re-password fields are not matching!");
                jPasswordField3.requestFocus(true);
            }else{
                stmt.executeUpdate("UPDATE user_details SET password = '"+re_pass+"'");
                JOptionPane.showMessageDialog(this, "Your password changed successfully!");
                jPasswordField1.setText("");
                jPasswordField2.setText("");
                jPasswordField3.setText("");
            }
        }catch(SQLException e){
            e.getMessage();
            e.getStackTrace();
            JOptionPane.showMessageDialog(this, "We are unable to process your request!\n"+e.getMessage());
        }
    }//GEN-LAST:event_jLabel6MouseClicked

    private void jTextField5FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField5FocusLost
    
    }//GEN-LAST:event_jTextField5FocusLost

    boolean stalled=true;

    private void jLabel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MouseClicked
        // TODO add your handling code here:
        String class_name=jTextField5.getText();
        int admYear=Integer.valueOf(String.valueOf(jComboBox1.getSelectedItem()));
        if(class_name.equals("") || admYear>Integer.valueOf(new SimpleDateFormat("yyyy").format(new Date()))){
            if(class_name.equals("")){
                JOptionPane.showMessageDialog(this, "Class Nick Name shouldn't be vacant!");
            }else{
                JOptionPane.showMessageDialog(this, "Select proper admission year!");
            }
        }else if(stalled){
            try{
                Connection con=(Connection)DriverManager.getConnection("jdbc:derby://localhost:1527/sam_manager","sam_user","pass_sam");
                Statement stmt=con.createStatement();

                ResultSet rs=stmt.executeQuery("SELECT class_name FROM service");
                boolean found=false;
                while(rs.next()){
                    if(rs.getString("class_name").equals(class_name)){
                        found=true;
                    }
                }
                if(!found){
                    jLabel7.setText("Creating...");
                    jLabel7.setForeground(new Color(55,150,198));
                    jLabel7.setBackground(Color.white);
                    jLabel7.setIcon(new ImageIcon(getClass().getResource("images/ring-alt.gif")));
                    stalled=false;
                    new add_new_class().start();
                }else{
                    JOptionPane.showMessageDialog(this, "This Nick Name exists!\nPlease try another one.");
                }
            }catch(SQLException e){
                e.getStackTrace();
            }
        }
    }//GEN-LAST:event_jLabel7MouseClicked

    private void jLabel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseClicked
        String password="";
        String passwd="";
        Connection con;
        Statement stmt;
        ResultSet rs;   

        JPanel panel = new JPanel();
        JLabel label = new JLabel("Enter password:");
        JPasswordField pass = new JPasswordField(10);
        panel.add(label);
        panel.add(pass);
        String[] options = new String[]{"OK", "Cancel"};
        int option = JOptionPane.showOptionDialog(null, panel, "The title",
                             JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                             null, options, options[1]);
        if(option == 0) // pressing OK button
        {
            password = String.valueOf(pass.getPassword());
            try{
                con=(Connection)DriverManager.getConnection("jdbc:derby://localhost:1527/sam_manager","sam_user","pass_sam");
                stmt=con.createStatement();

                rs=stmt.executeQuery("Select password from user_details");
                while(rs.next()){
                    passwd=rs.getString("PASSWORD");
                }
            }catch(SQLException e){
                e.getStackTrace();
            }

            if(passwd.equals(password)){
                    if(JOptionPane.showOptionDialog(null, "It will delete all your saved data related to this class.","Warning",
                        JOptionPane.INFORMATION_MESSAGE, JOptionPane.INFORMATION_MESSAGE,
                        null, options, options[0])==JOptionPane.YES_OPTION){
                       
                        if(delete()){
                            jComboBox12.removeItem(jComboBox12.getSelectedItem());
                            JOptionPane.showMessageDialog(this, "Deleted successfully!");
                        }
                    }

            }else{
                JOptionPane.showMessageDialog(this, "Password provided by you is not correct!");
            }
        }
    }//GEN-LAST:event_jLabel8MouseClicked

    private void jComboBox6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox6ActionPerformed

    private void jLabel9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel9MouseClicked
        // TODO add your handling code here:
        try{
            Connection con1=(Connection)DriverManager.getConnection("jdbc:derby://localhost:1527/sam_manager","sam_user","pass_sam");
            Statement stmt=con1.createStatement();

            String name=jTextField4.getText();
            String email=jTextField10.getText();
            String dep=jTextField6.getText();
            String des=jTextField7.getText();

            if(!name.equals("")){
                stmt.executeUpdate("UPDATE user_details SET name='"+name+"'");
            }
            if(!email.equals("")){
                stmt.executeUpdate("UPDATE user_details SET email='"+email+"'");
            }
            if(!dep.equals("")){
                stmt.executeUpdate("UPDATE user_details SET department='"+dep+"'");
            }
            if(!des.equals("")){
                stmt.executeUpdate("UPDATE user_details SET designation='"+des+"'");
            }
            jTextField4.setText("");
            jTextField10.setText("");
            jTextField6.setText("");
            jTextField7.setText("");
            JOptionPane.showMessageDialog(this, "Your profile updated successfully!");
        }catch(SQLException e){
            e.getStackTrace();
            System.out.println(e);
            JOptionPane.showMessageDialog(this, "There is a problem with your request!");
        }
    }//GEN-LAST:event_jLabel9MouseClicked

    private void jTextField4FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField4FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField4FocusLost

    private void jTextField6FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField6FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField6FocusLost

    private void jTextField7FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField7FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField7FocusLost

    private void jTextField10FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField10FocusLost

    }//GEN-LAST:event_jTextField10FocusLost

    private void jLabel12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel12MouseClicked
        // TODO add your handling code here:
        new send_message().start();
    }//GEN-LAST:event_jLabel12MouseClicked

    private void jPasswordField1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPasswordField1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jPasswordField1MouseClicked

    private void jPasswordField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPasswordField2ActionPerformed
        // TODO add your handling code here:
        jPasswordField3.requestFocus(true);
    }//GEN-LAST:event_jPasswordField2ActionPerformed

    private void jPasswordField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPasswordField1ActionPerformed
        // TODO add your handling code here:
        jPasswordField2.requestFocus(true);
    }//GEN-LAST:event_jPasswordField1ActionPerformed

    private void jPasswordField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPasswordField3ActionPerformed
        // TODO add your handling code here:
        jLabel6MouseClicked(null); 
    }//GEN-LAST:event_jPasswordField3ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox11;
    private javax.swing.JComboBox jComboBox12;
    private javax.swing.JComboBox jComboBox6;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JPasswordField jPasswordField2;
    private javax.swing.JPasswordField jPasswordField3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    // End of variables declaration//GEN-END:variables
}
