package co.sgfc.madgame.Items.General;

import co.sgfc.madgame.Equipment;

public class IceScraper extends Equipment {
    public static final String ID = "ICE_SCRAPER";

    public String getID() {
        return ID;
    }

    public IceScraper() {
        super("Ice Scraper", 15, 2.5);
    }

    @Override
    public boolean isUsable() {
        return false;
    }
}
