<html>
	<head>
		<title>sso auth center</title>
	</head>
	<body>
		<div>
			<form id="login" action="/login" method="post">
				登录用户:<input type="text" name="username" value="kingson" />
				登录密码:<input type="password" name="password" value="123456" />
				<input type="hidden" name="clientUrl" value="${clientUrl!}" />
				<input type="submit" value="Login" />
			</form>
		</div>
	</body>
</html>