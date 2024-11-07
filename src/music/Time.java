package music;
import graphicsLib.UC;

import java.util.ArrayList;

public class Time {
    public int x;
    public Head.List heads= new Head.List();
    public Time(Sys sys, int x){
        this.x = x;
        sys.times.add(this); //
    }

    public void unStemHeads(int y1, int y2){
        for(Head h: heads){
            int y = h.y();
            if(y > y1 && y < y2){h.unStem();}
        }
    }

//    public void stemHeads(Staff staff, boolean up, int y1, int y2){
//        Stem s = new Stem(staff, up);
//        System.out.println("stemHeads: size = " + heads.size());
//        for (Head h : heads){
//            int y = h.y();
//            System.out.println("y: " + y + " y1: " + y1 + " y2: " + y2);
//            if (y > y1 && y < y2){h.joinStem(s);}
//        }
//        if (s.heads.size() == 0){
//            System.out.println("WTF---empty head list after steming");
//        }else{
//            s.setWrongSides();
//        }
//    }

    //-------------------List--------------------
    public static class List extends ArrayList<Time> {
        public Sys sys; // belong to a sys
        public List(Sys sys){this.sys = sys;}
        public Time getTime(int x){
            if (size() == 0) {return new Time(sys, x);} // when no one is on the lise
            Time t = getClosestTime(x);
            return (Math.abs(x-t.x) < UC.snapTime) ? t : new Time(sys, x); //if real close, then return x
        }

        private Time getClosestTime(int x) {
            Time res = get(0);
            int bestSoFar = x - res.x; // how close the cur result is to x
            for(Time t: this) {
                int dist = Math.abs(x - t.x);
                if (dist < bestSoFar) {
                    res = t;
                    bestSoFar = dist;
                }
            }
            return res;
        }

    }
}
