package com.david.opengl.render;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.david.opengl.R;
import com.david.opengl.util.RenderUtil;
import com.david.opengl.util.TextureUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @Author: liuwei
 * @Create: 2019/9/18 15:05
 * @Description:
 */
public class TextureRender implements GLSurfaceView.Renderer {
    private static final String TAG = "TextureRenderer";

    private final FloatBuffer vertexBuffer, mTexVertexBuffer;

    private final ShortBuffer mVertexIndexBuffer;
    private int uMatrixLocation;

    private float[] mMatrix = new float[16];

    private int mProgram;

    private int textureId;

    private Context context;

    /**
     * 顶点坐标
     * (x,y,z)
     */
    private float[] POSITION_VERTEX = new float[]{
            0f, 0f, 0f,     //顶点坐标V0
            1f, 1f, 0f,     //顶点坐标V1
            -1f, 1f, 0f,    //顶点坐标V2
            -1f, -1f, 0f,   //顶点坐标V3
            1f, -1f, 0f     //顶点坐标V4
    };

    /**
     * 纹理坐标
     * (s,t)
     */
    private static final float[] TEX_VERTEX = {
            0.5f, 0.5f, //纹理坐标V0
            1f, 0f,     //纹理坐标V1
            0f, 0f,     //纹理坐标V2
            0f, 1.0f,   //纹理坐标V3
            1f, 1.0f    //纹理坐标V4
    };

    /**
     * 索引
     */
    private static final short[] VERTEX_INDEX = {
            0, 1, 2,  //V0,V1,V2 三个顶点组成一个三角形
            0, 2, 3,  //V0,V2,V3 三个顶点组成一个三角形
            0, 3, 4,  //V0,V3,V4 三个顶点组成一个三角形
            0, 4, 1   //V0,V4,V1 三个顶点组成一个三角形
    };


    /**
     * 顶点着色器
     */
    private String vertextShader =
            "#version 300 es\n" +
                    "layout (location = 0) in vec4 vPosition;\n" +
                    "layout (location = 1) in vec2 aTextureCoord;\n" +
                    "//矩阵\n" +
                    "uniform mat4 u_Matrix;\n"+
                    "//输出纹理坐标(s,t)\n" +
                    "out vec2 vTexCoord;\n" +
                    "void main() { \n" +
                    "     gl_Position  = u_Matrix * vPosition;\n" +
                    "     gl_PointSize = 10.0;\n" +
                    "     vTexCoord = aTextureCoord;\n" +
                    "}\n";

    /**
     * 片段着色器
     */
    private String fragmentShader =
            "#version 300 es\n" +
                    "precision mediump float;\n" +
                    "uniform sampler2D uTextureUnit;\n" +
                    "//接收刚才顶点着色器传入的纹理坐标(s,t)\n" +
                    "in vec2 vTexCoord;\n" +
                    "out vec4 vFragColor;\n" +
                    "void main() {\n" +
                    "     vFragColor = texture(uTextureUnit,vTexCoord);\n" +
                    "}\n";

    public TextureRender(Context context) {

        this.context = context;
        //分配内存空间,每个浮点型占4字节空间
        vertexBuffer = ByteBuffer.allocateDirect(POSITION_VERTEX.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        vertexBuffer.put(POSITION_VERTEX);
        vertexBuffer.position(0);

        mTexVertexBuffer = ByteBuffer.allocateDirect(TEX_VERTEX.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(TEX_VERTEX);
        mTexVertexBuffer.position(0);

        mVertexIndexBuffer = ByteBuffer.allocateDirect(VERTEX_INDEX.length * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
                .put(VERTEX_INDEX);
        mVertexIndexBuffer.position(0);
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //设置背景颜色
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
        //编译
        final int vertexShaderId = RenderUtil.compileShader(GLES30.GL_VERTEX_SHADER, vertextShader);
        final int fragmentShaderId = RenderUtil.compileShader(GLES30.GL_FRAGMENT_SHADER, fragmentShader);
        //链接程序片段
        mProgram = RenderUtil.linkProgram(vertexShaderId, fragmentShaderId);

        uMatrixLocation = GLES30.glGetUniformLocation(mProgram, "u_Matrix");
        //加载纹理
        textureId = TextureUtils.loadTexture(context, R.drawable.main);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);

        final float aspectRatio = width > height ?
                (float) width / (float) height :
                (float) height / (float) width;
        if (width > height) {
            //横屏
            Matrix.orthoM(mMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        } else {
            //竖屏
            Matrix.orthoM(mMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

        //使用程序片段
        GLES30.glUseProgram(mProgram);

        GLES30.glUniformMatrix4fv(uMatrixLocation, 1, false, mMatrix, 0);

        GLES30.glEnableVertexAttribArray(0);
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer);

        GLES30.glEnableVertexAttribArray(1);
        GLES30.glVertexAttribPointer(1, 2, GLES30.GL_FLOAT, false, 0, mTexVertexBuffer);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        //绑定纹理
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);

        // 绘制
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, VERTEX_INDEX.length, GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);
    }
}
