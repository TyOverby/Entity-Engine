package com.prealpha.sge.networking.networking

import java.net.{Socket, ServerSocket}
import com.prealpha.sge.logging.log

/**
 * ONLY FOR USE IN TESTS
 */
class MasterServer(port: Int, gen: Socket => Any) extends Thread {
  val serverSocket = new ServerSocket(port)

  private[this] var running = false

  override def run() {
    running = true
    while (running) {
      gen(serverSocket.accept())
      log.info("MASTER-SERVER: Client connection accepted")
    }
  }

  start()
}
