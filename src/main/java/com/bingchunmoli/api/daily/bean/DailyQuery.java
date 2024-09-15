package com.bingchunmoli.api.daily.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * @author moli
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyQuery {
    private LocalDate startDate;
    private LocalDate endDate;
    private List<String> urls;
}
