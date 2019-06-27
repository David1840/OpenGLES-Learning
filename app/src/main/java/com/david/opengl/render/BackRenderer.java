package com.david.opengl.render;

import android.graphics.Color;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @Author: liuwei
 * @Create: 2019/6/25 17:28
 * @Description:
 */
public class BackRenderer implements GLSurfaceView.Renderer {

    private int color;

    public BackRenderer(int color) {
        this.color = color;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //设置背景颜色
        float redF = (float) Color.red(color) / 255;
        float greenF = (float) Color.green(color) / 255;
        float blueF = (float) Color.blue(color) / 255;
        float alphaF = (float) Color.alpha(color) / 255;
        GLES30.glClearColor(redF, greenF, blueF, alphaF);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //设置视图窗口
        GLES30.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //把颜色缓冲区设置为我们预设的颜色
        GLES30.glClear(GL10.GL_COLOR_BUFFER_BIT);
    }
}

