package com.mossle.security.region;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PermissionMatcher {
    private static Logger logger = LoggerFactory
            .getLogger(PermissionMatcher.class);
    private boolean readOnly;

    public boolean match(String want, String have) {
        logger.debug("want : {}", want);
        logger.debug("have : {}", have);

        Permission wantPermission = new Permission(want);
        Permission havePermission = new Permission(have);

        // if this.region is *, it will match all of required region
        // else this.region must equal to required region
        if (!checkRegionPart(wantPermission.getRegion(),
                havePermission.getRegion())) {
            logger.debug("check region false");

            return false;
        }

        // if this.resource is *, it will match all of required resource
        // else this.resource must equal to required resource
        if (!checkPart(wantPermission.getResource(),
                havePermission.getResource())) {
            logger.debug("check resource false");

            return false;
        }

        // if this.operation is *, it will match all of required operation
        // else this.operation must equal to required operation
        String haveOperation = readOnly ? "read" : havePermission
                .getOperation();

        if (checkPart(wantPermission.getOperation(), haveOperation)) {
            logger.debug("check opertion true");

            return true;
        }

        logger.debug("check opertion false");

        return false;
    }

    /**
     * want indexOf , == -1 want == * or have == * return true want == have return true want indexOf , != -1 split and
     * check any of part pass return true split and all of part fail return false
     */
    private boolean checkRegionPart(String want, String have) {
        if (want.indexOf(',') == -1) {
            return checkPart(want, have) || checkRegionWithStar(want, have);
        }

        for (String partOfWant : want.split(",")) {
            if (checkRegionPart(partOfWant, have)) {
                return true;
            }
        }

        return false;
    }

    private boolean checkRegionWithStar(String want, String have) {
        if (!want.endsWith("*")) {
            return false;
        }

        String prefix = want.substring(0, want.length() - 1);

        return have.startsWith(prefix);
    }

    private boolean checkPart(String want, String have) {
        if ("*".equals(want) || "*".equals(have)) {
            return true;
        }

        return want.equals(have);
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean isReadOnly() {
        return readOnly;
    }
}
