package org.example.danggeun.trade.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TradeDto {
    private String title;
    private String productPrice;
    private String productDetail;
    private String address;
    private String imageUrl;
    private int views;
    private int chats;
    private MultipartFile productImg;

}
