//
// Created by DarkWayC0de on 24/06/2021.
//

#ifndef LOCALIZACION_INALAMBRICA_MAIN_H
#define LOCALIZACION_INALAMBRICA_MAIN_H

#include <stdio.h>
#include <stdlib.h>


char *location_to_encode_and_encrypter(int longitud,
                                       int latitud,
                                       int altitud,
                                       int bearing,
                                       int speed);

uint8_t *encode_binary(int longitud, int latitud, int altitud, int bearing, int speed);

int8_t *addMAC(int8_t msg[10], uint8_t k[16]);

int8_t *encriptar_msg(int8_t *mac);

char *encode_hex_char_array(const uint8_t *encryptado);

bool negative(int i);

char *tobit(int num, int sz);

uint8_t chartouint8_t(const char array[8])

#endif //LOCALIZACION_INALAMBRICA_MAIN_H
