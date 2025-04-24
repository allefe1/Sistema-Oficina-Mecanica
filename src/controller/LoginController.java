package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import application.Main;
import dao.UsuarioDAO;
import javafx.application.Platform; // Importando Platform

public class LoginController {

    @FXML
    private TextField usuarioField;
    
    @FXML
    private Label tituloLabel;
    
    @FXML
    private PasswordField senhaField;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private ProgressIndicator progressIndicator; // Adicionado o ProgressIndicator

    @FXML
    public void handleLogin() {
        String usuario = usuarioField.getText();
        String senha = senhaField.getText();

        // Exibe o indicador de progresso
        progressIndicator.setVisible(true);

        // Cria uma nova thread para processar o login sem travar a interface gráfica
        new Thread(() -> {
            // Simulando um processo de login com a verificação de credenciais
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            boolean sucesso = usuarioDAO.verificarCredenciais(usuario, senha);

            // Após o processo de login, esconde o indicador de progresso
            Platform.runLater(() -> progressIndicator.setVisible(false)); // Atualiza o UI na thread de eventos

            // Se o login for bem-sucedido
            if (sucesso) {
                // Carrega a próxima tela
                Platform.runLater(() -> {
                    Stage stage = (Stage) loginButton.getScene().getWindow(); // Obtendo o Stage atual
                    Main.carregarTelaPrincipal(stage); // Passando o Stage para carregar a tela principal
                });
            } else {
                // Se o login falhar
                Platform.runLater(() -> {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Erro de Login");
                    alert.setHeaderText(null);
                    alert.setContentText("Usuário ou senha incorretos, ou status não pago.");
                    alert.showAndWait();
                });
            }
        }).start();
    }
}
