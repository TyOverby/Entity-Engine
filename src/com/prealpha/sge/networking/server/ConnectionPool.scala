package com.prealpha.sge.networking.server

import collection.mutable.ListBuffer
import scala.concurrent.future
import scala.concurrent.ExecutionContext.Implicits.global

import java.net.{SocketException, ServerSocket}

import com.prealpha.sge.networking.{AbstractConnectionThread, Listener}
import com.prealpha.sge.logging.log
import com.prealpha.sge.messages.{GoodbyeMessage, Message}


class ConnectionPool(allowConnection: ToUserConnection => Boolean) extends Thread {
    /**
     * If the ConnectionPool is currently looping for incoming connections
     */
    protected var running = false
    /**
     * The list of users currently connected to the game
     */
    private[this] val userConnections = new ListBuffer[ToUserConnection]()
    /**
     * The socket that is currently listening for new connections
     */
    private[this] val serverSocket = new ServerSocket(AbstractConnectionThread.Port)

    val messageListener = new Listener[(ToUserConnection, Message)]

    final override def run() {
        running = true
        try {
            while (running) {
                val u = new ToUserConnection(serverSocket.accept(), this)
                log.info("SERVER-> new client connection")
                u.start()

                // Perform this asynchronously so that users aren't blocked
                // from joining while a player is stuck waiting
                future {
                    if (allowConnection(u)) {
                        userConnections += u
                        log.info("SERVER-> client accepted")
                    }
                    else {
                        u.write(GoodbyeMessage)
                        u.close()
                        log.info("SERVER-> client denied")
                    }
                }
            }
        }
        catch {
            case e: SocketException => // Don't do anything, the socket closed from inside
            case e: Throwable => log.trace(e)
        }
    }

    /**
     * Removes a user from the list of users.  This is used when a user disconnects
     * or when you want to kick a user from the game
     * @param userC The user that is being disconnected
     */
    def removeUserConnection(userC: ToUserConnection) {
        this.userConnections -= userC
    }

    /**
     * For if you want to keep the players that you have, but don't
     * want to accept any more.
     */
    def stopAccepting() {
        this.running = false
        this.serverSocket.close()
        log.info("SERVER: server has stopped accepting ")
    }

    /**
     * Kills the outgoing connections, kills the
     * server socket, and stops the connection pool from running
     */
    def kill() {
        running = false
        userConnections.foreach(_.close())
        serverSocket.close()
    }

    /**
     * Broadcasts a message to every player in
     * the ConnectinoPool.
     * @param m The message to send
     */
    def broadcast(m: Message) {
        userConnections.foreach(_.write(m))
    }

    def registerMessage(user: ToUserConnection, message: Message) {
        broadcast(message)
        messageListener.handle((user, message))
    }
}
