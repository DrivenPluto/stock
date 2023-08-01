package com.pluto.stock.service;

import com.pluto.stock.common.utils.InnerMarketDomain;
import com.pluto.stock.common.utils.StockBlockRtInfoDomain;
import com.pluto.stock.common.utils.StockUpdownDomain;
import com.pluto.stock.pojo.StockBlockRtInfo;
import com.pluto.stock.pojo.StockBusiness;
import com.pluto.stock.vo.resp.PageResult;
import com.pluto.stock.vo.resp.R;

import java.util.List;


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
}
