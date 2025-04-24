package dao;

import java.sql.*;

public class DatabaseSQLite {

    private static final String URL = "jdbc:sqlite:oficina.db";

    public static Connection getConnection() throws SQLException {
        try {
            // Registrar o driver SQLite
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver do SQLite não encontrado", e);
        }
        return DriverManager.getConnection(URL);
    }

    // Criar as tabelas do banco de dados
    public static void createTables() {
        // Criar a tabela "clientes"
        String sqlClientes = "CREATE TABLE IF NOT EXISTS clientes ("
                           + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                           + "nome TEXT NOT NULL, "
                           + "telefone TEXT, "
                           + "endereco TEXT"
                           + ");";

        // Criar a tabela "veiculos"
        String sqlVeiculos = "CREATE TABLE IF NOT EXISTS veiculos ("
                           + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                           + "modelo TEXT NOT NULL, "
                           + "placa TEXT NOT NULL, "
                           + "ano VARCHAR(4) NULL, "
                           + "chassi VARCHAR(17) NULL, "
                           + "cliente_id INTEGER NOT NULL, "
                           + "FOREIGN KEY(cliente_id) REFERENCES clientes(id)"
                           + ");";

        // Criar a tabela "servicos"
        String sqlServicos = "CREATE TABLE IF NOT EXISTS servicos ("
                           + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                           + "descricao TEXT NOT NULL, "
                           + "preco REAL NOT NULL, "
                           + "data TEXT NOT NULL, "
                           + "veiculo_id INTEGER NOT NULL, "
                           + "FOREIGN KEY(veiculo_id) REFERENCES veiculos(id)"
                           + ");";

        // Criar a tabela "empresa"
        String sqlEmpresa = "CREATE TABLE IF NOT EXISTS empresa ("
                          + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                          + "nome TEXT NOT NULL, "
                          + "cpf_cnpj TEXT NOT NULL, "
                          + "telefone TEXT, "
                          + "endereco TEXT"
                          + ");";

        // Criar a tabela "estoque"
        String sqlEstoque = "CREATE TABLE IF NOT EXISTS estoque ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "nome TEXT NOT NULL, "
                + "quantidade INTEGER NOT NULL, "
                + "preco REAL NOT NULL"
                + ");";

        // Criar a tabela "ordem_servico"
        String sqlOrdemServico = "CREATE TABLE IF NOT EXISTS ordem_servico ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "numero_ordem INTEGER, "
                + "cliente_id INTEGER, "
                + "veiculo_id INTEGER, "
                + "data TEXT, "
                + "descricao TEXT, "
                + "valor_total REAL, "
                + "FOREIGN KEY(cliente_id) REFERENCES clientes(id), "
                + "FOREIGN KEY(veiculo_id) REFERENCES veiculos(id)"
                + ");";
        
     // Criar a tabela "historico_servicos"
        String sqlHistoricoServicos = "CREATE TABLE IF NOT EXISTS historico_servicos ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "veiculo_id INTEGER NOT NULL, "
                + "cliente_nome TEXT NOT NULL, "
                + "cliente_telefone TEXT NOT NULL, "
                + "descricao TEXT NOT NULL, "
                + "data TEXT NOT NULL, "
                + "valor REAL NOT NULL, "
                + "FOREIGN KEY(veiculo_id) REFERENCES veiculos(id)"
                + ");";




        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.execute(sqlClientes);
            stmt.execute(sqlEstoque);
            stmt.execute(sqlVeiculos); 
            stmt.execute(sqlServicos); 
            stmt.execute(sqlEmpresa);
            stmt.execute(sqlOrdemServico);
            stmt.execute(sqlHistoricoServicos);

            System.out.println("Tabelas criadas com sucesso!");
            
         // Verificar se a tabela "historico_servicos" já tem a coluna "cliente_telefone"
            ResultSet rsHistorico = stmt.executeQuery("PRAGMA table_info(historico_servicos);");
            boolean colunaTelefoneHistoricoExiste = false;

            while (rsHistorico.next()) {
                if (rsHistorico.getString("name").equals("cliente_telefone")) {
                    colunaTelefoneHistoricoExiste = true;
                    break;
                }
            }

            if (!colunaTelefoneHistoricoExiste) {
                String alterTableHistorico = "ALTER TABLE historico_servicos ADD COLUMN cliente_telefone TEXT NOT NULL DEFAULT '';";
                stmt.execute(alterTableHistorico);
                System.out.println("Coluna 'cliente_telefone' adicionada à tabela 'historico_servicos'.");
            }

            // Verificar se a tabela "veiculos" já tem as colunas "ano" e "chassi"
            ResultSet rsVeiculos = stmt.executeQuery("PRAGMA table_info(veiculos);");
            boolean colunaAnoExiste = false;
            boolean colunaChassiExiste = false;

            while (rsVeiculos.next()) {
                if (rsVeiculos.getString("name").equals("ano")) {
                    colunaAnoExiste = true;
                }
                if (rsVeiculos.getString("name").equals("chassi")) {
                    colunaChassiExiste = true;
                }
            }

            if (!colunaAnoExiste) {
                String alterTableAno = "ALTER TABLE veiculos ADD COLUMN ano VARCHAR(4) NULL;";
                stmt.execute(alterTableAno);
                System.out.println("Coluna 'ano' adicionada à tabela 'veiculos'.");
            }

            if (!colunaChassiExiste) {
                String alterTableChassi = "ALTER TABLE veiculos ADD COLUMN chassi VARCHAR(17) NULL;";
                stmt.execute(alterTableChassi);
                System.out.println("Coluna 'chassi' adicionada à tabela 'veiculos'.");
            }

            // Verificar se a tabela "servicos" já tem a coluna "preco"
            ResultSet rsServicos = stmt.executeQuery("PRAGMA table_info(servicos);");
            boolean colunaPrecoExiste = false;
            while (rsServicos.next()) {
                if (rsServicos.getString("name").equals("preco")) {
                    colunaPrecoExiste = true;
                    break;
                }
            }

            if (!colunaPrecoExiste) {
                String alterTableSQL = "ALTER TABLE servicos ADD COLUMN preco REAL NOT NULL;";
                stmt.execute(alterTableSQL);
                System.out.println("Coluna 'preco' adicionada à tabela 'servicos'.");
            }

        } catch (SQLException e) {
            System.err.println("Erro ao criar tabelas: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
