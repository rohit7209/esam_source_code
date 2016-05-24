package sam;


import java.util.*;
import java.awt.*;
import java.util.List;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Sanatan Home
 */
public class find_record extends javax.swing.JPanel {

    /**
     * Creates new form find_record
     */
    String student_roll="";
    public find_record(String class_name, String student_key) {
        initComponents();
        student_roll=student_key;
        update(class_name,student_key);
    }
    
    String [] package_names={"","",""};
    
    List<String> imp_table = new ArrayList<String>();
                
    void update(String class_name, String student_key){
        //create table name
        Connection con; 
        ResultSet rs;
        try {
            con=(Connection)DriverManager.getConnection("jdbc:derby://localhost:1527/sam_manager","sam_user","pass_sam");
            Statement stmt=con.createStatement();
            rs=stmt.executeQuery("Select * from SERVICE");
            int i=0;
            while(rs.next()){
                if(rs.getString("CLASS_NAME").equalsIgnoreCase(class_name)){
                    package_names[i]=rs.getString("PACKAGE_NAME");
                    i++;
                }
            }
            rs.close();
            stmt.close();
            con.close();
        }
        catch(SQLException err){
            System.out.println("Error_packages1_fr::"+err.getMessage());
        }
        
        for(int i=0;i<3;i++){
            if(package_names[i].equals("")){
                continue;
            }
            String name_pattern=package_names[i]+"%";
            try {
                con=(Connection)DriverManager.getConnection("jdbc:derby://localhost:1527/sam_record","sam_user","pass_sam");
                Statement stmt=con.createStatement();
                
                DatabaseMetaData dbm = con.getMetaData();
                String[] types={"TABLE"};
                
                rs=dbm.getTables(null, null, name_pattern, types);

                List<String> table_list = new ArrayList<String>();
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
            
            
            
            try {
                con=(Connection)DriverManager.getConnection("jdbc:derby://localhost:1527/sam_package","sam_user1","pass_sam");
                Statement stmt=con.createStatement();
                
                rs=stmt.executeQuery("select * from "+package_names[i]);
                while(rs.next()){
                    if(rs.getString("roll").equals(student_roll)){
                        jTextField1.setText(rs.getString("email"));
                    }
                }
                
                rs.close();
                stmt.close();
                con.close();
            }
            catch(SQLException err){
                System.out.println("Error_packages1_fr2::"+err.getMessage());
            }
            
            
        }
    }
    
    void fetch_record(String table, String student_key){
        Connection con; 
        ResultSet rs;
        try {
            con=(Connection)DriverManager.getConnection("jdbc:derby://localhost:1527/sam_record","sam_user","pass_sam");
            Statement stmt=con.createStatement();
            String total_days="";
            String present_days="";
            rs=stmt.executeQuery("Select * from "+table);
            boolean found=false;
            while(rs.next()){
                if(rs.getString("ROLL").equalsIgnoreCase(student_key)){
                    total_days=rs.getString("MNTH_TOTAL_DAYS");
                    present_days=rs.getString("MNTH_PRESENT_DAYS");
                    
                    imp_table.add(table);
                    jComboBox1.addItem(month(Integer.valueOf(table.substring(table.length()-4,table.length()-2))));
                    found=true;
                }
            }
            if(found){
                DefaultTableModel model=(DefaultTableModel) jTable2.getModel();
                model.addRow(new Object[]{month(Integer.valueOf(table.substring(table.length()-4,table.length()-2))),total_days,present_days});
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
    
    String month_index(String month){
        if(month.equalsIgnoreCase("January")){
            return "01";
        }else if(month.equalsIgnoreCase("February")){
            return "02";
        }else if(month.equalsIgnoreCase("March")){
            return "03";
        }else if(month.equalsIgnoreCase("April")){
            return "04";
        }else if(month.equalsIgnoreCase("May")){
            return "05";
        }else if(month.equalsIgnoreCase("June")){
            return "06";
        }else if(month.equalsIgnoreCase("July")){
            return "07";
        }else if(month.equalsIgnoreCase("August")){
            return "08";
        }else if(month.equalsIgnoreCase("September")){
            return "09";
        }else if(month.equalsIgnoreCase("October")){
            return "10";
        }else if(month.equalsIgnoreCase("November")){
            return "11";
        }else if(month.equalsIgnoreCase("December")){
            return "12";
        }else{
            return "00";
        }
    }
    
    void update_month(String month){
        month=month_index(month);
        System.out.println(imp_table.size());
        for(int i=0;i<imp_table.size();i++){
            if(imp_table.get(i).substring(imp_table.get(i).length()-4, imp_table.get(i).length()-2).equals(month)){
                //do stuff
                Connection con; 
                ResultSet rs;
                String table=imp_table.get(i);
                DefaultTableModel model=(DefaultTableModel) jTable1.getModel();
                    
                try {
                    con=(Connection)DriverManager.getConnection("jdbc:derby://localhost:1527/sam_record","sam_user","pass_sam");
                    Statement stmt=con.createStatement();
                    rs=stmt.executeQuery("Select * from "+table);
                    
                    while(rs.next()){
                        if(rs.getString("roll").equals(student_roll)){
                            model.setNumRows(0);
                            for(int j=3;j<34;j++){
                                if(rs.getString(j).equalsIgnoreCase("p")){
                                    model.addRow(new Object[]{"DAY "+String.valueOf(j-2),"Present"});
                                }
                                else if(rs.getString(j).equalsIgnoreCase("a")){
                                    model.addRow(new Object[]{"DAY "+String.valueOf(j-2),"Absent"});
                                }
                                else if(rs.getString(j).equalsIgnoreCase("n")){
                                    if(month.equals(new SimpleDateFormat("MM").format(new Date()))){
                                        if(j-2>=Integer.valueOf(new SimpleDateFormat("dd").format(new Date()))){
                                            model.addRow(new Object[]{"DAY "+String.valueOf(j-2),"--"});
                                        }else{
                                            model.addRow(new Object[]{"DAY "+String.valueOf(j-2),"NA"});
                                        }
                                    }else{
                                        model.addRow(new Object[]{"DAY "+String.valueOf(j-2),"NA"});
                                    }
                                }
                            }
                        }
                    }
                    alignCenter(jTable1,0,1);
                    rs.close();
                    stmt.close();
                    con.close();
                }
                catch(SQLException err){
                    JOptionPane.showMessageDialog(this, "There is some problem!\n(fr3_1205[find_record])");
                }
            }
        }
    }
    
    private void alignCenter(JTable table, int... column) {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        
        for(int col:column){
            table.getColumnModel().getColumn(col).setCellRenderer(centerRenderer);
        }
    }
    
    boolean send_setup(String month){
        DefaultTableModel model=(DefaultTableModel)jTable2.getModel();
        String msg=jEditorPane1.getText();
        String t_days="";
        String p_days="";
    
        if(jCheckBox1.isSelected()){
            for(int i=0;i<model.getRowCount();i++){
                if(String.valueOf(model.getValueAt(i, 0)).equals(month)){
                    t_days=String.valueOf(model.getValueAt(i, 1));
                    p_days=String.valueOf(model.getValueAt(i, 2));
                }
            }
            
            msg+="\n\nYour Attendance for month '"+month+"':\n"+"Total Days: "+t_days+""
                    + "\nPresent Days: "+p_days+"\n\nRegards,\n";
        
        }else{
            msg+="\n\nRegards,\n";
        }
        
        String status=new Mailer().sendMail(jTextField1.getText(), msg, "No Subject");
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
            String month=String.valueOf(jComboBox1.getSelectedItem());
            if(send_setup(month)){
                jLabel2.setText("SEND MESSAGE");
                jLabel2.setBackground(new Color(55,150,198));
                jLabel2.setForeground(Color.white);
                jLabel2.setIcon(new ImageIcon());
                sentMsg();
            }else{
                jLabel2.setText("Sending failed! retry?");
                jLabel2.setBackground(new Color(55,150,198));
                jLabel2.setForeground(Color.white);
                jLabel2.setIcon(new ImageIcon());
            }
        }
    }
    
    void sentMsg(){
        JOptionPane.showMessageDialog(this,"Mail Sent Successfully.");
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel4 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jComboBox1 = new javax.swing.JComboBox();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();
        jTextField1 = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "DAY", "REMARK"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setRowHeight(25);
        jTable1.setOpaque(true);
        jTable1.setFillsViewportHeight(true);
        jTable1.setBackground(new Color(255, 255, 255));
        jTable1.getTableHeader().setBackground(new Color(255, 255, 255));
        jScrollPane1.setViewportView(jTable1);

        jComboBox1.setFont(new java.awt.Font("Segoe UI Semilight", 0, 18)); // NOI18N
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Month", "Total Days", "Present days"
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
        jTable2.setRowHeight(25);
        jTable2.setOpaque(true);
        jTable2.setFillsViewportHeight(true);
        jTable2.setBackground(new Color(255, 255, 255));
        jTable2.getTableHeader().setBackground(new Color(255, 255, 255));
        jScrollPane3.setViewportView(jTable2);

        jLabel26.setFont(new java.awt.Font("Segoe UI Semilight", 1, 16)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(55, 150, 198));
        jLabel26.setText("Record of selected Month:");

        jLabel27.setFont(new java.awt.Font("Segoe UI Semilight", 1, 16)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(55, 150, 198));
        jLabel27.setText("Total Record:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jComboBox1, 0, 267, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel26, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 325, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(jLabel27))
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jScrollPane2.setViewportView(jEditorPane1);

        jTextField1.setText("email@web.com");

        jLabel24.setFont(new java.awt.Font("Segoe UI Semilight", 1, 16)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(55, 150, 198));
        jLabel24.setText("Email:");

        jLabel2.setBackground(new java.awt.Color(55, 150, 198));
        jLabel2.setFont(new java.awt.Font("Segoe UI Semilight", 0, 18)); // NOI18N
        jLabel2.setForeground(java.awt.Color.white);
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("SEND MESSAGE");
        jLabel2.setOpaque(true);
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel2MouseClicked(evt);
            }
        });

        jLabel25.setFont(new java.awt.Font("Segoe UI Semilight", 1, 16)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(55, 150, 198));
        jLabel25.setText("Message:");

        jCheckBox1.setBackground(new java.awt.Color(255, 255, 255));
        jCheckBox1.setFont(new java.awt.Font("Segoe UI Semilight", 0, 11)); // NOI18N
        jCheckBox1.setSelected(true);
        jCheckBox1.setText("Include Attendance");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField1)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCheckBox1))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(101, 101, 101)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(100, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(59, 59, 59)
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 325, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 345, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
        update_month(String.valueOf(jComboBox1.getSelectedItem()));
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jLabel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseClicked
        // TODO add your handling code here:
            if(jTextField1.getText().equals("")||jEditorPane1.getText().equals("")){ 
                JOptionPane.showMessageDialog(this, "Please fillup all the fields properly!");
            }else{
                jLabel2.setText("Sending Mail...");
                jLabel2.setForeground(new Color(55,150,198));
                jLabel2.setBackground(Color.white);
                jLabel2.setIcon(new ImageIcon(getClass().getResource("images/ring-alt.gif")));
                new send_msg().start();
            }
    }//GEN-LAST:event_jLabel2MouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
