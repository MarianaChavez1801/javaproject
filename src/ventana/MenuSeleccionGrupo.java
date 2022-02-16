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
import javax.swing.JOptionPane;
import project2.conexionConsulta;
import static ventana.adminEquipos.Obt_RolesSinGrupo;

/**
 *
 * @author MarBugaboo
 */
public class MenuSeleccionGrupo extends javax.swing.JDialog {

    /**
     * Creates new form MenuSeleccionGrupo
     */
    public MenuSeleccionGrupo(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(null);
    }
    
    public void actualizarGrupo(){
        int elementosActualizadosGrupos = 0;
        
        if(Opc1.isSelected()){ //CAMBIAR NOMBRE GRUPO 
            String ListaLaboratorio[] = adminEquipos.Obt_Grupos();
            try{
                Connection c = conexionConsulta.conectar();
                Object lab = JOptionPane.showInputDialog(null, "Seleccione el GRUPO que se modificara", "Mostrar GRUPOS", JOptionPane.QUESTION_MESSAGE, null, ListaLaboratorio, "01" );
                String idGrupo = lab.toString();
                String idGrupoNuevo = JOptionPane.showInputDialog("Escriba el NUEVO NOMBRE CORTO del GRUPO: ").toUpperCase() ;
                PreparedStatement verificarStmt = c.prepareStatement("SELECT ID_GRUPO FROM GRUPOS WHERE ID_GRUPO = ? ", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                verificarStmt.setString(1, idGrupoNuevo); //Verificando que el grupo exista
                ResultSet rs = verificarStmt.executeQuery();
                if(rs.next()){ //Si existe
                    JOptionPane.showMessageDialog(null, "¡¡¡Este grupo ya fue creado, para agregar mas porfavor hagalo en agregar a grupo !!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                }else{
                    PreparedStatement actualizarStmt = c.prepareStatement("UPDATE GRUPOS SET ID_GRUPO =? WHERE ID_GRUPO = ? ", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    actualizarStmt.setString(1, idGrupoNuevo); 
                    actualizarStmt.setString(2, idGrupo);
                    elementosActualizadosGrupos = actualizarStmt.executeUpdate();
                    if(elementosActualizadosGrupos >= 1){
                        JOptionPane.showMessageDialog(null, "El GRUPO se ha ACTUALIZADO con exito\nElementos ACTUALIZADOS en total"+elementosActualizadosGrupos, "Éxito", JOptionPane.INFORMATION_MESSAGE );
                        this.setVisible(false);
                        this.dispose();
                    } else{
                        JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un ERROR al tratar de ACTUALIZAR!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                    }
                }
            }catch(SQLException ex){
                    JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un ERROR al tratar de ACTUALIZAR!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
            }finally{
                conexionConsulta.desconectar();
            }
               
        }
        if(Opc2.isSelected()){//CAMBIAR LABORATORIOS ASIGNADOS EN ESTE GRUPO
            //AGREGAR LABORATORIOS
            //ELIMINARLABORATORIOS         0                            1              2
            Object [] botones = { "AGREGAR LABORATORIOS", "ELIMINAR LABORATORIOS", "CANCELAR" };
            int resp = JOptionPane.showOptionDialog (null, "ELIJA LA OPCION QUE QUIERE REALIZAR", "ACTUALIZAR GRUPO", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null/*icono*/, botones, botones[0]);
            switch (resp){
                case 0:  //AGREGAR LABORATORIO 1.SELECCIONAR GRUPO A MODIFICAR 2.MOSTRAR DISPONIBLES 2.AGREGAR LOS SELECCIONADOS
                    String ListaLaboratorio[] = adminEquipos.Obt_Grupos();
                    try{
                        Connection c = conexionConsulta.conectar();
                        Object lab = JOptionPane.showInputDialog(null, "Seleccione el GRUPO que se modificara", "Mostrar GRUPOS", JOptionPane.QUESTION_MESSAGE, null, ListaLaboratorio, "01" );
                        String idGrupo = lab.toString();
                        PreparedStatement verificarStmt = c.prepareStatement("SELECT ID_LAB FROM GRUPOS WHERE ID_GRUPO = ? ", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        verificarStmt.setString(1, idGrupo); //Verificando que el grupo exista
                        ResultSet rs = verificarStmt.executeQuery();
                        if(rs.next()){ //Si existe
                            boolean creado = true;
                            CatalogoLaboratorios ch = new CatalogoLaboratorios(this, true, idGrupo, creado);
                            ch.setVisible(true);
                            this.setVisible(false);
                            this.dispose();
                        }
                    }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un ERROR al tratar de ACTUALIZAR!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );   
                    }finally{
                        conexionConsulta.desconectar();
                    }
                    break;
                case 1:  
                    String ListaGrupos[] = adminEquipos.Obt_Grupos();
                    try{
                        Connection c = conexionConsulta.conectar();
                        Object lab = JOptionPane.showInputDialog(null, "Seleccione el GRUPO que se modificara", "Mostrar GRUPOS", JOptionPane.QUESTION_MESSAGE, null, ListaGrupos, "01" );
                        String idGrupo = lab.toString();
                        PreparedStatement verificarStmt = c.prepareStatement("SELECT ID_LAB FROM GRUPOS WHERE ID_GRUPO = ? ", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        verificarStmt.setString(1, idGrupo); //Verificando que el grupo exista
                        ResultSet rs = verificarStmt.executeQuery();
                        String ListaLabs[] =adminEquipos.Obt_LaboratorioAsignado(idGrupo);
                        if(rs.next()){ //Si existe
                            PreparedStatement verificarStmt2 = c.prepareStatement("SELECT ID_USUARIO FROM GRUPOS WHERE ID_GRUPO = ? ", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                            verificarStmt2.setString(1, idGrupo); //OBTENIENDO USUARIO
                            ResultSet rs2 = verificarStmt2.executeQuery();
                            if(rs2.next()){
                                String idUsuario = rs2.toString();
                                boolean grupoCreado = true;
                                boolean eliminare = true;
                                CatalogoLaboratorios ch = new CatalogoLaboratorios(this, true, idGrupo, idUsuario, grupoCreado, eliminare);
                                ch.setVisible(true);
                                this.setVisible(false);
                                this.dispose();
                            }
                            
                        }
                    }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un ERROR al tratar de ACTUALIZAR!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );   
                    }finally{
                        conexionConsulta.desconectar();
                    }
                         break;
                case 2:  //SALIR
                    this.setVisible(false);
                    this.dispose();
                         break;
            }

        }
        if(Opc3.isSelected()){//CAMBIAR USUARIO ASIGNADO
            //ELEGIR EL GRUPO QUE SE VA A MODIFICAR
            //MOSTRAR QUE USUARIO TIENE ESTE GRUPO Y VERIFICAR MODIFICACION
            //CUANDO ACEPTE PEDIR QUE NUEVO USUARIO SE ASIGNARA A ESTE GRUPO, NO DEBE TENER NINGUNO ACTIVO
            //SE HACE LA ACTUALIZACION SOLO EN LA TABLA GRUPOS
            String ListaLaboratorio[] = adminEquipos.Obt_Grupos();
            try{
                Connection c = conexionConsulta.conectar();
                Object lab = JOptionPane.showInputDialog(null, "Seleccione el GRUPO que se modificara", "Mostrar GRUPOS", JOptionPane.QUESTION_MESSAGE, null, ListaLaboratorio, "01" );
                String idGrupo = lab.toString();
                PreparedStatement verificarStmt = c.prepareStatement("SELECT ID_USUARIO FROM GRUPOS WHERE ID_GRUPO = ? ", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                verificarStmt.setString(1, idGrupo); //Verificando que el grupo exista
                ResultSet rs = verificarStmt.executeQuery();
                if(rs.next()){ //Si existe
                    String idUsuario = rs.getString("ID_USUARIO");
                    int resp = JOptionPane.showConfirmDialog(null, "El GRUPO a ACTUAIZAR tiene asignado el SIGUIENTE USUARIO"+idUsuario+"¿Esta seguro?",//<- EL MENSAJE 
                    "Alerta!"/*<- El título de la ventana*/, JOptionPane.YES_NO_OPTION/*Las opciones (si o no)*/, JOptionPane.WARNING_MESSAGE/*El tipo de ventana, en este caso WARNING*/);
                    if (resp == 0){ //SI RESPONDIO SI
                        if(adminEquipos.Obt_RolesSinGrupo() == null){
                            JOptionPane.showMessageDialog(null, "¡¡¡No se encontro ningun USUARIO DISPONIBLE!!!, Por favor Ingrese a CREAR USUARIO", "Error", JOptionPane.ERROR_MESSAGE );
                            this.setVisible(false);
                            this.dispose();
                        }else{
                            String ListaRoles[] = adminEquipos.Obt_RolesSinGrupo();
                            Object usuario = JOptionPane.showInputDialog(null, "Seleccione el USUARIO que pertenecera a este GRUPO ","\n ACTUALIZAR USUARIO", JOptionPane.QUESTION_MESSAGE, null, ListaRoles, "01" );
                            String nameUsuario = usuario.toString();
                            PreparedStatement verificarStmt2 = c.prepareStatement("SELECT ID_USUARIO FROM USUARIOS WHERE NAME_USUARIO = ? ", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                            verificarStmt2.setString(1, nameUsuario);
                            ResultSet rs2 = verificarStmt2.executeQuery();
                            if(rs2.next()){
                                String idUsuarioNuevo = rs2.getString("ID_USUARIO");
                                PreparedStatement actualizarStmt2 = c.prepareStatement("UPDATE GRUPOS SET ID_USUARIO = ? WHERE ID_USUARIO = ?; ", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                actualizarStmt2.setString(1, idUsuarioNuevo);
                                actualizarStmt2.setString(2, idUsuario);
                                elementosActualizadosGrupos = actualizarStmt2.executeUpdate();
                                if(elementosActualizadosGrupos >= 1){
                                    JOptionPane.showMessageDialog(null, "El GRUPO se ha ACTUALIZADO con exito\nElementos ACTUALIZADOS en total"+elementosActualizadosGrupos, "Éxito", JOptionPane.INFORMATION_MESSAGE );
                                    this.setVisible(false);
                                    this.dispose();
                                } else{
                                    //NO SE ACTUALIZO NADA
                                    JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un ERROR al tratar de ACTUALIZAR!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                                }
                                
                            } else {
                                //NO SE ENCONTRO EL USUARIO SELECCIONADO
                                JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un ERROR al tratar de ACTUALIZAR, ESTE USUARIO NO ESTA DISPONIBLE!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                            }
                        }
                    }else{
                        this.setVisible(false);
                        this.dispose();
                    }
                }else{
                   JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un ERROR al tratar de ACTUALIZAR, ESTE GRUPO NO EXISTE!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE ); 
                }
            } catch (SQLException ex){
                JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un ERROR al tratar de ACTUALIZAR!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
            } finally{
                conexionConsulta.desconectar();
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        Opc1 = new javax.swing.JRadioButton();
        Opc2 = new javax.swing.JRadioButton();
        Opc3 = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Seleccione que es lo que desea realizar:");

        jButton1.setText("ACEPTAR");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        buttonGroup1.add(Opc1);
        Opc1.setText("CAMBIAR NOMBRE A GRUPO");

        buttonGroup1.add(Opc2);
        Opc2.setText("CAMBIAR LABORATORIOS ASIGNADOS EN ESTE GRUPO");
        Opc2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Opc2ActionPerformed(evt);
            }
        });

        buttonGroup1.add(Opc3);
        Opc3.setText("CAMBIAR ROL ASIGNADO");
        Opc3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Opc3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(Opc2)
                        .addComponent(Opc1)
                        .addComponent(Opc3))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(148, 148, 148)
                            .addComponent(jButton1))))
                .addContainerGap(103, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(31, 31, 31)
                .addComponent(Opc1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Opc2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Opc3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 64, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        actualizarGrupo();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void Opc2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Opc2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Opc2ActionPerformed

    private void Opc3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Opc3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Opc3ActionPerformed

    /**
     * @param args the command line arguments
     
    public static void main(String args[]) 
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
            java.util.logging.Logger.getLogger(MenuSeleccionGrupo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MenuSeleccionGrupo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MenuSeleccionGrupo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MenuSeleccionGrupo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog 
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MenuSeleccionGrupo dialog = new MenuSeleccionGrupo(new javax.swing.JFrame(), true);
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
    public javax.swing.JRadioButton Opc1;
    public javax.swing.JRadioButton Opc2;
    public javax.swing.JRadioButton Opc3;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables

}
