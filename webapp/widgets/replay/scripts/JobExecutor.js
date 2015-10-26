
/**
 * @description job executor for animation.
 * @author <a href="mailto:lingosurf168@gmail.com">Lingo Surf168</a>
 *
 * @class
 * @param {Replay} replay replay
 */
JobExecutor = function(replay) {
    this.replay = replay;
    this.running = false;
};

JobExecutor.prototype = {
    /**
     * @description start running.
     */
    start: function() {
        if (this.running !== true) {
            this.running = true;
            this.tid = new Date().getTime();
            this.run(this.tid);
        }
    },

    /**
     * @description running.
     * @param tid transactionId use to avoid the speed up when multi replays
     */
    run: function(tid) {
        if (this.running !== true) {
            return;
        }
        if (tid != this.tid) {
            return;
        }

        var count = 0;
        var tokens = Array.prototype.slice.call(this.replay.tokens, 0);
        for (var i = 0; i < tokens.length; i++) {
            var token = tokens[i];
            if (token.status === 'running') {
                count++;
                token.move();
            }
        }

        if (count !== 0) {
            var self = this;
            setTimeout(function() {
                self.run(tid);
            }, 100);
        } else {
            this.running = false;

            var tokens = [];
            for (var i = 0; i < this.replay.tokens.length; i++) {
                var token = this.replay.tokens[i];
                if (token.status !== 'removed') {
                    tokens.push(token);
                }
            }
            this.replay.tokens = tokens;
        }
    }
};
