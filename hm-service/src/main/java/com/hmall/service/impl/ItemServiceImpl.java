package com.hmall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmall.common.exception.BizIllegalException;
import com.hmall.common.utils.BeanUtils;
import com.hmall.domain.dto.ItemDTO;
import com.hmall.domain.dto.OrderDetailDTO;
import com.hmall.domain.po.Item;
import com.hmall.mapper.ItemMapper;
import com.hmall.service.IItemService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 * @author 虎哥
 */
@Service
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item> implements IItemService {

    /**
     * 批量扣减库存
     * @param items
     */
    @Override
    public void deductStock(List<OrderDetailDTO> items) {
        String sqlStatement = "com.hmall.mapper.ItemMapper.updateStock";
        boolean r = false;
        try {
            r = executeBatch(items, (sqlSession, entity) -> sqlSession.update(sqlStatement, entity));
        } catch (Exception e) {
            throw new BizIllegalException("更新库存异常，可能是库存不足!", e);
        }
        if (!r) {
            throw new BizIllegalException("库存不足！");
        }
    }

    /**
     * 查询商品信息
     * @param ids
     * @return
     */
    @Override
    public List<ItemDTO> queryItemByIds(Collection<Long> ids) {
        return BeanUtils.copyList(listByIds(ids), ItemDTO.class);
    }

    /**
     * 恢复库存
     * @param orderDetails
     */
    @Override
    public void restoreStock(List<OrderDetailDTO> orderDetails) {
        for (OrderDetailDTO orderDetail : orderDetails) {
            //1. 根据商品id查询商品信息
            Item item = lambdaQuery().eq(Item::getId, orderDetail.getItemId()).one();
            //2.还原库存
            lambdaUpdate()
                    .set(Item::getStock, item.getStock() + orderDetail.getNum())//现在的库存+购买的数量
                    .eq(Item::getId, orderDetail.getItemId())
                    .update();
        }
    }
}
