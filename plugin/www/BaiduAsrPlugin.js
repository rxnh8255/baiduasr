
var BaiduAsrPlugin = function() {};


BaiduAsrPlugin.prototype.begin = function(success, fail) {
	return cordova.exec(success, fail, "BaiduAsrPlugin", "begin", [{}]);
};
BaiduAsrPlugin.prototype.stop = function(success, fail) {
	return cordova.exec(success, fail, "BaiduAsrPlugin", "stop", [{}]);
};
BaiduAsrPlugin.prototype.registerNotify = function(success, fail) {
	return cordova.exec(success, fail, "BaiduAsrPlugin", "registerNotify", [{}]);
};
BaiduAsrPlugin.prototype.ttsPlay = function(message,utteranceId,success, fail) {
	return cordova.exec(success, fail, "BaiduAsrPlugin", "ttsPlay", [{text:message,utteranceId:utteranceId}]);
};
BaiduAsrPlugin.prototype.ttsStop = function(success, fail) {
	return cordova.exec(success, fail, "BaiduAsrPlugin", "ttsStop", [{}]);
};
window.baiduAsrPlugin = new BaiduAsrPlugin();