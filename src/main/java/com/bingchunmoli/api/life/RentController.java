package com.bingchunmoli.api.life;

import com.bingchunmoli.api.bean.ResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author moli
 */
@Slf4j
@RestController
@RequestMapping("rent")
@RequiredArgsConstructor
public class RentController {

    @GetMapping
    public ResultVO<Map<String, String>> calcRent(@RequestParam(required = false, defaultValue = "0") String month,
                                                  @RequestParam(required = false, defaultValue = "0") String rentPrice,
                                                  @RequestParam(required = false, defaultValue = "0") String waterConsumption,
                                                  @RequestParam(required = false, defaultValue = "0") String waterCostRate,
                                                  @RequestParam(required = false, defaultValue = "0") String electricityConsumption,
                                                  @RequestParam(required = false, defaultValue = "0") String powerRate){
        BigDecimal monthDecimal = new BigDecimal(month);
        BigDecimal rentPriceDecimal = new BigDecimal(rentPrice);
        BigDecimal waterConsumptionDecimal = new BigDecimal(waterConsumption);
        BigDecimal waterCostRateDecimal = new BigDecimal(waterCostRate);
        BigDecimal electricityConsumptionDecimal = new BigDecimal(electricityConsumption);
        BigDecimal powerRateDecimal = new BigDecimal(powerRate);
        BigDecimal rentPriceCalc = rentPriceDecimal.multiply(monthDecimal);
        BigDecimal waterPriceCalc = waterConsumptionDecimal.multiply(waterCostRateDecimal);
        BigDecimal powerPriceCalc = electricityConsumptionDecimal.multiply(powerRateDecimal);
        BigDecimal totalPrice = rentPriceCalc.add(waterPriceCalc).add(powerPriceCalc);
        HashMap<String, String> result = new HashMap<>();
        result.put("rentPriceCalc", rentPriceCalc.toString());
        result.put("waterPriceCalc", waterPriceCalc.toString());
        result.put("powerPriceCalc", powerPriceCalc.toString());
        result.put("totalPrice", totalPrice.toString());
        return ResultVO.ok(result);
    }

}
