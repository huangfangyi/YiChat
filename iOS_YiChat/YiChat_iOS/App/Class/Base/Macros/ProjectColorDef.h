//
//  ProjectColorDef.h
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/3/13.
//  Copyright © 2019年 GSY. All rights reserved.
//

#ifndef ProjectColorDef_h
#define ProjectColorDef_h

#define PROJECT_COLOR_WITH_HEX(hexValue) [UIColor colorWithRed:((float)((hexValue & 0xFF0000) >> 16)) / 255.0 green:((float)((hexValue & 0xFF00) >> 8)) / 255.0 blue:((float)(hexValue & 0xFF)) / 255.0 alpha:1.0f]

#define PROJECT_COLOR_GREEN [UIColor colorWithRed:54.0/255.0 green:163.0/255.0 blue:112.0/255.0 alpha:1]

#define PROJECT_COLOR_RED [UIColor colorWithRed:225.0/255.0 green:98.0/255.0 blue:98.0/255.0 alpha:1]

#define PROJECT_COLOR_ORANGECOLOR [UIColor colorWithRed:243.0/255.0 green:164.0/255.0 blue:34.0/255.0 alpha:1]

#define PROJECT_COLOR_TEXTCOLOR_BLACK [UIColor colorWithRed:0.0/255.0 green:0.0/255.0 blue:0.0/255.0 alpha:1]

#define PROJECT_COLOR_BlLUE [UIColor colorWithRed:107.0/255.0 green:125.0/255.0 blue:186.0/255.0 alpha:1]

#define PROJECT_COLOR_BlLUEDEEP [UIColor colorWithRed:8.0/255.0 green:33.0/255.0 blue:68.0/255.0 alpha:1]

#define PROJECT_COLOR_BlLUELIGHT PROJECT_COLOR_WITH_HEX(0x6886ab)

#define PROJECT_COLOR_DARKREDCOLOR [UIColor colorWithRed:170.0/255.0 green:51.0/255.0 blue:53.0/255.0 alpha:1]

#define PROJECT_COLOR_GRAY   [UIColor colorWithRed:238.0/255.0 green:239.0/255.0 blue:240.0/255.0 alpha:1]

#define PROJECT_COLOR_TEXTGRAY   [UIColor colorWithRed:155.0/255.0 green:155.0/255.0 blue:155.0/255.0 alpha:1]

#define PROJECT_COLOR_LINEBACK [UIColor colorWithRed:207.0/255.0 green:203.0/255.0 blue:203.0/255.0 alpha:1]

#define PROJECT_COLOR_LIGHTBLUE [UIColor colorWithRed:122.0/255.0 green:130.0/255.0 blue:138.0/255.0 alpha:1]

#endif /* ProjectColorDef_h */
