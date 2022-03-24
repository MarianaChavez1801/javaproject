/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ventana;



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
import com.digitalpersona.onetouch.verification.DPFPVerificationResult;
import com.toedter.calendar.JDateChooser;
import java.awt.Font;

import java.util.logging.Logger.*;
import java.util.logging.*;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;


import java.time.*; // Este paquete contiene LocalDate, LocalTime y LocalDateTime.
import java.time.format.*;  // Este paquete contiene DateTimeFormatter.
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import project2.conexionConsulta;
import project2.formulario;
import ventana.inicio;
import ventana.adminEquipos;
import ventana.formularioRolGrupo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.charset.StandardCharsets;
import project2.usuarios;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.time.*;
import javax.swing.JFrame;
import javax.swing.JLabel;


/**
 *
 * @author MarBugaboo
 */
public class ventanasMuestra extends javax.swing.JFrame{
   
    /**
     * Creates new form ventanasMuestra
     */
    
        ResultSet rs;
        public String grupo;
        public String usuario;
        
        
    public ventanasMuestra() {
        //initComponents();
        conexionConsulta.conectar();
        cerrar();
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Imposible modificar el tema visual", "LookAndFeel inválido", JOptionPane.ERROR_MESSAGE);
        }
        initComponents();
        //System.err.println("ESTE ES EL GRUPO"+grupo);
        setLocationRelativeTo(null); //para colocar el jframe en el centro de la pantalla  
        this.setExtendedState(6);
        dispose();
    }
    
     
     //5 variables principales que sirven para captura, insercion y template de la huella que se captura
    private DPFPCapture Lector = DPFPGlobal.getCaptureFactory().createCapture(); //variable que inicializa el dispositivo y sus metodos
    public DPFPEnrollment Reclutador = DPFPGlobal.getEnrollmentFactory().createEnrollment(); //permite establecer las capturas de las huellas para determinar caracteristicas y estimar la creacion del template para luego guardarla
    private DPFPVerification Verificador = DPFPGlobal.getVerificationFactory().createVerification(); //tambien captura huella del lector, y crea caracteristicas para autentificar o verificar con alguna almacenada en la BD
    public DPFPTemplate template; //para crear el template de la huella despues de haber creado sus caracteristicas
    public static String TEMPLATE_PROPERTY = "template";
    public String huellaPrueba = "";
    
    /************
     * Las dos Variables más importantes, que se utilizaran para procesar las caracteristicas de la huella: para crear una nueva o para verificarla
     ************
     */
    public DPFPFeatureSet featuresinscripcion;
    public DPFPFeatureSet featuresverificacion;

    ventanasMuestra(String laboratorioSeleccion) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
     protected  void Iniciar()
    {
        
        Lector.addDataListener(new DPFPDataAdapter(){
        @Override public void dataAcquired(final DPFPDataEvent e) {
            SwingUtilities.invokeLater(new Runnable() {@Override
                public void run() {
                    EnviarTexto("La Huella Digital ha sido Capturada");
                    ProcesarCaptura(e.getSample());
                }
            });}
        });
        
        Lector.addReaderStatusListener(new DPFPReaderStatusAdapter()
        {
            @Override public void readerConnected(final DPFPReaderStatusEvent e ){
                SwingUtilities.invokeLater(new Runnable() { @Override
                    public void run() {
                        EnviarTexto("El sensor de Huella Digital está Activado o Conectado");
                    }});}
            
            @Override public void readerDisconnected(final DPFPReaderStatusEvent e ){
                SwingUtilities.invokeLater(new Runnable(){ @Override
                    public void run(){
                        EnviarTexto("El sensor de Huella Digital está Desactivado o No Conectado");
                    }});}
        });
        
        
        Lector.addSensorListener(new DPFPSensorAdapter()
        {
            @Override public void fingerTouched(final DPFPSensorEvent e ){
                SwingUtilities.invokeLater(new Runnable() { @Override
                        public void run(){
                            EnviarTexto("El dedo ha sido colocado sobre el Lector de Huella");
                        }});}
            
            @Override public void fingerGone(final DPFPSensorEvent e ){
                SwingUtilities.invokeLater(new Runnable() { @Override
                    public void run(){
                        EnviarTexto("El dedo ha sido quitado del Lector de Huella, continúe");
                    }});}
        });
        
        
        Lector.addErrorListener(new DPFPErrorAdapter()
        {
            public void errorReader(final DPFPErrorEvent e){
                SwingUtilities.invokeLater(new Runnable() {@Override 
                    public void run(){
                        EnviarTexto("¡¡¡Ha ocurrido un Error!!! : "+e.getError() );
                        JOptionPane.showMessageDialog(null, "¡¡¡Ha ocurrido un Error!!! : "+e.getError(), "Error", JOptionPane.ERROR_MESSAGE );
                    }});}
        });
        
    } /////fin metodo iniciar
    
     public void resetHuella(){
       featuresverificacion = null;
       labelImagenHuella.setIcon(null); 
    }
    
    
    /**
     * Variables que se utilizaran para procesar las caracteristicas de la huella, para crearla o verificarla
     */
    //public DPFPFeatureSet featuresinscripcion;
    //public DPFPFeatureSet featuresverificacion;
    
    /**
     * 
     * Definicion de metodos que complementan al metodo ProcesarCaptura()
     * 
     */
    //metodo que va a extraer las caracteristicas de la huella
    public DPFPFeatureSet extraerCaracteristicas(DPFPSample sample, DPFPDataPurpose purpose ){
        DPFPFeatureExtraction extractor = DPFPGlobal.getFeatureExtractionFactory().createFeatureExtraction();
        try{
            return extractor.createFeatureSet(sample, purpose);
        }catch(DPFPImageQualityException e ){
            return null;
        }
    } //fin metodo extraerCaracteristicas
    
    //metodo para crear la imagen de la huella
    public Image CrearImagenHuella(DPFPSample sample ){ 
        return DPFPGlobal.getSampleConversionFactory().createImage(sample);
    }//fin metodo CrearImagenHuella
    
   // metodo que va a dibular la imagen de la huella en el label del formulario
    public void DibujarHuella(Image image){
        labelImagenHuella.setIcon(new ImageIcon(
               image.getScaledInstance(labelImagenHuella.getWidth(), 
                        labelImagenHuella.getHeight(),
                        Image.SCALE_DEFAULT)));
        repaint();
    }//fin DibujarHuella
    
    //metodo que va a obtener el estado de la huella
    public void EstadoHuellas(){ 
        EnviarTexto("Muestra de Huellas Necesarias para Guardar/Actualizar Huella: " + Reclutador.getFeaturesNeeded() );
    }//fin EstadoHuellas
    
    //metodo que va a enviar el texto al textArea del formulario
    public void EnviarTexto(String string){
       textArea.append(string + "\n");
    }//fin EnviarTexto
    
    //metodo que indicara cuando se este utilizando el lector biometrico
    public void start(){
        Lector.startCapture();
        EnviarTexto("Utilizando el Lector de Huella Digital");
    }//fin start
    
    //metodo que indicara cuando se deje de utilizar el lector biometrico
    public void stop(){
        Lector.stopCapture();
        EnviarTexto("No se esta usando el Lector de Huella Digital");
    }//fin stop

    
    
    /**
     * Fin de metodos que complementan a ProcesarCaptura()
     */
    public DPFPTemplate getTemplate(){ //getter
        return template;
    }//fin getTemplate
    
    
    public void setTemplate(DPFPTemplate template ){ //setter
        DPFPTemplate old = this.template;
        this.template = template;
        firePropertyChange(TEMPLATE_PROPERTY, old, template );
    }//fin setTemplate
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    
        public void ProcesarCaptura(DPFPSample sample ){
        //Procesar la muestra de la huella y crear un conjunto de caracteristicas con el proposito de inscripcion
        featuresinscripcion = extraerCaracteristicas(sample, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT );
        
        //Procesar la muestra de la huella y crear un conjunto de caracteristicas con el proposito de verificacion
        featuresverificacion = extraerCaracteristicas(sample, DPFPDataPurpose.DATA_PURPOSE_VERIFICATION );
        //huellaPrueba = new ByteArrayInputStream(template.serialize() ).toString();
        //Comprobar la calidad de la muestra de la huella y si es buena, lo añade a su reclutador si es bueno
        if(featuresinscripcion != null){
            try{
                //System.out.println("Las Caracteristicas de la Huella para verificacion han sido creadas, su hash es:"+featuresverificacion);
                Reclutador.addFeatures(featuresinscripcion); //Agregar las caracteristicas de la huella a la plantilla a crear
                
                //Dibuja la huella digital capturada
                Image image = CrearImagenHuella(sample);
                DibujarHuella(image);
                
                botonVerificar.setEnabled(true);
                botonIdentificar.setEnabled(true);
                botonPrestamo.setEnabled(true);
                botonDevolucion.setEnabled(true);
                
            }catch(DPFPImageQualityException ex){
                System.err.println("Error: "+ ex.getMessage());
            }finally{
                EstadoHuellas();
                //Comprueba si la plantilla se ha creado
                switch(Reclutador.getTemplateStatus()){
                    case TEMPLATE_STATUS_READY: //informe de exito y detiene la captura de huellas
                        stop();
                        setTemplate(Reclutador.getTemplate() );
                        EnviarTexto("Plantilla de Huella creada, puede dar de Alta/Actualizar al Alumno/Académico");
                        botonIdentificar.setEnabled(false); //los 4 botones presentes en la interfaz
                        botonIdentificar.setEnabled(true);
                        botonVerificar.setEnabled(true);
                        botonAlta.setEnabled(true);
                        botonAlta.grabFocus();
                        botonActualizarAgregandoHuella.setEnabled(true);
                        botonPrestamo.setEnabled(false);
                        botonDevolucion.setEnabled(false);
                    break;
                        
                    case TEMPLATE_STATUS_FAILED: //informe de fallas y reini
                        Reclutador.clear();
                        stop();
                        EstadoHuellas();
                        setTemplate(null);
                        JOptionPane.showMessageDialog(ventanasMuestra.this, "La Plantilla de la Huella no pudo ser creada. Vuelva a colocar su dedo.", "Advertencia", JOptionPane.WARNING_MESSAGE );
                    break;
                        
                }//fin switch
            }//fin try-catch-finally
        }//fin if
            
    }//fin metodo ProcesarCaptura
        

    /*public void guardarHuella(){
        //obtiene los datos del template de la huella actual
        //Establece los valores para la sentencia SQL
        System.out.println("Estoy en guardar huella");
        usuarios usuarios = null;
        System.out.println("Voy a asignar Valores");
        //usuarios = asignarValores();
        System.out.println("Sali de asignar valores");
        System.out.println("Voy a guardar los datos");
        //conexionConsulta.guardar(usuarios);
        System.out.println("Sali de guardar");
        
        
        
        botonAlta.setEnabled(false);
        botonActualizarAgregandoHuella.setEnabled(false);
        botonVerificar.grabFocus();
               
    }//fin metodo guardarHuella*/


    
    /*public usuarios asignarValores(){
        
        usuarios r = null;
        
        String nombre = JOptionPane.showInputDialog("Escribe el Nombre completo, empezando por apellidos:").toUpperCase() ;
        String numero = JOptionPane.showInputDialog("Escriba:\nNúmero de Cuenta sin guión (estudiante)\no RFC sin homoclave (académicos):").toUpperCase();
        
        //obtiene los datos del template de la huella actual
        //ByteArrayInputStream datosHuella = new ByteArrayInputStream(template.serialize() );
        String datosHuella = new ByteArrayInputStream(template.serialize() ).toString();
        System.out.println("Huella" + datosHuella);
        Integer TamanioHuella=template.serialize().length;
        String tamanioHuella = Integer.toString(TamanioHuella);
        System.out.println("Huella" + tamanioHuella);
        
        
        
        //Pregunta el nombre de la persona a la cual corresponde dicha huella
        
        System.out.println("Estoy en asignarValores y voy a verificar que halla datos para insertar");
        if((nombre != null) && (numero != null)){
            
            System.out.println("Si hay datos");
            
        }else{
            JOptionPane.showMessageDialog(null, "Se debe de dar algún valor en ambos campos. Vuelva a intentarlo", "Error", JOptionPane.ERROR_MESSAGE );
        }
        
        System.out.println("voy a enviar lo que voy a guardar");
        conexionConsulta.guardar(nombre, numero, datosHuella, tamanioHuella); 
        
        return r;
           
    }*/
        
        /**
     * Crea el objeto cn para llevar a cabo la conexion a la BD
     */
    //ConexionBD cn = new ConexionBD();//crea el objeto tipo conexion
    
    
    /**
     ************** Metodos que interactuan con la base de datos ***************
     * @param nom 
     */
    /** 
     * Metodo guardarHuella
     * @throws SQLException
     * Metodo que se emplea para almacenar los datos en la tabla de la base de datos,
     * previa extraccione de los datos de la huella digital
     */
    public void guardarHuella() throws SQLException{
        //obtiene los datos del template de la huella actual
        ByteArrayInputStream datosHuella = new ByteArrayInputStream(template.serialize() );
        Integer tamanioHuella=template.serialize().length;
        
        //Pregunta el nombre de la persona a la cual corresponde dicha huella
        String nombre = JOptionPane.showInputDialog("Escribe el Nombre completo, empezando por apellidos:").toUpperCase() ;
        String numero = JOptionPane.showInputDialog("Escriba:\nNúmero de Cuenta sin guión (estudiante)\no RFC sin homoclave (académicos):").toUpperCase();
        if((nombre != null) && (numero != null)){
            try{
                //Establece los valores para la sentencia SQL
                Connection c = conexionConsulta.conectar();
                
                //LocalDateTime t =LocalDateTime.now();
                //Time t = new Time();
                String fecha = new SimpleDateFormat("yyyy/MM/dd").format(Calendar.getInstance().getTime());
                String hora = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
                //PreparedStatement guardarStmt = c.prepareStatement("INSERT INTO usuarios (IdUsuarios,nombre, num_cuenta, huella ) VALUES (null,?,?,?)");
                PreparedStatement guardarStmt = c.prepareStatement("INSERT INTO ALUMNOS(NUM_CTA, NOMBRE, IMG_HUELLA, LAB_CREA_USU, DIA_CREA_USU, HORA_CREA_USU) VALUES (?, ?, ?, 'LABMOVIL', ?, ? )");
                guardarStmt.setString(1, numero);
                guardarStmt.setString(2, nombre);
                guardarStmt.setBinaryStream(3, datosHuella, tamanioHuella );
                guardarStmt.setString(4, fecha);
                guardarStmt.setString(5, hora);
                System.out.println("Esta es la hora" +hora);
               
                //Ejecuta la sentencia preparada
                guardarStmt.execute();
                guardarStmt.close();
                JOptionPane.showMessageDialog(null, "El usuario se ha registrado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE );
                conexionConsulta.desconectar();
                botonAlta.setEnabled(false);
                botonActualizarAgregandoHuella.setEnabled(false);
                botonVerificar.grabFocus();
            }catch(SQLException ex){
                if(ex instanceof SQLIntegrityConstraintViolationException ){
                    //Si ocurre una excepcion de integridad de restriccion en primary key, lo indica
                    JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de guardar los datos!!! \nIdentificador duplicado '"+numero+"' para el registro de usuario\n ** Los datos no se guardaron en la base de datos *** \nCorrija o use el panel de Identificación de Usuarios", "Error", JOptionPane.ERROR_MESSAGE );
                }
                else{
                    //Si ocurre alguna otra excepcion la indica
                    JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de guardar los datos!!! \n"+ex.getMessage()+"\n ** No se guardaron datos en la base de datos *** \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
                }
                System.err.println("¡¡¡Ocurrio un error al tratar de guardar los datos!!! \n\n ** No se guardaron datos en la base de datos *** \n Inténtelo nuevamente");
            }finally{
                conexionConsulta.desconectar();
            }
        }else{
            JOptionPane.showMessageDialog(null, "Se debe de dar algún valor en ambos campos. Vuelva a intentarlo", "Error", JOptionPane.ERROR_MESSAGE );
        }
    }//fin metodo guardarHuella
    
    /**
     * Método verificarHuella
     * @throws SQLException
     * Método que consulta los datos del usuario por la tabla ALUMNOS,
     * previa introduccion de su numero de cuenta / RFC.
     * Asi mismo, verifica si tiene registrada huella digital o no, y lo indica
     * 
     * @param cuenta 
     */
    public void verificarHuella(String numero){
        //System.out.println("eL #cta/RFC vale "+cuenta);
        System.out.println("Este es el numero de cuenta" +numero);
        if(numero.length() > 0 )
        {
            System.out.println("Dentro de if");
            try{
                System.out.println("Dentro de try");
                //Establece los valores para la sentencia SQL
                Connection c = conexionConsulta.conectar();
                //obtiene la plantilla correspondiente a la persona indicada
                //PreparedStatement verificarStmt = c.prepareStatement("SELECT IdUsuarios, nombre, num_cuenta, BIT_LENGTH(huella) FROM usuarios WHERE num_cuenta=?");
                PreparedStatement verificarStmt = c.prepareStatement("SELECT NOMBRE, ESTADO, BIT_LENGTH(IMG_HUELLA),CONSENTIMIENTO FROM ALUMNOS WHERE NUM_CTA=?");
                //PreparedStatement verificarStmt = c.prepareStatement("SELECT NOMBRE, BIT_LENGTH(IMG_HUELLA), ESTADO, FINALIZACION FROM usuarios_huella WHERE NUM_CTA=?");
                System.out.println("Guarde el query");
                verificarStmt.setString(1, numero);
                System.out.println("Consulta con numero");
                ResultSet rs = verificarStmt.executeQuery();
                System.out.println("Query ejecutado");
                
                //Si se encuentra el nombre en la base de datos
                if(rs.next()){
                    //Lee la plantilla de la base de datos
                    //byte templateBuffer[] = rs.getBytes("IMG_HUELLA");
                    String nombre = rs.getString("NOMBRE");
                    String huella = rs.getString("BIT_LENGTH(IMG_HUELLA)");
                    String estado = rs.getString("ESTADO");
                    String consentimiento = rs.getString("CONSENTIMIENTO");
                    String edoHuella = "";
                    
                    if(!huella.equals("0")){ edoHuella = " SÍ tiene huella digital registrada "; }
                    else{ edoHuella = " NO tiene huella digital registrada ";}
                    
                    String strEstado="Inscrito";
                    if(estado.equals("0")){
                        strEstado="NO INSCRITO";  
                    }
                    String strConsentimiento="OK";
                    if(consentimiento.equals("0")){
                        strConsentimiento="SIN CONSENTIMIENTO";  
                    }
                    
                    JOptionPane.showMessageDialog(null, "El #cta/RFC "+numero+" está a nombre de "+nombre+"\nEstado = "+strEstado+"\n Consentimiento = "+strConsentimiento+"\n y "+edoHuella+" su huella es "+huella, "Verificacion de #cta/RFC", JOptionPane.INFORMATION_MESSAGE );
                    //JOptionPane.showMessageDialog(null, "El #cta/RFC "+numero+" está a nombre de "+nombre+"\n y "+edoHuella+" su huella es "+huella, "Verificacion de #cta/RFC", JOptionPane.INFORMATION_MESSAGE );

                }//fin si se encuentra el nombre en la base de datos
                else{
                    JOptionPane.showMessageDialog(null, "No existe ningún registro para #cta/RFC "+numero, "Verificación de Huella", JOptionPane.ERROR_MESSAGE );
                }
            }catch(SQLException e){
                //si ocurre un error lo indica en la consola
                System.err.println("Error al verificar el número de cuenta "+numero);
                JOptionPane.showMessageDialog(null, "Error al verificar el #cta/RFC "+numero, "Error", JOptionPane.ERROR_MESSAGE);
            }finally{
                conexionConsulta.desconectar();
            }//fin try-catch-finally
        }else
        {   
            JOptionPane.showMessageDialog(null, "Para verificar, debe darse algún valor de #cta/RFC", "Error", JOptionPane.ERROR_MESSAGE );
        }
    }//fin metodo verificarHuella
    
        //Actualiza el arreglo de las huellas digitales
    public boolean actualizarArregloLocalHuellas() throws Exception{
        boolean arregloAct = false;
        try{EnviarTexto("\nSe activó la función de actualizar el arreglo local de las huellas");
            Connection c = conexionConsulta.conectar();
            //consulta y obtiene todas las huellas de la bd
            
            System.out.println("Hice la conexion");
            //PreparedStatement identificarStmt = c.prepareStatement("SELECT num_cuenta, nombre, huella FROM usuarios", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            PreparedStatement identificarStmt = c.prepareStatement("SELECT NUM_CTA, NOMBRE, ESTADO, IMG_HUELLA,CONSENTIMIENTO FROM ALUMNOS", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            /*ResultSet*/ rs = identificarStmt.executeQuery();
            System.out.println("Ejecute el query");
            //EnviarTexto("\n ////////// Se ejecutó la query para traer a todos los usuarios con huella registrada ////// \n");
            //System.out.println("\n////////// Se ejecutó la query para traer a todos los usuarios con huella registrada ////// \n");
            int cuentaHuellas = 0;
            if(rs.last()){
                cuentaHuellas = rs.getRow();
                //System.out.println("La cantidad de huellas son "+cuentaHuellas);
            }
            EnviarTexto("Lista local actualizado a "+cuentaHuellas+" huellas");
            rs.beforeFirst(); //debe regresarse antes del inicio el ResultSet para que recorra el array desde el inicio cuando entre al while
            arregloAct = true;
            return arregloAct;
        }catch(Exception e){
            //Si ocurre un error lo indica en la consola
            System.err.println("Ocurrio una excepcion de tipo "+e+" con mensaje:"+e.getMessage());
            return arregloAct;
            

        }
    }
    
        public void identificarHuella() throws Exception {
        //String datosHuella = this.huellaPrueba;
        try{EnviarTexto("\nSe activó la función de Identificar con Huella");
            //System.out.println("Se activó la función de Identificar con Huella");
            //Connection c = cn.conectar();
            //consulta y obtiene todas las huellas de la bd
            ///*PreparedStatement*/ identificarStmt = c.prepareStatement("SELECT NUM_CTA, NOMBRE, IMG_HUELLA FROM usuarios_huella");
            ///*ResultSet*/ rs = identificarStmt.executeQuery();
            //EnviarTexto("\n ////////// Se ejecutó la query para traer a todos los usuarios con huella registrada ////// \n");
            //System.out.println("\n////////// Se ejecutó la query para traer a todos los usuarios con huella registrada ////// \n");
            
        
            rs.beforeFirst(); //debe regresarse antes del inicio el ResultSet para que recorra el array desde el inicio cuando entre al while
            System.out.println("Ya regrese el rs");
            while(rs.next())
            {   //lee la plantilla de la base de datos             
                byte templateBuffer[] = rs.getBytes("IMG_HUELLA");
                String nombre = rs.getString("NOMBRE");
                String cuenta = rs.getString("NUM_CTA");
                //String estado = rs.getString("ESTADO");
                //String consentimiento = rs.getString("CONSENTIMIENTO");
                //System.out.println("El resultado donde NOMBRE  es "+nombre+" y NUM_CTA es "+cuenta+"\nESTADO es "+estado);
                try{    //crea una nueva plantilla a partir de la guardada en la base de datos
                    DPFPTemplate referenceTemplate = DPFPGlobal.getTemplateFactory().createTemplate(templateBuffer);
                    //envia la plantilla creada al objeto contenedor de Template del componente de huella digital
                    setTemplate(referenceTemplate); 
                }catch(Exception e){
                    //("Excepcion es "+e+" mensaje "+e.getMessage());
                }
                //Compara las caracteristicas de la huella recientemente capturada con alguna plantilla gurdada en la base de datos que coincide con ese tipo
                DPFPVerificationResult result = Verificador.verify(featuresverificacion , getTemplate());
                //EnviarTexto("Comparando la huella del numero de cuenta "+cuenta+" con la actual:"+result.toString()+" y el featuresverification es "+featuresverificacion.toString()); 
                //compara las plantillas (actual vs bd)
                //Si encuentra correspondencia dibuja el mapa e indica el nombre de la persona que coincidio
                if(result.isVerified()){
                    
                    System.out.println("Resultado");
                    //crea la imagen de los datos guardados de las huellas guardadas en la base de datos
                    //String strEstado="Inscrito";
                    /*if(estado.equals("0")){
                        strEstado="NO INSCRITO";  
                    }
                     String strConsentimiento="OK";
                    if(consentimiento.equals("0")){
                        strConsentimiento="SIN CONSENTIMIENTO";  
                    }*/
                    
                   
                    
                        JOptionPane.showMessageDialog(null, "La huella digital está registrada a nombre de: "+nombre+"\n y Número de Cuenta/RFC: "+cuenta, "\nIdentificación de Huella =", JOptionPane.INFORMATION_MESSAGE );            
                        return;
                }//fin if
            }//fin while
            //Si no encuentra alguna huella correspondiente al nombre, lo indica con un mensaje
            JOptionPane.showMessageDialog(null, "No existe ningun registro que coincida con la huella\n\nTip: Verifique si el usuario sólo está dado de alta con su Número de Cuenta (interfaz web de encargado).\nSi es el caso, actualice la huella. En caso contrario, délo de Alta.", "Verificacion de Huella", JOptionPane.ERROR_MESSAGE);
            setTemplate(null);
        }catch(SQLException e){
            //Si ocurre un error lo indica en la consola
            System.err.println("Error al identificar huella digital."+e.getMessage());
            JOptionPane.showMessageDialog(null, "Error al identificar la huella\nUtilice la funcion Verificar", "Error", JOptionPane.ERROR_MESSAGE );
            start(); //vuelve a inicializar la captura de huella y lo indica
            //JOptionPane.showMessageDialog(null, "Esta huella no se encontró registrada en la base de datos\nPruebe actualizar el registro de un Alumno/Académico", "Verificacion de Huella", JOptionPane.ERROR_MESSAGE);
        }
        catch(NullPointerException e){ //para cuando se hizo Reset y aun así intenta identificarse una huella
            //System.err.println("No hay huella para trabajar con ella");
            JOptionPane.showMessageDialog(null, "No hay huella para trabajar con ella, capture alguna con el lector", "Excepción", JOptionPane.ERROR_MESSAGE);
            start(); //vuelve a inicializar la captura de huella y lo indica
        }
        catch(Exception e){
            //Si ocurre un error lo indica en la consola
            System.err.println("Ocurrio una excepcion de tipo "+e+" con mensaje:"+e.getMessage());
            JOptionPane.showMessageDialog(null, "Ocurrio una excepcion de tipo "+e+" con mensaje:"+e.getMessage(), "Excepción", JOptionPane.ERROR_MESSAGE );
            start(); //vuelve a inicializar la captura de huella y lo indica

        }
        /*
        finally{
            cn.desconectar();
        }//fin try-catch-finally
        */
    }//fin metodo identificarHuella

    //Actualiza el registro previo de una persona para agregar huella digital 
    public void actualizarAgregandoHuella() throws Exception {
        //obtiene los datos del template de la huella actual
        
        ByteArrayInputStream datosHuella = new ByteArrayInputStream(template.serialize() );
        System.out.println("Al menos entre aqui?????");
        Integer tamanioHuella=template.serialize().length;
        
        //Pregunta el nombre de la persona a la cual corresponde dicha huella
        String numero = JOptionPane.showInputDialog("Número de Cuenta o RFC para agregar la huella ya capturada:");
        if(numero != null){
            try{
                //Establece los valores para la sentencia SQL
                Connection c = conexionConsulta.conectar();
                PreparedStatement guardarStmt = c.prepareStatement("UPDATE ALUMNOS SET IMG_HUELLA = ? WHERE NUM_CTA = ?");
                guardarStmt.setString(2, numero);
                
                guardarStmt.setBinaryStream(1, datosHuella, tamanioHuella );
               
                //Ejecuta la sentencia preparada
                guardarStmt.execute();
                guardarStmt.close();
                JOptionPane.showMessageDialog(null, "El registro de la persona y su huella han sido guardados correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE );
                conexionConsulta.desconectar();
                botonAlta.setEnabled(false);
                botonActualizarAgregandoHuella.setEnabled(false);
                botonVerificar.grabFocus();
                actualizarArregloLocalHuellas();
            }catch(SQLException ex){
                //Si ocurre un error lo indica en la consola
                System.err.println("¡¡¡Ocurrio un error al tratar de guardar los datos!!! \n\n ** No se guardaron datos en la base de datos *** \n Intentelo nuevamente");
                JOptionPane.showMessageDialog(null, "¡¡¡Ocurrio un error al tratar de guardar los datos!!! \n"+ex.getMessage()+"\n ** No se guardaron datos en la base de datos *** \n Intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE );
            }finally{
                    conexionConsulta.desconectar();
            }
        }else{
            JOptionPane.showMessageDialog(null, "Se debe de dar algún valor en ambos campos. Vuelva a intentarlo", "Error", JOptionPane.ERROR_MESSAGE );
        }
        
    }//fin metodo actualizarAgregandoHuella
    
    public String[] Obt_Equipos(String carrito){
        int tam = 250;
        String ListaLaboratorio[] = new String [tam];
        int n = 0;
        //ListaLaboratorio.addElement("Selecciona un laboratorio");
        Connection c = conexionConsulta.conectar();
        try{
            PreparedStatement pstm = c.prepareStatement("SELECT NUM_COMP, ASIGNADA FROM EQUIPOS WHERE ID_LAB= ? AND UTILIZABLE = ? ORDER BY NUM_COMP ASC", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            pstm.setString(1, carrito);
            pstm.setString(2, "1");
            ResultSet res = pstm.executeQuery();
            while(res.next()){
                int asignada = res.getInt("ASIGNADA");
                if (asignada == 0){
                ListaLaboratorio[n] = res.getString("NUM_COMP");
                //System.out.println(ListaLaboratorio[n].toString());
                n ++;
                }
                
            }
            
        } catch (SQLException e) {
            
            System.err.println("Error en la consulta:" + e.getMessage());;
            
        }
        
        return ListaLaboratorio;
    }
    
    
    /////para el prestamo utilizando la huella digital//////
    public void prestamo() throws Exception 
    {   
        int confirmaNumero = 3; //cuando no se ha confirmado el numero de cuenta
        try{
            //Establece los valores para la sentencia SQL
            ///////Connection c = cn.conectar();
            //obtiene todas las huellas de la bd
            //System.out.println("\nAntes de ejecutar la query");
            //EnviarTexto("\n Antes de ejecutar la query \n");
            ///////PreparedStatement identificarStmt = c.prepareStatement("SELECT NUM_CTA, NOMBRE, IMG_HUELLA FROM usuarios_huella"); 
            ///////ResultSet rs = identificarStmt.executeQuery();
            //EnviarTexto("\n ////// Ejecutó la query para traer todos los registros con huella ////// \n");
            //System.out.println("\n ////// Ejecutó la query para traer todos los registros con huella ////// \n");
            //si se encuentra el nombre en la base de datos
            //System.out.println("\n A punto de entrar a comparacion \n");
            rs.beforeFirst(); //////para que regrese a apuntar antes del inicio 
            while(rs.next()){
                //lee la plantilla de la base de datos
                byte templateBuffer[] = rs.getBytes("IMG_HUELLA");
                String nombre = rs.getString("NOMBRE");
                String cuenta = rs.getString("NUM_CTA");
                String estado = rs.getString("ESTADO");
                String consentimiento = rs.getString("CONSENTIMIENTO");
                //crea una nueva plantilla a partir de la guardada en la base de datos
                DPFPTemplate referenceTemplate = DPFPGlobal.getTemplateFactory().createTemplate(templateBuffer);
                //envia la plantilla creada al objeto contenedor de Template del
                //componente de huella digital
                setTemplate(referenceTemplate);
                //Compara las caracteristicas de la huella recientemente capturada con
                //alguna plantilla guardada en la base de datos que coincide con ese tipo
                DPFPVerificationResult result = Verificador.verify(featuresverificacion , getTemplate());
                //EnviarTexto("\nComparando la huella del numero de cuenta "+cuenta+" con la actual");
                //System.out.println("Comparando la huella del numero de cuenta "+cuenta+" con la actual");
                //compara las plantillas (actual vs bd)
                //Si encuentra correspondencia dibuja el mapa
                //e indica el nombre de la persona que coincidio
                if(result.isVerified()){
                    //EnviarTexto("\nLa huella coincidio con la del numero de cuenta "+cuenta);
                    //System.out.println("\nLa huella coincidio con la del numero de cuenta "+cuenta);
                    //crea la imagen de los datos guardados de las huellas guardadas en la bd
                    Connection c = conexionConsulta.conectar();
                    //System.out.println("sí se conecto");
                    //PreparedStatement buscarEstado = c.prepareStatement("SELECT ESTADO FROM ALUMNOS WHERE NUM_CTA = ? ");
                    //buscarEstado.setString(1, cuenta);
                    //ResultSet bestado = buscarEstado.executeQuery();
                    //System.out.println(bestado["ESTADO"]);
                    //String alumnoActivo = bestado.getString("ESTADO");
                    //System.out.println(estado);
                    //if(estado.equals("0")){
                    //    JOptionPane.showConfirmDialog(null, "No puede realizarse préstamo en PC Puma al usuario\n"+nombre+"\nNúmero de Cuenta/RFC: "+cuenta+"\nRazón: Alumno NO inscrito", "Usuario NO inscrito", JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE );
                    //    start(); //vuelve a inicializar la captura de huella y lo indica
                    //    confirmaNumero = 0;
                    //    break;
                    //}
                    //else{ // <------------------------------------------
                    //aqui el codigo para verificar penalizaciones
                        System.out.println("Voy a verificar penalizacion");
                        PreparedStatement buscarPenalizaciones = c.prepareStatement("SELECT ID_PENALIZACION, FECHA_INICIO, FECHA_FIN_PENALIZA, RAZON_PENALIZA, MULTA FROM PENALIZACIONES WHERE FK_NUM_CTA = ? AND EDO_PENALIZA = '1' AND TIPO_PENALIZACION = '2'", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        buscarPenalizaciones.setString(1, cuenta);
                        ResultSet bpe = buscarPenalizaciones.executeQuery();
                        System.out.println("Antes de boolean");
                        boolean hayPenalizacion = bpe.first();
                        System.out.println("ejecute el query" + hayPenalizacion);
                        if(hayPenalizacion) //si hay penalizacion
                        {   
                            System.out.println("Voy a verificar penalizacion");
                            String fechaIniPena = bpe.getString("FECHA_INICIO");
                            String fechaFinPena = bpe.getString("FECHA_FIN_PENALIZA");
                            String razonPena = bpe.getString("RAZON_PENALIZA");
                            String multaPena = bpe.getString("MULTA");
                            String idPena = bpe.getString("ID_PENALIZACION");
                            DateTimeFormatter dtf5 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            String fechaHoy = dtf5.format(LocalDateTime.now());
                            System.err.println("HOY"+fechaHoy);
                            SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
                            Date date1 = sdformat.parse(fechaFinPena);
                            System.err.println(fechaFinPena);
                            Date date2 = sdformat.parse(fechaHoy);
                            confirmaNumero = 0; //para que considere al usuario como encontrado y confirmado 
                            if (date2.after(date1)) {
                                PreparedStatement cambiarPenalizaciones = c.prepareStatement("UPDATE PENALIZACIONES SET EDO_PENALIZA = ? WHERE ID_PENALIZACION = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                cambiarPenalizaciones.setString(1, "0");
                                cambiarPenalizaciones.setString(2, idPena);
                                int cambioPenaliza = cambiarPenalizaciones.executeUpdate();                                                                 
                                PreparedStatement consultaAmoestacion = c.prepareStatement("SELECT AMONESTACIONES FROM ALUMNOS WHERE NUM_CTA = ? ", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                consultaAmoestacion.setString(1, cuenta);
                                ResultSet cars = consultaAmoestacion.executeQuery();
                                if (cars.next()) {
                                    int numAmonestaciones = cars.getInt("AMONESTACIONES");
                                    if (numAmonestaciones == 3) {
                                        PreparedStatement amonestacionAumenta = c.prepareStatement("UPDATE ALUMNOS SET AMONESTACIONES = ? WHERE NUM_CTA = ?");
                                        amonestacionAumenta.setString(1, "0");
                                        amonestacionAumenta.setString(2, cuenta);
                                        int amon = amonestacionAumenta.executeUpdate();
                                        if (amon > 0) {
                                            EnviarTexto("Se acabo la penalización por AMONSTACIONES al alumno con numero de cuenta " + cuenta);
                                            prestamo();
                                            break;
                                        }
                                    }
                                }
                                if (cambioPenaliza > 0){
                                    //JOptionPane.showMessageDialog(null, "Se actualizo el estado de penalización", "Éxito", JOptionPane.INFORMATION_MESSAGE );
                                    EnviarTexto("Se acabo la penalización por AMONSTACIONES al alumno con numero de cuenta " + cuenta);
                                    prestamo();
                                    break;
                                }
                            } else {
                                JOptionPane.showConfirmDialog(null, "No puede realizarse préstamo en PC Puma al usuario\n"+nombre+"\nNúmero de Cuenta/RFC: "+cuenta+"\nRazón: "+razonPena+"\nDesde el día "+fechaIniPena+"\nHasta el "+fechaFinPena, "Usuario Penalizado", JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE );                                                            
                                EnviarTexto("Proceso de Préstamo inviable por penalizacion a usuario con cuenta "+cuenta);
                                labelImagenHuella.setIcon(null);
                                //start(); //vuelve a inicializar la captura de huella y lo indica
                                break; 
                            }    
                        }
                        else
                        { //si no hay penalizacion    
                            confirmaNumero = JOptionPane.showConfirmDialog(null, "La huella digital está registrada a nombre de: "+nombre+"\n y Número de Cuenta/RFC: "+cuenta+"\n", "Identificación de Huella", JOptionPane.OK_CANCEL_OPTION );
                            //System.out.println("La confirmacion de numero vale "+confirmaNumero);
                            /*if(consentimiento.equals("0")){
                                JOptionPane.showMessageDialog(null, "No ha firmado hoja de consentimiento de uso de huella", "Excepción", JOptionPane.ERROR_MESSAGE);
                                start(); //vuelve a inicializar la captura de huella y lo indica
                            }*/
                            if(confirmaNumero == 0)
                            {   /*
                                //aqui el codigo para verificar penalizaciones
                                //System.out.println("Previo a buscar penalizaciones");
                                PreparedStatement buscarPenalizaciones = c.prepareStatement("SELECT ID_PENALIZACION, inter_fecha(FECHA_INICIO), inter_fecha(FECHA_FIN_PENALIZA), RAZON_PENALIZA, MULTA FROM PENALIZACIONES WHERE FK_NUM_CTA = ? AND EDO_PENALIZA = '1' AND TIPO_PENALIZACION = '2'");
                                buscarPenalizaciones.setString(1, cuenta);
                                ResultSet bpe = buscarPenalizaciones.executeQuery();
                                boolean hayPenalizacion = bpe.first();
                                if(hayPenalizacion) //si hay penalizacion
                                {   String fechaIniPena = bpe.getString("inter_fecha(FECHA_INICIO)");
                                    String fechaFinPena = bpe.getString("inter_fecha(FECHA_FIN_PENALIZA)");
                                    String razonPena = bpe.getString("RAZON_PENALIZA");
                                    String multaPena = bpe.getString("MULTA");
                                    JOptionPane.showMessageDialog(null, "No puede realizarse préstamo en PC Puma al usuario "+nombre+" Número de Cuenta/RFC: "+cuenta+"\n Tiene una Penalización desde el día "+fechaIniPena+" con fecha de finalización "+fechaFinPena+"\nRazón: "+razonPena+"\nMulta: $"+multaPena+"\nDeberá pagar su multa en la caja por concepto de '(Concepto)' y después acudir al módulo de PC Puma para despenalizarlo", "Usuario con Penalización", JOptionPane.WARNING_MESSAGE );

                                }
                                else
                                { //si no hay penalizacion
                                */
                                    PreparedStatement buscarPrestamo = c.prepareStatement("SELECT P.ID_PRESTAMO, P.ID_EQUIPO, E.NUM_COMP, L.NOMBRE_LAB FROM PRESTAMOS P, EQUIPOS E, LABORATORIOS L WHERE NUM_CTA = ? AND P.ID_EQUIPO = E.ID_EQUIPO AND E.FECHA_PRESTAMO = P.FECHA AND E.HORA_PRESTAMO = P.HORA_INICIO AND ASIGNADA = 1 AND P.ID_LAB = L.ID_LAB ORDER BY P.ID_PRESTAMO DESC" , ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                    buscarPrestamo.setString(1, cuenta);
                                    ResultSet bp = buscarPrestamo.executeQuery();
                                    boolean hayPrestamo = bp.first();

                                    if(hayPrestamo) //si hay prestamo
                                    {   String numComp = bp.getString("E.NUM_COMP");
                                        String nombreLaboratorio = bp.getString("L.NOMBRE_LAB");
                                        JOptionPane.showMessageDialog(null, "El usuario "+nombre+"\n y clave "+cuenta+" ya tiene previamente asignado el equipo "+numComp+"\nen el "+nombreLaboratorio+"\nNo se puede realizar otro préstamo hasta que lo devuelva", "Error", JOptionPane.ERROR_MESSAGE );
                                    }
                                    else
                                    {//si no hay prestamo previo
                                        //Revision de numero de prestamos en el dia. 
                                        PreparedStatement numeroPrestamosDia = c.prepareStatement("SELECT FECHA FROM PRESTAMOS WHERE NUM_CTA = ? and FECHA = CURDATE() and (ID_LAB IN ('CB03','CB02','CB01','L01','L02','L03','C01','C02','C03','C04','C05','C06','CEIEPAA','CEIEGT'))" , ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                        numeroPrestamosDia.setString(1, cuenta);
                                        ResultSet nPD = numeroPrestamosDia.executeQuery();
                                        nPD.last();
                                        //System.out.println(nPD.getRow());
                                        if (nPD.getRow() >= 2){
                                        JOptionPane.showMessageDialog(null, "El usuario "+nombre+"\n y clave "+cuenta+" ha solicitado prestamo de equipo "+nPD.getRow()+" veces en el día", "Alerta", JOptionPane.INFORMATION_MESSAGE );
                                        }
                                        //FIN Revision de numero de prestamos en el dia
                                        //Object carrito = JOptionPane.showInputDialog(null, "Seleccione el laboratorio movil", "Préstamo de equipo", JOptionPane.QUESTION_MESSAGE, null, new Object[] {"L01","L02","CB03","C01","C02","C03","C04","C05","C06"}, "01" );
                                        //Object carrito = laboratorio;
                                        String ListaLaboratorio[] = Obt_Laboratorio();
                                        Object carrito = JOptionPane.showInputDialog(null, "Seleccione el laboratorio movil", "Prestamo de equipo", JOptionPane.QUESTION_MESSAGE, null, ListaLaboratorio, "01" );
                                        
                                        
                                        if(carrito != null)
                                        {
                                            String ListaEquipos[] = Obt_Equipos(carrito.toString());
                                            Object numEquipo = JOptionPane.showInputDialog(null, "Indique el número de equipo", "Préstamo de equipo", JOptionPane.QUESTION_MESSAGE, null, ListaEquipos, "01" );
                                            //System.out.println("El numero de equipo seleccionado es "+numEquipo);

                                            if(numEquipo != null)
                                            {   String equipo = numEquipo.toString();
                                                try{
                                                    //JOptionPane.showMessageDialog(null, "Se recibió '"+equipo+"'", "Equipo", JOptionPane.INFORMATION_MESSAGE);
                                                    //revisa que exista el equipo y que no este ya en prestamo
                                                    PreparedStatement buscarEquipo = c.prepareStatement("SELECT ID_EQUIPO, ASIGNADA, CURTIME() AS HORA FROM EQUIPOS WHERE NUM_COMP = ? AND ID_LAB = ?" , ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                                    buscarEquipo.setString(1, equipo);
                                                    buscarEquipo.setString(2, carrito.toString() );
                                                    ResultSet be = buscarEquipo.executeQuery();
                                                    boolean nColumn = be.first();
                                                    String asignada = be.getString("ASIGNADA");
                                                    int estadoAsignada = Integer.parseInt(asignada);

                                                    String hora = be.getString("HORA");
                                                    EnviarTexto("La hora es "+hora);
                                                    if(estadoAsignada == 0) //esta libre
                                                    {
                                                        String idEquipo = be.getString("ID_EQUIPO");
                                                        System.out.println("Este es el idEquipo: "+idEquipo);

                                                        //Time t = new Time();
                                                        //String hora = t.getTime();
                                                        
                                                        PreparedStatement ObsEquipo = c.prepareStatement("SELECT OBS_EQUIPO FROM EQUIPOS WHERE ID_EQUIPO = ?");
                                                        ObsEquipo.setString(1, idEquipo);
                                                        ResultSet re = ObsEquipo.executeQuery();
                                                        if(re.next()){
                                                            String obsEquipo = re.getString("OBS_EQUIPO");
                                                            if(obsEquipo != ""){
                                                                JOptionPane.showMessageDialog(null, "Este EQUIPO Presenta estas observaciones: \n"+obsEquipo, "Aviso", JOptionPane.INFORMATION_MESSAGE );                                                        
                                                            } else {                                                                
                                                                System.out.println("Equipo sin observaciones");
                                                            }
                                                            
                                                        }

                                                        //JOptionPane.showMessageDialog(null, "El equipo seleccionado esta disponible, hacer la sentencia de prestamo ", "Resultado", JOptionPane.INFORMATION_MESSAGE );
                                                        PreparedStatement prestarEquipo = c.prepareStatement("INSERT INTO PRESTAMOS(NUM_CTA, ID_LAB, ID_EQUIPO, HORA_INICIO, HORA_FIN, FECHA) values(?, ?, ?, ?, ADDTIME(?, '02:00:00'), CURDATE())");
                                                        prestarEquipo.setString(1, cuenta);
                                                        prestarEquipo.setString(2,carrito.toString());
                                                        prestarEquipo.setString(3, idEquipo);
                                                        prestarEquipo.setString(4, hora);
                                                        prestarEquipo.setString(5, hora);
                                                        prestarEquipo.execute();
                                                        prestarEquipo.close();

                                                        PreparedStatement registrarAsignado = c.prepareStatement("UPDATE EQUIPOS SET ASIGNADA = 1, USO_COMP = 5, FECHA_PRESTAMO = CURDATE(), HORA_PRESTAMO=? WHERE ID_EQUIPO = ?");
                                                        registrarAsignado.setString(1, hora);
                                                        registrarAsignado.setString(2, idEquipo);
                                                        registrarAsignado.execute();
                                                        registrarAsignado.close();

                                                        JOptionPane.showMessageDialog(null, "El préstamo se autorizó y se registró correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE );                                                        
                                                        //conexionConsulta.desconectar();
                                                        //Registro de actividad en LOGS
                                                        DateTimeFormatter dtf5 = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                                                        System.out.println("yyyy/MM/dd-> "+dtf5.format(LocalDateTime.now()));
                                                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                                                        System.out.println("HH:mm:ss-> " + dtf.format(LocalDateTime.now()));
                                                        PreparedStatement registroLog = c.prepareStatement("INSERT INTO LOGS(NAME_USUARIO, ACCION, FECHA_ACCION, HORA_ACCION) values(?, ?, ?, ?)", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                                        registroLog.setString(1, usuario);
                                                        registroLog.setString(2, "Realizo el prestamo del equipo "+idEquipo+" en el laboratorio"+carrito.toString());
                                                        registroLog.setString(3, dtf5.format(LocalDateTime.now()));
                                                        registroLog.setString(4, dtf.format(LocalDateTime.now()));
                                                        registroLog.execute();
                                                        registroLog.close();
                                                        
                                                        Reclutador.clear();
                                                        labelImagenHuella.setIcon(null); 
                                                        botonAlta.setEnabled(false);
                                                        botonActualizarAgregandoHuella.setEnabled(false);
                                                        botonVerificar.setEnabled(false);
                                                        botonIdentificar.setEnabled(false);
                                                        botonPrestamo.setEnabled(false);
                                                        botonDevolucion.setEnabled(false);

                                                    }else if(estadoAsignada == 1){
                                                        JOptionPane.showMessageDialog(null, "El equipo con clave "+equipo+" no puede prestarse, pues ya está prestado\nDé clic en el botón Préstamo y proporcione otra clave de equipo", "Resultado", JOptionPane.WARNING_MESSAGE );
                                                        labelImagenHuella.setIcon(null);
                                                    }
                                                }catch(SQLException e){
                                                    labelImagenHuella.setIcon(null);
                                                    JOptionPane.showMessageDialog(null, "No existe esa clave de equipo, no se puede realizar el préstamo\nExcepción es "+e+"y su descripcion:"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );

                                                }finally{
                                                    //conexionConsulta.desconectar();
                                                    //start(); //vuelve a inicializar la captura de huella y lo indica

                                                }//fin del try-catch-finally

                                            }//fin if equipo != null
                                            else
                                            {
                                                //conexionConsulta.desconectar();
                                                EnviarTexto("Proceso de Préstamo cancelado por el usuario");
                                                //start(); //vuelve a inicializar la captura de huella y lo indica
                                            }
                                        }
                                        else
                                        {
                                            //conexionConsulta.desconectar();
                                            EnviarTexto("Proceso de Préstamo cancelado por el usuario");
                                            //start(); //vuelve a inicializar la captura de huella y lo indica
                                        }
                                    }//si no hay prestamo previo

                                //}//si no hay penalizacion
                            }else
                            {   //conexionConsulta.desconectar();
                                Reclutador.clear();
                                labelImagenHuella.setIcon(null);
                                EnviarTexto("Proceso de Préstamo cancelado por el usuario");
                            }
                            break;
                        }//fin si no hay penalizacion
                   // }// fin verifica estado Activo
                }//fin if se verifica
            }//fin while
            //Si no encuentra alguna huella correspondiente al nombre, lo indica con un mensaje
            if(confirmaNumero == 3)
            {   JOptionPane.showMessageDialog(null, "No existe ningun registro que coincida con la huella\n\nTip: Verifique si el usuario sólo está dado de alta con su Número de Cuenta (interfaz web de encargado).\nSi es el caso, actualice la huella. En caso contrario, délo de Alta.", "Verificacion de Huella", JOptionPane.ERROR_MESSAGE);
                setTemplate(null);
            }
        }catch(SQLException e){
            //Si ocurre un error lo indica en la consola
            //System.err.println("Error al identificar huella digital."+e.getMessage());
            JOptionPane.showMessageDialog(null, "Ocurrió un error, Excepcion:"+e+"\n descripcion:"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        catch(NullPointerException e){ //para cuando se hizo Reset y aun así intenta identificarse una huella
            //System.err.println("No hay huella para trabajar con ella");
            JOptionPane.showMessageDialog(null, "No hay huella para trabajar con ella, capture alguna con el lector", "Excepción", JOptionPane.ERROR_MESSAGE);
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null, "Excepcion "+e+"\nSu descripcion es "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        finally{
            conexionConsulta.desconectar();
        }//fin try-catch-finally
        
    }//fin metodo prestamo
    
    
    public void prestamo2(String cuenta) throws Exception //funcion que es para el prestamo alternativo (con numero de cuenta)
    {   
        if(cuenta == ""){
            cuenta = JOptionPane.showInputDialog("Número de Cuenta o RFC:");
        } 
        if(cuenta != null){
            try
            {
                //Establece los valores para la sentencia SQL
                Connection c = conexionConsulta.conectar();
                //EnviarTexto("\n ****** Conexión establecida a la base de datos ****** \n");
                //System.out.println("\n ****** Conexión establecida a la base de datos ****** \n");
                //obtiene la plantilla correspondiente a la persona indicada
                PreparedStatement identificarStmt = c.prepareStatement("SELECT NUM_CTA, NOMBRE, ESTADO,CONSENTIMIENTO, IMG_HUELLA FROM ALUMNOS WHERE NUM_CTA = ?");
                identificarStmt.setString(1, cuenta);
                ResultSet rs = identificarStmt.executeQuery();
                //EnviarTexto("\n ////// Ejecutó la query para que busca directamente el numero de cuenta en la base de datos ////// \n");
                //System.out.println("\n ////// Ejecutó la query para que busca directamente el numero de cuenta en la base de datos ////// \n");
                //Si se encuentra el numero de cuenta en la base de datos
                if(rs.next()){
                    String nombre = rs.getString("NOMBRE");
                    String estado = rs.getString("ESTADO");
                    String consentimiento = rs.getString("CONSENTIMIENTO");
                    /*if(consentimiento.equals("0")){
                        JOptionPane.showMessageDialog(null, "No ha firmado hoja de consentimiento de uso de huella", "Excepción", JOptionPane.ERROR_MESSAGE);
                    }*/
                    if(estado.equals("0")){
                        JOptionPane.showConfirmDialog(null, "No puede realizarse préstamo en PC Puma al usuario\n"+nombre+"\nNúmero de Cuenta/RFC: "+cuenta+"\nRazón: Alumno NO inscrito", "Usuario NO inscrito", JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE );
                    }
                    else{
                    //se buscara si hay penalizacion en Pc Puma o no
                        PreparedStatement buscarPenalizacion = c.prepareStatement("SELECT ID_PENALIZACION AS PENALIZA, FECHA_INICIO AS INICIO, FECHA_FIN_PENALIZA AS FIN, RAZON_PENALIZA AS RAZON FROM PENALIZACIONES WHERE FK_NUM_CTA = ? AND TIPO_PENALIZACION = '2' AND EDO_PENALIZA = '1'" ,ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        buscarPenalizacion.setString(1, cuenta);
                        ResultSet bpe = buscarPenalizacion.executeQuery();
                        if(bpe.next()){
                            String fechaIniPena = bpe.getString("INICIO");
                            String fechaFinPena = bpe.getString("FIN");
                            String razonPena = bpe.getString("RAZON");
                            String idPena = bpe.getString("PENALIZA");
                            //JOptionPane.showConfirmDialog(null, "El numero de cuenta "+cuenta+" está registrado a nombre de: "+nombre+"\nEl Usuario tiene una Penalizacion de PC Puma desde el dia "+fecha_inicio_penaliza+" y con fecha de fin "+fecha_fin_penaliza+".\nPor la razon de: "+razon_penaliza+"\n", "Penalizacion", JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE );
                            DateTimeFormatter dtf5 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            String fechaHoy = dtf5.format(LocalDateTime.now());
                            System.err.println("HOY" + fechaHoy);
                            SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
                            Date date1 = sdformat.parse(fechaFinPena);
                            System.err.println(fechaFinPena);
                            Date date2 = sdformat.parse(fechaHoy);
                            //confirmaNumero = 0; //para que considere al usuario como encontrado y confirmado 
                            if (date2.after(date1)) { // Revisando si la penalización ya terminó
                                PreparedStatement cambiarPenalizaciones = c.prepareStatement("UPDATE PENALIZACIONES SET EDO_PENALIZA = ? WHERE ID_PENALIZACION = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                cambiarPenalizaciones.setString(1, "0");
                                cambiarPenalizaciones.setString(2, idPena);
                                int cambioPenaliza = cambiarPenalizaciones.executeUpdate();
                                
                                PreparedStatement consultaAmoestacion = c.prepareStatement("SELECT AMONESTACIONES FROM ALUMNOS WHERE NUM_CTA = ? ", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                consultaAmoestacion.setString(1, cuenta);
                                ResultSet cars = consultaAmoestacion.executeQuery();
                                if(cars.next()){
                                    int numAmonestaciones = cars.getInt("AMONESTACIONES");                                                                        
                                    if (numAmonestaciones == 3) {
                                        PreparedStatement amonestacionAumenta = c.prepareStatement("UPDATE ALUMNOS SET AMONESTACIONES = ? WHERE NUM_CTA = ?");
                                        amonestacionAumenta.setString(1, "0");
                                        amonestacionAumenta.setString(2, cuenta);
                                        int amon = amonestacionAumenta.executeUpdate();
                                        if(amon > 0){
                                            EnviarTexto("Se acabo la penalización por AMONSTACIONES al alumno con numero de cuenta " + cuenta);
                                            prestamo2(cuenta);
                                        } 
                                    }
                                }
                                if (cambioPenaliza > 0) {
                                    //JOptionPane.showMessageDialog(null, "Se actualizo el estado de penalización", "Éxito", JOptionPane.INFORMATION_MESSAGE );
                                    EnviarTexto("Se acabo la penalización para el alumno con numero de cuenta, ACTUALIZADO " + cuenta);
                                    prestamo2(cuenta);
                                } else {
                                    JOptionPane.showConfirmDialog(null, "No puede realizarse préstamo en PC Puma al usuario\n" + nombre + "\nNúmero de Cuenta/RFC: " + cuenta + "\nRazón: NO SE PUDO DESPENALIZAR, Esta penalizado:\nDesde el " + fechaIniPena + "\nHasta el " + fechaFinPena, "Usuario Penalizado", JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE);
                                    conexionConsulta.desconectar();
                                    EnviarTexto("Proceso de Préstamo inviable por penalizacion a usuario con cuenta " + cuenta);
                                }
                            } else {
                                JOptionPane.showConfirmDialog(null, "No puede realizarse préstamo en PC Puma al usuario\n" + nombre + "\nNúmero de Cuenta/RFC: " + cuenta + "\nRazón: " + razonPena + "\nDesde el " + fechaIniPena + "\nHasta el " + fechaFinPena, "Usuario Penalizado", JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE);
                                conexionConsulta.desconectar();
                                EnviarTexto("Proceso de Préstamo inviable por penalizacion a usuario con cuenta " + cuenta);                               
                            }
                        }
                        else{ //no tiene penalizacion
                            //JOptionPane.showMessageDialog(null, "El número de cuenta "+cuenta+"\n corresponde a "+nombre, "Usuario válido", JOptionPane.INFORMATION_MESSAGE );
                            int confirmaNumero = JOptionPane.showConfirmDialog(null, "El numero de cuenta "+cuenta+" está registrado a nombre de: "+nombre+"\n", "Identificación de Huella", JOptionPane.OK_CANCEL_OPTION );
                            //System.out.println("La confirmacion de numero vale "+confirmaNumero);

                            if(confirmaNumero == 0) //revisara si tiene algun prestamo
                            {   PreparedStatement buscarPrestamo = c.prepareStatement("SELECT P.ID_PRESTAMO, P.ID_EQUIPO, E.NUM_COMP, L.NOMBRE_LAB FROM PRESTAMOS P, EQUIPOS E, LABORATORIOS L WHERE NUM_CTA = ? AND P.ID_EQUIPO = E.ID_EQUIPO AND E.FECHA_PRESTAMO = P.FECHA AND E.HORA_PRESTAMO = P.HORA_INICIO AND ASIGNADA = 1 AND P.ID_LAB = L.ID_LAB ORDER BY P.ID_PRESTAMO DESC" ,ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                buscarPrestamo.setString(1, cuenta);
                                ResultSet bp = buscarPrestamo.executeQuery();
                                boolean hayPrestamo = bp.first();

                                if(hayPrestamo) //si hay prestamo
                                {   String numComp = bp.getString("E.NUM_COMP");
                                    String nombreLaboratorio = bp.getString("L.NOMBRE_LAB");
                                    JOptionPane.showMessageDialog(null, "El usuario "+nombre+"\n y clave "+cuenta+" ya tiene previamente asignado el equipo "+numComp+"\nen el "+nombreLaboratorio+"\nNo se puede realizar otro préstamo hasta que lo devuelva", "Error", JOptionPane.ERROR_MESSAGE );
                                }
                                else
                                {//si no hay prestamo previo
                                    //Revision de numero de prestamos en el dia. 
                                        PreparedStatement numeroPrestamosDia = c.prepareStatement("SELECT FECHA FROM PRESTAMOS WHERE NUM_CTA = ? and FECHA = CURDATE() and (ID_LAB IN ('C01','CB03','CB02','CB01','L01','L02','L03','C01','C02','C03','C04','C05','C06','CEIEPAA','CEIEGT'))" ,ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                        numeroPrestamosDia.setString(1, cuenta);
                                        ResultSet nPD = numeroPrestamosDia.executeQuery();
                                        nPD.last();
                                        //System.out.println(nPD.getRow());
                                        if (nPD.getRow() >= 2){
                                        JOptionPane.showMessageDialog(null, "El usuario "+nombre+"\n y clave "+cuenta+" ha solicitado prestamo de equipo "+nPD.getRow()+" veces en el día", "Alerta", JOptionPane.INFORMATION_MESSAGE );
                                        }
                                        //FIN Revision de numero de prestamos en el dia

                                    //String equipo = JOptionPane.showInputDialog("Proporcione la clave del equipo para préstamo").toUpperCase();
                                    // ---- >Object carrito = JOptionPane.showInputDialog(null, "Seleccione el laboratorio movil", "Préstamo de equipo", JOptionPane.QUESTION_MESSAGE, null, new Object[] {"L01","L02","CB03","C01","C02","C03","C04","C05","C06"}, "01" );
                                    //Object carrito = laboratorio;
                                    //System.out.println("El carrito seleccionado es "+carrito);
                                    //String equipo = carrito.toString();
                                    String ListaLaboratorio[] = Obt_Laboratorio();
                                    Object carrito = JOptionPane.showInputDialog(null, "Seleccione el laboratorio movil", "Prestamo de equipo", JOptionPane.QUESTION_MESSAGE, null, ListaLaboratorio, "01" );

                                    if(carrito != null)
                                    {
                                        //Object numEquipo = JOptionPane.showInputDialog(null, "Indique el número de quipo", "Préstamo de equipo", JOptionPane.QUESTION_MESSAGE, null, new Object[] {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"}, "01" );
                                        String ListaEquipos[] = Obt_Equipos(carrito.toString());
                                            Object numEquipo = JOptionPane.showInputDialog(null, "Indique el número de equipo", "Préstamo de equipo", JOptionPane.QUESTION_MESSAGE, null, ListaEquipos, "01" );
                                        //System.out.println("El numero de equipo seleccionado es "+numEquipo);

                                        if(numEquipo != null)
                                        {   
                                            String equipo = numEquipo.toString();
                                            //String equipo = JOptionPane.showInputDialog("Proporcione la clave del equipo para prestar").toUpperCase();
                                            //if(equipo != null)
                                            try{
                                                //JOptionPane.showMessageDialog(null, "Se recibió '"+equipo+"'", "Equipo", JOptionPane.INFORMATION_MESSAGE);
                                                //revisa que exista el equipo y que no este ya en prestamo
                                                PreparedStatement buscarEquipo = c.prepareStatement("SELECT ID_EQUIPO, ASIGNADA, CURTIME() AS HORA FROM EQUIPOS WHERE NUM_COMP = ? AND ID_LAB = ?" ,ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                                buscarEquipo.setString(1, equipo);
                                                buscarEquipo.setString(2, carrito.toString() );
                                                ResultSet be = buscarEquipo.executeQuery();
                                                boolean nColumn = be.first();
                                                String asignada = be.getString("ASIGNADA");
                                                int estadoAsignada = Integer.parseInt(asignada);

                                                String hora = be.getString("HORA");
                                                EnviarTexto("La hora es "+hora);
                                                if(estadoAsignada == 0) //esta libre
                                                {
                                                    String idEquipo = be.getString("ID_EQUIPO");
                                                    //Time t = new Time();
                                                    //String hora = t.getTime();
                                                    //JOptionPane.showMessageDialog(null, "El equipo seleccionado esta disponible, hacer la sentencia de prestamo ", "Resultado", JOptionPane.INFORMATION_MESSAGE );

                                                    PreparedStatement ObsEquipo = c.prepareStatement("SELECT OBS_EQUIPO FROM EQUIPOS WHERE ID_EQUIPO = ?");
                                                    ObsEquipo.setString(1, idEquipo);
                                                    ResultSet re = ObsEquipo.executeQuery();
                                                    if (re.next()) {
                                                        String obsEquipo = re.getString("OBS_EQUIPO");
                                                        if (obsEquipo == "") {
                                                            System.out.println("Equipo sin observaciones");
                                                        } else {
                                                            JOptionPane.showMessageDialog(null, "Este EQUIPO Presenta estas observaciones: \n" + obsEquipo, "Aviso", JOptionPane.INFORMATION_MESSAGE);
                                                        }

                                                    }

                                                    PreparedStatement prestarEquipo = c.prepareStatement("INSERT INTO PRESTAMOS(NUM_CTA, ID_LAB, ID_EQUIPO, HORA_INICIO, HORA_FIN, FECHA) values(?, ?, ?, ?, ADDTIME(?, '02:00:00'), CURDATE())");
                                                    prestarEquipo.setString(1, cuenta);
                                                    prestarEquipo.setString(2, carrito.toString());
                                                    prestarEquipo.setString(3, idEquipo);
                                                    prestarEquipo.setString(4, hora);
                                                    prestarEquipo.setString(5, hora);
                                                    prestarEquipo.execute();
                                                    prestarEquipo.close();

                                                    PreparedStatement registrarAsignado = c.prepareStatement("UPDATE EQUIPOS SET ASIGNADA = 1, USO_COMP = 5, FECHA_PRESTAMO = CURDATE(), HORA_PRESTAMO=? WHERE ID_EQUIPO = ?");
                                                    registrarAsignado.setString(1, hora);
                                                    registrarAsignado.setString(2, idEquipo);
                                                    registrarAsignado.execute();
                                                    registrarAsignado.close();

                                                    JOptionPane.showMessageDialog(null, "El préstamo se ha autorizado y se ha registrado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE );
                                                    conexionConsulta.desconectar();
                                                    //Registro en 
                                                    DateTimeFormatter dtf5 = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                                                    System.out.println("yyyy/MM/dd-> "+dtf5.format(LocalDateTime.now()));
                                                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                                                    System.out.println("HH:mm:ss-> " + dtf.format(LocalDateTime.now()));
                                                    PreparedStatement registroLog = c.prepareStatement("INSERT INTO LOGS(NAME_USUARIO, ACCION, FECHA_ACCION, HORA_ACCION) values(?, ?, ?, ?)", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                                    registroLog.setString(1, usuario);
                                                    registroLog.setString(2, "Realizo el prestamo del equipo "+idEquipo+" en el laboratorio "+carrito.toString());
                                                    registroLog.setString(3, dtf5.format(LocalDateTime.now()));
                                                    registroLog.setString(4, dtf.format(LocalDateTime.now()));
                                                    registroLog.execute();
                                                    registroLog.close();

                                                    botonAlta.setEnabled(false);
                                                    botonActualizarAgregandoHuella.setEnabled(false);
                                                    botonVerificar.setEnabled(false);
                                                    botonIdentificar.setEnabled(false);
                                                    botonPrestamo.setEnabled(false);
                                                    botonDevolucion.setEnabled(false);

                                                }else if(estadoAsignada == 1){
                                                    JOptionPane.showMessageDialog(null, "El equipo con clave "+equipo+" ya está asignado\nDe clic en el botón Préstamo y proporcione otra clave de equipo", "Resultado", JOptionPane.WARNING_MESSAGE );
                                                }
                                            }catch(SQLException e){
                                                JOptionPane.showMessageDialog(null, "No existe esa clave de equipo, no se puede realizar el préstamo\n"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );

                                            }finally{
                                                conexionConsulta.desconectar();

                                            }//fin del try-catch-finally

                                        }//fin if equipo != null
                                        else
                                        {
                                            conexionConsulta.desconectar();
                                            EnviarTexto("Proceso de Préstamo cancelado por el usuario");
                                        }
                                    }
                                    else
                                    {
                                        conexionConsulta.desconectar();
                                        EnviarTexto("Proceso de Préstamo cancelado por el usuario");
                                    }
                                }//fin si no hay prestamo previo
                            }else
                            {   conexionConsulta.desconectar();
                                EnviarTexto("Proceso de Préstamo cancelado por el usuario");
                            }
                        }
                    }    
                }//fin si se encuentra el numero de cuenta en la base de datos
                else{
                    JOptionPane.showMessageDialog(null, "No se encontró registro de usuario con número de cuenta/RFC:"+cuenta, "Verificacion de Huella", JOptionPane.ERROR_MESSAGE );
                    conexionConsulta.desconectar();
                }
            }catch(SQLException e){
                //si ocurre un error lo indica en la consola
                //System.err.println("Error al verificar los datos de la huella.");
                JOptionPane.showMessageDialog(null, "Ocurrió un error al verificar los datos de la huella\nExepcion de tipo "+e+"\nDescripcion:"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
            }
            catch(Exception e){
                JOptionPane.showMessageDialog(null, "Excepcion "+e+"\nSu descripcion es "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }finally{
                conexionConsulta.desconectar();
            }//fin try-catch-finally
        }else{ //si el numero de cuenta no se propocion*
            JOptionPane.showMessageDialog(null, "No se recibió número de cuenta ni RFC\n Proporcione alguno", "Error", JOptionPane.WARNING_MESSAGE);
        }
        
    }//fin metodo prestamo2
    
    
    ////////funcion que es para el prestamo alternativo (con numero de cuenta)////////
    public void prestamoPrevio() throws Exception 
    {   //JOptionPane.showMessageDialog(null, "Se activo el boton para verificar prestamo previo", "Boton", JOptionPane.INFORMATION_MESSAGE );
        try{
            //Establece los valores para la sentencia SQL
            Connection c = conexionConsulta.conectar();
            //obtiene todas las huellas de la bd
            PreparedStatement identificarStmt = c.prepareStatement("SELECT NUM_CTA, NOMBRE, IMG_HUELLA FROM ALUMNOS" ,ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = identificarStmt.executeQuery();
            //si se encuentra el nombre en la base de datos
            while(rs.next()){
                //lee la plantilla de la base de datos
                byte templateBuffer[] = rs.getBytes("IMG_HUELLA");
                String nombre = rs.getString("NOMBRE");
                String cuenta = rs.getString("NUM_CTA");
                //crea una nueva plantilla a partir de la guardada en la base de datos
                DPFPTemplate referenceTemplate = DPFPGlobal.getTemplateFactory().createTemplate(templateBuffer);
                //envia la plantilla creada al objeto contenedor de Template del 
                //componente de huella digital
                setTemplate(referenceTemplate);
                //Compara las caracteristicas de la huella recientemente capturada con
                //alguna plantilla gurdada en la base de datos que coincide con ese tipo
                DPFPVerificationResult result = Verificador.verify(featuresverificacion , getTemplate());
                //compara las plantillas (actual vs bd)
                //Si encuentra correspondencia dibuja el mapa
                //e indica el nombre de la persona que coincidio
                if(result.isVerified()){
                    //crea la imagen de los datos guardados de las huellas guardadas
                    //en la base de datos
                    
                    try{
                                //JOptionPane.showMessageDialog(null, "Se recibió '"+equipo+"'", "Equipo", JOptionPane.INFORMATION_MESSAGE);
                                //revisa que exista el equipo y que no este ya en prestamo
                                PreparedStatement buscarEquipo = c.prepareStatement("SELECT ID_PRESTAMO, NUM_COMP, NOMBRE_LAB FROM EQUIPOS E, PRESTAMOS P, LABORATORIOS L WHERE NUM_CTA = ? AND ASIGNADA = 1 AND E.ID_EQUIPO = P.ID_EQUIPO AND FECHA_PRESTAMO = FECHA AND HORA_PRESTAMO = HORA_INICIO AND L.ID_LAB = P.ID_LAB " ,ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                buscarEquipo.setString(1, cuenta);
                                ResultSet be = buscarEquipo.executeQuery();
                                boolean nColumn = be.first();
                                
                                if(nColumn == true) //no esta libre
                                {   String numComputadora = be.getString("NUM_COMP");
                                    String nombreLaboratorio = be.getString("NOMBRE_LAB");
                                    
                                    //JOptionPane.showMessageDialog(null, "El equipo seleccionado esta disponible, hacer la sentencia de prestamo ", "Resultado", JOptionPane.INFORMATION_MESSAGE ); 
                                    

                                    JOptionPane.showMessageDialog(null, "El usuario "+nombre+" ya tiene asignado el equipo "+numComputadora+" en el "+nombreLaboratorio, "Advertencia", JOptionPane.WARNING_MESSAGE );
                                    conexionConsulta.desconectar();
                                    botonAlta.setEnabled(false);
                                    botonActualizarAgregandoHuella.setEnabled(false);
                                    botonVerificar.setEnabled(false);
                                    botonIdentificar.setEnabled(true);
                                    botonPrestamo.setEnabled(false);
                                    botonDevolucion.setEnabled(false);

                                }else {
                                    JOptionPane.showMessageDialog(null, "El usuario "+nombre+" no tiene ningún equipo asignado en ningún laboratorio\nDé clic en el botón Préstamo y proporcione alguna clave de equipo", "Resultado", JOptionPane.INFORMATION_MESSAGE );
                                }
                            }catch(Exception e){
                                JOptionPane.showMessageDialog(null, "Ocurrió un Error de tipo "+e+"y su descripcion:"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );

                            }finally{
                                conexionConsulta.desconectar();

                            }//fin del try-catch-finally
                    
                    
                }//fin if se verifica
                
            }//fin while
            //Si no encuentra alguna huella correspondiente al nombre, lo indica con un mensaje
            //JOptionPane.showMessageDialog(null, "No existe ningun registro que coincida con la huella.\n***NO PUEDE REALIZARSE EL Préstamo!!!", "Verificacion de Huella", JOptionPane.ERROR_MESSAGE);
            //JOptionPane.showMessageDialog(null, "Esta huella no se encontró registrada en la base de datos\nPruebe actualizar el registro de un Alumno/Académico", "Verificacion de Huella", JOptionPane.ERROR_MESSAGE);
            setTemplate(null);
        }catch(SQLException e){
            //Si ocurre un error lo indica en la consola
            //System.err.println("Error al identificar huella digital."+e.getMessage());
            JOptionPane.showMessageDialog(null, "Ocurrió un error, excepción:"+e+"\n Descripcion:"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }/*catch(Exception e){
            JOptionPane.showMessageDialog(null, "Esta huella no se encontró registrada en la base de datos\nPruebe actualizar el registro de un Alumno/Académico, la excepcion es "+e+" y su descripcion es "+e.getMessage(), "Verificacion de Huella", JOptionPane.ERROR_MESSAGE);
        }*/
        finally{
            conexionConsulta.desconectar();
        }//fin try-catch-finally
    } //fin metodo prestamoPrevio()
    
    /*public void devolver() throws Exception
    {   int confirmaNumero = 3; //cuando no se ha confirmado el numero de cuenta
        int confirmarPenalizacion = 1;
        try
        {
            //Establece los valores para la sentencia SQL
            //////Connection c = cn.conectar();
            //obtiene todas las huellas de la bd
            //////PreparedStatement identificarStmt = c.prepareStatement("SELECT NUM_CTA, NOMBRE, IMG_HUELLA FROM ALUMNOS");
            //////ResultSet rs = identificarStmt.executeQuery();
            //si se encuentra el nombre en la base de datos
            rs.beforeFirst();  //para que vaya antes del inicio del ResultSet para que en el while comience en el inicio
            while(rs.next())
            {
                //lee la plantilla de la base de datos
                byte templateBuffer[] = rs.getBytes("IMG_HUELLA");
                String nombre = rs.getString("NOMBRE");
                String cuenta = rs.getString("NUM_CTA");
                //crea una nueva plantilla a partir de la guardada en la base de datos
                DPFPTemplate referenceTemplate = DPFPGlobal.getTemplateFactory().createTemplate(templateBuffer);
                //envia la plantilla creada al objeto contenedor de Template del componente de huella digital
                setTemplate(referenceTemplate);
                //Compara las caracteristicas de la huella recientemente capturada con
                //alguna plantilla gurdada en la base de datos que coincide con ese tipo
                DPFPVerificationResult result = Verificador.verify(featuresverificacion , getTemplate());
                //compara las plantillas (actual vs bd)
                //Si encuentra correspondencia dibuja el mapa e indica el nombre de la persona que coincidio
                if(result.isVerified())
                {
                    //crea la imagen de los datos guardados de las huellas guardadas en la base de datos
                    //JOptionPane.showMessageDialog(null, "La huella capturada esta registrada a nombre de: "+nombre+"\n con Número de Cuenta/RFC: "+cuenta, "Verificacion de Huella", JOptionPane.INFORMATION_MESSAGE );
                        try
                        {   Connection c = conexionConsulta.conectar();
                            //revisa que exista el equipo y que este con edo de prestamo
                            PreparedStatement buscarEquipo = c.prepareStatement("SELECT P.ID_LAB, P.HORA_INICIO as horaInicio,P.FECHA,P.ID_PRESTAMO, P.ID_EQUIPO, NUM_COMP, CURTIME() AS HORA FROM PRESTAMOS P, EQUIPOS E WHERE NUM_CTA = ? AND P.ID_EQUIPO = E.ID_EQUIPO AND E.FECHA_PRESTAMO = P.FECHA AND E.HORA_PRESTAMO = P.HORA_INICIO AND ASIGNADA = 1 ORDER BY P.ID_PRESTAMO DESC");
                            buscarEquipo.setString(1, cuenta);
                            ResultSet be = buscarEquipo.executeQuery();
                            
                            
                            if(be.next() == true) //si hay prestamo
                            {   String idPrestamo = be.getString("P.ID_PRESTAMO");
                                String idEquipo = be.getString("P.ID_EQUIPO");
                                String numComp = be.getString("NUM_COMP");
                                String fecha = be.getString("FECHA");
                                String hora = be.getString("HORA");
                                String idLab =be.getString("ID_LAB");
                                String horainicio = be.getString("horaInicio");
                                System.out.println("horaInicio:" + fecha+" "+horainicio + "hora fin:" + fecha+" "+hora);
                                Date hourBegin = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fecha+" "+horainicio);
                                //Date hourBegin = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2018-12-05 15:42:32");
                                Date hourEnd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fecha+" "+hora);
                                //Date hourEnd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2018-12-13 22:24:32");
                                double diferencia = (double)((((hourEnd.getTime()- hourBegin.getTime()))-7200000)/ 60000);
                                //7200000 milisegundos para dos horas
                                //8100000 milisegundos para dos horas y 15 minutos  es la tolerancia
                                
                                 //para mostrar que se encontro el equipo y pedir si hay observaciones
                                confirmaNumero = JOptionPane.showConfirmDialog(null, "El equipo "+numComp+" está registrada en préstamo a nombre de: "+nombre+"\n con Número de Cuenta/RFC: "+cuenta+"", "Identificación de Huella", JOptionPane.OK_CANCEL_OPTION );
                                //System.out.println("La confirmacion de numero vale "+confirmaNumero);
                                if(confirmaNumero == 0)
                                {
                                
                                System.out.println(hourBegin + " " + hourEnd + " Diferencia de tiempo: "+ diferencia);
                                //if (1==1){
                                if (diferencia> 15){ //Si es mayor a los 15 minutos de tolerancia se procede a penalizar 
                                    //Se muestra alerta al usuario indicando el tiempo que ha excedido y las semanas que corresponden 
                                    int hours = (int)(diferencia /60);
                                    int minutos = (int)diferencia % 60;
                                    String mensaje =  "El alumno lleva " + hours+" horas con "+minutos+ " minutos de retraso \n Acreedor a "+ Math.ceil(diferencia/15)+" semanas de penalización";
                                    confirmarPenalizacion = JOptionPane.showConfirmDialog(null,mensaje,"Tiempo de Prestamo Excedido",JOptionPane.OK_CANCEL_OPTION);
                                    if (confirmarPenalizacion == 0){
                                        JDateChooser jd = new JDateChooser();
                                        String message ="Selecciona fecha";
                                        Object[] params = {message,jd};
                                        String fPenalizacion = JOptionPane.showInputDialog(null,params,"Fecha Penalización",JOptionPane.OK_CANCEL_OPTION);
                                        String datePenalizacion="";
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        datePenalizacion=sdf.format(((JDateChooser)params[1]).getDate());
                                        System.out.println("Fecha de termino de penalizacion "+ datePenalizacion);
                                        Calendar dateNow = Calendar.getInstance();
                                        String fechaNow = sdf.format(new Date());
                                        int hNow = dateNow.get(Calendar.HOUR_OF_DAY);
                                        String mNow = Integer.toString( dateNow.get(Calendar.MINUTE));
                                        if (mNow.length()==1){ mNow= "0"+mNow; }
                                        String sNow =  Integer.toString(dateNow.get(Calendar.SECOND));
                                        if (sNow.length()==1){ sNow= "0"+sNow; }
                                        String hourNow = Integer.toString(hNow)+":"+mNow+":"+sNow;
                                        
                                        try{
                                        PreparedStatement penalizaUser = c.prepareStatement("INSERT INTO PENALIZACIONES (EDO_PENALIZA,FECHA_INICIO, FECHA_FIN_PENALIZA, HORA_PENALIZA, LAB_PENALIZA, RAZON_PENALIZA,TIPO_PENALIZACION,MULTA,FK_NUM_CTA,DIA_DESPENALIZA,HORA_DESPENALIZA,LAB_DESPENALIZA) VALUES "
                                                + "('1',?,?,?,?,?,?,?,?,?,?,?)");
                                        penalizaUser.setString(1, fechaNow);
                                        penalizaUser.setString(2, datePenalizacion);
                                        penalizaUser.setString(3, hourNow);
                                        penalizaUser.setString(4, idLab);
                                        penalizaUser.setString(5, mensaje);
                                        penalizaUser.setString(6, "2");
                                        penalizaUser.setString(7, "0.0");
                                        penalizaUser.setString(8, cuenta);
                                        penalizaUser.setString(9, datePenalizacion);
                                        penalizaUser.setString(10,hourNow);
                                        penalizaUser.setString(11,"SIN LAB" );
                                        System.out.println(penalizaUser);
                                        penalizaUser.execute();
                                        penalizaUser.close();
                                        JOptionPane.showMessageDialog(null, "Se ha registrado la penalizacion", "Éxito", JOptionPane.INFORMATION_MESSAGE );
                                        conexionConsulta.desconectar();
                                        
                                        }catch(SQLException e){
                                            JOptionPane.showMessageDialog(null, "Ocurrió un error al registrar la penalización\nExcepción es "+e+" y su descripcion:"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
                                             labelImagenHuella.setIcon(null);
                                        }finally{
                                            conexionConsulta.desconectar();
                                        }
                                        
                                        
                                    }
                                
                                }
                                //para mostrar que se encontro el equipo y pedir si hay observaciones
                                //confirmaNumero = JOptionPane.showConfirmDialog(null, "El equipo "+numComp+" está registrada en préstamo a nombre de: "+nombre+"\n con Número de Cuenta/RFC: "+cuenta+"", "Identificación de Huella", JOptionPane.OK_CANCEL_OPTION );
                                //System.out.println("La confirmacion de numero vale "+confirmaNumero);
                                //if(confirmaNumero == 0)
                                //{
                                    String observaciones = JOptionPane.showInputDialog(null, "Indique si tiene alguna observación respecto al equipo devuelto:", "Observaciones", JOptionPane.QUESTION_MESSAGE );
                                    //System.out.println("observaciones vale "+observaciones);
                                    
                                        try{
                                        //JOptionPane.showMessageDialog(null, "El usuario tiene asigando el equipo, hacer la sentencia de prestamo ", "Resultado", JOptionPane.INFORMATION_MESSAGE );
                                        PreparedStatement liberarEquipo = c.prepareStatement("UPDATE EQUIPOS SET ASIGNADA = 0 WHERE ID_EQUIPO = ?");
                                        liberarEquipo.setString(1, idEquipo);
                                        liberarEquipo.execute();
                                        liberarEquipo.close();

                                        //Time t = new Time();
                                        //String hora = t.getTime();
                                        PreparedStatement liberarPrestamo = c.prepareStatement("UPDATE PRESTAMOS SET HORA_FIN=?, OBS_DEVOLUCION_MOVIL=? WHERE ID_PRESTAMO = ?");
                                        liberarPrestamo.setString(1, hora);
                                        liberarPrestamo.setString(2, observaciones);
                                        liberarPrestamo.setString(3, idPrestamo);
                                        liberarPrestamo.execute();
                                        liberarPrestamo.close();

                                        JOptionPane.showMessageDialog(null, "Se ha registrado con Éxito la devolución del equipo "+numComp, "Éxito", JOptionPane.INFORMATION_MESSAGE );
                                        conexionConsulta.desconectar();
                                        Reclutador.clear();
                                        labelImagenHuella.setIcon(null);
                                        botonAlta.setEnabled(false);
                                        botonActualizarAgregandoHuella.setEnabled(false);
                                        botonVerificar.setEnabled(true);
                                        botonIdentificar.setEnabled(false);
                                        botonPrestamo.setEnabled(false);
                                        botonDevolucion.setEnabled(false);
                                        }catch(SQLException e){
                                            JOptionPane.showMessageDialog(null, "Ocurrió un error al realizar el préstamo\nExcepción es "+e+" y su descripcion:"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
                                             labelImagenHuella.setIcon(null);
                                        }finally{
                                            conexionConsulta.desconectar();
                                        }//fin del try-catch-finally
                                }
                                else
                                {   conexionConsulta.desconectar();
                                    EnviarTexto("Proceso de devolución cancelado por el usuario");
                                    labelImagenHuella.setIcon(null);
                                }
                            }//fin if
                            else
                            {   JOptionPane.showMessageDialog(null, "No se tiene ningun equipo en préstamo para "+nombre, "Advertencia", JOptionPane.WARNING_MESSAGE );
                                labelImagenHuella.setIcon(null);

                            }
                            
                        
                        }catch(SQLException e){
                            JOptionPane.showMessageDialog(null, "Ocurrió un error al buscar algún equipo asignado a "+nombre+"\nExcepcion es "+e+", su getErrorCode es "+e.getErrorCode()+" y su descripcion:"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
                        
                        }catch(NullPointerException e){ //para cuando se hizo Reset y aun así intenta identificarse una huella
                            //System.err.println("No hay huella para trabajar con ella");
                            JOptionPane.showMessageDialog(null, "No hay huella para trabajar con ella, capture alguna con el lector", "Excepción", JOptionPane.ERROR_MESSAGE);
                        }
                        finally{
                            conexionConsulta.desconectar();       
                        }//fin del try-catch-finally
                        
                    
                    
                }//fin if verificado
                
            }//fin while
            //Si no encuentra alguna huella correspondiente 
            /*if(confirmaNumero == 3){
                JOptionPane.showMessageDialog(null, "Esta huella no se encontró registrada en la base de datos\nPruebe actualizar el registro de un Alumno/Académico", "Verificacion de Huella", JOptionPane.ERROR_MESSAGE);
                setTemplate(null);
            } //aqui se interrumpe comentario insertar de cerrar aqui
        }catch(SQLException e){
            //Si ocurre un error lo indica en la consola
            System.err.println("Error al identificar huella digital."+e.getMessage());
            JOptionPane.showMessageDialog(null, "No existe ningun registro que coincida con la huella.\n***NO PUEDE REALIZARSE EL Préstamo!!!", "Error", JOptionPane.ERROR_MESSAGE );
            //JOptionPane.showMessageDialog(null, "Esta huella no se encontró registrada en la base de datos\nPruebe actualizar el registro de un Alumno/Académico", "Verificacion de Huella", JOptionPane.ERROR_MESSAGE);
        }catch(NullPointerException e){ //para cuando se hizo Reset y aun así intenta identificarse una huella
            //System.err.println("No hay huella para trabajar con ella");
            JOptionPane.showMessageDialog(null, "No hay huella para trabajar con ella, capture alguna con el lector", "Excepción", JOptionPane.ERROR_MESSAGE);
        }/*catch(Exception e){
            JOptionPane.showMessageDialog(null, "Esta huella no se encontró registrada en la base de datos\nPruebe actualizar el registro de un Alumno/Académico, la excepcion es "+e+" y su descripcion es "+e.getMessage(), "Verificacion de Huella", JOptionPane.ERROR_MESSAGE);
        }//aqui se interrumpe comentario insertar de cerrar aqui
        /*
        finally{
            cn.desconectar();
        }//fin try-catch-finally
        //aqui se interrumpe comentario insertar de cerrar aqui
    }//fin metodo devolver()
    */
    
    //metodo devolucion con numero de cuenta
    /*public void devolverCta() throws Exception
    {   
        String cuenta = JOptionPane.showInputDialog("Número de Cuenta o RFC:");
        //System.out.println("El contenido es "+cuenta);
        if(cuenta.length() > 0)
        {
            try
            {
                //Establece los valores para la sentencia SQL
                Connection c=conexionConsulta.conectar();
                //obtiene la plantilla correspondiente a la persona indicada
                PreparedStatement identificarStmt = c.prepareStatement("SELECT NUM_CTA, NOMBRE, IMG_HUELLA FROM ALUMNOS WHERE NUM_CTA = ?");
                identificarStmt.setString(1, cuenta);
                ResultSet rs = identificarStmt.executeQuery();
                
                //Si se encuentra el numero de cuenta en la base de datos
                if(rs.next()){
                    String nombre = rs.getString("NOMBRE");
                    //JOptionPane.showMessageDialog(null, "El número de cuenta "+cuenta+"\n corresponde a "+nombre, "Usuario válido", JOptionPane.INFORMATION_MESSAGE );
                    int confirmaNumero = JOptionPane.showConfirmDialog(null, "Número de cuenta/RFC "+cuenta+" registrado a nombre de: "+nombre, "Identificación de Huella", JOptionPane.OK_CANCEL_OPTION );
                    //System.out.println("La confirmacion de numero vale "+confirmaNumero);
                    if(confirmaNumero == 0) //si dio clic en Aceptar
                    {       //revisa que tenga un equipo con edo de prestamo
                            PreparedStatement buscarEquipo = c.prepareStatement("SELECT P.ID_PRESTAMO, P.ID_EQUIPO, NUM_COMP FROM PRESTAMOS P, EQUIPOS E WHERE NUM_CTA = ? AND P.ID_EQUIPO = E.ID_EQUIPO AND E.FECHA_PRESTAMO = P.FECHA AND E.HORA_PRESTAMO = P.HORA_INICIO AND ASIGNADA = 1 ORDER BY P.ID_PRESTAMO DESC");
                            buscarEquipo.setString(1, cuenta);
                            ResultSet be = buscarEquipo.executeQuery();
                            
                            if(be.next() == true) //si tiene un equipo en prestamo
                            {   String idPrestamo = be.getString("P.ID_PRESTAMO");
                                String idEquipo = be.getString("P.ID_EQUIPO");
                                String numComp = be.getString("NUM_COMP");
                                
                                //para mostrar que se encontro el equipo y pedir si hay observaciones
                                int confirma = JOptionPane.showConfirmDialog(null, "El número de cuenta/RFC "+cuenta+" tiene el equipo "+numComp+" en préstamo\nLiberar?", "Identificación de Huella", JOptionPane.OK_CANCEL_OPTION );
                                //System.out.println("La confirmacion de numero vale "+confirmaNumero);
                                if(confirma == 0)
                                {
                                    String observaciones = JOptionPane.showInputDialog(null, "Indique si tiene alguna observación respecto al equipo devuelto:", "Observaciones", JOptionPane.QUESTION_MESSAGE );
                                    //System.out.println("observaciones vale "+observaciones);
                                    
                                        try{
                                        //JOptionPane.showMessageDialog(null, "El usuario tiene asigando el equipo, hacer la sentencia de prestamo ", "Resultado", JOptionPane.INFORMATION_MESSAGE );
                                        PreparedStatement liberarEquipo = c.prepareStatement("UPDATE EQUIPOS SET ASIGNADA = 0 WHERE ID_EQUIPO = ?");
                                        liberarEquipo.setString(1, idEquipo);
                                        liberarEquipo.execute();
                                        liberarEquipo.close();

                                        //Time t = new Time();
                                        //String hora = t.getTime();
                                        String fecha = new SimpleDateFormat("yyyy/MM/dd").format(Calendar.getInstance().getTime());
                                        String hora = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
                                        PreparedStatement liberarPrestamo = c.prepareStatement("UPDATE PRESTAMOS SET HORA_FIN=?, OBS_DEVOLUCION_MOVIL=? WHERE ID_PRESTAMO = ?");
                                        liberarPrestamo.setString(1, hora);
                                        liberarPrestamo.setString(2, observaciones);
                                        liberarPrestamo.setString(3, idPrestamo);
                                        liberarPrestamo.execute();
                                        liberarPrestamo.close();

                                        JOptionPane.showMessageDialog(null, "Se ha registrado con Éxito la devolución del equipo "+numComp, "Éxito", JOptionPane.INFORMATION_MESSAGE );
                                        conexionConsulta.desconectar();
                                        botonAlta.setEnabled(false);
                                        botonActualizarAgregandoHuella.setEnabled(false);
                                        botonVerificar.setEnabled(true);
                                        botonIdentificar.setEnabled(false);
                                        botonPrestamo.setEnabled(false);
                                        botonDevolucion.setEnabled(false);
                                        }catch(SQLException e){
                                            JOptionPane.showMessageDialog(null, "Ocurrió un error al realizar el préstamo\nExcepción es "+e+" y su descripcion:"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );

                                        }finally{
                                            conexionConsulta.desconectar();
                                        }//fin del try-catch-finally
                                }
                                else
                                {   conexionConsulta.desconectar();
                                    EnviarTexto("Proceso de devolución cancelado por el usuario");
                                }
                            }//fin if
                            else
                            {   conexionConsulta.desconectar();
                                JOptionPane.showMessageDialog(null, "No se tiene ningun equipo en préstamo para "+nombre, "Advertencia", JOptionPane.WARNING_MESSAGE );
                            }
                           
                    }//fin si confirmo
                    else
                    {   conexionConsulta.desconectar();
                        EnviarTexto("Proceso de devolución cancelado por el usuario");
                    }//fin si cancelo
                }//fin si se encuentra el numcta en bd
                else //no se encuentra el numcta en bd
                {
                }
            }catch(Exception e){
                 JOptionPane.showMessageDialog(null, "Ocurrió una excepción de tipo "+e+"\nSu descripcion:"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );   
            }finally{
                conexionConsulta.desconectar();       
            }//fin del try-catch-finally
        }//fin is no fue nulo
        else //cuando el dato fue nulo
        {   JOptionPane.showMessageDialog(null, "No se recibió número de cuenta ni RFC\n Proporcione alguno", "Error", JOptionPane.WARNING_MESSAGE);
        }   
    }//fin metodo devolverCta()
    */
    public void devolver() throws Exception
    {   int confirmaNumero = 3; //cuando no se ha confirmado el numero de cuenta
        int confirmarPenalizacion = 1;
         String mensaje = "";
        try
        {
            //Establece los valores para la sentencia SQL
            //////Connection c = cn.conectar();
            //obtiene todas las huellas de la bd
            //////PreparedStatement identificarStmt = c.prepareStatement("SELECT NUM_CTA, NOMBRE, IMG_HUELLA FROM ALUMNOS");
            //////ResultSet rs = identificarStmt.executeQuery();
            //si se encuentra el nombre en la base de datos
            rs.beforeFirst();  //para que vaya antes del inicio del ResultSet para que en el while comience en el inicio
            while(rs.next())
            {
                //lee la plantilla de la base de datos
                byte templateBuffer[] = rs.getBytes("IMG_HUELLA");
                String nombre = rs.getString("NOMBRE");
                String cuenta = rs.getString("NUM_CTA");
                //crea una nueva plantilla a partir de la guardada en la base de datos
                DPFPTemplate referenceTemplate = DPFPGlobal.getTemplateFactory().createTemplate(templateBuffer);
                //envia la plantilla creada al objeto contenedor de Template del componente de huella digital
                setTemplate(referenceTemplate);
                //Compara las caracteristicas de la huella recientemente capturada con
                //alguna plantilla gurdada en la base de datos que coincide con ese tipo
                DPFPVerificationResult result = Verificador.verify(featuresverificacion , getTemplate());
                //compara las plantillas (actual vs bd)
                //Si encuentra correspondencia dibuja el mapa e indica el nombre de la persona que coincidio
                if(result.isVerified())
                {
                    //crea la imagen de los datos guardados de las huellas guardadas en la base de datos
                    //JOptionPane.showMessageDialog(null, "La huella capturada esta registrada a nombre de: "+nombre+"\n con Número de Cuenta/RFC: "+cuenta, "Verificacion de Huella", JOptionPane.INFORMATION_MESSAGE );
                        try
                        {   Connection c = conexionConsulta.conectar();
                            //revisa que exista el equipo y que este con edo de prestamo
                            PreparedStatement buscarEquipo = c.prepareStatement("SELECT P.ID_LAB, P.HORA_INICIO as horaInicio,P.FECHA,P.ID_PRESTAMO, P.ID_EQUIPO, NUM_COMP, CURTIME() AS HORA FROM PRESTAMOS P, EQUIPOS E WHERE NUM_CTA = ? AND P.ID_EQUIPO = E.ID_EQUIPO AND E.FECHA_PRESTAMO = P.FECHA AND E.HORA_PRESTAMO = P.HORA_INICIO AND ASIGNADA = 1 ORDER BY P.ID_PRESTAMO DESC");
                            buscarEquipo.setString(1, cuenta);
                            ResultSet be = buscarEquipo.executeQuery();
                            
                            
                            if(be.next() == true) //si hay prestamo
                            {   String idPrestamo = be.getString("P.ID_PRESTAMO");
                                String idEquipo = be.getString("P.ID_EQUIPO");
                                String numComp = be.getString("NUM_COMP");
                                String fecha = be.getString("FECHA");
                                String hora = be.getString("HORA");
                                String idLab =be.getString("ID_LAB");
                                String horainicio = be.getString("horaInicio");
                                System.out.println("horaInicio:" + fecha+" "+horainicio + "hora fin:" + fecha+" "+hora);
                                Date hourBegin = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fecha+" "+horainicio);
                                //Date hourBegin = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2018-12-05 15:42:32");
                                Date hourEnd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fecha+" "+hora);
                                //Date hourEnd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2018-12-13 22:24:32");
                                double diferencia = (double)((((hourEnd.getTime()- hourBegin.getTime()))-7200000)/ 60000);
                                //7200000 milisegundos para dos horas
                                //8100000 milisegundos para dos horas y 15 minutos  es la tolerancia
                                
                                 //para mostrar que se encontro el equipo y pedir si hay observaciones
                                confirmaNumero = JOptionPane.showConfirmDialog(null, "El equipo "+numComp+" está registrada en préstamo a nombre de: "+nombre+"\n con Número de Cuenta/RFC: "+cuenta+"", "Identificación de Huella", JOptionPane.OK_CANCEL_OPTION );
                                //System.out.println("La confirmacion de numero vale "+confirmaNumero);
                                if(confirmaNumero == 0)
                                {
                                
                                System.out.println(hourBegin + " " + hourEnd + " Diferencia de tiempo: "+ diferencia);
                                //if (1==1){
                                if (diferencia> 15){ //Si es mayor a los 15 minutos de tolerancia se procede a penalizar 
                                    //Se muestra alerta al usuario indicando el tiempo que ha excedido y las semanas que corresponden 
                                    int hours = (int)(diferencia /60);
                                    int minutos = (int)diferencia % 60;
                                    mensaje =  "El alumno lleva " + hours+" horas con "+minutos+ " minutos de retraso \n Acreedor a "+ Math.ceil(diferencia/15)+" semanas de penalización";
                                    confirmarPenalizacion = JOptionPane.showConfirmDialog(null,mensaje,"Tiempo de Prestamo Excedido",JOptionPane.OK_CANCEL_OPTION);
                                    if (confirmarPenalizacion == 0){
                                        /*JDateChooser jd = new JDateChooser();
                                        String message ="Selecciona fecha";
                                        Object[] params = {message,jd};
                                        String fPenalizacion = JOptionPane.showInputDialog(null,params,"Fecha Penalización",JOptionPane.OK_CANCEL_OPTION);
                                        String datePenalizacion="";
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        datePenalizacion=sdf.format(((JDateChooser)params[1]).getDate());
                                        System.out.println("Fecha de termino de penalizacion "+ datePenalizacion);
                                        Calendar dateNow = Calendar.getInstance();
                                        String fechaNow = sdf.format(new Date());
                                        int hNow = dateNow.get(Calendar.HOUR_OF_DAY);
                                        String mNow = Integer.toString( dateNow.get(Calendar.MINUTE));
                                        if (mNow.length()==1){ mNow= "0"+mNow; }
                                        String sNow =  Integer.toString(dateNow.get(Calendar.SECOND));
                                        if (sNow.length()==1){ sNow= "0"+sNow; }
                                        String hourNow = Integer.toString(hNow)+":"+mNow+":"+sNow;*/
                                        //Date date = new Date();
                                        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        //String datePenalizacion = sdf.format(date); //Si regresa la fecha de hoy
                                        int tiempoPenalizacion = (int) Math.ceil(diferencia/15);
                                        tiempoPenalizacion = tiempoPenalizacion * 7;
                                        //System.out.println("Este es la cantidad de dias que se van a penalizar"+tiempoPenalizacion * 7);
                                        LocalDateTime today = LocalDateTime.now(); //Este es el dia de hoy
                                        DateTimeFormatter isoFecha = DateTimeFormatter.ISO_LOCAL_DATE;
                                        DateTimeFormatter isoHora = DateTimeFormatter.ISO_LOCAL_TIME;
                                        String fechaPenaliza = today.plusDays(tiempoPenalizacion).format(isoFecha);
                                        String horaPenaliza = today.format(isoHora);
                                        String fechaHoy = today.format(isoFecha);
                                        System.out.println(tiempoPenalizacion);
                                        System.out.println("Hasta esta fecha estara penalizado"+fechaPenaliza);
                                        System.out.println();
                                        try{   // Fecha de hoy   Fecha obtenida     hora actual     este se queda igual razon     se queda igual   igual   igual     fecha obtenida     hora actual  se queda igual                  
                                            DateTimeFormatter isoFechaMuestra = DateTimeFormatter.ofPattern("dd/MM/yyyy");                                            
                                            String fechaPenalizaMuestra = today.plusDays(tiempoPenalizacion).format(isoFechaMuestra);
                                            JLabel etiqueta = new JLabel("Hasta esta fecha estara penalizado"+fechaPenalizaMuestra+"Desea confirmar esta fecha de penalización");
                                            etiqueta.setFont(new Font("Arial", Font.BOLD, 18));
                                            int resp = JOptionPane.showConfirmDialog(null, etiqueta,//<- EL MENSAJE 
                                            "Alerta!"/*<- El título de la ventana*/, JOptionPane.YES_NO_OPTION/*Las opciones (si o no)*/, JOptionPane.WARNING_MESSAGE/*El tipo de ventana, en este caso WARNING*/);
                                            if (resp == 0){
                                                    PreparedStatement penalizaUser = c.prepareStatement("INSERT INTO PENALIZACIONES (EDO_PENALIZA,FECHA_INICIO, FECHA_FIN_PENALIZA, HORA_PENALIZA, LAB_PENALIZA, RAZON_PENALIZA,TIPO_PENALIZACION,MULTA,FK_NUM_CTA,DIA_DESPENALIZA,HORA_DESPENALIZA,LAB_DESPENALIZA) VALUES "
                                                            + "('1',?,?,?,?,?,?,?,?,?,?,?)");
                                                    penalizaUser.setString(1, fechaHoy); //fecha hoy
                                                    penalizaUser.setString(2, fechaPenaliza); //fecha obtenida
                                                    penalizaUser.setString(3, horaPenaliza); //hora actual
                                                    penalizaUser.setString(4, idLab); //
                                                    penalizaUser.setString(5, mensaje); //
                                                    penalizaUser.setString(6, "2"); //
                                                    penalizaUser.setString(7, "0.0"); //
                                                    penalizaUser.setString(8, cuenta); //
                                                    penalizaUser.setString(9, fechaPenaliza); // fecha obtenida
                                                    penalizaUser.setString(10,horaPenaliza); //hora actual
                                                    penalizaUser.setString(11,"SIN LAB" );
                                                    System.out.println(penalizaUser);
                                                    penalizaUser.execute();
                                                    penalizaUser.close();
                                                    JOptionPane.showMessageDialog(null, "Se ha registrado la penalizacion, la fecha de termino de la penalizacion es:\n"+fechaPenaliza, "Éxito", JOptionPane.INFORMATION_MESSAGE );
                                                    //conexionConsulta.desconectar();
                                            } else {
                                                JDateChooser jd = new JDateChooser();
                                                String message ="Selecciona fecha";
                                                Object[] params = {message,jd};
                                                String fPenalizacion = JOptionPane.showInputDialog(null,params,"Fecha Penalización",JOptionPane.OK_CANCEL_OPTION);
                                                String datePenalizacion="";
                                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                                datePenalizacion=sdf.format(((JDateChooser)params[1]).getDate());
                                                System.out.println("Fecha de termino de penalizacion "+ datePenalizacion);
                                                Calendar dateNow = Calendar.getInstance();
                                                String fechaNow = sdf.format(new Date());
                                                int hNow = dateNow.get(Calendar.HOUR_OF_DAY);
                                                String mNow = Integer.toString( dateNow.get(Calendar.MINUTE));
                                                if (mNow.length()==1){ mNow= "0"+mNow; }
                                                String sNow =  Integer.toString(dateNow.get(Calendar.SECOND));
                                                if (sNow.length()==1){ sNow= "0"+sNow; }
                                                String hourNow = Integer.toString(hNow)+":"+mNow+":"+sNow;
                                                try{
                                                    PreparedStatement penalizaUser = c.prepareStatement("INSERT INTO PENALIZACIONES (EDO_PENALIZA,FECHA_INICIO, FECHA_FIN_PENALIZA, HORA_PENALIZA, LAB_PENALIZA, RAZON_PENALIZA,TIPO_PENALIZACION,MULTA,FK_NUM_CTA,DIA_DESPENALIZA,HORA_DESPENALIZA,LAB_DESPENALIZA) VALUES "
                                                            + "('1',?,?,?,?,?,?,?,?,?,?,?)");
                                                    penalizaUser.setString(1, fechaNow);
                                                    penalizaUser.setString(2, datePenalizacion);
                                                    penalizaUser.setString(3, hourNow);
                                                    penalizaUser.setString(4, idLab);
                                                    penalizaUser.setString(5, mensaje);
                                                    penalizaUser.setString(6, "2");
                                                    penalizaUser.setString(7, "0.0");
                                                    penalizaUser.setString(8, cuenta);
                                                    penalizaUser.setString(9, datePenalizacion);
                                                    penalizaUser.setString(10,hourNow);
                                                    penalizaUser.setString(11,"SIN LAB" );
                                                    System.out.println(penalizaUser);
                                                    penalizaUser.execute();
                                                    penalizaUser.close();
                                                    JOptionPane.showMessageDialog(null, "Se ha registrado la penalizacion", "Éxito", JOptionPane.INFORMATION_MESSAGE );
                                                }catch(SQLException e){
                                                    JOptionPane.showMessageDialog(null, "Ocurrió un error al registrar la penalización\nExcepción es "+e+" y su descripcion:"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
                                                     labelImagenHuella.setIcon(null);
                                                }
                                                    
                                            }
                                        }catch(SQLException e){
                                            JOptionPane.showMessageDialog(null, "Ocurrió un error al registrar la penalización\nExcepción es "+e+" y su descripcion:"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
                                             labelImagenHuella.setIcon(null);
                                        }/*finally{
                                            conexionConsulta.desconectar();
                                        }*/
                                        
                                        
                                    }
                                
                                }
                                //para mostrar que se encontro el equipo y pedir si hay observaciones
                                //confirmaNumero = JOptionPane.showConfirmDialog(null, "El equipo "+numComp+" está registrada en préstamo a nombre de: "+nombre+"\n con Número de Cuenta/RFC: "+cuenta+"", "Identificación de Huella", JOptionPane.OK_CANCEL_OPTION );
                                //System.out.println("La confirmacion de numero vale "+confirmaNumero);
                                //if(confirmaNumero == 0)
                                //{
                                    String observaciones = JOptionPane.showInputDialog(null, "Indique si tiene alguna observación respecto al equipo devuelto:", "Observaciones", JOptionPane.QUESTION_MESSAGE );
                                    //System.out.println("observaciones vale "+observaciones);
                                    
                                        try{
                                        //JOptionPane.showMessageDialog(null, "El usuario tiene asigando el equipo, hacer la sentencia de prestamo ", "Resultado", JOptionPane.INFORMATION_MESSAGE );
                                        PreparedStatement liberarEquipo = c.prepareStatement("UPDATE EQUIPOS SET ASIGNADA = 0 WHERE ID_EQUIPO = ?");
                                        liberarEquipo.setString(1, idEquipo);
                                        liberarEquipo.execute();
                                        liberarEquipo.close();

                                        //Time t = new Time();
                                        //String hora = t.getTime();
                                        PreparedStatement liberarPrestamo = c.prepareStatement("UPDATE PRESTAMOS SET HORA_FIN=?, OBS_DEVOLUCION_MOVIL=? WHERE ID_PRESTAMO = ?");
                                        liberarPrestamo.setString(1, hora);
                                        liberarPrestamo.setString(2, observaciones.concat(" "+mensaje));
                                        liberarPrestamo.setString(3, idPrestamo);
                                        liberarPrestamo.execute();
                                        liberarPrestamo.close();

                                        JOptionPane.showMessageDialog(null, "Se ha registrado con éxito la devolución del equipo "+numComp, "Éxito", JOptionPane.INFORMATION_MESSAGE );
                                       // conexionConsulta.desconectar();
                                        DateTimeFormatter dtf5 = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                                        System.out.println("yyyy/MM/dd-> "+dtf5.format(LocalDateTime.now()));
                                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                                        System.out.println("HH:mm:ss-> " + dtf.format(LocalDateTime.now()));
                                        PreparedStatement registroLog = c.prepareStatement("INSERT INTO LOGS(NAME_USUARIO, ACCION, FECHA_ACCION, HORA_ACCION) values(?, ?, ?, ?)" , ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                        registroLog.setString(1, usuario);
                                        registroLog.setString(2, "Realizo la devolucion del equipo "+idEquipo+" en el laboratorio"+idLab);
                                        registroLog.setString(3, dtf5.format(LocalDateTime.now()));
                                        registroLog.setString(4, dtf.format(LocalDateTime.now()));
                                        registroLog.execute();
                                        registroLog.close();
                                        
                                        Reclutador.clear();
                                        labelImagenHuella.setIcon(null);
                                        botonAlta.setEnabled(false);
                                        botonActualizarAgregandoHuella.setEnabled(false);
                                        botonVerificar.setEnabled(true);
                                        botonIdentificar.setEnabled(false);
                                        botonPrestamo.setEnabled(false);
                                        botonDevolucion.setEnabled(false);
                                        }catch(SQLException e){
                                            JOptionPane.showMessageDialog(null, "Ocurrió un error al realizar el préstamo\nExcepción es "+e+" y su descripcion:"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
                                            
                                             labelImagenHuella.setIcon(null);
                                        }/*finally{
                                            conexionConsulta.desconectar();
                                        }*///fin del try-catch-finally
                                }
                                else
                                {   //conexionConsulta.desconectar();
                                    EnviarTexto("Proceso de Devolución cancelado por el usuario");
                                    labelImagenHuella.setIcon(null);
                                }
                            }//fin if
                            else
                            {   JOptionPane.showMessageDialog(null, "No se tiene ningun equipo en préstamo para "+nombre, "Advertencia", JOptionPane.WARNING_MESSAGE );
                                labelImagenHuella.setIcon(null);

                            }
                            
                        
                        }catch(SQLException e){
                            JOptionPane.showMessageDialog(null, "Ocurrió un error al buscar algún equipo asignado a "+nombre+"\nExcepcion es "+e+", su getErrorCode es "+e.getErrorCode()+" y su descripcion:"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
                        
                        }catch(NullPointerException e){ //para cuando se hizo Reset y aun así intenta identificarse una huella
                            //System.err.println("No hay huella para trabajar con ella");
                            JOptionPane.showMessageDialog(null, "No hay huella para trabajar con ella, capture alguna con el lector", "Excepción", JOptionPane.ERROR_MESSAGE);
                        }
                        /*finally{
                            conexionConsulta.desconectar();       
                        }*///fin del try-catch-finally
                        
                    
                    
                }//fin if verificado
                
            }//fin while
            //Si no encuentra alguna huella correspondiente 
            /*if(confirmaNumero == 3){
                JOptionPane.showMessageDialog(null, "Esta huella no se encontró registrada en la base de datos\nPruebe actualizar el registro de un Alumno/Académico", "Verificacion de Huella", JOptionPane.ERROR_MESSAGE);
                setTemplate(null);
            }*/
        }catch(SQLException e){
            //Si ocurre un error lo indica en la consola
            System.err.println("Error al identificar huella digital."+e.getMessage());
            JOptionPane.showMessageDialog(null, "No existe ningun registro que coincida con la huella.\n***NO PUEDE REALIZARSE EL PRÉSTAMO!!!", "Error", JOptionPane.ERROR_MESSAGE );
            //JOptionPane.showMessageDialog(null, "Esta huella no se encontró registrada en la base de datos\nPruebe actualizar el registro de un Alumno/Académico", "Verificacion de Huella", JOptionPane.ERROR_MESSAGE);
        }catch(NullPointerException e){ //para cuando se hizo Reset y aun así intenta identificarse una huella
            //System.err.println("No hay huella para trabajar con ella");
            JOptionPane.showMessageDialog(null, "No hay huella para trabajar con ella, capture alguna con el lector", "Excepción", JOptionPane.ERROR_MESSAGE);
        }/*catch(Exception e){
            JOptionPane.showMessageDialog(null, "Esta huella no se encontró registrada en la base de datos\nPruebe actualizar el registro de un Alumno/Academico, la excepcion es "+e+" y su descripcion es "+e.getMessage(), "Verificacion de Huella", JOptionPane.ERROR_MESSAGE);
        }*/
        
        finally{
            conexionConsulta.desconectar();
        }//fin try-catch-finally
        
    }//fin metodo devolver()
    
    
    //metodo devolucion con numero de cuenta
    public void devolverCta() throws Exception
    {   
        String mensaje = "";
        String cuenta = JOptionPane.showInputDialog("Número de Cuenta o RFC:");
        int confirmarPenalizacion = 1;
        int confirmaNumero = 3;
        //System.out.println("El contenido es "+cuenta);
        if(cuenta.length() > 0)
        {
            try
            {
                //Establece los valores para la sentencia SQL
                Connection c=conexionConsulta.conectar();
                //obtiene la plantilla correspondiente a la persona indicada
                PreparedStatement identificarStmt = c.prepareStatement("SELECT NUM_CTA, NOMBRE, IMG_HUELLA FROM ALUMNOS WHERE NUM_CTA = ?");
                identificarStmt.setString(1, cuenta);
                ResultSet rs = identificarStmt.executeQuery();
                
                //Si se encuentra el numero de cuenta en la base de datos
                if(rs.next()){
                    String nombre = rs.getString("NOMBRE");
                    //JOptionPane.showMessageDialog(null, "El número de cuenta "+cuenta+"\n corresponde a "+nombre, "Usuario válido", JOptionPane.INFORMATION_MESSAGE );
                    confirmaNumero = JOptionPane.showConfirmDialog(null, "Número de cuenta/RFC "+cuenta+" registrado a nombre de: "+nombre, "Identificación de Huella", JOptionPane.OK_CANCEL_OPTION );
                    //System.out.println("La confirmacion de numero vale "+confirmaNumero);
                    if(confirmaNumero == 0) //si dio clic en Aceptar
                    {       //revisa que tenga un equipo con edo de prestamo
                            PreparedStatement buscarEquipo = c.prepareStatement("SELECT P.ID_LAB, P.HORA_INICIO as horaInicio,P.FECHA,P.ID_PRESTAMO, P.ID_EQUIPO, NUM_COMP, CURTIME() AS HORA FROM PRESTAMOS P, EQUIPOS E WHERE NUM_CTA = ? AND P.ID_EQUIPO = E.ID_EQUIPO AND E.FECHA_PRESTAMO = P.FECHA AND E.HORA_PRESTAMO = P.HORA_INICIO AND ASIGNADA = 1 ORDER BY P.ID_PRESTAMO DESC");
                            //PreparedStatement buscarEquipo = c.prepareStatement("SELECT P.ID_PRESTAMO, P.ID_EQUIPO, NUM_COMP FROM PRESTAMOS P, EQUIPOS E WHERE NUM_CTA = ? AND P.ID_EQUIPO = E.ID_EQUIPO AND E.FECHA_PRESTAMO = P.FECHA AND E.HORA_PRESTAMO = P.HORA_INICIO AND ASIGNADA = 1 ORDER BY P.ID_PRESTAMO DESC");
                            buscarEquipo.setString(1, cuenta);
                            ResultSet be = buscarEquipo.executeQuery();
                            
                            if(be.next() == true) //si tiene un equipo en prestamo
                            {   String idPrestamo = be.getString("P.ID_PRESTAMO");
                                String idEquipo = be.getString("P.ID_EQUIPO");
                                String numComp = be.getString("NUM_COMP");
                                String fecha = be.getString("FECHA");
                                String hora = be.getString("HORA");
                                String idLab =be.getString("ID_LAB");
                                String horainicio = be.getString("horaInicio");
                                System.out.println("horaInicio:" + fecha+" "+horainicio + "hora fin:" + fecha+" "+hora);
                                Date hourBegin = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fecha+" "+horainicio);
                                //Date hourBegin = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2018-12-05 15:42:32");
                                Date hourEnd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fecha+" "+hora);
                                //Date hourEnd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2018-12-13 22:24:32");
                                double diferencia = (double)((((hourEnd.getTime()- hourBegin.getTime()))-7200000)/ 60000);
                                //7200000 milisegundos para dos horas
                                //8100000 milisegundos para dos horas y 15 minutos  es la tolerancia
                                
                                 //para mostrar que se encontro el equipo y pedir si hay observaciones
                                confirmaNumero = JOptionPane.showConfirmDialog(null, "El equipo "+numComp+" está registrada en préstamo a nombre de: "+nombre+"\n con Número de Cuenta/RFC: "+cuenta+"", "Identificación de Huella", JOptionPane.OK_CANCEL_OPTION );
                                //System.out.println("La confirmacion de numero vale "+confirmaNumero);
                                
                                //para mostrar que se encontro el equipo y pedir si hay observaciones
                                int confirma = JOptionPane.showConfirmDialog(null, "El número de cuenta/RFC "+cuenta+" tiene el equipo "+numComp+" en préstamo\nLiberar?", "Identificación de Huella", JOptionPane.OK_CANCEL_OPTION );
                                //System.out.println("La confirmacion de numero vale "+confirmaNumero);
                                if(confirma == 0)
                                {
                                    System.out.println(hourBegin + " " + hourEnd + " Diferencia de tiempo: "+ diferencia);
                                //if (1==1){
                                if (diferencia> 15){ //Si es mayor a los 15 minutos de tolerancia se procede a penalizar 
                                    //Se muestra alerta al usuario indicando el tiempo que ha excedido y las semanas que corresponden 
                                    int hours = (int)(diferencia /60);
                                    int minutos = (int)diferencia % 60;
                                    mensaje =  "El alumno lleva " + hours+" horas con "+minutos+ " minutos de retraso \n Acreedor a "+ Math.ceil(diferencia/15)+" semanas de penalización";
                                    confirmarPenalizacion = JOptionPane.showConfirmDialog(null,mensaje,"Tiempo de Prestamo Excedido",JOptionPane.OK_CANCEL_OPTION);
                                    if (confirmarPenalizacion == 0){
                                        /*JDateChooser jd = new JDateChooser();
                                        String message ="Selecciona fecha";
                                        Object[] params = {message,jd};
                                        String fPenalizacion = JOptionPane.showInputDialog(null,params,"Fecha Penalización",JOptionPane.OK_CANCEL_OPTION);
                                        String datePenalizacion="";
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        datePenalizacion=sdf.format(((JDateChooser)params[1]).getDate());
                                        System.out.println("Fecha de termino de penalizacion "+ datePenalizacion);
                                        Calendar dateNow = Calendar.getInstance();
                                        String fechaNow = sdf.format(new Date());
                                        int hNow = dateNow.get(Calendar.HOUR_OF_DAY);
                                        String mNow = Integer.toString( dateNow.get(Calendar.MINUTE));
                                        if (mNow.length()==1){ mNow= "0"+mNow; }
                                        String sNow =  Integer.toString(dateNow.get(Calendar.SECOND));
                                        if (sNow.length()==1){ sNow= "0"+sNow; }
                                        String hourNow = Integer.toString(hNow)+":"+mNow+":"+sNow;*/
                                        //Date date = new Date();
                                        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        //String datePenalizacion = sdf.format(date); //Si regresa la fecha de hoy
                                        int tiempoPenalizacion = (int) Math.ceil(diferencia/15);
                                        tiempoPenalizacion = tiempoPenalizacion * 7;
                                        //System.out.println("Este es la cantidad de dias que se van a penalizar"+tiempoPenalizacion * 7);
                                        LocalDateTime today = LocalDateTime.now(); //Este es el dia de hoy
                                        DateTimeFormatter isoFecha = DateTimeFormatter.ISO_LOCAL_DATE;
                                        DateTimeFormatter isoHora = DateTimeFormatter.ISO_LOCAL_TIME;
                                        String fechaPenaliza = today.plusDays(tiempoPenalizacion).format(isoFecha);
                                        String horaPenaliza = today.format(isoHora);
                                        String fechaHoy = today.format(isoFecha);
                                        System.out.println(tiempoPenalizacion);
                                        System.out.println("Hasta esta fecha estara penalizado"+fechaPenaliza);
                                        System.out.println();
                                        DateTimeFormatter isoFechaMuestra = DateTimeFormatter.ofPattern("dd/MM/yyyy");                                            
                                        String fechaPenalizaMuestra = today.plusDays(tiempoPenalizacion).format(isoFechaMuestra);
                                        JLabel etiqueta = new JLabel("Hasta esta fecha estara penalizado:\n ***"+fechaPenalizaMuestra+"***\nDesea confirmar esta fecha de penalización");
                                        etiqueta.setFont(new Font("Arial", Font.BOLD, 12));
                                        int resp = JOptionPane.showConfirmDialog(null, etiqueta,//<- EL MENSAJE 
                                        "Alerta!"/*<- El título de la ventana*/, JOptionPane.YES_NO_OPTION/*Las opciones (si o no)*/, JOptionPane.WARNING_MESSAGE/*El tipo de ventana, en este caso WARNING*/);
                                        if (resp == 0){ // 0 es SI
                                            try{                                                                                        // Fecha de hoy   Fecha obtenida     hora actual     este se queda igual razon     se queda igual   igual   igual     fecha obtenida     hora actual  se queda igual                  
                                            PreparedStatement penalizaUser = c.prepareStatement("INSERT INTO PENALIZACIONES (EDO_PENALIZA,FECHA_INICIO, FECHA_FIN_PENALIZA, HORA_PENALIZA, LAB_PENALIZA, RAZON_PENALIZA,TIPO_PENALIZACION,MULTA,FK_NUM_CTA,DIA_DESPENALIZA,HORA_DESPENALIZA,LAB_DESPENALIZA) VALUES "
                                                    + "('1',?,?,?,?,?,?,?,?,?,?,?)");
                                            penalizaUser.setString(1, fechaHoy); //fecha hoy
                                            penalizaUser.setString(2, fechaPenaliza); //fecha obtenida
                                            penalizaUser.setString(3, horaPenaliza); //hora actual
                                            penalizaUser.setString(4, idLab); //
                                            penalizaUser.setString(5, mensaje); //
                                            penalizaUser.setString(6, "2"); //
                                            penalizaUser.setString(7, "0.0"); //
                                            penalizaUser.setString(8, cuenta); //
                                            penalizaUser.setString(9, fechaPenaliza); // fecha obtenida
                                            penalizaUser.setString(10,horaPenaliza); //hora actual
                                            penalizaUser.setString(11,"SIN LAB" );
                                            System.out.println(penalizaUser);
                                            penalizaUser.execute();
                                            penalizaUser.close();
                                            JOptionPane.showMessageDialog(null, "Se ha registrado la penalizacion, la fecha de termino de la penalizacion es:\n"+fechaPenaliza, "Éxito", JOptionPane.INFORMATION_MESSAGE );
                                            //conexionConsulta.desconectar();
                                            }catch(SQLException e){
                                                JOptionPane.showMessageDialog(null, "Ocurrió un error al registrar la penalización\nExcepción es "+e+" y su descripcion:"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
                                                 labelImagenHuella.setIcon(null);
                                            }/*finally{
                                                conexionConsulta.desconectar();
                                            }*/
                                        } else {
                                            JDateChooser jd = new JDateChooser();
                                            String message ="Selecciona fecha";
                                            Object[] params = {message,jd};
                                            String fPenalizacion = JOptionPane.showInputDialog(null,params,"Fecha Penalización",JOptionPane.OK_CANCEL_OPTION);
                                            String datePenalizacion="";
                                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                            datePenalizacion=sdf.format(((JDateChooser)params[1]).getDate());
                                            System.out.println("Fecha de termino de penalizacion "+ datePenalizacion);
                                            Calendar dateNow = Calendar.getInstance();
                                            String fechaNow = sdf.format(new Date());
                                            int hNow = dateNow.get(Calendar.HOUR_OF_DAY);
                                            String mNow = Integer.toString( dateNow.get(Calendar.MINUTE));
                                            if (mNow.length()==1){ mNow= "0"+mNow; }
                                            String sNow =  Integer.toString(dateNow.get(Calendar.SECOND));
                                            if (sNow.length()==1){ sNow= "0"+sNow; }
                                            String hourNow = Integer.toString(hNow)+":"+mNow+":"+sNow;
                                            try{
                                                PreparedStatement penalizaUser = c.prepareStatement("INSERT INTO PENALIZACIONES (EDO_PENALIZA,FECHA_INICIO, FECHA_FIN_PENALIZA, HORA_PENALIZA, LAB_PENALIZA, RAZON_PENALIZA,TIPO_PENALIZACION,MULTA,FK_NUM_CTA,DIA_DESPENALIZA,HORA_DESPENALIZA,LAB_DESPENALIZA) VALUES "
                                                        + "('1',?,?,?,?,?,?,?,?,?,?,?)");
                                                penalizaUser.setString(1, fechaNow);
                                                penalizaUser.setString(2, datePenalizacion);
                                                penalizaUser.setString(3, hourNow);
                                                penalizaUser.setString(4, idLab);
                                                penalizaUser.setString(5, mensaje);
                                                penalizaUser.setString(6, "2");
                                                penalizaUser.setString(7, "0.0");
                                                penalizaUser.setString(8, cuenta);
                                                penalizaUser.setString(9, datePenalizacion);
                                                penalizaUser.setString(10,hourNow);
                                                penalizaUser.setString(11,"SIN LAB" );
                                                System.out.println(penalizaUser);
                                                penalizaUser.execute();
                                                penalizaUser.close();
                                                JOptionPane.showMessageDialog(null, "Se ha registrado la penalizacion", "Éxito", JOptionPane.INFORMATION_MESSAGE );
                                                fechaPenaliza = datePenalizacion;
                                            }catch(SQLException e){
                                                JOptionPane.showMessageDialog(null, "Ocurrió un error al registrar la penalización\nExcepción es "+e+" y su descripcion:"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
                                                 labelImagenHuella.setIcon(null);
                                            }
                                        }
                                        
                                    }
                                
                                }
                                //para mostrar que se encontro el equipo y pedir si hay observaciones
                                //confirmaNumero = JOptionPane.showConfirmDialog(null, "El equipo "+numComp+" está registrada en préstamo a nombre de: "+nombre+"\n con Número de Cuenta/RFC: "+cuenta+"", "Identificación de Huella", JOptionPane.OK_CANCEL_OPTION );
                                //System.out.println("La confirmacion de numero vale "+confirmaNumero);
                                //if(confirmaNumero == 0)
                                //{
                                    String observaciones = JOptionPane.showInputDialog(null, "Indique si tiene alguna observación respecto al equipo devuelto:", "Observaciones", JOptionPane.QUESTION_MESSAGE );
                                    //System.out.println("observaciones vale "+observaciones);
                                    if(observaciones.equals("")){ //Si la observación queda vacia
                                        //No se realiza nada
                                    } else {
                                        aumentoAmonestacion(observaciones, cuenta, idLab);
                                    }
                                    
                                        try{
                                        //JOptionPane.showMessageDialog(null, "El usuario tiene asigando el equipo, hacer la sentencia de prestamo ", "Resultado", JOptionPane.INFORMATION_MESSAGE );
                                        PreparedStatement liberarEquipo = c.prepareStatement("UPDATE EQUIPOS SET ASIGNADA = 0 WHERE ID_EQUIPO = ?");
                                        liberarEquipo.setString(1, idEquipo);
                                        liberarEquipo.execute();
                                        liberarEquipo.close();

                                        //Time t = new Time();
                                        //String hora = t.getTime();
                                        //String fecha = new SimpleDateFormat("yyyy/MM/dd").format(Calendar.getInstance().getTime());
                                        String hora2 = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
                                        PreparedStatement liberarPrestamo = c.prepareStatement("UPDATE PRESTAMOS SET HORA_FIN=?, OBS_DEVOLUCION_MOVIL=? WHERE ID_PRESTAMO = ?");
                                        liberarPrestamo.setString(1, hora2);
                                        liberarPrestamo.setString(2, observaciones.concat(" "+mensaje));
                                        liberarPrestamo.setString(3, idPrestamo);
                                        liberarPrestamo.execute();
                                        liberarPrestamo.close();

                                        JOptionPane.showMessageDialog(null, "Se ha registrado con éxito la devolución del equipo "+numComp, "Éxito", JOptionPane.INFORMATION_MESSAGE );
                                        //conexionConsulta.desconectar();
                                        DateTimeFormatter dtf5 = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                                        System.out.println("yyyy/MM/dd-> "+dtf5.format(LocalDateTime.now()));
                                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                                        System.out.println("HH:mm:ss-> " + dtf.format(LocalDateTime.now()));
                                        PreparedStatement registroLog = c.prepareStatement("INSERT INTO LOGS(NAME_USUARIO, ACCION, FECHA_ACCION, HORA_ACCION) values(?, ?, ?, ?)" , ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                        registroLog.setString(1, usuario);
                                        registroLog.setString(2, "Realizo la devolucion del equipo "+idEquipo+" en el laboratorio"+idLab);
                                        registroLog.setString(3, dtf5.format(LocalDateTime.now()));
                                        registroLog.setString(4, dtf.format(LocalDateTime.now()));
                                        registroLog.execute();
                                        registroLog.close();
                                        
                                        botonAlta.setEnabled(false);
                                        botonActualizarAgregandoHuella.setEnabled(false);
                                        botonVerificar.setEnabled(true);
                                        botonIdentificar.setEnabled(false);
                                        botonPrestamo.setEnabled(false);
                                        botonDevolucion.setEnabled(false);
                                        }catch(SQLException e){
                                            JOptionPane.showMessageDialog(null, "Ocurrió un error al realizar el préstamo\nExcepción es "+e+" y su descripcion:"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );

                                        }finally{
                                            conexionConsulta.desconectar();
                                        }//fin del try-catch-finally
                                }
                                else
                                {   conexionConsulta.desconectar();
                                    EnviarTexto("Proceso de Devolución cancelado por el usuario");
                                }
                            }//fin if
                            else
                            {   conexionConsulta.desconectar();
                                JOptionPane.showMessageDialog(null, "No se tiene ningun equipo en préstamo para "+nombre, "Advertencia", JOptionPane.WARNING_MESSAGE );
                            }
                           
                    }//fin si confirmo
                    else
                    {   conexionConsulta.desconectar();
                        EnviarTexto("Proceso de devolución cancelado por el usuario");
                    }//fin si cancelo
                }//fin si se encuentra el numcta en bd
                else //no se encuentra el numcta en bd
                {
                }
            }catch(Exception e){
                 JOptionPane.showMessageDialog(null, "Ocurrió una excepción de tipo "+e+"\nSu descripcion:"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );   
            }finally{
                conexionConsulta.desconectar();       
            }//fin del try-catch-finally
        }//fin is no fue nulo
        else //cuando el dato fue nulo
        {   JOptionPane.showMessageDialog(null, "No se recibió número de cuenta ni RFC\n Proporcione alguno", "Error", JOptionPane.WARNING_MESSAGE);
        }   
    }//fin metodo devolverCta()
    
    
    public void aumentoAmonestacion(String observaciones, String cuenta, String idLab){
        Connection c = conexionConsulta.conectar();
        //String razonAmonesta = observaciones;
        try{
            PreparedStatement consultaAmoestacion = c.prepareStatement("SELECT AMONESTACIONES, RAZON_AMONESTA FROM ALUMNOS WHERE NUM_CTA = ? ", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            consultaAmoestacion.setString(1, cuenta);
            ResultSet rs = consultaAmoestacion.executeQuery();
            if (rs.next()){
                int numAmonestaciones = rs.getInt("AMONESTACIONES");
                numAmonestaciones = numAmonestaciones + 1; 
                String razonAmonestaReg = rs.getString("RAZON_AMONESTA");
                if(numAmonestaciones == 3){ //Tiene tres amonestaciones
                    PreparedStatement pstm = c.prepareStatement("SELECT FECHA_FIN_PENALIZA, ID_PENALIZACION, RAZON_PENALIZA FROM PENALIZACIONES WHERE FK_NUM_CTA = ? AND EDO_PENALIZA = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    pstm.setString(1, cuenta);
                    pstm.setString(2, "1");
                    ResultSet res = pstm.executeQuery();
                    if(res.next()){ // SI tiene una penalizacion
                        String fechaFin = res.getString("FECHA_FIN_PENALIZA"); //Obtenemos la fecha hasta la que esta penalizado
                        String idPenaliza = res.getString("ID_PENALIZACION");
                        String mensaje = res.getString("RAZON_PENALIZA");
                        mensaje.concat(",acumuló 3 obseraciones acumuladas");
                        JDateChooser jd = new JDateChooser();
                        String message ="Selecciona fecha:\nLa fecha de penalizacion registrada es:" +fechaFin+"\n RAZON DE AMONESTACION: "+razonAmonestaReg.concat(","+ observaciones);
                        Object[] params = {message,jd};
                        String fPenalizacion = JOptionPane.showInputDialog(null,params,"Fecha Penalización",JOptionPane.OK_CANCEL_OPTION);
                        String datePenalizacion="";
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        datePenalizacion=sdf.format(((JDateChooser)params[1]).getDate());
                        System.out.println("Fecha de termino de penalizacion "+ datePenalizacion);
                        Calendar dateNow = Calendar.getInstance();
                        String fechaNow = sdf.format(new Date());
                        int hNow = dateNow.get(Calendar.HOUR_OF_DAY);
                        String mNow = Integer.toString( dateNow.get(Calendar.MINUTE));
                        if (mNow.length()==1){ mNow= "0"+mNow; }
                        String sNow =  Integer.toString(dateNow.get(Calendar.SECOND));
                        if (sNow.length()==1){ sNow= "0"+sNow; }
                        String hourNow = Integer.toString(hNow)+":"+mNow+":"+sNow;
                        try{
                            PreparedStatement penalizaUser = c.prepareStatement("UPDATE PENALIZACIONES SET FECHA_FIN_PENALIZA = ?, DIA_DESPENALIZA = ?, RAZON_PENALIZA = ? WHERE ID_PENALIZACION = ?");
                            penalizaUser.setString(1, datePenalizacion);
                            penalizaUser.setString(2, datePenalizacion);
                            penalizaUser.setString(3, mensaje);
                            penalizaUser.setString(4, idPenaliza);
                            penalizaUser.execute();
                            penalizaUser.close();
                            JOptionPane.showMessageDialog(null, "Se ha ACTUALIZADO la penalizacion", "Éxito", JOptionPane.INFORMATION_MESSAGE );
                            razonAmonestaReg = razonAmonestaReg + "," + observaciones;
                            PreparedStatement amonestacionAumenta = c.prepareStatement("UPDATE ALUMNOS SET AMONESTACIONES = ?, RAZON_AMONESTA = ? WHERE NUM_CTA = ?");
                            amonestacionAumenta.setString(1, "3");
                            amonestacionAumenta.setString(2, razonAmonestaReg);
                            amonestacionAumenta.setString(3, cuenta);
                            int amon = amonestacionAumenta.executeUpdate();
                            if (amon > 0) {
                                JOptionPane.showMessageDialog(null, "Se registro exitosamente la amonestación", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(null, "Ocurrió un error al registrar la amonestación", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }catch(SQLException e){
                            JOptionPane.showMessageDialog(null, "Ocurrió un error al ACTUALIZAR la penalización\nExcepción es "+e+" y su descripcion:"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
                             labelImagenHuella.setIcon(null);
                        }
                        
                    } else { //Si no viene penalizado
                        JDateChooser jd = new JDateChooser();
                        String message ="El alumno acumuló 3 AMONESTACIONES, por favor,\nSelecciona fecha:";
                        Object[] params = {message,jd};
                        String fPenalizacion = JOptionPane.showInputDialog(null,params,"Fecha Penalización",JOptionPane.OK_CANCEL_OPTION);
                        String datePenalizacion="";
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        datePenalizacion=sdf.format(((JDateChooser)params[1]).getDate());
                        System.out.println("Fecha de termino de penalizacion "+ datePenalizacion);
                        Calendar dateNow = Calendar.getInstance();
                        String fechaNow = sdf.format(new Date());
                        int hNow = dateNow.get(Calendar.HOUR_OF_DAY);
                        String mNow = Integer.toString( dateNow.get(Calendar.MINUTE));
                        if (mNow.length()==1){ mNow= "0"+mNow; }
                        String sNow =  Integer.toString(dateNow.get(Calendar.SECOND));
                        if (sNow.length()==1){ sNow= "0"+sNow; }
                        String hourNow = Integer.toString(hNow)+":"+mNow+":"+sNow;
                        try{
                            PreparedStatement penalizaUser = c.prepareStatement("INSERT INTO PENALIZACIONES (EDO_PENALIZA,FECHA_INICIO, FECHA_FIN_PENALIZA, HORA_PENALIZA, LAB_PENALIZA, RAZON_PENALIZA,TIPO_PENALIZACION,MULTA,FK_NUM_CTA,DIA_DESPENALIZA,HORA_DESPENALIZA,LAB_DESPENALIZA) VALUES "
                                    + "('1',?,?,?,?,?,?,?,?,?,?,?)");
                            penalizaUser.setString(1, fechaNow);
                            penalizaUser.setString(2, datePenalizacion);
                            penalizaUser.setString(3, hourNow);
                            penalizaUser.setString(4, idLab);
                            penalizaUser.setString(5, "El alumno acumuló 3 obseraciones de equipo");
                            penalizaUser.setString(6, "2");
                            penalizaUser.setString(7, "0.0");
                            penalizaUser.setString(8, cuenta);
                            penalizaUser.setString(9, datePenalizacion);
                            penalizaUser.setString(10,hourNow);
                            penalizaUser.setString(11,"SIN LAB" );
                            System.out.println(penalizaUser);
                            penalizaUser.execute();
                            penalizaUser.close();
                            razonAmonestaReg = razonAmonestaReg+ ","+observaciones;
                            PreparedStatement amonestacionAumenta = c.prepareStatement("UPDATE ALUMNOS SET AMONESTACIONES = ?, RAZON_AMONESTA = ? WHERE NUM_CTA = ?");
                            amonestacionAumenta.setString(1, "3");
                            amonestacionAumenta.setString(2, razonAmonestaReg);
                            amonestacionAumenta.setString(3, cuenta);
                            int amon = amonestacionAumenta.executeUpdate();
                            if (amon > 0) {
                                JOptionPane.showMessageDialog(null, "Se registro exitosamente la amonestación", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(null, "Ocurrió un error al registrar la amonestación", "Error", JOptionPane.ERROR_MESSAGE);
                            }

                            amonestacionAumenta.close();
                            JOptionPane.showMessageDialog(null, "Se ha registrado la penalizacion", "Éxito", JOptionPane.INFORMATION_MESSAGE );
                        }catch(SQLException e){
                            JOptionPane.showMessageDialog(null, "Ocurrió un error al registrar la penalización\nExcepción es "+e+" y su descripcion:"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
                             labelImagenHuella.setIcon(null);
                        }
                    }
                } else if(numAmonestaciones < 3){
                    String amonestaciones = Integer.toString(numAmonestaciones);
                    if(razonAmonestaReg == null){
                        razonAmonestaReg = observaciones;
                    } else if(razonAmonestaReg != null) {
                        razonAmonestaReg = razonAmonestaReg+ ","+observaciones;
                    }
                    
                    PreparedStatement amonestacionAumenta = c.prepareStatement("UPDATE ALUMNOS SET AMONESTACIONES = ?, RAZON_AMONESTA = ? WHERE NUM_CTA = ?");
                    amonestacionAumenta.setString(1, amonestaciones);
                    amonestacionAumenta.setString(2, razonAmonestaReg);
                    amonestacionAumenta.setString(3, cuenta);
                    int amon = amonestacionAumenta.executeUpdate();
                    if(amon > 0){
                        JOptionPane.showMessageDialog(null, "Se registro exitosamente la amonestación", "Éxito", JOptionPane.INFORMATION_MESSAGE );
                    } else {
                        JOptionPane.showMessageDialog(null, "Ocurrió un error al registrar la amonestación", "Error", JOptionPane.ERROR_MESSAGE );
                    }
                       
                    amonestacionAumenta.close();
                    
                }
            }
            
            
        } catch (SQLException e){
            JOptionPane.showMessageDialog(null, "Ocurrió una excepción de tipo "+e+"\nSu descripcion:"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );   
        } finally{
            
        }
        
        
    }
    
    public String[] Obt_Laboratorio (){
        System.err.println("LLEANR LABS");
        int tam = 50;
        String ListaLaboratorio[] = new String [tam];
        int n = 0;
        //ListaLaboratorio.addElement("Selecciona un laboratorio");
        Connection c = conexionConsulta.conectar();
        try{
            PreparedStatement pstm = c.prepareStatement("SELECT ID_LAB FROM GRUPOLABORATORIO WHERE ID_GRUPO= ? ORDER BY ID_LAB", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            pstm.setString(1, grupo);
            ResultSet res = pstm.executeQuery();
            while(res.next()){
                ListaLaboratorio[n] = res.getString("ID_LAB");
                System.err.println("Labbbbbbbb");
                //System.out.println(ListaLaboratorio[n].toString());
                n ++;
                
            }
            
        } catch (SQLException e) {
            System.err.println("Error en la consulta:" + e.getMessage());
        }
        return ListaLaboratorio;

    }
    
    public void cerrar (){
        DateTimeFormatter dtf5 = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        System.out.println("yyyy/MM/dd-> "+dtf5.format(LocalDateTime.now()));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        System.out.println("HH:mm:ss-> " + dtf.format(LocalDateTime.now()));
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
                    }finally {
                        System.exit(0);
                    }
                stop();
                }
            });
            this.setVisible (true);
        }catch (Exception e){
            e.printStackTrace ();
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

        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        labelImagenHuella = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        botonIdentificar = new javax.swing.JButton();
        botonVerificar = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        botonAlta = new javax.swing.JButton();
        botonActualizarAgregandoHuella = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        botonPrestamo2 = new javax.swing.JButton();
        botonPrestamo = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        botonActualizarArregloHuellas = new javax.swing.JButton();
        botonLimpiar = new javax.swing.JButton();
        administrar = new javax.swing.JButton();
        botonSalir = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        botonDevolucion = new javax.swing.JButton();
        botonDevolucionCta = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(138, 196, 255));
        setExtendedState(6);
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

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ventana/titulo ajustado.jpeg"))); // NOI18N
        getContentPane().add(jLabel1);
        jLabel1.setBounds(0, 0, 1360, 150);

        textArea.setBackground(new java.awt.Color(221, 234, 234));
        textArea.setColumns(20);
        textArea.setRows(5);
        textArea.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jScrollPane2.setViewportView(textArea);

        getContentPane().add(jScrollPane2);
        jScrollPane2.setBounds(480, 160, 850, 320);

        jPanel1.setBackground(new java.awt.Color(153, 189, 191));
        jPanel1.setForeground(java.awt.SystemColor.activeCaptionBorder);

        labelImagenHuella.setBackground(new java.awt.Color(255, 255, 255));
        labelImagenHuella.setForeground(java.awt.SystemColor.activeCaption);
        labelImagenHuella.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel9.setText("Huella Digital");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelImagenHuella, javax.swing.GroupLayout.DEFAULT_SIZE, 399, Short.MAX_VALUE)
                .addGap(21, 21, 21))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(172, 172, 172)
                .addComponent(jLabel9)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelImagenHuella, javax.swing.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
                .addContainerGap())
        );

        getContentPane().add(jPanel1);
        jPanel1.setBounds(30, 160, 430, 320);

        jPanel3.setBackground(java.awt.SystemColor.controlHighlight);
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        botonIdentificar.setText("Identificar con huella");
        botonIdentificar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        botonIdentificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonIdentificarActionPerformed(evt);
            }
        });

        botonVerificar.setText("Verificar registro con #cta/RFC");
        botonVerificar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        botonVerificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonVerificarActionPerformed(evt);
            }
        });

        jLabel2.setText("Identificación de Usuarios");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(76, 76, 76)
                .addComponent(jLabel2)
                .addContainerGap(77, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(botonVerificar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(botonIdentificar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(botonIdentificar, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(botonVerificar, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getContentPane().add(jPanel3);
        jPanel3.setBounds(30, 510, 280, 103);

        jPanel4.setBackground(java.awt.SystemColor.controlHighlight);
        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        botonAlta.setText("Alta");
        botonAlta.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        botonAlta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonAltaActionPerformed(evt);
            }
        });

        botonActualizarAgregandoHuella.setText("Actualizar huella");
        botonActualizarAgregandoHuella.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        botonActualizarAgregandoHuella.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonActualizarAgregandoHuellaActionPerformed(evt);
            }
        });

        jLabel4.setText("Administracion de Usuarios");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(81, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addGap(67, 67, 67))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(botonActualizarAgregandoHuella, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(botonAlta, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(botonAlta, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(botonActualizarAgregandoHuella, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getContentPane().add(jPanel4);
        jPanel4.setBounds(380, 510, 280, 103);

        jPanel7.setBackground(java.awt.SystemColor.controlHighlight);
        jPanel7.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        botonPrestamo2.setText("Prestamo con #cta/RFC");
        botonPrestamo2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        botonPrestamo2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonPrestamo2ActionPerformed(evt);
            }
        });

        botonPrestamo.setText("Prestamo");
        botonPrestamo.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        botonPrestamo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonPrestamoActionPerformed(evt);
            }
        });

        jLabel7.setText("Préstamos");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(100, 100, 100)
                .addComponent(jLabel7)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(botonPrestamo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(botonPrestamo2, javax.swing.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(botonPrestamo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(botonPrestamo2, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getContentPane().add(jPanel7);
        jPanel7.setBounds(720, 510, 265, 103);

        jPanel8.setBackground(java.awt.SystemColor.controlHighlight);
        jPanel8.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        botonActualizarArregloHuellas.setText("Recargar base de Datos");
        botonActualizarArregloHuellas.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        botonActualizarArregloHuellas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonActualizarArregloHuellasActionPerformed(evt);
            }
        });

        botonLimpiar.setText("Reset");
        botonLimpiar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        botonLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonLimpiarActionPerformed(evt);
            }
        });

        administrar.setText("Ingresar de nuevo");
        administrar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        administrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                administrarActionPerformed(evt);
            }
        });

        botonSalir.setText("Salir");
        botonSalir.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        botonSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonSalirActionPerformed(evt);
            }
        });

        jButton1.setText("Créditos");
        jButton1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(administrar, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(botonActualizarArregloHuellas, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 438, Short.MAX_VALUE)
                .addComponent(botonLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(botonSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(botonSalir, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(botonLimpiar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(administrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(botonActualizarArregloHuellas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18))
        );

        getContentPane().add(jPanel8);
        jPanel8.setBounds(30, 630, 1310, 60);

        jPanel6.setBackground(java.awt.SystemColor.controlHighlight);
        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        botonDevolucion.setText("Devolucion");
        botonDevolucion.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        botonDevolucion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonDevolucionActionPerformed(evt);
            }
        });

        botonDevolucionCta.setText("Devolucion con #cta/RFC");
        botonDevolucionCta.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        botonDevolucionCta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonDevolucionCtaActionPerformed(evt);
            }
        });

        jLabel8.setText("Devoluciones");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(botonDevolucion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(botonDevolucionCta, javax.swing.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(104, 104, 104)
                .addComponent(jLabel8)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(botonDevolucion, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(botonDevolucionCta, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getContentPane().add(jPanel6);
        jPanel6.setBounds(1050, 510, 280, 99);

        jLabel3.setBackground(new java.awt.Color(255, 102, 102));
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ventana/extrafondo.jpg"))); // NOI18N
        getContentPane().add(jLabel3);
        jLabel3.setBounds(0, 150, 1360, 600);

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ventana/fondodef.png"))); // NOI18N
        getContentPane().add(jLabel5);
        jLabel5.setBounds(460, 0, 620, 410);

        pack();
    }// </editor-fold>//GEN-END:initComponents

 
    
    
    
    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
                // lo que sucede cuando se abre el formulario
        
        Iniciar(); //escuchar si el lector esta conectado o si se captura huella
        //start(); //avisa cuando se usa el lector de huella
        EstadoHuellas(); //indica cuantas capturas se necesitan
        System.out.println("Este es el grupo que me mandaron: " +grupo);
        botonAlta.setEnabled(false); //inicialmente el boton esta apagado
        botonIdentificar.setEnabled(false); //inicialmente el boton esta apagado
        botonVerificar.setEnabled(true); //inicialmente el boton esta encendido
        botonSalir.grabFocus(); //foco al boton de Salir
        botonActualizarAgregandoHuella.setEnabled(false); //originalmente esta apagado
        try{
            actualizarArregloLocalHuellas();
            Reclutador.clear(); //limpia la variable global para insercion
            resetHuella(); //limpia label de imagen de la huella del formulario
            start(); //vuelve a inicializar la captura de huella y lo indica
        }catch(Exception ex){
            java.util.logging.Logger.getLogger(ventanasMuestra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            //Logger.getLogger(ventanasMuestra.class.getName()).log(Level.SEVERE, null, ex );
        }
        botonPrestamo.setEnabled(false);
        botonDevolucion.setEnabled(false);
        
        
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ventanasMuestra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ventanasMuestra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ventanasMuestra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ventanasMuestra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        

       
        
    }//GEN-LAST:event_formWindowOpened

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowClosing

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowClosed

    private void botonPrestamoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonPrestamoActionPerformed
        // TODO add your handling code here:
        // lo que ocurre cuando se da clic en el boton Prestamo
        try{
            prestamo(); //ejecuta el metodo para el prestamo de equipo
            resetHuella();
            Reclutador.clear(); //limpia la variable global para insercion
        }catch(Exception ex){
            java.util.logging.Logger.getLogger(ventanasMuestra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            //Logger.getLogger(ventanasMuestra.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_botonPrestamoActionPerformed

    private void botonPrestamo2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonPrestamo2ActionPerformed
        // TODO add your handling code here:
        try{
            prestamo2("");
            resetHuella();
            Reclutador.clear();
        }catch(Exception ex){
            java.util.logging.Logger.getLogger(ventanasMuestra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_botonPrestamo2ActionPerformed

    private void botonDevolucionCtaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonDevolucionCtaActionPerformed
        // TODO add your handling code here:
        try{
            devolverCta();
            resetHuella();
            Reclutador.clear();
        }catch(Exception ex){
            java.util.logging.Logger.getLogger(ventanasMuestra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }//fin try-catch//fin try-catch
    }//GEN-LAST:event_botonDevolucionCtaActionPerformed

    private void botonDevolucionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonDevolucionActionPerformed
        // TODO add your handling code here:
        try{
            devolver();
            resetHuella();
            Reclutador.clear();
        }catch(Exception ex){
            java.util.logging.Logger.getLogger(ventanasMuestra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }//fin try-catch//fin try-catch
    }//GEN-LAST:event_botonDevolucionActionPerformed

    private void botonActualizarAgregandoHuellaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonActualizarAgregandoHuellaActionPerformed
        // TODO add your handling code here:
        // lo que sucede al dar clic en el boton Actualizar agregando huella
        try{
            actualizarAgregandoHuella(); //ejecuta el metodo para actualizar agregando huella
            Reclutador.clear(); //limpia la variable global para insercion
            resetHuella(); //limpia label de imagen de la huella del formulario
            start(); //vuelve a inicializar la captura de huella y lo indica
        }catch(Exception ex){
            //Logger.getLogger(ventanasMuestra.class.getName()).log(Level.SEVERE, null, ex );
            java.util.logging.Logger.getLogger(ventanasMuestra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, " No se pudo realizar la actualización \n NOTA: PARA ACTUALIZAR LA HUELLA DIGITAL ES IMPORTANTE PRIMERO ESCANEAR LA MISMA");
        }
        try{
            boolean actualizacion = actualizarArregloLocalHuellas();
            if (actualizacion == true){
                System.err.println("El arreglo local a sido actualizado");
            }
            Reclutador.clear(); //limpia la variable global para insercion
            resetHuella(); //limpia label de imagen de la huella del formulario
            start(); //vuelve a inicializar la captura de huella y lo indica
        }catch(Exception ex){
            java.util.logging.Logger.getLogger(ventanasMuestra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex );
        }
    }//GEN-LAST:event_botonActualizarAgregandoHuellaActionPerformed

    private void botonAltaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonAltaActionPerformed
        // TODO add your handling code here:

        // lo que sucede cuando se da clic en el boton Alta
        try{
            guardarHuella(); //ejecuta el metodo para guardar huella y persona en bd
            Reclutador.clear(); //limpia la variable global para insercion
            resetHuella(); //limpia label de imagen de la huella del formulario
            start(); //vuelve a inicializar la captura de huella y lo indica
        }catch(SQLException ex){
            //Logger.getLogger(ventanasMuestra.class.getName()).log(Level.SEVERE, null, ex );
            java.util.logging.Logger.getLogger(ventanasMuestra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_botonAltaActionPerformed

    private void botonVerificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonVerificarActionPerformed
        // TODO add your handling code here:
        // lo que sucede cuando se da clic en el boton Verificar
        String cuenta = JOptionPane.showInputDialog("Número de cuenta/RFC para verificar"); //recibe la variable
        verificarHuella(cuenta); //verifica si es la huella almacenada
        //resetHuella();
        //Reclutador.clear(); //limpia la variable global para insercion
        botonPrestamo.setEnabled(true);
        botonPrestamo.grabFocus();

    }//GEN-LAST:event_botonVerificarActionPerformed

    private void botonIdentificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonIdentificarActionPerformed
        // TODO add your handling code here:

        // lo que sucede cuando se da clic en el boton Identificar
        try{
            actualizarArregloLocalHuellas();
            identificarHuella(); //ejecuta el metodo para identificar huella
            //resetHuella();
            //Reclutador.clear(); //limpia la variable global para insercion
            botonPrestamo.setEnabled(true);
            botonPrestamo.grabFocus();
            botonDevolucion.setEnabled(true);
            botonDevolucion.grabFocus();
            //Iniciar(); //escuchar si el lector esta conectado o si se captura huella
            //start(); //avisa cuando se usa el lector de huella
            //EstadoHuellas();

            //}catch(IOException ex){
        }catch(Exception ex){
            java.util.logging.Logger.getLogger(ventanasMuestra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            //Logger.getLogger(ventanasMuestra.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_botonIdentificarActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void botonSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonSalirActionPerformed
        // TODO add your handling code here:
        conexionConsulta.desconectar();
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
            System.exit(0);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Ocurrió un error al INSERTAR EN LOGS" + ex, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_botonSalirActionPerformed

    private void administrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_administrarActionPerformed
        // TODO add your handling code here:
        //conexionConsulta.desconectar();
        conexionConsulta.desconectar();
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
            //System.exit(0);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Ocurrió un error al INSERTAR EN LOGS" + ex, "Error", JOptionPane.ERROR_MESSAGE);
        }
        stop();
        this.setVisible(false);
        formularioGrupo ch = new formularioGrupo();
        ch.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_administrarActionPerformed

    private void botonLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonLimpiarActionPerformed
        // TODO add your handling code here:
        //lo que ocurre cuando se da clic en el boton de Reset
        try{
            Reclutador.clear();
            labelImagenHuella.setIcon(null); //limpia label de imagen de la huella del formulario
            textArea.setText(null);
            setTemplate(null);
            ////
            featuresverificacion = null;
            Lector.startCapture();
            botonAlta.setEnabled(false); //inicialmente el boton esta apagado
            botonIdentificar.setEnabled(false);
            //botonVerificar.setEnabled(true); //inicialmente el boton esta encendido
            botonActualizarAgregandoHuella.setEnabled(false); //originalmente esta apagado
            botonPrestamo.setEnabled(false);
            botonDevolucion.setEnabled(false);
            //Iniciar(); //escuchar si el lector esta conectado o si se captura huella
            start(); //avisa cuando se usa el lector de huella
            EstadoHuellas();

            ////
            EnviarTexto("------ Captura de huella digital reiniciada ------");
            //EstadoHuellas();
            //start(); //vuelve a inicializar la captura de huella y lo indica
        }catch(Exception ex){
            java.util.logging.Logger.getLogger(ventanasMuestra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_botonLimpiarActionPerformed

    private void botonActualizarArregloHuellasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonActualizarArregloHuellasActionPerformed
        // TODO add your handling code here:
        try{
            boolean actualizacion = actualizarArregloLocalHuellas();
            if (actualizacion == true){
                System.err.println("El arreglo local a sido actualizado");
            }
            Reclutador.clear(); //limpia la variable global para insercion
            resetHuella(); //limpia label de imagen de la huella del formulario
            start(); //vuelve a inicializar la captura de huella y lo indica
        }catch(Exception ex){
            java.util.logging.Logger.getLogger(ventanasMuestra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex );
        }
    }//GEN-LAST:event_botonActualizarArregloHuellasActionPerformed

    
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
            java.util.logging.Logger.getLogger(ventanasMuestra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ventanasMuestra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ventanasMuestra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ventanasMuestra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        

       
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                //new ventanasMuestra().setVisible(true);
                
                ventanasMuestra ch = new ventanasMuestra();
                ch.setVisible(true);

                
                try{
                    Connection c = conexionConsulta.conectar();
                    //prepara la query y ademas establece que el ResultSet será actualizable
                    PreparedStatement identificarStmt = c.prepareStatement("SELECT NUM_CTA, NOMBRE, ESTADO, IMG_HUELLA,CONSENTIMIENTO FROM ALUMNOS WHERE BIT_LENGTH(IMG_HUELLA)>0", ResultSet.CONCUR_UPDATABLE);
                    ch.rs = identificarStmt.executeQuery();
                    //actualizarArregloLocalHuellas();
                    System.out.println("Query bien hecho");
                }catch(Exception e){ 

                    //Si ocurre un error lo indica en la consola
                    System.err.println("Error al identificar huella digital."+e.getMessage());
                    JOptionPane.showMessageDialog(null, "Error al identificar la huella\nUtilice la funcion Verificar", "Error", JOptionPane.ERROR_MESSAGE );
                    //JOptionPane.showMessageDialog(null, "Esta huella no se encontró registrada en la base de datos\nPruebe actualizar el registro de un Alumno/Académico", "Verificacion de Huella", JOptionPane.ERROR_MESSAGE);

                }//fin catch
                finally{
                    conexionConsulta.desconectar();
                }
                
                
            }
        });
    }*/

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton administrar;
    private javax.swing.JButton botonActualizarAgregandoHuella;
    private javax.swing.JButton botonActualizarArregloHuellas;
    private javax.swing.JButton botonAlta;
    private javax.swing.JButton botonDevolucion;
    private javax.swing.JButton botonDevolucionCta;
    private javax.swing.JButton botonIdentificar;
    private javax.swing.JButton botonLimpiar;
    private javax.swing.JButton botonPrestamo;
    private javax.swing.JButton botonPrestamo2;
    private javax.swing.JButton botonSalir;
    private javax.swing.JButton botonVerificar;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel labelImagenHuella;
    public javax.swing.JTextArea textArea;
    // End of variables declaration//GEN-END:variables
}