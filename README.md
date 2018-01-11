#百度语音识别
只做了百度的语音识别.唤醒.语义分析等没有发布出来.内部在用

参考http://ai.baidu.com/tech/speech/asr 进行注册等,这是在线识别
## Installing the plugin

这三个参数都是百度管理中心里面能找到的
```
cordova plugin add cordova-plugin-baiduasr --variable APIKEY=your API_Key --variable BAPPID=baiduid --variable SECRETKEY=SECRET_KEY
```


## Using the plugin


0.开始录音识别
```javascript
window.baiduAsrPlugin.begin();
```

2.停止录音
```javascript
window.baiduAsrPlugin.stop();
```

3.使用registerNotify接收来通知的回调函数
```
//type说明
* asrBegin:     开始说话,根据这个来改变UI界面,这时候可以开始说话了.
* asrFinish:    说话结束
* asrText:      识别出来的文字,下面详细说明该参数
```

```javascript
var dataModel = JSON.parse(data.message); //从asrText获取到的message
if(dataModel.result_type =="final_result"){
    //最后的结果
}else if(dataModel.result_type =="partial_result") {
    //临时识别结果
}
```

```javascript
window.baiduAsrPlugin.registerNotify(function (res) {
    //res参数都带有一个type
    console.log(res);
},function(err){
    console.log(err);
});
```
