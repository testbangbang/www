#ifndef TOUCH_READER_H
#define TOUCH_READER_H

class TouchReader {

public:
    static const int ON_PRESS = 0;
    static const int ON_MOVE = 1;
    static const int ON_RELEASE = 2;

public:
    TouchReader();
    ~TouchReader();

public:
    typedef void(*onTouchPointReceived)(int,int,int,long,bool,int);

public:
    void setStrokeWidth(float width);
    bool inLimitRegion(float x, float y);
    int openDevice(const std::string& devicePath, std::string& deviceName);
    void processEvent(onTouchPointReceived callback, int type, int code, int value, long ts);
    std::string findDevice();
    void closeDevice();
    void setLimitRegion(int *array, int len);
    void readTouchEventLoop(onTouchPointReceived callback);


private:
    bool debug = true;

    int fd;
    int px, py, pressure;
    bool volatile running;
    bool volatile drawing;
    bool pressed;
    bool lastPressed;
    bool volatile erasing;
    int *limitArray;
    int limitArrayLength;
    float strokeWidth;

};

#endif // TOUCH_READER_H
