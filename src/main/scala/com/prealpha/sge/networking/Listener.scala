//package com.prealpha.sge.networking
//
//import scala.collection.mutable.ListBuffer
//import concurrent.{Await, Promise}
//import concurrent.duration.{Duration, MILLISECONDS}
//import collection.mutable
//
//class NoListener extends Listener[Unit]
//
//class Listener[M] {
//    type Consumer = M => Any
//
//    private[this] val listeners = new ListBuffer[Consumer]
//    private[this] val oneTime = new mutable.Queue[Consumer]
//    private[this] var waitingListeners = new ListBuffer[M=>Boolean]
//
//    def +=(c: Consumer) {
//        listeners += c
//    }
//
//    def -=(c: Consumer) {
//        listeners -= c
//    }
//
//    def handle(m: M) {
//        listeners.foreach(f => f(m))
//        oneTime.dequeueAll(_ => true).foreach(f => f(m))
//    }
//
//
//    def blockingWait(timeout: Duration): M = {
//        val message = Promise[M]()
//        var sleep = 0
//
//
//        val l = {
//            m: M =>
//                message.success(m)
//        }
//        oneTime.enqueue(l)
//
//        while (!message.isCompleted) {
//            if (sleep < 20) {
//                sleep += 1
//            }
//            Thread.sleep(sleep)
//        }
//        Await.result(message.future, timeout)
//    }
//    def blockingWait(timeout: Long): M = blockingWait(Duration(timeout, MILLISECONDS))
//    def blockingWait(): M = blockingWait(Duration.Inf)
//
//    def blockUntil(f: M => Boolean)(timeout: Duration): M = {
//        val m = blockingWait(timeout)
//        if (f(m)) m else (blockUntil(f)(timeout))
//
//    }
//    def blockUntil(f: M => Boolean)(timeout: Long): M = blockUntil(f)(Duration(timeout, MILLISECONDS))
//    def blockUntil(f: M => Boolean): M = blockUntil(f)(Duration.Inf)
//}
