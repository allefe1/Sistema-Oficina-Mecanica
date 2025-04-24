package dao;

import models.Servico;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicoDAO {
    private Connection connection;

    public ServicoDAO() {
        try {
            connection = DatabaseSQLite.getConnection();
        } catch (SQLException e) {
            e.printStackTrace(); // Registra o erro no console
            throw new RuntimeException("Erro ao conectar com o banco de dados: " + e.getMessage());
        }
    }

    // Método para inserir serviços padrão, evitando duplicatas
    public void inserirServicosPadrao() {
        String sqlInsert = "INSERT INTO servicos (descricao, preco, data, veiculo_id) "
                + "SELECT ?, ?, ?, ? "
                + "WHERE NOT EXISTS (SELECT 1 FROM servicos WHERE descricao = ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sqlInsert)) {
            String[][] servicosPadroes = {
                {"Troca de óleo com filtro", "100.00"},
                {"Troca de óleo sem filtro", "80.00"},
                {"Alinhamento", "80.00"},
                {"Balanceamento", "50.00"},
                {"Revisão completa", "300.00"},
                {"Troca de filtro de ar", "60.00"},
                {"Troca de pastilhas de freio", "150.00"},
                {"Substituição de bateria", "200.00"},
                {"Troca de vela de ignição", "120.00"},
                {"Troca de correia dentada", "500.00"},
                {"Regulagem de faróis", "40.00"},
                {"Reparo no sistema de arrefecimento", "250.00"},
                {"Revisão elétrica", "180.00"},
                {"Troca de óleo da transmissão", "220.00"},
                {"Higienização do ar-condicionado", "100.00"},
                {"Substituição de amortecedores", "600.00"},
                {"Revisão de suspensão", "350.00"},
                {"Revisão de freios", "200.00"},
                {"Troca de filtro de combustível", "70.00"},
                {"Reparo no sistema de direção hidráulica", "400.00"},
                {"Troca de lâmpadas", "30.00"},
                {"Diagnóstico de injeção eletrônica", "150.00"},
                {"Troca de embreagem", "800.00"},
                {"Reparo no alternador", "350.00"},
                {"Reparo no motor de partida", "300.00"},
                {"Troca de coxins do motor", "250.00"},
                {"Revisão de pneus", "50.00"},
                {"Revisão de sistema de escapamento", "150.00"},
                {"Troca de juntas do motor", "500.00"},
                {"Manutenção de turbo", "1000.00"},
                {"Revisão de bomba de combustível", "300.00"},
                {"Alinhamento de direção elétrica", "200.00"},
                {"Instalação de som automotivo", "120.00"},
                {"Polimento de faróis", "80.00"},
                {"Desempeno de rodas", "150.00"},
                {"Troca de óleo diferencial", "180.00"},
                {"Troca de óleo do freio", "100.00"},
                {"Calibração de pneus", "10.00"},
                {"Montagem e desmontagem de pneus", "80.00"},
                {"Instalação de película automotiva", "300.00"},
                {"Troca de correia do alternador", "200.00"},
                {"Reparo de trincas no para-brisa", "180.00"},
                {"Troca de palhetas do limpador", "50.00"},
                {"Descarbonização do motor", "400.00"},
                {"Limpeza de bicos injetores", "250.00"},
                {"Teste de carga da bateria", "50.00"},
                {"Verificação de aterramento elétrico", "100.00"},
                {"Manutenção preventiva", "350.00"},
                {"Troca de eixo traseiro", "800.00"},
                {"Inspeção pré-compra de veículos", "300.00"},
                {"Revisão de airbags", "400.00"},
                {"Troca de sensores de estacionamento", "150.00"},
                {"Revisão de sistema de suspensão pneumática", "600.00"},
                {"Reparo de trincas na pintura", "250.00"},
                {"Instalação de faróis auxiliares", "200.00"},
                {"Limpeza interna de motor", "500.00"},
                {"Troca de mangueiras de radiador", "200.00"},
                {"Reparo de freio de estacionamento", "180.00"},
                {"Revisão de chicote elétrico", "300.00"},
                {"Substituição de fusíveis", "20.00"},
                {"Mão de obra - Mecânica geral", "150.00"},
                {"Mão de obra - Elétrica", "120.00"},
                {"Mão de obra - Funilaria", "200.00"},
                {"Mão de obra - Pintura", "180.00"},
                {"Mão de obra - Acabamento", "100.00"},
                {"Mão de obra - Diagnóstico", "80.00"},
                {"Mão de obra - Solda", "160.00"},
                {"Mão de obra - Tapeçaria", "140.00"},
                {"Troca de radiador", "450.00"},
                {"Troca de bomba d'água", "280.00"},
                {"Troca de terminais de direção", "200.00"},
                {"Troca de pivôs", "180.00"},
                {"Troca de bieletas", "150.00"},
                {"Troca de buchas de suspensão", "220.00"},
                {"Troca de rolamentos", "180.00"},
                {"Troca de retentores", "120.00"},
                {"Troca de junta homocinética", "350.00"},
                {"Troca de coifas", "160.00"},
                {"Troca de tensor da correia", "180.00"},
                {"Troca de bomba de óleo", "400.00"},
                {"Troca de válvula termostática", "150.00"},
                {"Troca de cabeçote", "1500.00"},
                {"Troca de comando de válvulas", "800.00"},
                {"Troca de bronzinas", "600.00"},
                {"Troca de pistões", "1200.00"},
                {"Troca de anéis", "800.00"},
                {"Troca de válvulas", "900.00"},
                {"Troca de tuchos", "400.00"},
                {"Troca de bomba de combustível elétrica", "350.00"},
                {"Troca de catalisador", "800.00"},
                {"Troca de sensores de oxigênio", "200.00"},
                {"Troca de coletor de escape", "450.00"},
                {"Troca de coletor de admissão", "400.00"},
                {"Troca de filtro de partículas diesel", "1200.00"},
                {"Troca de turbina", "2000.00"},
                {"Troca de intercooler", "800.00"}
            };

            for (String[] servico : servicosPadroes) {
                stmt.setString(1, servico[0]); // Descrição do serviço
                stmt.setDouble(2, Double.parseDouble(servico[1])); // Preço
                stmt.setString(3, java.time.LocalDate.now().toString()); // Data atual
                stmt.setInt(4, 0); // Valor padrão para veiculo_id
                stmt.setString(5, servico[0]); // Verifica duplicatas
                stmt.executeUpdate();
            }

            System.out.println("Serviços padrão adicionados com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao inserir serviços padrão: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // Método para adicionar um novo serviço
    public void addServico(Servico servico) {
        String sql = "INSERT INTO servicos (descricao, preco, data, veiculo_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, servico.getDescricao());
            stmt.setDouble(2, servico.getPreco());
            stmt.setString(3, servico.getData());
            stmt.setObject(4, servico.getVeiculoId() == 0 ? null : servico.getVeiculoId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para buscar serviços associados a um cliente específico
    public List<Servico> getServicosByClienteId(int clienteId) {
        List<Servico> servicos = new ArrayList<>();
        String sql = "SELECT * FROM servicos WHERE veiculo_id IN (SELECT id FROM veiculos WHERE cliente_id = ?) "
                   + "OR veiculo_id IS NULL"; // Inclui serviços padrão (sem veículo associado)
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Servico servico = new Servico(
                        rs.getInt("id"),
                        rs.getString("descricao"),
                        rs.getDouble("preco"),
                        rs.getString("data"),
                        rs.getObject("veiculo_id") != null ? rs.getInt("veiculo_id") : 0
                    );
                    servicos.add(servico);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return servicos;
    }

    // Método para buscar todos os serviços, incluindo padrões e associados
    public List<Servico> getServicos() {
        List<Servico> servicos = new ArrayList<>();
        String sql = "SELECT DISTINCT * FROM servicos";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Servico servico = new Servico(
                    rs.getInt("id"),
                    rs.getString("descricao"),
                    rs.getDouble("preco"),
                    rs.getString("data"),
                    rs.getObject("veiculo_id") != null ? rs.getInt("veiculo_id") : 0
                );
                servicos.add(servico);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return servicos;
    }
    
 // Retorna todos os serviços, incluindo os padrões e associados
    public List<Servico> getServicosCompletos() {
        List<Servico> servicos = new ArrayList<>();
        String sql = "SELECT * FROM servicos"; // Sem filtro, pega todos os serviços (padrões e associados)
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Servico servico = new Servico(
                    rs.getInt("id"),
                    rs.getString("descricao"),
                    rs.getDouble("preco"),
                    rs.getString("data"),
                    rs.getInt("veiculo_id")
                );
                servicos.add(servico);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return servicos;
    }
    
 // Retorna apenas os serviços associados a veículos (com veiculo_id)
    public List<Servico> getServicosAssociados() {
        List<Servico> servicos = new ArrayList<>();
        // Alterando a query para garantir que só serviços com veiculo_id diferente de 0 sejam retornados
        String sql = "SELECT * FROM servicos WHERE veiculo_id != 0";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Servico servico = new Servico(
                    rs.getInt("id"),
                    rs.getString("descricao"),
                    rs.getDouble("preco"),
                    rs.getString("data"),
                    rs.getInt("veiculo_id")
                );
                servicos.add(servico);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return servicos;
    }


    // Método para remover um serviço por ID
    public void removeServico(int id) {
        String sql = "DELETE FROM servicos WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}