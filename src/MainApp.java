import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Calculadora calculadora;
    private Label labelSaldo;
    private TableView<Transacao> tabelaTransacao;



    @Override
    public void start(Stage primaryStage){
        // Carregar dados salvos
        calculadora = new Calculadora(GerenciadorDeArquivos.carregar());
        // Criar a interface
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        //Topo - Saldo
        VBox topo = criarTopo();
        root.setTop(topo);
        // Centro - Tabela
        tabelaTransacao = criarTabela();
        root.setCenter(tabelaTransacao);

        // Rodapé - Botões
        HBox rodape = criarBotoes();
        root.setBottom(rodape);

        // Configurar Janela
        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("Calculadora de Gastos");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> salvarAoFechar());
        primaryStage.show();

        atualizarInterface();
    }

    private VBox criarTopo(){
        VBox topo = new VBox(10);
        topo.setPadding(new Insets(0, 0, 15, 0));

        Label titulo = new Label("Minhas Finanças");
        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        labelSaldo = new Label("Saldo: R$ 0,00");
        labelSaldo.setStyle("-fx-font-size: 18px; -fx-text-fill: green;");

        topo.getChildren().addAll(titulo, labelSaldo);
        return topo;
    }

    private TableView<Transacao> criarTabela(){
        TableView<Transacao> tabela = new TableView<>();

        // Colunas
        TableColumn<Transacao, String> colData = new TableColumn<>("Data");
        TableColumn<Transacao, String> colDescricao = new TableColumn<>("Descrição");
        TableColumn<Transacao, String> colCategoria = new TableColumn<>("Categoria");
        TableColumn<Transacao, String> colTipo = new TableColumn<>("Tipo");
        TableColumn<Transacao, Double> colValor = new TableColumn<>("Valor");

        // IMPORTANTE: Dizer de onde vem os dados
        colData.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getData().toString()));
        colDescricao.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescricao()));
        colCategoria.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCategoria()));
        colTipo.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTipo()));
        colValor.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getValor()));

        // Formatar coluna de valor com R$
        colValor.setCellFactory(col -> new TableCell<Transacao, Double>() {
            @Override
            protected void updateItem(Double valor, boolean empty) {
                super.updateItem(valor, empty);
                if (empty || valor == null) {
                    setText(null);
                } else {
                    setText(String.format("R$ %.2f", valor));
                }
            }
        });

        colData.setPrefWidth(100);
        colDescricao.setPrefWidth(250);
        colCategoria.setPrefWidth(150);
        colTipo.setPrefWidth(100);
        colValor.setPrefWidth(120);

        tabela.getColumns().addAll(colData, colDescricao, colCategoria, colTipo, colValor);

        return tabela;
    }

    private HBox criarBotoes(){
        HBox botoes = new HBox(10);
        botoes.setPadding(new Insets(15, 0, 0, 0));

        Button btnAdicionar = new Button("➕ Adicionar Transação");
        Button btnRemover = new Button("\uD83D\uDDD1\uFE0F Remover");

        btnAdicionar.setOnAction(e -> adicionarTransacao());
        btnRemover.setOnAction((e -> removerTransacao()));

        botoes.getChildren().addAll(btnAdicionar, btnRemover);
        return botoes;
    }

    private void atualizarInterface(){
        // Atualizar saldo
        double saldo = calculadora.calcularSaldo();
        labelSaldo.setText(String.format("Saldo: R$ %.2f", saldo));

        if (saldo < 0){
            labelSaldo.setStyle("-fx-font-size: 18px; -fx-text-fill: red;");
        } else {
            labelSaldo.setStyle("-fx-font-size: 18px; -fx-text-fill: green;");
        }

        // Atualizar tabela
        tabelaTransacao.getItems().clear();
        tabelaTransacao.getItems().addAll(calculadora.getTransacoes());
    }

    private void adicionarTransacao() {
        // Criar janela de diálogo
        Dialog<Transacao> dialog = new Dialog<>();
        dialog.setTitle("Nova Transação");
        dialog.setHeaderText("Adicionar uma nova transação");

        // Criar campos
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

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

        DatePicker datePicker = new DatePicker();
        datePicker.setValue(java.time.LocalDate.now());

        // Adicionar ao grid
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

        // Botões
        ButtonType btnSalvar = new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSalvar, ButtonType.CANCEL);

        // Processar resultado
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnSalvar) {
                try {
                    double valor = Double.parseDouble(txtValor.getText().replace(",", "."));
                    return new Transacao(
                            datePicker.getValue(),
                            valor,
                            txtDescricao.getText(),
                            comboTipo.getValue(),
                            comboCategoria.getValue()
                    );
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erro");
                    alert.setContentText("Valor inválido!");
                    alert.showAndWait();
                    return null;
                }
            }
            return null;
        });

        // Mostrar e processar
        dialog.showAndWait().ifPresent(transacao -> {
            calculadora.adicionarTransacao(transacao);
            atualizarInterface();
        });
    }

    private void removerTransacao() {
        Transacao selecionada = tabelaTransacao.getSelectionModel().getSelectedItem();

        if (selecionada == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Atenção");
            alert.setContentText("Selecione uma transação para remover!");
            alert.showAndWait();
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar");
        confirmacao.setContentText("Tem certeza que deseja remover esta transação?");

        confirmacao.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                calculadora.getTransacoes().remove(selecionada);
                atualizarInterface();
            }
        });
    }

    private void salvarAoFechar(){
        GerenciadorDeArquivos.salvar(calculadora.getTransacoes());
    }


    public static void main(String[] args) {
        launch(args);
    }


}
