/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author kevin
 */
public class ManagerUsers {
    ManagerPermisos manager_permisos;
    private Connection conexion;
    private Conexion db;
    
    public ManagerUsers(){
    
        db = new Conexion();
        manager_permisos = new ManagerPermisos();
        
    }//constructor
    
    public DefaultTableModel getEmpleados() {

        DefaultTableModel table = new DefaultTableModel();

        try {
            table.addColumn("ID");
            table.addColumn("Nombre(s)");
            table.addColumn("Apellido Paterno");
            table.addColumn("Apellido Materno");
            table.addColumn("Telefono");
            
            //Consulta de los empleados
            String sql = "select id_empleado,nombres,apellido_p,apellido_m,area from Empleados where id_empleado not in (select id_empleado from User);";
            conexion = db.getConexion();
            Statement st = conexion.createStatement();
            Object datos[] = new Object[5];
            ResultSet rs = st.executeQuery(sql);

            //Llenar tabla
            while (rs.next()) {

                for(int i = 0;i<5;i++){
                    datos[i] = rs.getObject(i+1);
                }//Llenamos las columnas por registro

                table.addRow(datos);//Añadimos la fila
           }//while
            conexion.close();
        } catch (SQLException ex) {
            System.out.printf("Error getTabla Inventario SQL");
            Logger.getLogger(ManagerUsers.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            return table;
        }

    }//getEmpleados
    
    public DefaultTableModel getUsuarios(String usuario) {

        DefaultTableModel table = new DefaultTableModel();

        try {
            table.addColumn("Usuario");
            table.addColumn("Nombre(s)");
            table.addColumn("Apellido Paterno");
            table.addColumn("Apellido Materno");
            table.addColumn("Puesto");
            table.addColumn("Área");
            table.addColumn("Estatus");
            
            
            
            //Consulta de los usuarios
            String sql = "select u.id_user,e.nombres,e.apellido_p,e.apellido_m,u.puesto,e.area,u.estatus from User u " +
                         "inner join Empleados e on (u.id_empleado = e.id_empleado) where u.puesto != 'SuperUsuario' and u.id_user != '"+usuario+"';";
            conexion = db.getConexion();
            Statement st = conexion.createStatement();
            Object datos[] = new Object[7];
            ResultSet rs = st.executeQuery(sql);

            //Llenar tabla
            while (rs.next()) {

                for(int i = 0;i<7;i++){
                    datos[i] = rs.getObject(i+1);
                }//Llenamos las columnas por registro

                table.addRow(datos);//Añadimos la fila
           }//while
            conexion.close();
        } catch (SQLException ex) {
            System.out.printf("Error getTabla Inventario SQL");
            Logger.getLogger(ManagerUsers.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            return table;
        }

    }//getUsuarios
    
    public DefaultTableModel getEmpleadosCoincidencia(String usuario,int filtro,String busqueda) {

        DefaultTableModel table = new DefaultTableModel();
        String tipoBusqueda = "";
        try{
            
            /*
            filtro = 0; Nombres
            filtro = 1; Apellido P
            filtro = 2; Apellido M
            */
            
            switch(filtro){

                case 0:
                    tipoBusqueda = "nombres";
                    break;

                case 1:
                    tipoBusqueda = "apellido_p";
                    break;

                case 2:
                    tipoBusqueda = "apellido_m";
                    break;    

            }//Buscamos el nombre de la columna con lo que vamos a buscar la coincidencia

            table.addColumn("ID");
            table.addColumn("Nombre(s)");
            table.addColumn("Apellido Paterno");
            table.addColumn("Apellido Materno");
            table.addColumn("Area");
            
            //Consulta de los empleados
            String sql = "select id_empleado,nombres,apellido_p,apellido_m,area from Empleados " +
                         "where "+tipoBusqueda+" like '"+busqueda+"%' and id_empleado not in (select id_empleado from user);";
            conexion = db.getConexion();
            Statement st = conexion.createStatement();
            Object datos[] = new Object[5];
            ResultSet rs = st.executeQuery(sql);

            //Llenar tabla
            while (rs.next()) {

                for(int i = 0;i<5;i++){
                    datos[i] = rs.getObject(i+1);
                }//Llenamos las columnas por registro

                table.addRow(datos);//Añadimos la fila
           }//while
            conexion.close();
        } catch (SQLException ex) {
            System.out.printf("Error getTabla Inventario SQL");
            Logger.getLogger(ManagerUsers.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            return table;
        }

    }//getEmpleadosCoincidencia
    
    public DefaultTableModel getUsuariosCoincidencia(String usuario,int filtro,String busqueda) {

        DefaultTableModel table = new DefaultTableModel();
        String tipoBusqueda = "";
        try{
            
            /*
            filtro = 0; Usuario
            filtro = 1; Nombres
            filtro = 2; Apellido P
            filtro = 3; Apellido M
            filtro = 4; Cargo
            filtro = 5; Área
            */
            
            switch(filtro){

                case 0:
                    tipoBusqueda = "u.id_user";
                    break;

                case 1:
                    tipoBusqueda = "e.nombres";
                    break;

                case 2:
                    tipoBusqueda = "e.apellido_p";
                    break;

                case 3:
                    tipoBusqueda = "e.apellido_m";
                    break;

                case 4:
                    tipoBusqueda = "u.puesto";
                    break;

                case 5:
                    tipoBusqueda = "e.area";
                    break;    

            }//Buscamos el nombre de la columna con lo que vamos a buscar la coincidencia

            table.addColumn("Usuario");
            table.addColumn("Nombre(s)");
            table.addColumn("Apellido Paterno");
            table.addColumn("Apellido Materno");
            table.addColumn("Cargo");
            table.addColumn("Área");
            table.addColumn("Estatus");
            
            //Consulta de los empleados
            String sql = "select u.id_user,e.nombres,e.apellido_p,e.apellido_m,u.puesto,e.area.u.estatus from User u " +
                         "inner join Empleados e on (u.id_empleado = e.id_empleado) where u.puesto != 'SuperUsuario' "
                    +    "and u.id_user != '"+usuario+"' and "+tipoBusqueda+" like '"+busqueda+"%';";
            conexion = db.getConexion();
            Statement st = conexion.createStatement();
            Object datos[] = new Object[7];
            ResultSet rs = st.executeQuery(sql);

            //Llenar tabla
            while (rs.next()) {

                for(int i = 0;i<7;i++){
                    datos[i] = rs.getObject(i+1);
                }//Llenamos las columnas por registro

                table.addRow(datos);//Añadimos la fila
           }//while
            conexion.close();
        } catch (SQLException ex) {
            System.out.printf("Error getTabla Inventario SQL");
            Logger.getLogger(ManagerUsers.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            return table;
        }

    }//getUsuariosCoincidencia

    public boolean existeEmpleado(int filtro, String busqueda,String usuario){
        boolean estado = false;
        String tipoBusqueda = "";
        try{
            
            /*
            filtro = 0; Nombres
            filtro = 1; Apellido P
            filtro = 2; Apellido M
            */
            
            switch(filtro){

                case 0:
                    tipoBusqueda = "nombres";
                    break;

                case 1:
                    tipoBusqueda = "apellido_p";
                    break;

                case 2:
                    tipoBusqueda = "apellido_m";
                    break;    

            }//Buscamos el nombre de la columna con lo que vamos a buscar la coincidencia
            
            String sql = "select id_empleado,nombres,apellido_p,apellido_m,area from Empleados " +
                         "where "+tipoBusqueda+" like '"+busqueda+"%' and id_empleado not in (select id_empleado from user);";
            conexion = db.getConexion();
            Statement st = conexion.createStatement();
            ResultSet rs = st.executeQuery(sql);
            estado = rs.next();
                
        } //try  
        catch (SQLException ex) {
            Logger.getLogger(ManagerInventario.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        return estado; //Retorna el resultado, si se encontro o no
        
    }//Buscar si existe el empleado
    
    public boolean insertarEmpleado(String nombres, String apellidoP, String apellidoM, String telefono,String calle, String colonia, 
                                    String curp,String rfc,String fecha,String codigoP,String municipio,String localidad,String area) {
        try {
            //Hacemos la conexión
            conexion = db.getConexion();
            //Creamos la variable para hacer operaciones CRUD
            Statement st = conexion.createStatement();
            //Creamos la variable para guardar el resultado de las consultas
            ResultSet rs;
            
            //Primero insertamos al empleado
            String sql = "insert into Empleados (nombres,apellido_p,apellido_m,calle,colonia,telefono,codigo_postal,fecha_nacimiento,curp,rfc,municipio,localidad,area) "
                         +"values('"+nombres+"','"+apellidoP+"','"+apellidoM+"','"+calle+"','"+colonia+"','"
                         +telefono+"','"+codigoP+"','"+fecha+"','"+curp+"','"+rfc+"','"+municipio+"','"+localidad+"','"+area+"');";
            st.executeUpdate(sql);
            
            return true;
        } catch (SQLException ex) {
            System.out.printf("Error al insertar el empleado en SQL");
            Logger.getLogger(ManagerUsers.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } 
        
    }//insertarEmpleado
    
    
    public boolean asignarUsuario(int id_empleado,String usuario, String pass,String puesto) {
        
        try {
            //Hacemos la conexión,
            conexion = db.getConexion();
            //Creamos la variable para hacer operaciones CRUD
            Statement st = conexion.createStatement();
            //Creamos la variable para guardar el resultado de las consultas
            ResultSet rs;
            
            //Ya se realizo la inserción y se encontro el ID de ese nuevo registro, ahora insertamos el usuario y ligamos el ID, su cargo y su área
            String sql = "insert into User values('"+usuario+"',"+id_empleado+",true,'"+pass+"','"+puesto+"','Activo');";
            st.executeUpdate(sql);
            
            //Registramos el nuevo usuario en la tabla de permisos(por el momento no tendra ningún permiso, ya que solo es el registro)
            //Primero obtenemos la cantidad de modulos que hay
            sql = "select count(*) from Modulos";
            rs = st.executeQuery(sql);
            rs.next();
            int tamaño = rs.getInt(1);
            
            //Creamos el arreglo donde guardaremos el nombre de cada modulo
            String[] modulos = new String[tamaño];
            //Hacemos la consulta para obtener todos los nombres de los modulos
            sql = "select * from Modulos";
            rs = st.executeQuery(sql);
            rs.next();
            //Llenamos el arreglo con los nombres de los modulos
            for(int i = 0;i<tamaño;i++){
                modulos[i] = rs.getString(1);
                rs.next();
            }//for
            
            //Insertamos todos los modulos sin permisos al usuario
            for(int i = 0;i<tamaño;i++){
                sql = "insert into Permisos values('"+usuario+"','"+modulos[i]+"',false,false,false,false);";
                st.executeUpdate(sql);
            }//for
            
            //Ahora le damos los permisos de acuerdo al cargo que tiene
            manager_permisos.asignarPermisos_Puesto(puesto, usuario);
            //Cerramos la conexión
            conexion.close();
            return true;
        } catch (SQLException ex) {
            System.out.printf("Error al insertar el empleado en SQL");
            Logger.getLogger(ManagerUsers.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } 
        
    }//asignarUsuario
    
    public boolean actualizarEmpleado(String usuario, String nombres, String apellidoP, String apellidoM,String calle,String colonia, String telefono,String codigoP,String fecha,String curp,String rfc,String municipio,String localidad,String puesto) {

        try {
            //Primero obtenemos el id del empleado
            String sql = "select id_empleado from User where id_user = '"+usuario+"';";
            conexion = db.getConexion();
            Statement st = conexion.createStatement();
            ResultSet rs = st.executeQuery(sql);
            rs.next();
            int empleado = rs.getInt(1);
            
            //Ahora actualizamos el perfil del empleado
            sql = "update Empleados set nombres = '"+nombres+"',apellido_p = '"+apellidoP+"',apellido_m = '"+apellidoM
                  +"',calle = '"+calle+"',colonia = '"+colonia+"',telefono = '"+telefono+"',codigo_postal = '"+codigoP
                  +"',fecha_nacimiento = '"+fecha+"',curp = '"+curp+"',rfc = '"+rfc+"',municipio = '"+municipio+"',localidad = '"+localidad+"' "
                  + "where id_empleado = "+empleado+";";
            st.executeUpdate(sql);
            
            //manager_permisos.asignarPermisos_Puesto(puesto, usuario,area);
            
            conexion.close();
            return true;
        } catch (SQLException ex) {
            System.out.printf("Error al insertar el empleado en SQL");
            Logger.getLogger(ManagerUsers.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } 
        
    }//actualizarEmpleado
    
    public boolean passwordEquals(String usuario, String pass) {
        boolean coincidencia = false;
        try {
            //Actualizamos la contraseña
            String sql = "select password from User where id_user = '"+usuario+"' and password = '"+pass+"';";
            conexion = db.getConexion();
            Statement st = conexion.createStatement();
            ResultSet rs = st.executeQuery(sql);
            coincidencia = rs.next();
            
            conexion.close();
            return coincidencia;
        } catch (SQLException ex) {
            System.out.printf("Error al insertar el empleado en SQL");
            Logger.getLogger(ManagerUsers.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } 
        
    }//changePass
    
    public boolean changePass(String usuario, String antigüa, String nueva) {

        try {
            //Actualizamos la contraseña
            String sql = "update User set password = '"+nueva+"' where id_user = '"+usuario+"' and password = '"+antigüa+"';";
            conexion = db.getConexion();
            Statement st = conexion.createStatement();
            st.executeUpdate(sql);
            
            conexion.close();
            return true;
        } catch (SQLException ex) {
            System.out.printf("Error al insertar el empleado en SQL");
            Logger.getLogger(ManagerUsers.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } 
        
    }//changePass
    
    public boolean estatusUsuario(String usuario,String estatus) {
        try {
            //Actualizamos el estatus del usuario
            String sql = "update User set estatus = '"+estatus+"' where id_user = '"+usuario+"';";
            conexion = db.getConexion();
            Statement st = conexion.createStatement();
            st.executeUpdate(sql);
            
            //Cerramos la conexión
            conexion.close();
            return true;
        } catch (SQLException ex) {
            System.out.printf("Error al intentar dar el estatus de "+estatus+" al usuario en SQL");
            Logger.getLogger(ManagerUsers.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }//Eliminar empleado
    
    public boolean existeUsuario(String usuario) {

        boolean estado = false;
        
        try {
            //Consulta para saber si existe o no dicho usuario
            String sql = "select * from User where id_user = '"+usuario+"';";
            conexion = db.getConexion();
            Statement st = conexion.createStatement();
            ResultSet rs = st.executeQuery(sql);
            estado = rs.next();//Guardamos el resultado para retornar la respuesta.
            conexion.close();
            
        } catch (SQLException ex) {
            System.out.printf("Error al consultar el usuario en SQL");
            Logger.getLogger(ManagerUsers.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } 
            return estado;

    }//existeUsuario
    
    public String infoEmpleado(String usuario){
        String empleado;
        try {
            //Consulta para saber si existe o no dicho usuario
            String sql = "select e.nombres,e.apellido_p,e.apellido_m,e.calle,e.colonia,e.telefono,e.codigo_postal,e.fecha_nacimiento,e.curp,e.rfc,e.municipio,e.localidad,u.puesto,e.area from Empleados e "
                    + "inner join User u on (u.id_empleado = e.id_empleado)where u.id_user = '"+usuario+"';";
            conexion = db.getConexion();
            Statement st = conexion.createStatement();
            ResultSet rs = st.executeQuery(sql);
            rs.next();
            empleado = rs.getString(1);
            for(int i = 2;i<15;i++){
                empleado += ","+rs.getString(i);
            }
            conexion.close();
            
        } catch (SQLException ex) {
            System.out.printf("Error al obtener la información del empleado en SQL");
            Logger.getLogger(ManagerUsers.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        } 
            return empleado;
        
    }//infoEmpleado
    
    public void getNombresEmpleados(JComboBox combo) {
        try{
           
            String sql = "select concat(nombres,' ',apellido_p,' ',apellido_m) from Empleados;";
            conexion = db.getConexion();
            Statement st = conexion.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while(rs.next()){
                combo.addItem(rs.getObject(1).toString());
            }
            
            conexion.close();
        } catch (SQLException ex) {
            System.out.printf("Error al obtener los nombres de los empleados para ingresarlos al combo SQL");
            Logger.getLogger(ManagerUsers.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
    }//Obtiene todas los nombres de los empleados
    
    public String obtenerPuesto(String empleado) {
        try{
           
            //Hacemos la conexión
            conexion = db.getConexion();
            //Creamos la variable para hacer operaciones CRUD
            Statement st = conexion.createStatement();
            //Creamos la variable para guardar el resultado de las consultas
            ResultSet rs;

            //Obtenemos el id del empleado para dar con su usuario
            String sql = "select id_empleado from Empleados where concat(nombres,' ',apellido_p,' ',apellido_m) = '"+empleado+"';";
            rs = st.executeQuery(sql);
            rs.next();
            int idEmpleado = rs.getInt(1);
            
            //Ahora obtenemos el usuario gracias al id del empleado
            sql = "select u.id_user from User u inner join Empleados e on(e.id_empleado = u.id_empleado) where e.id_empleado = "+idEmpleado+";";
            rs = st.executeQuery(sql);
            rs.next();
            String usuario = rs.getString(1);
            
            //Ahora obtenemos el usuario gracias al id del empleado
            sql = "select puesto from User where id_user = '"+usuario+"';";
            rs = st.executeQuery(sql);
            rs.next();
            String puesto = rs.getString(1);
            
            //Cerramos la conexión
            conexion.close();
            return puesto;
            
        } catch (SQLException ex) {
            System.out.printf("Error al obtener los nombres de los empleados para ingresarlos al combo SQL");
            Logger.getLogger(ManagerUsers.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        } 
        
    }//obtenerPuesto
    
}//class
