package com.bingchunmoli.api.bean;

import com.bingchunmoli.api.bean.enums.DriveType;


public record Init(DriveType driveType, String activeDataFilePath, String activeSchemaPath, String activeDataPath,
                   String profile) {

}
//todo 功能开关？
