package dao;

import models.Estoque;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstoqueDAO {

    // Método para cadastrar uma peça no estoque
    public void cadastrarPeca(Estoque peca) {
        String sql = "INSERT INTO estoque (nome, quantidade, preco) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, peca.getNome());
            stmt.setInt(2, peca.getQuantidade());
            stmt.setDouble(3, peca.getPreco());
            stmt.executeUpdate();
            System.out.println("Peça cadastrada com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar peça: " + e.getMessage());
        }
    }

    // Método para buscar todas as peças no estoque
    public List<Estoque> buscarPecas() {
        List<Estoque> pecas = new ArrayList<>();
        String sql = "SELECT * FROM estoque";

        try (Connection connection = DatabaseSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Estoque peca = new Estoque(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getInt("quantidade"),
                    rs.getDouble("preco")
                );
                pecas.add(peca);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar peças: " + e.getMessage());
        }
        return pecas;
    }

    // Método para buscar uma peça pelo nome
    public Estoque buscarPecaPorNome(String nome) {
        String sql = "SELECT * FROM estoque WHERE nome = ?";
        Estoque peca = null;

        try (Connection connection = DatabaseSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nome);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                peca = new Estoque(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getInt("quantidade"),
                    rs.getDouble("preco")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar peça por nome: " + e.getMessage());
        }
        return peca;
    }
    
    public void aumentarQuantidade(int id, int quantidade) {
        String sql = "UPDATE estoque SET quantidade = quantidade + ? WHERE id = ?";
        
        try (Connection connection = DatabaseSQLite.getConnection();
        	PreparedStatement stmt = connection.prepareStatement(sql)) {
            
        	stmt.setInt(1, quantidade);
        	stmt.setInt(2, id);
        	stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println("Erro ao aumentar quantidade: " + e.getMessage());
        }
    }

    // Método para diminuir a quantidade de uma peça
    public void diminuirQuantidade(int id, int quantidade) {
        String sql = "UPDATE estoque SET quantidade = quantidade - ? WHERE id = ?";

        try (Connection connection = DatabaseSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quantidade);
            stmt.setInt(2, id);
            stmt.executeUpdate();
            System.out.println("Quantidade da peça atualizada com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao diminuir quantidade: " + e.getMessage());
        }
    }
    
    public void atualizarPreco(int id, double novoPreco) {
        String sql = "UPDATE estoque SET preco = ? WHERE id = ?";
        try (Connection connection = DatabaseSQLite.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, novoPreco);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para remover uma peça do estoque
    public void removerPeca(int id) {
        String sql = "DELETE FROM estoque WHERE id = ?";

        try (Connection connection = DatabaseSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Peça removida com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao remover peça: " + e.getMessage());
        }
    }
}