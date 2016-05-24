package sam;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.mxrck.autocompleter.TextAutoCompleter;
import java.sql.DatabaseMetaData;
import org.jdesktop.xswingx.PromptSupport;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Sanatan Home
 */

public class search extends javax.swing.JPanel {

    /**
     * Creates new form search
     */
    
    public search() {
        initComponents();
        initAutoCompleter_jtextField();
        fetch_tables("All","All","All");
        update_options();
        PromptSupport.setPrompt("Leave Blank to get the list of all students", jTextField1);
        jPanel2.setVisible(false);
    }

    void update_options(){
            Connection con; ResultSet rs;
        try {
            con=(Connection)DriverManager.getConnection("jdbc:derby://localhost:1527/sam_manager","sam_user","pass_sam");
            Statement stmt=con.createStatement();
            
            rs=stmt.executeQuery("Select * from SERVICE");
            while(rs.next()){
                String table_name=rs.getString("PACKAGE_NAME");
                String t_year="20"+table_name.substring(table_name.length()-2, table_name.length());
                String t_branch=table_name.substring(0, table_name.length()-5);
                String t_batch=table_name.substring(table_name.length()-5, table_name.length()-2);
                
                boolean t=false;
                for(int i=0;i<jComboBox1.getItemCount();i++){
                    if(jComboBox1.getItemAt(i).equals(t_year)){
                        t=true;
                    }
                }
                if(!t){
                    jComboBox1.addItem(t_year);
                }
                
                t=false;
                for(int i=0;i<jComboBox2.getItemCount();i++){
                    if(jComboBox2.getItemAt(i).equals(t_branch)){
                        t=true;
                    }
                }
                if(!t){
                    jComboBox2.addItem(t_branch);
                }
                
                t=false;
                for(int i=0;i<jComboBox3.getItemCount();i++){
                    if(jComboBox3.getItemAt(i).equals(t_batch)){
                        t=true;
                    }
                }
                if(!t){
                    jComboBox3.addItem(t_batch);
                }
                
            }
            rs.close();
            stmt.close();
            con.close();
        }
        catch(SQLException err){
            System.out.println("Error_packages1::"+err.getMessage());
        }
    }
    
    
    //initializes autocompleter, update it with the names available in database
    private void initAutoCompleter_jtextField(){
        Connection con; ResultSet rs;
        try {
            con=(Connection)DriverManager.getConnection("jdbc:derby://localhost:1527/sam_package","sam_user1","pass_sam");
            Statement stmt=con.createStatement();
            
            TextAutoCompleter complete1=new TextAutoCompleter(jTextField1);
            complete1.removeAllItems();
            DatabaseMetaData dbm = con.getMetaData();
            rs=dbm.getTables(null, null, "%", new String[] {"TABLE"});
            List<String> table_list=new ArrayList<>();
            while(rs.next()){
                String table_name=rs.getString(3);
                table_list.add(table_name);
            }
            for(int i=0;i<table_list.size();i++){
                String table_name=String.valueOf(table_list.get(i));
                String query="select NAME from "+table_name;
                rs = stmt.executeQuery(query);
                while(rs.next()){
                    complete1.addItem(rs.getString("name"));
                }
            }
            rs.close();
            stmt.close();
            con.close();
        }
        catch(SQLException err){
            System.out.println("Error_packages1::"+err.getMessage());
            JOptionPane.showMessageDialog(this, "Something is not right!");
        }
    }
    
    
    /**this function is backbone of the search feature don't modify code block from the second if case situated at line 10(about) without understanding it 
     * it properly
     * @param branch
     * @param batch
     * @param year 
     */
    
    private void fetch_tables(String branch, String batch, String year){
        Connection con; ResultSet rs;
        if(!year.equalsIgnoreCase("All")){
            year=year.substring(year.length()-2,year.length());
            System.out.println(year+"it here");
        }
        try {
            con=(Connection)DriverManager.getConnection("jdbc:derby://localhost:1527/sam_package","sam_user1","pass_sam");
            Statement stmt=con.createStatement();
            
            if(branch.equalsIgnoreCase("All")){
                branch="%";
            }
            if(batch.equalsIgnoreCase("All")){
                if(branch.equals("%")){
                    batch="";
                }else{
                    batch="%";
                }
            }
            if(year.equalsIgnoreCase("All")){
                if(batch.equals("")||batch.equals("%")){
                    year="";
                }else{
                    year="%";
                }
            }
            
            String name_pattern=branch+batch+year;
            
            DatabaseMetaData dbm = con.getMetaData();
            String[] types={"TABLE"};
            rs=dbm.getTables(null, null, name_pattern, types);
            
            jComboBox4.removeAllItems();
            while(rs.next()){
                jComboBox4.addItem(rs.getString(3));
            }
            rs.close();
            stmt.close();
            con.close();
        }
        catch(SQLException err){
            System.out.println("Error_packages1::"+err.getMessage());
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

        jComboBox4 = new javax.swing.JComboBox();
        jComboBox5 = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();
        jLabel19 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jLabel27 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel21 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jComboBox2 = new javax.swing.JComboBox();
        jLabel23 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox();
        jTextField1 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();

        jComboBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox4ActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("Segoe UI Semilight", 1, 16)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(55, 150, 198));
        jLabel2.setText("Name:");

        jLabel3.setFont(new java.awt.Font("Segoe UI Semilight", 1, 16)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(55, 150, 198));
        jLabel3.setText("Roll:");

        jLabel4.setFont(new java.awt.Font("Segoe UI Semilight", 1, 16)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(55, 150, 198));
        jLabel4.setText("Batch:");

        jLabel5.setFont(new java.awt.Font("Segoe UI Semilight", 1, 16)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(55, 150, 198));
        jLabel5.setText("Branch:");

        jLabel6.setFont(new java.awt.Font("Segoe UI Semilight", 1, 16)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(55, 150, 198));
        jLabel6.setText("Email:");

        jLabel7.setFont(new java.awt.Font("Segoe UI Semilight", 1, 16)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(55, 150, 198));
        jLabel7.setText("Contact No.:");

        jLabel8.setFont(new java.awt.Font("Segoe UI Semilight", 1, 16)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(55, 150, 198));
        jLabel8.setText("Parent's Contact No.:");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Segoe UI Semilight", 1, 16)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(140, 140, 140));
        jLabel9.setOpaque(true);

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Segoe UI Semilight", 1, 16)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(140, 140, 140));
        jLabel10.setOpaque(true);

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Segoe UI Semilight", 1, 16)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(140, 140, 140));
        jLabel11.setOpaque(true);

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Segoe UI Semilight", 1, 16)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(140, 140, 140));
        jLabel12.setOpaque(true);

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Segoe UI Semilight", 1, 16)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(140, 140, 140));
        jLabel13.setOpaque(true);

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Segoe UI Semilight", 1, 16)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(140, 140, 140));
        jLabel14.setOpaque(true);

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Segoe UI Semilight", 1, 16)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(140, 140, 140));
        jLabel15.setOpaque(true);

        jLabel16.setFont(new java.awt.Font("Segoe UI Semilight", 1, 16)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(55, 150, 198));
        jLabel16.setText("Year:");

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Segoe UI Semilight", 1, 16)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(140, 140, 140));
        jLabel17.setOpaque(true);

        jLabel18.setFont(new java.awt.Font("Segoe UI Semilight", 1, 16)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(55, 150, 198));
        jLabel18.setText("Message:");

        jScrollPane2.setViewportView(jEditorPane1);

        jLabel19.setBackground(new java.awt.Color(55, 150, 198));
        jLabel19.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("SEND");
        jLabel19.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel19.setOpaque(true);
        jLabel19.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel19MouseClicked(evt);
            }
        });

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Month", "Total days", "Present"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable3.setOpaque(true);
        jTable3.setFillsViewportHeight(true);
        jTable3.setBackground(new Color(255, 255, 255));
        jTable3.getTableHeader().setBackground(new Color(255, 255, 255));
        jScrollPane4.setViewportView(jTable3);

        jLabel27.setFont(new java.awt.Font("Segoe UI Semilight", 1, 16)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(55, 150, 198));
        jLabel27.setText("Attendance Report");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel4)
                            .addComponent(jLabel2)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel16)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(9, 9, 9)
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING))
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel4)
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel16)))
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel5)
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Roll", "Batch", "Branch", "Year", "Know More"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setShowHorizontalLines(false);
        jTable1.setShowVerticalLines(false);
        jTable1.setOpaque(true);
        jTable1.setFillsViewportHeight(true);
        jTable1.setBackground(new Color(255, 255, 255));
        jTable1.getTableHeader().setBackground(new Color(255, 255, 255));
        jScrollPane1.setViewportView(jTable1);
        jTable1.getColumn("Know More").setCellRenderer(new ButtonRenderer());
        jTable1.getColumn("Know More").setCellEditor(new ButtonEditor(new JCheckBox()));

        jLabel21.setFont(new java.awt.Font("Segoe UI Semilight", 1, 16)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(55, 150, 198));
        jLabel21.setText("Similar Names:");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addContainerGap())
                    .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jLabel22.setFont(new java.awt.Font("Segoe UI Semilight", 1, 16)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(55, 150, 198));
        jLabel22.setText("Year:");

        jComboBox1.setFont(new java.awt.Font("Segoe UI Semilight", 0, 18)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All" }));
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });

        jComboBox2.setFont(new java.awt.Font("Segoe UI Semilight", 0, 18)); // NOI18N
        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All" }));
        jComboBox2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox2ItemStateChanged(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("Segoe UI Semilight", 1, 16)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(55, 150, 198));
        jLabel23.setText("Branch:");

        jLabel20.setFont(new java.awt.Font("Segoe UI Semilight", 1, 16)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(55, 150, 198));
        jLabel20.setText("Search Name:");

        jLabel24.setFont(new java.awt.Font("Segoe UI Semilight", 1, 16)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(55, 150, 198));
        jLabel24.setText("Batch:");

        jComboBox3.setFont(new java.awt.Font("Segoe UI Semilight", 0, 18)); // NOI18N
        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All" }));
        jComboBox3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox3ItemStateChanged(evt);
            }
        });

        jTextField1.setFont(new java.awt.Font("Segoe UI Light", 0, 18)); // NOI18N
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sam/images/logo-big-searchoptimization35x35.png"))); // NOI18N
        jLabel1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(110, 110, 110)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)
                        .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)
                        .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 396, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(jLabel1))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22)
                    .addComponent(jLabel23)
                    .addComponent(jLabel24)
                    .addComponent(jLabel20))
                .addGap(8, 8, 8)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)))
        );

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sam/images/Book_Search_338x340.jpg"))); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(291, 291, 291)
                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
                .addGap(31, 31, 31)
                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
        // TODO add your handling code here:
        this.removeAll();
        this.repaint();
        this.add(jPanel4);
        jPanel2.setBounds(0, 85, 1013, 416);
        this.add(jPanel2);
        search_name();
        this.revalidate();
    }//GEN-LAST:event_jLabel1MouseClicked
    
    void search_name(){
        Connection con; ResultSet rs;
        DefaultTableModel model=(DefaultTableModel) jTable1.getModel();
        model.setNumRows(0);
        String search_key=jTextField1.getText();
        
        //set visible the info window
        jPanel2.setVisible(true);
        try {
            con=(Connection)DriverManager.getConnection("jdbc:derby://localhost:1527/sam_package","sam_user1","pass_sam");
            Statement stmt=con.createStatement();
            
            boolean found=false;
            for(int i=0;i<jComboBox4.getItemCount();i++){
                String table_name=String.valueOf(jComboBox4.getItemAt(i));
                rs = stmt.executeQuery("SELECT * FROM "+table_name);
                while(rs.next()){
                    if(rs.getString("name").equalsIgnoreCase(search_key)){
                        jLabel9.setText(rs.getString("name"));
                        jLabel10.setText(rs.getString("roll"));
                        jLabel11.setText(table_name.substring(table_name.length()-5, table_name.length()-2));
                        jLabel12.setText(table_name.substring(0, table_name.length()-5));
                        jLabel13.setText(rs.getString("email"));
                        jLabel14.setText(rs.getString("mob"));
                        jLabel15.setText(rs.getString("par_mob"));
                        jLabel17.setText("20"+table_name.substring(table_name.length()-2, table_name.length()));
                        found=true;
                        find_record_table(table_name,rs.getString("roll"));
                    }
                    
                    String first_name="";
                    if(search_key.indexOf(' ')>=0){
                        first_name=search_key.substring(0, search_key.indexOf(' '));
                    }else{
                        first_name=search_key;
                    }
                    
                    if(rs.getString("name").toLowerCase().contains(first_name.toLowerCase())){
                        String label="SEARCH NOW "+model.getRowCount();
                        model.addRow(new Object[]{  rs.getString("name"),
                                                    rs.getString("roll"),
                                                    table_name.substring(table_name.length()-5, table_name.length()-2),
                                                    table_name.substring(0, table_name.length()-5),
                                                    "20"+table_name.substring(table_name.length()-2, 
                                                    table_name.length()), label});
                    }
                }
            }
            if(!found){
                JOptionPane.showMessageDialog(this, "No match found for the entered name! \nplease make a look on similar names.");
            }
            
            stmt.close();
            con.close();
        }
        catch(SQLException err){
            System.out.println("Error_packages::"+err.getMessage());
            err.getStackTrace();
        }
    }
    
    void search_name1(String trigger){
        Connection con; ResultSet rs;
        DefaultTableModel model=(DefaultTableModel) jTable1.getModel();
        int row_num=Integer.valueOf(trigger.substring(11,trigger.length()));
        
        String roll=String.valueOf(model.getValueAt(row_num, 1));
        String batch=String.valueOf(model.getValueAt(row_num, 2));
        String branch=String.valueOf(model.getValueAt(row_num, 3));
        String year=String.valueOf(model.getValueAt(row_num, 4));
        
        String table_name=branch+batch+year.substring(2, 4);
        
            
        System.out.println(roll);
        
        try {
            con=(Connection)DriverManager.getConnection("jdbc:derby://localhost:1527/sam_package","sam_user1","pass_sam");
            Statement stmt=con.createStatement();
            
            rs = stmt.executeQuery("SELECT * FROM "+table_name);
            while(rs.next()){
                if(rs.getString("roll").equalsIgnoreCase(roll)){
                    jLabel9.setText(rs.getString("name"));
                    jLabel10.setText(rs.getString("roll"));
                    jLabel11.setText(table_name.substring(table_name.length()-5, table_name.length()-2));
                    jLabel12.setText(table_name.substring(0, table_name.length()-5));
                    jLabel13.setText(rs.getString("email"));
                    jLabel14.setText(rs.getString("mob"));
                    jLabel15.setText(rs.getString("par_mob"));
                    jLabel17.setText("20"+table_name.substring(table_name.length()-2, table_name.length()));
                }
            }

            stmt.close();
            con.close();
        }
        catch(SQLException err){
            System.out.println("Error_packages::"+err.getMessage());
            err.getStackTrace();
        }
    }
    
    void find_record_table(String table_key,String student_key){
        Connection con; ResultSet rs;
        DefaultTableModel model=(DefaultTableModel) jTable3.getModel();
        model.setNumRows(0);
        try {
            con=(Connection)DriverManager.getConnection("jdbc:derby://localhost:1527/sam_record","sam_user","pass_sam");
            Statement stmt=con.createStatement();

            DatabaseMetaData dbm = con.getMetaData();
            String[] types={"TABLE"};

            rs=dbm.getTables(null, null, table_key+"%", types);

            java.util.List<String> table_list = new ArrayList<String>();

            while(rs.next()){
                String table=rs.getString(3);
                table_list.add(table);
            }

            for(int k=0;k<table_list.size();k++){
                fetch_record(String.valueOf(table_list.get(k)),student_key);
            }

            rs.close();
            stmt.close();
            con.close();
        }
        catch(SQLException err){
            System.out.println("Error_packages1_fr2::"+err.getMessage());
        } 
    }
    
    void fetch_record(String table, String student_key){
        Connection con;
        DefaultTableModel model=(DefaultTableModel) jTable3.getModel();
        ResultSet rs;
        try {
            con=(Connection)DriverManager.getConnection("jdbc:derby://localhost:1527/sam_record","sam_user","pass_sam");
            Statement stmt=con.createStatement();
            int total_days=0;
            int present_days=0;
            rs=stmt.executeQuery("Select roll,mnth_total_days,mnth_present_days from "+table);
            boolean found=false;
            while(rs.next()){
                if(rs.getString("ROLL").equalsIgnoreCase(student_key)){
                    model.addRow(new Object[]{month(Integer.valueOf(table.substring(table.length()-4,table.length()-2))),rs.getString("mnth_total_days"),rs.getString("mnth_present_days")});
                }
            }
            if(found){
            }   
            rs.close();
            stmt.close();
            con.close();
        }
        catch(SQLException err){
            System.out.println("Error_packages1_fr3::"+err.getMessage());
        }
    }
    
    String month(int i){
        if(i==1){
            return "January";
        }else if(i==2){
            return "February";
        }else if(i==3){
            return "March";
        }else if(i==4){
            return "April";
        }else if(i==5){
            return "May";
        }else if(i==6){
            return "June";
        }else if(i==7){
            return "July";
        }else if(i==8){
            return "August";
        }else if(i==9){
            return "September";
        }else if(i==10){
            return "October";
        }else if(i==11){
            return "November";
        }
        else if(i==12){
            return "December";
        }else{
            return null;
        }
    }
    
    
    
    boolean send_setup(){
        String msg=jEditorPane1.getText();
        String status=new Mailer().sendMail(jLabel13.getText(), msg, "No Subject");
        if(status.equals("done")){
            return true;
        }else{
            JOptionPane.showMessageDialog(this, status);
            return false;
        }
    }
    
    //threads 
    class send_msg extends Thread{
        public void run(){
            if(send_setup()){
                jLabel19.setText("SEND");
                jLabel19.setBackground(new Color(55,150,198));
                jLabel19.setForeground(Color.white);
                jLabel19.setIcon(new ImageIcon());
                sentMsg();
            }else{
                jLabel19.setText("Retry?");
                jLabel19.setBackground(new Color(55,150,198));
                jLabel19.setForeground(Color.white);
                jLabel19.setIcon(new ImageIcon());
            }
        }
    }
    
    void sentMsg(){
        JOptionPane.showMessageDialog(this,"Mail Sent Successfully!\n An extra copy of record has been sent to your email for future references.");
    }
    
    
    
    
    
    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
        this.removeAll();
        this.repaint();
        this.add(jPanel4);
        jPanel2.setBounds(0, 85, 1013, 416);
        this.add(jPanel2);
        search_name();
        this.revalidate();
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jLabel19MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel19MouseClicked
        // TODO add your handling code here:
       if(jEditorPane1.getText().equals("")){ 
                JOptionPane.showMessageDialog(this, "Please write message!");
            }else{
                jLabel19.setText("Sending Mail...");
                jLabel19.setForeground(new Color(55,150,198));
                jLabel19.setBackground(Color.white);
                jLabel19.setIcon(new ImageIcon(getClass().getResource("images/ring-alt.gif")));
                new send_msg().start();
            }
    }//GEN-LAST:event_jLabel19MouseClicked
    
    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        // TODO add your handling code here:
        if(evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            fetch_tables(String.valueOf(jComboBox2.getSelectedItem()),String.valueOf(jComboBox3.getSelectedItem()),String.valueOf(evt.getItem()));
        }
    }//GEN-LAST:event_jComboBox1ItemStateChanged

    private void jComboBox2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox2ItemStateChanged
        // TODO add your handling code here:
        if(evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            fetch_tables(String.valueOf(evt.getItem()),String.valueOf(jComboBox3.getSelectedItem()),String.valueOf(jComboBox1.getSelectedItem()));
        }
    }//GEN-LAST:event_jComboBox2ItemStateChanged

    private void jComboBox3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox3ItemStateChanged
        // TODO add your handling code here:
        if(evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            fetch_tables(String.valueOf(jComboBox2.getSelectedItem()),String.valueOf(evt.getItem()),String.valueOf(jComboBox1.getSelectedItem()));
        }
    }//GEN-LAST:event_jComboBox3ItemStateChanged

    private void jComboBox4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox4ActionPerformed

    class ButtonRenderer extends JButton implements TableCellRenderer {

        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

            setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

            //setFont(new java.awt.Font("Segoe UI Semilight", 1, 10));
            if (isSelected) {
                setBackground(Color.WHITE);
                setForeground(new java.awt.Color(55, 150, 198));
            } else {
                setBackground(Color.WHITE);
                setForeground(new java.awt.Color(55, 150, 198));
            }
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {

        protected JButton button;
        private String label;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            
            button.setBackground(Color.white);
            button.setForeground(Color.red);
            
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            if (isSelected) {
                button.setForeground(table.getSelectionForeground());
                button.setBackground(table.getSelectionBackground());
            } else {
                button.setForeground(table.getForeground());
                button.setBackground(table.getBackground());
            }
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                search_name1(label);
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }

    }




    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JComboBox jComboBox4;
    private javax.swing.JComboBox jComboBox5;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable3;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}

