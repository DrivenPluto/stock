package com.pluto.stock.controller;

import com.pluto.stock.pojo.StockBusiness;
import com.pluto.stock.service.StockService;
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
}
