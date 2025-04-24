package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import models.OrdemServico;

public class OrdemServicoDAO {

    // Método para buscar clientes
    public static List<String> buscarClientes() {
        String sql = "SELECT nome FROM clientes";
        List<String> clientes = new ArrayList<>();
        try (Connection conn = DatabaseSQLite.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                clientes.add(rs.getString("nome"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientes;
    }
    
    public static int buscarClienteId(String nomeCliente) {
        String sql = "SELECT id FROM clientes WHERE nome = ?";
        try (Connection conn = DatabaseSQLite.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nomeCliente);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Retorna -1 se o cliente não for encontrado
    }
    
    public static List<String> buscarVeiculosPorCliente(int clienteId) {
        List<String> veiculos = new ArrayList<>();
        String sql = "SELECT modelo || ' - ' || placa AS veiculo FROM veiculos WHERE cliente_id = ?";
        try (Connection conn = DatabaseSQLite.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                veiculos.add(rs.getString("veiculo"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return veiculos;
    }
    
    public static boolean salvarNovoServico(String descricao, double preco) {
        String sql = "INSERT INTO servicos (descricao, preco, data, veiculo_id) VALUES (?, ?, ?, 0)";
        
        try (Connection conn = DatabaseSQLite.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, descricao);
            stmt.setDouble(2, preco);
            
            // Adiciona a data atual
            LocalDate dataAtual = LocalDate.now();
            String dataFormatada = dataAtual.format(DateTimeFormatter.ISO_LOCAL_DATE);
            stmt.setString(3, dataFormatada);
            
            // veiculo_id já está definido como 0 na query SQL
            
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método para buscar veículos
    public static List<String> buscarVeiculos() {
        String sql = "SELECT modelo || ' - ' || placa AS veiculo FROM veiculos";
        List<String> veiculos = new ArrayList<>();
        try (Connection conn = DatabaseSQLite.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                veiculos.add(rs.getString("veiculo"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return veiculos;
    }

    // Método para buscar serviços
    public static List<String> buscarServicos() {
        String sql = "SELECT descricao FROM servicos";
        List<String> servicos = new ArrayList<>();
        try (Connection conn = DatabaseSQLite.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                servicos.add(rs.getString("descricao"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return servicos;
    }

    // Método para buscar preço de um serviço pelo nome
    public static String buscarPrecoServico(String descricao) {
        String sql = "SELECT preco FROM servicos WHERE descricao = ?";
        try (Connection conn = DatabaseSQLite.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, descricao);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return String.valueOf(rs.getDouble("preco"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0.00";
    }
    
    public static String[] buscarDadosCompletos(String nomeCliente) {
        String[] dados = new String[3];
        try (Connection conn = DatabaseSQLite.getConnection()) {
            String sql = "SELECT nome, endereco, telefone FROM clientes WHERE nome = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nomeCliente);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                dados[0] = rs.getString("nome");
                dados[1] = rs.getString("endereco");
                dados[2] = rs.getString("telefone");
                return dados;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    public static boolean renomearServico(String nomeAntigo, String nomeNovo) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseSQLite.getConnection();
            
            // Verificar se o novo nome já existe
            String verificarSQL = "SELECT COUNT(*) FROM servicos WHERE descricao = ?";
            stmt = conn.prepareStatement(verificarSQL);
            stmt.setString(1, nomeNovo);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                // Nome já existe
                return false;
            }
            
            // Atualizar o nome do serviço
            String updateSQL = "UPDATE servicos SET descricao = ? WHERE descricao = ?";
            stmt = conn.prepareStatement(updateSQL);
            stmt.setString(1, nomeNovo);
            stmt.setString(2, nomeAntigo);
            
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean excluirServico(String nomeServico) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseSQLite.getConnection();

            String deleteSQL = "DELETE FROM servicos WHERE descricao = ?";
            stmt = conn.prepareStatement(deleteSQL);
            stmt.setString(1, nomeServico);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static int buscarUltimoNumeroOrdem() {
        String sql = "SELECT MAX(numero_ordem) AS max_numero FROM ordem_servico";

        try (Connection conn = DatabaseSQLite.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("max_numero");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0; // Caso não exista nenhuma ordem ainda
    }

    public static boolean salvarOrdemSeNaoExistir(OrdemServico ordemServico) {
        if (ordemServico.getNumeroOrdem() > 0 && ordemJaExiste(ordemServico.getNumeroOrdem())) {
            return false; // Já existe, não salva de novo
        }

        String sql = "INSERT INTO ordem_servico (numero_ordem) VALUES (?)";

        try (Connection conn = DatabaseSQLite.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ordemServico.getNumeroOrdem());
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private static boolean ordemJaExiste(int numero) {
        String sql = "SELECT COUNT(*) FROM ordem_servico WHERE numero_ordem = ?";

        try (Connection conn = DatabaseSQLite.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, numero);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    
    // Método para buscar dados da empresa
    public static String buscarDadosEmpresa() {
        String sql = "SELECT nome, cpf_cnpj, telefone, endereco FROM empresa LIMIT 1";
        try (Connection conn = DatabaseSQLite.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getString("nome") + ";" +
                       rs.getString("cpf_cnpj") + ";" +
                       rs.getString("telefone") + ";" +
                       rs.getString("endereco");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
