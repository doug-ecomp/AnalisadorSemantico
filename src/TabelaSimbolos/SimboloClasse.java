/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TabelaSimbolos;

import java.util.HashMap;
import modulo_completo.Category;
import modulo_completo.Type;

/**
 *
 * @author Douglas
 */
public class SimboloClasse extends Simbolo{
    
    private SimboloClasse heranca_pai;
    private MyHashMap<String, Simbolo> inner_scope;
    
    public SimboloClasse(Category categoria, MyHashMap<String, Simbolo> inner_scope) {
        super(categoria);
        heranca_pai = null;
        setTipo(Simbolo.CLASSE);
        this.inner_scope = inner_scope;
    }
    
    public MyHashMap<String, Simbolo> getInner_scope() {
        return inner_scope;
    }

    public void setInner_scope(MyHashMap<String, Simbolo> inner_scope) {
        this.inner_scope = inner_scope;
    }

    public SimboloClasse getHeranca_pai() {
        return heranca_pai;
    }

    public void setHeranca_pai(SimboloClasse heranca_pai) {
        this.heranca_pai = heranca_pai;
    }

}
