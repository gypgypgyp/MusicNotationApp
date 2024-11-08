package reaction;

import graphicsLib.I;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;


public class Layer extends ArrayList<I.Show> implements I.Show {

    public String name; // allow look up layer by its name
    public static HashMap<String, Layer> byName = new HashMap<>();
    public static Layer ALL = new Layer("ALL");
    public Layer(String name){
        this.name = name;
        if(!name.equals("ALL")){
            ALL.add(this);
        }
        byName.put(name, this);
    }

    public static void nuke(){
        for(I.Show lay: ALL){((Layer)lay).clear();} //layer ALL is a list of layer.
        // fetch the I.SHOW of ALL and call them 'lay', check if the lay is really a Layer(type checking). then clear the layer
    }

    @Override
    public void show(Graphics g){
        for (I.Show item : this){item.show(g);} //
    }
}
