//
// Created by DarkWayC0de on 24/06/2021.
//

#ifndef LOCALIZACION_INALAMBRICA_CHASKEY16_29BYTES_H
#define LOCALIZACION_INALAMBRICA_CHASKEY16_29BYTES_H

/**
   Chaskey-12 reference C implementation

   Written in 2015 by Nicky Mouha, based on Chaskey

   To the extent possible under law, the author has dedicated all copyright
   and related and neighboring rights to this software to the public domain
   worldwide. This software is distributed without any warranty.

   You should have received a copy of the CC0 Public Domain Dedication along with
   this software. If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.

   NOTE: This implementation assumes a little-endian architecture
         that does not require aligned memory accesses.
         *
         *
	We present Chaskey, a permutation-based MAC algorithm.
	Chaskey takes a 128-bit key K and processes a message m in 128-bit
	blocks using a 128-bit permutation π.
	This permutation is based on the Addition-Rotation-XOR (ARX)
	design methodology.
*/
#include <stdint.h>
#include <stdio.h>
#include <string.h>
#include <assert.h>

void subkeys(uint32_t k1[4], uint32_t k2[4], const uint32_t k[4]);

void chaskey(uint8_t *tag, uint32_t taglen, const uint8_t *m, const uint32_t mlen, const uint32_t k[4], const uint32_t k1[4], const uint32_t k2[4]);

#endif //LOCALIZACION_INALAMBRICA_CHASKEY16_29BYTES_H

///
///                           Ejemplo de uso
///
///int main() {
///    /*
///     * Datos de entrada:
///     * 	Mensaje: m, array de enteros de 1 byte. En la trama Beacon de Bluetooth como máximo tenemos mensajes de 29 bytes
///     * 	Clave: k, array de 16 enteros de 1 byte -> clave de 128 bits
///     *
///     *
///     * Datos de salida:
///     * 	Resumen/etiqueta: tag, array de 8 enteros de 1 byte, 64 bits
///     */
///    int8_t m[29]; // mensaje 29 bytes (uint8_t (uint8_t = 1 byte),
///    uint8_t k[16] = { 0x00, 0x11, 0x22, 0x33,
///                      0x44, 0x55, 0x66, 0x77,
///                      0x88, 0x99, 0xaa, 0xbb,
///                      0xcc, 0xdd, 0xee, 0xff }; //Clave definida de longitud 128 bits
///
///
///
///    uint32_t taglen = 8; //longitud del resumen, 8 bytes = 64 bits
///    uint8_t tag[8]; // resultado del resumen - tag 64 bits
///
///
///    uint32_t k1[4], k2[4]; // subclaves de 128 bits
///    int i;
///
///    /* key schedule */
///    subkeys(k1,k2,(uint32_t*) k);
///#if DEBUG
///    printf("K0 %08x %08x %08x %08x\n", k[0], k[1], k[2], k[3]);
///    printf("K1 %08x %08x %08x %08x\n", k1[0], k1[1], k1[2], k1[3]);
///    printf("K2 %08x %08x %08x %08x\n", k2[0], k2[1], k2[2], k2[3]);
///#endif
///
///    /* Cálculo del mac */
///    for (i = 0; i < 29; i++)
///        m[i] = i; //mensaje a resumir-etiquetar de longitud 29. En este caso enteros de 0 a 28
///
///    chaskey(tag, taglen, m, i, (uint32_t*) k, k1, k2);
///    /*
///     * los 64 bits menos significativos. Los 8 bytes menos significativos.
///     * Se usa little endian byte ordering.
///     * Inside every byte, bit numbering starts with the least significant
///     * bit.
///     */
///    printf("Resumen: ");
///    for (i = 0; i < 8; i++)
///        printf("%02x", tag[i]);
///    printf("\n");
///
///    return 0;
///}
///