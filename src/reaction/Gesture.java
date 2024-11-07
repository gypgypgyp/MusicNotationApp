package reaction;

import graphicsLib.G;
import graphicsLib.I;

import java.util.ArrayList;

public class Gesture {
    private static List UNDO = new List();
    public Shape shape;
    public G.VS vs;
    private Gesture(Shape shape, G.VS vs){
        this.shape = shape;
        this.vs = vs;
    }

    public static Gesture getNew(Ink ink){ //can return null
        Shape s = Shape.recognized(ink);
        return (s == null) ? null : new Gesture(s,ink.vs);
    }


    public void redoGesture(){ //don't add to UNDO list. it's already in the UNDO
        Reaction r = Reaction.best(this);
        if(r != null){r.act(this);}
    }

    public void doGesture(){ //Does add to the UNDO list
        Reaction r = Reaction.best(this);
        if(r != null){UNDO.add(this); r.act(this);}
    }

    public static void undo(){ //delete sth from the list ，删除list最后一个
        if(UNDO.size() > 0){
            UNDO.remove(UNDO.size() - 1);
            Layer.nuke(); //eliminate all the messes. nuclear
            Reaction.nuke(); //clear the byShape map, then reload initial reactions
            UNDO.redo();
        }
    }

    public static I.Area AREA = new I.Area(){
        public boolean hit(int x, int y){return true;}
        public void dn(int x, int y){Ink.BUFFER.dn(x,y);}
        public void drag(int x, int y){Ink.BUFFER.drag(x,y);}
        public void up(int x, int y){
            Ink.BUFFER.add(x, y);
            Ink ink = new Ink();
            Gesture gest = Gesture.getNew(ink); //can fail if unrecognized.
            Ink.BUFFER.clear();
            if (gest != null){
                System.out.println(gest.shape.name);
                if (gest.shape.name.equals("N-N")){
                    undo();
                }else{
                    gest.doGesture();
                }
//                Reaction r = Reaction.best(gest); //can fail if no reaction want it.
//                if (r != null){r.act(gest);}
            }
        }
    };

    //-----------------------------List-------------
    public static class List extends ArrayList<Gesture>{

        public void redo() {
            for(Gesture g: this){g.redoGesture();}
        }
    }

}
