#version 100
precision mediump float;
varying vec3 aPosition;
varying vec3 aNormal;

uniform vec3 mAmbient;
uniform vec3 mDiffuse;
uniform vec3 mSpecular;
uniform float opacity;

uniform float pX;
uniform float pY;
uniform float elapsedTime;

uniform vec3 sceneAmbience;

uniform int MAXLIGHTS;
uniform vec3 lightProperties[4 * 3];    // Position, Diffuse, and Spec for each light

void main(){

    vec3 totalLight = mAmbient * sceneAmbience * mDiffuse;

    //float shininess = .0;
    float timeDistort1 = sin((elapsedTime / 2.0)) + 3.5;
    float timeDistort2 = cos((elapsedTime / 4.0)) + 3.5;

    vec3 custom = vec3(aPosition.y + aPosition.x + timeDistort1 * timeDistort2 * 2.0, (aPosition.x - aPosition.z * timeDistort2) * 20.0, aPosition.x + timeDistort1 * 3.0);

	gl_FragColor = vec4(totalLight * custom / 2.5, opacity);
}