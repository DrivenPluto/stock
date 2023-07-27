package com.pluto.stock.service.impl;

import com.pluto.stock.mapper.StockBusinessMapper;
import com.pluto.stock.pojo.StockBusiness;
import com.pluto.stock.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockServiceImpl implements StockService {
    @Autowired
    private StockBusinessMapper stockBusinessMapper;

    @Override
    public List<StockBusiness> findAll() {
        return stockBusinessMapper.findAll();
    }
}
