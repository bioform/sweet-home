package sweethome.scripting

import jdk.nashorn.api.scripting.AbstractJSObject

class JsResources extends AbstractJSObject {
    @Override
    public Object call(Object thiz, Object... args) {
        if(args.length != 1){
            throw new IllegalArgumentException("You should provide only script name")
        }
        return getClass().getResource(args[1])
    }
    // yes, I'm a function !
    @Override
    public boolean isFunction() {
        return true;
    }
}
