package bj.mapper;

import java.util.List;

import bj.pojo.Goods;

public interface GoodsMapper {
	
	//按价格查询
	public List<Goods> queryPriceGoods();
	
	//默认查询
	public List<Goods> queryGoods();
	
	//清空表数据
	public void updateGoods();
	
	//插入商品数据
	public void insertGoods(Goods goods);

}
