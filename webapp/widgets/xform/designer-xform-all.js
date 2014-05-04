/*
 * Compressed by JSA(www.xidea.org)
 */
/*
 * Compressed by JSA(www.xidea.org)
 */
/**
 * @fileOverview This file contains many core function to create a new core of project.
 * @author <a href="mailto:lingo.mossle@gmail.com">Lingo Mossle</a>
 * @version 0.1
 * @ignore
 */
function createCore(PROJECT_NAME) {
    var _INNER = {
        svgns: "http://www.w3.org/2000/svg",

        linkns: "http://www.w3.org/1999/xlink",

        vmlns: "urn:schemas-microsoft-com:vml",

        officens: "urn:schemas-microsoft-com:office:office",

		emptyFn: function() {},
        //emptyFn: function() {console.error(this, 'NotSupported');},

        emptyArray: [],

        emptyMap: {},

        devMode: true,

        installVml: function() {
            if (_INNER.isVml) {
                document.attachEvent("onreadystatechange", function () {
                    var doc = document;
                    if (doc.readyState == "complete") {
                        // 创建vml的命名空间
                        if (!doc.namespaces["v"]) {
                            doc.namespaces.add("v", _INNER.vmlns);
                        }
                        if (!doc.namespaces["o"]) {
                            doc.namespaces.add("o", _INNER.officens);
                        }
                    }
                });
                // 设置vml需要的默认css样式
                var css = document.createStyleSheet();
                css.cssText = "v\\:*{behavior:url(#default#VML)}" +
                    "o\\:*{behavior:url(#default#VML)}";
            }
        },

        seed: 0,

        id: function() {
            if (!PROJECT_NAME) {
                return "_INNER_" + this.seed++;
            } else {
                return "_" + PROJECT_NAME + "_" + this.seed++;
            }
        },

        onReady: function(fn) {
            window.onload = function () {
                fn();
            };
        },

        error: function(err, key) {
            if (_INNER.devMode !== true) {
                return;
            }

            if (_INNER.isVml) {
                var info = (key ? key : '') + '\n';
                for (var name in err) {
                    info += name + ':' + err[name] + '\n';
                }
                _INNER.debug(info);
            } else {
                console.info(key);
                console.error(err);
            }
        },

        debug: function() {
            if (!_INNER.debugDiv) {
                var debugDiv = document.createElement('div');
                debugDiv.style.position = 'absolute';
                debugDiv.style.left = '50px';
                debugDiv.style.top = '50px';
                document.body.appendChild(debugDiv);
                var textarea = document.createElement('textarea');
                textarea.rows = 10;
                textarea.rols = 40;
                debugDiv.appendChild(textarea);
                var closeBtn = document.createElement('button');
                closeBtn.innerHTML = 'close';
                closeBtn.onclick = function() {
                    debugDiv.style.display = 'none';
                };
                debugDiv.appendChild(closeBtn);
                _INNER.debugDiv = debugDiv;
                _INNER.debugTextArea = textarea;
            }
            var result = '';
            for (var i = 0; i < arguments.length; i++) {
                result += ',' + arguments[i];
            }
            _INNER.debugTextArea.value += '\n' + result;
            _INNER.debugDiv.style.display = '';
        },

        getInt: function(s) {
            s += '';
            s = s.replace(/px/, '');
            var i = parseInt(s, 10);
            return isNaN(i) ? 0 : i;
        },

        extend : function() {
            var io = function(o) {
                for(var m in o) {
                     this[m] = o[m];
                }
            };
            var oc = Object.prototype.constructor;
            return function(sb, sp, overrides) {
                if (typeof sp == 'object') {
                    overrides = sp;
                    sp = sb;
                    sb = overrides.constructor != oc
                        ? overrides.constructor
                        : function() {sp.apply(this, arguments);};
                }
                var F = function(){}, sbp, spp = sp.prototype;
                F.prototype = spp;
                sbp = sb.prototype = new F();
                sbp.constructor = sb;
                sb.superclass = spp;
                if (spp.constructor == oc) {
                    spp.constructor = sp;
                }
                sbp.override = io;
                _INNER.override(sb, overrides);
                return sb;
            };
        }(),

        override: function(origclass, overrides) {
            if (overrides) {
                var p = origclass.prototype;
                for(var method in overrides) {
                    p[method] = overrides[method];
                }
                if(_INNER.isIE && overrides.toString != origclass.toString){
                    p.toString = overrides.toString;
                }
            }
        },

        ns: function() {
            for (var i = 0; i < arguments.length; i++) {
                var v = arguments[i];
                var d = v.split(".");
                var o = window[d[0]] = window[d[0]] || {};
                var vv = d.slice(1);
                for (var j = 0; j < vv.length; j++) {
                    var v2 = vv[j];
                    o = o[v2] = o[v2] || {};
                }
            }
            return o;
        },

        apply: function(o, c, defaults) {
            if(defaults){
                _INNER.apply(o, defaults);
            }
            if(o && c && typeof c == 'object'){
                for(var p in c){
                    o[p] = c[p];
                }
            }
            return o;
        },

        applyIf: function(o, c) {
            if (o && c){
                for (var p in c) {
                    if(typeof o[p] == "undefined") {
                        o[p] = c[p];
                    }
                }
            }
            return o;
        },

        join: function(buff) {
            var str = '';
            for (var i = 0; i < buff.length; i++) {
                str += buff[i];
            }
            return str;
        },

        getTextSize: function(text) {
            if (!_INNER.textDiv) {
                _INNER.textDiv = document.createElement('div');
                _INNER.textDiv.style.position = 'absolute';
                //_INNER.textDiv.style.background = 'gray';
                _INNER.textDiv.style.fontFamily = 'Verdana';
                _INNER.textDiv.style.fontSize = '12px';
                _INNER.textDiv.style.left = '-1000px';
                _INNER.textDiv.style.top = '-1000px';
                //_INNER.textDiv.style.visibility = 'hide';
                document.body.appendChild(_INNER.textDiv);
            }
            var dom = _INNER.textDiv;
            dom.innerHTML = text;
            var textSize = {
                w: Math.max(dom.offsetWidth, dom.clientWidth),
                h: Math.max(dom.offsetHeight, dom.clientHeight)
            };
            //_INNER.textDiv.innerHTML = '';
            return textSize;
        },

        notBlank: function(text) {
            if (typeof text == 'undefined') {
                return false;
            } else if (typeof text == 'string' && text.trim().length == 0) {
                return false;
            }
            return true;
        },

        safe: function(value) {
            if (value) {
                return value.trim();
            } else {
                return '';
            }
        },

        get: function(id) {
            return document.getElementById(id);
        },

        value: function(id, value) {
            var el = _INNER.get(id);
            if (typeof value != 'undefined') {
                el.value = _INNER.safe(value);
            }
            return _INNER.safe(el.value);
        },

        each : function(array, fn, scope){
            if (typeof array.length == "undefined" || typeof array == "string") {
                array = [array];
            }
            for (var i = 0, len = array.length; i < len; i++){
                if(fn.call(scope || array[i], array[i], i, array) === false) {
                    return i;
                };
            }
        },

        showMessage: function(key, defaultMsg) {
            alert(defaultMsg);
        },

        isEmpty: function(value) {
            if (typeof value == 'undefined') {
                return true;
            }
            if (value == null) {
                return true;
            }
            if (typeof value.length != 'undefined' && value.length == 0) {
                return true;
            }
            return false;
        },

        notEmpty: function(value) {
            return !this.isEmpty(value);
        },

		getWindow: function() {
			if (!this.currentWindow) {
				this.currentWindow = window;
			}
			return this.currentWindow;
		},

		getDocument: function() {
			return this.getWindow().document;
		},

		getHtml: function() {
			return this.getDocument().getElementsByTagName('html')[0];
		},

		getBody: function() {
			return this.getDocument().body;
		}
    };

    // 判断浏览器类型
    (function(){
        var ua = navigator.userAgent.toLowerCase();
        var isOpera = ua.indexOf("opera") > -1;
        var isSafari = (/webkit|khtml/).test(ua);
        var isIE = !isOpera && ua.indexOf("msie") > -1;
        var isIE7 = !isOpera && ua.indexOf("msie 7") > -1;
        var isIE8 = !isOpera && ua.indexOf("msie 8") > -1;
        var isGecko = !isSafari && ua.indexOf("gecko") > -1;

        var isVml = isIE || isIE7 || isIE8;
        var isSvg = !isVml;

        _INNER.isSafari = isSafari;
        _INNER.isIE = isIE;
        _INNER.isIE7 = isIE7;
        _INNER.isGecko = isGecko;
        _INNER.isVml = isVml;
        _INNER.isSvg = isSvg;

        if (isVml) {
            _INNER.installVml();
        }
        _INNER.applyIf(Array.prototype, {
            indexOf : function(o) {
               for (var i = 0, len = this.length; i < len; i++){
                  if(this[i] === o) return i;
               }
               return -1;
            },

            remove : function(o) {
               var index = this.indexOf(o);
               if (index != -1) {
                   this.splice(index, 1);
               }
               return this;
            }
        });
        String.prototype.trim = function() {
            var re = /^\s+|\s+$/g;
            return function() {
                return this.replace(re, "");
            };
        }();
    })();

    return _INNER;
};

;

Xf = createCore('Xf');

Xf.sed = 0;

Xf.id = function() {
    return '_xf_' + (++Xf.sed);
};

;

Xf.XForm = function() {
    this.model = new Xf.Model();
	this.listeners = [];
};

Xf.XForm.prototype = {
    init: function() {
        this.initEvent();
    },

    initEvent: function() {
        var self = this;

        document.onmousedown = function(e) {
            var request = self.createRequest(e, 'DRAG');
            return self.mouseDown(request);
        };
        document.onmousemove = function(e) {
            var request = self.createRequest(e, 'MOVE');
            return self.mouseMove(request);
        };
        document.onmouseup = function(e) {
            var request = self.createRequest(e, 'DROP');
            return self.mouseUp(request);
        };
        document.ondblclick = function(e) {
            var request = self.createRequest(e, 'DOUBLECLICK');
            return self.doubleClick(request);
        }
        //document.oncontextmenu = function(e) {
        //    var request = self.createRequest(e, 'CONTEXTMENU');
        //    return self.contextMenu(request);
        //}
    },

    createRequest: function(e, type) {
        var ev = window.event ? window.event : e;
        var x = ev.clientX;
        var y = ev.clientY;
        var target = ev.srcElement ? ev.srcElement : ev.target;

		var xtype = target.className;
		if (xtype.indexOf('xf-') == 0) {
			xtype = xtype.substring(3);
		}

        var request = {
            e: ev,
            type: type,
            xtype: xtype,
            x: x,
            y: y,
            target: target
        };
        return request;
    },

    mouseDown: function(request) {
		if (request.target.id == 'xf-layer-mask') {
			var field = this.model.template.selectField(request);
			for (var i = 0; i < this.listeners.length; i++) {
				var listener = this.listeners[i];
				listener.onSelect(field);
			}

			if (field != null) {
				this.status = 'EXCHANGE';
				this.source = this.model.template.selectedCell;

				document.getElementById('xf-layer-mask').style.cursor = 'move';
			}
			return false;
		}

        switch (request.xtype) {
            case 'oneColumn':
            case 'twoColumn':
            case 'threeColumn':
                var templateType = request.xtype;
                this.model.changeTemplate(templateType);
				break;
            case 'textfield':
            case 'password':
            case 'select':
            case 'radio':
            case 'checkbox':
            case 'textarea':
            case 'fileupload':
            case 'userPicker':
                this.drag(request);
                return false;
			return true;
        }
    },

    drag: function(request) {
        this.status = request.xtype;
        document.getElementById('xf-layer-mask').style.cursor = 'move';

        request.target.style.backgroundColor = '#CCCCCC';

        setTimeout(function() {
            request.target.style.backgroundColor = '';
        }, 300);
    },

    mouseMove: function(request) {
        if (!this.status) {
            return true;
        }

        switch (this.status) {
            case 'textfield':
            case 'password':
            case 'select':
            case 'radio':
            case 'checkbox':
            case 'textarea':
            case 'fileupload':
            case 'userPicker':
                this.move(request);
                return false;
			case 'EXCHANGE':
				this.move(request);
				return false;
        }
		return true;
    },

    move: function(request) {
        if (!this.status) {
            return;
        }

        if (this.model.template) {
            this.model.template.processRequest(request);
        }
    },

    mouseUp: function(request) {
        switch (this.status) {
            case 'textfield':
            case 'password':
            case 'select':
            case 'radio':
            case 'checkbox':
            case 'textarea':
            case 'fileupload':
            case 'userPicker':
                this.drop(request);
				return false;
			case 'EXCHANGE':
				this.drop(request);
				return false;
        }
		return true;
    },

    drop: function(request) {
        if (!this.status) {
            return;
        }

        request.xtype = this.status;
		request.source = this.source;
        if (this.model.template) {
            this.model.template.processRequest(request);
        }

        this.status = null;
        this.source = null;
        document.getElementById('xf-layer-mask').style.cursor = '';
    },

    doubleClick: function(request) {
        this.model.removeField(request);
		return true;
    },

    contextMenu: function(request) {
        request.e.preventDefault();

        var str = this.model.serial();
        alert(str);
		return true;
    },

	registerListener: function(listener) {
		this.listeners.push(listener);
	},

	setValue: function(data) {
		for (var key in data) {
			var value = data[key];
			var field = this.model.findFieldByName(key);
			if (field != null) {
				field.setValue(value);
			}
		}
	}
};

;

Xf.Model = function() {
    this.template = null;
    this.initProperty();

    this.templateFactory = new Xf.template.TemplateFactory();
    this.fieldFactory = new Xf.field.FieldFactory();
}

Xf.Model.prototype = {
    initProperty: function() {
        this.title = '表单';
		this.fields = {};
        this.buttons = ['保存', '提交'];
    },

    changeTemplate: function(type) {
        if (!this.template || this.template.type != type) {
            this.template = this.templateFactory.createTemplate(type);

			this.template.title = this.title;
			this.template.buttons = this.buttons;

            this.template.init();
            this.template.model = this;

            this.initProperty();
        }
    },

	getField: function(key) {
		return this.fields[key];
	},

	findFieldByName: function(name) {
		for (var key in this.fields) {
			var field = this.fields[key];
			if (field.name == name) {
				return field;
			}
		}
		return null;
	},

    createField: function(labelEl, fieldEl, fieldType, index, row, col) {
        var field = this.fieldFactory.createField(fieldType);
        field.row = row;
        field.col = col;

        if (field) {
            field.render(labelEl, fieldEl, index);
            var key = row + '-' + col;
            this.fields[key] = field;
        }
    },

    removeField: function(request) {
        var position = this.template.removeField(request);
        if (position) {
            var key = position.row + '-' + position.col;
            if (this.fields[key]) {
                delete this.fields[key];
            }
        }
    },

	updateTitle: function(title) {
		this.title = title;
		this.template.updateTitle(title);
	},

    serial: function() {
        //return this.template.serial();
        var json = '{';
        json += '"title":"' + this.title + '",';
        json += '"template":"' + this.template.type + '",';
        json += '"fields":[';

        var array = [];
        for (var key in this.fields) {
            array.push(this.fields[key]);
        }

        for (var i = 0; i < array.length; i++) {
            var field = array[i];

            json += '{';
            json += '"type":"' + field.type + '",';
            json += '"id":"' + field.id + '",';
            json += '"name":"' + field.name + '",';
            json += '"label":"' + field.label + '",';
            json += '"data":"' + field.data + '",';
            json += '"readOnly":' + (!!field.readOnly) + ',';
            json += '"row":' + field.row + ',';
            json += '"col":' + field.col;
            json += '}';
            if (i != array.length - 1) {
                json += ',';
            }
        }

        json += '],';
        json += '"buttons":[';

        for (var i = 0; i < this.buttons.length; i++) {
            json += '"' + this.buttons[i] + '"';
            if (i != this.buttons.length - 1) {
                json += ',';
            }
        }
        json += ']';
        json += '}';
        return json;
    },

	deserial: function(data) {
		if (!data) {
			return;
		}
		this.changeTemplate(data.template);
		this.template.init();
		this.template.deserial(data);
		this.updateTitle(data.title);
	}
};

;

Xf.ns('Xf.field');

Xf.field.Field = function() {
    this.id = Xf.id();
	this.data = '';
};

Xf.field.Field.prototype = {
	updateLabel: function(label) {
		this.label = label;
		var labelId = 'xf-form-table-body-row' + this.row + '' + (this.col * 2);
		document.getElementById(labelId).innerHTML = '<label for=' + this.id + '>' + label + '</label>';
	}
};

;

Xf.ns('Xf.field');

Xf.field.FieldFactory = function() {
};

Xf.field.FieldFactory.prototype = {
    createField: function(type) {
        switch (type) {
            case 'textfield':
                return new Xf.field.TextField();
            case 'password':
                return new Xf.field.PasswordField();
            case 'select':
                return new Xf.field.SelectField();
            case 'radio':
                return new Xf.field.RadioField();
            case 'checkbox':
                return new Xf.field.CheckboxField();
            case 'textarea':
                return new Xf.field.TextArea();
            case 'fileupload':
                return new Xf.field.FileUploadField();
            case 'userPicker':
                return new Xf.field.UserPicker();
        }
    }
};

;

Xf.ns('Xf.field');

Xf.field.TextField = Xf.extend(Xf.field.Field, {
    type: 'textfield',

    render: function(labelEl, fieldEl, index) {
		if (!this.name) {
	        this.name = 'textfield' + index;
		}
		if (!this.label) {
	        this.label = '文本框' + index;
		}

        labelEl.innerHTML = '<label for="' + this.id + '">' + this.label + '</label>';
		if (this.readOnly) {
	        fieldEl.innerHTML = '<span id="' + this.id + '"></span>';
		} else {
	        fieldEl.innerHTML = '<input id="' + this.id + '" type="text" name="' + this.name + '">';
		}
    },

	setValue: function(value) {
		if (this.readOnly) {
			document.getElementById(this.id).innerHTML = value;
		} else {
			document.getElementById(this.id).value = value;
		}
	}
});

;

Xf.ns('Xf.field');

Xf.field.PasswordField = Xf.extend(Xf.field.Field, {
    type: 'password',

    render: function(labelEl, fieldEl, index) {
		if (!this.name) {
	        this.name = 'password' + index;
		}
		if (!this.label) {
	        this.label = '密码域' + index;
		}

        labelEl.innerHTML = '<label for="' + this.id + '">' + this.label + '</label>';
		if (this.readOnly) {
	        fieldEl.innerHTML = '<span id="' + this.id + '"></span>';
		} else {
	        fieldEl.innerHTML = '<input id="' + this.id + '" type="password" name="' + this.name + '">';
		}
    },

	setValue: function(value) {
		if (this.readOnly) {
			document.getElementById(this.id).innerHTML = "****";
		} else {
			document.getElementById(this.id).value = value;
		}
	}
});

;

Xf.ns('Xf.field');

Xf.field.SelectField = Xf.extend(Xf.field.Field, {
    type: 'select',

    render: function(labelEl, fieldEl, index) {
		if (!this.name) {
	        this.name = 'select' + index;
		}
		if (!this.label) {
	        this.label = '选择框' + index;
		}

        labelEl.innerHTML = '<label for="' + this.id + '">' + this.label + '</label>';
        fieldEl.innerHTML = '<select id="' + this.id + '" name="' + this.name + '"></select>';

		this.updateData(this.data);
    },

	updateData: function(data) {
		this.data = data;

		var html = null;
		if (this.readOnly) {
			html = '<span id="' + this.id + '"></span>';
		} else {
			html = '<select id="' + this.id + '" name="' + this.name + '">';
			html += '<option value="">' + "</option>";
			var array = data.split(',');
			for (var i = 0; i < array.length; i++) {
				var item = array[i];
				html += '<option value="' + item + '">' + item + "</option>";
			}
			html += '</select>';
		}

		var fieldId = 'xf-form-table-body-row' + this.row + '' + (this.col * 2 + 1);
		var fieldEl = document.getElementById(fieldId);
		fieldEl.innerHTML = html;
	},

	setValue: function(value) {
		if (this.readOnly) {
			document.getElementById(this.id).innerHTML = value;
		} else {
			var el = document.getElementById(this.id);
			for (var i = 0; i < el.options.length; i++) {
				var option = el.options[i];
				if (option.value == value) {
					el.selectedIndex = i;
					return;
				}
			}
		}
	}
});

;

Xf.ns('Xf.field');

Xf.field.CheckboxField = Xf.extend(Xf.field.Field, {
    type: 'checkbox',

    render: function(labelEl, fieldEl, index) {
		if (!this.name) {
	        this.name = 'checkbox' + index;
		}
		if (!this.label) {
	        this.label = '多选框' + index;
		}

        labelEl.innerHTML = '<label for="' + this.id + '">' + this.label + '</label>';
        fieldEl.innerHTML = '<input id="'
            + this.id + '" type="checkbox" name="' + this.name + '" value="' + this.name + '">';

		this.updateData(this.data);
    },

	updateData: function(data) {
		this.data = data;

        var html = '';
		if (this.readOnly) {
			html += '<span id="' + this.id + '"></span>';
		} else {
			var array = data.split(',');
			for (var i = 0; i < array.length; i++) {
				var item = array[i];
				var itemId = this.id + '' + i;
				html += '<input id="' + itemId + '" type="checkbox" name="' + this.name + '" value="' + item + '">';
				html += '<label for="' + itemId + '" style="display:inline">' + item + '</label>';
			}
		}

		var fieldId = 'xf-form-table-body-row' + this.row + '' + (this.col * 2 + 1);
		var fieldEl = document.getElementById(fieldId);
		fieldEl.innerHTML = html;
	},

	setValue: function(value) {
		if (this.readOnly) {
			document.getElementById(this.id).innerHTML = value;
		} else {
			var values = value.split(',');
			var els = document.getElementsByName(this.name);
			for (var i = 0; i < els.length; i++) {
				var el = els[i];
				for (var v in values) {
					if (el.value == v) {
						el.checked = true;
					}
				}
			}
		}
	}
});

;

Xf.ns('Xf.field');

Xf.field.RadioField = Xf.extend(Xf.field.Field, {
    type: 'radio',

    render: function(labelEl, fieldEl, index) {
		if (!this.name) {
	        this.name = 'radio' + index;
		}
		if (!this.label) {
	        this.label = '单选框' + index;
		}

        labelEl.innerHTML = '<label for="' + this.id + '">' + this.label + '</label>';
        fieldEl.innerHTML = '<input id="'
            + this.id + '" type="radio" name="' + this.name + '" value="' + this.name + '">';

		this.updateData(this.data);
    },

	updateData: function(data) {
		this.data = data;

		var html = '';
		if (this.readOnly) {
			html += '<span id="' + this.id + '"></span>';
		} else {
			var array = data.split(',');
			for (var i = 0; i < array.length; i++) {
				var item = array[i];
				var itemId = this.id + '' + i;
				html += '<input id="' + itemId + '" type="radio" name="' + this.name + '" value="' + item + '">';
				html += '<label for="' + itemId + '" style="display:inline">' + item + '</label>';
			}
		}

		var fieldId = 'xf-form-table-body-row' + this.row + '' + (this.col * 2 + 1);
		var fieldEl = document.getElementById(fieldId);
		fieldEl.innerHTML = html;
	},

	setValue: function(value) {
		if (this.readOnly) {
			document.getElementById(this.id).innerHTML = value;
		} else {
			var els = document.getElementsByName(this.name);
			for (var i = 0; i < els.length; i++) {
				var el = els[i];
				if (el.value == value) {
					el.checked = true;
					return;
				}
			}
		}
	}
});

;

Xf.ns('Xf.field');

Xf.field.TextArea = Xf.extend(Xf.field.Field, {
    type: 'textarea',

    render: function(labelEl, fieldEl, index) {
		if (!this.name) {
	        this.name = 'textarea' + index;
		}
		if (!this.label) {
	        this.label = '文本域' + index;
		}

        labelEl.innerHTML = '<label for="' + this.id + '">' + this.label + '</label>';
		if (this.readOnly) {
			fieldEl.innerHTML = '<span id="' + this.id + '"></span>';
		} else {
			fieldEl.innerHTML = '<textarea id="' + this.id + '" name="' + this.name + '"></textarea>';
		}
    },

	setValue: function(value) {
		if (this.readOnly) {
			document.getElementById(this.id).innerHTML = value;
		} else {
			document.getElementById(this.id).value = value;
		}
	}
});

;

Xf.ns('Xf.field');

Xf.field.FileUploadField = Xf.extend(Xf.field.Field, {
    type: 'fileupload',

    render: function(labelEl, fieldEl, index) {
		if (!this.name) {
	        this.name = 'fileupload' + index;
		}
		if (!this.label) {
	        this.label = '上传框' + index;
		}

        labelEl.innerHTML = '<label for="' + this.id + '">' + this.label + '</label>';
		if (this.readOnly) {
	        fieldEl.innerHTML = '<input id="' + this.id + '" type="file" name="' + this.name + '" readOnly>';
		} else {
	        fieldEl.innerHTML = '<input id="' + this.id + '" type="file" name="' + this.name + '">';
		}
    },

	setValue: function(value) {
	}
});

;

Xf.ns('Xf.field');

Xf.field.UserPicker = Xf.extend(Xf.field.Field, {
    type: 'userPicker',

    render: function(labelEl, fieldEl, index) {
		if (!this.name) {
	        this.name = 'userPicker' + index;
		}
		if (!this.label) {
	        this.label = '选择用户' + index;
		}

        labelEl.innerHTML = '<label for="' + this.id + '">' + this.label + '</label>';
		if (this.readOnly) {
	        fieldEl.innerHTML = '<span id="' + this.id + '"></span>';
		} else {
			fieldEl.innerHTML = '<input id="' + this.id + '" type="text" class="input-medium userPicker" name="' + this.name + '">'
				+ '<span class="add-on" style="padding:2px;">'
				+ '<i class="icon-user userPickerBtn" style="cursor:pointer;"></i>'
				+ '</a>'
				+ '</span>';
		}
    },

	setValue: function(value) {
		if (this.readOnly) {
			document.getElementById(this.id).innerHTML = value;
		} else {
			document.getElementById(this.id).value = value;
		}
	}
});

;

Xf.ns('Xf.template');

Xf.template.AbstractColumn = function() {
    this.title = '表单';
    this.buttons = [
        '保存',
        '提交'
    ];

    this.positions = [];

    this.selectedCell = null;
};

Xf.template.AbstractColumn.prototype = {
    init: function() {
		this.positions = [];

        this.initHead();
        this.initBody();
        this.initFoot();

        this.initTablePosition();
        this.initHeadPosition();
        this.initBodyPosition();
        this.initFootPosition();
    },

	updateTitle: function(title) {
		this.title = title;
        var el = document.getElementById('xf-form-table-head').firstChild.firstChild;
        el.innerHTML = this.title;
	},

    initHead: function() {
        var el = document.getElementById('xf-form-table-head').firstChild.firstChild;
        el.colSpan = this.columnSize * 2;
        el.innerHTML = this.title;
    },

    initBody: function() {
        var bodyEl = document.getElementById('xf-form-table-body');
        for (var i = bodyEl.childNodes.length - 1; i >= 0; i--) {
            var node = bodyEl.childNodes[i];
            bodyEl.removeChild(node);
        }

		this.appendLine(0);
    },

    initFoot: function() {
        var el = document.getElementById('xf-form-table-foot').firstChild.firstChild;
        el.colSpan = this.columnSize * 2;
        var html = '';
        for (var i = 0; i < this.buttons.length; i++) {
            var button = this.buttons[i];
            html += '<button id="button' + i + '" type="button" name="transition">'
                + button + '</button>&nbsp;';
        }
        el.innerHTML = html;
    },

    initTablePosition: function() {
        var el = document.getElementById('xf-form-table');
        this.tablePosition = this.initPosition(el);
    },

    initHeadPosition: function() {
        var el = document.getElementById('xf-form-table-head');
        this.headPosition = this.initPosition(el);
    },

    initBodyPosition: function() {
        var el = document.getElementById('xf-form-table-body');
        this.bodyPosition = this.initPosition(el);
    },

    initFootPosition: function() {
        var el = document.getElementById('xf-form-table-foot');
        this.footPosition = this.initPosition(el);
    },

    initPosition: function(el) {
        return {
            x: el.offsetLeft,
            y: el.offsetTop,
            w: el.offsetWidth,
            h: el.offsetHeight
        };
    },

    processRequest: function(request) {
        this.processMove(request);
        this.processDrop(request);
    },

    processMove: function(request) {
        if (request.type != 'MOVE') {
            return;
        }

		var field = this.getTargetField(request.x, request.y);
		if (field == null) {
			return;
		}

		if (this.selectedCell) {
			var labelEl = document.getElementById('xf-form-table-body-row' + this.selectedCell.row + '' + (this.selectedCell.col * 2));
			var fieldEl = document.getElementById('xf-form-table-body-row' + this.selectedCell.row + '' + (this.selectedCell.col * 2 + 1));
			labelEl.style.backgroundColor = '';
			fieldEl.style.backgroundColor = '';
		}

		this.selectedCell = field;
		var labelEl = document.getElementById('xf-form-table-body-row' + field.row + '' + (field.col * 2));
		var fieldEl = document.getElementById('xf-form-table-body-row' + field.row + '' + (field.col * 2 + 1));
		labelEl.style.backgroundColor = '#CCCCCC';
		fieldEl.style.backgroundColor = '#CCCCCC';
    },

    processDrop: function(request) {
        if (request.type != 'DROP') {
            return;
        }

        if (this.selectedCell) {
			var labelEl = document.getElementById('xf-form-table-body-row' + this.selectedCell.row + '' + (this.selectedCell.col * 2));
			var fieldEl = document.getElementById('xf-form-table-body-row' + this.selectedCell.row + '' + (this.selectedCell.col * 2 + 1));
			labelEl.style.backgroundColor = '';
			fieldEl.style.backgroundColor = '';
            this.selectedCell = null;
        }

		var field = this.getTargetField(request.x, request.y);
		if (field == null) {
			return;
		}
		var labelEl = document.getElementById('xf-form-table-body-row' + field.row + '' + (field.col * 2));
		var fieldEl = document.getElementById('xf-form-table-body-row' + field.row + '' + (field.col * 2 + 1));
		var index = field.row;
		var no = index * this.columnSize;

		// 如果存在source，就是通过拖拽调整顺序
		if (request.source) {
			if (request.source.row == field.row && request.source.col == field.col) {
				return;
			} else {
				var srcKey = request.source.row + '-' + request.source.col;
				var src = this.model.getField(srcKey);

				var destKey = field.row + '-' + field.col;
				var dest = this.model.getField(destKey);

				if (dest == null) {
					var srcLabelEl = document.getElementById('xf-form-table-body-row' + request.source.row + '' + (request.source.col * 2));
					var srcFieldEl = document.getElementById('xf-form-table-body-row' + request.source.row + '' + (request.source.col * 2 + 1));
					srcLabelEl.innerHTML = '&nbsp;';
					srcFieldEl.innerHTML = '&nbsp;';

					src.row = field.row;
					src.col = field.col;
					delete this.model.fields[srcKey];
					this.model.fields[destKey] = src;
					src.render(
						document.getElementById('xf-form-table-body-row' + field.row + '' + (field.col * 2)),
						document.getElementById('xf-form-table-body-row' + field.row + '' + (field.col * 2 + 1)),
						src.row + 1
					);
				} else {
					var srcLabelEl = document.getElementById('xf-form-table-body-row' + src.row + '' + (src.col * 2));
					var srcFieldEl = document.getElementById('xf-form-table-body-row' + src.row + '' + (src.col * 2 + 1));
					var destLabelEl = document.getElementById('xf-form-table-body-row' + dest.row + '' + (dest.col * 2));
					var destFieldEl = document.getElementById('xf-form-table-body-row' + dest.row + '' + (dest.col * 2 + 1));
					src.row = field.row;
					src.col = field.col;
					dest.row = request.source.row;
					dest.col = request.source.col;
					this.model.fields[srcKey] = dest;
					this.model.fields[destKey] = src;
					src.render(
						destLabelEl,
						destFieldEl,
						src.row + 1
					);
					dest.render(
						srcLabelEl,
						srcFieldEl,
						dest.row + 1
					);
 				}
			}
		} else {
			// 如果不存在source，就通过拖拽添加控件
			this.model.createField(labelEl, fieldEl, request.xtype, no + 1, field.row, field.col);
		}

		if (index == this.positions.length - 1) {
			this.appendLine(index + 1);
		} else {
			this.refreshPositions();
		}
    },

    refreshPositions: function() {
        for (var i = 0; i < this.positions.length; i++) {
            var el = document.getElementById('xf-form-table-body-row' + i);
            this.positions[i] = this.initPosition(el);
        }
    },

    removeField: function(request) {
		var field = this.getTargetField(request.x, request.y);
		if (field == null) {
			return null;
		}
		var labelEl = document.getElementById('xf-form-table-body-row' + field.row + '' + (field.col * 2));
		var fieldEl = document.getElementById('xf-form-table-body-row' + field.row + '' + (field.col * 2 + 1));
		labelEl.innerHTML = '&nbsp;';
		fieldEl.innerHTML = '&nbsp;';
		this.refreshPositions();
		return field;
    },

	selectField: function(request) {
		if (this.selectedCell) {
			var labelEl = document.getElementById('xf-form-table-body-row' + this.selectedCell.row + '' + (this.selectedCell.col * 2));
			var fieldEl = document.getElementById('xf-form-table-body-row' + this.selectedCell.row + '' + (this.selectedCell.col * 2 + 1));
			labelEl.style.backgroundColor = '';
			fieldEl.style.backgroundColor = '';
			this.selectedCell = null;
		}

		var field = this.getTargetField(request.x, request.y);
		if (field == null) {
			return null;
		}
		var key = field.row + '-' + field.col;
		var fieldModel = this.model.getField(key);

		if (!fieldModel) {
			return null;
		}
		var labelEl = document.getElementById('xf-form-table-body-row' + field.row + '' + (field.col * 2));
		var fieldEl = document.getElementById('xf-form-table-body-row' + field.row + '' + (field.col * 2 + 1));
		labelEl.style.backgroundColor = '#CCCCCC';
		fieldEl.style.backgroundColor = '#CCCCCC';

		this.selectedCell = field;

		return fieldModel;
	},

    serial: function() {
        return document.getElementById('xf-layer-form').innerHTML;
    },

	deserial: function(data) {
		var fields = data.fields;
		var maxIndex = 0;
		for (var i = 0; i < fields.length; i++) {
			var field = fields[i];
			if (field.row > maxIndex) {
				maxIndex = field.row;
			}
		}
		for (var i = 1; i < maxIndex + 2; i++) {
			this.appendLine(i);
		}
		for (var i = 0; i < fields.length; i++) {
			var field = fields[i];
			var index = i;
			var labelEl = document.getElementById('xf-form-table-body-row' + field.row + '' + (field.col * 2));
			var fieldEl = document.getElementById('xf-form-table-body-row' + field.row + '' + (field.col * 2 + 1));
			this.model.createField(labelEl, fieldEl, field.type, index + 1, field.row, field.col);

			var key = field.row + '-' + field.col;
			var fieldElement = this.model.getField(key);
			fieldElement.label = field.label;
			fieldElement.name = field.name;
			fieldElement.readOnly = field.readOnly;
			if (field.data != null) {
				fieldElement.data = field.data;
			}
			fieldElement.render(labelEl, fieldEl, index + 1);
		}
		this.refreshPositions();
	}
};

;

Xf.ns('Xf.template');

Xf.template.OneColumn = Xf.extend(Xf.template.AbstractColumn, {
    type: 'oneColumn',
	columnSize: 1,

    appendLine: function(index) {
        var el = document.getElementById('xf-form-table-body');

        var tr = document.createElement('tr');
        tr.id = 'xf-form-table-body-row' + index;
        el.appendChild(tr);

        var labelCell = document.createElement('td');
        labelCell.id = 'xf-form-table-body-row' + index + '0';
        labelCell.width = '30%';
        labelCell.align = 'right';
        labelCell.innerHTML = 'Label' + (index + 1);
        tr.appendChild(labelCell);

        var fieldCell = document.createElement('td');
        fieldCell.id = 'xf-form-table-body-row' + index + '1';
        fieldCell.width = '70%';
        fieldCell.align = 'left';
        fieldCell.innerHTML = 'Field' + (index + 1);
        tr.appendChild(fieldCell);

        var el = document.getElementById('xf-form-table-body-row' + index);
        this.positions.push(this.initPosition(el));

        this.refreshPositions();
    },

	getTargetField: function(x, y) {
		for (var i = 0; i < this.positions.length; i++) {

			var x1 = this.tablePosition.x + this.positions[i].x;
			var y1 = this.tablePosition.y + this.positions[i].y;
			var x2 = x1 + this.positions[i].w;
			var y2 = y1 + this.positions[i].h;

			if (x > x1 && x < x2 && y > y1 && y < y2) {
				var index = i;

				//var labelEl = document.getElementById('xf-form-table-body-row' + field.row + '' + (field.col * 2));
				//var fieldEl = document.getElementById('xf-form-table-body-row' + field.row + '' + (field.col * 2 + 1));

				return {
					row: index,
					col: 0
				};
			}
		}
		return null;
	}
});

;

Xf.ns('Xf.template');

Xf.template.TwoColumn = Xf.extend(Xf.template.AbstractColumn, {
    type: 'twoColumn',
	columnSize: 2,

    appendLine: function(index) {
        var no = index * 2;

        var el = document.getElementById('xf-form-table-body');

        var tr = document.createElement('tr');
        tr.id = 'xf-form-table-body-row' + index;
        el.appendChild(tr);

        var td = null;

        td = document.createElement('td');
        td.id = 'xf-form-table-body-row' + index + '0';
        td.width = '15%';
        td.align = 'right';
        td.innerHTML = 'Label' + (no + 1);
        tr.appendChild(td);

        var td = document.createElement('td');
        td.id = 'xf-form-table-body-row' + index + '1';
        td.width = '35%';
        td.align = 'left';
        td.innerHTML = 'Field' + (no + 1);
        tr.appendChild(td);

        td = document.createElement('td');
        td.id = 'xf-form-table-body-row' + index + '2';
        td.width = '15%';
        td.align = 'right';
        td.innerHTML = 'Label' + (no + 2);
        tr.appendChild(td);

        var td = document.createElement('td');
        td.id = 'xf-form-table-body-row' + index + '3';
        td.width = '35%';
        td.align = 'left';
        td.innerHTML = 'Field' + (no + 2);
        tr.appendChild(td);

        var el = document.getElementById('xf-form-table-body-row' + index);
        this.positions.push(this.initPosition(el));

        this.refreshPositions();
    },

	getTargetField: function(x, y) {
		for (var i = 0; i < this.positions.length; i++) {

			var x1 = this.tablePosition.x + this.positions[i].x;
			var y1 = this.tablePosition.y + this.positions[i].y;
			var x2 = x1 + this.positions[i].w / 2;
			var y2 = y1 + this.positions[i].h;
            var x3 = x1 + this.positions[i].w;
			var index = i;

            if (x > x1 && x < x2 && y > y1 && y < y2) {
                //var labelEl = document.getElementById('xf-form-table-body-row' + index + '0');
                //var fieldEl = document.getElementById('xf-form-table-body-row' + index + '1');
                return {
                    row: index,
                    col: 0
                };
            } else if (x > x2 && x < x3 && y > y1 && y < y2) {
                //var labelEl = document.getElementById('xf-form-table-body-row' + index + '2');
                //var fieldEl = document.getElementById('xf-form-table-body-row' + index + '3');
                return {
                    row: index,
                    col: 1
                };
            }
		}
		return null;
	}
});

;

Xf.ns('Xf.template');

Xf.template.ThreeColumn = Xf.extend(Xf.template.AbstractColumn, {
    type: 'threeColumn',
	columnSize: 3,

    appendLine: function(index) {
        var no = index * 3;

        var el = document.getElementById('xf-form-table-body');

        var tr = document.createElement('tr');
        tr.id = 'xf-form-table-body-row' + index;
        el.appendChild(tr);

        var td = null;

        td = document.createElement('td');
        td.id = 'xf-form-table-body-row' + index + '0';
        td.width = '10%';
        td.align = 'right';
        td.innerHTML = 'Label' + (no + 1);
        tr.appendChild(td);

        var td = document.createElement('td');
        td.id = 'xf-form-table-body-row' + index + '1';
        td.width = '23%';
        td.align = 'left';
        td.innerHTML = 'Field' + (no + 1);
        tr.appendChild(td);

        td = document.createElement('td');
        td.id = 'xf-form-table-body-row' + index + '2';
        td.width = '10%';
        td.align = 'right';
        td.innerHTML = 'Label' + (no + 2);
        tr.appendChild(td);

        var td = document.createElement('td');
        td.id = 'xf-form-table-body-row' + index + '3';
        td.width = '23%';
        td.align = 'left';
        td.innerHTML = 'Field' + (no + 2);
        tr.appendChild(td);

        td = document.createElement('td');
        td.id = 'xf-form-table-body-row' + index + '4';
        td.width = '10%';
        td.align = 'right';
        td.innerHTML = 'Label' + (no + 3);
        tr.appendChild(td);

        var td = document.createElement('td');
        td.id = 'xf-form-table-body-row' + index + '5';
        td.width = '23%';
        td.align = 'left';
        td.innerHTML = 'Field' + (no + 3);
        tr.appendChild(td);

        var el = document.getElementById('xf-form-table-body-row' + index);
        this.positions.push(this.initPosition(el));

        this.refreshPositions();
    },

	getTargetField: function(x, y) {
		for (var i = 0; i < this.positions.length; i++) {

			var x1 = this.tablePosition.x + this.positions[i].x;
			var y1 = this.tablePosition.y + this.positions[i].y;
			var x2 = x1 + this.positions[i].w / 3;
			var y2 = y1 + this.positions[i].h;
            var x3 = x1 + this.positions[i].w * 2 / 3;
            var x4 = x1 + this.positions[i].w;
			var index = i;

            if (x > x1 && x < x2 && y > y1 && y < y2) {
                //var labelEl = document.getElementById('xf-form-table-body-row' + index + '0');
                //var fieldEl = document.getElementById('xf-form-table-body-row' + index + '1');
                return {
                    row: index,
                    col: 0
                };
            } else if (x > x2 && x < x3 && y > y1 && y < y2) {
                //var labelEl = document.getElementById('xf-form-table-body-row' + index + '2');
                //var fieldEl = document.getElementById('xf-form-table-body-row' + index + '3');
                return {
                    row: index,
                    col: 1
                };
            } else if (x > x3 && x < x4 && y > y1 && y < y2) {
                //var labelEl = document.getElementById('xf-form-table-body-row' + index + '2');
                //var fieldEl = document.getElementById('xf-form-table-body-row' + index + '3');
                return {
                    row: index,
                    col: 2
                };
            }
		}
		return null;
	}
});

;

Xf.ns('Xf.template');

Xf.template.TemplateFactory = function() {
};

Xf.template.TemplateFactory.prototype = {
    createTemplate: function(type) {
        switch (type) {
            case 'oneColumn':
                return new Xf.template.OneColumn();
            case 'twoColumn':
                return new Xf.template.TwoColumn();
            case 'threeColumn':
                return new Xf.template.ThreeColumn();
        }
    }
};

;

Xf.ns('App.form');

App.form.AbstractForm = Xf.extend(Object, {
    clearItem: function(p) {
		$('#__gef_property__').html('');
    }
});

;

Xf.ns('App.form');

App.form.XformForm = Xf.extend(App.form.AbstractForm, {
    decorate: function(model) {
        this.clearItem();
        this.render(model);
    },

	render: function(model) {
		$('#__gef_property__').html(
			"表单"
			+"<div class='modal-body'>"
				+"<label class='span1'>标题:</label>"
				+"<input id='f_xform_title' type='text' name='title' value='" + model.title + "' class='span2'>"
			+"</div>"
		);

		$('#f_xform_title').blur(function() {
			var newValue = this.value;
			// FIXME: use command
			model.updateTitle(newValue);
		});
	}
});
;

Xf.ns('App.form');

App.form.CheckboxForm = Xf.extend(App.form.AbstractForm, {
    decorate: function(model) {
        this.clearItem();
        this.render(model);
    },

	render: function(model) {
		$('#__gef_property__').html(
			"多选框"
			+"<div class='modal-body'>"
				+"<label class='span1' style='margin-left:2.5641%'>名称:</label>"
				+"<input id='f_xform_name' type='text' name='name' value='" + model.name + "' class='span2'>"
				+"<br>"
				+"<label class='span1'>标签:</label>"
				+"<input id='f_xform_label' type='text' name='label' value='" + model.label + "' class='span2'>"
				+"<br>"
				+"<label class='span1'>数据:</label>"
				+"<input id='f_xform_data' type='text' name='data' value='" + model.data + "' class='span2'>"
				+"<br>"
				+"<label class='span1'>只读:</label>"
				+"<input id='f_xform_readOnly' type='checkbox' name='readOnly' " + (!!model.readOnly ? 'checked' : '') + ">"
			+"</form>"
			+"</div>"
		);

		$('#f_xform_name').blur(function() {
			var newValue = this.value;
			// FIXME: use command
			model.name = newValue;
		});
		$('#f_xform_label').blur(function() {
			var newValue = this.value;
			// FIXME: use command
			model.updateLabel(newValue);
		});
		$('#f_xform_data').blur(function() {
			var newValue = this.value;
			// FIXME: use command
			model.updateData(newValue);
		});
		$('#f_xform_readOnly').blur(function() {
			// FIXME: use command
			model.readOnly = this.checked;
		});
	}
});
;

Xf.ns('App.form');

App.form.FileuploadForm = Xf.extend(App.form.AbstractForm, {
    decorate: function(model) {
        this.clearItem();
        this.render(model);
    },

	render: function(model) {
		$('#__gef_property__').html(
			"文件上传"
			+"<div class='modal-body'>"
				+"<label class='span1' style='margin-left:2.5641%'>名称:</label>"
				+"<input id='f_xform_name' type='text' name='name' value='" + model.name + "' class='span2'>"
				+"<br>"
				+"<label class='span1'>标签:</label>"
				+"<input id='f_xform_label' type='text' name='label' value='" + model.label + "' class='span2'>"
				+"<br>"
				+"<label class='span1'>只读:</label>"
				+"<input id='f_xform_readOnly' type='checkbox' name='readOnly' " + (!!model.readOnly ? 'checked' : '') + ">"
			+"</div>"
		);

		$('#f_xform_name').blur(function() {
			var newValue = this.value;
			// FIXME: use command
			model.name = newValue;
		});
		$('#f_xform_label').blur(function() {
			var newValue = this.value;
			// FIXME: use command
			model.updateLabel(newValue);
		});
		$('#f_xform_readOnly').blur(function() {
			// FIXME: use command
			model.readOnly = this.checked;
		});
	}
});
;

Xf.ns('App.form');

App.form.PasswordForm = Xf.extend(App.form.AbstractForm, {
    decorate: function(model) {
        this.clearItem();
        this.render(model);
    },

	render: function(model) {
		$('#__gef_property__').html(
			"密码框"
			+"<div class='modal-body'>"
				+"<label class='span1' style='margin-left:2.5641%'>名称:</label>"
				+"<input id='f_xform_name' type='text' name='name' value='" + model.name + "' class='span2'>"
				+"<br>"
				+"<label class='span1'>标签:</label>"
				+"<input id='f_xform_label' type='text' name='label' value='" + model.label + "' class='span2'>"
				+"<br>"
				+"<label class='span1'>只读:</label>"
				+"<input id='f_xform_readOnly' type='checkbox' name='readOnly' " + (!!model.readOnly ? 'checked' : '') + ">"
			+"</div>"
		);

		$('#f_xform_name').blur(function() {
			var newValue = this.value;
			// FIXME: use command
			model.name = newValue;
		});
		$('#f_xform_label').blur(function() {
			var newValue = this.value;
			// FIXME: use command
			model.updateLabel(newValue);
		});
		$('#f_xform_readOnly').blur(function() {
			// FIXME: use command
			model.readOnly = this.checked;
		});
	}
});
;

Xf.ns('App.form');

App.form.RadioForm = Xf.extend(App.form.AbstractForm, {
    decorate: function(model) {
        this.clearItem();
        this.render(model);
    },

	render: function(model) {
		$('#__gef_property__').html(
			"单选框"
			+"<div class='modal-body'>"
				+"<label class='span1' style='margin-left:2.5641%'>名称:</label>"
				+"<input id='f_xform_name' type='text' name='name' value='" + model.name + "' class='span2'>"
				+"<br>"
				+"<label class='span1'>标签:</label>"
				+"<input id='f_xform_label' type='text' name='label' value='" + model.label + "' class='span2'>"
				+"<br>"
				+"<label class='span1'>数据:</label>"
				+"<input id='f_xform_data' type='text' name='data' value='" + model.data + "' class='span2'>"
				+"<br>"
				+"<label class='span1'>只读:</label>"
				+"<input id='f_xform_readOnly' type='checkbox' name='readOnly' " + (!!model.readOnly ? 'checked' : '') + ">"
			+"</div>"
		);

		$('#f_xform_name').blur(function() {
			var newValue = this.value;
			// FIXME: use command
			model.name = newValue;
		});
		$('#f_xform_label').blur(function() {
			var newValue = this.value;
			// FIXME: use command
			model.updateLabel(newValue);
		});
		$('#f_xform_data').blur(function() {
			var newValue = this.value;
			// FIXME: use command
			model.updateData(newValue);
		});
		$('#f_xform_readOnly').blur(function() {
			// FIXME: use command
			model.readOnly = this.checked;
		});
	}
});
;

Xf.ns('App.form');

App.form.SelectForm = Xf.extend(App.form.AbstractForm, {
    decorate: function(model) {
        this.clearItem();
        this.render(model);
    },

	render: function(model) {
		$('#__gef_property__').html(
			"下拉框"
			+"<div class='modal-body'>"
				+"<label class='span1' style='margin-left:2.5641%'>名称:</label>"
				+"<input id='f_xform_name' type='text' name='name' value='" + model.name + "' class='span2'>"
				+"<br>"
				+"<label class='span1'>标签:</label>"
				+"<input id='f_xform_label' type='text' name='label' value='" + model.label + "' class='span2'>"
				+"<br>"
				+"<label class='span1'>数据:</label>"
				+"<input id='f_xform_data' type='text' name='data' value='" + model.data + "' class='span2'>"
				+"<br>"
				+"<label class='span1'>只读:</label>"
				+"<input id='f_xform_readOnly' type='checkbox' name='readOnly' " + (!!model.readOnly ? 'checked' : '') + ">"
			+"</div>"
		);

		$('#f_xform_name').blur(function() {
			var newValue = this.value;
			// FIXME: use command
			model.name = newValue;
		});
		$('#f_xform_label').blur(function() {
			var newValue = this.value;
			// FIXME: use command
			model.updateLabel(newValue);
		});
		$('#f_xform_data').blur(function() {
			var newValue = this.value;
			// FIXME: use command
			model.updateData(newValue);
		});
		$('#f_xform_readOnly').blur(function() {
			// FIXME: use command
			model.readOnly = this.checked;
		});
	}
});
;

Xf.ns('App.form');

App.form.TextareaForm = Xf.extend(App.form.AbstractForm, {
    decorate: function(model) {
        this.clearItem();
        this.render(model);
    },

	render: function(model) {
		$('#__gef_property__').html(
			"多行文本"
			+"<div class='modal-body'>"
				+"<label class='span1' style='margin-left:2.5641%'>名称:</label>"
				+"<input id='f_xform_name' type='text' name='name' value='" + model.name + "' class='span2'>"
				+"<br>"
				+"<label class='span1'>标签:</label>"
				+"<input id='f_xform_label' type='text' name='label' value='" + model.label + "' class='span2'>"
				+"<br>"
				+"<label class='span1'>只读:</label>"
				+"<input id='f_xform_readOnly' type='checkbox' name='readOnly' " + (!!model.readOnly ? 'checked' : '') + ">"
			+"</div>"
		);

		$('#f_xform_name').blur(function() {
			var newValue = this.value;
			// FIXME: use command
			model.name = newValue;
		});
		$('#f_xform_label').blur(function() {
			var newValue = this.value;
			// FIXME: use command
			model.updateLabel(newValue);
		});
		$('#f_xform_readOnly').blur(function() {
			// FIXME: use command
			model.readOnly = this.checked;
		});
	}
});
;

Xf.ns('App.form');

App.form.TextfieldForm = Xf.extend(App.form.AbstractForm, {
    decorate: function(model) {
        this.clearItem();
        this.render(model);
    },

	render: function(model) {
		$('#__gef_property__').html(
			"输入框"
			+"<div class='modal-body'>"
				+"<label class='span1' style='margin-left:2.5641%'>名称:</label>"
				+"<input id='f_xform_name' type='text' name='name' value='" + model.name + "' class='span2'>"
				+"<br>"
				+"<label class='span1'>标签:</label>"
				+"<input id='f_xform_label' type='text' name='label' value='" + model.label + "' class='span2'>"
				+"<br>"
				+"<label class='span1'>只读:</label>"
				+"<input id='f_xform_readOnly' type='checkbox' name='readOnly' " + (!!model.readOnly ? 'checked' : '') + ">"
			+"</div>"
		);

		$('#f_xform_name').blur(function() {
			var newValue = this.value;
			// FIXME: use command
			model.name = newValue;
		});
		$('#f_xform_label').blur(function() {
			var newValue = this.value;
			// FIXME: use command
			model.updateLabel(newValue);
		});
		$('#f_xform_readOnly').blur(function() {
			// FIXME: use command
			model.readOnly = this.checked;
		});
	}
});
;

Xf.ns('App.form');

App.form.UserPickerForm = Xf.extend(App.form.AbstractForm, {
    decorate: function(model) {
        this.clearItem();
        this.render(model);
    },

	render: function(model) {
		$('#__gef_property__').html(
			"选择人员"
			+"<div class='modal-body'>"
				+"<label class='span1' style='margin-left:2.5641%'>名称:</label>"
				+"<input id='f_xform_name' type='text' name='name' value='" + model.name + "' class='span2'>"
				+"<br>"
				+"<label class='span1'>标签:</label>"
				+"<input id='f_xform_label' type='text' name='label' value='" + model.label + "' class='span2'>"
				+"<br>"
				+"<label class='span1'>只读:</label>"
				+"<input id='f_xform_readOnly' type='checkbox' name='readOnly' " + (!!model.readOnly ? 'checked' : '') + ">"
			+"</div>"
		);

		$('#f_xform_name').blur(function() {
			var newValue = this.value;
			// FIXME: use command
			model.name = newValue;
		});
		$('#f_xform_label').blur(function() {
			var newValue = this.value;
			// FIXME: use command
			model.updateLabel(newValue);
		});
		$('#f_xform_readOnly').blur(function() {
			// FIXME: use command
			model.readOnly = this.checked;
		});
	}
});