package com.prealpha.sge.networking

import com.prealpha.sge.networking._

object EmmiterTest extends App{
    val emitter = new Publisher[String]

    emitter.observe{s => println(s)}

    emitter.publish("hello")
    emitter.publish("world")
}
