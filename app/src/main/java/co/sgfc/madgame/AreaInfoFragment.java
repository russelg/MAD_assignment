package co.sgfc.madgame;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;


public class AreaInfoFragment extends Fragment {
    GameData gameData = GameData.getInstance();
    TextView textAreaType;
    EditText textAreaDescription;
    CheckBox checkStarred;
    BroadcastReceiver mReceiver;
    CompoundButton.OnCheckedChangeListener checkListener;
    boolean isRegistered = false;
    Area areaToUse = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_area_info, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Status Bar elements
        textAreaDescription = view.findViewById(R.id.textAreaDescription);
        textAreaType = view.findViewById(R.id.textAreaType);
        checkStarred = view.findViewById(R.id.checkStarred);

        checkListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                Area area = getArea();
                if (area.isStarred() != isChecked) {
                    area.setStarred(isChecked);
                    gameData.updateArea(area);
                    gameData.broadcastUpdate();
                }
            }
        };
        checkStarred.setOnCheckedChangeListener(checkListener);

        textAreaDescription.setClickable(true);
        textAreaDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                Area area = getArea();

                if (!s.toString().equals(area.getDescription())) {
                    area.setDescription(s.toString());
                    gameData.updateArea(area);
                    gameData.broadcastUpdate();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        textAreaDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View view = LayoutInflater.from(getContext())
                        .inflate(R.layout.dialog_description_edit, (ViewGroup) getView(), false);
                final EditText input = view.findViewById(R.id.description);

                // Build dialog to ask for the new description
                builder.setTitle(R.string.area_description);
                builder.setView(view);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        textAreaDescription.setText(input.getText());
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                final AlertDialog dialog = builder.create();

                input.setText(textAreaDescription.getText());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setImeOptions(EditorInfo.IME_ACTION_DONE);
                input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        textAreaDescription.setText(v.getText());
                        dialog.dismiss();
                        return false;
                    }
                });

                dialog.show();

                input.requestFocus();
                input.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
                        inputMethodManager.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
                    }
                }, 500);
            }
        });

        mReceiver = gameData.registerBroadcastReceiver(view.getContext(), new Runnable() {
            @Override
            public void run() {
                //if (areaToUse == null)
                updateUI();
            }
        });
        isRegistered = true;

        updateUI();
    }

    public void updateUI() {
        Area area = getArea();

        textAreaDescription.setText(area.getDescription());
        textAreaType.setText(getString(R.string.area_type,
                getString(area.isTown() ? R.string.area_town : R.string.area_wilderness)));

        // if the listener isn't disabled at this point it'll cause an infinite loop
        // due to the broadcast updating UI
        checkStarred.setOnCheckedChangeListener(null);
        checkStarred.setChecked(area.isStarred());
        checkStarred.setOnCheckedChangeListener(checkListener);
    }

    public void setAreaToUse(Area areaToUse) {
        this.areaToUse = areaToUse;
    }

    public Area getArea() {
        Area area = areaToUse;
        if (areaToUse == null)
            area = gameData.getPlayerArea();
        return area;
    }

    @Override
    public void onPause() {
        gameData.unregisterBroadcastReceiver(getContext(), mReceiver);
        super.onPause();
    }
}
