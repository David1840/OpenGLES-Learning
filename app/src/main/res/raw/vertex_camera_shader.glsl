#version 300 es
layout (location = 0) in vec4 vPosition;
layout (location = 1) in vec4 aTextureCoord;
//纹理矩阵
uniform mat4 uTextureMatrix;
out vec2 yuvTexCoords;
void main() {
    gl_Position  = vPosition;
    gl_PointSize = 10.0;
    //只保留x和y分量
    yuvTexCoords = (uTextureMatrix * aTextureCoord).xy;
}