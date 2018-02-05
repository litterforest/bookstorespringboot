<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>
<%@ include file="include/header.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>模拟商品抢购，发出多个请求</title>
<%@ include file="include/pageResources.jsp" %>
<script type="text/javascript">
	function start_onclick()
	{
		
		$("#table").empty();
		$("#table").append($("<tr><th>用户号</th><th>处理线程</th><th>处理时间(秒)</th><th>消息信息</th><th>抢到的个数</th><th>还剩下的个数</th></tr>"));
		var batchNumber = ${param.batchNumber};
		var interval = 20;
		var startIdx = (batchNumber * interval);
		var endIdx = startIdx + interval;
		for (var i = startIdx; i < endIdx; i++)
		{
			$.ajax({
			   type: "POST",
			   url: "${ctx }/Auction/doAuction",
			   data: {userno : i, isbn : "00001"},
			   success: function(data){
		    	  {
			    	  var trObj = $("<tr><td>"+ data.userno +"</td><td>"+ data.threadName +"</td><td>"+ data.duration +"</td><td>"+ data.msg +"</td><td>"+ data.quality +"</td><td>"+ data.leftQuality +"</td></tr>");
			    	  $("#table").append(trObj);
		    	  }
			   },
			   error: function(xhr, textStatus, errorThrown)
			   {
				   var trObj = $("<tr><td colspan=\"6\">"+ textStatus +"</td></tr>");
			       $("#table").append(trObj);
			   }
			});
			
		}
		
	}
</script>
</head>
<body>
	<input type="button" value="开始" onclick="start_onclick();" >
	<table border="1" id="table" >
		<tr>
			<th>用户号</th>
			<th>处理线程</th>
			<th>处理时间(秒)</th>
			<th>消息信息</th>
			<th>抢到的个数</th>
			<th>还剩下的个数</th>
		</tr>
	</table>
</body>
</html>