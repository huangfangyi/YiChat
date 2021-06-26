//
//  ZFEmojiListView.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/30.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ZFEmojiListView.h"
#import "ZFChatGlobal.h"
#import "ProjectUIHelper.h"


#define XYEmojiListView_DEFALUT_EMOJ_PERROWSNUM 8
#define XYEmojiListView_ADD_EMOJ_PERROWSNUM 5

@interface ZFEmojiListView ()<UITableViewDelegate,UITableViewDataSource>

@property (nonatomic,copy) HelperObjFlagInvocation selecteEmoji;

@end

@implementation ZFEmojiListView

- (id)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if(self){
        
        
    }
    return self;
}

- (void)makeUI{
    [self makeTableView];
}


- (void)makeTableView{
    if([self.identifier isEqualToString:@"addEmoji"]){
        NSInteger rowsNum = [self getAddEmojiNumberRows];
           self.sectionsRowsNumSet = @[[NSNumber numberWithInteger:rowsNum]];
           self.cellH = self.frame.size.height / 2;
           
           self.cTable.backgroundColor = [UIColor whiteColor];
           [self addSubview:self.cTable];
    }
    else{
        NSInteger rowsNum = [self getDefaultEmojiNumberRows];
           self.sectionsRowsNumSet = @[[NSNumber numberWithInteger:rowsNum]];
           self.cellH = self.frame.size.height / 3;
           
           self.cTable.backgroundColor = [UIColor whiteColor];
           [self addSubview:self.cTable];
    }
}

- (void)addSelecteEmojiInvocation:(NSDictionary *)invocattion{
    if(invocattion[@"click"]){
        self.selecteEmoji = invocattion[@"click"];
    }
}

- (void)updateTable{
     if([self.identifier isEqualToString:@"addEmoji"]){
         self.cellH = self.frame.size.height / 2;
     }
     else{
         self.cellH = self.frame.size.height / 3;
     }
    [self.cTable reloadData];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    ZFEmojiViewCell *cell = nil;
    
    WS(weakSelf);
    if([self.identifier isEqualToString:@"defaultEmoji"]){
        static NSString *str = @"XYEmojiView_DefaultEmoji";
        cell = [self getTableViewCellWithTable:tableView type:0 reuseStr:str];
        
        HelperIntergeFlagInvocation click = ^void(NSInteger row){
            NSLog(@"%ld",row);
            if(weakSelf.selecteEmoji){
                weakSelf.selecteEmoji ([NSNumber numberWithInteger:indexPath.row * XYEmojiListView_DEFALUT_EMOJ_PERROWSNUM + row]);
            }
        };
        
        [cell addInvocation:@{@"click":click}];
        
        NSInteger begin = indexPath.row * XYEmojiListView_DEFALUT_EMOJ_PERROWSNUM;
        
        
        NSMutableArray *arr = [NSMutableArray arrayWithCapacity:0];
        
        for (NSInteger i = begin; i< begin + XYEmojiListView_DEFALUT_EMOJ_PERROWSNUM; i++) {
            if(_dataSourceArr.count - 1 >= i){
                [arr addObject:_dataSourceArr[i]];
            }
        }
        [cell getDataWithArr:arr];
        
        return cell;
    }
    else{
        //addEmoji
        static NSString *str = @"XYEmojiView_AddEmoji";
        cell = [self getTableViewCellWithTable:tableView type:1 reuseStr:str];
        
        HelperIntergeFlagInvocation click = ^void(NSInteger row){
                   NSLog(@"%ld",row);
                   if(weakSelf.selecteEmoji){
                       weakSelf.selecteEmoji ([NSNumber numberWithInteger:indexPath.row * XYEmojiListView_ADD_EMOJ_PERROWSNUM + row]);
                   }
               };
               
        [cell addInvocation:@{@"click":click}];
        
        NSInteger begin = indexPath.row * XYEmojiListView_ADD_EMOJ_PERROWSNUM;
        
        
        NSMutableArray *arr = [NSMutableArray arrayWithCapacity:0];
        
        for (NSInteger i = begin; i< begin + XYEmojiListView_ADD_EMOJ_PERROWSNUM; i++) {
            if(_dataSourceArr.count - 1 >= i){
                [arr addObject:_dataSourceArr[i]];
            }
        }
        [cell getDataWithArr:arr];
        return cell;
    }
}

- (ZFEmojiViewCell *)getTableViewCellWithTable:(UITableView *)table type:(NSInteger)type reuseStr:(NSString *)reuseStr{
    ZFEmojiViewCell *cell = [table dequeueReusableCellWithIdentifier:reuseStr];
    if(!cell){
        cell = [[ZFEmojiViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:reuseStr type:type];
        
        [cell createUI];
    }
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    return cell;
}

- (CGFloat)projectTableViewController_CellHWithIndex:(NSIndexPath *)index{
    return self.cellH;
}

- (CGFloat)XYTableView_SectionHWithIndex:(NSInteger)section{
    return 0.00001;
}

- (CGFloat)XYTableView_FooterHWithIndex:(NSInteger)section{
    return 0.000001;
}

- (CGFloat)getDefaultEmojiNumberRows{
    if(_dataSourceArr.count % XYEmojiListView_DEFALUT_EMOJ_PERROWSNUM ==0){
        return (_dataSourceArr.count / XYEmojiListView_DEFALUT_EMOJ_PERROWSNUM);
    }
    else{
        return (_dataSourceArr.count / XYEmojiListView_DEFALUT_EMOJ_PERROWSNUM) + 1;
    }
}

- (CGFloat)getAddEmojiNumberRows{
    NSInteger count =  self.dataSourceArr.count;
    
    if(count % XYEmojiListView_ADD_EMOJ_PERROWSNUM ==0){
        return (count / XYEmojiListView_ADD_EMOJ_PERROWSNUM);
    }
    else{
        return (count / XYEmojiListView_ADD_EMOJ_PERROWSNUM) + 1;
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


@interface ZFEmojiViewCell ()

@property (nonatomic) NSInteger type;

@property (nonatomic,copy) HelperIntergeFlagInvocation click;

@property (nonatomic,strong) NSArray <ZFDefaultEmojiUI *>*emojiUIArr;

@end

@implementation ZFEmojiViewCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier type:(NSInteger)type{
    self =[super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if(self){
        _type = type;
    }
    return self;
}

- (void)createUI{
    if(_type == 0){
        [self makeUIForDefaultEmoji];
    }
    else if(_type == 1){
        [self makeUIForAddEmoji];
    }
}

- (void)makeUIForDefaultEmoji{
    CGFloat x= [self getEmojiBlankW];
    CGFloat y = [self getEmojiBlankW];
    NSInteger perRowAppearNum = XYEmojiListView_DEFALUT_EMOJ_PERROWSNUM;
    CGFloat defaultEmojW = (PROJECT_SIZE_WIDTH - x * (perRowAppearNum - 1 + 2)) / perRowAppearNum;
    CGFloat defaultEmojH = defaultEmojW;
    
    WS(weakSelf);
    NSMutableArray * uiArr = [NSMutableArray arrayWithCapacity:0];
    
    for (int i = 0; i < XYEmojiListView_DEFALUT_EMOJ_PERROWSNUM; i ++) {
        HelperIntergeFlagInvocation invocation = ^void(NSInteger row){
            weakSelf.click(row);
        };
        
        ZFDefaultEmojiUI *emojiUI = [[ZFDefaultEmojiUI alloc] initWithFrame:CGRectMake(x + i * (x + defaultEmojW),y , defaultEmojW, defaultEmojH) image:nil num:i click:@{@"click":invocation}];
        [emojiUI makeUI];
        [self.contentView addSubview:emojiUI];
        [uiArr addObject:emojiUI];
    }
    _emojiUIArr = uiArr;
}

- (void)makeUIForAddEmoji{
    
    CGFloat x= [self getEmojiBlankW];
    CGFloat y = [self getEmojiBlankW];
    NSInteger perRowAppearNum = XYEmojiListView_ADD_EMOJ_PERROWSNUM;
    CGFloat defaultEmojW = (PROJECT_SIZE_WIDTH - x * (perRowAppearNum - 1 + 2)) / perRowAppearNum;
    CGFloat defaultEmojH = defaultEmojW;
    
    WS(weakSelf);
    NSMutableArray * uiArr = [NSMutableArray arrayWithCapacity:0];
    
    for (int i = 0; i < XYEmojiListView_ADD_EMOJ_PERROWSNUM; i ++) {
        HelperIntergeFlagInvocation invocation = ^void(NSInteger row){
            weakSelf.click(row);
        };
        
        ZFDefaultEmojiUI *emojiUI = [[ZFDefaultEmojiUI alloc] initWithFrame:CGRectMake(x + i * (x + defaultEmojW),y , defaultEmojW, defaultEmojH) image:nil num:i click:@{@"click":invocation}];
        [emojiUI makeUI];
        [self.contentView addSubview:emojiUI];
        [uiArr addObject:emojiUI];
    }
    _emojiUIArr = uiArr;
    
}

- (void)getDataWithArr:(NSArray *)arr{
    for (int i = 0; i < _emojiUIArr.count; i++) {
        if(arr.count - 1 >= i){
            ZFDefaultEmojiUI *ui = _emojiUIArr[i];
            ui.defaultIcon.image = arr[i];
            ui.hidden = NO;
        }
        else{
            ZFDefaultEmojiUI *ui = _emojiUIArr[i];
            ui.hidden = YES;
        }
    }
}

- (void)addInvocation:(NSDictionary *)invocattion{
    if(invocattion[@"click"]){
        self.click = invocattion[@"click"];
    }
}


- (CGFloat)downBlankH{
    return 5.0;
}

- (CGFloat)getEmojiBlankW{
    return PROJECT_SIZE_NAV_BLANK;
}
@end


@interface ZFDefaultEmojiUI ()
{
    UIImage *_icon;
    NSInteger _num;
}
@property (nonatomic,copy) HelperIntergeFlagInvocation click;
@end

@implementation ZFDefaultEmojiUI

- (id)initWithFrame:(CGRect)frame image:(UIImage *)img num:(NSInteger)num click:(NSDictionary *)clickInvocatiton{
    self = [super initWithFrame:frame];
    if(self){
        _icon = img;
        _num = num;
        if(clickInvocatiton[@"click"]){
            self.click = clickInvocatiton[@"click"];
        }
    }
    return self;
}

- (void)makeUI{
    UIImageView *icon =[ProjectHelper helper_factoryMakeImageViewWithFrame:CGRectMake(0, 0, self.frame.size.width, self.frame.size.height) andImg:_icon];
    _defaultIcon = icon;
    [self addSubview:icon];
    
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] init];
    [tap addTarget:self action:@selector(tapMethod:)];
    [self addGestureRecognizer:tap];
}

- (void)tapMethod:(UITapGestureRecognizer *)tap{
    if(self.click){
        self.click(_num);
    }
}

@end


@interface ZFAddeMojiUI ()
{
    
}
@property (nonatomic,strong) UIImage *icon;
@property (nonatomic) NSInteger num;
@property (nonatomic) BOOL selecteState;
@property (nonatomic) BOOL editeState;
@property (nonatomic,copy) HelperIntergeBoolFlagInvocation click;

@end

#define XYAddeMojiUI_ADDEMOJI_MAX_H [self getMaxHeight]
#define XYAddeMojiUI_ADDEMOJI_MAX_W [self getMaxWidth]
@implementation ZFAddeMojiUI

- (id)initWithFrame:(CGRect)frame image:(UIImage *)img num:(NSInteger)num click:(NSDictionary *)clickInvocatiton{
    self = [super initWithFrame:frame];
    if(self){
        _icon = img;
        _num = num;
        _selecteState = NO;
        _editeState = NO;
        if(clickInvocatiton[@"click"]){
            self.click = clickInvocatiton[@"click"];
        }
    }
    return self;
}
- (void)changeSelecteState:(BOOL)state{
    _selecteState = state;
    _addIcon.image = nil;
}

- (void)changeEditeState:(BOOL)state{
    if(state == NO){
        _addIcon.hidden = YES;
    }
    else{
        _addIcon.hidden = NO;
        _selecteState = NO;
        [self changeSelecteState:_selecteState];
    }
}

- (void)makeUI{
    if(_icon == nil){
        _icon = [UIImage imageNamed:@""];
    }
    
    CGFloat w ;
    CGFloat h ;
    CGFloat maxH = XYAddeMojiUI_ADDEMOJI_MAX_H;
    CGFloat maxW  = XYAddeMojiUI_ADDEMOJI_MAX_W;
    CGFloat scale = _icon.size.width / _icon.size.height;
    
    
    h = maxH;
    w = [ProjectHelper helper_GetWidthOrHeightIntoScale:scale width:0 height:h];
    if(w > maxW){
        w = maxW ;
        h = [ProjectHelper helper_GetWidthOrHeightIntoScale:scale width:w height:0];
        if(h >=maxH){
            h = maxH;
        }
    }
    
    UIImageView *img = [ProjectHelper helper_factoryMakeImageViewWithFrame:CGRectMake(self.frame.size.width / 2 - w / 2, self.frame.size.height / 2 - h / 2, w, h) andImg:_icon];
    [self addSubview:img];
    
    UIButton *btn = [ProjectHelper helper_factoryMakeClearButtonWithFrame:self.bounds target:self method:@selector(clearBtnMethod:)];
    [self addSubview:btn];
    
    UIImage *selecte = [UIImage imageNamed:@""];
    w = 30.0;
    h = [ProjectHelper helper_GetWidthOrHeightIntoScale:selecte.size.width / selecte.size.height width:w height:0];
    
    _addIcon = [ProjectHelper helper_factoryMakeImageViewWithFrame:CGRectMake(self.frame.size.width - w, self.frame.size.height - h, w , h) andImg:selecte];
    [self addSubview:_addIcon];
    if(_editeState == NO){
        _addIcon.hidden = YES;
    }
    else{
        _addIcon.hidden = NO;
    }
}

- (void)clearBtnMethod:(UIButton *)btn{
    WS(weakSelf);
    if(self.click){
        weakSelf.selecteState = !weakSelf.selecteState;
        [self changeSelecteState:weakSelf.selecteState];
        self.click(weakSelf.num,weakSelf.selecteState);
    }
}


#pragma mark  get


- (CGFloat)getMaxWidth{
    return self.frame.size.width - 5.0;
}

- (CGFloat)getMaxHeight{
    return self.frame.size.height - 5.0;
}


/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end

