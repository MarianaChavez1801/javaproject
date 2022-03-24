/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ventana;

import com.toedter.calendar.JDateChooser;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import project2.conexionConsulta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.UIManager;

/**
 *
 * @author MarBugaboo
 */
public class adminEquipos extends javax.swing.JFrame {

    /**
     * Creates new form adminEquipos
     */
    
    ResultSet rs;
    public String usuario;
    
    public adminEquipos() {
        cerrar();
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Imposible modificar el tema visual", "LookAndFeel inválido", JOptionPane.ERROR_MESSAGE);
        }
        initComponents(); 
        setLocationRelativeTo(null); //para colocar el jframe en el centro de la pantalla   
        this.setExtendedState(6);
    }
    
    public void EnviarTexto(String string){
       textArea.append(string + "\n");
    }//fin EnviarTexto
    
    public String guardarCarrito() throws SQLException{
       
        
        //Pregunta el nombre de la persona a la cual corresponde dicha huella
        String nombreCarrito = JOptionPane.showInputDialog("Escriba el Nombre del LABORATORIO a AGREGAR: ").toUpperCase() ; //Este es el nombre completo del laboratorio
            String nombreCorto = JOptionPane.showInputDialog("Escriba el Nombre corto para el LABORATORIO").toUpperCase();//Este es el nombre corto del laboratorio/ ID_LAB
            if((nombreCarrito != null) && (nombreCorto != null)){
                if (nombreCarrito.length() <=5){
                try{
                    //Establece los valores para la sentencia SQL
                    Connection c = conexionConsulta.conectar();                             
                    PreparedStatement guardarStmt = c.prepareStatement("INSERT INTO LABORATORIOS(ID_LAB, NOMBRE_LAB) VALUES (?, ? )"); //Se crea el laboratorio en la tabla laboratorios
                    guardarStmt.setString(1, nombreCorto); // Identificador del laboratorio
                    guardarStmt.setString(2, nombreCarrito); //nombre del laboratorio
                    //Ejecuta la sentencia preparada
                    guardarStmt.execute();
                    guardarStmt.close();

                    int numeroCarritos = Integer.parseInt(JOptionPane.showInputDialog("Escriba el numero de EQUIPOS que se agregaran en este LABORATORIO")); //numero de euipos
                    int numeroCarritosCont = numeroCarritos;
                    int count = 0;
                    AgregarCarritosNuevos(numeroCarritos, count, nombreCorto ); //Se llama al metodo que va agregando equipos uno por uno
                    JOptionPane.showMessageDialog(null, "El carrito con sus correspondientes equipos han sido registrados", "Éxito", JOptionPane.INFORMATION_MESSAGE );
                    conexionConsulta.desconectar();
                    return nombreCarrito;
                }catch(SQLException ex){ //Excepcion SQL
                    if(ex instanceof SQLIntegrityConstraintViolationException ){
                        //Si ocurre una excepcion de integridad de restriccion en primary key, lo indica
                        JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de guardar los datos!!! \nIdentificador duplicado '"+nombreCorto+"' para el registro de usuario\n ** Los datos no se guardaron en la base de datos *** \nCorrija o use el panel de Identificación de Usuarios", "Error", JOptionPane.ERROR_MESSAGE );
                        return "";
                    }else{
                        //Si ocurre alguna otra excepcion la indica
                        JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de guardar los datos!!! \n"+ex.getMessage()+"\n ** No se guardaron datos en la base de datos *** \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                    }
                    System.err.println("¡¡¡Ocurrio un error al tratar de guardar los datos!!! \n\n ** No se guardaron datos en la base de datos *** \n Inténtelo nuevamente");
                    return "";
                }finally{
                    conexionConsulta.desconectar();
                }
             } else{
                JOptionPane.showMessageDialog(null, "El nombre de LABORATORIO debe ser MAXIMO de 5 carcateres. Vuelva a intentarlo", "Error", JOptionPane.ERROR_MESSAGE );
                    return "";
             }
        }else{
            JOptionPane.showMessageDialog(null, "Se debe de dar algún valor en ambos campos. Vuelva a intentarlo", "Error", JOptionPane.ERROR_MESSAGE );
                return "";
        }
       
    }//fin metodo guardarCarrito
    
    public void guardarEquipo() throws SQLException{
        
        int count = 0;
        int numeroCarritos = -1;
            try{
                String ListaLaboratorio[] = Obt_Laboratorio();
                //String nombreCorto = JOptionPane.showInputDialog("Escriba el Nombre corto del laboratorio/carrito al que se le AGREGARAN EQUIPOS").toUpperCase();
                Object lab = JOptionPane.showInputDialog(null, "Seleccione el laboratorio al que se le agregaran los carritos", "Añadir equipo", JOptionPane.QUESTION_MESSAGE, null, ListaLaboratorio, "01" );
                //System.out.println("Estoy por guardar equipos");
                String nombreCorto = lab.toString();
                Connection c = conexionConsulta.conectar(); 
                PreparedStatement verificarStmt = c.prepareStatement("SELECT * FROM LABORATORIOS WHERE ID_LAB=?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                verificarStmt.setString(1, nombreCorto);
                ResultSet rs1 = verificarStmt.executeQuery();
                if(rs1.next()){
                    try {
                        numeroCarritos = Integer.parseInt(JOptionPane.showInputDialog("Escriba el numero de carritos que se agregaran en este laboratorio/carrito"));
                        PreparedStatement guardarStmt = c.prepareStatement("SELECT count(NUM_COMP) FROM EQUIPOS WHERE ID_LAB = ? ", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        guardarStmt.setString(1, nombreCorto);
                        //Ejecuta la sentencia preparada
                        ResultSet rs = guardarStmt.executeQuery();
                        //Si hay resultados obtengo el valor. 
                        if(rs.next()){
                            count = rs.getInt(1);
                            System.out.println(count);
                        }
                        guardarStmt.close();
                        AgregarCarritosNuevos(numeroCarritos, count, nombreCorto );
                        JOptionPane.showMessageDialog(null, "Se han registrado exitosamente los equipos en el carrito", "Éxito", JOptionPane.INFORMATION_MESSAGE );
                        conexionConsulta.desconectar();
                    } catch(NumberFormatException ex){
                        JOptionPane.showMessageDialog(null, "Para agregar, debe darse algún valor equipos a agregar", "Error", JOptionPane.ERROR_MESSAGE );
                    }                       
                } else {
                    JOptionPane.showMessageDialog(null, "No existe ningún carrito/laboratorio con el nombre corto:  "+nombreCorto, "Utilice la opción mostrar carritos", JOptionPane.ERROR_MESSAGE );
                }
            }catch(SQLException ex){
                if(ex instanceof SQLIntegrityConstraintViolationException ){
                    //Si ocurre una excepcion de integridad de restriccion en primary key, lo indica
                    JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de guardar los datos!!! \nIdentificador duplicado '' para el registro de usuario\n ** Los datos no se guardaron en la base de datos *** \nCorrija o use el panel de Identificación de Usuarios", "Error", JOptionPane.ERROR_MESSAGE );
                }
                else{
                    //Si ocurre alguna otra excepcion la indica
                    JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de guardar los datos!!! \n"+ex.getMessage()+"\n ** No se guardaron datos en la base de datos *** \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                }
                System.err.println("¡¡¡Ocurrio un error al tratar de guardar los datos!!! \n\n ** No se guardaron datos en la base de datos *** \n Inténtelo nuevamente");
            }finally{
                conexionConsulta.desconectar();
            }
        
    }//fin metodo guardarEquipo
    
    public void mostrarCarritos() throws SQLException{
        textArea.append("----------------------------------------------------------------------------------------------------------------------------------------------------\n");
        Connection c = conexionConsulta.conectar();
        //String nombreCorto = JOptionPane.showInputDialog("Escriba el Nombre corto del laboratorio/carrito a mostrar:").toUpperCase();
        PreparedStatement mostrarStmt = c.prepareStatement("SELECT ID_LAB, NOMBRE_LAB, UTILIZABLE FROM LABORATORIOS" , ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        
        try{
            
            //mostrarStmt.setString(1, nombreCorto);
            ResultSet rs1 = mostrarStmt.executeQuery();
            EnviarTexto("Estos son los registros en la tabla LABORATORIOS");
            EnviarTexto("ID_LAB    NOMBRE_LAB           UTILIZABLE");
            
            //rs1.beforeFirst(); 
            while (rs1.next()) {
                String idCarrito = rs1.getString("ID_LAB");
                String nomCarrito = rs1.getString("NOMBRE_LAB");
                String utilizable = rs1.getString("UTILIZABLE");
                EnviarTexto(idCarrito +"      " +nomCarrito+"      " +utilizable);   
            }   
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de mostrar los datos!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
        }finally{    
            conexionConsulta.desconectar();
        }        
    }
    
    public void mostrarEquipos(String nombreLab) throws SQLException{
        textArea.append("----------------------------------------------------------------------------------------------------------------------------------------------------\n");
        Connection c = conexionConsulta.conectar();
        if(nombreLab.equals("")){
            String ListaLaboratorio[] = Obt_Laboratorio();
            //String nombreCorto = JOptionPane.showInputDialog("Escriba el Nombre corto del laboratorio/carrito a mostrar:").toUpperCase();
            Object lab = JOptionPane.showInputDialog(null, "Seleccione el laboratorio del que se mostraran los equipos existentes", "Mostrar equipos", JOptionPane.QUESTION_MESSAGE, null, ListaLaboratorio, "01" );
            String nombreCorto = lab.toString();
            PreparedStatement mostrarStmt = c.prepareStatement("SELECT L.ID_LAB, L.NOMBRE_LAB, E.ID_EQUIPO, E.NUM_COMP, E.UTILIZABLE, E.OBS_EQUIPO FROM EQUIPOS E, LABORATORIOS L WHERE L.ID_LAB = E.ID_LAB AND E.ID_LAB = ? ORDER BY E.NUM_COMP" , ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            mostrarStmt.setString(1, nombreCorto);
            try{
                //mostrarStmt.setString(1, nombreCorto);
                ResultSet rs1 = mostrarStmt.executeQuery();
                EnviarTexto("Estos son los registros en la tabla EQUIPOS");
                EnviarTexto("ID_LAB    NOMBRE_LAB      ID_EQUIPO      NUM_COMP      UTILIZABLE      OBS_EQUIPO");
                rs1.beforeFirst(); 
                if(rs1.next()){
                    rs1.beforeFirst(); 
                    while (rs1.next()) {
                    String idCarrito = rs1.getString("ID_LAB");
                    String nomCarrito = rs1.getString("NOMBRE_LAB");
                    String idEquipo = rs1.getString("ID_EQUIPO");
                    String numComp = rs1.getString("NUM_COMP");
                    String utilizable = rs1.getString("UTILIZABLE");
                    String obsEquipo = rs1.getString("OBS_EQUIPO");
                    EnviarTexto(idCarrito +"      " +nomCarrito +"      " + idEquipo +"                     " + numComp+"                     " + utilizable+"    " + obsEquipo);
                    } 
                }else{
                    JOptionPane.showMessageDialog(null, "Este laboratorio no tiene ningún equipo registrado\n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                }
            }catch(SQLException ex){
                JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de mostrar los datos!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
            }finally{
                conexionConsulta.desconectar();
            }
        } else {            
            PreparedStatement mostrarStmt = c.prepareStatement("SELECT L.ID_LAB, L.NOMBRE_LAB, E.ID_EQUIPO, E.NUM_COMP, E.UTILIZABLE, E.OBS_EQUIPO FROM EQUIPOS E, LABORATORIOS L WHERE L.ID_LAB = E.ID_LAB AND E.ID_LAB = ? ORDER BY E.NUM_COMP" , ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            mostrarStmt.setString(1, nombreLab);
            try{
                //mostrarStmt.setString(1, nombreCorto);
                ResultSet rs1 = mostrarStmt.executeQuery();
                EnviarTexto("Estos son los registros en la tabla Carritos");
                EnviarTexto("ID_LAB    NOMBRE_LAB      ID_EQUIPO      NUM_COMP      UTILIZABLE      OBS_EQUIPO");
                rs1.beforeFirst(); 
                if(rs1.next()){
                    while (rs1.next()) {
                    String idCarrito = rs1.getString("ID_LAB");
                    String nomCarrito = rs1.getString("NOMBRE_LAB");
                    String idEquipo = rs1.getString("ID_EQUIPO");
                    String numComp = rs1.getString("NUM_COMP");
                    String utilizable = rs1.getString("UTILIZABLE");
                    String obsEquipo = rs1.getString("OBS_EQUIPO");
                    EnviarTexto(idCarrito +"      " +nomCarrito +"      " + idEquipo +"                     " + numComp+"                     " + utilizable+"    " + obsEquipo);

                    } 
                }else{
                    JOptionPane.showMessageDialog(null, "Este laboratorio no tiene ningún equipo registrado\n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                }
            }catch(SQLException ex){
                JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de mostrar los datos!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
            }finally{
                conexionConsulta.desconectar();
            }
            
        }
        
    }
    
    public void mostrarRoles() throws SQLException{
        textArea.append("----------------------------------------------------------------------------------------------------------------------------------------------------\n");
        Connection c = conexionConsulta.conectar();
        PreparedStatement mostrarStmt = c.prepareStatement("SELECT R.ID_USUARIO, R.NAME_USUARIO, R.TIPO_USUARIO, G.ID_GRUPO, G.ID_LAB FROM USUARIOS R INNER JOIN GRUPOLABORATORIO G ON R.ID_USUARIO = G.ID_USUARIO " , ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        
        try{
            ResultSet rs1 = mostrarStmt.executeQuery();
            EnviarTexto("Estos son los USUARIOS registrados con su correspondiente GRUPO");
            EnviarTexto("ID_USUARIO    TIPO_USUARIO      NAME_USUARIOS      ID_GRUPO      ID_LAB");
            if(rs1.next()){
                rs1.beforeFirst(); 
                while (rs1.next()) {
                String idUsuario = rs1.getString("R.ID_USUARIO");
                String nomUsuario = rs1.getString("R.NAME_USUARIO");
                String tipoUsuario = rs1.getString("R.TIPO_USUARIO");
                String idGrupo = rs1.getString("G.ID_GRUPO");
                String idLab = rs1.getString("G.ID_LAB");
                EnviarTexto(idUsuario +"                  " +tipoUsuario+"                  " +nomUsuario+"                   " + idGrupo+"                     " + idLab);
                
                }
                EnviarTexto("\n****************************************************************************************************************************");
                EnviarTexto("                    Usuarios sin grupo ASOCIADO\n");
                PreparedStatement mostrarStmt2 = c.prepareStatement("SELECT R.ID_USUARIO, R.NAME_USUARIO,R.TIPO_USUARIO FROM USUARIOS R LEFT JOIN GRUPOLABORATORIO G ON R.ID_USUARIO=G.ID_USUARIO WHERE G.ID_USUARIO IS NULL" , ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                try{
                    ResultSet rs2 = mostrarStmt2.executeQuery();
                    EnviarTexto("ID_USUARIO    NAME_USUARIO     TIPO_USUARIO");
                    if(rs2.next()){
                        rs2.beforeFirst(); 
                        while (rs2.next()) {
                        String idUsuario2 = rs2.getString("R.ID_USUARIO");
                        String nomUsuario2 = rs2.getString("R.NAME_USUARIO");
                        String tipoUsuario2 = rs2.getString("R.TIPO_USUARIO");
                        EnviarTexto(idUsuario2 +"                    " +nomUsuario2+"                         " +tipoUsuario2);
                        }                
                    }
                }catch(SQLException e){

                    JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de mostrar los datos!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                }
            }else{
                JOptionPane.showMessageDialog(null, "No hay GRUPOS ni USUARIOS registrados \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
            }   
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de mostrar los datos!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
        }finally{ 
            conexionConsulta.desconectar();
        }
    }
    
    public void AgregarCarritosNuevos (int numeroCarritos, int count, String nombreCorto) throws SQLException{
        // 
        /*while(numeroCarritos >= numeroCarritosRegistro - count && numeroCarritosRegistro - count  > 0 ){
                        System.out.println("Estoy en el while");
                        PreparedStatement guardarStmt2 = c.prepareStatement("INSERT INTO EQUIPOS(ID_LAB, NUM_COMP  ) VALUES (?, ? )");
                        guardarStmt2.setString(1, nombreCorto);
                        if(numeroCarritosRegistro <= 9 && numeroCarritos > 0){
                        String numComp = nombreCorto + ":E0" + (numeroCarritosRegistro);
                        System.out.println(numeroCarritosRegistro);
                        guardarStmt2.setString(1, nombreCorto);
                        guardarStmt2.setString(2, numComp);
                        guardarStmt2.execute();
                        }else{
                        String numComp = nombreCorto + ":E" + (numeroCarritosRegistro);
                        guardarStmt2.setString(1, nombreCorto);
                        guardarStmt2.setString(2, numComp);
                        guardarStmt2.execute();
                    }
                    guardarStmt2.close();
                    numeroCarritosRegistro --;
                    }*/
        int n = 1;
        int numeroEquipo = 0;
        Connection c = conexionConsulta.conectar();
        try{
            while( n <= numeroCarritos){
                numeroEquipo = count + n;
                if(numeroEquipo < 10){
                    String numComp = nombreCorto + ":E0" +numeroEquipo;
                    PreparedStatement guardarStmt2 = c.prepareStatement("INSERT INTO EQUIPOS(ID_LAB, NUM_COMP  ) VALUES (?, ? )");
                    guardarStmt2.setString(1, nombreCorto);
                    guardarStmt2.setString(2, numComp);
                    guardarStmt2.execute();
                    guardarStmt2.close();
                    n++;
                } else {
                    String numComp = nombreCorto + ":E" +numeroEquipo;
                    PreparedStatement guardarStmt2 = c.prepareStatement("INSERT INTO EQUIPOS(ID_LAB, NUM_COMP  ) VALUES (?, ? )");
                    guardarStmt2.setString(1, nombreCorto);
                    guardarStmt2.setString(2, numComp);
                    guardarStmt2.execute();
                    guardarStmt2.close();
                    n++;
                }
                
            }
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de GUARDAR LOS EQUIPOS!!! \n Intentelo nuevamente" +ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
        }
    }
    
    public static String[] Obt_Laboratorio (){
        
        int tam = 50;
        String ListaLaboratorio[] = new String [tam];
        int n = 0;
        //ListaLaboratorio.addElement("Selecciona un laboratorio");
        Connection c = conexionConsulta.conectar();
        try{
            PreparedStatement pstm = c.prepareStatement("SELECT ID_LAB FROM LABORATORIOS ORDER BY ID_LAB", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet res = pstm.executeQuery();
            while(res.next()){
                ListaLaboratorio[n] = res.getString("ID_LAB");
                //System.out.println(ListaLaboratorio[n].toString());
                n ++;
                
            }
            
        } catch (SQLException e) {
            
            System.err.println("Error em la consulta:" + e.getMessage());;
            
        }
        
        return ListaLaboratorio;

    }
    
    public static String[] Obt_LaboratorioAsignado (String grupo){
        
        int tam = 50;
        String ListaLaboratorio[] = new String [tam];
        int n = 0;
        //ListaLaboratorio.addElement("Selecciona un laboratorio");
        Connection c = conexionConsulta.conectar();
        try{
            PreparedStatement pstm = c.prepareStatement("SELECT ID_LAB FROM GRUPOS WHERE ID_GRUPO = ? ORDER BY ID_LAB", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            pstm.setString(1, grupo);
            ResultSet res = pstm.executeQuery();
            while(res.next()){
                ListaLaboratorio[n] = res.getString("ID_LAB");
                //System.out.println(ListaLaboratorio[n].toString());
                n ++;
                
            }
            
        } catch (SQLException e) {
            
            System.err.println("Error en la consulta:" + e.getMessage());;
            
        }
        
        return ListaLaboratorio;

    }
    public static String[] Obt_Grupos(){
        
        int tam = 50;
        String ListaLaboratorio[] = new String [tam];
        int n = 0;
        //ListaLaboratorio.addElement("Selecciona un laboratorio");
        Connection c = conexionConsulta.conectar();
        try{
            PreparedStatement pstm = c.prepareStatement("SELECT NOM_GRUPO FROM GRUPOS", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet res = pstm.executeQuery();
            while(res.next()){
                ListaLaboratorio[n] = res.getString("NOM_GRUPO");
                //System.out.println(ListaLaboratorio[n].toString());
                n ++;
                
            }
            
        } catch (SQLException e) {
            
            System.err.println("Error en la consulta:" + e.getMessage());;
            
        }
        
        return ListaLaboratorio;

    }
    
    public static String[] Obt_RolesSinGrupo(){
        
        int tam = 50;
        String ListaGrupos[] = new String [tam];
        int n = 0;
        //ListaLaboratorio.addElement("Selecciona un laboratorio");
        Connection c = conexionConsulta.conectar();
        try{
            PreparedStatement pstm = c.prepareStatement("SELECT R.NAME_USUARIO FROM USUARIOS R LEFT JOIN  GRUPOLABORATORIO G ON R.ID_USUARIO = G.ID_USUARIO WHERE G.ID_USUARIO IS NULL ", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet res = pstm.executeQuery();
               while(res.next()){
                ListaGrupos[n] = res.getString("NAME_USUARIO");
                n++;
                } 
            res.close();
            
        } catch (SQLException e) {
            
            System.err.println("Error en la consulta:" + e.getMessage());;
            
        } finally {
            conexionConsulta.desconectar();
        }
        
        if(n == 0){
            return null;
        } else {
            return ListaGrupos;
        }
    }
    
    public static String[] Obt_RolesDeGrupo(String grupo) {

        int tam = 50;
        String ListaGrupos[] = new String[tam];
        int n = 0;
        //ListaLaboratorio.addElement("Selecciona un laboratorio");
        Connection c = conexionConsulta.conectar();
        try {
            PreparedStatement pstm = c.prepareStatement("SELECT DISTINCT NAME_USUARIO FROM USUARIOS U INNER JOIN GRUPOLABORATORIO G ON U.ID_USUARIO = G.ID_USUARIO AND G.ID_GRUPO = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            pstm.setString(1, grupo);
            ResultSet res = pstm.executeQuery();
            while (res.next()) {
                ListaGrupos[n] = res.getString("NAME_USUARIO");
                n++;
            }
            res.close();
        } catch (SQLException e) {
            System.err.println("Error en la consulta:" + e.getMessage());;
        } finally {
            conexionConsulta.desconectar();
        }
        if (n == 0) {
            return null;
        } else {
            return ListaGrupos;
        }
    }
    
    public String[] Obt_Roles(){
        
        int tam = 50;
        String ListaGrupos[] = new String [tam];
        int n = 0;
        //ListaLaboratorio.addElement("Selecciona un laboratorio");
        Connection c = conexionConsulta.conectar();
        try{
            PreparedStatement pstm = c.prepareStatement("SELECT NAME_USUARIO FROM USUARIOS", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet res = pstm.executeQuery();
               while(res.next()){
                ListaGrupos[n] = res.getString("NAME_USUARIO");
                n++;
                } 
            res.close();
            
        } catch (SQLException e) {
            
            System.err.println("Error en la consulta:" + e.getMessage());;
            
        } finally {
            conexionConsulta.desconectar();
        }
        
        if(n == 0){
            return null;
        } else {
            return ListaGrupos;
        }
    }
    
    
    
    public static String guardarGrupo() throws SQLException{
        
        // String arregloLaboratorios[];
         List<String> arregloLaboratorios = new ArrayList<String>();
         try{
            Connection c = conexionConsulta.conectar();
            arregloLaboratorios.clear();
            String nombreGrupo = JOptionPane.showInputDialog("Escriba el NOMBRE CORTO del GRUPO a AGREGAR: ").toUpperCase() ;
            PreparedStatement verificarStmt = c.prepareStatement("SELECT ID_GRUPO FROM GRUPOS WHERE NOM_GRUPO = ? ", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            verificarStmt.setString(1, nombreGrupo);
            ResultSet rs = verificarStmt.executeQuery();
            if(rs.next()){
                JOptionPane.showMessageDialog(null, "¡¡¡Este grupo ya fue creado, para agregar mas porfavor hagalo en agregar a grupo !!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                return "";
            }else {
                //String nameRol = JOptionPane.showInputDialog("Escriba el NOMBRE del ROL que tendra acceso a este GRUPO a AGREGAR: ").toUpperCase() ;
                if(Obt_RolesSinGrupo() == null){
                    JOptionPane.showMessageDialog(null, "¡¡¡No se encontro ningun USUARIO DISPONIBLE!!!, Por favor Ingrese a CREAR USUARIO", "Error", JOptionPane.ERROR_MESSAGE );
                    return "";
                }else{
                    
                    return nombreGrupo;
                    
                    /*String ListaLaboratorio[] = Obt_RolesSinGrupo();
                    Object lab = JOptionPane.showInputDialog(null, "Seleccione el ROL que pertenecera a este GRUPO ","\n Añadir ROL", JOptionPane.QUESTION_MESSAGE, null, ListaLaboratorio, "01" );
                    String nameRol = lab.toString();*/
                    /*PreparedStatement verificarStmt2 = c.prepareStatement("SELECT ID_USUARIO FROM ROLES WHERE NAME_ROL = ? ", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    verificarStmt2.setString(1, nameRol);
                    //System.out.println("Estoy por hacer el query");
                    ResultSet rs2 = verificarStmt2.executeQuery();*/
                    //System.out.println("Ya lo hice");
                   ///if(rs2.next()){
                  /* if(rs2.next()){
                        String idRol = rs2.getString("ID_USUARIO");
                        System.out.println(idRol);
                        CatalogoLaboratorios ch = new CatalogoLaboratorios(this, true, nombreGrupo, idRol);
                        ch.setVisible(true);*/
                  /*  PreparedStatement verificarStmt3 = c.prepareStatement("SELECT ID_ROL FROM GRUPOS WHERE ID_ROL = ? ", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    verificarStmt3.setString(1, idRol);
                    ResultSet rs3 = verificarStmt3.executeQuery();
                    if(rs3.next()){
                        JOptionPane.showMessageDialog(null, "¡¡¡Este rol ya fue asignado !!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                    }else{*/
                        //int numeroLaboratorios = Integer.parseInt(JOptionPane.showInputDialog("Escriba el numero de LABORATORIOS que se agregaran en este GRUPO"));
                        //String ListaLaboratorio[] = Obt_Laboratorio();
                        //arregloLaboratorios = new String[numeroLaboratorios];

                       // while (numeroLaboratorios > 0){
                            //Object lab = JOptionPane.showInputDialog(null, "Seleccione el laboratorio que pertenecera a este GRUPO \n Numeros de laboratorios por agregar:( "+numeroLaboratorios, ") \n Añadir equipo", JOptionPane.QUESTION_MESSAGE, null, ListaLaboratorio, "01" );
                            //String idLaboratorio = lab.toString();
                          //  boolean idLabExiste = false;
                           // if (arregloLaboratorios.contains(idLaboratorio)){
                               // idLabExiste = true;
                                //System.out.println(idLabExiste);
                            //}else{
                                //idLabExiste = false;
                                //System.out.println(idLabExiste);
                           // }

                           /* if(idLabExiste == false){
                                arregloLaboratorios.add(idLaboratorio);
                                System.out.println(nombreGrupo);
                                System.out.println(idLaboratorio);
                                System.out.println(idRol);
                                 System.out.println("dentro del if");
                                PreparedStatement guardarStmt2 = c.prepareStatement("INSERT INTO GRUPOS(ID_GRUPO, ID_LAB, ID_ROL ) VALUES (?, ?, ? )");
                                guardarStmt2.setString(1, nombreGrupo);
                                guardarStmt2.setString(2, idLaboratorio);
                                guardarStmt2.setString(3, idRol);
                                guardarStmt2.execute();
                                guardarStmt2.close();
                                JOptionPane.showMessageDialog(null, "El grupo ha sido registrado con exito", "Éxito", JOptionPane.INFORMATION_MESSAGE );
                                numeroLaboratorios --;

                            }else{
                                JOptionPane.showMessageDialog(null, "¡¡¡Este LABORATORIO ya fue seleccionado!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                            }*/
                        //}
                    
                        
                            
                    /*}
                          
                
                }else {
                    JOptionPane.showMessageDialog(null, "¡¡¡Este rol NO EXISTE!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                }*/
                    /*}else {
                        JOptionPane.showMessageDialog(null, "¡¡¡No se encontro este usuario!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                    }*/
                }

            }

        }catch(SQLException ex){
            
            JOptionPane.showMessageDialog(null, "¡¡¡1Ocurrio un error al tratar de insertar los datos!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
            return "";
            
        }finally{
            
            conexionConsulta.desconectar();
            
        }
        
    }//fin metodo guardarCarrito
    
    
    
    

    
    
    public void mostrarGrupos() throws SQLException{
        textArea.append("----------------------------------------------------------------------------------------------------------------------------------------------------\n");
        try{
            Connection c = conexionConsulta.conectar();
            PreparedStatement mostrarStmt = c.prepareStatement("SELECT G.ID_GRUPO, G.ID_LAB, G.ID_USUARIO, R.NAME_USUARIO FROM GRUPOLABORATORIO G INNER JOIN USUARIOS R ON G.ID_USUARIO = R.ID_USUARIO ORDER BY G.ID_GRUPO" , ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            //mostrarStmt.setString(1, nombreCorto);
            ResultSet rs1 = mostrarStmt.executeQuery();
            EnviarTexto("Estos son los registros en la tabla GRUPO");
            EnviarTexto("ID_GRUPO                NOM_GRUPO               ID_LAB                    ID_USUARIO                    NAME_USUARIO");
            
            //rs1.beforeFirst(); 
            while (rs1.next()) {
                String idGrupo = rs1.getString("ID_GRUPO");
                String idLab = rs1.getString("ID_LAB");
                String idUsuario = rs1.getString("ID_USUARIO");
                String nomUsuario = rs1.getString("NAME_USUARIO");
                PreparedStatement verificarNombrestmt = c.prepareStatement("SELECT NOM_GRUPO FROM GRUPOS WHERE ID_GRUPO = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                verificarNombrestmt.setString(1, idGrupo);
                ResultSet res = verificarNombrestmt.executeQuery();
                if(res.next()){
                    String nomGrupo = res.getString("NOM_GRUPO");
                    EnviarTexto(idGrupo +"                   " +nomGrupo+"                   " +idLab+"              "+idUsuario+ "                " +nomUsuario);
                }
                
            }
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de MOSTRAR los datos!!! \n Intentelo nuevamente  " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
        }finally{
            conexionConsulta.desconectar();
        }
    
    }
    
    public void borrarRol(){
        Connection c = conexionConsulta.conectar();
        try{
            String ListaLaboratorio[] = Obt_Roles();
            Object lab = JOptionPane.showInputDialog(null, "Seleccione el USUARIO que SE ELIMINARA ","\n Eliminar USUARIO", JOptionPane.QUESTION_MESSAGE, null, ListaLaboratorio, "01" );
            String nameUsuario = lab.toString();
            int resp = JOptionPane.showConfirmDialog(null, "Si usted da a ACEPTAR se ELIMINARA el USUARIO\n"+nameUsuario+"¿Esta seguro?",//<- EL MENSAJE 
                    "Alerta!"/*<- El título de la ventana*/, JOptionPane.YES_NO_OPTION/*Las opciones (si o no)*/, JOptionPane.WARNING_MESSAGE/*El tipo de ventana, en este caso WARNING*/);
            //System.out.println(resp); // SI = 0, NO = 1
            if(resp == 0){
                System.out.println("DIJO QUE SI");
                PreparedStatement verificarStmt1 = c.prepareStatement("SELECT ID_USUARIO FROM USUARIOS WHERE NAME_USUARIO =?" , ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                verificarStmt1.setString(1, nameUsuario);
                ResultSet rs1 = verificarStmt1.executeQuery();
                if(rs1.next()){
                    System.out.println("SI EXISTE EN USUARIOS");
                    String idUsuario = rs1.getString("ID_USUARIO");
                    PreparedStatement verificarStmt2 = c.prepareStatement("SELECT ID_GRUPO FROM GRUPOLABORATORIO WHERE ID_USUARIO = ?" , ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    verificarStmt2.setString(1, idUsuario);
                    ResultSet rs2 = verificarStmt2.executeQuery();
                    if(rs2.next()){
                        System.out.println("SI EXISTE EN GRUPOS");
                        String grupo = rs2.getString("ID_GRUPO");
                        resp = JOptionPane.showConfirmDialog(null, "Este USUARIO tiene asignado el GRUPO: \n"+grupo+"Si da aceptar TAMBIEN se eliminara de GRUPOS  ¿Esta seguro?",//<- EL MENSAJE 
                        "Alerta!"/*<- El título de la ventana*/, JOptionPane.YES_NO_OPTION/*Las opciones (si o no)*/, JOptionPane.WARNING_MESSAGE/*El tipo de ventana, en este caso WARNING*/);
                        if(resp == 0){
                            System.out.println("DIJO QUE SI DE NUEVO");
                            PreparedStatement borrarStmt = c.prepareStatement("DELETE FROM GRUPOLABORATORIO WHERE ID_USUARIO =? ", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                            borrarStmt.setString(1, idUsuario);
                            int elementosBorradosGrupos = borrarStmt.executeUpdate();
                            if(elementosBorradosGrupos >= 1){
                                //Se elimino de grupos
                                PreparedStatement borrarGrupoStmt = c.prepareStatement("DELETE FROM GRUPOS WHERE ID_GRUPO =? ", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                borrarGrupoStmt.setString(1, idUsuario);
                                int elementosBorradosGrupo = 0;
                                elementosBorradosGrupo = elementosBorradosGrupo + borrarGrupoStmt.executeUpdate();
                                if(elementosBorradosGrupo >=1){
                                    System.out.println("SE ELIMINO DE GRUPOS");
                                    JOptionPane.showMessageDialog(null, "El USUARIO ha sido ELIMINADO con exito de GRUPOLABORATORIO Y GRUPO\nElementos borrados en total \n GRUPOLABORATORIO: "+elementosBorradosGrupos+"\nGRUPOS: "+elementosBorradosGrupo, "Éxito", JOptionPane.INFORMATION_MESSAGE );
                                    PreparedStatement borrarStmt2 = c.prepareStatement("DELETE FROM USUARIOS WHERE ID_USUARIO =? ", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                    borrarStmt2.setString(1, idUsuario);
                                    int elementosBorradosUsuarios = borrarStmt2.executeUpdate();
                                    if(elementosBorradosUsuarios >= 1){
                                        //Se elimino de USUARIOS
                                        System.out.println("SE ELIMINO DE USUARIOS");
                                        JOptionPane.showMessageDialog(null, "El USUARIO ha sido ELIMINADO con exito de USUARIOS\nElementos borrados en total"+elementosBorradosUsuarios, "Éxito", JOptionPane.INFORMATION_MESSAGE );
                                    }else{
                                        //No se elimino de USUARIOS
                                        JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de eliminar los datos de USUARIOS!!! \n Intentelo nuevamente", "Exiro", JOptionPane.ERROR_MESSAGE );
                                    }
                                }    
                            }else{
                                //NO se puedo eliminar de grupos
                                JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de eliminar los datos de GRUPOS!!! \n Intentelo nuevamente", "Exiro", JOptionPane.ERROR_MESSAGE );
                            }
                        }else{
                            //No acepto borrar
                            borrarRol();
                        }
                    }else {
                        //NO tiene ningun grupo registrado
                        PreparedStatement borrarStmt2 = c.prepareStatement("DELETE FROM USUARIOS WHERE ID_USUARIO = ? ", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        borrarStmt2.setString(1, idUsuario);
                        int elementosBorradosUsuarios = borrarStmt2.executeUpdate();
                        if(elementosBorradosUsuarios >= 1){
                            JOptionPane.showMessageDialog(null, "El USUARIOS ha sido ELIMINADO con exito de USUARIOS", "Éxito", JOptionPane.INFORMATION_MESSAGE );
                        }else{
                            JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de eliminar los datos de USUARIOS!!! \n Intentelo nuevamente", "Exiro", JOptionPane.ERROR_MESSAGE );
                        }
                    }
                } else {
                    //NO EXISTE EL USUARIO
                    JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de eliminar los datos!!! \n Intentelo nuevamente", "Exiro", JOptionPane.ERROR_MESSAGE );
                }
                
            }else{
                //No acepto borrar
                borrarRol();
            }
            
        }catch(SQLException ex){
            System.err.println("Ocurrio una excepcion de tipo "+ex+" con mensaje:"+ex.getMessage());
            JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de conectarse!!! \n Intentelo nuevamente", "Exiro", JOptionPane.ERROR_MESSAGE );
        }finally{
            conexionConsulta.desconectar();
        }
        
    }//Fin borrar USUARIO
    
    public void actualizarGurpo(){
        //CAMBIAR NOMBRE GRUPO
        //CAMBIAR LABORATORIOS DE ESTE GRUPO
        //CAMBIAR USUARIO ASIGNADO (NO DEBE TENER UN GRUPO ASIGNADO Y LIBERAR ESTE USUARIO)
        formularioActualizarGrupo ch = new formularioActualizarGrupo(this, true);
        ch.setVisible(true);
    }
    
    public void eliminarGrupo(){
        Connection c = conexionConsulta.conectar();
        try{
            String ListaGrupos[] = Obt_Grupos();
            Object lab = JOptionPane.showInputDialog(null, "Seleccione el GRUPO que SE ELIMINARA ","\n Eliminar USUARIO", JOptionPane.QUESTION_MESSAGE, null, ListaGrupos, "01" );
            String nameGrupo = lab.toString();
            PreparedStatement verificarStmt = c.prepareStatement("SELECT ID_GRUPO FROM GRUPOS WHERE NOM_GRUPO = ?" , ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            verificarStmt.setString(1, nameGrupo);
            ResultSet rs1 = verificarStmt.executeQuery();
            if(rs1.next()){
                String idGrupo = rs1.getString("ID_GRUPO");
                PreparedStatement verificarStmt1 = c.prepareStatement("SELECT ID_LAB, ID_USUARIO FROM GRUPOLABORATORIO WHERE ID_GRUPO = ?" , ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                verificarStmt1.setString(1, idGrupo);
                ResultSet rs = verificarStmt1.executeQuery();
                List<String> arregloLaboratorios = new ArrayList<String>();
                String Usuario = "";
                while (rs.next()){
                    arregloLaboratorios.add(rs.getString("ID_LAB"));
                    Usuario = rs.getString("ID_USUARIO");
                }

                int resp = JOptionPane.showConfirmDialog(null, "Si usted da a ACEPTAR se ELIMINARA el GRUPO: "+nameGrupo+"\nAdemas de sus relaciones con Laboratorios y USUARIOS que son \nLABORATORIOS:"+arregloLaboratorios+"\nUSUARIO:"+Usuario+"\n¿Esta seguro?",//<- EL MENSAJE 
                        "Alerta!"/*<- El título de la ventana*/, JOptionPane.YES_NO_OPTION/*Las opciones (si o no)*/, JOptionPane.WARNING_MESSAGE/*El tipo de ventana, en este caso WARNING*/);
                //System.out.println(resp); // SI = 0, NO = 1
                if(resp == 0){
                    System.out.println("DIJO QUE SI");
                    PreparedStatement eliminarStmt1 = c.prepareStatement("DELETE FROM GRUPOLABORATORIO WHERE ID_GRUPO = ?" , ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    eliminarStmt1.setString(1, idGrupo);
                    int elementosActualizadosGruposLab = eliminarStmt1.executeUpdate();
                    if(elementosActualizadosGruposLab >= 1){
                        PreparedStatement eliminarStmt2 = c.prepareStatement("DELETE FROM GRUPOS WHERE ID_GRUPO = ?" , ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        eliminarStmt2.setString(1, idGrupo);
                        int elementosActualizadosGrupos = eliminarStmt2.executeUpdate();
                        if(elementosActualizadosGruposLab >= 1){
                            JOptionPane.showMessageDialog(null, "El GRUPO se ha ACTUALIZADO con exito\nElementos ACTUALIZADOS en total \nGRUPOLABORATORIO: "+elementosActualizadosGruposLab+"\nGRUPOS: "+elementosActualizadosGrupos, "Éxito", JOptionPane.INFORMATION_MESSAGE );
                        } else {
                            JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un ERROR al tratar de ACTUALIZAR!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                        }
                    } else{
                        JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un ERROR al tratar de ACTUALIZAR!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                    }
                    eliminarStmt1.close();

                }
            }
        }catch(SQLException ex){
            System.err.println("Ocurrio una excepcion de tipo "+ex+" con mensaje:"+ex.getMessage());
            JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de conectarse!!! \n Intentelo nuevamente", "Exiro", JOptionPane.ERROR_MESSAGE );
        }finally{
            conexionConsulta.desconectar();
        }
    }
    
    public String inutilizarEquipo(boolean utiliza){
        Connection c = conexionConsulta.conectar();
        String ListaLaboratorio[] = Obt_Laboratorio();
        //String nombreCorto = JOptionPane.showInputDialog("Escriba el Nombre corto del laboratorio/carrito a mostrar:").toUpperCase();
        Object lab = JOptionPane.showInputDialog(null, "Seleccione el laboratorio del que se INUTILIZARAN los equipos existentes", "Mostrar equipos", JOptionPane.QUESTION_MESSAGE, null, ListaLaboratorio, "01" );
        String nombreCorto = lab.toString();
        CatalogoEquipos ch = new CatalogoEquipos(this, true, nombreCorto, utiliza, usuario);
        ch.setVisible(true);
        return nombreCorto;
    }
    
    
    public void cerrar (){
        DateTimeFormatter dtf5 = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        System.out.println("yyyy/MM/dd-> "+dtf5.format(LocalDateTime.now()));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        System.out.println("HH:mm:ss-> "+dtf.format(LocalDateTime.now()));
        try{
            this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            addWindowListener (new WindowAdapter(){
                public void windowClosing (WindowEvent e){
                    Connection c = conexionConsulta.conectar();
                    try (PreparedStatement registroLog = c.prepareStatement("INSERT INTO LOGS(NAME_USUARIO, ACCION, FECHA_ACCION, HORA_ACCION) values(?, ?, ?, ?)" , ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                        registroLog.setString(1, usuario);
                        registroLog.setString(2, "Salio del sistema");
                        registroLog.setString(3, dtf5.format(LocalDateTime.now()));
                        registroLog.setString(4, dtf.format(LocalDateTime.now()));
                        registroLog.execute();
                    }catch (SQLException ex){
                    JOptionPane.showMessageDialog(null, "Ocurrió un error al INSERTAR EN LOGS"+e, "Error", JOptionPane.ERROR_MESSAGE );
                    } finally {
                        System.exit(0);
                    }
                }
            });
            conexionConsulta.desconectar();
            this.setVisible (true);
        }catch (Exception e){
            e.printStackTrace ();
        }
    }
    
    public String inutilizarLaboratorio(){
        Connection c = conexionConsulta.conectar();
        String ListaLaboratorio[] = Obt_Laboratorio();
        int elementosActualizados = 0;
        //String nombreCorto = JOptionPane.showInputDialog("Escriba el Nombre corto del laboratorio/carrito a mostrar:").toUpperCase();
        Object lab = JOptionPane.showInputDialog(null, "Seleccione el laboratorio del que se INUTILIZARAN los equipos existentes", "Mostrar equipos", JOptionPane.QUESTION_MESSAGE, null, ListaLaboratorio, "01" );
        String nombreCorto = lab.toString();
        PreparedStatement actualizarStmt2;
        try {
            int resp = JOptionPane.showConfirmDialog(null, "Si ACEPTA ESTE LABORATORIO QUEDARA COMO INUTILIZABLE, ¿Desea continuar?",//<- EL MENSAJE 
            "Alerta!"/*<- El título de la ventana*/, JOptionPane.YES_NO_OPTION/*Las opciones (si o no)*/, JOptionPane.WARNING_MESSAGE/*El tipo de ventana, en este caso WARNING*/);
            if (resp == 0){// 0 es SI
                actualizarStmt2 = c.prepareStatement("UPDATE LABORATORIOS SET UTILIZABLE = ? WHERE ID_LAB = ? ");
                actualizarStmt2.setInt(1, 0);
                actualizarStmt2.setString(2, nombreCorto);
                elementosActualizados = elementosActualizados + actualizarStmt2.executeUpdate();
                actualizarStmt2.close();
                /*String carritoNuevo = guardarCarrito();
                if(carritoNuevo.equals("")){
                } else {
                    sustituirLaboratorio(nombreCorto, carritoNuevo); //Se creaba un nuevo laboratorio al inutilizar uno
                }*/
                if(elementosActualizados > 0){
                    int labActualizados = elementosActualizados;
                    PreparedStatement actualizarEquiposStmt = c.prepareStatement("UPDATE EQUIPOS SET UTILIZABLE = ? WHERE ID_LAB = ? ");
                    actualizarEquiposStmt.setInt(1, 0);
                    actualizarEquiposStmt.setString(2, nombreCorto);
                    elementosActualizados = elementosActualizados + actualizarEquiposStmt.executeUpdate();
                    actualizarEquiposStmt.close();
                    if(elementosActualizados > 0){
                        int equiposActualizados = elementosActualizados - labActualizados;
                        int count = 0;
                        PreparedStatement mostrarStmt = c.prepareStatement("SELECT ID_GRUPO, ID_USUARIO FROM GRUPOLABORATORIO WHERE ID_LAB = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        mostrarStmt.setString(1, nombreCorto);
                        ResultSet rs1 = mostrarStmt.executeQuery();
                        while (rs1.next()) {
                            String idGrupo = rs1.getString("ID_GRUPO");                            
                            try (
                                    PreparedStatement actualizarStmt3 = c.prepareStatement("DELETE FROM GRUPOLABORATORIO WHERE ID_GRUPO = ? AND ID_LAB = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                                    actualizarStmt3.setString(1, idGrupo);
                                    actualizarStmt3.setString(2, nombreCorto);
                                    count = count + actualizarStmt3.executeUpdate();                                     
                            } catch (SQLException exc) {
                                JOptionPane.showMessageDialog(null, "Ocurrió un error al tratar de INUTILIZAR este equipo (Sustitucion en GRUPOS)" + exc, "Error", JOptionPane.ERROR_MESSAGE);
                                return "";
                            }                            
                        }
                        if (count > 0) {
                            JOptionPane.showMessageDialog(null, "Se marco como INUTILIZABLE el LABORATORIO:" + nombreCorto + "\nLaboratorios actualizados: " + labActualizados + "\nEquipos actualizados: " + equiposActualizados + "\nGrupos actualizados: " + count, "Exito", JOptionPane.INFORMATION_MESSAGE);
                            return nombreCorto;
                        } else {
                            JOptionPane.showMessageDialog(null, "Se marco como INUTILIZABLE el LABORATORIO:" + nombreCorto + "\nLaboratorios actualizados: " + labActualizados + "\nEquipos actualizados: " + equiposActualizados + "\nGrupos actualizados: " + count, "Exito", JOptionPane.INFORMATION_MESSAGE);
                            return nombreCorto;
                        }                        
                    } else {
                        JOptionPane.showMessageDialog(null, "Ocurrió un error al tratar de INUTILIZAR EQUIPOS DE ESTE LABORATORIO", "Error", JOptionPane.ERROR_MESSAGE);
                        return "";
                    }                                                            
                } else {
                JOptionPane.showMessageDialog(null, "Ocurrió un error al tratar de INUTILIZAR este LABORATORIO", "Error", JOptionPane.ERROR_MESSAGE );
                return "";
                }
            } else {
                textArea.append("Proceso Cancelado por el ADMINISTRADOR");
                return "";
            }                                    
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Ocurrió un error al tratar de INUTILIZAR este equipo", "Error", JOptionPane.ERROR_MESSAGE );
            return "";
        } finally{
            conexionConsulta.desconectar();
        }                
    }
    
    public void sustituirLaboratorio(String idLabActual, String carritoNuevo){
    Connection c = conexionConsulta.conectar();        
        try {
            PreparedStatement mostrarStmt = c.prepareStatement("SELECT ID_GRUPO, ID_USUARIO FROM GRUPOLABORATORIO WHERE ID_LAB = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            mostrarStmt.setString(1, idLabActual);
            ResultSet rs1 = mostrarStmt.executeQuery();
            while (rs1.next()) {
                String idGrupo = rs1.getString("ID_GRUPO");
                String idUsuario = rs1.getString("ID_USUARIO");
                try (
                    PreparedStatement actualizarStmt2 = c.prepareStatement("DELETE FROM GRUPOLABORATORIO WHERE ID_GRUPO = ? AND ID_LAB = ?",ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                    actualizarStmt2.setString(1, idGrupo);
                    actualizarStmt2.setString(2, idLabActual);
                    actualizarStmt2.execute();
                    try (
                        PreparedStatement guardarStmt2 = c.prepareStatement("INSERT INTO GRUPOLABORATORIO(ID_GRUPO, ID_LAB, ID_USUARIO ) VALUES (?, ?, ? )", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                        guardarStmt2.setString(1, idGrupo);
                        guardarStmt2.setString(2, carritoNuevo);
                        guardarStmt2.setString(3, idUsuario);
                        guardarStmt2.execute();
                    } catch (SQLException e){
                        JOptionPane.showMessageDialog(null, "Ocurrió un error al tratar de INUTILIZAR este equipo (Sustitucion en GRUPOS)"+e, "Error", JOptionPane.ERROR_MESSAGE );
                    }
                }catch (SQLException exc){
                    JOptionPane.showMessageDialog(null, "Ocurrió un error al tratar de INUTILIZAR este equipo (Sustitucion en GRUPOS)"+exc, "Error", JOptionPane.ERROR_MESSAGE );
                }                
                 
            }                       
        } catch (SQLException ex) {
           JOptionPane.showMessageDialog(null, "Ocurrió un error al tratar de INUTILIZAR este equipo (Sustitucion en GRUPOS)"+ex, "Error", JOptionPane.ERROR_MESSAGE );
        }
        
        
    }
    
    public void mostrarLogs() throws SQLException{
        textArea.append("----------------------------------------------------------------------------------------------------------------------------------------------------\n");
        Connection c = conexionConsulta.conectar();
        PreparedStatement mostrarStmt = c.prepareStatement("SELECT ID_LOG, NAME_USUARIO, ACCION, FECHA_ACCION, HORA_ACCION FROM LOGS WHERE FECHA_ACCION = CURDATE()" , ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        
        try{
            ResultSet rs1 = mostrarStmt.executeQuery();
            EnviarTexto("Estos son los USUARIOS registrados con su correspondiente GRUPO");
            EnviarTexto("ID_LOG     NAME_USUARIO     ACCION     FECHA_ACCION     HORA_ACCION");
            if(rs1.next()){
                rs1.beforeFirst(); 
                while (rs1.next()) {
                String idUsuario = rs1.getString("ID_LOG");
                String nomUsuario = rs1.getString("NAME_USUARIO");
                String tipoUsuario = rs1.getString("ACCION");
                String idGrupo = rs1.getString("FECHA_ACCION");
                String idLab = rs1.getString("HORA_ACCION");
                EnviarTexto(idUsuario +"                  " +tipoUsuario+"                  " +nomUsuario+"                   " + idGrupo+"                     " + idLab);
                
                }
                
            }else{
                JOptionPane.showMessageDialog(null, "No hay LOGS ni USUARIOS registrados \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
            }   
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de mostrar los datos!!! \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
        }finally{ 
            conexionConsulta.desconectar();
        }
    }
    
    //Este modulo se utilizó para actualizar multiples campos de fecha con valor default "0000-00-00", lo cual causaba un error
   /* public void modificarFechas(){
        Connection c = conexionConsulta.conectar();
        int count = 0;
        int act = 0;
        try{
            PreparedStatement mostrarStmt = c.prepareStatement("SELECT NUM_CTA, FECHA_ULT_USO FROM ALUMNOS" , ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            //mostrarStmt.setString(1, "0000-00-00");
            ResultSet rs1 = mostrarStmt.executeQuery();
            EnviarTexto("Estos son los Equipos registrados ");
            EnviarTexto("NUM_CTA     FECHA_ULT_USO");
            if(rs1.next()){
                rs1.beforeFirst(); 
                while (rs1.next()) {
                String idUsuario = rs1.getString("NUM_CTA");
                String nomUsuario = rs1.getString("FECHA_ULT_USO");
                    if(nomUsuario.equals("0000-00-00")){
                        count ++;
                        EnviarTexto(idUsuario +"                  " +nomUsuario+" / "+count);
                        PreparedStatement updateStmt = c.prepareStatement("UPDATE ALUMNOS SET FECHA_ULT_USO = ? WHERE NUM_CTA = ?;" , ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        updateStmt.setString(1, "1111-11-11" );
                        updateStmt.setString(2, idUsuario);
                        act = act + updateStmt.executeUpdate();
                    }
                }
                
                JOptionPane.showMessageDialog(null, "Los registros actualizados son en total "+act, "Resultado", JOptionPane.INFORMATION_MESSAGE );
                
            }else{
                JOptionPane.showMessageDialog(null, "No hay LOGS ni USUARIOS registrados \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
            }   
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de mostrar los datos!!! \n Intentelo nuevamente \n"+ex, "Error", JOptionPane.ERROR_MESSAGE );
        }finally{ 
            conexionConsulta.desconectar();
        }
        
    }*/

    public String agregarObservacionEquipo(boolean Penaliza){
        if(Penaliza == false){
            Connection c = conexionConsulta.conectar();
            String ListaLaboratorio[] = Obt_Laboratorio();
            //String nombreCorto = JOptionPane.showInputDialog("Escriba el Nombre corto del laboratorio/carrito a mostrar:").toUpperCase();
            Object lab = JOptionPane.showInputDialog(null, "Seleccione el laboratorio del que se INUTILIZARAN los equipos existentes", "Mostrar equipos", JOptionPane.QUESTION_MESSAGE, null, ListaLaboratorio, "01" );
            String nombreCorto = lab.toString();
            CatalogoEquipos ch = new CatalogoEquipos(this, true, nombreCorto, usuario, true);
            ch.setVisible(true);
            return nombreCorto;
        } else {
            Connection c = conexionConsulta.conectar();
            String ListaLaboratorio[] = Obt_Laboratorio();
            //String nombreCorto = JOptionPane.showInputDialog("Escriba el Nombre corto del laboratorio/carrito a mostrar:").toUpperCase();
            Object lab = JOptionPane.showInputDialog(null, "Seleccione el laboratorio del que se INUTILIZARAN los equipos existentes", "Mostrar equipos", JOptionPane.QUESTION_MESSAGE, null, ListaLaboratorio, "01" );
            String nombreCorto = lab.toString();
            CatalogoEquipos ch = new CatalogoEquipos(this, true, nombreCorto, usuario, true);
            ch.setVisible(true);
            return nombreCorto;
        }   
    }
    
    public void penalizarAlumno(){
        
        String cuenta = JOptionPane.showInputDialog("Número de Cuenta o RFC:");
        if(cuenta.equals("")){
            JOptionPane.showMessageDialog(null, "Porfavor Igrese un NUMERO DE CUENATA/RFC", "Error", JOptionPane.ERROR_MESSAGE);    
            penalizarAlumno();                    
        } else {
            Connection c = conexionConsulta.conectar();
            try{
            PreparedStatement identificarStmt = c.prepareStatement("SELECT NUM_CTA, NOMBRE, ESTADO,CONSENTIMIENTO, IMG_HUELLA FROM ALUMNOS WHERE NUM_CTA = ?");
            identificarStmt.setString(1, cuenta);
            ResultSet rs = identificarStmt.executeQuery();
            if(rs.next()){
                String nombre = rs.getString("NOMBRE");
                JOptionPane.showConfirmDialog(null, "El numero de cuenta "+cuenta+" está registrado a nombre de: "+nombre+"\n Verifique con el alumno", "Identificación con NUMERO DE CUENTA", JOptionPane.OK_CANCEL_OPTION );

                String mensaje = "Daño a equipo";
                int resp = JOptionPane.showConfirmDialog(null, "¿Desea registrar OBSERVACIONES en el equipo?",//<- EL MENSAJE 
                        "Alerta!"/*<- El título de la ventana*/, JOptionPane.YES_NO_OPTION/*Las opciones (si o no)*/, JOptionPane.WARNING_MESSAGE/*El tipo de ventana, en este caso WARNING*/);
                //System.out.println(resp); // SI = 0, NO = 1
                if (resp == 0) { //Si responde si
                    agregarObservacionEquipo(true);
                }

                JDateChooser jd = new JDateChooser();
                String message = "Selecciona fecha";
                Object[] params = {message, jd};
                String fPenalizacion = JOptionPane.showInputDialog(null, params, "Fecha Penalización", JOptionPane.OK_CANCEL_OPTION);
                String datePenalizacion = "";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                datePenalizacion = sdf.format(((JDateChooser) params[1]).getDate());
                System.out.println("Fecha de termino de penalizacion " + datePenalizacion);
                Calendar dateNow = Calendar.getInstance();
                String fechaNow = sdf.format(new Date());
                int hNow = dateNow.get(Calendar.HOUR_OF_DAY);
                String mNow = Integer.toString(dateNow.get(Calendar.MINUTE));
                if (mNow.length() == 1) {
                    mNow = "0" + mNow;
                }
                String sNow = Integer.toString(dateNow.get(Calendar.SECOND));
                if (sNow.length() == 1) {
                    sNow = "0" + sNow;
                }
                String hourNow = Integer.toString(hNow) + ":" + mNow + ":" + sNow;
                try {
                    PreparedStatement penalizaUser = c.prepareStatement("INSERT INTO PENALIZACIONES (EDO_PENALIZA,FECHA_INICIO, FECHA_FIN_PENALIZA, HORA_PENALIZA, LAB_PENALIZA, RAZON_PENALIZA,TIPO_PENALIZACION,MULTA,FK_NUM_CTA,DIA_DESPENALIZA,HORA_DESPENALIZA,LAB_DESPENALIZA) VALUES "
                            + "('1',?,?,?,?,?,?,?,?,?,?,?)");
                    penalizaUser.setString(1, fechaNow);
                    penalizaUser.setString(2, datePenalizacion);
                    penalizaUser.setString(3, hourNow);
                    penalizaUser.setString(4, "DEPTO TEC");
                    penalizaUser.setString(5, mensaje);
                    penalizaUser.setString(6, "2");
                    penalizaUser.setString(7, "0.0");
                    penalizaUser.setString(8, cuenta);
                    penalizaUser.setString(9, datePenalizacion);
                    penalizaUser.setString(10, hourNow);
                    penalizaUser.setString(11, "SIN LAB");
                    System.out.println(penalizaUser);
                    penalizaUser.execute();
                    penalizaUser.close();
                    JOptionPane.showMessageDialog(null, "Se ha registrado la penalizacion", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    //fechaPenaliza = datePenalizacion;
                    
                    DateTimeFormatter dtf5 = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                    System.out.println("yyyy/MM/dd-> " + dtf5.format(LocalDateTime.now()));
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                    System.out.println("HH:mm:ss-> " + dtf.format(LocalDateTime.now()));
                    PreparedStatement registroLog = c.prepareStatement("INSERT INTO LOGS(NAME_USUARIO, ACCION, FECHA_ACCION, HORA_ACCION) values(?, ?, ?, ?)", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    registroLog.setString(1, usuario);
                    registroLog.setString(2, "Penalizó al alumno: "+nombre+"con numero de cuenta"+cuenta);
                    registroLog.setString(3, dtf5.format(LocalDateTime.now()));
                    registroLog.setString(4, dtf.format(LocalDateTime.now()));
                    registroLog.execute();
                    registroLog.close();

                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, "Ocurrió un error al registrar la penalización\nExcepción es " + e + " y su descripcion:" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    conexionConsulta.desconectar();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Ocurrió un error al registrar la penalización\n El alumno NO EXISTE", "Error", JOptionPane.ERROR_MESSAGE);
            }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Ocurrió un error al registrar la penalización\nExcepción es " + e + " y su descripcion:" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void agregarUsuario(){
        Connection c = conexionConsulta.conectar();
        try {
            String ListaGrupos[] = Obt_Grupos();
            Object lab = JOptionPane.showInputDialog(null, "Seleccione el GRUPO al que se le AGREGARA EL USUARIO ", "\n AGREGAR USUARIO", JOptionPane.QUESTION_MESSAGE, null, ListaGrupos, "01");
            String nameGrupo = lab.toString();
            PreparedStatement verificarStmt = c.prepareStatement("SELECT ID_GRUPO FROM GRUPOS WHERE NOM_GRUPO = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            verificarStmt.setString(1, nameGrupo);
            ResultSet rs1 = verificarStmt.executeQuery();
            if (rs1.next()) {
                String idGrupo = rs1.getString("ID_GRUPO");
                PreparedStatement verificarStmt1 = c.prepareStatement("SELECT DISTINCT ID_LAB FROM GRUPOLABORATORIO WHERE ID_GRUPO = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                verificarStmt1.setString(1, idGrupo);
                ResultSet rs = verificarStmt1.executeQuery();
                List<String> arregloLaboratorios = new ArrayList<String>();                               
                while (rs.next()) {
                    arregloLaboratorios.add(rs.getString("ID_LAB"));                    
                }
                String ListaUsuarios[] = Obt_RolesSinGrupo();
                Object user = JOptionPane.showInputDialog(null, "Seleccione el USUARIO que se le AGREGARA AL GRUPO", "\n AGREGAR USUARIO", JOptionPane.QUESTION_MESSAGE, null, ListaUsuarios, "01");
                String usuario= user.toString();
                PreparedStatement verificarStmt2 = c.prepareStatement("SELECT ID_USUARIO FROM USUARIOS WHERE NAME_USUARIO = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                verificarStmt2.setString(1, usuario);
                ResultSet rs2 = verificarStmt2.executeQuery();
                int grupoCreado = 0;
                int UsuarioAgregado = 0;
                if (rs2.next()){
                    String idUsuario = rs2.getString("ID_USUARIO");
                    int laboratorios = arregloLaboratorios.size();
                    while (laboratorios > 0){
                        PreparedStatement agregarStmt2 = c.prepareStatement("INSERT INTO GRUPOLABORATORIO(ID_GRUPO, ID_LAB, ID_USUARIO ) VALUES (?, ?, ? )", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);                        
                        agregarStmt2.setString(1, idGrupo);
                        agregarStmt2.setString(2, arregloLaboratorios.get(laboratorios - 1));
                        agregarStmt2.setString(3, idUsuario);                        
                        grupoCreado = agregarStmt2.executeUpdate();
                        if(grupoCreado < 1){
                            JOptionPane.showMessageDialog(null, "Ocurrió un error al Registrar al usuario", "Error", JOptionPane.ERROR_MESSAGE);
                            break;
                        } else {
                            UsuarioAgregado++;
                           laboratorios --; 
                        }
                        
                    }
                    if(UsuarioAgregado > 0){
                         JOptionPane.showMessageDialog(null, "El GRUPO se ha ACTUALIZADO con exito\nElementos ACTUALIZADOS en total: "+UsuarioAgregado, "Éxito", JOptionPane.INFORMATION_MESSAGE );
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Ocurrió un error al Registrar al usuario, NO SE ENCONTRÖ USUARIO", "Error", JOptionPane.ERROR_MESSAGE);
                }
                
            } else {
                JOptionPane.showMessageDialog(null, "Ocurrió un error al Registrar al usuario, NO SE ENCONTRÖ GRUPO", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch(SQLException e){
            JOptionPane.showMessageDialog(null, "Ocurrió un error al registrar la penalización\nExcepción es " + e + " y su descripcion:" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
    }
    
    public void eliminarUsuario(){
        Connection c = conexionConsulta.conectar();
        try {
            String ListaGrupos[] = Obt_Grupos();
            Object lab = JOptionPane.showInputDialog(null, "Seleccione el GRUPO al que se le ELIMINARA EL USUARIO ", "\n AGREGAR USUARIO", JOptionPane.QUESTION_MESSAGE, null, ListaGrupos, "01");
            String nameGrupo = lab.toString();
            PreparedStatement verificarStmt = c.prepareStatement("SELECT ID_GRUPO FROM GRUPOS WHERE NOM_GRUPO = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            verificarStmt.setString(1, nameGrupo);
            ResultSet rs1 = verificarStmt.executeQuery();
            if (rs1.next()) {
                String idGrupo = rs1.getString("ID_GRUPO");                
                String ListaUsuarios[] = Obt_RolesDeGrupo(idGrupo);
                Object user = JOptionPane.showInputDialog(null, "Seleccione el USUARIO que se ELIMINARA DEL GRUPO", "\n ELIMINAR USUARIO", JOptionPane.QUESTION_MESSAGE, null, ListaUsuarios, "01");
                String usuario = user.toString();
                PreparedStatement verificarStmt2 = c.prepareStatement("SELECT ID_USUARIO FROM USUARIOS WHERE NAME_USUARIO = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                verificarStmt2.setString(1, usuario);
                ResultSet rs2 = verificarStmt2.executeQuery();
                int grupoCreado = 0;                
                if (rs2.next()) {
                    String idUsuario = rs2.getString("ID_USUARIO");                    
                        PreparedStatement agregarStmt2 = c.prepareStatement("DELETE FROM GRUPOLABORATORIO WHERE ID_USUARIO = ? AND ID_GRUPO = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        agregarStmt2.setString(2, idGrupo);                        
                        agregarStmt2.setString(1, idUsuario);
                        grupoCreado = agregarStmt2.executeUpdate();
                        if (grupoCreado < 1) {
                            JOptionPane.showMessageDialog(null, "Ocurrió un error al Registrar al usuario", "Error", JOptionPane.ERROR_MESSAGE);                            
                        } else {
                            JOptionPane.showMessageDialog(null, "El GRUPO se ha ACTUALIZADO con exito\nElementos ACTUALIZADOS en total: " + grupoCreado, "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        }                                       
                } else {
                    JOptionPane.showMessageDialog(null, "Ocurrió un error al Registrar al usuario, NO SE ENCONTRÖ USUARIO", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } else {
                JOptionPane.showMessageDialog(null, "Ocurrió un error al Registrar al usuario, NO SE ENCONTRÖ GRUPO", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Ocurrió un error al registrar la penalización\nExcepción es " + e + " y su descripcion:" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        textArea = new javax.swing.JTextArea();
        botonLimpiar = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        botonCarritoNuevo = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jButton12 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jButton11 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        botonPrestamos = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(null);

        textArea.setBackground(new java.awt.Color(221, 234, 234));
        textArea.setColumns(20);
        textArea.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        textArea.setRows(5);
        textArea.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        jScrollPane1.setViewportView(textArea);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(469, 210, 870, 386);

        botonLimpiar.setText("Limpiar");
        botonLimpiar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        botonLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonLimpiarActionPerformed(evt);
            }
        });
        getContentPane().add(botonLimpiar);
        botonLimpiar.setBounds(930, 620, 118, 30);

        jButton1.setText("Salir");
        jButton1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(1230, 620, 115, 30);

        jTabbedPane1.setBackground(new java.awt.Color(153, 189, 191));
        jTabbedPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jTabbedPane1.setToolTipText("");
        jTabbedPane1.setFont(new java.awt.Font("Yu Gothic Medium", 2, 10)); // NOI18N
        jTabbedPane1.setMinimumSize(new java.awt.Dimension(97, 92));
        jTabbedPane1.setOpaque(true);

        jPanel2.setBackground(new java.awt.Color(153, 189, 191));

        botonCarritoNuevo.setText("Agregar laboratorio nuevo");
        botonCarritoNuevo.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        botonCarritoNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonCarritoNuevoActionPerformed(evt);
            }
        });

        jButton7.setText("Inutilizar laboratorio");
        jButton7.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton9.setText("Mostar laboratorio");
        jButton9.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(botonCarritoNuevo, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE))
                .addContainerGap(200, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addComponent(botonCarritoNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(266, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("         LABORATORIOS", jPanel2);

        jPanel3.setBackground(new java.awt.Color(153, 189, 191));

        jButton12.setText("Mostrar grupos");
        jButton12.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton14.setText("Eliminar grupo");
        jButton14.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jButton17.setText("Crear Grupo");
        jButton17.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        jButton13.setText("Actualizar grupo");
        jButton13.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jButton19.setText("Agregar Usuario");
        jButton19.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton19.setPreferredSize(new java.awt.Dimension(83, 19));
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        jButton20.setText("Eliminar Usuario");
        jButton20.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton13, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                    .addComponent(jButton14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(200, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addComponent(jButton17, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17)
                .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton19, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton20, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 144, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("             GRUPOS", jPanel3);

        jPanel4.setBackground(new java.awt.Color(153, 189, 191));

        jButton5.setText("Utilizar equipo");
        jButton5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton5.setPreferredSize(null);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton8.setText("Inutilizar equipo");
        jButton8.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton8.setPreferredSize(null);
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton10.setText("Mostrar equipos");
        jButton10.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton10.setPreferredSize(null);
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton4.setText("Agregar equipos a laboratorio");
        jButton4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton4.setPreferredSize(null);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton3.setText("Agregar Observaciones");
        jButton3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton3.setPreferredSize(null);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(200, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(184, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("             EQUIPOS", jPanel4);

        jPanel5.setBackground(new java.awt.Color(153, 189, 191));

        jButton11.setText("Crear Usuario");
        jButton11.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton15.setText("Borrar Usuario");
        jButton15.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jButton16.setText("Modificar Contraseña");
        jButton16.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        jButton18.setText("Mostrar Usuarios");
        jButton18.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton16, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                    .addComponent(jButton11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(200, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton18, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton15, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton16, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(225, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("             USUARIOS", jPanel5);

        getContentPane().add(jTabbedPane1);
        jTabbedPane1.setBounds(30, 170, 408, 470);

        jButton2.setText("Historial Logs");
        jButton2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2);
        jButton2.setBounds(480, 620, 142, 30);

        botonPrestamos.setText("Volver a Prestamos");
        botonPrestamos.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        botonPrestamos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                botonPrestamosMouseClicked(evt);
            }
        });
        botonPrestamos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonPrestamosActionPerformed(evt);
            }
        });
        getContentPane().add(botonPrestamos);
        botonPrestamos.setBounds(1060, 620, 147, 30);

        jButton6.setText("Penalizar");
        jButton6.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton6);
        jButton6.setBounds(660, 620, 147, 30);

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ventana/extrafondo.jpg"))); // NOI18N
        getContentPane().add(jLabel2);
        jLabel2.setBounds(0, 150, 1360, 600);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ventana/titulo ajustado.jpeg"))); // NOI18N
        getContentPane().add(jLabel1);
        jLabel1.setBounds(0, 0, 1360, 150);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void botonCarritoNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonCarritoNuevoActionPerformed
        // TODO add your handling code here:
        // lo que sucede cuando se da clic en el boton carritoNuevo
        try{
            guardarCarrito(); //ejecuta el metodo para guardar huella y persona en bd
        }catch(SQLException ex){
            //Logger.getLogger(ventanasMuestra.class.getName()).log(Level.SEVERE, null, ex );
            java.util.logging.Logger.getLogger(adminEquipos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_botonCarritoNuevoActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        try{
            guardarEquipo(); //ejecuta el metodo para guardar huella y persona en bd
        }catch(SQLException ex){
            //Logger.getLogger(ventanasMuestra.class.getName()).log(Level.SEVERE, null, ex );
            java.util.logging.Logger.getLogger(adminEquipos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        String laboratorioInutilizado = inutilizarLaboratorio();
        if(laboratorioInutilizado != ""){
            try{
                mostrarCarritos(); //ejecuta el metodo para guardar huella y persona en bd
            }catch(SQLException ex){
                //Logger.getLogger(ventanasMuestra.class.getName()).log(Level.SEVERE, null, ex );
                java.util.logging.Logger.getLogger(adminEquipos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
            try{
                mostrarEquipos(laboratorioInutilizado); //ejecuta el metodo para guardar huella y persona en bd
            }catch(SQLException ex){
                //Logger.getLogger(ventanasMuestra.class.getName()).log(Level.SEVERE, null, ex );
                java.util.logging.Logger.getLogger(adminEquipos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
            try {
                // TODO add your handling code here:
                mostrarGrupos();
            } catch (SQLException ex) {
                Logger.getLogger(adminEquipos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        // TODO add your handling code here:
        formularioRolGrupo ch = new formularioRolGrupo(this , true);
        ch.setVisible(true);            
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        if(project2.conexionConsulta.con == null){
        }else{
        conexionConsulta.desconectar();
        }
        DateTimeFormatter dtf5 = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        System.out.println("yyyy/MM/dd-> " + dtf5.format(LocalDateTime.now()));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        System.out.println("HH:mm:ss-> " + dtf.format(LocalDateTime.now()));
        Connection c = conexionConsulta.conectar();
        try (PreparedStatement registroLog = c.prepareStatement("INSERT INTO LOGS(NAME_USUARIO, ACCION, FECHA_ACCION, HORA_ACCION) values(?, ?, ?, ?)", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            registroLog.setString(1, usuario);
            registroLog.setString(2, "Salio del sistema");
            registroLog.setString(3, dtf5.format(LocalDateTime.now()));
            registroLog.setString(4, dtf.format(LocalDateTime.now()));
            registroLog.execute();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Ocurrió un error al INSERTAR EN LOGS" + ex, "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            System.exit(0);
        }
        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // TODO add your handling code here:
        try{
            mostrarCarritos(); //ejecuta el metodo para guardar huella y persona en bd
        }catch(SQLException ex){
            //Logger.getLogger(ventanasMuestra.class.getName()).log(Level.SEVERE, null, ex );
            java.util.logging.Logger.getLogger(adminEquipos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        // TODO add your handling code here:
        
        try{
            mostrarEquipos(""); //ejecuta el metodo para guardar huella y persona en bd
        }catch(SQLException ex){
            //Logger.getLogger(ventanasMuestra.class.getName()).log(Level.SEVERE, null, ex );
            java.util.logging.Logger.getLogger(adminEquipos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_jButton10ActionPerformed

    private void botonLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonLimpiarActionPerformed
        // TODO add your handling code here:
        //lo que ocurre cuando se da clic en el boton de Reset
        try{
            textArea.setText(null);
            
        }catch(Exception ex){
            java.util.logging.Logger.getLogger(ventanasMuestra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_botonLimpiarActionPerformed

    private void botonPrestamosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botonPrestamosMouseClicked
        // TODO add your handling code here:

        formularioGrupo ch = new formularioGrupo();
        ch.setVisible(true);
        this.dispose();

    }//GEN-LAST:event_botonPrestamosMouseClicked

    private void botonPrestamosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonPrestamosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_botonPrestamosActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        String lab = inutilizarEquipo(true);
        try{
            mostrarEquipos(lab); //ejecuta el metodo para guardar huella y persona en bd
        }catch(SQLException ex){
            //Logger.getLogger(ventanasMuestra.class.getName()).log(Level.SEVERE, null, ex );
            java.util.logging.Logger.getLogger(adminEquipos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        // TODO add your handling code here:
         try{
            String nombre = guardarGrupo();
            if (nombre.equals("")){
                System.err.println("No se pudo crear grupo");
            }else{
            CatalogoUsuariosGrupo ch = new CatalogoUsuariosGrupo(this, true, nombre);
            ch.setVisible(true);
            mostrarGrupos();
            }
        }catch(SQLException ex){
            java.util.logging.Logger.getLogger(adminEquipos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton17ActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        
        
    }//GEN-LAST:event_formWindowOpened

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        try {
            // TODO add your handling code here:
            mostrarGrupos();
        } catch (SQLException ex) {
            Logger.getLogger(adminEquipos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
        try {
            // TODO add your handling code here:
            mostrarRoles();
        } catch (SQLException ex) {
            Logger.getLogger(adminEquipos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton18ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        // TODO add your handling code here:
        borrarRol();
    }//GEN-LAST:event_jButton15ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        // TODO add your handling code here:      
        actualizarGurpo();
        try {
            mostrarGrupos();
        } catch (SQLException ex) {
            Logger.getLogger(adminEquipos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        // TODO add your handling code here:
        eliminarGrupo();
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        // TODO add your handling code here:
        formularioRolGrupoPass ch = new formularioRolGrupoPass(this , true);
        ch.setVisible(true); 
    }//GEN-LAST:event_jButton16ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
        String lab = inutilizarEquipo(false);
        try{
            mostrarEquipos(lab); //ejecuta el metodo para guardar huella y persona en bd
        }catch(SQLException ex){
            //Logger.getLogger(ventanasMuestra.class.getName()).log(Level.SEVERE, null, ex );
            java.util.logging.Logger.getLogger(adminEquipos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton8ActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_formWindowClosed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        cerrar();
    }//GEN-LAST:event_formWindowClosing

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            // TODO add your handling code here:
            mostrarLogs();
        } catch (SQLException ex) {
            Logger.getLogger(adminEquipos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        String lab =  agregarObservacionEquipo(false);
        try {
            mostrarEquipos(lab); //ejecuta el metodo para guardar huella y persona en bd
        } catch (SQLException ex) {
            //Logger.getLogger(ventanasMuestra.class.getName()).log(Level.SEVERE, null, ex );
            java.util.logging.Logger.getLogger(adminEquipos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        penalizarAlumno();        
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
        // TODO add your handling code here:
        agregarUsuario();
    }//GEN-LAST:event_jButton19ActionPerformed

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
        // TODO add your handling code here:
        eliminarUsuario();
    }//GEN-LAST:event_jButton20ActionPerformed

    /**
     * @param args the command line arguments
     */
    /*public static void main(String args[]) {
      
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(adminEquipos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(adminEquipos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(adminEquipos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(adminEquipos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

       
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new adminEquipos().setVisible(true);
            }
        });
    }*/

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botonCarritoNuevo;
    private javax.swing.JButton botonLimpiar;
    private javax.swing.JButton botonPrestamos;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea textArea;
    // End of variables declaration//GEN-END:variables
}
