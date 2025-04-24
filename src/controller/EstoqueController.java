package controller;

import java.util.List;
import java.util.stream.Collectors;

import dao.EstoqueDAO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import models.Estoque;

public class EstoqueController {

    @FXML private TextField txtNomePeca;
    @FXML private TextField txtQuantidade;
    @FXML private TextField txtPreco;

    @FXML private AnchorPane estoqueWindow;

    @FXML private TableView<Estoque> tableViewEstoque;
    @FXML private TableColumn<Estoque, Integer> colId;
    @FXML private TableColumn<Estoque, String> colNome;
    @FXML private TableColumn<Estoque, Integer> colQuantidade;
    @FXML private TableColumn<Estoque, Double> colPreco;

    private EstoqueDAO estoqueDAO = new EstoqueDAO();

    @FXML
    public void initialize() {
        configurarTabela();
        carregarDadosTabela();
        configurarEventos();
    }

    private void configurarTabela() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));

        tableViewEstoque.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        
        // Completely reset selection model to prevent errors
        tableViewEstoque.getSelectionModel().clearSelection();
        
        // Use Platform.runLater to ensure thread-safe UI updates
        tableViewEstoque.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                Platform.runLater(() -> {
                    if (newSelection != null && !tableViewEstoque.getItems().isEmpty()) {
                        selecionarItemTabela(newSelection);
                    }
                });
            }
        );
    }

    // Modify selecionarItemTabela to be more defensive
    private void selecionarItemTabela(Estoque peca) {
        if (peca != null && tableViewEstoque.getItems().contains(peca)) {
            txtNomePeca.setText(peca.getNome());
            txtQuantidade.setText(String.valueOf(peca.getQuantidade()));
            txtPreco.setText(String.valueOf(peca.getPreco()));
        }
    }

    private void configurarEventos() {
        // Remove previous text listener that was auto-loading details
        txtNomePeca.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                carregarDadosTabela();
                txtQuantidade.clear();
                txtPreco.clear();
            }
        });

        // Restore click to load details
        tableViewEstoque.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    selecionarItemTabela(newSelection);
                }
            }
        );
    }

    private void carregarDadosTabela() {
        tableViewEstoque.getItems().setAll(estoqueDAO.buscarPecas());
    }

    @FXML
    private void cadastrarPeca() {
        String nome = txtNomePeca.getText().trim();
        if (nome.isEmpty() || txtQuantidade.getText().isEmpty() || txtPreco.getText().isEmpty()) {
            showAlert("Erro", "Preencha todos os campos corretamente!");
            return;
        }

        try {
            int quantidade = Integer.parseInt(txtQuantidade.getText());
            double preco = Double.parseDouble(txtPreco.getText());

            if (quantidade < 0 || preco < 0) {
                showAlert("Erro", "Quantidade e preço devem ser valores positivos!");
                return;
            }

            Estoque peca = new Estoque(nome, quantidade, preco);
            estoqueDAO.cadastrarPeca(peca);

            txtNomePeca.clear();
            txtQuantidade.clear();
            txtPreco.clear();

            showAlert("Sucesso", "Peça cadastrada com sucesso!");
            carregarDadosTabela();
        } catch (NumberFormatException e) {
            showAlert("Erro", "Quantidade e preço devem ser números válidos!");
        }
    }

    @FXML
    private void buscarPeca() {
        String nomePesquisa = txtNomePeca.getText().trim().toLowerCase();

        if (nomePesquisa.isEmpty()) {
            carregarDadosTabela();
            return;
        }

        List<Estoque> pecasFiltradas = estoqueDAO.buscarPecas().stream()
            .filter(peca -> peca.getNome().toLowerCase().contains(nomePesquisa))
            .collect(Collectors.toList());

        if (!pecasFiltradas.isEmpty()) {
            tableViewEstoque.getItems().setAll(pecasFiltradas);
            
            // Quando só há um item, carrega automaticamente os detalhes
            if (pecasFiltradas.size() == 1) {
                Estoque peca = pecasFiltradas.get(0);
                txtQuantidade.setText(String.valueOf(peca.getQuantidade()));
                txtPreco.setText(String.valueOf(peca.getPreco()));
            }
        } else {
            showAlert("Aviso", "Nenhuma peça encontrada com esse nome!");
        }
    }

    @FXML
    private void atualizarQuantidade() {
        String nome = txtNomePeca.getText().trim();
        if (nome.isEmpty()) {
            showAlert("Erro", "Digite o nome da peça!");
            return;
        }

        try {
            int novaQuantidade = Integer.parseInt(txtQuantidade.getText());
            double novoPreco = Double.parseDouble(txtPreco.getText());

            if (novaQuantidade < 0 || novoPreco < 0) {
                showAlert("Erro", "Quantidade e preço devem ser valores positivos!");
                return;
            }

            Estoque peca = estoqueDAO.buscarPecaPorNome(nome);
            if (peca == null) {
                showAlert("Erro", "Peça não encontrada!");
                return;
            }

            int quantidadeAtual = peca.getQuantidade();
            double precoAtual = peca.getPreco();

            boolean quantidadeAlterada = novaQuantidade != quantidadeAtual;
            boolean precoAlterado = novoPreco != precoAtual;

            if (!quantidadeAlterada && !precoAlterado) {
                showAlert("Aviso", "Nenhuma alteração foi feita.");
                return;
            }

            // Atualizar a quantidade se mudou
            if (quantidadeAlterada) {
                if (novaQuantidade > quantidadeAtual) {
                    int diferenca = novaQuantidade - quantidadeAtual;
                    estoqueDAO.aumentarQuantidade(peca.getId(), diferenca);
                } else {
                    int diferenca = quantidadeAtual - novaQuantidade;
                    estoqueDAO.diminuirQuantidade(peca.getId(), diferenca);
                }
            }

            // Atualizar o preço se mudou
            if (precoAlterado) {
                estoqueDAO.atualizarPreco(peca.getId(), novoPreco);
            }

            showAlert("Sucesso", "Peça atualizada com sucesso!");
            carregarDadosTabela();

        } catch (NumberFormatException e) {
            showAlert("Erro", "Quantidade e preço devem ser números válidos!");
        }
    }

    @FXML
    private void removerPeca() {
        Estoque selecionada = tableViewEstoque.getSelectionModel().getSelectedItem();

        if (selecionada == null) {
            showAlert("Erro", "Selecione uma peça na tabela!");
            return;
        }

        estoqueDAO.removerPeca(selecionada.getId());
        tableViewEstoque.getItems().remove(selecionada);
        tableViewEstoque.refresh();
        showAlert("Sucesso", "Peça removida com sucesso!");
    }

    private void showAlert(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}