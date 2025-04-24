package controller;

import javafx.scene.control.TextArea;
import java.util.List;

import dao.ClienteDAO;
import dao.ServicoDAO;
import dao.VeiculoDAO;
import models.Cliente;
import models.Servico;
import models.Veiculo;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ClienteController {
	
	
    @FXML
    private TextField nomeField;
    @FXML
    private TextField telefoneField;
    @FXML
    private TextField enderecoField;
    @FXML
    private TextField searchFieldCliente;
    @FXML
    private Button addButton;
    @FXML
    private TextArea clienteDetalhes;
    @FXML
    private TextArea veiculosDetalhes;
    @FXML
    private TextArea servicosDetalhes;
    @FXML
    private TableView<Cliente> table;
    @FXML
    private TableColumn<Cliente, String> nomeColumn;
    @FXML
    private TableColumn<Cliente, String> telefoneColumn;

    private ClienteDAO clienteDAO;
    private VeiculoController veiculoController; // Referência ao VeiculoController

    public ClienteController() {
        clienteDAO = new ClienteDAO();
    }

    // Inicialização do controlador
    @FXML
    public void initialize() {
        if (nomeField == null || telefoneField == null || enderecoField == null || table == null) {
            System.err.println("Erro: Certifique-se de que todos os componentes FXML estão devidamente configurados.");
            return;
        }

        nomeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNome()));
        telefoneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTelefone()));

        // Listener para seleção de cliente na tabela
        table.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Cliente>() {
            @Override
            public void changed(ObservableValue<? extends Cliente> observable, Cliente oldValue, Cliente newValue) {
                if (newValue != null) {
                    
                    if (veiculoController != null) {
                        veiculoController.setClienteId(newValue.getId());
                    }
                }
            }
        });

        //listener de duplo clique para mostrar os detalhes do cliente
        table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { 
                Cliente clienteSelecionado = getClienteSelecionado();
                if (clienteSelecionado != null) {
                   
                    VeiculoDAO veiculoDAO = new VeiculoDAO();
                    ServicoDAO servicoDAO = new ServicoDAO();

                    
                    List<Veiculo> veiculos = veiculoDAO.getVeiculosByCliente(clienteSelecionado.getId());
                    List<Servico> servicos = servicoDAO.getServicosByClienteId(clienteSelecionado.getId());

                    // Exibir os detalhes na interface
                    displayClienteDetalhado(clienteSelecionado, veiculos, servicos);
                }
            }
        });

        updateTable();
    }
    
    public Cliente getClienteSelecionado() {
        return table.getSelectionModel().getSelectedItem();
    }

    // Adicionar cliente
    @FXML
    private void addCliente() {
        String nome = nomeField.getText();
        String telefone = telefoneField.getText();
        String endereco = enderecoField.getText();

        if (nome.isEmpty() || telefone.isEmpty() || endereco.isEmpty()) {
            showAlert("Erro", "Todos os campos devem ser preenchidos.");
            return;
        }

        Cliente cliente = new Cliente(nome, telefone, endereco);
        clienteDAO.addCliente(cliente);
        clearFields();
        updateTable();
    }

   
    @FXML
    private void removeCliente() {
        Cliente clienteSelecionado = table.getSelectionModel().getSelectedItem();

        if (clienteSelecionado != null) {
            clienteDAO.removeCliente(clienteSelecionado.getId());
            updateTable();
        } else {
            showAlert("Erro", "Nenhum cliente selecionado para remoção.");
        }
    }

    
    @FXML
    private void editCliente() {
        Cliente clienteSelecionado = table.getSelectionModel().getSelectedItem();

        if (clienteSelecionado != null) {
            nomeField.setText(clienteSelecionado.getNome());
            telefoneField.setText(clienteSelecionado.getTelefone());
            enderecoField.setText(clienteSelecionado.getEndereco());
        } else {
            showAlert("Erro", "Nenhum cliente selecionado para edição.");
        }
    }

   
    @FXML
    private void salvarAtualizacao() {
        Cliente clienteSelecionado = table.getSelectionModel().getSelectedItem();

        if (clienteSelecionado != null) {
            String nome = nomeField.getText();
            String telefone = telefoneField.getText();
            String endereco = enderecoField.getText();

            if (nome.isEmpty() || telefone.isEmpty() || endereco.isEmpty()) {
                showAlert("Erro", "Todos os campos devem ser preenchidos.");
                return;
            }

            clienteSelecionado.setNome(nome);
            clienteSelecionado.setTelefone(telefone);
            clienteSelecionado.setEndereco(endereco);

            clienteDAO.updateCliente(clienteSelecionado);
            clearFields();
            updateTable();
        } else {
            showAlert("Erro", "Nenhum cliente selecionado para salvar alterações.");
        }
    }

    @FXML
    private void searchCliente() {
        String query = searchFieldCliente.getText().trim();

        if (query.isEmpty()) {
            updateTable();
            return;
        }

        // Buscar clientes pelo nome ou telefone
        List<Cliente> clientesEncontrados = clienteDAO.getClientesByNameOrPhone(query);

        if (!clientesEncontrados.isEmpty()) {
            
            ObservableList<Cliente> clientesObservable = FXCollections.observableArrayList(clientesEncontrados);
            table.setItems(clientesObservable);

            
            Cliente clienteSelecionado = clientesEncontrados.get(0);

          
            VeiculoDAO veiculoDAO = new VeiculoDAO();
            ServicoDAO servicoDAO = new ServicoDAO();

          
            List<Veiculo> veiculos = veiculoDAO.getVeiculosByCliente(clienteSelecionado.getId());
            List<Servico> servicos = servicoDAO.getServicosByClienteId(clienteSelecionado.getId());

         
            displayClienteDetalhado(clienteSelecionado, veiculos, servicos);
        } else {
            // Caso nenhum cliente seja encontrado, limpar a tabela e os detalhes
            table.setItems(FXCollections.observableArrayList());
            clearDetalhes();
        }
    }
    
 // Método para limpar os detalhes do cliente 
    private void clearDetalhes() {
        clienteDetalhes.setText("");
        veiculosDetalhes.setText("");
        servicosDetalhes.setText("");
    }
    
    public void displayClienteDetalhado(Cliente cliente, List<Veiculo> veiculos, List<Servico> servicos) {
        
        clienteDetalhes.setText("Nome: " + cliente.getNome() + "\n");
        clienteDetalhes.appendText("Telefone: " + cliente.getTelefone() + "\n");
        clienteDetalhes.appendText("Endereço: " + cliente.getEndereco() + "\n");

   
        System.out.println("Veículos do Cliente:");
        StringBuilder veiculosText = new StringBuilder();
        for (Veiculo veiculo : veiculos) {
            if (veiculo.getClienteId() == cliente.getId()) {
                veiculosText.append(" - ").append(veiculo.getModelo()).append(" (").append(veiculo.getPlaca()).append(")\n");
            }
        }
        veiculosDetalhes.setText(veiculosText.toString());

  
        System.out.println("Serviços realizados:");
        StringBuilder servicosText = new StringBuilder();
        for (Veiculo veiculo : veiculos) {
            if (veiculo.getClienteId() == cliente.getId()) {  
                for (Servico servico : servicos) {
                    if (servico.getVeiculoId() == veiculo.getId()) {  
                        servicosText.append(" - ").append(servico.getDescricao()).append(" | Preço: R$")
                                .append(servico.getPreco()).append("\n");
                    }
                }
            }
        }
        servicosDetalhes.setText(servicosText.toString());
    }

    // Atualizar tabela
    private void updateTable() {
        ObservableList<Cliente> clientes = FXCollections.observableArrayList(clienteDAO.getClientes());
        System.out.println("Número de clientes: " + clientes.size()); 
        table.setItems(clientes);
    }

    
    private void clearFields() {
        nomeField.clear();
        telefoneField.clear();
        enderecoField.clear();
    }

    
    public void setVeiculoController(VeiculoController veiculoController) {
        this.veiculoController = veiculoController;
    }

   
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
