/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TabelaSimbolos;

import modulo_completo.*;

/**
 * @author Lucas Carneiro
 * @author Oto Lopes
 */
public class Simbolo {

    public static final String INT = "int";
    public static final String CHAR = "char";
    public static final String STRING = "string";
    public static final String FLOAT = "float";
    public static final String BOOL = "bool";
    public static final int OBJECT = 5;
    public static final int VOID = 6;

//    public static final int VAR = 10;
//    public static final int CONST = 11;
//    public static final int MET = 12;
//    public static final int CLASS = 13;
//    public static final int VET = 14;
//    public static final int MAIN = 15;

    private String lexeme;
    private Category categoria;
    private String tipo;

    public Simbolo(Category categoria) {
        this.categoria = categoria;
        lexeme = null;
        tipo = null;
    }
  
    public String getLexeme() {
        return lexeme;
    }

    public void setLexeme(String lexeme) {
        this.lexeme = lexeme;
    }

    public Category getCategoria() {
        return categoria;
    }

    public void setCategoria(Category categoria) {
        this.categoria = categoria;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "categoria: " + categoria + " tipo: "+ tipo + " " + lexeme + "\n"; //To change body of generated methods, choose Tools | Templates.
    }
    
}
