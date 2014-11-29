/**
 This library allows an 1-Wire board to control LiquidCrystal displays (LCDs) based on
 the Hitachi HD44780 (or a compatible) chipset, which is found on most text-based LCDs.
 The library works with in either 4- or 8-bit mode
 (i.e. using 4 or 8 data lines in addition to the rs, enable, and, optionally, the rw control lines).
 */

var Thread = Java.type("java.lang.Thread");
var LiquidCrystalCharsetClass = Java.type("sweethome.charsets.WH1602B_YYH_CTK_Charset");
var LiquidCrystalCharset = new LiquidCrystalCharsetClass();

function LiquidCrystal(device){
    this.device = device;

    var that = this;
    var displayControl = 0x0F

    this.begin = function begin(){
        var d = that.device;

        d.resetModeOff();

        /*
         d.writeLatchState(0xE3);
         Thread.sleep(5);
         d.writeLatchState(0xE3);
         Thread.sleep(100);
         d.writeLatchState(0xE3);
         */

        // switch to 4-bit interface (8-bit interface). Low bytes are always 0
        d.writeLatchState(0xE2);

        // function Set: data width 4 bits, 2 lines,5x10 dots
        d.writeLatchState(0xE2);
        d.writeLatchState(0xE8);

        // display ON, cursor ON, blink cursor
        d.writeLatchState(0xE0);
        d.writeLatchState(0xE0 | displayControl);

        // clear display
        that.clear();
    };

    this.firstLine = function firstLine(){
        // 1000 0000
        that.device.writeLatchState(0xE8);
        that.device.writeLatchState(0xE0);
    };

    this.secondLine = function secondLine(){
        // 1100 0000
        that.device.writeLatchState(0xEC);
        that.device.writeLatchState(0xE0);
    };

    this.clear = function clear(){
        that.device.writeLatchState(0xE0);
        that.device.writeLatchState(0xE1);
    };

    this.home = function home(){
        that.device.writeLatchState(0xE0);
        that.device.writeLatchState(0xE2);
    };

    this.blink = function blink(){
        displayControl = displayControl | 0x01;

        that.device.writeLatchState(0xE0);
        that.device.writeLatchState(0xE0 | displayControl);
    };

    this.noBlink = function noBlink(){
        displayControl = displayControl & 0x0E;

        that.device.writeLatchState(0xE0);
        that.device.writeLatchState(0xE0 | displayControl);
    };

    this.cursor = function cursor(){
        displayControl = displayControl | 0x02;

        that.device.writeLatchState(0xE0);
        that.device.writeLatchState(0xE0 | displayControl);
    };

    this.noCursor = function noCursor(){
        displayControl = displayControl & 0x0D;

        that.device.writeLatchState(0xE0);
        that.device.writeLatchState(0xE0 | displayControl);
    };

    this.display = function display(){
        displayControl = displayControl | 0x04;

        that.device.writeLatchState(0xE0);
        that.device.writeLatchState(0xE0 | displayControl);
    };

    this.noDisplay = function noDisplay(){
        displayControl = displayControl & 0x0B;

        that.device.writeLatchState(0xE0);
        that.device.writeLatchState(0xE0 | displayControl);
    };

    this.print = function print(str){
        var bytes = str.getBytes(LiquidCrystalCharset);
        for(var i=0;i<bytes.length;i++){
            var b = bytes[i];
            that.device.writeLatchState( 0xF0 | ((b >> 4) & 0x0F) ); // Upper 4 bit
            that.device.writeLatchState( 0xF0 | (b & 0x0F) ); // Lower 4 bit
        }
    }
}
