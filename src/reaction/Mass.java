package reaction;

import graphicsLib.G;
import graphicsLib.I;

public abstract class Mass extends Reaction.List implements I.Show {
    public Layer layer;
    public Mass (String layerName){
        layer = Layer.byName.get(layerName);
        if(layer != null){
            layer.add(this);
        }else{
            System.out.println("Bad layer name: " + layerName);
        }
    }
    public void deleteMass(){
        clearAll();
        layer.remove(this);
    }

    // bug fix ArrayList remove
    private int hashCode = G.rnd(100_000_000);
    public boolean equals(Object o){return this == o;}
    public int hashCode(){return hashCode;}
}
