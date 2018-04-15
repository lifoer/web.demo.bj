package bj.test;

import org.apache.log4j.Logger;

public class Test {
	private final static Logger logger = Logger.getLogger(Test.class);
	public static void main(String[] args) {
		try {
			int i = 1 / 0;
			System.out.println(i);
		
		} catch (Exception e) {
			logger.error(e.getStackTrace()[0] + " " +e.toString() + "\n");
		}
	}
}
