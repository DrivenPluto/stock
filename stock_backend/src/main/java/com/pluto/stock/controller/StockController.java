package com.pluto.stock.controller;

import com.pluto.stock.common.utils.InnerMarketDomain;
import com.pluto.stock.common.utils.StockBlockRtInfoDomain;
import com.pluto.stock.common.utils.StockUpdownDomain;
import com.pluto.stock.pojo.StockBlockRtInfo;
import com.pluto.stock.pojo.StockBusiness;
import com.pluto.stock.service.StockService;
import com.pluto.stock.vo.resp.PageResult;
import com.pluto.stock.vo.resp.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/quot")
public class StockController {
    @Autowired
    private StockService stockService;

    @GetMapping("/stock/business/all")
    public List<StockBusiness> findAll(){
        return stockService.findAll();
    }

    /**
     * 获取A股最新大盘信息
     * 如果不再股票交易日获取最近日期
     * @return
     */
    @GetMapping("/index/all")
    public R<List<InnerMarketDomain>> getNerAMarketInfo(){
        return stockService.getNerAMarketInfo();
    }

    /**
     * 需求说明: 沪深两市板块分时行情数据查询，以交易时间和交易总金额降序查询，取前10条数据
     * @return
     */
    @GetMapping("sector/all")
    public R<List<StockBlockRtInfoDomain>> getSectorAll(){
        return stockService.sectorAllLimit();
    }

    /**
     * 沪深两市个股涨幅分时行情数据查询，以时间顺序和涨幅查询前10条数据
     * @return
     */
    @GetMapping("/stock/increase")
    public R<List<StockUpdownDomain>> stockIncreaseLimit(){
        return stockService.stockIncreaseLimit();
    }

    /**
     * 沪深两市个股行情列表查询 ,以时间顺序和涨幅分页查询
     * @param page 当前页
     * @param pageSize 每页大小
     * @return
     */
    @GetMapping("/stock/all")
    public R<PageResult<StockUpdownDomain>> stockPage(Integer page,Integer pageSize){
        return stockService.stockPage(page,pageSize);

    }
}
