package com.example.abhi.basiclauncher;

import android.app.Activity;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    DrawerAdapter drawerAdapterObject;
    GridView drawerGrid;
    RelativeLayout homeView;
    static boolean appLaunchable = true;
    static List<Pac> pacForUpdateSerialization;
    List<Pac> pacs;
    Pac[] pacsTemp;
    FavPac[] favPacs;
    PackageManager pm;
    AppWidgetManager mAppWidgetManager;
    LauncherAppWidgetHost mAppWidgetHost;
    int numWidgets = 0;
    SharedPreferences globalPrefs;
    static Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);

        homeView = (RelativeLayout) findViewById(R.id.home_view);
      /*  Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

/*
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        homeView.post(new Runnable() {

            @Override
            public void run() {
                homeView.setBackground(wallpaperDrawable);
            }
        });
*/


        activity = this;
        mAppWidgetManager = AppWidgetManager.getInstance(this);
        mAppWidgetHost = new LauncherAppWidgetHost(this, R.id.APPWIDGET_HOST_ID);

        globalPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        pm = getPackageManager();
        drawerGrid = (GridView) findViewById(R.id.content);


        favPacs = new FavPac[10];
        new LoadApps().execute();
        pacForUpdateSerialization = Arrays.asList((new Pac[0]));



        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");
        registerReceiver(new PacReciever(), filter);

        final SearchView searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                for (int i = 0; i < pacs.size(); i++) {
                    if (pacs.get(i).name.contains(query)) {
                        drawerAdapterObject.getFilter().filter(query);
                    } else {
                        Toast.makeText(MainActivity.this, "No Match found", Toast.LENGTH_LONG).show();
                    }
                }
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                drawerAdapterObject.getFilter().filter(newText);
                return false;
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                configureWidget(data);
            } else if (requestCode == 2) {
                createWidget(data);
            } else if (requestCode == 3) {
                configureShortcut(data);
            } else if (requestCode == 4) {
                createShortcut(data);
            } else if (requestCode == 5) {
                globalPrefs.edit().putString("theme", data.getComponent().getPackageName()).commit();
                new LoadApps().execute();
            }
        } else if (resultCode == RESULT_CANCELED && data != null) {
            int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            if (appWidgetId != -1) {
                mAppWidgetHost.deleteAppWidgetId(appWidgetId);
            }
        }
    }

    void configureShortcut(Intent data) {
        startActivityForResult(data, 4);
    }

    public void createShortcut(Intent intent) {
        Intent.ShortcutIconResource iconResource = intent.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE);
        Bitmap icon = intent.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON);
        String shortcutLabel = intent.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);
        Intent shortIntent = intent.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);

        if (icon == null) {
            if (iconResource != null) {
                Resources resources = null;
                try {
                    resources = pm.getResourcesForApplication(iconResource.packageName);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                if (resources != null) {
                    int id = resources.getIdentifier(iconResource.resourceName, null, null);
                    if (resources.getDrawable(id) instanceof StateListDrawable) {
                        Drawable d = ((StateListDrawable) resources.getDrawable(id)).getCurrent();
                        icon = ((BitmapDrawable) d).getBitmap();
                    } else {
                        icon = ((BitmapDrawable) resources.getDrawable(id)).getBitmap();
                    }
                }
            }
        }

        if (shortcutLabel != null && shortIntent != null && icon != null) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.leftMargin = 100;
            lp.topMargin = 220;

            LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout ll = (LinearLayout) li.inflate(R.layout.drawer_items, null);

            ((ImageView) ll.findViewById(R.id.icon_image)).setImageBitmap(icon);
            ((TextView) ll.findViewById(R.id.icon_label)).setText(shortcutLabel);

            ll.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    v.setOnTouchListener(new AppTouchListenerWS());
                    return true;
                }
            });

            ll.setOnClickListener(new ShortcutClickListener(this));
            ll.setTag(shortIntent);
            homeView.addView(ll, lp);
        }
    }

    private void configureWidget(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        if (appWidgetInfo.configure != null) {
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(appWidgetInfo.configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            startActivityForResult(intent, 2);
        } else {
            createWidget(data);
        }
    }

    public void createWidget(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        final LauncherAppWidgetHostView hostView = (LauncherAppWidgetHostView) mAppWidgetHost.createView(this, appWidgetId, appWidgetInfo);
        hostView.setAppWidget(appWidgetId, appWidgetInfo);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(homeView.getWidth() / 2, homeView.getHeight() / 3);
        lp.leftMargin = numWidgets * (homeView.getWidth() / 2);
        lp.topMargin = 220;

        hostView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                v.setOnTouchListener(new AppTouchListenerWS());
                return true;
            }
        });
        homeView.addView(hostView, lp);
        numWidgets++;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAppWidgetHost.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAppWidgetHost.stopListening();
    }

    public class LoadApps extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

            List<ResolveInfo> pacsList = pm.queryIntentActivities(mainIntent, 0);
            pacs = Arrays.asList(new Pac[pacsList.size()]);
            pacForUpdateSerialization = Arrays.asList(new Pac[pacsList.size()]);

            for (int i = 0; i < pacsList.size(); i++) {
                pacs.set(i, new Pac());
                pacs.get(i).icon = pacsList.get(i).loadIcon(pm);
                pacs.get(i).packageName = pacsList.get(i).activityInfo.packageName;
                pacs.get(i).name = pacsList.get(i).activityInfo.name;
                pacs.get(i).label = pacsList.get(i).loadLabel(pm).toString();
                pacs.get(i).mainActivity = pacsList.get(i).activityInfo.parentActivityName;
                pacForUpdateSerialization.set(i, new Pac());
                pacForUpdateSerialization.get(i).icon = pacsList.get(i).loadIcon(pm);
                pacForUpdateSerialization.get(i).packageName = pacsList.get(i).activityInfo.packageName;
                pacForUpdateSerialization.get(i).name = pacsList.get(i).activityInfo.name;
                pacForUpdateSerialization.get(i).label = pacsList.get(i).loadLabel(pm).toString();
            }

            new SortApps().exchange_sort(pacs);
            new SortApps().exchange_sort(pacForUpdateSerialization);
            themePacs();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (drawerAdapterObject == null) {
                drawerAdapterObject = new DrawerAdapter(activity, pacs);
                drawerGrid.setAdapter(drawerAdapterObject);
                drawerGrid.setOnItemClickListener(new DrawerClickListener(activity, pacs, pm));

                drawerGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                        View appView = getLayoutInflater().inflate(R.layout.popup_on_app_icon, null);
                        final View addToHomeView = view;
                        final long addToHomeId = id;

                        final String packageName = pacs.get(position).packageName;

                        final Dialog mBottomSheetDialog = new Dialog(MainActivity.this, R.style.MaterialDialogSheet);
                        appView.setBackgroundColor(Color.WHITE);

                        LinearLayout appInfo = (LinearLayout) appView.findViewById(R.id.app_info);
                        appInfo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    //Open the specific App Info page:
                                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.setData(Uri.parse("package:" + packageName));
                                    startActivity(intent);

                                } catch (ActivityNotFoundException e) {
                                    //Open the generic Apps page:
                                    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                                    startActivity(intent);

                                }
                                mBottomSheetDialog.dismiss();
                            }
                        });

                        LinearLayout uninstall = (LinearLayout) appView.findViewById(R.id.uninstall);
                        uninstall.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_DELETE);
                                intent.setData(Uri.parse("package:" + packageName));
                                startActivity(intent);
                                mBottomSheetDialog.dismiss();
                            }
                        });

                        LinearLayout addToHome = (LinearLayout) appView.findViewById(R.id.add_to_home);
                        addToHome.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new DrawerLongClickListener(activity, homeView, pacs, parent, addToHomeView, position, addToHomeId);
                                mBottomSheetDialog.dismiss();
                            }
                        });


                        LinearLayout cancelDialog = (LinearLayout) appView.findViewById(R.id.cancel_dialog);
                        cancelDialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mBottomSheetDialog.dismiss();
                            }
                        });

                        mBottomSheetDialog.setContentView(appView);
                        mBottomSheetDialog.setCancelable(true);
                        mBottomSheetDialog.setCanceledOnTouchOutside(true);
                        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);

                        mBottomSheetDialog.show();

                        return true;
                    }
                });


            } else {
                drawerAdapterObject.originalList = pacs;
                drawerAdapterObject.notifyDataSetInvalidated();
            }
        }
    }


    public void themePacs() {
        //theming vars-----------------------------------------------
        final int ICONSIZE = Tools.numtodp(65, MainActivity.this);
        Resources themeRes = null;
        String resPacName = globalPrefs.getString("theme", "");
        String iconResource = null;
        int intres = 0;
        int intresiconback = 0;
        int intresiconfront = 0;
        int intresiconmask = 0;
        float scaleFactor = 1.0f;

        Paint p = new Paint(Paint.FILTER_BITMAP_FLAG);
        p.setAntiAlias(true);

        Paint origP = new Paint(Paint.FILTER_BITMAP_FLAG);
        origP.setAntiAlias(true);

        Paint maskp = new Paint(Paint.FILTER_BITMAP_FLAG);
        maskp.setAntiAlias(true);
        maskp.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        if (resPacName.compareTo("") != 0) {
            try {
                themeRes = pm.getResourcesForApplication(resPacName);
            } catch (Exception e) {
            }
            ;
            if (themeRes != null) {
                String[] backAndMaskAndFront = ThemeTools.getIconBackAndMaskResourceName(themeRes, resPacName);
                if (backAndMaskAndFront[0] != null)
                    intresiconback = themeRes.getIdentifier(backAndMaskAndFront[0], "drawable", resPacName);
                if (backAndMaskAndFront[1] != null)
                    intresiconmask = themeRes.getIdentifier(backAndMaskAndFront[1], "drawable", resPacName);
                if (backAndMaskAndFront[2] != null)
                    intresiconfront = themeRes.getIdentifier(backAndMaskAndFront[2], "drawable", resPacName);
            }
        }

        BitmapFactory.Options uniformOptions = new BitmapFactory.Options();
        uniformOptions.inScaled = false;
        uniformOptions.inDither = false;
        uniformOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Canvas origCanv;
        Canvas canvas;
        scaleFactor = ThemeTools.getScaleFactor(themeRes, resPacName);
        Bitmap back = null;
        Bitmap mask = null;
        Bitmap front = null;
        Bitmap scaledBitmap = null;
        Bitmap scaledOrig = null;
        Bitmap orig = null;

        if (resPacName.compareTo("") != 0 && themeRes != null) {
            try {
                if (intresiconback != 0)
                    back = BitmapFactory.decodeResource(themeRes, intresiconback, uniformOptions);
            } catch (Exception e) {
            }
            try {
                if (intresiconmask != 0)
                    mask = BitmapFactory.decodeResource(themeRes, intresiconmask, uniformOptions);
            } catch (Exception e) {
            }
            try {
                if (intresiconfront != 0)
                    front = BitmapFactory.decodeResource(themeRes, intresiconfront, uniformOptions);
            } catch (Exception e) {
            }
        }
        //theming vars-----------------------------------------------
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = true;

        for (int I = 0; I < pacs.size(); I++) {
            if (themeRes != null) {
                iconResource = null;
                intres = 0;
                iconResource = ThemeTools.getResourceName(themeRes, resPacName, "ComponentInfo{" + pacs.get(I).packageName + "/" + pacs.get(I).name + "}");
                if (iconResource != null) {
                    intres = themeRes.getIdentifier(iconResource, "drawable", resPacName);
                }

                if (intres != 0) {
                    pacs.get(I).icon = new BitmapDrawable(BitmapFactory.decodeResource(themeRes, intres, uniformOptions));
                } else {
                    orig = Bitmap.createBitmap(pacs.get(I).icon.getIntrinsicWidth(), pacs.get(I).icon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                    pacs.get(I).icon.setBounds(0, 0, pacs.get(I).icon.getIntrinsicWidth(), pacs.get(I).icon.getIntrinsicHeight());
                    pacs.get(I).icon.draw(new Canvas(orig));

                    scaledOrig = Bitmap.createBitmap(ICONSIZE, ICONSIZE, Bitmap.Config.ARGB_8888);
                    scaledBitmap = Bitmap.createBitmap(ICONSIZE, ICONSIZE, Bitmap.Config.ARGB_8888);
                    canvas = new Canvas(scaledBitmap);
                    if (back != null) {
                        canvas.drawBitmap(back, Tools.getResizedMatrix(back, ICONSIZE, ICONSIZE), p);
                    }

                    origCanv = new Canvas(scaledOrig);
                    orig = Tools.getResizedBitmap(orig, ((int) (ICONSIZE * scaleFactor)), ((int) (ICONSIZE * scaleFactor)));
                    origCanv.drawBitmap(orig, scaledOrig.getWidth() - (orig.getWidth() / 2) - scaledOrig.getWidth() / 2, scaledOrig.getWidth() - (orig.getWidth() / 2) - scaledOrig.getWidth() / 2, origP);

                    if (mask != null) {
                        origCanv.drawBitmap(mask, Tools.getResizedMatrix(mask, ICONSIZE, ICONSIZE), maskp);
                    }

                    if (back != null) {
                        canvas.drawBitmap(Tools.getResizedBitmap(scaledOrig, ICONSIZE, ICONSIZE), 0, 0, p);
                    } else
                        canvas.drawBitmap(Tools.getResizedBitmap(scaledOrig, ICONSIZE, ICONSIZE), 0, 0, p);

                    if (front != null)
                        canvas.drawBitmap(front, Tools.getResizedMatrix(front, ICONSIZE, ICONSIZE), p);

                    pacs.get(I).icon = new BitmapDrawable(scaledBitmap);
                }
            }
        }

        front = null;
        back = null;
        mask = null;
        scaledOrig = null;
        orig = null;
        scaledBitmap = null;
        canvas = null;
        origCanv = null;
        p = null;
        maskp = null;
        resPacName = null;
        iconResource = null;
        intres = 0;
    }

    public class PacReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            new LoadApps().execute();
            //setPacs(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        new Pac().cacheIcon();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);


        return super.onCreateOptionsMenu(menu);
    }
}
