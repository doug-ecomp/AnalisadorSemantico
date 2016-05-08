package modulo_completo;

import java.util.ArrayList;
import modulo_analisadorLexico.Token;

/**
 * Classe responsável por armazenar informações sobre identificadores. 
 *
 * @author Lucas Carneiro
 * @author Oto Lopes
 *
 * @see Token
 */
public class TabelaSimbolos {
    
    private final ArrayList<Simbolos> tabela;
    
    /**
     *
     */
    public TabelaSimbolos() {
        tabela = new ArrayList<>();
    }
    
    
    /**
     * Adiciona um identificador à tabela.
     * @param simbolo
     */
    public void add(Simbolos simbolo) {
        tabela.add(simbolo);
    }

    @Override
    public String toString() {
        return tabela.toString(); //To change body of generated methods, choose Tools | Templates.
    }
    
}