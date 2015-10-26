
/**
 * @description the node in ProcessDefinition.
 * @author <a href="mailto:lingosurf168@gmail.com">Lingo Surf168</a>
 *
 * @class
 * @param {Map} activity origin information from ProcessDefinition
 * @parma {Replay} replay Replay
 */
Node = function(activity, replay) {
    this.name = activity.name;
    this.type = activity.type;
    this.x = activity.x;
    this.y = activity.y;
    if (this.type === 'start' || this.type === 'end' || this.type === 'end-error'
            || this.type === 'end-cancel' || this.type === 'decision'
            || this.type === 'fork' || this.type === 'join') {
        this.w = 48;
        this.h = 48;
    } else {
        this.w = activity.w;
        this.h = activity.h;
    }
    this.activity = activity;
    this.replay = replay;

    this.parent = [];
    this.children = [];

    var duplicatedNode = this.replay.map[this.name];
    if (typeof duplicatedNode !== 'undefined') {
        if (duplicatedNode !== this) {
            throw new Error('node duplicated, name: ' + this.name);
        }
    } else {
        this.replay.map[this.name] = this;
    }

    if (!this.isCurrentActivity(this.name)) {
        this.init();
    }
};

Node.prototype = {
    /**
     * @description initialize.
     */
    init: function() {
        if (!this.hasHistory()) {
            this.findTransitions();
        }
    },

    /**
     * @description create child node by activity.
     * @param {Map} activity origin information
     */
    createChildNode: function(activity) {
        var name = activity.name;
        var alreadyCreatedNode = this.replay.map[activity.name];

        var childNode = null;
        if (typeof alreadyCreatedNode !== 'undefined') {
            childNode = alreadyCreatedNode;
        } else {
            childNode = new Node(activity, this.replay);
        }
        this.children.push(childNode);
        childNode.parent.push(this);
    },

    /**
     * @description compare history and acitivity.
     * return {Boolean} if has history
     */
    hasHistory: function() {
        var activities = this.replay.historyActivities;
        for (var i = 0; i < activities.length; i++) {
            var ha = activities[i];
            if (ha.name === this.activity.name) {
                var ht = ha.t;
                var ts = this.activity.ts;
                for (var j = 0; j < ts.length; j++) {
                    var t = ts[j];
                    if (t.name === ht) {
                        var activity = this.findActivity(t.to);
                        this.createChildNode(activity);
                        return true;
                    }
                }
            }
        }
        return false;
    },

    /**
     * @description find transitions.
     */
    findTransitions: function() {
        var ts = this.activity.ts;
        for (var i = 0; i < ts.length; i++) {
            var t = ts[i];
            var activityName = t.to;
            var activity = this.findActivity(activityName);
            this.createChildNode(activity);
        }
    },

    /**
     * @description find activity by name.
     * @parma {String} activityName activity name
     */
    findActivity: function(activityName) {
        var pd = this.replay.processDefinition;
        for (var i = 0; i < pd.length; i++) {
            var activity = pd[i];
            if (activity.name === activityName) {
                return activity;
            }
        }
    },

    /**
     * @description if it is the current activity.
     * @param {String} activity activity name
     * @return {Boolean} if is current
     */
    isCurrentActivity: function(activityName) {
        var activities = this.replay.currentActivities;
        for (var i = 0; i < activities.length; i++) {
            var currentActivityName = activities[i];
            if (currentActivityName === activityName) {
                return true;
            }
        }
        return false;
    }
};
