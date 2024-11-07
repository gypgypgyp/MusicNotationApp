package reaction;

import graphicsLib.G;
import graphicsLib.UC;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.TreeMap;

public class Shape implements Serializable {
    public static Database DB = Database.load();
    public static Collection<Shape> shapeList = DB.values(); //list backed by DB; changes to DB show here
    public static Shape DOT = DB.get("DOT");
    public Prototype.List prototypes = new Prototype.List();
    public String name;
    public Shape(String name){
        this.name = name;
    }
    public static void saveShapeDB(){Database.save();}

//    public static TreeMap<String, Shape> loadShapeDB(){
//        TreeMap<String, Shape> res = new TreeMap<>();
//        res.put("DOT", new Shape("DOT"));
//        String fileName = UC.ShapeDbFilename;
//        try{
//            System.out.println("attempting DB load...");
//            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
//            res = (TreeMap<String, Shape>)ois.readObject();
//            System.out.println("successful load");
//            ois.close();
//        }catch(Exception e){
//            System.out.println("load fail");
//            System.out.println(e);
//        }
//        return res;
//    }
//
//    public static void saveShapeDB(){
//        String filename = UC.ShapeDbFilename;
//        try{
//            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename));
//            oos.writeObject(DB);
//            System.out.println("saved "+filename);
//            oos.close();
//        }catch(Exception e){
//            System.out.println("failed database save");
//            System.out.println(e);
//        }
//    }
    public static Shape recognized(Ink ink){ //can return null
        if(ink.vs.size.x < UC.dotThreshold && ink.vs.size.y < UC.dotThreshold){return DOT;}
        Shape bestMatch = null;
        int bestSoFar = UC.noMatchDist;
        for(Shape s: shapeList){
            int d = s.prototypes.bestDist(ink.norm);
            if(d < bestSoFar){
                bestMatch = s;
                bestSoFar = d;
            }
        }
        return bestMatch;
    }

    //--------------------prototype--------------------
    public static class Prototype extends Ink.Norm implements Serializable{
        int nBlend = 1;
        public void blend(Ink.Norm norm){
            blend(norm, nBlend); //the blend in Ink.Norm
            nBlend ++;
        }

        //-----------------List---------------------
        //list of prototypes
        public static class List extends ArrayList<Prototype> implements Serializable{
            public static Prototype bestMatch; // side effect of bestDist
            public int bestDist(Ink.Norm norm){
                bestMatch = null;
                int bestSoFar = UC.noMatchDist;
                for(Prototype p: this){
                    int d = p.dist(norm);
                    if(d < bestSoFar){
                        bestMatch = p;
                        bestSoFar = d;
                    }
                }
                return bestSoFar;
            }


            public void train(Ink.Norm norm){
                if (bestDist(norm) < UC.noMatchDist){ //if the newly drawn shape's dist from an prototype is less than bestDist, find match and blend
                    bestMatch.blend(norm);
                }else{  //if no match, add a new prototype
                    add(new Shape.Prototype());
                }
            }

            private int showNdx(int x){return x/(m+w);}
            // Ndx = index. the index of the prototype we want to delete.
            // prototype list in the window. m is the margin. w is width.
            // if you click on the area of the prototype list, calculate which prototype to delete

            public boolean isShowDelete(G.VS vs){ //test if x,y is in the prototype list
                return vs.loc.y < m+w && showNdx(vs.loc.x) < size();
            }

            public void showDelete(G.VS vs){ //remove the prototype from the prototype list
                remove(showNdx(vs.loc.x));
            }

            private static int m = 10, w = 60;
            private static G.VS showBox = new G.VS(m, m, w, w);
            public void show(Graphics g){ //show box across top of screen
                g.setColor(Color.ORANGE);
                for(int i = 0; i < size(); i++){
                    Prototype p = get(i);
                    int x = m + i*(m+w);
                    showBox.loc.set(x,m);
                    p.drawAt(g,showBox);
                    g.drawString("" + p.nBlend, x, 20);
                }

            }
        }

    }

    //---------------DATABASE------------------
    public static class Database extends HashMap<String, Shape> {
        private Database(){
            super(); //set a TreeMap
            String dot = "DOT"; //has a dot in database
            put(dot, new Shape(dot)); //put dot into the TreeMap
        }

        public static Database load() {
            Database db = null;
            try{
                System.out.println("attempting DB load...");
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(UC.ShapeDbFilename));
                db = (Database)ois.readObject(); // object read. casting. i tell java that it is a database.
                System.out.println("successful load");
                ois.close();
            }catch(Exception e){
                System.out.println("load fail");
                System.out.println(e);
                db = new Database();//constuct a new
            }
            return db;
        }

        public static void save(){  //save database
            String filename = UC.ShapeDbFilename;
            try{
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename));
                oos.writeObject(DB);
                System.out.println("saved "+filename);
                oos.close();
            }catch(Exception e){
                System.out.println("failed database save");
                System.out.println(e);
            }
        }

        private Shape forceGet(String name){ //always return a shape.
            //get sth out the database, if the shape not exist, tree map return null.
            //if the result is null, give a new shpae.
            if (! DB.containsKey(name)){DB.put(name, new Shape(name));} // if not exist, then..
            return DB.get(name);
        }

        public void addPrototype(String name){
            if(isLegal(name)){
                forceGet(name).prototypes.add(new Prototype()); //add a prototype
            }
        }

        public void train(String name, Ink ink){
            if (isLegal(name)) {
                Shape rs = recognized(ink); //recognized shape
                if (rs == null || ! rs.name.equals(name)) {
                    //if not match
                    addPrototype(name);
                }else{
                    forceGet(name).prototypes.train(ink.norm); //fetch the prototypes, and train it, if input is legal
                }
            }
        }

        public static boolean isLegal(String name){  /// return type: boolean
            return ! name.equals("") && ! name.equals("DOT"); // 2 things dont want to train
        }
    }

}
