package com.pluto.stock;

import com.pluto.stock.service.StockTimerTaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Driven_Pluto
 * @date 8/12/23 16:33
 * @description:
 */
@SpringBootTest
public class TestStockCollect {
    @Autowired
    private StockTimerTaskService stockTimerTaskService;

    @Test
    public void testCollect(){
        stockTimerTaskService.collectInnerMarketInfo();
    }

    @Test
    public void testCollect2(){
        stockTimerTaskService.collectAShareInfo();
    }
    @Test
    public void testCollect3(){
        stockTimerTaskService.getStockSectorRtIndex();
    }
}
