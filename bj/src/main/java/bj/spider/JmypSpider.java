package bj.spider;

import java.io.IOException;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bj.mapper.GoodsMapper;
import bj.pojo.Goods;

@Service
public class JmypSpider {

	@Autowired
	private GoodsMapper goodsMapper;
	
	private final static Logger logger = Logger.getLogger(JmypSpider.class);

	public void insertJmypGoods(String keyword) {
		int page = 1;
		JmypCrawlyer(keyword, page);
	}

	private void JmypCrawlyer(String keyword, int page) {
		String url = "http://search.jumei.com/?filter=0-11-" + page + "&search=" + keyword + "#search_list_wrap";
		Connection conn = Jsoup.connect(url);
		try {
			Document doc = conn.get();
			for (int i = 0; i < 36; i++) {
				try {
					String no = UUID.randomUUID().toString();
					// 获取商品名称
					Element ele1 = doc.select("#search_list_wrap .products_wrap li").select(".s_l_name a").get(i);
					String title = ele1.text();
					// 获取商品价格
					Element ele2 = doc.select("#search_list_wrap .products_wrap li")
							.select(".s_l_view_bg .search_list_price label+span").get(i);
					Double price = Double.parseDouble(ele2.text());
					// 获取商品评论
					String comment = "";
					// 获取商品描述
					Element ele3 = doc.select("#search_list_wrap .products_wrap li").select(".search_deal_buttom_bg")
							.select(".clearfix").get(i);
					String div = ele3.text();
					String des = div.replaceAll("</?span[^><]*>", "");
					// 获取商品链接
					Element ele4 = doc.select("#search_list_wrap .products_wrap li").select(".s_l_name a").get(i);
					String link = ele4.attr("href");
					// 获取商品图片链接
					Element ele5 = doc.select("#search_list_wrap .products_wrap li").select(".s_l_pic img").get(i);
					String image = ele5.attr("original");
					// 商城名称
					String mall = "聚美优品";
					Goods goods = new Goods(no, title, price, comment, des, link, image, mall);
					goodsMapper.insertGoods(goods);
				} catch (Exception e) {
					logger.error(e.getStackTrace()[0] + " " +e.toString() + "\n");
					continue;
				}
			}
		} catch (IOException e) {
			logger.error(e.getStackTrace()[0] + " " +e.toString() + "\n");
		}

	}
}
