package vgapp.assist;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
	
	private Toolbar _toolbar;
	private AppBarLayout _app_bar;
	private CoordinatorLayout _coordinator;
	private BroadcastReceiver notificationReceiver;
	private String data = "";
	private boolean showSecondPermission = false;
	
	private ArrayList<String> list = new ArrayList<>();
	
	private LinearLayout linear_accept_perms;
	private LinearLayout linear_home;
	private TextView textview1;
	private Button button2;
	private LinearLayout linear2;
	private LinearLayout linear3;
	private TextView textview3;
	private Switch serviceStatus;
	private TextView textview7;
	private LinearLayout linear5;
	private TextView textview6;
	private LinearLayout linear4;
	private Button button3;
	private ImageView imageview1;
	private SeekBar seekbar2;
	private TextView textview5;
	private SeekBar seekbar1;
	private TextView textview4;
	
	private SharedPreferences sp;
	private Intent intent = new Intent();
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.main);
		initialize(_savedInstanceState);
		
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
		|| ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
			if(Build.VERSION.SDK_INT >= 30)
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 1000);
            else
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
		} else {
			initializeLogic();
		}
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1000) {
			initializeLogic();
		}
	}
	
	private void initialize(Bundle _savedInstanceState) {
		_app_bar = findViewById(R.id._app_bar);
		_coordinator = findViewById(R.id._coordinator);
		_toolbar = findViewById(R.id._toolbar);
		setSupportActionBar(_toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _v) {
				onBackPressed();
			}
		});
		linear_accept_perms = findViewById(R.id.linear_accept_perms);
		linear_home = findViewById(R.id.linear_home);
		textview1 = findViewById(R.id.textview1);
		button2 = findViewById(R.id.button2);
		linear2 = findViewById(R.id.linear2);
		linear3 = findViewById(R.id.linear3);
		textview3 = findViewById(R.id.textview3);
		serviceStatus = findViewById(R.id.serviceStatus);
		textview7 = findViewById(R.id.textview7);
		linear5 = findViewById(R.id.linear5);
		textview6 = findViewById(R.id.textview6);
		linear4 = findViewById(R.id.linear4);
		button3 = findViewById(R.id.button3);
		imageview1 = findViewById(R.id.imageview1);
		seekbar2 = findViewById(R.id.seekbar2);
		textview5 = findViewById(R.id.textview5);
		seekbar1 = findViewById(R.id.seekbar1);
		textview4 = findViewById(R.id.textview4);
		sp = getSharedPreferences("config", Activity.MODE_PRIVATE);
		
		button2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				if (textview1.getText().toString().contains("plano")) {
					Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:vgapp.assist"));
						startActivityForResult(intent, 6500);
				}
				else {
					startActivityForResult(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS), 6000);
				}
			}
		});
		
		serviceStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton _param1, boolean _param2) {
				final boolean _isChecked = _param2;
				new AppConfig(getApplicationContext()).setServiceOn(_isChecked);
			}
		});
		
		button3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				
			}
		});
		
		imageview1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				intent.setAction(Intent.ACTION_VIEW);
				intent.setClass(getApplicationContext(), ConfigActivity.class);
				startActivity(intent);
			}
		});
		
		seekbar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar _param1, int _param2, boolean _param3) {
				
				new AppConfig(getApplicationContext()).setVoiceSpeed(pitch);
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar _param1) {
				
			}
			
			@Override
			public void onStopTrackingTouch(SeekBar _param2) {
				
			}
		});
		
		seekbar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar _param1, int _param2, boolean _param3) {
				final int _progressValue = _param2;
				new AppConfig(getApplicationContext()).setVoiceVolume((float)_progressValue);
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar _param1) {
				
			}
			
			@Override
			public void onStopTrackingTouch(SeekBar _param2) {
				
			}
		});
	}
	
	private void initializeLogic() {
		
		button3.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)5, 0xFF1976D2));
		serviceStatus.setChecked(new AppConfig(getApplicationContext()).canReadNotifications());
		getSupportActionBar().hide();
		NotificationManager n = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
		if(n.isNotificationPolicyAccessGranted()){
			getSupportActionBar().show();
			linear_accept_perms.setVisibility(View.GONE);
		} else 
		_showPermissionScreen();
	}
	
	@Override
	protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
		super.onActivityResult(_requestCode, _resultCode, _data);
		if (_requestCode == 6000) {
				NotificationManager n = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
				if(n.isNotificationPolicyAccessGranted()){
						showSecondPermission = true;
						_showPermissionScreen();
				} else {
						VGUtil.showMessage(getApplicationContext(), "Permissão negada");
				}
		}
		if (_requestCode == 6500) {
				linear_accept_perms.setVisibility(View.GONE);
				getSupportActionBar().show();
		}
		
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Intent intent = new Intent(this, vgapp.assist.services.FloatWindow.class);
			stopService(intent);
	}
	public void _showPermissionScreen() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			textview1.setText("Seu dispositivo não possui a permissão de acesso às\nnotificações. Lamentamos.");
			button2.setVisibility(View.GONE);
			return;
		}
		if (showSecondPermission) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
				textview1.setText("Para que o app não feche, precisamos da sua permissão\npara que execute em segundo plano.");
			} else {
					Intent intent = new Intent(this, vgapp.assist.services.FloatWindow.class);
					startService(intent);
				
				linear_accept_perms.setVisibility(View.GONE);
			}
		}
	}
	
	

	

}
