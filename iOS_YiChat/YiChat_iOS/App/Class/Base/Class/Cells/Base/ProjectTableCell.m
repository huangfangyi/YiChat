//
//  ProjectTableCell.m
//  GSY
//
//  Created by 你是我的小呀小苹果 on 2019/3/14.
//  Copyright © 2019年 GSY. All rights reserved.
//

#import "ProjectTableCell.h"
#import "ProjectDef.h"
#import "ProjectHelper.h"


@implementation ProjectTableCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier indexPath:(NSIndexPath *)indexPath cellHeight:(NSNumber *)cellHeight cellWidth:(NSNumber *)cellWidth{
    self=[super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if(self){
        [self systemConfigureWithIndexPath:indexPath cellHeight:cellHeight cellWidth:cellWidth isHasDownLine:[NSNumber numberWithBool:NO] isHasRightArrow:[NSNumber numberWithBool:NO]];
    }
    return self;
}

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier indexPath:(NSIndexPath *)indexPath cellHeight:(NSNumber *)cellHeight cellWidth:(NSNumber *)cellWidth isHasDownLine:(NSNumber *)isHasDownLine{
    self=[super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if(self){
        [self systemConfigureWithIndexPath:indexPath cellHeight:cellHeight cellWidth:cellWidth isHasDownLine:isHasDownLine isHasRightArrow:[NSNumber numberWithBool:NO]];
    }
    return self;
}

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier indexPath:(NSIndexPath *)indexPath cellHeight:(NSNumber *)cellHeight cellWidth:(NSNumber *)cellWidth isHasRightArrow:(NSNumber *)isHasRightArrow{
    self=[super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if(self){
        [self systemConfigureWithIndexPath:indexPath cellHeight:cellHeight cellWidth:cellWidth isHasDownLine:[NSNumber numberWithBool:NO] isHasRightArrow:isHasRightArrow];
    }
    return self;
}

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier indexPath:(NSIndexPath *)indexPath cellHeight:(NSNumber *)cellHeight cellWidth:(NSNumber *)cellWidth isHasDownLine:(NSNumber *)isHasDownLine isHasRightArrow:(NSNumber *)isHasRightArrow{
    self=[super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if(self){
        [self systemConfigureWithIndexPath:indexPath cellHeight:cellHeight cellWidth:cellWidth isHasDownLine:isHasDownLine isHasRightArrow:isHasRightArrow];
    }
    return self;
}

#pragma mark system Configure

- (void)systemConfigureWithIndexPath:(NSIndexPath *)indexPath cellHeight:(NSNumber *)cellHeight cellWidth:(NSNumber *)cellWidth isHasDownLine:(NSNumber *)isHasDownLine isHasRightArrow:(NSNumber *)isHasRightArrow{
    
    _sIndexPath = indexPath;
    _sIsHasDownline= isHasDownLine.boolValue;
    _sIsHasRightArrow=isHasRightArrow.boolValue;
    _sCellHeight=cellHeight.floatValue;
    _sCellWidth =cellWidth.floatValue;
}


#pragma mark public

- (void)updateSystemConfigWithIndexPath:(NSIndexPath *)indexPaths arrow:(NSNumber *)isHasArrows downLine:(NSNumber *)isHasDownLines cellHeight:(NSNumber *)cellHeight{
    _sIndexPath=indexPaths;
    _sIsHasDownline=isHasDownLines.boolValue;
    _sIsHasRightArrow=isHasArrows.boolValue;
    _sCellHeight=cellHeight.floatValue;
    
    [self drawDownLine];
    [self drawRightArrow];
}

#pragma mark private

- (void)drawDownLine{
    if(_sIsHasDownline==YES){
        [self.sCDownLine removeFromSuperview];
        self.sCDownLine=nil;
        
        [self makeLineUI];
    }
    else{
        [self.sCDownLine removeFromSuperview];
        self.sCDownLine=nil;
    }
}

- (void)drawRightArrow{
    if(_sIsHasRightArrow==YES){
        [self.sCRightArrow removeFromSuperview];
        self.sCRightArrow=nil;
        
        [self makeArrowUI];
    }
    else{
        [self.sCRightArrow removeFromSuperview];
        self.sCRightArrow=nil;
    }
}

- (void)makeLineUI{
    UIView *line=[ProjectHelper helper_factoryMakeHorizontalLineWithPoint:CGPointMake(0, _sCellHeight) width:PROJECT_SIZE_WIDTH];
    [self.contentView addSubview:line];
    self.sCDownLine=line;
}

- (void)makeArrowUI{
    UIImage *arrowImg = [UIImage imageNamed:Project_Icon_rightGrayArrow];
    
    if(arrowImg && [arrowImg isKindOfClass:[UIImage class]]){
        CGSize size=arrowImg.size;
        
        CGFloat h=[ProjectHelper helper_getScreenSuitable_H:10.0];
        
        CGFloat w=[ProjectHelper helper_GetWidthOrHeightIntoScale:size.width / size.height width:0 height:h];
        
        UIImageView *img=[ProjectHelper helper_factoryMakeImageViewWithFrame:CGRectMake(PROJECT_SIZE_WIDTH - PROJECT_SIZE_NAV_BLANK  - w, _sCellHeight / 2 - h / 2, w, h) andImg:arrowImg];
        [self.contentView addSubview:img];
        self.sCRightArrow=img;
    }
   
}

- (CGRect)getRightArrowSize{
    return self.sCRightArrow.frame;
    
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
