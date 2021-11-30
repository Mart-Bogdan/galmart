package galmart.extractor;

import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.campaign.econ.EconomyAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.util.Misc;
import galmart.intel.element.ButtonViewFactory;
import galmart.ui.TableContent;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class OpportunitiesTableContent implements TableContent {
    private final EconomyAPI economy;
    private final List<MarketAPI> markets;
    private final List<CommoditySpecAPI> commodities;

    public OpportunitiesTableContent(EconomyAPI economy, List<MarketAPI> markets) {
        this.economy = economy;
        this.markets = markets;
        this.commodities = getCommoditySpecAPIS(economy);

    }

    private List<CommoditySpecAPI> getCommoditySpecAPIS(EconomyAPI economy) {
        ButtonViewFactory commFactory = new ButtonViewFactory();
        List<String> commodityIds = economy.getAllCommodityIds();
        commFactory.sortCommodities(commodityIds);
        List<CommoditySpecAPI> comms = new Stack<CommoditySpecAPI>();
        for (String commId: commodityIds) {
            CommoditySpecAPI commodity = economy.getCommoditySpec(commId);
            if (commFactory.canInclude(commodity)) {
                comms.add(commodity);
            }
        }
        return comms;
    }

    @Override
    public Object[] getHeaders(float width) {
        Object header[] =
                {
//                        "#",
//                        .05f * width,
                        "Profit",
                        .1f * width,
//                        "Ratio",
//                        .1f * width,
                        "System to Buy",
                        .1f * width,
//                        "Location to Buy",
//                        .1f * width,
                        "System to Sell",
                        .1f * width,
//                        "Location to Sell",
//                        .1f * width,
//                        "Systems dist (ly)",
//                        .1f * width,
                        "Dist from you (ly)",
                        .1f * width };
        return header;
    }

    @Override
    public List<Object[]> getRows() {
        List<Object[]> rows = new ArrayList<Object[]>();
        List<Triple<CommoditySpecAPI, MarketAPI, MarketAPI>> list = new ArrayList<>();
        for (CommoditySpecAPI commodity: commodities) {
            float higherDemmand = 0f;
            float lowerSupply = 0f;
            MarketAPI currentDemand = null;
            MarketAPI currentSupply = null;
            DemandPrice demand = new DemandPrice(commodity.getId(), economy);
            SupplyPrice supply = new SupplyPrice(commodity.getId(), economy);
            for (MarketAPI market: markets) {
                float currentDemandPrice = demand.getPrice(market);
                float currentSupplyPrice = supply.getPrice(market);
                if (higherDemmand < currentDemandPrice) {
                    higherDemmand = currentDemandPrice;
                    currentDemand = market;
                }
                if (lowerSupply >= currentSupplyPrice) {
                    lowerSupply = currentSupplyPrice;
                    currentSupply = market;
                }
            }
            if (currentDemand == null || currentSupply == null) {
                continue;
            }
            list.add(new Triple(commodity, currentDemand, currentSupply));
        }
        for(Triple<CommoditySpecAPI, MarketAPI, MarketAPI> item : list) {
            CommoditySpecAPI commodity = item.getFirst();
            MarketAPI demmand = item.getSecond();
            MarketAPI supply = item.getThird();

            DemandPrice demandPrice = new DemandPrice(commodity.getId(), economy);
            SupplyPrice supplyPrice = new SupplyPrice(commodity.getId(), economy);
            float profit = demandPrice.getPrice(demmand) - supplyPrice.getPrice(supply);

            Vector2f demmandLocation = demmand.getLocationInHyperspace();
            Vector2f supplyLocation = supply.getLocationInHyperspace();
            float distance = Misc.getDistanceLY(demmandLocation, supplyLocation);

            Object[] row = new Object[15];
            // Profit
            row[0] = Alignment.MID;
            row[1] = Misc.getHighlightColor();
            row[2] = Misc.getDGSCredits(profit);
            // Commodity
            row[3] = Alignment.MID;
            row[4] = Misc.getHighlightColor();
            row[5] = commodity.getName();
            // System to buy
            row[6] = Alignment.MID;
            row[7] = Misc.getHighlightColor();
            row[8] = demmand.getName();
            // System to sell
            row[9] = Alignment.MID;
            row[10] = Misc.getHighlightColor();
            row[11] = supply.getName();
            // Distance to each other
            row[12] = Alignment.MID;
            row[13] = Misc.getHighlightColor();
            row[14] = distance;

            rows.add(row);
        }
        return rows;
    }

    @Override
    public int getSize() {
        return 0;
    }
}
