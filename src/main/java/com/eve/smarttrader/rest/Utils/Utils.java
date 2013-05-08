package com.eve.smarttrader.rest.Utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.beimin.eveapi.character.marketorders.MarketOrdersParser;
import com.beimin.eveapi.core.ApiAuthorization;
import com.beimin.eveapi.core.ApiException;
import com.beimin.eveapi.shared.marketorders.AbstractMarketOrdersParser;
import com.beimin.eveapi.shared.marketorders.ApiMarketOrder;
import com.beimin.eveapi.shared.marketorders.MarketOrdersResponse;
import com.eve.smarttrader.rest.HibernateSessionFactory;
import com.eve.smarttrader.rest.enums.Region;
import com.eve.smarttrader.rest.models.jpa.Item;
import com.eve.smarttrader.rest.models.jpa.MarketData;
import com.eve.smarttrader.rest.models.jpa.MarketGroup;
import com.eve.smarttrader.rest.models.jpa.MarketOrder;
import com.eve.smarttrader.rest.models.jpa.User;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

public class Utils {
    private static final Session SESSION = HibernateSessionFactory.getSession();

    public static MarketOrder getCheapestSellMarketOrder(Collection<MarketOrder> mo) {
        MarketOrder[] marketOrders = mo.toArray(new MarketOrder[mo.size()]);
        return (marketOrders.length > 0 && marketOrders[0] != null && !marketOrders[0].isBid()) ? marketOrders[0] : null;
    }

    public static MarketOrder getHighestBuyMarketOrder(Collection<MarketOrder> mo) {
        MarketOrder[] marketOrders = mo.toArray(new MarketOrder[mo.size()]);
        return (marketOrders.length > 0 && marketOrders[marketOrders.length - 1] != null && marketOrders[marketOrders.length - 1].isBid()) ? marketOrders[marketOrders.length - 1] : null;
    }

    public static ImmutableSet<Long> getSellableItemsByRegion(Session session, Region region) {
        BigInteger typeID;
        ImmutableSet.Builder<Long> sellableItems = ImmutableSet.builder();
        StringBuilder query = new StringBuilder("SELECT mh.item_typeID FROM `history` h LEFT JOIN `markethistory` mh ON h.marketHistory_id=mh.id ");
        query.append("WHERE mh.region = :region AND mh.item_typeID IN :items ");
        query.append("GROUP BY mh.id ");
        query.append("HAVING COUNT(h.marketHistory_id) > 15 AND ((MEDIAN(h.quantity) >= 10 AND MEDIAN(h.high) >= 2000000) ");
        query.append("OR (MEDIAN(h.quantity) >= 100 AND MEDIAN(h.high) >= 1000000)) ");
        query.append("ORDER BY mh.item_typeID, h.date DESC;");
        SQLQuery sqlQuery = session.createSQLQuery(query.toString());
        sqlQuery.setParameter("region", region.toString());
        sqlQuery.setParameterList("items", Constants.getItems().keySet());
        for (Object object : sqlQuery.list()) {
            typeID = (BigInteger) object;
            sellableItems.add(typeID.longValue());
        }
        return sellableItems.build();
    }

    public static Map<Long, Item> loadItems() {
        Map<Long, Item> items = new HashMap<Long, Item>();
        Criteria itemsCriteria = SESSION.createCriteria(Item.class);
        itemsCriteria.add(Restrictions.isNotNull("marketGroupId"));
        itemsCriteria.add(Restrictions.lt("volume", 5000D));
        itemsCriteria.add(Restrictions.sqlRestriction("getMainParentGroupID(marketGroupID) IN (9,11,24,150,157,955)"));
        for (Object o : itemsCriteria.list()) {
            Item item = (Item) o;
            items.put(item.getTypeId(), item);
        }

        return items;
    }

    public static ImmutableMap<Integer, String> loadMarketGroupsName() {
        long marketGroupId;
        ImmutableMap.Builder<Integer, String> marketGroups = ImmutableMap.builder();
        Criteria rootMarketGroupCriteria = SESSION.createCriteria(MarketGroup.class);
        rootMarketGroupCriteria.add(Restrictions.isNull("marketGroup"));
        List<MarketGroup> invmarketgroupses = new ArrayList<MarketGroup>();
        for (Object o : rootMarketGroupCriteria.list()) {
            invmarketgroupses.add((MarketGroup) o);
        }
        ImmutableMap<Long, MarketGroup> rootMarketGroupMap = Maps.uniqueIndex(invmarketgroupses, new Function<MarketGroup, Long>() {
            @Override
            public Long apply(MarketGroup input) {
                return (long) input.getMarketGroupId();
            }
        });
        Criteria marketGroupCriteria = SESSION.createCriteria(MarketGroup.class);
        ProjectionList projectionList = Projections.projectionList();
        marketGroupCriteria.add(Restrictions.isNotNull("marketGroup"));
        projectionList.add(Projections.sqlProjection("getMainParentGroupID(parentGroupID) as rootMarketGroupId", new String[] { "rootMarketGroupId" }, new Type[] { StandardBasicTypes.LONG }));
        projectionList.add(Projections.property("marketGroupId"));
        marketGroupCriteria.setProjection(projectionList);
        for (Object o : marketGroupCriteria.list()) {
            Object[] marketGroup = (Object[]) o;
            marketGroupId = (Long) marketGroup[0];
            marketGroups.put((Integer) marketGroup[1], rootMarketGroupMap.get(marketGroupId).getMarketGroupName());
        }
        return marketGroups.build();
    }

    public static Map<Region, Map<Long, MarketData>> readBySellableItems(Session session, Set<Long> items, String[] regions) {
        Map<Region, Map<Long, MarketData>> marketDatas = new HashMap<Region, Map<Long, MarketData>>();
        for (String region : regions) {
            marketDatas.put(Region.valueOf(region), new HashMap<Long, MarketData>());
        }
        Criteria sellableItemsCriteria = session.createCriteria(MarketData.class, "md");
        if (regions.length > 0) {
            sellableItemsCriteria.add(Restrictions.in("region", regions));
        }
        if (!items.isEmpty()) {
            sellableItemsCriteria.add(Restrictions.in("itemTypeId", items));
        }
        DetachedCriteria sellOrderSizeCriteria = DetachedCriteria.forClass(MarketOrder.class);
        sellOrderSizeCriteria.add(Restrictions.eqProperty("marketData", "md.id"));
        sellOrderSizeCriteria.add(Restrictions.eq("bid", false));
        sellOrderSizeCriteria.setProjection(Projections.count("orderId"));
        sellableItemsCriteria.add(Subqueries.lt(0L, sellOrderSizeCriteria));
        for (Object object : sellableItemsCriteria.list()) {
            MarketData marketData = (MarketData) object;
            marketDatas.get(Region.valueOf(marketData.getRegion())).put(marketData.getItemTypeId(), marketData);
        }

        return marketDatas;
    }

    public static DateTime timeStampToDateTimeUTC(Date date) {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter dtf = DateTimeFormat.forPattern(pattern).withZoneUTC();
        return dtf.parseDateTime(date.toString().substring(0, pattern.length()));
    }

    public static User getUserByName(Session session, String name) {
        Criteria userCriteria = session.createCriteria(User.class);
        userCriteria.add(Restrictions.eq("name", name));
        return (User) userCriteria.uniqueResult();
    }

    public static void setUserItems(User user) throws ApiException {
        AbstractMarketOrdersParser parser = MarketOrdersParser.getInstance();
        ApiAuthorization auth = new ApiAuthorization(user.getKeyId(), user.getCharId(), user.getvCode());
        MarketOrdersResponse response = parser.getResponse(auth);
        Set<ApiMarketOrder> orders = response.getAll();
        for (ApiMarketOrder order : orders) {
            if (order.getOrderState() == 0) {
                user.getBuyOrderItems().add((long) order.getTypeID());
            }
            if (order.getOrderState() == 1) {
                user.getSellOrderItems().add((long) order.getTypeID());
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static List<MarketData> getPenuryItems(Session session, Set<Long> items, Region region) {
        Criteria penuryItemsCriteria = session.createCriteria(MarketData.class, "md");
        penuryItemsCriteria.add(Restrictions.eq("region", region.toString()));
        if (!items.isEmpty()) {
            penuryItemsCriteria.add(Restrictions.in("itemTypeId", items));
        }
        DetachedCriteria sellOrderSizeCriteria = DetachedCriteria.forClass(MarketOrder.class);
        sellOrderSizeCriteria.add(Restrictions.eqProperty("marketData", "md.id"));
        sellOrderSizeCriteria.add(Restrictions.eq("bid", false));
        sellOrderSizeCriteria.setProjection(Projections.count("orderId"));
        penuryItemsCriteria.add(Subqueries.eq(0L, sellOrderSizeCriteria));
        return penuryItemsCriteria.list();
    }
}
