//
// Created by DarkWayC0de on 24/06/2021.
//
#include "main.h"
#include "Chacha20-256-csprng.h"


void location_to_encode_and_encrypter(int longitud,
                                      int latitud,
                                      int altitud,
                                      int bearing,
                                      int speed,
                                      char mackey[32],
                                      char cifradokey[88],
                                      int reiniciaCifrado,
                                      char result[32]
) {
    uint8_t fmackey[16];
    uint8_t fcifradokey[44];
    uint8_t msg[10];
    int8_t msgEncriptado[10];
    int8_t msgMAC[16];

    decode_hex_char_array(mackey, 32, fmackey);
    //printf("mackery ");
    //printArray(fmackey, 16);

    decode_hex_char_array(cifradokey, 88, fcifradokey);
    //printf("fcifradokey ");
    //printArray(fcifradokey, 44);

    encode_binary(longitud, latitud, altitud, bearing, speed, msg);
    //printf("mesaje ");
    //printArray(msg, 10);

    encriptar_msg(msg, fcifradokey, reiniciaCifrado, msgEncriptado);
    //printf("cifrado ");
    //printArray(msgEncriptado, 10);

    addMAC(msgEncriptado, fmackey, msgMAC);
    //printf("mac ");
    //printArray(msgMAC, 16);

    encode_hex_char_array(16, msgMAC, result);

}

/*void printArray(uint8_t array[], uint8_t sz) {
    for (int i = 0; i < sz; i++) {
        printf("%i", array[i]);
    }
    printf("\n");

}*/

void freeme(char *ptr) {
    //printf("\nfreeing address: %p\n", ptr);
    free(ptr);
}

const char *descifrado(char *mackey, int mackey_sz,
                       char *cifradokey, int cifradokey_sz,
                       char *MensajeUsuario, int MensasjeUsuario_sz,
                       int nMensaje) {
    uint8_t fmackey[16];
    uint8_t fcifradokey[44];
    uint8_t fmensaje[16];
    uint8_t mensajeencriptado[10];
    uint8_t mac[6];
    uint8_t n_mac[6];
    uint8_t msgMAC[16];
    uint8_t msg[10];
    char result[20];
    char *resultado = malloc(sizeof(char) * 20);
    if (resultado == NULL) exit(1);
    decode_hex_char_array(mackey, 32, fmackey);

    //printf("mackery ");
    //printArray(fmackey, 16);

    decode_hex_char_array(cifradokey, 88, fcifradokey);
    //printf("fcifradokey ");
    //printArray(fcifradokey, 44);

    decode_hex_char_array(MensajeUsuario, 32, fmensaje);
    //printf("mensaje inicial ");
    //printArray(fmensaje, 16);

    for (int i = 0; i < 10; i++) {
        mensajeencriptado[i] = fmensaje[i];
        if (i < 6) {
            mac[i] = fmensaje[10 + i];
        }
    }

    addMAC(mensajeencriptado, fmackey, msgMAC);
    //printf("nuevo_mac ");
    //printArray(msgMAC, 16);

    for (int i = 0; i < 6; i++) {
        n_mac[i] = msgMAC[10 + i];
    }

    if (compareArray(mac, n_mac, 6) == 0) {

        desencriptar_msg(mensajeencriptado, fcifradokey, nMensaje, msg);
        //printf("msg original ");
        //printArray(msg, 10);

        encode_hex_char_array(10, msg, result);

        strcpy(resultado, result);

    } else {
        result[0] = 'E';
        result[1] = 'r';
        result[2] = 'r';
        result[3] = 'o';
        result[4] = 'r';
        result[5] = ' ';
        result[3] = 'm';
        result[4] = 'a';
        result[5] = 'c';
        strcpy(resultado, result);
    }
    //printf("\npointer address: %p\n", resultado);

    return resultado;
}

char compareArray(uint8_t array[], uint8_t array2[], uint8_t sz) {
    int i;

    for (i = 0; i < sz; i++) {
        //printf("\ncomparamos %i con %i", array[i], array2[i]);
        if (array[i] != array2[i]) {
            //printf("\nfallo");
            return 1;
        }
    }
    return 0;
}

void desencriptar_msg(int8_t msgEncryptado[10], uint8_t cifradokey[44], int nMensaje,
                      int8_t msg[10]) {
    uint8_t nonce[12];
    uint8_t key[32];
    for (int i = 0; i < 32; i++) {
        key[i] = cifradokey[i];
        if (i < 12) {
            nonce[i] = cifradokey[32 + i];
        }
    }
    chachaSeed(cifradokey, nonce);
    for (int i = 0; i < nMensaje; i++) {
        for (int i = 0; i < 10; ++i) {
            char gastachacha;
            gastachacha = gastachacha ^ chachaGet();
        }
    }
    for (int i = 0; i < 10; ++i) {
        msg[i] = msgEncryptado[i] ^ chachaGet();
    }
}

void
encode_binary(int longitud, int latitud, int altitud, int bearing, int speed, uint8_t msg[10]) {
/**
*       signo longitud -         longitud       - signolatitud -          latitud         -     altitud      -    bearing    -  speed   - notuse
*       |       0      - 0000000|00000000|00000 -       0      - 00|00000000|00000000|000 - 00000|00000000|0 - 0000000|00000 - 000|0000 - XXXX|
* bytes              0              1                  2              3         4         5           6         7            8         9
*/

    char arrays[8];
    char *cslong = (negative(longitud) ? "1" : "0");
    char *clongituda = tobit((negative(longitud) ? longitud / -1 : longitud), 21);
    char clongitud[21];
    for (int i = 0; i < 21; ++i) {
        clongitud[i] = clongituda[i];
    }
    char *cslat = (negative(latitud) ? "1" : "0");
    char *clatituda = tobit((negative(latitud) ? latitud / -1 : latitud), 20);
    char clatitud[20];
    for (int i = 0; i < 20; ++i) {
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
    arrays[5] = clongitud[20];
    arrays[6] = cslat[0];
    arrays[7] = clatitud[0];
    msg[2] = chartouint8_t(arrays);
    arrays[0] = clatitud[1];
    arrays[1] = clatitud[2];
    arrays[2] = clatitud[3];
    arrays[3] = clatitud[4];
    arrays[4] = clatitud[5];
    arrays[5] = clatitud[6];
    arrays[6] = clatitud[7];
    arrays[7] = clatitud[8];
    msg[3] = chartouint8_t(arrays);
    arrays[0] = clatitud[9];
    arrays[1] = clatitud[10];
    arrays[2] = clatitud[11];
    arrays[3] = clatitud[12];
    arrays[4] = clatitud[13];
    arrays[5] = clatitud[14];
    arrays[6] = clatitud[15];
    arrays[7] = clatitud[16];
    msg[4] = chartouint8_t(arrays);
    arrays[0] = clatitud[17];
    arrays[1] = clatitud[18];
    arrays[2] = clatitud[19];
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

void addMAC(int8_t msgEncriptado[10], uint8_t k[16], int8_t msgMAC[16]) {

    uint32_t taglen = 6;
    uint8_t tag[6];

    uint32_t k1[4], k2[4];
    subkeys(k1, k2, (uint32_t *) k);

    chaskey(tag, taglen, msgEncriptado, 10, (uint32_t *) k, k1, k2);

    for (int i = 0; i < 10; i++) {
        if (i < 6) {
            msgMAC[10 + i] = tag[i];
        }
        msgMAC[i] = msgEncriptado[i];
    }
}

void encriptar_msg(int8_t msg[10], uint8_t cifradokey[44], int reiniciaCifrado,
                   int8_t msgEncryptado[10]) {
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
    for (int i = 0; i < 10; ++i) {
        msgEncryptado[i] = msg[i] ^ chachaGet();
    }
}

void encode_hex_char_array(int size, const uint8_t msgMac[size], char string[size * 2]) {
    static char hex[] = "0123456789ABCDEF";
    for (size_t i = 0; i < size; i++) {
        string[(i * 2) + 0] = hex[((msgMac[i] & 0xF0) >> 4)];
        string[(i * 2) + 1] = hex[((msgMac[i] & 0x0F) >> 0)];
    }
}

void decode_hex_char_array(char mackey[88], int i, uint8_t msgdecode[44]) {
    int k = 0;
    for (int j = 0; j < i / 2; ++j) {
        msgdecode[j] = (hexval(mackey[k + 1]));
        msgdecode[j] += (hexval(mackey[k]) * 16);
        k += 2;
    }

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
