/*
 * jQuery Text Children plugin
 * Examples and documentation at: http://plugins.learningjquery.com/textchildren/
 * Version: 0.1 (02/27/2008)
 * Dual licensed under the MIT and GPL licenses:
 * http://www.opensource.org/licenses/mit-license.php
 * http://www.gnu.org/licenses/gpl.html
 * Requires: jQuery v1.1.3.1 or later
 */

(function($) {
$.fn.textChildren = function(n, options) {
  if (typeof n == 'object') {
    options = n;
    n = 'all';
  }
  var opts = $.extend({}, $.fn.textChildren.defaults, options || {}); 

  var output = opts.outputType == 'string' ? '' : [];
  var contents = '';
  this.each(function(index) {
    var nodeIndex = n;
    var txtNodes = $.grep( this.childNodes, function(node) {
      return (node.nodeType == 3);
    });
  
    if (n == 'first') {
      nodeIndex = 0;
    } else if (n == 'last') {
      nodeIndex = txtNodes.length - 1;
    } else if ( n < 0 ) {
      nodeIndex = txtNodes.length + n;
    }
    if ( nodeIndex >= txtNodes.length || nodeIndex < 0 ) {
      return;
    } 
    if (n == undefined || n == 'all') {
      for (var i=0; i < txtNodes.length; i++) {
        contents = opts.trim ? $.trim(txtNodes[i].nodeValue) : txtNodes[i].nodeValue;
        if (opts.outputType == 'array') {
          ( output.length && contents == '') ? output : output.push(contents);
        } else {
          output += output == '' ? contents : opts.stringDelimiter + contents;
        }
      }
    } else {
      contents = opts.trim ? $.trim(txtNodes[nodeIndex].nodeValue) : txtNodes[nodeIndex].nodeValue;
      if (opts.outputType == 'array') {
        output.push(contents);
      } else {
        output += output == '' ? contents : opts.stringDelimiter + contents;
      }
    }
  });
  return output;
};


$.fn.textChildren.defaults = {
  outputType: 'string',       // one of 'string' or 'array'
  stringDelimiter: '',        // if outputting to string, inserts a delimiter between nodes
  trim: true                  // whether to trim white space from start/end of text nodes
};

})(jQuery);