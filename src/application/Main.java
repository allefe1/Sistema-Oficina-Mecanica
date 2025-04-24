package application;

import java.io.IOException;

import controller.ClienteController;
import controller.VeiculoController;
import dao.ClienteDAO;
import dao.DatabaseSQLite;
import dao.ServicoDAO;
import dao.VeiculoDAO;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.ScrollPane;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
    	// Comentando a parte do login para testes
        /*
    	// Carregar a tela de login
        FXMLLoader loaderLogin = new FXMLLoader(getClass().getResource("/views/LoginView.fxml"));
        AnchorPane rootLogin = loaderLogin.load();
        
        Scene loginScene = new Scene(rootLogin, 400, 300);
        primaryStage.setTitle("Sistema Mecânica - Login");
        primaryStage.setScene(loginScene);
        */
    	
    	//retirar essa linha quando quiser o login
    	carregarTelaPrincipal(primaryStage);
        primaryStage.show();
    }

    public static void carregarTelaPrincipal(Stage primaryStage) {
        try {
            // Inicializa os serviços padrão
            ServicoDAO servicoDAO = new ServicoDAO();
            servicoDAO.inserirServicosPadrao();

            // Layout principal
            BorderPane root = new BorderPane();
            
            // Configurar a barra superior
            HBox topBar = createTopBar();
            root.setTop(topBar);
            
            // Container principal para o conteúdo
            HBox mainContent = createMainContent();
            
            // Scroll Pane para o conteúdo principal
            ScrollPane scrollPane = new ScrollPane(mainContent);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setId("custom-scroll-pane");
            
            root.setCenter(scrollPane);

            // Cena principal
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Main.class.getResource("application.css").toExternalForm());

            // Configurar o palco principal
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(650);
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static HBox createMainContent() throws IOException {
        // Carrega as views
        FXMLLoader loaderCliente = new FXMLLoader(Main.class.getResource("/views/ClientesView.fxml"));
        AnchorPane rootCliente = loaderCliente.load();
        ClienteController clienteController = loaderCliente.getController();

        FXMLLoader loaderVeiculo = new FXMLLoader(Main.class.getResource("/views/VeiculosView.fxml"));
        AnchorPane rootVeiculo = loaderVeiculo.load();
        VeiculoController veiculoController = loaderVeiculo.getController();

        veiculoController.setClienteController(clienteController);

        // Container para os cards
        VBox cardsContainer = createCardsContainer();
        
        // Container principal
        HBox mainContent = new HBox(10);
        mainContent.getChildren().addAll(rootCliente, rootVeiculo, cardsContainer);
        mainContent.setAlignment(Pos.CENTER_LEFT);
        mainContent.setPadding(new Insets(3));
        
        return mainContent;
    }

    private static VBox createCardsContainer() {
        // Obtém os dados
        ClienteDAO clienteDAO = new ClienteDAO();
        VeiculoDAO veiculoDAO = new VeiculoDAO();
        int totalClientes = clienteDAO.getClientes().size();
        int totalVeiculos = veiculoDAO.getVeiculos().size();

        // Criar os cards
        VBox cardsContainer = new VBox(20);
        cardsContainer.getChildren().addAll(
            createCardWithImage("Clientes", totalClientes, "/clientes.png"),
            createCardWithImage("Veículos", totalVeiculos, "/veiculos.png")
        );
        cardsContainer.setAlignment(Pos.TOP_CENTER);
        cardsContainer.setPadding(new Insets(70, 20, 0, 70));
        
        return cardsContainer;
    }

    private static HBox createTopBar() {
        // Logo
        ImageView logoView = new ImageView(new Image(Main.class.getResourceAsStream("/logo.png")));
        logoView.setFitWidth(100);
        logoView.setFitHeight(100);
        logoView.setPreserveRatio(true);

        // Botões
        Button openOrdemServicoButton = createTopBarButton("Abrir Ordem de Serviço", e -> openOrdemServicoWindow());
        Button openEstoqueButton = createTopBarButton("Abrir Controle de Estoque", e -> openEstoqueWindow());
        Button historicoVeiculoButton = createTopBarButton("Histórico de Veículos", e -> openHistoricoVeiculoWindow());

        // Botão de usuário
        Button userButton = createUserButton();

        // Espaçador
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Container da barra superior
        HBox topBar = new HBox(20);
        topBar.getChildren().addAll(
            logoView,
            spacer,
            openOrdemServicoButton,
            openEstoqueButton,
            historicoVeiculoButton,
            userButton
        );
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10, 20, 10, 20));
        topBar.setStyle("-fx-background-color: #3a414d;");

        return topBar;
    }


    
    private static void openHistoricoVeiculoWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/views/HistoricoServicoView.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(Main.class.getResource("application.css").toExternalForm());

            Stage stage = new Stage();
            stage.setTitle("Histórico de Serviços por Veículo");
            stage.setScene(scene);
            stage.setMaximized(true); // tela cheia
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private static Button createTopBarButton(String text, EventHandler<ActionEvent> action) {
        Button button = new Button(text);
        button.setOnAction(action);
        button.getStyleClass().add("top-bar-button");
        return button;
    }

    private static Button createUserButton() {
        ImageView userIconView = new ImageView(new Image(Main.class.getResourceAsStream("/user_icon.png")));
        userIconView.setFitWidth(30);
        userIconView.setFitHeight(30);

        Button userButton = new Button("", userIconView);
        userButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        userButton.setOnAction(e -> openEmpresaWindow());
        
        return userButton;
    }

    private static StackPane createCardWithImage(String title, int count, String imagePath) {
        // Configuração do conteúdo do card (título, contador, ícone, etc.)
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("title-label");

        Label countLabel = new Label(String.valueOf(count));
        countLabel.getStyleClass().add("count-label");

        ImageView icon = new ImageView(new Image(Main.class.getResourceAsStream(imagePath)));
        // Ajusta os tamanhos do ícone conforme o título
        icon.setFitWidth(title.equals("Clientes") ? 49 : 42);
        icon.setFitHeight(title.equals("Clientes") ? 49 : 42);
        icon.setPreserveRatio(true);

        StackPane iconContainer = new StackPane(icon);
        iconContainer.setAlignment(Pos.CENTER_RIGHT);
        iconContainer.setPadding(new Insets(0, 10, 0, 0));

        HBox topContent = new HBox(titleLabel, iconContainer);
        topContent.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(iconContainer, Priority.ALWAYS);

        VBox cardContent = new VBox(10);
        cardContent.getChildren().addAll(topContent, countLabel);
        cardContent.setAlignment(Pos.TOP_LEFT);
        cardContent.setPadding(new Insets(15));

        // Cria o container do card
        StackPane cardContainer = new StackPane(cardContent);
        cardContainer.setId("card-container");
        
        // Em vez de definir tamanhos fixos, adicionamos um listener para fazer binding assim que a cena estiver disponível
        cardContainer.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                // Define que o card terá 25% da largura da cena e 15% da altura da cena (exemplo)
                cardContainer.prefWidthProperty().bind(newScene.widthProperty().multiply(0.15));
                cardContainer.maxWidthProperty().bind(newScene.widthProperty().multiply(0.20));
                cardContainer.prefHeightProperty().bind(newScene.heightProperty().multiply(0.15));
                cardContainer.maxHeightProperty().bind(newScene.heightProperty().multiply(0.15));
            }
        });
        
        return cardContainer;
    }


    public static void openEstoqueWindow() {
        try {
            FXMLLoader loaderEstoque = new FXMLLoader(Main.class.getResource("/views/EstoqueView.fxml"));
            AnchorPane rootEstoque = loaderEstoque.load();
            Stage estoqueStage = new Stage();
            estoqueStage.setTitle("Controle de Estoque");

            Scene estoqueScene = new Scene(rootEstoque);
            estoqueScene.getStylesheets().add(Main.class.getResource("application.css").toExternalForm());

            estoqueStage.setScene(estoqueScene);

            // Abrir em tela cheia
            estoqueStage.setMaximized(true);

            estoqueStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openEmpresaWindow() {
        try {
            FXMLLoader loaderEmpresa = new FXMLLoader(Main.class.getResource("/views/EmpresaView.fxml"));
            AnchorPane rootEmpresa = loaderEmpresa.load();
            Stage empresaStage = new Stage();
            empresaStage.setTitle("Informações da Empresa");

            Scene empresaScene = new Scene(rootEmpresa, 400, 300);
            empresaScene.getStylesheets().add(Main.class.getResource("application.css").toExternalForm());

            empresaStage.setScene(empresaScene);

            // Exibir a janela centralizada
            empresaStage.centerOnScreen();
            empresaStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openServicoWindow() {
        try {
            FXMLLoader loaderServico = new FXMLLoader(Main.class.getResource("/views/ServicosView.fxml"));
            AnchorPane rootServico = loaderServico.load();
            Stage servicoStage = new Stage();
            servicoStage.setTitle("Gerenciamento de Serviços");

            Scene servicoScene = new Scene(rootServico);
            servicoScene.getStylesheets().add(Main.class.getResource("application.css").toExternalForm());

            servicoStage.setScene(servicoScene);

            // Abrir em tela cheia
            servicoStage.setMaximized(true);

            servicoStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void openOrdemServicoWindow() {
        try {
            FXMLLoader loaderOrdemServico = new FXMLLoader(Main.class.getResource("/views/OrdemServicoView.fxml"));
            
            // Alteração principal: Carrega o ScrollPane como raiz
            ScrollPane scrollRoot = loaderOrdemServico.load();
            
            Stage ordemServicoStage = new Stage();
            ordemServicoStage.setTitle("Gerenciamento de Ordem de Serviço");

            // Cena recebe o ScrollPane como nó raiz
            Scene ordemServicoScene = new Scene(scrollRoot); 
            ordemServicoScene.getStylesheets().add(Main.class.getResource("application.css").toExternalForm());

            ordemServicoStage.setScene(ordemServicoScene);
            ordemServicoStage.setMaximized(true);
            ordemServicoStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        DatabaseSQLite.createTables();
        launch(args);
    }
}
