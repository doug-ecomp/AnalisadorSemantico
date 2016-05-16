/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulo_analisadorSemantico;

import TabelaSimbolos.MyHashMap;
import TabelaSimbolos.Simbolo;
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
        
        if(!searchSymbol(symbol, escopo_atual)){
            escopo_atual.put(symbol.getLexeme(), symbol);
        } else {
            err_semantic.add(symbol.getLinha()+" ID já reservado e não pode ser sobrescrito");
        }
        //System.out.println(escopo_atual.size());
    }
    
    public boolean searchSymbol(Simbolo symbol, MyHashMap<String, Simbolo> start){
        MyHashMap<String, Simbolo> temp = start;
        boolean same_scope=true, category=false;
        Simbolo sy;
        while( !(temp == null) ){
            sy = temp.get(symbol.getLexeme());
            if( sy != null){
                if( !(sy.getCategoria()==Category.VARIAVEL || sy.getCategoria()==Category.VETOR) ){
                    category = true;
                }
                
                return (same_scope || category);
            }
            
            temp = temp.getUpper_scope();
            same_scope = false;
        }
        
        return false;
    }
    
    //Os proximos 4 métodos são usados para testar a compatibilidade dos tipos na atribuição à constantes
    public void matchTypeNum(String valor, String tipo, int line){

                if( !(  ( tipo.equals(Simbolo.FLOAT) && valor.contains(".") ) ||
                        ( tipo.equals(Simbolo.INT) && !( valor.contains(".") ) ) ) ){
                    err_semantic.add(line+" Tipo incompatível: esperando "+tipo);
                }
    }
    
    public void matchTypeStr(String tipo, int line){

                if(!tipo.equals(Simbolo.STRING) ){
                    err_semantic.add(line+" Tipo incompatível: esperando "+tipo);
                }
    }
    
    public void matchTypeChar(String tipo, int line){

                if(!tipo.equals(Simbolo.CHAR)){
                    err_semantic.add(line+" Tipo incompatível: esperando "+tipo);
                }
    }
    
    public void matchTypeBool(String tipo, int line){

                if(!tipo.equals(Simbolo.BOOL)){
                    err_semantic.add(line+" Tipo incompatível: esperando "+tipo);
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
    public Simbolo SearchAndMatchCatg(String id, Category catg, MyHashMap<String, Simbolo> hash, int line){
        Simbolo sy = hash.get(id);
        if(sy!=null){
            if(sy.getCategoria()!=catg){
                err_semantic.add(line + " ID " + id +" não é "+catg.toString().toLowerCase());
                return null;
            }                
            else
                return sy;
        }
        err_semantic.add(line + " ID " + id+" não existe");
        return null;
    }
    
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
