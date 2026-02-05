package com.hmall.cart.controller;


import com.hmall.cart.domain.dto.CartFormDTO;
import com.hmall.cart.domain.po.Cart;
import com.hmall.cart.domain.vo.CartVO;
import com.hmall.cart.service.ICartService;
import com.hmall.common.domain.R;
import com.hmall.common.exception.CartLimitException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "购物车相关接口")
@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
@Slf4j
public class CartController {
    private final ICartService cartService;

    @Operation(summary = "添加商品到购物车")
    @PostMapping
    public R<Void> addItem2Cart(@Valid @RequestBody CartFormDTO cartFormDTO){
        try {
            cartService.addItem2Cart(cartFormDTO);
            return R.ok();
        } catch (CartLimitException e) {
            log.warn("购物车添加数量超限: {}", e.getMessage());
            return R.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("添加商品到购物车失败", e);
            return R.error("添加商品失败");
        }
    }

    @Operation(summary = "更新购物车数据")
    @PutMapping
    public R<Void> updateCart(@RequestBody Cart cart){
        try {
            cartService.updateCartWithValidation(cart);
            return R.ok();
        } catch (CartLimitException e) {
            log.warn("购物车更新数量超限: {}", e.getMessage());
            return R.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("更新购物车失败", e);
            return R.error("更新购物车失败");
        }
    }

    @Operation(summary = "删除购物车中商品")
    @DeleteMapping("{id}")
    public void deleteCartItem(@Param ("购物车条目id")@PathVariable("id") Long id){
        cartService.removeById(id);
    }

    @Operation(summary = "查询购物车列表")
    @GetMapping
    public List<CartVO> queryMyCarts(){
        return cartService.queryMyCarts();
    }
    @Operation(summary = "批量删除购物车中商品")
    @Parameter(name = "ids", description = "购物车条目id集合")
    @DeleteMapping
    public void deleteCartItemByIds(@RequestParam("ids") List<Long> ids){
        cartService.removeByItemIds(ids);
    }
}