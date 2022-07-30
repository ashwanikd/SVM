package SupportVectorMachine;

import Data.Table;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author ashwani kumar dwivedi
 * @version 1.0
 */

public class ScatterPlot extends Frame implements GLEventListener {

    GLProfile profile;
    GLCanvas canvas;
    GLCapabilities capabilities;
    FPSAnimator animator;
    GL2 gl;
    Table table;
    double tx,ty,sx,sy,w1,w2,b,minx,maxx;
    double edging=1.2;

    public ScatterPlot(Table table,double w1,double w2,double b){
        this.table = table;
        setTransformationValues();
        this.w1=w1;
        this.w2=w2;
        this.b=b;
        profile = GLProfile.get(GLProfile.GL2);
        capabilities = new GLCapabilities(profile);
        canvas = new GLCanvas(capabilities);
        canvas.addGLEventListener(this);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                animator.stop();
                System.exit(0);
            }
        });
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        animator = new FPSAnimator(canvas,300,true);

        this.setSize(500,500);
        this.setVisible(true);
        this.add(canvas);
    }

    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {
    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        gl = glAutoDrawable.getGL().getGL2();
        //gl.glClear(gl.GL_DEPTH_BUFFER_BIT|gl.GL_COLOR_BUFFER_BIT);
        gl.glLoadIdentity();
        gl.glPointSize(5);
        gl.glBegin(GL2.GL_POINTS);
        gl.glColor3d(1,1,1);

        gl.glBegin(GL2.GL_POINTS);
        for(int i=0;i<table.records.size();i++){
            if(1 == (double)table.records.get(i).data[table.NumberOfColumns-1].data){
                gl.glColor3d(0,1,0);
            }else {
                gl.glColor3d(1,0,0);
            }
            gl.glVertex3d((((double)table.records.get(i).data[0].data)-tx)/sx,((double)table.records.get(i).data[1].data-ty)/sy,0);
        }
        gl.glEnd();

        gl.glBegin(GL2.GL_LINES);
        gl.glColor3d(1,1,0);
        gl.glVertex3d((minx-tx)/sx,(((b-(w1*minx))/w2)-ty)/sy,0);
        gl.glVertex3d((maxx-tx)/sx,(((b-(w1*maxx))/w2)-ty)/sy,0);
        gl.glEnd();

        gl.glFlush();
    }

    void setTransformationValues(){
        double minx=Double.POSITIVE_INFINITY,maxx=Double.NEGATIVE_INFINITY,miny=Double.POSITIVE_INFINITY,maxy=Double.NEGATIVE_INFINITY;
        for(int i=0;i<table.records.size();i++){
            if(minx > (double)table.records.get(i).data[0].data){
                minx = (double)table.records.get(i).data[0].data;
            }
            if(maxx < (double)table.records.get(i).data[0].data){
                maxx = (double)table.records.get(i).data[0].data;
            }
            if(miny > (double)table.records.get(i).data[1].data){
                miny = (double)table.records.get(i).data[1].data;
            }
            if(maxy < (double)table.records.get(i).data[1].data){
                maxy = (double)table.records.get(i).data[1].data;
            }
            tx = (minx+maxx)*edging/2.0;
            ty = (miny+maxy)*edging/2.0;
            sx = (maxx-minx)*edging/2.0;
            sy = (maxy-miny)*edging/2.0;
            this.minx = minx;
            this.maxx = maxx;
        }
    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {

    }
}
