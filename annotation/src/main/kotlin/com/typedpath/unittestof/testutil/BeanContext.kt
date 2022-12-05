package com.typedpath.unittestof.testutil

import java.util.function.Consumer

abstract class BeanContext<T>(val verifier:IVerifier = Verifier()) {
    val callLog = CallCentre()
    abstract val target: T

    @Suppress("unused")
    companion object {

        enum class OrderingOption { Ordered, OrderedExact, Unordered, UnorderedExact }

        // options are exact match, ordered, not ordered
        // Ordering ordering, verifyAll, verifySequence, verifyOrder
        /* TODO ordering - how the verification should be ordered
        inverse - when true, the verification will check that the behaviour specified did not happen
        atLeast - verifies that the behaviour happened at least atLeast times
        atMost - verifies that the behaviour happened at most atMost times
        exactly - verifies that the behaviour happened exactly exactly times. Use -1 to disable
        Ordering.enum()
            UNORDERED - Order is not important. Some calls just should happen
            ALL - Order is not important. Some calls just should happen
            ORDERED - Order is important, but not all calls are checked
            SEQUENCE - Order is important and all calls should be specified
         */
        fun <R, T : BeanContext<R>> verify(context: T,
                                           failAction: (List<String>)-> Unit = { result-> throw AssertionError(result.joinToString(System.lineSeparator())) },
                                           ordering: OrderingOption = OrderingOption.Unordered,
                                           block: T.() -> Unit): List<String> {
            val callCount = context.callLog.calls.size
            context.callLog.skipMatch = true
            context.run(block)
            val allCalls = context.callLog.calls
            val testCalls = allCalls.subList(0, callCount)
            val matchCalls = allCalls.subList(callCount, allCalls.size)
            //should be match unordered
            val result = when (ordering) {
                OrderingOption.UnorderedExact -> context.verifier.matchExactUnordered(testCalls, matchCalls)
                OrderingOption.OrderedExact -> context.verifier.matchExactOrdered(testCalls, matchCalls)
                OrderingOption.Ordered -> context.verifier.matchOrdered(testCalls, matchCalls)
                OrderingOption.Unordered -> context.verifier.matchUnordered(testCalls, matchCalls)
            }
            context.callLog.calls = testCalls
            if (result.isNotEmpty())
               failAction(result)
            return result

        }

        fun <R, T : BeanContext<R>> verifyUnordered(context: T,
                   failAction: (List<String>)-> Unit = { result-> throw AssertionError(result.joinToString(System.lineSeparator())) },
                   block: T.() -> Unit): List<String> = verify(context, failAction, OrderingOption.Unordered, block)

        fun <R, T : BeanContext<R>> verifyOrdered(context: T,
                                                    failAction: (List<String>)-> Unit = { result-> throw AssertionError(result.joinToString(System.lineSeparator())) },
                                                    block: T.() -> Unit): List<String> = verify(context, failAction, OrderingOption.Ordered, block)

        fun <R, T : BeanContext<R>> verifyUnorderedExact(context: T,
                                                  failAction: (List<String>)-> Unit = { result-> throw AssertionError(result.joinToString(System.lineSeparator())) },
                                                  block: T.() -> Unit): List<String> = verify(context, failAction, OrderingOption.UnorderedExact, block)

        fun <R, T : BeanContext<R>> verifyOrderedExact(context: T,
                                                         failAction: (List<String>)-> Unit = { result-> throw AssertionError(result.joinToString(System.lineSeparator())) },
                                                         block: T.() -> Unit): List<String> = verify(context, failAction, OrderingOption.OrderedExact, block)


        @JvmStatic
        fun <R, T : BeanContext<R>> verifyJ(contextJ: T, blockJ: Consumer<T>) =
            verify(context = contextJ, block = {blockJ.accept(contextJ)})

        @JvmStatic
        fun <R, T : BeanContext<R>> verifyUnorderedJ(contextJ: T, blockJ: Consumer<T>) =
            verify(context = contextJ, ordering = OrderingOption.Unordered, block = {blockJ.accept(contextJ)})

        @JvmStatic
        fun <R, T : BeanContext<R>> verifyOrderedJ(contextJ: T, blockJ: Consumer<T>) =
            verify(context = contextJ, ordering = OrderingOption.Unordered, block = {blockJ.accept(contextJ)})

        @JvmStatic
        fun <R, T : BeanContext<R>> verifyUnorderedExactJ(contextJ: T, blockJ: Consumer<T>) =
            verify(context = contextJ, ordering = OrderingOption.UnorderedExact, block = {blockJ.accept(contextJ)})

        @JvmStatic
        fun <R, T : BeanContext<R>> verifyOrderedExactJ(contextJ: T, blockJ: Consumer<T>) =
            verify(context = contextJ, ordering = OrderingOption.OrderedExact, block = {blockJ.accept(contextJ)})


    }

}

