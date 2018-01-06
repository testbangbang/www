package com.onyx.jdread.shop.cloud.entity.jdbean;

import java.util.List;

/**
 * Created by li on 2018/1/5.
 */

public class BookCartItemBean {
    private CartResultBean cartResult;
    private String code;

    public CartResultBean getCartResult() {
        return cartResult;
    }

    public void setCartResult(CartResultBean cartResult) {
        this.cartResult = cartResult;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static class CartResultBean {
        private double cashback;
        private String imageDomain;
        private String newImageDomain;
        private double originalPrice;
        private boolean success;
        private String totalCostcontent;
        private String totalCostcontent2;
        private int totalNum;
        private List<SignalProductListBean> signalProductList;
        private List<SuitEntityListBean> suitEntityList;

        public double getCashback() {
            return cashback;
        }

        public void setCashback(double cashback) {
            this.cashback = cashback;
        }

        public String getImageDomain() {
            return imageDomain;
        }

        public void setImageDomain(String imageDomain) {
            this.imageDomain = imageDomain;
        }

        public String getNewImageDomain() {
            return newImageDomain;
        }

        public void setNewImageDomain(String newImageDomain) {
            this.newImageDomain = newImageDomain;
        }

        public double getOriginalPrice() {
            return originalPrice;
        }

        public void setOriginalPrice(double originalPrice) {
            this.originalPrice = originalPrice;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getTotalCostcontent() {
            return totalCostcontent;
        }

        public void setTotalCostcontent(String totalCostcontent) {
            this.totalCostcontent = totalCostcontent;
        }

        public String getTotalCostcontent2() {
            return totalCostcontent2;
        }

        public void setTotalCostcontent2(String totalCostcontent2) {
            this.totalCostcontent2 = totalCostcontent2;
        }

        public int getTotalNum() {
            return totalNum;
        }

        public void setTotalNum(int totalNum) {
            this.totalNum = totalNum;
        }

        public List<SignalProductListBean> getSignalProductList() {
            return signalProductList;
        }

        public void setSignalProductList(List<SignalProductListBean> signalProductList) {
            this.signalProductList = signalProductList;
        }

        public List<SuitEntityListBean> getSuitEntityList() {
            return suitEntityList;
        }

        public void setSuitEntityList(List<SuitEntityListBean> suitEntityList) {
            this.suitEntityList = suitEntityList;
        }

        public static class SignalProductListBean {
            private String imgUrl;
            private double reAmount;
            private int shopId;
            private String shopName;
            private int shopNum;
            private double shopPrice;

            public String getImgUrl() {
                return imgUrl;
            }

            public void setImgUrl(String imgUrl) {
                this.imgUrl = imgUrl;
            }

            public double getReAmount() {
                return reAmount;
            }

            public void setReAmount(double reAmount) {
                this.reAmount = reAmount;
            }

            public int getShopId() {
                return shopId;
            }

            public void setShopId(int shopId) {
                this.shopId = shopId;
            }

            public String getShopName() {
                return shopName;
            }

            public void setShopName(String shopName) {
                this.shopName = shopName;
            }

            public int getShopNum() {
                return shopNum;
            }

            public void setShopNum(int shopNum) {
                this.shopNum = shopNum;
            }

            public double getShopPrice() {
                return shopPrice;
            }

            public void setShopPrice(double shopPrice) {
                this.shopPrice = shopPrice;
            }
        }

        public static class SuitEntityListBean {
            private PromotionalEntityBean promotionalEntity;
            private List<ProductEntityListBean> productEntityList;

            public PromotionalEntityBean getPromotionalEntity() {
                return promotionalEntity;
            }

            public void setPromotionalEntity(PromotionalEntityBean promotionalEntity) {
                this.promotionalEntity = promotionalEntity;
            }

            public List<ProductEntityListBean> getProductEntityList() {
                return productEntityList;
            }

            public void setProductEntityList(List<ProductEntityListBean> productEntityList) {
                this.productEntityList = productEntityList;
            }

            public static class PromotionalEntityBean {
                private String name;
                private double needPrice;
                private double rePrice;
                private double totalPrice;

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public double getNeedPrice() {
                    return needPrice;
                }

                public void setNeedPrice(double needPrice) {
                    this.needPrice = needPrice;
                }

                public double getRePrice() {
                    return rePrice;
                }

                public void setRePrice(double rePrice) {
                    this.rePrice = rePrice;
                }

                public double getTotalPrice() {
                    return totalPrice;
                }

                public void setTotalPrice(double totalPrice) {
                    this.totalPrice = totalPrice;
                }
            }

            public static class ProductEntityListBean {
                private String imgUrl;
                private double reAmount;
                private int shopId;
                private String shopName;
                private int shopNum;
                private double shopPrice;

                public String getImgUrl() {
                    return imgUrl;
                }

                public void setImgUrl(String imgUrl) {
                    this.imgUrl = imgUrl;
                }

                public double getReAmount() {
                    return reAmount;
                }

                public void setReAmount(double reAmount) {
                    this.reAmount = reAmount;
                }

                public int getShopId() {
                    return shopId;
                }

                public void setShopId(int shopId) {
                    this.shopId = shopId;
                }

                public String getShopName() {
                    return shopName;
                }

                public void setShopName(String shopName) {
                    this.shopName = shopName;
                }

                public int getShopNum() {
                    return shopNum;
                }

                public void setShopNum(int shopNum) {
                    this.shopNum = shopNum;
                }

                public double getShopPrice() {
                    return shopPrice;
                }

                public void setShopPrice(double shopPrice) {
                    this.shopPrice = shopPrice;
                }
            }
        }
    }
}
