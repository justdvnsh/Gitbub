package com.example.gitbub;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.renderscript.ScriptGroup;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView urlDisplay;
    TextView searchResults;
    TextView errorMessage;
    ProgressBar progressBar;
    final static String GITHUB_BASE_URL = "https://api.github.com/search/repositories";
    final static String PARAM_QUERY = "q";
    final static String PARAM_SORT = "sort";

    public URL buildUri(String text) {

        Uri builtUri = Uri.parse(GITHUB_BASE_URL).buildUpon().appendQueryParameter(PARAM_QUERY, text)
                        .appendQueryParameter(PARAM_SORT, "star").build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
            return url;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    };

    public void makeGithubSearchQuery() {

        String githubQuery = editText.getText().toString();
        URL url = buildUri(githubQuery);
        urlDisplay.setText(String.valueOf(url));

        String results = null;
        new DownloadManager().execute(url);

    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {

            InputStream inputStream = connection.getInputStream();

            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }

        return null;

    }

    public class DownloadManager extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... urls) {

            URL url = urls[0];
            String results = null;
            try {

                results = getResponseFromHttpUrl(url);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return results;

        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.INVISIBLE);
            if ( s != null && !s.equals("") ) {
                showJSONdata();
                searchResults.setText(s);
            } else {
                showErrorMessage();
            }
        }
    }

    public void showJSONdata() {
        searchResults.setVisibility(View.VISIBLE);
        errorMessage.setVisibility(View.INVISIBLE);
    }

    public void showErrorMessage() {
        searchResults.setVisibility(View.INVISIBLE);
        errorMessage.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editText = (EditText) findViewById(R.id.edit_text);
        urlDisplay = (TextView) findViewById(R.id.url_display);
        searchResults = (TextView) findViewById(R.id.search_results);
        errorMessage  = (TextView) findViewById(R.id.error_message);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search_button) {
            Toast.makeText(MainActivity.this, "You clicked the search bar", Toast.LENGTH_SHORT).show();
            makeGithubSearchQuery();
        }

        return super.onOptionsItemSelected(item);
    }
}
