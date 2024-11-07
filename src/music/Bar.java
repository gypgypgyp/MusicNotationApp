package music;

import graphicsLib.UC;
import reaction.Gesture;
import reaction.Mass;
import reaction.Reaction;

import java.awt.*;

import static music.AppMusicEd.PAGE;

public class Bar extends Mass {
    private static final int FAT = 0x2, RIGHT = 0x4, LEFT = 0x8; //bits in the bar type. use hex, using bits.
    public Sys sys;
    public int x, barType = 0; // boolean in java is an int. bartype include 4 bitst


    public Bar (Sys sys, int x){
        super("BACK");
        this.sys = sys;
        this.x = x;
        if (Math.abs(PAGE.margins.right - x) < UC.barToMarginSnap){
            this.x = PAGE.margins.right;
        }
        addReaction(new Reaction("S-S") { //CYCLE  the bartype
            @Override
            public int bid(Gesture g) {
                int x = g.vs.xM();
                if (Math.abs(x-Bar.this.x) > UC.barToMarginSnap){return UC.noBid;} // if too far, no bid
                int y1 = g.vs.yL(), y2 = g.vs.yH();
                if (y1 < Bar.this.sys.yTop() - 20 || y2 > Bar.this.sys.yBot() + 20){return UC.noBid;}
                //check the y values be in the range of the sys
                return Math.abs(x - Bar.this.x);
            }

            @Override
            public void act(Gesture g) {
                Bar.this.cycleType();
            }
        });

        addReaction(new Reaction("DOT") { //put a DOT on the bar
            @Override
            public int bid(Gesture g) {
                int x = g.vs.xM(), y= g.vs.yM();
                if (y < Bar.this.sys.yTop() || y > Bar.this.sys.yBot()){return UC.noBid;}
                int dist = Math.abs(x - Bar.this.x);  //
                if (dist > 3*PAGE.sysFmt.maxH) {return UC.noBid;}
                return dist;
            }

            @Override
            public void act(Gesture g) {
                if (g.vs.xM() < Bar.this.x) {Bar.this.toggleLeft();
                }else{
                    Bar.this.toggleRight();
                }
            }
        });
    }

    public void cycleType(){ //cycle the bartype
        barType++;
        if (barType > 2){barType = 0;}
    }


    public void toggleLeft(){barType = barType ^ LEFT;} //toggle bits with exclusive or. the new barType is True when barType and Left are different
    public void toggleRight(){barType = barType ^ RIGHT;} // like mask filled. only change specific bit . bit flip
    @Override
    public void show(Graphics g) {
        int sysTop = sys.yTop(), y1 = 0, y2 = 0; //y1, y2, top and bot of connected components
        boolean justSawBreak = true;
        for (int i = 0; i < sys.fmt.size(); i++){
            Staff.Fmt sf = sys.fmt.get(i);
            Staff staff = sys.staffs.get(i);
            if (justSawBreak) {y1 = staff.yTop();}
            y2 = staff.yBot();
            if (! sf.barContinues){
                drawLines(g, x, y1, y2);
            }
            justSawBreak = !sf.barContinues;
            if (barType > 3){
                drawDots(g, x, staff.yTop());
            }
        }

//        g.setColor(barType == 1 ? Color.RED : Color.BLACK);
//        int yTop = sys.yTop(), N = sys.fmt.size(); //list of format
//        for (int i = 0; i < N; i++){ //draw the bar cross each line of the sys
//            Staff.Fmt sf = sys.fmt.get(i); //fetch which staff fmt is for the i
//            int topLine = yTop + sys.fmt.staffOffset.get(i);
//            g.drawLine(x, topLine, x, topLine+sf.height());
//        }
    }

    public static void wings(Graphics g, int x, int y1, int y2, int dx, int dy){
        // dx, dy为barline上下两个括号的角出去的距离, x为竖线位置，y1, y2为竖线的上下
        g.drawLine(x, y1, x+dx, y1-dy);
        g.drawLine(x, y2, x+dx, y2+dy);
    }

    public static void fatBar(Graphics g, int x, int y1, int y2, int dx){
        g.fillRect(x, y1, dx, y2-y1);
    }

    public static void thinBar(Graphics g, int x, int y1, int y2){
        g.drawLine(x, y1, x, y2);
    }

    public void drawDots(Graphics g, int x, int top){
        int H = PAGE.sysFmt.maxH;
        if ((barType & LEFT) != 0){ //bits not equal
            g.fillOval(x-3*H, top+11*H/4, H/2, H/2);
            g.fillOval(x-3*H, top+19*H/4, H/2, H/2);
        }
        if ((barType & RIGHT) != 0){ //bits not equal
            g.fillOval(x+3*H/2, top+11*H/4, H/2, H/2);
            g.fillOval(x+3*H/2, top+19*H/4, H/2, H/2);
        }
    }

    public void drawLines(Graphics g, int x, int y1, int y2){
        int H = PAGE.sysFmt.maxH;
        if (barType == 0){thinBar(g, x, y1, y2);}
        if (barType == 1){thinBar(g, x, y1, y2); thinBar(g, x-H, y1, y2);}
        if (barType == 2){fatBar(g, x-H, y1, y2, H); thinBar(g, x-2*H, y1, y2);}
        if (barType >= 4){
            fatBar(g, x-H, y1, y2, H);
            if((barType & LEFT) != 0){
                thinBar(g, x-2*H, y1, y2);
                wings(g, x-2*H, y1, y2, -H, H);
            }
            if((barType & RIGHT) != 0){
                thinBar(g, x+H, y1, y2);
                wings(g, x+H, y1, y2, H, H);
            }
        }
    }
}
