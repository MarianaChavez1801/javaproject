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
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import project2.conexionConsulta;

/**
 *
 * @author MarBugaboo
 */
public class CatalogoLaboratorios extends javax.swing.JDialog {

    /**
     * Creates new form CatalogoLaboratorios
     */
    String Usuario = "";
    String nombre = "";
    boolean creado = false;
    boolean eliminar = false;
    boolean nuevo = false;
    ArrayList<String> AusuariosG = new ArrayList<String>();
    
    public CatalogoLaboratorios(javax.swing.JDialog parent, boolean modal, String nombreGrupo, ArrayList Ausuarios) {
        super(parent, modal);
        //Usuario = idUsuario;
        nombre = nombreGrupo;
        creado = false;
        eliminar = false;
        nuevo = true;
        AusuariosG = Ausuarios;
        initComponents();
        setLocationRelativeTo(null);
        
    }
    
    public CatalogoLaboratorios(javax.swing.JDialog parent, boolean modal, String nombreGrupo, boolean grupoCreado) {
        super(parent, modal);
        nombre = nombreGrupo;
        creado = grupoCreado;
        initComponents();
        setLocationRelativeTo(null);
        
    }
    
    public CatalogoLaboratorios(javax.swing.JDialog parent, boolean modal, String nombreGrupo, String idUsuario, boolean grupoCreado, boolean eliminare) {
        super(parent, modal);
        Usuario = idUsuario;
        nombre = nombreGrupo;
        creado = grupoCreado;
        eliminar = eliminare;
        initComponents();
        setLocationRelativeTo(null);
        
    }
    
    public void Obt_LaboratorioJlist (){
        
        
        DefaultListModel laboratorios = new DefaultListModel();
        Connection c = conexionConsulta.conectar();
        try{
            PreparedStatement pstm = c.prepareStatement("SELECT ID_LAB FROM LABORATORIOS ORDER BY ID_LAB", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet res = pstm.executeQuery();
            while(res.next()){
                laboratorios.addElement(res.getString("ID_LAB"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error en la consulta:" + e.getMessage());;
        }
        
        listaMultiple.setModel(laboratorios);
    }
    
    
    
    
    
    public void Obt_LaboratorioSinGrupoJlist (){
        
        
        DefaultListModel laboratorios = new DefaultListModel();
        Connection c = conexionConsulta.conectar();
        try{
            //PreparedStatement pstm = c.prepareStatement("SELECT L.ID_LAB FROM LABORATORIOS L, GRUPOS G WHERE L.ID_LAB <> G.ID_LAB AND G.ID_GRUPO = ? ", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            PreparedStatement pstm = c.prepareStatement("SELECT ID_LAB FROM GRUPOS WHERE ID_GRUPO = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            pstm.setString(1, nombre);
            System.out.println(nombre);
            PreparedStatement pstm2 = c.prepareStatement("SELECT ID_USUARIO FROM GRUPOS WHERE ID_GRUPO = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            pstm2.setString(1, nombre);
            ResultSet rs = pstm2.executeQuery();
            if(rs.next()){
                Usuario = rs.getString("ID_USUARIO");
            } else{
                JOptionPane.showMessageDialog(null, "¡¡¡No se pudo obtener el USUARIO!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
            }
            ResultSet res = pstm.executeQuery();
            while(res.next()){ 
                String idLab = res.getString("ID_LAB"); //L01
                PreparedStatement verificarStmt = c.prepareStatement("SELECT ID_LAB FROM LABORATORIOS WHERE ID_LAB <> ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                verificarStmt.setString(1, idLab);
                ResultSet res2 = verificarStmt.executeQuery();
                while(res2.next()){ //LD LX
                    
                    String idLab2 = res2.getString("ID_LAB");
                    System.out.println(idLab2);
                    PreparedStatement verificarStmt2 = c.prepareStatement("SELECT ID_LAB FROM GRUPOS WHERE ID_LAB = ? AND ID_GRUPO = ? ", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    verificarStmt2.setString(1, idLab2);
                    verificarStmt2.setString(2, nombre);
                    ResultSet res3 = verificarStmt2.executeQuery();
                    if(res3.next()){
                        System.out.println("NO se agrego este elemento"+idLab2);
                    } else {
                        if(laboratorios.contains(idLab2)){
                            System.out.println("Este elemento ya existia"+idLab2);
                        }else{
                        laboratorios.addElement(idLab2);
                        }
                    }
                }
                
            }
           if(laboratorios.size() == 0){
                JOptionPane.showMessageDialog(null, "¡¡¡No se encontro ningun LABORATORIO disponible!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                this.setVisible(false);
                this.dispose();
            }
        } catch (SQLException e) {
            System.err.println("Error en la consulta:" + e.getMessage());
            this.setVisible(false);
            this.dispose();
        }finally{
            conexionConsulta.desconectar();
            listaMultiple.setModel(laboratorios);
        }
    }
    public void Obt_LaboratoriosAsignadosJlist(){
        DefaultListModel laboratorios = new DefaultListModel();
        Connection c = conexionConsulta.conectar();
        try{
            PreparedStatement pstm = c.prepareStatement("SELECT ID_LAB FROM GRUPOS WHERE ID_GRUPO = ? ORDER BY ID_LAB", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            pstm.setString(1, nombre);
            ResultSet res = pstm.executeQuery();
            while(res.next()){
                laboratorios.addElement(res.getString("ID_LAB"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error en la consulta:" + e.getMessage());;
        }
        
        listaMultiple.setModel(laboratorios);
    }
    
    public boolean eliminarLaboratoriosSeleccionados() throws SQLException{
         boolean eliminar = false; 
         int elementosActualizadosGrupos = 0;
         if(listaMultiple.getSelectedValue().equals("")) {
            JOptionPane.showMessageDialog(null, "¡¡¡Porfavor seleccione al menos un elemento", "Error", JOptionPane.ERROR_MESSAGE );
            return eliminar= false;
        } else {
            List selectedItems = listaMultiple.getSelectedValuesList();
            System.out.println(selectedItems);
            for (Object sel : selectedItems ){
                String idLaboratorio = sel.toString();
                System.out.println(idLaboratorio);
               try{
                    Connection c = conexionConsulta.conectar();
                    PreparedStatement actualizarStmt2 = c.prepareStatement("DELETE FROM GRUPOS WHERE ID_GRUPO = ? AND ID_LAB = ?");
                    actualizarStmt2.setString(1, nombre);
                    actualizarStmt2.setString(2, idLaboratorio);
                    elementosActualizadosGrupos = elementosActualizadosGrupos + actualizarStmt2.executeUpdate();
                    actualizarStmt2.close();
                    eliminar = true;
                } catch (SQLException e){
                    JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de ACTUALIZAR los datos!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                    return eliminar = false;
                } finally {
                   conexionConsulta.desconectar();
               }
            }
            //
            if(elementosActualizadosGrupos >= 1){
                JOptionPane.showMessageDialog(null, "El GRUPO se ha ACTUALIZADO con exito\nElementos ACTUALIZADOS en total"+elementosActualizadosGrupos, "Éxito", JOptionPane.INFORMATION_MESSAGE );
                this.setVisible(false);
                this.dispose();
            } else{
                //NO SE ACTUALizo NADA
                JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un ERROR al tratar de ACTUALIZAR!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                eliminar = false;
            }
            
            this.setVisible(false);
            this.dispose();
            return eliminar;
        }
    }
    
    public boolean registroGruposSeleccionados() throws SQLException{
        System.err.println("ENTRE A GRUPOS SELECCION");
        boolean exito = false; 
        int grupoCreado = 0;
        if(listaMultiple.getSelectedValue().equals("")) {
            JOptionPane.showMessageDialog(null, "¡¡¡Porfavor seleccione al menos un elemento", "Error", JOptionPane.ERROR_MESSAGE );
            return exito = false;
        } else {
            List selectedItems = listaMultiple.getSelectedValuesList();
            System.out.println(selectedItems);
            System.err.println("ESTOS SON LOS ELEMENTOS SELECCIONADOS: "+ selectedItems.size());
            int entradas = 0;
            for (Object sel : selectedItems ){
                entradas ++;
                String idLaboratorio = sel.toString();
                System.out.println(idLaboratorio);
                Connection c = conexionConsulta.conectar();
               try{
                    //System.err.println("ESTE ES EL NOMBRE A INSERTAR"+nombre);
                    //System.err.println("ESTE ES EL LAB A INSERTAR"+idLaboratorio);
                    //System.err.println("ESTE ES EL USUARIO A INSERTAR"+Usuario);
                    PreparedStatement pstm = c.prepareStatement("SELECT ID_LAB FROM GRUPOS WHERE ID_GRUPO = ? AND ID_LAB =?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    pstm.setString(1, nombre);
                    pstm.setString(2, idLaboratorio);
                    ResultSet res = pstm.executeQuery();
                    if(res.next()){
                         //ESTE GRUPO CON ESTE LAB YA EXISTE
                         System.err.println("Este lab ya existia:" +idLaboratorio);
                    } else{
                        PreparedStatement guardarStmt2 = c.prepareStatement("INSERT INTO GRUPOS(ID_GRUPO, ID_LAB, ID_USUARIO ) VALUES (?, ?, ? )");
                        guardarStmt2.setString(1, nombre);
                        guardarStmt2.setString(2, idLaboratorio);
                        guardarStmt2.setString(3, Usuario);
                        //guardarStmt2.execute();
                        grupoCreado = guardarStmt2.executeUpdate();
                        System.out.println(grupoCreado);
                        if(grupoCreado >= 1){   
                            guardarStmt2.close();
                            exito = true;
                            //return exito;
                        } else {
                            JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de insertar los datos EN GRUPOS!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                            return exito = false;
                        }
                    }
                } catch (SQLException e){
                    JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de insertar los datos!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                    System.err.println("Error en la consulta:" + e.getMessage());
                    return exito;
                } finally {
                   conexionConsulta.desconectar();
               }
            } 
            //exito = false;
            JOptionPane.showMessageDialog(null, "El GRUPO se ha CREADO con exito\nElementos CREADOS en total"+entradas, "Éxito", JOptionPane.INFORMATION_MESSAGE );
            this.setVisible(false);
            this.dispose();
            return exito; 
        }
        
    }
    
    public boolean registroGrupo() throws SQLException{
        System.err.println("ENTRE A GRUPOS NUEVO");
        boolean exito = false; 
        int grupoCreado = 0; 
        if(listaMultiple.getSelectedValue().equals("")) {
            JOptionPane.showMessageDialog(null, "¡¡¡Porfavor seleccione al menos un elemento", "Error", JOptionPane.ERROR_MESSAGE );
            return eliminar= false;
        } else {
            Connection c = conexionConsulta.conectar();
            
            //System.out.println(selectedItems);
                try{
                    PreparedStatement guardarStmt = c.prepareStatement("INSERT INTO GRUPOS(NOM_GRUPO) VALUES (?)");
                    guardarStmt.setString(1, nombre);
                    grupoCreado= guardarStmt.executeUpdate();
                    if(grupoCreado >= 1){
                        PreparedStatement verificarStmt = c.prepareStatement("SELECT ID_GRUPO FROM GRUPOS WHERE NOM_GRUPO = ?");
                        verificarStmt.setString(1, nombre);                            
                        ResultSet res = verificarStmt.executeQuery();
                        if(res.next()){
                            String idGrupo = res.getString("ID_GRUPO");            
                            int count = AusuariosG.size();
                            System.err.println(AusuariosG.size());
                            grupoCreado = 0;
                            while(count >= 0){ //Por cada usuario mandado
                                List selectedItems = listaMultiple.getSelectedValuesList();
                                for (Object sel : selectedItems ){
                                    String idLaboratorio = sel.toString();
                                    System.out.println(idLaboratorio);                                                                            
                                    PreparedStatement guardarStmt2 = c.prepareStatement("INSERT INTO GRUPOLABORATORIO(ID_GRUPO, ID_LAB, ID_USUARIO ) VALUES (?, ?, ? )");
                                    guardarStmt2.setString(1, idGrupo);
                                    guardarStmt2.setString(2, idLaboratorio);
                                    guardarStmt2.setString(3, AusuariosG.get(count - 1));
                                    grupoCreado = grupoCreado + guardarStmt2.executeUpdate();
                                    System.out.println(grupoCreado);
                                }
                                count--;
                            }
                        } else {
                            //No encontre el grupo recien creado
                            JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de insertar los datos, no se encontro el GRUPO en la tabla GRUPO!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                        }
                    } else {
                        //No se creo grupo
                        JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de insertar los datos, no se CREO el GRUPO en la tabla GRUPO!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                    }
                    
                 } catch (SQLException e){
                     JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de insertar los datos!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                     System.err.println("Error en la consulta:" + e.getMessage());
                     return exito;
                 } finally {
                    if(grupoCreado > 0){
                        JOptionPane.showMessageDialog(null, "El GRUPO se ha CREADO con exito\nElementos CREADOS en total"+grupoCreado, "Éxito", JOptionPane.INFORMATION_MESSAGE );
                        this.setVisible(false);
                        this.dispose();
                        exito = true;
                        return exito; 
                    }
                    conexionConsulta.desconectar();
                }
        }
        return exito;
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

        textoVentanaListaMultiple.setText("Seleccione los LABORATORIOS para este GRUPO:");

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
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(textoVentanaListaMultiple, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(96, 96, 96)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(enviarLaboratorios)
                .addGap(16, 16, 16))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here
        System.err.println("VENTANA ABIERTA!!!!!!!!!!!!!");
        if(creado == false){
            Obt_LaboratorioJlist();
        } else {
            if(eliminar == false){
                if(creado == true){
                    Obt_LaboratorioSinGrupoJlist();
                }
            } else {
                Obt_LaboratoriosAsignadosJlist();
            }
        }
        
    }//GEN-LAST:event_formWindowOpened

    private void enviarLaboratoriosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enviarLaboratoriosActionPerformed
        try {
            // TODO add your handling code here:
            if(creado == true){
                System.err.println("ENTRE A CREADO"); //El grupo ya viene creado
                if(eliminar == true){
                    System.err.println("ENTRE A ELIMINAR"); // El grupo se va a eliminar
                    boolean eliminar1 = eliminarLaboratoriosSeleccionados();
                    if(eliminar1 == true){
                        //JOptionPane.showMessageDialog(null, "El grupo ha sido ACTUALIZADO con exito", "Éxito", JOptionPane.INFORMATION_MESSAGE );                          
                    } else {
                        JOptionPane.showMessageDialog(null, "¡¡¡*Ocurrio un error al tratar de ACTUALIZAR los datos*!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                    }
                } else{
                    System.err.println("ENTRE A CREADO NO ELIMINAR"); //El grupo ya esta creado pero no se va a eliminar
                    boolean exito1 = registroGruposSeleccionados();
                    if(exito1 == true){
                        JOptionPane.showMessageDialog(null, "El grupo ha sido registrado con exito", "Éxito", JOptionPane.INFORMATION_MESSAGE ); 
                    }else {
                    JOptionPane.showMessageDialog(null, "¡¡¡**Ocurrio un error al tratar de insertar los datos**!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                    }
                }
                
            } else {
                if(nuevo == true){
                    System.err.println("ENTRE A NUEVO"); // Se registrara un grupo NUEVO
                    boolean exito1 = registroGrupo();
                    if(exito1 == true){
                        JOptionPane.showMessageDialog(null, "El grupo ha sido registrado con exito", "Éxito", JOptionPane.INFORMATION_MESSAGE ); 
                    }else {
                        JOptionPane.showMessageDialog(null, "¡¡¡***Ocurrio un error al tratar de insertar los datos***!!! El proceso de INSERTAR NO SE PUDO REALIZAR \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                    }
                }
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(CatalogoLaboratorios.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_enviarLaboratoriosActionPerformed

    /**
     * @param args the command line arguments
     
    public static void main(String args[]) {
        /* Set the Nimbus look and feel 
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
            java.util.logging.Logger.getLogger(CatalogoLaboratorios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CatalogoLaboratorios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CatalogoLaboratorios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CatalogoLaboratorios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog 
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                CatalogoLaboratorios dialog = new CatalogoLaboratorios(new javax.swing.JFrame(), true);
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
