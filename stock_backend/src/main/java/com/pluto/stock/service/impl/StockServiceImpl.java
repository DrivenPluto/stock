package com.pluto.stock.service.impl;

import com.alibaba.excel.EasyExcel;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.pluto.stock.common.utils.InnerMarketDomain;
import com.pluto.stock.common.utils.StockBlockRtInfoDomain;
import com.pluto.stock.common.utils.StockExcelDomain;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
}
