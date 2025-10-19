import java.time.LocalDate;



public class Transacao {
    private LocalDate data;
    private double valor;
    private String descricao;
    private String tipo;
    private String categoria;

    Transacao(LocalDate data, double valor, String descricao, String tipo, String categoria){
        this.data = data;
        this.valor = valor;
        this.descricao = descricao;
        this.tipo = tipo;
        this.categoria = categoria;
    }

    public LocalDate getData(){
        return data;
    }

    public double getValor(){
        return valor;
    }

    public String getDescricao(){
        return descricao;
    }

    public String getTipo(){
        return tipo;
    }

    public String getCategoria(){
        return categoria;
    }

    public void setData(LocalDate data){
        this.data = data;
    }

    public void setValor(double valor){
        this.valor = valor;
    }

    public void setDescricao(String descricao){
        this.descricao = descricao;
    }

    public void setTipo(String tipo){
        this.tipo = tipo;
    }

    public void setCategoria(String categoria){
        this.categoria = categoria;
    }

    @Override
    public String toString(){
        return String.format("%s | %-15s | R$ %8.2f | %-12s | %s", data, descricao, valor, tipo, categoria);
    }

}
