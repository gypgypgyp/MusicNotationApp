package sandbox;

import graphicsLib.G;
import graphicsLib.Window;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Paint extends Window { //def of the class
    public static int clicks = 0; //detect the mouse clicks??? remember how many times we click the mouse
    public static Path path = new Path(); //new is a constructor. defult constructor.

    public static Pic pic = new Pic();
    public Paint(){super("Paint", 1000, 700);} //constructor


    @Override //tell the java compiler to check the function names?
    public void paintComponent(Graphics g){ //this function is called by the operating system. g is from operating system. we dont know that is the graphics, graphics are input of users
        G.fillBack(g); //paint component; when resize the window -- repaint the color
        g.setColor(G.rndColor());
        g.drawRect(100, 100, 100, 100);
        g.fillOval(100, 100, 100, 100);
        g.setColor(Color.BLUE);
        g.drawLine(300, 200, 500, 100);
        g.setColor(Color.BLACK);
        String msg = "Hello World!"; int x = 150; int y = 400;
        g.drawString(msg + clicks, x, y); //the number only change when we resize the window
        g.setColor(Color.RED);

        //path.draw(g); // draw the path ,remember
        pic.draw(g); // draw the whole pic

        g.fillOval(x-1, y-1, 2, 2);

        FontMetrics fm = g.getFontMetrics();
        int a = fm.getAscent();
        int d = fm.getDescent();
        int w = fm.stringWidth(msg);
        g.drawRect(x, y - a, w, a + d); //draw box around text
    }

    @Override
    public void mousePressed(MouseEvent me) { //count of how many times clicks
        clicks++;
        //path.clear(); //不能直接删除clear
        path = new Path(); //having a new path, stored in the path
        pic.add(path); //add the path to the picture. pointer semestics
        path.add(me.getPoint()); //event of where the mouse clicks
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent me){
        path.add(me.getPoint());
        repaint();
    }

    public static void main(String[] args){
        PANEL = new Paint();
        Window.launch();
    }

    //-------------------------------------Path------------------------------

    //build a nested class
    public static class Path extends ArrayList<Point> { //generic class
        // Paint.Path //Path is nested in the Paint. class helper
        // ArrayList stats as an empty list. defult constructor for arraylist.
        public void draw(Graphics g) {
            for (int i = 1; i < size(); i++){
                Point p = get(i - 1), n = get(i);
                g.drawLine(p.x, p.y, n.x, n.y);
            }
        }
    }
    //-------------------------------------Pic------------------------------

    public static class Pic extends ArrayList<Path> {
        //need a routine to know how to
        public void draw(Graphics g) {for(Path p:this) {p.draw(g);}} //p is variable of Path

    }
}
