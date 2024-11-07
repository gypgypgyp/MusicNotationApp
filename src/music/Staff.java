package music;

import graphicsLib.UC;
import reaction.Gesture;
import reaction.Mass;
import reaction.Reaction;

import java.awt.*;
import java.util.ArrayList;

import static music.AppMusicEd.PAGE;

public class Staff extends Mass {
    public Sys sys;  // staff lives in sys, sys is its dad
    public int iStaff; // staff index, record ith staff
    public Staff.Fmt fmt; //staff format

    public Staff(int iStaff, Staff.Fmt staffFmt){
        super("BACK"); //go into the BACK layer
        this.iStaff = iStaff;
        fmt = staffFmt;

        addReaction(new Reaction("S-S") { //build barline
            @Override
            public int bid(Gesture g) {
                int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH();
                if (x < PAGE.margins.left || x > PAGE.margins.right + UC.barToMarginSnap) {
                    return UC.noBid;
                }
                int d = Math.abs(y1 - Staff.this.yTop()) + Math.abs(y2- Staff.this.yBot());
                // d: how close is the drawn line close to a barline (go from the top line to the bottom line)
//                return (d < 40) ? d : UC.noBid;
                return (d < 40) ? d + UC.barToMarginSnap: UC.noBid; // allow cycle bartype to outbid this
            }

            @Override
            public void act(Gesture g) {
                new Bar(Staff.this.sys, g.vs.xM());

            }
        });

        addReaction (new Reaction("W-S") { //Q-REST
            @Override
            public int bid(Gesture g) {
                int x = g.vs.xL(), y = g.vs.yM();
                if (x < PAGE.margins.left || x > PAGE.margins.right){return UC.noBid;}
                int top = Staff.this.yTop(), bot = Staff.this.yBot();
                if (y < top || y > bot){return UC.noBid;}
                return 10;
            }

            @Override
            public void act(Gesture g) {
                Time t = Staff.this.sys.getTime(g.vs.xL());
                new Rest(Staff.this, t);
            }
        });

        addReaction (new Reaction("E-S") { //eighth-REST
            @Override
            public int bid(Gesture g) {
                int x = g.vs.xL(), y = g.vs.yM();
                if (x < PAGE.margins.left || x > PAGE.margins.right){return UC.noBid;}
                int top = Staff.this.yTop(), bot = Staff.this.yBot();
                if (y < top || y > bot){return UC.noBid;}
                return 10;
            }

            @Override
            public void act(Gesture g) {
                Time t = Staff.this.sys.getTime(g.vs.xL());
                (new Rest(Staff.this, t)).nFlag = 1;
            }
        });

        addReaction (new Reaction("S-S"){
           public int bid(Gesture g){
               if (Staff.this.sys.iSys != 0){return UC.noBid;}
               int y1 = g.vs.yL(), y2 = g.vs.yH();
               int iStaff = Staff.this.iStaff;
               if (iStaff == PAGE.sysFmt.size()-1){return UC.noBid;} //if the last staff, can not add continue
                if (Math.abs(y1 - Staff.this.yBot()) > 20){return UC.noBid;}
                Staff nextStaff = sys.staffs.get(iStaff+1);
                if (Math.abs(y2 - nextStaff.yTop())>20){return UC.noBid;}
                return 10;
           }
           public void act(Gesture g){
               PAGE.sysFmt.get(Staff.this.iStaff).toggleBarContinues();
           }
        });

        addReaction(new Reaction("SW-SW") { // add note to staff
            @Override
            public int bid(Gesture g) {
                int x = g.vs.xM(), y = g.vs.yM();
                if (x < PAGE.margins.left || x > PAGE.margins.right){return UC.noBid;}
                int H = Staff.this.H(), top = Staff.this.yTop() - H, bot = Staff.this.yBot() + H;
                if (y < top || y > bot){return UC.noBid;}
                return 10;
            }

            @Override
            public void act(Gesture g) {
                new Head(Staff.this, g.vs.xM(), g.vs.yM());
            }
        });
    }

    public int sysOff(){return sys.fmt.staffOffset.get(iStaff);}  //helper for the corrdinate. off set for the barline
    public int yTop(){return sys.yTop()+sysOff();}
    public int yBot(){return yTop()+fmt.height();}

    @Override
    public void show(Graphics g) {}

    public int H() {
        return fmt.H;
    }

    public int yLine(int n){return yTop()+ n*H();}
    public int lineOfY(int y){
        return (y + 100*H() - yTop() + H()/2)/H() - 100;
    } // deal with truncation --> add half of the H, to ensure the rounded in right way
    // if negative, when y-yTop is negative --> add a bias (100), and finally eliminate 100


    //-----------------Fmt------------------------------
    public static class Fmt{
        public int nLines = 5, H = 8;
        public boolean barContinues = false;
        public void toggleBarContinues (){barContinues = ! barContinues;}
        public int height() {return 2 * H * (nLines - 1);} //一组staff的height

        public void showAt(Graphics g, int y) { // where to draw the thing
            int LEFT = PAGE.margins.left, RIGHT = PAGE.margins.right; // PAGE is set in APP
            for (int i = 0; i < nLines; i++){
                g.drawLine(LEFT, y + 2*H*i, RIGHT, y + 2*H*i);
            }
        }
    }

    //-------------------List-----------------------
    public static class List extends ArrayList<Staff> {

    }

}
