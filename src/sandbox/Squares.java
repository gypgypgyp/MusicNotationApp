package sandbox;

import graphicsLib.G;
import graphicsLib.G.VS;

import graphicsLib.I;
import graphicsLib.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Squares extends Window implements ActionListener {
    //action list . time clickes, call to do action function.

//    public static G.VS vs = new G.VS(100, 100, 200, 300);
//    public static Color color = G.rndColor();
    public static I.Area curArea;
    public static Square.List list = new Square.List();
    public static Square BACKGROUND = new Square(0,0) {
        public void dn(int x, int y) {square = new Square(x, y);list.add(square);}
            public void drag(int x, int y){
                square.resize(x, y);

        };

          //functions in the {}static
    };
    static {
        BACKGROUND.size.set(3000,3000);
        BACKGROUND.c = Color.white;
        list.add(BACKGROUND);
    }
    public static Square square; //variable name square
    public boolean dragging = false; //not clicking anything????
    public static G.V mouseDelta = new G.V(0,0); //计算点击的地方和方块左上角的距离. overwritten in mouse Press
    public static Timer timer;  //java swing timer
    public static G.V pressedLoc = new G.V(0,0);

    public Squares() {
        super("squares", 1000, 700);
        timer = new Timer(30, this);//this: is getting call when you get a square object
        timer.setInitialDelay(5000); //5000 milisecond , 5 second. before clicking the timer .
        //timer.start(); //stop animation
    }//constructor for square


    public void paintComponent(Graphics g) {
        G.fillBack(g);
//        vs.fill(g, color);
        list.draw(g);
    }

    public void mousePressed(MouseEvent me) {
        int x = me.getX(), y = me.getY(); //capture. create local valuables
//        if (vs.hit(x, y)) {
//            color = G.rndColor();
//        }
        curArea = list.hit(x,y); //defined as an area, not a square. list is a ligimitive
        curArea.dn(x,y);
        //square = list.hit(x,y); //  //capture the new square
//        if (square == null){
//            dragging = false;
//            square = new Square(x,y);
//            list.add(square); //add the new square
//        }else{
//            dragging = true;
//            square.dv.set(0,0); // stop the square movement . try to freeze the square
//            pressedLoc.set(x,y);
//            mouseDelta.set(square.loc.x-x, square.loc.y-y);
//        } // test . if square is null, means we are not dragging.

        repaint();
    }

    public void mouseDragged(MouseEvent me) {
        int x=me.getX(), y=me.getY(); //declaring 2 variables, declaration. data type 后面可以加多个var.
//        if(dragging){square.move(x+mouseDelta.x,y+mouseDelta.y);}else{square.resize(me.getX(), me.getY());}//take the square
        curArea.drag(x, y);
        repaint();
    }

    public void mouseReleased(MouseEvent me){
//        if (dragging){
//            square.dv.set(me.getX()-pressedLoc.x, me.getY()-pressedLoc.y);
//        }
        int x=me.getX(), y=me.getY();
        curArea.up(getX(),getY());
        repaint();
    }

    public static void main(String[] args) {
        PANEL = new Squares(); //
        Window.launch();//launch a window constantly
    }

    @Override
    public void actionPerformed(ActionEvent e) {repaint();} //everytime call performed, repaint

    //----------------------Square---------------------
    public static class Square extends G.VS implements I.Area{ //class. global. hit routine
        //G.VS has no contructor
        public Color c = G.rndColor(); //member. non-global variable
        public G.V dv = new G.V(0,0);
            //stop animation//new G.V(G.rnd(20)-10, G.rnd(20)-10); //how fast its moving. set a random number

        public Square(int x, int y) { //constructor. have the same name as the class
            super(x, y, 100, 100);
        }

        public void draw(Graphics g){fill(g, c); moveAndBounce();}

        public void resize(int x, int y) {
            if (x > loc.x && y > loc.y) { //when click out of the square?
                size.set(x - loc.x, y - loc.y);
            }
        }

        public void move(int x, int y) {
            loc.set(x, y);
        } //to move the square

        public void moveAndBounce(){ //让方块在固定区域内活动
            loc.add(dv); //location of square when x,y passed
            if (loc.x < 0 && dv.x < 0 ) {dv.x = -dv.x;} //pass the margin,
            if (loc.x > 1000 && dv.x > 0 ) {dv.x = -dv.x;}
            if (loc.y < 0 && dv.y < 0 ) {dv.y = -dv.y;}
            if (loc.y > 700 && dv.y > 0 ) {dv.y = -dv.y;}
        }

        @Override
        public void dn(int x, int y) {mouseDelta.set(loc.x-x, loc.y-y);}

        @Override
        public void up(int x, int y) {}

        @Override
        public void drag(int x, int y) {loc.set(mouseDelta.x+x, mouseDelta.y+y);}

        //------------------List-----------------------
        public static class List extends ArrayList<Square> {
            public void draw(Graphics g) {
                for (Square s : this) {
                    s.draw(g);
                }
            } //draw squares

            public Square hit(int x, int y) {//hit routine
                Square res = null; //the result we return. set with defult value null
                for (Square s : this) {
                    if (s.hit(x, y)) {
                        res = s;
                    }
                }//go through the list. list itterater with ':'
                return res;

            }


        }

    }
}
