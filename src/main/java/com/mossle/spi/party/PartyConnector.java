package com.mossle.spi.party;

public interface PartyConnector {
    String findIdByRef(String ref);

    String findIdByPositionName(String name);

    String findCompanyNameByUserId(String userId);

    String findIdByNearestPositionName(String userId, String positionName);
}
