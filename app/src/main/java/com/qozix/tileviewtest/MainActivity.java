package com.qozix.tileviewtest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.qozix.tileview.TileView;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate( Bundle savedInstanceState ) {
    super.onCreate( savedInstanceState );

    // we'll reference the TileView multiple times
    TileView tileView = new TileView(this);

    // size and geolocation
    tileView.setSize( 7680, 7680 );

    // we won't use a downsample here, so color it similarly to tiles
    tileView.setBackgroundColor( 0xFFe7e7e7 );

    //tileView.addDetailLevel( 0.0125f, "tiles/map/phi-62500-%d_%d.jpg" );
    //tileView.addDetailLevel( 0.2500f, "tiles/map/d-%d-%d.png" );
    tileView.addDetailLevel( 0.5000f, "tiles/map/d-%d-%d.png" );
    tileView.addDetailLevel( 1.0000f, "tiles/map/o-%d-%d.png" );

    tileView.setShouldRenderWhilePanning( true );
    tileView.setTransitionsEnabled( false );

    setContentView(tileView);

  }
}
