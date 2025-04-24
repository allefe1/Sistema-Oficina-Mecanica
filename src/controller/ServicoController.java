package controller;

import dao.ServicoDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Servico;

public class ServicoController {

    @FXML
    private TableView<Servico> tableViewServicos;
    @FXML
    private TableColumn<Servico, String> descricaoColumn;
    @FXML
    private TableColumn<Servico, String> precoColumn;
    @FXML
    private TableColumn<Servico, String> dataColumn;
    @FXML
    private TableColumn<Servico, String> veiculoIdColumn;

    @FXML
    private TextField descricaoField;
    @FXML
    private TextField precoField;
    @FXML
    private TextField dataField;
    @FXML
    private TextField veiculoIdField;

    private ServicoDAO servicoDAO;

    @FXML
    public void initialize() {
        servicoDAO = new ServicoDAO();

        descricaoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescricao()));
        precoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getPreco())));
        dataColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getData()));
        veiculoIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getVeiculoId())));

        updateTable();  // Chama o método para carregar os dados corretos
    }

    @FXML
    private void addServico() {
        String descricao = descricaoField.getText();
        String precoText = precoField.getText();
        String data = dataField.getText();
        String veiculoIdText = veiculoIdField.getText();

        if (descricao.isEmpty() || precoText.isEmpty() || data.isEmpty() || veiculoIdText.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Campos Obrigatórios");
            alert.setHeaderText("Preencha todos os campos");
            alert.setContentText("Por favor, preencha todos os campos para adicionar um serviço.");
            alert.showAndWait();
            return;
        }

        try {
            double preco = Double.parseDouble(precoText);
            int veiculoId = Integer.parseInt(veiculoIdText);

            Servico servico = new Servico(descricao, preco, data, veiculoId);
            servicoDAO.addServico(servico);
            updateTable();

            descricaoField.clear();
            precoField.clear();
            dataField.clear();
            veiculoIdField.clear();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro de Formato");
            alert.setHeaderText("Formato inválido");
            alert.setContentText("Preço deve ser um número decimal e ID do veículo deve ser um número inteiro.");
            alert.showAndWait();
        }
    }

    @FXML
    private void removeServico() {
        Servico servicoSelecionado = tableViewServicos.getSelectionModel().getSelectedItem();
        if (servicoSelecionado != null) {
            servicoDAO.removeServico(servicoSelecionado.getId());
            updateTable();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Nenhum Serviço Selecionado");
            alert.setHeaderText("Selecione um serviço para remover");
            alert.setContentText("Por favor, selecione um serviço da tabela.");
            alert.showAndWait();
        }
    }
    
    @FXML
    private Button btnFechar; 

    // Método para fechar a janela
    public void fecharAbaServicos() {
        Stage stage = (Stage) (btnFechar.getScene().getWindow()); 
        stage.close(); // Fecha a janela de serviços
    }

    // Método que atualiza a tabela com os serviços associados (veiculo_id não nulo)
    private void updateTable() {
        ObservableList<Servico> servicos = FXCollections.observableArrayList(servicoDAO.getServicosAssociados());
        tableViewServicos.setItems(servicos);
    }
}
