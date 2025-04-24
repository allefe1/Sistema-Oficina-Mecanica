package models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login {

    // Método para validar o login
    public boolean validarLogin(String usuario, String senha) {
        String url = "jdbc:postgresql://<seu_servidor>:5432/meu_sistema";  // Substitua com seu host e banco
        String dbUsuario = "meu_usuario";
        String dbSenha = "minha_senha";
        
        // SQL para buscar o usuário e senha no banco de dados
        String sql = "SELECT status FROM usuarios WHERE usuario = ? AND senha = ?";

        try (Connection conexao = DriverManager.getConnection(url, dbUsuario, dbSenha)) {
            // Preparando o comando SQL
            try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
                // Definindo os parâmetros para nome de usuário e senha
                stmt.setString(1, usuario);
                stmt.setString(2, senha);

                // Executando a consulta
                ResultSet rs = stmt.executeQuery();
                
                // Verifica se encontrou um usuário correspondente
                if (rs.next()) {
                    String status = rs.getString("status");

                    // Verifica se o status é 'pago'
                    if ("pago".equals(status)) {
                        return true;  // Login bem-sucedido
                    } else {
                        System.out.println("Status não pago. Acesso negado.");
                    }
                } else {
                    System.out.println("Usuário ou senha incorretos.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao conectar ao banco: " + e.getMessage());
        }

        return false;  // Login falhou
    }

    public static void main(String[] args) {
        Login login = new Login();
        
        // Teste do login (coloque os dados de login fornecidos pelo usuário)
        boolean sucesso = login.validarLogin("teste", "senha123");

        if (sucesso) {
            System.out.println("Login bem-sucedido!");
        } else {
            System.out.println("Falha no login.");
        }
    }
}