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
    
    public void addSimbolo(Simbolo symbol, int line){
        
        if(!searchSymbol(symbol, escopo_atual)){
            escopo_atual.put(symbol.getLexeme(), symbol);
        } else {
            System.out.println(line+" ID já reservado e não pode ser sobrescrito");
        }
        System.out.println(escopo_atual.size());
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
    
    public void matchTypeNum(String valor, String tipo, int line){

                if( !(  ( tipo.equals(Simbolo.FLOAT) && valor.contains(".") ) ||
                        ( tipo.equals(Simbolo.INT) && !( valor.contains(".") ) ) ) ){
                    System.out.println(line+" Tipo incompatível: esperando "+tipo);
                }
    }
    
    public void matchTypeStr(String tipo, int line){

                if(!tipo.equals(Simbolo.STRING) ){
                    System.out.println(line+" Tipo incompatível: esperando "+tipo);
                }
    }
    
    public void matchTypeChar(String tipo, int line){

                if(!tipo.equals(Simbolo.CHAR)){
                    System.out.println(line+" Tipo incompatível: esperando "+tipo);
                }
    }
    
    public void matchTypeBool(String tipo, int line){

                if(!tipo.equals(Simbolo.BOOL)){
                    System.out.println(line+" Tipo incompatível: esperando "+tipo);
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
    }
    
    public Simbolo SearchAndMatchCatg(String id, Category catg, MyHashMap<String, Simbolo> hash, int line){
        Simbolo sy = hash.get(id);
        if(sy!=null){
            if(sy.getCategoria()!=catg){
                System.out.println(line + " ID " + id +" não é "+catg.toString().toLowerCase());
                return null;
            }                
            else
                return sy;
        }
        System.out.println(line + " ID " + id+" não existe");
        return null;
    }
}
