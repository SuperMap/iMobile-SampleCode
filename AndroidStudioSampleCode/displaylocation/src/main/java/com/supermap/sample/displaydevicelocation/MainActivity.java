/* Copyright 2016 Esri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.supermap.sample.displaydevicelocation;

import java.util.ArrayList;
import java.util.Objects;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Environment;
import com.supermap.data.GeoPoint;
import com.supermap.data.Point2D;
import com.supermap.data.Workspace;
import com.supermap.mapping.MapControl;
import com.supermap.plugin.LocationChangedListener;
import com.supermap.plugin.LocationManagePlugin;
import com.supermap.sample.spinner.ItemData;
import com.supermap.sample.spinner.SpinnerAdapter;
import com.supermap.mapping.MapView;

public class MainActivity extends AppCompatActivity {

  private Spinner mSpinner;
  private MapView mMapView;
  private Workspace mWorkspace;
  private MapControl mMapControl;
  Point2D mPoint = new Point2D(12969335.4856042,4863834.11645054);
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Environment.initialization(this);
    setContentView(R.layout.activity_main);
    // Get the Spinner from layout
    mSpinner = (Spinner) findViewById(R.id.spinner);
//    // Get the MapView from layout and set a map with the BasemapType Imagery
    openGoogleMaps();

    // Populate the list for the Location display options for the spinner's Adapter
    ArrayList<ItemData> list = new ArrayList<>();
    list.add(new ItemData("Stop", R.drawable.locationdisplaydisabled));
    list.add(new ItemData("OnLocation", R.drawable.locationdisplayon));
    list.add(new ItemData("Re-Center", R.drawable.locationdisplayrecenter));
//    list.add(new ItemData("Navigation", R.drawable.locationdisplaynavigation));
//    list.add(new ItemData("Compass", R.drawable.locationdisplayheading));

    SpinnerAdapter adapter = new SpinnerAdapter(this, R.layout.spinner_layout, R.id.txt, list);
    mSpinner.setAdapter(adapter);
    mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (position) {
          case 0:
            mMapControl.getMap().setScale(1/29351818.8615112);
            mMapControl.getMap().setCenter(new Point2D(0.0,0.0));
            mMapControl.getMap().refresh();
            break;
          case 1:
            mMapControl.getMap().setScale(1/3582.49285043382);
            getLoction();
            mMapControl.getMap().setCenter(mPoint);
            mMapControl.getMap().refresh();
              break;
          case 2:
            mMapControl.getMap().setScale(1/234814550.891814);
            mMapControl.getMap().setCenter(new Point2D(0.0,0.0));
            mMapControl.getMap().refresh();
              break;
          case 3:

            break;
          case 4:

              break;
        }

      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) { }

    });
  }
  public void getLoction() {

    LocationManagePlugin locationManagePlugin = new LocationManagePlugin();
    boolean b = locationManagePlugin.openGpsDevice((LocationManager) Objects.requireNonNull(this.getSystemService(Context.LOCATION_SERVICE)));
    if (b) {
      locationManagePlugin.setTimeInterval(2000);
      locationManagePlugin.addLocationChangedListener(new LocationChangedListener() {
        @Override
        public void locationChanged(LocationManagePlugin.GPSData gpsData, LocationManagePlugin.GPSData gpsData1) {

          if(gpsData1 != null){

            mPoint.setX(gpsData1.dLongitude);
            mPoint.setY(gpsData1.dLatitude);
          }else{
            mPoint.setX(12969335.4856042);
            mPoint.setY(4863834.11645054);

          }
        }

        @Override
        public void locationChanged(LocationManagePlugin.GPSData gpsData, LocationManagePlugin.GPSData gpsData1, boolean b) {

        }
      });
    }
  }
  private void openGoogleMaps() {

    mMapView = (MapView) findViewById(R.id.mapView);
    mMapControl = mMapView.getMapControl();
    mWorkspace = new Workspace();
    mMapControl.getMap().setWorkspace(mWorkspace);

    DatasourceConnectionInfo info = new DatasourceConnectionInfo();
    info.setAlias("GOOGLE");
    info.setEngineType(EngineType.GoogleMaps);
    String url3 = "http://www.google.cn/maps";
    info.setServer(url3);
    Datasource datasourcegoogle = mWorkspace.getDatasources().open(info);

    mMapControl.getMap().getLayers().add(datasourcegoogle.getDatasets().get(4),false);
    mMapControl.getMap().setScale(1/29351818.8615112);
    mMapControl.getMap().refresh();
  }


}
