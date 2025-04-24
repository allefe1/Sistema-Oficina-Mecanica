package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBanco {

    public static Connection getConnection() throws SQLException {
        
        String url = ""; // 
        String usuario = "";
        String senha = ""; 

        // Conectando ao banco de dados
        return DriverManager.getConnection(url, usuario, senha);
    }
}

