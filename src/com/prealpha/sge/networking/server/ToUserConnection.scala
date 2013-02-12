package com.prealpha.sge.networking.server

import com.prealpha.sge.networking.AbstractConnectionThread
import java.net.Socket
import com.prealpha.sge.messages.{GoodbyeMessage, Message}

class ToUserConnection(socket: Socket, pool: ConnectionPool) extends AbstractConnectionThread(socket) {

    def handleMessage(message: Message) {
        message match {
            case GoodbyeMessage => this.close()
            case x: Message     => pool.registerMessage(this,x)
        }
    }

    def onClose(){
        pool.removeUserConnection(this)
    }
}
