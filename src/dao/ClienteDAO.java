package dao;

import models.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    // adicionar um cliente
    public void addCliente(Cliente cliente) {
        String sql = "INSERT INTO clientes (nome, telefone, endereco) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getTelefone());
            stmt.setString(3, cliente.getEndereco());
            stmt.executeUpdate();
            System.out.println("Cliente adicionado com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // listar todos os clientes
    public List<Cliente> getClientes() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes";
        try (Connection connection = DatabaseSQLite.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                String telefone = rs.getString("telefone");
                String endereco = rs.getString("endereco");
                Cliente cliente = new Cliente(nome, telefone, endereco);
                cliente.setId(id);
                clientes.add(cliente);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientes;
    }

    // Método para buscar clientes por nome ou telefone
    public List<Cliente> getClientesByNameOrPhone(String searchTerm) {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes WHERE nome LIKE ? OR telefone LIKE ?";
        try (Connection connection = DatabaseSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + searchTerm + "%"); // Pesquisa por nome similar
            stmt.setString(2, "%" + searchTerm + "%"); // Pesquisa por telefone similar
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String nome = rs.getString("nome");
                    String telefone = rs.getString("telefone");
                    String endereco = rs.getString("endereco");
                    Cliente cliente = new Cliente(nome, telefone, endereco);
                    cliente.setId(id);
                    clientes.add(cliente);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientes;
    }
    
    public String getClienteNomeById(int clienteId) {
        String nome = null;
        String sql = "SELECT nome FROM clientes WHERE id = ?";

        try (Connection connection = DatabaseSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    nome = rs.getString("nome");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nome;
    }

    // Método para atualizar um cliente
    public void updateCliente(Cliente cliente) {
        String sql = "UPDATE clientes SET nome = ?, telefone = ?, endereco = ? WHERE id = ?";
        try (Connection connection = DatabaseSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getTelefone());
            stmt.setString(3, cliente.getEndereco());
            stmt.setInt(4, cliente.getId());
            stmt.executeUpdate();
            System.out.println("Cliente atualizado com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para remover um cliente
    public void removeCliente(int id) {
        String sql = "DELETE FROM clientes WHERE id = ?";
        try (Connection connection = DatabaseSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Cliente removido com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
