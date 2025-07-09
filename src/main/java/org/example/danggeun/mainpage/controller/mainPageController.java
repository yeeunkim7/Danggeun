package org.example.danggeun.mainpage.controller;

import org.example.danggeun.item.dto.ItemDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class mainPageController {

    @GetMapping("/")
    public String mainPage(Model model) {
        List<ItemDto> items = new ArrayList<>();

        ItemDto item1 = new ItemDto();
        item1.setId(1L);
        item1.setTitle("위닉스제습기");
        item1.setPrice(50000);
        item1.setLocation("서울 마포구 서교동");
        item1.setViewCount(18);
        item1.setChatCount(40);
        item1.setImageUrl("/asset/repo.png");
        items.add(item1);

        ItemDto item2 = new ItemDto();
        item2.setId(2L);
        item2.setTitle("LG 모니터 팝니다");
        item2.setPrice(150000);
        item2.setLocation("경기도 안양시 만안구 안양동");
        item2.setViewCount(32);
        item2.setChatCount(60);
        item2.setImageUrl("/asset/charater.png");
        items.add(item2);

        ItemDto item3 = new ItemDto();
        item3.setId(3L);
        item3.setTitle("닌텐도 스위치 2 마리오카트 월드 미개봉");
        item3.setPrice(650000);
        item3.setLocation("광주 남구 임암동");
        item3.setViewCount(32);
        item3.setChatCount(8);
        item3.setImageUrl("/asset/charater.png");
        items.add(item3);

        ItemDto item4 = new ItemDto();
        item4.setId(4L);
        item4.setTitle("아이폰16pro");
        item4.setPrice(800000);
        item4.setLocation("충북 청주시 용암동");
        item4.setViewCount(12);
        item4.setChatCount(36);
        item4.setImageUrl("/asset/charater.png");
        items.add(item4);

        ItemDto item5 = new ItemDto();
        item5.setId(5L);
        item5.setTitle("이사로 정리합니다. 가구들 필요한거 가져가세요");
        item5.setPrice(999);
        item5.setLocation("광주 북구 신용동");
        item5.setViewCount(58);
        item5.setChatCount(20);
        item5.setImageUrl("/asset/charater.png");
        items.add(item5);

        ItemDto item6 = new ItemDto();
        item6.setId(6L);
        item6.setTitle("워터밤 서울 2025 초대권");
        item6.setPrice(30000);
        item6.setLocation("경기도 성남시 분당구 이매동");
        item6.setViewCount(33);
        item6.setChatCount(31);
        item6.setImageUrl("/asset/charater.png");
        items.add(item6);

        ItemDto item7 = new ItemDto();
        item7.setId(7L);
        item7.setTitle("캠핑테이블");
        item7.setPrice(10000);
        item7.setLocation("경기도 부천시 중동");
        item7.setViewCount(14);
        item7.setChatCount(15);
        item7.setImageUrl("/asset/charater.png");
        items.add(item7);

        ItemDto item8 = new ItemDto();
        item8.setId(8L);
        item8.setTitle("피시방 폐업 정리해요");
        item8.setPrice(30000);
        item8.setLocation("강원도 원주시 단계동");
        item8.setViewCount(19);
        item8.setChatCount(36);
        item8.setImageUrl("/asset/charater.png");
        items.add(item8);

        model.addAttribute("items", items);
        return "mainPage/mainPage";
    }
}
