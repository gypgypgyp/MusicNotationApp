package music;

import graphicsLib.UC;
import reaction.Gesture;
import reaction.Ink;
import reaction.Mass;
import reaction.Reaction;

import java.awt.*;
import java.util.ArrayList;

import static music.AppMusicEd.PAGE;

public class Sys extends Mass {
    public Staff.List staffs = new Staff.List();
    public Page page = PAGE; // sys's dad is page
    public int iSys; // index of the sys
    public Sys.Fmt fmt;
    public Time.List times;
    public Stem.List stems = new Stem.List();

    public Sys(int iSys, Sys.Fmt sysFmt){
        super("BACK");
        this.iSys = iSys;
        this.fmt = sysFmt;
        this.times = new Time.List(this);
        for (int i = 0; i < sysFmt.size(); i++){
            addStaff(new Staff(i, sysFmt.get(i)));
        }

        addReaction(new Reaction("E-E") { // beam and stems
            @Override
            public int bid(Gesture g) {
                int x1 = g.vs.xL(), y1 = g.vs.yL(), x2 = g.vs.xH(), y2 = g.vs.yH();

                System.out.println("before fastreject");

                if(stems.fastReject(y1, y2)){return UC.noBid;}
                Stem.List temp = stems.allIntersectors(x1, y1, x2, y2);

                System.out.println("intersectored" + temp.size());

                if(temp.size() < 2){return UC.noBid;}
                Beam beam = temp.get(0).beam; // check all crossed stems owned by same beam(including null)
                //add master stems ; add flags

                for(Stem s: temp){
                    if(s.beam != beam){return UC.noBid;}
                }

                if(beam == null && temp.size() != 2){return UC.noBid;}
                if(beam == null && (temp.get(0).nFlag != 0 || temp.get(1).nFlag != 0)){return UC.noBid;}

                return 50;

            }

            @Override
            public void act(Gesture g) {
                int x1 = g.vs.xL(), y1 = g.vs.yL(), x2 = g.vs.xH(), y2 = g.vs.yH();
                Stem.List temp = stems.allIntersectors(x1, y1, x2, y2);
                Beam beam = temp.get(0).beam;
                if(beam == null){
                    //when there is no beam, construct new beam
                    new Beam(temp.get(0), temp.get(1));
                }else{
                    //when already have beam, add a beam
                    for(Stem s: temp){s.incFlag();}
                }
            }
        });
    }

    public Time getTime(int x){
        return times.getTime(x);
    }

    //where sys lives.
    public int yTop(){
        return page.sysTop(iSys); // let the dad return the x a
    }

    public int yBot(){ return staffs.get(staffs.size()-1).yBot();}

    public void addStaff(Staff s){
        staffs.add(s);
        s.sys = this;
    }

    @Override
    public void show(Graphics g) { //在staff左边加竖线
        int y = yTop(), x = PAGE.margins.left;
        g.drawLine(x, y, x, y + fmt.height());
    }

    //--------------------Fmt------------------------
    public static class Fmt extends ArrayList<Staff.Fmt> {
        public int maxH = 0;
        public ArrayList<Integer> staffOffset = new ArrayList<>();

        public int height() {
            int last = size() - 1; //
            return staffOffset.get(last) + get(last).height(); //get(last) is a Staff
        }

        public void showAt(Graphics g, int y){
            for (int i = 0; i < size(); i++){
                get(i).showAt(g, y + staffOffset.get(i));
                // get(i) is a staff format object
            }

        }

        public void addStaffFmt(Staff.Fmt sf, int yOff) {
            add(sf);
            staffOffset.add(yOff);
            if (maxH < sf.H) {
                maxH = sf.H;
            }
        }
    }

    //-------------------List-----------------------
    public static class List extends ArrayList<Sys> {

    }
}
