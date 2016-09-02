// Sample jQuery plugin pattern

// (function ($, window) {

// 	$.samplePlugin = function(elem) {
// 		var base 	= this;
// 		base.$elem 	= $(elem);

// 	$.fn.samplePlugin = function() {
// 		return this.each( function() {
// 			var samplePlugin = new $.samplePlugin(this);
// 		});
// 	};

// } (jQuery, window));