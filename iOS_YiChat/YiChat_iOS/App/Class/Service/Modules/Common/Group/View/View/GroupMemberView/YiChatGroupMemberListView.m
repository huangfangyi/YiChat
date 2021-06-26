//
//  YiChatGroupMemberListView.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/25.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatGroupMemberListView.h"
#import "YiChatGroupMemberListModel.h"
#import "ServiceGlobalDef.h"
#import "YiChatGroupMemberSingleCell.h"
#import "ProjectClickView.h"
#import "YiChatUserManager.h"

@interface YiChatGroupMemberListView ()<UIScrollViewDelegate>
{
    
}
@property (nonatomic,strong) NSMutableArray *dataSoureArr;

@property (nonatomic,strong) NSMutableArray *memberListArr;

@property (nonatomic,assign) BOOL isHasAddPower;

@property (nonatomic,assign) BOOL isHasDeletePower;

@property (nonatomic,assign) BOOL isLoadAll;

@property (nonatomic,assign) BOOL isShowLoadMoreMemberBtn;

@property (nonatomic,assign) NSInteger numOfLine;

@property (nonatomic,assign) CGFloat lineSpace;

@property (nonatomic,assign) CGFloat interSpace;

@property (nonatomic,assign) CGSize itemSize;

@property (nonatomic,assign) CGFloat magin;

@property (nonatomic,strong) ProjectClickView *clickMoreMember;
    
@property (nonatomic,strong) UIMenuController *menuVC;
    
@property (nonatomic, strong) UIMenuItem *shutUpMenuItem;
    
@property (nonatomic, strong) UIMenuItem *searchInfoMenuItem;
    
@property (nonatomic, strong) NSIndexPath *menuIndex;

@end

static NSString *cellIdentify = @"YiChatGroupMemberListViewCell";
static NSString *cellIUserInfodentify = @"YiChatGroupMemberListViewCellUserInfo";

#define YiChatGroupMemberListView_showNum 8
#define YiChatGroupMemberListView_showMoreBtnH 25.0f

@implementation YiChatGroupMemberListView

- (id)initWithFrame:(CGRect)frame datasoRerces:(NSArray *)dataSource isHasAdd:(BOOL)isHasAdd isHasDelete:(BOOL)isHasDelete isLoadAll:(BOOL)isLoadAll{
    self = [super initWithFrame:frame];
    if(self){
        
        _isHasAddPower = isHasAdd;
        _isHasDeletePower = isHasDelete;
        _isLoadAll = isLoadAll;
        _dataSoureArr = [NSMutableArray arrayWithCapacity:0];
        _isShowLoadMoreMemberBtn = NO;
        
        if(dataSource && [dataSource isKindOfClass:[NSArray class]]){
            
            [_dataSoureArr addObjectsFromArray:dataSource];
            [_memberListArr addObjectsFromArray:dataSource];
            
            if(_dataSoureArr.count >= YiChatGroupMemberListView_showNum && !_isLoadAll){
                _isShowLoadMoreMemberBtn = YES;
            }
        }
        
        [self configureData];
        
        [self makeUI];
        
    }
    return self;
}


- (void)configureData{
  
    [self insertAddModel];
    [self insertDeleteModel];
}

- (void)changeDataSource:(NSArray *)dataSource{
    if(dataSource && [dataSource isKindOfClass:[NSArray class]]){
        
        [_dataSoureArr removeAllObjects];
        [_memberListArr removeAllObjects];
        
        [_dataSoureArr addObjectsFromArray:dataSource];
        [_memberListArr addObjectsFromArray:dataSource];
        
        _isShowLoadMoreMemberBtn = NO;
        if(_dataSoureArr.count >= YiChatGroupMemberListView_showNum && !_isLoadAll){
            _isShowLoadMoreMemberBtn = YES;
        }
    }
}

- (void)changeIsHasAdd:(BOOL)hasAdd isHasDelete:(BOOL)hasDelete{
    _isHasAddPower = hasAdd;
    _isHasDeletePower = hasDelete;
}

- (void)updateAddDeleteUIData{
    BOOL hasAdd = _isHasAddPower;
    BOOL hasDelete = _isHasDeletePower;
    
    __block BOOL isHasAdd = NO;
    __block BOOL isHasdelete = NO;
    
    __block NSInteger addNum = -1;
    __block NSInteger deleteNum = -1;
    
    WS(weakSelf);
    
    for (int i = 0; i < _dataSoureArr.count; i ++) {
        YiChatGroupMemberListModel *model = weakSelf.dataSoureArr[i];
        if(model && [model isKindOfClass:[YiChatGroupMemberListModel class]]){
            if(model.type == 1){
                isHasAdd = YES;
                if((self.dataSoureArr.count - 1) >= i && i >= 0){
                    [self.dataSoureArr removeObjectAtIndex:i];
                }
                i --;
            }
            if(model.type == 0){
                isHasdelete = YES;
                deleteNum = i;
                if((self.dataSoureArr.count - 1) >= i && i >= 0){
                    [self.dataSoureArr removeObjectAtIndex:i];
                }
                i --;
            }
        }
    }
    
    //需要插入
    if(hasAdd){
        [self insertAddModel];
    }
    if(hasDelete){
        [self insertDeleteModel];
    }
   
    NSLog(@"%@",self.dataSoureArr);
    
    [self layoutSubview];
}

- (void)insertAddModel{
    YiChatGroupMemberListModel *add = [self getAddModel];
    
    if(add &&  [add isKindOfClass:[YiChatGroupMemberListModel class]]){
        
        if(!_isLoadAll){
            if(_dataSoureArr.count <= (YiChatGroupMemberListView_showNum )){
                 [_dataSoureArr addObject:add];
            }
            else if( _dataSoureArr.count > YiChatGroupMemberListView_showNum){
                 [_dataSoureArr insertObject:add atIndex:YiChatGroupMemberListView_showNum];
            }
            else{
                 [_dataSoureArr addObject:add];
            }
        }
        else{
            [_dataSoureArr addObject:add];
        }
    }
}

- (void)insertDeleteModel{
    
    YiChatGroupMemberListModel *delete = [self getDeleteModel];
    
    if(delete &&  [delete isKindOfClass:[YiChatGroupMemberListModel class]]){
        if(!_isLoadAll){
            if(_dataSoureArr.count <=  (YiChatGroupMemberListView_showNum + 1)){
                [_dataSoureArr addObject:delete];
            }
            else if(_dataSoureArr.count > (YiChatGroupMemberListView_showNum + 1)){
                [_dataSoureArr insertObject:delete atIndex:YiChatGroupMemberListView_showNum + 1];
            }
            else{
                 [_dataSoureArr addObject:delete];
            }
        }
        else{
            [_dataSoureArr addObject:delete];
        }
    }
}

- (YiChatGroupMemberListModel *)getAddModel{
    if(_isHasAddPower){
        YiChatGroupMemberListModel *cellModel = [[YiChatGroupMemberListModel alloc] init];
        
        cellModel.iconUrl = @"tianjia@3x.png";
        
        cellModel.type = 1;
        
        return cellModel;
    }
    return nil;
}

- (YiChatGroupMemberListModel *)getDeleteModel{
    if(_isHasDeletePower){
        YiChatGroupMemberListModel *cellModel = [[YiChatGroupMemberListModel alloc] init];
        
        cellModel.iconUrl = @"shanchu@3x.png";
        
        cellModel.type = 0;
        
        return cellModel;
    }
    return nil;
}

- (void)makeUI{
    
    WS(weakSelf);
    
    _numOfLine = 5;
    _magin = 10.0;
    _lineSpace = 10.0;
    _interSpace = 10.0;
    
    CGFloat magin = _magin;
    //设置行与行之间的间距最小距离
    CGFloat lineSpace = _lineSpace;
    CGFloat interalSpace = _interSpace;
    
    CGFloat w = (self.frame.size.width - magin * 2 - interalSpace * (_numOfLine - 1)) / _numOfLine;
    CGFloat h = w;
  
    _itemSize = CGSizeMake(w, h);
    
    [self setProjectCollectionViewNumItem:^NSUInteger(UICollectionView * _Nonnull collection, NSUInteger section) {
        if(weakSelf.isShowLoadMoreMemberBtn){
            if(weakSelf.dataSoureArr.count >= YiChatGroupMemberListView_showNum + 2){
                return YiChatGroupMemberListView_showNum + 2;
            }
        }
        return weakSelf.dataSoureArr.count;
    }];
    
    [self setProjectCollectionViewNumSection:^NSUInteger(UICollectionView * _Nonnull collection) {
        return 1;
    }];
    
    [self setProjectCollectionViewItemSize:^CGSize(UICollectionView * _Nonnull collection, UICollectionViewLayout * _Nonnull layout, NSIndexPath * _Nonnull index) {
        return CGSizeMake(w, h);
    }];
    
    [self setProjectCollectionViewItemInset:^UIEdgeInsets(UICollectionView * _Nonnull collection, UICollectionViewLayout * _Nonnull layout, NSInteger section) {
        
        return UIEdgeInsetsMake(magin, magin, magin, magin);
    }];
    
    [self setProjectCollectionViewItem:^UICollectionViewCell * _Nonnull(UICollectionView * _Nonnull collection, NSIndexPath * _Nonnull index) {
        
        YiChatGroupMemberSingleCell *cell = nil;
        if((weakSelf.dataSoureArr.count - 1) >= index.row){
            id model = weakSelf.dataSoureArr[index.row];
            if([model isKindOfClass:[YiChatGroupMemberListModel class]] && model){
                cell = [collection dequeueReusableCellWithReuseIdentifier:cellIdentify forIndexPath:index];
                cell.model = model;
            }
            else if([model isKindOfClass:[YiChatUserModel class]] && model){
                cell = [collection dequeueReusableCellWithReuseIdentifier:cellIUserInfodentify forIndexPath:index];
                cell.userModel = model;
            }
        }
        if(cell && [cell isKindOfClass:[YiChatGroupMemberSingleCell class]]){
            cell.yiChatGroupMemberSingleCellClick = ^(id  _Nonnull model) {
                
                if(weakSelf.yiChatGroupMemberListViewFetchUserPower){
                    NSInteger power =  weakSelf.yiChatGroupMemberListViewFetchUserPower();
                    if(power > 0){
                        
                        if(model && [model isKindOfClass:[YiChatUserModel class]]){
                            
                            YiChatUserModel *user = model;
                            
                            NSString *userStr = user.getUserIdStr;
                            
                            YiChatGroupMemberSingleCell *tmpCell = (YiChatGroupMemberSingleCell *)[weakSelf.collectionView cellForItemAtIndexPath:index];
                            
                            UIView *back = [tmpCell getIconBack];
                            
                            [weakSelf showIconClickMenu:back frame:back.bounds andIndexPath:index userId:userStr];
                        }
                        else{
                            weakSelf.yiChatGroupMemberListViewClickItems(model);
                        }
                        
                    }
                }
            };
            [cell setNeedsLayout];
        }
        
        return cell;
    }];
    
    [self setProjectCollectionViewSelecteItem:^(UICollectionView * _Nonnull collection, NSIndexPath * _Nonnull index) {
       
    }];
    
    [self setProjectCollectionViewLineSpace:^CGFloat(UICollectionView * _Nonnull collection, UICollectionViewLayout * _Nonnull layout, NSInteger section) {
        
        return lineSpace;
    }];
    
    [self setProjectCollectionViewInteritemSpace:^CGFloat(UICollectionView * _Nonnull collection, UICollectionViewLayout * _Nonnull layout, NSInteger section) {
        
        return interalSpace;
    }];
    
    [self setProjectCollectionViewHeaderSize:^CGSize(UICollectionView * _Nonnull collection, UICollectionViewLayout * _Nonnull layout, NSInteger section) {
        return CGSizeMake(0, 0);
    }];
    
    [self setProjectCollectionViewFooterSize:^CGSize(UICollectionView * _Nonnull collection, UICollectionViewLayout * _Nonnull layout, NSInteger section) {
        return CGSizeMake(0, 0);
    }];
    
    [self makeCollectionView];
    [self.collectionView registerClass:[YiChatGroupMemberSingleCell class] forCellWithReuseIdentifier:cellIdentify];
    [self.collectionView registerClass:[YiChatGroupMemberSingleCell class] forCellWithReuseIdentifier:cellIUserInfodentify];
    self.collectionView.backgroundColor = [UIColor whiteColor];
    
    
    if (@available(iOS 10.0,*)) {
        self.collectionView.prefetchingEnabled = NO;
    }
    
    self.collectionView.pagingEnabled = YES;
    self.layout.scrollDirection = UICollectionViewScrollDirectionVertical;
    self.collectionView.scrollEnabled = NO;
    
    [self layoutSubview];
    
}

- (void)layoutSubview{
    
    CGFloat showMoreMemberH = 0;
    if(_isShowLoadMoreMemberBtn){
        showMoreMemberH = YiChatGroupMemberListView_showMoreBtnH;
    }
    else{
        [_clickMoreMember removeFromSuperview];
        _clickMoreMember = nil;
    }
    
    self.collectionView.frame = CGRectMake(0, 0, self.frame.size.width, self.frame.size.height - showMoreMemberH);
    if(_isShowLoadMoreMemberBtn){
        [self addSubview:self.clickMoreMember];
    }
    
    [self refreshUI];
    
}

- (ProjectClickView *)clickMoreMember{
    if(!_clickMoreMember){
        WS(weakSelf);
        ProjectClickView *click =[[ProjectClickView alloc] initWithFrame:CGRectMake(self.frame.size.width / 2 - self.collectionView.frame.size.width / 2,self.collectionView.frame.origin.y + self.collectionView.frame.size.height, self.collectionView.frame.size.width, YiChatGroupMemberListView_showMoreBtnH) bgView:self];
        
        NSString *str = @"显示更多群成员";
        UIFont *font = PROJECT_TEXT_FONT_COMMON(14.0);
        CGRect rect = [ProjectHelper helper_getFontSizeWithString:str useSetFont:font withWidth:click.frame.size.width andHeight:click.frame.size.height];
        

        
        UIImage *arrow = [UIImage imageNamed:Project_Icon_rightGrayArrow];
        CGFloat iconW = 7.0;
        CGFloat iconH = 13.0;
        if(arrow && [arrow isKindOfClass:[UIImage class]]){
            iconH = [ProjectHelper helper_GetWidthOrHeightIntoScale:arrow.size.width / arrow.size.height width:iconW height:0];
        }
        click.lab.font = font;
        click.lab.textAlignment = NSTextAlignmentCenter;
        click.lab.frame = CGRectMake(click.frame.size.width / 2 - (rect.size.width + 10.0 + iconW) / 2 , 0, rect.size.width, click.frame.size.height);
        click.lab.text = str;
        
        click.icon.frame = CGRectMake(click.lab.frame.origin.x + click.lab.frame.size.width + 10.0, click.frame.size.height / 2 - iconH / 2, iconW, iconH);
        click.icon.image = arrow;
        
        click.clickInvocation = ^(NSString * _Nonnull identify) {
            if(weakSelf.yiChatGroupMemberListViewClickMore){
                weakSelf.yiChatGroupMemberListViewClickMore();
            }
        };
        
        _clickMoreMember = click;
    }
    return _clickMoreMember;
}

- (void)refreshUI{
    [ProjectHelper helper_getMainThread:^{

        NSInteger rows = [self getRows];
        CGFloat h = rows * (_itemSize.height) + (rows - 1) * _lineSpace + _magin * 2;
        CGFloat w = self.frame.size.width;
        
        if(_clickMoreMember){
            h += YiChatGroupMemberListView_showMoreBtnH;
        }
        
    
        if(_isLoadAll == NO){
            self.frame = CGRectMake(self.frame.origin.x, self.frame.origin.y, w, h);
        }
        
        if(_clickMoreMember){
            
            self.collectionView.frame = CGRectMake(0, 0, self.frame.size.width, self.frame.size.height - YiChatGroupMemberListView_showMoreBtnH);
            
            _clickMoreMember.frame = CGRectMake(self.frame.size.width / 2 - self.collectionView.frame.size.width / 2,self.collectionView.frame.origin.y + self.collectionView.frame.size.height, self.collectionView.frame.size.width, YiChatGroupMemberListView_showMoreBtnH);
        }
        else{
            self.collectionView.frame = self.bounds;
        }
        
        [UIView performWithoutAnimation:^{
            if(self.yiChatGroupMemberListViewDidFreshUI){
                self.yiChatGroupMemberListViewDidFreshUI(CGSizeMake(self.frame.size.width, self.frame.size.height));
            }
            [self.collectionView reloadData];
        }];
    }];
}

- (NSInteger)getRows{
    NSInteger row = 0;
    if(_numOfLine > 0 && _isLoadAll == YES){
        CGFloat left =  self.dataSoureArr.count % _numOfLine;
        if(left > 0){
            row = self.dataSoureArr.count / _numOfLine + 1;
        }
        else if(left == 0){
            row = self.dataSoureArr.count / _numOfLine;
        }
    }
    else if(_numOfLine > 0){
        NSInteger nums = YiChatGroupMemberListView_showNum;
        if(_isHasAddPower){
            nums += 1;
        }
        if(_isHasDeletePower){
            nums += 1;
        }
        if(nums > _dataSoureArr.count){
            nums = _dataSoureArr.count;
        }
        
        CGFloat left = nums % _numOfLine;
        if(left != 0){
            row = (nums / _numOfLine) + 1;
        }
        else if(left == 0){
            row = nums / _numOfLine;
        }
    }
    return row;
}
    
    
    
- (UIMenuController *)menuVC{
    if(!_menuVC){
        _menuVC = [UIMenuController sharedMenuController];
    }
    return _menuVC;
}
    
    
- (UIMenuItem *)shutUpMenuItem{
    if(!_shutUpMenuItem){
        _shutUpMenuItem = [[UIMenuItem alloc] initWithTitle:@"禁言" action:@selector(shutUpMethodAction:)];
        
    }
    return _shutUpMenuItem;
}
    
- (UIMenuItem *)searchInfoMenuItem{
    if(!_searchInfoMenuItem){
        _searchInfoMenuItem = [[UIMenuItem alloc] initWithTitle:@"查看资料" action:@selector(searchInfoMethodAction:)];
        
    }
    return _searchInfoMenuItem;
}
    
    
- (void)showIconClickMenu:(UIView *)showInView
                    frame:(CGRect)frame
             andIndexPath:(NSIndexPath *)indexPath userId:(NSString *)userId{
    
    
    if(userId && [userId isKindOfClass:[NSString class]]){
        if([self.menuVC isMenuVisible]){
            [self.menuVC setMenuVisible:NO animated:NO];
        }
        
        [self.menuVC setMenuItems:nil];
        
        self.menuIndex = indexPath;
        
        if([userId isEqualToString:YiChatUserInfo_UserIdStr]){
            [self.menuVC setMenuItems:@[self.searchInfoMenuItem]];
        }
        else{
            [self.menuVC setMenuItems:@[self.shutUpMenuItem,self.searchInfoMenuItem]];
        }
        
        [self.menuVC setTargetRect:frame inView:showInView];
        [self.menuVC update];
        [self.menuVC setMenuVisible:YES animated:YES];
    }
}
    
- (BOOL)canPerformAction:(SEL)action withSender:(id)sender
    {
        if( action == @selector(shutUpMethodAction:) || action == @selector(searchInfoMethodAction:)){
            return YES;
        }
        else{
            return NO;
        }
    }
    
- (void)shutUpMethodAction:(id)sender{
    
    NSIndexPath *selecte = [self selecteMenu];
    if(selecte && [selecte isKindOfClass:[NSIndexPath class]]){
        if(self.dataSoureArr.count - 1 >= selecte.row){
            id model = self.dataSoureArr[selecte.row];
            
            if(self.yiChatGroupMemberListViewShutUp){
                self.yiChatGroupMemberListViewShutUp(model);
            }
        }
    }
    
    
    
    
}
    
- (void)searchInfoMethodAction:(id)sender{
    
    NSIndexPath *selecte = [self selecteMenu];
    if(selecte && [selecte isKindOfClass:[NSIndexPath class]]){
        if(self.dataSoureArr.count - 1 >= selecte.row){
            id model = self.dataSoureArr[selecte.row];
            
            if(model && [model isKindOfClass:[YiChatUserModel class]]){
                if(self.yiChatGroupMemberListViewClickItems){
                    self.yiChatGroupMemberListViewClickItems(model);
                }
            }
        }
    }
}
    
- (NSIndexPath *)selecteMenu{
    if(self.menuIndex && [self.menuIndex isKindOfClass:[NSIndexPath class]]){
        NSIndexPath *selecte = [NSIndexPath indexPathForRow:self.menuIndex.row inSection:self.menuIndex.section];
        [self removeMenu];
        if(selecte && [selecte isKindOfClass:[NSIndexPath class]]){
            return selecte;
        }
    }
    return nil;
}
    
- (void)removeMenu{
    if([self.menuVC isMenuVisible]){
        [self.menuVC setMenuVisible:NO animated:NO];
    }
    self.menuIndex = nil;
}
    
- (void)scrollViewDidScroll:(UIScrollView *)scrollView{
    if(scrollView == self.collectionView){
        [self removeMenu];
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
