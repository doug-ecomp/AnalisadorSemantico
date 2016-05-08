package modulo_analisadorLexico;

import java.util.ArrayList;
import modulo_completo.TabelaSimbolos;

/**
 * Classe destinada à análise léxica do código fonte. Nessa classe são
 * reconhecidos os tokens presentes no código fonte, bem como os erros (se
 * houver).
 * 
 * @author Lucas Carneiro
 * @author Oto Lopes
 *
 * @see Token
 * @see EstruturaLexica
 * @see TabelaSimbolos
 */
public class AnalisadorLexico {

    /**
     * Atributo erpresentando fim de arquivo.
     */
    private static final char EOF = '\0';
    /**
     * Atributo representando quebra de linha.
     */
    private static final char QUEBRA_LINHA = ' ';
    /**
     * Atributo representando uma linha vazia.
     */
    private static final char LINHA_VAZIA = ' ';
    /**
     * Estrutura léxica que rege o compilador.
     */
    private final EstruturaLexica estruturaLexica;
    /**
     * Lista de tokens.
     */
    private final ArrayList<Token> tokens;
    /**
     * Lista com os erros.
     */
    private final ArrayList<String> erros;
    /**
     * Representa a linha atual no código.
     */
    private int linha;
    /**
     * Representa a coluna atual no código.
     */
    private int coluna;
    /**
     * Lista com todas as linhas do código.
     */
    private ArrayList<String> codigo;
    /**
     * Identifica se uma linha está vazia ou não.
     */
    private boolean linhaVazia;
    /**
     * identifica se o operador '-' pode gerar um número (negativo).
     */
    private boolean podeSerNumero;

    /**
     * Construtor da classe.
     */
    public AnalisadorLexico() {

        this.estruturaLexica = new EstruturaLexica();
        this.tokens = new ArrayList<>();
        this.erros = new ArrayList<>();
        this.coluna = 0;
        this.linha = 0;

        this.linhaVazia = false; // Não se sabe se a primeira linha do código é vazia.
        this.podeSerNumero = true; // A primeira ocorrência do operador '-' pode gerar um número negativo.
    }

    /**
     * Retorna a lista contendo os tokens encontrados no código.
     *
     * @return A lista de tokens
     */
    public ArrayList<Token> getTokens() {
        return this.tokens;
    }

    /**
     * Retorna a lista contendo os erros encontrados no código.
     *
     * @return A lista de erros
     */
    public ArrayList<String> getErros() {
        return this.erros;
    }

    /**
     * Método destinado à padronização dos erros. Coloca na lista de erros no
     * formato: Sequência_errada Tipo_do_erro Linha:Coluna
     *
     * @param tipo Tipo do erro (relacionado ao token que esta errado)
     * @param erro A sequência que originou o erro, exceto para erro de
     * comentário
     * @param linhaInicial Linha de início da sequência errada
     * @param colunaInicial Coluna de inicio da sequência errada
     */
    private void novoErro(String tipo, String erro, int linhaInicial, int colunaInicial) {

        this.erros.add("\n" + erro + " " + tipo + " " + (linhaInicial + 1) + ":" + (colunaInicial + 1));
    }

    /**
     * Recupera, sequêncialmente, uma nova linha do código fonte e a partir dela
     * retorna, também sequêncialmente, o próximo caractere encontrado (se
     * baseando pela linha e coluna atual).
     *
     * @return Retorna o próximo caractere a ser lido pelo analisador léxico
     */
    private char novoChar() {

        if (!this.codigo.isEmpty()) { // Verifica se o código fonte é um arquivo vazio.
            char c[] = this.codigo.get(this.linha).toCharArray(); // Tranforma a linha atual do código em uma sequência de caracteres.
            if (c.length == this.coluna) { // Verifica se a linha acabou. 
                this.linhaVazia = false; // Não se sabe se a próxima linha é vazia.
                return QUEBRA_LINHA;
            } else if (c.length > this.coluna) { // Retorna um caracter da linha atual. 
                this.linhaVazia = false; // Não se sabe se a próxima linha é vazia.
                return c[this.coluna]; // Retorna o determinado caractere da linha e coluna atual.
            } else if (this.codigo.size() > (this.linha + 1)) { // Verifica se o arquivo ainda possui linhas a serem tratadas.
                this.linha++; // Incrementa a linha completa.
                this.podeSerNumero = true; // Mesmo com o incremento da linha, o operador '-' pode gerar um número negativo.
                c = this.codigo.get(this.linha).toCharArray();
                this.coluna = 0; // Volta a coluna ao estado inicial.

                if (c.length == 0) { // Caso uma linha não tenha absolutamente nada, apenas um "enter".
                    this.linhaVazia = true;
                    return LINHA_VAZIA;
                }

                return c[this.coluna]; // Retorna o determinado caractere da linha e coluna atual.
            } else {
                return EOF; // Fim de arquivo.
            }
        } else {
            return EOF; // Fim de arquivo.
        }
    }

    /**
     * Verifica a qual token o próximo caractere está relacionado, ou se este
     * deve ser ignorado.
     *
     * @param codigo Código fonte a ser analisado
     */
    public void analise(ArrayList<String> codigo) {

        this.codigo = codigo;
        String lexema;
        char ch = this.novoChar(); // Recupera o primeiro caractere do código.
        while (ch != EOF) {
            if (!this.linhaVazia) { // Se for linha vazia pula para a próxima.
                lexema = "";

                if (this.estruturaLexica.ehEspaco(ch)) { // Verifica se é um espaço em branco ou caractere de tabulação.
                    this.coluna++;
                } else if (estruturaLexica.ehLetra(ch)) { // Verifica se é um identificador.
                    this.identificador(lexema, ch);

                } else if (ch == '\'') { // Verifica se é um caractere constante.
                    this.caractereConstante(lexema, ch);

                } else if (ch == '"') { // Verifica se é cadeia constante.
                    this.cadeiaConstante(lexema, ch);

                } else if (estruturaLexica.ehDigito(ch)) { // Verifica se é número.
                    this.numero(lexema, ch);

                } else if (estruturaLexica.ehOperador(ch)) { // Verifica se é operador.
                    this.operador(lexema, ch);

                } else if (this.estruturaLexica.ehDelimitador(ch)) { // Verifica se é delimitador.
                    this.delimitador(lexema, ch);

                } else { // Símbolos inválidos.
                    this.simboloInvalido(lexema, ch);
                }
            } else {
                this.linhaVazia = false;
                this.linha++;
            }

            if (this.podeSerNumero && (this.estruturaLexica.ehOperador(ch) || this.estruturaLexica.ehDelimitador(ch))) { //Verifica se a partir do caracteres já consumido se -Digito é um número negativo ou operador seguido de número.
                this.podeSerNumero = true;
            } else if (this.estruturaLexica.ehLetra(ch) || this.estruturaLexica.ehDigito(ch)) {
                this.podeSerNumero = false;
            }

            ch = this.novoChar();
        }
    }

    /**
     * Análisa se o primeiro caractere de uma sequência pode formar um
     * identificador, uma palavra reservada ou gerar erro.
     *
     * @param lexema Token em formação
     *
     * @param ch Caractere a ser analisado para compor o lexema
     */
    private void identificador(String lexema, char ch) {

        int linhaInicial = this.linha; // Linha onde se inicia a sequência.
        int colunaInicial = this.coluna; // Coluna onde se inicia a sequência.
        boolean error = false; // Identifica se houve erro.

        lexema = lexema + ch;  // Cria o lexema apartir da composição do caractere lido. 
        this.coluna++;
        ch = this.novoChar();
        //Percorre enquanto encontrar um delimitador de identificador.
        while (!(ch == EOF || this.estruturaLexica.ehEspaco(ch) || this.estruturaLexica.ehDelimitador(ch) || this.estruturaLexica.ehOperador(ch) || ch == '\'' || ch == '"')) {
            if (!(this.estruturaLexica.ehLetra(ch) || this.estruturaLexica.ehDigito(ch) || ch == '_')) { // Verifica se existe algum caractere inválido no identificador
                error = true;
            }
            lexema = lexema + ch;
            this.coluna++;
            ch = this.novoChar();
        }

        if (!error) { // Se não hovue erro.
            Token tk;
            if (this.estruturaLexica.ehPalavraReservada(lexema)) { // Verifica se é uma palavra reservada.
                tk = new Token(lexema, "palavra_reservada", linhaInicial + 1, colunaInicial + 1);
            } else {
                tk = new Token(lexema, "id", linhaInicial + 1, colunaInicial + 1);
            }
            this.tokens.add(tk);
        } else {
            this.novoErro("identificador_mal_formado", lexema, linhaInicial, colunaInicial);
        }
    }

    /**
     * Análisa se o primeiro caractere de uma sequência pode formar um número
     * e/ou operador '.' ou gerar erro.
     *
     * @param lexema Token em formação
     *
     * @param ch Caractere a ser analisado para compor o lexema
     */
    private void numero(String lexema, char ch) {

        int linhaInicial = this.linha; // Linha onde se inicia a sequência.
        int colunaInicial = this.coluna; // Coluna onde se inicia a sequência.
        boolean error = false; // Identifica se houve erro.

        if (lexema.equals("-")) { // Se for um número negativo, a coluna incial é decrementada em um.
            colunaInicial--;
        }

        lexema += ch; // Cria o lexema apartir da composição do caractere lido. 
        this.coluna++;
        ch = this.novoChar();

        // Percorre acumulando caracteres enquanto não encontrar um delimitador de número.
        while (!(ch == EOF || this.estruturaLexica.ehEspaco(ch) || this.estruturaLexica.ehDelimitador(ch) || this.estruturaLexica.ehOperador(ch) || ch == '\'' || ch == '"')) {
            if (!(this.estruturaLexica.ehDigito(ch))) { // Verifica se algum caractere não é digito, gerando erro. 
                error = true;
            }
            lexema += ch;
            this.coluna++;
            ch = this.novoChar();
        }
        if (ch == '.') {  // Verifica se o próximo caractere é um ponto para definir se o número é um ponto flutuante.
            this.coluna++;
            ch = this.novoChar();
            if (!this.estruturaLexica.ehDigito(ch)) { // Se após o ponto não aparecer um número, então o ponto é um operador.
                if (!error) { // Se antes do operador ponto, não houve erro no número.
                    Token tk;
                    tk = new Token(lexema, "numero", linhaInicial + 1, colunaInicial + 1);
                    this.tokens.add(tk);
                } else {
                    this.novoErro("nro_mal_formado", lexema, linhaInicial, colunaInicial);
                }

                this.podeSerNumero = true; // Depois do operador ponto, o operador '-' pode formar um número negativo.
                Token tk2;
                tk2 = new Token(".", "operador", linhaInicial + 1, this.coluna);
                this.tokens.add(tk2);
                return;

            } else 
             if (!error) { // Se antes do operador ponto, não houve erro no número.
                    lexema += "." + ch; // O ponto é inserido ao lexema juntamente com o número encontrado.
                    this.coluna++;
                    ch = this.novoChar();
                    
                    // Percorre acumulando caracteres enquanto não encontrar um delimitador de número.
                    while (this.estruturaLexica.ehDigito(ch) || this.estruturaLexica.ehLetra(ch)) { 
                        if (this.estruturaLexica.ehLetra(ch)) { // Se for letra gera erro.
                            error = true;
                        }
                        lexema += ch;
                        this.coluna++;
                        ch = this.novoChar();
                    }
                } else {
                    this.novoErro("nro_mal_formado", lexema, linhaInicial, colunaInicial);

                    this.podeSerNumero = true;
                    Token tk2;
                    tk2 = new Token(".", "operador", linhaInicial + 1, this.coluna);
                    this.tokens.add(tk2);
                    return;
                }
        }
        if (!error) { // Se não houve erro no número completo.
            Token tk;
            tk = new Token(lexema, "numero", linhaInicial + 1, colunaInicial + 1);
            this.tokens.add(tk);
        } else {
            this.novoErro("nro_mal_formado", lexema, linhaInicial, colunaInicial);
        }
    }

    /**
     * Análisa se o primeiro caractere de uma sequência pode formar uma cadeia
     * constante ou gerar erro.
     *
     * @param lexema Token em formação
     *
     * @param ch Caractere a ser analisado para compor o lexema
     */
    private void cadeiaConstante(String lexema, char ch) {

        int linhaInicial = this.linha; // Linha onde se inicia a sequência.
        int colunaInicial = this.coluna; // Coluna onde se inicia a sequência.
        boolean error = false; // Identifica se houve erro.

        lexema = lexema + ch; // Cria o lexema apartir da composição do caractere lido. 
        this.coluna++;
        ch = this.novoChar();
        while (ch != '"' && ch != EOF && linhaInicial == this.linha) { // Procura por aspas para feixar a cadeia ou até fim de linha ou fim de arquivo.
            if (!this.estruturaLexica.ehSimbolo(ch) && ch != 9) {  // Verifica se o caracter é um simbolo invalido.
                error = true;
            }
            
            // Como representamos quebra de linha como um espaço, aqui é verificado se realmente é um espaço ou quebra de linha.
            if (Character.isSpaceChar(ch)) {
                this.coluna++;
                ch = this.novoChar();
                if (ch == ' ' && linhaInicial == this.linha) { // Se o próximo caractere for espaço e se mantém na linha atual, o espaço anterior é inserido ao lexema. 
                    lexema += " ";
                } else if (ch != EOF && linhaInicial == this.linha) {
                    if (!this.estruturaLexica.ehSimbolo(ch) && ch != 9) { // Verifica se dentro das aspas não há um símbolo inválido
                        error = true;
                    }
                    lexema += " "; // Já se sabe que o caractere lido não representa quebra de linha, logo, o espaço anterior é inserido ao lexema. 
                    if (ch != '"') { // Se o próximo caractere não for espaço nem aspas duplas e estiver na linha atual, o espaço anterior e este novo caractere são inseridos ao lexema. 
                        lexema += ch;
                        this.coluna++;
                        ch = this.novoChar();
                    }
                }
            } else { // Caso não seja espaço, o caractere é inserido normalmente ao lexema.
                lexema += ch;
                this.coluna++;
                ch = this.novoChar();
            }
        }

        if (ch == '"' && linhaInicial == this.linha) {  // Verifica se a cadeia constante foi fechada na mesma linha.
            lexema += ch;
            this.coluna++;
        }
        
        if (!error && linhaInicial == this.linha) { // Se não houve erro.
            Token tk;
            tk = new Token(lexema, "cadeia_constante", linhaInicial + 1, colunaInicial + 1);
            this.tokens.add(tk);
        } else {
            this.novoErro("cadeia_mal_formada", lexema, linhaInicial, colunaInicial);
        }
    }

    /**
     * Análisa se o primeiro caractere de uma sequência pode formar um caractere
     * constante ou gerar erro.
     *
     * @param lexema Token em formação
     *
     * @param ch Caractere a ser analisado para compor o lexema
     */
    private void caractereConstante(String lexema, char ch) {

        int linhaInicial = this.linha; // Linha onde se inicia a sequência.
        int colunaInicial = this.coluna; // Coluna onde se inicia a sequência.
        boolean error = false; // Identifica se houve erro.

        lexema = lexema + ch; // Cria o lexema apartir da composição do caractere lido. 
        this.coluna++;
        int qtdConteudo = 0; // Quantidade de caracteres dentro das aspas simples.
        ch = this.novoChar();
        while (ch != '\'' && ch != EOF && linhaInicial == this.linha) { // Consome caracteres até encontar aspas simples, quebra de linha ou fim de arquivo.

            if (!(this.estruturaLexica.ehLetra(ch) || this.estruturaLexica.ehDigito(ch)) || qtdConteudo > 0) { // Verifica se o conteúdo tem espaço (um caractere apenas) e se é válido (letra ou digito).
                error = true;
            }
            
            // Como representamos quebra de linha como um espaço, aqui é verificado se realmente é um espaço ou quebra de linha.
            if (Character.isSpaceChar(ch)) { 
                this.coluna++;
                ch = this.novoChar();
                if (ch == ' ' && linhaInicial == this.linha) { // Se o próximo caractere for espaço e se mantém na linha atual, o espaço anterior é inserido ao lexema. 
                    lexema += " ";
                } else if (ch != EOF && linhaInicial == this.linha) { 
                    lexema += " "; // Já se sabe que o caractere lido não representa quebra de linha, logo, o espaço anterior é inserido ao lexema. 
                    if (ch != '\'') { // Se o próximo caractere não for espaço nem aspas simples e estiver na linha atual, o espaço anterior e este novo caractere são inseridos ao lexema. 
                        lexema += ch;
                        this.coluna++;
                        ch = this.novoChar();
                    }
                }
            } else { // Caso não seja espaço, o caractere é inserido normalmente ao lexema.
                qtdConteudo++;
                lexema = lexema + ch;
                this.coluna++;
                ch = this.novoChar();
            }
        }

        if (ch == '\'' && linhaInicial == this.linha) { // Verifica se o caractere constante foi fechado na mesma linha.
            lexema += ch;
            this.coluna++;
        }

        if (!error && qtdConteudo != 0 && linhaInicial == this.linha) { // Se não houve erro.
            Token tk;
            tk = new Token(lexema, "caractere_constante", linhaInicial + 1, colunaInicial + 1);
            this.tokens.add(tk);
        } else {
            this.novoErro("caractere_mal_formado", lexema, linhaInicial, colunaInicial);
        }
    }

    /**
     * Análisa se o primeiro caractere de uma sequência pode formar um operador,
     * um número negativo, um comentário ou gerar erro.
     *
     * @param lexema Token em formação
     *
     * @param ch Caractere a ser analisado para compor o lexema
     */
    private void operador(String lexema, char ch) {

        int linhaInicial = this.linha; // Linha onde se inicia a sequência.
        int colunaInicial = this.coluna; // Coluna onde se inicia a sequência.
        boolean error = false; // Identifica se houve erro.

        lexema += ch; // Cria o lexema apartir da composição do caractere lido. 
        this.coluna++;
        if (ch == '.' || ch == '*') { // Estes operadores são lidos e inseridos na tabela de tokens diretamente.
        } else if (ch == '+') {
            ch = this.novoChar();
            if (ch == '+') {
                lexema += ch;
                this.coluna++;
            }

        } else if (ch == '-') {
            ch = this.novoChar();
            if (ch == '-') { // Se for o operador "--".
                lexema += ch;
                this.coluna++;
            } else if (this.podeSerNumero) {
                while (this.estruturaLexica.ehEspaco(ch)) { // Desconsidera quebra de linhas, espaços e TAB's.
                    this.coluna++;
                    ch = this.novoChar();
                }
                if (this.estruturaLexica.ehDigito(ch)) { // Se encontrar um número após quebra de linhas, espaços e TAB's.
                    this.podeSerNumero = false; // Após formar um número o operador '-' não pode gerar um número negativo.
                    this.numero(lexema, ch); 
                    return;
                }
            }

        } else if (ch == '/') {
            ch = this.novoChar();

            // Se o próximo caractere formar um comentário, o operador de antes não é consumido e parte-se para o autômato de comentário.
            if (ch == '/' || ch == '*') {
                this.comentario(lexema + ch);
                return;
            }

        } else if (ch == '=' || ch == '>' || ch == '<') {
            ch = this.novoChar();
            if (ch == '=') {
                lexema += ch;
                this.coluna++;
            }

        } else if (ch == '!') {
            ch = this.novoChar();
            if (ch == '=') {
                lexema += ch;
                this.coluna++;
            } else {
                if (!this.estruturaLexica.ehEspaco(ch)) { // Se não for espaço/TAB, o outro caractere é inserido à sequência.
                    lexema += ch;
                    this.coluna++;
                }
                error = true;
            }

        } else if (ch == '&' || ch == '|') {
            ch = this.novoChar();
            if (ch == lexema.charAt(0)) { // Verifica se o outro operador é igual ao de antes (&==& ou |==|). 
                lexema += ch;
                this.coluna++;
            } else { // Gera erro.
                if (!this.estruturaLexica.ehEspaco(ch)) { // Se não for espaço/TAB, o outro caractere é inserido à sequência.
                    lexema += ch;
                    this.coluna++;
                }
                error = true;
            }
        }

        if (lexema.equals("++") || lexema.equals("--")) { // Após um operador de incremento ou decremento, o operador '-' pode gerar um número negativo.
            this.podeSerNumero = false;
        } else {
            this.podeSerNumero = true;
        }

        if (!error) { // Se não houve erro.
            Token tk;
            tk = new Token(lexema, "operador", linhaInicial + 1, colunaInicial + 1);
            this.tokens.add(tk);
        } else {
            this.novoErro("operador_mal_formado", lexema, linhaInicial, colunaInicial);
        }
    }

    /**
     * Insere o delimitador na lista de tokens.
     *
     * @param lexema Token de delimitador
     *
     * @param ch Caractere que compõe o token delimitador
     */
    private void delimitador(String lexema, char ch) {

        int linhaInicial = this.linha; // Linha onde se inicia a sequência.
        int colunaInicial = this.coluna; // Coluna onde se inicia a sequência.

        lexema += ch; // Cria o lexema apartir da composição do caractere lido. 
        this.coluna++;

        Token tk = new Token(lexema, "delimitador", linhaInicial + 1, colunaInicial + 1);
        this.tokens.add(tk);
    }

    /**
     * Percorre um comentário, em bloco ou não, desconsiderando as sequências
     * encontradas. Caso o comentário em bloco não seja fechado, um erro é
     * inserido na lista de erros.
     *
     * @param coment "/*" ou "//" (representando um comentário em bloco e um
     * comentário em linha, respectivamente)
     */
    private void comentario(String coment) {

        int linhaInicial = this.linha; // Linha onde se inicia a sequência.
        int colunaInicial = this.coluna; // Coluna onde se inicia a sequência.

        boolean saiuBloco = false; // Identifica se o comentário em bloco foi fechado.
        this.coluna++;
        char ch = this.novoChar();

        if (coment.equals("//")) {
            while (linhaInicial == this.linha && ch != EOF) { // Consome os caraacteres até o final da linha ou final do arquivo.
                this.coluna++;
                ch = this.novoChar();
            }
        } else if (coment.equals("/*")) {
            while (ch != EOF && !saiuBloco) { // Consome os caraacteres até ser fechado ou final do arquivo.
                if (ch == '*') {
                    this.coluna++;
                    ch = this.novoChar();
                    if (ch == '/') {
                        this.coluna++;
                        saiuBloco = true;
                    }
                } else {
                    this.coluna++;
                    ch = this.novoChar();
                }
            }
            if (!saiuBloco) { // Caso o comentário em bloco não seja fechado um erro é criado.
                this.novoErro("comentário_não_finalizado", "###comentário_não_impresso###", linhaInicial, colunaInicial - 1);
            }
        }
    }

    /**
     * Análisa se o primeiro caractere de uma sequência pode formar uma
     * sequência inválida.
     *
     * @param simbolo Sequência inválida em formação
     *
     * @param ch Caractere a ser analisado para compor a sequência inválida
     */
    private void simboloInvalido(String simbolo, char ch) {

        int linhaInicial = this.linha; // Linha onde se inicia a sequência.
        int colunaInicial = this.coluna; // Coluna onde se inicia a sequência.

        if (ch == 9) { // Um caractere de tabulação não é considerado símbolo inválido.
            this.coluna++;
            return;
        }

        // Consome outros símbolos inválidos, letras e números.
        while (!(ch == EOF || this.estruturaLexica.ehEspaco(ch) || this.estruturaLexica.ehDelimitador(ch) || this.estruturaLexica.ehOperador(ch))) {
            simbolo = simbolo + ch;
            this.coluna++;
            ch = this.novoChar();
        }

        this.novoErro("sequência_inválida", simbolo, linhaInicial, colunaInicial);
    }
}