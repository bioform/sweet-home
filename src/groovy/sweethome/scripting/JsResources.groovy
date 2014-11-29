package sweethome.scripting

import jdk.nashorn.api.scripting.AbstractJSObject

class JsResources extends AbstractJSObject {
    @Override
    public Object call(Object thiz, Object... args) {
        if(args.length != 1){
            throw new IllegalArgumentException("You should provide only script name")
        }
        String path = args[0].toString().trim()
        path = path.startsWith("/") ? path : ("/resources/"+path)
        URL url = getClass().getResource(path)
        Objects.requireNonNull(url, "Cannot find the resource file \"$path\"")
        return url
    }
    // yes, I'm a function !
    @Override
    public boolean isFunction() {
        return true;
    }
}
