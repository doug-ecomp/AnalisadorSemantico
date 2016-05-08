package modulo_analisadorLexico;

import manipulação_arquivosIO.Arquivo;

/**
 * Classe destinada ao armazenamento dos tokens encontrados em um código fonte, 
 * juntamente com o seu determinado valor e sua posição (linha e coluna) no 
 * arquivo de origem.
 *
 * @author Lucas Carneiro
 * @author Oto Lopes
 * 
 * @see EstruturaLexica
 * @see Arquivo
 */
public class Token {

    /**
     * Token propriamente dito (Palavra Reservada, Identificador ou Operador,
     * por exemplo).
     */
    private final String tipo;
    /**
     * Lexema referente ao token.
     */
    private final String valor;
    /**
     * Linha onde se encontra o inicio deste lexema.
     */
    private final int linha;
    /**
     * Coluna onde se inicia este lexema.
     */
    private final int coluna;

    /**
     * Construtor da classe.
     *
     * @param tipo O token (Delimitador, Identificador ou Operador, por exemplo)
     * @param valor Lexema (valor refente ao token)
     * @param linha Linha de início do lexema
     * @param coluna Coluna onde se inicia o lexema
     */
    public Token(String valor, String tipo, int linha, int coluna) {

        this.tipo = tipo;
        this.valor = valor;
        this.linha = linha;
        this.coluna = coluna;
    }
    
    /**
     * Retorna qual o tipo deste token.
     * 
     * @return Qual é o token  
     */
    public String getTipo() {
        return this.tipo;
    }
    
    /**
     * Retorna o lexema deste token
     * 
     * @return O lexema referente a este token
     */
    public String getValor() {
        return this.valor;
    }
    
    /**
     * Retorna a linha referente a ocorrencia do lexema.
     * 
     * @return A linha referente a ocorrencia do lexema
     */
    public int getLinha() {
        return this.linha;
    }
    
    /**
     * Retorna a coluna refente ao inicio do lexema.
     * 
     * @return A coluna onde se inicia o lexema
     */
    public int getColuna() {
        return this.coluna;
    }
}