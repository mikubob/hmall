package com.hmall.common.exception;

/**
 * 购物车数量限制异常
 */
public class CartLimitException extends CommonException {
    
    public CartLimitException(String message) {
        super(message, 400); // 使用400状态码表示客户端错误
    }
    
    public CartLimitException(String message, Throwable cause) {
        super(message, cause, 400);
    }
}