<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/common/taglibs.jsp"%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>修改密码</title>
	<%@include file="/common/s.jsp"%>
    <script type="text/javascript">
jQuery.validator.addMethod("enforcePassword", function(value, element, params) {
	if (!validatePassword(value)) {
		return false;
	}
	if (!validateUsername(value, 'lingo')) {
		return false;
	}
	if (!validateCharacterType(value)) {
		return false;
	}
	if (!validateKeyword(value, ['xiaomi', 'miui', 'mitalk', 'miliao', 'miphone', 'qwer', '1234', '0okm'])) {
		return false;
	}
	return true;
}, "Please follow the password policy.");

$.extend($.validator.messages, {
	enforcePassword: "不满足密码要求"
});

function validatePassword(password) {
	return password != $('#oldPassword').val();
}

function validateUsername(password, username) {
	password = password.toLowerCase();
	username = username.toLowerCase();
	if (password.indexOf(username) != -1) {
		return false;
	}
	for (var i = 0, len = username.length - 1; i < len; i++) {
		var item = username.substring(i, i + 2);
		if (password.indexOf(item) != -1) {
			return false;
		}
	}
	return true;
}

function validateCharacterType(password) {
	var patterns = [];
	for (var i = 0, len = password.length; i < len; i++) {
		var c = password[i];
		if (isNumber(c)) {
			if (patterns.indexOf('number') == -1) {
				patterns.push('number');
			}
		} else if (isUpperCase(c)) {
			if (patterns.indexOf('upper') == -1) {
				patterns.push('upper');
			}
		} else if (isLowerCase(c)) {
			if (patterns.indexOf('lower') == -1) {
				patterns.push('lower');
			}
		} else {
			if (patterns.indexOf('other') == -1) {
				patterns.push('other');
			}
		}
		if (patterns.length > 2) {
			return true;
		}
	}
	return false;
}

function validateKeyword(password, keywords) {
	password = password.toLowerCase();
	for (var i = 0, len = keywords.length; i < len; i++) {
		var item = keywords[i].toLowerCase();
		if (password.indexOf(item) != -1) {
			return false;
		}
	}
	return true;
}

function isNumber(text) {
	return /\d/.test(text);
}

function isUpperCase(text) {
	return /[A-Z]/.test(text);
}

function isLowerCase(text) {
	return /[a-z]/.test(text);
}

$(function() {
    $("#passwordForm").validate({
        submitHandler: function(form) {
			bootbox.animate(false);
			var box = bootbox.dialog('<div class="progress progress-striped active" style="margin:0px;"><div class="bar" style="width: 100%;"></div></div>');
            form.submit();
        },
        errorClass: 'validate-error',
        rules: {
			newPassword: {
				enforcePassword: true
			},
            confirmPassword: {
				equalTo: '#newPassword'
            }
        }
    });
});
     </script>
  </head>

  <body onload='document.f.oldPassword.focus();'>

    <!-- start of header bar -->
    <div class="navbar navbar-inverse">
      <div class="navbar-inner">
        <div class="container">
          <a href="${scopePrefix}/" class="brand">Mossle</a>
        </div>
      </div><!-- /navbar-inner -->
    </div>
    <!-- end of header bar -->

	<div class="row-fluid">
	  <div class="span3"></div>

	<!-- start of main -->
    <section class="span6">
	  <div class="alert m-alert-error" ${param.error==true ? '' : 'style="display:none"'}>
        <strong>修改密码失败</strong>
		&nbsp;
        ${sessionScope['SPRING_SECURITY_LAST_EXCEPTION'].message}
      </div>
      <br>

      <article class="m-widget">
        <header class="header">
		  <h4 class="title">修改密码</h4>
		</header>

		<div class="content content-inner">

<form id="passwordForm" name="f" method="post" action="change-password-save.do" class="form-horizontal">
  <div class="control-group">
    <label class="control-label" for="oldPassword">原密码</label>
	<div class="controls">
      <input type='password' id="oldPassword" name='oldPassword' class="required" value="">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="newPassword">新密码</label>
	<div class="controls">
      <input type='password' id="newPassword" name='newPassword' class="required" value="" minlength="8">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="confirmPassword">重复新密码</label>
	<div class="controls">
      <input type='password' id="confirmPassword" name='confirmPassword' class="required" value="">
    </div>
  </div>
  <div class="control-group">
    <div class="controls">
      <input class="btn" name="submitButton" type="submit" value="<spring:message code='core.login.submit' text='提交'/>"/>
    </div>
  </div>
</form>
        </div>
      </article>

	  <article class="m-widget">
	    <header class="header">
		  <h4 class="title">规则</h4>
		</header>

		<div class="content content-inner">
<ol>
  <li>不能包含用户的帐户名，不能包含用户姓名中超过两个连续字符的部分</li>
  <li>至少有8个字符长</li>
  <li>包含以下四类字符中的三类字符:</li>
  <li>
    <ol>
	  <li>英文大写字母(A 到 Z)</li>
      <li>英文小写字母(a 到 z)</li>
      <li>10个基本数字(0 到 9)</li>
      <li>非字母字符(例如 !、$、#、%)</li>
    </ol>
  </li>
  <li> 密码不能包含指定关键字，如xiaomi、miui、mitalk、miliao、miphone等，包括大小写。</li>
  <li>密码不能包含超过4位的连续键盘字符，如qwer，1234，0okm等，包括大小写。</li>
  <li>不能与最近使用的三次密码相同</li>
</ol>
		</div>
	  </article>

      <div class="m-spacer"></div>
	</section>
	<!-- end of main -->

	  <div class="span3"></div>
    </div>

  </body>
</html>
