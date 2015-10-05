/*
 *
 *                       ADOBE CONFIDENTIAL
 *                     _ _ _ _ _ _ _ _ _ _ _ _
 *
 * Copyright 2009, Adobe Systems Incorporated
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains the property of
 * Adobe Systems Incorporated and its suppliers, if any.  The intellectual and
 * technical concepts contained herein are proprietary to Adobe Systems
 * Incorporated and its suppliers and may be covered by U.S. and Foreign
 * Patents, patents in process, and are protected by trade secret or copyright
 * law.  Dissemination of this information or reproduction of this material is
 * strictly forbidden unless prior written permission is obtained from Adobe
 * Systems Incorporated.
 */

#include "fonts.h"

#include <string.h>

#ifdef WIN32
#include <windows.h>
#include <tchar.h>
#include <io.h>
#else
#include <sys/types.h>
#include <sys/mman.h>
#include <sys/stat.h>
#include <fcntl.h>
#endif

unsigned const char * AdobeMingStd_Light()
{
    static unsigned const char * data = 0;
    if (data == 0)
    {
#ifdef WIN32
        wchar_t * absPathName = L"C:\\Users\\Li\\Desktop\\adobefont\\AdobeMingStd_Light_FONT";
        void * file = CreateFileW( absPathName, GENERIC_READ, FILE_SHARE_READ, NULL, 
                                     OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, NULL );

        if( file != INVALID_HANDLE_VALUE )
        {
            DWORD sizeHigh;
            DWORD size = GetFileSize( file, &sizeHigh );
            void * mapping = CreateFileMapping( file, NULL, PAGE_READONLY, 0, 0, NULL );
            if( mapping != NULL )
            {
                data = (unsigned const char *)MapViewOfFile( mapping, FILE_MAP_READ, 0, 0, 0 );
            }
        }
#else
        char font_path[256] = { 0 };
        char * path = getenv("FONT_DIR");
        if (path == 0)
        {
            path = "/media/sd/font/";
        }

        strcpy(font_path, path);
        char * p = font_path;
        p += strlen(path);
        strcpy(p, "AdobeMingStd_Light_FONT.ttf");
        printf("Font Path:%s\n\n", font_path);

        int file = open(font_path, O_RDONLY);
        if( file >= 0 )
        {
            struct stat sbuf;
            if( fstat( file, &sbuf ) == 0 )
            {
                size_t size = (size_t)sbuf.st_size;
                printf("AdobeMingStd_Light_FONT Size:%d\n\n", size);

                void * ptr = mmap( 0, size, PROT_READ, MAP_SHARED, file, 0 );
                if( ptr != 0 && ptr != (unsigned char*)(-1) )
                {
                    printf("mmap succeeded!\n\n");
                    data = (unsigned char *)ptr;
                }
            }
            if( data == 0 )
            {
                close( file );
                file = -1;
            }
        }
#endif
    }
    return data;
}

unsigned const int AdobeMingStd_Light_Length()
{
    static unsigned int length = 0;
    if (length == 0)
    {
#ifdef WIN32
        wchar_t * absPathName = L"C:\\Users\\Li\\Desktop\\adobefont\\AdobeMingStd_Light_FONT.ttf";
        void * file = CreateFileW( absPathName, GENERIC_READ, FILE_SHARE_READ, NULL, 
                                     OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, NULL );

        if( file != 0 && file != INVALID_HANDLE_VALUE )
        {
            DWORD sizeHigh;
            length = GetFileSize( file, &sizeHigh );
            CloseHandle( file );
        }
#else
        char font_path[256] = { 0 };
        char * path = getenv("FONT_DIR");
        if (path == 0)
        {
            path = "/media/sd/font/";
        }

        strcpy(font_path, path);
        char * p = font_path;
        p += strlen(path);
        strcpy(p, "AdobeMingStd_Light_FONT.ttf");

        int file = open(font_path, O_RDONLY);
        if( file >= 0 )
        {
            struct stat sbuf;
            if( fstat( file, &sbuf ) == 0 )
            {
                length = (size_t)sbuf.st_size;
                printf("AdobeMingStd_Light_Length:%d\n\n", length);
            }
            close( file );
        }
#endif
    }
    return length;
}

//unsigned const char AdobeMingStd_Light[] =
//{
//#ifdef SUPPORT_TRADITIONAL_CHINESE
//	#include "AdobeMingStd-Light.h"
//#else
//	0
//#endif
//};

