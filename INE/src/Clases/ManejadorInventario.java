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
public class ManejadorInventario {
    private Connection conexion;
    private Conexion db;
    
    public ManejadorInventario(){
        db = new Conexion();
    }//constructor
    
    public DefaultTableModel getInventarioG() {
        
        DefaultTableModel table = new DefaultTableModel();

        try {
            table.addColumn("Clave");
            table.addColumn("Producto");
            table.addColumn("Descripción");
            table.addColumn("Almacén");
            table.addColumn("Marca");
            table.addColumn("Observaciones");
            table.addColumn("Stock");
            
            
            //Consulta de los empleados
            String sql = "select id_productoGranel,nombre_prod,descripcion,almacen,marca,observaciones,stock from Inventario_granel where estatus = 'DISPONIBLE';";
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

    }//getInventarioG

    //Este método retorna la tabla de inventario normal solo con los productos disponibles, esto para mostrarse en el Manejador Inventario
    //cuando se quiere realizar una asignación
    public DefaultTableModel getInventarioParaAsignacion(String nomeclatura) {
        String orden = "";
        DefaultTableModel table = new DefaultTableModel();

        try {
            
            table.addColumn("Clave");
            table.addColumn("Nombre_corto");
            table.addColumn("Descripción");
            table.addColumn("Ubicación");
            table.addColumn("Marca");
            table.addColumn("No. Serie");
            table.addColumn("Modelo");
            
            String sql = "";
            if(nomeclatura.equals("")){
                //Consulta de los empleados
                sql = "select concat(Folio,'-',Numero,Extension),nombre_prod,descripcion,ubicacion,marca,no_serie,modelo "
                        + "from Inventario where estatus = 'Disponible';";
            }else{
                sql = "select concat(Folio,'-',Numero,Extension),nombre_prod,descripcion,ubicacion,marca,no_serie,modelo "
                    + "from Inventario where Folio = '"+nomeclatura+"' and estatus = 'Disponible';";
            }
            
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

    }//getInventario
    
    public int cantidadInventarioG(String id_producto) {

        int cantidad;
        
        try {
            //Consulta para saber si existe o no dicho producto
            String sql = "select stock from Inventario_granel where id_productoGranel = '"+id_producto+"';";
            conexion = db.getConexion();
            Statement st = conexion.createStatement();
            ResultSet rs = st.executeQuery(sql);
            rs.next();//Guardamos el resultado para retornar la respuesta.
            cantidad = rs.getInt(1);
            conexion.close();
            return cantidad;
        } catch (SQLException ex) {
            System.out.printf("Error al consultar el inventario en SQL");
            Logger.getLogger(ManagerUsers.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        } 

    }//cantidadInventarioG
    
    public int productosSuficientesInventarioG(String id_producto,int cantidad) {
        int stock = 0;
        try {
            //Hacemos el update de la resta del inventario
            String sql = "update Inventario_granel set stock = stock - "+cantidad+" where id_productoGranel = '"+id_producto+"' and stock > "+cantidad+";";
            conexion = db.getConexion();
            Statement st = conexion.createStatement();
            st.executeUpdate(sql);
            //Obtenemos el stock del producto para saber si se realizo o no el update
            sql = "select stock from Inventario_granel where id_productoGranel = '"+id_producto+"';";
            ResultSet rs = st.executeQuery(sql);
            rs.next();
            stock = rs.getInt(1);
            conexion.close();
            return stock;
        } catch (SQLException ex) {
            System.out.printf("Error al consultar el inventario en SQL");
            Logger.getLogger(ManagerUsers.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }//productosSuficientesInventarioG
    
    public int productosIgualesInventarioG(String id_producto,int cantidad) {
        int stock = 0;
        try {
            //Hacemos el update de la resta del inventario
            String sql = "update Inventario_granel set stock = 0,estatus = 'AGOTADO' where id_productoGranel = '"+id_producto+"' and stock = "+cantidad+";";
            conexion = db.getConexion();
            Statement st = conexion.createStatement();
            st.executeUpdate(sql);
            //Obtenemos el stock del producto para saber si se realizo o no el update
            sql = "select stock from Inventario_granel where id_productoGranel = '"+id_producto+"';";
            ResultSet rs = st.executeQuery(sql);
            rs.next();
            stock = rs.getInt(1);
            conexion.close();
            return stock;
        } catch (SQLException ex) {
            System.out.printf("Error al consultar el inventario en SQL");
            Logger.getLogger(ManagerUsers.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }//productosIgualesInventarioG
    
    //Este método es para cancelar la acción de asignación de uno o mas productos en la pestaña de manejador de inventario, se usa cuando
    //cancelas un producto, cuando presionas el boton cancelar o cuando se cierra la ventana y quedaron los productos sin generar el vale
    public boolean regresarInventario(String[] Claves,int[] Cantidad){
        
        try{
                String sql = "";
                conexion = db.getConexion();
                Statement st = conexion.createStatement();
                ResultSet rs;
                for(int i = 0; i < Claves.length; i++){
                    //Buscamos si es de inventario o de granel
                    sql = "select * from Inventario where concat(Folio,'-',Numero,Extension) = '"+Claves[i]+"';";
                    rs = st.executeQuery(sql);
                    System.out.println("Hicimos la consulta para ver si es inventario o granel");
                    //Si entra es a inventario
                    if(rs.next()){
                        sql = "update Inventario set estatus = 'Disponible' where concat(Folio,'-',Numero,Extension) = '"+Claves[i]+"';";
                        st.executeUpdate(sql);
                        System.out.println("Es inventario normal y cambio el estatus a disponible");
                    }
                    //Si no entra es a granel
                    else{
                        sql = "update Inventario_granel set stock = stock + "+Cantidad[i]+" where id_productoGranel = '"+Claves[i]+"' and stock > 0;";
                        st.executeUpdate(sql);
                        System.out.println("Llego a querer hacer el update para sumarle la cantidad que se le quito");
                        
                        sql = "update Inventario_granel set estatus = 'Disponible', stock = "+Cantidad[i]+" where id_productoGranel = '"+Claves[i]+"' and stock = 0;";
                        st.executeUpdate(sql);
                        System.out.println("Llego a querer hacer el update para ponerlo disponible si el stock es 0");
                    }
                }//for
                conexion.close();
        } //try  
        catch (SQLException ex) {
            Logger.getLogger(ManagerAsignarEquipo.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        return true; //Da una respuesta positiva del incremento del inventario de ese producto 
        
    }//Regresa los productos a su estado orignal (estatus y/o cantidad)
    
    //Este método realiza el vale de resguardo, en donde todos los productos seleccionados se le asignan a un responsable
    public boolean asignarInventario(String[] Claves,int[] Cantidad,String empleado){
        
        try{
                String sql = "";
                conexion = db.getConexion();
                Statement st = conexion.createStatement();
                ResultSet rs;
                
                //Obtenemos la fecha y hora exacta del sistema
                sql = "select now();";
                rs = st.executeQuery(sql);
                rs.next();
                String fecha = rs.getString(1);
                
                //Obtenemos el id del empleado para encontrar el usuario
                sql = "select id_empleado from Empleados where concat(nombres,' ',apellido_p,' ',apellido_m) = '"+empleado+"';";
                rs = st.executeQuery(sql);
                rs.next();
                int id_empleado = rs.getInt(1);
                
                //Insertamos el registro del vale de asignación
                sql = "insert into Vales (tipo_vale,fecha_vale,id_empleado) values('Vale de resguardo','"+fecha+"','"+id_empleado+"');";
                st.executeUpdate(sql);
                
                //Obtenemos el id del vale que se acaba de crear
                sql = "select id_vale from Vales where fecha_vale = '"+fecha+"';";
                rs = st.executeQuery(sql);
                rs.next();
                int idVale = rs.getInt(1);
                
                for(int i = 0; i < Claves.length; i++){
                    //Insertamos los datos en la tabla "detalle_vale"
                    sql = "insert into Detalle_vale (id_vale,id_producto,cantidad,estado)values("+idVale+",'"+Claves[i]+"',"+Cantidad[i]+",'Asignado');";
                    st.executeUpdate(sql);
                    
                }//for
                conexion.close();
                return true;
        } //try  
        catch (SQLException ex) {
            Logger.getLogger(ManagerAsignarEquipo.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }//Regresa los productos a su estado orignal (estatus y/o cantidad)
    
    //Este método es para llenar el combo solamente con los empleados que tengan asignaciones (vales de resguardo) y que todavia no hayan sido
    //recogidos (vales de recolección)
    public void getEmpleadosAsignacion(JComboBox combo) {
        try{
           
            String sql = "select concat(e.nombres,' ',e.apellido_p,' ',e.apellido_m) as Empleado from empleados e " +
                         "where e.id_empleado in ( " +
                         "select v.id_empleado from Vales v " +
                         "inner join Empleados e on (e.id_empleado = v.id_empleado) " +
                         "inner join Detalle_vale dv on (dv.id_vale = v.id_vale) " +
                         "inner join Inventario_granel ig on (dv.id_producto = ig.id_productoGranel) " +
                         ") or e.id_empleado in ( " +
                         "select e.id_empleado as Empleado from Empleados e " +
                         "where e.id_empleado in ( " +
                         "select v.id_empleado from Vales v " +
                         "inner join Empleados e on (e.id_empleado = v.id_empleado) " +
                         "inner join Detalle_vale dv on (dv.id_vale = v.id_vale) " +
                         "inner join Inventario i on (dv.id_producto = concat(i.Folio,'-',i.Numero,i.Extension))) " +
                         ") group by concat(e.nombres,' ',e.apellido_p,' ',e.apellido_m);";
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
        
    }//Obtiene todas los nombres de los empleados que tienen productos asignados
    
    public DefaultTableModel getInventarioEmpleadoAsignaciones(String empleado) {
            DefaultTableModel table = new DefaultTableModel();

        try {
            table.addColumn("Vale");
            table.addColumn("Clave");
            table.addColumn("Producto");
            table.addColumn("Descripción");
            table.addColumn("Observaciones");
            table.addColumn("Cantidad");
            
            //Obtiene los productos asignados de acuerdo al empleado (Inventario)
            String sql = "select v.id_vale, dv.id_producto, ig.nombre_prod,ig.descripcion,ig.observaciones,dv.cantidad from Vales v inner join Detalle_vale dv on (dv.id_vale = v.id_vale) inner join inventario ig on (dv.id_producto = ig.id_producto) inner join user u on (u.id_user = v.id_user) inner join empleados e on (e.id_empleado = u.id_empleado) where concat(e.nombres,' ',e.apellido_p,' ',e.apellido_m)= '"+empleado+"';";
            conexion = db.getConexion();
            Statement st = conexion.createStatement();
            Object datos[] = new Object[6];
            ResultSet rs = st.executeQuery(sql);

            //Llenar tabla
            while (rs.next()) {

                for(int i = 0;i<6;i++){
                    datos[i] = rs.getObject(i+1);
                }//Llenamos las columnas por registro

                table.addRow(datos);//Añadimos la fila
            }//while
            
            //Obtiene los productos asignados de acuerdo al empleado (Inventario a granel)
            sql = "select v.id_vale, dv.id_producto, ig.nombre_prod,ig.descripcion,ig.observaciones,dv.cantidad from Vales v inner join Detalle_vale dv on (dv.id_vale = v.id_vale) inner join inventario_granel ig on (dv.id_producto = ig.id_productoGranel) inner join user u on (u.id_user = v.id_user) inner join empleados e on (e.id_empleado = u.id_empleado) where concat(e.nombres,' ',e.apellido_p,' ',e.apellido_m)= '"+empleado+"';";
            conexion = db.getConexion();
            rs = st.executeQuery(sql);

            //Llenar tabla
            while (rs.next()) {

                for(int i = 0;i<6;i++){
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

    }//getInventarioEmpleadoAsignaciones
    
    public boolean recoleccionInventario(int idVale,String idProducto,int cantidad){
        
        try{
                String sql = "";
                conexion = db.getConexion();
                Statement st = conexion.createStatement();
                ResultSet rs;
                
                //Regresamos el producto (Ya sea a inventario o inventario a granel)
                sql = "select * from Inventario where id_producto = '"+idProducto+"';";
                rs = st.executeQuery(sql);
                //Si entra es a inventario
                if(rs.next()){
                    sql = "update Inventario set estatus = 'DISPONIBLE' where id_producto = '"+idProducto+"';";
                    st.executeUpdate(sql);
                }
                //Si no entra es a granel
                else{
                    sql = "update Inventario_granel set stock = stock + "+cantidad+" where id_productoGranel = '"+idProducto+"' and stock > 0;";
                    st.executeUpdate(sql);

                    sql = "update Inventario_granel set estatus = 'DISPONIBLE', stock = "+cantidad+" where id_productoGranel = '"+idProducto+"' and stock = 0;";
                    st.executeUpdate(sql);
                }
                
                sql = "select * from productosEntregados where id_vale = "+idVale+" and id_producto = '"+idProducto+"';";
                rs = st.executeQuery(sql);
                //Si el registro existe entonces solo sumamos la cantidad entregada
                if(rs.next()){
                    sql = "update productosEntregados set cantidad = cantidad + "+cantidad+" where id_vale = "+idVale+" and id_producto = '"+idProducto+"';";
                    st.executeUpdate(sql);
                }
                //Si el registro no existe entonces hacemos el nuevo registro en la tabla "productosEntregados"
                else{
                    sql = "insert into productosEntregados values("+idVale+",'"+idProducto+"',"+cantidad+");";
                    st.executeUpdate(sql);
                }
                
                //Actualizamos o eliminamos el registro segun corresponda en la tabla "detalle_vale" 
                sql = "update Detalle_vale set cantidad = cantidad - "+cantidad+" where id_vale = "+idVale+" and id_producto = '"+idProducto+"';";
                st.executeUpdate(sql);
                //Si es 0 entonces ya no quedan mas producto por entregar, se elimina de la tabla
                sql = "delete from Detalle_vale where id_vale = "+idVale+" and id_producto = '"+idProducto+"' and cantidad = 0;";
                st.executeUpdate(sql);                
                
                conexion.close();
        } //try  
        catch (SQLException ex) {
            Logger.getLogger(ManagerAsignarEquipo.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        return true;
        
    }//Regresa los productos a su estado orignal (estatus y/o cantidad)
    
    public boolean regresarRecoleccion(int[] IDVales,String[] Claves,int[] Cantidad){
        
        try{
                String sql = "";
                conexion = db.getConexion();
                Statement st = conexion.createStatement();
                ResultSet rs;
                
                for(int i = 0; i<Claves.length; i++){
                    
                    //Regresamos el producto (Ya sea a inventario o inventario a granel)
                    sql = "select * from Inventario where id_producto = '"+Claves[i]+"';";
                    rs = st.executeQuery(sql);
                    //Si entra es a inventario
                    if(rs.next()){
                        sql = "update Inventario set estatus = 'ASIGNADO' where id_producto = '"+Claves[i]+"';";
                        st.executeUpdate(sql);
                    }
                    //Si no entra es a granel
                    else{
                        sql = "update Inventario_granel set stock = stock - "+Cantidad[i]+" where id_productoGranel = '"+Claves[i]+"' and stock >= "+Cantidad[i]+";";
                        st.executeUpdate(sql);

                        sql = "update Inventario_granel set estatus = 'AGOTADO' where id_productoGranel = '"+Claves[i]+"' and stock = 0;";
                        st.executeUpdate(sql);
                    }
                    
                    //Devolvemos la cantidad de la tabla "productosEntregados"
                    sql = "update productosEntregados set cantidad = cantidad - "+Cantidad[i]+" where id_vale = "+IDVales[i]+" and id_producto = '"+Claves[i]+"';";
                    st.executeUpdate(sql);
                    
                    //Eliminamos el registro si queda en 0
                    sql = "delete from productosEntregados where id_vale = "+IDVales[i]+" and id_producto = '"+Claves[i]+"' and cantidad = 0;";
                    st.executeUpdate(sql);
                    
                    sql = "select * from Detalle_vale where id_vale = "+IDVales[i]+" and id_producto = '"+Claves[i]+"';";
                    rs = st.executeQuery(sql);
                    //Si el registro existe entonces solo sumamos la cantidad entregada
                    if(rs.next()){
                        sql = "update Detalle_vale set cantidad = cantidad + "+Cantidad[i]+" where id_vale = "+IDVales[i]+" and id_producto = '"+Claves[i]+"';";
                        st.executeUpdate(sql);
                    }
                    //Si el registro no existe entonces hacemos el nuevo registro en la tabla "detalle_vale"
                    else{
                        sql = "insert into Detalle_vale values("+IDVales[i]+",'"+Claves[i]+"',"+Cantidad[i]+",'ASIGNADO');";
                        st.executeUpdate(sql);
                    }

                }//for
                
                conexion.close();
        } //try  
        catch (SQLException ex) {
            Logger.getLogger(ManagerAsignarEquipo.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        return true;
        
    }//Regresa los productos de recoleccion a su estado orignal
    
    public DefaultTableModel getInventarioEmpleadoAsignacionesPersonales(String usuario) {
            DefaultTableModel table = new DefaultTableModel();

        try {
            table.addColumn("Vale");
            table.addColumn("Clave");
            table.addColumn("Producto");
            table.addColumn("Descripción");
            table.addColumn("Observaciones");
            table.addColumn("Estado");
            
            //Obtiene los productos asignados de acuerdo al empleado
            String sql = "select v.id_vale,dv.id_producto, ig.nombre_prod,ig.descripcion,ig.observaciones,dv.estado from Vales v " +
                         "inner join Detalle_vale dv on (dv.id_vale = v.id_vale) " +
                         "inner join Inventario ig on (dv.id_producto = ig.id_producto) " +
                         "inner join User u on (u.id_user = v.id_user) " +
                         "where u.id_user = '"+usuario+"';";
            conexion = db.getConexion();
            Statement st = conexion.createStatement();
            Object datos[] = new Object[6];
            ResultSet rs = st.executeQuery(sql);

            //Llenar tabla
            while (rs.next()) {

                for(int i = 0;i<6;i++){
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

    }//getInventarioEmpleadoAsignacionesPersonales
    
    public DefaultTableModel getInventarioEmpleadoAsignacionesPersonalesG(String usuario) {
            DefaultTableModel table = new DefaultTableModel();

        try {
            table.addColumn("Vale");
            table.addColumn("Clave");
            table.addColumn("Producto");
            table.addColumn("Descripción");
            table.addColumn("Observaciones");
            table.addColumn("Cantidad");
            
            //Obtiene los productos asignados de acuerdo al empleado
            String sql = "select v.id_vale,dv.id_producto, ig.nombre_prod,ig.descripcion,ig.observaciones,dv.cantidad from Vales v " +
                         "inner join Detalle_vale dv on (dv.id_vale = v.id_vale) " +
                         "inner join Inventario_granel ig on (dv.id_producto = ig.id_productoGranel) " +
                         "inner join User u on (u.id_user = v.id_user) " +
                         "where u.id_user = '"+usuario+"';";
            conexion = db.getConexion();
            Statement st = conexion.createStatement();
            Object datos[] = new Object[6];
            ResultSet rs = st.executeQuery(sql);

            //Llenar tabla
            while (rs.next()) {

                for(int i = 0;i<6;i++){
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

    }//getInventarioEmpleadoAsignacionesPersonales
    
    public boolean registro_Solicitud(int idVale,String idProd, String tipo,String user,String motivo,int cantidad){
        
        try {
            //Hacemos la conexión
            conexion = db.getConexion();
            //Creamos la variable para hacer operaciones CRUD
            Statement st = conexion.createStatement();
            //Creamos la variable para guardar el resultado de las consultas
            ResultSet rs;
            
            //Obtenemos la fecha del sistema
            String sql = "select now();";
            rs = st.executeQuery(sql);
            rs.next();
            String fecha = rs.getString(1); 
            
            //Registramos la solicitud
            sql = "insert into Solicitudes (tipo_solicitud,id_user,motivo,cantidad,fecha_solicitud,estado) "
                        +"values('"+tipo+"','"+user+"','"+motivo+"',"+cantidad+",'"+fecha+"','SOLICITUD PERSONAL');";
            st.executeUpdate(sql);
            
            //Cambiamos el estatus del equipo seleccionado
            sql = "update Detalle_vale set estado = 'SOLICITUD' where id_producto = '"+idProd+"' and id_vale = "+idVale+";";
            st.executeUpdate(sql);
            
            //Buscamos el id de la solicitud
            sql = "select id_solicitud from Solicitudes where fecha_solicitud = '"+fecha+"';";
            rs = st.executeQuery(sql);
            rs.next();
            String idSol = rs.getString(1); 
            
            //Realizamos el registro de los detalles de la solicitud
            sql = "insert into Detalle_solicitud values("+idSol+",'"+idProd+"');";
            st.executeUpdate(sql);
            
            //Cerramos la conexión
            conexion.close();
            return true;
            
        } catch (SQLException ex) {
            System.out.printf("Error al insertar la solicitud en SQL");
            Logger.getLogger(ManagerSolicitud.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } 
        
    }//registro_solicitud
    
    public boolean actualizar_Solicitud(int idSol,String empleado){
        
        try {
            //Hacemos la conexión
            conexion = db.getConexion();
            //Creamos la variable para hacer operaciones CRUD
            Statement st = conexion.createStatement();
            
            //Actualizamos la solicitud
            String sql = "update Solicitudes set estado = 'PENDIENTE PERSONAL' where id_solicitud = "+idSol+"";
            st.executeUpdate(sql);
            
            //Obtenemos el id del producto
            sql = "select id_producto from Detalle_solicitud where id_solicitud = "+idSol+";";
            ResultSet rs = st.executeQuery(sql);
            rs.next();
            String idProd = rs.getString(1);
            System.out.println("encontre el producto: "+idProd);
            
            //Obtenemos el id del vale
            sql = "select v.id_vale from Vales v " +
                   "inner join Detalle_vale dv on (dv.id_vale = v.id_vale) " +
                   "inner join Inventario ig on (dv.id_producto = ig.id_producto) " +
                   "inner join User u on (u.id_user = v.id_user) " +
                   "inner join Empleados e on (e.id_empleado = u.id_empleado) " +
                   "where concat(e.nombres,' ',e.apellido_p,' ',e.apellido_m) = '"+empleado+"' and dv.estado = 'SOLICITUD' and ig.id_producto = '"+idProd+"';";
            rs = st.executeQuery(sql);
            rs.next();
            int idVale = rs.getInt(1);
                        System.out.println("encontre el vale: "+idVale);
            //Actualizamos el estatus del producto
            sql = "update Detalle_vale set estado = 'PENDIENTE' where id_producto = '"+idProd+"' and id_vale = "+idVale+";";
            st.executeUpdate(sql);
                        System.out.println("Actualice todo");

            //Cerramos la conexión
            conexion.close();
            return true;
            
        } catch (SQLException ex) {
            System.out.printf("Error al insertar la solicitud en SQL");
            Logger.getLogger(ManagerSolicitud.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } 
        
    }//actualizar_Solicitud
    
    public DefaultTableModel getInventarioStockMin() {
            DefaultTableModel table = new DefaultTableModel();

        try {
            table.addColumn("Clave");
            table.addColumn("Producto");
            table.addColumn("Descripción");
            table.addColumn("Observaciones");
            table.addColumn("Cantidad");
            table.addColumn("Estado");
            
            //Obtiene los productos que tienen su stock menor o igual que el stock minimo
            String sql = "select id_productoGranel,nombre_prod,descripcion,observaciones,stock,estatus from Inventario_granel where stock_min >= stock;";
            conexion = db.getConexion();
            Statement st = conexion.createStatement();
            Object datos[] = new Object[6];
            ResultSet rs = st.executeQuery(sql);

            //Llenar tabla
            while (rs.next()) {

                for(int i = 0;i<6;i++){
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

    }//getInventarioEmpleadoAsignacionesPersonales
    
    public boolean actualizarStock(String codigo,int cantidad){
        
        try{
        
            //Hacemos la conexión
            conexion = db.getConexion();
            //Creamos la variable para hacer operaciones CRUD
            Statement st = conexion.createStatement();
            
            //Actualizamos el stock y el estado si el stock es igual a 0
            String sql = "update Inventario_granel set stock = stock + "+cantidad+" where id_productoGranel = '"+codigo+"' and stock > 0;";
            st.executeUpdate(sql);
            
            //Actualizamos el stock y el estado si el stock es igual a 0
            sql = "update Inventario_granel set stock = "+cantidad+", estatus = 'DISPONIBLE' where id_productoGranel = '"+codigo+"' and stock = 0;";
            st.executeUpdate(sql);
            
            conexion.close();
            return true;
        }catch(SQLException ex){
            System.out.printf("Error al querer actualizar el stock SQL");
            Logger.getLogger(ManagerUsers.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
    }//actualizarStock
    
}//class
