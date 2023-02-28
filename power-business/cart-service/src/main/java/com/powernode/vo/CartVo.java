package com.powernode.vo;

import com.powernode.model.ShopCart;
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
@ApiModel("购物车对象")
public class CartVo {

    @ApiModelProperty("购物车店铺对象集合")
    private List<ShopCart> shopCarts;
}
