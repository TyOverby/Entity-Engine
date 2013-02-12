package com.prealpha.sge.networking.server

import collection.mutable.ListBuffer
import scala.concurrent.future
import scala.concurrent.ExecutionContext.Implicits.global

import java.net.{SocketException, ServerSocket}

import com.prealpha.sge.networking.AbstractConnectionThread
import com.prealpha.sge.logging.log
import com.prealpha.sge.messages.{GoodbyeMessage, Message}

abstract class ConnectionPool extends Thread{
    /**
     * If the ConnectionPool is currently looping for incomming connections
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



    final override def run() {
        running = true
        try{
        while(running){
            val u = new ToUserConnection(serverSocket.accept(), this)
            log.info("SERVER-> new client connection")
            u.start()

            // Perform this asynchronously so that users aren't blocked
            // from joining while a player is stuck waiting
            future {
                if (allowConnection(u)){
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
        catch{
            case e: SocketException => // Don't do anything, the socket closed from inside
            case e: Throwable       => log.trace(e)
        }
    }

    /**
     * Removes a user from the list of users.  This is used when a user disconnects
     * or when you want to kick a user from the game
     * @param userC The user that is being disconnected
     */
    def removeUserConnection(userC: ToUserConnection){
        this.userConnections -= userC
    }

    /**
     * For if you want to keep the players that you have, but don't
     * want to accept any more.
     */
    def stopAccepting(){
        this.running = false
        this.serverSocket.close()
    }

    /**
     * Kills the outgoing connections, kills the
     * server socket, and stops the connection pool from running
     */
    def kill(){
        running = false
        userConnections.foreach(_.close())
        serverSocket.close()
    }

    /**
     * Broadcasts a message to every player in
     * the ConnectinoPool.
     * @param m The message to send
     */
    def broadcast(m: Message){
        userConnections.foreach(_.write(m))
    }

    /**
     * Used to validate that a player can actually join a game,
     * this should be used to initiate a handshake with something
     * like a password
     * @param user The user that is attempting to connect
     * @return If the user is allowed to actually connect
     */
    def allowConnection(user: ToUserConnection): Boolean

    def onPlayerUpdate(user: ToUserConnection, message: Message)

    def registerMessage(user: ToUserConnection, message: Message){
        broadcast(message)
        onPlayerUpdate(user, message)
    }
}
