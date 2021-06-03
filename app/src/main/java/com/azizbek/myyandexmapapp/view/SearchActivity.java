package com.azizbek.myyandexmapapp.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.azizbek.myyandexmapapp.database.DBHelper;
import com.azizbek.myyandexmapapp.model.Model;
import com.azizbek.myyandexmapapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.GeoObjectCollection;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.layers.GeoObjectTapEvent;
import com.yandex.mapkit.layers.GeoObjectTapListener;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateSource;
import com.yandex.mapkit.map.GeoObjectSelectionMetadata;
import com.yandex.mapkit.map.InertiaMoveListener;
import com.yandex.mapkit.map.InputListener;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.VisibleRegionUtils;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.search.Response;
import com.yandex.mapkit.search.SearchOptions;
import com.yandex.mapkit.search.Session;
import com.yandex.mapkit.search.SuggestOptions;
import com.yandex.mapkit.search.SuggestSession;
import com.yandex.mapkit.geometry.BoundingBox;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.search.SearchFactory;
import com.yandex.mapkit.search.SearchManager;
import com.yandex.mapkit.search.SearchManagerType;
import com.yandex.mapkit.search.SuggestItem;
import com.yandex.mapkit.search.SuggestType;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class SearchActivity extends AppCompatActivity implements SuggestSession.SuggestListener,
        Session.SearchListener, CameraListener, GeoObjectTapListener, InputListener, InertiaMoveListener {

    private final static String MAP_KIT_API_KEY = "522fb9ba-acc3-4c2a-ad64-371448cace44";
    private final static int RESULT_NUMBER_LIMIT = 5;
    private SearchManager searchManager;
    private SuggestSession suggestSession;
    private ListView suggestResultView;
    private ArrayAdapter<String> resultAdapter;
    private List<String> suggestResult;
    private final Point CENTER = new Point(55.75, 37.62);
    private final double BOX_SIZE = 0.2;
    private final BoundingBox BOUNDING_BOX = new BoundingBox(new Point(CENTER.getLatitude() - BOX_SIZE, CENTER.getLongitude() - BOX_SIZE),
            new Point(CENTER.getLatitude() + BOX_SIZE, CENTER.getLongitude() + BOX_SIZE));
    private final SuggestOptions SEARCH_OPTIONS = new SuggestOptions().setSuggestTypes(SuggestType.GEO.value | SuggestType.BIZ.value | SuggestType.TRANSIT.value);
    private MapView mapview;
    private String searchEdit;
    private CardView currentLocation, searchBar;
    private EditText queryEdit;
    private AlertDialog.Builder alertDialog;
    private boolean isPressed = false;
    private boolean isChiqish=false;
    private TextView cardLocationName;
    private int a = 0;
    private SharedPreferences sharedPreferences;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private final int locationRequestCode = 1000;
    private double wayLatitude = 0.0, wayLongitude = 0.0;
    BottomNavigationView bottomNavigationView;
    ConstraintLayout layout;
    LottieAnimationView lottieAnimationView;

    public SearchActivity() {
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MapKitFactory.setApiKey(MAP_KIT_API_KEY);

        MapKitFactory.initialize(SearchActivity.this);
        SearchFactory.initialize(SearchActivity.this);
        setContentView(R.layout.activity_search);
        super.onCreate(savedInstanceState);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        loadCallback();
        alertDialog = new AlertDialog.Builder(SearchActivity.this);
        sharedPreferences = getSharedPreferences("locationName", Context.MODE_PRIVATE);
        bottomNavigationView = findViewById(R.id.bottomview);

        Objects.requireNonNull(getSupportActionBar()).hide();
        mapview = findViewById(R.id.mapview);
        mapview.getMap().addTapListener(this);
        mapview.getMap().addInputListener(this);
        mapview.getMap().addInertiaMoveListener(this);



        moveCamera(new Point(41.312301,69.28209),15f);
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED);
        suggestSession = searchManager.createSuggestSession();
        queryEdit = findViewById(R.id.suggest_query);
        suggestResultView = findViewById(R.id.suggest_result);
        suggestResult = new ArrayList<>();

        currentLocation=findViewById(R.id.currentLocation);
        lottieAnimationView = findViewById(R.id.movablePin);
        searchBar = findViewById(R.id.searchbar);
        currentLocation = findViewById(R.id.currentLocation);
        cardLocationName = findViewById(R.id.cardlocationname);
        resultAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                suggestResult);
        suggestResultView.setAdapter(resultAdapter);

        suggestResultView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            searchEdit = resultAdapter.getItem(position);
            layout = findViewById(R.id.container);

            submitQuery(searchEdit);
            if (isPressed) {
                setAlertDialog(searchEdit);
                isPressed = false;
            }
            suggestResultView.setVisibility(View.GONE);
        });


        controlBottomView();
        queryEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                requestSuggest(editable.toString());
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        lottieAnimationView.playAnimation();
    }

    @SuppressLint("NonConstantResourceId")
    private void controlBottomView(){

        bottomNavigationView.setOnNavigationItemSelectedListener((BottomNavigationView.OnNavigationItemSelectedListener) item -> {
            currentLocation.setVisibility(View.VISIBLE);
            lottieAnimationView.setVisibility(View.VISIBLE);
            searchBar.setVisibility(View.VISIBLE);

            switch (item.getItemId()){

                case R.id.saved_address: myBookmarks();break;
                case R.id.my_location:
                    myLocation()
                    ;break;
                case R.id.add_address:addAddress();break;
            }
            return true;
        });
    }

    public void getSharedPref(int count) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("Count", count);
        editor.apply();
        editor.commit();
    }

    private void loadCallback(){
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(20 * 1000);

        new LocationCallback() {
            @Override
            public void onLocationResult(@NotNull LocationResult locationResult) {

                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                    }
                }
            }
        };
    }

    @Override
    public void onBackPressed() {
        if (!isChiqish) {
            Toast.makeText(this, "Press again", Toast.LENGTH_SHORT).show();
            new Handler(getMainLooper()).postDelayed(() -> isChiqish = false, 2000);
            isChiqish = true;
        } else {
            super.onBackPressed();
            getSharedPref(0);
        }
    }

    @Override
    protected void onStop() {
        MapKitFactory.getInstance().onStop();
        mapview.onStop();
        super.onStop();
    }


    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapview.onStart();
        Menu menu = bottomNavigationView.getMenu();
        for (int i = 0; i < bottomNavigationView.getChildCount(); i++) {
            MenuItem itemView = (MenuItem) menu.getItem(i);
            if (i != 1) {
                itemView.setChecked(true);
            }
        }
        searchSavedAddress();

    }

    public void searchSavedAddress(){
        String bookmarks = sharedPreferences.getString("locationName", "");
        int count = sharedPreferences.getInt("Count", 0);
        if (count == 1) {
           submitQuery(bookmarks);
        }
    }

    @Override
    public void onResponse(@NonNull List<SuggestItem> suggest) {
        suggestResult.clear();
        for (int i = 0; i < Math.min(RESULT_NUMBER_LIMIT, suggest.size()); i++) {
            suggestResult.add(suggest.get(i).getDisplayText());
        }
        resultAdapter.notifyDataSetChanged();
        suggestResultView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onError(@NonNull Error error) {
        String errorMessage = getString(R.string.unknown_error_message);
        if (error instanceof RemoteError) {
            errorMessage = getString(R.string.remote_error_message);
        } else if (error instanceof NetworkError) {
            errorMessage = getString(R.string.network_error_message);
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void requestSuggest(String query) {
        suggestResultView.setVisibility(View.INVISIBLE);
        suggestSession.suggest(query, BOUNDING_BOX, SEARCH_OPTIONS, this);
    }

    @Override
    public void onCameraPositionChanged(@NonNull Map map, @NonNull
            CameraPosition cameraPosition, @NonNull CameraUpdateSource cameraUpdateSource, boolean b) {

        if (b) {
            submitQuery(searchEdit);
        }
    }

    @Override
    public void onSearchResponse(@NonNull Response response) {
        MapObjectCollection mapObjects = mapview.getMap().getMapObjects();
        mapObjects.clear();

        for (GeoObjectCollection.Item searchResult : response.getCollection().getChildren()) {
            Point resultLocation = Objects.requireNonNull(searchResult.getObj()).getGeometry().get(0).getPoint();
            if (resultLocation != null) {
                moveCamera(resultLocation, 15f);
                mapObjects.addPlacemark(
                        resultLocation,
                        ImageProvider.fromResource(this, R.drawable.search_layer_pin_selected_default));
            }
        }
    }

    private void moveCamera(Point point, Float zoom) {

        mapview.getMap().move(
                new CameraPosition(point, zoom, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 1),
                null);
    }

    @Override
    public void onSearchError(@NonNull Error error) {
        String errorMessage = getString(R.string.unknown_error_message);
        if (error instanceof RemoteError) {
            errorMessage = getString(R.string.remote_error_message);
        } else if (error instanceof NetworkError) {
            errorMessage = getString(R.string.network_error_message);
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void submitQuery(String query) {
        cardLocationName.setText(query);
        searchManager.submit(
                query,
                VisibleRegionUtils.toPolygon(mapview.getMap().getVisibleRegion()),
                new SearchOptions(),
                this);
    }

    //selection
    @Override
    public boolean onObjectTap(@NonNull GeoObjectTapEvent geoObjectTapEvent) {

        String name = geoObjectTapEvent.getGeoObject().getName();

            if (a == 1) {
                currentLocation.setVisibility(View.VISIBLE); }

            if (name!=null) {

            cardLocationName.setText(name);
            }

            final GeoObjectSelectionMetadata selectionMetadata = geoObjectTapEvent
                    .getGeoObject()
                    .getMetadataContainer()
                    .getItem(GeoObjectSelectionMetadata.class);


            return selectionMetadata != null;
    }


    public void myBookmarks() {
        DBHelper helper = new DBHelper(SearchActivity.this);
        ArrayList<Model> models;
        models = helper.getAllLocations();
        if (models.size() > 0) {
            startActivity(new Intent(this, SavedLocationActivity.class));
            getSharedPref(0);
        } else {
            Toast.makeText(this, "Address no found", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteCard(View view) {
        a = 1;
        currentLocation.setVisibility(View.INVISIBLE);
    }

    public void addAddress() {
        queryEdit.setFocusable(true);
        Toast.makeText(this, "Enter Address and Press it", Toast.LENGTH_SHORT).show();
    }

    public void addCurrentLocation(View view) {
        setAlertDialog(cardLocationName.getText().toString().trim());
    }

    private void setAlertDialog(String LOCATION_NAME) {
        DBHelper helper = new DBHelper(SearchActivity.this);
        final ArrayList<Model> arrayList = new ArrayList<>();
        final EditText input = new EditText(SearchActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        input.setLayoutParams(lp);
        alertDialog.setTitle("Add Bookmark")
                .setTitle("Enter Name")
                .setIcon(R.drawable.ic_baseline_add_location_24)
                .setView(input);

        alertDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        alertDialog.setPositiveButton("Add", (dialog, which) -> {
            String myinput = input.getText().toString().trim();
            Model model = new Model(myinput, LOCATION_NAME);
            long result = helper.insertData(model);
            if (result > 0) {
                Toast.makeText(SearchActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public void clearText(View view) {
        suggestResultView.setVisibility(View.GONE);
    }

    public void myLocation() {
        getMyLocation();
    }

    private void getMyLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, locationRequestCode);

        } else {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this,
                    location -> {
                        if (location != null) {
                            wayLatitude = location.getLatitude();
                            wayLongitude = location.getLongitude();
                            Point point = new Point(wayLatitude, wayLongitude);
                            mapview.getMap().move(
                                    new CameraPosition(point, 15f, 0.0f, 0.0f),
                                    new Animation(Animation.Type.SMOOTH, 1),
                                    null);
                            MapObjectCollection mapObjects = mapview.getMap().getMapObjects();
                            mapObjects.clear();
                            mapObjects.addPlacemark(
                                    point,
                                    ImageProvider.fromResource(this, R.drawable.user_arrow));

                        }
                    });
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    }, locationRequestCode);
                    return;
                }
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this,
                        location -> {
                            if (location != null) {
                                wayLatitude = location.getLatitude();
                                wayLongitude = location.getLongitude();
                            }
                        });
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapTap(@NonNull @NotNull Map map, @NonNull @NotNull Point point) {

    }

    @Override
    public void onMapLongTap(@NonNull @NotNull Map map, @NonNull @NotNull Point point) {

    }


    @Override
    public void onStart(@NonNull @NotNull Map map, @NonNull @NotNull CameraPosition cameraPosition) {
        lottieAnimationView.playAnimation();
        android.view.animation.Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_up);
        lottieAnimationView.startAnimation(animation);
    }

    @Override
    public void onCancel(@NonNull @NotNull Map map, @NonNull @NotNull CameraPosition cameraPosition) {
    }

    @Override
    public void onFinish(@NonNull @NotNull Map map, @NonNull @NotNull CameraPosition cameraPosition) {
    }

}
