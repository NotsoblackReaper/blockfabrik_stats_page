package at.demski.blockfabrik_stats_page.entities;

public class VisitorCount  {
    private int counter;
    private int maxcount;

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getMaxcount() {
        return maxcount;
    }

    public void setMaxcount(int maxCount) {
        this.maxcount = maxCount;
    }

    @Override
    public String toString(){
        return "{act: "+ counter +", max: "+maxcount+"}";
    }
}
