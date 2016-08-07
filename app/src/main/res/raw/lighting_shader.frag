#version 100
precision mediump float;
varying vec3 aPosition;
varying vec3 aNormal;

uniform vec3 mAmbient;
uniform vec3 mDiffuse;
uniform vec3 mSpecular;
//uniform float shininess;
uniform float opacity;

uniform float pX;
uniform float pY;
uniform float elapsedTime;

uniform vec3 sceneAmbience;

uniform int MAXLIGHTS;
uniform vec3 lightProperties[4 * 3];    // Position, Diffuse, and Spec for each light

float pow(float num, int power){
    float returnNum = 1.0;
    for(int i = 0; i < power; i++){
        returnNum = returnNum * num;
    }
    return returnNum;
}

void main(){

    //float distance = length(aPosition - lightProperties[0]);
    //float att = 1.0 / (1.0 + (0.25 * pow(distance, 2)));

    vec3 totalLight = mAmbient * sceneAmbience;// + (mDiffuse * (att * 0.5));

    float shininess = 0.05;

    //vec3 mSpecular = mAmbient;

    //float timeDistort = cos((1.0 * 3.459657 * elapsedTime))+0.5;

    for(int i = 0; i < 1; i++){//}MAXLIGHTS; i++){
        int offset = i * 3;
        vec3 uLightPosition = lightProperties[offset];
        float distance = length(aPosition - uLightPosition);
        float att = 4.0 / (1.0 + (0.25 * pow(distance, 2)));

        vec3 lDiffuse = lightProperties[offset + 1];
        vec3 surfaceToLight = normalize(uLightPosition - aPosition);
        vec3 norm = normalize(aNormal);
        float difContribution = max(0.0, dot(norm, surfaceToLight));
        vec3 diffuse = difContribution * mDiffuse * lDiffuse;// * vec3(aPosition.x, aPosition.z, aPosition.y * timeDistort);

        vec3 lSpecular = lightProperties[offset + 2];
        vec3 surfaceToView = normalize(-aPosition);
        vec3 reflection = reflect(-surfaceToLight, norm);

        float specContribution = pow(max(0.0, dot(surfaceToView, reflection)), shininess);
        vec3 specular = specContribution * mSpecular * lSpecular;
        totalLight = vec3(totalLight + (diffuse + specular)*att);// No specular..its weird
	}
	gl_FragColor = vec4(totalLight, opacity);
}