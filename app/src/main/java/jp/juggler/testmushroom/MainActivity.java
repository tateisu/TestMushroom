package jp.juggler.testmushroom;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
	
	private static final int REQUEST_CODE_MUSHROOM = 3;
	private static final String STATE_MUSHROOM_INPUT = "mushroom_input";
	private static final String STATE_MUSHROOM_START = "mushroom_start";
	private static final String STATE_MUSHROOM_END = "mushroom_end";
	int mushroom_input;
	int mushroom_start;
	int mushroom_end;
	
	EditText editText1;
	EditText editText2;
	
	@Override
	protected void onCreate( Bundle savedInstanceState ){
		super.onCreate( savedInstanceState );
		
		setContentView( R.layout.activity_main );
		editText1 = (EditText) findViewById( R.id.editText1 );
		editText2 = (EditText) findViewById( R.id.editText2 );
		
		findViewById( R.id.btnMushroom ).setOnClickListener( new View.OnClickListener() {
			@Override public void onClick( View v ){
				openMushroom();
			}
		} );
		
		if( savedInstanceState != null ){
			mushroom_input = savedInstanceState.getInt( STATE_MUSHROOM_INPUT, 0 );
			mushroom_start = savedInstanceState.getInt( STATE_MUSHROOM_START, 0 );
			mushroom_end = savedInstanceState.getInt( STATE_MUSHROOM_END, 0 );
		}
	}
	
	@Override
	protected void onSaveInstanceState( Bundle outState ){
		super.onSaveInstanceState( outState );
		
		outState.putInt( STATE_MUSHROOM_INPUT, mushroom_input );
		outState.putInt( STATE_MUSHROOM_START, mushroom_start );
		outState.putInt( STATE_MUSHROOM_END, mushroom_end );
		
	}

	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent data ){
		if( requestCode == REQUEST_CODE_MUSHROOM && resultCode == RESULT_OK ){
			String text = data.getStringExtra( "replace_key" );
			applyMushroomResult( text );
		}
		super.onActivityResult( requestCode, resultCode, data );
	}
	
	void openMushroom(){
		try{
			String text;
			if( editText1.hasFocus() ){
			//	hideKeyboard( this, editText1 );
				mushroom_input = 1;
				text = prepareMushroomText( editText1 );
			}else{
			//	hideKeyboard( this, editText2 );
				mushroom_input = 2;
				text = prepareMushroomText( editText2 );
			}
			Intent intent = new Intent( "com.adamrocker.android.simeji.ACTION_INTERCEPT" );
			intent.addCategory( "com.adamrocker.android.simeji.REPLACE" );
			intent.putExtra( "replace_key", text );
			
			Intent chooser = Intent.createChooser( intent, "select plugin" );
			
			startActivityForResult( chooser, REQUEST_CODE_MUSHROOM );
			
		}catch( Throwable ex ){
			ex.printStackTrace();
		}
	}
	
	@NonNull String prepareMushroomText( @NonNull EditText et ){
		mushroom_start = et.getSelectionStart();
		mushroom_end = et.getSelectionEnd();
		if( mushroom_end > mushroom_start ){
			return et.getText().toString().substring( mushroom_start, mushroom_end );
		}else{
			return "";
		}
	}
	
	void applyMushroomText( @NonNull EditText et, @NonNull String text ){
		String src = et.getText().toString();
		if( mushroom_start > src.length() ) mushroom_start = src.length();
		if( mushroom_end > src.length() ) mushroom_end = src.length();
		
		StringBuilder sb = new StringBuilder();
		sb.append( src.substring( 0, mushroom_start ) );
		int new_sel_start = sb.length();
		sb.append( text );
		int new_sel_end = sb.length();
		sb.append( src.substring( mushroom_end ) );
		et.setText( sb );
		et.setSelection( new_sel_end, new_sel_end );
	}
	
	private void applyMushroomResult( String text ){
		if( mushroom_input == 1 ){
			applyMushroomText( editText1, text );
		}else{
			applyMushroomText( editText2, text );
		}
	}
	
	public static void hideKeyboard( Context context, View v ){
		InputMethodManager imm = (InputMethodManager) context.getSystemService( Context.INPUT_METHOD_SERVICE );
		imm.hideSoftInputFromWindow( v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS );
	}
}
