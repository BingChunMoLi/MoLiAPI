package com.bingchunmoli.api.down.bean;

import lombok.Builder;
import lombok.Data;

/**
 * @author MoLi
 */
@Data
@Builder
public class OSInfo {
    private String name;
    private String arch;
    private String version;
    private String serviceName;
}
