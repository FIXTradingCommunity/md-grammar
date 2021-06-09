package io.fixprotocol.md.event;


/**
 * An object that belongs to a graph
 * 
 * @author Don Mendelson
 *
 */
public interface MutableGraphContext {

  /**
   * Set a parent Context to build a graph
   *
   * @param parent parent Context
   */
  void setParent(Context parent);

}
