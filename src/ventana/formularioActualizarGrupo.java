/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package ventana;

import java.awt.Component;
import java.awt.Dimension;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataListener;
import project2.conexionConsulta;
import static ventana.CatalogoLaboratorios.listaMultiple;

/**
 *
 * @author MarBugaboo
 */
public class formularioActualizarGrupo extends javax.swing.JDialog {

    /**
     * Creates new form formularioActualizarGrupo
     */
    public formularioActualizarGrupo(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setLocationRelativeTo(null);
        initComponents();
    }
    
    static String idGrupo;
    String nombreActual = "";
    String usuarioActual = "";
    String labActual = "";
    String idGrupoActual = "";
    
    public void Obt_RolesDisponibles(String usuarioActual){
        try{
            cUsuariosDisponibles.removeAllItems();
            int index = -1;
            Connection c = conexionConsulta.conectar();
            PreparedStatement pstm = c.prepareStatement("SELECT R.NAME_USUARIO FROM USUARIOS R LEFT JOIN  GRUPOLABORATORIO G ON R.ID_USUARIO = G.ID_USUARIO WHERE G.ID_USUARIO IS NULL", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet res = pstm.executeQuery();
            while(res.next()){
                index ++;
                cUsuariosDisponibles.addItem(res.getString("NAME_USUARIO")); 
            }
            
            PreparedStatement verificarStmt2 = c.prepareStatement("SELECT NAME_USUARIO FROM USUARIOS WHERE ID_USUARIO = ? ", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            verificarStmt2.setString(1, usuarioActual); 
            ResultSet rs2 = verificarStmt2.executeQuery();
            if(rs2.next()){
                index ++;
                cUsuariosDisponibles.addItem(rs2.getString("NAME_USUARIO"));
                cUsuariosDisponibles.setSelectedIndex(index);
            } else {
                JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un ERROR al tratar de OBTENER USUARIOS DISPONIBLES, no se pudo encontrar el USUARIO de este grupo!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );   
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un ERROR al tratar de OBTENER USUARIOS DISPONIBLES!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );   
            System.err.println("Error en la consulta:" + e.getMessage());
        } finally{
            conexionConsulta.desconectar(); 
        }
    }
    
    public void Obt_GruposDisponibles(){
        try{
            Connection c = conexionConsulta.conectar();
            PreparedStatement pstm = c.prepareStatement("SELECT NOM_GRUPO, ID_GRUPO FROM GRUPOS ORDER BY NOM_GRUPO", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet res = pstm.executeQuery();
            while(res.next()){
                cGruposDisponibles.addItem(res.getString("NOM_GRUPO"));
            }
        } catch (SQLException e) {
            System.err.println("Error en la consulta:" + e.getMessage());;
        } finally{
            conexionConsulta.desconectar();
        }
    }
    
    public void Obt_LaboratorioJlist (String nombre){
        
        DefaultListModel laboratorios = new DefaultListModel();
        Connection c = conexionConsulta.conectar();
        //int elementoSeleccion = -1;
        //int[] indices = new int[50];
        try{
            PreparedStatement pstm = c.prepareStatement("SELECT ID_LAB FROM GRUPOLABORATORIO WHERE ID_GRUPO = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            pstm.setString(1, nombre); 
            ResultSet res = pstm.executeQuery();
            int n = -1;
            while(res.next()){
                laboratorios.addElement(res.getString("ID_LAB"));
                /*String idLabAgregado =  res.getString("ID_LAB");
                elementoSeleccion = elementoSeleccion +1;
                PreparedStatement verificarStmt = c.prepareStatement("SELECT ID_LAB FROM GRUPOS WHERE ID_GRUPO = ? AND ID_LAB = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                verificarStmt.setString(1, nombre); 
                verificarStmt.setString(2, idLabAgregado);
                ResultSet rs = verificarStmt.executeQuery();
                if(rs.next()){
                    n++;
                    indices[n] = elementoSeleccion;
                    System.out.println("n: "+n);
                    System.out.println("Seleccion: "+elementoSeleccion);
                 }*/
            }
            
            listaLaboratoriosRegistrados.setModel(laboratorios);
            //listaLaboratoriosNoRegistrados.setSelectedIndices(indices);

        } catch (SQLException e) {
            System.err.println("Error en la consulta:" + e.getMessage());
        } finally{
            conexionConsulta.desconectar();
        }
    }
    
    public void Obt_TodosLaboratoriosJlist (){
        
        DefaultListModel laboratorios = new DefaultListModel();
        Connection c = conexionConsulta.conectar();
        try{
            PreparedStatement pstm = c.prepareStatement("SELECT ID_LAB FROM LABORATORIOS", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet res = pstm.executeQuery();
            while(res.next()){
                laboratorios.addElement(res.getString("ID_LAB"));
            }
            listaLaboratoriosNoRegistrados.setModel(laboratorios);
        } catch (SQLException e) {
            System.err.println("Error en la consulta:" + e.getMessage());
        } finally{
            conexionConsulta.desconectar();
        }
    }
    
    public void Obt_LaboratorioQueNoSonGrupoJlist (){
        try{
            DefaultListModel model = (DefaultListModel) listaLaboratoriosRegistrados.getModel(); //LABS DE ESTE GRUPO
            DefaultListModel model2 = (DefaultListModel) listaLaboratoriosNoRegistrados.getModel(); //TODOS LOS LABS
            //Component[] tamaño = listaLaboratoriosNoRegistrados.getComponents();
            int tamaño = model2.getSize();
            if(tamaño >= 0){
                int[] indices = new int[50];
                int n = 0;
                while(tamaño >= 0){
                    n++;
                    indices[n] = n;
                    tamaño--;
                }
                listaLaboratoriosNoRegistrados.setSelectedIndices(indices);
                List selectedItems = listaLaboratoriosNoRegistrados.getSelectedValuesList();
                listaLaboratoriosNoRegistrados.clearSelection();
                for (Object sel2 : selectedItems ){
                        System.err.println(sel2.toString());
                    if(model.contains(sel2)){ //Si este grupo tiene este laboratorio
                        System.err.println("ESTE ELEMENTO SI LO CONTIENE"+sel2.toString());
                        model2.removeElement(sel2); //Lo elimina de los no registrados porque si esta registrado
                    } else{
                        System.err.println("ESTE ELEMENTO no LO CONTIENE"+sel2.toString());
                    }
                }
            } else {
                System.err.println("Este JList esta vacio");
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Excepcion "+e+"\nSu descripcion es "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } 
    }
    
    public void remove (){
        DefaultListModel model = (DefaultListModel) listaLaboratoriosNoRegistrados.getModel();
        DefaultListModel model2 = (DefaultListModel) listaLaboratoriosRegistrados.getModel();
        System.out.println("Este es el tamaño: "+model2.size());
       if(model.getSize() <= 0){
            List selectedItems = listaLaboratoriosRegistrados.getSelectedValuesList();
            for (Object sel : selectedItems ){
                model.addElement(sel);
                model2.removeElement(sel);
            }
        } else {
           List selectedItems = listaLaboratoriosRegistrados.getSelectedValuesList();
            System.out.println(selectedItems);
            for (Object sel : selectedItems ){
                model.add(WIDTH, sel);
                model2.removeElement(sel);
            }
       }
          
    }
    
    public void add (){
        DefaultListModel model = (DefaultListModel) listaLaboratoriosRegistrados.getModel();
        DefaultListModel model2 = (DefaultListModel) listaLaboratoriosNoRegistrados.getModel();
        System.out.println("Este es el tamaño: "+model.size());
        if(model.getSize() <= 0){
            List selectedItems = listaLaboratoriosNoRegistrados.getSelectedValuesList();
            for (Object sel : selectedItems ){
                model.addElement(sel);
                model2.removeElement(sel);
            }
        } else {
            List selectedItems = listaLaboratoriosNoRegistrados.getSelectedValuesList();
            System.out.println(selectedItems);
            for (Object sel : selectedItems ){
                model.add(WIDTH, sel);
                model2.removeElement(sel);
            }
        }
    }
    
    public void obtenerDatosGrupo(){
        try{
            Connection c = conexionConsulta.conectar();
            nombreActual = cGruposDisponibles.getSelectedItem().toString();
            PreparedStatement verificarStmt1 = c.prepareStatement("SELECT ID_GRUPO FROM GRUPOS WHERE NOM_GRUPO = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            verificarStmt1.setString(1, nombreActual); //Verificando que el grupo exista
            ResultSet rs1 = verificarStmt1.executeQuery();
            if(rs1.next()){
                idGrupoActual = rs1.getString("ID_GRUPO");
                PreparedStatement verificarStmt = c.prepareStatement("SELECT ID_USUARIO, ID_LAB FROM GRUPOLABORATORIO WHERE ID_GRUPO = ? ", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                verificarStmt.setString(1, idGrupoActual); //Verificando que el grupo exista
                ResultSet rs = verificarStmt.executeQuery();
                if(rs.next()){
                    usuarioActual = rs.getString("ID_USUARIO");
                    labActual = rs.getString("ID_LAB");
                    fNombreGrupo.setText(nombreActual);
                    Obt_RolesDisponibles(usuarioActual);
                    Obt_LaboratorioJlist (idGrupoActual);
                    Obt_TodosLaboratoriosJlist ();
                    Obt_LaboratorioQueNoSonGrupoJlist ();
                } else {
                    JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un ERROR al tratar de OBTENER LOS DATOS de este grupo!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );   
                }
            }
        } catch (SQLException ex){
            JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un ERROR al tratar de OBTENER LOS DATOS de este grupo!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );   
        } finally{
            conexionConsulta.desconectar();
        }
    }
    
    public void actualizarGrupo() {
        
        int grupoCreado = 0;
        int elementosActualizadosGrupos = 0;
        String nombreGrupoNuevo = fNombreGrupo.getText(); 
        String idUsuarioNuevo = cUsuariosDisponibles.getSelectedItem().toString();
        DefaultListModel registrados = (DefaultListModel) listaLaboratoriosRegistrados.getModel();
        DefaultListModel noRegistrados = (DefaultListModel) listaLaboratoriosNoRegistrados.getModel();        
        if(registrados.getSize() <= 0){ //SI ES 0 ENTONCES ESTA VACIO
            JOptionPane.showMessageDialog(null, "¡¡¡NO se puede dejar el grupo sin LABORATORIOS!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
        } else {
            int tamaño = registrados.getSize();
            if(tamaño >= 0){
                int[] indices = new int[50];
                int n = 0;
                while(tamaño >= 0){
                    n++;
                    indices[n] = n;
                    tamaño--;
                }
                listaLaboratoriosRegistrados.setSelectedIndices(indices);
                List selectedItems = listaLaboratoriosRegistrados.getSelectedValuesList();
                listaLaboratoriosRegistrados.clearSelection();
                for (Object sel2 : selectedItems ){
                System.err.println(sel2.toString());
                try{
                    Connection c = conexionConsulta.conectar();
                    PreparedStatement verificarStmt = c.prepareStatement("SELECT ID_LAB FROM GRUPOLABORATORIO WHERE ID_LAB = ? AND ID_GRUPO = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    verificarStmt.setString(1, sel2.toString()); //Verificando que el grupo exista
                    verificarStmt.setString(2, idGrupoActual); 
                    ResultSet rs = verificarStmt.executeQuery();
                    if(rs.next()){ // Si ya existe
                            //NO HACE NADA
                    } else { // Si no existe lo agrega
                        PreparedStatement guardarStmt2 = c.prepareStatement("INSERT INTO GRUPOLABORATORIO(ID_GRUPO, ID_LAB, ID_USUARIO ) VALUES (?, ?, ? )");
                        guardarStmt2.setString(1, idGrupoActual);
                        guardarStmt2.setString(2, sel2.toString());
                        guardarStmt2.setString(3, usuarioActual);
                        //guardarStmt2.execute();
                        grupoCreado = guardarStmt2.executeUpdate();
                        System.out.println(grupoCreado);
                        if(grupoCreado >= 1){   
                            grupoCreado ++;
                            guardarStmt2.close();                                
                        } else {
                            JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de ACTUALIZAR los LABORATORIOS EN GRUPOS!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                        }
                    }
                }catch (SQLException ex){
                    JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un ERROR al tratar de ACTUALIZAR LABORATORIOS *add*!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                } finally{
                    conexionConsulta.desconectar();
                }
            }    
        } //Fin registrados
        
        int tamaño2 = noRegistrados.getSize();
        if(tamaño2 >= 0){
            int[] indices2 = new int[50];
            int n2 = 0;
            while(tamaño2 >= 0){
                n2++;
                indices2[n2] = n2;
                tamaño2--;
            }
            listaLaboratoriosNoRegistrados.setSelectedIndices(indices2);
            List selectedItems2 = listaLaboratoriosNoRegistrados.getSelectedValuesList();
            listaLaboratoriosNoRegistrados.clearSelection();
            for (Object sel3 : selectedItems2 ){
                System.err.println(sel3.toString());
                try{
                    Connection c = conexionConsulta.conectar();
                    PreparedStatement verificarStmt = c.prepareStatement("SELECT ID_LAB FROM GRUPOLABORATORIO WHERE ID_LAB = ? AND ID_GRUPO = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    verificarStmt.setString(1, sel3.toString()); //Verificando que el grupo exista
                    verificarStmt.setString(2, idGrupoActual); 
                    ResultSet rs = verificarStmt.executeQuery();
                    if(rs.next()){ // Si ya existe
                        //LO ELIMINA
                        PreparedStatement actualizarStmt2 = c.prepareStatement("DELETE FROM GRUPOLABORATORIO WHERE ID_GRUPO = ? AND ID_LAB = ?");
                        actualizarStmt2.setString(1, idGrupoActual);
                        actualizarStmt2.setString(2, sel3.toString());
                        elementosActualizadosGrupos = elementosActualizadosGrupos + actualizarStmt2.executeUpdate();
                        actualizarStmt2.close();
                    }
                }catch (SQLException ex){
                     JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un ERROR al tratar de ACTUALIZAR LABORATORIOS *remove*!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                }finally{
                    conexionConsulta.desconectar();
                }
            }
        } else {
            //No hay por eliminar
        }   
    } //Fin actualizacion de LABORATORIOS
        
        //ACTUALIZACION DE USUARIO    
        if(idUsuarioNuevo.equals(usuarioActual)){
            //NO SE CAMBIO EL USUARIO
            System.err.println("EL USUARIO ES IGUAL AL QUE YA TENIA");
        } else{
            try{
                Connection c = conexionConsulta.conectar();
                PreparedStatement verificarStmt = c.prepareStatement("SELECT ID_USUARIO FROM GRUPOLABORATORIO WHERE ID_GRUPO = ? ", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                verificarStmt.setString(1, idGrupoActual); //Verificando que el grupo exista
                ResultSet rs = verificarStmt.executeQuery();
                if(rs.next()){ //Si existe el grupo con este usuario
                    PreparedStatement verificarStmt2 = c.prepareStatement("SELECT ID_USUARIO FROM USUARIOS WHERE NAME_USUARIO = ? ", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    verificarStmt2.setString(1, idUsuarioNuevo);//Verificando que el usuario nuevo exista
                    ResultSet rs2 = verificarStmt2.executeQuery();
                    if(rs2.next()){ //SI existe el usuario
                        idUsuarioNuevo = rs2.getString("ID_USUARIO");
                        PreparedStatement actualizarStmt2 = c.prepareStatement("UPDATE GRUPOLABORATORIO SET ID_USUARIO = ? WHERE ID_USUARIO = ? AND ID_GRUPO = ? ", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        actualizarStmt2.setString(2, usuarioActual);
                        actualizarStmt2.setString(1, idUsuarioNuevo);
                        actualizarStmt2.setString(3, idGrupoActual);
                        elementosActualizadosGrupos = actualizarStmt2.executeUpdate();
                        if(elementosActualizadosGrupos >= 1){
                            JOptionPane.showMessageDialog(null, "El USUARIO de GRUPO se ha ACTUALIZADO con exito\nElementos ACTUALIZADOS en total", "Éxito", JOptionPane.INFORMATION_MESSAGE );
                            this.setVisible(false);
                            this.dispose();
                        } else{
                            //NO SE ACTUALIZO NADA
                            JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un ERROR al tratar de ACTUALIZAR (USUARIO)!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                        }
                    } 
                }else{
                   JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un ERROR al tratar de ACTUALIZAR USUARIO, ESTE GRUPO O USUARIO NO EXISTE!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE ); 
                }
            } catch (SQLException ex){
                JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un ERROR al tratar de ACTUALIZAR USUARIO!!! \n Intentelo nuevamente\n MENSAJE:\n" +ex.getMessage()+"CODIGO SQLSTATE"+ex.getSQLState()+"\nCODIGO ERROR"+ex.getErrorCode(), "Error", JOptionPane.ERROR_MESSAGE );
            } finally{
                conexionConsulta.desconectar();
            }
        }  
        
        try{
            //ACTUALIZACION DE NOMBRE GRUPO
            if(nombreGrupoNuevo.equals(nombreActual)){
                //No se cambio nombre de grupo
            }else{    
                Connection c = conexionConsulta.conectar();
                PreparedStatement verificarStmt = c.prepareStatement("SELECT NOM_GRUPO FROM GRUPOS WHERE NOM_GRUPO = ? ", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                verificarStmt.setString(1, nombreGrupoNuevo); //Verificando que el grupo no exista
                ResultSet rs = verificarStmt.executeQuery();
                if(rs.next()){ //Si existe
                    JOptionPane.showMessageDialog(null, "¡¡¡Este grupo ya fue creado, para agregar mas porfavor hagalo en agregar a grupo !!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                }else{
                    PreparedStatement actualizarStmt = c.prepareStatement("UPDATE GRUPOS SET NOM_GRUPO =? WHERE ID_GRUPO = ? ", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    actualizarStmt.setString(1, nombreGrupoNuevo); 
                    actualizarStmt.setString(2, idGrupoActual);
                    int elementosActualizadosNomGrupos = actualizarStmt.executeUpdate();
                    if(elementosActualizadosGrupos >= 1){
                        JOptionPane.showMessageDialog(null, "El NOMBRE de GRUPO se ha ACTUALIZADO con exito\nElementos ACTUALIZADOS en total"+elementosActualizadosNomGrupos, "Éxito", JOptionPane.INFORMATION_MESSAGE );
                        this.setVisible(false);
                        this.dispose();
                    } else{
                        JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un ERROR al tratar de ACTUALIZAR NOMBRE GRUPO!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                    }
                }
            }
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un ERROR al tratar de ACTUALIZAR NOMBRE GRUPO!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
        } finally{
            conexionConsulta.desconectar();
        }
        
        if(grupoCreado >= 1){
            JOptionPane.showMessageDialog(null, "Los LABORATORIOS AGREGADOS A GRUPO se han ACTUALIZADO con exito\nElementos ACTUALIZADOS en total: "+grupoCreado, "Éxito", JOptionPane.INFORMATION_MESSAGE );
        }
        
        if(grupoCreado >= 1){
            JOptionPane.showMessageDialog(null, "Los LABORATORIOS ELIMINADOS DE GRUPO se han ACTUALIZADO con exito\nElementos ACTUALIZADOS en total: "+elementosActualizadosGrupos, "Éxito", JOptionPane.INFORMATION_MESSAGE );
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        fNombreGrupo = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listaLaboratoriosRegistrados = new javax.swing.JList<>();
        cUsuariosDisponibles = new javax.swing.JComboBox<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        listaLaboratoriosNoRegistrados = new javax.swing.JList<>();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        cGruposDisponibles = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jLabel1.setText("Grupo Seleccionado:");

        fNombreGrupo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fNombreGrupoActionPerformed(evt);
            }
        });

        jLabel2.setText("Usuario de Grupo:");

        jLabel3.setText("A continuacion se muestran los laboratorios dentro de este grupo, si desea agregar o eliminar laboratorios");

        jLabel4.setText("porfavor, seleccione los laboratorios que perteneceran a este grupo:");

        jScrollPane1.setViewportView(listaLaboratoriosRegistrados);

        cUsuariosDisponibles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cUsuariosDisponiblesActionPerformed(evt);
            }
        });

        jScrollPane3.setViewportView(listaLaboratoriosNoRegistrados);

        jButton1.setText("Remove >>");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("<<Add");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel5.setText("Registrados:");

        jLabel6.setText("No registrados:");

        jButton3.setText("Actualizar");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Cancelar");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        cGruposDisponibles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cGruposDisponiblesActionPerformed(evt);
            }
        });

        jLabel7.setText("Nombre Grupo:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(185, 185, 185)
                        .addComponent(jButton3)
                        .addGap(31, 31, 31)
                        .addComponent(jButton4))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE))
                        .addGap(50, 50, 50)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(fNombreGrupo, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cUsuariosDisponibles, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cGruposDisponibles, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton1)
                            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(14, 14, 14)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 428, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(201, 201, 201)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cGruposDisponibles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fNombreGrupo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(cUsuariosDisponibles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(82, 82, 82)
                        .addComponent(jButton2)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 201, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(7, 7, 7)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jButton4)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        //String ListaLaboratorio[] = adminEquipos.Obt_Grupos();
        //Object lab = JOptionPane.showInputDialog(null, "Seleccione el GRUPO que se modificara", "Mostrar GRUPOS", JOptionPane.QUESTION_MESSAGE, null, ListaLaboratorio, "01" );
        //idGrupo = lab.toString();
        Obt_GruposDisponibles();
    }//GEN-LAST:event_formWindowOpened

    private void fNombreGrupoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fNombreGrupoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fNombreGrupoActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        remove();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        add();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        actualizarGrupo();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void cUsuariosDisponiblesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cUsuariosDisponiblesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cUsuariosDisponiblesActionPerformed

    private void cGruposDisponiblesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cGruposDisponiblesActionPerformed
        // TODO add your handling code here:
        System.err.println("ELEMENTO SELECCIONADO");
        
        obtenerDatosGrupo();
        
    }//GEN-LAST:event_cGruposDisponiblesActionPerformed

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
            java.util.logging.Logger.getLogger(formularioActualizarGrupo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(formularioActualizarGrupo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(formularioActualizarGrupo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(formularioActualizarGrupo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog 
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                formularioActualizarGrupo dialog = new formularioActualizarGrupo(new javax.swing.JFrame(), true);
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
    private javax.swing.JComboBox<String> cGruposDisponibles;
    public javax.swing.JComboBox<String> cUsuariosDisponibles;
    public javax.swing.JTextField fNombreGrupo;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    public javax.swing.JList<String> listaLaboratoriosNoRegistrados;
    public javax.swing.JList<String> listaLaboratoriosRegistrados;
    // End of variables declaration//GEN-END:variables
}
