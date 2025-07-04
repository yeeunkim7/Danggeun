package org.example.danggeun.controller.Dto;

public class TradeDto {
    private String title;
    private String productPrice;
    private String productDetail;
    private String address;
    private String imageUrl;
    private int views;
    private int chats;

    public TradeDto() {
    }

    public TradeDto(String title, String productPrice, String productDetail,
                    String address, String imageUrl, int views, int chats) {
        this.title = title;
        this.productPrice = productPrice;
        this.productDetail = productDetail;
        this.address = address;
        this.imageUrl = imageUrl;
        this.views = views;
        this.chats = chats;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductDetail() {
        return productDetail;
    }

    public void setProductDetail(String productDetail) {
        this.productDetail = productDetail;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getChats() {
        return chats;
    }

    public void setChats(int chats) {
        this.chats = chats;
    }
}
