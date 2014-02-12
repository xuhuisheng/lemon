var createUserPicker = function(conf) {
	if (!conf) {
		conf = {
			modalId: 'userPicker',
			multiple: false,
			url: '/mossle-web-user/default/rs/user/search'
		};
	}
	if ($('#' + conf.modalId).length == 0) {
		$(document.body).append(
'<div id="' + conf.modalId + '" class="modal hide fade">'
+'  <div class="modal-header">'
+'    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>'
+'    <h3>选择用户</h3>'
+'  </div>'
+'  <div class="modal-body">'
+'      <!--'
+'	  <article class="m-blank">'
+'	    <div class="pull-left">'
+'		  <form name="userForm" method="post" action="javascript:void(0);return false;" class="form-inline m-form-bottom">'
+'    	    <label for="user_username">账号:</label>'
+'			<input type="text" id="user_username" name="filter_LIKES_username" value="">'
+'			<button class="btn btn-small" onclick="document.userForm.submit()">查询</button>'
+'		  </form>'
+'		</div>'
+'	    <div class="m-clear"></div>'
+'	  </article>'
+'      -->'
+'      <article class="m-widget">'
+'        <header class="header">'
+'		  <h4 class="title">用户列表</h4>'
+'		</header>'
+'		<div class="content">'
+'<form id="userPickerForm" name="userPickerForm" method="post" action="#" class="m-form-blank">'
+'  <table id="userPickerGrid" class="m-table table-hover">'
+'    <thead>'
+'      <tr>'
+'        <th width="10" class="m-table-check">&nbsp;</th>'
+'        <th>账号</th>'
+'      </tr>'
+'    </thead>'
+'    <tbody id="userPickerBody">'
+'      <tr>'
+'        <td><input id="selectedItem1" type="checkbox" class="selectedItem" name="selectedItem" value="1"></td>'
+'        <td>admin</td>'
+'      </tr>'
+'      <tr>'
+'        <td><input id="selectedItem2" type="checkbox" class="selectedItem" name="selectedItem" value="2"></td>'
+'        <td>user</td>'
+'      </tr>'
+'    </tbody>'
+'  </table>'
+'</form>'
+'        </div>'
+'      </article>'
+'  </div>'
+'  <div class="modal-footer">'
+'    <span id="userPickerResult"></span>'
+'    <a id="userPickerBtnClose" href="#" class="btn" data-dismiss="modal">关闭</a>'
+'    <a id="userPickerBtnSelect" href="#" class="btn btn-primary">选择</a>'
+'  </div>'
+'</div>');
	}

	$(document).delegate('.userPicker .add-on', 'click', function(e) {
		$('#' + conf.modalId).data('userPicker', $(this).parent());
		$('#' + conf.modalId).modal();
		$.ajax({
			url: conf.url,
			data: {
				username: ''
			},
			success: function(data) {
				var html = '';
				for (var i = 0; i < data.length; i++) {
					var item = data[i];
					html +=
					  '<tr>'
						+'<td><input id="selectedItem' + i + '" type="radio" class="selectedItem" name="selectedItem" value="'
						+ item.id + '" title="' + item.displayName + '"></td>'
						+'<td><label for="selectedItem' + i + '">' + item.displayName + '</label></td>'
					  +'</tr>'
				}
				$('#' + conf.modalId + 'Body').html(html);
			}
		});
	});

	$(document).delegate('#' + conf.modalId + 'BtnSelect', 'click', function(e) {
		$('#' + conf.modalId).modal('hide');
		var userPickerElement = $('#' + conf.modalId).data('userPicker');
		userPickerElement.children('input[type=hidden]').val($('.selectedItem:checked').val());
		userPickerElement.children('input[type=text]').val($('.selectedItem:checked').attr('title'));
	});
}
