package com.prealpha.sge.networking

import client.ToServerConnection
import server.{ToUserConnection, ConnectionPool}


object ConnectionPoolStopTest extends App{
    val cp = new ConnectionPool(_ => true)
    cp.start()
    val client = new ToServerConnection("localhost")
    cp.stopAccepting()
    val c2 = new ToServerConnection("localhost")
}
