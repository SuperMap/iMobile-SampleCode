attribute vec4 aPosition;//Vertex Position
attribute vec4 aTexCoord;//S T texture coordinates
varying vec2 vTexCoord;
uniform mat4 uMatrix;
uniform mat4 uSTMatrix;



varying float vAlphaFactor;
uniform float uAlphaFactor;


void main() {

    vTexCoord = (uSTMatrix * aTexCoord).xy;
    gl_Position = uMatrix*aPosition;

    vAlphaFactor = uAlphaFactor;

}