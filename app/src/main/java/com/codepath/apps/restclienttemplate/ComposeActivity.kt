package com.codepath.apps.restclienttemplate

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class ComposeActivity : AppCompatActivity() {

    lateinit var etCompose: EditText
    lateinit var btnTweet: Button
    lateinit var tvTweetLength: TextView
    lateinit var client: TwitterClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        etCompose = findViewById(R.id.etTweetCompose)
        btnTweet = findViewById(R.id.btnTweet)
        tvTweetLength = findViewById(R.id.tvTweetLength)


        client = TwitterApplication.getRestClient(this)

        etCompose.addTextChangedListener(object: TextWatcher {

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (s.length > 280){
                    //Red
                    tvTweetLength.setTextColor(Color.parseColor("#FF0000"))
                }
                else{
                    tvTweetLength.setTextColor(Color.parseColor("#000000"))
                }
                tvTweetLength.text = "${s.length.toString()}/280"
            }
        })

        btnTweet.setOnClickListener {
            val tweetContent = etCompose.text.toString()

            //1. Make sure the tweet isn't empty

            //2. Make sure the tweet is under character count

            if (tweetContent.isEmpty()){
                Toast.makeText(this, "Empty tweets not allowed!", Toast.LENGTH_SHORT).show()
                // Look into displaying snackBar message
            }
            else if (tweetContent.length > 280){
                Toast.makeText(this, "Tweet is too long! Limit is 280 characters.", Toast.LENGTH_SHORT).show()

            }
            else{
                client.publichTweet(tweetContent, object : JsonHttpResponseHandler(){
                    override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                        Log.i(TAG, "Successfully published tweet")

                        val tweet = Tweet.fromJson(json.jsonObject)

                        val intent = Intent()
                        intent.putExtra("tweet", tweet)
                        setResult(RESULT_OK, intent)
                        finish()
                    }

                    override fun onFailure(
                        statusCode: Int,
                        headers: Headers?,
                        response: String?,
                        throwable: Throwable?
                    ) {
                        Log.e(TAG, "Failed to publish tweet", throwable)
                    }

                })
            }
        }
    }

    companion object{
        var TAG = "ComposeActivity"
    }
}