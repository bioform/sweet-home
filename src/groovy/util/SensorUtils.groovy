package util

class SensorUtils {
    static Object addCorrection(Object val, float coefficient, float correction){
        if(val == null) return null;

        if( val instanceof Byte ||
                val instanceof Integer ||
                val instanceof Short ||
                val instanceof Long ||
                val instanceof Float ||
                val instanceof Double){
            // fix value
            val = val*coefficient+correction
        }
        return val
    }
}
