/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sam;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import javax.swing.filechooser.FileSystemView;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.PageSize;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.util.Date;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sanatan Home
 */
public class forwarding extends javax.swing.JPanel {

    /**
     * Creates new form forwarding
     */
        List<String> table_list=new ArrayList<>();
    public forwarding() {
        initComponents();
        
        DefaultTableModel model=(DefaultTableModel)jTable2.getModel();
        for(int i=0;i<model.getRowCount();i++){
            model.setValueAt(false, i, 4);
        }
    }

    //function is acting as button event listener function for the bulk messaging button
    
    void bulk_setup(String class_name, String month){
        DefaultTableModel model=(DefaultTableModel)jTable2.getModel();
        String msg=jEditorPane1.getText();
        String info_packet="";
        String from="";
        String f_name="";
        int include=0;
        String subject=jTextField2.getText();
        List<String> rolls=new ArrayList<>();
        List<String> names=new ArrayList<>();
        List<String> total_days=new ArrayList<>();
        List<String> present_days=new ArrayList<>();
        List<String> emails=new ArrayList<>();
        //important:: msg;infopacket;name(name of user);email(from);no. of mails to send in bulk(j);
        int j=0;
        
        //retrieveing the list of selected students... key is roll
        for(int i=0;i<model.getRowCount();i++){
            if(model.getValueAt(i, 4).equals(true)){
                j++;
                rolls.add(String.valueOf(model.getValueAt(i, 0)));
                names.add(String.valueOf(model.getValueAt(i, 1)));
                total_days.add(String.valueOf(model.getValueAt(i, 2)));
                present_days.add(String.valueOf(model.getValueAt(i, 3)));
            }
        }
        
        //test print
        System.out.println("rolls: "+rolls);
        
        Connection con; 
        ResultSet rs;
        try {
            con=(Connection)DriverManager.getConnection("jdbc:derby://localhost:1527/sam_manager","sam_user","pass_sam");
            Statement stmt=con.createStatement();
            //package name is collected from service table
            rs=stmt.executeQuery("select PACKAGE_NAME from SERVICE where CLASS_NAME='"+class_name+"'");
            List<String> packages=new ArrayList<>();
            while(rs.next()){
                packages.add(rs.getString("PACKAGE_NAME"));
            }
            //details of user is collected from user_details table
            rs=stmt.executeQuery("select name,email from user_details");
            while(rs.next()){
                from=rs.getString("EMAIL");
                f_name=rs.getString("NAME");
            }
            
            //roll of students is searched in package tables and email is collected in 'emails' list 
            con=(Connection)DriverManager.getConnection("jdbc:derby://localhost:1527/sam_package","sam_user1","pass_sam");
            stmt=con.createStatement();
            for(int i=0;i<rolls.size();i++){
                String roll=rolls.get(i);
                for(int k=0;k<packages.size();k++){
                    String package_name=packages.get(k);
                    rs=stmt.executeQuery("select email from "+package_name+" where roll='"+roll+"'");
                    while(rs.next()){
                        emails.add(rs.getString("email"));
                    }
                }
            }
            
            //only for test
            System.out.println(rolls);
            System.out.println(emails);
            
            System.out.println(names);
            System.out.println(present_days);
            
            rs.close();
            stmt.close();
            con.close();
        }
        catch(SQLException err){
            System.out.println("Error_packages121_fr3::"+err.getMessage());
        }
    
        //creation of info_packet
        for(int i=0;i<rolls.size();i++){
            info_packet=info_packet+rolls.get(i)+";"+names.get(i)+";"+total_days.get(i)+";"+present_days.get(i)+";"+emails.get(i)+"/";
        }
        
        //test print
        System.out.println(info_packet);
        
        if(jCheckBox1.isSelected()){
            include=1;
        }
        if(send_bulk(msg, info_packet, from, j,include,subject,f_name,month)){
            JOptionPane.showMessageDialog(this, "Your request has been processed successfully!");    
        };
    }
    
    boolean send_bulk(String msg, String info_packet, String from, int bulk_no,int include, String subject, String f_name, String month){        
        try {
            URL url = new URL("http://aecwb.16mb.com/mailing/send_bulk_msg.php");
            URLConnection con = url.openConnection();
            con.setDoOutput(true);
            
            PrintStream ps = new PrintStream(con.getOutputStream());
            ps.print("msg="+msg);
            ps.print("&info_packet="+info_packet);
            ps.print("&from="+from);
            ps.print("&bulk_no="+bulk_no);
            ps.print("&include="+include);
            ps.print("&subject="+subject);
            ps.print("&month="+month);
            ps.print("&_name="+f_name);
    
            // we have to get the input stream in order to actually send the request
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = "";
            String res = "";
            while ((line = in.readLine()) != null) {
                res+=line;
            }
            //data is recieved in a stream of strings
            ps.close();
            return true;
        } 
        catch (MalformedURLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "There is a problem with your request!\n"+e.getMessage());
            return false;
        } 
        catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "There is a problem with your request!\n"+e.getMessage());
            return false;
        }
    }
    

    //function is acting as button event listener function
    void send_record1(){
        String class_name="classon";
        String month="January";
        String to=jTextField1.getText();
        send_record(class_name,month,to);
    }
    
    
    void send_record(String class_name,String month, String to){
        DefaultTableModel model=(DefaultTableModel) jTable2.getModel();
        String msg="";
        String from="";
        String name="";
        String user="";
        String branch="";
        String batch="";
        String year="";
        
        List<String> batch_list=new ArrayList<String>();
        
        for(int i=0;i<model.getRowCount();i++){
            msg=msg+model.getValueAt(i, 0)+";"+model.getValueAt(i, 1)+";"+model.getValueAt(i, 2)+";"+model.getValueAt(i, 3)+"\n";
        }
        
        Connection con; 
        ResultSet rs;
        try {
            con=(Connection)DriverManager.getConnection("jdbc:derby://localhost:1527/sam_manager","sam_user","pass_sam");
            Statement stmt=con.createStatement();
            
            rs=stmt.executeQuery("Select name,username,email from user_details");
            while(rs.next()){
                name=rs.getString("name");
                from=rs.getString("email");
                user=rs.getString("username");
            }
            
            rs=stmt.executeQuery("select PACKAGE_NAME from SERVICE where CLASS_NAME='"+class_name+"'");
            while(rs.next()){
                String package_name=rs.getString("PACKAGE_NAME");
                branch=package_name.substring(0, package_name.length()-5);
                year="20"+package_name.substring(package_name.length()-2, package_name.length());
                batch_list.add(package_name.substring(package_name.length()-5,package_name.length()-2));
            }
            
            for(int i=0;i<batch_list.size();i++){
                if(i==0){
                    batch="Batch "+batch_list.get(i);
                }else if(i==batch_list.size()-1){
                    batch=" & "+batch_list.get(i);
                }else{
                    batch=", "+batch_list.get(i);
                }
            }
            
            if(send_mail(msg, to, from, name, user, branch, batch, year,month,class_name)){
                //do the true stuffs
                JOptionPane.showMessageDialog(this, "Message Sent Successfully!\n An extra copy of record has been sent to your mail for future references.");
            }
            
            rs.close();
            stmt.close();
            con.close();
        }
        catch(SQLException err){
            System.out.println("Error_packages1210_fr3::"+err.getMessage());
        }
    }
    
    
    boolean send_mail(String msg, String to, String from, String name, String user, String branch, String batch, String year, String month, String class_name){
        try {
            URL url = new URL("http://aecwb.16mb.com/records/generate_pdf.php");
            
            URLConnection con = url.openConnection();
            con.setDoOutput(true);
            
            PrintStream ps = new PrintStream(con.getOutputStream());
            ps.print("record="+msg);
            ps.print("&to="+to);
            ps.print("&from="+from);
            ps.print("&name="+name);
            ps.print("&user="+user);
            ps.print("&branch="+branch);
            ps.print("&batch="+batch);
            ps.print("&year="+year);
            ps.print("&month="+month);
            ps.print("&class_name="+class_name);
            
            // we have to get the input stream in order to actually send the request
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = "";
            String res = "";
            while ((line = in.readLine()) != null) {
                res+=line;
            }
            //data is recieved in a stream of strings
            ps.close();
        } 
        catch (MalformedURLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "There is a problem with your request!\n"+e.getMessage());
            return false;
        } 
        catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "There is a problem with your request!\n"+e.getMessage());
            return false;
        }
        return true;
    }
    
    void connectionMsg(){
        JOptionPane.showMessageDialog(this, "what the hell is it?");
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane3 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();
        jButton3 = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        jTextField2 = new javax.swing.JTextField();

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Roll", "Student Name", "Total Days", "Present Days", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jTable2.setRowHeight(25);
        jTable2.setOpaque(true);
        jTable2.setFillsViewportHeight(true);
        jTable2.setBackground(new Color(255, 255, 255));
        jTable2.getTableHeader().setBackground(new Color(255, 255, 255));
        jScrollPane3.setViewportView(jTable2);

        jButton1.setText("get soft copy");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("send mail");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jScrollPane1.setViewportView(jEditorPane1);

        jButton3.setText("jButton3");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jCheckBox1.setBackground(new java.awt.Color(255, 255, 255));
        jCheckBox1.setFont(new java.awt.Font("Segoe UI Semilight", 0, 11)); // NOI18N
        jCheckBox1.setText("Include Attendance");

        jTextField2.setFont(new java.awt.Font("Segoe UI Semilight", 0, 18)); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 603, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextField1))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton3)
                        .addComponent(jButton2))
                    .addComponent(jTextField2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox1))
                .addGap(18, 18, 18))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addGap(18, 18, 18)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addGap(18, 18, 18)
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton3)
                .addGap(34, 34, 34))
            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        /*try{
            PrintWriter writer = new PrintWriter("C:/sam/the-file-name.txt", "UTF-8");
            writer.println("The first line");
            writer.println("The second line");
            writer.close();
        }catch(Exception e){
            e.getStackTrace();
        }*/
        
        
        DefaultTableModel model=(DefaultTableModel) jTable2.getModel();
        
        //create_pdf();
        String branch="";
        String year="";
        String batch="";
        String name="";
        String record_date="January";
        String class_name="classy";
        create_table_pdf(model,branch,year,batch,record_date,name,class_name);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        send_record1();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        bulk_setup("classon","January");
    }//GEN-LAST:event_jButton3ActionPerformed

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public void onStartPage(PdfWriter writer, Document document) {
        //ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Top Left"), 30, 800, 0);
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Generated by e-SAM System"), 500, 800, 0);
    }

    public void onEndPage(PdfWriter writer, Document document) {
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("http://www.aecwb.edu.in"), 110, 30, 0);
        int i=Integer.valueOf(document.getPageNumber())+1;
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Document:" + i), 520, 30, 0);
    }
    
    void create_table_pdf(DefaultTableModel model, String branch, String year, String batch,String record_date, String name, String class_name){
    
        branch="Computer Science & Engineering";
        year="2013";
        batch="Batch B11 & B12";
        name="Rohit Sharma";
        
        Date myDate=new Date();
        String prep_date=new SimpleDateFormat("dd/MM/yyyy").format(myDate);
        String dir=FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
        String file_name=class_name+"_"+record_date+".pdf";
        record_date=record_date+", "+new SimpleDateFormat("yyyy").format(myDate);

        Document document = new Document(PageSize.A4);
        
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(dir+"/"+file_name));

            Font fontStyle1 = FontFactory.getFont(FontFactory.HELVETICA, 22, Font.BOLD);
            Font fontStyle2 = FontFactory.getFont(FontFactory.HELVETICA, 16, Font.NORMAL);

            document.open();

            onStartPage(writer,document);

            Paragraph paragraphOne = new Paragraph("\nAsansol Engineering College", fontStyle1);
            document.add(paragraphOne);

            paragraphOne = new Paragraph("Student Attendance Report", fontStyle2);
            document.add(paragraphOne);
            paragraphOne = new Paragraph("\n", fontStyle2);
            document.add(paragraphOne);

            PdfPTable table1 = new PdfPTable(2); // 3 columns.
            table1.setWidthPercentage(100); //Width 100%
            table1.setSpacingBefore(10f); //Space before table
            table1.setSpacingAfter(10f); //Space after table

            table1.setWidths(new float[]{1f,1f});
            String cell="";
            String value="";
            for(int i=0;i<6;i++){

                if(i==0){
                    cell="Attendance Record of: ";
                    value=record_date;
                }else if(i==1){
                    cell="Date of preparation: ";
                    value=prep_date;
                }else if(i==2){
                    cell="Department: ";
                    value=branch;
                }else if(i==3){
                    cell="Year (Year of Enrolment): ";
                    value=year;
                }else if(i==4){
                    cell="Batch: ";
                    value=batch;
                }else if(i==5){
                    cell="Respective Faculty: ";
                    value=name;
                }

                PdfPCell cell_name = new PdfPCell(new Paragraph(cell));
                PdfPCell cell_value = new PdfPCell(new Paragraph(value));

                cell_name.setPadding(5);
                cell_value.setPadding(5);

                cell_name.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell_value.setHorizontalAlignment(Element.ALIGN_LEFT);

                cell_name.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell_value.setVerticalAlignment(Element.ALIGN_MIDDLE);

                cell_name.setBorderColor(BaseColor.WHITE);
                cell_value.setBorderColor(BaseColor.WHITE);

                table1.addCell(cell_name);
                table1.addCell(cell_value);
            }

            document.add(table1);

            //add table to pdf
            PdfPTable table = new PdfPTable(5); // 5 columns.
            table.setWidthPercentage(100); //Width 100%
            table.setSpacingBefore(10f); //Space before table
            table.setSpacingAfter(10f); //Space after table

            //Set Column widths
            float[] columnWidths = {0.3f, 1f, 2f, 0.9f, 1.2f};
            table.setWidths(columnWidths);
            for(int i=0;i<model.getRowCount()+1;i++){
                String col1="";
                String col2="ROLL";
                String col3="NAME";
                String col4="TOTAL DAYS";
                String col5="PRESENT DAYS";
                if(i!=0){
                    col1=String.valueOf(i);
                    col2=String.valueOf(model.getValueAt(i-1, 0));
                    col3=String.valueOf(model.getValueAt(i-1, 1));
                    col4=String.valueOf(model.getValueAt(i-1, 2));
                    col5=String.valueOf(model.getValueAt(i-1, 3));
                }
                PdfPCell cell1 = new PdfPCell(new Paragraph(col1));
                PdfPCell cell2 = new PdfPCell(new Paragraph(col2));
                PdfPCell cell3 = new PdfPCell(new Paragraph(col3));
                PdfPCell cell4 = new PdfPCell(new Paragraph(col4));
                PdfPCell cell5 = new PdfPCell(new Paragraph(col5));

                cell1.setBorderColor(BaseColor.WHITE);
                cell2.setBorderColor(BaseColor.WHITE);
                cell3.setBorderColor(BaseColor.WHITE);
                cell4.setBorderColor(BaseColor.WHITE);
                cell5.setBorderColor(BaseColor.WHITE);

                cell1.setPadding(5);
                cell2.setPadding(5);
                cell3.setPadding(5);
                cell4.setPadding(5);
                cell5.setPadding(5);

                cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell3.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell5.setHorizontalAlignment(Element.ALIGN_CENTER);

                cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell5.setVerticalAlignment(Element.ALIGN_MIDDLE);

                table.addCell(cell1);
                table.addCell(cell2);
                table.addCell(cell3);
                table.addCell(cell4);
                table.addCell(cell5);
            }
            document.add(table);
            onEndPage(writer, document);
            document.close();
            writer.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
}
    
    void create_pdf(){
        
    String head="Asansol Engineering ";
    String para="hey what r you doing.\nnothing well is going on";
        
        
        Document document = new Document();
        try{
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("C:/sam/new_test.pdf"));
            document.open();
            document.add(new Paragraph(para));
            document.add(new Paragraph(para));
            document.close();
            writer.close();
            JOptionPane.showMessageDialog(this, "job done!");
        }catch (DocumentException e){
            e.printStackTrace();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}
