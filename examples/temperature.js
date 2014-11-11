// Put your code here
var location = home['My Kitchen'];
var device = location['My Temperature Sensor'];
var t = device.read();
log.debug("--->", t);