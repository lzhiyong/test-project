package com.text.edit;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.Manifest;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.File;
import android.os.Environment;
import android.util.Log;
import com.text.edit.R;
import java.util.ArrayList;
import android.util.TypedValue;
import android.util.DisplayMetrics;
import android.graphics.Rect;
import java.lang.reflect.Field;
import android.text.SpannedString;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.graphics.Typeface;

public class MainActivity extends AppCompatActivity {
    
	private TextView mTextEdit;
	
	private TextBuffer mTextBuffer;
	
	private final String TAG = this.getClass().getSimpleName();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		
		Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().hide();
		
	
		mTextEdit = findViewById(R.id.mTextEdit);
		//mTextEdit.setTypeface(Typeface.MONOSPACE);
		
		((TextScrollView)findViewById(R.id.mScrollView)).setScrollListener(mTextEdit.getScrollListener());
		((TextHorizontalScrollView)findViewById(R.id.mHorizontalScrollView)).setScrollListener(mTextEdit.getScrollListener());
		
		mTextBuffer = new TextBuffer(mTextEdit.getPaint());
		mTextEdit.setTextBuffer(mTextBuffer);
		
		String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
		
		if(!hasPermission(permission)){
			applyPermission(permission);
		}
		
		String pathname = File.separator;
		
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			pathname = Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		
		openFile(pathname + File.separator + "Download/books/doupo.txt");

    }
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO: Implement this method
		super.onWindowFocusChanged(hasFocus);
	}
	
	
	public boolean hasPermission(String permission){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        else
            return true;
    }

    public void applyPermission(String permission){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(shouldShowRequestPermissionRationale(permission)){
                Toast.makeText(this, "request read sdcard permmission", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{permission},0);
        }
    }
	
	public void openFile(String pathname) {
        FileInputStream in = null;
		InputStreamReader reader = null;
        BufferedReader br = null;
		
        StringBuilder buf = new StringBuilder();
		ArrayList<Integer> indexList = new ArrayList<>();
        indexList.add(0);
		
		int lineCount = 0;
		float maxWidth = 0;
		
		try {
            in = new FileInputStream(new File(pathname));
			reader = new InputStreamReader(in, "utf-8");
            br = new BufferedReader(reader);
			String text = null;
			
			float width = 0;
			
			while((text = br.readLine()) != null) {
				++lineCount;
				width = mTextEdit.getPaint().measureText(text);
				if(width > maxWidth)
					maxWidth = width;
				buf.append(text + "\n");
				indexList.add(buf.length());
            }
			
            in.close();
			reader.close();
            br.close();
        } catch(Exception e) {
            e.printStackTrace();
			Log.e(TAG, e.getMessage());
        }
		
		// remove the last item 
		indexList.remove(lineCount);
		
		mTextBuffer.setBuffer(buf);
		mTextBuffer.setLineCount(lineCount);
		mTextBuffer.setIndexList(indexList);
		mTextBuffer.setTextMaxWidth((int)maxWidth);
		
		mTextEdit.setCursorPosition(0, 0);
    }
}