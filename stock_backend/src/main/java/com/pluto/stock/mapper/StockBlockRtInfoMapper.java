package com.pluto.stock.mapper;

import com.pluto.stock.common.utils.StockBlockRtInfoDomain;
import com.pluto.stock.common.utils.StockUpdownDomain;
import com.pluto.stock.pojo.StockBlockRtInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* @author plutodriven
* @description 针对表【stock_block_rt_info(股票板块详情信息表)】的数据库操作Mapper
* @createDate 2023-07-26 21:45:32
* @Entity com.pluto.stock.pojo.StockBlockRtInfo
*/
public interface StockBlockRtInfoMapper {

    int deleteByPrimaryKey(Long id);

    int insert(StockBlockRtInfo record);

    int insertSelective(StockBlockRtInfo record);

    StockBlockRtInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StockBlockRtInfo record);

    int updateByPrimaryKey(StockBlockRtInfo record);
    /**
     * 需求说明: 沪深两市板块分时行情数据查询，以交易时间和交易总金额降序查询，取前10条数据
     * @return
     */
    List<StockBlockRtInfoDomain> sectorAllLimit();

    /**
     *
     * @param timePoint
     * @return
     */
    List<StockUpdownDomain> stockIncreaseLimit(@Param("timePoint") Date timePoint);

    /**
     * 根据时间和涨幅降序排序全表查询
     * @return
     */
    List<StockUpdownDomain> stockPage();

    /**
     *统计指定日期内涨停或者跌停的数据,日期范围不能失效，否则分库分表查询失效
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param flag  标识 :1代表涨停，0代表跌停
     * @return
     */
    List<Map> getStockUpDowmCount(@Param("startTime") Date startTime,
                                  @Param("endTime") Date endTime,
                                  @Param("flag") int flag);
}
