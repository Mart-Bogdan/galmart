package galmart.extractor;

import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.campaign.econ.EconomyAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.util.Misc;
import galmart.intel.element.ButtonViewFactory;
import galmart.ui.TableContent;
import org.lwjgl.util.vector.Vector2f;

import java.util.*;

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
                        .07f * width,
                        "Commodity",
                        .15f * width,
                        "System to Buy",
                        .15f * width,
                        "Location to Buy",
                        .15f * width,
                        "System to Sell",
                        .15f * width,
                        "Location to Sell",
                        .15f * width,
                        "Dist (ly)",
                        .07f * width,
                        "Each (ly)",
                        .07f * width };
        return header;
    }

    private Tuple<String, String> getSupDemMarkets(List<MarketAPI> markets, CommoditySpecAPI commodity) {
        float higherDemand = Float.MIN_VALUE;
        float lowerSupply = Float.MAX_VALUE;
        String currentDemand = null;
        String currentSupply = null;

        for (MarketAPI market: markets) {

            float currentDemandPrice = PriceHelper.getDemandPrice(commodity, market);
            float currentSupplyPrice = PriceHelper.getSupplyPrice(commodity, market);

            if (higherDemand <= currentDemandPrice) {
                higherDemand = currentDemandPrice;
                currentDemand = market.getId();
            }
            if (lowerSupply >= currentSupplyPrice) {
                lowerSupply = currentSupplyPrice;
                currentSupply = market.getId();
            }
        }

        return new Tuple(currentSupply, currentDemand);
    }

    @Override
    public List<Object[]> getRows() {
        List<Object[]> rows = new ArrayList<Object[]>();
        for (CommoditySpecAPI commodity: commodities) {
            Tuple<String, String> supDem = getSupDemMarkets(markets, commodity);
            ArrayList<Object> row = getRowObjects(commodity, supDem);
            rows.add(row.toArray());
        }
        return rows;
    }

    private ArrayList<Object> getRowObjects(CommoditySpecAPI commodity, Tuple<String, String> supDem) {
        TableCellHelper helper = new TableCellHelper();
        MarketAPI demand = economy.getMarket(supDem.Second);
        MarketAPI supply = economy.getMarket(supDem.First);

        DemandPrice demandPrice = new DemandPrice(commodity.getId(), economy);
        SupplyPrice supplyPrice = new SupplyPrice(commodity.getId(), economy);
        float profit = demandPrice.getPrice(demand) - supplyPrice.getPrice(supply);

        Vector2f demandLocation = demand.getLocationInHyperspace();
        Vector2f supplyLocation = supply.getLocationInHyperspace();

        float distance = Misc.getDistanceLY(demandLocation, supplyLocation);
        float distancePlayer = Misc.getDistanceToPlayerLY(supplyLocation);

        ArrayList<Object> row = new ArrayList<Object>();
        // Profit
        row.add(Alignment.RMID);
        row.add(Misc.getHighlightColor());
        row.add(Misc.getDGSCredits(profit));
        // Commodity
        row.add(Alignment.LMID);
        row.add(Misc.getHighlightColor());
        row.add(commodity.getName());
        // System to buy
        row.add(Alignment.MID);
        row.add(helper.getClaimingFactionColor(supply));
        row.add(helper.getSystemName(supply));
        // Location to buy
        row.add(Alignment.LMID);
        row.add(supply.getTextColorForFactionOrPlanet());
        row.add(supply.getName());
        // System to sell
        row.add(Alignment.MID);
        row.add(helper.getClaimingFactionColor(demand));
        row.add(helper.getSystemName(demand));
        // Location to sell
        row.add(Alignment.LMID);
        row.add(demand.getTextColorForFactionOrPlanet());
        row.add(demand.getName());
        // Distance to you
        row.add(Alignment.MID);
        row.add(Misc.getHighlightColor());
        row.add(String.format("%.1f", distancePlayer) + "ly");
        // Distance to each other
        row.add(Alignment.MID);
        row.add(Misc.getHighlightColor());
        row.add(String.format("%.1f", distance) + "ly");

        return row;
    }

    @Override
    public int getSize() {
        return 0;
    }
}