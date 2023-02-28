package com.powernode.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel("购物车店铺对象")
public class ShopCart {

    @ApiModelProperty("店铺id")
    private Long shopId;

    @ApiModelProperty("店铺商品条目集合对象")
    private List<CartItem> shopCartItems;
}
