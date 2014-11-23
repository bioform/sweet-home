var Thread = Java.type("java.lang.Thread");

var d = home.device('620000000D2BEC29');

/*
 var memBanks = d.getMemoryBanks()
 var registers = memBanks[1];
 registers.write([0x04], 2);
 registers.write([0x04], 2);
 registers.write([0x04], 2);
 */
d.resetModeOn();

Thread.sleep(1000);

d.writeLatchState(0xE2);
d.writeLatchState(0xE2);

d.writeLatchState(0xE8);
d.writeLatchState(0xE0);


d.writeLatchState(0xEF);
d.writeLatchState(0xE0);


d.writeLatchState(0xE1);