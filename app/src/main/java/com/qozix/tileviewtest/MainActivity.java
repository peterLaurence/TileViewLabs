package com.qozix.tileviewtest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

import com.qozix.tileview.TileView;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate( Bundle savedInstanceState ) {
    super.onCreate( savedInstanceState );

    setContentView(R.layout.activity_main);

    // we'll reference the TileView multiple times
    final TileView tileView = (TileView) findViewById( R.id.tileview );

    // size and geolocation
    tileView.setSize( 7680, 7680 );

    //tileView.addDetailLevel( 0.0125f, "tiles/map/phi-62500-%d_%d.jpg" );
    //tileView.addDetailLevel( 0.2500f, "tiles/map/d-%d-%d.png" );
    tileView.addDetailLevel( 0.5000f, "tiles/map/d-%d-%d.jpg" );
    tileView.addDetailLevel( 1.0000f, "tiles/map/o-%d-%d.jpg" );

    tileView.setShouldRenderWhilePanning( true );
    tileView.setTransitionsEnabled( true );

    tileView.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        tileView.getTileCanvasViewGroup().invalidate();
        return false;
      }
    });



  }
}
