(function() {
    jQuery.showMessage = function(message, options){
		if (typeof(message) == 'undefined' || message == '') {
			return;
		}
        // defaults
        settings = jQuery.extend({
             id: 'sliding_message_box',
             position: 'bottom',
             size: '90',
             backgroundColor: 'rgb(143, 177, 240)',
             delay: 1500,
             speed: 500,
             fontSize: '30px'
        }, options);

        var elem = $('#' + settings.id);
        var delayed;

        // generate message div if it doesn't exist
        if(elem.length == 0){
            elem = $('<div></div>').attr('id', settings.id);

            elem.css({'z-index': '999',
                      'background-color': settings.backgroundColor,
                      'text-align': 'center',
                      'position': 'absolute',
                      'position': 'fixed',
                      'left': '0',
                      'width': '100%',
                      'line-height': settings.size + 'px',
					  'height': setting.size + 'px',
                      'font-size': settings.fontSize
                      });

            $('body').append(elem);
        }

        elem.html(message);

        if(settings.position == 'bottom'){
            elem.css('bottom', '-' + settings.size + 'px');
            elem.animate({bottom:'0'}, settings.speed);
            delayed = '$("#' + settings.id + '").animate({bottom:"-' + settings.size + 'px"}, ' + settings.speed + ');';
            setTimeout(delayed, settings.delay);
        }
        else if(settings.position == 'top'){
            elem.css('top', '-' + settings.size + 'px');
            elem.animate({top:'0'}, settings.speed);
            delayed = '$("#' + settings.id + '").animate({top:"-' + settings.size + 'px"}, ' + settings.speed + ');';
            setTimeout(delayed, settings.delay);
        }
    }
})(jQuery);