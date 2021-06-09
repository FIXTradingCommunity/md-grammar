package io.fixprotocol.md.event;


/**
 * An object that has context in a graph
 *
 * @author Don Mendelson
 *
 */
public interface GraphContext {

  /**
   * Returns the Context to which an object belongs, or a broader Context
   *
   * @return a parent Context or {@code null} if there is no parent (root context)
   */
  Context getParent();

}
