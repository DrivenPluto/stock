package com.pluto.stock.common.utils;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Driven_Pluto
 * @date 7/30/23 15:27
 * @description:
 */
@Data
public class StockBlockRtInfoDomain implements Serializable {

    /**
     * 公司数量
     */
    private Integer companyNum;
    /**
     * 交易量
     */
    private Long tradeAmt;

    private String code;

    /**
     * 平均价格
     */
    private BigDecimal avgPrice;

    private String name;

    /**
     * 当前日期（精确到秒）
     */
    private String curDate;
    /**
     * 交易金额
     */
    private BigDecimal tradeVol;

    /**
     * 涨跌幅
     */
    private BigDecimal updownRate;
}
