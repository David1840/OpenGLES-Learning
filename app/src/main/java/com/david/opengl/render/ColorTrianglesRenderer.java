package com.david.opengl.render;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static com.david.opengl.util.RenderUtil.compileShader;
import static com.david.opengl.util.RenderUtil.linkProgram;

/**
 * @Author: liuwei
 * @Create: 2019/6/26 16:47
 * @Description: 绘制一个彩色的三角形
 */
public class ColorTrianglesRenderer implements GLSurfaceView.Renderer {
    private float[] vertexPoints = new float[]{
            0.0f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f
    };

    private float color[] = {
            0.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f
    };


    /**
     * 顶点着色器
     */
    private String vertextShader =
            "#version 300 es \n"
                    + "layout (location = 0) in vec4 vPosition;\n"
                    + "layout (location = 1) in vec4 aColor;\n"
                    + "out vec4 vColor;\n"
                    + "void main() { \n"
                    + "gl_Position  = vPosition;\n"
                    + "gl_PointSize = 10.0;\n"
                    + "vColor = aColor;\n"
                    + "}\n";

    private String fragmentShader =
            "#version 300 es \n"
                    + "precision mediump float;\n"
                    + "in vec4 vColor;\n"
                    + "out vec4 fragColor;\n"
                    + "void main() { \n"
                    + "fragColor = vColor; \n"
                    + "}\n";

    private final FloatBuffer vertexBuffer;
    private final FloatBuffer colorBuffer;

    public ColorTrianglesRenderer(){
        //分配内存空间,每个浮点型占4字节空间
        vertexBuffer = ByteBuffer.allocateDirect(vertexPoints.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        vertexBuffer.put(vertexPoints);
        vertexBuffer.position(0);

        colorBuffer = ByteBuffer.allocateDirect(color.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的数据
        colorBuffer.put(color);
        colorBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES30.glClearColor(0f, 0f, 0f, 0f);

        final int vertexShaderId = compileShader(GLES30.GL_VERTEX_SHADER, vertextShader);
        final int fragmentShaderId = compileShader(GLES30.GL_FRAGMENT_SHADER, fragmentShader);
        //在OpenGLES环境中使用程序片段
        GLES30.glUseProgram(linkProgram(vertexShaderId, fragmentShaderId));
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

        //准备坐标数据
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer);
        //启用顶点的句柄
        GLES30.glEnableVertexAttribArray(0);

        //绘制三角形颜色
        GLES30.glEnableVertexAttribArray(1);
        GLES30.glVertexAttribPointer(1, 4, GLES30.GL_FLOAT, false, 0, colorBuffer);
        //绘制三角形
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3);

        //禁止顶点数组的句柄
        GLES30.glDisableVertexAttribArray(0);
        GLES30.glDisableVertexAttribArray(1);
    }
}
