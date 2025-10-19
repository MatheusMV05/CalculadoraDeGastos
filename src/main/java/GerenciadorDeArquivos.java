import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class GerenciadorDeArquivos {
    private static final String NOME_ARQUIVO = "transações.txt";

    public static void salvar(ArrayList<Transacao> transacoes){
        try{
            FileWriter fw = new FileWriter(NOME_ARQUIVO);
            BufferedWriter bw = new BufferedWriter(fw);

            for(Transacao t :  transacoes){
                String linha = t.getData() + ";" + t.getValor() + ";" + t.getDescricao() + ";" + t.getTipo() + ";" + t.getCategoria();

                bw.write(linha);
                bw.newLine();
            }

            bw.close();
            if (!transacoes.isEmpty()) {
                System.out.println(transacoes.size() + " transação(ões) carregada(s)!");
            } else {
                System.out.println("Nenhuma transação encontrada. Começando do zero.");
            }


        } catch (IOException e){
            System.out.println("Erro ao salvar o arquivo!" +  e.getMessage());
        }
    }

    public static ArrayList<Transacao> carregar(){
        ArrayList<Transacao> transacoes = new ArrayList<>();

        try{
            FileReader fr = new FileReader(NOME_ARQUIVO);
            BufferedReader br = new BufferedReader(fr);

            String linha;

            while((linha = br.readLine()) != null) {

                String[] partes = linha.split(";");

                LocalDate data = LocalDate.parse(partes[0]);
                double valor = Double.parseDouble(partes[1]);
                String descricao = partes[2];
                String tipo = partes[3];
                String categoria = partes[4];

                Transacao t = new Transacao(data, valor, descricao, tipo, categoria);
                transacoes.add(t);
            }

            br.close();
            if(!transacoes.isEmpty()){
                System.out.println("Dados carregados com sucesso!");
            }

        }catch (FileNotFoundException e){
            System.out.println("Nenhum arquivo encontrado. Começando do zero.");
        }catch (IOException e){
            System.out.println("Erro ao carregar: " + e.getMessage());
        }

        return transacoes;
    }






}
