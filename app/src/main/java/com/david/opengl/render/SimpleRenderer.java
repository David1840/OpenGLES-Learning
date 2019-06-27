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
 * @Create: 2019/6/25 17:31
 * @Description:
 */
public class SimpleRenderer implements GLSurfaceView.Renderer {
//    private float[] vertexPoints = new float[]{
//            0.0f, 0.5f, 0.0f,
//            -0.5f, -0.5f, 0.0f,
//            0.5f, -0.5f, 0.0f
//    };

    float[] vertexPoints = {
            // 第一个点就是三角形扇的中心点
            0f, 0f,
            -0.5f, -0.8f,
            0.5f, -0.8f,
            0.5f, 0.8f,
            -0.5f, 0.8f,
            -0.5f, -0.8f
            // 重复第二个点，使三角形扇闭合
    };
    private final FloatBuffer vertexBuffer;
    /**
     * 顶点着色器
     */
    private String vertextShader =
            "#version 300 es\n" +
                    "layout (location = 0) in vec4 vPosition;\n" +
                    "void main() {\n" +
                    "     gl_Position  = vPosition;\n" +
                    "     gl_PointSize = 10.0;\n" +
                    "}\n";

    /**
     * 片段着色器
     */
    private String fragmentShader =
            "#version 300 es\n" +
                    "precision mediump float;\n" +
                    "out vec4 fragColor;\n" +
                    "void main() {\n" +
                    "     fragColor = vec4(1.0,1.0,1.0,1.0);\n" +
                    "}\n";


    public SimpleRenderer() {
        //分配内存空间,每个浮点型占4字节空间
        vertexBuffer = ByteBuffer.allocateDirect(vertexPoints.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        vertexBuffer.put(vertexPoints);
        vertexBuffer.position(0);
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
        //绘制三个点
//        GLES30.glDrawArrays(GLES30.GL_POINTS, 0, 3);

        //绘制直线
//        GLES30.glDrawArrays(GLES30.GL_LINE_STRIP, 0, 2);
//        GLES30.glLineWidth(10);

        //绘制三角形
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, 6);

        //禁止顶点数组的句柄
        GLES30.glDisableVertexAttribArray(0);
    }
}
