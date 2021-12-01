package galmart.extractor;

import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;

public class PriceHelper {
    
    public static float getDemandPrice(CommoditySpecAPI commodity, MarketAPI market) {
        float econUnit = commodity.getEconUnit();
        return market.getDemandPrice(commodity.getId(), econUnit, true) / econUnit;
    }

    public static float getSupplyPrice(CommoditySpecAPI commodity, MarketAPI market) {
        float econUnit = commodity.getEconUnit();
        return market.getSupplyPrice(commodity.getId(), econUnit, true) / econUnit;
    }
}
