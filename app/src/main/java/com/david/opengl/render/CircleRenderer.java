package com.david.opengl.render;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static com.david.opengl.util.RenderUtil.compileShader;
import static com.david.opengl.util.RenderUtil.linkProgram;

/**
 * @Author: liuwei
 * @Create: 2019/6/25 17:31
 * @Description:
 */
public class CircleRenderer implements GLSurfaceView.Renderer {
    private final FloatBuffer vertexBuffer;
    private int uMatrixLocation;
    private final float[] mMatrix = new float[16];

    /**
     * 顶点着色器
     */
    private String vertextShader =
            "#version 300 es\n" +
                    "layout (location = 0) in vec4 vPosition;\n" +
                    "uniform mat4 u_Matrix;\n" +
                    "void main() {\n" +
                    "     gl_Position  = u_Matrix * vPosition;\n" +
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


    public CircleRenderer() {
        //分配内存空间,每个浮点型占4字节空间
        vertexBuffer = ByteBuffer.allocateDirect(createPositions().length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        vertexBuffer.put(createPositions());
        vertexBuffer.position(0);
    }


    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES30.glClearColor(0f, 0f, 0f, 0f);

        final int vertexShaderId = compileShader(GLES30.GL_VERTEX_SHADER, vertextShader);
        final int fragmentShaderId = compileShader(GLES30.GL_FRAGMENT_SHADER, fragmentShader);
        //在OpenGLES环境中使用程序片段
        int mProgram = linkProgram(vertexShaderId, fragmentShaderId);
        GLES30.glUseProgram(mProgram);

        uMatrixLocation = GLES30.glGetUniformLocation(mProgram, "u_Matrix");
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
        float aspectRatio = width > height ? (float) width / (float) height : (float) height / (float) width;
        if (width > height) {
            Matrix.orthoM(mMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, 0f, 10f);
        } else {
            Matrix.orthoM(mMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, 0f, 10f);
        }
    }

    @Override

    public void onDrawFrame(GL10 gl10) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

        //准备坐标数据
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer);
//        //启用顶点的句柄
        GLES30.glEnableVertexAttribArray(0);
        GLES30.glUniformMatrix4fv(uMatrixLocation, 1, false, mMatrix, 0);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, 362);

        //禁止顶点数组的句柄
        GLES30.glDisableVertexAttribArray(0);
    }


    private float[] createPositions() {
        // 绘制的半径
        float radius = 0.8f;
        ArrayList<Float> data = new ArrayList<>();
        data.add(0.0f);             //设置圆心坐标
        data.add(0.0f);
        data.add(0.0f);
        float angDegSpan = 360f / 360;
        for (float i = 0; i < 360 + angDegSpan; i += angDegSpan) {
            data.add((float) (radius * Math.sin(i * Math.PI / 180f)));
            data.add((float) (radius * Math.cos(i * Math.PI / 180f)));
            data.add(0.0f);
        }
        float[] f = new float[data.size()];
        for (int i = 0; i < f.length; i++) {
            f[i] = data.get(i);
        }
        return f;
    }
}
