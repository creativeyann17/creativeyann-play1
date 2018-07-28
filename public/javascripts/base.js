// Write where it's called the current year
function getCurrentYear(){
	var d = new Date();
	document.write(d.getFullYear());
}

// active current page menu icon
function setPage(name){
	var icon=document.getElementById(name);
	icon.className += ' active';
}

function resizeIframe(iframe) {
  	iframe.style.height = iframe.contentWindow.document.body.scrollHeight + "px";
}  