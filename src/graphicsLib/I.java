package graphicsLib;

import reaction.Gesture;

import java.awt.*;

public interface I {
    public interface Area{
        public boolean hit(int x, int y); // whether an area is hit. function signatures with no body tool.
        public void dn(int x,int y); // down
        public void up(int x,int y); // up
        public void drag(int x,int y); // drag

    }

    // call the list to do a same function
    public interface Show{public void show(Graphics g);}
    public interface Act{public void act(Gesture g);}
    public interface React extends Act {public int bid(Gesture g);}
}
