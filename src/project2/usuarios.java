/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package project2;

import java.io.ByteArrayInputStream;
import ventana.ventanasMuestra;

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
import java.io.ByteArrayInputStream;
import project2.usuarios;
import java.sql.*;

/**
 *
 * @author MarBugaboo
 */
public class usuarios {
    
    String nombre;
    String numero;
    ByteArrayInputStream datosHuella;
    Integer tamanioHuella;

    public usuarios(String nombre, String numero, ByteArrayInputStream datosHuella, Integer tamanioHuella) {
        this.nombre = nombre;
        this.numero = numero;
        this.datosHuella = datosHuella;
        this.tamanioHuella = tamanioHuella;
    }

    

    public String getNombre() {
        return nombre;
    }

    public String getNumero() {
        return numero;
    }

    public ByteArrayInputStream getDatosHuella() {
        return datosHuella;
    }

    public Integer getTamanioHuella() {
        return tamanioHuella;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public void setDatosHuella(ByteArrayInputStream datosHuella) {
        this.datosHuella = datosHuella;
    }

    public void setTamanioHuella(Integer tamanioHuella) {
        this.tamanioHuella = tamanioHuella;
    }
    
    

    
    
}
