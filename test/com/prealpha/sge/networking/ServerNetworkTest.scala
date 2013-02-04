package com.prealpha.sge.networking

import client.ToServerConnection
import server.{ToUserConnection, ConnectionPool}
import com.prealpha.sge.messages.Message


object ServerNetworkTest extends App {

    val server = new ConnectionPool {
        def allowConnection(user: ToUserConnection): Boolean = false
    }

    server.start()
    val client = new ToServerConnection("localhost"){
        def handleMessage(message: Message) {}

        def onClose() {}
    }
    client.start()
}
