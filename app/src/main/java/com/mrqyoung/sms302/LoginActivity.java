package com.mrqyoung.sms302;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import static java.text.DateFormat.getDateTimeInstance;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String VERSION = " ⓘ " + "2017-05-28T11:07:02.SunZ";  // YYYY-MM-DDTHH:mm:(ver).(w)Z
    //[V0.1.private]: "2017-04-30T18:45:01.SunZ";

    private UserLoginTask mAuthTask = null;

    private DataHelper dataHelper = null;

    private String helpText;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView helloView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        // init data helper
        this.dataHelper = new DataHelper(this);
        this.helpText = this.getString(R.string.global_help);
        // For receiver
        this.helloView = (TextView) findViewById(R.id.hello);
        this.helloView.setText(this.dataHelper.isNotReady() ? this.helpText : VERSION);
        // For decode the SMS
        this.helloView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = mEmailView.getText().toString().trim();
                if (s.isEmpty()) {return;}
                String msg = dataHelper.decode(s);
                if (msg.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "OUCH!", Toast.LENGTH_LONG).show();
                    return;
                }
                helloView.setText(msg);
                mEmailView.setText("");
            }
        });
    }



    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }




    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            if (mEmail.startsWith("@")) {
                return at(mEmail, mPassword);
            }

            boolean r = new MailSender(mEmail, mPassword).selfSend("Savvy?", VERSION);
            if (r) {
                dataHelper.saveEmail(mEmail);
                r = dataHelper.saveToken(mPassword);
            }
            return r;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Toast.makeText(LoginActivity.this, "Aye, sir!", Toast.LENGTH_LONG).show();
                if ("@SETKEY".equalsIgnoreCase(mEmail)) {
                    String hint = getString(R.string.key_changed_hint);
                    helloView.setText(hint);
                    mPasswordView.setError(hint);
                    dataHelper.refresh();
                }
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }


    private boolean at(String cmd, String param) {
        cmd = cmd.toUpperCase();
        switch (cmd) {
            case "@SETKEY":
                return this.dataHelper.keepTheKey(param);
            case "@TEST":
                SMS302IntentService.startActionFoo(LoginActivity.this, "10086", VERSION);
                String now = getDateTimeInstance().format(new Date());
                SMS302IntentService.startActionBaz(LoginActivity.this, "13800138000", now);
                return true;
            case "@SETSMTP":
                this.dataHelper.saveSMTPInfo(param);
                return true;
            case "@CLEAR":
                this.dataHelper.clearCache();
                return true;
            case "@HELP":
                this.helloView.setText(this.helpText);
                return true;
            case "@SETDEBUG":  // Caution: !!!Security Risk!!!
                if (this.dataHelper.loadEmail().equalsIgnoreCase(param)
                    && (this.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0 )
                    this.dataHelper.DEBUG = true;
            default:
                break;
        }
        return false;
    }
}

