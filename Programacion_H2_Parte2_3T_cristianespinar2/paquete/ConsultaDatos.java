package paquete;

import java.sql.*;  // Importa las clases necesarias para trabajar con bases de datos
import java.util.Scanner;  // Importa la clase Scanner para leer entradas del usuario

public class ConsultaDatos {
    // Método que muestra el menú para que el usuario elija qué opción ejecutar
    public static void mostrarMenu() {
        Scanner scanner = new Scanner(System.in);  // Crea un objeto Scanner para leer la opción del usuario
        while (true) {  // Bucle para mostrar el menú hasta que el usuario decida salir
            System.out.println("Elige una opcion: ");
            System.out.println("1.Ver películas");
            System.out.println("2.Añadir película");
            System.out.println("3.Eliminar película");
            System.out.println("4.Modificar película");
            System.out.println("5.Salir");
            int opcion = scanner.nextInt();  // Lee la opción seleccionada por el usuario
            scanner.nextLine();
            switch (opcion) {
                case 1:
                    mostrarPeliculas();  // Si se selecciona la opción 1, se llaman las películas
                    break;
                case 2:
                    añadirPelicula(scanner);  // Si se selecciona la opción 2, se añade una nueva película
                    break;
                case 3:
                    eliminarPelicula(scanner);  // Si se selecciona la opción 3, se elimina una película
                    break;
                case 4:
                    modificarPelicula(scanner);  // Si se selecciona la opción 4, se modifica una película
                    break;
                case 5:
                    System.out.println("saliendo..");  // Si se selecciona la opción 5, se sale del programa
                    return;  // Sale del bucle y termina el programa
                default:
                    System.out.println("Opción no válida.");  // Muestra un mensaje si la opción es incorrecta
            }
        }
    }
    // Método que muestra las películas disponibles en la base de datos
    public static void mostrarPeliculas() {
        String url = "jdbc:mysql://localhost:3306/cine_cristianespinar";  // URL de la base de datos
        String usuario = "root";  // Usuario de la base de datos
        String contraseña = "1234";  // Contraseña de la base de datos

        try (Connection conexion = DriverManager.getConnection(url, usuario, contraseña)) {  // Establece la conexión con la base de datos
            // Consulta SQL para obtener las películas y sus tipos
            String query = "SELECT p.id_pelicula, p.titulo, p.director, p.duracion, p.clasificacion, t.nombre_tipo " +
                           "FROM peliculas p JOIN tipo t ON p.id_tipo = t.id_tipo";

            Statement stmt = conexion.createStatement();  // Crea un Statement para ejecutar la consulta
            ResultSet rs = stmt.executeQuery(query);  // Ejecuta la consulta y obtiene los resultados

            // Imprime el encabezado de la tabla con los nombres de las columnas
            System.out.printf("\n%-10s %-30s %-20s %-10s %-10s %-15s\n", 
                              "ID", "Titulo", "Director", "Duracion", "Clasificacion", "Tipo");
            System.out.println("---------------------------------------------------------------------------------");
            // Recorre los resultados de la consulta e imprime cada película
            while (rs.next()) {
                System.out.printf("%-10s %-30s %-20s %-10d %-10s %-15s\n", 
                                  rs.getString("id_pelicula"),
                                  rs.getString("titulo"),
                                  rs.getString("director"),
                                  rs.getInt("duracion"),
                                  rs.getString("clasificacion"),
                                  rs.getString("nombre_tipo"));
            }
        } catch (SQLException e) {
            // Si ocurre un error en la consulta o conexión, se imprime un mensaje de error
            System.out.println("Error al consultar las peliculas: " + e.getMessage());
        }
    }
    // Método que añade una nueva película a la base de datos
    public static void añadirPelicula(Scanner scanner) {
        System.out.println("añade una película");
        System.out.print("Escribe el id de la pelicula: ");
        String id = scanner.nextLine();
        String url = "jdbc:mysql://localhost:3306/cine_cristianespinar";  // URL de la base de datos
        String usuario = "root";  // Usuario de la base de datos
        String contraseña = "1234";  // Contraseña de la base de datos
        try (Connection conexion = DriverManager.getConnection(url, usuario, contraseña)) {
            // Verificar si ya existe una película con el mismo id
            String consultaExistencia = "SELECT * FROM peliculas WHERE id_pelicula = ?";
            PreparedStatement psExistencia = conexion.prepareStatement(consultaExistencia);
            psExistencia.setString(1, id);
            ResultSet rs = psExistencia.executeQuery();
            if (rs.next()) {
                System.out.println("error id repetido.");
                return;
            }
            // Pedir el resto de los datos
            System.out.print("Titulo: ");
            String titulo = scanner.nextLine();
            System.out.print("Director: ");
            String director = scanner.nextLine();
            System.out.print("Duración en minutos : ");
            int duracion = scanner.nextInt();
            scanner.nextLine(); 
            System.out.print("Clasificacion (A,B,C,D,E (segun te haya gustado): ");
            String clasificacion = scanner.nextLine();
            System.out.print("Tipo : ");
            String idTipo = scanner.nextLine();
            // Verificar si el idTipo existe en la tabla tipo
            String consultaTipo = "SELECT * FROM tipo WHERE id_tipo = ?";
            PreparedStatement psConsultaTipo = conexion.prepareStatement(consultaTipo);
            psConsultaTipo.setString(1, idTipo);
            ResultSet rsTipo = psConsultaTipo.executeQuery();
            if (!rsTipo.next()) {
                // Si el idTipo no existe, lo insertamos
                System.out.println(" El ID de tipo no existe. Lo vamos a añadir.");
                // Insertar el nuevo tipo en la tabla tipo
                String insertTipoSQL = "INSERT INTO tipo (id_tipo, nombre_tipo) VALUES (?, ?)";
                System.out.print("Nombre del tipo: ");
                String nombreTipo = scanner.nextLine(); // Pedir nombre del tipo
                PreparedStatement psInsertTipo = conexion.prepareStatement(insertTipoSQL);
                psInsertTipo.setString(1, idTipo);
                psInsertTipo.setString(2, nombreTipo);

                int filasTipoAfectadas = psInsertTipo.executeUpdate();
                if (filasTipoAfectadas > 0) {
                    System.out.println("El tipo se ha añadido correctamente ");
                } else {
                    System.out.println("Error al añadir el tipo");
                    return;
                }
            }
            // Insertar la película
            String insertSQL = "INSERT INTO peliculas (id_pelicula, titulo, director, duracion, clasificacion, id_tipo) " +
                               "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement psInsert = conexion.prepareStatement(insertSQL);
            psInsert.setString(1, id);
            psInsert.setString(2, titulo);
            psInsert.setString(3, director);
            psInsert.setInt(4, duracion);
            psInsert.setString(5, clasificacion);
            psInsert.setString(6, idTipo);

            int filasAfectadas = psInsert.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("La pelicula se ha añadido .");
            } else {
                System.out.println(" Error, no ha podido añadirse.");
            }

        } catch (SQLException e) {
            System.out.println("Error al añadir la pelicula: " + e.getMessage());
        }
    }
    // Método que elimina una película de la base de datos por su ID
    public static void eliminarPelicula(Scanner scanner) {
        System.out.println("\n Eliminar pelicula: ");
        System.out.print("ID de la película a eliminar: ");
        String id = scanner.nextLine();
        String url = "jdbc:mysql://localhost:3306/cine_cristianespinar";  // URL de la base de datos
        String usuario = "root";  // Usuario de la base de datos
        String contraseña = "1234";  // Contraseña de la base de datos
        try (Connection conexion = DriverManager.getConnection(url, usuario, contraseña)) {
            // Verificar si existe
            String consulta = "SELECT * FROM peliculas WHERE id_pelicula = ?";
            PreparedStatement psConsulta = conexion.prepareStatement(consulta);
            psConsulta.setString(1, id);
            ResultSet rs = psConsulta.executeQuery();

            if (!rs.next()) {
                System.out.println("la pelicula no existe.");
                return;
            }
            // Eliminar
            String deleteSQL = "DELETE FROM peliculas WHERE id_pelicula = ?";
            PreparedStatement psDelete = conexion.prepareStatement(deleteSQL);
            psDelete.setString(1, id);
            int filas = psDelete.executeUpdate();

            if (filas > 0) {
                System.out.println("pelicula eliminada.");
            } else {
                System.out.println("no se pudo eliminar la pelicula.");
            }

        } catch (SQLException e) {
            System.out.println("error al eliminar la pelicula: " + e.getMessage());
        }
    }

    // Método que modifica una película por su ID
    public static void modificarPelicula(Scanner scanner) {
        System.out.println("\n Modificar pelicula: ");

        System.out.print("Escribe el id de la pelicula para modificarla : ");
        String id = scanner.nextLine();

        String url = "jdbc:mysql://localhost:3306/cine_cristianespinar";  // URL de la base de datos
        String usuario = "root";  // Usuario de la base de datos
        String contraseña = "1234";  // Contraseña de la base de datos

        try (Connection conexion = DriverManager.getConnection(url, usuario, contraseña)) {

            // Verificar si existe
            String consulta = "SELECT * FROM peliculas WHERE id_pelicula = ?";
            PreparedStatement psConsulta = conexion.prepareStatement(consulta);
            psConsulta.setString(1, id);
            ResultSet rs = psConsulta.executeQuery();

            if (!rs.next()) {
                System.out.println("La pelicula no existe.");
                return;
            }
            // Pedir nuevos datos
            System.out.print("Escribe el nuevo titulo: ");
            String nuevoTitulo = scanner.nextLine();
            System.out.print("Escribe el nuevo director: ");
            String nuevoDirector = scanner.nextLine();

            // Actualizar
            String updateSQL = "UPDATE peliculas SET titulo = ?, director = ? WHERE id_pelicula = ?";
            PreparedStatement psUpdate = conexion.prepareStatement(updateSQL);
            psUpdate.setString(1, nuevoTitulo);
            psUpdate.setString(2, nuevoDirector);
            psUpdate.setString(3, id);
            int filas = psUpdate.executeUpdate();
            if (filas > 0) {
                System.out.println("la pelicula ha sido actualizada.");
            } else {
                System.out.println("No se pudo actualizar la pelicula.");
            }

        } catch (SQLException e) {
            System.out.println("Error al modificar la pelicula: " + e.getMessage());
        }
    }
}



