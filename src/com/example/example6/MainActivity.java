package com.example.example6;

import java.util.Locale;

import android.os.Bundle;
import android.app.Activity;
import android.app.TabActivity;
import android.content.ClipData.Item;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {
	Cursor model = null;
	RestaurantAdapter adapter = null;
	EditText name = null;
	EditText address = null;
	EditText notes = null;
	RadioGroup types = null;
	RestaurantHelper helper = null;
	Integer _id = null;
	Button save;
	Button delete;
	MenuItem english;
	MenuItem spanish;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		helper = new RestaurantHelper(this);

		name = (EditText) findViewById(R.id.name);
		address = (EditText) findViewById(R.id.addr);
		notes = (EditText) findViewById(R.id.notes);
		types = (RadioGroup) findViewById(R.id.types);

		save = (Button) findViewById(R.id.save);
		delete = (Button) findViewById(R.id.delete);
		
		save.setOnClickListener(onSave);
		delete.setOnClickListener(onDelete);
		
		ListView list = (ListView) findViewById(R.id.restaurants);

		model = helper.getAll();
		startManagingCursor(model);
		adapter = new RestaurantAdapter(model);
		list.setAdapter(adapter);
		
		TabHost.TabSpec spec = getTabHost().newTabSpec("tag1");
		
		spec.setContent(R.id.restaurants);
		spec.setIndicator(getString(R.string.list), getResources().getDrawable(R.drawable.list));
		getTabHost().addTab(spec);

		spec = getTabHost().newTabSpec("tag2");
		spec.setContent(R.id.details);
		spec.setIndicator(getString(R.string.details),
				getResources().getDrawable(R.drawable.restaurant));
		getTabHost().addTab(spec);

		getTabHost().setCurrentTab(0);

		list.setOnItemClickListener(onListClick);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();

		helper.close();
	}
	
	private View.OnClickListener onEnglish = new View.OnClickListener() {
		@Override
		public void onClick(View v) {			
			//Locale locale = new Locale("en");
			//Locale.setDefault(locale);
		}
	};
	
	private View.OnClickListener onSpanish = new View.OnClickListener() {
		@Override
		public void onClick(View v) {			
			//Locale locale = new Locale("es");
			//Locale.setDefault(locale);
		}
	};
	
	private View.OnClickListener onSave = new View.OnClickListener() {
		public void onClick(View v) {
			String type = null;

			switch (types.getCheckedRadioButtonId()) {
			case R.id.sit_down:
				type = "sit_down";
				break;
			case R.id.take_out:
				type = "take_out";
				break;
			case R.id.delivery:
				type = "delivery";
				break;
			}
			String message = name.getText().toString();
			if (_id == null) {
				helper.insert(name.getText().toString(), address.getText()
							.toString(), type, notes.getText().toString());
				message += " inserted successfully";
			} else {
				helper.update(_id, name.getText().toString(), address.getText()
						.toString(), type, notes.getText().toString());
				message += " updated successfully";
			}
			
			_id = null;
			name.setText("");
			address.setText("");
			types.check(R.id.sit_down);
			notes.setText("");
			
			getTabHost().setCurrentTab(0);
			InputMethodManager imm = (InputMethodManager)getSystemService(
				      Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(name.getWindowToken(), 0);
			Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
			
			model.requery();
		}
	};
	
	private View.OnClickListener onDelete = new View.OnClickListener() {
		public void onClick(View v) {			
			helper.delete(_id);
			
			String message = name.getText() + " deleted successfully!";
			
			_id = null;
			name.setText("");
			address.setText("");
			types.check(R.id.sit_down);
			notes.setText("");
			
			getTabHost().setCurrentTab(0);
			Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
			
			model.requery();
		}
	};

	private AdapterView.OnItemClickListener onListClick = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			model.moveToPosition(position);
			_id = helper.getId(model);
			name.setText(helper.getName(model));
			address.setText(helper.getAddress(model));
			notes.setText(helper.getNotes(model));

			if (helper.getType(model).equals(getString(R.id.sit_down))) {
				types.check(R.id.sit_down);
			} else if (helper.getType(model).equals(getString(R.id.take_out))) {
				types.check(R.id.take_out);
			} else {
				types.check(R.id.delivery);
			}

			getTabHost().setCurrentTab(1);
		}
	};

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		MenuItem theItem = item;
		switch(item.getItemId()) {
		case R.id.spanish:
			toSpanish();
			return true;
		case R.id.english:
			toEnglish();
			return true;
		}
		
		return true;
	}
	
	public void toSpanish() {
		Locale locale = new Locale("es");
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		getApplicationContext().getResources().updateConfiguration(config,
		      getApplicationContext().getResources().getDisplayMetrics());
		recreate();
	}
	
	public void toEnglish() {
		Locale locale = new Locale("en");
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config,
		      getBaseContext().getResources().getDisplayMetrics());
		recreate();
	}

	//************************ The Restaurant Adapter ********************************//
	public class RestaurantAdapter extends CursorAdapter {
		public RestaurantAdapter(Cursor c) {
			super(MainActivity.this, c, true);
		}

		@Override
		public void bindView(View row, Context context, Cursor c) {
			RestaurantHolder holder = (RestaurantHolder) row.getTag();
			holder.populateFrom(c, helper);
		}

		@Override
		public View newView(Context context, Cursor c, ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.row, parent, false);
			RestaurantHolder holder = new RestaurantHolder(row);

			row.setTag(holder);

			return (row);
		}
	}
	
	//************************ THe Restaurant Holder **********************************//
	static class RestaurantHolder {
		private TextView name = null;
		private TextView address = null;
		private ImageView icon = null;
		private View row = null;
		
		RestaurantHolder(View row) {
			this.row = row;
			name = (TextView) row.findViewById(R.id.title);
			address = (TextView) row.findViewById(R.id.address);
			icon = (ImageView) row.findViewById(R.id.icon);
		}
		void populateFrom(Cursor c, RestaurantHelper helper) {
			name.setText(helper.getName(c));
			address.setText(helper.getAddress(c));

			if (helper.getType(c).equals("sit_down")) {
				icon.setImageResource(R.drawable.ball_red);
			} else if (helper.getType(c).equals("take_out")) {
				icon.setImageResource(R.drawable.ball_yellow);
			} else {
				icon.setImageResource(R.drawable.ball_green);
			}
		}
	}
}
