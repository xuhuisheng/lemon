
function initUserCard() {	
	var userPopOverTimer = null;
	var userCurrentPopOver = null;
	var userLatestPopOver =  null;

	$('[data-toggle="popover"]').hover(function() {
		userCurrentPopOver = $(this);
		if (userCurrentPopOver == userLatestPopOver) {
			return;
		}
		userPopOverTimer = setTimeout(function() {
			userCurrentPopOver.popover('show');
			if (userLatestPopOver != null) {
				userLatestPopOver.popover('hide');
			}
			userLatestPopOver = userCurrentPopOver;
			userPopOverTimer = null;
			$.get('rs/search/' + userCurrentPopOver.data('username'), {
			}, function(data) {
				var username = data[0].username;
				var displayName = data[0].displayName;

				var y = $('.popover.in').position().top;
				var h1 = $('.popover.in').height();

				$('.popover.in .popover-content').empty();

				//Fill in content with new AJAX data
				$('.popover.in .popover-content').html(
  '<div class="media">'
+   '<div class="media-left">'
+       '<img src="../avatar/api/' + username + '?width=64" style="width:64px;height:64px;margin-right:5px;" class="img-circle">'
+   '</div>'
+   '<div class="media-body">'
+     '<h3 class="media-heading">' + displayName + '</h3>'
+     username
+   '</div>'
+ '</div>'
+ '<div style="padding-top:10px;">'
+   '<div><i class="glyphicon glyphicon-earphone"></i> 15812345678</div>'
+   '<div><i class="glyphicon glyphicon-envelope"></i> ' + username + '@mossle.com</div>'
+   '<div><i class="glyphicon glyphicon-map-marker"></i> China</div>'
+ '</div>'
				);

				var h2 = $('.popover.in').height();

				$('.popover.in').css({
					top: (y + h1 / 2 - h2 / 2)
				});
			});
		}, 500);
	}, function() {
		if (userPopOverTimer != null) {
			clearTimeout(userPopOverTimer);
			userPopOverTimer = null;
		}
	});

	$(document).delegate('', 'click', function(event) {
		if ($(event.target).parents('.popover').length == 0) {
			$('[data-toggle="popover"]').popover('hide');
			userCurrentPopOver = null;
			userLatestPopOver = null;
		}
	});
}

