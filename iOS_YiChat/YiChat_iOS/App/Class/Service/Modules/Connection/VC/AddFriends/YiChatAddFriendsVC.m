//
//  YiChatAddFriendsVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/5/27.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "YiChatAddFriendsVC.h"
#import "ServiceGlobalDef.h"
#import "ProjectSearchBarView.h"
#import "ProjectCommonCellModel.h"
#import "YiChatAddFriendsMainCell.h"

#import "XYQRCodeScan.h"
#import "YiChatInvitePhoneConnectionVC.h"
#import "YiChatQRCodeVisitCardView.h"
#import "YiChatContactMatchVC.h"
#import <TencentOpenAPI/TencentOAuth.h>
#import <TencentOpenAPI/QQApiInterface.h>
#import <TencentOpenAPI/QQApiInterfaceObject.h>

#import "YiChatSearchFriendInfoVC.h"
#import "WXApi.h"
@interface YiChatAddFriendsVC ()<UITableViewDelegate,UITableViewDataSource>

@property (nonatomic,strong) ProjectSearchBarView *searchBar;

@property (nonatomic,strong) UITableView *table;

@property (nonatomic,strong) NSArray *toolCellData;

@end

#define YiChatAddFriendsVC_CellH 60.0f
@implementation YiChatAddFriendsVC


+ (id)initialVC{
    
    return [self initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"addFriends") leftItem:nil rightItem:nil];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self makeTable];
    // Do any additional setup after loading the view.
}

- (ProjectSearchBarView *)searchBar{
    if(!_searchBar){
        WS(weakSelf);
        
        _searchBar = [[ProjectSearchBarView alloc] initWithFrame:CGRectMake(0, 0,self.view.frame.size.width, ProjectUIHelper_SearchBarH)];
        _searchBar.placeHolder =  PROJECT_TEXT_LOCALIZE_NAME(@"addFriendsSearchPlaceHolder");
        [_searchBar initialSearchType:1];
         [_searchBar initialSearchStyle:2];
        [_searchBar createUI];
        _searchBar.projectSearchBarSearchResult = ^(id  _Nonnull obj) {
            if([obj isKindOfClass:[NSArray class]] && obj){
                //搜到一个人
                NSArray *arr = obj;
                if(arr.count == 1){
                    [ProjectHelper helper_getMainThread:^{
                        YiChatSearchFriendInfoVC *info = [YiChatSearchFriendInfoVC initialVC];
                        info.fromDes = @"好友搜索";
                        info.infoDic = arr.lastObject;
                        [weakSelf.navigationController pushViewController:info animated:YES];
                    }];
                    return ;
                }
            }
            
            
        };
    }
    return _searchBar;
}

- (void)makeTable{
    [ProjectHelper helper_getGlobalThread:^{
        [self loadSystemData];
        [ProjectHelper helper_getMainThread:^{
            [self.view addSubview:self.cTable];
            self.cTable.frame = CGRectMake(self.cTable.frame.origin.x, self.cTable.frame.origin.y, self.cTable.frame.size.width, self.view.frame.size.height - self.cTable.frame.origin.y);
        }];
    }];
    
}

- (void)loadSystemData{
    NSMutableArray *tool = [NSMutableArray arrayWithCapacity:0];
    
    for (int section = 0; section < 3; section ++) {
        
        if(section == 0){
            ProjectCommonCellModel *model = [[ProjectCommonCellModel alloc] init];
            model.titleStr = @"我的二维码";
            model.iconUrl = @"";
            if(model != nil){
                [tool addObject:@[model]];
            }
        }
        else if(section == 1){
            //@"手机通讯录匹配"
            NSArray *titleStrArr = @[@"扫一扫"];
            //@"匹配手机通讯录中的朋友",
            NSArray *contentStrArr = @[@"扫描好友的二维码"];
            NSArray *iconStrArr = @[@"icon_scan.png"];
            NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
            for (int j = 0; j < titleStrArr.count; j ++) {
                ProjectCommonCellModel *model = [[ProjectCommonCellModel alloc] init];
                model.titleStr = titleStrArr[j];
                if((iconStrArr.count - 1) >= j){
                    if(iconStrArr[j] != nil){
                        model.iconUrl = iconStrArr[j];
                    }
                }
                model.contentStr = contentStrArr[j];
                
                if(model != nil){
                    [tmp addObject:model];
                }
            }
            if(tmp != nil && tmp.count != 0){
                [tool addObject:tmp];
            }
            
        }
        else if(section == 2){
            NSArray *titleStrArr = @[@"邀请手机联系人",@"邀请好友"];
            NSArray *contentStrArr = @[@"邀请手机通讯录中的朋友",@"邀请朋友"];
            NSArray *iconStrArr = @[@"connect_cont_icon.png",@"connect_add_icon.png"];
            
            if (YiChatProject_IsNeedQQLogin == 0 && YiChatProject_IsNeedWeChatLogin == 0) {
                titleStrArr = @[@"邀请手机联系人"];
                contentStrArr = @[@"邀请手机通讯录中的朋友"];
                iconStrArr = @[@"connect_cont_icon.png"];
            }
            
            NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
            for (int j = 0; j < titleStrArr.count; j ++) {
                ProjectCommonCellModel *model = [[ProjectCommonCellModel alloc] init];
                model.titleStr = titleStrArr[j];
                if((iconStrArr.count - 1) >= j){
                    if(iconStrArr[j] != nil){
                        model.iconUrl = iconStrArr[j];
                    }
                }
                model.contentStr = contentStrArr[j];
                if(model != nil){
                    [tmp addObject:model];
                }
            }
            if(tmp != nil && tmp.count != 0){
                [tool addObject:tmp];
            }
        }
    }
    
    _toolCellData = tool;
    NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:0];
    for (int i = 0; i < tool.count; i ++) {
        if([tool[i] isKindOfClass:[NSArray class]]){
            NSArray *toolsub = tool[i];
            [tmp addObject:[NSNumber numberWithInteger:toolsub.count]];
        }
       
    }
    self.sectionsRowsNumSet = tmp;
}

- (CGFloat)projectTableViewController_CellHWithIndex:(NSIndexPath *)index{
    return YiChatAddFriendsVC_CellH;
}

- (CGFloat)projectTableViewController_SectionHWithIndex:(NSInteger)section{
    if(section == 0){
        return ProjectUIHelper_SearchBarH;
    }
    else{
        return 10.0;
    }
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    if(section == 0){
        return self.searchBar;
    }
    else{
        UIView *back = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.table.frame.size.width, [self projectTableViewController_SectionHWithIndex:section])];
        back.backgroundColor = PROJECT_COLOR_APPBACKCOLOR;
       
        return back;
    }
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    YiChatAddFriendsMainCell *cell =  nil;
    CGFloat cellH = YiChatAddFriendsVC_CellH;
    
    if(indexPath.section == 0){
        static NSString *str = @"YiChatConnection_addFriends_QRCode";
        cell =  [tableView dequeueReusableCellWithIdentifier:str];
        if(!cell){
            cell = [YiChatAddFriendsMainCell initialWithStyle:UITableViewCellStyleDefault reuseIdentifier:str indexPath:indexPath cellHeight:[NSNumber numberWithFloat:cellH] cellWidth:[NSNumber numberWithFloat:self.view.frame.size.width] isHasDownLine:[NSNumber numberWithBool:NO] type:0];
        }
        
        [cell updateSystemConfigWithIndexPath:indexPath arrow:[NSNumber numberWithBool:NO] downLine:[NSNumber numberWithBool:NO]  cellHeight:[NSNumber numberWithFloat:cellH]];
        
        [cell updateType:0];
        
        cell.cellMyQrCodeClick = ^{
            YiChatQRCodeVisitCardView *visitCard = [[YiChatQRCodeVisitCardView alloc] initWithFrame:CGRectMake(PROJECT_SIZE_NAV_BLANK, self.view.frame.size.height / 2 -  self.view.frame.size.height / 3, self.view.frame.size.width - PROJECT_SIZE_NAV_BLANK * 2, self.view.frame.size.height / 3 * 2)];
            [visitCard showAnimateCompletionHandle:^{
                
            }];
            [self.view addSubview:visitCard];
        };
        
    }
    else if(indexPath.section == 1){
        static NSString *str = @"YiChatConnection_addFriends_Scan";
        cell =  [tableView dequeueReusableCellWithIdentifier:str];
        if(!cell){
            cell = [YiChatAddFriendsMainCell initialWithStyle:UITableViewCellStyleDefault reuseIdentifier:str indexPath:indexPath cellHeight:[NSNumber numberWithFloat:cellH] cellWidth:[NSNumber numberWithFloat:self.view.frame.size.width] isHasDownLine:[NSNumber numberWithBool:YES] type:1];
        }
        [cell updateSystemConfigWithIndexPath:indexPath arrow:[NSNumber numberWithBool:NO] downLine:[NSNumber numberWithBool:YES]  cellHeight:[NSNumber numberWithFloat:cellH]];
        
         [cell updateType:1];
    }
    
    else if(indexPath.section == 2){
        static NSString *str = @"YiChatConnection_addFriends_Invite";
        cell =  [tableView dequeueReusableCellWithIdentifier:str];
        if(!cell){
            cell = [YiChatAddFriendsMainCell initialWithStyle:UITableViewCellStyleDefault reuseIdentifier:str indexPath:indexPath cellHeight:[NSNumber numberWithFloat:cellH] cellWidth:[NSNumber numberWithFloat:self.view.frame.size.width] isHasDownLine:[NSNumber numberWithBool:YES] type:1];
        }
        [cell updateSystemConfigWithIndexPath:indexPath arrow:[NSNumber numberWithBool:NO] downLine:[NSNumber numberWithBool:YES]  cellHeight:[NSNumber numberWithFloat:cellH]];
        
        [cell updateType:1];
    }
    
    if((_toolCellData.count - 1) >= indexPath.section){
         id obj = _toolCellData[indexPath.section];
        if([obj isKindOfClass:[NSArray class]]){
            NSArray *tmp = obj;
            if((tmp.count - 1) >= indexPath.row){
                 cell.cellModel = tmp[indexPath.row];
            }
        }
    }
    
    return cell;
}

- (UIImage *)appIcon{
    NSDictionary *infoPlist = [[NSBundle mainBundle] infoDictionary];
    NSString *icon = [[infoPlist valueForKeyPath:@"CFBundleIcons.CFBundlePrimaryIcon.CFBundleIconFiles"] lastObject];
    UIImage* image = [UIImage imageNamed:icon];
    return image;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    
    if(indexPath.section == 1){
        if(indexPath.row == 0){
            //扫一扫
            UIViewController *vc = [[XYQRCodeScan alloc] init];
            [self.navigationController pushViewController:vc animated:YES];
        }
    }
    else if(indexPath.section == 2){
        if(indexPath.row == 0){
            YiChatInvitePhoneConnectionVC *invite = [YiChatInvitePhoneConnectionVC initialVC];
            [self.navigationController pushViewController:invite animated:YES];
        }else{
            YiChatUserManager *manager = [YiChatUserManager defaultManagaer];

            UIAlertController *aletr = [UIAlertController alertControllerWithTitle:@"邀请好友" message:@"" preferredStyle:UIAlertControllerStyleActionSheet];
            
            UIAlertAction *qq = [UIAlertAction actionWithTitle:@"邀请qq好友" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
                TencentOAuth *auth = [[TencentOAuth alloc] initWithAppId:YiChatProject_QQ_AppId andDelegate:self];
                //qq分享
                NSString *utf8String = manager.sharedLink;
                NSString *title = [NSString stringWithFormat:@"%@ ----- 开心就用%@",PROJECT_TEXT_APPNAME,PROJECT_TEXT_APPNAME];;
                NSString *description = [NSString stringWithFormat:@"%@",manager.sharedContent];;

//                NSString *previewImageUrl = [[NSBundle mainBundle] pathForResource:@"qqShare" ofType:@"png"];
//                NSData *imageData = [NSData dataWithContentsOfFile:previewImageUrl];
                NSData *imageData = UIImagePNGRepresentation([self appIcon]);
                if(!(utf8String && [utf8String isKindOfClass:[NSString class]])){
                    utf8String = @"";
                }
                
                QQApiNewsObject *newsObj = [QQApiNewsObject
                                            objectWithURL:[NSURL URLWithString:utf8String]
                                            title:title
                                            description:description
                                            previewImageData:imageData];
                SendMessageToQQReq *req = [SendMessageToQQReq reqWithContent:newsObj];
                //将内容分享到qq
                
                if ([QQApiInterface isQQInstalled]) {
                    [QQApiInterface sendReq:req];
                }else{
                    
                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"未安装qq应用或版本过低"];
                }
                
                
            }];
            
            UIAlertAction *wechat = [UIAlertAction actionWithTitle:@"邀请微信好友" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
                //微信分享
                if([WXApi isWXAppInstalled]){//判断当前设备是否安装微信客户端  PROJECT_TEXT_APPNAME
                    //创建多媒体消息结构体
                    WXMediaMessage *message = [WXMediaMessage message];
                    message.title = [NSString stringWithFormat:@"%@ ----- 开心就用%@",PROJECT_TEXT_APPNAME,PROJECT_TEXT_APPNAME];//标题
                    message.description = manager.sharedContent;//描述
                    //                [message setThumbImage:[UIImage imageNamed:@"complaint_icon"]];//设置预览图
                    
                    //创建网页数据对象
                    WXWebpageObject *webObj = [WXWebpageObject object];
                    webObj.webpageUrl = manager.sharedLink;//链接
                    message.mediaObject = webObj;
                    
                    SendMessageToWXReq *sendReq = [[SendMessageToWXReq alloc] init];
                    sendReq.bText = NO;//不使用文本信息
                    sendReq.message = message;
                    sendReq.scene = WXSceneSession;//分享到好友会话
                    
                    [WXApi sendReq:sendReq];//发送对象实例
                }else{
                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"未安装微信应用或版本过低"];
                    //未安装微信应用或版本过低
                }
            }];
            
            UIAlertAction *cancel = [UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
                
            }];
            
            
            if (YiChatProject_IsNeedQQLogin == 0 && YiChatProject_IsNeedWeChatLogin == 1) {
                [aletr addAction:wechat];
            }
            
            if (YiChatProject_IsNeedQQLogin == 1 && YiChatProject_IsNeedWeChatLogin == 0) {
                [aletr addAction:qq];
            }
            
            if (YiChatProject_IsNeedQQLogin == 1 && YiChatProject_IsNeedWeChatLogin == 1) {
                [aletr addAction:qq];
                [aletr addAction:wechat];
            }
            
            [aletr addAction:cancel];
            [self presentViewController:aletr animated:YES completion:nil];
        }
    }
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
