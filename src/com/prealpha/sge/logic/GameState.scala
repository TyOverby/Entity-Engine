package com.prealpha.sge.logic

import com.prealpha.sge.data.ActorCollection
import com.prealpha.sge.networking.client.ToServerConnection
import java.util.concurrent.LinkedBlockingQueue
import com.prealpha.sge.messages.{UpdateMessage, Message}
import scala.collection.JavaConversions._
import com.prealpha.sge.networking.server.{ToUserConnection, ConnectionPool}

abstract class GameState {
    val actors = new ActorCollection
}

abstract class ServerState extends GameState{
    def acceptUser: ToUserConnection => Boolean
    lazy val cPool = new ConnectionPool(acceptUser)

    cPool.messageListener += {
        case (user, message) => cPool.broadcast(message)
    }


}

class ClientState(target: String) extends GameState{
    lazy val toServer = new ToServerConnection(target)

    private[this] val outgoingMessages = new LinkedBlockingQueue[Message]()
    private[this] val myMessages = new LinkedBlockingQueue[UpdateMessage]()
    private[this] val messageQueue = new LinkedBlockingQueue[UpdateMessage]()

    /**
     * Send all of the message that have accumulated over the last frame
     */

    def sendMessages() {
        outgoingMessages.foreach{
            m =>
                this.toServer.write(m)
                m match {
                    case m: UpdateMessage => messageQueue.put(m)
                }
        }
        outgoingMessages.clear()
        this.toServer.flush()
    }

    def applyMessages(curFrame: Time){
        def app(queue: LinkedBlockingQueue[UpdateMessage]){
            queue
                .filter(_.frame > curFrame)
                .foreach(m=>actors.passMessage(m))
        }
        app(myMessages)
        app(messageQueue)
    }
}
