package controller;

import dao.HistoricoServicoDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.HistoricoServico;

public class HistoricoServicoController {

    @FXML
    private TextField txtBusca;

    @FXML
    private TableView<HistoricoServico> tabelaHistorico;

    @FXML
    private TableColumn<HistoricoServico, String> colunaDescricao;

    @FXML
    private TableColumn<HistoricoServico, String> colunaData;

    @FXML
    private TableColumn<HistoricoServico, String> colunaValor;

    @FXML
    private TableColumn<HistoricoServico, String> colunaModelo;

    @FXML
    private TableColumn<HistoricoServico, String> colunaPlaca;

    @FXML
    private TableColumn<HistoricoServico, String> colunaCliente;

    @FXML
    private Button btnBuscar, btnExcluir;

    private ObservableList<HistoricoServico> listaServicos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colunaDescricao.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getDescricao()));
        colunaData.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getData()));
        colunaValor.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.format("R$ %.2f", c.getValue().getValor())));
        colunaModelo.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getModeloVeiculo()));
        colunaPlaca.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getPlacaVeiculo()));
        colunaCliente.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNomeCliente()));
        tabelaHistorico.setItems(listaServicos);
    }

    @FXML
    private void buscar() {
        String termo = txtBusca.getText();
        listaServicos.setAll(HistoricoServicoDAO.buscarHistoricoPorVeiculo(termo));
    }

    @FXML
    private void excluirSelecionado() {
        HistoricoServico selecionado = tabelaHistorico.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            HistoricoServicoDAO.excluirHistorico(selecionado.getId());
            listaServicos.remove(selecionado);
        }
    }
}
