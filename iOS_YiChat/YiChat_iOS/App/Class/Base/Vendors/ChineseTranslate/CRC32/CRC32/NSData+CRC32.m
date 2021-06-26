//
//  NSData+CRC32.m
//  CRC32_iOS
//
//  Created by 宣佚 on 15/7/14.
//  Copyright (c) 2015年 宣佚. All rights reserved.
//

#import "NSData+CRC32.h"



@implementation NSData (CRC32)

- (int32_t)imcrc32
{
    uint32_t *table = malloc(sizeof(uint32_t) * 1024);
    uint32_t crc = 0xffffffff;
    uint8_t *bytes = (uint8_t *)[self bytes];
    
    for (uint32_t i=0; i<1024; i++) {
        table[i] = i;
        for (int j=0; j<8; j++) {
            if (table[i] & 1) {
                table[i] = (table[i] >>= 1) ^ 0xedb88320;
            } else {
                table[i] >>= 1;
            }
        }
    }
    
    for (int i=0; i<self.length; i++) {
        uint32_t value = table[crc & (0xff) ^ (bytes[i])];
        
        crc = (crc >> 8) ^ value;
    }
    
    crc ^= 0xffffffff;
    
    free(table);
    return crc;
    
}






@end
