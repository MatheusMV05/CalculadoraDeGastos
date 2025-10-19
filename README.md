# 💰 Calculadora de Gastos Pessoais

## 📋 Visão Geral

Aplicação desktop desenvolvida em **Java com JavaFX** para controle financeiro pessoal. O projeto permite registrar receitas e despesas, visualizar saldo em tempo real, e persistir dados localmente em arquivo de texto.

Iniciado como projeto de aprendizado para aprofundar conhecimentos em Java, com objetivo futuro de evoluir para uma ferramenta completa similar ao Evernote com funcionalidades para finanças pessoais.

---

## 🛠️ Tecnologias Utilizadas

- **Java 21** - Linguagem principal
- **JavaFX 21** - Interface gráfica
- **Maven** - Gerenciamento de dependências e build
- **File I/O** - Persistência de dados em arquivo texto

---

## 📁 Estrutura do Projeto

### Classes Implementadas

#### 1. **Transacao.java**
Classe modelo que representa uma transação financeira.

**Atributos:**
- `LocalDate data` - Data da transação
- `double valor` - Valor monetário
- `String descricao` - Descrição da transação
- `String tipo` - "Receita" ou "Despesa"
- `String categoria` - Categoria (Alimentação, Transporte, etc.)

**Métodos principais:**
- Construtor completo
- Getters e Setters para todos os atributos
- `toString()` - Formatação customizada para exibição

#### 2. **Calculadora.java**
Gerenciador central de transações.

**Atributos:**
- `ArrayList<Transacao> transacoes` - Lista de todas as transações

**Métodos principais:**
- `adicionarTransacao(Transacao t)` - Adiciona nova transação
- `listarTransacoes()` - Exibe todas as transações
- `calcularSaldo()` - Retorna saldo atual (receitas - despesas)
- `getTransacoes()` - Retorna lista de transações

#### 3. **GerenciadorDeArquivos.java**
Responsável pela persistência de dados.

**Formato do arquivo:** `transacoes.txt`
```
2025-10-18;3000.00;Salário;Receita;Trabalho
2025-10-18;45.50;Almoço;Despesa;Alimentação
```

**Métodos principais:**
- `salvar(ArrayList<Transacao>)` - Salva transações em arquivo
- `carregar()` - Carrega transações do arquivo
- Tratamento de erros com try-catch
- Mensagens informativas baseadas em quantidade de dados

#### 4. **CapitalizarPrimeiraLetra.java**
Utilitário para padronização de texto.

**Funcionalidade:**
- Converte strings para formato capitalizado (primeira letra maiúscula)
- Exemplo: "receita" → "Receita", "DESPESA" → "Despesa"
- Método estático para fácil utilização
- Tratamento de casos especiais (null, string vazia)

#### 5. **MainApp.java** (JavaFX)
Interface gráfica principal da aplicação.

**Componentes da UI:**

**Topo:**
- Título "Minhas Finanças"
- Label de saldo com formatação dinâmica (verde para positivo, vermelho para negativo)

**Centro:**
- TableView com 5 colunas:
    - Data (100px)
    - Descrição (250px)
    - Categoria (150px)
    - Tipo (100px)
    - Valor (120px formatado como R$ XX,XX)

**Rodapé:**
- Botão "➕ Adicionar Transação"
- Botão "🗑️ Remover"

**Funcionalidades:**

**Dialog de Adicionar Transação:**
- DatePicker para seleção de data (padrão: hoje)
- TextField para descrição com placeholder
- TextField para valor com validação numérica
- ComboBox de Tipo (Receita/Despesa)
- ComboBox de Categoria (Alimentação, Transporte, Lazer, Saúde, Educação, Salário, Outros)
- Validação de entrada com mensagens de erro
- Suporte para vírgula ou ponto como separador decimal

**Funcionalidade de Remover:**
- Seleção de linha na tabela
- Dialog de confirmação antes de deletar
- Alert caso nenhuma transação esteja selecionada

**Persistência automática:**
- Carrega dados ao iniciar (método `start()`)
- Salva automaticamente ao fechar janela (`setOnCloseRequest`)

#### 6. **Main.java** (Console - Versão Original)
Versão em linha de comando (mantida para referência).

Menu interativo com 4 opções:
1. Adicionar transação
2. Listar transações
3. Ver saldo
4. Sair (salva dados)

Usa `Scanner` para entrada de dados com `Locale` brasileiro (vírgula como decimal).

---

## 🚀 Como Executar

### Pré-requisitos
- Java 17 ou superior instalado
- Maven instalado
- IntelliJ IDEA (recomendado) ou outra IDE

### Passos

1. Clone o repositório
```bash
git clone [URL_DO_REPOSITORIO]
cd calculadora-gastos
```

2. Execute via Maven
```bash
mvn javafx:run
```

**Ou pelo IntelliJ:**
- Abra o painel Maven (lateral direita)
- Navegue: `calculadora-gastos` → `Plugins` → `javafx`
- Duplo clique em `javafx:run`

---

## ✨ Funcionalidades Atuais

- ✅ Adicionar transações (receitas e despesas)
- ✅ Remover transações selecionadas
- ✅ Visualização em tabela organizada
- ✅ Cálculo automático de saldo
- ✅ Saldo colorido (verde/vermelho)
- ✅ Persistência de dados em arquivo
- ✅ Carregamento automático ao iniciar
- ✅ Salvamento automático ao fechar
- ✅ Validação de entradas
- ✅ Seleção de data via calendário
- ✅ Categorias pré-definidas
- ✅ Interface responsiva (900x600px)

---

## 🗺️ Roadmap - Próximas Funcionalidades

### Fase 1: Filtros e Buscas 🔍
- [ ] Filtrar transações por período (dia, semana, mês, ano)
- [ ] Campo de busca por descrição
- [ ] Filtro por categoria
- [ ] Filtro por tipo (receita/despesa)
- [ ] Filtro por faixa de valor

### Fase 2: Relatórios 📊
- [ ] Gráfico de pizza: gastos por categoria
- [ ] Gráfico de linha: evolução mensal do saldo
- [ ] Gráfico de barras: receitas vs despesas por mês
- [ ] Relatório de resumo mensal
- [ ] Exportar para CSV
- [ ] Exportar para PDF
- [ ] Estatísticas (média, mediana, gastos totais)

### Fase 3: Melhorias Visuais 🎨
- [ ] Aplicar CSS customizado
- [ ] Tema claro/escuro (toggle)
- [ ] Ícones SVG profissionais
- [ ] Animações suaves de transição
- [ ] Dashboard com cards informativos
- [ ] Paleta de cores moderna
- [ ] Responsividade para diferentes tamanhos de tela

### Fase 4: Funcionalidades Avançadas 🚀
- [ ] Sistema de metas de gastos por categoria
- [ ] Alertas quando ultrapassar orçamento
- [ ] Categorias personalizadas (CRUD)
- [ ] Subcategorias
- [ ] Tags customizáveis
- [ ] Backup automático (local/nuvem)
- [ ] Histórico de backups
- [ ] Importar transações de CSV/Excel
- [ ] Transações recorrentes
- [ ] Anexar notas/observações
- [ ] Múltiplas contas/carteiras

### Fase 5: Banco de Dados 💾
- [ ] Migração de arquivo texto para SQLite
- [ ] Queries otimizadas
- [ ] Índices para melhor performance
- [ ] Suporte a múltiplos usuários
- [ ] Sistema de login/autenticação
- [ ] Criptografia de dados sensíveis
- [ ] Migrations versionadas

### Fase 6: Features Avançadas 🌟
- [ ] Sincronização em nuvem
- [ ] App mobile (Android/iOS)
- [ ] API REST para integração
- [ ] Integração com bancos (Open Banking)
- [ ] OCR para extrair dados de notas fiscais
- [ ] Machine Learning para categorização automática
- [ ] Previsão de gastos futuros

---

## 🎓 Aprendizados Técnicos

### Java Core
- Programação Orientada a Objetos (POO)
- Collections (ArrayList)
- File I/O (FileWriter, FileReader, BufferedWriter, BufferedReader)
- Exception handling (try-catch)
- LocalDate e manipulação de datas
- String manipulation (split, substring, format)
- Static methods vs instance methods

### JavaFX
- Application lifecycle
- Scene Graph (Stage, Scene, Layouts)
- Layouts: BorderPane, VBox, HBox, GridPane
- Componentes: TableView, Button, Label, TextField, ComboBox, DatePicker
- TableColumn e Cell Value Factories
- Dialogs e Alerts
- Event handling (setOnAction, setOnCloseRequest)
- CSS inline styling

### Maven
- Estrutura de projeto Maven
- Gerenciamento de dependências (pom.xml)
- Plugins (javafx-maven-plugin)
- Build lifecycle
- Execução via Maven (mvn javafx:run)

### Boas Práticas
- Separação de responsabilidades (MVC pattern básico)
- Nomenclatura descritiva de variáveis e métodos
- Comentários e documentação
- Validação de entradas do usuário
- Tratamento de erros com feedback visual
- Persistência de dados

---

## 📝 Notas de Desenvolvimento

### Decisões Técnicas

**Por que arquivo texto e não banco de dados?**
- Simplicidade para começar
- Fácil debug (arquivo legível)
- Sem dependências externas
- Migração planejada para SQLite na Fase 5

**Por que vírgula para decimais?**
- Padrão brasileiro
- Scanner configurado com `Locale` padrão do sistema
- Conversão implementada no dialog (aceita ambos)

**Por que JavaFX e não Swing?**
- JavaFX é mais moderno
- Melhor suporte a CSS
- Componentes mais bonitos out-of-the-box
- Futuro do Java para desktop

**Estrutura de arquivo (ponto-e-vírgula):**
- Fácil parsing com `split(";")`
- Evita conflitos com vírgulas em descrições
- Padrão CSV-like

### Desafios Superados

1. **JavaFX Runtime não encontrado**
    - Solução: Executar via `mvn javafx:run` em vez de diretamente

2. **Tabela não exibindo dados**
    - Solução: Configurar `setCellValueFactory` para cada coluna

3. **Scanner não aceitando ponto decimal**
    - Solução: `scanner.useLocale(Locale.US)` ou aceitar vírgula

4. **Formatação de moeda na tabela**
    - Solução: Custom `CellFactory` para formatar valores

---

## 🤝 Contribuindo

Este é um projeto pessoal de aprendizado, mas sugestões são bem-vindas!

### Como contribuir
1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/MinhaFeature`)
3. Commit suas mudanças (`git commit -m 'Adiciona MinhaFeature'`)
4. Push para a branch (`git push origin feature/MinhaFeature`)
5. Abra um Pull Request

---

## 📄 Licença

Este projeto é de código aberto para fins educacionais.

---

## 👤 Contatos




[Github](https://github.com/MatheusMV05)
<br>
[LinkedIn](https://www.linkedin.com/in/matheus-martins-8696422b8/)

---

## 🙏 Agradecimentos

- Comunidade JavaFX
- Documentação oficial Oracle
- Tutoriais e recursos online

---

**Última atualização:** Outubro 2025
**Versão atual:** 1.0.0 (MVP com interface gráfica)