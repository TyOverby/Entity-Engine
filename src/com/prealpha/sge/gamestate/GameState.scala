package com.prealpha.sge.gamestate

import com.prealpha.sge.data.ActorCollection
import com.prealpha.sge.networking.server.{ToUserConnection, ConnectionPool}
import com.prealpha.sge.networking.client.ToServerConnection
import com.prealpha.sge.messages._

/**
 * A GameState keeps track of an ActorCollection and all
 * the open connections
 */
trait GameState {
    val actors = new ActorCollection
}

trait ServerGameState extends GameState {
    def allowUser(user: ToUserConnection): Boolean

    val connectionPool = new ConnectionPool {
        def allowConnection(u: ToUserConnection) = allowUser(u)
    }
}

trait ClientGameState extends GameState {
    var serverConnection: Option[ToServerConnection] = None

    def connect(target: String){
        val ss = new ToServerConnection(target){
            override def handleMessage(message: Message) = message match {
                case GoodbyeMessage    => this.close()
                case m: UpdateMessage  => actors.passMessage(m)
                case CreateMessage(e)  => actors.add(e)
                case x                 => throw new IllegalArgumentException("Unknown message: " + x.toString)
            }

            //TODO: fill this out
            override def onClose() {}
        }
        serverConnection = Some(ss)
    }
}
