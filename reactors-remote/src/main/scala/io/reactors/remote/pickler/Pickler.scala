package io.reactors
package remote.pickler



import java.io._
import java.nio.ByteBuffer



/** Pickles an object into a byte buffer, so that it can be sent over the wire.
 */
trait Pickler {
  def pickle[@spec(Int, Long, Double) T](x: T, buffer: ByteBuffer): Unit
  def depickle[@spec(Int, Long, Double) T](buffer: ByteBuffer): T
}


object Pickler {
  /** Pickler implementation based on Java serialization.
   */
  class JavaSerialization extends Pickler {
    def pickle[@spec(Int, Long, Double) T](x: T, buffer: ByteBuffer) = {
      val os = new ByteBufferOutputStream(buffer)
      val oos = new ObjectOutputStream(os)
      oos.writeObject(x)
    }
    def depickle[@spec(Int, Long, Double) T](buffer: ByteBuffer): T = {
      val is = new ByteBufferInputStream(buffer)
      val ois = new ObjectInputStream(is)
      ois.readObject().asInstanceOf[T]
    }
  }

  private class ByteBufferOutputStream(val buf: ByteBuffer) extends OutputStream {
    def write(b: Int): Unit = buf.put(b.toByte)
    override def write(bytes: Array[Byte], off: Int, len: Int): Unit = {
      buf.put(bytes, off, len)
    }
  }

  private class ByteBufferInputStream(val buffer: ByteBuffer) extends InputStream {
    def read() = buffer.get()
    override def read(dst: Array[Byte], offset: Int, length: Int) = {
      val count = math.min(buffer.remaining, length)
      if (count == 0) -1
      else {
        buffer.get(dst, offset, length)
        count
      }
    }
  }
}
