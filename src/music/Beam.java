package music;

import reaction.Mass;

import java.awt.*;

public class Beam extends Mass {
    public static Polygon poly;
    static {int[] foo = {0,0,0,0}; poly = new Polygon(foo, foo, 4);}
    public Stem.List stems = new Stem.List();

    public Beam(Stem f, Stem l) { //first stem and last stem
        super("NOTE");
//        stems.addStem(f);
//        stems.addStem(l);
        f.nFlag = 1;
        l.nFlag = 1;
        addStem(f);
        addStem(l);
    }

    public Stem first(){return stems.get(0);}
    public Stem last(){return stems.get(stems.size()-1);}
    public void deleteBeam(){
        for (Stem s: stems) {s.beam = null;}
        deleteMass();
    }
    public void addStem(Stem s){
        if (s.beam == null){stems.add(s); s.beam = this; stems.sort();}
    }

    @Override
    public void show(Graphics g) {
        g.setColor(Color.BLACK);
        drawBeamGroup(g);
    }

    public void drawBeamGroup(Graphics g){
        setMasterBeam();
        Stem firstStem = first();
        int H = firstStem.staff.H();
        int sH = firstStem.isUp ? H: -H; //signed H. stacks going up or down
        int nPrev = 0, nCur = firstStem.nFlag, nNext = stems.get(1).nFlag; //counts on the 3 stems of their nFlag

        int pX; //x of the prev stem
        int cX = firstStem.x(); //x of the cur stem
        int bX = cX + 3*H; // first beamlet leans forward cX to bX
        if (nCur > nNext) {drawBeamStack(g, nNext, nCur, cX, bX, sH);}
        for(int cur = 1; cur < stems.size(); cur ++){
            Stem sCur = stems.get(cur); //current stem
            pX = cX;
            cX = sCur.x();
            nPrev = nCur;
            nCur = nNext;
            nNext = (cur < (stems.size() -1)) ? stems.get(cur+1).nFlag : 0; //below the last stem
            int nBack = Math.min(nPrev, nCur); //take the smallest number of flags, to draw backward the beams
            drawBeamStack(g, 0, nBack, pX, cX, sH);

            // 确认哪一侧的flag更多
            if(nCur > nPrev && nCur > nNext){
                if(nPrev < nNext){
                    bX = cX + 3*H;
                    drawBeamStack(g, nNext, nCur, cX, bX, sH);
                }else{
                    bX = cX - 3*H;
                    drawBeamStack(g, nPrev, nCur, bX, cX, sH);
                }
            }
        }

    }

    public void setMasterBeam(){
        mX1 = first().x();  //master beam x 1
        mY1 = first().yBeamEnd();
        mX2 = last().x();
        mY2 = last().yBeamEnd();
    }

    public static void setPoly(int x1, int y1, int x2, int y2, int h){
        int[] a = poly.xpoints;
        a[0] = x1; a[1] = x2; a[2] = x2; a[3] = x1;
        a = poly.ypoints;
        a[0] = y1; a[1] = y2; a[2] = y2+h; a[3] = y1+h;
    }

    public static void drawBeamStack(Graphics g, int n1, int n2, int x1, int x2, int h){
        int y1 = yOfX(x1), y2 = yOfX(x2);
        for (int i = n1 ; i < n2 ; i++){
            setPoly(x1, y1 + i*2*h, x2, y2 + i*2*h, h);
            g.fillPolygon(poly);
        }
    }

    // ----math functions--------
    public static int yOfX(int x, int x1, int y1, int x2, int y2){
        int dy = y2-y1, dx = x2-x1;

        if(dx == 0){System.out.println("x: " + x + " dy:" + dy) ;}

        return (x-x1)*dy/dx + y1;
    }

    public static int mX1, mY1, mX2, mY2; //coordinates for master beams
    //buffer for the coordinate. create a buffer, and make sure
    // functional programing: lock out variables. for distrubuted systems , decrease

    public static int yOfX(int x){
        return yOfX(x, mX1, mY1, mX2, mY2);
    }

    public static void setMasterBeam(int x1, int y1, int x2, int y2){
        mX1 = x1;
        mY1 = y1;
        mX2 = x2;
        mY2 = y2;
    }

    public static boolean verticalLineCrossesSegment(int x, int y1, int y2, int bX, int bY, int eX, int eY){
        if(x < bX || x > eX){return false;}
        int y = yOfX(x, bX, bY, eX, eY);
        if(y1 < y2){return y1<y && y<y2;} else {return y2<y && y<y1;}
    }
}
