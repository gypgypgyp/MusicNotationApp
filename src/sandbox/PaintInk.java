package sandbox;

import graphicsLib.G;
import graphicsLib.UC;
import graphicsLib.Window;
import reaction.Ink;
import reaction.Shape;

import java.awt.*;
import java.awt.event.MouseEvent;

public class PaintInk extends Window {

    public static Ink.List inkList = new Ink.List();
    public static Shape.Prototype.List pList = new Shape.Prototype.List();
//    static {inkList.add(new Ink());}
    public PaintInk() {
        super("PaintInk", UC.mainWindowWidth, UC.mainWindowHeight);
    }
    public void paintComponent(Graphics g ){
        G.fillBack(g);
        g.setColor(Color.BLUE);
        g.drawString("points:"+Ink.BUFFER.n, 20,20);
//        g.drawLine(0,0,100,100);
        inkList.show(g);
        pList.show(g);
        g.setColor(Color.RED);
        Ink.BUFFER.show(g);
        if (inkList.size() > 1){
            int last = inkList.size()-1;
            int dist = inkList.get(last).norm.dist(inkList.get(last-1).norm); //distance between last 2 norms
            g.setColor(dist > UC.noMatchDist ? Color.RED : Color.BLACK);
            g.drawString("dist: "+ dist, 600,60);
        }
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
        Ink.BUFFER.up(me.getX(), me.getY());
        Ink ink = new Ink();
        Shape.Prototype proto;
        inkList.add(ink);
        if(pList.bestDist(ink.norm) < UC.noMatchDist){
            proto = Shape.Prototype.List.bestMatch;
            proto.blend(ink.norm);
        }else{
            proto = new Shape.Prototype();
            pList.add(proto);
        }
        ink.norm = proto;
        repaint();
    }

    public static void main(String[] args) {
        PANEL = new PaintInk();
        Window.launch();

    }
}

