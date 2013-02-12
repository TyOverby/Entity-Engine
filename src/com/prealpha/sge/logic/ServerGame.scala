package com.prealpha.sge.logic

import com.prealpha.sge.gamestate.ServerGameState
import com.prealpha.sge.networking.server.ToUserConnection
import com.prealpha.sge.messages.{UpdateMessage, Message}

abstract class ServerGame extends ServerGameState {
    override def fromUserUpdate(user: ToUserConnection, message: Message){
        message match{
            case x: UpdateMessage => actors.passMessage(x)
            case _ =>
        }
    }
}
