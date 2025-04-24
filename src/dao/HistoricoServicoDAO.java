package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.HistoricoServico;

public class HistoricoServicoDAO {

    public static void salvarServicoNoHistorico(HistoricoServico servico) {
    	System.out.println("Salvando serviço no histórico: " + servico.getDescricao());
    	System.out.println("Descrição: " + servico.getDescricao());
    	System.out.println("Data: " + servico.getData());
    	System.out.println("Valor: " + servico.getValor());
    	System.out.println("Veiculo ID: " + servico.getVeiculoId());
    	System.out.println("Cliente Nome: " + servico.getClienteNome());
    	System.out.println("Cliente Telefone: " + servico.getClienteTelefone());
        String sql = "INSERT INTO historico_servicos (descricao, data, valor, veiculo_id, cliente_nome, cliente_telefone) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseSQLite.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, servico.getDescricao());
            stmt.setString(2, servico.getData());
            stmt.setDouble(3, servico.getValor());
            stmt.setInt(4, servico.getVeiculoId());
            stmt.setString(5, servico.getClienteNome());
            stmt.setString(6, servico.getClienteTelefone());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<HistoricoServico> buscarHistoricoPorVeiculo(String termoBusca) {
        List<HistoricoServico> lista = new ArrayList<>();
        String sql = "SELECT hs.id, hs.descricao, hs.data, hs.valor, hs.veiculo_id, "
                   + "v.modelo, v.placa, c.nome AS nome_cliente "
                   + "FROM historico_servicos hs "
                   + "JOIN veiculos v ON hs.veiculo_id = v.id "
                   + "JOIN clientes c ON v.cliente_id = c.id "
                   + "WHERE v.modelo LIKE ? OR v.placa LIKE ?";

        try (Connection conn = DatabaseSQLite.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + termoBusca + "%");
            stmt.setString(2, "%" + termoBusca + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                HistoricoServico hs = new HistoricoServico();
                hs.setId(rs.getInt("id"));
                hs.setDescricao(rs.getString("descricao"));
                hs.setData(rs.getString("data"));
                hs.setValor(rs.getDouble("valor"));
                hs.setVeiculoId(rs.getInt("veiculo_id"));
                hs.setModeloVeiculo(rs.getString("modelo"));
                hs.setPlacaVeiculo(rs.getString("placa"));
                hs.setNomeCliente(rs.getString("nome_cliente"));
                lista.add(hs);
            }

        } catch (SQLException e) {
        	System.err.println("Erro ao salvar no histórico de serviços: " + e.getMessage());
        }

        return lista;
    }

    public static void excluirHistorico(int id) {
        String sql = "DELETE FROM historico_servicos WHERE id = ?";

        try (Connection conn = DatabaseSQLite.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
