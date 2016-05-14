/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TabelaSimbolos;

import java.util.ArrayList;
import modulo_completo.*;

/**
 *
 * @author Douglas
 */
public class SimboloMetodo extends Simbolo{
    private ArrayList<Simbolo> parametros;

    public SimboloMetodo(Category categoria){
        super(categoria);
        parametros = new ArrayList<>();
    }

    public void addParametro(Simbolo parametro) {
        parametros.add(parametro);
    }

    public ArrayList<Simbolo> getParametros() {
        return parametros;
    }


}
