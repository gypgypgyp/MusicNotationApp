package music;

import graphicsLib.UC;
import reaction.Gesture;
import reaction.Reaction;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class Stem extends Duration implements Comparable<Stem>{
    public Staff staff;
    public Head.List heads = new Head.List();
    public boolean isUp = true;
    public Beam beam = null;

    public Stem(Staff staff, Head.List heads, boolean up){
        this.staff = staff;
//        staff.sys.stems.addStem(this); // this is down in Time
        isUp = up;

        //
        for(Head h : heads){
            h.unStem(); //stems used to connected to
            h.stem = this;
        }

        this.heads = heads;

        staff.sys.stems.addStem(this);
        setWrongSides();

        addReaction(new Reaction("E-E") { // increment flags
            @Override
            public int bid(Gesture g) {
                int y = g.vs.yM(), x1 = g.vs.xL(), x2 = g.vs.xH();
                int xS = Stem.this.heads.get(0).time.x; // get the first head on the stem, and get the x of it
                if (x1 > xS || x2 < xS){return UC.noBid;} //gesture not cross
                int y1 = Stem.this.yLo(), y2 = Stem.this.yHi();
                if (y < y1 || y > y2){return UC.noBid;} //gesture need to cross the heads
//                return Math.abs(y-(y1+y2)/2);
                return Math.abs(y-(y1+y2)/2) + 100; //allow sys.E-E to win
            }

            @Override
            public void act(Gesture g) { //increment flag
                Stem.this.incFlag();
            }
        });

        addReaction(new Reaction("W-W") { // decrement flags
            @Override
            public int bid(Gesture g) {
                int y = g.vs.yM(), x1 = g.vs.xL(), x2 = g.vs.xH();
                int xS = Stem.this.heads.get(0).time.x; // get the first head on the stem, and get the x of it
                if (x1 > xS || x2 < xS){return UC.noBid;} //gesture not cross
                int y1 = Stem.this.yLo(), y2 = Stem.this.yHi();
                if (y < y1 || y > y2){return UC.noBid;} //gesture need to cross the heads
                return Math.abs(y-(y1+y2)/2);
            }

            @Override
            public void act(Gesture g) { //decrement flag
                Stem.this.decFlag();
            }
        });
    }


    //factory method
    public static Stem getStem(Staff staff, Time time, int y1, int y2, boolean up){
        Head.List heads = new Head.List();
        for(Head h : time.heads){
            int yH = h.y();
            if(yH > y1 && yH < y2){heads.add(h);} // add the head to the heads list
        }

        if(heads.size() == 0){return null;} //no stem is created if there is no heads
        Beam b = internalStem(staff.sys, time.x, y1, y2); //for the internal stems. 两端的stem中间的其他stems
        Stem res = new Stem(staff, heads, up);
        if(b!=null){b.addStem(res); res.nFlag = 1;}
        return res;
    }

    //returns non-null if find a beam crossed by a line
    private static Beam internalStem(Sys sys, int x, int y1, int y2) {
        for(Stem s: sys.stems){

            // if there is a beam
            if(s.beam != null && s.x() < x && s.yLo() < y2 && s.yHi() > y1){
                //beginning and ending xY for the stem. where the beam is
                int bX = s.beam.first().x(), bY = s.beam.first().yBeamEnd();
                int eX = s.beam.last().x(), eY = s.beam.last().yBeamEnd();
                //whther cross the line
                if(Beam.verticalLineCrossesSegment(x, y1, y2, bX, bY, eX, eY)){
                    return s.beam; //return the beam being crossed
                }
            }
        }
        return null; //didnt find it
    }

    public void show(Graphics g){

        if (nFlag >= -1 && heads.size() > 0){
            // heads.size() > 0 will guards
            int x = x(), h = staff.H(), yH = yFirstHead(), yB = yBeamEnd();
            //yH: y value for the first head on the stem
            //yB: 2 stems can have a beam
            g.drawLine(x, yH, x, yB);

            if (nFlag > 0 && beam == null){
                if (nFlag == 1){(isUp ? Glyph.FLAG1D : Glyph.FLAG1U).showAt(g, h, x, yB);}
                if (nFlag == 2){(isUp ? Glyph.FLAG2D : Glyph.FLAG2U).showAt(g, h, x, yB);}
                if (nFlag == 3){(isUp ? Glyph.FLAG3D : Glyph.FLAG3U).showAt(g, h, x, yB);}
                if (nFlag == 4){(isUp ? Glyph.FLAG4D : Glyph.FLAG4U).showAt(g, h, x, yB);}
            }
        }
    }

    public Head firstHead(){return heads.get(isUp ? heads.size()-1 : 0);}
    // if isUP is TRUE: the last one on the list
    // if isUP is False: or get the first one on the list

    public Head lastHead(){return heads.get(isUp ? 0 : heads.size()-1);}

    public int yFirstHead(){Head h = firstHead(); return h.staff.yLine(h.line);}

    public boolean isInternalStem(){
        return beam != null && beam.stems != null && this != beam.first() && this != beam.last();
    }

    public int yBeamEnd(){
        if(isInternalStem()){beam.setMasterBeam(); return Beam.yOfX(x());}

        Head h = lastHead(); //
        int line = h.line;
        line += isUp? -7 : 7; //octive above or down

        //increase more if more flags has been added
        //if flag is more than 2, give room for the extra flag(s)
        int flagInc = nFlag > 2 ? 2*(nFlag-2) : 0;
        line += isUp? -flagInc : flagInc;

        //Head toward center
        if ((isUp && line > 4) || (!isUp && line < 4)){line = 4;}
        //go up but not yet reach the mid line, or go down but not ...
        return h.staff.yLine(line);
    }


    public int x(){
        Head h = firstHead();
        // if the head is up, put the head to the right, so add the width of head
        return h.time.x + (isUp? h.W() : 0);
    }

    public int yLo(){return isUp ? yBeamEnd() : yFirstHead();}
    public int yHi(){return isUp ? yFirstHead() : yBeamEnd();}

    public void deleteStem() {
        staff.sys.stems.remove(this);
        deleteMass();
    }

    public void setWrongSides() { //stub
        Collections.sort(heads);
        int i, last, next;
        if (isUp){
            i = heads.size() - 1;
            last = 0;
            next = -1;
        }else{
            i = 0;
            last = heads.size() - 1;
            next = 1;
        }

        Head ph = heads.get(i);
        ph.wrongSide = false;
        while (i != last){
            // actually set wrong sides
            i += next;
            Head nh = heads.get(i);
//            nh.wrongSide = (Math.abs(nh.line - ph.line) <= 1 && !ph.wrongSide);
            nh.wrongSide = ph.staff == nh.staff && (Math.abs(nh.line - ph.line) <= 1 && !ph.wrongSide);
            ph = nh;
        }
    }

    @Override
    public int compareTo(Stem s) {//comparing to another stem, difference in x value
        return x() - s.x();
    }

    //--------------------------list-----------------------
    public static class List extends ArrayList<Stem> {

        //fast reject to determine the intersections, what stems are not to be looked at
        //先预设一个最大值和最小值
        public int yMin = 1_000_000, yMax = -1_000_000;
        public void addStem(Stem s){
            add(s);
            //track the min value of stems in the list and max value...
            if (s.yLo() < yMin){yMin = s.yLo();}
            if (s.yHi() > yMax){yMax = s.yHi();}
        }

        // if the y value is out of the y range of the stem list, reject to bid
//        public boolean fastReject (int y){return y < yMin || y > yMax;}

        public boolean fastReject (int y1, int y2){return false;} //y2 < yMin || y1 > yMax;}

        public void sort(){Collections.sort(this);}

        public Stem.List allIntersectors(int x1, int y1, int x2, int y2){
            Stem.List res = new Stem.List();
            for(Stem s: this){
                int x = s.x(), y = Beam.yOfX(x, x1, y1, x2, y2);
                if(x>x1 && x<x2 && y>s.yLo() && y<s.yHi()){res.add(s);}
            }
            return res;
        }
    }
}
