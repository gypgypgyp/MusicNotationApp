package reaction;

import graphicsLib.I;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import graphicsLib.UC;

public abstract class Reaction implements I.React {
    public static Map byShape = new Map(); // do the reactions by shape.
    public static List initialReactions = new List(); //used by undo to restart everything.
    // list of gestures we did so far. throw the last one of the list and reload the gestures.
    public Shape shape;
    public Reaction(String shapeName){
        shape = Shape.DB.get(shapeName);
        if (shape == null) {
            System.out.println("WTF? - Shape DB does not have : space" + shapeName);
        }
    }

    public static void nuke() {
        byShape = new Map();
        initialReactions.enable();
    }

    public void enable(){
        List list = byShape.getList(shape);
        if(!list.contains(this)){
            list.add(this);
        }
    }

    public void disable(){
        List list = byShape.getList(shape);
        list.remove(this);
    }

    public static Reaction best(Gesture g){
        return byShape.getList(g.shape).lowBid(g);
    }

    //-------------------List--------------------
    public static class List extends ArrayList<Reaction> {
        public void addReaction(Reaction r){add(r);r.enable();}
        public void removeReaction(Reaction r){remove(r);r.disable();}
        public void enable(){for(Reaction r: this){r.enable();}}
        public void clearAll(){
            for(Reaction r: this){
                r.disable();
            }
            this.clear();
        }
        public Reaction lowBid(Gesture g){ //can return null
            Reaction res = null;
            int bestSoFar = UC.noBid;
            for(Reaction r: this){
                int b = r.bid(g);
                if(b < bestSoFar){
                    bestSoFar = b;
                    res = r;
                }
            }
            return res;
        }
    }

    //------------------Map---------------------
    public static class Map extends HashMap<Shape, List> {
        //splitting things o cut by shape. reaction, by shape is a map??
        public List getList(Shape s){ //always succeed
            List res = get(s);
            if(res == null){
                res = new List();
                put(s, res);
            }
            return res;
        }

    }
}
