//
//  YiChatGroupAnnouncementVC.m
//  YiChat_iOS
//
//  Created by mac on 2019/8/18.
//  Copyright © 2019 ZhangFengTechnology. All rights reserved.
//

#import "YiChatGroupAnnouncementVC.h"
#import "YiChatSetGroupNoticeVC.h"
#import "YiChatGroupAnnouncementCell.h"
#import "YiChatGroupNoticeModel.h"

@interface YiChatGroupAnnouncementVC ()<UITableViewDelegate,UITableViewDataSource>
@property (nonatomic,strong) UITableView *tableView;
@property (nonatomic,strong) NSMutableArray *dataArr;
@property (nonatomic,assign) BOOL isManeger;
@end

@implementation YiChatGroupAnnouncementVC

+ (id)initialVCWithManeger:(BOOL)maneger{
    YiChatGroupAnnouncementVC *groupList = [YiChatGroupAnnouncementVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_13 centeritem:PROJECT_TEXT_LOCALIZE_NAME(@"GroupAnnouncement") leftItem:nil rightItem:maneger?@"发布":nil];
    groupList.isManeger = maneger;
    return groupList;
}

//发布群公告
- (void)navBarButtonRightItemMethod:(UIButton *)btn{
    YiChatSetGroupNoticeVC *vc = [YiChatSetGroupNoticeVC initialVC];
    vc.groupID = self.groupID;
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.dataArr = [NSMutableArray new];
    [self setUI];
   
    // Do any additional setup after loading the view.
}


-(void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    [self reloadGroupNotice];
}

-(void)setUI{
    self.tableView = [[UITableView alloc]initWithFrame:TableViewRectMake style:UITableViewStylePlain];
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    self.tableView.tableFooterView = [[UIView alloc]initWithFrame:CGRectZero];
    [self.view addSubview:self.tableView];
}

-(void)reloadGroupNotice{
    WS(weakSelf);
    NSDictionary *param = [ProjectRequestParameterModel setGroupNoticeListWithGroupId:self.groupID];
    id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];
    
    [ProjectRequestHelper groupNoticeListWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:progress isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {
    } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
        [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
            if([obj isKindOfClass:[NSDictionary class]]){
                NSDictionary *dataDic = (NSDictionary *)obj;
                YiChatGroupNoticeModel *model = [YiChatGroupNoticeModel mj_objectWithKeyValues:dataDic];
                if (model.code == 0) {
                    dispatch_async(dispatch_get_main_queue(), ^{
                        [weakSelf.dataArr removeAllObjects];
                        [weakSelf.dataArr addObjectsFromArray:model.data];
                        [weakSelf.tableView reloadData];
                    });
                }else{
                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:model.msg];
                }
            }
            else if([obj isKindOfClass:[NSString class]]){
                [ProjectRequestHelper progressHidden:progress];
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
            }
            [ProjectRequestHelper progressHidden:progress];
        }];
    } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {
        
    }];
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return self.dataArr.count;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    YiChatGroupAnnouncementCell *cell = [tableView dequeueReusableCellWithIdentifier:@"cell"];
    if (cell == nil) {
        cell = [[YiChatGroupAnnouncementCell alloc]initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:@"cell"];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
    }
    YiChatGroupNoticeInfoModel *model = self.dataArr[indexPath.row];
    cell.model = model;
    return cell;
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    YiChatGroupNoticeInfoModel *model = self.dataArr[indexPath.row];
    if (model.content.length == 0 || model.content == nil || [model.content isEqualToString:@""]) {
        return 100;
    }
    
    NSMutableParagraphStyle *paraStyle = [[NSMutableParagraphStyle alloc] init];
    paraStyle.lineSpacing = 3;
    NSDictionary *dic = @{ NSFontAttributeName:[UIFont systemFontOfSize:14], NSParagraphStyleAttributeName:paraStyle };
    CGSize size = [model.content boundingRectWithSize:CGSizeMake(PROJECT_SIZE_WIDTH - 30, MAXFLOAT) options: NSStringDrawingUsesLineFragmentOrigin | NSStringDrawingUsesFontLeading attributes:dic context:nil].size;
    return 80 + size.height;
}

- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    return self.isManeger? YES : NO;
}
// 定义编辑样式
- (UITableViewCellEditingStyle)tableView:(UITableView *)tableView editingStyleForRowAtIndexPath:(NSIndexPath *)indexPath {
    return UITableViewCellEditingStyleDelete;
    
}
// 进入编辑模式，按下出现的编辑按钮后,进行删除操作
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        YiChatGroupNoticeInfoModel *model = self.dataArr[indexPath.row];
        id progress = [ProjectUIHelper ProjectUIHelper_getProgressWithText:@""];
        NSDictionary *param = [ProjectRequestParameterModel getDeleteGroupNoticeParametersWithNoticeId:model.noticeId];
        [ProjectRequestHelper deleteGroupNoticeWithParameters:param headerParameters:[ProjectRequestParameterModel getTokenParamWithToken:YiChatUserInfo_Token] progress:progress isScrete:YES isAsyn:YES identider:^(NSString * _Nonnull identify) {

        } successHandle:^(NSData * _Nonnull data, NSHTTPURLResponse * _Nonnull response) {
            [ProjectRequestHelper requestHelper_feltRequestData:data response:response handle:^(id  _Nonnull obj, BOOL isNeedLogin) {
                if([obj isKindOfClass:[NSDictionary class]]){
                    YiChatBassModel *model = [YiChatBassModel mj_objectWithKeyValues:obj];
                    if (model.code == 0) {
                        [self.dataArr removeAllObjects];
                        dispatch_async(dispatch_get_main_queue(), ^{
                            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"公告删除成功"];
                            [self.tableView reloadData];
                        });
                    }
                }else if([obj isKindOfClass:[NSString class]]){
                    [ProjectRequestHelper progressHidden:progress];
                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:obj];
                }
                [ProjectRequestHelper progressHidden:progress];
            }];
        } fail:^(NSString * _Nonnull error, NSString * _Nonnull identify) {

        }];
    }
    
}
// 修改编辑按钮文字
- (NSString *)tableView:(UITableView *)tableView titleForDeleteConfirmationButtonForRowAtIndexPath:(NSIndexPath *)indexPath {
    return @"删除";
}

@end
