//
//  YiChatConversationMenuView.m
//  YiChat_iOS
//
//  Created by mac on 2019/8/21.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import "YiChatConversationMenuView.h"
#import  "UIImageView+WebCache.h"

@interface YiChatConversationMenuView ()

@property (nonatomic,strong) NSArray <ZFMenuEntity *>*dataArr;

@property (nonatomic,assign) NSInteger numberOfRowNum;

@property (nonatomic,strong) NSValue *itemSize;

@property (nonatomic,assign) CGFloat blank;

@property (nonatomic,assign) CGFloat rightLeftBlank;

@property (nonatomic,strong) NSValue *iconItemSize;

@property (nonatomic,strong) NSValue *textItemSize;

@property (nonatomic,strong) UIScrollView *scroll;

@end

@implementation YiChatConversationMenuView

- (id)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if(self){
        [self systemConfig];
        
        [self addSubview:self.scroll];
        
        [self loadMenuData];
    }
    return self;
}

+ (id)createMenu{
    return [[self alloc] initWithFrame:CGRectMake(0, - 100.0, [UIScreen mainScreen].bounds.size.width, 100.0)];
}

- (UIScrollView *)scroll{
    if(!_scroll){
        _scroll = [[UIScrollView alloc] initWithFrame:CGRectMake(0, 0, self.frame.size.width, self.frame.size.height)];
        _scroll.bounces = YES;
    }
    return _scroll;
}

- (void)systemConfig{
    _blank = 10.0;
    _rightLeftBlank = 6.0;
    _numberOfRowNum = 4;
    
    [self textItemSize];
    [self iconItemSize];
    [self itemSize];
}

- (NSValue *)itemSize{
    if(!_itemSize){
        CGFloat w = self.textItemSize.CGSizeValue.width;
        CGFloat h = self.iconItemSize.CGSizeValue.height;
        
        _itemSize = [NSValue valueWithCGSize:CGSizeMake(w, h + self.textItemSize.CGSizeValue.height)];
    }
    return _itemSize;
}

- (NSValue *)iconItemSize{
    if(!_iconItemSize){
        CGFloat w = 40.0;
        _iconItemSize = [NSValue valueWithCGSize:CGSizeMake(w, w)];
    }
    return _iconItemSize;
}

- (NSValue *)textItemSize{
    if(!_textItemSize){
        CGFloat w = (self.frame.size.width - _rightLeftBlank * 2 - _blank * (_numberOfRowNum - 1)) / _numberOfRowNum;
        CGFloat h = 30.0;
        _textItemSize = [NSValue valueWithCGSize:CGSizeMake(w, h)];
    }
    return _textItemSize;
}

- (void)loadMenuData{
    [self downLoadData];
}

- (void)downLoadData {
    WS(weakSelf);
    [ProjectRequestHelper xcxWithParameters:@{} headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {

    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            if([obj isKindOfClass:[NSDictionary class]]){
                NSDictionary *dic = (NSDictionary *)obj;
                YiChatBassModel *model = [YiChatBassModel mj_objectWithKeyValues:obj];
                if (model.code == 0) {
                    id obj = dic[@"data"];
                    if([obj isKindOfClass:[NSArray class]]){
                        NSArray *dataArr = obj;
                        NSMutableArray *menuData = [NSMutableArray arrayWithCapacity:0];
                        for (int i = 0; i < dataArr.count; i ++) {
                            ZFMenuEntity *entity = [[ZFMenuEntity alloc] initWithDic:dataArr[i]];
                            [menuData addObject:entity];
                        }
                        weakSelf.dataArr = menuData;
                        
                        if(weakSelf.loadDataDoneBlock){
                            if (menuData.count == 0) {
                                weakSelf.loadDataDoneBlock(NO);
                            }else{
                                weakSelf.loadDataDoneBlock(YES);
                            }
                        }
                        dispatch_async(dispatch_get_main_queue(), ^{
                            [weakSelf refreshUI];
                        });
                    }

                } else{
                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:model.msg];
                }

            }else if([obj isKindOfClass:[NSString class]]){

                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
            }
        }];
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {

    }];
}

+ (void)downLoadDataWithDataArrs:(void(^)(NSArray *menus))dataBlock{
    WS(weakSelf);
    [ProjectRequestHelper xcxWithParameters:@{} headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:nil isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
        
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            if([obj isKindOfClass:[NSDictionary class]]){
                NSDictionary *dic = (NSDictionary *)obj;
                YiChatBassModel *model = [YiChatBassModel mj_objectWithKeyValues:obj];
                if (model.code == 0) {
                    id obj = dic[@"data"];
                    if([obj isKindOfClass:[NSArray class]]){
                        NSArray *dataArr = obj;
                        NSMutableArray *menuData = [NSMutableArray arrayWithCapacity:0];
                        for (int i = 0; i < dataArr.count; i ++) {
                            ZFMenuEntity *entity = [[ZFMenuEntity alloc] initWithDic:dataArr[i]];
                            [menuData addObject:entity];
                        }
                        dataBlock(menuData);
                    }
                    
                } else{
                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:model.msg];
                }
                
            }else if([obj isKindOfClass:[NSString class]]){
                
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
            }
            //                                [ProjectRequestHelper progressHidden:progress];
        }];
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
        
    }];
//    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
//    NSString *urlStr = [NSString stringWithFormat:@"%@%@", SERVER_HOST, @"api/getColumnList"];
//    [manager GET:urlStr parameters:nil success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
//        NSError *err;
//        NSDictionary *dic = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:&err];
//        if ([dic[@"code"] integerValue] == 1) {
//            id obj = dic[@"data"];
//            if([obj isKindOfClass:[NSArray class]]){
//                NSArray *dataArr = obj;
//                NSMutableArray *menuData = [NSMutableArray arrayWithCapacity:0];
//                for (int i = 0; i < dataArr.count; i ++) {
//                    ZFMenuEntity *entity = [[ZFMenuEntity alloc] initWithDic:dataArr[i]];
//                    [menuData addObject:entity];
//                }
//
//                data(menuData);
//            }
//
//        }
//    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
//        NSLog(@"请求错误");
//        data(nil);
//    }];
}

- (void)reloadDataWhenNoData{
    if(self.dataArr.count == 0){
        [self downLoadData];
    }
}


- (void)makeUI{
    for (id temp in self.scroll.subviews) {
        [temp removeFromSuperview];
    }
    
    CGFloat w = (self.itemSize.CGSizeValue.width) * self.dataArr.count + (self.dataArr.count + 1) * _blank;
    
    if(w < self.frame.size.width){
        w = self.frame.size.width;
    }
    self.scroll.frame = self.bounds;
    self.scroll.contentSize = CGSizeMake(w, self.frame.size.height);
    
    CGFloat x = _blank;
    CGFloat y = _rightLeftBlank;
    w = self.itemSize.CGSizeValue.width;
    CGFloat h = self.itemSize.CGSizeValue.height;
    
    self.userInteractionEnabled = YES;
    
    for (int i = 0; i < _dataArr.count; i ++) {
        UIView *back = [[UIView alloc] initWithFrame:CGRectMake(x + i * (w + _blank), y , w, h)];
        [self.scroll addSubview:back];
        back.userInteractionEnabled = YES;
        
        UIImageView *icon = [[UIImageView alloc] initWithFrame:CGRectMake(back.frame.size.width / 2 - self.iconItemSize.CGSizeValue.width / 2, 0, self.iconItemSize.CGSizeValue.width, self.iconItemSize.CGSizeValue.height)];
        [back addSubview:icon];
        
        [[SDWebImageDownloader sharedDownloader] setValue:@"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8" forHTTPHeaderField:@"Accept"];
        
        NSString * str1 = [_dataArr[i].icon stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
        
        NSURL *url = [NSURL URLWithString:str1];
        
        [icon sd_setImageWithURL:url];
        icon.layer.cornerRadius = icon.frame.size.height / 2;
        icon.clipsToBounds = YES;
        icon.userInteractionEnabled = NO;
        
        UILabel *lab = [[UILabel alloc] initWithFrame:CGRectMake(0, icon.frame.origin.y + icon.frame.size.height, self.textItemSize.CGSizeValue.width, self.textItemSize.CGSizeValue.height)];
        [back addSubview:lab];
        lab.font = [UIFont systemFontOfSize:12.0];
        lab.textAlignment = NSTextAlignmentCenter;
        lab.text = _dataArr[i].title;
        
        UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
        btn.frame = back.bounds;
        [back addSubview:btn];
        btn.tag = 1000000 + i;
        [btn addTarget:self action:@selector(jumpMethod:) forControlEvents:UIControlEventTouchUpInside];
    }
}

- (void)jumpMethod:(UIButton *)btn{
    NSInteger row = btn.tag - 1000000;
    if(self.zfConversationItemClick){
        self.zfConversationItemClick(_dataArr[row]);
    }
}

- (void)refreshUI{
    
    CGFloat h = self.iconItemSize.CGSizeValue.height + self.textItemSize.CGSizeValue.height + _blank * 2;
    
    self.frame = CGRectMake(0, - h, self.frame.size.width, h);
    
    //    if(_dataArr.count <= _numberOfRowNum){
    //        self.frame = CGRectMake(0, - h, self.frame.size.width, h);
    //    }
    //    else{
    //        if(_dataArr.count % _numberOfRowNum == 0){
    //            h = _dataArr.count / _numberOfRowNum * (self.itemSize.CGSizeValue.height + self.blank) + self.blank;
    //        }
    //        else{
    //            h = (_dataArr.count / _numberOfRowNum + 1) * (self.itemSize.CGSizeValue.height + self.blank) + self.blank;
    //        }
    //
    //        self.frame = CGRectMake(0, - h , self.frame.size.width, h);
    //    }
    
    [self makeUI];
}
/*
 // Only override drawRect: if you perform custom drawing.
 // An empty implementation adversely affects performance during animation.
 - (void)drawRect:(CGRect)rect {
 // Drawing code
 }
 */

@end

@implementation ZFMenuEntity

- (id)initWithDic:(NSDictionary *)dic{
    self = [super init];
    if(self){
        [self setValuesForKeysWithDictionary:dic];
    }
    return self;
}

- (void)setValue:(id)value forUndefinedKey:(NSString *)key{
    if([key isEqualToString:@"id"]){
        [self setValue:value forKey:@"itemId"];
    }
}

- (void)setNilValueForKey:(NSString *)key{
    
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
