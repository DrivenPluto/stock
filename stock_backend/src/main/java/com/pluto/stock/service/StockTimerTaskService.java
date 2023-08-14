package com.pluto.stock.service;

/**
 * @author Driven_Pluto
 * @date 8/12/23 11:17
 * @description:   定义采集股票数据的定时任务的服务接口
 */
public interface  StockTimerTaskService {

    /**
     * 获取国内大盘的实时数据信息
     */
    void collectInnerMarketInfo();

    /**
     * 获取国内A股信息
     */
    void collectAShareInfo();

    /**
     * 获取板块数据
     */
    void getStockSectorRtIndex();
}
