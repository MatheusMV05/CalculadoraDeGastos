import java.time.LocalDate;
import java.util.Scanner;
// Antiga main com comandos so pelo terminal
public class Main {
    public static void main(String[] args) {
        Calculadora calc = new Calculadora(GerenciadorDeArquivos.carregar());
        Scanner input = new Scanner(System.in);


        while(true){
            System.out.println("\n=== CALCULADORA DE GASTOS ===");
            System.out.println("1. Adicionar Transação");
            System.out.println("2. Listar Transação");
            System.out.println("3. Ver Saldo");
            System.out.println("4. Sair");

            int opcao = input.nextInt();
            input.nextLine();

            if(opcao == 1){
                System.out.println("\n=== NOVA TRANSAÇÃO ===");

                System.out.println("Descrição: ");
                String descricao = input.nextLine();

                System.out.println("Valor: R$ ");
                double valor = input.nextDouble();

                input.nextLine();

                System.out.println("Tipo (Receita/Despesa): ");
                String tipo = input.nextLine();
                tipo = CapitalizarPrimeiraLetra.converter(tipo);

                System.out.println("Categoria: ");
                String categoria = input.nextLine();
                categoria = CapitalizarPrimeiraLetra.converter(categoria);

                Transacao nova = new Transacao(LocalDate.now(), valor, descricao, tipo, categoria);
                calc.adicionarTransacao(nova);
                System.out.println("Transação adicionada com sucesso!");
            }else if(opcao == 2){
                calc.listarTransacoes();
            }else if(opcao == 3){
                System.out.printf("Saldo atual: R$ %.2f\n", calc.calcularSaldo());
            }else if(opcao == 4){
                System.out.println("Salvando...");
                GerenciadorDeArquivos.salvar(calc.getTransacoes());
                System.out.println("Fechando sistema...");
                break;
            }else{
                System.out.println("Opção invalida tente novamente.");
            }
        }

        input.close();


    }
}