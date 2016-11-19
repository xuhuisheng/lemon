package com.mossle.core.query;

import java.util.Collection;
import java.util.List;

public class PropertyFilterUtils {
    protected PropertyFilterUtils() {
    }

    public static void buildConfigurations(
            Collection<PropertyFilter> propertyFilters, StringBuilder buff,
            List<Object> params) {
        buildConfigurations(propertyFilters, buff, params, true);
    }

    public static void buildConfigurations(
            Collection<PropertyFilter> propertyFilters, StringBuilder buff,
            List<Object> params, boolean checkWhere) {
        for (PropertyFilter propertyFilter : propertyFilters) {
            buildConfiguration(propertyFilter, buff, params, checkWhere);
        }
    }

    public static void buildConfiguration(PropertyFilter propertyFilter,
            StringBuilder buff, List<Object> params) {
        buildConfiguration(propertyFilter, buff, params, true);
    }

    public static void buildConfiguration(PropertyFilter propertyFilter,
            StringBuilder buff, List<Object> params, boolean checkWhere) {
        if (checkWhere
                && (buff.toString().toLowerCase().indexOf("where") == -1)) {
            buff.append(" where ");
        } else {
            buff.append(" and ");
        }

        String propertyName = propertyFilter.getPropertyName();
        Object propertyValue = propertyFilter.getMatchValue();
        MatchType matchType = propertyFilter.getMatchType();

        switch (matchType) {
        case EQ:
            buff.append(propertyName).append("=?");
            params.add(propertyValue);

            break;

        case NOT:
            buff.append(propertyName).append("<>?");
            params.add(propertyValue);

            break;

        case LIKE:
            buff.append(propertyName).append(" like ?");
            params.add("%" + propertyValue + "%");

            break;

        case LE:
            buff.append(propertyName).append("<=?");
            params.add(propertyValue);

            break;

        case LT:
            buff.append(propertyName).append("<?");
            params.add(propertyValue);

            break;

        case GE:
            buff.append(propertyName).append(">=?");
            params.add(propertyValue);

            break;

        case GT:
            buff.append(propertyName).append(">?");
            params.add(propertyValue);

            break;

        case IN:
            buff.append(propertyName).append("in (?)");
            params.add(propertyValue);

            break;

        case INL:
            buff.append(propertyName).append(" is null");

            break;

        case NNL:
            buff.append(propertyName).append(" is not null");

            break;

        default:
            buff.append(propertyName).append("=?");
            params.add(propertyValue);

            break;
        }
    }
}
