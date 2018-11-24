package com.starrealmtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.starrealmtracker.calc.Calculator;
import com.starrealmtracker.calc.CalculatorException;

public class MainActivity extends AppCompatActivity implements View.OnLayoutChangeListener, EditValueDialogFragment.EditValueListener {

    private static final String AUTHORITY = "authority";
    private static final String TRADE = "trade";
    private static final String COMBAT = "combat";
    private static final String EDIT_AUTHORITY_DIALOG = "edit authority dialog";
    private static final String EDIT_TRADE_DIALOG = "edit trade dialog";
    private static final String EDIT_COMBAT_DIALOG = "edit combat dialog";

    private static final int SETTINGS_REQUEST = 1;

    private boolean onlyAuthority;
    private Tracker tracker;
    private Calculator calculator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPreferences.getBoolean(SettingsActivity.KEY_PREF_KEEP_SCREEN_ON, false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        onlyAuthority = sharedPreferences.getBoolean(SettingsActivity.KEY_PREF_ONLY_AUTHORITY, false);
        if(onlyAuthority) {
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setContentView(R.layout.activity_main_authority_only_landscape);
            } else {
                setContentView(R.layout.activity_main_authority_only);
            }
        } else {
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setContentView(R.layout.activity_main_landscape);
            } else {
                setContentView(R.layout.activity_main);
            }
            findViewById(R.id.image_combat).addOnLayoutChangeListener(this);
            findViewById(R.id.image_trade).addOnLayoutChangeListener(this);
        }
        findViewById(R.id.image_authority).addOnLayoutChangeListener(this);

        sharedPreferences = getPreferences(MODE_PRIVATE);
        if(sharedPreferences.contains(AUTHORITY)) {
            tracker = new Tracker(sharedPreferences.getInt(AUTHORITY, -1), sharedPreferences.getInt(TRADE, -1), sharedPreferences.getInt(COMBAT, -1));
        } else {
            tracker = new Tracker();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(AUTHORITY, tracker.getAuthority());
        editor.putInt(TRADE, tracker.getTrade());
        editor.putInt(COMBAT, tracker.getCombat());
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.mi_new:
                tracker = new Tracker();
                TextView tv = (TextView)findViewById(R.id.text_authority);
                tv.setText(""+tracker.getAuthority());
                if(!onlyAuthority) {
                    tv = (TextView)findViewById(R.id.text_trade);
                    tv.setText(""+tracker.getTrade());
                    tv = (TextView)findViewById(R.id.text_combat);
                    tv.setText(""+tracker.getCombat());
                }
                return true;
            case R.id.mi_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent, SETTINGS_REQUEST);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case SETTINGS_REQUEST:
                if(resultCode == RESULT_OK) {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                    boolean onlyAuthority = sharedPreferences.getBoolean(SettingsActivity.KEY_PREF_ONLY_AUTHORITY, false);
                    if(this.onlyAuthority != onlyAuthority) {
                        this.onlyAuthority = onlyAuthority;
                        if(onlyAuthority) {
                            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                setContentView(R.layout.activity_main_authority_only_landscape);
                            } else {
                                setContentView(R.layout.activity_main_authority_only);
                            }
                        } else {
                            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                setContentView(R.layout.activity_main_landscape);
                            } else {
                                setContentView(R.layout.activity_main);
                            }
                            findViewById(R.id.image_combat).addOnLayoutChangeListener(this);
                            findViewById(R.id.image_trade).addOnLayoutChangeListener(this);
                        }
                        findViewById(R.id.image_authority).addOnLayoutChangeListener(this);
                    }
                    if(sharedPreferences.getBoolean(SettingsActivity.KEY_PREF_KEEP_SCREEN_ON, false)) {
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    } else {
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    }
                }
                calculator = null;
                break;
        }
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if(left != oldLeft || right != oldRight || top != oldTop || bottom != oldBottom) {
            String test, text;
            TextView txt;
            int width, height;
            switch (v.getId()) {
                case R.id.image_authority:
                    test = "00";
                    width = v.getWidth() / 2;
                    height = v.getHeight() / 2;
                    txt = (TextView) findViewById(R.id.text_authority);
                    text = ""+tracker.getAuthority();
                    break;
                case R.id.image_trade:
                    test = "00";
                    width = 2 * v.getWidth() / 3;
                    height = 2 * v.getHeight() / 3;
                    txt = (TextView) findViewById(R.id.text_trade);
                    text = ""+tracker.getTrade();
                    break;
                case R.id.image_combat:
                    test = "00";
                    width = 2 * v.getWidth() / 3;
                    height = 2 * v.getHeight() / 3;
                    txt = (TextView) findViewById(R.id.text_combat);
                    text = ""+tracker.getCombat();
                    break;
                default:
                    return;
            }
            Paint p = txt.getPaint();
            float size = p.getTextSize();
            Rect bounds = new Rect();
            p.getTextBounds(test, 0, test.length(), bounds);
            while (bounds.width() < width && bounds.height() < height) {
                size++;
                p.setTextSize(size);
                p.getTextBounds(test, 0, test.length(), bounds);
            }
            size--;
            p.setTextSize(size);
            txt.setText(text);
        }
    }

    public void btnPress(View view) {
        switch(view.getId()) {
            case R.id.btn_authority_up:
                tracker.changeAuthority(1);
                ((TextView)findViewById(R.id.text_authority)).setText("" + tracker.getAuthority());
                break;
            case R.id.btn_authority_down:
                tracker.changeAuthority(-1);
                ((TextView)findViewById(R.id.text_authority)).setText("" + tracker.getAuthority());
                break;
            case R.id.btn_trade_up:
                tracker.changeTrade(1);
                ((TextView)findViewById(R.id.text_trade)).setText("" + tracker.getTrade());
                break;
            case R.id.btn_trade_down:
                tracker.changeTrade(-1);
                ((TextView)findViewById(R.id.text_trade)).setText("" + tracker.getTrade());
                break;
            case R.id.btn_combat_up:
                tracker.changeCombat(1);
                ((TextView)findViewById(R.id.text_combat)).setText("" + tracker.getCombat());
                break;
            case R.id.btn_combat_down:
                tracker.changeCombat(-1);
                ((TextView)findViewById(R.id.text_combat)).setText("" + tracker.getCombat());
                break;
            case R.id.btn_reset:
                tracker.reset();
                ((TextView)findViewById(R.id.text_trade)).setText("" + tracker.getTrade());
                ((TextView)findViewById(R.id.text_combat)).setText("" + tracker.getCombat());
                break;
            case R.id.image_authority:
                DialogFragment frag = new EditValueDialogFragment();
                Bundle args = new Bundle();
                args.putInt(EditValueDialogFragment.IMAGE, R.drawable.authority);
                args.putString(EditValueDialogFragment.TITLE, "Edit Authority");
                args.putInt(EditValueDialogFragment.INITAL_VALUE, tracker.getAuthority());
                frag.setArguments(args);
                frag.show(getSupportFragmentManager(), EDIT_AUTHORITY_DIALOG);
                break;
            case R.id.image_trade:
                frag = new EditValueDialogFragment();
                args = new Bundle();
                args.putInt(EditValueDialogFragment.IMAGE, R.drawable.trade);
                args.putString(EditValueDialogFragment.TITLE, "Edit Trade");
                args.putInt(EditValueDialogFragment.INITAL_VALUE, tracker.getTrade());
                frag.setArguments(args);
                frag.show(getSupportFragmentManager(), EDIT_TRADE_DIALOG);
                break;
            case R.id.image_combat:
                frag = new EditValueDialogFragment();
                args = new Bundle();
                args.putInt(EditValueDialogFragment.IMAGE, R.drawable.combat);
                args.putString(EditValueDialogFragment.TITLE, "Edit Combat");
                args.putInt(EditValueDialogFragment.INITAL_VALUE, tracker.getCombat());
                frag.setArguments(args);
                frag.show(getSupportFragmentManager(), EDIT_COMBAT_DIALOG);
                break;
        }
    }

    @Override
    public void valueChanged(String tag, int value) {
        switch(tag) {
            case EDIT_AUTHORITY_DIALOG:
                tracker.setAuthority(value);
                ((TextView)findViewById(R.id.text_authority)).setText("" + value);
                break;
            case EDIT_TRADE_DIALOG:
                tracker.setTrade(value);
                ((TextView)findViewById(R.id.text_trade)).setText("" + value);
                break;
            case EDIT_COMBAT_DIALOG:
                tracker.setCombat(value);
                ((TextView)findViewById(R.id.text_combat)).setText("" + value);
                break;
        }
    }

    public void calcButton(View view) {
        TextView expression = (TextView)findViewById(R.id.text_expression);
        TextView result = (TextView)findViewById(R.id.text_result);
        switch(view.getId()) {
            case R.id.btn_0:
                expression.append("0");
                break;
            case R.id.btn_1:
                expression.append("1");
                break;
            case R.id.btn_2:
                expression.append("2");
                break;
            case R.id.btn_3:
                expression.append("3");
                break;
            case R.id.btn_4:
                expression.append("4");
                break;
            case R.id.btn_5:
                expression.append("5");
                break;
            case R.id.btn_6:
                expression.append("6");
                break;
            case R.id.btn_7:
                expression.append("7");
                break;
            case R.id.btn_8:
                expression.append("8");
                break;
            case R.id.btn_9:
                expression.append("9");
                break;
            case R.id.btn_plus:
                expression.append("+");
                break;
            case R.id.btn_minus:
                expression.append("-");
                break;
            case R.id.btn_backspace:
                String s = expression.getText().toString();
                if(!s.isEmpty()) {
                    expression.setText(s.substring(0, s.length() - 1));
                }
                break;
            case R.id.btnCalcOk:
                if(calculator != null) {
                    if (calculator.hasResult()) {
                        int value = (int) calculator.getResult();
                        if (value == calculator.getResult()) {
                            expression.setText("");
                            result.setText("");
                            calculator = null;
                            tracker.setAuthority(value);
                            ((TextView) findViewById(R.id.text_authority)).setText("" + value);
                        } else {
                            MessageDialogFragment frag = new MessageDialogFragment();
                            Bundle args = new Bundle();
                            args.putString(MessageDialogFragment.TITLE, "Error");
                            args.putString(MessageDialogFragment.MESSAGE, "Value must be an integer.");
                            frag.setArguments(args);
                            frag.show(getSupportFragmentManager(), "EditValueDialogFragment.Message");
                        }
                    } else {
                        try {
                            calculator.finalCalculation();
                            int value = (int) calculator.getResult();
                            if (value == calculator.getResult()) {
                                expression.setText("");
                                result.setText("");
                                calculator = null;
                                tracker.setAuthority(value);
                                ((TextView) findViewById(R.id.text_authority)).setText("" + value);
                            } else {
                                MessageDialogFragment frag = new MessageDialogFragment();
                                Bundle args = new Bundle();
                                args.putString(MessageDialogFragment.TITLE, "Error");
                                args.putString(MessageDialogFragment.MESSAGE, "Value must be an integer.");
                                frag.setArguments(args);
                                frag.show(getSupportFragmentManager(), "EditValueDialogFragment.Message");
                            }
                        } catch (CalculatorException eRef) {
                            MessageDialogFragment frag = new MessageDialogFragment();
                            Bundle args = new Bundle();
                            args.putString(MessageDialogFragment.TITLE, "Error");
                            args.putString(MessageDialogFragment.MESSAGE, eRef.getMessage());
                            frag.setArguments(args);
                            frag.show(getSupportFragmentManager(), "EditValueDialogFragment.Message");
                        }
                    }
                }
                return;
            case R.id.btnCalcCancel:
                calculator = null;
                expression.setText("");
                result.setText("");
                return;
        }
        if(calculator == null) {
            calculator = new Calculator();
        }
        TextView authority = (TextView)findViewById(R.id.text_authority);
        result.setText(calculator.partialCalculation(authority.getText().toString() + expression.getText().toString()));
    }
}
