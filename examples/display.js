load(resources('WH1602B-YYH-CTK.js'));
log.debug('Loaded');
var device = home.device('620000000D2BEC29');
var lcd = new LiquidCrystal(device);
//lcd.begin();
lcd.clear();
lcd.print('вася');
lcd.secondLine();
lcd.print('супер!');
log.debug('ok');
