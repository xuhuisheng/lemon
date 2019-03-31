var createDepartmentPicker = function(conf) {
	conf = conf ? conf : {};
	var defaults = {
		modalId: 'departmentPicker',
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
+'  <div class="modal-dialog" style="width:400px;">'
+'    <div class="modal-content">'
+'      <div class="modal-header">'
+'        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>'
+'        <h3>选择部门</h3>'
+'      </div>'
+'      <div class="modal-body" style="height:300px;overflow:auto;">'
+'	<div class="padd" style="padding-bottom: 0px;">'
+'        <div class="input-append row-fluid" style="margin-bottom: 0px;">'
+'			<input id="search_condition" type="text" placeholder="请输入搜索条件" class="span8" style="font-size:12px"/>'
+'			<button type="button" class="btn btn-info" onclick="search_ztree(\'' + conf.modalId + 'treeMenu\', \'search_condition\')">搜索</button>'
+'        </div>'
+'    </div>'
+'        <ul id="' + conf.modalId + 'treeMenu" class="ztree"></ul>'
+'      </div>'
+'      <div class="modal-footer">'
+'        <span id="' + conf.modalId + '_result" style="float:left;"></span>'
+'        <a id="' + conf.modalId + '_close" href="#" class="btn" data-dismiss="modal">关闭</a>'
+'        <a id="' + conf.modalId + '_select" href="#" class="btn btn-primary">选择</a>'
+'      </div>'
+'    </div>'
+'  </div>'
+'</div>');

    }

    $(document).delegate('.departmentPicker .input-group-addon', 'click', function(e) {
		$('#' + conf.modalId + '_result').html('');

		var multiple = $(this).parent().data('multiple');
		if (multiple) {
			conf.multiple = true;
		} else {
			conf.multiple = false;
		}

		var setting = {
			async: {
				enable: true,
				url: conf.treeUrl
			},
			callback: {
				onClick: function(event, treeId, treeNode) {
					// console.info(treeNode.id);
					// doSearchChild(treeNode.id);
					// console.info(treeNode);
					var id = treeNode.id;
					var name = treeNode.name;
					var html = '<span class="label label-default" id="' + id + '" title="' + name + '" style="margin-left:5px;">' + name
						+ '<i class="glyphicon glyphicon-remove" style="cursor:pointer;"></i></span>';
					if (conf.multiple === true) {
						var el = $('#' + conf.modalId + '_result');
						if ($('#' + id).length == 0) {
							el.html(el.html() + html);
						}
					} else {
						$('#' + conf.modalId + '_result').html(html);
					}
				}
			},
			view: {
				nameIsHTML: true,
				fontCss: setFontCss_ztree
			}
		};

		var zNodes = [];

		try {
			$.fn.zTree.init($("#" + conf.modalId + "treeMenu"), setting, zNodes);
		} catch(e) {
			console.error(e);
		}

        $('#' + conf.modalId).data('departmentPicker', $(this).parent());
        $('#' + conf.modalId).modal();

		// doSearch('');
    });

	$(document).delegate('.glyphicon-remove', 'click', function(e) {
		var id = $(this).parent().attr('id');
		$('#' + conf.modalId + '_item_' + id).prop('checked', false);
		$(this).parent().remove();
	});

    $(document).delegate('#' + conf.modalId + '_select', 'click', function(e) {
        $('#' + conf.modalId).modal('hide');
        var departmentPickerElement = $('#' + conf.modalId).data('departmentPicker');
		if (conf.multiple) {
			var el = $('#' + conf.modalId + '_result .label');
			var ids = [];
			var names = [];
			el.each(function(index, item) {
				ids.push($(item).attr('id'));
				names.push($(item).attr('title'));
			});

			departmentPickerElement.children('input[type=hidden]').val(ids.join(','));
			departmentPickerElement.children('span').text(names.join(','));
		} else {
			var el = $('#' + conf.modalId + '_result .label');
			departmentPickerElement.children('input[type=hidden]').val(el.attr('id'));
			departmentPickerElement.children('span').text(el.attr('title'));
		}
    });
}
