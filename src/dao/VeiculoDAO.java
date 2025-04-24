package dao;

import models.Veiculo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VeiculoDAO {

    public void addVeiculo(Veiculo veiculo) {
        String sql = "INSERT INTO veiculos (modelo, placa, ano, chassi, cliente_id) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseSQLite.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, veiculo.getModelo());
            stmt.setString(2, veiculo.getPlaca().toUpperCase());
            stmt.setString(3, veiculo.getAno());
            stmt.setString(4, veiculo.getChassi());
            stmt.setInt(5, veiculo.getClienteId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int generatedId = rs.getInt(1);
                        veiculo.setId(generatedId);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void createVeiculosTable() {
        String sql = "CREATE TABLE IF NOT EXISTS veiculos ("
                 + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                 + "modelo TEXT NOT NULL, "
                 + "placa TEXT NOT NULL, "
                 + "ano VARCHAR(4) NOT NULL, "
                 + "chassi VARCHAR(17) NOT NULL, "
                 + "cliente_id INTEGER, "
                 + "FOREIGN KEY(cliente_id) REFERENCES clientes(id)"
                 + ");";

        try (Connection connection = DatabaseSQLite.getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabela veiculos criada com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static Veiculo buscarVeiculoPorModeloEPlaca(String modelo, String placa) {
        String sql = "SELECT * FROM veiculos WHERE modelo = ? AND placa = ?";
        try (Connection conn = DatabaseSQLite.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, modelo);
            stmt.setString(2, placa);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String ano = rs.getString("ano");
                    String chassi = rs.getString("chassi");
                    int clienteId = rs.getInt("cliente_id");
                    Veiculo veiculo = new Veiculo(modelo, placa, ano, chassi, clienteId);
                    veiculo.setId(id); // <-- Adiciona o ID corretamente
                    return veiculo;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Veiculo> getVeiculosByCliente(int clienteId) {
        List<Veiculo> veiculos = new ArrayList<>();
        String sql = "SELECT * FROM veiculos WHERE cliente_id = ?";
        try (Connection connection = DatabaseSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String modelo = rs.getString("modelo");
                    String placa = rs.getString("placa");
                    String ano = rs.getString("ano");
                    String chassi = rs.getString("chassi");
                    Veiculo veiculo = new Veiculo(modelo, placa, ano, chassi, clienteId);
                    veiculo.setId(id);
                    veiculos.add(veiculo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return veiculos;
    }

    public void updateVeiculo(Veiculo veiculo) {
        String sql = "UPDATE veiculos SET modelo = ?, placa = ?, ano = ?, chassi = ?, cliente_id = ? WHERE id = ?";
        try (Connection connection = DatabaseSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, veiculo.getModelo());
            stmt.setString(2, veiculo.getPlaca().toUpperCase());
            stmt.setString(3, veiculo.getAno());
            stmt.setString(4, veiculo.getChassi());
            stmt.setInt(5, veiculo.getClienteId());
            stmt.setInt(6, veiculo.getId());
            stmt.executeUpdate();
            System.out.println("Veículo atualizado com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeVeiculo(int id) {
        String sql = "DELETE FROM veiculos WHERE id = ?";
        try (Connection connection = DatabaseSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Veículo removido com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public List<Veiculo> getVeiculos() {
        List<Veiculo> veiculos = new ArrayList<>();
        String sql = "SELECT * FROM veiculos";

        try (Connection connection = DatabaseSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String modelo = rs.getString("modelo");
                String placa = rs.getString("placa");
                String ano = rs.getString("ano");
                String chassi = rs.getString("chassi");
                int clienteId = rs.getInt("cliente_id");

                Veiculo veiculo = new Veiculo(modelo, placa, ano, chassi, clienteId);
                veiculo.setId(id);
                veiculos.add(veiculo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return veiculos;
    }

    public List<Veiculo> searchVeiculos(String searchTerm) {
        List<Veiculo> veiculos = new ArrayList<>();
        String sql = "SELECT * FROM veiculos WHERE modelo LIKE ? OR placa LIKE ? OR ano LIKE ? OR chassi LIKE ?";
        try (Connection connection = DatabaseSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String modelo = rs.getString("modelo");
                    String placa = rs.getString("placa");
                    String ano = rs.getString("ano");
                    String chassi = rs.getString("chassi");
                    int clienteId = rs.getInt("cliente_id");
                    Veiculo veiculo = new Veiculo(modelo, placa, ano, chassi, clienteId);
                    veiculo.setId(id);
                    veiculos.add(veiculo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return veiculos;
    }
}