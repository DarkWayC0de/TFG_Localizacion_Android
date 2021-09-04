//
// Created by DarkWayC0de on 24/06/2021.
//
#include "include/main_jni.h"


JNIEXPORT jstring JNICALL
Java_com_example_localizacionInalambrica_servicios_ServicioBluetooth_location_1to_1encode_1and_1encrypter(
        JNIEnv *env, jobject thiz, jint longitud, jint latitud, jint altitud, jint bearing,
        jint speed) {
    const char *result = location_to_encode_and_encrypter(longitud, latitud, altitud, bearing,
                                                          speed);
    jstring string = (*env)->NewStringUTF(env, result);
    return string;
}