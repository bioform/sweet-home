package sweethome

class A {
    void abc()  {
        println "original"
    }
}

x = new A()

//Create a Method Closure or Method pointer
originalMethod = A.metaClass.getMetaMethod('abc', [] as Class[])

//Replace Original call with Method pointer
//x.abc()
x.abc()

//Meta classed
A.metaClass.abc={-> println "new (prev: ${originalMethod.invoke(delegate)})" }

//Call method pointer instead of original again
//x.abc()
x.abc()

A.metaClass.methods.findAll{it.name=="abc"}.each { println "Method $it"}
println "----------------"
new A().abc()