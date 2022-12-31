package com.bingchunmoli.api.navigation.bean;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author MoLi
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Navigation extends NavigationPO{
    List<TagPO> tagList;
}
