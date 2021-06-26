//
//  ProjectLocationManager.m
//  YiChat_iOS
//
//  Created by 你是我的小呀小苹果 on 2019/7/16.
//  Copyright © 2019年 ZhangFengTechnology. All rights reserved.
//

#import "ProjectLocationManager.h"
#import "ServiceGlobalDef.h"


static ProjectLocationManager *manager = nil;
@interface ProjectLocationManager ()<AMapLocationManagerDelegate,AMapSearchDelegate>
{
    
    AMapLocationManager *_locationManager;
    MAMapView *_mapView;
    AMapSearchAPI *_search;
}
@property (nonatomic,copy) ProjectPOISearchResonseInvocation poiSearchInvocation;
@end

@implementation ProjectLocationManager


+ (id)defualtLocationManager{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        manager=[[self alloc] init];
        
        [manager initSearch];
    });
    return manager;
}

- (void)initialMapConfigure{
    //设置高德地图的key
    [AMapServices sharedServices].apiKey = YiChatProject_Map_Key;
}

- (void)initSearch
{
    if (_search == nil)
    {
        _search = [[AMapSearchAPI alloc] init];
        _search.delegate = self;
    }
}

- (MAMapView *)getMap{
    
    MAMapView *map = [[MAMapView alloc] init];
    map.showsBuildings = YES;
    map.showsUserLocation = YES;
    map.zoomLevel = 15;
    map.customizeUserLocationAccuracyCircleRepresentation = YES;
    
    return map;
}

- (MAPointAnnotation *)getPointAnnotation{
    MAPointAnnotation *annotation = [[MAPointAnnotation alloc] init];
    return annotation;
}


- (void)projectLocationManagerSearchLocation:(CLLocationCoordinate2D)cordinate invocation:(ProjectPOISearchResonseInvocation)invocation{
    self.poiSearchInvocation = invocation;
    
    AMapPOIAroundSearchRequest *request = [[AMapPOIAroundSearchRequest alloc] init];
    
    request.location=[AMapGeoPoint locationWithLatitude:cordinate.latitude longitude:cordinate.longitude];
    request.sortrule=0;
    request.radius = 1000;
    request.keywords = @"";
    request.requireExtension  = YES;
    request.types = @"050000|060000|070000|080000|090000|100000|110000|120000|130000|140000|150000|160000|170000";
    [_search AMapPOIAroundSearch:request];
}


- (void)onPOISearchDone:(AMapPOISearchBaseRequest *)request response:(AMapPOISearchResponse *)response
{
    
    if (response.pois.count == 0)
    {
        if(self.poiSearchInvocation){
            
            self.poiSearchInvocation(nil,@"无搜索目标位置");
            self.poiSearchInvocation = nil;
        }
        return;
    }
    
    NSMutableArray *arr = [NSMutableArray arrayWithCapacity:0];
    [response.pois enumerateObjectsUsingBlock:^(AMapPOI *obj, NSUInteger idx, BOOL *stop) {
        [arr addObject:obj];
    }];
    
    if(self.poiSearchInvocation){
        self.poiSearchInvocation(arr, nil);
        self.poiSearchInvocation = nil;
    }
    
}

/**
 *  定位用户当前地址
 */
- (void)projectlocationManaer_getCurrentLocationCompletionHandle:(AMapLocatingCompletionBlock)completionBlock{
    _locationManager=[[AMapLocationManager alloc] init];
    // 带逆地理信息的一次定位（返回坐标和地址信息）
    // 定位超时时间，最低2s，此处设置为2s
    _locationManager.locationTimeout =2;
    _locationManager.desiredAccuracy=kCLLocationAccuracyHundredMeters;
    // 逆地理请求超时时间，最低2s，此处设置为2s
    _locationManager.reGeocodeTimeout = 2;
    
    dispatch_async(dispatch_get_main_queue(), ^{
    [_locationManager requestLocationWithReGeocode:YES completionBlock:^(CLLocation *location, AMapLocationReGeocode *regeocode, NSError *error) {
            NSLog(@"%@",error);
            if(error){
                completionBlock(nil,nil,error);
            }
            
            completionBlock(location,regeocode,error);
        }];
        
    });
    
}


@end
