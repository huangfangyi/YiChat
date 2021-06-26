//
//  ProjectMapVC.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/16.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectMapVC.h"
#import "ServiceGlobalDef.h"
#import "ProjectMapCell.h"

#define Nav_Cell_H 55.0
#define Nav_Header_H 0.00001
#define Nav_Footer_H 0.00001
@interface ProjectMapVC ()<MAMapViewDelegate,UITableViewDelegate,UITableViewDataSource>

{
    
    
}
@property (nonatomic) NSInteger selecteRow;
@property (nonatomic,strong) MAPointAnnotation *annotation;
@property (nonatomic,strong)  MAMapView *mapView;
@property (nonatomic,strong) NSArray <AMapPOI *>*locationArr;
@property (nonatomic,strong) UITableView *cTable;
@property (nonatomic) BOOL isUpdateLocation;
@property (nonatomic) CLLocationCoordinate2D location;
@property (nonatomic) NSInteger type;
@property (nonatomic,strong) NSString *address;
@property (nonatomic,strong) NSString *des;

@end

@implementation ProjectMapVC

- (void)dealloc{
    [_mapView removeFromSuperview];
    _mapView = nil;
    _locationArr = nil;
    _annotation = nil;
}

+ (id)initialMapVCWithLocation:(CLLocationCoordinate2D)location address:(NSString *)address description:(NSString *)description{
    ProjectMapVC *map = [ProjectMapVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_1 centeritem:@"位置" leftItem:nil rightItem:nil];
    map.type = 0;
    //查看
    map.address = address;
    map.des = description;
    map.location = location;
    return map;
}

+ (id)initialSendMapVC{
    ProjectMapVC *map = [ProjectMapVC initialVCWithNavBarStyle:ProjectNavBarStyleCommon_3 centeritem:@"位置" leftItem:nil rightItem:@"发送"];
    map.type = 1;
    //发送位置
    return map;
}

- (void)navBarButtonRightItemMethod:(UIButton *)btn{
    if(self.sendLocationHandle){
        if(_isUpdateLocation == YES){
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"位置更新中.."];
            return;
        }
        if(_locationArr.count !=0 && (_selecteRow <= (_locationArr.count - 1))){
            CLLocationCoordinate2D location = CLLocationCoordinate2DMake(_annotation.coordinate.latitude, _annotation.coordinate.longitude);
            self.sendLocationHandle(location,_locationArr[_selecteRow].address,_locationArr[_selecteRow].name,[_mapView takeSnapshotInRect:_mapView.bounds]);
        }
        else{
            [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"发送位置消息出错"];
        }
    }
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self makeUI];
    // Do any additional setup after loading the view.
}

- (void)makeUI{
    _mapView = [[ProjectLocationManager defualtLocationManager] getMap];
    _mapView.delegate = self;
    [self.view addSubview:_mapView];
    _mapView.zoomLevel = 19;
    
    _selecteRow = 0;
    
    WS(weakSelf);
    
    if(self.type == 1){
        //发送位置
        
        _annotation = [[ProjectLocationManager defualtLocationManager] getPointAnnotation];
        
        _mapView.frame = CGRectMake(0, PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH, PROJECT_SIZE_WIDTH,(PROJECT_SIZE_HEIGHT - (PROJECT_SIZE_NAVH + PROJECT_SIZE_STATUSH + PROJECT_SIZE_SafeAreaInset.bottom)) / 2);
        
        [_mapView addAnnotation:_annotation];
        
        [self makeTable];
        
        [self locationUser:^(CLLocation *location, AMapLocationReGeocode *regeocode, NSError *error) {
            if(error == nil){
                [weakSelf getUserLocationWithlocation:location regeocode:regeocode];
            }
            else{
                [ProjectUIHelper ProjectUIHelper_getAlertWithAlertMessage:@"获取用户位置信息失败是否重试？" clickBtns:@[@"是",@"否"] invocation:^(NSInteger row) {
                    if(row == 0){
                        [weakSelf locationUser:^(CLLocation *location, AMapLocationReGeocode *regeocode, NSError *error) {
                            if(error != nil){
                                [ProjectHelper helper_getMainThread:^{
                                    [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"获取用户信息失败"];
                                    [weakSelf.navigationController popViewControllerAnimated:YES];
                                }];
                            }
                            else{
                                [weakSelf getUserLocationWithlocation:location regeocode:regeocode];
                            }
                        }];
                    }
                }];
            }
        }];
    }
    else{
        //查看用户位置
        [self searchUserLocation];
    }
    
}

- (void)locationUser:(void(^)(CLLocation *location, AMapLocationReGeocode *regeocode, NSError *error))handle{
    [[ProjectLocationManager defualtLocationManager] projectlocationManaer_getCurrentLocationCompletionHandle:^(CLLocation *location, AMapLocationReGeocode *regeocode, NSError *error) {
        handle(location,regeocode,error);
    }];
}


- (void)searchUserLocation{
    
    _annotation = [[ProjectLocationManager defualtLocationManager] getPointAnnotation];
    _annotation.coordinate = _location;
    _mapView.frame = CGRectMake(0, PROJECT_SIZE_NAVH +  PROJECT_SIZE_STATUSH, PROJECT_SIZE_WIDTH,PROJECT_SIZE_HEIGHT - (PROJECT_SIZE_NAVH +  PROJECT_SIZE_STATUSH) - (PROJECT_SIZE_NAVH +  PROJECT_SIZE_STATUSH));
    
    [_mapView addAnnotation:_annotation];
    _mapView.centerCoordinate = _location;
    
    [self makeLocationView];
}

- (void)makeLocationView{
    UIView *back = [ProjectHelper helper_factoryMakeViewWithFrame:CGRectMake(0, _mapView.frame.origin.y + _mapView.frame.size.height, self.view.frame.size.width,(PROJECT_SIZE_NAVH +  PROJECT_SIZE_STATUSH) ) backGroundColor:[UIColor whiteColor]];
    [self.view addSubview:back];
    
    CGFloat blank = PROJECT_SIZE_NAV_BLANK;
    CGFloat driveBtnW = back.frame.size.height - blank  * 2;
    CGFloat driveBtnH = driveBtnW;
    
    UILabel *addess = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(blank, 0, back.frame.size.width - driveBtnW - blank * 3, back.frame.size.height / 2) andfont:PROJECT_TEXT_FONT_COMMON(18) textColor:PROJECT_COLOR_TEXTCOLOR_BLACK textAlignment:NSTextAlignmentLeft];
    [back addSubview:addess];
    addess.text =self.address;
    
    UILabel *des = [ProjectHelper helper_factoryMakeLabelWithFrame:CGRectMake(addess.frame.origin.x, addess.frame.origin.y + addess.frame.size.height, addess.frame.size.width, addess.frame.size.height) andfont:PROJECT_TEXT_FONT_COMMON(14.0) textColor:PROJECT_COLOR_TEXTGRAY textAlignment:NSTextAlignmentLeft];
    [back addSubview:des];
    des.text = self.des;
    //icon_daohang
    NSMutableAttributedString *string = [[NSMutableAttributedString alloc] init];
    
    NSTextAttachment *attach = [[NSTextAttachment alloc] init];
    attach.bounds = CGRectMake(0,0, 10, 10);
    attach.image = [UIImage imageNamed:@"icon_daohang.png"];
    [string appendAttributedString:[NSAttributedString attributedStringWithAttachment:attach]];
    [string appendAttributedString:[[NSAttributedString alloc] initWithString:@"导航"]];
    
    NSMutableParagraphStyle *parag=[[NSMutableParagraphStyle alloc] init];
    
    UIFont *baseFont = [UIFont systemFontOfSize:13];
    [string addAttribute:NSFontAttributeName value:baseFont range:NSMakeRange(0, string.length)];
    [string addAttribute:NSParagraphStyleAttributeName value:parag range:NSMakeRange(0, string.length)];
    [string addAttribute:NSForegroundColorAttributeName value:[UIColor whiteColor] range:NSMakeRange(0,string.length)];
    
    
    UIButton *btn = [ProjectHelper helper_factoryMakeButtonWithFrame:CGRectMake(addess.frame.origin.x + addess.frame.size.width + blank, blank, driveBtnW, driveBtnH) andBtnType:UIButtonTypeCustom];
    [btn setAttributedTitle:string forState:UIControlStateNormal];
    
    [back addSubview:btn];
    btn.backgroundColor = PROJECT_COLOR_APPMAINCOLOR;
    btn.layer.cornerRadius = btn.frame.size.height / 2;
    [btn addTarget:self action:@selector(driveBtn) forControlEvents:UIControlEventTouchUpInside];
    
}

- (void)driveBtn{
    
    [ProjectUIHelper projectActionSheetWithListArr:@[@"百度地图",@"高德地图",@"腾讯地图",@"苹果地图"] click:^(NSInteger row) {
        [[ProjectLocationManager defualtLocationManager] projectlocationManaer_getCurrentLocationCompletionHandle:^(CLLocation *location, AMapLocationReGeocode *regeocode, NSError *error) {
            if(!error){
                [self gotoMapWithType:row currentCLLocation:location address:regeocode.formattedAddress];
            }
            else{
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:@"获取用户当前位置失败，请点击重试."];
            }
        }];
    }];
}


- (void)gotoMapWithType:(NSInteger)type currentCLLocation:(CLLocation *)location address:(NSString *)currentAddress{
    // 后台返回的目的地坐标是百度地图的
    // 百度地图与高德地图、苹果地图采用的坐标系不一样，故高德和苹果只能用地名不能用后台返回的坐标
    CGFloat latitude  = _location.latitude;  // 纬度
    CGFloat longitude = _location.longitude; // 经度
    NSString *address = self.address; // 送达地址
    
    if (type == 0) {
        // 百度地图
        // 起点为“我的位置”，终点为后台返回的坐标
        //        CGFloat bdLatitude = [YRLocationTranslate wgs84ToBd09:_location].latitude;
        //        CGFloat bdLongtitu = [YRLocationTranslate wgs84ToBd09:_location].longitude;
        //        CGFloat user_bdLatitude =[YRLocationTranslate wgs84ToBd09:location.coordinate].latitude;
        //        CGFloat user_bdLongtitu =[YRLocationTranslate wgs84ToBd09:location.coordinate].longitude;
        
        CGFloat bdLatitude = _location.latitude;
        CGFloat bdLongtitu = _location.longitude;
        CGFloat user_bdLatitude =location.coordinate.latitude;
        CGFloat user_bdLongtitu =location.coordinate.longitude;
        
        if([[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:@"baidumap://"]]){
            NSString *urlString = [[NSString stringWithFormat:@"baidumap://map/direction?origin=latlng:%f,%f|name:我的位置&destination=latlng:%f,%f|name:%@&mode=driving&coord_type=gcj02",user_bdLatitude,user_bdLongtitu ,bdLatitude, bdLongtitu,address] stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
            
            [self systempOpenUrl:urlString];
            
        }
        else{
            [self alertWithType:type];
        }
    }else if (type == 1) {
        // 高德地图
        // 起点为“我的位置”，终点为后台返回的address
        if([[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:@"iosamap://"]]){
            NSString *urlString = [[NSString stringWithFormat:@"iosamap://path?sourceApplication=applicationName&sid=BGVIS1&sname=%@&did=BGVIS2&dname=%@&dev=0&t=0",@"我的位置",address] stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
            
            [self systempOpenUrl:urlString];
        }
        else{
            [self alertWithType:type];
        }
    }else if (type == 2){
        // 腾讯
        if([[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:@"qqmap://"]]){
            NSMutableDictionary *qqMapDic = [NSMutableDictionary dictionary];
            qqMapDic[@"title"] = @"腾讯地图";
            NSString *urlString = [[NSString stringWithFormat:@"qqmap://map/routeplan?from=我的位置&type=drive&tocoord=%f,%f&to=终点&coord_type=1&policy=0",latitude,longitude] stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
            
            [self systempOpenUrl:urlString];
        }
        else{
            [self alertWithType:type];
        }
    }
    else if (type == 3){
        // 苹果地图
        // 起点为“我的位置”，终点为后台返回的address
        if([[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:@"http://maps.apple.com"]]){
            NSString *urlString = [[NSString stringWithFormat:@"http://maps.apple.com/?daddr=%@",address] stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
            
            [self systempOpenUrl:urlString];
        }
        else{
            [self alertWithType:type];
        }
        
    }else{
        // 快递员没有安装上面三种地图APP，弹窗提示安装地图APP
    }
}


- (void)systempOpenUrl:(NSString *)URL{
    if ([[[UIDevice currentDevice] systemVersion] floatValue] >= 10.0) {
        //设备系统为IOS 10.0或者以上的
        [[UIApplication sharedApplication] openURL:[NSURL URLWithString:URL] options:@{} completionHandler:nil];
    }else{
        //设备系统为IOS 10.0以下的
        [[UIApplication sharedApplication] openURL:[NSURL URLWithString:URL]];
    }
}

- (void)alertWithType:(NSInteger)type{
    NSArray *arr = @[@"建议安装百度地图APP",@"建议安装高德地图APP",@"建议安装腾讯地图APP",@"建议安装苹果地图APP"];
    if(type <= 3){
        [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:[NSString stringWithFormat:@"%@\r\n%@",@"暂无相关的应用APP",arr[type]]];
    }
}

- (void)getUserLocationWithlocation:(CLLocation *)location regeocode:(AMapLocationReGeocode *)regeocode{
    
    WS(weakSelf);
    
    [ProjectHelper helper_getMainThread:^{
        
        
        weakSelf.mapView.centerCoordinate = location.coordinate;
        
        
        [[ProjectLocationManager defualtLocationManager] projectLocationManagerSearchLocation:location.coordinate invocation:^(NSArray<AMapPOI *> *POI, NSString *error) {
            if(error == nil){
                weakSelf.locationArr = POI;
                [weakSelf.cTable reloadData];
                if(weakSelf.locationArr.count != 0){
                    [self addAnotationWithLocation:CLLocationCoordinate2DMake(weakSelf.locationArr[0].location.latitude, weakSelf.locationArr[0].location.longitude)];
                }
            }
            else{
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:error];
            }
        }];
    }];
    
    
}

- (void)addAnotationWithLocation:(CLLocationCoordinate2D)location{
    _annotation.coordinate = location;
    [_mapView selectAnnotation:_annotation animated:YES];
}

- (void)mapView:(MAMapView *)mapView mapDidMoveByUser:(BOOL)wasUserAction{
    if(wasUserAction == YES && self.type == 1){
        //用户移动地图
        _isUpdateLocation = YES;
        
        WS(weakSelf);
        
        [self addAnotationWithLocation:_mapView.centerCoordinate];
        
        [[ProjectLocationManager defualtLocationManager] projectLocationManagerSearchLocation:_mapView.centerCoordinate invocation:^(NSArray<AMapPOI *> *POI, NSString *error) {
            if(error == nil){
                
                
                [ProjectHelper helper_getMainThread:^{
                    [weakSelf.locationArr sortedArrayUsingComparator:^NSComparisonResult(AMapPOI  *obj1, AMapPOI   *obj2) {
                        if(obj1.distance > obj2.distance){
                            return NSOrderedAscending;
                        }
                        else{
                            return NSOrderedDescending;
                        }
                    }];
                    
                    weakSelf.locationArr = POI;
                    weakSelf.selecteRow = 0;
                    
                    
                    [weakSelf.cTable reloadData];
                    
                    weakSelf.isUpdateLocation = NO;
                    
                    if(weakSelf.locationArr.count != 0){
                        [weakSelf.cTable scrollToRowAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:0] atScrollPosition:UITableViewScrollPositionMiddle animated:YES];
                    }
                    
                    [weakSelf.locationArr enumerateObjectsUsingBlock:^(AMapPOI * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
                        NSLog(@"%@ %@ %ld",obj.name,obj.address,obj.distance);
                    }];
                }];
                
                
            }
            else{
                weakSelf.isUpdateLocation = NO;
                [ProjectUIHelper ProjectUIHelper_getAlertWithMsm:error];
            }
        }];
    }
}

- (void)makeTable{
    _cTable  = [ProjectHelper helper_factoryMakeTableViewWithFrame:CGRectMake(0,_mapView.frame.origin.y + _mapView.frame.size.height, PROJECT_SIZE_WIDTH, _mapView.frame.size.height) backgroundColor:PROJECT_COLOR_APPBACKCOLOR style:UITableViewStyleGrouped bounces:YES pageEnabled:NO superView:self.view object:self];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return _locationArr.count;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return 1;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return Nav_Cell_H;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section{
    return Nav_Header_H;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section{
    return Nav_Footer_H;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    static NSString *str = @"nav";
    ProjectMapCell *cell = [tableView dequeueReusableCellWithIdentifier:str];
    if(!cell){
        cell = [ProjectMapCell initalWithStyle:UITableViewCellStyleDefault reuseIdentifier:str indexPath:indexPath cellHeight:[NSNumber numberWithFloat:Nav_Cell_H] cellWidth:[NSNumber numberWithFloat:_cTable.frame.size.width] isHasDownLine:[NSNumber numberWithBool:YES]];
    }
    [cell updateSystemConfigWithIndexPath:indexPath arrow:[NSNumber numberWithBool:NO] downLine:[NSNumber numberWithBool:YES] cellHeight:[NSNumber numberWithFloat:Nav_Cell_H]];
    [cell setNavValue:_locationArr[indexPath.row] selecte:_selecteRow];
    return cell;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    return [ProjectHelper helper_factoryMakeViewWithFrame:CGRectMake(0, 0, _cTable.frame.size.width, Nav_Header_H) backGroundColor:PROJECT_COLOR_APPBACKCOLOR];
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section{
    return [ProjectHelper helper_factoryMakeViewWithFrame:CGRectMake(0, 0, _cTable.frame.size.width, Nav_Footer_H) backGroundColor:PROJECT_COLOR_APPBACKCOLOR];
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    
    CLLocationCoordinate2D location = CLLocationCoordinate2DMake(_locationArr[indexPath.row].location.latitude, _locationArr[indexPath.row].location.longitude);
    
    _annotation.coordinate = location;
    
    _mapView.centerCoordinate = location;
    
    [_mapView selectAnnotation:_annotation animated:YES];
    
    _selecteRow = indexPath.row;
    
    [_cTable reloadData];
    if(_locationArr.count != 0){
        [_cTable scrollToRowAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:0] atScrollPosition:UITableViewScrollPositionMiddle animated:YES];
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
