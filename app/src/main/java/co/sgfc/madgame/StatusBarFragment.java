package co.sgfc.madgame;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class StatusBarFragment extends Fragment {
    // Status Bar elements
    TextView textCash, textHealth, textEquipmentMass, textGameStatus;
    Button buttonRestart;
    GameData gameData = GameData.getInstance();
    BroadcastReceiver mReceiver;

    View.OnClickListener restartListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            GameData gameData = GameData.getInstance();
            gameData.initializeGameMap();
            gameData.saveGameMap();
            gameData.initializePlayer();
            gameData.savePlayer();

            // go back to navigation on restart
            startActivity(NavigationActivity.newIntent(getContext()));

            gameData.broadcastUpdate();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_status_bar, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Status Bar elements
        textCash = view.findViewById(R.id.textCash);
        textHealth = view.findViewById(R.id.textHealth);
        textEquipmentMass = view.findViewById(R.id.textEquipmentMass);
        textGameStatus = view.findViewById(R.id.textGameStatus);
        buttonRestart = view.findViewById(R.id.buttonRestart);
        buttonRestart.setOnClickListener(restartListener);

        mReceiver = gameData.registerBroadcastReceiver(view.getContext(), new Runnable() {
            @Override
            public void run() {
                updateUI();
            }
        });

        updateUI();
    }

    public void updateUI() {
        Player player = gameData.getPlayer();
        // hide while still playing
        textGameStatus.setVisibility(View.GONE);

        textCash.setText(getString(R.string.status_cash, player.getCash()));
        textHealth.setText(getString(R.string.status_health, player.getHealth()));
        textEquipmentMass.setText(getString(R.string.status_mass, player.getEquipmentMass()));

        GameStatus status = player.getGameStatus();
        if (status != GameStatus.PLAYING) {
            // tell the user they won/lost
            Toast.makeText(getContext(),
                    getString(R.string.game_status_message,
                            getString(status == GameStatus.WON ? R.string.game_status_won : R.string.game_status_lost).toLowerCase()
                    ), Toast.LENGTH_SHORT).show();
            textGameStatus.setVisibility(View.VISIBLE);
            textGameStatus.setText(getString(R.string.game_status,
                    getString(status == GameStatus.WON ? R.string.game_status_won : R.string.game_status_lost)));
        }
    }

    @Override
    public void onPause() {
        gameData.unregisterBroadcastReceiver(getContext(), mReceiver);
        super.onPause();
    }
}
