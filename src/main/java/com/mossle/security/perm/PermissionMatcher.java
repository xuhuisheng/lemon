package com.mossle.security.perm;

public class PermissionMatcher {
    public boolean match(String want, String have) {
        Permission wantPermission = new Permission(want);
        Permission havePermission = new Permission(have);

        // if this.resource is *, it will match all of required resource
        // else this.resource must equal to required resource
        if (!checkPart(wantPermission.getResource(),
                havePermission.getResource())) {
            return false;
        }

        // if this.operation is *, it will match all of required operation
        // else this.operation must equal to required operation
        if (checkPart(wantPermission.getOperation(),
                havePermission.getOperation())) {
            return true;
        }

        return false;
    }

    private boolean checkPart(String want, String have) {
        if ("*".equals(want) || "*".equals(have)) {
            return true;
        }

        return want.equals(have);
    }
}
