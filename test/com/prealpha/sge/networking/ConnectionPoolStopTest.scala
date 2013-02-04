package com.prealpha.sge.networking

import server.{ToUserConnection, ConnectionPool}


object ConnectionPoolStopTest extends App{
    val cp = new ConnectionPool{
        def allowConnection(user: ToUserConnection): Boolean = true
    }
    cp.start()

    cp.stopAccepting()
}
