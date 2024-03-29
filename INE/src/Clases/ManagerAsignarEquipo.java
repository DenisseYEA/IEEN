/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author kevin
 */
public class ManagerAsignarEquipo {
    
    private Connection conexion;
    private Conexion db;

    public ManagerAsignarEquipo(){
        db = new Conexion();
    }
    
    public DefaultTableModel getEquipoComputo(){

        DefaultTableModel table = new DefaultTableModel();

        try {
            table.addColumn("Clave");
            table.addColumn("Producto");
            table.addColumn("No. serie");
            table.addColumn("Modelo");
                       
            CallableStatement st = db.getConexion()
                                    .prepareCall("{CALL `ine`.`sp_get_equiposComputo`(?)}");
            st.setString(0, "DISPONIBLE");
            Object datos[] = new Object[4];
            ResultSet rs = st.executeQuery();          
            //Llenar tabla
            while (rs.next()) {
                for(int i = 0;i<4;i++){
                    datos[i] = rs.getObject(i+1);
                }//Llenamos las columnas por registro
                table.addRow(datos);//Añadimos la fila
           }//while
            conexion.close();
        } catch (SQLException ex) {
            System.err.printf("Error getTabla Asignar equipo SQL");
            Logger.getLogger(ManagerUsers.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return table;
        }

    }//getEquipoComputo
    
    public DefaultTableModel getConjuntosEquipoComputo(){

        DefaultTableModel table = new DefaultTableModel();

        try {
            table.addColumn("Clave");
            table.addColumn("CPU");
            table.addColumn("Monitor");
            table.addColumn("Teclado");
            table.addColumn("Responsable");
            
            Object datos[] = new Object[5];
            ResultSet rs = db.getConexion()
                            .prepareCall("CALL `ine`.`sp_get_conjuntosEquipo`();")
                            .executeQuery();
            //Llenar tabla
            while (rs.next()) {
                for(int i = 0;i<5;i++){
                    datos[i] = rs.getObject(i+1);
                }//Llenamos las columnas por registro
                table.addRow(datos);//Añadimos la fila
           }//while
            conexion.close();
        } catch (SQLException ex) {
            System.err.printf("Error getTabla Detalles Equipo Computo SQL");
            Logger.getLogger(ManagerUsers.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            return table;
        }

    }//getDetallesEquipoComputo
    
    public DefaultTableModel getConjuntosEquipoComputoReemplazo(){

        DefaultTableModel table = new DefaultTableModel();

        try {
            table.addColumn("Clave");
            table.addColumn("CPU");
            table.addColumn("Monitor");
            table.addColumn("Teclado");
            table.addColumn("Responsable");
            
            CallableStatement st = db.getConexion().prepareCall("{CALL `ine`.`sp_get_conjuntosEquipoReemplazo`(?);}");
            st.setString(0, "REEMPLAZO AUTORIZADO");
            Object datos[] = new Object[5];
            ResultSet rs = st.executeQuery();
            
            //Llenar tabla
            while (rs.next()) {
                for(int i = 0;i<5;i++){
                    datos[i] = rs.getObject(i+1);
                }//Llenamos las columnas por registro

                table.addRow(datos);//Añadimos la fila
           }//while
            conexion.close();
        } catch (SQLException ex) {
            System.out.printf("Error getTabla Detalles Equipo Computo SQL");
            Logger.getLogger(ManagerUsers.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return table;
        }

    }//getDetallesEquipoComputoReemplazo
    
    public DefaultTableModel getDetallesEquipoComputo(String[] Claves){

        DefaultTableModel table = new DefaultTableModel();

        try {
            table.addColumn("Clave");
            table.addColumn("Equipo");
            table.addColumn("No. serie");
            table.addColumn("Modelo");
            
            conexion = db.getConexion();
            Object datos[] = new Object[4];
            CallableStatement sp = db.getConexion().prepareCall("{CALL `ine`.`sp_get_detallesEquipo`(?);}");
            
            sp.setString(0, Claves[0]);
            ResultSet rs = sp.executeQuery();
            rs.next();
            
            for(int i = 0;i<4;i++){
                datos[i] = rs.getObject(i+1);
            }//Llenamos las columnas por registro
            table.addRow(datos);//Añadimos la fila del CPU
            
            //Obtenemos la info del Monitor
            sp.clearParameters();
            sp.setString(0, Claves[1]);
            rs = sp.executeQuery();
            rs.next();
            
            for(int i = 0;i<4;i++){
                datos[i] = rs.getObject(i+1);
            }//Llenamos las columnas por registro
            table.addRow(datos);//Añadimos la fila del Monitor
            
            //Obtenemos la info del Teclado
            sp.clearParameters();
            sp.setString(0, Claves[2]);
            rs = sp.executeQuery();
            rs.next();
            
            for(int i = 0;i<4;i++){
                datos[i] = rs.getObject(i+1);
            }//Llenamos las columnas por registro
            table.addRow(datos);//Añadimos la fila del Teclado
            
            conexion.close();
        } catch (SQLException ex) {
            System.err.printf("Error getTabla Detalles Equipo Computo SQL");
            Logger.getLogger(ManagerUsers.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            return table;
        }

    }//getDetallesEquipoComputo
    
    //Este método es para actualizar el estatus del producto a asignado porque ya se selecciono como posible asignación a algun empleado,
    //sin embargo en esta parte aun no se realiza el vale de resguardo, aqui es simplemente seguridad de cuando se seleccione algun producto 
    //del inventario para que alguien mas no intente asignarselo a otro empleado
    public boolean asignarEquipo(String clave){
        try {
            /*
            // Preparamos el procedimiento almacenado
            CallableStatement st = db.getConexion().prepareCall("{CALL `ine`.`sp_update_asignarEquipo`(?);}");
            // Asignamos el parametro
            st.setString(0, clave);
            // executeUpdate retorna el numero de filas modificadas
            return st.executeUpdate() == 1;
            */
            
            //Hacemos la conexión
            conexion = db.getConexion();
            //Creamos la variable para hacer operaciones CRUD
            Statement st = conexion.createStatement();
            //Cambiamos el estatus del equipo seleccionado
            String sql = "update Inventario set estatus = 'Asignado' " +
                         "where concat(Folio,'-',Numero,Extension) = '"+clave+"';";
            st.executeUpdate(sql);
            //Cerramos la conexión
            conexion.close();
            return true;
            
        } catch (SQLException ex) {
            System.err.printf("Error al intentar actualizar el estauts del producto a \"asignado\" en SQL ");
            Logger.getLogger(ManagerSolicitud.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } 
    }//AsingarEquipo
    
    
    public boolean cambioAsignacionEquipo(String claveA,String claveN,String tipoEquipo,String claveEquipo){
        
        try {
            //Hacemos la conexión
            conexion = db.getConexion();
            //Creamos la variable para hacer operaciones CRUD
            Statement st = conexion.createStatement();
            //Creamos la variable para guardar el resultado de las consultas
            ResultSet rs;

            //Cambiamos el estatus del equipo seleccionado
            String sql = "update detalles_equipo_computo set id_"+tipoEquipo+" = '"+claveN+"' where id_"+tipoEquipo+" = '"+claveA+"' and id_equipo = '"+claveEquipo+"';";
            st.executeUpdate(sql);
            
            //Cerramos la conexión
            conexion.close();
            return true;
            
        } catch (SQLException ex) {
            System.out.printf("Error al insertar la solicitud en SQL");
            Logger.getLogger(ManagerSolicitud.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } 
        
    }//cambioAsignacionEquipo
    
    public boolean desasignarEquipo(String clave){
        
        try {
            //Hacemos la conexión
            conexion = db.getConexion();
            //Creamos la variable para hacer operaciones CRUD
            Statement st = conexion.createStatement();
            //Creamos la variable para guardar el resultado de las consultas
            ResultSet rs;

            //Cambiamos el estatus del equipo seleccionado
            String sql = "update Inventario set estatus = 'DISPONIBLE' where id_producto = '"+clave+"';";
            st.executeUpdate(sql);
            
            //Cerramos la conexión
            conexion.close();
            return true;
            
        } catch (SQLException ex) {
            System.out.printf("Error al insertar la solicitud en SQL");
            Logger.getLogger(ManagerSolicitud.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } 
        
    }//DesasingarEquipo

    public boolean regresarEquipos(String[] Claves){
        
        try{
                String sql = "";
                conexion = db.getConexion();
                Statement st = conexion.createStatement();
                
                for(int i = 0; i < Claves.length; i++){
                    //Si la cantidad es mayor a 0 entonces solo actualiza la cantidad
                    sql = "update inventario set estatus = 'DISPONIBLE' where id_producto = '"+Claves[i]+"';";
                    st.executeUpdate(sql);
                }
                conexion.close();
        } //try  
        catch (SQLException ex) {
            Logger.getLogger(ManagerAsignarEquipo.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        return true; //Da una respuesta positiva del incremento del inventario de ese producto 
        
    }//Agregar una existencia mas a un producto
    
    public boolean insertarEquipoComputo(String empleado,String[] Claves,String[] Equipos,String claveEquipo){
        
        try {
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
            sql = "select u.id_user from user u inner join empleados e on(e.id_empleado = u.id_empleado) where e.id_empleado = "+idEmpleado+";";
            rs = st.executeQuery(sql);
            rs.next();
            String usuario = rs.getString(1);
            
            //Insertamos la clave del conjunto de equipos que van a conformar dicho equipo de computo
            sql = "insert into equipo_computo values('"+claveEquipo+"','"+usuario+"')";
            st.executeUpdate(sql);
            
            //Insertamos los equipos en la tabla detalles_equipo_computo
            sql = "insert into detalles_equipo_computo (id_equipo,id_"+Equipos[0]+",id_"+Equipos[1]+",id_"+Equipos[2]+") "
                    + "values('"+claveEquipo+"','"+Claves[0]+"','"+Claves[1]+"','"+Claves[2]+"')";
            st.executeUpdate(sql);
            
            //Cerramos la conexión
            conexion.close();
            return true;
            
        } catch (SQLException ex) {
            System.out.printf("Error al insertar la solicitud en SQL");
            Logger.getLogger(ManagerSolicitud.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } 
        
    }//insertarEquipoComputo
    
    public String obtenerEquipo(String clave){
        String info = "";
        try {
            //Hacemos la conexión
            conexion = db.getConexion();
            //Creamos la variable para hacer operaciones CRUD
            Statement st = conexion.createStatement();
            //Creamos la variable para guardar el resultado de las consultas
            ResultSet rs;

            //Cambiamos el estatus del equipo seleccionado
            String sql = "select id_producto, nombre_prod, no_serie, modelo from inventario " +
                         "where id_producto = '"+clave+"';";
            rs = st.executeQuery(sql);
            rs.next();
            info = rs.getString(1)+","+rs.getString(2)+","+rs.getString(3)+","+rs.getString(4);
            //Cerramos la conexión
            conexion.close();
            return info;
            
        } catch (SQLException ex) {
            System.out.printf("Error al insertar la solicitud en SQL");
            Logger.getLogger(ManagerSolicitud.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        } 
        
    }//obtenerEquipo
    
}//class
