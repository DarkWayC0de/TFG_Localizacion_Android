//
// Created by DarkWayC0de on 24/06/2021.
//
#include "main.h"
#include "Chacha20-256-csprng.h"


char *location_to_encode_and_encrypter(int longitud,
                                       int latitud,
                                       int altitud,
                                       int bearing,
                                       int speed,
                                       char mackey[32],
                                       char cifradokey[88],
                                       int reiniciaCifrado
) {
    uint8_t *k; //TODOdd
    k = decode_hex_char_array(mackey, 32);
    uint8_t fmackey[16];
    for (int i = 0; i < 16; ++i) {
        fmackey[i] = k[i];
    }
    k = decode_hex_char_array(cifradokey, 88);
    uint8_t fcifradokey[44];
    for (int i = 0; i < 44; ++i) {
        fcifradokey[i] = k[i];
    }

    uint8_t *msg; //[10]
    msg = encode_binary(longitud, latitud, altitud, bearing, speed);


    int8_t *msgMAC; //[16]
    msgMAC = addMAC(msg, fmackey);


    int8_t *msgEncriptado; //[16]
    msgEncriptado = encriptar_msg(msgMAC, fcifradokey, reiniciaCifrado);


    char *msgfinal; //[32]
    msgfinal = encode_hex_char_array(msgEncriptado);

    return msgfinal;

}


uint8_t *encode_binary(int longitud, int latitud, int altitud, int bearing, int speed) {
/**
*       signo longitud -         longitud       - signolatitud -          latitud         -     altitud      -    bearing    -  speed   - notuse
*       |       0      - 0000000|00000000|00000 -       0      - 00|00000000|00000000|000 - 00000|00000000|0 - 0000000|00000 - 000|0000 - XXXX|
* bytes              0              1                  2              3         4         5           6         7            8         9
*/
    static uint8_t msg[10];
    char arrays[8];
    char *cslong = (negative(longitud) ? "1" : "0");
    char *clongituda = tobit((negative(longitud) ? longitud / -1 : longitud), 20);
    char clongitud[20];
    for (int i = 0; i < 20; ++i) {
        clongitud[i] = clongituda[i];
    }
    char *cslat = (negative(latitud) ? "1" : "0");
    char *clatituda = tobit((negative(latitud) ? latitud / -1 : latitud), 21);
    char clatitud[21];
    for (int i = 0; i < 21; ++i) {
        clatitud[i] = clatituda[i];
    }
    char *caltituda = tobit(altitud, 14);
    char caltitud[14];
    for (int i = 0; i < 14; ++i) {
        caltitud[i] = caltituda[i];
    }
    char *cbearinga = tobit(bearing, 12);
    char cbearing[12];
    for (int i = 0; i < 12; ++i) {
        cbearing[i] = cbearinga[i];
    }
    char *cspeed = tobit(speed, 7);
    arrays[0] = cslong[0];
    arrays[1] = clongitud[0];
    arrays[2] = clongitud[1];
    arrays[3] = clongitud[2];
    arrays[4] = clongitud[3];
    arrays[5] = clongitud[4];
    arrays[6] = clongitud[5];
    arrays[7] = clongitud[6];
    msg[0] = chartouint8_t(arrays);
    arrays[0] = clongitud[7];
    arrays[1] = clongitud[8];
    arrays[2] = clongitud[9];
    arrays[3] = clongitud[10];
    arrays[4] = clongitud[11];
    arrays[5] = clongitud[12];
    arrays[6] = clongitud[13];
    arrays[7] = clongitud[14];
    msg[1] = chartouint8_t(arrays);
    arrays[0] = clongitud[15];
    arrays[1] = clongitud[16];
    arrays[2] = clongitud[17];
    arrays[3] = clongitud[18];
    arrays[4] = clongitud[19];
    arrays[5] = cslat[0];
    arrays[6] = clatitud[0];
    arrays[7] = clatitud[1];
    msg[2] = chartouint8_t(arrays);
    arrays[0] = clatitud[2];
    arrays[1] = clatitud[3];
    arrays[2] = clatitud[4];
    arrays[3] = clatitud[5];
    arrays[4] = clatitud[6];
    arrays[5] = clatitud[7];
    arrays[6] = clatitud[8];
    arrays[7] = clatitud[9];
    msg[3] = chartouint8_t(arrays);
    arrays[0] = clatitud[10];
    arrays[1] = clatitud[11];
    arrays[2] = clatitud[12];
    arrays[3] = clatitud[13];
    arrays[4] = clatitud[14];
    arrays[5] = clatitud[15];
    arrays[6] = clatitud[16];
    arrays[7] = clatitud[17];
    msg[4] = chartouint8_t(arrays);
    arrays[0] = clatitud[18];
    arrays[1] = clatitud[19];
    arrays[2] = clatitud[20];
    arrays[3] = caltitud[0];
    arrays[4] = caltitud[1];
    arrays[5] = caltitud[2];
    arrays[6] = caltitud[3];
    arrays[7] = caltitud[4];
    msg[5] = chartouint8_t(arrays);
    arrays[0] = caltitud[5];
    arrays[1] = caltitud[6];
    arrays[2] = caltitud[7];
    arrays[3] = caltitud[8];
    arrays[4] = caltitud[9];
    arrays[5] = caltitud[10];
    arrays[6] = caltitud[11];
    arrays[7] = caltitud[12];
    msg[6] = chartouint8_t(arrays);
    arrays[0] = caltitud[13];
    arrays[1] = cbearing[0];
    arrays[2] = cbearing[1];
    arrays[3] = cbearing[2];
    arrays[4] = cbearing[3];
    arrays[5] = cbearing[4];
    arrays[6] = cbearing[5];
    arrays[7] = cbearing[6];
    msg[7] = chartouint8_t(arrays);
    arrays[0] = cbearing[7];
    arrays[1] = cbearing[8];
    arrays[2] = cbearing[9];
    arrays[3] = cbearing[10];
    arrays[4] = cbearing[11];
    arrays[5] = cspeed[0];
    arrays[6] = cspeed[1];
    arrays[7] = cspeed[2];
    msg[8] = chartouint8_t(arrays);
    arrays[0] = cspeed[3];
    arrays[1] = cspeed[4];
    arrays[2] = cspeed[5];
    arrays[3] = cspeed[6];
    arrays[4] = '0';
    arrays[5] = '0';
    arrays[6] = '0';
    arrays[7] = '0';
    msg[9] = chartouint8_t(arrays);

    return msg;
}

uint8_t chartouint8_t(const char array[8]) {
    uint8_t value = 0;
    value += (array[7] == '0') ? 0 : 1;
    value += (array[6] == '0') ? 0 : 2;
    value += (array[5] == '0') ? 0 : 4;
    value += (array[4] == '0') ? 0 : 8;
    value += (array[3] == '0') ? 0 : 16;
    value += (array[2] == '0') ? 0 : 32;
    value += (array[1] == '0') ? 0 : 64;
    value += (array[0] == '0') ? 0 : 128;
    return value;
}

char *tobit(int num, int sz) {
    char bits[sz];
    char dbits[sz];
    for (int i = 0; i < sz; ++i) {
        if (num % 2 == 1) {
            dbits[i] = '1';
        } else {
            dbits[i] = '0';
        }
        num /= 2;
    }
    for (int i = 0; i < sz; ++i) {
        bits[i] = dbits[sz - 1 - i];
    }
    static char sbits[21];
    for (int i = 0; i < 21; ++i) {
        sbits[i] = (i < sz) ? (bits[i]) : '0';
    }
    return sbits;
}

bool negative(int i) {
    return (i < 0) ? true : false;
}

int8_t *addMAC(int8_t msg[10], uint8_t k[16]) {
    static int8_t msgMAC[16];

    uint32_t taglen = 6;
    uint8_t tag[6];

    uint32_t k1[4], k2[4];
    subkeys(k1, k2, (uint32_t *) k);

    chaskey(tag, taglen, msg, 10, (uint32_t *) k, k1, k2);

    for (int i = 0; i < 10; i++) {
        if (i < 6) {
            msgMAC[10 + i] = tag[i];
        }
        msgMAC[i] = msg[i];
    }

    return msgMAC;
}

int8_t *encriptar_msg(int8_t msgMac[16], uint8_t cifradokey[44], int reiniciaCifrado) {
    static int8_t msgEncryptado[16];
    if (reiniciaCifrado == 0) {
        uint8_t nonce[12];
        uint8_t key[32];
        for (int i = 0; i < 32; i++) {
            key[i] = cifradokey[i];
            if (i < 12) {
                nonce[i] = cifradokey[32 + i];
            }
        }
        chachaSeed(cifradokey, nonce);
    }
    for (int i = 0; i < 16; ++i) {
        msgEncryptado[i] = msgMac[i] ^ chachaGet();
    }

    return msgEncryptado;
}

char *encode_hex_char_array(const uint8_t msgEncriptado[16]) {
    static char string[32];
    static char hex[] = "0123456789ABCDEF";
    for (size_t i = 0; i < 16; i++) {
        string[(i * 2) + 0] = hex[((msgEncriptado[i] & 0xF0) >> 4)];
        string[(i * 2) + 1] = hex[((msgEncriptado[i] & 0x0F) >> 0)];
    }
    return string;
}

uint8_t *decode_hex_char_array(char mackey[88], int i) {
    uint8_t msgdecode[44];
    int k = 0;
    for (int j = 0; j < i / 2; ++j) {
        msgdecode[j] = (hexval(mackey[k + 1]));
        msgdecode[j] += (hexval(mackey[k]) * 16);
        k += 2;
    }
    return msgdecode;
}

uint8_t hexval(char h) {
    uint8_t n = 16;
    static char hex[] = "0123456789ABCDEF";

    for (int i = 0; i < 16; ++i) {
        if (h == hex[i]) {
            return i;
        }
    }
    return n;
}