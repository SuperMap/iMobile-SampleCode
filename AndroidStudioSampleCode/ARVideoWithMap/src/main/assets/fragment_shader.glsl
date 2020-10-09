#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 vTexCoord;
uniform samplerExternalOES sTexture;
uniform vec3 uGrayEffects;

varying float vAlphaFactor;

vec3 desaturate(vec3 color, float amount) {
    // Convert color to grayscale using Luma formula:
    // https://en.wikipedia.org/wiki/Luma_%28video%29
    vec3 gray = vec3(dot(vec3(0.2126, 0.7152, 0.0722), color));

    return vec3(mix(color, gray, amount));
}

vec3 rgb2hsv(vec3 c)
{
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

vec3 hsv2rgb(vec3 c)
{
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}


void main() {
    //gl_FragColor=texture2D(sTexture, vTexCoord);

    vec3 centralColor = texture2D(sTexture, vTexCoord).rgb;

    vec3 keyColor = vec3(0.1843, 1.0, 0.098);

    float threshold = 0.675;
    float slope = 0.2;
    float distance = abs(length(abs(keyColor - centralColor.rgb)));

    float edge0 = threshold * (1.0 - slope);
    float alpha = smoothstep(edge0, threshold, distance);

    vec3  color = desaturate(centralColor.rgb, 1.0 - (alpha * alpha * alpha));


    vec4 resultColor = vec4(color,1.0);
    resultColor.a = alpha;
    resultColor.rgb = color.rgb ;//inverseTonemapSRGB(color.rgb);
    resultColor.rgb *= resultColor.a;

    // for special result.
   //  gl_FragColor = vec4(0.299*centralColor.r+0.587*centralColor.g+0.114*centralColor.b);




/*
    if(resultColor.r > 0.5 && resultColor.g >  0.5  && resultColor.b  >  0.5)
    {
        resultColor.r = 0.9;
        resultColor.g = 0.9;
        resultColor.b = 0.9;

        resultColor.a -= 0.2;
        //discard;
        // resultColor .a = 1.0;
       //  resultColor.g = 0.7f;
   }*/



    //test processing black background.



    gl_FragColor = vec4(centralColor,  vAlphaFactor);




     /*
    //and we add alpha tunnel for adjust. finished.
    resultColor.rgb *=  1.0 - vAlphaFactor;//uAlphaFactor;
    resultColor.a = vAlphaFactor;

    gl_FragColor = vec4(centralColor,   vAlphaFactor);
    */




}