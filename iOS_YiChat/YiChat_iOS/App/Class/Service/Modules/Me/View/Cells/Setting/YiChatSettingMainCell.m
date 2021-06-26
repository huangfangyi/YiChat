//
//  YiChatSettingMainCell.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/4.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatSettingMainCell.h"
#import "ServiceGlobalDef.h"
#import "ProjectCommonCellModel.h"
#import <JPush/JPUSHService.h>
#import "YiChatServiceClient.h"
@interface YiChatSettingMainCell ()
{
    NSInteger _type;
}

@property (nonatomic,strong) UILabel *title;
@property (nonatomic,strong) UILabel *content;

@property (nonatomic,strong) UISwitch *switchControl;

@property (nonatomic,assign) BOOL switchState;

@end

@implementation YiChatSettingMainCell


+ (id)initialWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier indexPath:(NSIndexPath *)indexPath cellHeight:(NSNumber *)cellHeight cellWidth:(NSNumber *)cellWidth isHasDownLine:(NSNumber *)isHasDownLine type:(NSInteger)type{
    return [[self alloc] initWithStyle:style reuseIdentifier:reuseIdentifier indexPath:indexPath cellHeight:cellHeight cellWidth:cellWidth isHasDownLine:isHasDownLine type:type];
}

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier indexPath:(NSIndexPath *)indexPath cellHeight:(NSNumber *)cellHeight cellWidth:(NSNumber *)cellWidth isHasDownLine:(NSNumber *)isHasDownLine type:(NSInteger)type{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier indexPath:indexPath cellHeight:cellHeight cellWidth:cellWidth isHasDownLine:isHasDownLine];
    if(self){
        _type = type;
        
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        
        [self makeUI];
    }
    return self;
}

- (void)updateType:(NSInteger)type{
    _type = type;
}

- (void)makeUI{

    [self makeUIForCommon];
    
    if(_type == 0){
        [self makeUIWithType_0];
    }
    else if(_type == 1){
        [self makeUIWithType_1];
    }
    
}

- (void)makeUIForCommon{
    CGFloat x = PROJECT_SIZE_NAV_BLANK;
    CGFloat y = 0;
    CGFloat w = (self.sCellWidth - x * 2) / 2;
    CGFloat h = self.sCellHeight;
    
    
    UILabel *name = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(x , y, w, h) andfont:PROJECT_TEXT_FONT_COMMON(15) textColor:PROJECT_COLOR_TEXTCOLOR_BLACK textAlignment:NSTextAlignmentLeft];
    [self.contentView addSubview:name];
    _title = name;
}

- (void)makeUIWithType_0{
    
    CGFloat x = self.sCellWidth - PROJECT_SIZE_NAV_BLANK - _title.frame.size.width;
    CGFloat y = 0;
    CGFloat w = _title.frame.size.width;
    CGFloat h = self.sCellHeight;
    
    UILabel *content = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(x , y, w, h) andfont:PROJECT_TEXT_FONT_COMMON(14) textColor:PROJECT_COLOR_TEXTGRAY textAlignment:NSTextAlignmentRight];
    [self.contentView addSubview:content];
    _content = content;
    

}


- (void)makeUIWithType_1{
    CGFloat w = 45.0;
    CGFloat h = 30.0;
    CGFloat x = self.sCellWidth - PROJECT_SIZE_NAV_BLANK - w;
    CGFloat y = self.sCellHeight / 2 - h / 2;
    
    
    UISwitch *switchClick = [[UISwitch alloc] initWithFrame:CGRectMake(x, y, w, h )];
    switchClick.selected = NO;
    [switchClick addTarget:self action:@selector(valueChanged:) forControlEvents:UIControlEventValueChanged];
    [self.contentView addSubview:switchClick];
    _switchControl = switchClick;
    _switchState = NO;
}

- (void)valueChanged:(UISwitch *)switchBtn{
    _switchState = !_switchState;
    
    NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
    [user setObject:[NSString stringWithFormat:@"%d",_switchState] forKey:PROJECT_GLOBALNODISTURB];
    [user synchronize];
    
    if (_switchState) {
        [JPUSHService cleanTags:nil seq:1];
        [JPUSHService deleteAlias:nil seq:1];
    }else{
        [JPUSHService setAlias:YiChatUserInfo_UserIdStr completion:nil seq:1];
        YiChatServiceClient *client = [YiChatServiceClient defaultChatClient];
        [client setJGJushTagWithGroupAdd:YES];
    }
}

- (void)setCellModel:(ProjectCommonCellModel *)cellModel{
    if([cellModel isKindOfClass:[ProjectCommonCellModel class]]){
        _cellModel = cellModel;
        
        
        if([cellModel.titleStr isKindOfClass:[NSString class]]){
            _title.text = cellModel.titleStr;
        }
        if([cellModel.contentStr isKindOfClass:[NSString class]] && cellModel.contentStr){
            _content.text = cellModel.contentStr;
        }

    }
}

- (void)setSwitch{
    NSString *state = [[NSUserDefaults standardUserDefaults] objectForKey:PROJECT_GLOBALNODISTURB];
    
    if(state && [state isKindOfClass:[NSString class]]){
        _switchState = [state boolValue];
        [_switchControl setOn:_switchState animated:YES];
    }
}

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
