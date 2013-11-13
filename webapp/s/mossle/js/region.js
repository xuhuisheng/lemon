
region = {
	readOnly: false
};

region.Permission = function(text) {
	var array = text.split(":");
	if (array.length == 1) {
		console.error("there must 2 or 3 parts in text : [" + text + "]");
		this.region = "system";
		this.resource = array[0];
		this.operation = "*";

		return;
	}

	this.region = array[0];
	this.resource = array[1];
	if (array.length == 3) {
		this.operation = array[2];
	} else {
		this.operation = "*";
	}
};

region.havePermission = function(want, have) {
	var wantPermission = new region.Permission(want);
	var havePermission = new region.Permission(have);

	var checkRegionWithStar = function(want, have) {
		if (!want.endsWith("*")) {
			return false;
		}

		var prefix = want.substring(0, want.length - 1);

		return have.startsWith(prefix);
	};

    var checkPart = function(want, have) {
        if ("*" == want || "*" == have) {
            return true;
        }

        return want == have;
    }

	var checkRegionPart = function(want, have) {
		if (want.indexOf(',') == -1) {
			return checkPart(want, have) || checkRegionWithStar(want, have);
		}

		for (var partOfWant in want.split(",")) {
			if (checkRegionPart(partOfWant, have)) {
				return true;
			}
		}

		return false;
	};

	// if this.region is *, it will match all of required region
	// else this.region must equal to required region
	if (!checkRegionPart(wantPermission.region, havePermission.region)) {
		//console.debug("check region false");

		return false;
	}

	// if this.resource is *, it will match all of required resource
	// else this.resource must equal to required resource
	if (!checkPart(wantPermission.resource, havePermission.resource)) {
		//console.debug("check resource false");

		return false;
	}

	// if this.operation is *, it will match all of required operation
	// else this.operation must equal to required operation
	var haveOperation = region.readOnly ? "read" : havePermission.operation;

	if (checkPart(wantPermission.operation, haveOperation)) {
		//console.debug("check opertion true");

		return true;
	}

	//console.debug("check opertion false");

	return false;
};
