package com.pluto.stock.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pluto.stock.common.utils.InnerMarketDomain;
import com.pluto.stock.common.utils.StockBlockRtInfoDomain;
import com.pluto.stock.common.utils.StockUpdownDomain;
import com.pluto.stock.config.vo.StockInfoConfig;
import com.pluto.stock.mapper.StockBlockRtInfoMapper;
import com.pluto.stock.mapper.StockBusinessMapper;
import com.pluto.stock.mapper.StockMarketIndexInfoMapper;
import com.pluto.stock.pojo.StockBlockRtInfo;
import com.pluto.stock.pojo.StockBusiness;
import com.pluto.stock.service.StockService;
import com.pluto.stock.utils.DateTimeUtil;
import com.pluto.stock.vo.resp.PageResult;
import com.pluto.stock.vo.resp.R;
import com.pluto.stock.vo.resp.ResponseCode;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

@Service
public class StockServiceImpl implements StockService {
    @Autowired
    private StockBusinessMapper stockBusinessMapper;

    @Autowired
    private StockInfoConfig stockInfoConfig;
    @Autowired
    private StockMarketIndexInfoMapper stockMarketIndexInfoMapper;

    @Autowired
    private StockBlockRtInfoMapper stockBlockRtInfoMapper;


    @Override
    public List<StockBusiness> findAll() {
        return stockBusinessMapper.findAll();
    }

    /**
     * 获取A股最新大盘信息
     * 如果不再股票交易日获取最近日期
     * @return
     */
    @Override
    public R<List<InnerMarketDomain>> getNerAMarketInfo() {
        //1.获取国内A股大盘id集合
        List<String> inners = stockInfoConfig.getInner();
        //2。获取最近股票交易日期
        DateTime lastDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
        //3.转java中Date
        Date lastDate = lastDateTime.toDate();
        //TODO 测试数据，后期通过第三方接口动态获取实时数据 可删除
        lastDate = DateTime.parse("2022-01-03 11:15:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //4.获取java Date转入接口
        List<InnerMarketDomain> list = stockMarketIndexInfoMapper.getMarketInfo(inners,lastDate);
        return R.ok(list);
    }

    /**
     * 需求说明: 沪深两市板块分时行情数据查询，以交易时间和交易总金额降序查询，取前10条数据
     * @return
     */
    @Override
    public R<List<StockBlockRtInfoDomain>> sectorAllLimit() {
        //1.调用mapper接口获取数据 TODO 优化 避免全表查询 根据时间范围查询，提高查询效率
        List<StockBlockRtInfoDomain> infos = stockBlockRtInfoMapper.sectorAllLimit();
        //2.组装数据
        if (CollectionUtils.isEmpty(infos)) {
            return R.error(ResponseCode.NO_RESPONSE_DATA.getMessage());
        }
        for (StockBlockRtInfoDomain info : infos) {
            System.out.println(info);
        }
        return R.ok(infos);
    }

    /**
     * 沪深两市个股涨幅分时行情数据查询，以时间顺序和涨幅查询前10条数据
     * @return
     */
    @Override
    public R<List<StockUpdownDomain>> stockIncreaseLimit() {
        //1.直接调用mapper查询前10的数据 TODO 以时间顺序取前10
        Date lastDate = DateTimeUtil.getLastDate4Stock(DateTime.now()).toDate();
        //TODO mock数据
        lastDate = DateTime.parse("2021-12-30 09:42:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        List<StockUpdownDomain> list = stockBlockRtInfoMapper.stockIncreaseLimit(lastDate);
        if (CollectionUtils.isEmpty(list)) {
            return R.error(ResponseCode.NO_RESPONSE_DATA.getMessage());
        }
        return R.ok(list);
    }

    @Override
    public R<PageResult<StockUpdownDomain>> stockPage(Integer page, Integer pageSize) {
        //1。设置分页
        PageHelper.startPage(page,pageSize);
        //2.通过mapper查询
        List<StockUpdownDomain> pages = stockBlockRtInfoMapper.stockPage();
        if (CollectionUtils.isEmpty(pages)) {
            return R.error(ResponseCode.NO_RESPONSE_DATA.getMessage());
        }
        //3.封装到PageInfo中
        PageInfo<StockUpdownDomain> listPageInfo = new PageInfo<>(pages);
        //4.将PageInfo转PageResult
        PageResult<StockUpdownDomain> pageResult = new PageResult<>(listPageInfo);
        //5.返回
        return R.ok(pageResult);
    }
}
