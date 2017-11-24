
REPLAY_TOKEN_IMAGE = 'user.png';

/**
 * @description use animation to replay the process progress.
 * @author <a href="mailto:lingosurf168@gmail.com">Lingo Surf168</a>
 *
 * @class
 * @param processDefintion all nodes
 * @param historyActivities history nodes
 * @param currentActivities currnet nodes
 */
Replay = function(processDefinition, historyActivities, currentActivities) {
    this.processDefinition = processDefinition;
    this.historyActivities = historyActivities;
    this.currentActivities = currentActivities;
    this.tokens = [];
    this.map = {};

    this.initialize();

    this.jobExecutor = new JobExecutor(this);
};

Replay.prototype = {
    /**
     * @description initialize replay.
     */
    initialize: function() {
        for (var i = 0; i < this.processDefinition.length; i++) {
            var activity = this.processDefinition[i];
            if (activity.type === '开始事件') {
                var startNode = new Node(activity, this);
                this.init = startNode;
                this.tokens.push(new Token(startNode, this));
                break;
            }
        }
    },

    /**
     * @description notify to replay by future step.
     * @param {Number} future future step
     */
    notify: function(future) {
        if (future !== 0) {
            var tokens = Array.prototype.slice.call(this.tokens, 0);
            for (var i = 0; i < tokens.length; i++) {
                var token = tokens[i];
                if (token.startMove(future) === true) {
                    this.jobExecutor.start();
                }
            }
        }
    },

    /**
     * @description move to previous step.
     */
    prev: function() {
        this.notify(-1);
    },

    /**
     * @description move to next step.
     */
    next: function() {
        this.notify(1);
    },

    /**
     * @description replay all steps from first node.
     */
    replay: function() {
        this.destoryToken();
        this.tokens = [new Token(this.init, this)];
        this.notify(this.processDefinition.length);
    },

    /**
     * @description destroy all tokens.
     */
    destoryToken: function() {
        this.jobExecutor.running = false;

        for (var i = 0; i < this.tokens.length; i++) {
            var token = this.tokens[i];
            token.destroy();
        }
        delete this.tokens;
    }
};
