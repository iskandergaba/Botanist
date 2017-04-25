// User's account page
// @author: Iskander Gaba
package com.scientists.happy.botanist.ui;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.scientists.happy.botanist.R;
import com.scientists.happy.botanist.data.DatabaseManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import za.co.riggaroo.materialhelptutorial.TutorialItem;
public class AccountActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "AccountActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private ImageView mAccountImageView;
    private TextView mNameTextView;
    private TextView mEmailTextView;
    private TextView mBotanistSinceTextView;
    private TextView mPlantsNumberTextView;
    private ProgressDialog mProgressDialog;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseManager mDatabase;
    /**
     * Launch the activity
     * @param savedInstanceState - current app state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        // Views
        mNameTextView = (TextView) findViewById(R.id.name);
        mEmailTextView = (TextView) findViewById(R.id.email);
        mBotanistSinceTextView = (TextView) findViewById(R.id.botanist_since);
        mPlantsNumberTextView = (TextView) findViewById(R.id.plants_number);
        mAccountImageView = (ImageView) findViewById(R.id.account_picture);
        TextView levelTextView = (TextView) findViewById(R.id.level_text_view);
        ImageView badge = (ImageView) findViewById(R.id.user_badge);
        ProgressBar levelProgressBar = (ProgressBar) findViewById(R.id.level_progress_bar);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = DatabaseManager.getInstance();
        mDatabase.showTutorial(this, loadTutorialItems(), false);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            /**
             * Handle the authentication state change
             * @param firebaseAuth - the user's authentication
             */
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                }
                else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().requestIdToken(getString(R.string.default_web_client_id)).build();
        // Build a GoogleApiClient with access to the Google Sign-In API and the options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
        double rating = mDatabase.getUserRating();
        if (rating < 0) {
            badge.setImageResource(R.drawable.badge_level_0);
            levelTextView.setText(getString(R.string.level_0));
            levelProgressBar.setProgress(0);
        }
        else if (rating < 0.35) {
            badge.setImageResource(R.drawable.badge_level_1);
            levelTextView.setText(getString(R.string.level_1));
            levelProgressBar.setProgress(35);
        }
        else if (rating < 0.75) {
            badge.setImageResource(R.drawable.badge_level_2);
            levelTextView.setText(getString(R.string.level_2));
            levelProgressBar.setProgress(75);
        }
        else {
            badge.setImageResource(R.drawable.badge_level_3);
            levelTextView.setText(getString(R.string.level_3));
            levelProgressBar.setProgress(100);
        }
        populateUserStatsChart();
    }

    /**
     * Action overflow menu
     * @param item - selected item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sign_out) {
            buildSignOutDialog().show();
            return true;
        }
        else if (id == R.id.action_delete_account) {
            buildRevokeAccessDialog().show();
        }
        else if (id == R.id.action_help) {
            mDatabase.showTutorial(this, loadTutorialItems(), true);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Create Action Overflow menu
     * @param menu - actions
     * @return Returns success code
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_account, menu);
        return true;
    }

    /**
     * The app was started
     */
    @Override
    public void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        }
        else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently. Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                /**
                 * Try to sign in the user silently
                 * @param googleSignInResult - the sign in attempt result
                 */
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
        mAuth.addAuthStateListener(mAuthListener);
    }

    /**
     * The activity was stopped
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * Handle back button press
     * @return Returns a success code
     */
    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }

    /**
     * The sign in activity resolved
     * @param requestCode - the request code
     * @param resultCode - sign in result
     * @param data - the sign in intent data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    /**
     * Handle a google signin
     * @param result - the result of the signin attempt
     */
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                mNameTextView.setText(acct.getDisplayName());
                mEmailTextView.setText(getString(R.string.email_fmt, acct.getEmail()));
                mBotanistSinceTextView.setText(getString(R.string.botanist_since_fmt, dateFormat.format(mDatabase.getBotanistSince())));
                mPlantsNumberTextView.setText(getString(R.string.plants_number_fmt, mDatabase.getPlantsNumber()));
                Glide.with(this).load(acct.getPhotoUrl()).asBitmap().centerCrop().into(new BitmapImageViewTarget(mAccountImageView) {
                    /**
                     * Set a glide image
                     * @param resource - the image to set
                     */
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        mAccountImageView.setImageDrawable(circularBitmapDrawable);
                    }
                });
            }
        }
    }

    /**
     * Sign the user out
     */
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            /**
             * Result of signout attempt
             * @param status - the status of the signout
             */
            @Override
            public void onResult(@NonNull Status status) {
                showProgressDialog();
                mAuth.signOut();
                mDatabase.resetMemberData();
                mDatabase.deleteAllReminders(AccountActivity.this);
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                hideProgressDialog();
                finish();
            }
        });
    }

    /**
     * Remove user's access to database on signout/timeout
     */
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            /**
             * Revoke access result
             * @param status - status of the revoke access attempt
             */
            @Override
            public void onResult(@NonNull Status status) {
                showProgressDialog();
                deleteUser();
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                hideProgressDialog();
                finish();
            }
        });
    }

    /**
     * Delete a user from the database
     */
    private void deleteUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            mDatabase.deleteUserRecords(userId);
            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                /**
                 * Delete the user task completed
                 * @param task - the completed task
                 */
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User account deleted.");
                    }
                }
            });
            mDatabase.resetMemberData();
            mDatabase.deleteAllReminders(AccountActivity.this);
        }
    }

    /**
     * Handle failed connection
     * @param connectionResult - failed connection result
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    /**
     * Signout dialog
     * @return Returns the dialog
     */
    private AlertDialog buildSignOutDialog() {
        // Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.sign_out_message).setTitle(R.string.sign_out_title);
        // Add the buttons
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            /**
             * Handle a click on the confirm button
             * @param dialog - the log out dialog
             * @param id - the user id
             */
            public void onClick(DialogInterface dialog, int id) {
                signOut();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            /**
             * Handle a click on the cancel button
             * @param dialog - the log out dialog
             * @param id - the user id
             */
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        // Get the AlertDialog from create()
        return builder.create();
    }

    /**
     * Revoke access dialog
     * @return Return the window warning user of access rekoving
     */
    private AlertDialog buildRevokeAccessDialog() {
        //Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.revoke_access_message).setTitle(R.string.revoke_access_title);
        // Add the buttons
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            /**
             * User clicked confirm
             * @param dialog - the revoke dialog
             * @param id - the user's id
             */
            public void onClick(DialogInterface dialog, int id) {
                revokeAccess();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            /**
             * User clicked cancel
             * @param dialog - the revoke dialog
             * @param id - the user's id
             */
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        // Get the AlertDialog from create()
        return builder.create();
    }

    /**
     * Show loading progress
     */
    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.show();
    }

    /**
     * Hide the progress message
     */
    private void hideProgressDialog() {
        if ((mProgressDialog != null) && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    /**
     * Fetch assets for the tutorial
     * @return - Returns the list of tutorial items
     */
    private ArrayList<TutorialItem> loadTutorialItems() {
        TutorialItem tutorialItem0 = new TutorialItem(getString(R.string.account_tutorial_title_0), getString(R.string.account_tutorial_contents_0),
                R.color.colorAccent, R.drawable.account_tutorial_0,  R.drawable.account_tutorial_0);
        TutorialItem tutorialItem1 = new TutorialItem(getString(R.string.account_tutorial_title_1_0), getString(R.string.account_tutorial_contents_1_0),
                R.color.colorAccent, R.drawable.account_tutorial_1,  R.drawable.account_tutorial_1);
        TutorialItem tutorialItem2 = new TutorialItem(getString(R.string.account_tutorial_title_1_1), getString(R.string.account_tutorial_contents_1_1),
                R.color.colorAccent, R.drawable.account_tutorial_2,  R.drawable.account_tutorial_2);
        TutorialItem tutorialItem3 = new TutorialItem(getString(R.string.account_tutorial_title_2), getString(R.string.account_tutorial_contents_2),
                R.color.colorAccent, R.drawable.account_tutorial_3,  R.drawable.account_tutorial_3);
        TutorialItem tutorialItem4 = new TutorialItem(getString(R.string.account_tutorial_title_3), getString(R.string.account_tutorial_contents_3),
                R.color.colorAccent, R.drawable.badge_level_0,  R.drawable.badge_level_0);
        TutorialItem tutorialItem5 = new TutorialItem(getString(R.string.account_tutorial_title_4), getString(R.string.account_tutorial_contents_4),
                R.color.colorAccent, R.drawable.badge_level_1,  R.drawable.badge_level_1);
        TutorialItem tutorialItem6 = new TutorialItem(getString(R.string.account_tutorial_title_5), getString(R.string.account_tutorial_contents_5),
                R.color.colorAccent, R.drawable.badge_level_2,  R.drawable.badge_level_2);
        TutorialItem tutorialItem7 = new TutorialItem(getString(R.string.account_tutorial_title_6), getString(R.string.account_tutorial_contents_6),
                R.color.colorAccent, R.drawable.badge_level_3,  R.drawable.badge_level_3);
        TutorialItem tutorialItem8 = new TutorialItem(getString(R.string.account_tutorial_title_7), getString(R.string.account_tutorial_contents_7),
                R.color.colorAccent, R.drawable.account_tutorial_4,  R.drawable.account_tutorial_4);
        TutorialItem tutorialItem9 = new TutorialItem(getString(R.string.account_tutorial_title_8), getString(R.string.account_tutorial_contents_8),
                R.color.colorAccent, R.drawable.account_tutorial_5,  R.drawable.account_tutorial_5);
        ArrayList<TutorialItem> tutorialItems = new ArrayList<>();
        tutorialItems.add(tutorialItem0);
        tutorialItems.add(tutorialItem1);
        tutorialItems.add(tutorialItem2);
        tutorialItems.add(tutorialItem3);
        tutorialItems.add(tutorialItem4);
        tutorialItems.add(tutorialItem5);
        tutorialItems.add(tutorialItem6);
        tutorialItems.add(tutorialItem7);
        tutorialItems.add(tutorialItem8);
        tutorialItems.add(tutorialItem9);
        return tutorialItems;
    }
  
    /**
     * Show user stats graph
     */
    private void populateUserStatsChart() {
        final String[] userStatsChartXAxisLabel = getResources().getStringArray(R.array.user_stats_x_axis_labels);
        BarChart chart = (BarChart) findViewById(R.id.user_stats_chart);
        chart.setTouchEnabled(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int v = (int) value;
                return userStatsChartXAxisLabel[v];
            }
        });
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(11f);
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
        chart.getAxisLeft().setGranularity(1);
        chart.getDescription().setEnabled(false);
        mDatabase.populateUserStatsChart(this, chart);
    }
}