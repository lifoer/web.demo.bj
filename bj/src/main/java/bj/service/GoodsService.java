package bj.service;

import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import bj.mapper.GoodsMapper;
import bj.pojo.Goods;
import bj.spider.DdSpider;
import bj.spider.JdSpider;
import bj.spider.JmypSpider;
import bj.spider.YhdSpider;

@Service
public class GoodsService {

	@Autowired
	private GoodsMapper goodsMapper;

	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;

	@Autowired
	private JdSpider jdSpider;

	@Autowired
	private DdSpider ddSpider;

	@Autowired
	private YhdSpider yhdSpider;

	@Autowired
	private JmypSpider jmypSpider;
	
	private final static Logger logger = Logger.getLogger(GoodsService.class);

	public PageInfo<Goods> updateGoods(String word, String[] shop, String sort, int pageNum, HttpSession session) {

		// 定义爬虫闭锁
		CountDownLatch countDownLatch = new CountDownLatch(shop.length);

		// jd爬虫
		Runnable jdRunnable = new Runnable() {
			@Override
			public void run() {
				jdSpider.insertJdGoods(word);
				countDownLatch.countDown();
			}
		};

		// dd爬虫
		Runnable ddRunnable = new Runnable() {
			@Override
			public void run() {
				ddSpider.insertDdGoods(word);
				countDownLatch.countDown();
			}
		};

		// yhd爬虫
		Runnable yhdRunnable = new Runnable() {
			@Override
			public void run() {
				yhdSpider.insertYhdGoods(word);
				countDownLatch.countDown();
			}
		};

		// jmyp爬虫
		Runnable jmypRunnable = new Runnable() {
			@Override
			public void run() {
				jmypSpider.insertJmypGoods(word);
				countDownLatch.countDown();
			}
		};

		// 定义flag，session名称
		String flag = word;
		for (String sh : shop) {
			flag += sh;
		}
		Enumeration<?> attr = session.getAttributeNames();
		while (attr.hasMoreElements()) {
			String key = attr.nextElement().toString();
			if (key != flag) {
				session.removeAttribute(key);
			}
		}

		// 判断session是否存在
		if (session.getAttribute(flag) == null) {
			// 删除表
			goodsMapper.updateGoods();
			// 启动爬虫线程
			for (String shopStr : shop) {
				// 是否查询jd
				if (shopStr.equals("jd")) {
					taskExecutor.execute(jdRunnable);
				}
				// 是否查询dd
				else if (shopStr.equals("dd")) {
					taskExecutor.execute(ddRunnable);
				}
				// 是否查询yhd
				else if (shopStr.equals("yhd")) {
					taskExecutor.execute(yhdRunnable);
				}
				// 是否查询jmyp
				else if (shopStr.equals("jmyp")) {
					taskExecutor.execute(jmypRunnable);
				}
			}
			try {
				// 阻塞,直到爬虫工程完毕
				countDownLatch.await();
			} catch (InterruptedException e) {
				logger.error(e.getStackTrace()[0] + " " +e.toString() + "\n");
			}

			// 设置session
			session.setAttribute(flag, true);
			// session.setMaxInactiveInterval(60);
		}

		// 查询数据
		// 分页，每页30条
		if (sort.equals("price")) {
			PageHelper.startPage(pageNum, 30);
			List<Goods> goodsList = goodsMapper.queryPriceGoods();
			PageInfo<Goods> pageInfo = new PageInfo<Goods>(goodsList);
			return pageInfo;
		} else {
			PageHelper.startPage(pageNum, 30);
			List<Goods> goodsList = goodsMapper.queryGoods();
			PageInfo<Goods> pageInfo = new PageInfo<Goods>(goodsList);
			return pageInfo;
		}
	}
}
