package music;

import graphicsLib.UC;
import reaction.Gesture;
import reaction.Reaction;

import java.awt.*;

public class Rest extends Duration{
    public Staff staff;
    public Time time;
    public int line = 4; //middle line of the 5 lines. 0, 2, 4, 4 is the middle line
    public Rest(Staff staff, Time time){
        this.staff = staff;
        this.time = time;
        // reactions go next
        addReaction(new Reaction("E-E") { //add a flag to the rest, up to four flags
            @Override
            public int bid(Gesture g) {
                int y = g.vs.yM(), x1 = g.vs.xL(), x2 = g.vs.xH(), x = Rest.this.time.x;
                if (x1 > x || x2 < x){return UC.noBid;} //crossed somewhere
                return Math.abs(y-Rest.this.staff.yLine(4)); //how far is the gesture from the middle line
            }

            @Override
            public void act(Gesture g) {
                Rest.this.incFlag(); //duration, increment flag
            }
        });

        addReaction(new Reaction("W-W") { //decrement a flag to the rest
            @Override
            public int bid(Gesture g) {
                int y = g.vs.yM(), x1 = g.vs.xL(), x2 = g.vs.xH(), x = Rest.this.time.x;
                if (x1 > x || x2 < x){return UC.noBid;} //crossed somewhere
                return Math.abs(y-Rest.this.staff.yLine(4)); //how far is the gesture from the middle line
            }

            @Override
            public void act(Gesture g) {
                Rest.this.decFlag(); //duration, decrement flag
            }
        });

        addReaction(new Reaction("DOT") { //cycleDots
            @Override
            public int bid(Gesture g) {
                int xr = Rest.this.time.x, yr = Rest.this.y();
                int x = g.vs.xM(), y = g.vs.yM();
                if (x < xr || x > xr+40 || y < yr-40 || y > yr+40){return UC.noBid;}
                //dots are supposed to be on the right
                return Math.abs(x-xr) + Math.abs(y-yr);
            }

            @Override
            public void act(Gesture g) {cycleDot();}
        });
    }
    public int y(){return staff.yLine(line);} // coordinate calculation

    public void show(Graphics g){
        int H = staff.H(), y = y();
        if (nFlag == -2){Glyph.REST_W.showAt(g, H, time.x, y);}
        if (nFlag == -1){Glyph.REST_H.showAt(g, H, time.x, y);}
        if (nFlag == 0){Glyph.REST_Q.showAt(g, H, time.x, y);}
        if (nFlag == 1){Glyph.REST_1F.showAt(g, H, time.x, y);}
        if (nFlag == 2){Glyph.REST_2F.showAt(g, H, time.x, y);}
        if (nFlag == 3){Glyph.REST_3F.showAt(g, H, time.x, y);}
        if (nFlag == 4){Glyph.REST_4F.showAt(g, H, time.x, y);}

        int off = UC.gapRestToFirstDot, sp = UC.gapBetweenAugDot; //offset where you put dots
        for (int i = 0; i < nDot; i++){g.fillOval(time.x+off+i*sp, y-3*H/2, H*2/3, H*2/3);}
    }
}
