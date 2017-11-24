/**
Bootstrap input clear button
Dual licensed under the MIT or GPL Version 2 licenses.

Copyright (c) 2013 Ehsan Mahpour

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/

(function ($) {
    "use strict";
    function enableInputClearOption() {
	// add private event handler to avoid conflict
        $("input[type=text]").not(".no-clear").unbind("clear-focus").bind("clear-focus", (function () {
            if ($(this).data("clear-button")) return;
            var x = $("<a class='clear-text' style='cursor:pointer;color:#888;'><i class='icon-remove'></i></a>");
            $(x).data("text-box", this);
            $(x).mouseover(function () { $(this).addClass("over"); }).mouseleave(function () { $(this).removeClass("over"); });
            $(this).data("clear-button", x);
            $(x).css({ "position": "absolute", "left": ($(this).position().right), "top": $(this).position().top, "margin": "3px 0px 0px -20px" });
            $(this).after(x);
            //$(this));
        })).unbind("clear-blur").bind("clear-blur", (function (e) {
            var x = $(this).data("clear-button");
            if (x) {
                if ($(x).hasClass("over")) {
                    $(x).removeClass("over");
                    $(x).hide().remove();
                    $(this).val("");
                    $(this).removeData("clear-button");
                    var txt = this;
                    e.stopPropagation();
                    e.stopImmediatePropagation();
                    setTimeout($.proxy(function () { $(this).trigger("focus"); }, txt), 50);
                    return false;

                }
            }
            if (x && !$(x).hasClass("over")) {
                $(this).removeData("clear-button");
                $(x).remove();
            }
        }));
	// add private event to the focus/unfocus events as branches
        $("input[type=text]").on("focus", function () {
            $(this).trigger("clear-focus");
        }).on("blur", function () {
            $(this).trigger("clear-blur");
        });
    }
    window.enableInputClearOption = enableInputClearOption;
})(jQuery);