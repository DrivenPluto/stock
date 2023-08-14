package com.pluto.stock.service.impl;

import com.google.common.collect.Lists;
import com.pluto.stock.config.vo.StockInfoConfig;
import com.pluto.stock.mapper.StockBusinessMapper;
import com.pluto.stock.mapper.StockMarketIndexInfoMapper;
import com.pluto.stock.mapper.StockRtInfoMapper;
import com.pluto.stock.pojo.StockBlockRtInfo;
import com.pluto.stock.pojo.StockMarketIndexInfo;
import com.pluto.stock.service.StockTimerTaskService;
import com.pluto.stock.utils.DateTimeUtil;
import com.pluto.stock.utils.IdWorker;
import com.pluto.stock.utils.ParseType;
import com.pluto.stock.utils.ParserStockInfoUtil;
import lombok.extern.slf4j.Slf4j;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Driven_Pluto
 * @date 8/12/23 16:21
 * @description:
 */
@Service("StockTimerTaskService")
@Slf4j
public class StockTimerTaskServiceImpl implements StockTimerTaskService {
    @Autowired
    private StockInfoConfig stockInfoConfig;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private IdWorker idWorker;
    @Autowired
    private StockMarketIndexInfoMapper stockMarketIndexInfoMapper;

    @Autowired
    private StockBusinessMapper stockBusinessMapper;

    @Autowired
    private ParserStockInfoUtil parserStockInfoUtil;

    @Autowired
    private StockRtInfoMapper stockRtInfoMapper;

    /**
     * 采集国内大盘数据
     */
    @Override
    public void collectInnerMarketInfo() {
        //1.定义采集url的接口
        String url = stockInfoConfig.getMarketUrl() + String.join(",",stockInfoConfig.getInner());
        //2.调用restTemplate采集数据
        HttpHeaders headers = new HttpHeaders();
        //2.1组装请求头
        headers.add("Referer","https://finance.sina.com.cn/stock/");
        headers.add("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
        //2.2组装请求对象
        HttpEntity<Object> entity = new HttpEntity<>(headers);
        //2.3 restTemplate
        String reqString = restTemplate.postForObject(url, entity, String.class);
        log.info("当前采集的数据：{}",reqString);
        //获取共工采集时间点，精确到分钟
        Date curDateTime = DateTimeUtil.getDateTimeWithoutSecond(DateTime.now()).toDate();

        //3.数据解析（⭐️）
        /*var hq_str_s_sh000001="上证指数,3189.2480,-65.3117,-2.01,3301377,33918713";
        var hq_str_s_sz399001="深证成指,10808.87,-241.356,-2.18,371809334,42430388";*/
        String reg = "var hq_str_(.+)=\"(.+)\";";
        //编译表达式,获取编译对象
        Pattern pattern = Pattern.compile(reg);
        //匹配字符串
        Matcher matcher = pattern.matcher(reqString);
        ArrayList<StockMarketIndexInfo> list = new ArrayList<>();
        //判断是否有匹配的数值
        while (matcher.find()){
            //获取大盘的code
            String marketCode = matcher.group(1);
            //获取其他信息，字符串以逗号间隔
            String otherInfo = matcher.group(2);
            //以逗号切换字符串，形成数据
            String[] splitArr = otherInfo.split(",");
            //大盘名称
            String marketName = splitArr[0];
            //获取当前大盘的点数
            BigDecimal curPoint = new BigDecimal(splitArr[1]);
            //获取大盘的涨跌值
            BigDecimal curPrice = new BigDecimal(splitArr[2]);
            //获取大盘的涨幅
            BigDecimal upDownRate = new BigDecimal(splitArr[3]);
            //获取成交量
            Long tradeVol = Long.valueOf(splitArr[4]);
            //获取成交金额
            Long tradeAmount = Long.valueOf(splitArr[5]);
            //组装entity对象
            StockMarketIndexInfo info = StockMarketIndexInfo.builder()
                    .id(idWorker.nextId() + "")
                    .markId(marketCode)
                    .markName(marketName)
                    .curPoint(curPoint)
                    .currentPrice(curPrice)
                    .updownRate(upDownRate)
                    .tradeVolume(tradeVol)
                    .curTime(curDateTime)
                    .tradeAccount(tradeAmount).build();
            log.info("当前采集信息：{}",info.toString());
            list.add(info);
        }
//        System.out.println(list);
        //批量保存
        stockMarketIndexInfoMapper.insertBatch(list);

    }
    /**
     * 获取国内A股信息
     */
    @Override
    public void collectAShareInfo() {
        //1。获取所有股票code的集合 TODO 后期加入redis缓存
        List<String> stockCodeList = stockBusinessMapper.getAllStockCode();
        //转换集合集合中股票编码，添加前缀
        stockCodeList = stockCodeList.stream().map(id->{
            if (id.startsWith("6")) {
                id = "sh" + id;
            }else {
                id = "sz" + id;
            }
            return id;
        }).collect(Collectors.toList());
        //构建请求头对象
        HttpHeaders headers = new HttpHeaders();
        //2.1组装请求头
        headers.add("Referer","https://finance.sina.com.cn/stock/");
        headers.add("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
        //2.2组装请求对象
        HttpEntity<Object> entity = new HttpEntity<>(headers);
        //2。将股票集合分开处理 均等分，比如每份20
        /*
         * @Author: pluto
         * @Description: 参数1：指定被分片的集合，参数2：指定分片后每份集合的元素个数
         * @DateTime: 8/12/23 18:20
         * @Return List<List<String>>
         */
        Lists.partition(stockCodeList,20).forEach(list->{
            //3.w为一份动态拼接url，然后通过restTemplate拉取数据
            //格式：http://hq.sinajs.cn/list=sh601003,sh601001,xxx,...
            String url = stockInfoConfig.getMarketUrl() + String.join(",",list);
            String resultData = restTemplate.postForObject(url, entity, String.class);
            //解析处理
            //4。解析数据，封装pojo
            List stockRtInfos = parserStockInfoUtil.parser4StockOrMarketInfo(resultData, ParseType.ASHARE);
            log.info("当前解析数据{}：",stockRtInfos);
            //5。批量插入
            stockRtInfoMapper.insertBatch(stockRtInfos);
        });
    }

    /**
     * 获取板块实时数据
     * http://vip.stock.finance.sina.com.cn/q/view/newSinaHy.php
     */
    @Override
    public void getStockSectorRtIndex() {
        //发送板块数据
        String result = restTemplate.getForObject(stockInfoConfig.getBlockUrl(), String.class);
        //响应结果转 板块集合
        List<StockBlockRtInfo> infos = parserStockInfoUtil.parse4StockBlock(result);
        log.info("板块数据量：{}",infos.size());
        //数据分片保存到数据库下，行业板块大概50个，可每小时查询一次即可
        Lists.partition(infos,20).forEach(list->{
            //20个一组，批量插入
            stockMarketIndexInfoMapper.insertBlock(list);
        });
    }
}
