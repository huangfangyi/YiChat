
#import <Foundation/Foundation.h>
#import <objc/runtime.h>

NS_ASSUME_NONNULL_BEGIN


typedef NS_OPTIONS(NSUInteger, QSEncodingType) {
    QSEncodingTypeMask       = 0xFF, ///< mask of type value
    QSEncodingTypeUnknown    = 0, ///< unknown
    QSEncodingTypeVoid       = 1, ///< void
    QSEncodingTypeBool       = 2, ///< bool
    QSEncodingTypeInt8       = 3, ///< char / BOOL
    QSEncodingTypeUInt8      = 4, ///< unsigned char
    QSEncodingTypeInt16      = 5, ///< short
    QSEncodingTypeUInt16     = 6, ///< unsigned short
    QSEncodingTypeInt32      = 7, ///< int
    QSEncodingTypeUInt32     = 8, ///< unsigned int
    QSEncodingTypeInt64      = 9, ///< long long
    QSEncodingTypeUInt64     = 10, ///< unsigned long long
    QSEncodingTypeFloat      = 11, ///< float
    QSEncodingTypeDouble     = 12, ///< double
    QSEncodingTypeLongDouble = 13, ///< long double
    QSEncodingTypeObject     = 14, ///< id
    QSEncodingTypeClass      = 15, ///< Class
    QSEncodingTypeSEL        = 16, ///< SEL
    QSEncodingTypeBlock      = 17, ///< block
    QSEncodingTypePointer    = 18, ///< void*
    QSEncodingTypeStruct     = 19, ///< struct
    QSEncodingTypeUnion      = 20, ///< union
    QSEncodingTypeCString    = 21, ///< char*
    QSEncodingTypeCArray     = 22, ///< char[10] (for example)
    
    QSEncodingTypeQualifierMask   = 0xFF00,   ///< mask of qualifier
    QSEncodingTypeQualifierConst  = 1 << 8,  ///< const
    QSEncodingTypeQualifierIn     = 1 << 9,  ///< in
    QSEncodingTypeQualifierInout  = 1 << 10, ///< inout
    QSEncodingTypeQualifierOut    = 1 << 11, ///< out
    QSEncodingTypeQualifierBycopy = 1 << 12, ///< bycopy
    QSEncodingTypeQualifierByref  = 1 << 13, ///< byref
    QSEncodingTypeQualifierOneway = 1 << 14, ///< oneway
    
    QSEncodingTypePropertyMask         = 0xFF0000, ///< mask of property
    QSEncodingTypePropertyReadonly     = 1 << 16, ///< readonly
    QSEncodingTypePropertyCopy         = 1 << 17, ///< copy
    QSEncodingTypePropertyRetain       = 1 << 18, ///< retain
    QSEncodingTypePropertyNonatomic    = 1 << 19, ///< nonatomic
    QSEncodingTypePropertyWeak         = 1 << 20, ///< weak
    QSEncodingTypePropertyCustomGetter = 1 << 21, ///< getter=
    QSEncodingTypePropertyCustomSetter = 1 << 22, ///< setter=
    QSEncodingTypePropertyDynamic      = 1 << 23, ///< @dynamic
};


QSEncodingType QSEncodingGetType(const char *typeEncoding);


@interface QSClassIvarInfo : NSObject
@property (nonatomic, assign, readonly) Ivar ivar;              ///< ivar opaque struct
@property (nonatomic, strong, readonly) NSString *name;         ///< Ivar's name
@property (nonatomic, assign, readonly) ptrdiff_t offset;       ///< Ivar's offset
@property (nonatomic, strong, readonly) NSString *typeEncoding; ///< Ivar's type encoding
@property (nonatomic, assign, readonly) QSEncodingType type;    ///< Ivar's type

- (instancetype)initWithIvar:(Ivar)ivar;
@end


@interface QSClassMethodInfo : NSObject
@property (nonatomic, assign, readonly) Method method;                  ///< method opaque struct
@property (nonatomic, strong, readonly) NSString *name;                 ///< method name
@property (nonatomic, assign, readonly) SEL sel;                        ///< method's selector
@property (nonatomic, assign, readonly) IMP imp;                        ///< method's implementation
@property (nonatomic, strong, readonly) NSString *typeEncoding;         ///< method's parameter and return types
@property (nonatomic, strong, readonly) NSString *returnTypeEncoding;   ///< return value's type
@property (nullable, nonatomic, strong, readonly) NSArray<NSString *> *argumentTypeEncodings; ///< array of arguments' type

- (instancetype)initWithMethod:(Method)method;
@end

@interface QSClassPropertyInfo : NSObject
@property (nonatomic, assign, readonly) objc_property_t property; ///< property's opaque struct
@property (nonatomic, strong, readonly) NSString *name;           ///< property's name
@property (nonatomic, assign, readonly) QSEncodingType type;      ///< property's type
@property (nonatomic, strong, readonly) NSString *typeEncoding;   ///< property's encoding value
@property (nonatomic, strong, readonly) NSString *ivarName;       ///< property's ivar name
@property (nullable, nonatomic, assign, readonly) Class cls;      ///< may be nil
@property (nullable, nonatomic, strong, readonly) NSArray<NSString *> *protocols; ///< may nil
@property (nonatomic, assign, readonly) SEL getter;               ///< getter (nonnull)
@property (nonatomic, assign, readonly) SEL setter;               ///< setter (nonnull)


- (instancetype)initWithProperty:(objc_property_t)property;
@end

@interface QSClassInfo : NSObject
@property (nonatomic, assign, readonly) Class cls; ///< class object
@property (nullable, nonatomic, assign, readonly) Class superCls; ///< super class object
@property (nullable, nonatomic, assign, readonly) Class metaCls;  ///< class's meta class object
@property (nonatomic, readonly) BOOL isMeta; ///< whether this class is meta class
@property (nonatomic, strong, readonly) NSString *name; ///< class name
@property (nullable, nonatomic, strong, readonly) QSClassInfo *superClassInfo; ///< super class's class info
@property (nullable, nonatomic, strong, readonly) NSDictionary<NSString *, QSClassIvarInfo *> *ivarInfos; ///< ivars
@property (nullable, nonatomic, strong, readonly) NSDictionary<NSString *, QSClassMethodInfo *> *methodInfos; ///< methods
@property (nullable, nonatomic, strong, readonly) NSDictionary<NSString *, QSClassPropertyInfo *> *propertyInfos; ///< properties

- (void)setNeedUpdate;

- (BOOL)needUpdate;

+ (nullable instancetype)classInfoWithClass:(Class)cls;

+ (nullable instancetype)classInfoWithClassName:(NSString *)className;

@end

NS_ASSUME_NONNULL_END
