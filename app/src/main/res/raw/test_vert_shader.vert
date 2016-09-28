uniform mat4 uMVPMatrix;
uniform mat4 uMVMatrix;
attribute vec4 vPosition;
uniform vec4 vColor;
attribute vec3 vNormal;
varying vec3 aPosition;
varying vec3 aNormal;

uniform float elapsedTime;
varying float passedTime;

float rand(vec2 co){
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

void main(){
	aPosition = vec3(uMVMatrix * vPosition);
	aNormal = vec3(uMVMatrix * vec4(vNormal, 0.0));

    passedTime = elapsedTime;
    float timeDistort1 = sin((elapsedTime / 2.0)) + 0.5;
    float timeDistort2 = cos((elapsedTime / 4.0)) + 0.5;

	vec3 custom = vec3(timeDistort1, 0.0, 0.0);

    vec4 pos = vec4(uMVPMatrix * vPosition);
	gl_Position = vec4(pos.x + custom.x, pos.y + custom.y, pos.z + custom.z, pos.w);
}