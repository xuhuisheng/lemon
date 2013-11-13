/**
 * JQuery Organisation Chart Plugin-in.
 *
 * Author: Mark Lee.
 *
 * Copyright (C)2010 Caprica Software Limited.
 * http://www.capricasoftware.co.uk
 *
 * This software is licensed under the Creative Commons Attribution-ShareAlike
 * 3.0 License.
 *
 * See here for license terms:
 *
 * http://creativecommons.org/licenses/by-sa/3.0
 */
(function($) {

  $.fn.orgChart = function(options, $appendTo) {
    var opts = $.extend({}, $.fn.orgChart.defaults, options);

    return this.each(function() {
      $this = $(this);
      var $container = $("<div class='" + opts.chartClass + "'/>");
      if($this.is("ul")) {
        buildNode($this.find("li:first"), $container, 0, opts);
      }
      else if($this.is("li")) {
        buildNode($this, $container, 0, opts);
      }
      $appendTo.append($container);
    });
  };

  $.fn.orgChart.defaults = {
    depth      : -1,
    stack      : false,
    chartClass : "orgChart",
    hoverClass : "hover",
    nodeText   : function($node) {return "";}
  };

  function buildNode($node, $appendTo, level, opts) {
    var $table = $("<table cellpadding='0' cellspacing='0' border='0'/>");
    var $tbody = $("<tbody/>");

    // Make this node...
    var $nodeRow = $("<tr/>").addClass("nodes");
    var $nodeCell = $("<td/>").addClass("node").attr("colspan", 2);
    var $childNodes = $node.children("ul:first").children("li");
    if($childNodes.length > 1) {
      $nodeCell.attr("colspan", $childNodes.length * 2);
    }
    var $heading = $("<h2>").text(opts.nodeText($node));
    $nodeDiv = $("<div>").addClass("node").append($heading);
    $nodeCell.append($nodeDiv);
    $nodeRow.append($nodeCell);
    $tbody.append($nodeRow);

    $nodeDiv.click(function() {
      var $this = $(this);
      var $row = $this.closest("tr");
      if($row.next("tr").is(":visible")) {
        $row.nextAll("tr").fadeOut("slow");
		$row.addClass('nodeCollapsed');
      }
      else {
        $row.nextAll("tr").fadeIn("slow");
		$row.removeClass('nodeCollapsed');
      }
    });

    $nodeDiv.hover(function() {$(this).addClass(opts.hoverClass);}, function() {$(this).removeClass(opts.hoverClass);});

    if($childNodes.length > 0) {
      if(opts.depth == -1 || (level+1 < opts.depth)) {
        var $downLineRow = $("<tr/>").addClass("lines");
        var $downLineCell = $("<td/>").attr("colspan", $childNodes.length*2);
        $downLineRow.append($downLineCell);

        var $downLineTable = $("<table cellpadding='0' cellspacing='0' border='0'>");
        $downLineTable.append("<tbody>");
        var $downLineLine = $("<tr/>").addClass("lines");
        var $downLeft = $("<td>").addClass("line left");
        var $downRight = $("<td>").addClass("line right");
        $downLineLine.append($downLeft).append($downRight);
        $downLineTable.children("tbody").append($downLineLine);
        $downLineCell.append($downLineTable);

        $tbody.append($downLineRow);

        // Recursively make child nodes...
        var $linesRow = $("<tr/>").addClass("lines");
        $childNodes.each(function() {
          var $left = $("<td/>").addClass("line left top");
          var $right = $("<td/>").addClass("line right top");
          $linesRow.append($left).append($right);
        });
        $linesRow.find("td:first").removeClass("top");
        $linesRow.find("td:last").removeClass("top");
        $tbody.append($linesRow);
        var $childNodesRow = $("<tr/>");
        $childNodes.each(function() {
           var $td = $("<td/>");
           $td.attr("colspan", 2);
           buildNode($(this), $td, level+1, opts);
           $childNodesRow.append($td);
        });
      }
      else if(opts.stack) {
        // TODO what to do about this?
        var $list = $("<ul>");
        $childNodes.each(function() {
          $item = $("<li>").text($(this).textChildren());
          $list.append($item);
        });
        $nodeDiv.after($list);
      }
      $tbody.append($childNodesRow);
    }

    $table.append($tbody);
    $appendTo.append($table);
  };

})(jQuery);
