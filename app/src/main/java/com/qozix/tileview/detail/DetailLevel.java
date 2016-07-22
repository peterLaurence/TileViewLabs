package com.qozix.tileview.detail;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.Log;

import com.qozix.tileview.tiles.Tile;

import java.util.HashSet;
import java.util.Set;

public class DetailLevel implements Comparable<DetailLevel> {

  private float mScale;
  private int mTileWidth;
  private int mTileHeight;
  private Object mData;

  private DetailLevelManager mDetailLevelManager;

  private StateSnapshot mLastStateSnapshot;

  private Set<Tile> mTilesVisibleInViewport = new HashSet<>();

  public DetailLevel( DetailLevelManager detailLevelManager, float scale, Object data, int tileWidth, int tileHeight ) {
    mDetailLevelManager = detailLevelManager;
    mScale = scale;
    mData = data;
    mTileWidth = tileWidth;
    mTileHeight = tileHeight;
  }

  public DetailLevelManager getDetailLevelManager() {
    return mDetailLevelManager;
  }

  /**
   * Returns true if there has been a change, false otherwise.
   *
   * @return True if there has been a change, false otherwise.
   */
  public StateSnapshot computeCurrentState() {
    float relativeScale = getRelativeScale();
    int drawableWidth = mDetailLevelManager.getScaledWidth();
    int drawableHeight = mDetailLevelManager.getScaledHeight();
    float offsetWidth = mTileWidth * relativeScale;
    float offsetHeight = mTileHeight * relativeScale;
    Rect viewport = new Rect( mDetailLevelManager.getComputedViewport() ); // TODO: set
    viewport.top = Math.max( viewport.top, 0 );
    viewport.left = Math.max( viewport.left, 0 );
    viewport.right = Math.min( viewport.right, drawableWidth );
    viewport.bottom = Math.min( viewport.bottom, drawableHeight );
    int rowStart = (int) Math.floor( viewport.top / offsetHeight );
    int rowEnd = (int) Math.ceil( viewport.bottom / offsetHeight );
    int columnStart = (int) Math.floor( viewport.left / offsetWidth );
    int columnEnd = (int) Math.ceil( viewport.right / offsetWidth );
    mLastStateSnapshot = new StateSnapshot( this, rowStart, rowEnd, columnStart, columnEnd );  // TODO: set
    return mLastStateSnapshot;
  }

  public boolean hasComputedState() {
    return mLastStateSnapshot != null;
  }

  public void computeVisibleTilesFromViewport() {
    if( !hasComputedState() ) {
      Log.d( getClass().getSimpleName(), "need state before computing tiles" );
      return;
    }
    int startSize = mTilesVisibleInViewport.size();
    /*
    Iterator<Tile> visibleTileIterator = mTilesVisibleInViewport.iterator();
    while( visibleTileIterator.hasNext() ) {
      Tile tile = visibleTileIterator.next();
      if( !mLastStateSnapshot.contains( tile ) ) {
        tile.destroy( true );  // TODO:
        tile.reset();
      }
      // separate block since it might have been destroyed elsewhere
      if( tile.getState() == Tile.State.DESTROYED ) {
        visibleTileIterator.remove();
      }
    }
    */
    mTilesVisibleInViewport.clear();
    Log.d( getClass().getSimpleName(), "should be adding from " + mLastStateSnapshot.columnStart + ":" + mLastStateSnapshot.rowStart +
      " through " + mLastStateSnapshot.columnEnd + ":" + mLastStateSnapshot.columnStart);
    for( int rowCurrent = mLastStateSnapshot.rowStart; rowCurrent < mLastStateSnapshot.rowEnd; rowCurrent++ ) {
      for( int columnCurrent = mLastStateSnapshot.columnStart; columnCurrent < mLastStateSnapshot.columnEnd; columnCurrent++ ) {
        Tile tile = new Tile( columnCurrent, rowCurrent, mTileWidth, mTileHeight, mData, this );
        mTilesVisibleInViewport.add( tile );
      }
    }
    Log.d( getClass().getSimpleName(), "DetailLevel added " + (mTilesVisibleInViewport.size() - startSize) + " tiles" );
  }

  public Set<Tile> getTilesVisibleInViewport() {
    return mTilesVisibleInViewport;
  }

  /**
   * Ensures that computeCurrentState will return true, indicating a change has occurred.
   */
  public void invalidate() {
    mLastStateSnapshot = null;
  }

  public float getScale() {
    return mScale;
  }

  public float getRelativeScale() {
    return mDetailLevelManager.getScale() / mScale;
  }

  public int getTileWidth() {
    return mTileWidth;
  }

  public int getTileHeight() {
    return mTileHeight;
  }

  public Object getData() {
    return mData;
  }

  @Override
  public int compareTo( @NonNull DetailLevel detailLevel ) {
    return (int) Math.signum( getScale() - detailLevel.getScale() );
  }

  @Override
  public boolean equals( Object object ) {
    if( this == object ) {
      return true;
    }
    if( object instanceof DetailLevel ) {
      DetailLevel detailLevel = (DetailLevel) object;
      return mScale == detailLevel.getScale();
    }
    return false;
  }

  @Override
  public int hashCode() {
    long bits = (Double.doubleToLongBits( getScale() ) * 43);
    return (((int) bits) ^ ((int) (bits >> 32)));
  }

  public static class StateNotComputedException extends IllegalStateException {
    public StateNotComputedException() {
      super( "Grid has not been computed; " +
        "you must call computeCurrentState at some point prior to calling " +
        "getVisibleTilesFromLastViewportComputation." );
    }
  }

  public static class StateSnapshot {
    public int rowStart;
    public int rowEnd;
    public int columnStart;
    public int columnEnd;
    public DetailLevel detailLevel;

    public StateSnapshot( DetailLevel detailLevel, int rowStart, int rowEnd, int columnStart, int columnEnd ) {
      this.detailLevel = detailLevel;
      this.rowStart = rowStart;
      this.rowEnd = rowEnd;
      this.columnStart = columnStart;
      this.columnEnd = columnEnd;
    }

    public boolean contains( Tile tile ) {
      return tile.getColumn() >= columnStart
        && tile.getColumn() <= columnEnd
        && tile.getRow() >= rowStart
        && tile.getRow() <= rowEnd;
    }

    public boolean equals( Object o ) {
      if( o == this ) {
        return true;
      }
      if( o == null ) {
        return false;
      }
      if( o instanceof StateSnapshot ) {
        StateSnapshot stateSnapshot = (StateSnapshot) o;
        return detailLevel.equals( stateSnapshot.detailLevel )
          && rowStart == stateSnapshot.rowStart
          && columnStart == stateSnapshot.columnStart
          && rowEnd == stateSnapshot.rowEnd
          && columnEnd == stateSnapshot.columnEnd;
      }
      return false;
    }
  }


}