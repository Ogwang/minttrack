package com.ponyinc.minttrack;

import static com.ponyinc.minttrack.Constants.*;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;

public class AccountManager extends Activity {
	private Budget budget;
	private Spinner accountSpinner;
	private TextView nameText, balText;
	private Button saveButton, newAccount, editAccount;
	private CheckBox activateCb;
	private TextView tvAccountName, tvBalance, tvActive;
	
	/**mode for manage account*/
	private static final int Default = 1;
	/**mode for editing account	*/
	private static final int Update = 2;
	/**mode for creating new account*/
	private static final int New = 3;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.acctmgr);
		budget = new Budget(this);
		
		findViewById(R.id.new_acct).setOnClickListener(newAccountListener);
		findViewById(R.id.edit_acct).setOnClickListener(editAccountListener);
		findViewById(R.id.save_acct).setOnClickListener(saveAccountListener);
		
		setWidgets();
	
		fillAccountDropDown(accountSpinner);
		accountSpinner.setOnItemSelectedListener(spinnerListener);
	}
	
	private void setWidgets(){
		newAccount = (Button)findViewById(R.id.new_acct);
		editAccount = (Button)findViewById(R.id.edit_acct);
		accountSpinner = (Spinner)findViewById(R.id.acct_spinner);
		tvAccountName = (TextView)findViewById(R.id.tv_acctname);
		nameText = (EditText)findViewById(R.id.acct_name);
		tvBalance = (TextView)findViewById(R.id.tv_balance);
		balText = (EditText)findViewById(R.id.acct_bal);
		tvActive = (TextView)findViewById(R.id.tv_active);
		activateCb = (CheckBox)findViewById(R.id.active_acct);
		saveButton = (Button)findViewById(R.id.save_acct);
		setWidgetVisiblity(Default);
	}
	
	/**OnClickListener for New Account button**/
	View.OnClickListener newAccountListener = new View.OnClickListener()
	{	   
		@Override
		public void onClick(View v) 
		{
			setWidgetVisiblity(New);
		}
	};
	   
	/**OnClickListener for Edit Account button**/
	View.OnClickListener editAccountListener = new View.OnClickListener()
	{	   
		@Override
		public void onClick(View v) 
		{
			setWidgetVisiblity(Update);
		}
	};
   
	/**OnClickListener for Save Account button**/
	View.OnClickListener saveAccountListener = new View.OnClickListener()
	{	   
		@Override
		public void onClick(View v) 
		{	
			String name = String.valueOf(nameText.getText());
			String bal = String.valueOf(balText.getText());
			
			if((name.equals("") == false) && (bal.equals("") == false))
			{
				if(!newAccount.isEnabled())
				{
					if(activateCb.isChecked() == true)
						budget.addAccount(String.valueOf(nameText.getText()), Double.parseDouble(String.valueOf(balText.getText())), true);
					else
						budget.addAccount(String.valueOf(nameText.getText()), Double.parseDouble(String.valueOf(balText.getText())), false);
				}
				else if(!editAccount.isEnabled())
				{
					SimpleCursorAdapter s = (SimpleCursorAdapter) accountSpinner.getAdapter();
					Cursor spinCoursor = s.getCursor();
					
					spinCoursor.moveToPosition(accountSpinner.getSelectedItemPosition());
				
					budget.EditAccountName(spinCoursor.getInt(spinCoursor.getColumnIndex(_ID)), String.valueOf(nameText.getText()));
					budget.EditAccountTotal(spinCoursor.getInt(spinCoursor.getColumnIndex(_ID)), Double.parseDouble(String.valueOf(balText.getText())));
					
					if(activateCb.isChecked() == true)
						budget.ReactivateAccount(spinCoursor.getInt(spinCoursor.getColumnIndex(_ID)));
					
					else if(activateCb.isChecked() == false)
						budget.DeactivateAccount(spinCoursor.getInt(spinCoursor.getColumnIndex(_ID)));
				}
				fillAccountDropDown(accountSpinner);
				
				setWidgetVisiblity(Default);
			}
			else;
			//TODO add error message
		}
	};

	AdapterView.OnItemSelectedListener spinnerListener = new OnItemSelectedListener(){

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			
			SimpleCursorAdapter s = (SimpleCursorAdapter) accountSpinner.getAdapter();
			Cursor spinCoursor = s.getCursor();
			
	//		spinCoursor.moveToPosition(accountSpinner.getSelectedItemPosition());
			spinCoursor.moveToPosition(arg2);
			Cursor cursor = budget.getAccount(spinCoursor.getInt(spinCoursor.getColumnIndex(_ID)));
			cursor.moveToFirst();
			
			String name = cursor.getString(cursor.getColumnIndex(ACCOUNT_NAME));
			String amount = cursor.getString(cursor.getColumnIndex(ACCOUNT_TOTAL));
			String activity = cursor.getString(cursor.getColumnIndex(ACCOUNT_ACTIVE));
			
			nameText.setText(name);
			balText.setText(amount);
			if(activity.equals("active"))
				activateCb.setChecked(true);
			else
				activateCb.setChecked(false);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// nothing needed here
			
		}
	};
	
	/** Fill in Account drop down
	 * @param s Spinner to be used to fill drop down*/
	public void fillAccountDropDown(Spinner s) {
		Cursor cursor = budget.getAllAccounts();
		SimpleCursorAdapter s1 = new SimpleCursorAdapter(this,
				android.R.layout.simple_spinner_item, cursor, new String[] {
				ACCOUNT_NAME, _ID }, new int[] { android.R.id.text1,
						android.R.id.text2 });
		s1
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s.setAdapter(s1);
	}
	/**
	 * @param mode which visibility setting is used
	 * @see Default
	 * @see Update
	 * @see New
	 */
	private void setWidgetVisiblity(int mode)
	{
		switch(mode)
		{
			case(Default):
			{
				balText.setText("");
				nameText.setText("");
				activateCb.setChecked(true);
				
				newAccount.setEnabled(true);
				editAccount.setEnabled(true);
				
				accountSpinner.setVisibility(View.GONE);
				tvAccountName.setVisibility(View.GONE);
				nameText.setVisibility(View.GONE);
				tvBalance.setVisibility(View.GONE);
				balText.setVisibility(View.GONE);
				saveButton.setVisibility(View.GONE);
				tvActive.setVisibility(View.GONE);
				activateCb.setVisibility(View.GONE);
				break;
			}	
			case(Update):
			{
				spinnerListener.onItemSelected(accountSpinner, null, 0, 0);
				newAccount.setEnabled(true); //
				editAccount.setEnabled(false);//
				accountSpinner.setVisibility(View.VISIBLE);//
				tvAccountName.setVisibility(View.VISIBLE);
				nameText.setVisibility(View.VISIBLE);
				tvBalance.setVisibility(View.VISIBLE);
				balText.setVisibility(View.VISIBLE);
				saveButton.setVisibility(View.VISIBLE);
				tvActive.setVisibility(View.VISIBLE);
				activateCb.setVisibility(View.VISIBLE);
				break;
			}
			case(New):
			{
				balText.setText("");
				nameText.setText("");
				activateCb.setChecked(true);
				newAccount.setEnabled(false);//
				editAccount.setEnabled(true);//
				accountSpinner.setVisibility(View.GONE);//
				tvAccountName.setVisibility(View.VISIBLE);
				nameText.setVisibility(View.VISIBLE);
				tvBalance.setVisibility(View.VISIBLE);
				balText.setVisibility(View.VISIBLE);
				saveButton.setVisibility(View.VISIBLE);
				tvActive.setVisibility(View.VISIBLE);
				activateCb.setVisibility(View.VISIBLE);
				break;
			}
			default:
		}
				
		
	}
}
