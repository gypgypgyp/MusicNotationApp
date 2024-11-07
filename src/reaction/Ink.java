package reaction;

import graphicsLib.G;
import graphicsLib.I;
import graphicsLib.UC;


import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class Ink implements I.Show, Serializable {
//    public static G.VS TEMP = new G.VS(100,100,100,100); // for debugging
    public static Buffer BUFFER = new Buffer();
//    public static final int K = UC.normSampleSize;  //how many points we want in the subsampling
    public Norm norm;
    public G.VS vs;
    public Ink (){
//        super(K); //画出buffer
//        BUFFER.subSample(this);
//        G.V.T.set(BUFFER.bBox, TEMP); //single transformer
//        this.transform();
        norm = new Norm();
        vs = BUFFER.bBox.getNewVS();
    }
    @Override
    public void show(Graphics g) {
//        g.setColor(Color.RED);
//        g.fillRect(100, 100, 100, 100);
//        draw(g);
        g.setColor(UC.inkColor);
        norm.drawAt(g, vs);
    }


    //_________________________________________________LIST________________________________________________

    public static class List extends ArrayList<Ink> implements I.Show, Serializable {

        @Override
        //this: parameter that does not show up in the func(). object. "this" here is a list.
        public void show(Graphics g) {
            for (Ink ink : this) {ink.show(g);}


        }
    }
    //_________________________________________________BUFFER________________________________________________

    //hold something for a while before
    public static class Buffer extends G.PL implements I.Show, I.Area{
        public static final int MAX = UC.intBufferMax;
        public int n; // how many points are in the buffer
        public G.BBox bBox= new G.BBox(); // the actual bounding box
        private Buffer(){super(MAX);}//super is what you extends. in this case is G.PL. use private here不让其他的改变Buffer
        public void clear(){n = 0;} //clear the buffer  //加上int的话n就是private var
        public void add(int x, int y){
            if(n < MAX){
                points[n++].set(x, y);
                bBox.add(x,y);
            } //add new points into the buffer. prefixing, postfixing, updating the n one by one.
        }
        // pass in a result, have 25 .
        public void subSample(G.PL res){
            int K = res.size(); //number of points
            //for loop to add points into PL 25 points. linear function
            for(int i = 0; i < K; i++){
                res.points[i].set(points[i*(n-1)/(K-1)]); //确保起点和终点位置不变
            }
        }

        @Override
        public boolean hit(int x, int y) {return true;}

        @Override
        public void dn(int x, int y) {clear(); add(x,y); bBox.set(x,y);} // put the first point into the buffer

        @Override
        public void up(int x, int y) {}

        @Override
        public void drag(int x, int y) {add(x,y);} //add to the buffer

        @Override
        //show the actual buffer
        public void show(Graphics g) {
            drawN(g, n);
            // bBox.draw(g);
        } //PL

    }
    //----------------------Norm------------------------------
    public static class Norm extends G.PL implements Serializable {
        public static final int N = UC.normSampleSize, MAX = UC.normCoordMax;
        public static final G.VS NCS = new G.VS(0,0, MAX, MAX); //new coordinate system. coordinate box for transform
        public Norm() {
            super(N);
            BUFFER.subSample(this);
            G.V.T.set(BUFFER.bBox, NCS);
            this.transform();
        }

        public void drawAt(Graphics g, G.VS vs){
            G.V.T.set(NCS,vs);
            for(int i = 1; i < N; i++){
                g.drawLine(points[i-1].tx(), points[i-1].ty(), points[i].tx(), points[i].ty());
            }
        }

        public int dist(Norm norm){
            int res = 0;
            for(int i = 0; i < N; i++){
                int dx = points[i].x - norm.points[i].x, dy = points[i].y - norm.points[i].y;
                res += dx * dx + dy * dy;
            }
            return res;
        }

        public void blend(Norm norm, int nBlend){
            for(int i = 0; i < N; i++){
                points[i].blend(norm.points[i], nBlend);
            }
        }

    }
}
