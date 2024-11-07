package sandbox;

import graphicsLib.G;
import graphicsLib.UC;
import graphicsLib.Window;
import reaction.*;

import java.awt.*;
import java.awt.event.MouseEvent;

public class ReactionTest extends Window {
    static{new Layer("BACK");new Layer("FORE");}
    public ReactionTest(){
        super("Reaction Test", 1000,700);
        Reaction.initialReactions.addReaction(new Reaction("SW-SW"){
            public int bid(Gesture g){return 0;}
            public void act(Gesture g){new Box(g.vs);}
        }); //anonymous class
    }
    public void paintComponent(Graphics g){
        G.fillBack(g);
        g.setColor(Color.BLUE);
        Ink.BUFFER.show(g);
        Layer.ALL.show(g);
    }
    public void mousePressed(MouseEvent me){
        Gesture.AREA.dn(me.getX(), me.getY());
        repaint();
    }
    public void mouseDragged(MouseEvent me){
        Gesture.AREA.drag(me.getX(), me.getY());
        repaint();
    }
    public void mouseReleased(MouseEvent me){
        Gesture.AREA.up(me.getX(), me.getY());
        repaint();
    }
    public static void main (String[] args){
        PANEL = new ReactionTest();
        Window.launch();
    }
    //--------------------box------------------------
    public static class Box extends Mass {
        public G.VS vs;
        public Color c = G.rndColor();
        public Boolean isOval = false;
        public Box(G.VS vs){
            super("BACK");this.vs = vs;
            addReaction(new Reaction("S-S"){
                public int bid(Gesture g){
                    int x = g.vs.xM(), y = g.vs.yL(); //lo value at y, middle at x
                    if(Box.this.vs.hit(x, y)){  //compound this: referring to the Box
                        return Math.abs(x - Box.this.vs.xM()); //计算点击的点和中点的距离，if 2 shapes overlap, it will delete the one with closer mid point
                    }else{
                        return UC.noBid; //
                    }
                }
                public void act(Gesture g){
                    Box.this.deleteMass();
                }

            }); // add reaction to each box, so that we can delete box.
            // anonymous class. 

            addReaction(new Reaction("DOT"){
               public int bid(Gesture g) {
                   int x = g.vs.xM(), y = g.vs.yL(); //lo value at y, middle at x
                   if (Box.this.vs.hit(x, y)) {  //compound this: referring to the Box
                       return Math.abs(x - Box.this.vs.xM()); //计算点击的点和中点的距离，if 2 shapes overlap, it will delete the one with closer mid point
                   } else {
                       return UC.noBid; //
                   }
               }
                   public void act(Gesture g){
                       Box.this.c = G.rndColor();
               }
            });

            addReaction(new Reaction("E-E"){
                public int bid(Gesture g) {
                    int x = g.vs.xL(), y = g.vs.yM(); //lo value at y, middle at x
                    if (Box.this.vs.hit(x, y)) {  //compound this: referring to the Box
                        return Math.abs(x - Box.this.vs.xM()); //计算点击的点和中点的距离，if 2 shapes overlap, it will delete the one with closer mid point
                    } else {
                        return UC.noBid; //
                    }
                }
                public void act(Gesture g){
                    Box.this.isOval = !Box.this.isOval;
                }
            });
        }
        @Override
        public void show(Graphics g) {
            if(isOval) {
                g.setColor(c);
                g.fillOval(vs.loc.x, vs.loc.y, vs.size.x, vs.size.y);
            }else{
                vs.fill(g, c);
            }
        }
    }
}
