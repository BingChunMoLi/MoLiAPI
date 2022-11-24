package com.bingchunmoli.api.bean;

import com.bingchunmoli.api.bean.enums.DriveType;


public record Init(DriveType driveType, String activeDataFilePath, String activeSchemaPath, String activeDataPath) {

}
//todo 功能开关？
