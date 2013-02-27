package com.prealpha.sge.networking

import client.ToServerConnection
import server.ToUserConnection


trait Handshake {
    def serverHandshake(toUser: ToUserConnection): Boolean
    def clientHandshake(toServer: ToServerConnection): Any
}

object Handshake{
    class WrongMessageException extends RuntimeException
}


