package com.prealpha.sge.networking

import java.net.{SocketException, Socket}
import com.prealpha.sge.messages.Message
import java.io.{EOFException, ObjectInputStream, ObjectOutputStream}
import com.prealpha.sge.logging.log


object AbstractConnectionThread{
    val Port = 8008
}

class AbstractConnectionThread(socket: Socket) extends Thread{
    /**
     * Input and output streams are used for communication with the
     * Socket
     */
    private[this] val out = new ObjectOutputStream(socket.getOutputStream)
    private[this] val in  = new ObjectInputStream(socket.getInputStream)

    /**
     * If the loop is running looking for more messages
     */
    private[this] var running = false

    val messagePublisher = new Publisher[Message]
    val closePublisher   = new Publisher[Unit]

    private[this] var closed = false

    override def run(){
        setName(f"${getClass.getSimpleName} [${socket.getRemoteSocketAddress}]")
        running = true

        try {
            while(running){
                val msg = in.readObject().asInstanceOf[Message]
                messagePublisher.publish(msg)
            }
        }
        catch {
            // This happens if "we" kill the connection while still trying to read
            case e: EOFException    => this.close(e)
            // This happens if "they" kill the connection while still trying to read
            case e: SocketException => this.close(e)
            // Oh god, what is happening, make it stop!
            case e: Throwable       => log.trace(e)
        }
        finally {
            if (!closed){
                running = false

                out.close()
                in.close()
                socket.close()
            }
        }
    }

    /**
     * Writes a message to the output stream and pushes
     * the message down the pipe
     * @param message The Message to send
     */
    def write(message: Message){
        out.writeObject(message)
    }
    def flush(){
        out.flush()
    }

    /**
     * Closes the socket and stops the loop
     */
    def close(){
        log.info("connection closed from a " + this.getClass.getSimpleName)
        running = false
        closed = true
        this.socket.close()
        this.closePublisher.publish()
    }
    protected def close(reason: Exception){
        log.trace(reason)
        this.close()
    }
}
