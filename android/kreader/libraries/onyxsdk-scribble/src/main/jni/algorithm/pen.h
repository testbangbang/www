#ifndef    ANDROID_COLORPEN_H_
#define    ANDROID_COLORPEN_H_

/* -------------------导出函数------------- */
//#ifdef __cplusplus
//extern "C"{
//#endif

    /**
     * 画图预处理函数
     * 调用时机:在画图窗口建立时，调用此函数
     * 
     * @param nWight
     *            画图区域的宽度
     * @param nHeight
     *            画图区域的高度
     * @param pDrawMemory
     *            图片原始像素
     * @return 操作是否成功
     */
    bool initializeEx(int nWidth, int nHeight, int* pDrawMemory );
    
    bool initializeExByte(int nWidth, int nHeight, unsigned char *pDrawMemory );
    
    //bool initializeBitmap(Bitmap pDrawMemory);

    /**
     * 汉王画笔属性设置函数；
     * 
     * @param nAntiLevel
     *            反走样级别，范围为[0—3]，0表示不反走样,HWPEN_SetPen()函数里nPenStyle才生效,级别越高，反走样效果越好；
     * @param nPenStyle
     *            画笔风格　在不反走样时生效,1=铅笔, 2=钢笔,　3=毛笔， , 4=蜡笔
     * @param nColorType
     *            颜色类型　( 0-蓝色，1-绿色，2-青色，3-红色，4-粉红色，5-黄色，6-黑色，7-深蓝色
     *            8-橄榄色，9-浅蓝色，10-栗色，11-紫色，12-暗黄色，13-银灰色，14-黑灰色
     *            100～112-渐变1～渐变13 )
     * @param nPenWidth
     *            画笔宽度[1, 5]，值越大笔形越粗
     * @param nColorRate
     *            彩笔颜色变化速率，[10, 80]，值越大变化越快
     */
    void setPen( int nAntiLevel, int nPenStyle, int nColorType, int nPenWidth, int nColorRate );
    
    /**
     * 汉王画笔颜色函数；
     * 
     * @param color
     *            数值，个数为4，依次代表alpha，red，green，blue；
     */
    void setPenColor(int *color);

    /**
     * 采样点处理函数
     * 说明：在MouseUp也调用一次这个函数drawLine(-1，-1);，参数赋(-1,-1)表抬笔
     * 
     * @param x
     *            当前采样点 X 坐标
     * @param y
     *            当前采样点 Y 坐标
     * @param pressure
     *            当前采样点 压感值
     */
    void drawLine( int x, int y, float pressure, int *pRect, int *pDrawMemory );

    /**
     * 采样点处理函数
     * 说明：在MouseUp也调用一次这个函数drawLine(-1，-1);，参数赋(-1,-1)表抬笔
     * 
     * @param x
     *            当前采样点 X 坐标          
     * @param y
     *            当前采样点 Y 坐标           
     * @param pressure
     * 
     *            当前采样点 压感值
     * @param pRect
     *            返回值，此次对像素点的操作区域，可当作裁减区做刷新区域   
     *             
     * @param points
     *            返回值，此次插值的补点信息
     */
    void drawLineEx( int x, int y, float pressure, int *pRect, float *points );
    
    /**
     * 设置裁剪区函数
     * 说明：调用这个函数只能设置一个矩形区域的裁剪区
     * 
     * @param pRect
     *            裁剪区区域，数组依次表示为矩形的left，top，right，bottom
     */
    void setClipRegion( int *pRect );
    
    /**
     * 添加裁剪区函数
     * 说明：调用这个函数增加多个裁剪区，最多能加到256个
     * 
     * @param pRect
     *            裁剪区区域，数组依次表示为矩形的left，top，right，bottom
     */    
    void addClipRegion( int *pRect );

    /**
     * 取消裁剪区函数
     * 说明：一般是在调用setClipRegion或者addClipRegion了，重绘完成后使用
     * 
     */ 
    void reSet();

    /**
     * 将裁剪区刷成指定颜色
     * 说明：一般是在设置裁剪区之后，重绘之前调用
     * 
     * @param color
     *            指定颜色值
     */    
    void clear(int color);
 
    /**
     * 将裁剪区刷成指定背景
     * 说明：一般是在设置裁剪区之后，重绘之前调用
     * 
     * @param pBackground
     *            背景所存的像素，同pDrawMemory大小一致
     */ 
    void clearBackground(int *pBackground);
    
    void clearBackgroundByte(unsigned char *pBackground);
    
    void setBackground(int *pBackground);
    
    /**
     * 重绘函数
     * 说明：如果设置在裁剪区内，只重绘裁剪区内笔迹。只重绘一个stroke。
     * 
     * @param pPoints
     *            坐标点，按照（x，y）的顺序
     * @param pressures
     *            坐标点对应的压感值
     * @param count
     *            pPoints的数组大小                  
     */ 
    void reDrawLine( int *pPoints, float *pressures, int count, int *pDrawMemory );
 
    /**
     * 重绘函数
     * 说明：如果设置在裁剪区内，只重绘裁剪区内笔迹。只重绘一个stroke。
     * 
     * @param pPoints
     *            坐标点，压感值, 按照（x，y, p）的顺序(直接使用笔迹的插值信息)
     * @param count
     *            pPoints的数组大小                  
     */ 
    void reDrawLineEx( float *pPoints, int count, bool isErase);
    
    /**
     * 重绘函数
     * 说明：如果设置在裁剪区内，只重绘裁剪区内笔迹。重绘多个stroke。
     * 
     * @param pPoints
     *            按照 画笔风格（nPenStyle），颜色类型（nColorType），画笔宽度（nPenWidth），坐标点（x，y）的顺序
     * @param pressures
     *            坐标点对应的压感值
     * @param count
     *            重绘的stroke的数目                  
     */  
    void reDraw( int *pPoints, float *pressures, int count);
    
    /**
     * 重绘函数
     * 说明：如果设置在裁剪区内，只重绘裁剪区内笔迹。在没有笔迹的插值信息时候调用
     * 
     * @param pPoints
     *            按照 画笔风格（nPenStyle），颜色类型（nColorType），画笔宽度（nPenWidth），坐标点（x，y）的顺序
     * @param pressures
     *            坐标点对应的压感值
     * @param pRect
     *            返回值，笔迹的Bounds    
     * @param pathData
     *            返回值，笔迹的插值信息                   
     */ 
    void interpolate(int *pPoints, float *pressures, int *pRect, float *pathData, bool isErase);
    
    void setSimulatePressure(bool isSimulatePressure);



//#ifdef __cplusplus
//#endif


#endif  