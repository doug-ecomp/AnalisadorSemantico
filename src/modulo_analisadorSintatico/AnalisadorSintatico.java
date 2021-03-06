/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulo_analisadorSintatico;

import TabelaSimbolos.MyHashMap;
import java.util.ArrayList;
import java.util.HashMap;
import modulo_analisadorLexico.Token;
import modulo_completo.Compilador;
import modulo_analisadorSemantico.AnalisadorSemantico;
import TabelaSimbolos.Simbolo;
import TabelaSimbolos.SimboloClasse;
import TabelaSimbolos.SimboloMetodo;
import modulo_completo.Category;
import modulo_completo.Type;

/**
 * Classe responsável pela análise sintatica dos códigos fontes. Os metodos de
 * reconhecimento sao baseados nas produçoes da gramatica. A gramatica utilizada
 * foi a versao 5.4.
 *
 * @author Lucas Carneiro
 * @author Oto Lopes
 *
 * @see Token
 * @see Compilador
 */
public class AnalisadorSintatico {

    private Token proximo;              //token atual em análise
    private ArrayList<Token> tokens;    //lista com os tokens recebidos
    private ArrayList<String> erros;    //lista com os erros encontrados na análise.
    private int contTokens = 0;         //contador que aponta para o proximo token da lista
    //private Simbolo escopo;            //salvar o escopo atual da tabela de simbolos
    private Simbolo atual;             //simbolo atual
    private AnalisadorSemantico semantico;

    /**
     * Construtor do analisador Sintatico.
     *
     * 
     * @param semantico Instância do Analisador Semantico
     */
    public AnalisadorSintatico(AnalisadorSemantico semantico) {
        this.semantico = semantico;
        
    }

    /**
     * Metodo responsavel pela analise sintatica dos codigos fontes.
     *
     * @param tokens lista com os tokens vindos do lexico.
     */
    public void analise(ArrayList<Token> tokens) {
        this.tokens = tokens; //recebe os tokens vindos do lexico.
        proximo = proximo();  //recebe o primeiro token da lista
        erros = new ArrayList<>(); //cria a lista de erros
        recArquivo();   //inicia a analise do arquivo
    }

    /**
     * Método que retorna os erros encontrados durante a análise sintatica.
     *
     * @return lista de erros sintaticos encontrados.
     */
    public ArrayList<String> getErros() {
        return erros;
    }

    /**
     * Metodo para capturar o proximo token para ser analisado.
     *
     * @return o proximo token da lista de tokens.
     *
     */
    private Token proximo() {
        if (contTokens < tokens.size()) { //verifica se ainda possuem tokens para a analise.
            return tokens.get(contTokens++);
        } else {
            return new Token("EOF", "EOF", 0, 0);  //cria um token de fim de arquivo. 
        }
    }

    /**
     * Metodo para normalizaçao dos erros encontrados.
     */
    private void erroSintatico(String erro) {
        if (!proximo.getValor().equals("EOF")) {
            erros.add(proximo.getLinha() + " " + erro + "\n"); //gera o erro normalizado e adiciona na lista de erros.
        } else {
            erros.add(erro);
        }
    }

    /**
     * Metodo para verificar terminais.
     *
     * @param esperado valor do terminal esperado no proximo token.
     */
    private void terminal(String esperado) {
        if ((!proximo.getValor().equals("EOF")) && proximo.getValor().equals(esperado)) { //verifica se o token atual e o que era esperado
            proximo = proximo();
        } else {
            erroSintatico("falta " + esperado); //gera o erro se o token nao e o esperado 
        }
    }

    /**
     * Metodo para verificar os tipos.
     *
     * @param esperado valor do tipo esperado no proximo token.
     */
    private void Tipo(String esperado) {
        if (!proximo.getValor().equals("EOF") && proximo.getTipo().equals(esperado)) { //verifica se o tipo do token atual e o que era esperado
            proximo = proximo();
        } else {
            erroSintatico("falta " + esperado); //gera o erro se o tipo do token nao e o esperado 
        }
    }

    /**
     * Metodo para o reconhecimento do arquivo.
     */
    private void recArquivo() {
        recConstantes(); //reconhece as constante
        atual = null;
        recVariaveis(); //reconhece as variaveis
        atual=null;
        recPreMain();  //reconhece as classes e o metodo main
    }

    /**
     * Metodo para reconhecimento de classes antes do metodo main e do metodo
     * main.
     */
    private void recPreMain() {
        if (!proximo.getValor().equals("EOF")) {
            switch (proximo.getValor()) {
                case "void":  //verifica se e uma classe ou o metodo main
                    recMain();
                    recClasses();
                    break;
                case "class":
                    recClasse();
                    recPreMain();
                    break;
                default:
                    while (!proximo.getValor().equals("void") && !proximo.getValor().equals("class")) { //recuperaçao de erro, busca uma classe ou main no arquivo
                        erroSintatico("falta palavra reservada: class, void");
                        proximo = proximo();
                    }
                    recPreMain();
                    break;
            }
        } else {
            erroSintatico("falta palavra reservada: class, void");
        }
    }

    /**
     * Metodo para reconhecimento de varias classes.
     */
    private void recClasses() {
        switch (proximo.getValor()) {
            case "class":
                recClasse();
                recClasses();
                break;
            default:
                break;
        }
    }

    /**
     * Metodo para verificar varias constantes.
     */
    private void recConstantes() {
        switch (proximo.getValor()) {
            case "const":
                recConst();
                recConstantes();
                break;
            default:
                break;
        }
    }

    /**
     * Metodo para reconhecimento de variaveis.
     */
    private void recVariaveis() {
        atual = new Simbolo(Category.VARIAVEL);  //criaçao de um simbolo para adicionar na tabela de simbolos.
        switch (proximo.getValor()) {
            case "char":
                atual.setTipo(Simbolo.CHAR);  //salva o tipo do simbolo.
                recDeclaracaoVariavel();
                recVariaveis();
                break;
            case "int":
                atual.setTipo(Simbolo.INT); //salva o tipo do simbolo.
                recDeclaracaoVariavel();
                recVariaveis();
                break;
            case "bool":
                atual.setTipo(Simbolo.BOOL); //salva o tipo do simbolo.
                recDeclaracaoVariavel();
                recVariaveis();
                break;
            case "string":
                atual.setTipo(Simbolo.STRING); //salva o tipo do simbolo.
                recDeclaracaoVariavel();
                recVariaveis();
                break;
            case "float":
                atual.setTipo(Simbolo.FLOAT); //salva o tipo do simbolo.
                recDeclaracaoVariavel();
                recVariaveis();
                break;
            default:
                break;
        }
    }

    /**
     * Metodo para reconhecimento da main.
     */
    private void recMain() {
        
        atual = new SimboloMetodo(Category.METODO); //cria um simbolo.
        atual.setTipo(Simbolo.VOID);
        atual.setLexeme("main");
                
        terminal("void");
        
        atual.setLinha(proximo.getLinha());
        semantico.addSimbolo(atual);
        
        terminal("main");
        terminal("(");
        terminal(")");
        terminal("{");
        
        semantico.setEscopo_atual(new MyHashMap<>(semantico.getEscopo_global()));
        
        recConteudoMetodo();
        
        semantico.setEscopo_atual(semantico.getEscopo_global());
        
        terminal("}");
        
    }

    /**
     * Metodo para reconhecimento de classe.
     */
    private void recClasse() {
        switch (proximo.getValor()) {
            case "class":
                
                atual = new SimboloClasse(Category.CLASSE, new MyHashMap<>(semantico.getEscopo_global()));
                ((SimboloClasse)atual).getInner_scope().setDono((SimboloClasse)atual);
                
                terminal("class");
                
                if(proximo.getTipo().equals("id")){
                    atual.setLexeme(proximo.getValor());
                    atual.setLinha(proximo.getLinha());
                    semantico.addSimbolo(atual);
                }
                
                Tipo("id");
                recExpressaoHerenca();
                terminal("{");
                
                semantico.setEscopo_atual(((SimboloClasse)atual).getInner_scope());
                atual = null;
                
                recConteudoClasse();
                
                semantico.setEscopo_atual(semantico.getEscopo_global());
                
                terminal("}");
                break;
            default:
                erroSintatico("Classe com erro.");
                break;
        }
    }
    
    /**
     * Metodo para reconhecimento de herança.
     */
    private void recExpressaoHerenca() {
        switch (proximo.getValor()) {
            case ">":
                terminal(">");
                
                
                ArrayList<Category> aux = new ArrayList<>();
                aux.add(Category.CLASSE);
                if(proximo.getTipo().equals("id") && 
                        (semantico.MatchTipoAndCatg(proximo.getValor(), aux, null, semantico.getEscopo_global(), proximo.getLinha()) )  ){
                    
                    Simbolo sy2 = semantico.getEscopo_global().get(proximo.getValor());
                    ((SimboloClasse) atual).setHeranca_pai((SimboloClasse)sy2);
                    
                }
                
                Tipo("id");
                break;
            default:
                break;
        }

    }

    /**
     * Metodo para reconhecimento de conteudo de classe.
     */
    private void recConteudoClasse() {
        switch (proximo.getValor()) {
            case "const":
                recConst();
                recConteudoClasse();
                break;
            case "void":
                recIdDeclaracao();
                recConteudoClasse();
                break;
            default:
                if (proximo.getTipo().equals("palavra_reservada") || proximo.getTipo().equals("id")) {
                    recIdDeclaracao();
                    recConteudoClasse();
                    break;
                } else if (!proximo.getValor().equals("}") && !proximo.getValor().equals("class")) { //recuperaçao do erro, verifica se acabou o bloco, ou surgiu outra classe
                    erroSintatico("falta declaraçao de variavel ou de metodo");
                    proximo = proximo();
                    recConteudoClasse();
                }
                break;
        }
    }

    private void recConst() {
        switch (proximo.getValor()) {
            case "const":
                terminal("const");
                terminal("{");
                recBlocoConstantes();
                terminal("}");
                break;
            default:
                erroSintatico("Esperava um bloco de contantes");
                break;
        }
    }

    private void recBlocoConstantes() {
        atual = new Simbolo(Category.CONSTANTE);
        switch (proximo.getValor()) {
            case "char":
                terminal("char");
                atual.setTipo(Simbolo.CHAR);
                recListaConst();
                break;
            case "int":
                terminal("int");
                atual.setTipo(Simbolo.INT);
                recListaConst();
                break;
            case "bool":
                terminal("bool");
                atual.setTipo(Simbolo.BOOL);
                recListaConst();
                break;
            case "string":
                terminal("string");
                atual.setTipo(Simbolo.STRING);
                recListaConst();
                break;
            case "float":
                terminal("float");
                atual.setTipo(Simbolo.FLOAT);
                recListaConst();
                break;
            default:
                if (!proximo.getValor().equals("}")) {
                    erroSintatico("falta palavra reservada: int, char, bool, string, float");
                    proximo = proximo();
                    recBlocoConstantes();
                }
                break;
        }
    }

    private void recListaConst() {
        if(proximo.getTipo().equals("id")){
            atual.setLexeme(proximo.getValor());
            atual.setLinha(proximo.getLinha());
            semantico.addSimbolo(atual);
        }
        Tipo("id");
        terminal("=");
        recAtribuicaoConstante();
        recAuxiliarDeclaracao();

    }

    private void recAtribuicaoConstante() {
        switch (proximo.getTipo()) {
            case "numero":
                semantico.matchTypeNum(proximo.getValor(), atual.getTipo(), proximo.getLinha());
                Tipo("numero");
                break;
            case "cadeia_constante":
                semantico.MatchType(proximo.getValor(), Simbolo.STRING, atual.getTipo(), proximo.getLinha());
                Tipo("cadeia_constante");
                break;
            case "caractere_constante":
                semantico.MatchType(proximo.getValor(), Simbolo.CHAR, atual.getTipo(), proximo.getLinha());
                Tipo("caractere_constante");
                break;
            default:
                if (proximo.getValor().equals("true")) {
                    semantico.MatchType(proximo.getValor(), Simbolo.BOOL, atual.getTipo(), proximo.getLinha());
                    terminal("true");
                } else if (proximo.getValor().equals("false")) {
                    semantico.MatchType(proximo.getValor(), Simbolo.BOOL, atual.getTipo(), proximo.getLinha());
                    terminal("false");
                } else {
                    erroSintatico("falta numero, cadeia constante, caracter constante ou boolean");
                }
                break;
        }
    }

    private void recAuxiliarDeclaracao() {
        switch (proximo.getValor()) {
            case ",":
                terminal(",");
                recListaConst();
                break;
            case ";":
                terminal(";");
                recBlocoConstantes();
                break;
            default:
                erroSintatico("falta , ou ;");
                break;
        }
    }

    private void recDeclaracaoVariavel() {
        switch (proximo.getValor()) {
            case "char":
                terminal("char");
                
                if(proximo.getTipo().equals("id")){
                    atual.setLexeme(proximo.getValor());
                    atual.setLinha(proximo.getLinha());
                    semantico.addSimbolo(atual);
                }
                
                Tipo("id");
                recListaVariavel();
                break;
            case "int":
                terminal("int");
                
                if(proximo.getTipo().equals("id")){
                    atual.setLexeme(proximo.getValor());
                    atual.setLinha(proximo.getLinha());
                    semantico.addSimbolo(atual);
                }
                
                Tipo("id");
                recListaVariavel();
                break;
            case "bool":
                terminal("bool");
                
                if(proximo.getTipo().equals("id")){
                    atual.setLexeme(proximo.getValor());
                    atual.setLinha(proximo.getLinha());
                    semantico.addSimbolo(atual);
                }
                
                Tipo("id");
                recListaVariavel();
                break;
            case "string":
                terminal("string");
                
                if(proximo.getTipo().equals("id")){
                    atual.setLexeme(proximo.getValor());
                    atual.setLinha(proximo.getLinha());
                    semantico.addSimbolo(atual);
                }
                
                Tipo("id");
                recListaVariavel();
                break;
            case "float":
                terminal("float");
                
                if(proximo.getTipo().equals("id")){
                    atual.setLexeme(proximo.getValor());
                    atual.setLinha(proximo.getLinha());
                    semantico.addSimbolo(atual);
                }
                
                Tipo("id");
                recListaVariavel();
                break;
            default:
                break;
        }
    }

    private void recIdDeclaracao() {
        atual = new Simbolo();
        switch (proximo.getValor()) {
            case "void":
                atual = new SimboloMetodo(Category.METODO);
                atual.setTipo(Simbolo.VOID);
                
                terminal("void");
                
                if(proximo.getTipo().equals("id")){
                    atual.setLexeme(proximo.getValor());
                    atual.setLinha(proximo.getLinha());
                }
                
                Tipo("id");
                terminal("(");
                
                semantico.setEscopo_atual(new MyHashMap<>(semantico.getEscopo_atual()));
               
                recDeclParametros();
                terminal(")");
                terminal("{");
                recConteudoMetodo();
                
                semantico.setEscopo_atual(semantico.getEscopo_atual().getUpper_scope());
                
                terminal("}");
                break;
            case "char":
                terminal("char");
                
                atual.setTipo(Simbolo.CHAR);
                if(proximo.getTipo().equals("id")){
                    atual.setLexeme(proximo.getValor());
                    atual.setLinha(proximo.getLinha());
                }
                
                Tipo("id");
                recCompId();
                break;
            case "int":
                terminal("int");
                
                atual.setTipo(Simbolo.INT);
                if(proximo.getTipo().equals("id")){
                    atual.setLexeme(proximo.getValor());
                    atual.setLinha(proximo.getLinha());
                }
                
                Tipo("id");
                recCompId();
                break;
            case "bool":
                terminal("bool");
                
                atual.setTipo(Simbolo.BOOL);
                if(proximo.getTipo().equals("id")){
                    atual.setLexeme(proximo.getValor());
                    atual.setLinha(proximo.getLinha());
                }
                
                Tipo("id");
                recCompId();
                break;
            case "string":
                terminal("string");
                
                atual.setTipo(Simbolo.STRING);
                if(proximo.getTipo().equals("id")){
                    atual.setLexeme(proximo.getValor());
                    atual.setLinha(proximo.getLinha());
                }
                
                Tipo("id");
                recCompId();
                break;
            case "float":
                terminal("float");
                
                atual.setTipo(Simbolo.FLOAT);
                if(proximo.getTipo().equals("id")){
                    atual.setLexeme(proximo.getValor());
                    atual.setLinha(proximo.getLinha());
                }
                
                Tipo("id");
                recCompId();
                break;
            default:
                if (proximo.getTipo().equals("id")) {
                    
                    ArrayList<Category> aux = new ArrayList<>();
                    aux.add(Category.CLASSE);
                    if(semantico.MatchTipoAndCatg(proximo.getValor(), aux, Simbolo.CLASSE, semantico.getEscopo_global(), proximo.getLinha()))
                        atual.setTipo(proximo.getValor());
                    
                    Tipo("id");
                    
                    if(proximo.getTipo().equals("id")){
                        atual.setLexeme(proximo.getValor());
                        atual.setLinha(proximo.getLinha());
                    }
                    
                    Tipo("id");
                    recCompId();
                    break;
                } else {
                    erroSintatico("espera um tipo: id, int, float, char, string, bool, void");
                }
                break;
        }
    }

    private void recCompId() {
        switch (proximo.getValor()) {
            case "[":
                
                atual.setCategoria(Category.VETOR);
                if(atual.getLexeme() != null && atual.getTipo()!=null ){
                    semantico.addSimbolo(atual);
                }
                
                terminal("[");
                recIndice();
                terminal("]");
                recListaVetor();
                break;
            case "(":
                
                Simbolo temp = atual;
                atual = new SimboloMetodo(Category.METODO);
                atual.setLexeme(temp.getLexeme());
                atual.setTipo(temp.getTipo());
                atual.setLinha(temp.getLinha());
                
                terminal("(");
                
                semantico.setEscopo_atual(new MyHashMap<>(semantico.getEscopo_atual()));
                
                recDeclParametros();
                terminal(")");
                terminal("{");
                recConteudoMetodo();
                terminal("return");
                recRetorno();
                
                semantico.setEscopo_atual(semantico.getEscopo_atual().getUpper_scope());
                
                terminal("}");
                break;
            case ",":
                
                atual.setCategoria(Category.VARIAVEL);
                if(atual.getLexeme() != null && atual.getTipo()!=null ){
                    semantico.addSimbolo(atual);
                }
                
                recListaVariavel();
                break;
            case ";":
                
                atual.setCategoria(Category.VARIAVEL);
                if(atual.getLexeme() != null && atual.getTipo()!=null ){
                    semantico.addSimbolo(atual);
                }
                
                terminal(";");
                break;
            default:
                erroSintatico("falta ; ou , ou [ ou (");
                break;
        }
    }

    private void recListaVariavel() {
        switch (proximo.getValor()) {
            case ",":
                terminal(",");
  
                if(proximo.getTipo().equals("id")){
                    atual.setLexeme(proximo.getValor());
                    atual.setLinha(proximo.getLinha());
                    semantico.addSimbolo(atual);
                }
                
                Tipo("id");
                recListaVariavel();
                break;
            case ";":
                terminal(";");
                break;
            default:
                while (!proximo.getValor().equals(",") && !proximo.getValor().equals(";") && !proximo.getTipo().equals("palavra_reservada")) {
                    erroSintatico("falta , ou ;");
                    proximo = proximo();
                }
                break;
        }
    }

    private void recListaVetor() {
        switch (proximo.getValor()) {
            case ",":
                terminal(",");
  //              int aux = atual.getTipo();
  //              escopo.addFilho(atual);
  //              atual = new Simbolo();
  //              atual.setCategoria(Simbolo.VET);
  //              atual.setTipo(aux);
  //              atual.setLexeme(proximo.getValor());
                Tipo("id");
                terminal("[");
                recIndice();
                terminal("]");
                recListaVetor();
                break;
            case ";":
  //              escopo.addFilho(atual);
                terminal(";");
                break;
            default:
                while (!proximo.getValor().equals(",") && !proximo.getValor().equals(";") && !proximo.getTipo().equals("palavra_reservada")) {
                    erroSintatico("falta , ou ;");
                    proximo = proximo();
                }
                break;
        }
    }

    private void recIndice() {
        switch (proximo.getTipo()) {
            case "id":
                
                ArrayList<Category> aux = new ArrayList<>();
                aux.add(Category.VARIAVEL);
                aux.add(Category.CONSTANTE);
                semantico.MatchTipoAndCatg(proximo.getValor(), aux, Simbolo.INT, semantico.getEscopo_atual(), proximo.getLinha());
                
                Tipo("id");
                break;
            case "numero":
                
                semantico.matchTypeNum(proximo.getValor(), Simbolo.INT, proximo.getLinha());
                
                Tipo("numero");
                break;
            default:
                erroSintatico("falta identificador ou numero");
                break;
        }
    }

    private void recDeclParametros() {
        SimboloMetodo metodo_pai = (SimboloMetodo)atual;
        atual = new Simbolo();
        switch (proximo.getValor()) {
            case "char":
                
                
                terminal("char");
                
                atual.setTipo(Simbolo.CHAR);
                if(proximo.getTipo().equals("id")){
                    atual.setLexeme(proximo.getValor());
                    atual.setLinha(proximo.getLinha());
                }
                
                Tipo("id");
                recVarVet(metodo_pai);
                recListaParametros(metodo_pai);
                break;
            case "int":
                terminal("int");
                
                atual.setTipo(Simbolo.INT);
                if(proximo.getTipo().equals("id")){
                    atual.setLexeme(proximo.getValor());
                    atual.setLinha(proximo.getLinha());
                }
                
                Tipo("id");
                recVarVet(metodo_pai);
                recListaParametros(metodo_pai);
                break;
            case "bool":
                terminal("bool");
                
                atual.setTipo(Simbolo.BOOL);
                if(proximo.getTipo().equals("id")){
                    atual.setLexeme(proximo.getValor());
                    atual.setLinha(proximo.getLinha());
                }
                
                Tipo("id");
                recVarVet(metodo_pai);
                recListaParametros(metodo_pai);
                break;
            case "string":
                terminal("string");
                
                atual.setTipo(Simbolo.STRING);
                if(proximo.getTipo().equals("id")){
                    atual.setLexeme(proximo.getValor());
                    atual.setLinha(proximo.getLinha());
                }
                
                Tipo("id");
                recVarVet(metodo_pai);
                recListaParametros(metodo_pai);
                break;
            case "float":
                terminal("float");
                
                atual.setTipo(Simbolo.FLOAT);
                if(proximo.getTipo().equals("id")){
                    atual.setLexeme(proximo.getValor());
                    atual.setLinha(proximo.getLinha());
                }
                
                Tipo("id");
                recVarVet(metodo_pai);
                recListaParametros(metodo_pai);
                break;
            default:
                if (proximo.getTipo().equals("id")) {
                    
                    ArrayList<Category> aux = new ArrayList<>();
                    aux.add(Category.CLASSE);
                    if(semantico.MatchTipoAndCatg(proximo.getValor(), aux, Simbolo.CLASSE, semantico.getEscopo_global(), proximo.getLinha()))
                        atual.setTipo(proximo.getValor());
                    
                    Tipo("id");
                    
                    if(proximo.getTipo().equals("id")){
                        atual.setLexeme(proximo.getValor());
                        atual.setLinha(proximo.getLinha());
                    }
                    
                    Tipo("id");
                    recVarVet(metodo_pai);
                    recListaParametros(metodo_pai);
                }
                break;
        }
    }

    private void recListaParametros(Simbolo metodo_pai) {
        atual = new Simbolo();
        switch (proximo.getValor()) {
            case ",":
                terminal(",");
                recTipo();
                
                if(proximo.getTipo().equals("id")){
                        atual.setLexeme(proximo.getValor());
                        atual.setLinha(proximo.getLinha());
                }
                
                Tipo("id");
                recVarVet(metodo_pai);
                recListaParametros(metodo_pai);
                break;
            default:
                if(metodo_pai.getLexeme()!=null && metodo_pai.getTipo()!=null){
                    MyHashMap<String, Simbolo> aux = semantico.getEscopo_atual();
                    semantico.setEscopo_atual(semantico.getEscopo_atual().getUpper_scope());
                    semantico.addSimbolo(metodo_pai);
                    semantico.setEscopo_atual(aux);
                }
                break;
        }
    }

    private void recTipo() {
        switch (proximo.getValor()) {
            case "char":
                atual.setTipo(Simbolo.CHAR);
                terminal("char");
                break;
            case "int":
                atual.setTipo(Simbolo.INT);
                terminal("int");
                break;
            case "bool":
                atual.setTipo(Simbolo.BOOL);
                terminal("bool");
                break;
            case "string":
                atual.setTipo(Simbolo.STRING);
                terminal("string");
                break;
            case "float":
                atual.setTipo(Simbolo.FLOAT);
                terminal("float");
                break;
            default:
                
                if (proximo.getTipo().equals("id")) {
                    
                    ArrayList<Category> aux = new ArrayList<>();
                    aux.add(Category.CLASSE);
                    if(semantico.MatchTipoAndCatg(proximo.getValor(), aux, "classe", semantico.getEscopo_global(), proximo.getLinha()))
                        atual.setTipo(proximo.getValor());
                    
                    Tipo("id");
                } else {
                    erroSintatico("falta um tipo: id, int, float, char, string, bool");
                }
                break;
        }
    }

    private void recVarVet(Simbolo metodo_pai) {
        switch (proximo.getValor()) {
            case "[":
                atual.setCategoria(Category.VETOR);
                ((SimboloMetodo)metodo_pai).addParametro(atual);
                if(atual.getLexeme() != null && atual.getTipo()!=null ){
                    semantico.addSimbolo(atual);
                }
                terminal("[");
                recIndice();
                terminal("]");
                break;
            default:
                atual.setCategoria(Category.VARIAVEL);
                ((SimboloMetodo)metodo_pai).addParametro(atual);
                if(atual.getLexeme() != null && atual.getTipo()!=null){
                    semantico.addSimbolo(atual);
                }
                break;
        }
    }

    private void recConteudoMetodo() {
        atual = new Simbolo();
        switch (proximo.getTipo()) {
            case "palavra_reservada":
                if (proximo.getValor().equals("return")) {
                    break;
                }
                recComando();
                recConteudoMetodo();
                break;
            case "id":
                recComando();
                recConteudoMetodo();
                break;
            default:
                if (!proximo.getValor().equals("}")) {
                    erroSintatico("Conteudo de médoto inválido, espera um comando.");
                    proximo = proximo();
                    recConteudoMetodo();
                }
                break;
        }
    }

    private void recComando() {
  //      atual = new Simbolo();
        switch (proximo.getValor()) {
            case "read":
                recRead();
                break;
            case "write":
                recWrite();
                break;
            case "new":
                recInicializaObjeto();
                break;
            case "if":
                recIf();
                break;
            case "while":
                recWhile();
                break;
            case "char":
                terminal("char");
  //              atual.setTipo(Simbolo.CHAR);
  //              atual.setLexeme(proximo.getValor());
                Tipo("id");
                recIdDecl();
                break;
            case "int":
                terminal("int");
  //              atual.setTipo(Simbolo.INT);
//                atual.setLexeme(proximo.getValor());
                Tipo("id");
                recIdDecl();
                break;
            case "bool":
                terminal("bool");
   //             atual.setTipo(Simbolo.BOOL);
   //             atual.setLexeme(proximo.getValor());
                Tipo("id");
                recIdDecl();
                break;
            case "string":
                terminal("string");
  //              atual.setTipo(Simbolo.STRING);
  //              atual.setLexeme(proximo.getValor());
                Tipo("id");
                recIdDecl();
                break;
            case "float":
                terminal("float");
  //              atual.setTipo(Simbolo.FLOAT);
   //             atual.setLexeme(proximo.getValor());
                Tipo("id");
                recIdDecl();
                break;
            default:
                if (proximo.getTipo().equals("id")) {
                    Tipo("id");
                    if (proximo.getTipo().equals("id")) {
//                        atual.setLexeme(proximo.getValor());
                        Tipo("id");
   //                     atual.setTipo(Simbolo.OBJECT);
                        recIdDecl();
                    } else {
                        recIdComando();
                    }

                } else {
                    erroSintatico("falta identificador ou palavra reservada: read, write, new, if, while, char, int, float, string, bool");
                }
                break;

        }
    }

    private void recIdDecl() {
        switch (proximo.getValor()) {
            case ",":
                recListaVariavel();
                break;
            case ";":
                recListaVariavel();
                break;
            case "[":
 //               atual.setCategoria(Simbolo.VET);
                terminal("[");
                recIndice();
                terminal("]");
                recListaVetor();
                break;
            default:
                erroSintatico("falta ; ou , ou [");
                break;
        }
    }

    private void recIdComando() {
        switch (proximo.getValor()) {
            case "(":
                terminal("(");
                recParametros();
                terminal(")");
                terminal(";");
                break;
            case ".":
                terminal(".");
                Tipo("id");
                recAcessoObjeto();
                terminal(";");
                break;
            case "=":
                terminal("=");
                recAtribuicao();
                terminal(";");
                break;
            case "[":
                terminal("[");
                recIndice();
                terminal("]");
                terminal("=");
                recAtribuicao();
                terminal(";");
                break;
            default:
                erroSintatico("falta: [ ou = ou . ou (");
                break;
        }
    }

    private void recAcessoObjeto() {
        switch (proximo.getValor()) {
            case "(":
                terminal("(");
                recParametros();
                terminal(")");
                break;
            case "=":
                terminal("=");
                recAtribuicao();
                break;
            default:
                erroSintatico("falta ( ou =");
                break;
        }
    }

    private void recAtribuicao() {
        switch (proximo.getValor()) {
            case "(":
                terminal("(");
                recAtribuicao();
                terminal(")");
                recOperacao();
                break;
            case "++":
                recIdAcesso();
                break;
            case "--":
                recIdAcesso();
                break;
            case "true":
                terminal("true");
                recOpLogico();
                break;
            case "false":
                terminal("false");
                recOpLogico();
                break;
            case "-":
                terminal("-");
                recNegativo();
                break;
            default:
                switch (proximo.getTipo()) {
                    case "id":
                        recIdAcesso();
                        break;
                    case "numero":
                        Tipo("numero");
                        recOperadorNumero();
                        break;
                    case "cadeia_constante":
                        Tipo("cadeia_constante");
                        break;
                    case "caractere_constante":
                        Tipo("caractere_constante");
                        break;
                    default:
                        erroSintatico("falta booleano, numero, identificador, cadeia constante, caracter constante,  ( ou operadores: ++ ou -- ou - ");
                        while(!proximo.getTipo().equals("palavra_reservada") && !proximo.getValor().equals(")") && !proximo.getValor().equals("{") && !proximo.getValor().equals("}")){
                            proximo=proximo();
                        }
                        break;
                }
        }
    }

    private void recOperadorNumero() {
        switch (proximo.getValor()) {
            case ">":
                terminal(">");
                recExpAritmetica();
                recOpLogico();
                break;
            case "<":
                terminal("<");
                recExpAritmetica();
                recOpLogico();
                break;
            case ">=":
                terminal(">=");
                recExpAritmetica();
                recOpLogico();
                break;
            case "<=":
                terminal("<=");
                recExpAritmetica();
                recOpLogico();
                break;
            case "+":
                terminal("+");
                recExpAritmetica();
                recExpRelacionalOpcional();
                break;
            case "-":
                terminal("-");
                recExpAritmetica();
                recExpRelacionalOpcional();
                break;
            case "*":
                terminal("*");
                recExpAritmetica();
                recExpRelacionalOpcional();
                break;
            case "/":
                terminal("/");
                recExpAritmetica();
                recExpRelacionalOpcional();
                break;
            case "==":
                terminal("==");
                recExpAritmetica();
                recOpLogico();
                break;
            case "!=":
                terminal("!=");
                recExpAritmetica();
                recOpLogico();
                break;
            default:
                break;
        }
    }

    private void recNegativo() {
        switch (proximo.getValor()) {
            case "(":
                terminal("(");
                recNegativo();
                terminal(")");
                break;
            case "++":
                recIdAcesso();
                break;
            case "--":
                recIdAcesso();
                break;
            default:
                switch (proximo.getTipo()) {
                    case "numero":
                        Tipo("numero");
                        recOperadorNumero();
                        break;
                    case "id":
                        recIdAcesso();
                        break;
                    default:
                        erroSintatico("falta: ++, --, (, numero ou identificador");
                        break;
                }

        }
    }

    private void recExpRelacionalOpcional() {
        switch (proximo.getValor()) {
            case ">":
                terminal(">");
                recExpAritmetica();
                recOpLogico();
                break;
            case "<":
                terminal("<");
                recExpAritmetica();
                recOpLogico();
                break;
            case ">=":
                terminal(">=");
                recExpAritmetica();
                recOpLogico();
                break;
            case "<=":
                terminal("<=");
                recExpAritmetica();
                recOpLogico();
                break;
            default:
                break;
        }
    }

    private void recOpLogico() {
        switch (proximo.getValor()) {
            case "==":
                terminal("==");
                recExpLogica();
                break;
            case "!=":
                terminal("!=");
                recExpLogica();
                break;
            case "&&":
                terminal("&&");
                recExp();
                break;
            case "||":
                terminal("||");
                recExp();
                break;
            default:
                break;
        }
    }

    private void recRetorno() {
        recAtribuicao();
        terminal(";");
    }

    private void recIdAcesso() {
        switch (proximo.getValor()) {
            case "++":
                terminal("++");
                Tipo("id");
                recOperacao();
                break;
            case "--":
                terminal("--");
                Tipo("id");
                recOperacao();
                break;
            default:
                switch (proximo.getTipo()) {
                    case "id":
                        Tipo("id");
                        recAcesso();
                        recOperacao();
                        break;
                    default:
                        erroSintatico("falta: ++, --, identificador");
                }

        }
    }

    private void recAcesso() {
        switch (proximo.getValor()) {
            case "[":
                terminal("[");
                recIndice();
                terminal("]");
                break;
            case "(":
                terminal("(");
                recParametros();
                terminal(")");
                break;
            case ".":
                terminal(".");
                Tipo("id");
                recChamadaMetodo();
                break;
            case "++":
                terminal("++");
                break;
            case "--":
                terminal("--");
                break;
            default:
                break;
        }
    }

    private void recOperacao() {
        switch (proximo.getValor()) {
            case ">":
                recOperador();
                break;
            case "<":
                recOperador();
                break;
            case ">=":
                recOperador();
                break;
            case "<=":
                recOperador();
                break;
            case "+":
                recOperador();
                break;
            case "-":
                recOperador();
                break;
            case "*":
                recOperador();
                break;
            case "/":
                recOperador();
                break;
            case "==":
                recOperador();
                break;
            case "!=":
                recOperador();
                break;
            case "&&":
                recOperador();
                break;
            case "||":
                recOperador();
                break;
            default:
                break;
        }
    }

    private void recChamadaMetodo() {
        switch (proximo.getValor()) {
            case "(":
                terminal("(");
                recParametros();
                terminal(")");
                break;
            default:
                break;

        }
    }

    private void recOperador() {
        switch (proximo.getValor()) {

            case ">":
                terminal(">");
                recExpAritmetica();
                recOpLogico();
                break;
            case "<":
                terminal("<");
                recExpAritmetica();
                recOpLogico();
                break;
            case ">=":
                terminal(">=");
                recExpAritmetica();
                recOpLogico();
                break;
            case "<=":
                terminal("<=");
                recExpAritmetica();
                recOpLogico();
                break;
            case "+":
                terminal("+");
                recExpAritmetica();
                recExpRelacionalOpcional();
                break;
            case "-":
                terminal("-");
                recExpAritmetica();
                recExpRelacionalOpcional();
                break;
            case "*":
                terminal("*");
                recExpAritmetica();
                recExpRelacionalOpcional();
                break;
            case "/":
                terminal("/");
                recExpAritmetica();
                recExpRelacionalOpcional();
                break;
            case "==":
                terminal("==");
                recAtribuicao();
                break;
            case "!=":
                terminal("!=");
                recAtribuicao();
                break;
            case "&&":
                terminal("&&");
                recExp();
                break;
            case "||":
                terminal("||");
                recExp();
                break;
            default:
                erroSintatico("falta operador: >, <, >=, <=, ==, !=, +, -, *, /, &&, ||");
                break;
        }
    }

    private void recParametros() {
        switch (proximo.getValor()) {
            case "(":
                recAtribuicao();
                recNovoParametro();
                break;
            case "true":
                recAtribuicao();
                recNovoParametro();
                break;
            case "false":
                recAtribuicao();
                recNovoParametro();
                break;
            case "-":
                recAtribuicao();
                recNovoParametro();
                break;
            case "++":
                recAtribuicao();
                recNovoParametro();
                break;
            case "--":
                recAtribuicao();
                recNovoParametro();
                break;
            default:
                switch (proximo.getTipo()) {
                    case "id":
                        recAtribuicao();
                        recNovoParametro();
                        break;
                    case "numero":
                        recAtribuicao();
                        recNovoParametro();
                        break;
                    case "cadeia_constante":
                        recAtribuicao();
                        recNovoParametro();
                        break;
                    case "caractere_constante":
                        recAtribuicao();
                        recNovoParametro();
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    private void recNovoParametro() {
        switch (proximo.getValor()) {
            case ",":
                terminal(",");
                recParametros();
                break;
            default:
                break;
        }
    }

    private void recInicializaObjeto() {
        terminal("new");
        Tipo("id");
        terminal(";");
    }

    private void recWhile() {
        terminal("while");
        terminal("(");
        recExp();
        terminal(")");
        terminal("{");
        recConteudoEstrutura();
        terminal("}");
    }

    private void recRead() {
        terminal("read");
        terminal("(");
        Tipo("id");
        recListaRead();
        terminal(")");
        terminal(";");
    }

    private void recListaRead() {
        switch (proximo.getValor()) {
            case ",":
                terminal(",");
                Tipo("id");
                recListaRead();
                break;
            default:
                break;
        }
    }

    private void recWrite() {
        terminal("write");
        terminal("(");
        recParametrosWrite();
        terminal(")");
        terminal(";");
    }

    private void recParametrosWrite() {
        recImprimiveis();
        recNovoParametroWrite();
    }

    private void recNovoParametroWrite() {

        switch (proximo.getValor()) {
            case ",":
                terminal(",");
                recParametrosWrite();
                break;
            default:
                break;
        }
    }

    private void recImprimiveis() {
        switch (proximo.getTipo()) {
            case "id":
                Tipo("id");
                recOpWrite();
                break;
            case "numero":
                Tipo("numero");
                recOpWrite();
                break;
            case "cadeia_constante":
                Tipo("cadeia_constante");
                break;
            case "caractere_constante":
                Tipo("caractere_constante");
                break;
            default:
                switch (proximo.getValor()) {
                    case "(":
                        terminal("(");
                        recImprimiveis();
                        terminal(")");
                        break;
                    default:
                        erroSintatico("falta identificador, numero, cadeia constante, caracter consatante ou (");
                        break;
                }

        }
    }

    private void recOpWrite() {
        switch (proximo.getValor()) {
            case "+":
                terminal("+");
                recExpAritmetica();
                break;
            case "-":
                terminal("-");
                recExpAritmetica();
                break;
            case "*":
                terminal("*");
                recExpAritmetica();
                break;
            case "/":
                terminal("");
                recExpAritmetica();
                break;
            default:
                break;
        }
    }

    private void recIf() {
        terminal("if");
        terminal("(");
        recExp();
        terminal(")");
        terminal("{");
        recConteudoEstrutura();
        terminal("}");
        recComplementoIf();
    }

    private void recComplementoIf() {
        switch (proximo.getValor()) {
            case "else":
                terminal("else");
                terminal("{");
                recConteudoEstrutura();
                terminal("}");
                break;
            default:
                break;
        }
    }

    private void recConteudoEstrutura() {
        switch (proximo.getTipo()) {
            case "palavra_reservada":
                recComandoEstrutura();
                recConteudoEstrutura();
                break;
            case "id":
                recComandoEstrutura();
                recConteudoEstrutura();
                break;
            default:
                if (!proximo.getValor().equals("}")) {
                    erroSintatico("falta um comando: identificador ou palavra reservada");
                    proximo = proximo();
                    recConteudoEstrutura();
                }
                break;
        }
    }

    private void recComandoEstrutura() {
        switch (proximo.getValor()) {
            case "read":
                recRead();
                break;
            case "write":
                recWrite();
                break;
            case "new":
                recInicializaObjeto();
                recInicializaObjeto();
                break;
            case "if":
                recIf();
                break;
            case "while":
                recWhile();
                break;
            case "char":
                terminal("char");
                Tipo("id");
                recIdDecl();
                break;
            case "int":
                terminal("int");
                Tipo("id");
                recIdDecl();
                break;
            case "bool":
                terminal("bool");
                Tipo("id");
                recIdDecl();
                break;
            case "string":
                terminal("string");
                Tipo("id");
                recIdDecl();
                break;
            case "float":
                terminal("float");
                Tipo("id");
                recIdDecl();
                break;
            default:
                if (proximo.getTipo().equals("id")) {
                    Tipo("id");
                    recIdComando();

                } else {
                    erroSintatico("falta um comando: identificador ou palavra reservada");
                }
                break;

        }
    }

    private void recExp() {
        switch (proximo.getValor()) {
            case "true":
                terminal("true");
                recComplementoLogico();
                break;
            case "false":
                terminal("false");
                recComplementoLogico();
                break;
            case "++":
                terminal("++");
                Tipo("id");
                recIdExp();
                recComplementoAritmetico1();
                break;
            case "--":
                terminal("--");
                Tipo("id");
                recIdExp();
                recComplementoAritmetico1();
                break;
            case "(":
                terminal("(");
                recExp();
                terminal(")");
                break;
            default:
                switch (proximo.getTipo()) {
                    case "id":
                        Tipo("id");
                        recIdExpArit();
                        recComplementoAritmetico1();
                        break;
                    case "numero":
                        Tipo("numero");
                        recComplementoAritmetico();
                        recOpRelacional();
                        break;
                    default:
                        erroSintatico("falta identificador, numero, boolean, (, ou operador: ++, --");
                        while(!proximo.getTipo().equals("palavra_reservada") && !proximo.getValor().equals(")") && !proximo.getValor().equals("{") && !proximo.getValor().equals("}")){
                            proximo=proximo();
                        }
                        break;
                }

        }
    }

    private void recComplementoAritmetico1() {
        switch (proximo.getValor()) {
            case "+":
                terminal("+");
                recFatorAritmetico();
                recOpIdRelacional();
                break;
            case "-":
                terminal("-");
                recFatorAritmetico();
                recOpIdRelacional();
                break;
            case "*":
                terminal("*");
                recFatorAritmetico();
                recOpIdRelacional();
                break;
            case "/":
                terminal("/");
                recFatorAritmetico();
                recOpIdRelacional();
                break;
            default:
                recOpIdLogico();
                break;

        }
    }

    private void recExpLogica() {
        switch (proximo.getValor()) {
            case "true":
                terminal("true");
                recComplementoLogico();
                break;
            case "false":
                terminal("false");
                recComplementoLogico();
                break;
            case "++":
                terminal("++");
                Tipo("id");
                recIdExp();
                recComplementoAritmetico();
                recOpIdLogico();
                break;
            case "--":
                terminal("--");
                Tipo("id");
                recIdExp();
                recComplementoAritmetico();
                recOpIdLogico();
                break;
            case "(":
                terminal("(");
                recExpLogica();
                terminal(")");
                recComplementoExpLogica();
                break;
            default:
                switch (proximo.getTipo()) {
                    case "id":
                        Tipo("id");
                        recIdExpArit();
                        recComplementoAritmetico();
                        recOpIdLogico();
                        break;
                    case "numero":
                        Tipo("numero");
                        recComplementoAritmetico();
                        recCoOpRelacional();
                        break;
                    default:
                        erroSintatico("falta identificador, numero, boolean, (, ou operador: ++, --");
                        while(!proximo.getTipo().equals("palavra_reservada") && !proximo.getValor().equals(")") && !proximo.getValor().equals("{") && !proximo.getValor().equals("}")){
                            proximo=proximo();
                        }
                        break;
                }

        }
    }

    private void recCoOpRelacional() {
        switch (proximo.getValor()) {
            case ">":
                recOpRelacional();
                break;
            case "<":
                recOpRelacional();
                break;
            case ">=":
                recOpRelacional();
                break;
            case "<=":
                recOpRelacional();
                break;
            case "==":
                recOpRelacional();
                break;
            case "!=":
                recOpRelacional();
                break;
            case "&&":
                terminal("&&");
                recExp();
                break;
            case "||":
                terminal("||");
                recExp();
                break;
            default:
                break;
        }
    }

    private void recComplementoExpLogica() {
        switch (proximo.getValor()) {
            case ">":
                terminal(">");
                recFatorAritmetico();
                recComplementoLogico();
                break;
            case "<":
                terminal("<");
                recFatorAritmetico();
                recComplementoLogico();
                break;
            case ">=":
                terminal(">=");
                recFatorAritmetico();
                recComplementoLogico();
                break;
            case "<=":
                terminal("<=");
                recFatorAritmetico();
                recComplementoLogico();
                break;
            case "==":
                terminal("==");
                recExpLogica();
                break;
            case "!=":
                terminal("!=");
                recExpLogica();
                break;
            case "&&":
                terminal("&&");
                recExp();
                break;
            case "||":
                terminal("||");
                recExp();
                break;
            case "+":
                terminal("+");
                recFatorAritmetico();
                recComplementoLogico();
                break;
            case "-":
                terminal("-");
                recFatorAritmetico();
                recComplementoLogico();
                break;
            case "*":
                terminal("*");
                recFatorAritmetico();
                recComplementoLogico();
                break;
            case "/":
                terminal("/");
                recFatorAritmetico();
                recComplementoLogico();
                break;
            default:
                break;
        }
    }

    private void recIdExp() {
        switch (proximo.getValor()) {
            case "(":
                terminal("(");
                recParametros();
                terminal(")");
                break;
            case ".":
                terminal(".");
                Tipo("id");
                recChamadaMetodo();
                break;
            case "[":
                terminal("[");
                recIndice();
                terminal("]");
                break;
            default:
                break;
        }
    }

    private void recOpIdLogico() {
        switch (proximo.getValor()) {
            case ">":
                recOpIdRelacional();
                break;
            case "<":
                recOpIdRelacional();
                break;
            case ">=":
                recOpIdRelacional();
                break;
            case "<=":
                recOpIdRelacional();
                break;
            case "==":
                recOpIdRelacional();
                break;
            case "!=":
                recOpIdRelacional();
                break;
            case "&&":
                terminal("&&");
                recExp();
                break;
            case "||":
                terminal("||");
                recExp();
                break;
            default:
                break;
        }
    }

    private void recComplementoLogico() {
        switch (proximo.getValor()) {
            case "==":
                terminal("==");
                recExpLogica();
                break;
            case "!=":
                terminal("!=");
                recExpLogica();
                break;
            case "&&":
                terminal("&&");
                recExp();
                break;
            case "||":
                terminal("||");
                recExp();
                break;
            default:
                break;
        }
    }

    private void recOpRelacional() {
        switch (proximo.getValor()) {
            case ">":
                terminal(">");
                recExpAritmetica();
                recOpLogico();
                break;
            case "<":
                terminal("<");
                recExpAritmetica();
                recOpLogico();
                break;
            case ">=":
                terminal(">=");
                recExpAritmetica();
                recOpLogico();
                break;
            case "<=":
                terminal("<=");
                recExpAritmetica();
                recOpLogico();
                break;
            case "==":
                terminal("==");
                recExpAritmetica();
                recOpLogico();
                break;
            case "!=":
                terminal("!=");
                recExpAritmetica();
                recOpLogico();
                break;
            default:
                erroSintatico("falta operador: >, <, >=, <=, ==, !=");
                break;
        }
    }

    private void recOpIdRelacional() {
        switch (proximo.getValor()) {
            case ">":
                terminal(">");
                recExpAritmetica();
                recOpLogico();
                break;
            case "<":
                terminal("<");
                recExpAritmetica();
                recOpLogico();
                break;
            case ">=":
                terminal(">=");
                recExpAritmetica();
                recOpLogico();
                break;
            case "<=":
                terminal("<=");
                recExpAritmetica();
                recOpLogico();
                break;
            case "==":
                terminal("==");
                recExpLogica();
                break;
            case "!=":
                terminal("!=");
                recExpLogica();
                break;
            default:
                erroSintatico("falta operador: >, <, >=, <=, ==, !=");
                break;
        }
    }

    private void recExpAritmetica() {
        switch (proximo.getValor()) {
            case "++":
                recFatorAritmetico();
                break;
            case "--":
                recFatorAritmetico();
                break;
            case "-":
                terminal("-");
                recExpAritmetica();
                break;
            case "(":
                recFatorAritmetico();
                break;
            default:
                if (proximo.getTipo().equals("id") || proximo.getTipo().equals("numero")) {
                    recFatorAritmetico();
                    break;
                }
                erroSintatico("falta identificar, numero, ( ou operador: ++, --, -");
                        while(!proximo.getTipo().equals("id") && !proximo.getTipo().equals("palavra_reservada") && !proximo.getValor().equals(")") && !proximo.getValor().equals("{") && !proximo.getValor().equals("}")){
                            proximo=proximo();
                        }
                break;
        }

    }

    private void recFatorAritmetico() {
        switch (proximo.getValor()) {
            case "++":
                recIdAritmetico();
                recComplementoAritmetico();
                break;
            case "--":
                recIdAritmetico();
                recComplementoAritmetico();
                break;
            case "-":
                terminal("-");
                recExpAritmetica();
                break;
            case "(":
                recFatorAritmetico();
                recComplementoAritmetico();
                break;
            default:
                if (proximo.getTipo().equals("id")) {
                    recIdAritmetico();
                    recComplementoAritmetico();
                    break;
                } else if (proximo.getTipo().equals("numero")) {
                    Tipo("numero");
                    recComplementoAritmetico();
                    break;
                }
                erroSintatico("falta numero, identificador, (, ou operador: ++, --, -");
                while (!proximo.getTipo().equals("palavra_reservada") && !proximo.getValor().equals(")") && !proximo.getValor().equals("{") && !proximo.getValor().equals("}") && !proximo.getValor().equals(";")) {
                    proximo = proximo();
                }
                break;
        }
    }

    private void recIdAritmetico() {
        switch (proximo.getValor()) {
            case "++":
                terminal("++");
                Tipo("id");
                break;
            case "--":
                terminal("--");
                Tipo("id");
                break;
            default:
                if (proximo.getTipo().equals("id")) {
                    Tipo("id");
                    recIdExpArit();
                }
                erroSintatico("falta identificador ou operador: ++, --");
                break;
        }
    }

    private void recIdExpArit() {
        switch (proximo.getValor()) {
            case "(":
                recIdExp();
                break;
            case ".":
                recIdExp();
                break;
            case "[":
                recIdExp();
                break;
            case "++":
                terminal("++");
                break;
            case "--":
                terminal("--");
                break;
            default:
                break;
        }
    }

    private void recComplementoAritmetico() {
        switch (proximo.getValor()) {
            case "+":
                terminal("+");
                recFatorAritmetico();
                break;
            case "-":
                terminal("-");
                recFatorAritmetico();
                break;
            case "*":
                terminal("*");
                recFatorAritmetico();
                break;
            case "/":
                terminal("/");
                recFatorAritmetico();
                break;
            default:
                break;
        }
    }

}
