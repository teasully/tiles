#version 100
precision mediump float;
varying vec3 aPosition;

uniform vec3 mAmbient;
uniform vec3 mDiffuse;
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

    //float shininess = .0;

   // float timeDistort = cos((1.0 * 3.459657 * elapsedTime))+0.5;
    vec3 custom = vec3(aPosition.y, aPosition.x, aPosition.z);

     vec3 totalLight = mDiffuse + custom; //mAmbient * sceneAmbience;


    /*for(int i = 0; i < MAXLIGHTS; i++){
        int offset = i * 3;
        vec3 uLightPosition = lightProperties[offset];
        float distance = length(aPosition - uLightPosition);
        float att = 1.0 / (1.0 + (0.25 * pow(distance, 2)));

        vec3 lDiffuse = lightProperties[offset + 1];
        vec3 surfaceToLight = normalize(uLightPosition - aPosition);
        vec3 norm = normalize(aNormal);
        float difContribution = max(0.0, dot(norm, surfaceToLight));
        vec3 diffuse = difContribution * mDiffuse * lDiffuse;// * vec3(aPosition.x, aPosition.z, aPosition.y * timeDistort);

        //vec3 lSpecular = lightProperties[offset + 2];
        //vec3 surfaceToView = normalize(-aPosition);
        //vec3 reflection = reflect(-surfaceToLight, norm);

        //float specContribution = pow(max(0.0, dot(surfaceToView, reflection)), shininess);
        //vec3 specular = specContribution * mSpecular * lSpecular;
        totalLight = vec3(totalLight + (diffuse)*att);// No specular..its weird
	}*/
	gl_FragColor = vec4(totalLight, opacity);
}