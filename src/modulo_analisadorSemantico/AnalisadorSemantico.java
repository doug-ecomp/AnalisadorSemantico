/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulo_analisadorSemantico;

import TabelaSimbolos.MyHashMap;
import TabelaSimbolos.Simbolo;
import TabelaSimbolos.SimboloMetodo;
import java.util.ArrayList;
import java.util.HashMap;
import modulo_completo.Category;

/**
 *
 * @author Douglas
 */
public class AnalisadorSemantico {
    private MyHashMap<String, Simbolo> escopo_global;
    private MyHashMap<String, Simbolo> escopo_atual;
    private ArrayList<String> err_semantic;
    
    public AnalisadorSemantico() {
        escopo_global = new MyHashMap<>(null);
        escopo_atual = escopo_global;
        err_semantic = new ArrayList<>();
    }
    
    public void addSimbolo(Simbolo symbol){
        Simbolo aux = searchSymbol(symbol, escopo_atual);
        if(aux==null || (aux.getTipo().contains("heranca") && aux.getTipo().split(":")[0].equals(symbol.getTipo()) &&
            ( ((SimboloMetodo)aux).ComparaParametros(((SimboloMetodo)symbol).getParametros()) )==null ) ){
            
            escopo_atual.put(symbol.getLexeme(), symbol);
            
        } else if(aux.getTipo().contains("heranca")){
              
            for(String str: ((SimboloMetodo)aux).ComparaParametros(((SimboloMetodo)symbol).getParametros()) ){
                err_semantic.add(symbol.getLinha()+" "+str);
            }
//            err_semantic.add(symbol.getLinha()+" Sobrescrita do método "+symbol.getLexeme()
//                    +" na classe mãe é inválida. "
//                    + "Os tipos do retorno e dos parametros devem ser os mesmos");
            
        } else{
            err_semantic.add(symbol.getLinha()+" ID "+symbol.getLexeme()+" já reservado e não pode ser sobrescrito");
        }
        System.out.println(escopo_atual.size());
    }
    
    public Simbolo searchSymbol(Simbolo symbol, MyHashMap<String, Simbolo> start){
        MyHashMap<String, Simbolo> temp = start;
        boolean same_scope=true, category=false, heranca = false;
        Simbolo sy;
        while( temp != null ){
            sy = temp.get(symbol.getLexeme());
            if( sy != null){
                if( !(sy.getCategoria()==Category.VARIAVEL) ){
                    category = true;
                }
                
                if (same_scope || category){
                    if(heranca && sy instanceof SimboloMetodo && symbol instanceof SimboloMetodo){
                        SimboloMetodo aux = new SimboloMetodo(Category.METODO);
                        aux.setLexeme(sy.getLexeme());
                        aux.setTipo(sy.getTipo().concat(":heranca"));
                        aux.setLinha(sy.getLinha());
                        aux.setParametros(((SimboloMetodo)sy).getParametros());
                        return aux;
                    } else
                        return sy;
                }
                else
                    return null;
            }
            
            if(temp.getDono()!=null && temp.getDono().getHeranca_pai()!=null){
                temp = temp.getDono().getHeranca_pai().getInner_scope();
                heranca = true;
            } else {
                temp = temp.getUpper_scope();
                heranca = false;
                same_scope = false;
            }
        }
        
        return null;
    }
    
    //Os proximos 4 métodos são usados para testar a compatibilidade dos tipos na atribuição à constantes
    public void matchTypeNum(String valor, String tipo, int line){

                if( //!(  ( tipo.equals(Simbolo.FLOAT) && valor.contains(".") ) ||
                        ( tipo.equals(Simbolo.INT) && ( valor.contains(".") ) )  ){
                    err_semantic.add(line+" Token "+ valor + " incompatível: esperando "+tipo);
                }
    }
    
    public void MatchType(String valor, String tipo_esperado, String tipo, int line){

                if(!tipo.equals(tipo_esperado) ){
                    err_semantic.add(line+" Tipo "+ valor + " incompatível: esperando "+tipo);
                }
    }
    
    public MyHashMap<String, Simbolo> getEscopo_global() {
        return escopo_global;
    }

    public MyHashMap<String, Simbolo> getEscopo_atual() {
        return escopo_atual;
    }
    
    public void setEscopo_atual(MyHashMap<String, Simbolo> escopo_atual) {
        this.escopo_atual = escopo_atual;
       // System.out.println("MUDOU ESCOPO");
    }
    
    //usado pra checar validade da herança de classes
    public boolean MatchTipoAndCatg(String id, ArrayList<Category> catg, String tipo, MyHashMap<String, Simbolo> hash, int line){
        MyHashMap<String, Simbolo> aux = hash;
        Simbolo sy = null;
        boolean same_tipo=true, same_catg=true, both = false;
        
        if(catg!=null && tipo!=null)
            both = true;
        
            
        while(aux!=null){
            sy = aux.get(id);
            if(sy!=null){
                if(both){
                    if(catg!=null && (catg.contains(sy.getCategoria())) && (tipo!=null && sy.getTipo()!= null && sy.getTipo().equals(tipo))){
                        return true;
                    } 
                } else{
                    if(catg!=null && (catg.contains(sy.getCategoria())) || (tipo!=null && sy.getTipo()!= null && sy.getTipo().equals(tipo))){
                        return true;
                    }
                }
                
                if(tipo!=null && (sy.getTipo()!= null && !sy.getTipo().equals(tipo) || sy.getTipo()==null))
                    same_tipo = false;
                else
                    same_tipo = true;
                
                if(catg!=null && !catg.contains(sy.getCategoria()))
                    same_catg = false;
                else
                    same_catg = true;
            }
            aux = aux.getUpper_scope();
        }
        if(!same_catg && catg!=null){
            String str = line + " ID " + id +" não é ";
            boolean first = true;
            for(Category ctg: catg){
                if(!first)
                    str = str.concat(" ou ");
                
                str = str.concat(ctg.toString().toLowerCase());
                first = false;
                
            }
            err_semantic.add(str);
            
        } else if(!same_tipo && tipo!=null){
            err_semantic.add(line + " ID " + id +" não é "+tipo);
        } else
            err_semantic.add(line + " ID " + id+" não existe");
        
        return false;
    }
    
    public void printErrs(){
        for (String str : err_semantic){
            System.out.println(str);
        }
    }
}
