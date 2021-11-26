/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.project1;
import java.sql.*;

/**
 *
 * @author MarBugaboo
 */
public class conexionConsulta {
    static Connection conexion = null;
    static Statement sentencia;
    public static void conectar(){

        try{
            Class.forName("con.sysql.jdbc.Driver");
            conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/formulariobd","user","12345");
            sentencia = conexion.createStatement();
            System.out.println("Conectado");
            
        } catch (Exception exception ){
            System.out.println("No conectado");
        }
    }
    
    public static void guardar (formulario formulario){
        
        String consulta = "INSERT INTO usuarios VALUES ('" +formulario.getNombreUsuario()+"','" +formulario.getCorreoUsuario()+"','"+formulario.getContraseñaUsuario()+"')";
        
        try {
            sentencia.executeUpdate(consulta);
            System.out.println("Correcto");
        } catch (Exception e) {
            System.out.println("Error");
        }
        
    }
    
    
}
