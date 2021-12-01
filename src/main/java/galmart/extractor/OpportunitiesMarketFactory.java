package galmart.extractor;

import com.fs.starfarer.api.campaign.econ.EconomyAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import galmart.CollectionsHelper;
import galmart.filter.market.MarketAroundFilter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OpportunitiesMarketFactory extends MarketFactory {

    public OpportunitiesMarketFactory(EconomyAPI economy) {
        super(economy);
    }

    @Override
    protected void filterMarkets(List<MarketAPI> markets) {
        CollectionsHelper.reduce(markets, new MarketAroundFilter(5));
    }

    @Override
    protected void sortMarkets(List<MarketAPI> markets) {
        final TableCellHelper helper = new TableCellHelper();
        Collections.sort(markets, new Comparator<MarketAPI>() {
            @Override
            public int compare(MarketAPI marketA, MarketAPI marketB) {
            float distA = helper.getDistanceFloat(marketA);
            float distB = helper.getDistanceFloat(marketB);
            return (int) Math.signum(distA-distB);
        }});
    }
}
