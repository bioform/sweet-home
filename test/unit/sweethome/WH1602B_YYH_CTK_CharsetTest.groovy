package sweethome

import grails.util.BuildSettings
import sweethome.charsets.WH1602B_YYH_CTK_Charset

import java.nio.charset.Charset
import java.nio.charset.spi.CharsetProvider


class WH1602B_YYH_CTK_CharsetTest extends GroovyTestCase {
    void testEncodingRus(){
        // FIXME doesn't work yet
        //assertNotNull(Charset.forName("WH1602B-YYH-CTK"));
    }
}
