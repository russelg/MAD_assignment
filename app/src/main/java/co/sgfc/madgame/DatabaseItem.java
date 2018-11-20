package co.sgfc.madgame;

public abstract class DatabaseItem {
    private long uniqueId = -1;
    
    public long getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(long uniqueId) {
        this.uniqueId = uniqueId;
    }
}
