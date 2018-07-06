package com.mossle.spi.party;

public class MockPartyConnector implements PartyConnector {
    public String findIdByRef(String ref) {
        return ref;
    }

    public String findIdByPositionName(String name) {
        return name;
    }

    public String findCompanyNameByUserId(String userId) {
        return null;
    }

    public String findIdByNearestPositionName(String userId, String positionName) {
        return null;
    }
}
