/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package ventana;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import javax.swing.JOptionPane;
import project2.conexionConsulta;

/**
 *
 * @author MarBugaboo
 */
public class formularioRolGrupo extends javax.swing.JDialog {

    /**
     * Creates new form formularioUsuarioGrupo
     */
    
    boolean grupoProceso = false;
    String grupoRecibido = "";
    String tipoUsuario = "";
    
    public formularioRolGrupo(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        dispose();
        initComponents();
        setLocationRelativeTo(null); //para colocar el jframe en el centro de la pantalla   
    }
    
    public formularioRolGrupo(javax.swing.JDialog parent, boolean modal) {
        super(parent, modal);
        dispose();
        initComponents();
        setLocationRelativeTo(null); //para colocar el jframe en el centro de la pantalla   
    }
    
    public formularioRolGrupo(javax.swing.JDialog parent, boolean modal, boolean grupo, String grupoS) {
        super(parent, modal);
        dispose();
        initComponents();
        setLocationRelativeTo(null); //para colocar el jframe en el centro de la pantalla  
        grupoProceso = true;
        grupoRecibido = grupoS;
    }
    
    public boolean verificacionContrasena(String pass1){
        
        boolean may = false;
        boolean min = false;
        boolean num = false;
        boolean car = false;
        boolean tam = false;
        
        if (pass1.length() >= 8){
            tam = true;
            for(int n = 0; n<pass1.length (); n ++){ 
                System.out.println(pass1.length());
                int c = pass1.charAt (n);
                if (c>64 && c<91 ){
                    may = true;
                }else{
                    if (c>96 && c<123){
                        min = true;
                    }else{
                        if (c>47 && c<58){
                            num = true;
                        } else {
                            if (c>31 && c<48 || c>57 && c<65 || c>90 && c<97 || c>122 && c<127){
                                car = true;
                            }
                        }
                    }
                }
            }
        }else{
            JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de guardar los datos!!!\n RECUERDE QUE LA CONTRASEÑA DEBE SER DE ALMENOS 8 CARACTERES \n Recuerde que la contraseña debe contener al menos un(a):\n Minusculas,Mayusculas,Numero y Caracter especial\n ** No se guardaron datos en la base de datos *** \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
        }
        if (may == true && min == true && num == true && car == true && tam == true){
            return true;
        }else{
            return false;
        }

    }
    public boolean tipoUsuarioSeleccionado(){
        
        if(admin.isSelected() == true){
            tipoUsuario = "A";
            return true;
        } else if(temporal.isSelected() == true){
            tipoUsuario = "T";
            return true;
        } else if(prestador.isSelected()){
            tipoUsuario = "P";
            return true;
        } else {
            //JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de guardar los datos!!!\n PORFAVOR SELECCIONE UN TIPO DE USUARIO", "Error", JOptionPane.ERROR_MESSAGE );
            return false;
        }
        
    }
    public void guardarRol(){
        
        //Pregunta el nombre de la persona a la cual corresponde dicha huella
        String nombre = "";
        nombre = new String(nombreUsuario.getText());
        String password = "";
        password = new String(pass1.getPassword());    
        
        try{
            //Establece los valores para la sentencia SQL
            Connection c = conexionConsulta.conectar();   
            //PreparedStatement guardarStmt = c.prepareStatement("INSERT INTO ROLES(NAME_ROL, PASSWORD) VALUES (?, ? )");
            PreparedStatement guardarStmt = c.prepareStatement("INSERT INTO USUARIOS(NAME_USUARIO, PASSWORD, TIPO_USUARIO) VALUES (?,AES_ENCRYPT(?,?), ?)");
            guardarStmt.setString(1, nombre);
            guardarStmt.setString(2, password);
            guardarStmt.setString(3, password);
            guardarStmt.setString(4, tipoUsuario);
            //Ejecuta la sentencia preparada
            guardarStmt.execute();
            guardarStmt.close();
            JOptionPane.showMessageDialog(null, "El USUARIO ha sido registrado con exito", "Éxito", JOptionPane.INFORMATION_MESSAGE );
            

        }catch(SQLException ex){
            if(ex instanceof SQLIntegrityConstraintViolationException ){
                //Si ocurre una excepcion de integridad de restriccion en primary key, lo indica
                JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de guardar los datos!!! \nIdentificador duplicado en '"+nombre+"' para el registro de usuario\n ** Los datos no se guardaron en la base de datos *** \nCorrija o use el panel de Identificación de Usuarios", "Error", JOptionPane.ERROR_MESSAGE );
                pass1.setText(null);
                pass2.setText(null);
                nombreUsuario.setText(null);
            }
            else{
                //Si ocurre alguna otra excepcion la indica
                JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de guardar los datos!!! \n"+ex.getMessage()+"\n ** No se guardaron datos en la base de datos *** \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                pass1.setText(null);
                pass2.setText(null);
                nombreUsuario.setText(null);
                
            }
            System.err.println("¡¡¡Ocurrio un error al tratar de guardar los datos!!! \n\n ** No se guardaron datos en la base de datos *** \n Inténtelo nuevamente");
            pass1.setText(null);
            pass2.setText(null);
            nombreUsuario.setText(null);
            
        }finally{
            conexionConsulta.desconectar();
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

        tipoUsuarioBG = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        nombreUsuario = new javax.swing.JTextField();
        pass1 = new javax.swing.JPasswordField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        pass2 = new javax.swing.JPasswordField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        passVisibles = new java.awt.Checkbox();
        admin = new javax.swing.JRadioButton();
        prestador = new javax.swing.JRadioButton();
        temporal = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Nombre Usuario:");

        nombreUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nombreUsuarioActionPerformed(evt);
            }
        });

        pass1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pass1ActionPerformed(evt);
            }
        });

        jLabel2.setText("Contraseña:");

        jLabel3.setText("Verfique la contraseña:");

        pass2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pass2ActionPerformed(evt);
            }
        });

        jButton1.setText("Enviar");
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

        tipoUsuarioBG.add(admin);
        admin.setText("Administrador");
        admin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adminActionPerformed(evt);
            }
        });

        tipoUsuarioBG.add(prestador);
        prestador.setText("Prestador");

        tipoUsuarioBG.add(temporal);
        temporal.setText("Temporal");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(temporal, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(prestador, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(admin, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pass2)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(passVisibles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(37, 37, 37))))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(nombreUsuario, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
                            .addComponent(pass1)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(160, 160, 160)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addComponent(jButton2)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(nombreUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(pass1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(pass2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(passVisibles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(admin))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(prestador)
                .addGap(7, 7, 7)
                .addComponent(temporal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void nombreUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nombreUsuarioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nombreUsuarioActionPerformed

    private void pass1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pass1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pass1ActionPerformed

    private void pass2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pass2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pass2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        String Spass1 = new String(pass1.getPassword());
        String Spass2 = new String(pass2.getPassword());
        String nombreUsuario1 = new String (nombreUsuario.getText());
        boolean tipo = tipoUsuarioSeleccionado();
        if(pass1 != null && pass2 != null && nombreUsuario != null && tipo == true){

            if (Spass1.equals(Spass2)){

                boolean passIguales = verificacionContrasena(Spass1);

                if(passIguales == true){
                    if(grupoProceso == true){
                        guardarRol();
                        this.setVisible(false);
                        this.dispose();
                        CatalogoUsuariosGrupo ch = new CatalogoUsuariosGrupo(this, true, grupoRecibido);
                        ch.setVisible(true);
                    } else {
                        guardarRol();
                        this.dispose();
                    }
                }else{
                    //JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de guardar los datos!!! ** No se guardaron datos en la base de datos *** \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                    pass1.setText(null);
                    pass2.setText(null);
                }
            }else{
                JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de guardar los datos!!!\n Las contraseñas no coinciden\n ** No se guardaron datos en la base de datos *** \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                pass1.setText(null);
                pass2.setText(null);
            }
        } else {
            JOptionPane.showMessageDialog(null, "¡¡¡Por favor, asegurese de llenar todos los campos!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
            pass1.setText(null);
            pass2.setText(null);
            nombreUsuario.setText(null);
        }

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void passVisiblesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_passVisiblesItemStateChanged
        // TODO add your handling code here:
        if(passVisibles.getState() == true){
            pass1.setEchoChar((char)0);
            pass2.setEchoChar((char)0);
        } else if(passVisibles.getState() == false){
            pass1.setEchoChar('*');
            pass2.setEchoChar('*');
        }
    }//GEN-LAST:event_passVisiblesItemStateChanged

    private void adminActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adminActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_adminActionPerformed

    /**
     * @param args the command line arguments
     
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(formularioRolGrupo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(formularioRolGrupo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(formularioRolGrupo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(formularioRolGrupo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog 
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                formularioRolGrupo dialog = new formularioRolGrupo(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }*/

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton admin;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField nombreUsuario;
    private javax.swing.JPasswordField pass1;
    private javax.swing.JPasswordField pass2;
    public java.awt.Checkbox passVisibles;
    private javax.swing.JRadioButton prestador;
    private javax.swing.JRadioButton temporal;
    private javax.swing.ButtonGroup tipoUsuarioBG;
    // End of variables declaration//GEN-END:variables
}
