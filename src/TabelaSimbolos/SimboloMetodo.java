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

    public void setParametros(ArrayList<Simbolo> parametros) {
        this.parametros = parametros;
    }

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
    
    public ArrayList<String> ComparaParametros(ArrayList<Simbolo> par_in){
        ArrayList<String> erros = new ArrayList<>();
        if(parametros.size()==par_in.size()){
            for(int i = 0; i < parametros.size(); i++){
                if( !(parametros.get(i).getTipo().equals(par_in.get(i).getTipo())) ){
                   
                    erros.add(parametros.get(i).getLinha()+" Tipo incompatível na sobrescrita do metodo: esperando "+parametros.get(i).getTipo());
                    
                }
            }
            
            if(erros.size()>0)
                return erros;
            else
                return null;
            
        } else{
            erros.add("Quantidade de parametros incompativel");
            return erros;
        }
        
    }


}
