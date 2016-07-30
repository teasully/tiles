uniform mat4 uMVPMatrix;
uniform mat4 uMVMatrix;
attribute vec4 vPosition;
uniform vec4 vColor;
attribute vec3 vNormal;
varying vec3 aPosition;

void main(){
	aPosition = vec3(uMVMatrix * vPosition);

	gl_Position = uMVPMatrix * vPosition;
}