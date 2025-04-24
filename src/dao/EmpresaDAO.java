package dao;

import models.Empresa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmpresaDAO {

    public Empresa buscarEmpresa() {
        String sql = "SELECT * FROM empresa LIMIT 1;";
        try (Connection connection = DatabaseSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                Empresa empresa = new Empresa();
                empresa.setId(rs.getInt("id"));
                empresa.setNome(rs.getString("nome"));
                empresa.setCpfCnpj(rs.getString("cpf_cnpj"));
                empresa.setTelefone(rs.getString("telefone"));
                empresa.setEndereco(rs.getString("endereco"));
                System.out.println("Empresa encontrada: ID=" + empresa.getId());
                return empresa;
            } else {
                System.out.println("Nenhuma empresa encontrada no banco de dados.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar empresa: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public void salvarEmpresa(Empresa empresa) {
        if (empresa.getId() == null) {
            String sqlInsert = "INSERT INTO empresa (nome, cpf_cnpj, telefone, endereco) VALUES (?, ?, ?, ?);";
            try (Connection connection = DatabaseSQLite.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sqlInsert)) {

                stmt.setString(1, empresa.getNome());
                stmt.setString(2, empresa.getCpfCnpj());
                stmt.setString(3, empresa.getTelefone());
                stmt.setString(4, empresa.getEndereco());
                stmt.executeUpdate();
                System.out.println("Empresa salva com sucesso!");
            } catch (SQLException e) {
                System.err.println("Erro ao salvar empresa: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            atualizarEmpresa(empresa); // Chama o método de atualização caso o ID exista
        }
    }

    public void atualizarEmpresa(Empresa empresa) {
        if (empresa.getId() <= 0) {
            System.err.println("Erro: ID inválido para atualizar a empresa.");
            return; // Retorna sem tentar atualizar caso o ID seja inválido.
        }

        String sql = "UPDATE empresa SET nome = ?, cpf_cnpj = ?, telefone = ?, endereco = ? WHERE id = ?;";
        try (Connection connection = DatabaseSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, empresa.getNome());
            stmt.setString(2, empresa.getCpfCnpj());
            stmt.setString(3, empresa.getTelefone());
            stmt.setString(4, empresa.getEndereco());
            stmt.setInt(5, empresa.getId());
            
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated == 0) {
                System.err.println("Erro: Nenhuma empresa foi atualizada. Verifique se o ID está correto.");
            } else {
                System.out.println("Empresa atualizada com sucesso!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
