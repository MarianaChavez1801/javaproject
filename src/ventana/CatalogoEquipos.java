/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package ventana;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
public class CatalogoEquipos extends javax.swing.JDialog {

    /**
     * Creates new form CatalogoUsuariosGrupo
     */
    ResultSet rs;
    String nombre = "";
    boolean utilizar = false;
    String usuario = "";
    boolean obs = false;
    
    public CatalogoEquipos(java.awt.Frame parent, boolean modal, String nombreLab, boolean utiliza, String user) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(null);
        nombre = nombreLab;
        utilizar = utiliza; 
        usuario = user;
        
    }
    
    public CatalogoEquipos(javax.swing.JDialog parent, boolean modal, String nombreLab, boolean utiliza, String user) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(null);
        nombre = nombreLab;
        utilizar = utiliza;
        usuario = user;
    }
    
    public CatalogoEquipos(java.awt.Frame parent, boolean modal, String nombreLab, String user, boolean observacion) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(null);
        nombre = nombreLab;        
        usuario = user;
        obs = observacion;
        
    }
    
    public CatalogoEquipos(javax.swing.JDialog parent, boolean modal, String nombreLab, String user, boolean observacion) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(null);
        nombre = nombreLab;        
        usuario = user;
        obs = observacion;
    }
    
    public void Obt_EquiposJlist (int utilizable){
        
        
        DefaultListModel equipos = new DefaultListModel();
        Connection c = conexionConsulta.conectar();
        int totalElementos = 0;
        try{
            PreparedStatement pstm = c.prepareStatement("SELECT NUM_COMP FROM EQUIPOS WHERE ID_LAB = ? AND UTILIZABLE = ? ORDER BY NUM_COMP", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            pstm.setString(1, nombre);
            pstm.setInt(2, utilizable);
            ResultSet res = pstm.executeQuery();
            while(res.next()){
                equipos.addElement(res.getString("NUM_COMP"));
                totalElementos ++;
            }
            
        } catch (SQLException e) {
            System.err.println("Error en la consulta:" + e.getMessage());
        }
        
        listaMultiple.setModel(equipos);
        if (totalElementos < 1) {
            JOptionPane.showMessageDialog(null, "No se encontro NINGÚN EQUIPO para esta acción", "Éxito", JOptionPane.INFORMATION_MESSAGE );
            this.setVisible(false);
            this.dispose();
        }
    }
    
    public void inutilizarEquipo (int utilizable, String equipo){
        
        if(equipo.equals("")){
            if(listaMultiple.getSelectedValue().equals("")) {
                JOptionPane.showMessageDialog(null, "¡¡¡Seleccione un EQUIPO!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
            } else{
                int elementosActualizadosGrupos = 0;
                DefaultListModel model = (DefaultListModel) listaMultiple.getModel();
                List selectedItems = listaMultiple.getSelectedValuesList();
                for (Object sel : selectedItems ){
                    try{
                        Connection c = conexionConsulta.conectar();
                        PreparedStatement actualizarStmt2 = c.prepareStatement("UPDATE EQUIPOS SET UTILIZABLE = ? WHERE NUM_COMP = ? AND ID_LAB = ?");
                        actualizarStmt2.setInt(1, utilizable);
                        actualizarStmt2.setString(2, sel.toString());
                        actualizarStmt2.setString(3, nombre);
                        elementosActualizadosGrupos = elementosActualizadosGrupos + actualizarStmt2.executeUpdate();
                        actualizarStmt2.close();

                        DateTimeFormatter dtf5 = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                        System.out.println("yyyy/MM/dd-> " + dtf5.format(LocalDateTime.now()));
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                        System.out.println("HH:mm:ss-> " + dtf.format(LocalDateTime.now()));
                        //Connection c = conexionConsulta.conectar();
                        try (PreparedStatement registroLog = c.prepareStatement("INSERT INTO LOGS(NAME_USUARIO, ACCION, FECHA_ACCION, HORA_ACCION) values(?, ?, ?, ?)", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                            registroLog.setString(1, usuario);
                            if(utilizable == 0){
                                String utilizableString = "INUTILIZABLE";
                                registroLog.setString(2, "El equipo: "+sel.toString()+"cambio a: "+utilizableString);
                                observacionEquipo(sel.toString());                            
                            } else if(utilizable == 1){
                                String utilizableString = "UTILIZABLE";
                                registroLog.setString(2, "El equipo: " + sel.toString() + "cambio a: " + utilizableString);
                            }
                            registroLog.setString(3, dtf5.format(LocalDateTime.now()));
                            registroLog.setString(4, dtf.format(LocalDateTime.now()));
                            registroLog.execute();
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(null, "Ocurrió un error al INSERTAR EN LOGS" + ex, "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }catch (SQLException ex){
                         JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un ERROR al tratar de ACTUALIZAR LABORATORIOS!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                    }finally{
                        conexionConsulta.desconectar();
                    }
                }

                if(elementosActualizadosGrupos > 0){
                    JOptionPane.showMessageDialog(null, "Se actualizo corectamente el estado UTILIZABLE de "+elementosActualizadosGrupos+" EQUIPOS", "Éxito", JOptionPane.INFORMATION_MESSAGE );
                }

            }
        } else {            
            try {
                Connection c = conexionConsulta.conectar();
                PreparedStatement actualizarStmt2 = c.prepareStatement("UPDATE EQUIPOS SET UTILIZABLE = ? WHERE NUM_COMP = ? AND ID_LAB = ?");
                actualizarStmt2.setInt(1, utilizable);
                actualizarStmt2.setString(2, equipo);
                actualizarStmt2.setString(3, nombre);
                int elementosActualizadosGrupos = actualizarStmt2.executeUpdate();
                if (elementosActualizadosGrupos > 0) {
                JOptionPane.showMessageDialog(null, "Se actualizo corectamente el estado UTILIZABLE de " + elementosActualizadosGrupos + " EQUIPOS", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Ocurrió un error al tratar de Modificar Utilizable en Equipo" + equipo, "Error", JOptionPane.ERROR_MESSAGE);
                }
                actualizarStmt2.close();

                DateTimeFormatter dtf5 = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                System.out.println("yyyy/MM/dd-> " + dtf5.format(LocalDateTime.now()));
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                System.out.println("HH:mm:ss-> " + dtf.format(LocalDateTime.now()));
                //Connection c = conexionConsulta.conectar();
                try (PreparedStatement registroLog = c.prepareStatement("INSERT INTO LOGS(NAME_USUARIO, ACCION, FECHA_ACCION, HORA_ACCION) values(?, ?, ?, ?)", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                    registroLog.setString(1, usuario);
                    if (utilizable == 0) {
                        String utilizableString = "INUTILIZABLE";
                        registroLog.setString(2, "El equipo: " + equipo + "cambio a: " + utilizableString);
                        //observacionEquipo(equipo);
                    } /*else if (utilizable == 1) {
                        String utilizableString = "UTILIZABLE";
                        registroLog.setString(2, "El equipo: " + sel.toString() + "cambio a: " + utilizableString);
                    }*/
                    registroLog.setString(3, dtf5.format(LocalDateTime.now()));
                    registroLog.setString(4, dtf.format(LocalDateTime.now()));
                    registroLog.execute();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Ocurrió un error al INSERTAR EN LOGS" + ex, "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un ERROR al tratar de ACTUALIZAR LABORATORIOS!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                conexionConsulta.desconectar();
            }            
        }
        
    }
    
    public void observacionEquipo(String equipo){
        if(equipo == ""){
            int countElementos = 0;
            if(listaMultiple.getSelectedValue().equals("")) {
                JOptionPane.showMessageDialog(null, "¡¡¡Seleccione un EQUIPO!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
            } else{
                DefaultListModel model = (DefaultListModel) listaMultiple.getModel();
                List selectedItems = listaMultiple.getSelectedValuesList();
                for (Object sel : selectedItems ){
                    countElementos ++;
                }
                if(countElementos > 1){
                    JOptionPane.showMessageDialog(null, "¡¡¡Seleccione solo UN EQUIPO!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                } else {
                    Connection c = conexionConsulta.conectar();
                    try {
                        PreparedStatement consultaObs = c.prepareStatement("SELECT OBS_EQUIPO FROM EQUIPOS WHERE NUM_COMP = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        consultaObs.setString(1, listaMultiple.getSelectedValue()); 
                        ResultSet rs1 = consultaObs.executeQuery();
                        if(rs1.next()){
                            String observacionesReg = rs1.getString("OBS_EQUIPO");
                            String observaciones = JOptionPane.showInputDialog(null, "Indique las observaciones del equipo seleccionado:", "Observaciones", JOptionPane.QUESTION_MESSAGE );                                                        
                            if (observacionesReg .equals("")) {
                                 if(observaciones == ""){
                                 JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un ERROR al tratar de agregar OBSERVACIONES!!! \n Intentelo nuevamente ", "Error", JOptionPane.ERROR_MESSAGE );
                                 } else { 
                                     PreparedStatement registroLog = c.prepareStatement("UPDATE EQUIPOS SET OBS_EQUIPO = ? WHERE NUM_COMP = ? AND ID_LAB= ?  ", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                     registroLog.setString(1, observaciones);
                                     registroLog.setString(2, listaMultiple.getSelectedValue());
                                     registroLog.setString(3, nombre);
                                     int actualizo = registroLog.executeUpdate();
                                     if (actualizo > 0) {
                                         JOptionPane.showMessageDialog(null, "Se actualizo las observaciones del equipo" + listaMultiple.getSelectedValue() + ".Elementos modificados: " + actualizo, "Éxito", JOptionPane.INFORMATION_MESSAGE);
                                         int resp = JOptionPane.showConfirmDialog(null, "¿Desea marcar el equipo como inutilizable?",//<- EL MENSAJE 
                                                 "Alerta!"/*<- El título de la ventana*/, JOptionPane.YES_NO_OPTION/*Las opciones (si o no)*/, JOptionPane.WARNING_MESSAGE/*El tipo de ventana, en este caso WARNING*/);
                                         //System.out.println(resp); // SI = 0, NO = 1
                                         if (resp == 0) { //Si responde si
                                             inutilizarEquipo(0, listaMultiple.getSelectedValue());
                                         }
                                     }
                                 } 
                                
                            } else {
                                if (observaciones == "") {
                                    JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un ERROR al tratar de agregar OBSERVACIONES!!! \n Intentelo nuevamente ", "Error", JOptionPane.ERROR_MESSAGE);
                                } else {
                                    String obsFinal = "";
                                    obsFinal = observacionesReg + ", " + observaciones;
                                    PreparedStatement registroLog = c.prepareStatement("UPDATE EQUIPOS SET OBS_EQUIPO = ? WHERE NUM_COMP = ? AND ID_LAB= ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                    registroLog.setString(1, obsFinal);
                                    registroLog.setString(2, listaMultiple.getSelectedValue());
                                    registroLog.setString(3, nombre);
                                    int actualizo = registroLog.executeUpdate();
                                    if (actualizo > 0) {
                                        JOptionPane.showMessageDialog(null, "Se actualizo las observaciones del equipo" + listaMultiple.getSelectedValue() + ".Elementos modificados: " + actualizo, "Éxito", JOptionPane.INFORMATION_MESSAGE);
                                        int resp = JOptionPane.showConfirmDialog(null, "¿Desea marcar el equipo como inutilizable?",//<- EL MENSAJE 
                                                "Alerta!"/*<- El título de la ventana*/, JOptionPane.YES_NO_OPTION/*Las opciones (si o no)*/, JOptionPane.WARNING_MESSAGE/*El tipo de ventana, en este caso WARNING*/);
                                        //System.out.println(resp); // SI = 0, NO = 1
                                        if (resp == 0) { //Si responde si
                                            inutilizarEquipo(0, listaMultiple.getSelectedValue());
                                        }
                                    }
                                }                                
                            }                            
                        }
                    } catch (SQLException ex){
                         JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un ERROR al tratar de agregar OBSERVACIONES!!! \n Intentelo nuevamente "+ex, "Error", JOptionPane.ERROR_MESSAGE );
                    }finally{
                        conexionConsulta.desconectar();
                    }                                
                }            
            }
        } else {
            Connection c = conexionConsulta.conectar();
            try {
                PreparedStatement consultaObs = c.prepareStatement("SELECT OBS_EQUIPO FROM EQUIPOS WHERE NUM_COMP = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                consultaObs.setString(1, equipo);
                ResultSet rs1 = consultaObs.executeQuery();
                if (rs1.next()) {
                    String observacionesReg = rs1.getString("OBS_EQUIPO");
                    String observaciones = JOptionPane.showInputDialog(null, "Indique las observaciones del equipo seleccionado:", "Observaciones", JOptionPane.QUESTION_MESSAGE);
                    if (observacionesReg == "") {
                        PreparedStatement registroLog = c.prepareStatement("UPDATE EQUIPOS SET OBS_EQUIPO = ? WHERE NUM_COMP = ? AND ID_LAB= ?  ", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        registroLog.setString(1, observaciones);
                        registroLog.setString(2, equipo);
                        registroLog.setString(3, nombre);
                        int actualizo = registroLog.executeUpdate();
                        if (actualizo > 0) {
                            //JOptionPane.showMessageDialog(null, "Se actualizo las observaciones del equipo" + listaMultiple.getSelectedValue() + ".Elementos modificados: " + actualizo, "Éxito", JOptionPane.INFORMATION_MESSAGE);                            
                            
                        }
                    } else {
                        String obsFinal = "";
                        obsFinal = observacionesReg + ", " + observaciones;
                        PreparedStatement registroLog = c.prepareStatement("UPDATE EQUIPOS SET OBS_EQUIPO = ? WHERE NUM_COMP = ? AND ID_LAB= ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        registroLog.setString(1, obsFinal);
                        registroLog.setString(2, equipo);
                        registroLog.setString(3, nombre);
                        int actualizo = registroLog.executeUpdate();
                        if (actualizo > 0) {
                            //JOptionPane.showMessageDialog(null, "Se actualizo las observaciones del equipo" + listaMultiple.getSelectedValue() + ".Elementos modificados: " + actualizo, "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }

                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un ERROR al tratar de agregar OBSERVACIONES!!! \n Intentelo nuevamente " + ex, "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
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

        jScrollPane1 = new javax.swing.JScrollPane();
        listaMultiple = new javax.swing.JList<>();
        textoVentanaListaMultiple = new javax.swing.JLabel();
        enviarLaboratorios = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jScrollPane1.setViewportView(listaMultiple);

        textoVentanaListaMultiple.setText("Seleccione los EQUIPOS a MODIFICAR:");

        enviarLaboratorios.setText("Aceptar");
        enviarLaboratorios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enviarLaboratoriosActionPerformed(evt);
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
                .addGap(106, 106, 106)
                .addComponent(enviarLaboratorios)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(textoVentanaListaMultiple, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(enviarLaboratorios)
                .addGap(10, 10, 10))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        if(utilizar == true){ //UTILIZA
            Obt_EquiposJlist (0);
        } else { //INUTILIZA
            Obt_EquiposJlist (1);
        }
        
    }//GEN-LAST:event_formWindowOpened

    private void enviarLaboratoriosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enviarLaboratoriosActionPerformed
        if(utilizar == true){ //UTILIZA
            inutilizarEquipo(1, "");
            this.setVisible(false);
            this.dispose();
        } else if (obs == false){ //INUTILIZA
            inutilizarEquipo(0, "");
            this.setVisible(false);
            this.dispose();
        } else if(obs == true){
            observacionEquipo("");
        }
        
        this.setVisible(false);
        this.dispose();
        
                    
    }//GEN-LAST:event_enviarLaboratoriosActionPerformed

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
    private javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JList<String> listaMultiple;
    private javax.swing.JLabel textoVentanaListaMultiple;
    // End of variables declaration//GEN-END:variables
}
