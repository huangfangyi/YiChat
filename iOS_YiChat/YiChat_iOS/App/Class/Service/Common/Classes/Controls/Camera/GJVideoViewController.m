#import "GJVideoViewController.h"
#import <AVFoundation/AVFoundation.h>
#import "GJAVPlayer.h"
#import "GJProgressView.h"
#import <Foundation/Foundation.h>
#import <AssetsLibrary/AssetsLibrary.h>

//#import "ProjectPhotoVideoVC.h"

#define GJScreenW [UIScreen mainScreen].bounds.size.width
#define GJScreenH [UIScreen mainScreen].bounds.size.height

typedef void(^PropertyChangeBlock)(AVCaptureDevice *captureDevice);
@interface GJVideoViewController ()<AVCaptureFileOutputRecordingDelegate>

//轻触拍照，按住摄像
@property (strong, nonatomic) UILabel *labelTipTitle;

//视频输出流
@property (strong,nonatomic) AVCaptureMovieFileOutput *captureMovieFileOutput;
//图片输出流
//@property (strong,nonatomic) AVCaptureStillImageOutput *captureStillImageOutput;//照片输出流
//负责从AVCaptureDevice获得输入数据
@property (strong,nonatomic) AVCaptureDeviceInput *captureDeviceInput;
//后台任务标识
@property (assign,nonatomic) UIBackgroundTaskIdentifier backgroundTaskIdentifier;

@property (assign,nonatomic) UIBackgroundTaskIdentifier lastBackgroundTaskIdentifier;

@property (weak, nonatomic)  UIImageView *focusCursor; //聚焦光标

//负责输入和输出设备之间的数据传递
@property(nonatomic)AVCaptureSession *session;

//图像预览层，实时显示捕获的图像
@property(nonatomic)AVCaptureVideoPreviewLayer *previewLayer;

@property (nonatomic,strong) UIButton *pickAlbumnBtn;

// 返回
@property (strong, nonatomic)  UIButton *btnBack;
//重新录制
@property (strong, nonatomic)  UIButton *btnAfresh;
//确定
@property (strong, nonatomic)  UIButton *btnEnter;
//摄像头切换
@property (strong, nonatomic)  UIButton *btnCamera;

@property (strong, nonatomic)  UIImageView *bgView;
//记录录制的时间 默认最大60秒
@property (assign, nonatomic) NSInteger seconds;

//记录需要保存视频的路径
@property (strong, nonatomic) NSURL *saveVideoUrl;

//是否在对焦
@property (assign, nonatomic) BOOL isFocus;
@property (strong, nonatomic)  NSLayoutConstraint *afreshCenterX;
@property (strong, nonatomic)  NSLayoutConstraint *enterCenterX;
@property (strong, nonatomic)  NSLayoutConstraint *backCenterX;

//视频播放
@property (strong, nonatomic) GJAVPlayer *player;

@property (weak, nonatomic)  GJProgressView *progressView;


//是否是摄像 YES 代表是录制  NO 表示拍照
@property (assign, nonatomic) BOOL isVideo;

//     照片、图片
@property (strong, nonatomic) UIImage *takeImage;
//     最上层的图层，承载照片的
@property (strong, nonatomic) UIImageView *takeImageView;

//    中间按钮录制视频
@property (strong, nonatomic)  UIImageView *imgRecord;


@end

//时间大于这个就是视频，否则为拍照
#define TimeMax 1

@implementation GJVideoViewController


-(void)dealloc{
    [self removeNotification];
}

- (UIImage *)getBundleImageWithName:(NSString *)name{
    NSString * bundlePath = [[ NSBundle mainBundle] pathForResource:@"images" ofType :@ "bundle"];
    
    NSString *imgPath= [bundlePath stringByAppendingPathComponent:name];
    
    UIImage *image_1=[UIImage imageWithContentsOfFile:imgPath];
    
    return image_1;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    if (self.GJSeconds == 0) {
        self.GJSeconds = 10.0;
    }
    
    [self.view addSubview:self.bgView];
    
    UIImage *image =  [UIImage imageNamed:@"sc_btn_take.png"];
    
    [self.view addSubview:self.imgRecord];
    self.imgRecord.image = image;
    
    [self.view addSubview:self.progressView];
    
    //   [self.view addSubview:self.pickAlbumnBtn];
    
    [self.view addSubview:self.btnCamera];
    
    [self.view addSubview:self.btnBack];
    
    [self.view addSubview:self.btnEnter];
    
    [self.view addSubview:self.btnAfresh];
    
    self.btnAfresh.hidden = YES;
    self.btnEnter.hidden = YES;
    
    
    self.progressView.layer.cornerRadius = self.progressView.frame.size.width / 2;
    
    [self customCamera];
    
    [self performSelector:@selector(hiddenTipsLabel) withObject:nil afterDelay:4];
    
    // [self performSelector:@selector(updateAlbmun) withObject:nil afterDelay:1];
}

- (void)hiddenTipsLabel {
    self.labelTipTitle.hidden = YES;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (BOOL)prefersStatusBarHidden {
    
    return YES;
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    
    [self prefersStatusBarHidden];
    [self.session startRunning];
}

-(void)viewDidDisappear:(BOOL)animated{
    [super viewDidDisappear:animated];
    [self.session stopRunning];
}

- (UIImageView *)bgView{
    if(!_bgView){
        CGFloat w = self.view.frame.size.width;
        CGFloat h = self.view.frame.size.height;
        
        UIImageView *img = [[UIImageView alloc] initWithFrame:CGRectMake(self.view.frame.size.width / 2 - w / 2, self.view.frame.size.height / 2 - h / 2, w, h)];
        img.userInteractionEnabled = YES;
        _bgView = img;
    }
    return _bgView;
}

- (UIImageView *)imgRecord{
    if(!_imgRecord){
        
        CGFloat w = 80.0;
        CGFloat h = 80.0;
        
        UIImageView *img = [[UIImageView alloc] initWithFrame:CGRectMake(self.view.frame.size.width / 2 - w / 2, self.view.frame.size.height - self.view.frame.size.height / 20.0 - h, w, h)];
        _imgRecord = img;
        img.backgroundColor = [UIColor clearColor];
        img.userInteractionEnabled = YES;
    }
    return _imgRecord;
}

- (GJProgressView *)progressView{
    if(!_progressView){
        CGFloat w = 80.0;
        CGFloat h = 80.0;
        
        GJProgressView *progress = [[GJProgressView alloc] initWithFrame:CGRectMake(_imgRecord.frame.origin.x + _imgRecord.frame.size.width / 2 - w / 2,_imgRecord.frame.origin.y + _imgRecord.frame.size.height / 2 - h / 2, w, h)];
        progress.backgroundColor = [UIColor clearColor];
        progress.timeMax = self.seconds;
        progress.lineColor = [UIColor greenColor];
        _progressView = progress;
    }
    return _progressView;
}

- (UIButton *)btnBack{
    if(!_btnBack){
        CGFloat w = 40.0;
        CGFloat h = 40.0;
        
        UIButton *btnBack = [UIButton buttonWithType:UIButtonTypeCustom];
        btnBack.backgroundColor = [UIColor clearColor];
        btnBack.frame = CGRectMake(20.0, 20.0, w, h);
        [btnBack setImage:[UIImage imageNamed:@"gVideo_back@2x.png"] forState:UIControlStateNormal];
        [btnBack addTarget:self action:@selector(onCancelAction:) forControlEvents:UIControlEventTouchUpInside];
        self.btnBack = btnBack;
    }
    return _btnBack;
}

- (UIButton *)btnAfresh{
    if(!_btnAfresh){
        UIButton *btnAfresh = [UIButton buttonWithType:UIButtonTypeCustom];
        btnAfresh.backgroundColor = [UIColor clearColor];
        CGFloat w = 60.0;
        CGFloat h = 60.0;
        
        btnAfresh.frame = CGRectMake(40.0, _imgRecord.frame.origin.y + _imgRecord.frame.size.height / 2 - h / 2, w, h);
        btnAfresh.backgroundColor = [UIColor clearColor];
        [btnAfresh setImage:[UIImage imageNamed:@"SE_quxiao@3x.png"] forState:UIControlStateNormal];
        [btnAfresh addTarget:self action:@selector(onAfreshAction:) forControlEvents:UIControlEventTouchUpInside];
        self.btnAfresh = btnAfresh;
    }
    return _btnAfresh;
}

- (UIButton *)btnEnter{
    if(!_btnEnter){
        CGFloat w = 60.0;
        CGFloat h = 60.0;
        
        UIButton *btnEnter = [UIButton buttonWithType:UIButtonTypeCustom];
        btnEnter.frame = CGRectMake(self.view.frame.size.width - w - 40.0, _imgRecord.frame.origin.y + _imgRecord.frame.size.height / 2 - h / 2, w, h);
        btnEnter.backgroundColor = [UIColor clearColor];
        [btnEnter setImage:[UIImage imageNamed:@"SX_confirm@3x.png"] forState:UIControlStateNormal];
        [btnEnter addTarget:self action:@selector(onEnsureAction:) forControlEvents:UIControlEventTouchUpInside];
        self.btnEnter = btnEnter;
    }
    return _btnEnter;
}

- (UIButton *)btnCamera{
    if(!_btnCamera){
        UIButton *btnCamera = [UIButton buttonWithType:UIButtonTypeCustom];
        btnCamera.backgroundColor = [UIColor clearColor];
        btnCamera.frame = CGRectMake(self.view.frame.size.width - 20.0 - 40.0, 20.0, 40.0, 40.0);
        [btnCamera setImage:[UIImage imageNamed:@"btn_video_flip_camera@2x.png"] forState:UIControlStateNormal];
        [btnCamera addTarget:self action:@selector(onCameraAction:) forControlEvents:UIControlEventTouchUpInside];
        self.btnCamera = btnCamera;
    }
    return _btnCamera;
}

- (UIButton *)pickAlbumnBtn{
    if(!_pickAlbumnBtn){
        UIButton *albumnBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        albumnBtn.backgroundColor = [UIColor clearColor];
        [albumnBtn addTarget:self action:@selector(albumnBtnMethod:) forControlEvents:UIControlEventTouchUpInside];
        _pickAlbumnBtn = albumnBtn;
    }
    return _pickAlbumnBtn;
}





#pragma mark


/**
 *
 * 1、核心是自定义相机
 *      1> 初始化捕捉设备 AVCaptureSession - 会话
 *      2> 对设备进行设置，例如分辨率等
 *      3> 获取硬件设备，摄像头 - AVCaptureDevice
 *    ps: 4> 这里需要录制视频，所以还要加上获取音频设备 - audioCaptureDevice
 *      5> AVCaptureDeviceInput - 初始化设备输入
 *      6> 初始化设备输出  -  AVCaptureMovieFileOutput
 *    ps: 可以设置 防抖设置、 图层等
 *      7> 设备输入、输出添加到会话
 *    PS: 想要设置对焦、录制视频，需要给设备增加通知，实时监听捕捉画面的移动！
 */

#pragma mark - 自定义相机核心
- (void)customCamera {
    
    //初始化会话，用来结合输入输出
    self.session = [[AVCaptureSession alloc] init];
    //设置分辨率 (设备支持的最高分辨率)
    if ([self.session canSetSessionPreset:AVCaptureSessionPreset1280x720]) {
        self.session.sessionPreset = AVCaptureSessionPreset1280x720;
    }
    //取得后置摄像头
    AVCaptureDevice *captureDevice = [self getCameraDeviceWithPosition:AVCaptureDevicePositionBack];
    
    
    //添加一个音频输入设备
    AVCaptureDevice *audioCaptureDevice;
    if (@available(iOS 10.0, *)) {
        //   改版后的新方法
        AVCaptureDeviceDiscoverySession *audioCaptureDeviceSession = [AVCaptureDeviceDiscoverySession discoverySessionWithDeviceTypes:@[AVCaptureDeviceTypeBuiltInMicrophone] mediaType:AVMediaTypeAudio position:0];
        NSArray *devicesIOS  = audioCaptureDeviceSession.devices;
        audioCaptureDevice = devicesIOS.lastObject;
    } else {
        
        //添加一个音频输入设备
        audioCaptureDevice= [[AVCaptureDevice devicesWithMediaType:AVMediaTypeAudio] firstObject];
        
    }
    
    //初始化输入设备
    NSError *error = nil;
    self.captureDeviceInput = [[AVCaptureDeviceInput alloc] initWithDevice:captureDevice error:&error];
    if (error) {
        NSLog(@"取得设备输入对象时出错，错误原因：%@",error.localizedDescription);
        return;
    }
    
    //添加音频
    error = nil;
    AVCaptureDeviceInput *audioCaptureDeviceInput=[[AVCaptureDeviceInput alloc]initWithDevice:audioCaptureDevice error:&error];
    if (error) {
        NSLog(@"取得设备输入对象时出错，错误原因：%@",error.localizedDescription);
        return;
    }
    
    //输出对象
    self.captureMovieFileOutput = [[AVCaptureMovieFileOutput alloc] init];//视频输出
    
    //将输入设备添加到会话
    if ([self.session canAddInput:self.captureDeviceInput]) {
        [self.session addInput:self.captureDeviceInput];
        [self.session addInput:audioCaptureDeviceInput];
        
        //设置视频防抖
        AVCaptureConnection *connection = [self.captureMovieFileOutput connectionWithMediaType:AVMediaTypeVideo];
        
        if ([connection isVideoStabilizationSupported]) {
            connection.preferredVideoStabilizationMode = AVCaptureVideoStabilizationModeCinematic;
        }
    }
    
    //将输出设备添加到会话 (刚开始 是照片为输出对象)
    if ([self.session canAddOutput:self.captureMovieFileOutput]) {
        [self.session addOutput:self.captureMovieFileOutput];
    }
    
    //创建视频预览层，用于实时展示摄像头状态
    self.previewLayer = [[AVCaptureVideoPreviewLayer alloc] initWithSession:self.session];
    self.previewLayer.frame = self.view.bounds;//CGRectMake(0, 0, self.view.width, self.view.height);
    self.previewLayer.videoGravity = AVLayerVideoGravityResizeAspectFill;//填充模式
    [self.bgView.layer addSublayer:self.previewLayer];
    
    [self addNotificationToCaptureDevice:captureDevice];
    [self addGenstureRecognizer];
}


#pragma mark - x退出按钮
- (void)onCancelAction:(UIButton *)sender {
    [self dismissViewControllerAnimated:YES completion:^{
        
        //         TODO:  隐藏进度
        
    }];
}

#pragma mark - 录制视频 - touch按钮
- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    if ([[touches anyObject] view] == self.imgRecord) {
        NSLog(@"开始录制");
        //根据设备输出获得连接
        AVCaptureConnection *connection = [self.captureMovieFileOutput connectionWithMediaType:AVMediaTypeAudio];
        //根据连接取得设备输出的数据
        if (![self.captureMovieFileOutput isRecording]) {
            //如果支持多任务则开始多任务
            if ([[UIDevice currentDevice] isMultitaskingSupported]) {
                self.backgroundTaskIdentifier = [[UIApplication sharedApplication] beginBackgroundTaskWithExpirationHandler:nil];
            }
            if (self.saveVideoUrl) {
                [[NSFileManager defaultManager] removeItemAtURL:self.saveVideoUrl error:nil];
            }
            //预览图层和视频方向保持一致
            connection.videoOrientation = [self.previewLayer connection].videoOrientation;
            NSString *outputFielPath=[NSTemporaryDirectory() stringByAppendingString:@"myMovie.mov"];
            NSLog(@"save path is :%@",outputFielPath);
            NSURL *fileUrl=[NSURL fileURLWithPath:outputFielPath];
            NSLog(@"fileUrl:%@",fileUrl);
            //             开始录制视频
            [self.captureMovieFileOutput startRecordingToOutputFileURL:fileUrl recordingDelegate:self];
        } else {
            [self.captureMovieFileOutput stopRecording];
        }
    }
}


- (void)touchesEnded:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    if ([[touches anyObject] view] == self.imgRecord) {
        NSLog(@"结束触摸");
        if (!self.isVideo) {
            [self performSelector:@selector(endRecord) withObject:nil afterDelay:0.3];
        } else {
            [self endRecord];
        }
    }
}

#pragma mark - 停止录制
- (void)endRecord {
    [self.captureMovieFileOutput stopRecording];//停止录制
}

- (void)onAfreshAction:(UIButton *)sender {
    [self recoverLayout];
}


#pragma mark - 点击确定 - 保存图片或者视频
- (void)onEnsureAction:(UIButton *)sender {
    
    if (self.saveVideoUrl) {
        
        typeof(self) weakSelf = self;
        
        if (weakSelf.takeBlock) {
            weakSelf.takeBlock(self.saveVideoUrl,[self getVideoIconWithUrl:self.saveVideoUrl]);
        }        [weakSelf onCancelAction:nil];
    }
    else {
        //照片
        UIImageWriteToSavedPhotosAlbum(self.takeImage, self, nil, nil);
        if (self.takeBlock) {
            self.takeBlock(self.takeImage,nil);
        }
        
        [self onCancelAction:nil];
    }
}

//前后摄像头的切换
- (void)onCameraAction:(UIButton *)sender {
    
    AVCaptureDevice *currentDevice=[self.captureDeviceInput device];
    AVCaptureDevicePosition currentPosition=[currentDevice position];
    [self removeNotificationFromCaptureDevice:currentDevice];
    AVCaptureDevice *toChangeDevice;
    AVCaptureDevicePosition toChangePosition = AVCaptureDevicePositionFront;//前
    if (currentPosition == AVCaptureDevicePositionUnspecified || currentPosition == AVCaptureDevicePositionFront) {
        toChangePosition = AVCaptureDevicePositionBack;//后
    }
    toChangeDevice=[self getCameraDeviceWithPosition:toChangePosition];
    [self addNotificationToCaptureDevice:toChangeDevice];
    //获得要调整的设备输入对象
    AVCaptureDeviceInput *toChangeDeviceInput=[[AVCaptureDeviceInput alloc]initWithDevice:toChangeDevice error:nil];
    
    //改变会话的配置前一定要先开启配置，配置完成后提交配置改变
    [self.session beginConfiguration];
    //移除原有输入对象
    [self.session removeInput:self.captureDeviceInput];
    //添加新的输入对象
    if ([self.session canAddInput:toChangeDeviceInput]) {
        [self.session addInput:toChangeDeviceInput];
        self.captureDeviceInput = toChangeDeviceInput;
    }
    //提交会话配置
    [self.session commitConfiguration];
}


#pragma mark - 录制视频 - 记录秒数，q超过强制停止
- (void)onStartTranscribe:(NSURL *)fileURL {
    if ([self.captureMovieFileOutput isRecording]) {
        -- self.seconds;
        if (self.seconds > 0) {
            if (self.GJSeconds - self.seconds >= TimeMax && !self.isVideo) {
                self.isVideo = YES;//长按时间超过TimeMax 表示是视频录制
                self.progressView.timeMax = self.seconds;
            }
            [self performSelector:@selector(onStartTranscribe:) withObject:fileURL afterDelay:1.0];
        } else {
            if ([self.captureMovieFileOutput isRecording]) {
                [self.captureMovieFileOutput stopRecording];
            }
        }
    }
}


#pragma mark - 视频输出代理
-(void)captureOutput:(AVCaptureFileOutput *)captureOutput didStartRecordingToOutputFileAtURL:(NSURL *)fileURL fromConnections:(NSArray *)connections{
    self.seconds = self.GJSeconds;
    [self performSelector:@selector(onStartTranscribe:) withObject:fileURL afterDelay:1.0];
}

#pragma mark - 视频录制完成后
-(void)captureOutput:(AVCaptureFileOutput *)captureOutput didFinishRecordingToOutputFileAtURL:(NSURL *)outputFileURL fromConnections:(NSArray *)connections error:(NSError *)error{
    [self changeLayout];
    if (self.isVideo) {
        self.saveVideoUrl = outputFileURL;
        if (!self.player) {
            self.player = [[GJAVPlayer alloc] initWithFrame:self.bgView.bounds withShowInView:self.bgView url:outputFileURL];
        } else {
            if (outputFileURL) {
                self.player.videoUrl = outputFileURL;
                self.player.hidden = NO;
            }
        }
    }
    else {
        //照片
        self.saveVideoUrl = nil;
        [self videoHandlePhoto:outputFileURL];
    }
    
}

#pragma mark - 处理图片
- (void)videoHandlePhoto:(NSURL *)url {
    AVURLAsset *urlSet = [AVURLAsset assetWithURL:url];
    AVAssetImageGenerator *imageGenerator = [AVAssetImageGenerator assetImageGeneratorWithAsset:urlSet];
    imageGenerator.appliesPreferredTrackTransform = YES;    // 截图的时候调整到正确的方向
    NSError *error = nil;
    CMTime time = CMTimeMake(0,30);//缩略图创建时间 CMTime是表示电影时间信息的结构体，第一个参数表示是视频第几秒，第二个参数表示每秒帧数.(如果要获取某一秒的第几帧可以使用CMTimeMake方法)
    CMTime actucalTime; //缩略图实际生成的时间
    CGImageRef cgImage = [imageGenerator copyCGImageAtTime:time actualTime:&actucalTime error:&error];
    if (error) {
        //  NSLog(@"截取视频图片失败:%@",error.localizedDescription);
    }
    CMTimeShow(actucalTime);
    UIImage *image = [UIImage imageWithCGImage:cgImage];
    
    CGImageRelease(cgImage);
    if (image) {
        //  NSLog(@"视频截取成功");
    } else {
        //  NSLog(@"视频截取失败");
    }
    
    
    self.takeImage = image;//[UIImage imageWithCGImage:cgImage];
    
    [[NSFileManager defaultManager] removeItemAtURL:url error:nil];
    
    if (!self.takeImageView) {
        self.takeImageView = [[UIImageView alloc] initWithFrame:self.view.frame];
        [self.bgView addSubview:self.takeImageView];
    }
    self.takeImageView.hidden = NO;
    self.takeImageView.image = self.takeImage;
}

- (UIImage *)getVideoIconWithUrl:(NSURL *)url{
    AVURLAsset *urlSet = [AVURLAsset assetWithURL:url];
    AVAssetImageGenerator *imageGenerator = [AVAssetImageGenerator assetImageGeneratorWithAsset:urlSet];
    imageGenerator.appliesPreferredTrackTransform = YES;    // 截图的时候调整到正确的方向
    NSError *error = nil;
    CMTime time = CMTimeMake(0,30);//缩略图创建时间 CMTime是表示电影时间信息的结构体，第一个参数表示是视频第几秒，第二个参数表示每秒帧数.(如果要获取某一秒的第几帧可以使用CMTimeMake方法)
    CMTime actucalTime; //缩略图实际生成的时间
    CGImageRef cgImage = [imageGenerator copyCGImageAtTime:time actualTime:&actucalTime error:&error];
    if (error) {
        NSLog(@"截取视频图片失败:%@",error.localizedDescription);
    }
    CMTimeShow(actucalTime);
    UIImage *image = [UIImage imageWithCGImage:cgImage];
    
    CGImageRelease(cgImage);
    if (image) {
        // NSLog(@"视频截取成功");
    } else {
        //  NSLog(@"视频截取失败");
    }
    return image;
}

#pragma mark - 通知

//注册通知
- (void)setupObservers
{
    NSNotificationCenter *notification = [NSNotificationCenter defaultCenter];
    [notification addObserver:self selector:@selector(applicationDidEnterBackground:) name:UIApplicationWillResignActiveNotification object:[UIApplication sharedApplication]];
}

//进入后台就退出视频录制
- (void)applicationDidEnterBackground:(NSNotification *)notification {
    [self onCancelAction:nil];
}

/**
 *  给输入设备添加通知
 */
-(void)addNotificationToCaptureDevice:(AVCaptureDevice *)captureDevice{
    //注意添加区域改变捕获通知必须首先设置设备允许捕获
    [self changeDeviceProperty:^(AVCaptureDevice *captureDevice) {
        captureDevice.subjectAreaChangeMonitoringEnabled=YES;
    }];
    NSNotificationCenter *notificationCenter= [NSNotificationCenter defaultCenter];
    //捕获区域发生改变
    [notificationCenter addObserver:self selector:@selector(areaChange:) name:AVCaptureDeviceSubjectAreaDidChangeNotification object:captureDevice];
}

#pragma mark - 移除当前通知
-(void)removeNotificationFromCaptureDevice:(AVCaptureDevice *)captureDevice{
    NSNotificationCenter *notificationCenter= [NSNotificationCenter defaultCenter];
    [notificationCenter removeObserver:self name:AVCaptureDeviceSubjectAreaDidChangeNotification object:captureDevice];
}
/**
 *  移除所有通知
 */
-(void)removeNotification{
    NSNotificationCenter *notificationCenter= [NSNotificationCenter defaultCenter];
    [notificationCenter removeObserver:self];
}

-(void)addNotificationToCaptureSession:(AVCaptureSession *)captureSession{
    NSNotificationCenter *notificationCenter= [NSNotificationCenter defaultCenter];
    //会话出错
    [notificationCenter addObserver:self selector:@selector(sessionRuntimeError:) name:AVCaptureSessionRuntimeErrorNotification object:captureSession];
}

/**
 *  设备连接成功
 *
 *  @param notification 通知对象
 */
-(void)deviceConnected:(NSNotification *)notification{
    NSLog(@"设备已连接...");
}
/**
 *  设备连接断开
 *
 *  @param notification 通知对象
 */
-(void)deviceDisconnected:(NSNotification *)notification{
    NSLog(@"设备已断开.");
}
/**
 *  捕获区域改变
 *
 *  @param notification 通知对象
 */
-(void)areaChange:(NSNotification *)notification{
    NSLog(@"捕获区域改变...");
}

/**
 *  会话出错
 *
 *  @param notification 通知对象
 */
-(void)sessionRuntimeError:(NSNotification *)notification{
    NSLog(@"会话发生错误.");
}



/**
 *  取得指定位置的摄像头
 *
 *  @param position 摄像头位置
 *
 *  @return 摄像头设备
 */
-(AVCaptureDevice *)getCameraDeviceWithPosition:(AVCaptureDevicePosition )position{
    NSArray *cameras= [AVCaptureDevice devicesWithMediaType:AVMediaTypeVideo];
    for (AVCaptureDevice *camera in cameras) {
        if ([camera position] == position) {
            return camera;
        }
    }
    return nil;
}

/**
 *  改变设备属性的统一操作方法
 *
 *  @param propertyChange 属性改变操作
 */
-(void)changeDeviceProperty:(PropertyChangeBlock)propertyChange{
    
    AVCaptureDevice *captureDevice= [self.captureDeviceInput device];
    NSError *error;
    //注意改变设备属性前一定要首先调用lockForConfiguration:调用完之后使用unlockForConfiguration方法解锁
    if ([captureDevice lockForConfiguration:&error]) {
        //自动白平衡
        if ([captureDevice isWhiteBalanceModeSupported:AVCaptureWhiteBalanceModeContinuousAutoWhiteBalance]) {
            [captureDevice setWhiteBalanceMode:AVCaptureWhiteBalanceModeContinuousAutoWhiteBalance];
        }
        //自动根据环境条件开启闪光灯
        if ([captureDevice isFlashModeSupported:AVCaptureFlashModeAuto]) {
            [captureDevice setFlashMode:AVCaptureFlashModeAuto];
        }
        
        propertyChange(captureDevice);
        [captureDevice unlockForConfiguration];
        
    }else{
        NSLog(@"设置设备属性过程发生错误，错误信息：%@",error.localizedDescription);
    }
}

/**
 *  设置闪光灯模式
 *
 *  @param flashMode 闪光灯模式
 */
-(void)setFlashMode:(AVCaptureFlashMode )flashMode{
    [self changeDeviceProperty:^(AVCaptureDevice *captureDevice) {
        if ([captureDevice isFlashModeSupported:flashMode]) {
            [captureDevice setFlashMode:flashMode];
        }
    }];
}
/**
 *  设置聚焦模式
 *
 *  @param focusMode 聚焦模式
 */
-(void)setFocusMode:(AVCaptureFocusMode )focusMode{
    [self changeDeviceProperty:^(AVCaptureDevice *captureDevice) {
        if ([captureDevice isFocusModeSupported:focusMode]) {
            [captureDevice setFocusMode:focusMode];
        }
    }];
}
/**
 *  设置曝光模式
 *
 *  @param exposureMode 曝光模式
 */
-(void)setExposureMode:(AVCaptureExposureMode)exposureMode{
    [self changeDeviceProperty:^(AVCaptureDevice *captureDevice) {
        if ([captureDevice isExposureModeSupported:exposureMode]) {
            [captureDevice setExposureMode:exposureMode];
        }
    }];
}
/**
 *  设置聚焦点
 *
 *  @param point 聚焦点
 */
-(void)focusWithMode:(AVCaptureFocusMode)focusMode exposureMode:(AVCaptureExposureMode)exposureMode atPoint:(CGPoint)point{
    [self changeDeviceProperty:^(AVCaptureDevice *captureDevice) {
        //        if ([captureDevice isFocusPointOfInterestSupported]) {
        //            [captureDevice setFocusPointOfInterest:point];
        //        }
        //        if ([captureDevice isExposurePointOfInterestSupported]) {
        //            [captureDevice setExposurePointOfInterest:point];
        //        }
        if ([captureDevice isExposureModeSupported:exposureMode]) {
            [captureDevice setExposureMode:exposureMode];
        }
        if ([captureDevice isFocusModeSupported:focusMode]) {
            [captureDevice setFocusMode:focusMode];
        }
    }];
}

/**
 *  添加点按手势，点按时聚焦
 */
-(void)addGenstureRecognizer{
    UITapGestureRecognizer *tapGesture=[[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(tapScreen:)];
    [self.bgView addGestureRecognizer:tapGesture];
}

#pragma mark - 聚焦 设置
-(void)tapScreen:(UITapGestureRecognizer *)tapGesture{
    if ([self.session isRunning]) {
        CGPoint point= [tapGesture locationInView:self.bgView];
        //将UI坐标转化为摄像头坐标
        CGPoint cameraPoint= [self.previewLayer captureDevicePointOfInterestForPoint:point];
        [self setFocusCursorWithPoint:point];
        
        //        设置曝光模式和聚焦模式
        [self focusWithMode:AVCaptureFocusModeContinuousAutoFocus exposureMode:AVCaptureExposureModeContinuousAutoExposure atPoint:cameraPoint];
    }
}

/**
 *  设置聚焦光标位置
 *
 *  @param point 光标位置
 */
-(void)setFocusCursorWithPoint:(CGPoint)point{
    if (!self.isFocus) {
        self.isFocus = YES;
        self.focusCursor.center=point;
        self.focusCursor.transform = CGAffineTransformMakeScale(1.25, 1.25);
        self.focusCursor.alpha = 1.0;
        [UIView animateWithDuration:0.5 animations:^{
            self.focusCursor.transform = CGAffineTransformIdentity;
        } completion:^(BOOL finished) {
            [self performSelector:@selector(onHiddenFocusCurSorAction) withObject:nil afterDelay:0.5];
        }];
    }
}

#pragma mark - 隐藏光标
- (void)onHiddenFocusCurSorAction {
    self.focusCursor.alpha=0;
    self.isFocus = NO;
}

//拍摄完成时调用
- (void)changeLayout {
    self.imgRecord.hidden = YES;
    self.btnCamera.hidden = YES;
    self.btnAfresh.hidden = NO;
    self.btnEnter.hidden = NO;
    self.btnBack.hidden = YES;
    //   self.pickAlbumnBtn.hidden = YES;
    if (self.isVideo) {
        [self.progressView clearProgress];
    }
    self.afreshCenterX.constant = -(GJScreenW/2/2);
    self.enterCenterX.constant = GJScreenW/2/2;
    [UIView animateWithDuration:0.25 animations:^{
        [self.view layoutIfNeeded];
    }];
    
    self.lastBackgroundTaskIdentifier = self.backgroundTaskIdentifier;
    self.backgroundTaskIdentifier = UIBackgroundTaskInvalid;
    [self.session stopRunning];
}


//重新拍摄时调用
- (void)recoverLayout {
    if (self.isVideo) {
        self.isVideo = NO;
        [self.player stopPlayer];
        self.player.hidden = YES;
    }
    [self.session startRunning];
    
    if (!self.takeImageView.hidden) {
        self.takeImageView.hidden = YES;
    }
    //    self.saveVideoUrl = nil;
    //  self.pickAlbumnBtn.hidden = NO;
    self.afreshCenterX.constant = 0;
    self.enterCenterX.constant = 0;
    self.imgRecord.hidden = NO;
    self.btnCamera.hidden = NO;
    self.btnAfresh.hidden = YES;
    self.btnEnter.hidden = YES;
    self.btnBack.hidden = NO;
    [UIView animateWithDuration:0.25 animations:^{
        [self.view layoutIfNeeded];
    }];
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

