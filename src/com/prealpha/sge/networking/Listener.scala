package com.prealpha.sge.networking

import scala.collection.mutable.ListBuffer

class NoListener{
    type Consumer = () => Any

    private[this] val listeners = new ListBuffer[Consumer]

    def += (c: Consumer) {
        listeners += c
    }
    def -= (c: Consumer) {
        listeners -= c
    }

    def handle(){
        listeners.foreach(f => f())
    }
}

class Listener[P] {
    type Consumer = P => Any


    private[this] val listeners = new ListBuffer[Consumer]

    def += (c: Consumer) {
        listeners += c
    }
    def -= (c: Consumer) {
        listeners -= c
    }

    def handle(m: P){
        listeners.foreach(f => f(m))
    }
}
