var createUserPicker = function(conf) {
	conf = conf ? conf : {};
	var defaults = {
		modalId: 'userPicker',
		multiple: false,
		searchUrl: '/mossle-web-user/default/rs/user/search',
		treeUrl: '/mossle-app-lemon/rs/party/tree?partyStructTypeId=1'
	};
	for (var key in defaults) {
		if (!conf[key]) {
			conf[key] = defaults[key];
		}
	}

    if ($('#' + conf.modalId).length == 0) {
        $(document.body).append(
'<div id="' + conf.modalId + '" class="modal fade">'
+'  <div class="modal-dialog">'
+'    <div class="modal-content">'
+'      <div class="modal-header">'
+'        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>'
+'        <h3>选择用户</h3>'
+'      </div>'
+'      <div class="modal-body">'
+'        <ul id="myTabs" class="nav nav-tabs">'
+'          <li role="presentation" class="active"><a href="#org">组织机构</a></li>'
+'          <li role="presentation"><a href="#alias">常用语</a></li>'
+'          <li role="presentation"><a href="#expr">表达式</a></li>'
+'        </ul>'
+'        <div class="tab-content">'
+'          <div role="tabpanel" class="tab-pane active" id="org">'
+'        <div class="row" id="org">'
+'          <div class="col-md-4">'
+'            <ul id="' + conf.modalId + 'treeMenu" class="ztree"></ul>'
+'          </div>'
+'          <div class="col-md-8">'
+'            <div>'
+'              <label for="' + conf.modalId + '_username" style="display:inline" class="">账号:</label>'
+'              <input type="text" id="' + conf.modalId + '_username" value="" style="margin-bottom:0px; width:auto; display:inline;" class="form-control">'
+'              <button id="' + conf.modalId + '_search" class="btn btn-default">查询</button>'
+'            </div>'
+'            <div class="panel panel-default" style="max-height:300px;overflow:auto;">'
+'              <div class="panel-heading">'
+'                <h3 class="panel-title">用户</h3>'
+'              </div>'
+'              <table id="' + conf.modalId + '_grid" class="table table-hover">'
+'                <thead>'
+'                  <tr>'
+'                    <th width="10" class="m-table-check">&nbsp;</th>'
+'                    <th>姓名</th>'
+'                  </tr>'
+'                </thead>'
+'                <tbody id="' + conf.modalId + '_body">'
/*    
+'                  <tr>'
+'                    <td><input id="' + conf.modalId + '_item_1" type="' + (conf.multiple ? 'checkbox' : 'radio') + '" name="selectedItem" class="selectedItem" value="1" title="admin" style="margin-top:0px;"></td>'
+'                    <td>admin</td>'
+'                  </tr>'
+'                  <tr>'
+'                    <td><input id="' + conf.modalId + '_item_2" type="' + (conf.multiple ? 'checkbox' : 'radio') + '" name="selectedItem" class="selectedItem" value="2" title="user" style="margin-top:0px;"></td>'
+'                    <td>user</td>'
+'                  </tr>'
*/    
+'                </tbody>'
+'              </table>'
+'            </div>'
+'          </div>'
+'        </div>'
+'          </div>'
+'          <div role="tabpanel" class="tab-pane" id="alias">'
+'    <div class="panel panel-default">'
+'      <div class="panel-heading">'
+'        用户'
+'      </div>'
+'      <div class="panel-content">'
+'  <table id="' + conf.modalId + '_aliasGrid" class="table table-hover">'
+'    <thead>'
+'      <tr>'
+'        <th width="10" class="m-table-check">&nbsp;</th>'
+'        <th>姓名</th>'
+'      </tr>'
+'    </thead>'
+'    <tbody id="' + conf.modalId + '_aliasBody">'
+'      <tr>'
+'        <td><input id="' + conf.modalId + '_item_alias_1" type="radio" name="selectedItem" class="selectedItem" value="常用语:流程发起人" title="常用语:流程发起人" style="margin-top:0px;"></td>'
+'        <td>常用语:流程发起人</td>'
+'      </tr>'
+'      <tr>'
+'        <td><input id="' + conf.modalId + '_item_alias_1" type="radio" name="selectedItem" class="selectedItem" value="常用语:直接上级" title="常用语:直接上级" style="margin-top:0px;"></td>'
+'        <td>常用语:直接上级</td>'
+'      </tr>'
+'      <tr>'
+'        <td><input id="' + conf.modalId + '_item_alias_2" type="radio" name="selectedItem" class="selectedItem" value="岗位:经理" title="岗位:经理" style="margin-top:0px;"></td>'
+'        <td>岗位:经理</td>'
+'      </tr>'
+'      <tr>'
+'        <td><input id="' + conf.modalId + '_item_alias_3" type="radio" name="selectedItem" class="selectedItem" value="岗位:总经理" title="岗位:总经理" style="margin-top:0px;"></td>'
+'        <td>岗位:总经理</td>'
+'      </tr>'
+'    </tbody>'
+'  </table>'
+'      </div>'
+'    </div>'
+'          </div>'
+'          <div role="tabpanel" class="tab-pane" id="expr">'
+'    <div class="panel panel-default">'
+'      <div class="panel-heading">'
+'        表达式'
+'      </div>'
+'      <div class="panel-content">'
+'  <table id="' + conf.modalId + '_exprGrid" class="table table-hover">'
+'    <thead>'
+'      <tr>'
+'        <th>姓名</th>'
+'      </tr>'
+'    </thead>'
+'    <tbody id="' + conf.modalId + '_exprBody">'
+'      <tr>'
+'        <td><input id="' + conf.modalId + '_item_expr_1" type="text" name="selectedItem" class="selectedItem form-control" value="${initiator}" title="${initator}" style="margin-top:0px;"></td>'
+'      </tr>'
+'    </tbody>'
+'  </table>'
+'      </div>'
+'    </div>'
+'          </div>'
+'        </div>'
+'      </div>'
+'      <div class="modal-footer">'
+'        <span id="' + conf.modalId + '_result" style="float:left;"></span>'
+'        <a id="' + conf.modalId + '_close" href="#" class="btn" data-dismiss="modal">关闭</a>'
+'        <a id="' + conf.modalId + '_select" href="#" class="btn btn-primary">选择</a>'
+'      </div>'
+'    </div>'
+'  </div>'
+'</div>');

		$('#myTabs a').click(function (e) {
		  e.preventDefault()
		  $(this).tab('show')
		});

    }

	var doSearch = function(username) {
        $.ajax({
            url: conf.searchUrl,
            data: {
                username: username
            },
            success: function(data) {
                var html = '';
                for (var i = 0; i < data.length; i++) {
                    var item = data[i];
                    html +=
                      '<tr>'
                        +'<td><input id="' + conf.modalId + '_item_' + i + '" type="' + (conf.multiple ? 'checkbox' : 'radio')
						+ '" class="selectedItem" name="name" value="'
                        + item.id + '" title="' + item.displayName + '"></td>'
                        +'<td><label for="' + conf.modalId + '_item_' + i + '">' + item.displayName + '</label></td>'
                      +'</tr>'
                }
                $('#' + conf.modalId + '_body').html(html);
            }
        });
	}

	var doSearchChild = function(parentId) {
        $.ajax({
            url: conf.childUrl,
            data: {
                parentId: parentId
            },
			dataType: "json",
            success: function(data) {
                var html = '';
                for (var i = 0; i < data.length; i++) {
                    var item = data[i];
                    html +=
                      '<tr>'
                        +'<td><input id="' + conf.modalId + '_item_' + i + '" type="' + (conf.multiple ? 'checkbox' : 'radio')
						+ '" class="selectedItem" name="name" value="'
                        + item.id + '" title="' + item.displayName + '"></td>'
                        +'<td><label for="' + conf.modalId + '_item_' + i + '">' + item.displayName + '</label></td>'
                      +'</tr>'
                }
                $('#' + conf.modalId + '_body').html(html);
            }
        });
	}

    $(document).delegate('.userPicker .input-group-addon', 'click', function(e) {

		var multiple = $(this).parent().data('multiple');
		if (multiple) {
			conf.multiple = true;
		}

		var setting = {
			async: {
				enable: true,
				url: conf.treeUrl
			},
			callback: {
				onClick: function(event, treeId, treeNode) {
					// console.info(treeNode.id);
					doSearchChild(treeNode.id);
				}
			}
		};

		var zNodes = [];

		try {
			$.fn.zTree.init($("#" + conf.modalId + "treeMenu"), setting, zNodes);
		} catch(e) {
			console.error(e);
		}

        $('#' + conf.modalId).data('userPicker', $(this).parent());
        $('#' + conf.modalId).modal();

		// doSearch('');
    });

    // $(document).delegate('#' + conf.modalId + '_body tr', 'click', function(e) {
	//	$('input[type=radio].selectedItem').prop('checked', false);
	//	$(this).find('.selectedItem').prop('checked', true);
    // });

    $(document).delegate('#' + conf.modalId + '_body .selectedItem', 'click', function(e) {
		if (conf.multiple) {
			var el = $(this);
			if (el.prop('checked')) {
				var html = '&nbsp;<span class="label label-default" id="' + $(this).val() + '" title="' + $(this).attr('title') + '">' + $(this).attr('title') + '<i class="glyphicon glyphicon-remove" style="cursor:pointer;"></i></span>';
				$('#' + conf.modalId + '_result').append(html);
			} else {
				$('#' + conf.modalId + '_result #' + el.val()).remove();
			}
		} else {
			var html = '<span class="label label-default" id="' + $(this).val() + '" title="' + $(this).attr('title') + '">' + $(this).attr('title') + '<i class="glyphicon glyphicon-remove" style="cursor:pointer;"></i></span>';
			$('#' + conf.modalId + '_result').html(html);
		}
	});

    $(document).delegate('#' + conf.modalId + '_aliasBody .selectedItem', 'click', function(e) {
		var html = '<span class="label" id="' + $(this).val() + '" title="' + $(this).attr('title') + '">' + $(this).attr('title') + '<i class="icon-minus-sign" style="cursor:pointer;"></i></span>';
		$('#' + conf.modalId + '_result').html(html);
	});

    $(document).delegate('#' + conf.modalId + '_exprBody .selectedItem', 'blur', function(e) {
		var html = '<span class="label" id="' + $(this).val() + '" title="' + $(this).val() + '">' + $(this).val() + '<i class="icon-minus-sign" style="cursor:pointer;"></i></span>';
		$('#' + conf.modalId + '_result').html(html);
	});

	$(document).delegate('.glyphicon-remove', 'click', function(e) {
		var id = $(this).parent().attr('id');
		$('#' + conf.modalId + '_item_' + id).prop('checked', false);
		$(this).parent().remove();
	});

	$(document).delegate('#' + conf.modalId + '_search', 'click', function(e) {
		doSearch($('#' + conf.modalId + '_username').val());
	});

	$(document).delegate('#' + conf.modalId + '_username', 'keypress', function(e) {
		if (e.which == 13) {
			doSearch($('#' + conf.modalId + '_username').val());
		}
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
