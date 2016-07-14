var createUserPicker = function(conf) {
	conf = conf ? conf : {};
	var defaults = {
		modalId: 'userPicker',
		multiple: false,
		showExpression: false,
		url: '/mossle-web-user/default/rs/user/search'
	};
	for (var key in defaults) {
		if (!conf[key]) {
			conf[key] = defaults[key];
		}
	}

    if ($('#' + conf.modalId).length == 0) {
        $(document.body).append(
'<div id="' + conf.modalId + '" class="modal hide fade">'
+'  <div class="modal-header">'
+'    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>'
+'    <h3>选择用户</h3>'
+'  </div>'
+'  <div class="modal-body">'
+'    <ul class="nav nav-tabs" role="tablist" id="userPickerTabs">'
+'      <li role="presentation" class="active"><a href="#user" aria-controls="user" role="tab" data-toggle="tab">选择用户</a></li>'
+'      <li role="presentation" ' + (conf.showExpression !== true ? 'style="display:none;"' : '') + '><a href="#common" aria-controls="common" role="tab" data-toggle="tab">常用语</a></li>'
+'      <li role="presentation" ' + (conf.showExpression !== true ? 'style="display:none;"' : '') + '><a href="#expr" aria-controls="expr" role="tab" data-toggle="tab">表达式</a></li>'
+'    </ul>'
+'    <div class="tab-content">'
+'      <div role="tabpanel" class="tab-pane active" id="user">'
+'    <article class="m-blank">'
+'      <div class="pull-left" style="display:table"><div style="display:table-cell">'
+'        <label for="' + conf.modalId + '_username" style="display:inline">账号:</label>'
+'        <input type="text" id="' + conf.modalId + '_username" value="" style="margin-bottom:0px;">'
+'        <button id="' + conf.modalId + '_search" class="btn btn-small">查询</button></div>'
+'      </div>'
+'      <div class="m-clear"></div>'
+'    </article>'
+'    <article class="m-widget">'
+'      <header class="header">'
+'        <h4 class="title">用户</h4>'
+'      </header>'
+'      <div class="content">'
+'  <table id="' + conf.modalId + '_grid" class="m-table table-hover">'
+'    <thead>'
+'      <tr>'
+'        <th width="10" class="m-table-check">&nbsp;</th>'
+'        <th>姓名</th>'
+'      </tr>'
+'    </thead>'
+'    <tbody id="' + conf.modalId + '_body">'
+'      <tr>'
+'        <td><input id="' + conf.modalId + '_item_1" type="' + (conf.multiple ? 'checkbox' : 'radio') + '" name="selectedItem" class="selectedItem" value="1" title="admin" style="margin-top:0px;"></td>'
+'        <td>admin</td>'
+'      </tr>'
+'      <tr>'
+'        <td><input id="' + conf.modalId + '_item_2" type="' + (conf.multiple ? 'checkbox' : 'radio') + '" name="selectedItem" class="selectedItem" value="2" title="user" style="margin-top:0px;"></td>'
+'        <td>user</td>'
+'      </tr>'
+'    </tbody>'
+'  </table>'
+'      </div>'
+'    </article>'
+'      </div>'
+'      <div role="tabpanel" class="tab-pane" id="common">'
+'    <article class="m-widget">'
+'      <header class="header">'
+'        <h4 class="title">用户</h4>'
+'      </header>'
+'      <div class="content">'
+'  <table id="' + conf.modalId + '_commonGrid" class="m-table table-hover">'
+'    <thead>'
+'      <tr>'
+'        <th width="10" class="m-table-check">&nbsp;</th>'
+'        <th>姓名</th>'
+'      </tr>'
+'    </thead>'
+'    <tbody id="' + conf.modalId + '_commonBody">'
+'      <tr>'
+'        <td><input id="' + conf.modalId + '_item_common_1" type="radio" name="selectedItem" class="selectedItem" value="常用语:直接上级" title="常用语:直接上级" style="margin-top:0px;"></td>'
+'        <td>常用语:直接上级</td>'
+'      </tr>'
+'      <tr>'
+'        <td><input id="' + conf.modalId + '_item_common_2" type="radio" name="selectedItem" class="selectedItem" value="岗位:经理" title="岗位:经理" style="margin-top:0px;"></td>'
+'        <td>岗位:经理</td>'
+'      </tr>'
+'      <tr>'
+'        <td><input id="' + conf.modalId + '_item_common_3" type="radio" name="selectedItem" class="selectedItem" value="岗位:总经理" title="岗位:总经理" style="margin-top:0px;"></td>'
+'        <td>岗位:总经理</td>'
+'      </tr>'
+'    </tbody>'
+'  </table>'
+'      </div>'
+'    </article>'
+'      </div>'
+'      <div role="tabpanel" class="tab-pane" id="expr">'
+'    <article class="m-widget">'
+'      <header class="header">'
+'        <h4 class="title">表达式</h4>'
+'      </header>'
+'      <div class="content">'
+'  <table id="' + conf.modalId + '_exprGrid" class="m-table table-hover">'
+'    <thead>'
+'      <tr>'
+'        <th>姓名</th>'
+'      </tr>'
+'    </thead>'
+'    <tbody id="' + conf.modalId + '_exprBody">'
+'      <tr>'
+'        <td><input id="' + conf.modalId + '_item_expr_1" type="text" name="selectedItem" class="selectedItem" value="${initiator}" title="${initator}" style="margin-top:0px;"></td>'
+'      </tr>'
+'    </tbody>'
+'  </table>'
+'      </div>'
+'    </article>'
+'      </div>'
+'    </div>'
+'  </div>'
+'  <div class="modal-footer">'
+'    <span id="' + conf.modalId + '_result" style="float:left;"></span>'
+'    <a id="' + conf.modalId + '_close" href="#" class="btn" data-dismiss="modal">关闭</a>'
+'    <a id="' + conf.modalId + '_select" href="#" class="btn btn-primary">选择</a>'
+'  </div>'
+'</div>');

		$('#userPickerTabs a').click(function (e) {
		  e.preventDefault();
		  $(this).tab('show');
		})
    }

	var doSearch = function(username) {
        $.ajax({
            url: conf.url,
            data: {
                username: username
            },
            success: function(data) {
                var html = '';
                for (var i = 0; i < data.length; i++) {
                    var item = data[i];
                    html +=
                      '<tr>'
                        +'<td><input id="' + conf.modalId + '_item_' + i + '" type="' + (conf.multiple ? 'checkbox' : 'radio') + '" class="selectedItem" name="name" value="'
                        + item.id + '" title="' + item.displayName + '"></td>'
                        +'<td><label for="' + conf.modalId + '_item_' + i + '">' + item.displayName + '</label></td>'
                      +'</tr>'
                }
                $('#' + conf.modalId + '_body').html(html);
            }
        });
	}

    $(document).delegate('.userPicker .add-on', 'click', function(e) {
        $('#' + conf.modalId).data('userPicker', $(this).parent());
        $('#' + conf.modalId).modal();

		doSearch('');
    });

    // $(document).delegate('#' + conf.modalId + '_body tr', 'click', function(e) {
	//	$('input[type=radio].selectedItem').prop('checked', false);
	//	$(this).find('.selectedItem').prop('checked', true);
    // });

    $(document).delegate('#' + conf.modalId + '_body .selectedItem', 'click', function(e) {
		if (conf.multiple) {
			var el = $(this);
			if (el.prop('checked')) {
				var html = '&nbsp;<span class="label" id="' + $(this).val() + '" title="' + $(this).attr('title') + '">' + $(this).attr('title') + '<i class="icon-minus-sign" style="cursor:pointer;"></i></span>';
				$('#' + conf.modalId + '_result').append(html);
			} else {
				$('#' + conf.modalId + '_result #' + el.val()).remove();
			}
		} else {
			var html = '<span class="label" id="' + $(this).val() + '" title="' + $(this).attr('title') + '">' + $(this).attr('title') + '<i class="icon-minus-sign" style="cursor:pointer;"></i></span>';
			$('#' + conf.modalId + '_result').html(html);
		}
	});

    $(document).delegate('#' + conf.modalId + '_commonBody .selectedItem', 'click', function(e) {
		var html = '<span class="label" id="' + $(this).val() + '" title="' + $(this).attr('title') + '">' + $(this).attr('title') + '<i class="icon-minus-sign" style="cursor:pointer;"></i></span>';
		$('#' + conf.modalId + '_result').html(html);
	});

    $(document).delegate('#' + conf.modalId + '_exprBody .selectedItem', 'click', function(e) {
		var html = '<span class="label" id="' + $(this).val() + '" title="' + $(this).attr('title') + '">' + $(this).attr('title') + '<i class="icon-minus-sign" style="cursor:pointer;"></i></span>';
		$('#' + conf.modalId + '_result').html(html);
	});

	$(document).delegate('.icon-minus-sign', 'click', function(e) {
		var id = $(this).parent().attr('id');
		$('#' + conf.modalId + '_item_' + id).prop('checked', false);
		$(this).parent().remove();
	});

	$(document).delegate('#' + conf.modalId + '_search', 'click', function(e) {
		doSearch($('#' + conf.modalId + '_username').val());
	});

    $(document).delegate('#' + conf.modalId + '_select', 'click', function(e) {
        $('#' + conf.modalId).modal('hide');
        var userPickerElement = $('#' + conf.modalId).data('userPicker');
		if (conf.multiple) {
			var el = $('#' + conf.modalId + '_result .label');
			var ids = [];
			var names = [];
			el.each(function(index, item) {
				ids.push($(item).attr('id'));
				names.push($(item).attr('title'));
			});
			userPickerElement.children('input[type=hidden]').val(ids.join(','));
			userPickerElement.children('input[type=text]').val(names.join(','));
		} else {
			var el = $('#' + conf.modalId + '_result .label');
			userPickerElement.children('input[type=hidden]').val(el.attr('id'));
			userPickerElement.children('input[type=text]').val(el.attr('title'));
		}
    });
}
