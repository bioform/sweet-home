package sweethome.scripting;

import groovy.lang.Closure;
import jdk.nashorn.api.scripting.AbstractJSObject;
import sweethome.Script;

public class JsScript extends AbstractJSObject {
    @Override
    public Object call(Object thiz, Object... args) {
        if(args.length != 1){
            throw new IllegalArgumentException("You should provide only script name")
        }
        Script script = Script.findByName(args[0])
        return [script: script.code, name: script.name]
    }
    // yes, I'm a function !
    @Override
    public boolean isFunction() {
        return true;
    }
}