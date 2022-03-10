/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ventana;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javax.swing.JOptionPane;
import project2.conexionConsulta;
import ventana.inicio;

/**
 *
 * @author MarBugaboo
 */
public class formularioGrupo extends javax.swing.JFrame {

    /**
     * Creates new form formularioGrupo
     */
    ResultSet rs;
    boolean exito = false;
    public String grupo;
    public String tipoUsuario;
    String nombre = "";
    
    public formularioGrupo( ) {
        initComponents();
        setLocationRelativeTo(null);
        dispose();
        
    }
    
    public boolean BuscarRol() throws SQLException{
        
        exito = false;
        boolean ingresaAdmin = false;
        nombre = new String(usuarioIngresa.getText());
        String password = "";
        password = new String(passIngresa.getPassword());
        if ((nombre != "") || (password != "")){
            //Establece los valores para la sentencia SQL
            try{
                Connection c = conexionConsulta.conectar();
                PreparedStatement consultarStmt = c.prepareStatement("SELECT ID_USUARIO FROM USUARIOS WHERE NAME_USUARIO = BINARY ? AND PASSWORD = AES_ENCRYPT(?,?)"); //BINARY para diferenciar min y may
                consultarStmt.setString(1, nombre);
                consultarStmt.setString(2, password);
                consultarStmt.setString(3, password);
                ResultSet rs1 = consultarStmt.executeQuery();
                if (rs1.next()) {
                    String idUsuario = rs1.getString("ID_USUARIO");
                    System.out.println(idUsuario);
                    PreparedStatement consultarStmt2 = c.prepareStatement("SELECT ID_USUARIO, TIPO_USUARIO FROM USUARIOS WHERE ID_USUARIO = ? ");
                    consultarStmt2.setString(1, idUsuario);
                    //consultarStmt2.setString(2, grupo);
                    ResultSet rs2 = consultarStmt2.executeQuery();
                    if (rs2.next()){
                        tipoUsuario = rs2.getString("TIPO_USUARIO");
                        PreparedStatement consultarStmt3 = c.prepareStatement("SELECT DISTINCT ID_GRUPO FROM GRUPOLABORATORIO WHERE ID_USUARIO = ? ");
                        consultarStmt3.setString(1, idUsuario);                            
                        ResultSet rs3 = consultarStmt3.executeQuery();
                        if(tipoUsuario.equals("A") && rs3.next()){
                            System.err.println("ESTOY AQUI");
                            grupo = rs3.getString("ID_GRUPO");
                            System.out.println("ESTE ES EL GRUPO:  "+grupo);
                            ingresaAdmin = true;     
                        } else if( rs3.next()){
                            grupo = rs3.getString("ID_GRUPO");
                        } else {
                            JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de igresar, ESTE USUARIO NO PERTENECE A NINGUN GRUPO!!!  \n Inténtelo nuevamente con otro USUARIO", "Error", JOptionPane.ERROR_MESSAGE );
                            return false;
                        }
                        switch (tipoUsuario){
                            case "A":
                                JOptionPane.showMessageDialog(null, "Usted a INGRESADO con exito \n USTED INGRESO COMO ADMINISTRADOR", "Éxito", JOptionPane.INFORMATION_MESSAGE );
                                break;
                            case "P":
                                JOptionPane.showMessageDialog(null, "Usted a INGRESADO con exito \n Esta ingresando al GRUPO: " + grupo+"\nINGRESO COMO PRESTADOR", "Éxito", JOptionPane.INFORMATION_MESSAGE );
                                break;
                            case "T":
                                JOptionPane.showMessageDialog(null, "Usted a INGRESADO con exito \n Esta ingresando al GRUPO: " + grupo+"\nINGRESO COMO TEMPORAL", "Éxito", JOptionPane.INFORMATION_MESSAGE );
                                break;
                        }
                        consultarStmt.close();
                        exito = true;
                        return ingresaAdmin;
                    }else{
                        JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de igresar, ESTE USUARIO NO PERTENECE A ESTE GRUPO!!!  \n Inténtelo nuevamente con otro GRUPO", "Error", JOptionPane.ERROR_MESSAGE );
                        return ingresaAdmin;
                    } 
                } else {
                    JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de igresar, por favor verifique su USUARIO y su CONTRASEÑA!!!  \n Inténtelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                    return ingresaAdmin;
                }
            }catch (SQLException ex){
                JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de igresar, por favor verifique su USUARIO y su CONTRASEÑA!!!  \n Inténtelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                return ingresaAdmin;
            }finally{
                conexionConsulta.desconectar();
            }
        }else {
            JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de igresar, por favor ingrese su USUARIO y su CONTRASEÑA!!! \n Inténtelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
            return ingresaAdmin;
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

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        usuarioIngresa = new javax.swing.JTextField();
        passIngresa = new javax.swing.JPasswordField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        passVisibles = new java.awt.Checkbox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jLabel1.setText("Ingrese el nombre de su usuario:");

        jLabel2.setText("Ingrese su cotraseña:");

        usuarioIngresa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usuarioIngresaActionPerformed(evt);
            }
        });

        passIngresa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                passIngresaActionPerformed(evt);
            }
        });

        jButton1.setText("Aceptar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Cancelar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        passVisibles.setLabel("Contraseña Visible");
        passVisibles.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                passVisiblesItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(59, 59, 59)
                .addComponent(jButton1)
                .addGap(75, 75, 75)
                .addComponent(jButton2)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(passVisibles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(passIngresa, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(usuarioIngresa, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(usuarioIngresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(passIngresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                .addComponent(passVisibles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addGap(31, 31, 31))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void usuarioIngresaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usuarioIngresaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_usuarioIngresaActionPerformed

    private void passIngresaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passIngresaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_passIngresaActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        try {
           
            boolean admin = BuscarRol();
            if (exito == true){
                if(admin == true){
                    adminEquipos ch = null;
                    ch = new adminEquipos();
                    ch.setVisible(true);
                    ch.usuario = nombre;
                    //Registro en 
                    DateTimeFormatter dtf5 = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                    System.out.println("yyyy/MM/dd-> "+dtf5.format(LocalDateTime.now()));
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                    System.out.println("HH:mm:ss-> " + dtf.format(LocalDateTime.now()));
                    try{
                        Connection c = conexionConsulta.conectar();
                        PreparedStatement registroLog = c.prepareStatement("INSERT INTO LOGS(NAME_USUARIO, ACCION, FECHA_ACCION, HORA_ACCION) values(?, ?, ?, ?)", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        registroLog.setString(1, nombre);
                        registroLog.setString(2, "Ingreso al sistema");
                        registroLog.setString(3, dtf5.format(LocalDateTime.now()));
                        registroLog.setString(4, dtf.format(LocalDateTime.now()));
                        registroLog.execute();
                        registroLog.close();
                    }catch (SQLException e){
                        JOptionPane.showMessageDialog(null, "Ocurrió un error al INSERTAR EN LOGS"+e, "Error", JOptionPane.ERROR_MESSAGE );
                    }
                    
                    this.setVisible(false);
                    this.dispose();
                }else if(grupo == null){
                    usuarioIngresa.setText(null);
                    passIngresa.setText(null);
                    usuarioIngresa.requestFocus();
                } else {
                
                    ventanasMuestra ch = null;
                    ch = new ventanasMuestra();
                    ch.setVisible(true);
                    ch.grupo = grupo;
                    ch.usuario = nombre;
                    DateTimeFormatter dtf5 = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                    System.out.println("yyyy/MM/dd-> "+dtf5.format(LocalDateTime.now()));
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                    System.out.println("HH:mm:ss-> " + dtf.format(LocalDateTime.now()));
                    try{
                        Connection c = conexionConsulta.conectar();
                        PreparedStatement registroLog = c.prepareStatement("INSERT INTO LOGS(NAME_USUARIO, ACCION, FECHA_ACCION, HORA_ACCION) values(?, ?, ?, ?)", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        registroLog.setString(1, nombre);
                        registroLog.setString(2, "Ingreso al sistema");
                        registroLog.setString(3, dtf5.format(LocalDateTime.now()));
                        registroLog.setString(4, dtf.format(LocalDateTime.now()));
                        registroLog.execute();
                        registroLog.close();
                    }catch (SQLException e){
                        JOptionPane.showMessageDialog(null, "Ocurrió un error al INSERTAR EN LOGS"+e, "Error", JOptionPane.ERROR_MESSAGE );
                    }
                    this.setVisible(false);
                    this.dispose();
                }    
            }else {
                usuarioIngresa.setText(null);
                passIngresa.setText(null);
                usuarioIngresa.requestFocus();
            }
        }catch (SQLException ex){
            java.util.logging.Logger.getLogger(formularioGrupo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        System.exit(0);
        //inicio ch1 = new inicio();
        //ch1.setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String datePenalizacion = sdf.format(date);
        System.out.println(datePenalizacion);
    }//GEN-LAST:event_formWindowOpened

    private void passVisiblesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_passVisiblesItemStateChanged
        // TODO add your handling code here:
        if(passVisibles.getState() == true){
            passIngresa.setEchoChar((char)0);
        } else if(passVisibles.getState() == false){
            passIngresa.setEchoChar('*');
        }
    }//GEN-LAST:event_passVisiblesItemStateChanged

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(formularioGrupo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(formularioGrupo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(formularioGrupo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(formularioGrupo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new formularioGrupo().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    public javax.swing.JPasswordField passIngresa;
    public java.awt.Checkbox passVisibles;
    public javax.swing.JTextField usuarioIngresa;
    // End of variables declaration//GEN-END:variables
}
