//
// Created by DarkWayC0de on 24/06/2021.
//

#include "Chaskey16_29bytes.h"


#define DEBUG 1
#define ROTL(x,b) (uint32_t)( ((x) >> (32 - (b))) | ( (x) << (b)) )

#define ROUND \
  do { \
    v[0] += v[1]; v[1]=ROTL(v[1], 5); v[1] ^= v[0]; v[0]=ROTL(v[0],16); \
    v[2] += v[3]; v[3]=ROTL(v[3], 8); v[3] ^= v[2]; \
    v[0] += v[3]; v[3]=ROTL(v[3],13); v[3] ^= v[0]; \
    v[2] += v[1]; v[1]=ROTL(v[1], 7); v[1] ^= v[2]; v[2]=ROTL(v[2],16); \
  } while(0)

#define PERMUTE \
  ROUND; \
  ROUND; \
  ROUND; \
  ROUND; \
  ROUND; \
  ROUND; \
  ROUND; \
  ROUND; \
  ROUND; \
  ROUND; \
  ROUND; \
  ROUND; \
  ROUND; \
  ROUND; \
  ROUND; \
  ROUND;

const volatile uint32_t C[2] = { 0x00, 0x87 };

#define TIMESTWO(out,in) \
  do { \
    out[0] = (in[0] << 1) ^ C[in[3] >> 31]; \
    out[1] = (in[1] << 1) | (in[0] >> 31); \
    out[2] = (in[2] << 1) | (in[1] >> 31); \
    out[3] = (in[3] << 1) | (in[2] >> 31); \
  } while(0)

void subkeys(uint32_t k1[4], uint32_t k2[4], const uint32_t k[4]) {
    TIMESTWO(k1,k);
    TIMESTWO(k2,k1);
}

void chaskey(uint8_t *tag, uint32_t taglen, const uint8_t *m, const uint32_t mlen, const uint32_t k[4], const uint32_t k1[4], const uint32_t k2[4]) {

    const uint32_t *M = (uint32_t*) m; //puntero asociado al mensaje para procesarlo. El mensaje inicialmente se especifica como puntero a const uint8_t*  y aquí se hace un casting a uint32_t* en subbloques de longitud 128
    const uint32_t *end = M + (((mlen-1)>>4)<<2); /* pointer to last message block. Necesario para completar el bloque en caso de que la longitud del mensaje no sea múltiplo de 128*/


    const uint32_t *l;
    uint8_t lb[16];
    const uint32_t *lastblock;
    uint32_t v[4];

    int i;
    uint8_t *p;
    /*
     *  Resumen menor que 16 bytes (128 bits), si no se da la condición
     *  entonces se interrumpe la ejecución ¿Encontrar por qué?
     */
    assert(taglen <= 16);

    v[0] = k[0];
    v[1] = k[1];
    v[2] = k[2];
    v[3] = k[3];

    if (mlen != 0) {
        /*
         * Bucle principal en el que se procesa cada subbloque del mensaje
         * de entrada. Ver Figura 1 del artículo.
         * (K xor m1) permutar xor (mi permutar)
        */
        for ( ; M != end; M += 4 ) {
#ifdef DEBUG
            printf("(%3d) v[0] %08x\n", mlen, v[0]);
            printf("(%3d) v[1] %08x\n", mlen, v[1]);
            printf("(%3d) v[2] %08x\n", mlen, v[2]);
            printf("(%3d) v[3] %08x\n", mlen, v[3]);
            printf("(%3d) compress %08x %08x %08x %08x\n", mlen, m[0], m[1], m[2], m[3]);
#endif
            v[0] ^= M[0];
            v[1] ^= M[1];
            v[2] ^= M[2];
            v[3] ^= M[3];
            PERMUTE;
        }
    }
    /*
     * Procesamiento del último subbloque del mensaje dependiendo de si
     * su longitud es múltiplo del tamaño del bloque o no.
     * En este último caso se añade padding y se usa también la subclave
     * k2
    */
    printf ("Longitud del subbloque %d\n", 0xF);

    /*
     * si el mensaje tiene longitud múltiplo de la longitud del bloque.
     * En este caso, la longitud de bloque es 128 bits.
     */
    if ((mlen != 0) && ((mlen & 0xF) == 0)) {

        l = k1;
        lastblock = M;
    } else {
        l = k2;
        p = (uint8_t*) M;
        i = 0;
        for ( ; p != m + mlen; p++,i++) {
            lb[i] = *p;
        }
        lb[i++] = 0x01; /* padding bit */
        for ( ; i != 16; i++) {
            lb[i] = 0;
        }
        lastblock = (uint32_t*) lb;
    }

#ifdef DEBUG
    printf("(%3d) v[0] %08x\n", mlen, v[0]);
    printf("(%3d) v[1] %08x\n", mlen, v[1]);
    printf("(%3d) v[2] %08x\n", mlen, v[2]);
    printf("(%3d) v[3] %08x\n", mlen, v[3]);
    printf("(%3d) last block %08x %08x %08x %08x\n", mlen, lastblock[0], lastblock[1], lastblock[2], lastblock[3]);
#endif
// Último bloque  xor con lo anterior
    v[0] ^= lastblock[0];
    v[1] ^= lastblock[1];
    v[2] ^= lastblock[2];
    v[3] ^= lastblock[3];
// Se añade la subclave correspondiente con un xor
    v[0] ^= l[0];
    v[1] ^= l[1];
    v[2] ^= l[2];
    v[3] ^= l[3];
// Ultima permutación
    PERMUTE;

#ifdef DEBUG
    printf("(%3d) v[0] %08x\n", mlen, v[0]);
    printf("(%3d) v[1] %08x\n", mlen, v[1]);
    printf("(%3d) v[2] %08x\n", mlen, v[2]);
    printf("(%3d) v[3] %08x\n", mlen, v[3]);
#endif
// xor con la clave correspondiente
    v[0] ^= l[0];
    v[1] ^= l[1];
    v[2] ^= l[2];
    v[3] ^= l[3];
#ifdef DEBUG
    printf("\n RESULTADO DEL RESUMEN:\n");
    printf("(%3d) v[0] %08x\n", mlen, v[0]);
    printf("(%3d) v[1] %08x\n", mlen, v[1]);
    printf("(%3d) v[2] %08x\n", mlen, v[2]);
    printf("(%3d) v[3] %08x\n", mlen, v[3]);
#endif
    //los  taglen bits menos sinificaticos defienen el resumen

    memcpy(tag,v,taglen);


}




