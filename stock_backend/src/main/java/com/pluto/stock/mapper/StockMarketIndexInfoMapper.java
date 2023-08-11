package com.pluto.stock.mapper;

import com.pluto.stock.common.utils.InnerMarketDomain;
import com.pluto.stock.common.utils.Stock4EvrDayDomain;
import com.pluto.stock.common.utils.Stock4EvrWeekDomain;
import com.pluto.stock.common.utils.Stock4MinuteDomain;
import com.pluto.stock.pojo.StockMarketIndexInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* @author plutodriven
* @description 针对表【stock_market_index_info(股票大盘数据详情表)】的数据库操作Mapper
* @createDate 2023-07-26 21:45:32
* @Entity com.pluto.stock.pojo.StockMarketIndexInfo
*/
@Mapper
public interface StockMarketIndexInfoMapper {

    int deleteByPrimaryKey(Long id);

    int insert(StockMarketIndexInfo record);

    int insertSelective(StockMarketIndexInfo record);

    StockMarketIndexInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StockMarketIndexInfo record);

    int updateByPrimaryKey(StockMarketIndexInfo record);

    /**
     * 根据大盘id和时间查询大盘信息
     * @param marketIds 大盘id
     * @param timePoint 当前时间 （默认精确到分钟）
     * @return
     */
    List<InnerMarketDomain> getMarketInfo(@Param("marketIds") List<String> marketIds, @Param("timePoint")Date timePoint);

    /**
     * 根据时间范围和指定的大盘id统计每分钟的交易量
     * @param markedIds 大盘id集合
     * @param startTime 交易开始时间
     * @param endTime   结束时间
     * @return
     */
    List<Map> getStockTradeVol(@Param("markedIds") List<String> markedIds,
                               @Param("startTime") Date startTime,
                               @Param("endTime") Date endTime);

    /**
     * 查询指定时间点下股票各个涨幅区间的数量
     * @param timePoint 股票交易时间点（精确到分钟）
     * @return
     */
    List<Map> getStockUpDownRegion(@Param("timePoint") Date timePoint);

    /**
     * 根据时间范围查询指定股票的交易流水
     * @param stockCode 股票code
     * @param startTime 起始时间
     * @param endTime   结束时间
     * @return
     */
    List<Stock4MinuteDomain> getStockInfoByCodeAndDate(@Param("stockCode") String stockCode,
                                                       @Param("startTime") Date startTime,
                                                       @Param("endTime") Date endTime);

    /**
     * 查询指定日期范围内指定股票每天的交易数据
     * @param stockCode 股票code
     * @param startTime 起始时间
     * @param endTime   结束时间
     * @return
     */
    List<Stock4EvrDayDomain> getStockInfo4EvrDay(@Param("stockCode") String stockCode,
                                                 @Param("startTime") Date startTime,
                                                 @Param("endTime") Date endTime);
    /**
     * 查询指定日期范围内指定股票每周的交易数据
     * @param stockCode 股票code
     * @param startTime 起始时间
     * @param endTime   结束时间
     * @return
     */
    List<Stock4EvrWeekDomain> getStockInfo4EvrWek(@Param("stockCode") String stockCode,
                                                  @Param("startTime") Date startTime,
                                                  @Param("endTime") Date endTime);
}
