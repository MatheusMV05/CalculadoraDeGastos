import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class MainApp extends Application {

    private Calculadora calculadora;
    private TableView<Transacao> tabelaTransacao;
    private ObservableList<Transacao> transacoesObservable;

    // Labels dos cards
    private Label labelSaldo;
    private Label labelReceitas;
    private Label labelDespesas;
    private Label labelTotal;

    // Filtros
    private TextField campoBusca;
    private ComboBox<String> filtroTipo;
    private ComboBox<String> filtroCategoria;

    // Gráficos
    private PieChart graficoCategoria;
    private BarChart<String, Number> graficoMensal;

    @Override
    public void start(Stage primaryStage) {
        // Carregar dados
        calculadora = new Calculadora(GerenciadorDeArquivos.carregar());
        transacoesObservable = FXCollections.observableArrayList(calculadora.getTransacoes());

        // Container principal
        BorderPane root = new BorderPane();
        root.getStyleClass().add("main-container");

        // Topo com título
        VBox topo = criarTopo();
        root.setTop(topo);

        // Centro com conteúdo
        VBox centro = criarCentro();
        ScrollPane scrollPane = new ScrollPane(centro);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        root.setCenter(scrollPane);

        // Carregar CSS
        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setTitle("Calculadora de Gastos");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.setOnCloseRequest(e -> salvarAoFechar());
        primaryStage.show();

        atualizarInterface();
    }

    private VBox criarTopo() {
        VBox topo = new VBox(5);
        topo.getStyleClass().add("top-bar");

        Label titulo = new Label("Minhas Finanças");
        titulo.getStyleClass().add("app-title");

        Label subtitulo = new Label("Controle completo dos seus gastos");
        subtitulo.getStyleClass().add("app-subtitle");

        topo.getChildren().addAll(titulo, subtitulo);
        return topo;
    }

    private VBox criarCentro() {
        VBox centro = new VBox(20);
        centro.getStyleClass().add("content-wrapper");

        // Cards de estatísticas
        GridPane cardsGrid = criarCardsEstatisticas();

        // Seção de gráficos
        HBox graficos = criarGraficos();

        // Seção da tabela
        VBox tabelaSecao = criarSecaoTabela();

        centro.getChildren().addAll(cardsGrid, graficos, tabelaSecao);
        return centro;
    }

    private GridPane criarCardsEstatisticas() {
        GridPane grid = new GridPane();
        grid.getStyleClass().add("stats-grid");
        grid.setHgap(15);
        grid.setVgap(15);

        // Card Saldo
        VBox cardSaldo = criarCard("💳", "Saldo Atual", "R$ 0,00", "saldo");
        labelSaldo = (Label) ((VBox) cardSaldo.getChildren().get(1)).getChildren().get(1);

        // Card Receitas
        VBox cardReceitas = criarCard("💚", "Receitas", "R$ 0,00", "receitas");
        labelReceitas = (Label) ((VBox) cardReceitas.getChildren().get(1)).getChildren().get(1);

        // Card Despesas
        VBox cardDespesas = criarCard("💔", "Despesas", "R$ 0,00", "despesas");
        labelDespesas = (Label) ((VBox) cardDespesas.getChildren().get(1)).getChildren().get(1);

        // Card Total Transações
        VBox cardTotal = criarCard("📊", "Transações", "0", "transacoes");
        labelTotal = (Label) ((VBox) cardTotal.getChildren().get(1)).getChildren().get(1);

        // Configurar colunas responsivas
        ColumnConstraints col = new ColumnConstraints();
        col.setPercentWidth(25);
        grid.getColumnConstraints().addAll(col, col, col, col);

        grid.add(cardSaldo, 0, 0);
        grid.add(cardReceitas, 1, 0);
        grid.add(cardDespesas, 2, 0);
        grid.add(cardTotal, 3, 0);

        return grid;
    }

    private VBox criarCard(String icone, String titulo, String valorInicial, String tipo) {
        VBox card = new VBox(15);
        card.getStyleClass().add("stat-card");
        card.setAlignment(Pos.TOP_LEFT);

        // Header com ícone
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label iconLabel = new Label(icone);
        iconLabel.getStyleClass().addAll("stat-icon", "stat-icon-" + tipo);

        VBox titleBox = new VBox(3);
        Label tituloLabel = new Label(titulo.toUpperCase());
        tituloLabel.getStyleClass().add("stat-label");

        Label valorLabel = new Label(valorInicial);
        valorLabel.getStyleClass().add("stat-value");

        titleBox.getChildren().addAll(tituloLabel, valorLabel);
        header.getChildren().addAll(iconLabel);

        card.getChildren().addAll(header, titleBox);
        return card;
    }

    private HBox criarGraficos() {
        HBox container = new HBox(15);

        // Gráfico de Pizza - Gastos por Categoria
        VBox pizzaContainer = new VBox(10);
        pizzaContainer.getStyleClass().add("chart-container");
        pizzaContainer.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(pizzaContainer, Priority.ALWAYS);

        Label tituloPizza = new Label("Gastos por Categoria");
        tituloPizza.getStyleClass().add("chart-title");

        graficoCategoria = new PieChart();
        graficoCategoria.setTitle("");
        graficoCategoria.setLegendVisible(true);
        graficoCategoria.getStyleClass().add("chart");

        pizzaContainer.getChildren().addAll(tituloPizza, graficoCategoria);

        // Gráfico de Barras - Receitas vs Despesas
        VBox barrasContainer = new VBox(10);
        barrasContainer.getStyleClass().add("chart-container");
        barrasContainer.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(barrasContainer, Priority.ALWAYS);

        Label tituloBarras = new Label("Receitas vs Despesas");
        tituloBarras.getStyleClass().add("chart-title");

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Tipo");
        yAxis.setLabel("Valor (R$)");

        graficoMensal = new BarChart<>(xAxis, yAxis);
        graficoMensal.setTitle("");
        graficoMensal.setLegendVisible(false);
        graficoMensal.getStyleClass().add("chart");

        barrasContainer.getChildren().addAll(tituloBarras, graficoMensal);

        container.getChildren().addAll(pizzaContainer, barrasContainer);
        return container;
    }

    private VBox criarSecaoTabela() {
        VBox secao = new VBox(15);
        secao.getStyleClass().add("table-section");

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("section-header");

        Label titulo = new Label("Transações Recentes");
        titulo.getStyleClass().add("section-title");
        header.getChildren().add(titulo);

        // Barra de busca e filtros
        HBox barraBusca = criarBarraBusca();

        // Tabela
        tabelaTransacao = criarTabela();

        // Botões
        HBox botoes = criarBotoes();

        secao.getChildren().addAll(header, barraBusca, tabelaTransacao, botoes);
        return secao;
    }

    private HBox criarBarraBusca() {
        HBox barra = new HBox(10);
        barra.getStyleClass().add("search-bar");
        barra.setAlignment(Pos.CENTER_LEFT);

        campoBusca = new TextField();
        campoBusca.setPromptText("Buscar por descrição...");
        campoBusca.getStyleClass().add("search-field");
        campoBusca.textProperty().addListener((obs, old, novo) -> filtrarTransacoes());

        filtroTipo = new ComboBox<>();
        filtroTipo.getItems().addAll("Todos", "Receita", "Despesa");
        filtroTipo.setValue("Todos");
        filtroTipo.getStyleClass().add("filter-combo");
        filtroTipo.setOnAction(e -> filtrarTransacoes());

        filtroCategoria = new ComboBox<>();
        filtroCategoria.getItems().addAll("Todas", "Alimentação", "Transporte",
                "Lazer", "Saúde", "Educação", "Salário", "Outros");
        filtroCategoria.setValue("Todas");
        filtroCategoria.getStyleClass().add("filter-combo");
        filtroCategoria.setOnAction(e -> filtrarTransacoes());

        Button btnLimpar = new Button("Limpar Filtros");
        btnLimpar.getStyleClass().addAll("btn", "btn-secondary");
        btnLimpar.setOnAction(e -> limparFiltros());

        HBox.setHgrow(campoBusca, Priority.ALWAYS);
        barra.getChildren().addAll(campoBusca, filtroTipo, filtroCategoria, btnLimpar);
        return barra;
    }

    private TableView<Transacao> criarTabela() {
        TableView<Transacao> tabela = new TableView<>();
        tabela.setItems(transacoesObservable);

        // Coluna Data
        TableColumn<Transacao, String> colData = new TableColumn<>("Data");
        colData.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getData().toString()));
        colData.setPrefWidth(120);

        // Coluna Descrição
        TableColumn<Transacao, String> colDescricao = new TableColumn<>("Descrição");
        colDescricao.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getDescricao()));
        colDescricao.setPrefWidth(300);

        // Coluna Categoria
        TableColumn<Transacao, String> colCategoria = new TableColumn<>("Categoria");
        colCategoria.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getCategoria()));
        colCategoria.setPrefWidth(150);

        // Coluna Tipo com Badge
        TableColumn<Transacao, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getTipo()));
        colTipo.setCellFactory(col -> new TableCell<Transacao, String>() {
            @Override
            protected void updateItem(String tipo, boolean empty) {
                super.updateItem(tipo, empty);
                if (empty || tipo == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(tipo);
                    badge.getStyleClass().add("badge");
                    if (tipo.equals("Receita")) {
                        badge.getStyleClass().add("badge-receita");
                    } else {
                        badge.getStyleClass().add("badge-despesa");
                    }
                    setGraphic(badge);
                }
            }
        });
        colTipo.setPrefWidth(120);

        // Coluna Valor formatada
        TableColumn<Transacao, Double> colValor = new TableColumn<>("Valor");
        colValor.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(
                        cellData.getValue().getValor()));
        colValor.setCellFactory(col -> new TableCell<Transacao, Double>() {
            @Override
            protected void updateItem(Double valor, boolean empty) {
                super.updateItem(valor, empty);
                if (empty || valor == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("R$ %.2f", valor));
                    Transacao t = getTableView().getItems().get(getIndex());
                    if (t.getTipo().equals("Receita")) {
                        getStyleClass().add("valor-positivo");
                    } else {
                        getStyleClass().add("valor-negativo");
                    }
                }
            }
        });
        colValor.setPrefWidth(150);

        tabela.getColumns().addAll(colData, colDescricao, colCategoria, colTipo, colValor);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        return tabela;
    }

    private HBox criarBotoes() {
        HBox botoes = new HBox(10);
        botoes.getStyleClass().add("button-bar");

        Button btnAdicionar = new Button("Nova Transação");
        btnAdicionar.getStyleClass().addAll("btn", "btn-primary");
        btnAdicionar.setOnAction(e -> adicionarTransacao());

        Button btnEditar = new Button("Editar");
        btnEditar.getStyleClass().addAll("btn", "btn-secondary");
        btnEditar.setOnAction(e -> editarTransacao());

        Button btnRemover = new Button("Remover");
        btnRemover.getStyleClass().addAll("btn", "btn-danger");
        btnRemover.setOnAction(e -> removerTransacao());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        botoes.getChildren().addAll(btnAdicionar, btnEditar, btnRemover, spacer);
        return botoes;
    }

    private void atualizarInterface() {
        // Atualizar cards
        double saldo = calculadora.calcularSaldo();
        double receitas = calcularReceitas();
        double despesas = calcularDespesas();

        labelSaldo.setText(String.format("R$ %.2f", saldo));
        labelReceitas.setText(String.format("R$ %.2f", receitas));
        labelDespesas.setText(String.format("R$ %.2f", despesas));
        labelTotal.setText(String.valueOf(calculadora.getTransacoes().size()));

        // Atualizar gráficos
        atualizarGraficos();
    }

    private void atualizarGraficos() {
        // Gráfico de Pizza - Despesas por Categoria
        Map<String, Double> gastosPorCategoria = new HashMap<>();
        for (Transacao t : calculadora.getTransacoes()) {
            if (t.getTipo().equals("Despesa")) {
                gastosPorCategoria.merge(t.getCategoria(), t.getValor(), Double::sum);
            }
        }

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        gastosPorCategoria.forEach((categoria, valor) ->
                pieData.add(new PieChart.Data(categoria + " (R$ " +
                        String.format("%.2f", valor) + ")", valor)));
        graficoCategoria.setData(pieData);

        // Gráfico de Barras
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Receitas", calcularReceitas()));
        series.getData().add(new XYChart.Data<>("Despesas", calcularDespesas()));

        graficoMensal.getData().clear();
        graficoMensal.getData().add(series);
    }

    private double calcularReceitas() {
        return calculadora.getTransacoes().stream()
                .filter(t -> t.getTipo().equals("Receita"))
                .mapToDouble(Transacao::getValor)
                .sum();
    }

    private double calcularDespesas() {
        return calculadora.getTransacoes().stream()
                .filter(t -> t.getTipo().equals("Despesa"))
                .mapToDouble(Transacao::getValor)
                .sum();
    }

    private void filtrarTransacoes() {
        String busca = campoBusca.getText().toLowerCase();
        String tipo = filtroTipo.getValue();
        String categoria = filtroCategoria.getValue();

        ObservableList<Transacao> filtradas = FXCollections.observableArrayList(
                calculadora.getTransacoes().stream()
                        .filter(t -> t.getDescricao().toLowerCase().contains(busca))
                        .filter(t -> tipo.equals("Todos") || t.getTipo().equals(tipo))
                        .filter(t -> categoria.equals("Todas") || t.getCategoria().equals(categoria))
                        .toList()
        );

        tabelaTransacao.setItems(filtradas);
    }

    private void limparFiltros() {
        campoBusca.clear();
        filtroTipo.setValue("Todos");
        filtroCategoria.setValue("Todas");
        tabelaTransacao.setItems(transacoesObservable);
    }

    private void adicionarTransacao() {
        Dialog<Transacao> dialog = new Dialog<>();
        dialog.setTitle("Nova Transação");
        dialog.setHeaderText("💰 Adicionar uma nova transação");

        GridPane grid = new GridPane();
        grid.getStyleClass().add("form-grid");
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        DatePicker datePicker = new DatePicker(LocalDate.now());
        TextField txtDescricao = new TextField();
        txtDescricao.setPromptText("Ex: Almoço no restaurante");
        TextField txtValor = new TextField();
        txtValor.setPromptText("Ex: 45,50");
        ComboBox<String> comboTipo = new ComboBox<>();
        comboTipo.getItems().addAll("Receita", "Despesa");
        comboTipo.setValue("Despesa");
        ComboBox<String> comboCategoria = new ComboBox<>();
        comboCategoria.getItems().addAll("Alimentação", "Transporte", "Lazer",
                "Saúde", "Educação", "Salário", "Outros");
        comboCategoria.setValue("Outros");

        grid.add(new Label("Data:"), 0, 0);
        grid.add(datePicker, 1, 0);
        grid.add(new Label("Descrição:"), 0, 1);
        grid.add(txtDescricao, 1, 1);
        grid.add(new Label("Valor:"), 0, 2);
        grid.add(txtValor, 1, 2);
        grid.add(new Label("Tipo:"), 0, 3);
        grid.add(comboTipo, 1, 3);
        grid.add(new Label("Categoria:"), 0, 4);
        grid.add(comboCategoria, 1, 4);

        dialog.getDialogPane().setContent(grid);
        ButtonType btnSalvar = new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSalvar, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnSalvar) {
                try {
                    double valor = Double.parseDouble(txtValor.getText().replace(",", "."));
                    return new Transacao(datePicker.getValue(), valor,
                            txtDescricao.getText(), comboTipo.getValue(),
                            comboCategoria.getValue());
                } catch (NumberFormatException e) {
                    mostrarErro("Valor inválido!");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(transacao -> {
            calculadora.adicionarTransacao(transacao);
            transacoesObservable.setAll(calculadora.getTransacoes());
            atualizarInterface();
        });
    }

    private void editarTransacao() {
        Transacao selecionada = tabelaTransacao.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            mostrarAviso("Selecione uma transação para editar!");
            return;
        }
        // Implementar edição similar ao adicionar
        mostrarInfo("Funcionalidade de edição em desenvolvimento!");
    }

    private void removerTransacao() {
        Transacao selecionada = tabelaTransacao.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            mostrarAviso("Selecione uma transação para remover!");
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar");
        confirmacao.setHeaderText("Tem certeza?");
        confirmacao.setContentText("Deseja realmente remover esta transação?");

        confirmacao.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                calculadora.getTransacoes().remove(selecionada);
                transacoesObservable.setAll(calculadora.getTransacoes());
                atualizarInterface();
            }
        });
    }

    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarAviso(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Atenção");
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarInfo(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informação");
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void salvarAoFechar() {
        GerenciadorDeArquivos.salvar(calculadora.getTransacoes());
    }

    public static void main(String[] args) {
        launch(args);
    }
}