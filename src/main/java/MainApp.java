import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    private LineChart<String, Number> graficoTendencia;

    // Progress bars
    private ProgressBar progReceitas;
    private ProgressBar progDespesas;
    private Label progReceitasLabel;
    private Label progDespesasLabel;

    // Atalhos de teclado
    private KeyCombination atalhoNovo = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);
    private KeyCombination atalhoRemover = new KeyCodeCombination(KeyCode.DELETE);

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
        scrollPane.getStyleClass().add("scroll-pane");
        root.setCenter(scrollPane);

        // Carregar CSS
        Scene scene = new Scene(root, 1400, 900);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        // Atalhos de teclado
        configurarAtalhosTeclado(scene);

        primaryStage.setTitle("Dashboard Financeiro");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.setOnCloseRequest(e -> salvarAoFechar());
        primaryStage.show();

        atualizarInterface();
    }

    private void configurarAtalhosTeclado(Scene scene) {
        scene.setOnKeyPressed(event -> {
            if (atalhoNovo.match(event)) {
                adicionarTransacao();
            } else if (atalhoRemover.match(event)) {
                if (tabelaTransacao.getSelectionModel().getSelectedItem() != null) {
                    removerTransacao();
                }
            }
        });
    }

    private VBox criarTopo() {
        VBox topo = new VBox();
        topo.getStyleClass().add("top-bar");

        VBox headerContent = new VBox();
        headerContent.getStyleClass().add("header-content");

        Label titulo = new Label("💰 Dashboard Financeiro");
        titulo.getStyleClass().add("app-title");

        Tooltip tooltipTitulo = new Tooltip("Controle total das suas finanças pessoais");
        Tooltip.install(titulo, tooltipTitulo);

        Label subtitulo = new Label("Acompanhe receitas, despesas e tendências em tempo real");
        subtitulo.getStyleClass().add("app-subtitle");

        headerContent.getChildren().addAll(titulo, subtitulo);
        topo.getChildren().add(headerContent);
        return topo;
    }

    private VBox criarCentro() {
        VBox centro = new VBox(24);
        centro.getStyleClass().add("content-wrapper");

        // Cards de estatísticas
        GridPane cardsGrid = criarCardsEstatisticas();

        // Container de gráficos em grid 2x2
        GridPane graficosGrid = criarGridGraficos();

        // Progress bars para categorias
        VBox progressSection = criarSecaoProgress();

        // Seção da tabela
        VBox tabelaSecao = criarSecaoTabela();

        centro.getChildren().addAll(cardsGrid, graficosGrid, progressSection, tabelaSecao);
        return centro;
    }

    private GridPane criarCardsEstatisticas() {
        GridPane grid = new GridPane();
        grid.getStyleClass().add("stats-grid");

        // Card Saldo
        VBox cardSaldo = criarCard("SALDO ATUAL", "R$ 0,00", "saldo",
                "Diferença entre receitas e despesas");
        labelSaldo = buscarLabelValor(cardSaldo);

        // Card Receitas
        VBox cardReceitas = criarCard("RECEITAS", "R$ 0,00", "receitas",
                "Total de entradas de dinheiro");
        labelReceitas = buscarLabelValor(cardReceitas);

        // Card Despesas
        VBox cardDespesas = criarCard("DESPESAS", "R$ 0,00", "despesas",
                "Total de saídas de dinheiro");
        labelDespesas = buscarLabelValor(cardDespesas);

        // Card Total Transações
        VBox cardTotal = criarCard("TRANSAÇÕES", "0", "transacoes",
                "Número total de registros");
        labelTotal = buscarLabelValor(cardTotal);

        // Configurar colunas responsivas
        for (int i = 0; i < 4; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(25);
            grid.getColumnConstraints().add(col);
        }

        grid.add(cardSaldo, 0, 0);
        grid.add(cardReceitas, 1, 0);
        grid.add(cardDespesas, 2, 0);
        grid.add(cardTotal, 3, 0);

        return grid;
    }

    private VBox criarCard(String titulo, String valorInicial, String tipo, String tooltipText) {
        VBox card = new VBox();
        card.getStyleClass().add("stat-card");

        VBox content = new VBox();
        content.getStyleClass().add("stat-card-content");

        // Header com ícone
        HBox header = new HBox();
        header.getStyleClass().add("stat-header");

        // Ícone usando Circle
        StackPane iconContainer = new StackPane();
        iconContainer.getStyleClass().addAll("stat-icon-container", "stat-icon-" + tipo);

        Label iconLabel = criarIcone(tipo);
        iconContainer.getChildren().add(iconLabel);

        header.getChildren().add(iconContainer);

        // Info do card
        VBox info = new VBox();
        info.getStyleClass().add("stat-info");

        Label labelTitulo = new Label(titulo);
        labelTitulo.getStyleClass().add("stat-label");

        Label labelValor = new Label(valorInicial);
        labelValor.getStyleClass().add("stat-value");

        info.getChildren().addAll(labelTitulo, labelValor);
        content.getChildren().addAll(header, info);
        card.getChildren().add(content);

        // Tooltip
        Tooltip tooltip = new Tooltip(tooltipText);
        Tooltip.install(card, tooltip);

        return card;
    }

    private Label criarIcone(String tipo) {
        Label icon = new Label();
        icon.getStyleClass().add("stat-icon");

        switch (tipo) {
            case "saldo":
                icon.setText("$");
                break;
            case "receitas":
                icon.setText("↑");
                break;
            case "despesas":
                icon.setText("↓");
                break;
            case "transacoes":
                icon.setText("≡");
                break;
        }

        return icon;
    }

    private Label buscarLabelValor(VBox card) {
        VBox content = (VBox) card.getChildren().get(0);
        VBox info = (VBox) content.getChildren().get(1);
        return (Label) info.getChildren().get(1);
    }

    private GridPane criarGridGraficos() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);

        // Configurar colunas
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(33);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(33);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(34);

        grid.getColumnConstraints().addAll(col1, col2, col3);

        // Gráfico de Pizza - Gastos por Categoria
        VBox pizzaContainer = new VBox();
        pizzaContainer.getStyleClass().add("chart-container");
        pizzaContainer.setMaxWidth(Double.MAX_VALUE);

        Label tituloPizza = new Label("📊 Despesas por Categoria");
        tituloPizza.getStyleClass().add("chart-title");

        graficoCategoria = new PieChart();
        graficoCategoria.setTitle("");
        graficoCategoria.setLegendVisible(true);
        graficoCategoria.getStyleClass().add("chart");
        graficoCategoria.setMinHeight(400);
        graficoCategoria.setPrefHeight(450);
        graficoCategoria.setLabelsVisible(true);

        pizzaContainer.getChildren().addAll(tituloPizza, graficoCategoria);

        // Gráfico de Barras - Receitas vs Despesas
        VBox barrasContainer = new VBox();
        barrasContainer.getStyleClass().add("chart-container");
        barrasContainer.setMaxWidth(Double.MAX_VALUE);

        Label tituloBarras = new Label("📈 Comparativo Financeiro");
        tituloBarras.getStyleClass().add("chart-title");

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("");
        yAxis.setLabel("Valor (R$)");

        // Configurar eixo X para centralizar labels
        xAxis.setTickLabelRotation(0);
        xAxis.setTickMarkVisible(true);

        graficoMensal = new BarChart<>(xAxis, yAxis);
        graficoMensal.setTitle("");
        graficoMensal.setLegendVisible(false);
        graficoMensal.getStyleClass().add("chart");
        graficoMensal.setMinHeight(400);
        graficoMensal.setPrefHeight(450);
        graficoMensal.setBarGap(3);
        graficoMensal.setCategoryGap(60);

        barrasContainer.getChildren().addAll(tituloBarras, graficoMensal);

        // Gráfico de Linha - Tendência
        VBox linhaContainer = new VBox();
        linhaContainer.getStyleClass().add("chart-container");
        linhaContainer.setMaxWidth(Double.MAX_VALUE);

        Label tituloLinha = new Label("📉 Tendência Mensal");
        tituloLinha.getStyleClass().add("chart-title");

        CategoryAxis xAxisLinha = new CategoryAxis();
        NumberAxis yAxisLinha = new NumberAxis();
        xAxisLinha.setLabel("");
        yAxisLinha.setLabel("Saldo (R$)");

        graficoTendencia = new LineChart<>(xAxisLinha, yAxisLinha);
        graficoTendencia.setTitle("");
        graficoTendencia.setCreateSymbols(true);
        graficoTendencia.getStyleClass().add("chart");
        graficoTendencia.setMinHeight(400);
        graficoTendencia.setPrefHeight(450);

        linhaContainer.getChildren().addAll(tituloLinha, graficoTendencia);

        grid.add(pizzaContainer, 0, 0);
        grid.add(barrasContainer, 1, 0);
        grid.add(linhaContainer, 2, 0);

        return grid;
    }

    private VBox criarSecaoProgress() {
        VBox secao = new VBox(16);
        secao.getStyleClass().add("content-section");
        secao.setMaxWidth(Double.MAX_VALUE);

        Label titulo = new Label("💹 Distribuição do Orçamento");
        titulo.getStyleClass().add("section-title");

        // Progress bar Receitas
        VBox progReceitasBox = new VBox(8);
        HBox receitasHeader = new HBox();
        receitasHeader.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(receitasHeader, Priority.ALWAYS);

        Label lblReceitas = new Label("Receitas Totais");
        lblReceitas.getStyleClass().add("progress-label");
        lblReceitas.setStyle("-fx-text-fill: #10b981;");

        progReceitasLabel = new Label("R$ 0,00");
        progReceitasLabel.getStyleClass().add("progress-label");
        progReceitasLabel.setStyle("-fx-text-fill: #10b981; -fx-font-weight: 700;");

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        receitasHeader.getChildren().addAll(lblReceitas, spacer1, progReceitasLabel);

        progReceitas = new ProgressBar(0);
        progReceitas.setMaxWidth(Double.MAX_VALUE);
        progReceitas.getStyleClass().add("progress-bar");
        progReceitas.setStyle("-fx-accent: #10b981;");

        progReceitasBox.getChildren().addAll(receitasHeader, progReceitas);

        // Progress bar Despesas
        VBox progDespesasBox = new VBox(8);
        HBox despesasHeader = new HBox();
        despesasHeader.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(despesasHeader, Priority.ALWAYS);

        Label lblDespesas = new Label("Despesas Totais");
        lblDespesas.getStyleClass().add("progress-label");
        lblDespesas.setStyle("-fx-text-fill: #ef4444;");

        progDespesasLabel = new Label("R$ 0,00");
        progDespesasLabel.getStyleClass().add("progress-label");
        progDespesasLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: 700;");

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        despesasHeader.getChildren().addAll(lblDespesas, spacer2, progDespesasLabel);

        progDespesas = new ProgressBar(0);
        progDespesas.setMaxWidth(Double.MAX_VALUE);
        progDespesas.getStyleClass().add("progress-bar");
        progDespesas.setStyle("-fx-accent: #ef4444;");

        progDespesasBox.getChildren().addAll(despesasHeader, progDespesas);

        secao.getChildren().addAll(titulo, progReceitasBox, progDespesasBox);
        return secao;
    }

    private VBox criarSecaoTabela() {
        VBox secao = new VBox();
        secao.getStyleClass().add("content-section");

        // Header
        VBox header = new VBox();
        header.getStyleClass().add("section-header");

        Label titulo = new Label("Histórico de Transações");
        titulo.getStyleClass().add("section-title");

        Label subtitulo = new Label("Visualize e gerencie todas as suas movimentações financeiras");
        subtitulo.getStyleClass().add("section-subtitle");

        header.getChildren().addAll(titulo, subtitulo);

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
        HBox barra = new HBox(12);
        barra.getStyleClass().add("search-bar");

        campoBusca = new TextField();
        campoBusca.setPromptText("Buscar por descrição...");
        campoBusca.getStyleClass().add("search-field");
        campoBusca.textProperty().addListener((obs, old, novo) -> filtrarTransacoes());

        Tooltip tooltipBusca = new Tooltip("Digite para filtrar transações pela descrição");
        campoBusca.setTooltip(tooltipBusca);

        filtroTipo = new ComboBox<>();
        filtroTipo.getItems().addAll("Todos os Tipos", "Receita", "Despesa");
        filtroTipo.setValue("Todos os Tipos");
        filtroTipo.getStyleClass().add("filter-combo");
        filtroTipo.setOnAction(e -> filtrarTransacoes());

        Tooltip tooltipTipo = new Tooltip("Filtrar por tipo de transação");
        filtroTipo.setTooltip(tooltipTipo);

        filtroCategoria = new ComboBox<>();
        filtroCategoria.getItems().addAll("Todas as Categorias", "Alimentação", "Transporte",
                "Lazer", "Saúde", "Educação", "Salário", "Outros");
        filtroCategoria.setValue("Todas as Categorias");
        filtroCategoria.getStyleClass().add("filter-combo");
        filtroCategoria.setOnAction(e -> filtrarTransacoes());

        Tooltip tooltipCategoria = new Tooltip("Filtrar por categoria");
        filtroCategoria.setTooltip(tooltipCategoria);

        Button btnLimpar = new Button("Limpar Filtros");
        btnLimpar.getStyleClass().addAll("btn", "btn-secondary");
        btnLimpar.setOnAction(e -> limparFiltros());

        Tooltip tooltipLimpar = new Tooltip("Remover todos os filtros aplicados");
        btnLimpar.setTooltip(tooltipLimpar);

        HBox.setHgrow(campoBusca, Priority.ALWAYS);
        barra.getChildren().addAll(campoBusca, filtroTipo, filtroCategoria, btnLimpar);
        return barra;
    }

    private TableView<Transacao> criarTabela() {
        TableView<Transacao> tabela = new TableView<>();
        tabela.setItems(transacoesObservable);
        tabela.setPlaceholder(criarEstadoVazio());

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
                getStyleClass().removeAll("valor-positivo", "valor-negativo");
                if (empty || valor == null) {
                    setText(null);
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

        // Permitir seleção múltipla
        tabela.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        return tabela;
    }

    private VBox criarEstadoVazio() {
        VBox emptyState = new VBox(16);
        emptyState.getStyleClass().add("empty-state");

        Label icon = new Label("📊");
        icon.getStyleClass().add("empty-state-icon");

        Label title = new Label("Nenhuma transação encontrada");
        title.getStyleClass().add("empty-state-title");

        Label message = new Label("Adicione sua primeira transação para começar a controlar suas finanças");
        message.getStyleClass().add("empty-state-message");
        message.setWrapText(true);
        message.setMaxWidth(300);

        emptyState.getChildren().addAll(icon, title, message);
        return emptyState;
    }

    private HBox criarBotoes() {
        HBox botoes = new HBox();
        botoes.getStyleClass().add("button-bar");

        Button btnAdicionar = new Button("Nova Transação");
        btnAdicionar.getStyleClass().addAll("btn", "btn-primary");
        btnAdicionar.setOnAction(e -> adicionarTransacao());

        Tooltip tooltipAdicionar = new Tooltip("Adicionar nova transação (Ctrl+N)");
        btnAdicionar.setTooltip(tooltipAdicionar);

        Button btnEditar = new Button("Editar");
        btnEditar.getStyleClass().addAll("btn", "btn-secondary");
        btnEditar.setOnAction(e -> editarTransacao());

        Tooltip tooltipEditar = new Tooltip("Editar transação selecionada");
        btnEditar.setTooltip(tooltipEditar);

        Button btnRemover = new Button("Remover");
        btnRemover.getStyleClass().addAll("btn", "btn-danger");
        btnRemover.setOnAction(e -> removerTransacao());

        Tooltip tooltipRemover = new Tooltip("Remover transação selecionada (Delete)");
        btnRemover.setTooltip(tooltipRemover);

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

        // Atualizar progress bars
        double total = receitas + despesas;
        if (total > 0) {
            progReceitas.setProgress(receitas / total);
            progDespesas.setProgress(despesas / total);
        } else {
            progReceitas.setProgress(0);
            progDespesas.setProgress(0);
        }

        progReceitasLabel.setText(String.format("R$ %.2f (%.0f%%)", receitas, total > 0 ? (receitas / total * 100) : 0));
        progDespesasLabel.setText(String.format("R$ %.2f (%.0f%%)", despesas, total > 0 ? (despesas / total * 100) : 0));

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
        if (gastosPorCategoria.isEmpty()) {
            pieData.add(new PieChart.Data("Sem dados", 1));
        } else {
            gastosPorCategoria.forEach((categoria, valor) ->
                    pieData.add(new PieChart.Data(categoria + "\nR$ " +
                            String.format("%.2f", valor), valor)));
        }
        graficoCategoria.setData(pieData);

        // Gráfico de Barras - Definir categorias explicitamente
        CategoryAxis xAxis = (CategoryAxis) graficoMensal.getXAxis();
        ObservableList<String> categorias = FXCollections.observableArrayList("Receitas", "Despesas");
        xAxis.setCategories(categorias);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Valores");
        series.getData().add(new XYChart.Data<>("Receitas", calcularReceitas()));
        series.getData().add(new XYChart.Data<>("Despesas", calcularDespesas()));

        graficoMensal.getData().clear();
        graficoMensal.getData().add(series);

        // Gráfico de Linha - Tendência (últimos 6 meses)
        XYChart.Series<String, Number> serieLinha = new XYChart.Series<>();
        serieLinha.setName("Saldo");

        // Dados simulados para demonstração
        LocalDate hoje = LocalDate.now();
        for (int i = 5; i >= 0; i--) {
            LocalDate mes = hoje.minusMonths(i);
            String nomeMes = mes.getMonth().name().substring(0, 3);

            // Calcular saldo até aquele mês
            double saldoMes = calculadora.getTransacoes().stream()
                .filter(t -> !t.getData().isAfter(mes))
                .mapToDouble(t -> t.getTipo().equals("Receita") ? t.getValor() : -t.getValor())
                .sum();

            serieLinha.getData().add(new XYChart.Data<>(nomeMes, saldoMes));
        }

        graficoTendencia.getData().clear();
        graficoTendencia.getData().add(serieLinha);
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
                        .filter(t -> tipo.equals("Todos os Tipos") || t.getTipo().equals(tipo))
                        .filter(t -> categoria.equals("Todas as Categorias") || t.getCategoria().equals(categoria))
                        .toList()
        );

        tabelaTransacao.setItems(filtradas);
    }

    private void limparFiltros() {
        campoBusca.clear();
        filtroTipo.setValue("Todos os Tipos");
        filtroCategoria.setValue("Todas as Categorias");
        tabelaTransacao.setItems(transacoesObservable);
    }

    private void adicionarTransacao() {
        Dialog<Transacao> dialog = new Dialog<>();
        dialog.setTitle("Nova Transação");
        dialog.setHeaderText("Adicionar uma nova transação");

        GridPane grid = new GridPane();
        grid.getStyleClass().add("form-grid");
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPadding(new Insets(32));

        // Data
        Label lblData = new Label("Data");
        lblData.getStyleClass().add("form-label");
        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setTooltip(new Tooltip("Selecione a data da transação"));

        // Descrição
        Label lblDescricao = new Label("Descrição");
        lblDescricao.getStyleClass().add("form-label");
        TextField txtDescricao = new TextField();
        txtDescricao.setPromptText("Ex: Almoço no restaurante");
        txtDescricao.setTooltip(new Tooltip("Descreva a transação de forma clara"));

        // Valor
        Label lblValor = new Label("Valor");
        lblValor.getStyleClass().add("form-label");
        TextField txtValor = new TextField();
        txtValor.setPromptText("Ex: 45,50");
        txtValor.setTooltip(new Tooltip("Valor em reais (use vírgula ou ponto)"));

        Label errorValor = new Label("");
        errorValor.getStyleClass().add("error-message");
        errorValor.setVisible(false);

        // Validação em tempo real
        txtValor.textProperty().addListener((obs, old, novo) -> {
            if (!novo.isEmpty()) {
                try {
                    Double.parseDouble(novo.replace(",", "."));
                    txtValor.getStyleClass().remove("text-field-error");
                    errorValor.setVisible(false);
                } catch (NumberFormatException e) {
                    txtValor.getStyleClass().add("text-field-error");
                    errorValor.setText("Valor inválido");
                    errorValor.setVisible(true);
                }
            }
        });

        // Tipo
        Label lblTipo = new Label("Tipo");
        lblTipo.getStyleClass().add("form-label");
        ComboBox<String> comboTipo = new ComboBox<>();
        comboTipo.getItems().addAll("Receita", "Despesa");
        comboTipo.setValue("Despesa");
        comboTipo.setTooltip(new Tooltip("Entrada ou saída de dinheiro"));

        // Categoria
        Label lblCategoria = new Label("Categoria");
        lblCategoria.getStyleClass().add("form-label");
        ComboBox<String> comboCategoria = new ComboBox<>();
        comboCategoria.getItems().addAll("Alimentação", "Transporte", "Lazer",
                "Saúde", "Educação", "Salário", "Outros");
        comboCategoria.setValue("Outros");
        comboCategoria.setTooltip(new Tooltip("Classifique a transação"));

        grid.add(lblData, 0, 0);
        grid.add(datePicker, 1, 0);
        grid.add(lblDescricao, 0, 1);
        grid.add(txtDescricao, 1, 1);
        grid.add(lblValor, 0, 2);

        VBox valorBox = new VBox(4, txtValor, errorValor);
        grid.add(valorBox, 1, 2);

        grid.add(lblTipo, 0, 3);
        grid.add(comboTipo, 1, 3);
        grid.add(lblCategoria, 0, 4);
        grid.add(comboCategoria, 1, 4);

        dialog.getDialogPane().setContent(grid);
        ButtonType btnSalvar = new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSalvar, btnCancelar);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnSalvar) {
                try {
                    if (txtDescricao.getText().trim().isEmpty()) {
                        mostrarErro("Descrição obrigatória", "Por favor, informe uma descrição para a transação.");
                        return null;
                    }

                    double valor = Double.parseDouble(txtValor.getText().replace(",", "."));

                    if (valor <= 0) {
                        mostrarErro("Valor inválido", "O valor deve ser maior que zero.");
                        return null;
                    }

                    return new Transacao(datePicker.getValue(), valor,
                            txtDescricao.getText(), comboTipo.getValue(),
                            comboCategoria.getValue());
                } catch (NumberFormatException e) {
                    mostrarErro("Valor inválido", "Por favor, informe um valor numérico válido.");
                    return null;
                }
            }
            return null;
        });

        Optional<Transacao> resultado = dialog.showAndWait();
        resultado.ifPresent(transacao -> {
            calculadora.adicionarTransacao(transacao);
            transacoesObservable.setAll(calculadora.getTransacoes());
            atualizarInterface();
            mostrarSucesso("Transação adicionada com sucesso!");
        });
    }

    private void editarTransacao() {
        Transacao selecionada = tabelaTransacao.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            mostrarAviso("Nenhuma seleção", "Por favor, selecione uma transação para editar.");
            return;
        }
        mostrarInfo("Em desenvolvimento", "A funcionalidade de edição estará disponível em breve.");
    }

    private void removerTransacao() {
        Transacao selecionada = tabelaTransacao.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            mostrarAviso("Nenhuma seleção", "Por favor, selecione uma transação para remover.");
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Exclusão");
        confirmacao.setHeaderText("Deseja realmente remover esta transação?");
        confirmacao.setContentText(String.format("%s - R$ %.2f",
                selecionada.getDescricao(), selecionada.getValor()));

        confirmacao.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                calculadora.getTransacoes().remove(selecionada);
                transacoesObservable.setAll(calculadora.getTransacoes());
                atualizarInterface();
                mostrarSucesso("Transação removida com sucesso!");
            }
        });
    }

    private void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(titulo);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarAviso(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(titulo);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarInfo(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(titulo);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarSucesso(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
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

