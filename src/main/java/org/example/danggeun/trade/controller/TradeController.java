package org.example.danggeun.trade.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TradeController {

    @GetMapping("/trade")
    public String showTradePage() {
        return "trade/trade";
    }

}
