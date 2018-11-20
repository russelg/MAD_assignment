package co.sgfc.madgame.Items.Usables;

import android.content.Context;

import co.sgfc.madgame.Equipment;
import co.sgfc.madgame.SmellOScopeActivity;

public class SmellOScope extends Equipment {
    public static final String ID = "SMELL_O_SCOPE";

    public String getID() {
        return ID;
    }

    public SmellOScope() {
        super("Portable Smell-O-Scope", 10, 5.0);
    }

    @Override
    public boolean isUsable() {
        return true;
    }

    @Override
    public void use(Context context) {
        context.startActivity(SmellOScopeActivity.newIntent(context));
    }
}
