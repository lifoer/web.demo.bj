<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8" />
	<title>lifo</title>
	<link rel="stylesheet" type="text/css" href="/staticfile/css/index.css">
	<script type="text/javascript" src="/staticfile/js/jquery-3.3.1.min.js"></script>
</head>
<body>
	 <div id="logo">
		<img alt="lifo" src="/staticfile/image/logo.png">
	</div>
	<div>
		<nobr>
			<form action="goods.html" method="post" id="thisform">
				<div id="se">
					<input type="text" name="word" value="${word}" id="se_in">
					<input type="submit"  value="lifo" id="se_bu">
				</div>
				
				<div id="select">
					<input type="checkbox" name="shop" value="jd" <c:if test="${checkJd}">checked</c:if>>京东&emsp;
					<input type="checkbox" name="shop" value="dd" <c:if test="${checkDd}">checked</c:if>>当当&emsp;
					<input type="checkbox" name="shop" value="yhd" <c:if test="${checkYhd}">checked</c:if>>一号店&emsp;
					<input type="checkbox" name="shop" value="jmyp" <c:if test="${checkJmyp}">checked</c:if>>聚美优品&emsp;
					<input type="radio" name="sort" value="default" <c:if test="${checkDe}">checked</c:if>>默认方式&emsp;
					<input type="radio" name="sort" value="price" <c:if test="${checkPr}">checked</c:if>>价格优先&emsp;
					<c:if test="${pageInfo.hasPreviousPage}">
						<input type="hidden" id="beforeinput"/>  
						<a id="beforepage" class="page">←</a>
					</c:if>
				 	<c:if test="${pageInfo.hasNextPage}">  
	                	<input type="hidden" id="afterinput"/>
	                	<a  id="afterpage" class="page">&emsp;&emsp;&emsp;→</a>
		        	</c:if>
		        	<script type="text/javascript">
							$(function(){
								$("#beforepage").click(function() {
									$("#beforeinput").val("${pageInfo.pageNum-1}");
									$("#beforeinput").attr("name","pageNum");
									$("#afterinput").attr("name","");
									$("#thisform").submit();	
								});
								$("#afterpage").click(function() {
									$("#afterinput").val("${pageInfo.pageNum+1}");
									$("#afterinput").attr("name","pageNum");
									$("#beforeinput").attr("name","");
									$("#thisform").submit();
								});
							});
					</script>
				</div>	
			</form>
		</nobr>
	</div>
	<div id="shop">
		<ul id="list">
		 <c:forEach items="${pageInfo.list}" var="goods">
			<li class="goods">
				<a href="${goods.link}" target="_blank">
					<img id="picture" src="${goods.image}">
				</a>
				<p class="price">
					<c:choose>
						<c:when test="${goods.price gt 0}">
							￥${goods.price}
						</c:when>
						<c:otherwise>
							暂未获取价格
						</c:otherwise>
					</c:choose>
					
				</p>
				<p class="name">
					<a href="${goods.link}" target="_blank">
						 ${goods.title}
					</a>
				</p>
				<p class="des">
					${goods.des}
				</p>
				<p class="comment">
					${goods.comment}条评论
				</p>
				<p class="mall">
					<c:if test="${goods.mall eq '京东'}">
						<a href="https://www.jd.com" target="_blank">${goods.mall}商城</a>
					</c:if>
					<c:if test="${goods.mall eq '当当'}">
						<a href="http://www.dangdang.com" target="_blank">${goods.mall}商城</a>
					</c:if>
					 <c:if test="${goods.mall eq '一号店'}">
						<a href="https://www.yhd.com">${goods.mall}商城</a>
					</c:if>
					<c:if test="${goods.mall eq '聚美优品'}">
						<a href="http://bj.jumei.com">${goods.mall}商城</a>
					</c:if>
				</p>
			</li>
		</c:forEach>
		</ul>
	</div>
</body>
</html>


