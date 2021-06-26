//
//  ProjectUntilites.h
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/3/13.
//  Copyright © 2019年 GSY. All rights reserved.
//

#ifndef ProjectUntilites_h
#define ProjectUntilites_h

#define WS(weakSelf)  __weak __typeof(&*self)weakSelf = self;

#define StrongSelf(strongSelf)  __strong __typeof(&*self) strongSelf = weakSelf;

#define PROJECT_Method_KeyboardAddObserver \
[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardDidShow:) name:UIKeyboardWillShowNotification object:nil]; \

#define PROJECT_Method_KeyboardRemoveObserver \
[[NSNotificationCenter defaultCenter] removeObserver:self name:UIKeyboardWillShowNotification object:nil];

#define PROJECT_Method_KeyBoardDisappearObserver \
[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardDidHidden:) name:UIKeyboardWillHideNotification object:nil]; \

#define PROJECT_Method_KeyBoardDisappearRemoveObserver \
[[NSNotificationCenter defaultCenter] removeObserver:self name:UIKeyboardWillHideNotification object:nil];

#endif /* ProjectUntilites_h */
