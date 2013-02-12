package com.prealpha.sge.networking.client

import com.prealpha.sge.networking.AbstractConnectionThread
import java.net.Socket
import com.prealpha.sge.messages.{GoodbyeMessage, Message}
import com.prealpha.sge.logging.log

abstract class ToServerConnection(address: String) extends AbstractConnectionThread(
    new Socket(address, AbstractConnectionThread.Port)) {

    log.info("CLIENT-> New connection with server")
}
