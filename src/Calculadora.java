import java.util.ArrayList;
import java.time.LocalDate;

public class Calculadora {
    private ArrayList<Transacao> transacoes;

    public ArrayList<Transacao> getTransacoes() {
        return transacoes;
    }

    public Calculadora(ArrayList<Transacao> transacoesIniciais) {
        this.transacoes = transacoesIniciais;
    }

    public void adicionarTransacao(Transacao t){
        transacoes.add(t);
    }

    public void listarTransacoes(){
        for(Transacao t:transacoes){
            System.out.println(t);
        }
    }

    public double calcularSaldo(){
        double receitaTotal = 0;
        double despesaTotal = 0;
        for(Transacao t:transacoes){
            if(t.getTipo().equals("Receita")){
                receitaTotal += t.getValor();
            }else if(t.getTipo().equals("Despesa")){
                despesaTotal += t.getValor();
            }
        }
        return receitaTotal - despesaTotal;
    }

}
