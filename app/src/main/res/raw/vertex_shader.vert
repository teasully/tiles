uniform mat4 uMVPMatrix;
uniform mat4 uMVMatrix;
attribute vec4 vPosition;
uniform vec4 vColor;
attribute vec3 vNormal;
varying vec3 aPosition;
varying vec3 aNormal;

void main(){
	aPosition = vec3(uMVMatrix * vPosition);
	aNormal = vec3(uMVMatrix * vec4(vNormal, 0.0));

	gl_Position = uMVPMatrix * vPosition;
}