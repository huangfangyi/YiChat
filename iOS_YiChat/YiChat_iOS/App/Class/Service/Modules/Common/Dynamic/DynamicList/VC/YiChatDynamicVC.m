//
//  YiChatDynamicVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/6/6.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatDynamicVC.h"
#import "ServiceGlobalDef.h"

#import "YiChatDynamicHeader.h"
#import "YiChatDynamicPresenter.h"

#import "YiChatDynamicCell.h"
#import "YiChatFriendInfoVC.h"

@interface YiChatDynamicVC ()<UIScrollViewDelegate,UIGestureRecognizerDelegate>

@property (nonatomic,strong) YiChatDynamicPresenter *presenter;

@property (nonatomic,strong) UIMenuController *menuVC;

@property (nonatomic, strong) UIMenuItem * deleteMenuItem;

@property (nonatomic, strong) UIMenuItem * cpMenuItem;

@property (nonatomic, strong) NSIndexPath *menuIndex;

@end

#define YiChatDynamicVC_tableHeaderReuse_TEXT @"YiChatDynamicVC_tableHeaderReuse_dynamicContent_TEXT"
#define YiChatDynamicVC_tableHeaderReuse_IMAGE @"YiChatDynamicVC_tableHeaderReuse_dynamicContent_IMAGE"
#define YiChatDynamicVC_tableHeaderReuse_VIDEO @"YiChatDynamicVC_tableHeaderReuse_dynamicContent_VIDEO"

@implementation YiChatDynamicVC

+ (id)initialVC{
    YiChatDynamicVC *dynamic = [YiChatDynamicVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_14 centeritem:nil leftItem:nil rightItem:[UIImage imageNamed:@"news_chat_more_camera@3x.png"]];
    return dynamic;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self makeTable];
    
    [self setNavBar];
    
    [self.presenter addDynamicHeader];
    
    [self.presenter addHeaderRefresh];
    
    [self.presenter addtoolCommitLikeBar];
    
    [self.presenter addCommitView];
    // Do any additional setup after loading the view.
}

- (void)setNavBar{
    UIView *back = [self navBarGetNavBar];
    back.backgroundColor =[UIColor clearColor];
    
    [self.view bringSubviewToFront:back];
}

- (YiChatDynamicPresenter *)presenter{
    if(!_presenter){
        _presenter = [[YiChatDynamicPresenter alloc] init];
        _presenter.controlVC = self;
        _presenter.dynamicUserId = self.userId;
    }
    return _presenter;
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    [self.presenter HeaderRefreshEnd];
    [self.presenter refreshDynamicList];
}

- (void)navBarButtonRightItemMethod:(UIButton *)btn{
    [self presentVCWithName:@"YiChatSendDynamicVC" initialMethod:@selector(initialVC)];
}

- (void)presentVCWithName:(NSString *)name initialMethod:(SEL)method{
    if([name isKindOfClass:[NSString class]]){
        if(name){
            UIViewController *vc = [ProjectHelper helper_getVCWithName:name initialMethod:method];
            vc.hidesBottomBarWhenPushed = YES;
            if(vc){
                [self presentViewController:vc animated:YES completion:^{
                    
                }];
            }
        }
    }
}

- (void)pushVCWithName:(NSString *)name{
    if([name isKindOfClass:[NSString class]]){
        if(name){
            UIViewController *vc = [ProjectHelper helper_getVCWithName:name initialMethod:@selector(initialVC)];
            vc.hidesBottomBarWhenPushed = YES;
            if(vc){
                [self.navigationController pushViewController:vc animated:YES];
            }
        }
    }
}

- (void)makeTable{
    self.tableStyle = 1;
    [self.view addSubview:self.cTable];
    self.cTable.frame = CGRectMake(self.cTable.frame.origin.x, 0, self.cTable.frame.size.width, PROJECT_SIZE_HEIGHT  - PROJECT_SIZE_SafeAreaInset.bottom);
    self.cTable.backgroundColor = [UIColor whiteColor];
}

- (CGFloat)projectTableViewController_CellHWithIndex:(NSIndexPath *)index{
    if(self.presenter && [self.presenter isKindOfClass:[YiChatDynamicPresenter class]]){
        YiChatDynamicDataSource *data = [self.presenter getDataSourceWithIndex:index.section];
        if(data && [data isKindOfClass:[YiChatDynamicDataSource class]]){
            return [data getCellH:index];
        }
    }
    return 0;
}

- (CGFloat)projectTableViewController_SectionHWithIndex:(NSInteger)section{
    if(self.presenter && [self.presenter isKindOfClass:[YiChatDynamicPresenter class]]){
        YiChatDynamicDataSource *data = [self.presenter getDataSourceWithIndex:section];
        if(data && [data isKindOfClass:[YiChatDynamicDataSource class]]){
            return [data getHeaderH];
        }
    }
    return 0;
}

- (CGFloat)projectTableViewController_FooterHWithIndex:(NSInteger)section{
    if(self.presenter && [self.presenter isKindOfClass:[YiChatDynamicPresenter class]]){
        YiChatDynamicDataSource *data = [self.presenter getDataSourceWithIndex:section];
        if(data && [data isKindOfClass:[YiChatDynamicDataSource class]]){
            return [data getFooterH];
        }
    }
    return 0;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    if(self.presenter && [self.presenter isKindOfClass:[YiChatDynamicPresenter class]]){
        
        YiChatDynamicDataSource *data = [self.presenter getDataSourceWithIndex:section];
        if(data && [data isKindOfClass:[YiChatDynamicDataSource class]]){
              WS(weakSelf);
            NSInteger type = data.type;
            YiChatDynamicHeader *view = nil;
            if(type == 1){
                static NSString *str = YiChatDynamicVC_tableHeaderReuse_TEXT;
                view = [self tableHeaderViewReuseWithReuseStr:str type:type table:tableView];
            }
            else if(type == 2){
                static NSString *str = YiChatDynamicVC_tableHeaderReuse_IMAGE;
                view = [self tableHeaderViewReuseWithReuseStr:str type:type table:tableView];;
            }
            else if(type == 3){
                static NSString *str = YiChatDynamicVC_tableHeaderReuse_VIDEO;
                view = [self tableHeaderViewReuseWithReuseStr:str type:type table:tableView];;
            }
            view.model = data;
            
            view.YiChatDynamicHeaderClickCommitLikeBar = ^(YiChatDynamicDataSource * _Nonnull model, CGPoint point) {
                if(model && [model isKindOfClass:[YiChatDynamicDataSource class]]){
                    [weakSelf.presenter toolCommitLikeBarAppearWithPoint:point model:model index:section];
                }
            };
            
            view.YiChatDynamicHeaderHideOrReport = ^(YiChatDynamicDataSource * _Nonnull model, CGPoint point) {
                if (YiChatProject_IsUpAppStore == 0) {
                    return ;
                }
                if(model && [model isKindOfClass:[YiChatDynamicDataSource class]]){
                    [self.presenter toolCommitLikeBarDisappear];
                    [self.presenter commitViewResign];
                    [self removeMenu];
                    
                    UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"" message:@"您可以对这条内容进行以下操作" preferredStyle:UIAlertControllerStyleActionSheet];
                    
                    UIAlertAction *report = [UIAlertAction actionWithTitle:@"举报此内容" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
                        [[YiChatUserManager defaultManagaer] fetchUserInfoWithUserId:YiChatUserInfo_UserIdStr invocation:^(YiChatUserModel * _Nonnull modelUser, NSString * _Nonnull error) {
                           [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"举报成功"];
                        }];
                    }];
                    
                    UIAlertAction *hide = [UIAlertAction actionWithTitle:@"隐藏此内容" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
                        [weakSelf.presenter removeDynamicWithTrendId:[model getTrendId] indx:section];
                    }];
                    
                    UIAlertAction *cancel = [UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
                        
                    }];
                    
                    [alert addAction:report];
                    [alert addAction:hide];
                    [alert addAction:cancel];
                    [weakSelf presentViewController:alert animated:YES completion:nil];
                }
            };
            
            view.YiChatDynamicHeaderClickDelete = ^(YiChatDynamicDataSource * _Nonnull model) {
                if(model && [model isKindOfClass:[YiChatDynamicDataSource class]]){
                    [weakSelf.presenter removeDynamicWithTrendId:[model getTrendId] indx:section];
                }
            };
            view.YiChatDynamicHeaderClickUserIcon = ^(YiChatDynamicDataSource * _Nonnull model) {
                if(model && [model isKindOfClass:[YiChatDynamicDataSource class]]){
                    YiChatFriendInfoVC *info = [YiChatFriendInfoVC initialVC];
                    info.userId = [model getUserIdStr];
                    [weakSelf.navigationController pushViewController:info animated:YES];
                }
            };
            
            return view;

        }
       
    }

    return [UIView new];
}

- (YiChatDynamicHeader *)tableHeaderViewReuseWithReuseStr:(NSString *)str type:(NSInteger)type table:(UITableView *)table{
    YiChatDynamicHeader *view = [table dequeueReusableHeaderFooterViewWithIdentifier:str];
    if(view == nil){
        view = [YiChatDynamicHeader initialWithReuseIdentifier:str type:[NSNumber numberWithInteger:type]];
        view.controlVC = self;
        [view createUI];
    }
    return view;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    YiChatDynamicDataSource *model = [self.presenter getDataSourceWithIndex:indexPath.section];
    YiChatDynamicCell *cell = nil;
    UITableViewCellStyle style = UITableViewCellStyleDefault;
    
    CGFloat cellH = [self projectTableViewController_CellHWithIndex:indexPath];
    CGFloat cellW = self.cTable.frame.size.width;
    
    static NSString *reuserIdentifier = @"YiChatDynamicCell_coment";
    cell = [self getCellWithTable:tableView style:style reuseIdentifier:reuserIdentifier index:indexPath cellH:cellH cellW:cellW type:0];
    
    
    [cell updateSystemConfigWithIndexPath:indexPath arrow:[NSNumber numberWithBool:NO] downLine:[NSNumber numberWithBool:NO] cellHeight:[NSNumber numberWithFloat:cellH]];
    
    [cell setModel:model index:indexPath];
    
    WS(weakSelf);
    cell.YiChatDynamicLongPress = ^(YiChatDynamicDataSource * _Nonnull model, NSIndexPath * _Nonnull index) {
        if(model && [model isKindOfClass:[YiChatDynamicDataSource class]] && index){
            
            YiChatDynamicCell *tmpCell = [tableView cellForRowAtIndexPath:index];
            UIView *back = [tmpCell getCellBack];
            
            [weakSelf showMenuViewController:back frame:back.bounds andIndexPath:index model:model];
        }
    };
    
    if(!(cell && [cell isKindOfClass:[YiChatDynamicCell class]])){
        cell = [YiChatDynamicCell new];
    }
    return cell;
}

- (YiChatDynamicCell *)getCellWithTable:(UITableView *)table style:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuse index:(NSIndexPath *)index cellH:(CGFloat)cellH cellW:(CGFloat)cellW type:(NSInteger)cellType{
    YiChatDynamicCell *cell = [table dequeueReusableCellWithIdentifier:reuse];
    if(!cell){
        cell = [YiChatDynamicCell initialWithStyle:style reuseIdentifier:reuse indexPath:index cellHeight:[NSNumber numberWithFloat:cellH] cellWidth:[NSNumber numberWithFloat:cellW] type:cellType];
    }
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{

}

- (void)scrollViewWillBeginDecelerating:(UIScrollView *)scrollView{
    if(scrollView.contentOffset.y <= - 20.0){
        [self.presenter refreshDynamicList];
    }
}


- (void)scrollViewDidScroll:(UIScrollView *)scrollView{
    [self.presenter scrolltoolCommitLikeBarDisappear];
    
    [self removeMenu];
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    [self.presenter toolCommitLikeBarDisappear];
    [self.presenter commitViewResign];
    
    [self removeMenu];
}

- (void)showMenuViewController:(UIView *)showInView
                         frame:(CGRect)frame
                  andIndexPath:(NSIndexPath *)indexPath
                         model:(YiChatDynamicDataSource *)model{
    
    
    if([self.menuVC isMenuVisible]){
        [self.menuVC setMenuVisible:NO animated:NO];
    }
    
    [self.menuVC setMenuItems:nil];
    
    BOOL isSet = NO;
    NSArray *commit = model.getCommentList;
    
    if(commit && [commit isKindOfClass:[NSArray class]]){
        if(commit.count - 1 >= indexPath.row){
            YiChatDynamicCommitEntityModel *model = commit[indexPath.row];
            if(model && [model isKindOfClass:[YiChatDynamicCommitEntityModel class]]){
                if(model.userId == YiChatUserInfo_UserId){
                    [self.menuVC setMenuItems:@[self.deleteMenuItem,self.cpMenuItem]];
                    isSet = YES;
                }
            }
        }
    }
    
    if(!isSet){
         [self.menuVC setMenuItems:@[self.cpMenuItem]];
    }
    
    self.menuIndex = indexPath;
    
    [self.menuVC setTargetRect:frame inView:showInView];
    [self.menuVC update];
    [self.menuVC setMenuVisible:YES animated:YES];
}

- (BOOL)canPerformAction:(SEL)action withSender:(id)sender
{
    if(action ==  @selector(deleteMenuAction:) || action == @selector(copyMenuAction:)){
        return YES;
    }
    else{
        return NO;
    }
}

- (UIMenuController *)menuVC{
    if(!_menuVC){
        _menuVC = [UIMenuController sharedMenuController];
    }
    return _menuVC;
}

- (UIMenuItem *)deleteMenuItem{
    if(!_deleteMenuItem){
        _deleteMenuItem = [[UIMenuItem alloc] initWithTitle:@"删除" action:@selector(deleteMenuAction:)];
    }
    return _deleteMenuItem;
}

- (UIMenuItem *)cpMenuItem{
    if(!_cpMenuItem){
        _cpMenuItem = [[UIMenuItem alloc] initWithTitle:@"复制" action:@selector(copyMenuAction:)];
    }
    return _cpMenuItem;
}

- (void)deleteMenuAction:(id)sender{
    NSIndexPath *selecte = [self selecteMenu];
    if(selecte && [selecte isKindOfClass:[NSIndexPath class]]){
        [self.presenter deleteCommitWithIndexPath:selecte];
    }
}

- (void)copyMenuAction:(id)sender{
    NSIndexPath *selecte = [self selecteMenu];
    if(selecte && [selecte isKindOfClass:[NSIndexPath class]]){
        [self.presenter copyCommitWithIndexPath:selecte];
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
/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end

