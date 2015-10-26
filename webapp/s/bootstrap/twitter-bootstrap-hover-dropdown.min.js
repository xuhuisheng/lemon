/*
 * Project: Twitter Bootstrap Hover Dropdown
 * Author: Cameron Spear
 * Contributors: Mattia Larentis
 *
 * Dependencies?: Twitter Bootstrap's Dropdown plugin
 *
 * A simple plugin to enable twitter bootstrap dropdowns to active on hover and provide a nice user experience.
 *
 * No license, do what you want. I'd love credit or a shoutout, though.
 *
 * http://cameronspear.com/blog/twitter-bootstrap-dropdown-on-hover-plugin/
 */(function(e,t,n){var r=e();e.fn.dropdownHover=function(n){r=r.add(this.parent());return this.each(function(){var n=e(this).parent(),i={delay:500,instantlyCloseOthers:!0},s={delay:e(this).data("delay"),instantlyCloseOthers:e(this).data("close-others")},o=e.extend(!0,{},i,o,s),u;n.hover(function(){o.instantlyCloseOthers===!0&&r.removeClass("open");t.clearTimeout(u);e(this).addClass("open")},function(){u=t.setTimeout(function(){n.removeClass("open")},o.delay)})})};e('[data-hover="dropdown"]').dropdownHover()})(jQuery,this);