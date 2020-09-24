

function jsonCall(url, type, data, callback) {
    var cookie = readCookie("T-PLATFORM");

    if(cookie) {
        window.session = JSON.parse(cookie);
    }

	$.ajax({
        headers: {
            Authorization: "Basic " + btoa(window.session.name + ":" + window.session.token)
        },
        type: type,
        url: url,
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        data: data,
        timeout: 3000,
        error: function(xReq, status, error) {
        	if(typeof callback === 'function') {
				callback(false, error);
			}
        },
        success: function(json) {
        	if(typeof callback === 'function') {
				callback(true, json);
			}
        }
    });
}

function jsonpCall(url, type, data, callback) {
	$.ajax({
        type: type,
        url: url,
        contentType: "application/json; charset=utf-8",
        dataType: "jsonp",
		jsonpCallback: "callback",
		data: data,
        timeout: 3000,
        error: function(xReq, status, error) {
        	if(typeof callback === 'function') {
				callback('false', error);
			}
        },
        success: function(json) {
        	if(typeof callback === 'function') {
				callback('true', json);
			}
        }
    });
}

function objToString (obj) {
    var str = '';
    for (var p in obj) {
        if (obj.hasOwnProperty(p)) {
            str += p + '::' + obj[p] + '\n';
        }
    }
    return str;
}

function createCookie(name, value, days) {
    var expires = "";
    if (days) {
        var date = new Date();
        date.setTime(date.getTime() + (days*24*60*60*1000));
        expires = "; expires=" + date.toUTCString();
    }
    document.cookie = name + "=" + value + expires + "; path=/";
}

function readCookie(name) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for(var i=0;i < ca.length;i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1,c.length);
        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
    }
    return null;
}

function eraseCookie(name) {
    createCookie(name,"",-1);
}