/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TabelaSimbolos;

import java.util.HashMap;

/**
 *
 * @author Douglas
 */
public class MyHashMap<K, V> extends HashMap<K, V>{
    private MyHashMap<K, V> upper_scope;
    private SimboloClasse dono;

    public MyHashMap(MyHashMap<K, V> upper_scope){
        dono = null;
        this.upper_scope = upper_scope;
    }   
    
    public MyHashMap<K, V> getUpper_scope() {
        return upper_scope;
    }

    public void setUpper_scope(MyHashMap<K, V> upper_scope) {
        this.upper_scope = upper_scope;
    }
    
    public SimboloClasse getDono() {
        return dono;
    }

    public void setDono(SimboloClasse dono) {
        this.dono = dono;
    }
    
    
}
