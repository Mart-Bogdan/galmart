package galmart.filter.market;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import galmart.extractor.TableCellHelper;

public class MarketAroundFilter implements MarketFilter {

    protected final TableCellHelper helper;
    protected final Integer distance;

    public MarketAroundFilter(Integer distance) {
        this.helper = new TableCellHelper();
        this.distance = distance;
    }

    @Override
    public boolean accept(MarketAPI market) {
        Float distance = helper.getDistanceFloat(market);
        return (distance<=this.distance);
    }
}
