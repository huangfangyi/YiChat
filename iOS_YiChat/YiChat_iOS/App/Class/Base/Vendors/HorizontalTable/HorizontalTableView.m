//
//  HorizontalTableView.m
//  Scroller
//
//  Created by Martin Volerich on 5/22/10.
//  Copyright 2010 Martin Volerich - Bill Bear Technologies. All rights reserved.
//

// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
// documentation files (the "Software"), to deal in the Software without restriction, including without
// limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
// the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
// conditions:
// 
// The above copyright notice and this permission notice shall be included in all copies or substantial
// portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
// LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
// EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
// AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
// OR OTHER DEALINGS IN THE SOFTWARE.

#import "HorizontalTableView.h"
#import <Foundation/Foundation.h>

#define kColumnPoolSize 1


@interface HorizontalTableView() <UIScrollViewDelegate>

@property (nonatomic, retain) NSMutableArray *pageViews;
@property (nonatomic, retain) UIScrollView *scrollView;
@property (nonatomic, readonly) NSUInteger currentPageIndex;
@property (nonatomic) NSUInteger physicalPageIndex;
@property (nonatomic, retain) NSMutableArray *columnPool;

- (void)prepareView;
- (void)layoutPages;
- (void)currentPageIndexDidChange;
- (NSUInteger)numberOfPages;
- (void)layoutPhysicalPage:(NSUInteger)pageIndex;
- (UIView *)viewForPhysicalPage:(NSUInteger)pageIndex;
- (void)removeColumn:(NSInteger)index;

@end
@implementation HorizontalTableView

@synthesize pageViews=_pageViews;
@synthesize scrollView=_scrollView;
@synthesize currentPageIndex=_currentPageIndex;
@synthesize delegate=_delegate;
@synthesize columnPool=_columnPool;
@synthesize page;


- (void)refreshData {
    self.pageViews = [NSMutableArray array];
	// to save time and memory, we won't load the page views immediately
	NSUInteger numberOfPhysicalPages = [self numberOfPages];
	for (NSUInteger i = 0; i < numberOfPhysicalPages; ++i)
		[self.pageViews addObject:[NSNull null]];
    
    [self setNeedsLayout];
}

- (NSUInteger)numberOfPages {
	NSInteger numPages = 0;
    if (_delegate)
        numPages = [_delegate numberOfColumnsForTableView:self];
    return numPages;
}

- (UIView *)viewForPhysicalPage:(NSUInteger)pageIndex {
	NSParameterAssert(pageIndex >= 0);
	NSParameterAssert(pageIndex < [self.pageViews count]);
	
	UIView *pageView;
	if ([self.pageViews objectAtIndex:pageIndex] == [NSNull null]) {
        
        if (_delegate) {
            pageView = [_delegate tableView:self viewForIndex:pageIndex];
            [self.pageViews replaceObjectAtIndex:pageIndex withObject:pageView];
            [self.scrollView addSubview:pageView];
        //    DLog(@"View loaded for page %d", pageIndex);
            self.page=[NSString stringWithFormat:@"%lu",(unsigned long)pageIndex];
        }
	} else {
		pageView = [self.pageViews objectAtIndex:pageIndex];
	}
	return pageView;
}

- (CGSize)pageSize {
    CGRect rect = self.scrollView.bounds;
	return rect.size;

    
    
}

- (CGFloat)columnWidth {
    if (!_columnWidth) {
        if (_delegate) {
            CGFloat width = [_delegate columnWidthForTableView:self];
            _columnWidth = [[NSNumber numberWithFloat:width] retain];
        }
    }
    return [_columnWidth floatValue];

}

- (BOOL)isPhysicalPageLoaded:(NSUInteger)pageIndex {
	return [self.pageViews objectAtIndex:pageIndex] != [NSNull null];
}

- (void)layoutPhysicalPage:(NSUInteger)pageIndex {
	UIView *pageView = [self viewForPhysicalPage:pageIndex];
    CGFloat viewWidth = pageView.bounds.size.width;
	CGSize pageSize = [self pageSize];
    
    CGRect rect = CGRectMake(viewWidth * pageIndex, 0, viewWidth, pageSize.height);
	pageView.frame = rect;
}

- (void)awakeFromNib {
    [self prepareView];
}

- (void)queueColumnView:(UIView *)vw {
    if ([self.columnPool count] >= kColumnPoolSize) {
        return;
    }
    [self.columnPool addObject:vw];
}

- (UIView *)dequeueColumnView {
    UIView *vw = [[self.columnPool lastObject] retain];
    if (vw) {
        [self.columnPool removeLastObject];
   //     DLog(@"Supply from reuse pool");
    }
    return [vw autorelease];
}

- (void)removeColumn:(NSInteger)index {
    if ([self.pageViews objectAtIndex:index] != [NSNull null]) {
     //   DLog(@"Removing view at position %d", index);
        UIView *vw = [self.pageViews objectAtIndex:index];
        [self queueColumnView:vw];
        [vw removeFromSuperview];
        [self.pageViews replaceObjectAtIndex:index withObject:[NSNull null]];
    }
}

- (void)currentPageIndexDidChange {
    CGSize pageSize = [self pageSize];
    CGFloat columnWidth = [self columnWidth];
    _visibleColumnCount = pageSize.width / columnWidth + 2;
    
    NSInteger leftMostPageIndex = -1;
    NSInteger rightMostPageIndex = 0;
    
    for (NSInteger i = -2; i < _visibleColumnCount; i++) {
        NSInteger index = _currentPhysicalPageIndex + i;
        if (index < [self.pageViews count] && (index >= 0)) {
            [self layoutPhysicalPage:index];
            if (leftMostPageIndex < 0)
                leftMostPageIndex = index;
            rightMostPageIndex = index;
        }
    }
    
    // clear out views to the left
    for (NSInteger i = 0; i < leftMostPageIndex; i++) {
        [self removeColumn:i];
    }
    
    // clear out views to the right
    for (NSInteger i = rightMostPageIndex + 1; i < [self.pageViews count]; i++) {
        [self removeColumn:i];
    }

 
}

- (void)layoutPages {
    CGSize pageSize = self.bounds.size;
	self.scrollView.contentSize = CGSizeMake([self.pageViews count] * [self columnWidth], pageSize.height);
}

- (id)init {
    self = [super init];
    if (self) {
        [self prepareView];
    }
    return self;
}
- (id)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        [self prepareView];
     
    }
    return self;
}
- (void)prepareView {
	_columnPool = [[NSMutableArray alloc] initWithCapacity:kColumnPoolSize];
    _columnWidth = nil;
    [self setClipsToBounds:YES];
    
    self.autoresizesSubviews = YES;
    
    UIScrollView *scroller = [[UIScrollView alloc] init];
    CGRect rect = self.bounds;
    scroller.frame = rect;
    scroller.backgroundColor = [UIColor clearColor];
	scroller.delegate = self;
    scroller.autoresizesSubviews = YES;
    scroller.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    
	//self.scrollView.pagingEnabled = YES;
    scroller.pagingEnabled=YES;
	scroller.showsHorizontalScrollIndicator = YES;
	scroller.showsVerticalScrollIndicator = NO;
    scroller.alwaysBounceVertical = NO;
    self.scrollView = scroller;
	[self addSubview:scroller];
    [scroller release];
    scroller = nil;
    
 
  //  [self performSelector:@selector(delay) withObject:nil afterDelay:0.5];
}
//- (void)delay
//{
//    if(_cBussniessSocialVC)
//    {
//        [self.scrollView setContentOffset:CGPointMake(0, 0) animated:NO];
//    }
//
//    if(_cThinkTankVC)
//    {
//       [self.scrollView setContentOffset:CGPointMake(0, 0) animated:NO];
//    }
//
//}
- (void)changeScrollViewContentOffSetX:(CGFloat)contentOffset
{
    
    [UIView animateWithDuration:0.5 animations:^{
        _scrollView.contentOffset=CGPointMake(contentOffset, 0);
    }];
}

- (NSUInteger)physicalPageIndex {
    NSUInteger tempPage = self.scrollView.contentOffset.x / [self columnWidth];
    return tempPage;
}

- (void)setPhysicalPageIndex:(NSUInteger)newIndex {
	self.scrollView.contentOffset = CGPointMake(newIndex * [self pageSize].width, 0);
}


#pragma mark -
#pragma mark UIScrollViewDelegate methods

- (void)scrollViewDidScroll:(UIScrollView *)scrollView {
    //DLog(@"Did Scroll");
	
	NSUInteger newPageIndex = self.physicalPageIndex;
	if (newPageIndex == _currentPhysicalPageIndex) return;
	_currentPhysicalPageIndex = newPageIndex;
	_currentPageIndex = newPageIndex;
	
	[self currentPageIndexDidChange];
   
}

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate {

}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView {
	//DLog(@"scrollViewDidEndDecelerating");
    self.offSetX=_scrollView.contentOffset.x;
    
//    if(self.cBussniessSocialVC.isInScreen == YES){
//        NSLog(@"%f",self.offSetX);
//        
//        if(((int)self.offSetX) / (int)(KGENERAL_SIZE_WIDTH) == 0)
//        {
//            [self.cBussniessSocialVC yrBussinessHeaderSelecteRow:0];
//        }
//        else if(((int)self.offSetX) / (int)(KGENERAL_SIZE_WIDTH) == 1)
//        {
//            [self.cBussniessSocialVC yrBussinessHeaderSelecteRow:1];
//        }
//        else if(((int)self.offSetX) / (int)(KGENERAL_SIZE_WIDTH) == 2)
//        {
//            [self.cBussniessSocialVC yrBussinessHeaderSelecteRow:2];
//        }
//        else if(((int)self.offSetX) / (int)(KGENERAL_SIZE_WIDTH) == 3)
//        {
//            [self.cBussniessSocialVC yrBussinessHeaderSelecteRow:3];
//        }
//        
//    }
//     if(self.cThinkTankVC.isInScreen == YES){
//        if(((int)self.offSetX) / (int)(KGENERAL_SIZE_WIDTH) == 0)
//        {
//            [self.cThinkTankVC yrBussinessHeaderSelecteRow:0];
//        }
//        else if(((int)self.offSetX) / (int)(KGENERAL_SIZE_WIDTH) == 1)
//        {
//            [self.cThinkTankVC yrBussinessHeaderSelecteRow:1];
//        }
//        else if(((int)self.offSetX) / (int)(KGENERAL_SIZE_WIDTH) == 2)
//        {
//            [self.cThinkTankVC yrBussinessHeaderSelecteRow:2];
//        }
//        else if(((int)self.offSetX) / (int)(KGENERAL_SIZE_WIDTH) == 3)
//        {
//            [self.cThinkTankVC yrBussinessHeaderSelecteRow:3];
//        }
//
//    }

}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
    return (interfaceOrientation != UIInterfaceOrientationPortraitUpsideDown);
}


- (void)layoutSubviews {
    [self layoutPages];
    [self currentPageIndexDidChange];
}

- (void)didRotateFromInterfaceOrientation:(UIInterfaceOrientation)fromInterfaceOrientation {
	// adjust frames according to the new page size - this does not cause any visible changes
	[self layoutPages];
	self.physicalPageIndex = _currentPhysicalPageIndex;
	
	// unhide
	for (NSUInteger pageIndex = 0; pageIndex < [self.pageViews count]; ++pageIndex)
		if ([self isPhysicalPageLoaded:pageIndex])
			[self viewForPhysicalPage:pageIndex].hidden = NO;
	
    self.scrollView.contentSize = CGSizeMake([self.pageViews count] * [self columnWidth], [self pageSize].height);

    [self currentPageIndexDidChange];
}

#pragma mark dealloc 方法
- (void)dealloc {
    [_columnPool release], _columnPool = nil;
    [_columnWidth release], _columnWidth = nil;
    [_pageViews release], _pageViews = nil;
    [_scrollView release], _scrollView = nil;
    
    /*
    if(_myInverst)
    {
        [self removeObserver:_myInverst forKeyPath:@"offSetX"];
    }
    if(_inverst)
    {
        [self removeObserver:_inverst forKeyPath:@"offSetX"];
    }
    if(_trading)
    {
        [self removeObserver:_trading forKeyPath:@"offSetX"];
    }
    if(_credicator)
    {
        [self removeObserver:_credicator forKeyPath:@"offSetX"];
    }
    */
    [super dealloc];
}

@end
