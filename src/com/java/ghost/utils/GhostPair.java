package com.java.ghost.utils;

import org.apache.log4j.Logger;


public class GhostPair<InputObjectType, InputObjectType2>{ //implements Comparable< GhostPair<InputObjectType, InputObjectType2> > {
  private static Logger logger = Logger.getLogger("GhostPair");
  private InputObjectType _left;
  private InputObjectType2 _right;

  public GhostPair(InputObjectType key, InputObjectType2 value) {
    _left   = key;
    _right = value;
  }
  
  public InputObjectType getLeft() {
    return _left;
  }
  
  public InputObjectType2 getRight() {
    return _right;
  }
  
//  public String toString() {
//    System.out.println("in toString()");
//    StringBuffer buff = new StringBuffer();
//      buff.append("Key: ");
//      buff.append(_left);
//      buff.append("\tValue: ");
//      buff.append(_right);
//    return(buff.toString() );
//  }
  
//  public int compareTo( GhostPair<InputObjectType, InputObjectType2> p1 ) {
//    if ( null != p1 ) { 
//      if ( p1.equals(this) ) { 
//        return 0; 
//      } else if ( p1.hashCode() > this.hashCode() ) { 
//            return 1;
//      } else if ( p1.hashCode() < this.hashCode() ) { 
//        return -1;  
//      }
//    }
//    return(-1);
//  }
  
  public boolean equals(Object obj) {
	if(this == obj){
		return true;
	}
	
    if ( obj!=null && obj instanceof GhostPair<?,?>) {//(GhostPair<InputObjectType, InputObjectType2>)obj
    	try{
    	    @SuppressWarnings("unchecked")
			GhostPair<InputObjectType, InputObjectType2> tmp = (GhostPair<InputObjectType, InputObjectType2>)obj;
    	    if ( tmp._left.equals( this._left ) && tmp._right.equals( this._right ) ) { 
		         return true; 
		    }
    	    }catch(ClassCastException e){
    	    	logger.error(e.getMessage(),e);
    	    }
    }
    return false;
  }

  public void setLeft(InputObjectType left) {
		_left = left;
	   }
  
  public void setRight(InputObjectType2 right) {
	_right = right;
   }
  
  public int hashCode() { 
	  //TODO: NO idea if this is correct usage
	  int[] keyArray = {_left.hashCode(),_right.hashCode(),_right.hashCode()};
    //int hashCode = _left.hashCode() + (31 * _right.hashCode());
    return GhostHash.hashFunction(keyArray , 0, 3, _right.hashCode());
  }
}

