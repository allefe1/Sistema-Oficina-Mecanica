package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Empresa;

public class EmpresaViewController {
    @FXML private TextField nomeField;
    @FXML private TextField cpfCnpjField;
    @FXML private TextField telefoneField;
    @FXML private TextField enderecoField;

    private EmpresaController empresaController;

    public EmpresaViewController() {
        empresaController = new EmpresaController();
    }

    @FXML
    public void initialize() {
        Empresa empresa = empresaController.buscarEmpresa();
        if (empresa != null) {
            nomeField.setText(empresa.getNome());
            cpfCnpjField.setText(empresa.getCpfCnpj());
            telefoneField.setText(empresa.getTelefone());
            enderecoField.setText(empresa.getEndereco());
        }
    }

 
 // Método para salvar os dados da empresa
    @FXML
    private void salvarEmpresa() {
        // Recuperar os dados dos campos
        String nome = nomeField.getText();
        String cpfCnpj = cpfCnpjField.getText();
        String telefone = telefoneField.getText();
        String endereco = enderecoField.getText();

        // Buscar a empresa existente
        Empresa empresa = empresaController.buscarEmpresa();

        if (empresa == null) {
            // Se a empresa não existir, criar uma nova
            empresa = new Empresa();
        }

        // Atualizar os dados da empresa
        empresa.setNome(nome);
        empresa.setCpfCnpj(cpfCnpj);
        empresa.setTelefone(telefone);
        empresa.setEndereco(endereco);

        // Salvar ou atualizar no banco de dados
        empresaController.salvarEmpresa(empresa);

        // Exibir mensagem de sucesso
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText("Empresa salva com sucesso!");
        alert.showAndWait();

        // Fechar a janela
        Stage stage = (Stage) nomeField.getScene().getWindow();
        stage.close();
    }

}

