var $ = require("jquery");
var adb = require("adbkit");
var client = adb.createClient();

$(document).ready(function(){
	
	$("form").hide();

	setHeight = $(window).height();
	setWidth = $(window).width();
	$("#bpImage").css("height",setHeight.toString());
	$("#bpImage").css("width",setWidth.toString());

});

//	KEYEVENTS
$(document).keydown(function(e) {

	//	BLOOP ACTION
	if(e.ctrlKey && (e.which === 13)){
		console.log('bloop action');
		alert('bloop');
		// bloop();
	}

	//	toggle designer info box display
	//	CTRL + SHIFT
	if(e.ctrlKey && (e.which === 16)){
		if( $("form").is(":visible") ){
			console.log('hiding designer box');
			$("form").hide();
		}else{
			console.log('showing designer box');
			$("form").show();
		}
	}    
});

// function bloop(){
// 	client.trackDevices().then(function(tracker) {
// 		tracker.on('add', function(device) {
// 			console.log('Device %s was plugged in', device.id);
// 		});
// 	    tracker.on('remove', function(device) {
// 	    	console.log('Device %s was unplugged', device.id);
// 	    });
// 	    tracker.on('end', function() {
// 	    	console.log('Tracking stopped');
// 	    });
// 	}).catch(function(err) {
// 		console.error('Something went wrong:', err.stack);
// 	});
// }
