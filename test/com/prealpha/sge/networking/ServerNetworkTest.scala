package com.prealpha.sge.networking

import client.ToServerConnection
import server.{ToUserConnection, ConnectionPool}
import com.prealpha.sge.messages.Message


object ServerNetworkTest extends App {

    val server = new ConnectionPool(_=>true)

    server.start()
    val client = new ToServerConnection("localhost")

    client.start()
}
