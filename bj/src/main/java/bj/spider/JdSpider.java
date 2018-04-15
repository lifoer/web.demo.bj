package bj.spider;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bj.mapper.GoodsMapper;
import bj.pojo.Goods;

@Service
public class JdSpider {

	@Autowired
	private GoodsMapper goodsMapper;
	
	private final static Logger logger = Logger.getLogger(JdSpider.class);
	
	public void insertJdGoods(String keyword) {
		// 页数，每次加2
		int page = 1;
		// 条数，每次加1
		int s = 1;
		for (int i = 0; i < 1; i++) {
			try {
				keyword = new String(keyword.getBytes(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				logger.error(e.getStackTrace()[0] + " " +e.toString() + "\n");
				continue;
			}
			jdCrawler(keyword, page, s);
			s += 30;
			page++;
		}
	}

	private void jdCrawler(String keyword, int page, int s) {
		String url = "https://search.jd.com/Search?keyword=" + keyword + "&enc=utf-8&wq=" + keyword + "&psort=3&page="
				+ page + "&s=" + s + "&click=0&scrolling=y";
		Connection conn = Jsoup.connect(url);
		try {
			Document doc = conn.get();
			for (int i = 0; i < 30; i++) {
				try {
					// 生成商品序号uuid
					String no = UUID.randomUUID().toString();
					//为了通用，新加判断条件
					Elements eles = doc.select("#J_goodsList ul li .p-name").select(".p-name-type-2");
					// 获取商品名称
					Element ele1;
					if(eles != null && !eles.toString().equals("")) {
						ele1 = doc.select("#J_goodsList ul li .gl-i-wrap").select(".p-name")
							.select(".p-name-type-2 em").get(i);
					} else {
						ele1 = doc.select("#J_goodsList ul li .gl-i-wrap").select(".p-name em").get(i);
					}
					String title = ele1.toString().replaceAll("</?font[^><]*>", " ").replaceAll("</?em[^><]*>", "")
							.replaceAll("</?img[^><]*>", "");
					// 获取商品价格
					double price = 0L;
					Element ele2 = doc.select("#J_goodsList ul li .gl-i-wrap .p-price strong i").get(i);
					Element ele2New = doc.select("#J_goodsList ul li .gl-i-wrap .p-price strong").get(i);
					if (!ele2.text().equals("")) {
						price = Double.parseDouble(ele2.text());
					} else if (!ele2New.attr("data-price").equals("")) {
						price = Double.parseDouble(ele2New.attr("data-price"));
					}
					// 获取商品图片
					Element ele3 = doc.select("#J_goodsList ul li .gl-i-wrap .p-img a img").get(i);
					String image = "";
					if (ele3.attr("src") != null && !ele3.attr("src").equals("")) {
						image = "http:" + ele3.attr("src");
					} else if (ele3.attr("data-lazy-img") != null && !ele3.attr("data-lazy-img").equals("")) {
						image = "http:" + ele3.attr("data-lazy-img");
					}
					// 商品图片下载
					/*
					 * if(!image.equals("")) { String imageName =
					 * UUID.randomUUID().toString() +
					 * image.substring(image.lastIndexOf(".")); String dir =
					 * "E://pictures/jd"; FileDown.imageDown(image, dir,
					 * imageName); }
					 */
					// 获取商品描描述
					Element ele4;
					if(eles != null && !eles.toString().equals("")) {
						ele4 = doc.select("#J_goodsList ul li .p-name").select(".p-name-type-2 a").get(i);
					} else {
						ele4 = doc.select("#J_goodsList ul li .p-name a").get(i);
					}
					String des = ele4.attr("title");
					// 获取商品链接
					Element ele5;
					if(eles != null && !eles.toString().equals("")) {
						ele5 = doc.select("#J_goodsList ul li .p-name").select(".p-name-type-2 a").get(i);
					} else {
						ele5 = doc.select("#J_goodsList ul li .p-name a").get(i);
					}
					String link = "http:" + ele5.attr("href");
					// 获取商品评论数
					Element ele6 = doc.select("#J_goodsList ul li .p-commit strong a").get(i);
					String comment = ele6.text();
					// 设置商城名称
					String mall = "京东";
					// 封装Goods
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
