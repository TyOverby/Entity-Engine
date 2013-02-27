package com.prealpha.sge.networking.networking

import java.net.Socket
import com.prealpha.sge.networking.AbstractConnectionThread
import com.prealpha.sge.logging.log

/**
 * ONLY FOR USE IN TESTING
 * @param toClient
 */
class Server(toClient: Socket) extends AbstractConnectionThread(toClient){
  val observer = messagePublisher.observe{
    case m => log.info(f"SERVER: Got message: ${m.getClass.getSimpleName}")
  }

  start()
}
