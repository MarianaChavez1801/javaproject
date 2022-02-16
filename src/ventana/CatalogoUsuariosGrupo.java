/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package ventana;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import project2.conexionConsulta;
import static ventana.CatalogoLaboratorios.listaMultiple;


/**
 *
 * @author MarBugaboo
 */
public class CatalogoUsuariosGrupo extends javax.swing.JDialog {

    /**
     * Creates new form CatalogoUsuariosGrupo
     */
    ResultSet rs;
    String nombre = "";
    
    public CatalogoUsuariosGrupo(java.awt.Frame parent, boolean modal, String nombreGrupo) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(null);
        nombre = nombreGrupo;
    }
    
    public CatalogoUsuariosGrupo(javax.swing.JDialog parent, boolean modal, String nombreGrupo) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(null);
        nombre = nombreGrupo;
    }
    
    public void Obt_RolesJlist (){
        
        
        DefaultListModel laboratorios = new DefaultListModel();
        Connection c = conexionConsulta.conectar();
        try{
            PreparedStatement pstm = c.prepareStatement("SELECT R.NAME_USUARIO FROM USUARIOS R LEFT JOIN GRUPOLABORATORIO G ON R.ID_USUARIO = G.ID_USUARIO WHERE G.ID_USUARIO IS NULL", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet res = pstm.executeQuery();
            while(res.next()){
                laboratorios.addElement(res.getString("R.NAME_USUARIO"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error en la consulta:" + e.getMessage());
        }
        
        listaMultiple.setModel(laboratorios);
    }
    
   

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        listaMultiple = new javax.swing.JList<>();
        textoVentanaListaMultiple = new javax.swing.JLabel();
        enviarLaboratorios = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jScrollPane1.setViewportView(listaMultiple);

        textoVentanaListaMultiple.setText("Seleccione el USUARIO  para este GRUPO:");

        enviarLaboratorios.setText("Aceptar");
        enviarLaboratorios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enviarLaboratoriosActionPerformed(evt);
            }
        });

        jButton1.setText("Crear Usuario");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(textoVentanaListaMultiple, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(enviarLaboratorios)
                .addGap(35, 35, 35)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(textoVentanaListaMultiple, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(enviarLaboratorios)
                    .addComponent(jButton1))
                .addGap(16, 16, 16))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        Obt_RolesJlist ();
        
    }//GEN-LAST:event_formWindowOpened

    private void enviarLaboratoriosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enviarLaboratoriosActionPerformed
        if(listaMultiple.getSelectedValue().equals("")) {
            //JOPTION NO SE CREO
        } else{
            Connection c = conexionConsulta.conectar();        
             String nameUsuario =listaMultiple.getSelectedValue().toString();
            try {
                PreparedStatement verificarStmt2 = c.prepareStatement("SELECT ID_USUARIO FROM USUARIOS WHERE NAME_USUARIO = ? ", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                verificarStmt2.setString(1, nameUsuario);
                ResultSet rs2 = verificarStmt2.executeQuery();
               if(rs2.next()){
                    String idUsuario = rs2.getString("ID_USUARIO");
                    CatalogoLaboratorios ch = new CatalogoLaboratorios(this, true, nombre, idUsuario);
                    ch.setVisible(true);
                    this.setVisible(false);
                    this.dispose();
               }
            } catch (SQLException ex) {
                Logger.getLogger(CatalogoUsuariosGrupo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
                    
    }//GEN-LAST:event_enviarLaboratoriosActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:     
        this.setVisible(false);
        formularioRolGrupo ch = new formularioRolGrupo(this , true, true, nombre);
        ch.setVisible(true);  
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     
    public static void main(String args[]) {
        /* Set the Nimbus look and feel 
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
 For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CatalogoUsuariosGrupo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CatalogoUsuariosGrupo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CatalogoUsuariosGrupo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CatalogoUsuariosGrupo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog 
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                CatalogoUsuariosGrupo dialog = new CatalogoUsuariosGrupo(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton enviarLaboratorios;
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JList<String> listaMultiple;
    private javax.swing.JLabel textoVentanaListaMultiple;
    // End of variables declaration//GEN-END:variables
}
