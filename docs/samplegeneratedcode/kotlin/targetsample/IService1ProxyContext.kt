package targetsample        
// Factory generated on Tue Jul 02 09:08:31 BST 2024
//   by com.typedpath.unittestof.processor.TestableRoots2Kotlin
//   for UnitTestOfKspSymbolProcessor    

import com.typedpath.unittestof.testutil.CallCentre
import com.typedpath.unittestof.testutil.CallCentre.Match
import com.typedpath.unittestof.testutil.CallCentre.MatchRow
@Suppress("unused")
class IService1ProxyContext(callCentre: CallCentre, id: String) : com.typedpath.unittestof.testutil.ServiceProxyContext<IService1>(targetsample.IService1::class, callCentre, id) {
    @Suppress("UNUSED_PARAMETER")
    fun whenEquals(other: Match<Any> ,  result: kotlin.Boolean) {
       callCentre.add(MatchRow(id, "equals_Any", listOf(other), result))
    }

    @Suppress("UNUSED_PARAMETER")
    fun whenHashCode(  result: kotlin.Int) {
       callCentre.add(MatchRow(id, "hashCode_", listOf(), result))
    }

    @Suppress("UNUSED_PARAMETER")
    fun whenSayHello(name: Match<String> ,  result: kotlin.String) {
       callCentre.add(MatchRow(id, "sayHello_String", listOf(name), result))
    }

    @Suppress("UNUSED_PARAMETER")
    fun whenSayHello(name: Match<String>, lastName: Match<String> ,  result: kotlin.String) {
       callCentre.add(MatchRow(id, "sayHello_String_String", listOf(name, lastName), result))
    }

    @Suppress("UNUSED_PARAMETER")
    fun whenToString(  result: kotlin.String) {
       callCentre.add(MatchRow(id, "toString_", listOf(), result))
    }

        @Suppress("UNUSED_PARAMETER")
    fun captureEquals(other: Match<Any> ) : List<Any> {
       return callCentre.getArgumentsByProxyIdMethodId(id, "equals_Any").map { it[0]   } 
    }     
        

        @Suppress("UNUSED_PARAMETER")
    fun captureHashCode( ) : List<Any> {
       return callCentre.getArgumentsByProxyIdMethodId(id, "hashCode_").map { listOf<Any>()  } 
    }     
        

        @Suppress("UNUSED_PARAMETER")
    fun captureSayHello(name: Match<String> ) : List<String> {
       return callCentre.getArgumentsByProxyIdMethodId(id, "sayHello_String").map { it[0]  as String  } 
    }     
        

    data class sayHello_String_StringParams( val name: String, val lastName: String )        
    @Suppress("UNUSED_PARAMETER")
    fun captureSayHello(name: Match<String>, lastName: Match<String> ) : List<sayHello_String_StringParams> {
       return callCentre.getArgumentsByProxyIdMethodId(id, "sayHello_String_String").map { sayHello_String_StringParams(it[0]  as String,it[1]  as String)  } 
    }
        

        @Suppress("UNUSED_PARAMETER")
    fun captureToString( ) : List<Any> {
       return callCentre.getArgumentsByProxyIdMethodId(id, "toString_").map { listOf<Any>()  } 
    }     
        

}
    