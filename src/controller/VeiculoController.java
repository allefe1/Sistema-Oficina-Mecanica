package controller;

import dao.ClienteDAO;
import dao.VeiculoDAO;
import models.Veiculo;
import models.Cliente;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class VeiculoController {

    @FXML
    private TextField modeloField;
    @FXML
    private TextField placaField;
    @FXML
    private TextField anoField;  // Novo campo
    @FXML
    private TextField chassiField;  // Novo campo
    @FXML
    private TextField searchFieldVeiculo;
    @FXML
    private TableView<Veiculo> tableViewVeiculos;
    @FXML
    private TableColumn<Veiculo, String> idColumn;  
    @FXML
    private TableColumn<Veiculo, String> modeloColumn;
    @FXML
    private TableColumn<Veiculo, String> placaColumn;
    @FXML
    private TableColumn<Veiculo, String> anoColumn;  // Nova coluna
    @FXML
    private TableColumn<Veiculo, String> chassiColumn;  // Nova coluna
    @FXML
    private TextArea modeloDetalhes;
    @FXML
    private TextArea placaDetalhes;
    @FXML
    private TextArea anoDetalhes;  // Nova área de detalhes
    @FXML
    private TextArea chassiDetalhes;  // Nova área de detalhes
    @FXML
    private TextArea donoDetalhes;

    private VeiculoDAO veiculoDAO;
    private ClienteController clienteController;  

    public VeiculoController() {
        veiculoDAO = new VeiculoDAO();
    }

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getId())));
        modeloColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getModelo()));
        placaColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPlaca()));
        anoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAno()));
        chassiColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getChassi()));

        // Converter placa para maiúsculas automaticamente
        placaField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                placaField.setText(newValue.toUpperCase());
            }
        });
        
        updateTable();
        tableViewVeiculos.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                exibirDetalhesDoVeiculo(newValue);
            }
        });
    }

    public void setClienteController(ClienteController clienteController) {
        this.clienteController = clienteController;
    }

    @FXML
    private void addVeiculo() {
        try {
            String modelo = modeloField.getText();
            String placa = placaField.getText().toUpperCase();
            String ano = anoField.getText();
            String chassi = chassiField.getText();

            Cliente clienteSelecionado = clienteController.getClienteSelecionado();
            
            if (clienteSelecionado == null) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Aviso");
                alert.setHeaderText(null);
                alert.setContentText("Por favor, selecione um cliente antes de adicionar o veículo.");
                alert.showAndWait();
                return;
            }

            if (modelo.isEmpty() || placa.isEmpty() || ano.isEmpty() || chassi.isEmpty()) {
                System.out.println("Todos os campos devem ser preenchidos.");
                return;
            }

            // Validação do ano
            try {
                int anoInt = Integer.parseInt(ano);
                if (anoInt < 1900 || anoInt > 2100) {
                    showAlert("Erro", "Ano inválido. Digite um ano entre 1900 e 2100.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Erro", "Ano inválido. Digite apenas números.");
                return;
            }

            int clienteId = clienteSelecionado.getId();
            Veiculo veiculo = new Veiculo(modelo, placa, ano, chassi, clienteId);
            veiculoDAO.addVeiculo(veiculo);
            updateTable();
            clearFields();
        } catch (Exception e) {
            System.out.println("Erro ao adicionar veículo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void removeVeiculo() {
        Veiculo veiculoSelecionado = tableViewVeiculos.getSelectionModel().getSelectedItem();
        if (veiculoSelecionado != null) {
            veiculoDAO.removeVeiculo(veiculoSelecionado.getId());
            updateTable();
        }
    }

    @FXML
    private void editarVeiculo() {
        Veiculo veiculoSelecionado = tableViewVeiculos.getSelectionModel().getSelectedItem();

        if (veiculoSelecionado != null) {
            modeloField.setText(veiculoSelecionado.getModelo());
            placaField.setText(veiculoSelecionado.getPlaca());
            anoField.setText(veiculoSelecionado.getAno());
            chassiField.setText(veiculoSelecionado.getChassi());
        } else {
            showAlert("Erro", "Nenhum veículo selecionado para edição.");
        }
    }

    @FXML
    private void salvarAlteracoes() {
        Veiculo veiculoSelecionado = tableViewVeiculos.getSelectionModel().getSelectedItem();

        if (veiculoSelecionado != null) {
            String modelo = modeloField.getText();
            String placa = placaField.getText().toUpperCase();
            String ano = anoField.getText();
            String chassi = chassiField.getText();

            if (modelo.isEmpty() || placa.isEmpty() || ano.isEmpty() || chassi.isEmpty()) {
                showAlert("Erro", "Todos os campos devem ser preenchidos.");
                return;
            }

            veiculoSelecionado.setModelo(modelo);
            veiculoSelecionado.setPlaca(placa);
            veiculoSelecionado.setAno(ano);
            veiculoSelecionado.setChassi(chassi);

            veiculoDAO.updateVeiculo(veiculoSelecionado);
            clearFields();
            updateTable();
        } else {
            showAlert("Erro", "Nenhum veículo selecionado para salvar alterações.");
        }
    }
    
    @FXML
    private TextField clienteIdField; 

    public void setClienteId(int clienteId) {
        if (clienteIdField != null) {
            clienteIdField.setText(String.valueOf(clienteId));
        } else {
            System.out.println("Erro: campo clienteIdField não está configurado.");
        }
    }
    
    private void exibirDetalhesDoVeiculo(Veiculo veiculo) {
        if (veiculo != null) {
            modeloDetalhes.setText(veiculo.getModelo());
            placaDetalhes.setText(veiculo.getPlaca());
            anoDetalhes.setText(veiculo.getAno());
            chassiDetalhes.setText(veiculo.getChassi());

            ClienteDAO clienteDAO = new ClienteDAO();
            String nomeDono = clienteDAO.getClienteNomeById(veiculo.getClienteId());
            donoDetalhes.setText(nomeDono != null ? nomeDono : "Não encontrado");
        } else {
            modeloDetalhes.setText("");
            placaDetalhes.setText("");
            anoDetalhes.setText("");
            chassiDetalhes.setText("");
            donoDetalhes.setText("");
        }
    }
    
    private void updateTable() {
        ObservableList<Veiculo> veiculos = FXCollections.observableArrayList(veiculoDAO.getVeiculos());
        tableViewVeiculos.setItems(veiculos);
    }

    private void clearFields() {
        modeloField.clear();
        placaField.clear();
        anoField.clear();
        chassiField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void searchVeiculo() {
        String searchTerm = searchFieldVeiculo.getText();
        ObservableList<Veiculo> veiculos = FXCollections.observableArrayList(veiculoDAO.searchVeiculos(searchTerm));
        tableViewVeiculos.setItems(veiculos);
    }
}