package com.prealpha.sge.networking.server

import com.prealpha.sge.networking.AbstractConnectionThread
import java.net.Socket
import com.prealpha.sge.messages.{GoodbyeMessage, Message}

class ToUserConnection(socket: Socket, pool: ConnectionPool) extends AbstractConnectionThread(socket) {
    messageListener += {
        case GoodbyeMessage => this.close()
        case x: Message     => pool.registerMessage(this, x)
    }

    closeListener += {() =>
        pool.removeUserConnection(this)
    }
}
