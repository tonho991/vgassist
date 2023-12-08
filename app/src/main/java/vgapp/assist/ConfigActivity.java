package vgapp.assist;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.widget.CompoundButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import vgapp.assist.utils.AppConfig;
import androidx.documentfile.provider.DocumentFile;


public class ConfigActivity extends AppCompatActivity {
	
	private Toolbar _toolbar;
	private AppBarLayout _app_bar;
	private CoordinatorLayout _coordinator;
	
	private LinearLayout linear1;
	private CheckBox checkbox1;
	private CheckBox checkbox2;
	private CheckBox checkbox3;
	
	private AlertDialog.Builder dialog;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.config);
		initialize(_savedInstanceState);
		
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
			ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
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
		linear1 = findViewById(R.id.linear1);
		checkbox1 = findViewById(R.id.checkbox1);
		checkbox2 = findViewById(R.id.checkbox2);
		checkbox3 = findViewById(R.id.checkbox3);
		dialog = new AlertDialog.Builder(this);
		
		checkbox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton _param1, boolean _param2) {
				final boolean _isChecked = _param2;
				new AppConfig(getApplicationContext()).setReadGroups(_isChecked);
			}
		});
		
		checkbox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton _param1, boolean _param2) {
				final boolean _isChecked = _param2;
				if (!new AppConfig(getApplicationContext()).canPlayAudio()) {
					_showDialogWaring();
				}
			}
		});
		
		checkbox3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton _param1, boolean _param2) {
				final boolean _isChecked = _param2;
				new AppConfig(getApplicationContext()).setFemaleVoice(_isChecked);
			}
		});
	}
	
	private void initializeLogic() {
		setTitle("Configurações");
		checkbox1.setChecked(new AppConfig(this).canReadGroups());
		checkbox2.setChecked(new AppConfig(this).canPlayAudio());
		checkbox3.setChecked(new AppConfig(this).isFemaleVoice());
	}
	
	@Override
	protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
		super.onActivityResult(_requestCode, _resultCode, _data);
		if (_requestCode == 5000) {
				Uri uri = _data.getData();
				if (uri.toString().endsWith("WhatsApp%20Voice%20Notes")) {
						VGUtil.showMessage(getApplicationContext(), "Permissão Sucessedida!");
						getContentResolver().takePersistableUriPermission(uri, 1);
						ArrayList list = Utils.getWppAudioFiles(uri, getApplicationContext(), true);
						if (list.size() > 0) {
								String filePath= ((DocumentFile) list.get(0)).getParentFile().getParentFile().getUri().toString();
								new AppConfig(getApplicationContext()).setWppUri(filePath).setCanPlayAudio(true);
						}
				}
				else {
						VGUtil.showMessage(getApplicationContext(), "Permissão Mal Sucessedida!");
				}
		}
		
	}
	
	public void _makePermission() {
		String data = FileUtil.getExternalStorageDir().concat("/Android/media/com.whatsapp/WhatsApp/WhatsApp Voice Notes/");
		Intent intent = new Intent("android.intent.action.OPEN_DOCUMENT_TREE");
		intent.addFlags(1);
		intent.addCategory("android.intent.category.DEFAULT");
		intent.putExtra("android.provider.extra.INITIAL_URI", data);
		startActivityForResult(Intent.createChooser(intent, "Selecione o Diretório"), 5000);
	}
	
	
	public void _showDialogWaring() {
		dialog.setTitle("Atenção");
		dialog.setMessage("Precisamos que você ative a permissão de acesso a uma pasta específica , para a reprodução de áudio.");
		dialog.setPositiveButton("Aceitar", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface _dialog, int _which) {
				_makePermission();
			}
		});
		dialog.setNegativeButton("Recusar", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface _dialog, int _which) {
				
			}
		});
		dialog.create().show();
	}

}
