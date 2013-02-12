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
    def fromUserUpdate(user: ToUserConnection, message: Message)

    val connectionPool = new ConnectionPool {
        def allowConnection(u: ToUserConnection) = allowUser(u)
        def onPlayerUpdate(user: ToUserConnection, message: Message) = fromUserUpdate(user, message)
    }
}

abstract class ClientGameState(serverTarget: String) extends GameState {
    lazy val serverConnection: ToServerConnection = connect(serverTarget)

    def handleMessage(m: Message)
    def onClose()

    def connect(target: String) = {
        val ss = new ToServerConnection(target){
            override def handleMessage(message: Message) {
                message match {
                    case GoodbyeMessage => this.close()
                    // pass it off to the inheriting method
                    case x => ClientGameState.this.handleMessage(x)
                }
            }

            override def onClose() {
                ClientGameState.this.onClose()
            }
        }
        ss.start()
        ss
    }
}
