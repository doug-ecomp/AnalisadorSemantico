package modulo_analisadorLexico;

import java.util.ArrayList;

/**
 * Classe que representa a estrutura léxica que rege o compilador.
 *
 * @author Lucas Carneiro
 * @author Oto Lopes
 *
 * @see Token
 * @see AnalisadorLexico
 */
public class EstruturaLexica {

    /**
     * Lista com as palavras reservadas.
     */
    private final ArrayList<String> palavrasReservadas;
    /**
     * Lista com letras de 'a' a 'z' (maiúsculas e minúsculas).
     */
    private final ArrayList<Character> letra;
    /**
     * Lista com os digitos de 0 a 9.
     */
    private final ArrayList<Character> digito;
    /**
     * Lista com os simbolos de 32 a 126 da tablea ASCII.
     */
    private final ArrayList<Character> simbolo;
    /**
     * Lista de operadores.
     */
    private final ArrayList<Character> operadores;
    /**
     * Lista de delimitadores.
     */
    private final ArrayList<Character> delimitadores;
    /**
     * Lista com identificador de inicio de comentário.
     */
    private final ArrayList<String> comentarios;

    /**
     * Construtor da classe, inserindo todos os elementos das listas.
     */
    public EstruturaLexica() {

        // Criando listas da estrutura
        this.palavrasReservadas = new ArrayList<>();
        this.letra = new ArrayList<>();
        this.digito = new ArrayList<>();
        this.simbolo = new ArrayList<>();
        this.operadores = new ArrayList<>();
        this.delimitadores = new ArrayList<>();
        this.comentarios = new ArrayList<>();

        // Inserindo palavras reservadas.
        this.palavrasReservadas.add("class");
        this.palavrasReservadas.add("const");
        this.palavrasReservadas.add("else");
        this.palavrasReservadas.add("if");
        this.palavrasReservadas.add("new");
        this.palavrasReservadas.add("read");
        this.palavrasReservadas.add("write");
        this.palavrasReservadas.add("return");
        this.palavrasReservadas.add("void");
        this.palavrasReservadas.add("while");
        this.palavrasReservadas.add("int");
        this.palavrasReservadas.add("float");
        this.palavrasReservadas.add("bool");
        this.palavrasReservadas.add("string");
        this.palavrasReservadas.add("char");
        this.palavrasReservadas.add("true");
        this.palavrasReservadas.add("false");
        this.palavrasReservadas.add("main");

        // Inserindo letras de a..z e A..Z.
        for (char i = 'a'; i <= 'z'; i++) {
            this.letra.add((char) i);
        }
        for (char i = 'A'; i <= 'Z'; i++) {
            this.letra.add((char) i);
        }

        // Inserindo dígitos 0..9.
        for (char i = '0'; i <= '9'; i++) {
            this.digito.add(i);
        }

        // Inserindo inserindo códigos ASCII do 32 ao 126.
        for (int i = 32; i <= 126; i++) {
            this.simbolo.add((char) i);
        }

        // Inserindo operadores.
        this.operadores.add('.');
        this.operadores.add('+');
        this.operadores.add('-');
        this.operadores.add('*');
        this.operadores.add('/');
        this.operadores.add('+');
        this.operadores.add('-');
        this.operadores.add('!');
        this.operadores.add('>');
        this.operadores.add('<');
        this.operadores.add('&');
        this.operadores.add('|');
        this.operadores.add('=');

        // Inserindo delimitadores.
        this.delimitadores.add(';');
        this.delimitadores.add(',');
        this.delimitadores.add('(');
        this.delimitadores.add(')');
        this.delimitadores.add('{');
        this.delimitadores.add('}');
        this.delimitadores.add('[');
        this.delimitadores.add(']');

        // Inserindo simbolos de comentarios.
        this.comentarios.add("*/");
        this.comentarios.add("/*");
        this.comentarios.add("//");
    }

    /**
     * Verifica se o caractere enviado é um espaço em branco ou um caractere de tabulação.
     * 
     * @param ch Espaço/TAB encontrado no código
     * 
     * @return Verdadeiro se o char enviado for um espaço em branco ou caractere de tabulação, Falso caso contrário
     */
    public boolean ehEspaco(char ch){
        
        return (Character.isSpaceChar(ch) || ch == 9);
    }
            
    /**
     * Verifica se a palavra enviada é uma palavra reservada.
     *
     * @param pReservada Palavra encontrada no código
     *
     * @return Verdadeiro se a palavra for reservada, Falso caso contrário
     */
    public boolean ehPalavraReservada(String pReservada) {

        return this.palavrasReservadas.contains(pReservada);
    }

    /**
     * Verifica se o caractere enviado é uma letra.
     *
     * @param letra Letra encontrada no código
     *
     * @return Verdadeiro se o char enviado for uma letra, Falso caso contrário
     */
    public boolean ehLetra(char letra) {

        return this.letra.contains(letra);
    }
    
    /**
     * Verifica se o caractere enviado é um digito.
     *
     * @param digito Digito encontrado no código
     *
     * @return Verdadeiro se o char enviado for um digito, Falso caso contrário
     */
    public boolean ehDigito(char digito) {

        return this.digito.contains(digito);
    }

    /**
     * Verifica se o símbolo enviado é um símbolo válido.
     *
     * @param simb Símbolo encontrado no código
     *
     * @return Verdadeiro se o símbolo enviado for um símbolo válido, Falso caso
     * contrário
     */
    public boolean ehSimbolo(char simb) {

        return this.simbolo.contains(simb);
    }

    /**
     * Verifica se o símbolo enviado é um operador.
     *
     * @param ope Operador encontrado no código
     *
     * @return Verdadeiro se o símbolo enviado for um operador, Falso caso
     * contrário
     */
    public boolean ehOperador(char ope) {

        return this.operadores.contains(ope);
    }

    /**
     * Verifica se o símbolo enviado é um delimitador.
     *
     * @param delim Delimitador encontrado no código
     *
     * @return Verdadeiro se o símbolo enviado for um delimitador, Falso caso
     * contrário
     */
    public boolean ehDelimitador(char delim) {

        return this.delimitadores.contains(delim);
    }
}