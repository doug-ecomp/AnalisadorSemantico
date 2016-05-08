package modulo_completo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import manipulação_arquivosIO.Arquivo;
import modulo_analisadorLexico.AnalisadorLexico;
import modulo_analisadorLexico.Token;
import modulo_analisadorSintatico.AnalisadorSintatico;

/**
 * Classe responsável por executar os modulos do compilador, assim, capturando
 * os códigos fonte e gerando suas respectivas saídas.
 *
 * @author Lucas Carneiro
 * @author Oto Lopes
 *
 * @see AnalisadorLexico
 * @see Arquivo
 */
public class Compilador {

    Simbolos tabelaSimbolos;

    /**
     * Manipulador dos documentos de entrada e saída.
     */
    private final Arquivo arquivo;

    /**
     * Módulo da análise léxica do código.
     */
    private AnalisadorLexico analisadorLexico;
    private AnalisadorSintatico analisadorSintatico;

    /**
     * Construtor da Classe.
     */
    public Compilador() {

        arquivo = new Arquivo(); // Criação do manipulador de entrada e saída.
        //tabelaSimbolos = new Simbolos();
        //tabelaSimbolos.setNome("GLOBAL");
    }

    /**
     * Ler todos os códigos fonte da pasta <i>/src/testes/in/</i> e envia cada
     * um ao módulo léxico, sendo que ao fim do processo deste módulo, são
     * gerados os arquivos de cada código fonte
     *
     * @throws FileNotFoundException Se não encontrar o arquivo do código
     * @throws IOException Arquivo de saida não foi gerado com sucesso
     */
    public void compilar() throws FileNotFoundException, IOException {

        ArrayList<String> localFiles = arquivo.lerCodigos(); // Recebe a lista com todos os códigos da pasta.
        if (localFiles.isEmpty()) { // Pasta de códigos de entrada vazia.
            System.out.println("Sem Códigos para Compilar");
            System.exit(0);
        }
        for (String lF : localFiles) { // Para cada arquivo fonte, o analisador léxico gera as listas de tokens e erros (se houver).
            tabelaSimbolos = new Simbolos(); //cria a tabela de simbolos
            tabelaSimbolos.setNome("GLOBAL"); //nomeia o primeiro simbolo como global
            ArrayList<String> codigoFonte = arquivo.lerCodigoFonte(lF);
            analisadorLexico = new AnalisadorLexico();
            analisadorLexico.analise(codigoFonte);
            arquivo.escreverSaidaLexico(analisadorLexico.getTokens(), analisadorLexico.getErros());
            ArrayList<Token> listaTokens;
            listaTokens = arquivo.lerSaidaLexico();
            analisadorSintatico = new AnalisadorSintatico(tabelaSimbolos);
            analisadorSintatico.analise(listaTokens);
            arquivo.escreverSaidaSintatico(analisadorSintatico.getErros());
            System.out.println(tabelaSimbolos);
        }
    }

    /**
     * Inicializa os módulos do compilador.
     *
     * @param args
     */
    public static void main(String args[]) {

        try {
            Compilador compilador = new Compilador(); // Cria o compilador.
            compilador.compilar(); // Executa o compilador.

        } catch (FileNotFoundException error1) {
            System.out.println("Arquivo não encontrado");
            System.exit(0);
        } catch (IOException ex) {
            System.out.println("Arquivo de saida não foi gerado com sucesso");
            System.exit(0);
        }
        System.out.println("COMPILADO !");
    }
}
