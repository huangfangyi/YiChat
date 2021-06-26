//
//  YiChatDynamicCell.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/8/13.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatDynamicCell.h"
#import "ServiceGlobalDef.h"
#import "YiChatDynamicUIConfigure.h"
#import "YiChatDynamicDataSource.h"

@interface YiChatDynamicCell ()

@property (nonatomic,assign) NSInteger type;

@property (nonatomic,weak) YiChatDynamicUIConfigure *uiConfigure;

@property (nonatomic,strong) UIView *back;

@property (nonatomic,strong) UILabel *commitContent;

@property (nonatomic,strong) UILongPressGestureRecognizer *contentLongGes;

@end

@implementation YiChatDynamicCell

+ (id)initialWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier indexPath:(NSIndexPath *)indexPath cellHeight:(NSNumber *)cellHeight cellWidth:(NSNumber *)cellWidth type:(NSInteger)type{
    return [[self alloc] initWithStyle:style reuseIdentifier:reuseIdentifier indexPath:indexPath cellHeight:cellHeight cellWidth:cellWidth type:type];
    
}

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier indexPath:(NSIndexPath *)indexPath cellHeight:(NSNumber *)cellHeight cellWidth:(NSNumber *)cellWidth type:(NSInteger)type {
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier indexPath:indexPath cellHeight:cellHeight cellWidth:cellWidth];
    if(self){
        _type = type;
        _uiConfigure = [YiChatDynamicUIConfigure initialUIConfigure];
        
        self.contentView.backgroundColor = [UIColor whiteColor];
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        
        [self makeUI];
    }
    return self;
}

- (void)makeUI{
   
    _back = [UIView new];
    _back.backgroundColor = PROJECT_COLOR_APPBACKCOLOR;
    [self.contentView addSubview:_back];
    _back.layer.cornerRadius = 5.0;
    _back.clipsToBounds = YES;
    
    _commitContent = [UILabel new];
    _commitContent.textAlignment = NSTextAlignmentLeft;
    [_back addSubview:_commitContent];
    _commitContent.font = _uiConfigure.dynamicCommitFont;
    _commitContent.numberOfLines = 0;
    _commitContent.userInteractionEnabled = YES;
    
    [_back addGestureRecognizer:self.contentLongGes];
    
}

- (UILongPressGestureRecognizer *)contentLongGes{
    if(!_contentLongGes){
        UILongPressGestureRecognizer *longPress = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(longPressMethod:)];
        longPress.minimumPressDuration = 1;
        _contentLongGes = longPress;
    }
    return _contentLongGes;
}

- (void)longPressMethod:(UILongPressGestureRecognizer *)longPress{
    if(longPress.state == UIGestureRecognizerStateBegan){
        if(self.YiChatDynamicLongPress){
            [self becomeFirstResponder];
            self.YiChatDynamicLongPress(_model, _index);
        }
    }
}

- (UIView *)getCellBack{
    return self.commitContent;
}

- (void)setModel:(YiChatDynamicDataSource *)model index:(NSIndexPath *)index{
    if(model && [model isKindOfClass:[YiChatDynamicDataSource class]]){
        _model = model;
        _index = index;
        
        NSArray *commentArr = _model.showCommentListStrArr;
        NSArray *commentRectArr = _model.showCommentStrRectArr;
        
        if(commentArr && [commentArr isKindOfClass:[NSArray class]] && commentRectArr && [commentRectArr isKindOfClass:[NSArray class]]){
            if(commentArr.count == commentRectArr.count){
                if(commentArr.count - 1 >= index.row){
                    NSAttributedString *content = commentArr[index.row];
                    CGRect rect = [commentRectArr[index.row] CGRectValue];
                    
                    CGFloat x = _uiConfigure.userIconRect.origin.x + _uiConfigure.userIconRect.size.width + _uiConfigure.contentBlank;
                    CGFloat y = 0;
                    
                    _back.frame = CGRectMake(x, y, _uiConfigure.contentMaxSize, self.sCellHeight);
                    
                    _commitContent.frame = CGRectMake((_uiConfigure.contentMaxSize - _uiConfigure.commitMaxSize) / 2, 5.0, rect.size.width, rect.size.height);
                    
                    if(content && [content isKindOfClass:[NSAttributedString class]]){
                         _commitContent.attributedText = content;
                    }
                }
            }
        }
    }
}

- (BOOL)canBecomeFirstResponder{
    return YES;
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
