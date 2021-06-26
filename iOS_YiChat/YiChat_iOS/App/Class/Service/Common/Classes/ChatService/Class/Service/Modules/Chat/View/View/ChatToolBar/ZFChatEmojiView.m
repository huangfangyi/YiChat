//
//  ZFChatEmojiView.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/30.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ZFChatEmojiView.h"
#import "HorizontalTableView.h"
#import "ZFChatGlobal.h"
#import "ZFEmojiListView.h"
#import "ZFChatResourceHelper.h"

@interface ZFChatEmojiView ()<HorizontalTableViewDelegate,UITableViewDelegate,UITableViewDataSource>
{
    
}

@property (nonatomic,strong) HelperObjFlagInvocation selecteEmojiInvocation;

@property (nonatomic,strong) HelperInvocation sendDefaultEmojiInvocation;

@property (nonatomic,strong) NSMutableArray *emojiControlArr;

@property (nonatomic,assign) NSInteger menuNum;

//表情列表
@property (nonatomic,strong) ZFEmojiListView *emojiListScroll;
//底部菜单
@property (nonatomic,strong) UIScrollView *emojiMenuScroll;
//收藏的表情列表
@property (nonatomic,strong) ZFEmojiListView *emojiAddListScroll;

@property (nonatomic,strong) HorizontalTableView *horizontallTable;

@property (nonatomic,strong) ZFChatMenuSingle *sendEmojiSingle;

@property (nonatomic,strong) NSMutableArray <ZFChatMenuSingle *>*menuSingleArr;

@end

#define XYEmojiListView_EMoji_H 200.0
@implementation ZFChatEmojiView

- (void)dealloc{
    [self removeObserbver];
}

- (id)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if(self){
        _emojiControlArr = [NSMutableArray arrayWithCapacity:0];
        _menuSingleArr = [NSMutableArray arrayWithCapacity:0];
        _menuNum = 2;
    }
    return self;
}

- (void)createUI{
    self.backgroundColor = [UIColor whiteColor];
    
    [self makeHorizontal];
    
    [self makeMenu];
    
    [self updateHorizontal];
}

- (void)updateHorizontal{
    [_horizontallTable refreshData];
}

- (void)makeHorizontal{
    _horizontallTable = [[HorizontalTableView alloc] initWithFrame:CGRectMake(0, 0, self.frame.size.width, [self getScrollHWithType:0])];
    
    _horizontallTable.delegate = self;
    
    [self addSubview:_horizontallTable];
}

- (void)makeMenu{
    WS(weakSelf);
    
    CGFloat h = [self getScrollHWithType:1];
    
    CGFloat singleW = 40.0;
    _emojiMenuScroll = [ProjectHelper helper_factoryMakeScrollViewWithFrame:CGRectMake(0, self.frame.size.height - h, self.frame.size.width - singleW, h) contentSize:CGSizeMake(self.frame.size.width, h) pagingEnabled:NO showsHorizontalScrollIndicator:NO showsVerticalScrollIndicator:NO scrollEnabled:YES];
    [self addSubview:_emojiMenuScroll];
    
    //emoji menu arr
    //,[UIImage imageNamed:@"aixin.png"]
    
    BOOL isNeedAddEmoji = YiChatProject_IsNeedGifEmoji;
    
    NSArray *imgArr = @[];
    if(isNeedAddEmoji){
        imgArr = @[[UIImage imageNamed:@"xiaolian.png"],[UIImage imageNamed:@"aixin.png"]];
    }
    else{
        imgArr = @[[UIImage imageNamed:@"xiaolian.png"]];
        _menuNum = 1;
    }
    UIImage *tmp = nil;
    
    NSMutableArray *menuSingleArr = [NSMutableArray arrayWithCapacity:0];
    for (int i = 0; i < _menuNum; i++) {
        if((imgArr.count - 1) >= i){
            tmp = imgArr[i];
        }
        
        HelperInvocation invocation = ^void(){
            for (int k = 0; k < weakSelf.menuNum; k ++) {
                if(k != i){
                    if((weakSelf.menuSingleArr.count - 1) >= k){
                        [weakSelf.menuSingleArr[k] changeUIForSelecte:NO];
                    }
                }
                else{
                    if((weakSelf.menuSingleArr.count - 1) >= k){
                        [weakSelf.menuSingleArr[k] changeUIForSelecte:YES];
                    }
                    [weakSelf.horizontallTable changeScrollViewContentOffSetX:self.frame.size.width * k];
                    if(k == 0){
                        weakSelf.sendEmojiSingle.hidden = NO;
                    }
                    else{
                        weakSelf.sendEmojiSingle.hidden = YES;
                    }
                }
            }
        };
        
        ZFChatMenuSingle *single = [[ZFChatMenuSingle alloc] initWithFrame:CGRectMake(i * singleW, 0, singleW, _emojiMenuScroll.frame.size.height)];
        single.icon = tmp;
        [single addInvocation:@{@"click":invocation}];
        [single createUI];
        [_emojiMenuScroll addSubview:single];
        [menuSingleArr addObject:single];
    }
    
    [_menuSingleArr addObjectsFromArray:menuSingleArr];
    
    HelperInvocation invocation = ^void(){
        if(weakSelf.sendDefaultEmojiInvocation){
            weakSelf.sendDefaultEmojiInvocation();
        }
    };
    
    ZFChatMenuSingle *single = [[ZFChatMenuSingle alloc] initWithFrame:CGRectMake(_emojiMenuScroll.frame.origin.x + _emojiMenuScroll.frame.size.width, _emojiMenuScroll.frame.origin.y, singleW, _emojiMenuScroll.frame.size.height)];
    single.str = @"发送";
    [single addInvocation:@{@"click":invocation}];
    [single createUI];
    [self addSubview:single];
    [_menuSingleArr addObject:single];
    _sendEmojiSingle = single;
    
    if(_menuSingleArr.count != 0){
        [_menuSingleArr[0] changeUIForSelecte:YES];
    }
    
    [_emojiControlArr removeAllObjects];
    for (int i = 0; i < _menuNum; i ++) {
        if(i == 0){
            [_emojiControlArr addObject:self.emojiListScroll];
        }
        else if(i == 1){
            [_emojiControlArr addObject:self.emojiAddListScroll];
        }
    }
}

- (ZFEmojiListView *)emojiListScroll{
    if(!_emojiListScroll){
        WS(weakSelf);
        
        _emojiListScroll = [[ZFEmojiListView alloc] initWithFrame:CGRectMake(self.frame.size.width, 0, self.frame.size.width, [self getScrollHWithType:0])];
        _emojiListScroll.dataSourceArr = [ZFChatResourceHelper ZFResourceHelperGetChatEmojiArr];
        _emojiListScroll.identifier = @"defaultEmoji";
        [_emojiListScroll makeUI];
        
        HelperObjFlagInvocation sendEmojiInvocation = ^void(NSNumber *num){
            NSArray *textArr = [ZFChatResourceHelper ZFResourceHelperGetChatEmojiTTextArr];
            if(textArr.count - 1 >= num.integerValue){
                weakSelf.selecteEmojiInvocation(textArr[num.integerValue]);
            }
        };
        [_emojiListScroll addSelecteEmojiInvocation:@{@"click":sendEmojiInvocation}];
        
        [self addobserver];
    }
    return _emojiListScroll;
}

- (ZFEmojiListView *)emojiAddListScroll{
    if(!_emojiAddListScroll){
        WS(weakSelf);
        
        _emojiAddListScroll = [[ZFEmojiListView alloc] initWithFrame:CGRectMake(self.frame.size.width, 0, self.frame.size.width, [self getScrollHWithType:0])];
        _emojiAddListScroll.dataSourceArr = [ZFChatResourceHelper ZFResourceHelperGetChatGIFEmojiArr];
        _emojiAddListScroll.identifier = @"addEmoji";
        [_emojiAddListScroll makeUI];
        
        HelperObjFlagInvocation sendEmojiInvocation = ^void(NSNumber *num){
            weakSelf.selecteEmojiInvocation([NSString stringWithFormat:@"gemoji_%ld",num.integerValue + 1]);
        };
        [_emojiAddListScroll addSelecteEmojiInvocation:@{@"click":sendEmojiInvocation}];
    }
    return _emojiAddListScroll;
}

- (void)addSelecteDefaultEmojiInvocation:(NSDictionary *)dic{
    if(dic[@"click"]){
        self.selecteEmojiInvocation = dic[@"click"];
    }
}

- (void)addSendDefaultEmojiInvocation:(NSDictionary *)dic{
    if(dic[@"click"]){
        self.sendDefaultEmojiInvocation = dic[@"click"];
    }
}

- (void)makeAddEmojiListScroll{
    
    _emojiAddListScroll = [[ZFEmojiListView alloc] initWithFrame:CGRectMake(self.frame.size.width, 0, self.frame.size.width, [self getScrollHWithType:0])];
    
    [self addobserver];
    
  //  [self makeAddEmojiSubUI];
}

#pragma mark

- (NSInteger)numberOfColumnsForTableView:(HorizontalTableView *)tableView{
    return _menuNum;
}

- (CGFloat)columnWidthForTableView:(HorizontalTableView *)tableView{
    return self.frame.size.width;
}

- (UIView *)tableView:(HorizontalTableView *)tableView viewForIndex:(NSInteger)index{
    if((_emojiControlArr.count - 1) >= index){
        return _emojiControlArr[index];
    }
    else{
        return [[UIView alloc] initWithFrame:CGRectMake(0, 0, tableView.frame.size.width, tableView.frame.size.height)];
    }
}


- (CGFloat)getScrollHWithType:(NSInteger)type{
    CGFloat menuh = 35.0;
    if(type == 0){
        return self.frame.size.height - menuh;
    }
    else{
        return menuh;
    }
}

- (CGRect)getMessageMoreDisappearRect{
    return CGRectMake(_originRect.origin.x, _originRect.origin.y + _originRect.size.height,  _originRect.size.width , _originRect.size.height);
}

- (CGRect)getMessageMoreAppaerRect{
    return CGRectMake(_originRect.origin.x,_originRect.origin.y , _originRect.size.width, _originRect.size.height);
}

- (void)XYChatEmojiView_appearWithAnimate:(void(^)(void))animate{
    WS(weakSelf);
    self.hidden = NO;
    self.frame = [self getMessageMoreDisappearRect];
    [UIView animateWithDuration:0.3 animations:^{
        weakSelf.frame=[weakSelf getMessageMoreAppaerRect];
    } completion:^(BOOL finished) {
        
        animate();
    }];
}

- (void)XYChatEmojiView_disappearWithAnimate:(void(^)(void))finishInvocation{
    WS(weakSelf);
    [UIView animateWithDuration:0.3 animations:^{
        weakSelf.frame=[weakSelf getMessageMoreDisappearRect];
        weakSelf.alpha = 1;
    } completion:^(BOOL finished) {
        weakSelf.hidden = YES;
        finishInvocation();
        
    }];
}


- (void)addobserver{
    [_horizontallTable addObserver:self forKeyPath:@"offSetX" options:NSKeyValueObservingOptionNew context:nil];
}

- (void)removeObserbver{
    [_horizontallTable removeObserver:self forKeyPath:@"offSetX"];
}

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary<NSKeyValueChangeKey,id> *)change context:(void *)context{
    if(object == _horizontallTable && [keyPath isEqualToString:@"offSetX"]){
        NSLog(@"%@",change);
        
        for (int i = 0; i<_menuSingleArr.count; i++) {
            
            [_menuSingleArr[i] changeUIForSelecte:NO];
            
            if([change[@"new"] intValue] == self.frame.size.width * i){
                [_menuSingleArr[i] changeUIForSelecte:YES];
            }
        }
        if([change[@"new"] intValue] == self.frame.size.width * 0){
            //隐藏
            _sendEmojiSingle.hidden = NO;
        }
        else{
            //显示
            _sendEmojiSingle.hidden = YES;
        }
    }
}
/*
 // Only override drawRect: if you perform custom drawing.
 // An empty implementation adversely affects performance during animation.
 - (void)drawRect:(CGRect)rect {
 // Drawing code
 }
 */

@end

@interface ZFChatMenuSingle ()

@property (nonatomic,copy) HelperInvocation click;

@end

@implementation ZFChatMenuSingle

- (void)createUI{
    if(_icon == nil && _str != nil){
        [self makeUIForText];
    }
    else if(_icon != nil && _str == nil){
        [self makeUIForIcon];
    }
}

- (void)makeUIForIcon{
    UIImage *tmp = _icon;
    
    CGFloat h = self.frame.size.height * 0.6;
    CGFloat w = [ProjectHelper helper_GetWidthOrHeightIntoScale:tmp.size.width / tmp.size.height width:0 height:h];
    
    UIImageView *icon = [ProjectHelper helper_factoryMakeImageViewWithFrame:CGRectMake(self.frame.size.width / 2- w / 2, self.frame.size.height / 2 - h / 2, w, h) andImg:tmp];
    [self addSubview:icon];
    _menuIcon = icon;
    
    UIButton *btn = [ProjectHelper helper_factoryMakeClearButtonWithFrame:self.bounds target:self method:@selector(menuIconClickBtnMethod:)];
    [self addSubview:btn];
}

- (void)changeUIForSelecte:(BOOL)selecte{
    if(selecte == YES){
        self.backgroundColor  = PROJECT_COLOR_APPBACKCOLOR;
    }
    else{
        self.backgroundColor = [UIColor whiteColor];
    }
}

- (void)menuIconClickBtnMethod:(UIButton *)btn{
    if(self.click){
        self.click();
    }
}

- (void)makeUIForText{
    NSString *tmp = _str;
    
    _menuLab = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(0, 0, self.frame.size.width, self.frame.size.height) andfont:PROJECT_TEXT_FONT_COMMON(14) textColor:PROJECT_COLOR_TEXTCOLOR_BLACK textAlignment:NSTextAlignmentCenter];
    [self addSubview:_menuLab];
    _menuLab.text = tmp;
    
    UIButton *btn = [ProjectHelper helper_factoryMakeClearButtonWithFrame:_menuLab.frame target:self method:@selector(menuTextClickMethtod:)];
    [self addSubview:btn];
    
}

- (void)addInvocation:(NSDictionary *)invocation{
    if(invocation[@"click"]){
        self.click = invocation[@"click"];
    }
}

- (void)menuTextClickMethtod:(UIButton *)btn{
    if(self.click){
        self.click();
    }
}

@end

