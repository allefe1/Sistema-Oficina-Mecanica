package controller;

import dao.HistoricoServicoDAO;
import dao.OrdemServicoDAO;
import dao.VeiculoDAO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import models.Cliente;
import models.HistoricoServico;
import models.OrdemServico;
import models.Servico;
import models.Veiculo;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import javafx.scene.control.Alert;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;

public class OrdemServicoController {

    @FXML
    private ComboBox<String> servicosComboBox;

    @FXML
    private Label empresaNomeLabel, empresaCnpjLabel, empresaTelefoneLabel, empresaEnderecoLabel, totalLabel;

    @FXML
    private TextField buscarClienteField, buscarVeiculoField, buscarServicoField;

    @FXML
    private ListView<String> listaClientes, listaVeiculos;
    
    @FXML
    private ListView<String> listaServicos;

    @FXML
    private TableView<Servico> servicosTableView;

    @FXML
    private TableColumn<Servico, String> servicoColumn;

    @FXML
    private TableColumn<Servico, Double> valorColumn;
    
    @FXML
    private TableColumn<Servico, Integer> quantidadeColumn;
    
    @FXML
    private Button btnNovoServico; // Adicione este botão no seu FXML
    
    @FXML
    private TextField descontoField; // Campo para entrada do valor de desconto

    @FXML
    private ComboBox<String> tipoDescontoComboBox; // Tipo de desconto (percentual ou valor)

    @FXML
    private Label subtotalLabel; // Novo label para mostrar o subtotal antes do desconto
    
    @FXML
    private RadioButton radioSim;

    @FXML
    private RadioButton radioNao;

    
    private ToggleGroup historicoToggle = new ToggleGroup();
    
    private Veiculo veiculoSelecionado;
    
    private Cliente clienteSelecionado;




    
    private ContextMenu servicosContextMenu;

    private ObservableList<Servico> servicosList = FXCollections.observableArrayList();
    private ObservableList<String> clientesList = FXCollections.observableArrayList();
    private ObservableList<String> veiculosList = FXCollections.observableArrayList();
    private ObservableList<String> servicosDisponiveisList = FXCollections.observableArrayList();

    private OrdemServico ordemServico;

    public void initialize() {
        ordemServico = new OrdemServico();
        carregarDadosEmpresa();
        carregarClientes();
        carregarVeiculos();
        carregarServicos();
        configurarTabelaServicos();
        
        configurarMenuContextoServicos();
        
        radioSim.setToggleGroup(historicoToggle);
        radioNao.setToggleGroup(historicoToggle);
        
        int ultimoNumero = OrdemServicoDAO.buscarUltimoNumeroOrdem();
        int novoNumero = ultimoNumero + 1;
        ordemServico.setNumeroOrdem(novoNumero);
        
        // Inicializar componentes de desconto
        tipoDescontoComboBox.getItems().addAll("Percentual (%)", "Valor (R$)");
        tipoDescontoComboBox.setValue("Percentual (%)");
        
        // Usar TextFormatter para controlar a entrada de dados de maneira mais robusta
        UnaryOperator<TextFormatter.Change> decimalFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty()) {
                return change;
            }
            
            // Permitir apenas dígitos e no máximo um ponto decimal
            if (newText.matches("\\d*\\.?\\d*")) {
                return change;
            }
            
            return null;
        };
        
        descontoField.setTextFormatter(new TextFormatter<>(decimalFilter));
        
        // Resto do código de inicialização...
        buscarClienteField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                listaClientes.setItems(clientesList);
            } else {
                filtrarClientes();
            }
        });
        
        // Adiciona o listener para o campo de busca de veículos
        buscarVeiculoField.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarVeiculos();
        });
        
        // Adiciona o listener para o campo de busca de serviços
        buscarServicoField.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarServicos();
        });
    }
    
    private double subtotal = 0.0;
    private double valorDesconto = 0.0;

    

    private void carregarDadosEmpresa() {
        String dadosEmpresa = OrdemServicoDAO.buscarDadosEmpresa();
        if (dadosEmpresa != null) {
            String[] campos = dadosEmpresa.split(";");
            ordemServico.setNomeEmpresa(campos[0]);
            ordemServico.setCnpjEmpresa(campos[1]);
            ordemServico.setTelefoneEmpresa(campos[2]);
            ordemServico.setEnderecoEmpresa(campos[3]);

            empresaNomeLabel.setText(campos[0]);
            empresaCnpjLabel.setText(campos[1]);
            empresaTelefoneLabel.setText(campos[2]);
            empresaEnderecoLabel.setText(campos[3]);
        }
    }
    
    

    private void carregarClientes() {
        List<String> clientes = OrdemServicoDAO.buscarClientes();
        clientesList.setAll(clientes);
        listaClientes.setItems(clientesList);
    }

    private void carregarVeiculos() {
        List<String> veiculos = OrdemServicoDAO.buscarVeiculos();
        veiculosList.setAll(veiculos);
        listaVeiculos.setItems(veiculosList);
    }

    private void carregarServicos() {
        List<String> servicos = OrdemServicoDAO.buscarServicos();
        servicosDisponiveisList.setAll(servicos);
        listaServicos.setItems(servicosDisponiveisList); // Atualiza a ListView de serviços
    }

    private void configurarTabelaServicos() {
        servicoColumn.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        valorColumn.setCellValueFactory(new PropertyValueFactory<>("preco"));
        quantidadeColumn.setCellValueFactory(new PropertyValueFactory<>("quantidade"));

        // Ajustando as larguras proporcionalmente
        servicoColumn.prefWidthProperty().bind(servicosTableView.widthProperty().multiply(0.6));   // Aumentando para 60% da largura
        valorColumn.prefWidthProperty().bind(servicosTableView.widthProperty().multiply(0.15));    // Diminui para 15% da largura
        quantidadeColumn.prefWidthProperty().bind(servicosTableView.widthProperty().multiply(0.25)); // 25% permanece

        // Make quantity column editable
        quantidadeColumn.setCellFactory(TextFieldTableCell.forTableColumn(new javafx.util.converter.IntegerStringConverter()));
        quantidadeColumn.setOnEditCommit(event -> {
            Servico servico = event.getRowValue();
            servico.setQuantidade(event.getNewValue());
            atualizarTotal();
        });

        // Make price column editable
        valorColumn.setCellFactory(TextFieldTableCell.forTableColumn(new javafx.util.converter.DoubleStringConverter()));
        valorColumn.setOnEditCommit(event -> {
            Servico servico = event.getRowValue();
            servico.setPreco(event.getNewValue());
            atualizarTotal();
        });

        servicosTableView.setItems(servicosList);
        servicosTableView.setEditable(true);
        servicosTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    @FXML
    private void filtrarClientes() {
        String filtro = buscarClienteField.getText().toLowerCase();
        ObservableList<String> filtrados = FXCollections.observableArrayList();

        for (String cliente : clientesList) {
            if (cliente.toLowerCase().contains(filtro)) {
                filtrados.add(cliente);
            }
        }
        listaClientes.setItems(filtrados);  // Atualiza apenas a lista filtrada
    }

    @FXML
    private void filtrarVeiculos() {
        // Verifica se há um cliente selecionado
        if (ordemServico.getCliente() != null) {
            String filtro = buscarVeiculoField.getText().toLowerCase();  // Pega o texto digitado na busca
            ObservableList<String> filtrados = FXCollections.observableArrayList();  // Lista filtrada
            
            // Filtra os veículos que pertencem ao cliente e contém o texto digitado
            for (String veiculo : veiculosList) {
                // Supondo que veiculosList armazene os veículos com um formato adequado, 
                // podemos verificar se o veículo pertence ao cliente
                if (veiculo.toLowerCase().contains(filtro) && veiculo.contains(ordemServico.getCliente())) {
                    filtrados.add(veiculo);  // Adiciona veículos que correspondem ao filtro e ao cliente
                }
            }

            listaVeiculos.setItems(filtrados);  // Atualiza a lista de veículos exibidos
        } else {
            // Se não há cliente selecionado, exibe todos os veículos
            listaVeiculos.setItems(veiculosList);
        }
    }

    @FXML
    private void filtrarServicos() {
        String filtro = buscarServicoField.getText().toLowerCase();
        ObservableList<String> filtrados = FXCollections.observableArrayList();

        // Usar um Set para evitar duplicatas
        Set<String> unicos = new HashSet<>();

        for (String servico : servicosDisponiveisList) {
            if (servico.toLowerCase().contains(filtro)) {
                unicos.add(servico); // Adiciona ao Set (ignora duplicatas)
            }
        }

        filtrados.addAll(unicos); // Adiciona os serviços únicos à lista
        listaServicos.setItems(filtrados); // Atualiza a lista filtrada de serviços
    }
    
    @FXML
    private void selecionarCliente() {
        String clienteNome = listaClientes.getSelectionModel().getSelectedItem();
        if (clienteNome != null) {
            String[] dadosCliente = OrdemServicoDAO.buscarDadosCompletos(clienteNome);
            if (dadosCliente != null) {
                clienteSelecionado = new Cliente();
                clienteSelecionado.setNome(dadosCliente[0]);
                clienteSelecionado.setEndereco(dadosCliente[1]);
                clienteSelecionado.setTelefone(dadosCliente[2]);

                ordemServico.setCliente(dadosCliente[0]);
                ordemServico.setEndereco(dadosCliente[1]);
                ordemServico.setTelefone(dadosCliente[2]);
            }

            buscarClienteField.setText(clienteNome);
            listaClientes.getItems().clear();

            int clienteId = OrdemServicoDAO.buscarClienteId(clienteNome);
            carregarVeiculosPorCliente(clienteId);
        }
    }
    
    private void carregarVeiculosPorCliente(int clienteId) {
        List<String> veiculos = OrdemServicoDAO.buscarVeiculosPorCliente(clienteId);
        veiculosList.setAll(veiculos); // Atualiza a lista com os veículos filtrados
        listaVeiculos.setItems(veiculosList);
    }

    @FXML
    private void selecionarVeiculo() {
        String veiculoSelecionadoStr = listaVeiculos.getSelectionModel().getSelectedItem();
        if (veiculoSelecionadoStr != null) {
            String[] partes = veiculoSelecionadoStr.split(" - ");
            String modelo = partes[0];
            String placa = partes[1];

            Veiculo veiculo = VeiculoDAO.buscarVeiculoPorModeloEPlaca(modelo, placa);
            if (veiculo != null) {
                veiculoSelecionado = veiculo; // agora guardamos o objeto
                ordemServico.setVeiculo(veiculoSelecionadoStr);
                ordemServico.setAnoVeiculo(veiculo.getAno());
                ordemServico.setChassiVeiculo(veiculo.getChassi());
            }

            buscarVeiculoField.setText(veiculoSelecionadoStr);
            listaVeiculos.getItems().clear();
        }
    }


    @FXML
    private void adicionarServico() {
        String descricao = listaServicos.getSelectionModel().getSelectedItem();
        if (descricao != null && !descricao.isEmpty()) {
            String precoString = OrdemServicoDAO.buscarPrecoServico(descricao);
            try {
                double preco = Double.parseDouble(precoString);
                Servico servico = new Servico(descricao, preco); // Default quantity is 1
                ordemServico.adicionarServico(servico);
                servicosList.add(servico);
                atualizarTotal();
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText("Valor inválido");
                alert.setContentText("O preço do serviço não é válido.");
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void removerServico() {
        Servico servicoSelecionado = servicosTableView.getSelectionModel().getSelectedItem();
        if (servicoSelecionado != null) {
            ordemServico.getServicos().remove(servicoSelecionado);
            servicosList.remove(servicoSelecionado);
            atualizarTotal();
        }
    }

    private void atualizarTotal() {
        subtotal = 0;
        for (Servico servico : servicosList) {
            subtotal += servico.getTotal();
        }
        
        // Atualizar o label de subtotal
        subtotalLabel.setText(String.format("Subtotal: R$ %.2f", subtotal));
        
        // Recalcular o desconto com base no novo subtotal
        calcularDesconto();
    }
    
    @FXML
    private void abrirDialogNovoServico() {
        // Criar um diálogo personalizado
        Dialog<Servico> dialog = new Dialog<>();
        dialog.setTitle("Novo Serviço");
        dialog.setHeaderText("Cadastrar novo serviço");

        // Configurar os botões
        ButtonType buttonTypeSalvar = new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeSalvar, buttonTypeCancelar);

        // Criar o layout do formulário
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField descricaoField = new TextField();
        descricaoField.setPromptText("Descrição do serviço");
        
        TextField precoField = new TextField();
        precoField.setPromptText("Preço (R$)");
        
        // Usando TextFormatter para garantir que apenas números e um ponto decimal sejam digitados
        UnaryOperator<TextFormatter.Change> decimalFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty()) {
                return change;
            }
            
            if (newText.matches("\\d*\\.?\\d*")) {
                return change;
            }
            
            return null;
        };
        
        precoField.setTextFormatter(new TextFormatter<>(decimalFilter));

        grid.add(new Label("Descrição:"), 0, 0);
        grid.add(descricaoField, 1, 0);
        grid.add(new Label("Preço (R$):"), 0, 1);
        grid.add(precoField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Focar no campo de descrição quando o diálogo abrir
        Platform.runLater(() -> descricaoField.requestFocus());

        // Converter o resultado do diálogo para um objeto Servico
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == buttonTypeSalvar) {
                try {
                    String descricao = descricaoField.getText().trim();
                    double preco = Double.parseDouble(precoField.getText().isEmpty() ? "0" : precoField.getText());
                    
                    if (descricao.isEmpty()) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Erro");
                        alert.setHeaderText("Campo obrigatório");
                        alert.setContentText("A descrição do serviço é obrigatória.");
                        alert.showAndWait();
                        return null;
                    }
                    
                    if (preco <= 0) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Erro");
                        alert.setHeaderText("Valor inválido");
                        alert.setContentText("O preço deve ser maior que zero.");
                        alert.showAndWait();
                        return null;
                    }
                    
                    return new Servico(descricao, preco);
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erro");
                    alert.setHeaderText("Valor inválido");
                    alert.setContentText("O preço informado não é um número válido.");
                    alert.showAndWait();
                    return null;
                }
            }
            return null;
        });

        // Processar o resultado
        Optional<Servico> resultado = dialog.showAndWait();
        resultado.ifPresent(this::salvarNovoServico);
    }
    
    private void salvarNovoServico(Servico novoServico) {
        try {
            // Chama o método DAO para salvar o serviço no banco de dados
            boolean sucesso = OrdemServicoDAO.salvarNovoServico(novoServico.getDescricao(), novoServico.getPreco());
            
            if (sucesso) {
                // Adiciona o novo serviço à lista de serviços disponíveis
                servicosDisponiveisList.add(novoServico.getDescricao());
                listaServicos.setItems(servicosDisponiveisList);
                
                // Seleciona o novo serviço na lista
                listaServicos.getSelectionModel().select(novoServico.getDescricao());
                
                // Mensagem de sucesso
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Sucesso");
                alert.setHeaderText("Serviço cadastrado");
                alert.setContentText("O serviço \"" + novoServico.getDescricao() + "\" foi cadastrado com sucesso.");
                alert.showAndWait();
            } else {
                // Mensagem de erro
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText("Falha ao cadastrar");
                alert.setContentText("Não foi possível cadastrar o serviço. Verifique se ele já existe.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Mensagem de erro
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Erro ao cadastrar");
            alert.setContentText("Ocorreu um erro ao cadastrar o serviço: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    @FXML
    private void calcularDesconto() {
        try {
            // Calcular o subtotal (soma de todos os serviços)
            subtotal = 0.0;
            for (Servico servico : servicosList) {
                subtotal += servico.getTotal();
            }
            
            // Atualizar o label de subtotal
            subtotalLabel.setText(String.format("R$ %.2f", subtotal));
            
            // Calcular o desconto
            String descontoTexto = descontoField.getText();
            if (descontoTexto == null || descontoTexto.isEmpty()) {
                valorDesconto = 0.0;
            } else {
                try {
                    double valorDigitado = Double.parseDouble(descontoTexto);
                    
                    // Verificar o tipo de desconto (percentual ou valor)
                    if (tipoDescontoComboBox.getValue().equals("Percentual (%)")) {
                        // Não permitir valores negativos ou maiores que 100%
                        if (valorDigitado < 0) {
                            valorDigitado = 0;
                            descontoField.setText("0");
                        } else if (valorDigitado > 100) {
                            valorDigitado = 100;
                            descontoField.setText("100");
                        }
                        valorDesconto = subtotal * (valorDigitado / 100.0);
                    } else { // Valor em R$
                        // Não permitir valores negativos ou maiores que o subtotal
                        if (valorDigitado < 0) {
                            valorDigitado = 0;
                            descontoField.setText("0");
                        } else if (valorDigitado > subtotal) {
                            valorDigitado = subtotal;
                            descontoField.setText(String.format("%.2f", subtotal));
                        }
                        valorDesconto = valorDigitado;
                    }
                    
                    // Formatação do texto do campo para 2 casas decimais quando for valor em R$
                    if (tipoDescontoComboBox.getValue().equals("Valor (R$)") && !descontoField.getText().isEmpty()) {
                        descontoField.setText(String.format("%.2f", valorDigitado));
                    }
                } catch (NumberFormatException e) {
                    // Se não conseguir converter, define desconto como 0
                    descontoField.setText("0");
                    valorDesconto = 0.0;
                }
            }
            
            // Arredondar o valor do desconto para duas casas decimais para evitar problemas de precisão
            valorDesconto = Math.round(valorDesconto * 100.0) / 100.0;
            
            // Atualizar o total final (subtotal - desconto)
            double totalFinal = subtotal - valorDesconto;
            totalLabel.setText(String.format("R$ %.2f", totalFinal));
            
            // Armazenar o desconto no objeto OrdemServico
            ordemServico.setDesconto(valorDesconto);
        } catch (Exception e) {
            e.printStackTrace();
            descontoField.setText("0");
            valorDesconto = 0.0;
        }
    }
    
    //parte de remover ou alterar serviços
    
    private void configurarMenuContextoServicos() {
        // Criar o menu de contexto
        servicosContextMenu = new ContextMenu();
        
        // Criar itens de menu
        MenuItem menuItemRenomear = new MenuItem("Renomear Serviço");
        MenuItem menuItemExcluir = new MenuItem("Excluir Serviço");
        
        menuItemRenomear.setStyle("-fx-text-fill: black;");
        menuItemExcluir.setStyle("-fx-text-fill: black;");
        
        // Adicionar ações aos itens
        menuItemRenomear.setOnAction(event -> renomearServico());
        menuItemExcluir.setOnAction(event -> excluirServico());
        
        // Adicionar itens ao menu
        servicosContextMenu.getItems().addAll(menuItemRenomear, menuItemExcluir);
        
        // Adicionar evento de mouse à lista de serviços
        listaServicos.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) { // Clique direito
                servicosContextMenu.show(listaServicos, event.getScreenX(), event.getScreenY());
            } else if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                // Opcionalmente você pode adicionar ação de duplo clique também
                // Por exemplo: adicionarServico();
            }
        });
    }

    private void renomearServico() {
        String servicoSelecionado = listaServicos.getSelectionModel().getSelectedItem();
        if (servicoSelecionado == null || servicoSelecionado.isEmpty()) {
            mostrarAlerta("Selecione um serviço", "Nenhum serviço selecionado", 
                         "Por favor, selecione um serviço para renomear.", Alert.AlertType.WARNING);
            return;
        }
        
        // Criar diálogo para renomear
        TextInputDialog dialog = new TextInputDialog(servicoSelecionado);
        dialog.setTitle("Renomear Serviço");
        dialog.setHeaderText("Renomear Serviço");
        dialog.setContentText("Novo nome do serviço:");
        
        // Mostrar diálogo e processar resultado
        Optional<String> resultado = dialog.showAndWait();
        resultado.ifPresent(novoNome -> {
            if (!novoNome.isEmpty() && !novoNome.equals(servicoSelecionado)) {
                boolean sucesso = OrdemServicoDAO.renomearServico(servicoSelecionado, novoNome);
                if (sucesso) {
                    // Atualizar a lista de serviços
                    int indice = servicosDisponiveisList.indexOf(servicoSelecionado);
                    if (indice >= 0) {
                        servicosDisponiveisList.set(indice, novoNome);
                        listaServicos.refresh();
                    }
                    
                    mostrarAlerta("Sucesso", "Serviço renomeado", 
                                 "O serviço foi renomeado com sucesso.", Alert.AlertType.INFORMATION);
                } else {
                    mostrarAlerta("Erro", "Falha ao renomear", 
                                 "Não foi possível renomear o serviço. Verifique se o novo nome já existe.", 
                                 Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void excluirServico() {
        String servicoSelecionado = listaServicos.getSelectionModel().getSelectedItem();
        if (servicoSelecionado == null || servicoSelecionado.isEmpty()) {
            mostrarAlerta("Selecione um serviço", "Nenhum serviço selecionado", 
                         "Por favor, selecione um serviço para excluir.", Alert.AlertType.WARNING);
            return;
        }
        
        // Confirmar exclusão
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar exclusão");
        confirmacao.setHeaderText("Excluir serviço");
        confirmacao.setContentText("Tem certeza que deseja excluir o serviço \"" + servicoSelecionado + "\"?");
        
        Optional<ButtonType> resultado = confirmacao.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            boolean sucesso = OrdemServicoDAO.excluirServico(servicoSelecionado);
            if (sucesso) {
                // Remover da lista de serviços disponíveis
                servicosDisponiveisList.remove(servicoSelecionado);
                
                mostrarAlerta("Sucesso", "Serviço excluído", 
                             "O serviço foi excluído com sucesso.", Alert.AlertType.INFORMATION);
            } else {
                mostrarAlerta("Erro", "Falha ao excluir", 
                             "Não foi possível excluir o serviço. Ele pode estar vinculado a ordens de serviço existentes.", 
                             Alert.AlertType.ERROR);
            }
        }
    }
    
    private void mostrarAlerta(String titulo, String cabecalho, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(cabecalho);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
 


    @FXML
    private void gerarOrdemServico() {
        if (camposObrigatoriosPreenchidos()) {
            imprimirOrdemServico();
        } else {
            mostrarAlertaCamposObrigatorios();
        }
    }

    @FXML
    private void imprimirOrdemServico() {
        if (camposObrigatoriosPreenchidos()) {
            gerarPdfOrdemServico(); // Chama a função para criar o PDF
        } else {
            mostrarAlertaCamposObrigatorios();
        }
    }

    private boolean camposObrigatoriosPreenchidos() {
        return ordemServico.getCliente() != null 
               && ordemServico.getVeiculo() != null 
               && !ordemServico.getServicos().isEmpty()
               && ordemServico.getEndereco() != null
               && ordemServico.getTelefone() != null;
    }

    private void mostrarAlertaCamposObrigatorios() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText("Campos obrigatórios");
        alert.setContentText("Por favor, preencha todos os campos antes de gerar a ordem de serviço.");
        alert.showAndWait();
    }

    private void gerarPdfOrdemServico() {
    	String filePath = "Ordem_de_Servico.pdf";

        try {
            if (!camposObrigatoriosPreenchidos()) {
                mostrarAlertaCamposObrigatorios();
                return;
            }

            // Verifica se o usuário selecionou uma opção
            if (historicoToggle.getSelectedToggle() == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Atenção");
                alert.setHeaderText("Selecione uma opção");
                alert.setContentText("Você precisa marcar se deseja salvar no histórico.");
                alert.showAndWait();
                return;
            }

            boolean salvarNoHistorico = radioSim.isSelected();

            // Pergunta se é uma nova ordem
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Nova Ordem de Serviço");
            alert.setHeaderText("Deseja salvar como uma nova Ordem de Serviço?");
            alert.setContentText("Clique em SIM para gerar um novo número.");

            ButtonType buttonSim = new ButtonType("Sim");
            ButtonType buttonNao = new ButtonType("Não", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(buttonSim, buttonNao);

            Optional<ButtonType> resultado = alert.showAndWait();
            if (resultado.isPresent() && resultado.get() == buttonSim) {
                int ultimoNumero = OrdemServicoDAO.buscarUltimoNumeroOrdem();
                ordemServico.setNumeroOrdem(ultimoNumero + 1);
                OrdemServicoDAO.salvarOrdemSeNaoExistir(ordemServico);
            }

            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            Table headerTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                .setWidth(UnitValue.createPercentValue(100));
            headerTable.addCell(new Cell().setBorder(Border.NO_BORDER)
                .add(new Paragraph("OS Nº: " + ordemServico.getNumeroOrdem())
                .setFont(boldFont)
                .setFontSize(10)));
            headerTable.addCell(new Cell().setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT)
                .add(new Paragraph("Data: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .setFont(normalFont)
                .setFontSize(10)));
            document.add(headerTable);
            document.add(new LineSeparator(new SolidLine()).setMarginTop(5).setMarginBottom(5));

            document.add(new Paragraph(ordemServico.getNomeEmpresa())
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFont(boldFont)
                    .setFontSize(16));
            document.add(new Paragraph("CNPJ/CPF: " + ordemServico.getCnpjEmpresa() + " - Telefone: " + ordemServico.getTelefoneEmpresa())
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFont(normalFont));
            document.add(new Paragraph("Endereço: " + ordemServico.getEnderecoEmpresa())
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFont(normalFont));
            document.add(new LineSeparator(new SolidLine()).setMarginTop(5).setMarginBottom(10));

            document.add(new Paragraph("Ordem de Serviço - Faturado")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFont(boldFont)
                    .setFontSize(14));
            document.add(new LineSeparator(new SolidLine()).setMarginTop(5).setMarginBottom(10));

            // Dados do cliente
            Table clienteTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                    .setWidth(UnitValue.createPercentValue(100));
            clienteTable.addCell(new Cell().setBorder(Border.NO_BORDER)
                    .add(new Paragraph("Cliente: ").setFont(boldFont)
                            .add(new Text(ordemServico.getCliente()).setFont(normalFont))));
            clienteTable.addCell(new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT)
                    .add(new Paragraph("Telefone: ").setFont(boldFont)
                            .add(new Text(ordemServico.getTelefone()).setFont(normalFont))));
            clienteTable.addCell(new Cell(1, 2).setBorder(Border.NO_BORDER)
                    .add(new Paragraph("Endereço: ").setFont(boldFont)
                            .add(new Text(ordemServico.getEndereco()).setFont(normalFont))));
            document.add(clienteTable);
            document.add(new LineSeparator(new SolidLine()).setMarginTop(5).setMarginBottom(10));

            // Dados do veículo
            String[] veiculoInfo = ordemServico.getVeiculo().split(" - ");
            String modelo = veiculoInfo[0];
            String placa = veiculoInfo[1];
            Table tabelaVeiculo = new Table(UnitValue.createPercentArray(new float[]{25, 25, 25, 25}))
                    .setWidth(UnitValue.createPercentValue(100));
            tabelaVeiculo.addCell(new Cell().setBorder(Border.NO_BORDER)
                    .add(new Paragraph("Veículo: ").setFont(boldFont)
                            .add(new Text(modelo).setFont(normalFont))));
            tabelaVeiculo.addCell(new Cell().setBorder(Border.NO_BORDER)
                    .add(new Paragraph("Ano: ").setFont(boldFont)
                            .add(new Text(ordemServico.getAnoVeiculo()).setFont(normalFont))));
            tabelaVeiculo.addCell(new Cell().setBorder(Border.NO_BORDER)
                    .add(new Paragraph("Chassi: ").setFont(boldFont)
                            .add(new Text(ordemServico.getChassiVeiculo()).setFont(normalFont))));
            tabelaVeiculo.addCell(new Cell().setBorder(Border.NO_BORDER)
                    .add(new Paragraph("Placa: ").setFont(boldFont)
                            .add(new Text(placa).setFont(normalFont))));
            document.add(tabelaVeiculo);
            document.add(new Paragraph("\n"));

            // Contorno principal
            Table contornoTable = new Table(1).setWidth(UnitValue.createPercentValue(100));
            Cell contornoCell = new Cell().setBorder(new SolidBorder(1));

            contornoCell.add(new Paragraph("Serviços Realizados")
                    .setFont(boldFont)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(5));

            Table table = new Table(UnitValue.createPercentArray(new float[]{45, 10, 20, 25}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setFontSize(9);

            table.addHeaderCell(createHeaderCell("Descrição do Serviço", boldFont));
            table.addHeaderCell(createHeaderCell("Qtd", boldFont).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(createHeaderCell("Valor Unitário (R$)", boldFont).setTextAlignment(TextAlignment.RIGHT));
            table.addHeaderCell(createHeaderCell("Total (R$)", boldFont).setTextAlignment(TextAlignment.RIGHT));

            double total = 0;
            for (Servico servico : ordemServico.getServicos()) {
                double valorUnitario = servico.getPreco();
                double totalServico = servico.getQuantidade() * valorUnitario;

                table.addCell(new Cell().setBorder(new SolidBorder(0.5f))
                        .add(new Paragraph(servico.getDescricao()).setFont(normalFont)));
                table.addCell(new Cell().setTextAlignment(TextAlignment.CENTER)
                        .setBorder(new SolidBorder(0.5f))
                        .add(new Paragraph(String.valueOf(servico.getQuantidade())).setFont(normalFont)));
                table.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT)
                        .setBorder(new SolidBorder(0.5f))
                        .add(new Paragraph(String.format("%,.2f", valorUnitario)).setFont(normalFont)));
                table.addCell(new Cell().setTextAlignment(TextAlignment.RIGHT)
                        .setBorder(new SolidBorder(0.5f))
                        .add(new Paragraph(String.format("%,.2f", totalServico)).setFont(normalFont)));

                total += totalServico;
            }

            contornoCell.add(table);

            contornoCell.add(new Paragraph("\nResumo Financeiro:")
                    .setFont(boldFont)
                    .setFontSize(11));

            Table resumoTable = new Table(UnitValue.createPercentArray(new float[]{70, 30}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setFontSize(9);

            double subtotal = total;

            resumoTable.addCell(new Cell().setBorder(Border.NO_BORDER)
                    .add(new Paragraph("Subtotal:").setFont(normalFont)).setTextAlignment(TextAlignment.RIGHT));
            resumoTable.addCell(new Cell().setBorder(Border.NO_BORDER)
                    .add(new Paragraph(String.format("R$ %,.2f", subtotal)).setFont(normalFont)).setTextAlignment(TextAlignment.RIGHT));

            if (valorDesconto > 0) {
                resumoTable.addCell(new Cell().setBorder(Border.NO_BORDER)
                        .add(new Paragraph("Desconto:").setFont(normalFont)).setTextAlignment(TextAlignment.RIGHT));
                resumoTable.addCell(new Cell().setBorder(Border.NO_BORDER)
                        .add(new Paragraph(String.format("R$ %,.2f", valorDesconto)).setFont(normalFont)).setTextAlignment(TextAlignment.RIGHT));
            }

            double totalFinal = subtotal - valorDesconto;
            resumoTable.addCell(new Cell().setBorder(Border.NO_BORDER)
                    .add(new Paragraph("Total:").setFont(boldFont)).setTextAlignment(TextAlignment.RIGHT));
            resumoTable.addCell(new Cell().setBorder(Border.NO_BORDER)
                    .add(new Paragraph(String.format("R$ %,.2f", totalFinal)).setFont(boldFont)).setTextAlignment(TextAlignment.RIGHT));

            contornoCell.add(resumoTable);

            contornoCell.add(new Paragraph("\n\n\n"));
            contornoCell.add(new Paragraph("__________________________        __________________________")
                    .setTextAlignment(TextAlignment.CENTER));
            contornoCell.add(new Paragraph("Assinatura do Mecânico              Assinatura do Cliente")
                    .setTextAlignment(TextAlignment.CENTER));

            contornoTable.addCell(contornoCell);
            document.add(contornoTable);

            document.close();
            
         // Salva no histórico se estiver marcado e se um veículo estiver selecionado
            if (salvarNoHistorico) {
                if (veiculoSelecionado != null) {
                    for (Servico servico : ordemServico.getServicos()) {
                        HistoricoServico hs = new HistoricoServico();
                        hs.setDescricao(servico.getDescricao());
                        hs.setValor(servico.getPreco());
                        hs.setData(LocalDate.now().toString()); // ou formate se preferir
                        hs.setVeiculoId(veiculoSelecionado.getId());
                        
                        // Adiciona os dados do cliente
                        hs.setClienteNome(clienteSelecionado.getNome());
                        hs.setClienteTelefone(clienteSelecionado.getTelefone());

                        HistoricoServicoDAO.salvarServicoNoHistorico(hs);
                    }
                } else {
                    System.out.println("Veículo não selecionado. Histórico não será salvo.");
                }
            }

            File file = new File(filePath);
            if (file.exists()) {
                Desktop.getDesktop().open(file);
            }

        } catch (IOException e) {
            handlePdfError(e);
        }
    }



    private void handlePdfError(Exception e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText("Erro ao gerar PDF");
        alert.setContentText("Ocorreu um erro ao criar o PDF da ordem de serviço.");
        alert.showAndWait();
    }
    
    @FXML
    private void tipoDescontoAlterado() {
        // Limpar o campo de desconto quando mudar o tipo
        descontoField.setText("0");
        
        // Se o tipo for percentual, remove formatação específica
        if (tipoDescontoComboBox.getValue().equals("Percentual (%)")) {
            descontoField.setPromptText("0-100");
        } else {
            descontoField.setPromptText("0.00");
        }
        
        // Recalcula o desconto
        calcularDesconto();
    }
    
 // Método para aplicar desconto ao pressionar Enter no campo
    @FXML
    private void aplicarDescontoComEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            calcularDesconto();
        }
    }

    private Cell createHeaderCell(String text, PdfFont font) {
        return new Cell().add(new Paragraph(text).setFont(font)).setTextAlignment(TextAlignment.CENTER);
    }
}
