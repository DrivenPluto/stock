package com.pluto.stock.service;

import com.pluto.stock.common.utils.InnerMarketDomain;
import com.pluto.stock.common.utils.Stock4MinuteDomain;
import com.pluto.stock.common.utils.StockBlockRtInfoDomain;
import com.pluto.stock.common.utils.StockUpdownDomain;
import com.pluto.stock.pojo.StockBlockRtInfo;
import com.pluto.stock.pojo.StockBusiness;
import com.pluto.stock.vo.resp.PageResult;
import com.pluto.stock.vo.resp.R;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


public interface StockService {
    List<StockBusiness> findAll();

    /**
     * 获取A股最新大盘信息
     * 如果不再股票交易日获取最近日期
     * @return
     */
    R<List<InnerMarketDomain>> getNerAMarketInfo();
    /**
     * 需求说明: 沪深两市板块分时行情数据查询，以交易时间和交易总金额降序查询，取前10条数据
     * @return
     */
    R<List<StockBlockRtInfoDomain>> sectorAllLimit();

    /**
     * 沪深两市个股涨幅分时行情数据查询，以时间顺序和涨幅查询前10条数据
     * @return
     */
    R<List<StockUpdownDomain>> stockIncreaseLimit();

    /**
     * 沪深两市个股行情列表查询 ,以时间顺序和涨幅分页查询
     * @param page 当前页
     * @param pageSize 每页大小
     * @return
     */
    R<PageResult<StockUpdownDomain>> stockPage(Integer page, Integer pageSize);

    /**
     * 统计T日（最近一次股票交易日)涨停和跌停分时统计
     * @return
     */
    R<Map> getStockUpDowmCount();

    /**
     * 到处股票信息到excel下
     * @param response http的响应对象，可获取流对象
     * @param page  当前页
     * @param pageSize 每页大小
     */
    void stockExport(HttpServletResponse response, Integer page, Integer pageSize);
    /**
     * 功能描述：统计国内A股大盘T日和T-1日成交量对比功能（成交量为沪市和深市成交量之和）
     * 如果当前日期不在股票交易日，则按照前一个有效股票交易日作为T日查询
     *  map结构示例：
     *           {
     *              "volList": [{"count": 3926392,"time": "202112310930"},......],
     *              "yesVolList":[{"count": 3926392,"time": "202112310930"},......]
     *           }
     * @return
     */
    R<Map> getStockTradeVol4Comparison();

    /**
     * 统计当前时间下（精确到分钟），股票在各个涨跌区间的数量
     * 如果当前不在股票有效时间内，则以前一个有效股票交易日作为查询时间点；
     * map结构说明
     * "time": "2021-12-31 14:58:00",
     *         "infos": [
     *             {
     *                 "count": 17,
     *                 "title": "-3~0%"
     *             },
     *             {
     *                 "count": 2,
     *                 "title": "-5~-3%"
     *             }
     *             ]
     * @return
     */
    R<Map> getStockUpDownRegion();
    /**
     * 查询单个个股的分时行情数据，也就是统计指定股票T日每分钟的交易数据；
     * 如果当前日期不在有效时间内，则以最近的一个股票交易时间作为查询时间点
     * @param stockCode 股票编码
     * @return
     */
    R<List<Stock4MinuteDomain>> stockCreenSharing(String stockCode);
    /**
     * 个股日K数据查询 ，可以根据时间区间查询数日的K线数据
     * 默认查询历史20天的数据；
     * @param code 股票code
     * @return
     */
    R<List> stockScreenDkLine(String code);
    /**
     * 个股周K数据查询 ，可以根据时间区间查询每周的K线数据
     * @param code 股票code
     * @return
     */
    R<List> stockScreenWeekLine(String code);
}
