package com.steemit.dchestra;

import android.content.Context;
import android.opengl.GLES10;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class PostView extends GLSurfaceView {

    private float w, h;
    private int screenW, screenH;

    public float tx, ty;

    private FloatBuffer vertBuffer;

    public PostView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setEGLConfigChooser(8, 8, 8, 8, 0, 0);
        setRenderer(new PostRenderer());

        float verts[] = {
                -1f, -1f, 0f,
                1f, -1f, 0f,
                -1f, 1f, 0f,
                1f, 1f, 0f,
        };

        ByteBuffer bb = ByteBuffer.allocateDirect(verts.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertBuffer = bb.asFloatBuffer();
        vertBuffer.put(verts);
        vertBuffer.position(0);
/*
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tx = event.getX();
                ty = event.getY();
                return false;
            }
        });*/
    }

    private class PostRenderer implements GLSurfaceView.Renderer {

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            // Transparency
            gl.glEnable(GL10.GL_ALPHA_TEST);
            gl.glEnable(GL10.GL_BLEND);
            gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);

            // 2D so no need for depth I guess
            gl.glDisable(GL10.GL_DEPTH_TEST);

            // Just the way we give it the cube
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            gl.glClearColor(0.5f, 0f, 0.5f, 1f);

            h = height;
            w = width;

            screenH = height;
            screenW = width;

            gl.glViewport(0, 0, screenW, screenH);

            gl.glMatrixMode(GL10.GL_PROJECTION);
            gl.glLoadIdentity();
            gl.glOrthof(0, w, h, 0, -1, 1);
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadIdentity();
        }

        private float rot = 0;

        @Override
        public void onDrawFrame(GL10 gl) {
            gl.glPushMatrix();

            if(tx == 0)
                tx = w / 2;
            if(ty == 0)
                ty = h / 2;

            gl.glClear(GLES10.GL_COLOR_BUFFER_BIT);
            gl.glTranslatef(tx, ty, 0);
            gl.glScalef(200, 200, 0);
            gl.glRotatef(45, 0, 0, 1);
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertBuffer);
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP,0 , 4);

            gl.glPopMatrix();
        }
    }

}
