<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:useBean id="security" class="security.web.BaseSecurityAction" scope="page" />

<%@ include file="include/pagetop.jsp"%>

<!DOCTYPE html>
<html>

<head>
<%@ include file="include/head.jsp"%>
</head>

<body>

	<%@ include file="include/loading.jsp"%>

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTENT -->
	<div class="ifr-dody">

		<!-- START CONTAINER -->
		<div class="ifr-con">
			<h3>用户管理</h3>

			<%@ include file="include/alert.jsp"%>
			
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<form action="<%=basePath%>/mall/seller/list.action" method="post" id="queryForm">
			    <input type="hidden" name="pageNo" id="pageNo"/>
				<input type="hidden" name="name_para" id="name_para"/>
				<input type="hidden" name="rolename_para" id="rolename_para"/>				
			</form>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			
			<div class="row">
				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">

						<div class="panel-title">
							新增店铺用户
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>

						<div class="panel-body">
						
							<form class="form-horizontal" action="<%=basePath%>normal/adminUserAction!insert-shop"
								method="post" name="mainForm" id="mainForm">
								
								<input type="hidden" name="id" id="id"/>

									<div class="form-group">
										<label class="col-sm-2 control-label form-label">用户名(邮箱账号)</label>
										<div class="col-sm-5">
											<input id="username" name="username" class="form-control" maxlength="100" placeholder="必须符合邮箱格式,长度不超过100"/>
										</div>
									</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">手机号</label>

									<div class="col-sm-1">
										<select id="mobilePrefix" name="mobilePrefix" class="form-control">
											<c:forEach var="item" items="${mobileMap}">
												<option value="${item.key}">${item.value}</option>
											</c:forEach>
										</select>
									</div>

									<div class="col-sm-4">
										<input id="phone" name="phone" class="form-control" maxlength="20"
											   oninput="value=value.replace(/[^\d]/g,'')" placeholder="最大长度为20"/>
									</div>
								</div>
									<div class="form-group">
										<label class="col-sm-2 control-label form-label">店铺名</label>
										<div class="col-sm-5">
											<input id="sellerName" name="sellerName" class="form-control" maxlength="50" placeholder="长度必须为2-50"/>
										</div>
									</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">上级用户或上级代理商UID(选填)</label>
									<div class="col-sm-5">
										<input id="parentsCode" name="parentsCode" class="form-control" maxlength="12" minlength="4" placeholder="请输入上级用户UID"/>
									</div>
								</div>
									<div class="form-group">
										<label class="col-sm-2 control-label form-label">登录密码</label>
										<div class="col-sm-5">
											<input id="password" name="password" class="form-control" maxlength="12" minlength="6" placeholder="长度6-12位,允许字母、数字、下划线等常规字符"/>
										</div>
									</div>
								
									<div class="form-group">
										<label class="col-sm-2 control-label form-label"></label>
										<div class="col-sm-5">
											演示账号资金密码默认为000000，可登录后修改
										</div>
									</div>
			

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">登录权限</label>
									<div class="col-sm-4">
										<select id="loginAuthority" name="loginAuthority" class="form-control " >
										   <option value="1" selected>正常</option>
										   <option value="0">限制登录</option>
										</select>
									</div>
								</div>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">是否锁定</label>
									<div class="col-sm-4">									
										<select id="enabled" name="enabled" class="form-control " >
										   <option value="1" selected>正常</option>
										   <option value="0">业务锁定（登录不受影响）</option>
										</select>											
									</div>
								</div>


								<div class="form-group">
									<label for="remarks" class="col-sm-2 control-label form-label">备注</label>
									<div class="col-sm-5">
										<input id="remarks" name="remarks" class="form-control input-lg" rows="3" cols="10"/>
									</div>
								</div>


								<div class="form-group">
									<div class="col-sm-offset-2 col-sm-10">
										<a href="javascript:goUrl()" class="btn">取消</a>
										<a href="javascript:submit()" class="btn btn-default">保存</a>
									</div>
								</div>

							</form>

						</div>

					</div>
				</div>
			</div>

		</div>
		<!-- END CONTAINER -->

		<%@ include file="include/footer.jsp"%>

	</div>
	<!-- End Content -->
	<!-- //////////////////////////////////////////////////////////////////////////// -->

	<%@ include file="include/js.jsp"%>
	<script src="path/to/select2.min.js"></script>
	<script type="text/javascript">
		var a = true;
		function submit() {
			swal({
				title : "是否保存?",
				text : "",
				type : "warning",
				showCancelButton : true,
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : false
			}, function() {
				let phone="";
				if($("#phone").val()){
					phone=$("#mobilePrefix").val()+" "+$("#phone").val()
				}
				checkParameter();
				if (!a){
					a = true;
					return false;
				}
				let data = {
					username: $("#username").val(),
					password: $("#password").val(),
					sellerName: $("#sellerName").val(),
					loginAuthority: $("#loginAuthority").val(),
					enabled: $("#enabled").val(),
					remarks: $("#remarks").val(),
					parentsCode: $("#parentsCode").val(),
					phone: phone
				};

				$.ajax({
					url: "<%=basePath%>normal/adminUserAction!insert-shop",
					type: "POST",
					data: data,
					success: function(res) {
						console.log("响应",res)
						if(res.code==='1'){
							swal({
							title: "提交成功",
							timer: 1500,
							showConfirmButton: false
						})
						}else{
							swal({
								title: res.msg,
								timer: 1500,
								showConfirmButton: false
							})
						}
					},
					error: function(err) {
						swal({
							title: "提交失败",
							timer: 1500,
							showConfirmButton: false
						})
						console.error(err);
					}
				});
			});
		}

		function checkParameter(){
			let username = $("#username").val();
			let password = $("#password").val();
			let sellerName = $("#sellerName").val();
			if(password.length > 12 || password.length < 6){
				swal({
					title: "密码长度必须在6到12位之间",
					timer: 1500,
					showConfirmButton: false
				})
				a = false;
			}

			let usernameRegExp = /^[\w\u4e00-\u9fa5@.]{2,100}$/;
			let usernameRegExp2 = /^.{2,50}$/;
			if(username){
				let ok = username != null && usernameRegExp.test(username);
				if(!ok){
					swal({
						title: "用户名(邮箱账号)格式不正确,必须符合邮箱格式,长度不超过100",
						timer: 1500,
						showConfirmButton: false
					})
					a = false;
				}
			}

			let ok2 = sellerName != null && usernameRegExp2.test(sellerName);
			if(!ok2){
				swal({
					title: "店铺名格式不正确,长度必须为2-50",
					timer: 1500,
					showConfirmButton: false
				})
				a = false;
			}
		}

		$(function(){
			$('.nav-tabs a').filter(function() {
				var b = document.URL;
				return this.href == "<%=basePath%>normal/adminUserAction!toAdd.action?registerType=${registerType}";  //获取当前页面的地址
			}).closest('li').addClass('active');  //给当前最靠近的li（其实是现在进入的li）添加‘active’样式

		})

		$(document).ready(function() {
			$("#mobilePrefix").select2();
		});

		// 获取下拉列表元素
		var selectElement = document.getElementById("mobilePrefix");

		// 添加事件监听，响应输入框的值变化
		selectElement.addEventListener("input", function() {
			var inputText = selectElement.value.toLowerCase(); // 获取输入框的值并转为小写
			var options = selectElement.getElementsByTagName("option");

			for (var i = 0; i < options.length; i++) {
				var optionText = options[i].textContent.toLowerCase();
				if (optionText.includes(inputText)) {
					options[i].style.display = ""; // 显示匹配的选项
				}
			}
		});
	</script>
	
</body>

</html>
