package bj.pojo;

public class Goods {
	private String no;
	private String title;
	private Double price;
	private String comment;
	private String des;
	private String link;
	private String image;
	private String mall;
	
	public Goods() {
	}
	
	public Goods(String no, String title, Double price, String comment, String des, String link, String image,
			String mall) {
		super();
		this.no = no;
		this.title = title;
		this.price = price;
		this.comment = comment;
		this.des = des;
		this.link = link;
		this.image = image;
		this.mall = mall;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getMall() {
		return mall;
	}

	public void setMall(String mall) {
		this.mall = mall;
	}

	@Override
	public String toString() {
		return "Goods [no=" + no + ", title=" + title + ", price=" + price + ", comment=" + comment + ", des=" + des
				+ ", link=" + link + ", image=" + image + ", mall=" + mall + "]";
	}
}
