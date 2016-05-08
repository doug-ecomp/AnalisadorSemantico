/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulo_completo;

/**
 * @author Lucas Carneiro
 * @author Oto Lopes
 */
public class Simbolos {

    public static final int INT = 0;
    public static final int CHAR = 1;
    public static final int STRING = 2;
    public static final int FLOAT = 3;
    public static final int BOOL = 4;
    public static final int OBJECT = 5;
    public static final int VOID = 6;

    public static final int VAR = 10;
    public static final int CONST = 11;
    public static final int MET = 12;
    public static final int CLASS = 13;
    public static final int VET = 14;
    public static final int MAIN = 15;

    private String nome;
    private Object valor;
    private int categoria;
    private int tipo;
    private TabelaSimbolos filhos;

    public Simbolos() {
        filhos = new TabelaSimbolos();
    }


    /**
     *
     * @param filho
     */
    public void addFilho(Simbolos filho) {
        filhos.add(filho);
    }

    /**
     *
     * @param valor
     */
    public void setValor(Object valor) {
        this.valor = valor;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getCategoria() {
        return categoria;
    }

    public void setCategoria(int categoria) {
        this.categoria = categoria;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public TabelaSimbolos getFilhos() {
        return filhos;
    }

    public void setFilhos(TabelaSimbolos filhos) {
        this.filhos = filhos;
    }

    @Override
    public String toString() {
        return "categoria: " + categoria + " tipo: "+ tipo + " " + nome + filhos.toString() + "\n"; //To change body of generated methods, choose Tools | Templates.
    }
    
}
