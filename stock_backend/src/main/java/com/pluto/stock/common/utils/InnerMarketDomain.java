package com.pluto.stock.common.utils;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Driven_Pluto
 * @date 7/30/23 11:13
 * @description:
 */
@Data
public class InnerMarketDomain implements Serializable {
    /*
      jdbc:bigint--->java:long
     */
    private Long tradeAmt;
    /*
        jdbc:decimal --->java:BigDecimal
     */
    private BigDecimal preClosePrice;
    private String code;
    private String name;
    private String curDate;
    private BigDecimal openPrice;
    private Long tradeVol;
    private BigDecimal upDown;
    private BigDecimal tradePrice;
}
