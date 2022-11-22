package com.bingchunmoli.api.bean;

import com.bingchunmoli.api.bean.enums.DriveType;


public record Init(DriveType driveType, boolean cacheEnable, String activeSchemaPath, String activeDataPath) {

}
