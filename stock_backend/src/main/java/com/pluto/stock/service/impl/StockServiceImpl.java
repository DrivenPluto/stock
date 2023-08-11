package com.pluto.stock.service.impl;

import com.alibaba.excel.EasyExcel;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.pluto.stock.common.utils.*;
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
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
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

    /**
     * 功能描述：沪深两市涨跌停分时行情数据查询，查询T日每分钟的涨跌停数据（T：当前股票交易日）
     * 		查询每分钟的涨停和跌停的数据的同级；
     * 		如果不在股票的交易日内，那么就统计最近的股票交易下的数据
     * 	 map:
     * 	    upList:涨停数据统计
     * 	    downList:跌停数据统计
     * @return
     */
    @Override
    public R<Map> getStockUpDowmCount() {
        //1.获取股票最近的有效交易日期,精确到秒
        DateTime curDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
        //当前最近有效期
        Date openTime = DateTimeUtil.getOpenDate(curDateTime).toDate();
        Date closeTime = DateTimeUtil.getCloseDate(curDateTime).toDate();
        //TODO mock数据
        openTime = DateTime.parse("2021-12-19 09:30:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        closeTime = DateTime.parse("2021-12-19 15:00:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //2.查询涨停的统计
        List<Map> upList = stockBlockRtInfoMapper.getStockUpDowmCount(openTime,closeTime,1);
        List<Map> downList = stockBlockRtInfoMapper.getStockUpDowmCount(openTime,closeTime,0);
        //3.组装map，将涨停和跌停的数据组装到map中
        HashMap<String,List> map = new HashMap<>();
        map.put("upList",upList);
        map.put("downList",upList);
        //返回数据
        return R.ok(map);
    }

    /**
     * 到处股票信息到excel下
     * @param response http的响应对象，可获取流对象
     * @param page  当前页
     * @param pageSize 每页大小
     */
    @Override
    public void stockExport(HttpServletResponse response, Integer page, Integer pageSize) {
       try {
           //1.设置响应数据的类型:excel
            response.setContentType("application/vnd.ms-excel");
            //2.设置响应数据的编码格式
            response.setCharacterEncoding("utf-8");
            //3.设置默认的文件名称
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("stockRt", "UTF-8");
            //设置默认文件名称
            response.setHeader("content-disposition", "attachment;filename=" + fileName + ".xlsx");
            //读取导出的数据集合
            //1.设置分页参数
            //1。设置分页
            PageHelper.startPage(page,pageSize);
            //2.通过mapper查询
            List<StockUpdownDomain> pages = stockBlockRtInfoMapper.stockPage();
            if (CollectionUtils.isEmpty(pages)) {
                R<String> error = R.error(ResponseCode.NO_RESPONSE_DATA.getMessage());
                //将错误信息转换为json字符响应前段
                String jsonData = new Gson().toJson(error);
                //响应前端数据
                response.getWriter().write(jsonData);
                //终止当前程序
                return;
            }
            //将List<StockUpdownDomain> 转换为 List<StockExcelDomain>
            List<StockExcelDomain> domains = pages.stream().map(item -> {
                StockExcelDomain domain = new StockExcelDomain();
                BeanUtils.copyProperties(item, domain);
                return domain;
            }).collect(Collectors.toList());
            //导出数据
            EasyExcel.write(response.getOutputStream(), StockExcelDomain.class)
                    .sheet("stockInfo")
                    .doWrite(domains);
        }catch (IOException e){
           log.info("股票excel数据导出异常，当前页：{}，每页大小：{}，异常信息：{}",page,pageSize,e.getMessage());
       }
    }
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
    @Override
    public R<Map> getStockTradeVol4Comparison() {
        //1.获取T日和T-1日的开始时间和结束时间
        //1.1获取最近股票有效交易结束时间
        DateTime lastDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
        //1.2 根据有效交易日时间获取开盘时间
        DateTime openDateTime = DateTimeUtil.getOpenDate(lastDateTime);
        //1.3 转换成Java中的Date，这样jdbc默认识别
        Date startTime4T = openDateTime.toDate();
        Date endTime4T = lastDateTime.toDate();
        //TODO mock数据
        startTime4T = DateTime.parse("2022-01-03 09:30:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        endTime4T = DateTime.parse("2022-01-03 14:40:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //1.4获取T-1日的区间范围
        //1.5 获取lastDateTime的上一个股票交易日和开盘日期
        DateTime preLastDateTime = DateTimeUtil.getPreviousTradingDay(lastDateTime);
        DateTime preOpenDateTime = DateTimeUtil.getOpenDate(preLastDateTime);
        //转换成Java中的Date，这样jdbc默认识别
        Date startTime4PreT = preOpenDateTime.toDate();
        Date endTime4PreT = preLastDateTime.toDate();
        //TODO mock数据
        startTime4PreT = DateTime.parse("2022-01-02 09:30:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        endTime4PreT = DateTime.parse("2022-01-02 14:40:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //2.获取上证和深证的配置的大盘id
        List<String> markedIds = stockInfoConfig.getInner();
        //3.分别查询T日和T-1日的交易量数据，得到两个集合
        //3.1 查询T日大盘交易统计数据
        List<Map> data4T = stockMarketIndexInfoMapper.getStockTradeVol(markedIds,startTime4T,endTime4T);
        if (CollectionUtils.isEmpty(data4T)) {
            data4T = new ArrayList<>();
        }
        //3.2 查询T-1日大盘交易统计数据
        List<Map> data4PreT = stockMarketIndexInfoMapper.getStockTradeVol(markedIds,startTime4PreT,endTime4PreT);
        if (CollectionUtils.isEmpty(data4T)) {
            data4PreT = new ArrayList<>();
        }
        //4。组装响应数据
        HashMap<String, List> info = new HashMap<>();
        info.put("volList",data4T);
        info.put("yesVolList",data4PreT);
        //5 返回数据
        return R.ok(info);
    }
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
    @Override
    public R<Map> getStockUpDownRegion() {
        //1.获取股票最新交易时间
        DateTime lastDate4Stock = DateTimeUtil.getLastDate4Stock(DateTime.now());
        Date lastDate = lastDate4Stock.toDate();
        //TODO mock data
        lastDate = DateTime.parse("2021-12-30 09:42:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //2.插入mapper接口获取统计数据
        List<Map> infos = stockMarketIndexInfoMapper.getStockUpDownRegion(lastDate);
        if (CollectionUtils.isEmpty(infos)) {
             infos = new ArrayList<>();
        }
        List<String> upDownRange = stockInfoConfig.getUpDownRange();
        //通过stream map映射和过滤完成转换
        List<Map> finalInfos = infos;
        List<Map> newMaps = upDownRange.stream().map(item->{
            Optional<Map> optional = finalInfos.stream().filter(map -> map.get("title").equals(item)).findFirst();
            Map tmp = null;
            //判断是否有map
            if (optional.isPresent()) {
                tmp = optional.get();
            }else {
                tmp = new HashMap();
                tmp.put("title",item);
                tmp.put("count",0);
            }
            return tmp;
        }).collect(Collectors.toList());
        //3.组装数据
        HashMap<String , Object> data = new HashMap<>();
        //获取日期格式，为了满足友好展示
        String stringDateTime = lastDate4Stock.toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
        data.put("time",stringDateTime);
        data.put("infos",newMaps);
        return R.ok(data);
    }
    /**
     * 查询单个个股的分时行情数据，也就是统计指定股票T日每分钟的交易数据；
     * 如果当前日期不在有效时间内，则以最近的一个股票交易时间作为查询时间点
     * @param stockCode 股票编码
     * @return
     */
    @Override
    public R<List<Stock4MinuteDomain>> stockCreenSharing(String stockCode) {
       //1.获取最近最新的交易时间和对应的开盘日期
        // 1.1获取最近有效时间
        DateTime lastDate4Stock = DateTimeUtil.getLastDate4Stock(DateTime.now());
        Date endTime = lastDate4Stock.toDate();
        endTime = DateTime.parse("2021-12-30 14:42:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //1.2 获取最近有效时间点对应的开盘日期
        DateTime openDateTime = DateTimeUtil.getOpenDate(lastDate4Stock);
        Date startTime = openDateTime.toDate();
        startTime = DateTime.parse("2021-12-30 09:42:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //2。根据股票code和日期范围查询
        List<Stock4MinuteDomain> list = stockMarketIndexInfoMapper.getStockInfoByCodeAndDate(stockCode,startTime,endTime);
        if (CollectionUtils.isEmpty(list)) {
            list = new ArrayList<>();
        }
        return R.ok(list);


    }
    /**
     * 个股日K数据查询 ，可以根据时间区间查询数日的K线数据
     * 默认查询历史20天的数据；
     * @param code 股票code
     * @return
     */
    @Override
    public R<List> stockScreenDkLine(String code) {
        //1.获取查询的日期范围
        //1.1获取截止日期
        DateTime endDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
        Date endTime = endDateTime.toDate();
        //TODO mock Data
        endTime = DateTime.parse("2022-01-07 14:42:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //1.2获取开始日期
        DateTime startDateTime = endDateTime.minusDays(10);
        Date startTime = startDateTime.toDate();
        //TODO ,ock Data
        startTime = DateTime.parse("2022-01-01 14:42:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //2。调用mapper接口获取查询的集合信息
        List<Stock4EvrDayDomain> data = stockMarketIndexInfoMapper.getStockInfo4EvrDay(code,startTime,endTime);
        if (CollectionUtils.isEmpty(data)) {
            data = new ArrayList<>();
        }
        return R.ok(data);
    }
    /**
     * 个股周K数据查询 ，可以根据时间区间查询每周的K线数据
     * @param code 股票code
     * @return
     */
    @Override
    public R<List> stockScreenWeekLine(String code) {
        //获取股票交易最新时间
        DateTime lastDate4Stock = DateTimeUtil.getLastDate4Stock(DateTime.now());
        Date endTime = lastDate4Stock.toDate();
        endTime = DateTime.parse("2022-01-07 14:42:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //2.获取开始时间
        DateTime startDateTime = lastDate4Stock.minusDays(7);
        Date startTime = startDateTime.toDate();
        startTime = DateTime.parse("2022-01-01 14:42:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //3.调用mapper接口
        List<Stock4EvrWeekDomain> data = stockMarketIndexInfoMapper.getStockInfo4EvrWek(code,startTime,endTime);
        if (CollectionUtils.isEmpty(data)) {
            data = new ArrayList<>();
        }
        return R.ok(data);
    }
}
