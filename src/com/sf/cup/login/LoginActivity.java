package com.sf.cup.login;

import com.sf.cup.MainActivity;
import com.sf.cup.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class LoginActivity extends Activity {
	
	Button loginButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		TextView registerScreen = (TextView) findViewById(R.id.link_to_register);
 
        // Listening to register new account link
        registerScreen.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View v) {
                // Switching to Register screen
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
            }
        });
        
        loginButton=(Button)findViewById(R.id.btnLogin);
        loginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				
				if(true){
					Intent i = new Intent(getApplicationContext(), MainActivity.class);
	                startActivity(i);
				}else{
					
				}
			}
		});
	}
}
