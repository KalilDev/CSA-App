package com.kalil.csa;

// android.content
import android.content.Context;

// android.net
import android.net.Uri;

// android.support
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;

// android.os
import android.os.Bundle;

// android.text
import android.text.TextUtils;

// android.util
import android.util.Log;

// android.view
import android.view.LayoutInflater;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;

// android.widget
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

// java.util
import java.util.ArrayList;


// Needed by the Drawer
// Controls an individual item on the menu
class ListItem {
    String mTitle;
    String mSubtitle;
    int mIcon;

    ListItem(String title, String subtitle, int icon) {
        mTitle = title;
        mSubtitle = subtitle;
        mIcon = icon;
    }
}


// The actual drawer backend
class DrawerListAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<ListItem> mNavItems;

    DrawerListAdapter(Context context, ArrayList<ListItem> listItems) {
        mContext = context;
        mNavItems = listItems;
    }

    @Override
    public int getCount() {
        return mNavItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mNavItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.drawer_item, null);
        } else {
            view = convertView;
        }

        TextView titleView = (TextView) view.findViewById(R.id.Title);
        TextView subtitleView = (TextView) view.findViewById(R.id.subTitle);
        ImageView iconView = (ImageView) view.findViewById(R.id.Icon);

        titleView.setText(mNavItems.get(position).mTitle);
        subtitleView.setText(mNavItems.get(position).mSubtitle);
        iconView.setImageResource(mNavItems.get(position).mIcon);

        return view;
    }
}


/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends AppCompatActivity
    implements WebviewFragment.OnFragmentInteractionListener
    {
    // Log tag
    public static final String TAG = "CSA-Shitty-App";

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;

    // Drawer variables
    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    ArrayList<ListItem> mListItems = new ArrayList<>();

    // Hamburger menu
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle
        // If it returns true, then it has handled
        // the nav drawer indicator touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up title and layout
        setTitle(getString(R.string.name_login));
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);

        // Set up each drawer item
        mListItems.add(new ListItem("Inicio", "Tela inicial", R.mipmap.ic_launcher_round));
        mListItems.add(new ListItem("Configuracoes", "Mude suas configuraçoes", R.mipmap.ic_launcher_round));
        mListItems.add(new ListItem("Sobre", "Saiba mais sobre este app", R.mipmap.ic_launcher_round));

        // DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_view);

        // Populate the Navigtion Drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mListItems);
        mDrawerList.setAdapter(adapter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Drawer Item click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
                }
                /*
                * Called when a particular item from the navigation drawer
                * is selected.
                * */
                private void selectItemFromDrawer(int position) {
                    setTitle(mListItems.get(position).mTitle);

                    // Close the drawer
                    mDrawerLayout.closeDrawer(mDrawerPane);
                }
        });


        // Hamburger menu
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                invalidateOptionsMenu();
            }


            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Log.d(TAG, "onDrawerClosed: " + getTitle());

                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mUsernameSignInButton = (Button) findViewById(R.id.username_sign_in_button);
        mUsernameSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }


    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)){
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            mUsernameView.setError(getString(R.string.error_invalid_email));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Start webview activity.
            openWebview(username, password);
        }
    }


    // Start the webview activity and parse the username and password params
    // from the function as extra info on the intent.
    public void openWebview(String... param) {
        //Fragment webview = new WebviewFragment();
        FragmentTransaction webtransaction = getSupportFragmentManager().beginTransaction();
        webtransaction.replace(R.id.activity_view, WebviewFragment.newInstance(param[0], param[1]));
        webtransaction.addToBackStack(null);
        webtransaction.commit();
    }


    // Function that validates the username.
    private boolean isUsernameValid(String username) { return username.contains("CSA") && username.contains("-"); }


    // Function that validates the password.
    private boolean isPasswordValid(String password) { return password.length() > 6; }


    @Override
    public void onBackPressed() {
        // Kill myself
        moveTaskToBack(true);
        finish();
    }


    @Override
    public void onFragmentInteraction(Uri uri){
        // Google wants me to implement this.
        // Shame on them.
    }
}

