/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package project2;
import java.sql.*;
import javax.swing.JTextField;
import ventana.ventanasMuestra;



import java.io.ByteArrayInputStream;
import javax.swing.JOptionPane;
import ventana.ventanasMuestra;
import project2.conexionConsulta;

import com.digitalpersona.onetouch.DPFPDataPurpose;
import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.DPFPTemplate;
import com.digitalpersona.onetouch.capture.DPFPCapture;
import com.digitalpersona.onetouch.capture.event.DPFPDataAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPDataEvent;
import com.digitalpersona.onetouch.capture.event.DPFPErrorAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPErrorEvent;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusEvent;
import com.digitalpersona.onetouch.capture.event.DPFPSensorAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPSensorEvent;
import com.digitalpersona.onetouch.processing.DPFPEnrollment;
import com.digitalpersona.onetouch.processing.DPFPFeatureExtraction;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;
import com.digitalpersona.onetouch.verification.DPFPVerification;
import java.awt.Image;
import java.sql.ResultSet;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import project2.conexionConsulta;
import project2.formulario;
import project2.usuarios;
import ventana.ventanasMuestra;

import java.time.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author MarBugaboo
 */
public class conexionConsulta {
    
    public static Connection con ;
    static Statement setmt;
    static ResultSet rs;


    /*public static Connection conectar(){
        
        
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://132.248.62.56:3306/LABORATORIOS","testhuella","Hu3ll4s.*21");
            setmt = con.createStatement();
            System.out.println("Conectado");
            
        } catch (Exception exception ){
            System.out.println("No conectado");
        }
         
        return con;
        
    }*/
    
    public static Connection conectar(){
        
        
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://132.248.62.56:3306/LABORATORIOS","testhuella","Hu3ll4s.*21");
            //con = DriverManager.getConnection("jdbc:mysql://localhost:3306/LABORATORIOS","root","");
            setmt = con.createStatement();
            System.out.println("Conectado");
            
        } catch (Exception exception ){
            System.out.println("No conectado");
        }
        
        return con;
            
        
    }
    
    /*public static void guardar (String nombre, String numero, String datosHuella, String tamanioHuella){
        
        Connection c = conexionConsulta.conectar();

        
        System.out.println("voy a guardar");
        
        try{
                //Establece los valores para la sentencia SQL
                //Time t = new Time();
                //String hora = t.getTime();
                System.out.println("Estoy dentro del try ");
                PreparedStatement guardarStmt = c.prepareStatement("INSERT INTO usuarios (IdUsuarios,nombre, num_cuenta, huella ) VALUES (null,?,?,?)");
                //PreparedStatement guardarStmt = c.prepareStatement("INSERT INTO ALUMNOS(NUM_CTA, NOMBRE, IMG_HUELLA, LAB_CREA_USU, DIA_CREA_USU, HORA_CREA_USU) VALUES (?, ?, ?, 'LABMOVIL', CURDATE(), ? )");
                System.out.println("Guarde la sentencia");
                guardarStmt.setString(2, nombre);
                System.out.println("Prepare nombre" .concat(nombre));
                guardarStmt.setString(3, numero);
                System.out.println("Prepare numero" .concat(numero));
                System.out.println("Prepare numero");
                String huella = datosHuella.concat(tamanioHuella);
                System.out.println(huella);
                guardarStmt.setString(4, huella);
                System.out.println("Estoy aqui!!");
                //guardarStmt.setString(4, hora);
               
                //Ejecuta la sentencia preparada
                guardarStmt.execute();
                guardarStmt.close();
                JOptionPane.showMessageDialog(null, "El usuario se ha registrado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE );
                //cn.desconectar();
                
            }catch(SQLException ex){
                if(ex instanceof SQLIntegrityConstraintViolationException ){
                    //Si ocurre una excepcion de integridad de restriccion en primary key, lo indica
                    JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de guardar los datos!!! \nIdentificador duplicado '"+numero+"' para el registro de usuario\n ** Los datos no se guardaron en la base de datos *** \nCorrija o use el panel de Identificación de Usuarios", "Error", JOptionPane.ERROR_MESSAGE );
                }
        

          
            }
    }*/
    
    /**
     *
     * @param usuarios
     */
    
    /*public static void consultar (usuarios usuarios){
      try {
        
            System.out.println("Haré la consulta");
             String query="SELECT * FROM ALUMNOS WHERE NUM_CTA ='" +usuarios.getNumero()+"'"; 
             rs = setmt.executeQuery(query);
                    System.out.println("El numero de cuentra ingresado es:"+ usuarios.getNumero());
                    while (rs.next()){
                        System.out.println(rs.getString("NUM_CTA"));System.out.println(rs.getString(1));
                        System.out.println(rs.getString(2)); System.out.println(rs.getString(3));
                    }
                    
         
        } catch (Exception e) {
            System.out.println("Error");
        }  
        
    
        
    }*/
    
    public static void desconectar (){
    
        try {
            con.close();
            System.out.println("Desconectado");
            //JOptionPane.showMessageDialog(null, " Se a desconectado");
        } catch (SQLException ex) {
            
            JOptionPane.showMessageDialog(null, " !No se puede realizar la desconexion! \n");
            
        }
    }
    
    public static ResultSet consultar (String sql){
        
        ResultSet res = null;
        try{
            PreparedStatement pstm = con.prepareStatement(sql);
            res = pstm.executeQuery();
            
        } catch (SQLException e) {
            
            System.err.println("Error em la consulta:" + e.getMessage());;
            
        }
        
        return res;
        
    }
    
    

    public conexionConsulta() {
    }
}
