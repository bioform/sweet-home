var Thread = Java.type("java.lang.Thread");

var d = home.device('620000000D2BEC29');
d.resetModeOn();

d.writeLatchState(0xE2);

Thread.sleep(1000);
log.debug(d.read());

d.writeLatchState(0xE2);

Thread.sleep(1000);
log.debug(d.read());

d.writeLatchState(0xE8);

Thread.sleep(1000);
log.debug(d.read());

d.writeLatchState(0xE0);

Thread.sleep(1000);
log.debug(d.read());

d.writeLatchState(0xEF);

Thread.sleep(1000);
log.debug(d.read());

d.writeLatchState(0xE0);

Thread.sleep(1000);
log.debug(d.read());

d.writeLatchState(0xE1);

Thread.sleep(1000);
log.debug(d.read());