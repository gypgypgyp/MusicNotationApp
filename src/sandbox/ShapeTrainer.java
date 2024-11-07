package sandbox;

import graphicsLib.G;
import graphicsLib.UC;
import graphicsLib.Window;
import reaction.Shape;
import reaction.Ink;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class ShapeTrainer extends Window {
    public static final String UNKNOWN = "<- this name is unknown";
    public static final String KNOWN = "<- this is a known shape";
    public static final String ILLEGAL = "<- this name is NOT a legal shape name";

    public static String curName = ""; //the name user type in that will be trained. Should be given an empty string
    public static String curState = ILLEGAL;
    public static Shape.Prototype.List pList = new Shape.Prototype.List();

    public ShapeTrainer(){
        super("shapeTrainer",1000,700);
    }

    public void paintComponent(Graphics g){
        G.fillBack(g);
        g.setColor(Color.BLACK);
        g.drawString(curName,600,30);
        g.drawString(curState,700,30);
        g.setColor(Color.RED);
        Ink.BUFFER.show(g);
        if(pList != null){pList.show(g);}
    }

    public void mousePressed(MouseEvent me){
        Ink.BUFFER.dn(me.getX(), me.getY());
        repaint();
    }

    public void mouseDragged(MouseEvent me){
        Ink.BUFFER.drag(me.getX(), me.getY());
        repaint();
    }

    public void mouseReleased(MouseEvent me){
        Ink ink = new Ink();
        if(pList != null && pList.isShowDelete(ink.vs)){ //if the
            pList.showDelete(ink.vs); //which one to delete
            repaint();
            return;
        }
        Shape.DB.train(curName, ink); //
        setState();
        repaint();
    }

//    public void mouseReleased(MouseEvent me){
//        Ink.BUFFER.up(me.getX(), me.getY());
//        Ink ink = new Ink();
//        Shape.Prototype proto;
//        if(pList == null){
//            Shape s = new Shape(curName);
//            Shape.DB.put(curName, s);
//            pList = s.prototypes;
//        }
//        if(pList.bestDist(ink.norm) < UC.noMatchDist){
//            proto = Shape.Prototype.List.bestMatch;
//            proto.blend(ink.norm);
//        }else{
//            proto = new Shape.Prototype();
//            pList.add(proto);
//        }
//        setState(); //possibly unknown converted to known
//        repaint();
//    }

    public void keyTyped(KeyEvent ke){
        char c = ke.getKeyChar();
        System.out.println("typed: " + c);
        curName = (c == ' ' || c == 0x0D || c == 0x0A) ? "" : curName + c;
        if(c == 0x0D || c == 0x0A){Shape.saveShapeDB();}
        setState();
        repaint();
    }

    public void setState(){
        curState = (curName.equals("") || curName.equals("DOT")) ? ILLEGAL : UNKNOWN;
        if(curState == UNKNOWN){
            if(Shape.DB.containsKey(curName)){
                curState = KNOWN;
                pList = Shape.DB.get(curName).prototypes;
            }else{
                pList = null;
            }
        }
    }

    public static void main(String[] args){
        PANEL = new ShapeTrainer();
        Window.launch();
    }
}
