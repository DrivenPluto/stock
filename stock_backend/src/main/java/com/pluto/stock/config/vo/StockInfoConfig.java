package com.pluto.stock.config.vo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author Driven_Pluto
 * @date 7/30/23 11:37
 * @description:
 */
@Data
@ConfigurationProperties(prefix = "stock")
public class StockInfoConfig {
    //A股大盘ID集合
    private List<String> inner;
    //外盘ID集合
    private List<String> outer;
    //股票区间
    private List<String>  upDownRange;
}
