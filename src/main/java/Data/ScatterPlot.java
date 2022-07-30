package Data;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author ashwani kumar dwivedi
 * @version 1.0
 */

public class ScatterPlot extends Frame implements GLEventListener {

    /**
     * related to graphics
     */
    GLProfile profile;
    GLCanvas canvas;
    GLCapabilities capabilities;
    FPSAnimator animator;
    GL2 gl;
    Table table;
    /**
     * transformation variables
     * @variable tx,ty "translation in x and y direction"
     * @variable sx,sy "scaling in x and y direction"
     */
    double tx,ty,sx,sy;
    /**
     * for edging in x and y direction
     */
    double edging=1.2;

    /**
     * constructor
     * @param table data to be plotted
     */
    public ScatterPlot(Table table){
        this.table = table;
        setTransformationValues();

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

    /**
     * drawing graphics
     * @param glAutoDrawable drawable object
     */
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
            gl.glVertex3d((((double)table.records.get(i).data[0].data)-tx)/sx,((double)table.records.get(i).data[1].data-ty)/sy,0);
        }
        gl.glEnd();

        gl.glFlush();
    }

    /**
     * for setting transformation values
     * tx,ty,sx,sy for further drawing
     */
    void setTransformationValues(){
        double minx=Double.POSITIVE_INFINITY,maxx=0,miny=Double.POSITIVE_INFINITY,maxy=0;
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
        }
    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {

    }
}
