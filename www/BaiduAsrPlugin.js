
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
window.baiduAsrPlugin = new BaiduAsrPlugin();