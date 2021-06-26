//
//  ZFChatAddView.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/30.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ZFChatAddView.h"
#import "ZFChatGlobal.h"

@interface ZFChatAddView ()


@property (nonatomic) CGRect originRect;
@property (nonatomic,copy) HelperintergerObjFlagInvocation click;
@property (nonatomic,strong) NSArray *dataSourceArr;
@property (nonatomic,strong) UIScrollView *listScroll;

@property (nonatomic,strong) NSArray *baseDataSource;

@property (nonatomic,assign) NSInteger power;
    
@property (nonatomic,assign) BOOL isSingleChat;
    
@end

@implementation ZFChatAddView

- (void)dealloc{
    
}

//显示位置
- (id)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if(self){
        _originRect = frame;
        _power = 0;
        _isSingleChat = NO;
        [self baseDataSource];
        self.frame = [self getMessageMoreDisappearRect];
        self.backgroundColor = [UIColor whiteColor];
    }
    return self;
}

- (void)addInvocation:(NSDictionary *)dic{
    if(dic[@"click"]){
        self.click = dic[@"click"];
    }
}

- (void)createUI{
    //,@"news_chat_more_position@3x.png"
    //,@"news_qr@3x.png"
    //,@"位置",@"个人名片"
    [self initialWithBaseData];
}

- (NSArray *)baseDataSource{
    NSArray *imgArr= nil;
    NSArray *textArr= nil;
    
    if(YiChatProject_IsNeedRedPackge){
        imgArr = @[@"news_chat_more_camera@3x.png",@"news_chat_more_photo@3x.png",@"chat_redPackge.png"];
        textArr = @[@"拍照",@"相册",@"红包"];
    }
    else{
        imgArr = @[@"news_chat_more_camera@3x.png",@"news_chat_more_photo@3x.png"];
        textArr = @[@"拍照",@"相册"];
    }
    
    if(_isSingleChat){
        NSMutableArray *img = [NSMutableArray arrayWithCapacity:0];
        NSMutableArray *text = [NSMutableArray arrayWithCapacity:0];
        
        [img addObjectsFromArray:imgArr];
        [text addObjectsFromArray:textArr];
        
        [img addObject:@"tabbar_contacts@3x.png"];
        [text addObject:@"个人名片"];
        
        imgArr = img;
        textArr = text;
    }
    
    NSMutableArray *entityArr=[NSMutableArray arrayWithCapacity:0];
    
    for (int i=0; i<imgArr.count; i++) {
        NSMutableDictionary *entity=[NSMutableDictionary dictionaryWithCapacity:0];
        
        [entity setObject:imgArr[i] forKey:@"img"];
        [entity setObject:textArr[i] forKey:@"text"];
        
        [entityArr addObject:entity];
    }
    return entityArr;
}

- (void)initialWithBaseData{
    self.dataSourceArr = [self.baseDataSource mutableCopy];
}

- (NSArray *)zhenEntity{
    NSArray *imgArr=@[@"chat_zhen.png"];
    NSArray *textArr=@[@"震"];
    
    NSMutableArray *entityArr=[NSMutableArray arrayWithCapacity:0];
    for (int i=0; i<imgArr.count; i++) {
        NSMutableDictionary *entity=[NSMutableDictionary dictionaryWithCapacity:0];
        
        [entity setObject:imgArr[i] forKey:@"img"];
        [entity setObject:textArr[i] forKey:@"text"];
        
        [entityArr addObject:entity];
    }
    return entityArr;
}

- (void)changeAddViewWithPower:(NSInteger)power{
    if(power == _power){
        return;
    }
    else{
        if(power == 0){
            [self initialWithBaseData];
        }
        else{
            NSMutableArray *arr = [NSMutableArray arrayWithCapacity:0];
            [arr addObjectsFromArray:[self.baseDataSource mutableCopy]];
            [arr addObjectsFromArray:[self zhenEntity]];
            
            self.dataSourceArr = arr;
        }
        
        if(_listScroll && [_listScroll isKindOfClass:[UIScrollView class]]){
            for (id tmp in _listScroll.subviews) {
                [tmp removeFromSuperview];
            }
            [_listScroll removeFromSuperview];
            _listScroll = nil;
        }
        
        [self makeUI];
    }
}

- (void)changeAddViewWithSingleChat{
    if(!_isSingleChat){
        _isSingleChat = YES;
        
        [self initialWithBaseData];
        
        if(_listScroll && [_listScroll isKindOfClass:[UIScrollView class]]){
            for (id tmp in _listScroll.subviews) {
                [tmp removeFromSuperview];
            }
            [_listScroll removeFromSuperview];
            _listScroll = nil;
        }
        
        [self makeUI];
    }
}

- (void)makeUI{
    CGSize contentSize=[self getScrollContentSize];
    
    UIScrollView *scroll=[ProjectHelper helper_factoryMakeScrollViewWithFrame:self.bounds contentSize:contentSize pagingEnabled:YES showsHorizontalScrollIndicator:YES showsVerticalScrollIndicator:NO scrollEnabled:YES];
    [self addSubview:scroll];
    _listScroll=scroll;
    
    [self makeScrollSubView];
}

- (void)makeScrollSubView{
    CGFloat x=18.0;
    CGFloat y=20.0;
    CGFloat w=(self.frame.size.width - x * 5) / 4;
    CGFloat h=(self.frame.size.height - y * 3) / 2;
    WS(weakSelf);
    
    for (int i=0; i < _dataSourceArr.count; i++) {
        
        ZFChatAddSingle *single=[[ZFChatAddSingle alloc] initWithFrame:CGRectMake(18.0 +  (i % 4) * (18.0 + w) , y + (i / 4) * (h + y), w, h)];
        single.dataSourceDic = _dataSourceArr[i];
        [single makeUI];
        [_listScroll addSubview:single];
        single.backgroundColor = [UIColor clearColor];
        
        NSDictionary *data = _dataSourceArr[i];
        NSString *text = @"";
        if([data isKindOfClass:[NSDictionary class]] && data){
            NSString *tmp = data[@"text"];
            if(tmp && [tmp isKindOfClass:[NSString class]]){
                text = tmp;
            }
        }
        //text
        HelperInvocation click = ^void(){
            if(weakSelf.click){
                weakSelf.click(i,text);
            }
        };
        
        [single addInvocation:@{@"click":click}];
        
    }
}

- (CGSize)getScrollContentSize{
    
    NSInteger page=_dataSourceArr.count / 8;
    NSInteger page_lost=_dataSourceArr.count % 8;
    if(page == 0){
        return CGSizeMake(self.frame.size.width, self.frame.size.height);
    }
    else{
        if(page_lost == 0){
            return CGSizeMake(self.frame.size.width * page, self.frame.size.height);
        }
        else{
            return CGSizeMake(self.frame.size.width * (page + 1) , self.frame.size.height);
        }
    }
}

- (void)XYChatAddView_appearWithAnimate:(void(^)(void))animate{
    WS(weakSelf);
    self.hidden = NO;
    [UIView animateWithDuration:0.3 animations:^{
        weakSelf.frame=[weakSelf getMessageMoreAppaerRect];
    } completion:^(BOOL finished) {
        animate();
    }];
}

- (void)XYChatAddView_disappearWithAnimate:(void(^)(void))finishInvocation{
    WS(weakSelf);
    [UIView animateWithDuration:0.3 animations:^{
        weakSelf.frame=[weakSelf getMessageMoreDisappearRect];
        weakSelf.alpha = 1;
    } completion:^(BOOL finished) {
        weakSelf.hidden = YES;
        finishInvocation();
        
    }];
}

#pragma mark  get

- (CGRect)getMessageMoreDisappearRect{
    return CGRectMake(_originRect.origin.x, _originRect.origin.y + _originRect.size.height,  _originRect.size.width , _originRect.size.height);
}

- (CGRect)getMessageMoreAppaerRect{
    return CGRectMake(_originRect.origin.x,_originRect.origin.y , _originRect.size.width, _originRect.size.height);
}
/*
 // Only override drawRect: if you perform custom drawing.
 // An empty implementation adversely affects performance during animation.
 - (void)drawRect:(CGRect)rect {
 // Drawing code
 }
 */

@end

@interface ZFChatAddSingle ()

@property (nonatomic,copy) HelperInvocation click;
@property (nonatomic,strong) UIImageView *icon;
@property (nonatomic,strong) UILabel *lab;

@end

@implementation ZFChatAddSingle

- (void)makeUI{
    
    CGFloat h=self.frame.size.height - 30.0;
    CGFloat w=self.frame.size.width;
    
    UIImageView *img=[ProjectHelper helper_factoryMakeImageViewWithFrame:CGRectMake(0, 0, w, h) andImg:nil];
    [self addSubview:img];
    _icon=img;
    
    UILabel *lab=[ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(0, h, w, h) andfont:PROJECT_TEXT_FONT_COMMON(14.0) textColor:PROJECT_COLOR_TEXTCOLOR_BLACK textAlignment:NSTextAlignmentCenter];
    [self addSubview:lab];
    _lab=lab;
    
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] init];
    [tap addTarget:self action:@selector(tapMethod:)];
    [self addGestureRecognizer:tap];
    
    [self yrMessageSingleEmotionSetValueWithResource];
}

- (void)addInvocation:(NSDictionary *)dic{
    if(dic[@"click"]){
        self.click = dic[@"click"];
    }
}


- (void)tapMethod:(UITapGestureRecognizer *)tap{
    if(self.click){
        self.click();
    }
}

- (void)yrMessageSingleEmotionSetValueWithResource{
    
    UIImage *img = [UIImage imageNamed:_dataSourceDic[@"img"]];
    NSString *text = _dataSourceDic[@"text"];
    
    CGFloat scale=img.size.width / img.size.height;
    CGFloat h=self.frame.size.height * 0.6;
    CGFloat w=self.frame.size.width;
    CGFloat img_W;
    CGFloat img_H=h;
    CGFloat temp=[ProjectHelper helper_GetWidthOrHeightIntoScale:scale width:0 height:h];
    if(temp <= w){
        img_W=temp;
    }
    else{
        img_W=w;
    }
    _icon.frame=CGRectMake(self.frame.size.width / 2 - img_W / 2, 0, img_W, img_H);
    _icon.image=img;
    
    _lab.frame=CGRectMake(0,_icon.frame.size.height, self.frame.size.width, self.frame.size.height * 0.4);
    
    _lab.text=text;
}

- (NSString *)getText{
    return _dataSourceDic[@"text"];
}


@end
