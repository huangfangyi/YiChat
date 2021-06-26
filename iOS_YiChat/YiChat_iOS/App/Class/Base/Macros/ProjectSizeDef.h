//
//  ProjectSizeDef.h
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/3/13.
//  Copyright © 2019年 GSY. All rights reserved.
//

#ifndef ProjectSizeDef_h
#define ProjectSizeDef_h

#import "YRGeneralApis.h"
#import "ProjectHelper.h"
#import "ProjectConfigure.h"

#define PROJECT_SIZE_NAV_BLANK PROJECT_SIZE_SUTEBLE_W(18.0)

#define PROJECT_SIZE_WIDTH [UIScreen mainScreen].bounds.size.width

#define PROJECT_SIZE_SafeAreaInset [[ProjectConfigure defaultConfigure] safeArea]

#define PROJECT_SIZE_HEIGHT [[ProjectConfigure defaultConfigure] screenHeight]

#define PROJECT_SIZE_NAVH  [[ProjectConfigure defaultConfigure] navSize].height

#define PROJECT_SIZE_STATUSH  [[ProjectConfigure defaultConfigure] statusSize].height

#define PROJECT_SIZE_TABH  [[ProjectConfigure defaultConfigure] tabSize].height

#define PROJECT_SIZE_SUTEBLE_H(SIZE) [YRGeneralApis yrGeneralApisGetScreenSuitable_H:SIZE]

#define PROJECT_SIZE_SUTEBLE_W(SIZE) [YRGeneralApis yrGeneralApisGetScreenSuitable_W:SIZE]

#define PROJECT_SIZE_SACLE(SECTION,TOTAL) (SECTION / TOTAL)


#endif /* ProjectSizeDef_h */
