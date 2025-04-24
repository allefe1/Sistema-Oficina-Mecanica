package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    public boolean verificarCredenciais(String usuario, String senha) {
        String sql = "SELECT * FROM usuarios WHERE usuario = ? AND senha = ? AND status = 'pago'";

        try (Connection conn = ConexaoBanco.getConnection(); // Usando o método de conexão
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuario);
            stmt.setString(2, senha);
            
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Retorna true se o usuário for encontrado e o status for 'pago'
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false; // Se não encontrar o usuário ou se houver um erro
    }
}
